# Python Orchestration

Last revision: 27 July 2026

## Responsibility

Python does not implement rewrite rules or interpret Jena syntax or algebra. It invokes the executable rewriter JAR, validates responses, and provides library and command-line entry points.

| Component | Responsibility |
|---|---|
| [`java_client.py`](python/sparql_rewriter/java_client.py) | Internal persistent-JVM client that exchanges and validates JSONL requests and responses |
| [`rewrite.py`](python/sparql_rewriter/rewrite.py) | Public library for single-query, bounded-list, and streaming partition execution |
| [`rewrite_query.py`](rewrite_query.py) | Command-line wrapper around `rewrite_query()` for one `.rq` file |
| [`rewrite_queries.py`](rewrite_queries.py) | Command-line wrapper for recursively rewriting ordinary `.rq` files in a directory |
| [`rewrite_fixtures.py`](rewrite_fixtures.py) | Command-line wrapper around `rewrite_queries()` for recursive fixture discovery and output handling |

## Library interface

```python
rewrite_query(query: str, query_id: str, rewriter_jar: Path)
rewrite_queries(queries: Iterable[tuple[str, str]], rewriter_jar: Path)
iter_rewrite_queries(queries: Iterable[tuple[str, str]], rewriter_jar: Path)
```

All three functions require the executable rewriter JAR path. `rewrite_query()`
starts the JAR for one query. `rewrite_queries()` returns a list for a bounded
local iterable. `iter_rewrite_queries()` streams results and bounds each child
process lifetime while preserving input order.

`java_client.py` starts the executable JAR, sends queries over standard input,
reads results from standard output, and checks the request ID, query ID, protocol
version, SHA-256 of the exact input query, and complete rewrite-result schema. It
does not parse or rewrite SPARQL. Protocol, process, and transport failures raise
`RewriterError`; deterministic query outcomes remain ordinary result
dictionaries.

## Operational errors

Every `RewriterError` provides `code`, `message`, `retryable`, and `details` attributes. `details` contains only operational context and never the query text. Errors caused by an underlying Python exception retain it through exception chaining.

| Code | Retryable | Meaning |
|---|---:|---|
| `java_start_failed` | yes | The Java process could not be started |
| `pipes_unavailable` | no | The client was created without required process pipes |
| `request_write_failed` | yes | A request could not be written or flushed |
| `response_read_failed` | yes | A response could not be read |
| `unexpected_eof` | yes | Java exited without returning a response |
| `invalid_json_response` | yes | Java returned malformed JSON |
| `non_object_response` | yes | Java returned JSON that was not an object |
| `protocol_version_mismatch` | no | The response used another protocol version |
| `request_id_mismatch` | no | The response did not match the request |
| `query_id_mismatch` | no | The response did not match the query ID |
| `query_hash_mismatch` | no | The response did not match the exact query text |
| `invalid_rewrite_result` | no | The successful envelope contained an invalid result |
| `invalid_java_max_heap` | no | The configured rewrite-JVM heap cap is invalid |
| `invalid_max_requests` | no | The configured per-process request bound is invalid |

`RewriterResponseError` is a `RewriterError` for non-success Java envelopes. It preserves `status` and the complete `diagnostic`, uses the diagnostic code and message, and is retryable only for `engine_error`.

The JAR runs with `java` from `PATH` by default. Set `SPARQL_REWRITER_JAVA` to an
explicit Java 11 executable when the surrounding environment, including Spark,
uses another Java version. Every child uses a 512 MiB maximum heap by default;
set `SPARQL_REWRITER_JAVA_MAX_HEAP` to another positive JVM size when the
executor memory-overhead calculation requires it.

`iter_rewrite_queries()` streams results and replaces a child after 10,000
requests by default. Set `SPARQL_REWRITER_MAX_REQUESTS` or pass `max_requests`
to change that bound. Corpus workers should use this iterator so a partition
does not accumulate results in Python memory. `rewrite_queries()` remains the
list-returning convenience API for bounded local batches.

Corpus partitioning, retry policy, Spark execution, checkpointing, database storage, and `sparql-diff` orchestration are outside this package.

## Command-line interface

Rewrite one query to standard output:

```bash
python3 rewrite_query.py path/to/query.rq
```

Save the rewritten query to a file:

```bash
python3 rewrite_query.py path/to/query.rq > path/to/query.rewritten.rq
```

Rewrite every ordinary `.rq` file recursively under a directory:

```bash
python3 rewrite_queries.py path/to/queries --output-dir path/to/output
```

The batch command preserves relative directories, writes each result as `name.rewritten.rq`, and excludes existing `.rewritten.rq` files and the output directory from its inputs.

## Tests

The Python suite covers:

- JAR request and response validation, failures, and process shutdown.
- Single-query ownership, persistent batch reuse, iterable consumption, and result ordering.
- Recursive ordinary-query discovery and mirrored batch output paths.
- Recursive fixture discovery and safe generated-versus-golden paths.
- End-to-end execution through the built JAR and every checked-in fixture.

Run the Python tests with:

```bash
python3 -m unittest discover -s tests -v
```
