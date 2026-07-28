"""Persistent JSONL client for the Java SPARQL rewrite engine."""

from __future__ import annotations

import hashlib
import json
import os
from pathlib import Path
import re
import subprocess
from typing import Any, NoReturn


DEFAULT_JAVA_MAX_HEAP = "512m"
VALID_REWRITE_STATUSES = {
    "rewritten",
    "unchanged",
    "skipped_unsupported",
    "ambiguous_conflicting",
    "validation_failed",
    "parse_error",
    "blazegraph_error",
}


class RewriterError(RuntimeError):
    """Report a structured Java-process, transport, or protocol failure."""

    def __init__(self, code: str, message: str, retryable: bool,
                 details: dict[str, Any] | None = None) -> None:
        """Store stable failure data for callers and logs.

        Args:
            code: Stable machine-readable failure code.
            message: Human-readable failure description.
            retryable: Whether a bounded infrastructure retry is appropriate.
            details: Optional structured context that excludes query text.

        Returns:
            None.
        """

        self.code = code
        self.message = message
        self.retryable = retryable
        self.details = details or {}
        super().__init__(f"{code}: {message}")


class RewriterResponseError(RewriterError):
    """Expose a structured non-success protocol response."""

    def __init__(self, status: str, diagnostic: dict[str, Any]) -> None:
        """Store the protocol status and diagnostic.

        Args:
            status: Java response status.
            diagnostic: Structured protocol diagnostic.

        Returns:
            None.
        """
        self.status = status
        self.diagnostic = diagnostic
        code = str(diagnostic.get("code") or "java_response_error")
        message = str(diagnostic.get("message") or "Java returned an error response.")
        super().__init__(
            code, message, status == "engine_error",
            {"status": status, "diagnostic": diagnostic})


class JavaRewriter:
    """Manage one reusable Java rewrite process for multiple queries."""

    def __init__(self, jar_path: Path, max_heap: str | None = None) -> None:
        """Start the rewrite engine from an executable JAR.

        Args:
            jar_path: Path to the executable Java rewriter JAR.
            max_heap: JVM maximum heap such as ``512m``. The environment variable
                ``SPARQL_REWRITER_JAVA_MAX_HEAP`` overrides the default.

        Returns:
            None.
        """
        java_executable = os.environ.get("SPARQL_REWRITER_JAVA", "java")
        heap = max_heap or os.environ.get(
            "SPARQL_REWRITER_JAVA_MAX_HEAP", DEFAULT_JAVA_MAX_HEAP)
        if re.fullmatch(r"[1-9][0-9]*[kKmMgG]", heap) is None:
            raise RewriterError(
                "invalid_java_max_heap",
                "SPARQL_REWRITER_JAVA_MAX_HEAP must be a positive JVM size.",
                False,
                {"max_heap": heap})
        try:
            self._process = subprocess.Popen(
                [java_executable, f"-Xmx{heap}", "-XX:+ExitOnOutOfMemoryError",
                 "-jar", str(jar_path)],
                stdin=subprocess.PIPE, stdout=subprocess.PIPE,
                stderr=None, text=True, encoding="utf-8")
        except OSError as error:
            raise RewriterError(
                "java_start_failed", "Could not start the Java rewriter.", True,
                {"java_executable": java_executable, "jar_path": str(jar_path),
                 "max_heap": heap}
            ) from error
        self._request_number = 0
        self._closed = False

    def close(self) -> None:
        """Close the request stream and wait for Java to exit.

        Returns:
            None.
        """

        if self._closed:
            return
        try:
            if self._process.stdin is not None and not self._process.stdin.closed:
                try:
                    self._process.stdin.close()
                except OSError:
                    pass
            try:
                self._process.wait(timeout=10)
            except subprocess.TimeoutExpired:
                self._process.terminate()
                try:
                    self._process.wait(timeout=5)
                except subprocess.TimeoutExpired:
                    self._process.kill()
                    self._process.wait(timeout=5)
        finally:
            if self._process.stdout is not None and not self._process.stdout.closed:
                self._process.stdout.close()
            self._closed = True

    def __enter__(self) -> "JavaRewriter":
        """Return this process for context-managed use."""

        return self

    def __exit__(self, exc_type: object, exc: object, traceback: object) -> None:
        """Stop Java when leaving a context.

        Args:
            exc_type: Type of a raised exception, if any.
            exc: Raised exception, if any.
            traceback: Exception traceback, if any.

        Returns:
            None.
        """

        self.close()

    def rewrite(self, query: str, query_id: str) -> dict[str, Any]:
        """Rewrite one query and return its public result.

        Args:
            query: Complete source query.
            query_id: Caller-supplied query identifier.

        Returns:
            A Single-Query Rewrite Result dictionary.
        """

        if self._process.stdin is None or self._process.stdout is None:
            raise RewriterError(
                "pipes_unavailable",
                "The Java rewriter pipes are unavailable.",
                False)

        self._request_number += 1
        request_id = f"request-{self._request_number}"
        request = {
            "protocol_version": 1,
            "request_id": request_id,
            "query_id": query_id,
            "query": query}
        try:
            self._process.stdin.write(
                json.dumps(request, separators=(",", ":")) + "\n")
            self._process.stdin.flush()
        except OSError as error:
            raise RewriterError(
                "request_write_failed",
                "Could not send a request to the Java rewriter.", True,
                {"request_id": request_id, "query_id": query_id},
            ) from error
        try:
            line = self._process.stdout.readline()
        except OSError as error:
            raise RewriterError(
                "response_read_failed",
                "Could not read a response from the Java rewriter.", True,
                {"request_id": request_id, "query_id": query_id},
            ) from error
        if not line:
            raise RewriterError(
                "unexpected_eof",
                "The Java rewriter exited without a response "
                f"(exit={self._process.poll()}).", True,
                {"request_id": request_id, "query_id": query_id,
                 "exit_code": self._process.poll()}
            )
        try:
            response = json.loads(line)
        except json.JSONDecodeError as error:
            raise RewriterError(
                "invalid_json_response",
                "The Java rewriter returned invalid JSON.", True,
                {"request_id": request_id, "query_id": query_id}
            ) from error
        if not isinstance(response, dict):
            raise RewriterError(
                "non_object_response",
                "The Java rewriter returned a non-object response.", True,
                {"response_type": type(response).__name__}
            )
        if response.get("protocol_version") != 1:
            raise RewriterError(
                "protocol_version_mismatch",
                "The Java response has the wrong protocol_version.", False,
                {"expected": 1, "actual": response.get("protocol_version")}
            )
        if response.get("request_id") != request_id:
            raise RewriterError(
                "request_id_mismatch",
                "The Java response has the wrong request_id.", False,
                {"expected": request_id, "actual": response.get("request_id")}
            )
        if response.get("query_id") != query_id:
            raise RewriterError(
                "query_id_mismatch",
                "The Java response has the wrong query_id.", False,
                {"expected": query_id, "actual": response.get("query_id")}
            )
        if response.get("status") != "ok":
            diagnostic = response.get("diagnostic", {})
            if not isinstance(diagnostic, dict):
                diagnostic = {}
            raise RewriterResponseError(str(response.get("status")), diagnostic)
        expected_hash = hashlib.sha256(query.encode("utf-8")).hexdigest()
        if response.get("query_sha256") != expected_hash:
            raise RewriterError(
                "query_hash_mismatch",
                "The Java response belongs to another query revision.", False,
                {"expected": expected_hash, "actual": response.get("query_sha256")}
            )
        result = _validate_result(response.get("result"), query_id)
        return result


def _validate_result(value: object, query_id: str) -> dict[str, Any]:
    """Validate the complete public rewrite-result contract."""

    if not isinstance(value, dict):
        _invalid_result(value, query_id, "result is not an object")
    result = value
    if result.get("schema_version") != 1 or result.get("contract_version") != 1:
        _invalid_result(result, query_id, "unsupported result schema or contract version")
    if result.get("query_id") != query_id:
        _invalid_result(result, query_id, "result query_id does not match")
    status = result.get("rewrite_status")
    if not isinstance(status, str) or status not in VALID_REWRITE_STATUSES:
        _invalid_result(result, query_id, "invalid rewrite_status")
    rewrites = result.get("rewrites")
    if not isinstance(rewrites, list) or any(
            not isinstance(item, dict)
            or not isinstance(item.get("rule_id"), str)
            or not isinstance(item.get("variant_id"), str)
            for item in rewrites):
        _invalid_result(result, query_id, "invalid rewrites array")
    if (not isinstance(result.get("warnings"), list)
            or not isinstance(result.get("errors"), list)):
        _invalid_result(result, query_id, "invalid warning or error array")
    rewritten = result.get("rewritten_query")
    expects_query = status in {"rewritten", "unchanged"}
    if ((expects_query and not isinstance(rewritten, str))
            or (not expects_query and rewritten is not None)):
        _invalid_result(result, query_id, "rewritten_query conflicts with status")
    return result


def _invalid_result(value: object, query_id: str, reason: str) -> NoReturn:
    """Raise one stable error for any malformed successful result."""

    raise RewriterError(
        "invalid_rewrite_result",
        "The Java response has an invalid rewrite result.", False,
        {"result_type": type(value).__name__,
         "expected_query_id": query_id,
         "actual_query_id": (
             value.get("query_id") if isinstance(value, dict) else None),
         "reason": reason})
