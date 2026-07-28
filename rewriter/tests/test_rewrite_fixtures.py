"""Manifest-driven regression tests for every checked-in rewrite fixture."""

from __future__ import annotations

import json
from pathlib import Path
import sys
import unittest


PROJECT_ROOT = Path(__file__).resolve().parents[1]
FIXTURE_ROOT = PROJECT_ROOT / "fixtures"
sys.path.insert(0, str(PROJECT_ROOT / "python"))
sys.path.insert(0, str(PROJECT_ROOT))

from sparql_rewriter import rewrite_queries  # noqa: E402


class RewriteFixtureTest(unittest.TestCase):
    """Run every JSON fixture manifest beneath the fixture root."""

    def test_all_fixture_manifests(self) -> None:
        """Require every result status, diagnostic, and golden query to match."""

        manifests = sorted(FIXTURE_ROOT.rglob("*.json"))
        self.assertTrue(manifests, "No fixture manifests were found")
        queries = []
        records = {}
        referenced_originals = set()
        expected_rewrites = set()
        for manifest_path in manifests:
            record = json.loads(manifest_path.read_text(encoding="utf-8"))
            self.assertNotIn(
                record["query_id"],
                records,
                f"Duplicate query_id in {manifest_path}",
            )
            records[record["query_id"]] = (manifest_path, record)
            original_path = manifest_path.parent / record["original_query_path"]
            referenced_originals.add(original_path)
            queries.append(
                (record["query_id"], original_path.read_text(encoding="utf-8"))
            )
            if record["expected_rewrite_status"] in {"rewritten", "unchanged"}:
                expected_rewrites.add(manifest_path.with_suffix(".rewritten.rq"))

        self.assertEqual(
            set(FIXTURE_ROOT.rglob("*.original.rq")),
            referenced_originals,
            "Every original query must have exactly one fixture manifest",
        )
        self.assertEqual(
            set(FIXTURE_ROOT.rglob("*.rewritten.rq")),
            expected_rewrites,
            "Golden queries must exist exactly for successful fixture manifests",
        )

        actual = dict(
            rewrite_queries(
                queries,
                PROJECT_ROOT / "java-rewriter" / "target" / "sparql-rewriter.jar",
            )
        )

        for query_id, (manifest_path, expected) in records.items():
            with self.subTest(query_id=query_id):
                result = actual[query_id]
                self.assertEqual(result["query_id"], query_id)
                self.assertEqual(result["rewrite_status"], expected["expected_rewrite_status"])
                self.assertEqual(result["rewrites"], expected["rewrites"])
                self.assertEqual(result["warnings"], expected["expected_warnings"])
                self.assertEqual(result["errors"], expected["expected_errors"])
                if expected["expected_rewrite_status"] in {"rewritten", "unchanged"}:
                    rewritten_path = manifest_path.with_suffix(".rewritten.rq")
                    self.assertEqual(
                        result["rewritten_query"],
                        rewritten_path.read_text(encoding="utf-8"),
                    )
                else:
                    self.assertIsNone(result["rewritten_query"])


if __name__ == "__main__":
    unittest.main()
