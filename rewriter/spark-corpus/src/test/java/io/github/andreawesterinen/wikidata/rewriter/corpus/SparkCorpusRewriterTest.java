package io.github.andreawesterinen.wikidata.rewriter.corpus;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertNull;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.api.Assertions.assertTrue;

import org.junit.jupiter.api.Test;

final class SparkCorpusRewriterTest {
    @Test
    void parsesResumeBatchOptions() {
        SparkCorpusRewriter.Options options =
                SparkCorpusRewriter.Options.parse(new String[] {
                    "--output-table", "awesterinen.wdqs_query_rewrites",
                    "--batch-count", "8",
                    "--batch-index", "3"
                });

        assertEquals("awesterinen.wdqs_query_logs", options.sourceTable());
        assertEquals("awesterinen.wdqs_query_rewrites",
                options.outputTable());
        assertEquals(8, options.batchCount());
        assertEquals(3, options.batchIndex());
        assertFalse(options.isExplicitRerun());
    }

    @Test
    void parsesExplicitRerunOptions() {
        SparkCorpusRewriter.Options options =
                SparkCorpusRewriter.Options.parse(new String[] {
                    "--output-table", "awesterinen.wdqs_query_rewrites",
                    "--rerun-ids-table", "awesterinen.rewrite_rerun_ids"
                });

        assertTrue(options.isExplicitRerun());
        assertEquals("awesterinen.rewrite_rerun_ids",
                options.rerunIdsTable());
    }

    @Test
    void rejectsUnsafeOrInvalidOptions() {
        assertThrows(IllegalArgumentException.class,
                () -> SparkCorpusRewriter.Options.parse(new String[0]));
        assertThrows(IllegalArgumentException.class,
                () -> SparkCorpusRewriter.Options.parse(new String[] {
                    "--source-table", "awesterinen.same",
                    "--output-table", "awesterinen.same"
                }));
        assertThrows(IllegalArgumentException.class,
                () -> SparkCorpusRewriter.Options.parse(new String[] {
                    "--output-table", "awesterinen.output",
                    "--batch-count", "4",
                    "--batch-index", "4"
                }));
    }

    @Test
    void projectsUnchangedResultIntoCorpusSchema() {
        String query = "SELECT * WHERE { ?s ?p ?o }";

        SparkCorpusRewriter.CorpusRewrite result =
                SparkCorpusRewriter.rewrite("query-1", query);

        assertEquals("query-1", result.getQueryId());
        assertEquals(query, result.getOriginalQuery());
        assertEquals(query, result.getRewrittenQuery());
        assertEquals("unchanged", result.getRewriteStatus());
        assertEquals("[ ]", result.getRewrites());
        assertEquals("[ ]", result.getWarnings());
        assertEquals("[ ]", result.getErrors());
        assertTrue(result.getRewrittenDt() != null);
    }

    @Test
    void projectsNullRewrittenQueryForParseError() {
        SparkCorpusRewriter.CorpusRewrite result =
                SparkCorpusRewriter.rewrite("query-2", "SELECT WHERE {");

        assertEquals("parse_error", result.getRewriteStatus());
        assertNull(result.getRewrittenQuery());
    }

    @Test
    void projectsRewrittenResultAndRuleRecords() {
        String query = "PREFIX hint: <http://www.bigdata.com/queryHints#>\n"
                + "SELECT * WHERE {\n"
                + "  ?s ?p ?o .\n"
                + "  hint:Query hint:optimizer \"None\" .\n"
                + "}";

        SparkCorpusRewriter.CorpusRewrite result =
                SparkCorpusRewriter.rewrite("query-3", query);

        assertEquals("rewritten", result.getRewriteStatus());
        assertFalse(result.getRewrittenQuery().contains("queryHints"));
        assertTrue(result.getRewrites().contains("rewrite-query-hint"));
    }

    @Test
    void buildsExplicitColumnMerge() {
        String sql = SparkCorpusRewriter.mergeSql(
                "awesterinen.wdqs_query_rewrites");

        assertTrue(sql.startsWith(
                "MERGE INTO awesterinen.wdqs_query_rewrites AS target"));
        assertTrue(sql.contains("ON target.query_id = source.query_id"));
        assertTrue(sql.contains("WHEN MATCHED THEN UPDATE SET"));
        assertTrue(sql.contains("WHEN NOT MATCHED THEN INSERT"));
    }
}
