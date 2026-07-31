"""Unit tests for fixture discovery and output path selection."""

from __future__ import annotations

from pathlib import Path
import sys
import tempfile
import unittest


PROJECT_ROOT = Path(__file__).resolve().parents[1]
sys.path.insert(0, str(PROJECT_ROOT))

from rewrite_fixtures import destination_path, discover_fixtures  # noqa: E402


class FixturePathTest(unittest.TestCase):
    """Verify recursive fixture catalog path behavior."""

    def test_discovers_nested_originals_in_sorted_order(self) -> None:
        """Find only original query files and derive "golden" paths."""

        with tempfile.TemporaryDirectory() as directory:
            root = Path(directory)
            nested = root / "rule"
            nested.mkdir()
            (nested / "b.original.rq").write_text("b", encoding="utf-8")
            (nested / "a.original.rq").write_text("a", encoding="utf-8")
            (nested / "ignored.rewritten.rq").write_text("x", encoding="utf-8")

            fixtures = discover_fixtures(root)

        self.assertEqual(
            [(item[0].name, item[1].name) for item in fixtures],
            [("a.original.rq", "a.rewritten.rq"),
             ("b.original.rq", "b.rewritten.rq")]
        )

    def test_empty_fixture_root_is_rejected(self) -> None:
        """Fail clearly when a requested catalog contains no originals."""

        with tempfile.TemporaryDirectory() as directory:
            with self.assertRaisesRegex(OSError, "No \*\.original\.rq fixtures"):
                discover_fixtures(Path(directory))

    def test_destination_is_generated_or_explicit_golden(self) -> None:
        """Keep normal output separate unless "golden" replacement is requested."""

        fixture_root = Path("fixtures")
        original = fixture_root / "rule" / "case.original.rq"
        output_root = Path("generated")
        self.assertEqual(
            destination_path(original, fixture_root, output_root, False),
            output_root / "rule" / "case.rewritten.rq"
        )
        self.assertEqual(
            destination_path(original, fixture_root, output_root, True),
            fixture_root / "rule" / "case.rewritten.rq"
        )


if __name__ == "__main__":
    unittest.main()
