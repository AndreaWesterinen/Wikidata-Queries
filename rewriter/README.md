# WDQS SPARQL Rewriter

Last revision: 27 July 2026

## Overview

This experimental rewriter converts Blazegraph-specific Wikidata Query Service queries to SPARQL 1.1 and GeoSPARQL.

Java owns the rewrite engine. Apache Jena ARQ 4.10.0 parses the complete query, compiles unoptimized algebra for binding analysis, applies rewrite rules to Jena syntax objects, serializes canonical SPARQL, and reparses after each rule. Python only keeps the Java process alive and provides command-line and batch orchestration.

- [README_java_rewriter.md](README_java_rewriter.md) describes the Java engine.
- [README_python_rewriter.md](README_python_rewriter.md) describes the Python boundary.
- [README_interface_python_java.md](README_interface_python_java.md) defines the JSONL process protocol interfacing the Python and Java code.
- [README-rewrites.md](README-rewrites.md) records implemented feature behavior.

## License

Project-authored source, documentation, and fixtures are released under
[CC0 1.0 Universal](../LICENSE). Third-party dependencies retain their own
licenses. They are not vendored in this source directory; a generated shaded
JAR carries dependency license and notice files under `META-INF/`.

## Current status

The engine implements:

- 32 checked-in `wikibase:label` fixtures: 24 successful rewrites and 8 expected unsupported or Blazegraph-error outcomes
  - Coverage includes manual and automatic modes, labels, descriptions, constrained aliases, language normalization, fixed IRIs, unbound entities, pre-bound outputs, repeated services, nested query scopes, and collision-free variables

The parser supplies WDQS predefined prefixes only to parse input that uses a prefix without declaring it. The WDQS engine prefixes `bd`, `gas`, `hint`, and `mwapi` are forbidden in rewritten output, even when the source declares them. The engine removes those prefix names during serialization. Other surviving QNames receive explicit declarations so that successful output is standalone. An `unchanged` result preserves the exact source text and is not a rewritten query.

Cataloged proprietary features without implementations are returned atomically
as `skipped_unsupported`; they are never reported as `unchanged`. Top-level
`FROM` and `FROM NAMED` clauses are also rejected because Wikidata has no named
graphs to select into a query dataset. Named `GRAPH` patterns may still
occur inside federated queries.

## Rewrite rule index

This catalog identifies the planned rewrite rules. Reference paths name their
rule-contract documents; those documents are maintained separately.

| Rule id | Feature | Type | Parse phase | Reference | Notes |
|---|---|---|---|---|---|
| rewrite-gas-service | `SERVICE gas:service` | SERVICE | post-ARQ | `services/gas-service.md` | Standard SERVICE syntax, proprietary behavior. |
| rewrite-wikibase-box-service | `SERVICE wikibase:box` | SERVICE | post-ARQ | `services/wikibase-box.md` | Standard SERVICE syntax, proprietary behavior. |
| rewrite-wikibase-around-service | `SERVICE wikibase:around` | SERVICE | post-ARQ | `services/wikibase-around.md` | Standard SERVICE syntax, proprietary behavior. |
| rewrite-wikibase-globe | `wikibase:globe()` | function | post-ARQ | `functions/wikibase-globe.md` | Standard function syntax, proprietary behavior. |
| rewrite-wikibase-latitude | `wikibase:latitude()` | function | post-ARQ | `functions/wikibase-latitude.md` | Standard function syntax, proprietary behavior. |
| rewrite-wikibase-longitude | `wikibase:longitude()` | function | post-ARQ | `functions/wikibase-longitude.md` | Standard function syntax, proprietary behavior. |
| rewrite-geof-distance | `geof:distance()` | function | post-ARQ | `functions/geof-distance.md` | Standard function syntax, but no units parameter. |
| rewrite-bd-sample-service | `SERVICE bd:sample` | SERVICE | post-ARQ | `services/bd-sample.md` | Standard SERVICE syntax, proprietary behavior. |
| rewrite-bd-slice-service | `SERVICE bd:slice` | SERVICE | post-ARQ | `services/bd-slice.md` | Standard SERVICE syntax, proprietary behavior. |
| rewrite-named-subquery | `WITH { ... } AS %name` / `INCLUDE %name` | syntax | pre-ARQ | `syntax/named-subquery.md` | Non-standard grammar. |
| rewrite-query-hint | `hint:` | syntax | pre-ARQ | `syntax/query-hint.md` | Engine directive, non-standard grammar. |
| rewrite-wikibase-decodeURI | `wikibase:decodeURI()` | function | post-ARQ | `functions/wikibase-decodeURI.md` | Standard function syntax, proprietary behavior. |
| rewrite-wikibase-label-service | `SERVICE wikibase:label` | SERVICE | post-ARQ | `services/wikibase-label.md` | Standard SERVICE syntax, proprietary behavior. |
| rewrite-mwapi-service | `SERVICE wikibase:mwapi` | SERVICE | post-ARQ | `services/wikibase-mwapi.md` | Standard SERVICE syntax, proprietary behavior. |

## Requirements

- Python 3.10 or later
- Maven
- JDK 11 or later

The rewriter runtime is Java 11 with Jena ARQ 4.10.0. Set `SPARQL_REWRITER_JAVA` to the Java 11 executable when `java` on `PATH` is a different version. Because the rewriter runs as a child process, this does not require changing the JVM used by Spark itself.

## Build

```bash
mvn -f java-rewriter/pom.xml \
  -Dmaven.repo.local=.m2 \
  clean package
```

The executable artifact is `java-rewriter/target/sparql-rewriter.jar`.

## Usage

All three command-line scripts require the executable rewriter JAR. They use `java-rewriter/target/sparql-rewriter.jar` by default; pass `--rewriter-jar PATH` only when the JAR is stored elsewhere.

### Rewrite one file to standard output:

```bash
python3 rewrite_query.py path/to/original-query.rq
```

Save it explicitly:

```bash
python3 rewrite_query.py path/to/original-query.rq > path/to/rewritten-query.rq
```

### Rewrite every ordinary `.rq` file recursively under a directory:

```bash
python3 rewrite_queries.py path/to/queries --output-dir path/to/output
```

### Rewrite every `.original.rq` recursively under `fixtures/`:

```bash
python3 rewrite_fixtures.py
```

Generated files normally go under the gitignored `generated/` directory. Use `--output-dir PATH` to choose another location. Replace checked-in golden files only after review:

```bash
python3 rewrite_fixtures.py --update-golden
```

## Tests

Run the Java unit suite:

```bash
mvn -f java-rewriter/pom.xml \
  -Dmaven.repo.local=.m2 \
  test
```

The Java suite directly tests algebra binding analysis, rule-specific analysis and atomic failure behavior, rewrite-engine statuses and reparsing, and JSONL protocol validation.

Build the JAR, then run the Python unit, process-boundary, and fixture suites:

```bash
mvn -f java-rewriter/pom.xml \
  -Dmaven.repo.local=.m2 \
  package
python3 -m unittest discover -s tests -v
```

Python unit tests cover protocol validation, process ownership and reuse, ordered batches, and fixture paths without starting Java.

Integration tests check the process boundary and all fixture records through the executable Java process.

Semantic acceptance additionally requires the existing `sparql-diff` harness against Blazegraph and QLever.

## Security boundary

The Java engine parses, compiles, transforms, and serializes queries. It must not execute algebra, evaluate extension functions, contact endpoints, or be exposed directly as a public network service.

Jena ARQ 4.10.0 is used with Java 11. The rewrite process does not enable or evaluate JavaScript SPARQL functions.

## Layout

```text
java-rewriter/                         Java 11 rewrite engine
python/sparql_rewriter/java_client.py  Persistent JSONL process client
python/sparql_rewriter/rewrite.py      Single-query and local-batch orchestration
fixtures/                              Inputs, manifests, and golden outputs
tests/                                 Boundary and fixture regression tests
rewrite_query.py                       File-in, query-out CLI
rewrite_queries.py                     Recursive query-directory CLI
rewrite_fixtures.py                    Recursive fixture runner
```

## Adding a rewrite

Rule detection and transformation is implemented in Java and added to `SparqlRewriteEngine.applyNextRule()` in deterministic order. A rule operates on the current parsed query and returns either no match, one successful rule step, or a terminal classification. The engine serializes and reparses after every successful step before looking for another rule.

Query-log distribution, retries, database writes, and `sparql-diff` execution is outside the Java engine.

For corpus integration, use `iter_rewrite_queries()` inside each Spark partition.
It streams ordered results, caps each child JVM heap at 512 MiB by default, and
periodically replaces the persistent process. Database retries, checkpoints,
and Iceberg upserts remain outside this repository.
