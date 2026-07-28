"""Unit tests for general query-directory discovery and output paths."""

from __future__ import annotations

from pathlib import Path
import sys
import tempfile
import unittest
from unittest.mock import patch


PROJECT_ROOT = Path(__file__).resolve().parents[1]
sys.path.insert(0, str(PROJECT_ROOT))

from rewrite_queries import destination_path, discover_queries, main  # noqa: E402


class QueryDirectoryPathTest(unittest.TestCase):
    """Verify recursive query discovery and mirrored output behavior."""

    def test_discovers_queries_recursively_in_sorted_order(self) -> None:
        """Find ordinary queries while excluding prior rewritten outputs."""

        with tempfile.TemporaryDirectory() as directory:
            root = Path(directory)
            nested = root / "nested"
            nested.mkdir()
            (nested / "b.rq").write_text("b", encoding="utf-8")
            (nested / "a.rq").write_text("a", encoding="utf-8")
            (nested / "a.rewritten.rq").write_text("output", encoding="utf-8")
            (nested / "ignored.txt").write_text("text", encoding="utf-8")

            queries = discover_queries(root, root / "output")

        self.assertEqual([path.name for path in queries], ["a.rq", "b.rq"])

    def test_excludes_output_directory_beneath_input(self) -> None:
        """Do not feed a previous output tree back into a later run."""

        with tempfile.TemporaryDirectory() as directory:
            root = Path(directory)
            output = root / "output"
            output.mkdir()
            (root / "input.rq").write_text("input", encoding="utf-8")
            (output / "old.rq").write_text("old", encoding="utf-8")

            queries = discover_queries(root, output)

        self.assertEqual([path.name for path in queries], ["input.rq"])

    def test_empty_query_directory_is_rejected(self) -> None:
        """Fail clearly when the input tree contains no eligible queries."""

        with tempfile.TemporaryDirectory() as directory:
            root = Path(directory)
            with self.assertRaisesRegex(OSError, "No input \\.rq files"):
                discover_queries(root, root / "output")

    def test_destination_preserves_relative_directories(self) -> None:
        """Mirror the input tree and mark each generated query as rewritten."""

        input_root = Path("queries")
        original = input_root / "nested" / "case.rq"

        self.assertEqual(
            destination_path(original, input_root, Path("output")),
            Path("output") / "nested" / "case.rewritten.rq",
        )

    def test_main_rewrites_directory_with_one_batch_call(self) -> None:
        """Send every input through the batch API and write mirrored results."""

        with tempfile.TemporaryDirectory() as directory:
            root = Path(directory)
            input_root = root / "queries"
            output_root = root / "output"
            nested = input_root / "nested"
            nested.mkdir(parents=True)
            (input_root / "one.rq").write_text("query one", encoding="utf-8")
            (nested / "two.rq").write_text("query two", encoding="utf-8")
            results = [
                ("nested/two.rq", {"rewritten_query": "rewritten two"}),
                ("one.rq", {"rewritten_query": "rewritten one"}),
            ]

            with (
                patch(
                    "rewrite_queries.rewrite_queries", return_value=results
                ) as rewrite_batch,
                patch(
                    "sys.argv",
                    [
                        "rewrite_queries.py",
                        str(input_root),
                        "--output-dir",
                        str(output_root),
                    ],
                ),
            ):
                exit_code = main()

            self.assertEqual(exit_code, 0)
            rewrite_batch.assert_called_once()
            query_pairs, _ = rewrite_batch.call_args.args
            self.assertEqual(
                query_pairs,
                [("nested/two.rq", "query two"), ("one.rq", "query one")],
            )
            self.assertEqual(
                (output_root / "one.rewritten.rq").read_text(encoding="utf-8"),
                "rewritten one",
            )
            self.assertEqual(
                (output_root / "nested" / "two.rewritten.rq").read_text(
                    encoding="utf-8"
                ),
                "rewritten two",
            )


if __name__ == "__main__":
    unittest.main()
