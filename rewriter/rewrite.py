"""Shared implementation for the local Python rewrite commands."""

from __future__ import annotations

from collections.abc import Iterable
import json
import os
from pathlib import Path
import subprocess
from typing import Any


class RewriterError(RuntimeError):
    """Cleanly catch failures."""


class JavaRewriter:
    """Keep one local Java rewrite process open for multiple queries."""

    def __init__(self, jar_path: Path) -> None:
        """Start the executable rewriter JAR."""

        java = os.environ.get("SPARQL_REWRITER_JAVA", "java")
        try:
            self._process = subprocess.Popen(
                [java, "-jar", str(jar_path)],
                stdin=subprocess.PIPE, stdout=subprocess.PIPE,
                text=True, encoding="utf-8")
        except OSError as error:
            raise RewriterError("Could not start the Java rewriter.") from error
        self._request_number = 0

    def close(self) -> None:
        """Close standard input and wait for the local Java process."""

        if self._process.stdin is not None and not self._process.stdin.closed:
            self._process.stdin.close()
        self._process.wait()
        if self._process.stdout is not None and not self._process.stdout.closed:
            self._process.stdout.close()

    def __enter__(self) -> "JavaRewriter":
        """Return the open client."""

        return self

    def __exit__(self, exc_type: object, exc: object,
            traceback: object) -> None:
        """Close the Java process."""

        self.close()

    def request(self, query: str, query_id: str) -> dict[str, Any]:
        """Send one query and return the matching Java response object."""

        stdin = self._process.stdin
        stdout = self._process.stdout
        if stdin is None or stdout is None:
            raise RewriterError("The Java rewriter pipes are unavailable.")

        self._request_number += 1
        request_id = f"request-{self._request_number}"
        request = {"protocol_version": 1, "request_id": request_id,
            "query_id": query_id, "query": query}
        try:
            stdin.write(json.dumps(request, separators=(",", ":")) + "\n")
            stdin.flush()
            line = stdout.readline()
        except OSError as error:
            raise RewriterError(
                "Could not communicate with the Java rewriter.") from error

        if not line:
            raise RewriterError("The Java rewriter exited without a response.")
        try:
            response = json.loads(line)
        except json.JSONDecodeError as error:
            raise RewriterError(
                "The Java rewriter returned invalid JSON.") from error
        if not isinstance(response, dict):
            raise RewriterError(
                "The Java rewriter returned a non-object response.")
        if (response.get("request_id") != request_id
                or response.get("query_id") != query_id):
            raise RewriterError("The Java response does not match the request.")
        return response


def rewrite_query(query: str, query_id: str,
        rewriter_jar: Path) -> dict[str, Any]:
    """Rewrite one query through the Java JAR."""

    with JavaRewriter(rewriter_jar) as rewriter:
        return _result(rewriter.request(query, query_id))


def rewrite_queries(queries: Iterable[tuple[str, str]],
        rewriter_jar: Path) -> list[tuple[str, dict[str, Any]]]:
    """Rewrite query-ID and query-text pairs through a Java process."""

    with JavaRewriter(rewriter_jar) as rewriter:
        return [
            (query_id, _result(rewriter.request(query, query_id)))
            for query_id, query in queries
        ]


def _result(response: dict[str, Any]) -> dict[str, Any]:
    """Return the rewrite result from a successful Java response."""

    if response.get("status") != "ok":
        diagnostic = response.get("diagnostic")
        if isinstance(diagnostic, dict):
            message = diagnostic.get("message")
            if isinstance(message, str):
                raise RewriterError(message)
        raise RewriterError("The Java rewriter returned an error response.")

    result = response.get("result")
    if not isinstance(result, dict):
        raise RewriterError("The Java response has no rewrite result.")
    return result
