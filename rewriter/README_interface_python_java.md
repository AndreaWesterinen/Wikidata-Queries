# Python-Java Rewrite Protocol

Last revision: 27 July 2026

The persistent Java process reads and writes one JSON object per line. Standard output is reserved for protocol responses. Diagnostics and runtime logging use standard error. Protocol version `1` has one operation: rewrite the supplied complete query.

## Request

```json
{
  "protocol_version": 1,
  "request_id": "request-1",
  "query_id": "query-1",
  "query": "SELECT ..."
}
```

`request_id` pairs one transport response with one request. `query_id` is copied into the public rewrite result.

## Successful envelope

```json
{
  "protocol_version": 1,
  "request_id": "request-1",
  "query_id": "query-1",
  "query_sha256": "...",
  "status": "ok",
  "result": {
    "schema_version": 1,
    "contract_version": 1,
    "query_id": "query-1",
    "rewrites": [],
    "rewrite_status": "unchanged",
    "rewritten_query": "SELECT ...",
    "warnings": [],
    "errors": []
  }
}
```

`query_sha256` is the lowercase SHA-256 of the exact request query. Envelope status `ok` means Java completed the operation and the query outcome is in `result.rewrite_status`.

Python validates every required result field, schema and contract versions,
rewrite entries, status, arrays, and the status-dependent nullability of
`rewritten_query` before exposing the result to a caller.

## Rewrite result

The `rewrites` array is ordered by rule application. A rewritten result contains canonical, reparsed, standalone SPARQL. The engine prefix names `bd`, `gas`, `hint`, and `mwapi` are absent, even if the source declared them. Other surviving QNames have explicit declarations. Prefix declarations are ordered by prefix name and the default prefix first. An unchanged result contains the exact original source text rather than a rewritten query. `validation_failed`, `skipped_unsupported`, `ambiguous_conflicting`, `parse_error`, and `blazegraph_error` use `null` for `rewritten_query`.

Top-level `FROM` and `FROM NAMED` clauses produce `skipped_unsupported` because
Wikidata has no named graphs to select into a query dataset. This does not
prohibit named `GRAPH` patterns inside federated `SERVICE` queries.

Rule-specific errors use the stable error-object fields documented by the rewrite contracts. Parse errors currently carry no rule-specific error object.

## Operational failure envelope

```json
{
  "protocol_version": 1,
  "request_id": "request-1",
  "query_id": "query-1",
  "status": "protocol_error",
  "diagnostic": {
    "code": "invalid_request",
    "message": "Missing or non-string field: query"
  }
}
```

Envelope statuses other than `ok`, including `protocol_error` and `engine_error`, are process or protocol failures, not rewrite statuses. Python raises `RewriterResponseError` for them, preserving the envelope status and diagnostic code, message, and fields. An `engine_error` is eligible for bounded retry; a `protocol_error` is not.

## Invariants

- Responses are emitted in request order.
- One request produces at most one response line.
- Logs never appear on standard output.
- Java objects, syntax trees, and algebra do not cross the boundary.
- The same input and engine version produce the same logical result and canonical rewritten text.
