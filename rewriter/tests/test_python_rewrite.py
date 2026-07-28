"""Unit tests for the thin Python rewrite orchestration functions."""

from __future__ import annotations

from pathlib import Path
import sys
import unittest
from unittest.mock import MagicMock, patch


PROJECT_ROOT = Path(__file__).resolve().parents[1]
sys.path.insert(0, str(PROJECT_ROOT / "python"))

from sparql_rewriter.rewrite import (  # noqa: E402
    iter_rewrite_queries,
    rewrite_queries,
    rewrite_query,
)
from sparql_rewriter.java_client import RewriterError  # noqa: E402


class RewriteOrchestrationTest(unittest.TestCase):
    """Verify process ownership, reuse, and ordering."""

    def test_rewrite_query_runs_supplied_jar(self) -> None:
        """Create and close a process for the supplied JAR path."""

        process = MagicMock()
        process.__enter__.return_value = process
        process.rewrite.return_value = {"query_id": "one"}
        with patch("sparql_rewriter.rewrite.JavaRewriter", return_value=process) as cls:
            result = rewrite_query("query text", "one", Path("rewriter.jar"))

        cls.assert_called_once_with(Path("rewriter.jar"))
        process.__exit__.assert_called_once()
        self.assertEqual(result, {"query_id": "one"})

    def test_rewrite_queries_uses_one_process_and_preserves_order(self) -> None:
        """Consume an iterable through one Java client in input order."""

        process = MagicMock()
        process.rewrite.side_effect = (
            {"query_id": "first"},
            {"query_id": "second"},
        )
        queries = ((item for item in (("first", "q1"), ("second", "q2"))))
        with patch("sparql_rewriter.rewrite.JavaRewriter", return_value=process) as cls:
            result = rewrite_queries(queries, Path("rewriter.jar"))

        cls.assert_called_once_with(Path("rewriter.jar"))
        self.assertEqual(
            process.rewrite.call_args_list,
            [unittest.mock.call("q1", "first"), unittest.mock.call("q2", "second")],
        )
        self.assertEqual(
            result,
            [("first", {"query_id": "first"}), ("second", {"query_id": "second"})],
        )
        process.close.assert_called_once_with()

    def test_stream_rotates_bounded_processes(self) -> None:
        """Replace a partition JVM after its configured request bound."""

        first = MagicMock()
        second = MagicMock()
        first.rewrite.return_value = {"query_id": "first"}
        second.rewrite.return_value = {"query_id": "second"}
        with patch(
            "sparql_rewriter.rewrite.JavaRewriter",
            side_effect=(first, second),
        ) as cls:
            result = list(iter_rewrite_queries(
                (("first", "q1"), ("second", "q2")),
                Path("rewriter.jar"),
                max_requests=1,
            ))

        self.assertEqual(len(result), 2)
        self.assertEqual(cls.call_count, 2)
        first.close.assert_called_once_with()
        second.close.assert_called_once_with()

    def test_stream_rejects_invalid_request_bound(self) -> None:
        """Reject an unbounded or malformed process-lifetime configuration."""

        with self.assertRaisesRegex(RewriterError, "positive integer"):
            list(iter_rewrite_queries([], Path("rewriter.jar"), max_requests=0))


if __name__ == "__main__":
    unittest.main()
