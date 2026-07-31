"""Unit tests for the small local Java process client."""

from __future__ import annotations

import io
import json
from pathlib import Path
import sys
import unittest
from unittest.mock import MagicMock, patch


PROJECT_ROOT = Path(__file__).resolve().parents[1]
sys.path.insert(0, str(PROJECT_ROOT))

from rewrite import JavaRewriter, RewriterError  # noqa: E402


def response_line(request_id: str = "request-1", query_id: str = "query-1") -> str:
    """Return one minimal protocol response."""

    response: dict[str, object] = {
        "protocol_version": 1,
        "request_id": request_id,
        "query_id": query_id,
        "status": "ok",
        "result": {"query_id": query_id}
    }
    return json.dumps(response) + "\n"


def fake_process(stdout_text: str) -> MagicMock:
    """Create and return a fake subprocess for testing via MagicMock()."""

    process = MagicMock()
    process.stdin = io.StringIO()
    process.stdout = io.StringIO(stdout_text)
    return process


class JavaRewriterTest(unittest.TestCase):
    """Verify the behavior owned by the local harness."""

    def test_starts_jar_and_reuses_process(self) -> None:
        """Send ordered requests through the Java process."""

        process = fake_process(
            response_line() + response_line("request-2", "query-2"))
        with patch("rewrite.subprocess.Popen",
                return_value=process) as popen:
            rewriter = JavaRewriter(Path("rewriter.jar"))
            first = rewriter.request("query one", "query-1")
            second = rewriter.request("query two", "query-2")

        popen.assert_called_once_with(
            ["java", "-jar", "rewriter.jar"],
            stdin=-1,
            stdout=-1,
            text=True,
            encoding="utf-8"
        )
        requests = [json.loads(line)
                    for line in process.stdin.getvalue().splitlines()]
        self.assertEqual(
            [(item["request_id"], item["query_id"], item["query"])
             for item in requests],
            [("request-1", "query-1", "query one"),
             ("request-2", "query-2", "query two")]
        )
        self.assertEqual(first["result"], {"query_id": "query-1"})
        self.assertEqual(second["result"], {"query_id": "query-2"})

    def test_closes_process(self) -> None:
        """Close the streams and wait for Java on context exit."""

        process = fake_process("")
        with patch("rewrite.subprocess.Popen",
                return_value=process):
            with JavaRewriter(Path("rewriter.jar")):
                pass

        self.assertTrue(process.stdin.closed)
        self.assertTrue(process.stdout.closed)
        process.wait.assert_called_once_with()

    def test_reports_start_failure(self) -> None:
        """Wrap a failure to start Java."""

        with patch("rewrite.subprocess.Popen",
                side_effect=OSError("missing")):
            with self.assertRaisesRegex(RewriterError, "Could not start"):
                JavaRewriter(Path("rewriter.jar"))

    def test_rejects_bad_responses(self) -> None:
        """Reject missing, malformed, and mismatched responses."""

        cases = (
            ("", "without a response"),
            ("not-json\n", "invalid JSON"),
            ("[]\n", "non-object"),
            (response_line(query_id="other"), "does not match")
        )
        for line, message in cases:
            with self.subTest(message=message):
                process = fake_process(line)
                with patch("rewrite.subprocess.Popen",
                        return_value=process):
                    rewriter = JavaRewriter(Path("rewriter.jar"))
                    with self.assertRaisesRegex(RewriterError, message):
                        rewriter.request("query", "query-1")


if __name__ == "__main__":
    unittest.main()
