from __future__ import annotations

import csv
import tempfile
import unittest
from pathlib import Path

import extract_wmcloud_examples as extractor


def write_csv(path: Path, rows: list[dict[str, str]], fieldnames: list[str] | None = None) -> None:
    with path.open("w", newline="", encoding="utf-8") as handle:
        writer = csv.DictWriter(handle, fieldnames=fieldnames or extractor.REQUIRED_COLUMNS)
        writer.writeheader()
        writer.writerows(rows)


class ExtractWmcloudExamplesTests(unittest.TestCase):
    def test_writes_sanitized_deduped_query_files_atomically(self) -> None:
        with tempfile.TemporaryDirectory() as tmp_dir:
            base = Path(tmp_dir)
            input_csv = base / "large_sparql_queries_dataset.csv"
            output_dir = base / "wmcloud_queries"
            output_dir.mkdir()
            (output_dir / "stale.rq").write_text("stale\n", encoding="utf-8")
            write_csv(
                input_csv,
                [
                    {"page_id": "1", "page_title": "A/B Test?", "query": "\nSELECT * WHERE {}\n"},
                    {"page_id": "2", "page_title": "AB Test", "query": "ASK {}\n"},
                    {"page_id": "3", "page_title": "", "query": "CONSTRUCT WHERE {}\n"},
                    {"page_id": "4", "page_title": "Empty", "query": "   \n"},
                ],
            )

            stats = extractor.write_queries_atomically(input_csv, output_dir)

            self.assertEqual(stats.row_count, 4)
            self.assertEqual(stats.query_count, 3)
            self.assertEqual(stats.duplicate_file_names, 1)
            self.assertEqual(stats.missing_title_count, 1)
            self.assertEqual(stats.skipped_empty_queries, 1)
            self.assertFalse((output_dir / "stale.rq").exists())
            self.assertEqual((output_dir / "AB_Test.rq").read_text(encoding="utf-8"), "SELECT * WHERE {}\n")
            self.assertEqual((output_dir / "AB_Test_2.rq").read_text(encoding="utf-8"), "ASK {}\n")
            self.assertEqual((output_dir / "Page_3.rq").read_text(encoding="utf-8"), "CONSTRUCT WHERE {}\n")

    def test_rejects_unexpected_csv_columns(self) -> None:
        with tempfile.TemporaryDirectory() as tmp_dir:
            base = Path(tmp_dir)
            input_csv = base / "bad.csv"
            output_dir = base / "wmcloud_queries"
            write_csv(input_csv, [{"page_id": "1", "page_title": "Title"}], fieldnames=["page_id", "page_title"])

            with self.assertRaisesRegex(RuntimeError, "Expected CSV columns"):
                extractor.write_queries_atomically(input_csv, output_dir)

            self.assertFalse(output_dir.exists())

    def test_dedupes_case_insensitive_file_name_collisions(self) -> None:
        with tempfile.TemporaryDirectory() as tmp_dir:
            base = Path(tmp_dir)
            input_csv = base / "large_sparql_queries_dataset.csv"
            output_dir = base / "wmcloud_queries"
            write_csv(
                input_csv,
                [
                    {"page_id": "1", "page_title": "Case", "query": "ASK {}\n"},
                    {"page_id": "2", "page_title": "case", "query": "SELECT * WHERE {}\n"},
                ],
            )

            stats = extractor.write_queries_atomically(input_csv, output_dir)

            self.assertEqual(stats.query_count, 2)
            self.assertEqual(stats.duplicate_file_names, 1)
            self.assertEqual((output_dir / "Case.rq").read_text(encoding="utf-8"), "ASK {}\n")
            self.assertEqual((output_dir / "case_2.rq").read_text(encoding="utf-8"), "SELECT * WHERE {}\n")

    def test_dedupes_generated_file_name_collisions(self) -> None:
        with tempfile.TemporaryDirectory() as tmp_dir:
            base = Path(tmp_dir)
            input_csv = base / "large_sparql_queries_dataset.csv"
            output_dir = base / "wmcloud_queries"
            write_csv(
                input_csv,
                [
                    {"page_id": "1", "page_title": "Title", "query": "ASK {}\n"},
                    {"page_id": "2", "page_title": "Title", "query": "SELECT * WHERE {}\n"},
                    {"page_id": "3", "page_title": "Title_2", "query": "CONSTRUCT WHERE {}\n"},
                ],
            )

            stats = extractor.write_queries_atomically(input_csv, output_dir)

            self.assertEqual(stats.query_count, 3)
            self.assertEqual(stats.duplicate_file_names, 2)
            self.assertEqual((output_dir / "Title.rq").read_text(encoding="utf-8"), "ASK {}\n")
            self.assertEqual((output_dir / "Title_2.rq").read_text(encoding="utf-8"), "SELECT * WHERE {}\n")
            self.assertEqual((output_dir / "Title_2_2.rq").read_text(encoding="utf-8"), "CONSTRUCT WHERE {}\n")


if __name__ == "__main__":
    unittest.main()
