from __future__ import annotations

import tempfile
import unittest
from pathlib import Path

import report_blazegraph_usage_in_examples as report


class ReportBlazegraphUsageTests(unittest.TestCase):
    def test_default_example_dirs_are_next_to_scripts_dir(self) -> None:
        dirs = report.resolve_example_dirs(None)

        self.assertEqual([path.name for path in dirs], report.DEFAULT_EXAMPLE_DIRS)
        self.assertTrue(all(path.parent == report.PROJECT_ROOT for path in dirs))

    def test_default_output_paths_are_in_project_root(self) -> None:
        self.assertEqual(
            report.resolve_output_path(None, "blazegraph_usage_report.md"),
            report.DEFAULT_REPORT_DIR / "blazegraph_usage_report.md",
        )

    def test_matching_is_case_insensitive(self) -> None:
        with tempfile.TemporaryDirectory() as tmp_dir:
            examples_dir = Path(tmp_dir)
            query_path = examples_dir / "query.rq"
            query_path.write_text("select * where { service wikibase:around {} }\n", encoding="utf-8")

            report_data = report.collect_report_data([examples_dir])
            markdown = report.build_report(report_data)

        self.assertIn("| SERVICE Extensions | `SERVICE wikibase:around` | 1 |", markdown)

    def test_summary_text_uses_exact_count(self) -> None:
        self.assertEqual(report.summary_text(42), "42 matching files (not listed individually)")

    def test_summary_threshold_summarizes_large_match_lists(self) -> None:
        with tempfile.TemporaryDirectory() as tmp_dir:
            examples_dir = Path(tmp_dir)
            for index in range(2):
                (examples_dir / f"query_{index}.rq").write_text(
                    "SELECT * WHERE { SERVICE wikibase:around {} }\n",
                    encoding="utf-8",
                )

            report_data = report.collect_report_data([examples_dir], summary_threshold=2)
            markdown = report.build_report(report_data)

        self.assertIn("| SERVICE Extensions | `SERVICE wikibase:around` | 2 |", markdown)
        self.assertIn("- 2 matching files (not listed individually)", markdown)
        self.assertNotIn("query_0.rq", markdown)

    def test_html_renderer_turns_markdown_code_spans_into_code_elements(self) -> None:
        rendered = report.render_inline_markdown_html("Use `SERVICE wikibase:label` here")

        self.assertEqual(rendered, "Use <code>SERVICE wikibase:label</code> here")

    def test_report_data_is_shared_by_markdown_and_html_renderers(self) -> None:
        with tempfile.TemporaryDirectory() as tmp_dir:
            examples_dir = Path(tmp_dir)
            query_path = examples_dir / "query.rq"
            query_path.write_text("SELECT * WHERE { SERVICE wikibase:around {} }\n", encoding="utf-8")

            report_data = report.collect_report_data([examples_dir])
            markdown = report.build_report(report_data)
            html = report.build_report_html(report_data)

        self.assertIn("| SERVICE Extensions | `SERVICE wikibase:around` | 1 |", markdown)
        self.assertIn("<td><code>SERVICE wikibase:around</code></td><td>1</td>", html)

    def test_report_title_is_rendered(self) -> None:
        with tempfile.TemporaryDirectory() as tmp_dir:
            examples_dir = Path(tmp_dir)
            (examples_dir / "query.rq").write_text("SELECT * WHERE {}\n", encoding="utf-8")

            report_data = report.collect_report_data([examples_dir], title="Custom Report")

        self.assertIn("# Custom Report", report.build_report(report_data))
        self.assertIn("<title>Custom Report</title>", report.build_report_html(report_data))

    def test_report_includes_wikibase_some_value_findings(self) -> None:
        with tempfile.TemporaryDirectory() as tmp_dir:
            examples_dir = Path(tmp_dir)
            query_path = examples_dir / "query.rq"
            query_path.write_text("SELECT * WHERE { ?statement ?predicate wikibase:someValue }\n", encoding="utf-8")

            report_data = report.collect_report_data([examples_dir])
            markdown = report.build_report(report_data)
            html = report.build_report_html(report_data)

        self.assertIn("| Wikidata RDF Predicates | `wikibase:someValue` | 1 |", markdown)
        self.assertNotIn("| Supporting Blazegraph-Specific Syntax | `wikibase:someValue` |", markdown)
        self.assertIn("<td><code>wikibase:someValue</code></td><td>1</td>", html)

    def test_report_includes_wikibase_geo_globe_findings(self) -> None:
        with tempfile.TemporaryDirectory() as tmp_dir:
            examples_dir = Path(tmp_dir)
            query_path = examples_dir / "query.rq"
            query_path.write_text("SELECT * WHERE { ?coord wikibase:geoGlobe ?globe }\n", encoding="utf-8")

            report_data = report.collect_report_data([examples_dir])
            markdown = report.build_report(report_data)
            html = report.build_report_html(report_data)

        self.assertIn("| Supporting Blazegraph-Specific Syntax | `wikibase:geoGlobe` | 1 |", markdown)
        self.assertIn("<td><code>wikibase:geoGlobe</code></td><td>1</td>", html)

    def test_report_includes_wikibase_globe_findings(self) -> None:
        with tempfile.TemporaryDirectory() as tmp_dir:
            examples_dir = Path(tmp_dir)
            query_path = examples_dir / "query.rq"
            query_path.write_text("SELECT * WHERE { ?coord wikibase:globe ?globe }\n", encoding="utf-8")

            report_data = report.collect_report_data([examples_dir])
            markdown = report.build_report(report_data)
            html = report.build_report_html(report_data)

        self.assertIn("| Wikidata RDF Predicates | `wikibase:globe` | 1 |", markdown)
        self.assertNotIn("| Supporting Blazegraph-Specific Syntax | `wikibase:globe` |", markdown)
        self.assertIn("<td><code>wikibase:globe</code></td><td>1</td>", html)

    def test_report_includes_qlever_commons_federation_in_miscellaneous_table(self) -> None:
        with tempfile.TemporaryDirectory() as tmp_dir:
            examples_dir = Path(tmp_dir)
            query_path = examples_dir / "query.rq"
            query_path.write_text(
                "SELECT * WHERE { SERVICE <https://qlever.dev/api/wikimedia-commons> { ?s ?p ?o } }\n",
                encoding="utf-8",
            )

            report_data = report.collect_report_data([examples_dir])
            markdown = report.build_report(report_data)
            html = report.build_report_html(report_data)

        self.assertIn("## Miscellaneous", markdown)
        self.assertIn(
            "| Federated SERVICE endpoint | `https://qlever.dev/api/wikimedia-commons` | 1 |",
            markdown,
        )
        self.assertIn("| Federated SERVICE endpoint | Other `SERVICE <...>` endpoint | 0 |", markdown)
        self.assertIn(
            "<td>Federated SERVICE endpoint</td><td><code>https://qlever.dev/api/wikimedia-commons</code></td><td>1</td>",
            html,
        )

    def test_report_includes_other_federated_service_endpoints_in_miscellaneous_table(self) -> None:
        with tempfile.TemporaryDirectory() as tmp_dir:
            examples_dir = Path(tmp_dir)
            query_path = examples_dir / "query.rq"
            query_path.write_text(
                """SELECT * WHERE {
  SERVICE <https://query.example.org/sparql> { ?s ?p ?o }
  SERVICE SILENT <https://query.other.example/sparql> { ?s ?p ?o }
  SERVICE <https://qlever.dev/api/wikimedia-commons> { ?s ?p ?o }
  SERVICE wikibase:label {}
  SERVICE bd:slice {}
  SERVICE mwapi:Example {}
  SERVICE gas:service {}
}
""",
                encoding="utf-8",
            )

            report_data = report.collect_report_data([examples_dir])
            markdown = report.build_report(report_data)
            html = report.build_report_html(report_data)

        self.assertIn("| Federated SERVICE endpoint | Other `SERVICE <...>` endpoint | 1 |", markdown)
        self.assertIn(
            "<td>Federated SERVICE endpoint</td><td>Other <code>SERVICE &lt;...&gt;</code> endpoint</td><td>1</td>",
            html,
        )

    def test_report_includes_mwapi_request_details_in_miscellaneous_table(self) -> None:
        with tempfile.TemporaryDirectory() as tmp_dir:
            examples_dir = Path(tmp_dir)
            query_path = examples_dir / "query.rq"
            query_path.write_text(
                """SELECT * WHERE {
  SERVICE wikibase:mwapi {
    bd:serviceParam wikibase:api "Generator" ;
      mwapi:generator "search" .
  }
  SERVICE wikibase:mwapi {
    bd:serviceParam wikibase:api "Search" .
  }
}
""",
                encoding="utf-8",
            )

            report_data = report.collect_report_data([examples_dir])
            markdown = report.build_report(report_data)
            html = report.build_report_html(report_data)

        self.assertIn("| `wikibase:api` value | `Generator` | 1 |", markdown)
        self.assertIn("| `wikibase:api` value | `Categories` | 0 |", markdown)
        self.assertIn("| `wikibase:api` value | `Search` | 1 |", markdown)
        self.assertIn(
            "| `mwapi:generator` value for `wikibase:api` `Generator` | `search` | 1 |",
            markdown,
        )
        self.assertNotIn("| Supporting Blazegraph-Specific Syntax | `bd:serviceParam` |", markdown)
        self.assertIn(
            "<td><code>mwapi:generator</code> value for <code>wikibase:api</code> <code>Generator</code></td><td><code>search</code></td><td>1</td>",
            html,
        )

    def test_merge_report_data_sums_counts_without_rescanning(self) -> None:
        with tempfile.TemporaryDirectory() as tmp_dir:
            base = Path(tmp_dir)
            one = base / "one"
            two = base / "two"
            one.mkdir()
            two.mkdir()
            (one / "one.rq").write_text(
                """SELECT * WHERE {
  SERVICE wikibase:around {}
  SERVICE wikibase:mwapi {
    bd:serviceParam wikibase:api "Generator" ;
      mwapi:generator "search" .
  }
}
""",
                encoding="utf-8",
            )
            (two / "two.rq").write_text(
                """SELECT * WHERE {
  SERVICE wikibase:around {}
  SERVICE wikibase:mwapi {
    bd:serviceParam wikibase:api "Generator" ;
      mwapi:generator "search" .
  }
}
""",
                encoding="utf-8",
            )

            first = report.collect_report_data([one], title="One")
            second = report.collect_report_data([two], title="Two")
            merged = report.merge_report_data("Merged", [first, second], report.DEFAULT_SUMMARY_THRESHOLD)

        self.assertEqual(merged.total_queries, 2)
        self.assertEqual(merged.examples_dirs, [one, two])
        markdown = report.build_report(merged)
        self.assertIn("| SERVICE Extensions | `SERVICE wikibase:around` | 2 |", markdown)
        self.assertIn("| `wikibase:api` value | `Generator` | 2 |", markdown)
        self.assertIn(
            "| `mwapi:generator` value for `wikibase:api` `Generator` | `search` | 2 |",
            markdown,
        )

    def test_generate_standard_reports_writes_four_report_pairs(self) -> None:
        with tempfile.TemporaryDirectory() as tmp_dir:
            base = Path(tmp_dir)
            report_dir = base / "reports"
            examples = base / "examples"
            advanced = base / "advanced_examples"
            human = base / "human_examples"
            maintenance = base / "maintenance_examples"
            commons = base / "commons_examples"
            wmcloud = base / "wmcloud_queries"
            other_root = base / "other_examples"
            other = other_root / "one"
            for directory in [examples, advanced, human, maintenance, wmcloud, other]:
                directory.mkdir(parents=True)
                (directory / "query.rq").write_text(
                    "SELECT * WHERE { SERVICE wikibase:label {} }\n",
                    encoding="utf-8",
                )
            commons.mkdir()
            (commons / "query.rq").write_text(
                "SELECT * WHERE { SERVICE wikibase:label {} }\n",
                encoding="utf-8",
            )

            original_project_root = report.PROJECT_ROOT
            original_report_dir = report.DEFAULT_REPORT_DIR
            try:
                report.PROJECT_ROOT = base
                report.DEFAULT_REPORT_DIR = report_dir
                written = report.generate_standard_reports(report_dir, summary_threshold=100)
            finally:
                report.PROJECT_ROOT = original_project_root
                report.DEFAULT_REPORT_DIR = original_report_dir

            self.assertEqual(len(written), 8)
            self.assertEqual(
                sorted(path.name for path in written),
                [
                    "all_blazegraph_usage_report.html",
                    "all_blazegraph_usage_report.md",
                    "other_blazegraph_usage_report.html",
                    "other_blazegraph_usage_report.md",
                    "wikimedia_blazegraph_usage_report.html",
                    "wikimedia_blazegraph_usage_report.md",
                    "wmcloud_blazegraph_usage_report.html",
                    "wmcloud_blazegraph_usage_report.md",
                ],
            )
            self.assertIn(
                "# Blazegraph Feature Usage Report: Wikimedia Queries",
                (report_dir / "wikimedia_blazegraph_usage_report.md").read_text(encoding="utf-8"),
            )
            self.assertIn(
                "| SERVICE Extensions | `SERVICE wikibase:label` | 6 |",
                (report_dir / "all_blazegraph_usage_report.md").read_text(encoding="utf-8"),
            )
            self.assertNotIn(
                "commons_examples",
                (report_dir / "wikimedia_blazegraph_usage_report.md").read_text(encoding="utf-8"),
            )


if __name__ == "__main__":
    unittest.main()
