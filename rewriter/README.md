# WDQS SPARQL Rewriter

Last revision: 31 July 2026

## Overview

This rewriter converts Blazegraph-specific Wikidata Query Service queries to SPARQL 1.1 and GeoSPARQL.

The rewrite engine is Java-based. Apache Jena ARQ 4.10.0 parses the complete query, compiles unoptimized algebra for binding analysis, applies rewrite rules to the SPARQL syntax objects, serializes the resulting SPARQL, and reparses after each rule. 

Separately, there is a Spark Java runner that reads the 3 days of query logs. It embeds the Jena engine directly in the Spark executor JVMs.

These components are documented in the following files:

- docs/README_java_rewriter.md describes the Java rewrite engine.
- docs/README_spark_corpus.md describes the Spark Java corpus/log processing, which uses the Java rewrite engine

Python is retained only for local command-line testing, golden-fixture generation, and regression tests of the executable JAR boundary. It is not part of the Spark corpus runtime and does not provide a supported production integration API. Its use is described later in this README.

### Rewrite Rule Index

The catalog below identifies the various rewrite rules. Reference paths indicate their documentation files. The individual rule implementations are in the `java-rewriter/src/main/java/.../rewriter/rules` directory.

| Rule id | Feature | Type | Parse phase | Reference | Notes |
|---|---|---|---|---|---|
| rewrite-gas-service | `SERVICE gas:service` | SERVICE | post-ARQ | `docs/services/gas-service.md` | Standard SERVICE syntax, proprietary behavior |
| rewrite-wikibase-box-service | `SERVICE wikibase:box` | SERVICE | post-ARQ | `docs/services/wikibase-box.md` | Standard SERVICE syntax, proprietary behavior |
| rewrite-wikibase-around-service | `SERVICE wikibase:around` | SERVICE | post-ARQ | `docs/services/wikibase-around.md` | Standard SERVICE syntax, proprietary behavior |
| rewrite-wikibase-globe | `wikibase:globe()` | function | post-ARQ | `docs/functions/wikibase-globe.md` | Standard function syntax, proprietary behavior |
| rewrite-wikibase-latitude | `wikibase:latitude()` | function | post-ARQ | `docs/functions/wikibase-latitude.md` | Standard function syntax, proprietary behavior |
| rewrite-wikibase-longitude | `wikibase:longitude()` | function | post-ARQ | `docs/functions/wikibase-longitude.md` | Standard function syntax, proprietary behavior |
| rewrite-geof-distance | `geof:distance()` | function | post-ARQ | `docs/functions/geof-distance.md` | Standard function syntax, but no units parameter |
| rewrite-bd-sample-service | `SERVICE bd:sample` | SERVICE | post-ARQ | `docs/services/bd-sample.md` | Standard SERVICE syntax, proprietary behavior |
| rewrite-bd-slice-service | `SERVICE bd:slice` | SERVICE | post-ARQ | `docs/services/bd-slice.md` | Standard SERVICE syntax, proprietary behavior |
| rewrite-named-subquery | `WITH { ... } AS %name` / `INCLUDE %name` | syntax | pre-ARQ | `docs/syntax/named-subquery.md` | Non-standard grammar |
| rewrite-query-hint | `hint:` | syntax | pre-ARQ | `docs/syntax/query-hint.md` | Engine directive, non-standard grammar |
| rewrite-wikibase-decodeURI | `wikibase:decodeURI()` | function | post-ARQ | `docs/functions/wikibase-decodeURI.md` | Standard function syntax, proprietary behavior |
| rewrite-wikibase-label-service | `SERVICE wikibase:label` | SERVICE | post-ARQ | `docs/services/wikibase-label.md` | Standard SERVICE syntax, proprietary behavior |
| rewrite-mwapi-service | `SERVICE wikibase:mwapi` | SERVICE | post-ARQ | `docs/services/wikibase-mwapi.md` | Standard SERVICE syntax, proprietary behavior |

## License

Project-authored source, documentation, and fixtures are released under the [Apache License, Version 2.0](LICENSE). Third-party dependencies retain their own licenses. They are not vendored in this source directory. The generated shaded JAR carries its dependency license and notice files under `META-INF/`.

## Overall Directory Structure 

```text
docs/								   Documentation
spark-corpus/                          Resumable Spark Java corpus processor
java-rewriter/                         Java 11 rewrite engine
fixtures/                              Inputs, manifests, and golden outputs
tests/                                 Boundary and fixture regression tests
rewrite.py                             Shared local Python test implementation
rewrite_query.py                       File-in, query-out CLI
rewrite_queries.py                     Recursive query-directory CLI
rewrite_fixtures.py                    Recursive fixture runner
```

## Requirements

- Maven
- JDK 11 or later

Python 3.10 or later is required only for the optional local scripts. The rewriter runtime is Java 11 with Jena ARQ 4.10.0. 

## Build

```bash
mvn -f java-rewriter/pom.xml \
  -Dmaven.repo.local=.m2 \
  clean package
```

The executable artifact is `java-rewriter/target/sparql-rewriter.jar`.

## Local Test Tools

Three, optional Python CLI scripts exercise the executable JAR locally. They use `java-rewriter/target/sparql-rewriter.jar` by default.

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

Generated files normally go under the gitignored `generated/` directory. Use `--output-dir PATH` to choose another location. 

To regenerate fixture results (generated files replace the checked-in golden files):

```bash
python3 rewrite_fixtures.py --update-golden
```

## Unit and Integration Tests

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

The Python tests are local regression tests for basic protocol handling, process reuse, fixture paths, and execution of the checked-in fixtures through the JAR. 

## Current Status

The rewrite implementation currently includes:

- Pre-parse removal of repeated Blazegraph `hint:Query`, `hint:Prior`, `hint:Group`, `hint:GroupAndSubGroups`, and `hint:SubQuery` statements, including resolved prefix aliases
- Top-level `FROM` and `FROM NAMED` clauses rejected (Wikidata has no defined named graphs)
  - Named `GRAPH` patterns can occur inside federated queries
- 34 checked-in `wikibase:label` test fixtures: 30 successful rewrites and 4 expected unsupported or Blazegraph-error outcomes
  - Coverage includes manual and automatic modes, labels, descriptions, constrained aliases, language normalization, fixed IRIs, unbound entities, pre-bound outputs, repeated services, mixed nested query scopes, and collision-free variables

`sparql-diff` execution for verification of the rewrite queries remains outside this repository.
