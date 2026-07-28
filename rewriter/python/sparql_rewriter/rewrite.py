"""Python orchestration for the Java SPARQL rewrite engine."""

from __future__ import annotations

from collections.abc import Iterable, Iterator
import os
from pathlib import Path
from typing import Any

from .java_client import JavaRewriter, RewriterError


DEFAULT_MAX_REQUESTS_PER_PROCESS = 10_000


def rewrite_query(query: str, query_id: str, rewriter_jar: Path) -> dict[str, Any]:
    """Rewrite one query through Java.

    Args:
        query: Complete query text.
        query_id: Identifier included in the result.
        rewriter_jar: Path to the executable rewriter JAR.

    Returns:
        A Single-Query Rewrite Result dictionary.
    """

    with JavaRewriter(rewriter_jar) as rewriter:
        return rewriter.rewrite(query, query_id)


def iter_rewrite_queries(
        queries: Iterable[tuple[str, str]], rewriter_jar: Path,
        max_requests: int | None = None,
) -> Iterator[tuple[str, dict[str, Any]]]:
    """Stream ordered results while periodically replacing the persistent JVM.

    Args:
        queries: Query-ID and source-query pairs.
        rewriter_jar: Path to the executable Java rewriter JAR.
        max_requests: Maximum requests handled by one child process. The
            ``SPARQL_REWRITER_MAX_REQUESTS`` environment variable overrides the
            default when this argument is omitted.

    Yields:
        Query-ID and rewrite-result pairs in input order.
    """

    limit = _max_requests(max_requests)
    rewriter: JavaRewriter | None = None
    request_count = 0
    try:
        for query_id, query in queries:
            if rewriter is None or request_count >= limit:
                if rewriter is not None:
                    rewriter.close()
                    rewriter = None
                rewriter = JavaRewriter(rewriter_jar)
                request_count = 0
            yield query_id, rewriter.rewrite(query, query_id)
            request_count += 1
    finally:
        if rewriter is not None:
            rewriter.close()


def rewrite_queries(
        queries: Iterable[tuple[str, str]], rewriter_jar: Path,
        max_requests: int | None = None,
) -> list[tuple[str, dict[str, Any]]]:
    """Rewrite query-ID/text pairs through one persistent Java process.

    Args:
        queries: Query-ID and source-query pairs.
        rewriter_jar: Path to the executable Java rewriter JAR.
        max_requests: Maximum requests handled by one child process.

    Returns:
        Query-ID and rewrite-result pairs in input order.
    """

    return list(iter_rewrite_queries(queries, rewriter_jar, max_requests))


def _max_requests(configured: int | None) -> int:
    """Resolve and validate the bounded child-process request count."""

    value: object = configured
    if value is None:
        value = os.environ.get(
            "SPARQL_REWRITER_MAX_REQUESTS",
            str(DEFAULT_MAX_REQUESTS_PER_PROCESS))
    try:
        limit = int(value)
    except (TypeError, ValueError) as error:
        raise RewriterError(
            "invalid_max_requests",
            "SPARQL_REWRITER_MAX_REQUESTS must be a positive integer.",
            False,
            {"max_requests": value}) from error
    if limit <= 0:
        raise RewriterError(
            "invalid_max_requests",
            "SPARQL_REWRITER_MAX_REQUESTS must be a positive integer.",
            False,
            {"max_requests": value})
    return limit
