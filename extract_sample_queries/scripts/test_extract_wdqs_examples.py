from __future__ import annotations

import tempfile
import unittest
from pathlib import Path

import extract_wdqs_examples as extractor


class ExtractWdqsExamplesTests(unittest.TestCase):
    def test_build_parse_url_uses_custom_api_url(self) -> None:
        url = extractor.build_parse_url(
            "Project:SPARQL/examples",
            "https://example.org/w/api.php",
        )

        self.assertTrue(url.startswith("https://example.org/w/api.php?"))
        self.assertIn("page=Project%3ASPARQL%2Fexamples", url)

    def test_html_parser_extracts_category_title_and_query(self) -> None:
        html = """
        <div class="mw-heading"><h2>General</h2></div>
        <div class="mw-heading"><h3>Cats</h3></div>
        <div class="mw-highlight mw-highlight-lang-sparql">
          <pre><span class="lineno">1</span>SELECT * WHERE {
            ?item wdt:P31 wd:Q146 .
          }</pre>
        </div>
        """
        parser = extractor.ExamplesHTMLParser()
        parser.feed(html)
        parser.close()

        self.assertEqual(len(parser.examples), 1)
        self.assertEqual(parser.examples[0].category, "General")
        self.assertEqual(parser.examples[0].title, "Cats")
        self.assertEqual(
            parser.examples[0].query,
            "          SELECT * WHERE {\n            ?item wdt:P31 wd:Q146 .\n          }",
        )

    def test_write_examples_atomically_sanitizes_dedupes_and_omits_combined_export(self) -> None:
        examples = [
            extractor.Example(category="A/B Category?", title="Cats?", query="ASK {}"),
            extractor.Example(category="A/B Category?", title="Cats", query="SELECT * WHERE {}"),
            extractor.Example(category="", title="", query="CONSTRUCT WHERE {}"),
        ]
        with tempfile.TemporaryDirectory() as tmp_dir:
            output_dir = Path(tmp_dir) / "examples"
            output_dir.mkdir()
            (output_dir / "stale.rq").write_text("stale\n", encoding="utf-8")

            stats = extractor.write_examples_atomically(output_dir, examples)

            self.assertEqual(stats.category_count, 2)
            self.assertEqual(stats.example_count, 3)
            self.assertEqual(stats.duplicate_category_names, 0)
            self.assertEqual(stats.duplicate_file_names, 1)
            self.assertEqual(stats.uncategorized_examples, 1)
            self.assertFalse((output_dir / "stale.rq").exists())
            self.assertFalse((output_dir / "all_examples.txt").exists())
            self.assertEqual(
                (output_dir / "AB_Category" / "Cats.rq").read_text(encoding="utf-8"),
                "ASK {}\n",
            )
            self.assertEqual(
                (output_dir / "AB_Category" / "Cats_2.rq").read_text(encoding="utf-8"),
                "SELECT * WHERE {}\n",
            )
            self.assertEqual(
                (output_dir / "Uncategorized" / "Untitled.rq").read_text(encoding="utf-8"),
                "CONSTRUCT WHERE {}\n",
            )


if __name__ == "__main__":
    unittest.main()
