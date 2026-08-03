# Blazegraph Feature Usage Report: Other Queries

Source page reviewed: `https://wikitech.wikimedia.org/wiki/User:AWesterinen/Blazegraph_Features_and_Capabilities`

Scanned local example queries: `11` `.rq` files under `/Users/arwest/Hold/Wikidata-Queries/extract_sample_queries/other_examples/phab_issues`, `/Users/arwest/Hold/Wikidata-Queries/extract_sample_queries/other_examples/submitted`.

This report uses the feature inventory described on the referenced Wikitech page, then maps each feature to matching files in the local example trees.

## Summary Table

| Section | Feature | Matches |
| --- | --- | ---: |
| Blazegraph Features | Named sub-queries (`WITH { ... } AS %name`, `INCLUDE %name`) | 5 |
| Function Extensions | `geof:globe()` | 0 |
| Function Extensions | `geof:latitude()` | 0 |
| Function Extensions | `geof:longitude()` | 0 |
| Function Extensions | `geof:distance()` | 0 |
| Function Extensions | `wikibase:decodeUri()` | 0 |
| Function Extensions | `wikibase:isSomeValue()` | 0 |
| SERVICE Extensions | `SERVICE wikibase:around` | 1 |
| SERVICE Extensions | `SERVICE wikibase:box` | 0 |
| SERVICE Extensions | `SERVICE wikibase:label` | 7 |
| SERVICE Extensions | `SERVICE bd:slice` | 0 |
| SERVICE Extensions | `SERVICE wikibase:mwapi` | 0 |
| SERVICE Extensions | `SERVICE gas:service` | 0 |
| SERVICE Extensions | `SERVICE bd:sample` | 0 |
| Supporting Blazegraph-Specific Syntax | `hint:Query ...` query hints | 1 |

## Miscellaneous

| Category | Detail | Matches |
| --- | --- | ---: |
| Wikidata RDF Pseudo-Value | `wikibase:someValue` | 0 |
| Wikidata RDF Predicates | `wikibase:geoGlobe` | 0 |
| Federated SERVICE endpoint | `https://qlever.dev/api/wikimedia-commons` | 0 |
| Federated SERVICE endpoint | Other `SERVICE <...>` endpoint | 1 |
| `wikibase:api` value | `Generator` | 0 |
| `wikibase:api` value | `Categories` | 0 |
| `wikibase:api` value | `Search` | 0 |
| `wikibase:api` value | `EntitySearch` | 0 |

### Wikidata RDF Pseudo-Value: `wikibase:someValue`

- Local matches: 0

Matching files:
- None in the current example trees

### Wikidata RDF Predicates: `wikibase:geoGlobe`

- Local matches: 0

Matching files:
- None in the current example trees

### Federated SERVICE endpoint: `https://qlever.dev/api/wikimedia-commons`

- Local matches: 0

Matching files:
- None in the current example trees

### Federated SERVICE endpoint: Other `SERVICE <...>` endpoint

- Local matches: 1

Matching files:
- `/Users/arwest/Hold/Wikidata-Queries/extract_sample_queries/other_examples/phab_issues/T225205-NamedGraph.rq`

### `wikibase:api` value: `Generator`

- Local matches: 0

Matching files:
- None in the current example trees

### `wikibase:api` value: `Categories`

- Local matches: 0

Matching files:
- None in the current example trees

### `wikibase:api` value: `Search`

- Local matches: 0

Matching files:
- None in the current example trees

### `wikibase:api` value: `EntitySearch`

- Local matches: 0

Matching files:
- None in the current example trees

## Blazegraph Features

- Features in this section: 1
- Total matches across this section: 5

### Named sub-queries (`WITH { ... } AS %name`, `INCLUDE %name`)

- Local matches: 5

Matching files:
- `/Users/arwest/Hold/Wikidata-Queries/extract_sample_queries/other_examples/phab_issues/T323423-NamedSubqueries-Optional1.rq`
- `/Users/arwest/Hold/Wikidata-Queries/extract_sample_queries/other_examples/phab_issues/T323423-NamedSubqueries-Optional2.rq`
- `/Users/arwest/Hold/Wikidata-Queries/extract_sample_queries/other_examples/submitted/geo1.rq`
- `/Users/arwest/Hold/Wikidata-Queries/extract_sample_queries/other_examples/submitted/geo2.rq`
- `/Users/arwest/Hold/Wikidata-Queries/extract_sample_queries/other_examples/submitted/geo3.rq`

## Function Extensions

- Features in this section: 6
- Total matches across this section: 0

### `geof:globe()`

- Local matches: 0

Matching files:
- None in the current example trees

### `geof:latitude()`

- Local matches: 0

Matching files:
- None in the current example trees

### `geof:longitude()`

- Local matches: 0

Matching files:
- None in the current example trees

### `geof:distance()`

- Local matches: 0

Matching files:
- None in the current example trees

### `wikibase:decodeUri()`

- Local matches: 0

Matching files:
- None in the current example trees

### `wikibase:isSomeValue()`

- Local matches: 0

Matching files:
- None in the current example trees

## SERVICE Extensions

- Features in this section: 7
- Total matches across this section: 8

### `SERVICE wikibase:around`

- Local matches: 1

Matching files:
- `/Users/arwest/Hold/Wikidata-Queries/extract_sample_queries/other_examples/submitted/geo1.rq`

### `SERVICE wikibase:box`

- Local matches: 0

Matching files:
- None in the current example trees

### `SERVICE wikibase:label`

- Local matches: 7

Matching files:
- 7 matching files (not listed individually)

### `SERVICE bd:slice`

- Local matches: 0

Matching files:
- None in the current example trees

### `SERVICE wikibase:mwapi`

- Local matches: 0

Matching files:
- None in the current example trees

### `SERVICE gas:service`

- Local matches: 0

Matching files:
- None in the current example trees

### `SERVICE bd:sample`

- Local matches: 0

Matching files:
- None in the current example trees

## Supporting Blazegraph-Specific Syntax

- Features in this section: 1
- Total matches across this section: 1

### `hint:Query ...` query hints

- Local matches: 1

Matching files:
- `/Users/arwest/Hold/Wikidata-Queries/extract_sample_queries/other_examples/phab_issues/T278518-Bind.rq`

