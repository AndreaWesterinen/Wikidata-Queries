"""Unit tests for the local Python rewrite helpers."""

from __future__ import annotations

from pathlib import Path
import sys
import unittest
from unittest.mock import MagicMock, call, patch


PROJECT_ROOT = Path(__file__).resolve().parents[1]
sys.path.insert(0, str(PROJECT_ROOT))

from rewrite import RewriterError, rewrite_queries, rewrite_query  # noqa: E402


class RewriteTest(unittest.TestCase):
    """Verify that local helpers reuse and close the Java client."""

    def test_rewrite_query(self) -> None:
        """Rewrite one query with the supplied JAR."""

        client = MagicMock()
        client.__enter__.return_value = client
        client.request.return_value = {
            "status": "ok",
            "result": {"query_id": "one"}
        }
        with patch("rewrite.JavaRewriter",
                return_value=client) as constructor:
            result = rewrite_query("query text", "one", Path("rewriter.jar"))

        constructor.assert_called_once_with(Path("rewriter.jar"))
        client.request.assert_called_once_with("query text", "one")
        client.__exit__.assert_called_once()
        self.assertEqual(result, {"query_id": "one"})

    def test_rewrite_queries(self) -> None:
        """Rewrite an iterable in order through the Java process."""

        client = MagicMock()
        client.__enter__.return_value = client
        client.request.side_effect = (
            {"status": "ok", "result": {"query_id": "first"}},
            {"status": "ok", "result": {"query_id": "second"}}
        )
        queries = iter((("first", "q1"), ("second", "q2")))
        with patch("rewrite.JavaRewriter",
                return_value=client) as constructor:
            result = rewrite_queries(queries, Path("rewriter.jar"))

        constructor.assert_called_once_with(Path("rewriter.jar"))
        self.assertEqual(
            client.request.call_args_list,
            [call("q1", "first"), call("q2", "second")]
        )
        client.__exit__.assert_called_once()
        self.assertEqual(
            result,
            [("first", {"query_id": "first"}),
             ("second", {"query_id": "second"})]
        )

    def test_rejects_error_or_missing_result(self) -> None:
        """Interpret Java response details in the rewrite layer."""

        responses = (
            ({"status": "protocol_error",
              "diagnostic": {"message": "bad request"}}, "bad request"),
            ({"status": "ok"}, "no rewrite result")
        )
        for response, message in responses:
            with self.subTest(message=message):
                client = MagicMock()
                client.__enter__.return_value = client
                client.request.return_value = response
                with patch("rewrite.JavaRewriter",
                        return_value=client):
                    with self.assertRaisesRegex(RewriterError, message):
                        rewrite_query("query", "one", Path("rewriter.jar"))


if __name__ == "__main__":
    unittest.main()
