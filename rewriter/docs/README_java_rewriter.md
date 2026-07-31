# Java Rewrite Engine

Last revision: 31 July 2026

## Responsibility

The `java-rewriter` module owns parsing, rule detection, binding analysis, syntax mutation, query serialization, status assignment, and reparse validation. It uses Jena ARQ 4.10.0 directly on Java 11. Jena objects remain inside Java whether the engine is called through JSONL or embedded in a Spark executor.

```text
JSONL request                 Embedded string request
    ↓                              ↓
SparqlRewriteServer          EmbeddedSparqlRewriter
    └────────────────┬────────────────┘
                    ↓
          SparqlRewriteEngine
                    ├─ apply ordered source-text rules
                    ├─ classify remaining unsupported pre-parse syntax
                    ├─ add used WDQS parser defaults and parse as SPARQL 1.1
                    ├─ reject top-level dataset clauses
                    ├─ classify remaining proprietary features in Jena syntax
                    ├─ apply one parsed-query rule
                    ├─ serialize and reparse
                    └─ repeat until no rule applies
                    ↓
       Single-Query Rewrite Result
```

## Files

| File | Responsibility |
|---|---|
| [`EmbeddedSparqlRewriter.java`](../java-rewriter/src/main/java/io/github/andreawesterinen/wikidata/rewriter/EmbeddedSparqlRewriter.java) | String-in/JSON-string-out entry point used by the Spark corpus runner; calls and serializes `SparqlRewriteEngine` |
| [`SparqlRewriteServer.java`](../java-rewriter/src/main/java/io/github/andreawesterinen/wikidata/rewriter/SparqlRewriteServer.java) | Exposes the rewriter engine as a long-running command-line process for the local Python tools |
| [`SparqlRewriteEngine.java`](../java-rewriter/src/main/java/io/github/andreawesterinen/wikidata/rewriter/SparqlRewriteEngine.java) | Owns the ordered pre-parse pipeline, parsed-query loop, serialization, reparse validation, and final result aggregation for one query |
| [`BindingAnalysis.java`](../java-rewriter/src/main/java/io/github/andreawesterinen/wikidata/rewriter/BindingAnalysis.java) | Compiles the current query with Jena's `Algebra.compile` and determines which variables are definitely bound, possibly bound or unbound when evaluation reaches a particular SERVICE |
| [`RewriteResult.java`](../java-rewriter/src/main/java/io/github/andreawesterinen/wikidata/rewriter/RewriteResult.java) | Result accumulator with private state, defensive collection accessors, public rule-level additions, and package-level engine outcome/aggregation methods |
| [`PreParseRule.java`](../java-rewriter/src/main/java/io/github/andreawesterinen/wikidata/rewriter/PreParseRule.java) | Internal handoff contract for ordered source-text rules that run before the first SPARQL parse |
| [`WdqsPrefixes.java`](../java-rewriter/src/main/java/io/github/andreawesterinen/wikidata/rewriter/WdqsPrefixes.java) | Defines WDQS parser defaults, explicit-binding precedence, and standalone output prefix handling |
| [`UnsupportedFeatureDetector.java`](../java-rewriter/src/main/java/io/github/andreawesterinen/wikidata/rewriter/UnsupportedFeatureDetector.java) | Classifies remaining unsupported pre-parse grammar and post-parse services, functions, and proprietary RDF-term namespaces; it performs no rewrites |

Rewrite rules live in the flat `rules/` package:

| File | Responsibility |
|---|---|
| [`QueryHintRewriter.java`](../java-rewriter/src/main/java/io/github/andreawesterinen/wikidata/rewriter/rules/QueryHintRewriter.java) | Pre-parse removal of resolved query-hint statements with a supported Query, Prior, Group, GroupAndSubGroups, or SubQuery subject |
| [`WikibaseLabelRewriter.java`](../java-rewriter/src/main/java/io/github/andreawesterinen/wikidata/rewriter/rules/WikibaseLabelRewriter.java) | `wikibase:label` detection and syntax transformation |

### The SparqlRewriteEngine

Its processing flow is:

```text
Original query text
  → Apply each ordered pre-parse rule to the current source text
  → Detect unsupported pre-parse syntax that remains
  → Add required WDQS parser prefixes
  → Parse with Jena
  → Reject FROM/FROM NAMED
  → Detect and apply one parsed-query rule
  → Serialize
  → Reparse
  → Repeat until no rule applies
  → Return RewriteResult
```

#### Pre-Parse Handoff

`SparqlRewriteEngine.preParseRules()` defines a deterministic list. Each registered rule is invoked once, in order, with the text produced by the preceding rule. Through `PreParseRule`, a rule returns:

- `null` when it does not apply 
- a `rewritten` `RewriteResult` containing changed source text and its rewrite record
- a terminal problem/error `RewriteResult`

After every implemented pre-parse rule has declined or completed, `UnsupportedFeatureDetector.detectPreParse()` classifies non-standard grammar that remains. 

#### Parsed-Query Handoff

After the pre-parse fallback succeeds, the engine supplies required, undeclared WDQS parser prefixes and parses the complete current text with Jena. It rejects top-level `FROM` and `FROM NAMED`, then `SparqlRewriteEngine.applyNextRule()` checks for unsupported proprietary syntax and otherwise invokes the rewriter rules in the order specified in that function. This ordering prevents a supported rule from producing a partial rewrite when another proprietary feature remains unsupported.

A successful parsed-query step is appended to the accumulated result, then the complete query is serialized and reparsed before rule detection repeats. An error or problematic rule result is returned without a partial rewritten query. A generated-query parse failure becomes `validation_failed` while retaining the rewrite records accumulated before validation failed.

A rewritten query is canonically serialized by Jena, has prefixes cleaned and sorted, and ends with a newline. An unchanged query returns the exact original text. 

Note that the engine is package-private. External callers use either EmbeddedSparqlRewriter or SparqlRewriteServer, which prevents Jena objects and mutable internal results from becoming a public integration API.

### The RewriteResult

It stores:

- status
- rewrittenQuery
- ordered rewrites (both the rule and the specific variant)
- ordered warnings
- ordered errors

It is used at two levels:

1. A rule returns a step result, such as one successful manual label rewrite.
2. The engine accumulates those step results into the final query result.

All fields are private. Rule classes add rewrite, warning, and error records through public methods. Engine classes use package-private methods to append a complete step and set the final status/query outcome. Collection accessors return defensive copies, so callers cannot mutate accumulated state indirectly.

The query ID is not stored in the object. However, it is supplied when toJson(queryId) creates the public representation:

```text
  {
    "schema_version": 1,
    "contract_version": 1,
    "query_id": "query-123",
    "rewrites": [
      {
        "rule_id": "rewrite-wikibase-label-service",
        "variant_id": "automatic"
      }
    ],
    "rewrite_status": "rewritten",
    "rewritten_query": "SELECT ...",
    "warnings": [],
    "errors": []
  }
```

An invalid original query returns a `parse_error` status, with `rewritten_query` set to `null`, and an error object with code `original_query_parse_error`. Other general engine error codes are `rewritten_query_parse_error` (indicating invalid generated output), `unsupported_proprietary_feature` (indicating an unknown feature), and `unsupported_dataset_clause` (indicating the inclusion of a top-level FROM/FROM NAMED clause). Rule-specific documentation defines rule-specific codes.

Note that the rewritten query is null for terminal outcomes that produce no query such as parse_error, skipped_unsupported, and validation_failed.

#### Rewrite Status Outputs

| Condition | Status |
|---|---|
| Valid SPARQL with no proprietary features | `unchanged` |
| Successful, valid rewritten query returned | `rewritten` |
| Invalid original SPARQL | `parse_error` (`original_query_parse_error`) |
| Valid SPARQL but invalid Blazegraph semantics/parse error | `blazegraph_error` |
| Rewrite results in unsafe/unconstrained/invalid queries | `skipped_unsupported` |
| Proprietary features without rewrite rule implementations | `skipped_unsupported` |
| Rewrite cannot be generated due to unclear or conflicting rewrite rules | `ambiguous_conflicting` |
| Rewrite generated but fails SPARQL validation | `validation_failed` |

## Tests

Java tests are defined in the directory, `java-rewriter/src/test/java` and run during `mvn test` and `mvn package`.

- `BindingAnalysisTest` covers definite and possible bindings from triples, OPTIONAL, UNION, VALUES, BIND, projected subqueries, marker collisions, and syntax restoration.
- `EmbeddedSparqlRewriterTest` covers JSON result serialization and null validation at the embedded public boundary.
- `PreParseRuleTest` covers deterministic rule ordering, one-pass application, non-match continuation, terminal-result propagation, ordered rewrite records, and rejection of null or unchanged successful output.
- `RewriteResultTest` covers defensive collection access and complete step aggregation.
- `UnsupportedFeatureDetectorTest` covers named-subquery lexical masking, every cataloged service and function, unknown proprietary features, nested expressions, one representative remaining proprietary term, and allowed standard cases.
- `WdqsPrefixesTest` covers used-only parser defaults, explicit prefix precedence, implicit label-vocabulary expansion, forbidden output prefix removal, and unused prefix removal.
- `SparqlRewriteEngineTest` covers unchanged input, parse errors, ordered hint/label composition, named-subquery fallback classification, surviving proprietary-term rejection, top-level dataset rejection, federated graph patterns, atomic unsupported-feature classification, nested scope modes, implicit output-prefix expansion, successful rewrite records, and generated-query reparsing.
- `SparqlRewriteServerTest` covers successful envelopes, request identity, protocol versions, missing fields, hashes, and the separation between transport success and query failure.

Rule-specific tests are found under `java-rewriter/src/test/java/io/github/andreawesterinen/wikidata/rewriter/rules_tests`:

- `QueryHintRewriterTest` covers all five supported hint subjects, repeated hints, arbitrary hint predicates, aliases, full IRIs, rebound prefixes, non-subject terms, inert lexical regions, nested scopes, and source offsets after non-BMP characters.
- `WikibaseLabelRewriterTest` covers language normalization and graph-pattern-group isolation, binding guards, alias safety, ASK, CONSTRUCT, and DESCRIBE forms, atomic failures, IRI fallback, variable collisions, nested scope modes, and non-matches.

The local Python suite checks basic process orchestration, then exercises JVM reuse and every checked-in fixture through the executable JAR.
