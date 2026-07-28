# Blazegraph Feature Rewrite Behaviors

Last Revision: 27 July 2026

## Rule index

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

WDQS implicit prefixes are input-parser conveniences only. The engine prefix
names `bd`, `gas`, `hint`, and `mwapi` are forbidden in successful rewritten
output, even if the source declares them. Other surviving QNames must be
explicitly declared or serialized as full IRIs.

Top-level `FROM` and `FROM NAMED` clauses are rejected because Wikidata has no
named graphs to select into a query dataset. Named `GRAPH` patterns are
permitted where they can occur, notably inside federated `SERVICE` queries.

## `wikibase:label` behavior

### Manual and automatic modes

Manual mode rewrites the explicit label, description, and alias output variables declared inside the service.

Automatic mode recognizes projected variables ending in `Label`, `Description`, or `AltLabel`. Automatic `SELECT *` removes the service without generating output bindings. Automatic mode in `ASK`, `CONSTRUCT`, or `DESCRIBE` is unsupported.

### Language normalization

Language parameters from repeated label services in one query scope are concatenated in source order. Surrounding whitespace and empty tokens are removed, duplicates are removed case-insensitively while preserving the first occurrence, and `mul` is appended when absent.

A missing language parameter is a Blazegraph input error; a structurally invalid or nonliteral parameter is unsupported.

### Alias aggregation

Aliases are aggregated for the first requested language with results. A variable alias entity requires a safe candidate relation derived from preceding, in-scope Jena syntax elements and verified against possible algebra bindings. The rewrite is skipped when the entity cannot be safely constrained. Fixed entity IRIs use a singleton candidate relation.

### Unsupported label-service input

Unsupported inputs include:

- Unknown executable service content
- Entity terms other than variables or fixed IRIs
- Manual output terms that are not variables

The rewriter returns a structured diagnostic without producing a partial rewrite.

## Known unimplemented features

The engine detects cataloged proprietary features before returning `unchanged`.
Until their rewrite contracts and implementations are complete, it returns
`skipped_unsupported` atomically for:

- Named subqueries
- Blazegraph query hints
- `SERVICE gas:service`
- `SERVICE wikibase:around`, `wikibase:box`, and `wikibase:mwapi`
- `SERVICE bd:sample` and `bd:slice`
- `geof:globe`, `geof:latitude`, `geof:longitude`, and `geof:distance`
- `wikibase:decodeUri`

Feature detection uses grammar shape or resolved IRIs. A familiar prefix rebound
to another namespace is not classified as proprietary.
