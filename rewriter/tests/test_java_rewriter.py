"""Boundary tests for the persistent Java rewrite engine."""

from __future__ import annotations

from pathlib import Path
import sys
import unittest


PROJECT_ROOT = Path(__file__).resolve().parents[1]
JAR = PROJECT_ROOT / "java-rewriter" / "target" / "sparql-rewriter.jar"
sys.path.insert(0, str(PROJECT_ROOT))

from rewrite import rewrite_queries, rewrite_query  # noqa: E402


class JavaRewriterTest(unittest.TestCase):
    """Verify transport behavior not duplicated by fixture expectations."""

    def test_one_process_rewrites_multiple_queries(self) -> None:
        """Pair ordered responses with requests over one JVM lifetime."""

        label_query = (
            PROJECT_ROOT
            / "fixtures"
            / "wikibase-label"
            / "manual-label-only.original.rq"
        ).read_text(encoding="utf-8")
        unchanged_query = "SELECT * WHERE { ?s ?p ?o }\n"

        first, second = [result for _, result in rewrite_queries(
            (("first", label_query), ("second", unchanged_query)), JAR)]

        self.assertEqual(first["rewrite_status"], "rewritten")
        self.assertNotIn("wikibase:label", first["rewritten_query"])
        self.assertNotIn("bd:", first["rewritten_query"])
        self.assertNotIn("gas:", first["rewritten_query"])
        self.assertNotIn("hint:", first["rewritten_query"])
        self.assertNotIn("mwapi:", first["rewritten_query"])
        self.assertEqual(second["rewrite_status"], "unchanged")
        self.assertEqual(second["rewritten_query"], unchanged_query)

    def test_parse_error_is_a_query_result(self) -> None:
        """Keep parse failures distinct from transport failures."""

        result = rewrite_query("SELECT WHERE {", "invalid", JAR)

        self.assertEqual(result["rewrite_status"], "parse_error")
        self.assertIsNone(result["rewritten_query"])
        self.assertEqual(result["rewrites"], [])

    def test_top_level_dataset_clause_is_rejected(self) -> None:
        """Reject root FROM clauses, which have no Wikidata dataset meaning."""

        query = "SELECT * FROM <http://example.com/data> WHERE { ?s ?p ?o }"
        result = rewrite_query(query, "dataset", JAR)

        self.assertEqual(result["rewrite_status"], "skipped_unsupported")
        self.assertIsNone(result["rewritten_query"])
        self.assertEqual(result["errors"][0]["code"], "unsupported_dataset_clause")


if __name__ == "__main__":
    unittest.main()
