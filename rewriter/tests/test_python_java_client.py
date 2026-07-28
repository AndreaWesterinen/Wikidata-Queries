"""Unit tests for the Python JSONL Java client without starting Java."""

from __future__ import annotations

import hashlib
import io
import json
from pathlib import Path
import subprocess
import sys
import unittest
from unittest.mock import MagicMock, patch


PROJECT_ROOT = Path(__file__).resolve().parents[1]
sys.path.insert(0, str(PROJECT_ROOT / "python"))

from sparql_rewriter.java_client import (  # noqa: E402
    JavaRewriter,
    RewriterError,
    RewriterResponseError,
)


def response_line(
    query: str = "SELECT * WHERE { ?s ?p ?o }",
    query_id: str = "query-1",
    **changes: object,
) -> str:
    """Build one Java response line with optional envelope changes."""

    response = {
        "protocol_version": 1,
        "request_id": "request-1",
        "query_id": query_id,
        "query_sha256": hashlib.sha256(query.encode("utf-8")).hexdigest(),
        "status": "ok",
        "result": {
            "schema_version": 1,
            "contract_version": 1,
            "query_id": query_id,
            "rewrites": [],
            "rewrite_status": "unchanged",
            "rewritten_query": query,
            "warnings": [],
            "errors": [],
        },
    }
    response.update(changes)
    return json.dumps(response) + "\n"


def fake_process(stdout_text: str) -> MagicMock:
    """Create a process double with writable stdin and prepared stdout."""

    process = MagicMock()
    process.stdin = io.StringIO()
    process.stdout = io.StringIO(stdout_text)
    process.poll.return_value = None
    return process


class JavaRewriterUnitTest(unittest.TestCase):
    """Verify Python-owned process and protocol behavior."""

    def test_wraps_process_start_failure(self) -> None:
        """Expose a missing or unstartable Java executable consistently."""

        with patch(
            "sparql_rewriter.java_client.subprocess.Popen",
            side_effect=OSError("java missing"),
        ):
            with self.assertRaisesRegex(RewriterError, "Could not start") as raised:
                JavaRewriter(Path("rewriter.jar"))

        self.assertEqual(raised.exception.code, "java_start_failed")
        self.assertTrue(raised.exception.retryable)
        self.assertEqual(raised.exception.details["jar_path"], "rewriter.jar")
        self.assertIsInstance(raised.exception.__cause__, OSError)

    def test_uses_configured_java_executable(self) -> None:
        """Allow the rewriter JVM to differ from Spark's Java runtime."""

        process = fake_process("")
        with patch.dict(
            "os.environ", {
                "SPARQL_REWRITER_JAVA": "/opt/java11/bin/java",
                "SPARQL_REWRITER_JAVA_MAX_HEAP": "384m",
            }
        ), patch(
            "sparql_rewriter.java_client.subprocess.Popen", return_value=process
        ) as popen:
            JavaRewriter(Path("rewriter.jar"))

        self.assertEqual(
            popen.call_args.args[0],
            ["/opt/java11/bin/java", "-Xmx384m",
             "-XX:+ExitOnOutOfMemoryError", "-jar", "rewriter.jar"],
        )

    def test_rejects_invalid_java_heap_cap(self) -> None:
        """Reject malformed JVM options before starting a child process."""

        with self.assertRaises(RewriterError) as raised:
            JavaRewriter(Path("rewriter.jar"), max_heap="unbounded")

        self.assertEqual(raised.exception.code, "invalid_java_max_heap")
        self.assertFalse(raised.exception.retryable)

    def test_sends_compact_request_and_returns_result(self) -> None:
        """Pair a valid response with the exact serialized request."""

        query = "SELECT * WHERE { ?s ?p ?o }"
        process = fake_process(response_line(query))
        with patch("sparql_rewriter.java_client.subprocess.Popen", return_value=process) as popen:
            rewriter = JavaRewriter(Path("rewriter.jar"))
            result = rewriter.rewrite(query, "query-1")

        popen.assert_called_once_with(
            ["java", "-Xmx512m", "-XX:+ExitOnOutOfMemoryError",
             "-jar", "rewriter.jar"],
            stdin=-1,
            stdout=-1,
            stderr=None,
            text=True,
            encoding="utf-8",
        )
        self.assertEqual(
            json.loads(process.stdin.getvalue()),
            {
                "protocol_version": 1,
                "request_id": "request-1",
                "query_id": "query-1",
                "query": query,
            },
        )
        self.assertEqual(result["rewritten_query"], query)

    def test_close_closes_streams_and_waits(self) -> None:
        """End the persistent child cleanly when the client closes."""

        process = fake_process("")
        with patch("sparql_rewriter.java_client.subprocess.Popen", return_value=process):
            rewriter = JavaRewriter(Path("rewriter.jar"))
            rewriter.close()

        self.assertTrue(process.stdin.closed)
        self.assertTrue(process.stdout.closed)
        process.wait.assert_called_once_with(timeout=10)

    def test_close_terminates_then_kills_a_stuck_child(self) -> None:
        """Ensure a nonresponsive rewrite process cannot leak from a worker."""

        process = fake_process("")
        process.wait.side_effect = (
            subprocess.TimeoutExpired("java", 10),
            subprocess.TimeoutExpired("java", 5),
            0,
        )
        with patch("sparql_rewriter.java_client.subprocess.Popen", return_value=process):
            rewriter = JavaRewriter(Path("rewriter.jar"))
            rewriter.close()

        process.terminate.assert_called_once_with()
        process.kill.assert_called_once_with()

    def test_non_success_response_preserves_diagnostic(self) -> None:
        """Expose a structured Java protocol failure to callers."""

        line = response_line(
            status="protocol_error",
            diagnostic={"code": "invalid_request", "message": "bad query"},
        )
        process = fake_process(line)
        with patch("sparql_rewriter.java_client.subprocess.Popen", return_value=process):
            rewriter = JavaRewriter(Path("rewriter.jar"))
            with self.assertRaises(RewriterResponseError) as raised:
                rewriter.rewrite("SELECT * WHERE { ?s ?p ?o }", "query-1")

        self.assertEqual(raised.exception.status, "protocol_error")
        self.assertEqual(raised.exception.code, "invalid_request")
        self.assertEqual(raised.exception.message, "bad query")
        self.assertFalse(raised.exception.retryable)
        self.assertEqual(raised.exception.diagnostic["code"], "invalid_request")
        self.assertEqual(raised.exception.details["status"], "protocol_error")

    def test_engine_error_is_retryable(self) -> None:
        """Allow bounded retries for Java engine failures."""

        line = response_line(
            status="engine_error",
            diagnostic={"code": "engine_failure", "message": "failed"},
        )
        process = fake_process(line)
        with patch(
            "sparql_rewriter.java_client.subprocess.Popen", return_value=process
        ):
            rewriter = JavaRewriter(Path("rewriter.jar"))
            with self.assertRaises(RewriterResponseError) as raised:
                rewriter.rewrite("SELECT * WHERE { ?s ?p ?o }", "query-1")

        self.assertEqual(raised.exception.code, "engine_failure")
        self.assertTrue(raised.exception.retryable)

    def test_rejects_mismatched_response_identity(self) -> None:
        """Reject responses for another request, query, or query revision."""

        cases = (
            (
                {"request_id": "request-other"},
                "request_id",
                "request_id_mismatch",
            ),
            ({"query_id": "query-other"}, "query_id", "query_id_mismatch"),
            (
                {"query_sha256": "0" * 64},
                "query revision",
                "query_hash_mismatch",
            ),
            (
                {"protocol_version": 2},
                "protocol_version",
                "protocol_version_mismatch",
            ),
        )
        for changes, message, code in cases:
            with self.subTest(field=message):
                process = fake_process(response_line(**changes))
                with patch(
                    "sparql_rewriter.java_client.subprocess.Popen",
                    return_value=process,
                ):
                    rewriter = JavaRewriter(Path("rewriter.jar"))
                    with self.assertRaisesRegex(RewriterError, message) as raised:
                        rewriter.rewrite(
                            "SELECT * WHERE { ?s ?p ?o }", "query-1")
                self.assertEqual(raised.exception.code, code)
                self.assertFalse(raised.exception.retryable)
                self.assertIn("expected", raised.exception.details)
                self.assertIn("actual", raised.exception.details)

    def test_rejects_invalid_json_and_non_object_json(self) -> None:
        """Classify malformed stdout as a protocol failure."""

        cases = (
            ("not-json\n", "invalid JSON", "invalid_json_response"),
            ("[]\n", "non-object", "non_object_response"),
        )
        for line, message, code in cases:
            with self.subTest(line=line):
                process = fake_process(line)
                with patch(
                    "sparql_rewriter.java_client.subprocess.Popen",
                    return_value=process,
                ):
                    rewriter = JavaRewriter(Path("rewriter.jar"))
                    with self.assertRaisesRegex(RewriterError, message) as raised:
                        rewriter.rewrite(
                            "SELECT * WHERE { ?s ?p ?o }", "query-1")
                self.assertEqual(raised.exception.code, code)
                self.assertTrue(raised.exception.retryable)

    def test_rejects_eof_and_invalid_result(self) -> None:
        """Reject a dead process and a malformed successful payload."""

        cases = (
            ("", "exited without a response", "unexpected_eof", True),
            (
                response_line(result=[]),
                "invalid rewrite result",
                "invalid_rewrite_result",
                False,
            ),
            (
                response_line(result={"query_id": "query-other"}),
                "invalid rewrite result",
                "invalid_rewrite_result",
                False,
            ),
            (
                response_line(result={
                    "schema_version": 1,
                    "contract_version": 1,
                    "query_id": "query-1",
                    "rewrites": [],
                    "rewrite_status": "skipped_unsupported",
                    "rewritten_query": "must be null",
                    "warnings": [],
                    "errors": [],
                }),
                "invalid rewrite result",
                "invalid_rewrite_result",
                False,
            ),
        )
        for line, message, code, retryable in cases:
            with self.subTest(message=message):
                process = fake_process(line)
                process.poll.return_value = 7
                with patch(
                    "sparql_rewriter.java_client.subprocess.Popen",
                    return_value=process,
                ):
                    rewriter = JavaRewriter(Path("rewriter.jar"))
                    with self.assertRaisesRegex(RewriterError, message) as raised:
                        rewriter.rewrite(
                            "SELECT * WHERE { ?s ?p ?o }", "query-1")
                self.assertEqual(raised.exception.code, code)
                self.assertEqual(raised.exception.retryable, retryable)

    def test_rejects_unavailable_pipes(self) -> None:
        """Classify missing process pipes as a nonretryable client invariant."""

        process = fake_process("")
        process.stdin = None
        with patch(
            "sparql_rewriter.java_client.subprocess.Popen", return_value=process
        ):
            rewriter = JavaRewriter(Path("rewriter.jar"))
            with self.assertRaises(RewriterError) as raised:
                rewriter.rewrite("SELECT * WHERE { ?s ?p ?o }", "query-1")

        self.assertEqual(raised.exception.code, "pipes_unavailable")
        self.assertFalse(raised.exception.retryable)

    def test_wraps_pipe_write_and_read_failures(self) -> None:
        """Expose child-process pipe failures as client errors."""

        write_failure = fake_process("")
        write_failure.stdin = MagicMock()
        write_failure.stdin.write.side_effect = BrokenPipeError("closed")

        read_failure = fake_process("")
        read_failure.stdout = MagicMock()
        read_failure.stdout.readline.side_effect = OSError("read failed")

        cases = (
            (write_failure, "send a request", "request_write_failed"),
            (read_failure, "read a response", "response_read_failed"),
        )
        for process, message, code in cases:
            with self.subTest(message=message):
                with patch(
                    "sparql_rewriter.java_client.subprocess.Popen",
                    return_value=process,
                ):
                    rewriter = JavaRewriter(Path("rewriter.jar"))
                    with self.assertRaisesRegex(RewriterError, message) as raised:
                        rewriter.rewrite(
                            "SELECT * WHERE { ?s ?p ?o }", "query-1")
                self.assertEqual(raised.exception.code, code)
                self.assertTrue(raised.exception.retryable)
                self.assertEqual(raised.exception.details["query_id"], "query-1")
                self.assertIsInstance(raised.exception.__cause__, OSError)


if __name__ == "__main__":
    unittest.main()
