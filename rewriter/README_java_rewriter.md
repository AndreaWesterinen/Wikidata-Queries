# Java Rewrite Engine

Last revision: 27 July 2026

## Responsibility

The `java-rewriter` module owns parsing, rule detection, binding analysis, syntax mutation, canonical serialization, status assignment, and reparse validation. It uses Jena ARQ 4.10.0 directly on Java 11; Jena objects do not cross the process boundary.

```text
JSONL request
    ↓
SparqlRewriteServer
    ↓
SparqlRewriteEngine
    ├─ strict SPARQL 1.1 parse
    ├─ reject top-level dataset clauses
    ├─ classify unimplemented proprietary features
    ├─ apply one rule to Jena syntax
    ├─ serialize and reparse
    └─ repeat until no rule applies
    ↓
Single-Query Rewrite Result
```

## Files

| File | Responsibility |
|---|---|
| [`SparqlRewriteServer.java`](java-rewriter/src/main/java/io/github/andreawesterinen/wikidata/rewriter/SparqlRewriteServer.java) | Persistent JSONL process and protocol envelope |
| [`SparqlRewriteEngine.java`](java-rewriter/src/main/java/io/github/andreawesterinen/wikidata/rewriter/SparqlRewriteEngine.java) | Parse, rule loop, serialization, reparse, and result aggregation |
| [`BindingAnalysis.java`](java-rewriter/src/main/java/io/github/andreawesterinen/wikidata/rewriter/BindingAnalysis.java) | Definite and possible incoming bindings from unoptimized algebra |
| [`RewriteResult.java`](java-rewriter/src/main/java/io/github/andreawesterinen/wikidata/rewriter/RewriteResult.java) | Internal result assembly and public JSON representation |
| [`WdqsPrefixes.java`](java-rewriter/src/main/java/io/github/andreawesterinen/wikidata/rewriter/WdqsPrefixes.java) | Used-only WDQS parser defaults with explicit-binding precedence |
| [`UnsupportedFeatureDetector.java`](java-rewriter/src/main/java/io/github/andreawesterinen/wikidata/rewriter/UnsupportedFeatureDetector.java) | Atomic pre/post-parse classification for unimplemented proprietary features |

Rewrite rules live in the flat `rules/` package:

| File | Responsibility |
|---|---|
| [`WikibaseLabelRewriter.java`](java-rewriter/src/main/java/io/github/andreawesterinen/wikidata/rewriter/rules/WikibaseLabelRewriter.java) | `wikibase:label` detection and syntax transformation |

## Rule loop

`SparqlRewriteEngine` parses the query and checks rewrite rules in deterministic order. After a rule changes the query, the engine serializes and reparses the complete query before checking for the next rule. A terminal status stops processing without returning a partial rewrite.

## Syntax changes

Each rule identifies features by resolved IRI and changes Jena syntax objects. Rules may insert, replace, or remove syntax elements as required by their rewrite contracts. Post-parse rules do not splice ranges in the original source text.

Jena provides canonical serialization. The engine sorts `PREFIX` declarations by prefix name, with the default prefix first, before reparsing. Comments, original formatting, and original escape spellings are not preserved unless Jena preserves them.

WDQS implicit prefixes are supplied only while parsing original input. The
engine-only prefix names `bd`, `gas`, `hint`, and `mwapi` are removed before
serializing a successful rewrite, even if the source declared them. Other
surviving QNames are serialized with explicit declarations.

## Binding analysis

`BindingAnalysis` compiles the complete current query with `Algebra.compile`; it never calls `Algebra.optimize`. It calculates definite and possible variables in evaluation order around `JOIN`, `SEQUENCE`, `LEFT JOIN`, `UNION`, `VALUES`, `BIND`, grouping, projection, paths, and related operators.

Jena does not retain the original `ElementService` identity in every compiled shape used by the rewriter. For analysis, Java temporarily replaces only the target SERVICE endpoint with a collision-free internal IRI, compiles the query, locates that marker in algebra, and immediately restores the original syntax in a `finally` block. The marker is never serialized or returned.

## Failure boundary

Original syntax failures return `parse_error`. Cataloged non-standard grammar is
classified before parsing, and parsed proprietary features are classified by
resolved IRI. Root `FROM` and `FROM NAMED` clauses return
`skipped_unsupported`; `GRAPH` patterns inside federated queries remain valid.
A generated query that does not reparse returns
`validation_failed`. Deterministic unsupported rule inputs return their
documented query status. Process startup, malformed JSONL, broken pipes, and
unexpected Java exceptions remain operational failures.

The engine does not execute algebra, evaluate functions, or contact endpoints.

## Tests

Java tests live under `java-rewriter/src/test/java` and run during `mvn test` and `mvn package`.

- `BindingAnalysisTest` covers definite and possible bindings from triples, OPTIONAL, UNION, VALUES, BIND, projected subqueries, marker collisions, and syntax restoration.
- `WikibaseLabelRewriterTest` covers language normalization, guard selection, alias safety, failure atomicity, fixed-IRI fallback, collision suffixes, and rule non-matches.
- `SparqlRewriteEngineTest` covers unchanged input, parse errors, top-level dataset rejection, federated graph patterns, atomic unsupported-feature classification, successful rewrite records, and generated-query reparsing.
- `SparqlRewriteServerTest` covers successful envelopes, request identity, protocol versions, missing fields, hashes, and the separation between transport success and query failure.

The Python suite separately unit-tests orchestration and protocol validation, then exercises persistent JVM reuse and every checked-in fixture through the executable JAR.
