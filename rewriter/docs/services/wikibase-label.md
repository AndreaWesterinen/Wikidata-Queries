# rewrite-wikibase-label-service

Last Revision: 31 July 2026

## Supporting Evidence

- Documentation page and section: https://wikitech.wikimedia.org/wiki/Blazegraph_Migration:_Rewrite_of_Label_and_Utility_Services_and_Functions#wikibase:label
- Retrieved date: 22 Jul 2026
- Reproducible observation: wrapping `LANG(...)` in `LCASE` caused the manual label-only QLever fixture to time out; direct `LANG(...) = "de"` filters completed successfully.

## Feature

- Type: SERVICE
- Name: `wikibase:label`
- Feature URI / canonical URI: `http://wikiba.se/ontology#label`
- Parse phase: post-ARQ
- Supported forms:
  - Manual mode: the service block contains explicit label, description, or alias triples that name the output variables.
  - Automatic mode: the service block contains no explicit output triples; projected variables with recognized suffixes define the requested outputs. One or more language parameters define the combined language-priority list.
  
Repeated instances allowed: yes

### Parameters / Arguments

- `bd:serviceParam wikibase:language <language-list>`
  - Resolved subject IRI: `http://www.bigdata.com/rdf#serviceParam`
  - Resolved predicate IRI: `http://wikiba.se/ontology#language`
  - Required: yes
  - Allowed values: a literal comma-separated language-priority list plus the literal, `[AUTO_LANGUAGE]`
  - Default: none
  - Meaning: within the immediately containing graph-pattern group, process all language predicates in source order, concatenate their comma-separated lists, normalize the combined token sequence, and use/bind the first language with a value
    - Meaning of `[AUTO_LANGUAGE]`: the UI substitutes the requester’s language; no equivalent exists outside that UI processing
- Manual output triple
  - Required: manual mode only
  - Allowed predicates:
    - `http://www.w3.org/2000/01/rdf-schema#label`
    - `http://schema.org/description`
    - `http://www.w3.org/2004/02/skos/core#altLabel`
  - Meaning: maps a variable or fixed IRI entity term and result type to the explicitly named output variable


## Logic Summary

The wikibase:label rewrite replaces Blazegraph’s label service with standard SPARQL patterns.

- Manual mode uses explicit rdfs:label, schema:description, or skos:altLabel output triples inside the service
- Automatic mode infers outputs from projected variables ending in Label, Description, or AltLabel
  - When applying automatic mode to `SELECT *` and `ASK` queries, Blazegraph's (and the rewrite) behavior is to essentially remove the label service without generating output bindings
  - When applying automatic mode to `DESCRIBE`, any projections of labels, descriptions, or aliases are removed because their literal values cannot be "described"
- Languages are normalized into priority order within the graph-pattern group where they are declared
  - The language literals are split on commas and the token sequences concatenated without reordering predicates or tokens; Whitespace is trimmed; Empty language strings are removed; Duplicate language strings are removed
  - Labels and aliases include a fall back to mul; descriptions do not
  - ['AUTO_LANGUAGE'] is preserved
- Ordered OPTIONAL patterns select the first available language
  - In addition, labels fall back to the Wikidata entity ID or full IRI
- Aliases use constrained GROUP_CONCAT(DISTINCT ...) subqueries
  - Aliases are aggregated for the first requested language with results
  - A variable alias entity requires a safe candidate relation derived from preceding, in-scope Jena syntax elements and verified against possible algebra bindings
    - If the entity cannot be bound when the service is reached, the alias output remains unbound and the service is removed
	- TODO: This is done to improve processing time; determine if the restriction should be removed
- Binding analysis prevents unconstrained lookups and preserves already-bound output variables
  - If an entity is may be unbound, a label or description lookup is 'guarded' using the following statement, `BIND(IF(BOUND(?entity), ?entity, "unbound") AS ?entity__label_subject)`
  - Alias-only output does not require a guard since its candidate relation handles entity selection inside the aggregation subquery
- Nested query scopes are handled independently
- All services are planned before making any SPARQL changes, preventing partial rewrites on errors
- ASK, SELECT *, CONSTRUCT, and DESCRIBE receive query-form-specific handling

### Nested Scopes

'Nesting' occurs when curly braces { } are used to define group graph patterns, scoping blocks, and various operational structures (such as OPTIONAL, UNION, MINUS, and FILTER EXISTS). Different label services CAN be declared in different scopes (although this is uncommon). Typically, a global label service is declared at the end of a query. 

For rewriting, every nested label service is classified as `manual` or `automatic` from its own service contents and specifies its own language list. A query containing both modes can have different behaviors in Blazegraph than would be expected:

- Queries that are a UNION of triples and individual label services (within each UNIONed scope) return results on Blazegraph but they do not include any label results when listed in the projected variables (even if the services request the same language)
- Queries with a nested manual scope and automatic global scope successfully execute but return 0 results (see `fixtures/nested-manual-automatic.original.rq` and `rewritten.rq`)

However, the following return the correct results on Blazegraph:

- Queries with a nested automatic scope and manual global scope (see `fixtures/nested-automatic-manual.original.rq` and `rewritten.rq`)
- Queries with both nested and global automatic scopes (see `fixtures/nested-scope-automatic.original.rq` and `rewritten.rq`)
- Queries with both nested and global manual scopes (see `fixtures/nested-scope-manual-languages.original.rq` and `rewritten.rq`)

When being rewritten, each graph-pattern group uses only the language parameters from label services immediately contained in that group, and each query scope uses only its own output definitions. An output projected by a nested subquery belongs to that nested scope and is not regenerated by an automatic service in an outer scope, even when the outer query also projects the variable.

The `nested-automatic-manual` and `nested-manual-automatic` fixtures preserve useful QLever bindings that Blazegraph omits. Their fixture manifests set `sparql_diff.diffs` to `true` to record that the semantic difference is intentional and accepted. For all other fixtures the optional `diffs` field defaults to `false`. When `diffs` is true, both endpoint executions must still
succeed and their normalized results are expected not to match.

### Processing rdfs:label 

Use one shared internal label variable across ordered OPTIONALs. The left joins implement first-match-wins priority:

```sparql
OPTIONAL {
  ?labelSubject rdfs:label ?entity__label_value .
  FILTER(LANG(?entity__label_value) = "de")
}
OPTIONAL {
  ?labelSubject rdfs:label ?entity__label_value .
  FILTER(LANG(?entity__label_value) = "en")
}
OPTIONAL {
  ?labelSubject rdfs:label ?entity__label_value .
  FILTER(LANG(?entity__label_value) = "mul")
}
BIND(
  IF(
    BOUND(?entity__label_value),
    STR(?entity__label_value),
    STRAFTER(STR(?entity), "entity/")
  ) AS ?labelOutput
)
```

### Processing schema:description 

Use one shared internal description variable across ordered OPTIONALs (and the 'mul' fallback is not needed since 'mul' descriptions are not defined):

```sparql
OPTIONAL {
  ?labelSubject schema:description ?entity__description_value .
  FILTER(LANG(?entity__description_value) = "de")
}
OPTIONAL {
  ?labelSubject schema:description ?entity__description_value .
  FILTER(LANG(?entity__description_value) = "en")
}
BIND(STR(?entity__description_value) AS ?descriptionOutput)
```

### Processing skos:altLabel

Aliases are multivalued. For each language in priority order, aggregate all aliases in that language in a grouped subquery, and use the same output variable across the ordered OPTIONALs. However, each alias aggregation subquery must be constrained to the candidate entities produced by all preceding, in-scope patterns that may bind `?entity` at the service occurrence. The inner `SELECT DISTINCT ?entity` is constructed from those binding patterns. The constraints may come from various locations in the query - triples, property paths, `VALUES`, `BIND`, `OPTIONAL`, `UNION`, projected subqueries, or multiple alternative binders. 

Without this candidate relation, the aggregation subquery may scan and group aliases across the entire repository before the outer left join is evaluated.

```sparql
OPTIONAL {
  SELECT ?entity
         (GROUP_CONCAT(DISTINCT ?entity__alias_value; SEPARATOR=", ") AS ?aliasOutput)
  WHERE {
    {
      SELECT DISTINCT ?entity WHERE {
        # preceding, in-scope patterns that may bind ?entity
      }
    }
    ?entity skos:altLabel ?entity__alias_value .
    FILTER(LANG(?entity__alias_value) = "de")
  }
  GROUP BY ?entity
}
```

Note that the above OPTIONAL pattern is repeated for every language, using the language priority order and using the same `?aliasOutput`. `mul` is used as a fallback if not already specified in the language list. The left joins implement first-language-with-aliases priority.

For a fixed entity IRI, a candidate variable with a singleton `VALUES` relation is introduced. That variable is used as the alias lookup subject, and group by variable. This ensures that no matching aliases produce aggregate rows. 

  ```sparql
  OPTIONAL {
    SELECT ?entity__alias_candidate
           (GROUP_CONCAT(DISTINCT ?entity__alias_value; SEPARATOR=", ") AS ?aliasOutput)
    WHERE {
      VALUES ?entity__alias_candidate { wd:Q42 }
      ?entity__alias_candidate skos:altLabel ?entity__alias_value .
      FILTER(LANG(?entity__alias_value) = "en")
    }
    GROUP BY ?entity__alias_candidate
  }
  ```

Semantic validation must treat comma-joined alias strings as equivalent when they contain the same distinct alias values, regardless of member order or duplicate multiplicity.

## Unsupported Cases

- Service content other than language-parameter triples and supported manual output triples.
- A manual output predicate other than `rdfs:label`, `schema:description`, or `skos:altLabel`.
- Any language parameter that is not a supported literal comma-separated list.
- A variable entity term known not to be a Wikidata item or property.
- A manual entity term that is neither a variable nor a fixed IRI.
- Manual output not a variable.
- Alias entity variable cannot be safely constrained.

## Rewrite Variants

### Variant: manual

Applies when:

- The service block contains one or more supported manual output triples.

Required parameters / arguments:

- One or more manual triples of these forms:
  - `entityTerm rdfs:label ?output`
  - `entityTerm schema:description ?output`
  - `entityTerm skos:altLabel ?output`
- One or more language parameters, concatenated and normalized as one language-priority list

Here, `entityTerm` is either a variable or a fixed IRI, and `?output` must be a variable.

Optional parameters / arguments:

- `[AUTO_LANGUAGE]` as a member of the language list
- Any combination of supported output types and entity terms 
  - Distinct output variables are independent, including when they request the same entity term and output type

#### Example: manual-label-description-alias

Fixture files:

- `fixtures/wikibase-label/manual-label-description-alias.json`
- `fixtures/wikibase-label/manual-label-description-alias.original.rq`
- `fixtures/wikibase-label/manual-label-description-alias.rewritten.rq`

Purpose:

- Rewrite explicit label, description, and alias variables with `de`, `en`, and `mul` fallback behavior.

Input:

```sparql
SELECT ?item ?label ?description ?alternate WHERE {
  ?item wdt:P31 wd:Q2031121 .
  SERVICE wikibase:label {
    bd:serviceParam wikibase:language "de,en" .
    ?item rdfs:label ?label .
    ?item schema:description ?description .
    ?item skos:altLabel ?alternate .
  }
}
```

Expected rewrite status: `rewritten`

Expected output: See `fixtures/wikibase-label/manual-label-description-alias.rewritten.rq`

### Variant: automatic

Applies when:

- The service block contains no manual output triples.
- The service occurs within a `SELECT`, `ASK`, `CONSTRUCT`, or `DESCRIBE` query scope.
  - An explicit `SELECT` projection or `CONSTRUCT` template may contain recognized automatic output variables.
  - `SELECT *` and `ASK` produce no automatic label outputs.

Required parameters / arguments:

- Zero or more recognized automatic output variable names from the immediately containing `SELECT` projection or `CONSTRUCT` template.
  - `SELECT *` and `ASK` have none and contribute no label details. 
  - `DESCRIBE` removes recognized label, description, and alias target variables and retains the other targets.
- One or more language parameters. Concatenate and normalize them as one ordered language-priority list.

Optional parameters / arguments:

- `[AUTO_LANGUAGE]` as a member of the language list.
- Any number of entity variables with recognized projected outputs.

#### Example: automatic-output-names

Fixture files:

- `fixtures/wikibase-label/automatic-output-names.json`
- `fixtures/wikibase-label/automatic-output-names.original.rq`
- `fixtures/wikibase-label/automatic-output-names.rewritten.rq`

Purpose:

- Derive all three output types for `?item` and preserve the UI language token.

Input:

```sparql
SELECT ?item ?itemLabel ?itemDescription ?itemAltLabel WHERE {
  ?item wdt:P31 wd:Q515 .
  SERVICE wikibase:label {
    bd:serviceParam wikibase:language "[AUTO_LANGUAGE],de,en" .
  }
}
```

Expected rewrite status: `rewritten`

Expected output: See `fixtures/wikibase-label/automatic-output-names.rq`

## Non-Applicable, Unsupported, and Ambiguous Cases

Does not apply when:

- The SERVICE IRI does not resolve to `http://wikiba.se/ontology#label`.
- A lexical `wikibase:label` name is rebound to a different IRI.

Unsupported cases:

- Unknown predicates or executable content in a manual service block.
- Nonliteral or structurally invalid language parameters.
- Entity terms other than variables and fixed IRIs.
- Manual output not a variable.
- Alias entity variable cannot be safely constrained.

## Error Codes

General engine errors can occur, such as `parse_error` on input or `rewritten_query_parse_error`. These are not label-specific.

| Condition | Status | Code |
|---|---|---|
| Missing language parameter | `blazegraph_error` | `wikibase_label_missing_language_parameter` |
| Alias entity variable cannot be safely constrained | `skipped_unsupported` | `wikibase_label_alias_entity_not_safely_constrained` |
| Manual output not a variable | `skipped_unsupported` | `wikibase_label_manual_output_not_variable` |
| Unsupported service content | `skipped_unsupported` | `wikibase_label_unsupported_service_content` |
| Malformed language parameter | `skipped_unsupported` | `wikibase_label_malformed_language_parameter` |
| Entity term that is neither a variable nor a fixed IRI | `skipped_unsupported` | `wikibase_label_invalid_entity_term` |
