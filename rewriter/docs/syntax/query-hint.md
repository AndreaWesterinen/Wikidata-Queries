# rewrite-query-hint

Last Revision: 31 July 2026

## Feature

- Type: syntax
- Name: Blazegraph query hints
- Canonical namespace: `http://www.bigdata.com/queryHints#`
- Parse phase: pre-ARQ
- Supported subjects:
  - `http://www.bigdata.com/queryHints#Query`
  - `http://www.bigdata.com/queryHints#Prior`
  - `http://www.bigdata.com/queryHints#Group`
  - `http://www.bigdata.com/queryHints#GroupAndSubGroups`
  - `http://www.bigdata.com/queryHints#SubQuery`
  
Repeated instances allowed: yes

### Parameters / Arguments

There are a variety of hint:xxx predicates defined - such as hint:optimizer, hint:runFirst, etc. They are not individually listed since they do not affect the rewrite. Any triple with a hint:xxx subject is removed.

## Logic Summary

Before the first SPARQL parse, the engine removes every complete triple statement whose resolved subject is `hint:Query`, `hint:Prior`, `hint:Group`, `hint:GroupAndSubGroups`, or `hint:SubQuery` in the `http://www.bigdata.com/queryHints#` namespace. The predicate and value do not affect the rewrite. Repeated statements and semicolon or comma property lists are removed together and recorded as one `rewrite-query-hint` / `default` application. Explicitly rebound `hint` prefixes are not matched.

One `rewrite-query-hint` / `default` rewrite entry is recorded although multiple matching statements are removed.

## Error Codes

None except the general input status `parse_error` with error code `original_query_parse_error`.
