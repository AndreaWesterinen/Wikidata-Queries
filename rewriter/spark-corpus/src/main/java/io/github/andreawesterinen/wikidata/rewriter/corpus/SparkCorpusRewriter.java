package io.github.andreawesterinen.wikidata.rewriter.corpus;

import static org.apache.spark.sql.functions.col;
import static org.apache.spark.sql.functions.lit;
import static org.apache.spark.sql.functions.pmod;
import static org.apache.spark.sql.functions.xxhash64;

import java.io.Serializable;
import java.sql.Timestamp;
import java.util.Iterator;
import java.util.NoSuchElementException;
import java.util.regex.Pattern;

import org.apache.jena.atlas.json.JSON;
import org.apache.jena.atlas.json.JsonObject;
import org.apache.jena.atlas.json.JsonValue;
import org.apache.spark.api.java.function.MapPartitionsFunction;
import org.apache.spark.sql.Dataset;
import org.apache.spark.sql.Encoders;
import org.apache.spark.sql.Row;
import org.apache.spark.sql.SparkSession;

import io.github.andreawesterinen.wikidata.rewriter.EmbeddedSparqlRewriter;

/** Applies the embedded SPARQL rewriter to the Iceberg query-log corpus. */
public final class SparkCorpusRewriter {
    private static final String BATCH_VIEW = "__sparql_rewrite_batch";

    private SparkCorpusRewriter() {
    }

    /** Runs one resumable corpus batch. */
    public static void main(String[] args) throws Exception {
        Options options = Options.parse(args);
        SparkSession spark = SparkSession.builder()
                .appName("wdqs-sparql-corpus-rewriter")
                .enableHiveSupport()
                .getOrCreate();
        try {
            Dataset<Row> source = spark.table(options.sourceTable())
                    .select(
                            col("id").alias("query_id"),
                            col("query").alias("original_query"))
                    .where(col("query_id").isNotNull()
                            .and(col("original_query").isNotNull()))
                    .where(pmod(xxhash64(col("query_id")),
                            lit(options.batchCount()))
                            .equalTo(options.batchIndex()));

            Dataset<Row> selected;
            if (options.isExplicitRerun()) {
                Dataset<Row> rerunIds = spark.table(options.rerunIdsTable())
                        .select(col("query_id"))
                        .where(col("query_id").isNotNull())
                        .distinct();
                selected = source.alias("source")
                        .join(rerunIds.alias("ids"),
                                col("source.query_id")
                                        .equalTo(col("ids.query_id")),
                                "inner")
                        .select(
                                col("source.query_id").alias("query_id"),
                                col("source.original_query")
                                        .alias("original_query"));
            } else {
                Dataset<Row> completed = spark.table(options.outputTable())
                        .select(col("query_id"))
                        .where(col("query_id").isNotNull())
                        .distinct();
                selected = source.alias("source")
                        .join(completed.alias("completed"),
                                col("source.query_id")
                                        .equalTo(col("completed.query_id")),
                                "left_anti")
                        .select(
                                col("source.query_id").alias("query_id"),
                                col("source.original_query")
                                        .alias("original_query"));
            }

            Dataset<CorpusRewrite> rewritten = selected
                    .select(col("query_id"), col("original_query"))
                    .mapPartitions(new RewritePartition(),
                            Encoders.bean(CorpusRewrite.class));
            Dataset<Row> batch = outputColumns(rewritten);

            if (options.isExplicitRerun()) {
                batch.createOrReplaceTempView(BATCH_VIEW);
                spark.sql(mergeSql(options.outputTable()));
            } else {
                batch.writeTo(options.outputTable()).append();
            }

            System.out.printf(
                    "Corpus rewrite committed: source=%s output=%s batch=%d/%d mode=%s%n",
                    options.sourceTable(), options.outputTable(),
                    options.batchIndex(), options.batchCount(),
                    options.isExplicitRerun() ? "rerun" : "resume");
        } finally {
            spark.stop();
        }
    }

    private static Dataset<Row> outputColumns(Dataset<CorpusRewrite> rewritten) {
        return rewritten.select(
                col("queryId").alias("query_id"),
                col("originalQuery").alias("original_query"),
                col("rewrittenQuery").alias("rewritten_query"),
                col("rewrites"),
                col("rewriteStatus").alias("rewrite_status"),
                col("warnings"),
                col("errors"),
                col("rewrittenDt").alias("rewritten_dt"));
    }

    /** Builds the explicit-rerun upsert statement for a validated table name. */
    static String mergeSql(String outputTable) {
        return "MERGE INTO " + outputTable + " AS target\n"
                + "USING " + BATCH_VIEW + " AS source\n"
                + "ON target.query_id = source.query_id\n"
                + "WHEN MATCHED THEN UPDATE SET\n"
                + "  original_query = source.original_query,\n"
                + "  rewritten_query = source.rewritten_query,\n"
                + "  rewrites = source.rewrites,\n"
                + "  rewrite_status = source.rewrite_status,\n"
                + "  warnings = source.warnings,\n"
                + "  errors = source.errors,\n"
                + "  rewritten_dt = source.rewritten_dt\n"
                + "WHEN NOT MATCHED THEN INSERT (\n"
                + "  query_id, original_query, rewritten_query, rewrites,\n"
                + "  rewrite_status, warnings, errors, rewritten_dt\n"
                + ") VALUES (\n"
                + "  source.query_id, source.original_query,\n"
                + "  source.rewritten_query, source.rewrites,\n"
                + "  source.rewrite_status, source.warnings, source.errors,\n"
                + "  source.rewritten_dt\n"
                + ")";
    }

    /** Rewrites all rows handled by one Spark task in its executor JVM. */
    static final class RewritePartition
            implements MapPartitionsFunction<Row, CorpusRewrite> {
        private static final long serialVersionUID = 1L;

        @Override
        public Iterator<CorpusRewrite> call(Iterator<Row> rows) {
            return new Iterator<CorpusRewrite>() {
                @Override
                public boolean hasNext() {
                    return rows.hasNext();
                }

                @Override
                public CorpusRewrite next() {
                    if (!hasNext()) {
                        throw new NoSuchElementException();
                    }
                    Row row = rows.next();
                    return rewrite(row.getString(0), row.getString(1));
                }
            };
        }
    }

    /** Projects one engine result into the corpus table schema. */
    static CorpusRewrite rewrite(String queryId, String originalQuery) {
        JsonObject result = JSON.parse(
                EmbeddedSparqlRewriter.rewriteJson(queryId, originalQuery));
        return new CorpusRewrite(
                queryId,
                originalQuery,
                nullableString(result.get("rewritten_query")),
                JSON.toStringFlat(result.get("rewrites")),
                result.get("rewrite_status").getAsString().value(),
                JSON.toStringFlat(result.get("warnings")),
                JSON.toStringFlat(result.get("errors")),
                new Timestamp(System.currentTimeMillis()));
    }

    private static String nullableString(JsonValue value) {
        return value.isNull() ? null : value.getAsString().value();
    }

    /** Java bean matching the production Iceberg rewrite table. */
    public static final class CorpusRewrite implements Serializable {
        private static final long serialVersionUID = 1L;

        private String queryId;
        private String originalQuery;
        private String rewrittenQuery;
        private String rewrites;
        private String rewriteStatus;
        private String warnings;
        private String errors;
        private Timestamp rewrittenDt;

        public CorpusRewrite() {
        }

        CorpusRewrite(String queryId, String originalQuery,
                String rewrittenQuery, String rewrites, String rewriteStatus,
                String warnings, String errors, Timestamp rewrittenDt) {
            this.queryId = queryId;
            this.originalQuery = originalQuery;
            this.rewrittenQuery = rewrittenQuery;
            this.rewrites = rewrites;
            this.rewriteStatus = rewriteStatus;
            this.warnings = warnings;
            this.errors = errors;
            this.rewrittenDt = rewrittenDt;
        }

        public String getQueryId() {
            return queryId;
        }

        public void setQueryId(String queryId) {
            this.queryId = queryId;
        }

        public String getOriginalQuery() {
            return originalQuery;
        }

        public void setOriginalQuery(String originalQuery) {
            this.originalQuery = originalQuery;
        }

        public String getRewrittenQuery() {
            return rewrittenQuery;
        }

        public void setRewrittenQuery(String rewrittenQuery) {
            this.rewrittenQuery = rewrittenQuery;
        }

        public String getRewrites() {
            return rewrites;
        }

        public void setRewrites(String rewrites) {
            this.rewrites = rewrites;
        }

        public String getRewriteStatus() {
            return rewriteStatus;
        }

        public void setRewriteStatus(String rewriteStatus) {
            this.rewriteStatus = rewriteStatus;
        }

        public String getWarnings() {
            return warnings;
        }

        public void setWarnings(String warnings) {
            this.warnings = warnings;
        }

        public String getErrors() {
            return errors;
        }

        public void setErrors(String errors) {
            this.errors = errors;
        }

        public Timestamp getRewrittenDt() {
            return rewrittenDt;
        }

        public void setRewrittenDt(Timestamp rewrittenDt) {
            this.rewrittenDt = rewrittenDt;
        }
    }

    /** Validated command-line options for one batch. */
    static final class Options {
        private static final String DEFAULT_SOURCE =
                "awesterinen.wdqs_query_logs";
        private static final Pattern TABLE_IDENTIFIER = Pattern.compile(
                "[A-Za-z_][A-Za-z0-9_]*(\\.[A-Za-z_][A-Za-z0-9_]*){1,2}");

        private final String sourceTable;
        private final String outputTable;
        private final String rerunIdsTable;
        private final int batchCount;
        private final int batchIndex;

        private Options(String sourceTable, String outputTable,
                String rerunIdsTable, int batchCount, int batchIndex) {
            this.sourceTable = sourceTable;
            this.outputTable = outputTable;
            this.rerunIdsTable = rerunIdsTable;
            this.batchCount = batchCount;
            this.batchIndex = batchIndex;
        }

        static Options parse(String[] args) {
            String source = DEFAULT_SOURCE;
            String output = null;
            String rerunIds = null;
            int batchCount = 1;
            int batchIndex = 0;
            for (int index = 0; index < args.length; index += 2) {
                if (index + 1 >= args.length) {
                    throw new IllegalArgumentException(
                            "Missing value for option " + args[index]);
                }
                String value = args[index + 1];
                switch (args[index]) {
                    case "--source-table":
                        source = value;
                        break;
                    case "--output-table":
                        output = value;
                        break;
                    case "--rerun-ids-table":
                        rerunIds = value;
                        break;
                    case "--batch-count":
                        batchCount = positiveInteger("--batch-count", value);
                        break;
                    case "--batch-index":
                        batchIndex = nonnegativeInteger(
                                "--batch-index", value);
                        break;
                    default:
                        throw new IllegalArgumentException(
                                "Unknown option: " + args[index]);
                }
            }
            requireTableIdentifier("--source-table", source);
            requireTableIdentifier("--output-table", output);
            if (rerunIds != null) {
                requireTableIdentifier("--rerun-ids-table", rerunIds);
            }
            if (source.equals(output)) {
                throw new IllegalArgumentException(
                        "Source and output tables must differ.");
            }
            if (batchIndex >= batchCount) {
                throw new IllegalArgumentException(
                        "--batch-index must be less than --batch-count.");
            }
            return new Options(
                    source, output, rerunIds, batchCount, batchIndex);
        }

        private static int positiveInteger(String option, String value) {
            int parsed = nonnegativeInteger(option, value);
            if (parsed > 0) {
                return parsed;
            }
            throw new IllegalArgumentException(
                    option + " must be a positive integer.");
        }

        private static int nonnegativeInteger(String option, String value) {
            try {
                int parsed = Integer.parseInt(value);
                if (parsed >= 0) {
                    return parsed;
                }
            } catch (NumberFormatException error) {
                // Use the common message below.
            }
            throw new IllegalArgumentException(
                    option + " must be a nonnegative integer.");
        }

        private static void requireTableIdentifier(String option, String value) {
            if (value == null || !TABLE_IDENTIFIER.matcher(value).matches()) {
                throw new IllegalArgumentException(
                        option + " must be a two- or three-part table identifier.");
            }
        }

        String sourceTable() {
            return sourceTable;
        }

        String outputTable() {
            return outputTable;
        }

        String rerunIdsTable() {
            return rerunIdsTable;
        }

        boolean isExplicitRerun() {
            return rerunIdsTable != null;
        }

        int batchCount() {
            return batchCount;
        }

        int batchIndex() {
            return batchIndex;
        }
    }
}
