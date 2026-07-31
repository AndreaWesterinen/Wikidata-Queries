# Spark Java Corpus Rewriter

Last revision: 31 July 2026

## Purpose

The `spark-corpus` module applies the embedded Java 11/Jena 4.10.0 rewriter to `awesterinen.wdqs_query_logs` on YARN and stores the rewritten queries to `awesterinen.wdqs_query_rewrites`. 

Spark owns Iceberg access, distribution, task retries, and commits. Each executor task rewrites its input iterator in
the executor JVM and does not start a child JVM.

## Processing modes

Normal mode is resumable. The runner left-anti-joins the selected source IDs against IDs already present in the output table, rewrites only missing rows, and appends one Iceberg snapshot. Repeating a completed batch does not create duplicate rows.

Explicit rerun mode takes `--rerun-ids-table TABLE`. That table must contain a non-null `query_id` column. The runner selects those source rows and uses an Iceberg `MERGE` to replace existing records or insert missing records.

`--batch-count N --batch-index I` selects IDs for which `pmod(xxhash64(id), N) = I` (indexes start at zero). Keep the batch count, `N`, unchanged across a run. The default is one batch (`N=1`, `I=0`). Hash batching provides separate commit checkpoints, but every batch still scans the source table because the source is not partitioned by this hash. 

Run batches sequentially, and do NOT launch concurrent writers to the same output table.

## Output table

The production/output table is created once on `stat1008`:

```bash
spark3-sql --database awesterinen -e "
CREATE EXTERNAL TABLE wdqs_query_rewrites (
  query_id STRING,
  original_query STRING,
  rewritten_query STRING,
  rewrites STRING,
  rewrite_status STRING,
  warnings STRING,
  errors STRING,
  rewritten_dt TIMESTAMP
)
USING ICEBERG
LOCATION '/user/andreawest/query_logs/wdqs_query_rewrites/'
TBLPROPERTIES (
  'write.distribution-mode'='range',
  'write.parquet.compression-codec'='zstd'
);

ALTER TABLE wdqs_query_rewrites
WRITE ORDERED BY rewrite_status, query_id;
"
```

The runner requires the source and output tables to exist. It never creates, replaces, or modifies the source table.

## Build and stage

Build the Maven reactor with Java 11:

```bash
export JAVA_HOME=/absolute/path/to/jdk-11
export PATH="$JAVA_HOME/bin:$PATH"
mvn -Dmaven.repo.local=.m2 clean package
```

Copy the production artifact to the analytics client:

```bash
scp spark-corpus/target/sparql-rewriter-spark-corpus.jar \
  andreawest@stat1008.eqiad.wmnet:/home/andreawest/sparql-rewriter-spark-corpus.jar
```

Spark and Hadoop are provided by the cluster and are not bundled in the JAR. The rewriter, Jena, and their runtime dependencies are bundled.

## Submit a normal batch

On `stat1008`, enter a named `tmux` session and select Java 11:

```bash
tmux new -s wdqs-rewriter-corpus
export JAVA_HOME=/usr/lib/jvm/java-11-openjdk-amd64
export PATH="$JAVA_HOME/bin:$PATH"
java -version
```

Submit batch zero of a single-batch run:

```bash
spark3-submit \
  --master yarn \
  --deploy-mode cluster \
  --class io.github.andreawesterinen.wikidata.rewriter.corpus.SparkCorpusRewriter \
  --name wdqs-sparql-corpus-rewriter \
  --conf spark.yarn.appMasterEnv.JAVA_HOME=/usr/lib/jvm/java-11-openjdk-amd64 \
  --conf spark.executorEnv.JAVA_HOME=/usr/lib/jvm/java-11-openjdk-amd64 \
  --conf spark.driver.extraJavaOptions="--add-opens=java.base/java.lang=ALL-UNNAMED --add-opens=java.base/java.invoke=ALL-UNNAMED" \
  --conf spark.executor.extraJavaOptions="--add-opens=java.base/java.lang=ALL-UNNAMED --add-opens=java.base/java.invoke=ALL-UNNAMED" \
  --conf spark.sql.session.timeZone=UTC \
  --conf spark.driver.memory=32g \
  --conf spark.executor.memory=32g \
  --conf spark.executor.memoryOverhead=4g \
  --conf spark.executor.cores=4 \
  --conf spark.dynamicAllocation.enabled=true \
  --conf spark.dynamicAllocation.minExecutors=4 \
  --conf spark.dynamicAllocation.initialExecutors=8 \
  --conf spark.dynamicAllocation.maxExecutors=60 \
  --conf spark.sql.shuffle.partitions=1024 \
  sparql-rewriter-spark-corpus.jar \
  --source-table awesterinen.wdqs_query_logs \
  --output-table awesterinen.wdqs_query_rewrites \
  --batch-count 1 \
  --batch-index 0
```

For multiple checkpoints, choose a batch count and submit every index from zero through `batch-count - 1` sequentially. Record the batch count, index, application ID, code revision, and output table for each submission.

## Explicit rerun

Create or populate an Iceberg table whose `query_id` values are the exact rows to replace. Then submit the same application with:

```text
--rerun-ids-table awesterinen.wdqs_rewriter_rerun_ids
```

Explicit rerun mode requires Iceberg `MERGE` support in the Spark session.

## Verify

Check for duplicate IDs and inspect status counts:

```bash
spark3-sql --database awesterinen -e "
SELECT COUNT(*) AS stored_rows,
       COUNT(DISTINCT query_id) AS distinct_ids
FROM wdqs_query_rewrites;

SELECT rewrite_status, COUNT(*) AS rows
FROM wdqs_query_rewrites
GROUP BY rewrite_status
ORDER BY rewrite_status;
"
```

Verify completeness after all batches:

```bash
spark3-sql --database awesterinen -e "
SELECT COUNT(*) AS missing_rows
FROM wdqs_query_logs source
LEFT ANTI JOIN wdqs_query_rewrites rewritten
  ON source.id = rewritten.query_id
WHERE source.id IS NOT NULL
  AND source.query IS NOT NULL;
"
```

Rerunning a completed normal batch should leave the stored row count unchanged.

Deterministic `parse_error`, `skipped_unsupported`, `ambiguous_conflicting`, `validation_failed`, and `blazegraph_error` results are stored as outcomes. They are not infrastructure failures and are not automatically retried. 
