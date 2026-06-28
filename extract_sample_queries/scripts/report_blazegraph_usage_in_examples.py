#!/usr/bin/env python3

from __future__ import annotations

import argparse
import html
import re
from dataclasses import dataclass
from pathlib import Path


PAGE_URL = "https://wikitech.wikimedia.org/wiki/User:AWesterinen/Blazegraph_Features_and_Capabilities"


@dataclass(frozen=True)
class Feature:
    name: str
    section: str
    pattern: str
    note: str


FEATURES = [
    Feature(
        name="Stored queries (`SERVICE <http://www.bigdata.com/rdf/stored-query#...>`) ",
        section="Page-Listed Blazegraph Features",
        pattern=r"SERVICE\s*<http://www\.bigdata\.com/rdf/stored-query#[^>]+>",
        note="The Blazegraph StoredQuery page defines stored queries as custom applications exposed through a SERVICE URI. No matches were found in the current local example set.",
    ),
    Feature(
        name="Named sub-queries (`WITH { ... } AS %name`, `INCLUDE %name`)",
        section="Page-Listed Blazegraph Features",
        pattern=r"\bWITH\s*\{|\bINCLUDE\s*%",
        note="Documented on the page as a Blazegraph-specific readability/performance feature.",
    ),
    Feature(
        name="`geof:globe()`",
        section="Page-Listed Function Extensions",
        pattern=r"\bgeof:globe\s*\(",
        note="Listed on the page as a Blazegraph-specific function for extracting globe information from coordinates.",
    ),
    Feature(
        name="`geof:latitude()`",
        section="Page-Listed Function Extensions",
        pattern=r"\bgeof:latitude\s*\(",
        note="Listed on the page as a Blazegraph-specific function for extracting latitude.",
    ),
    Feature(
        name="`geof:longitude()`",
        section="Page-Listed Function Extensions",
        pattern=r"\bgeof:longitude\s*\(",
        note="Listed on the page as a Blazegraph-specific function for extracting longitude.",
    ),
    Feature(
        name="`geof:distance()`",
        section="Page-Listed Function Extensions",
        pattern=r"\bgeof:distance\s*\(",
        note="The page treats this as the geospatial distance function currently used in Blazegraph-backed WDQS queries.",
    ),
    Feature(
        name="`wikibase:decodeUri()`",
        section="Page-Listed Function Extensions",
        pattern=r"\bwikibase:decodeUri\s*\(",
        note="Listed on the page as a custom Blazegraph function extension.",
    ),
    Feature(
        name="`SERVICE wikibase:around`",
        section="Page-Listed SERVICE Extensions",
        pattern=r"SERVICE\s+wikibase:around\b",
        note="Geospatial service extension documented on the page.",
    ),
    Feature(
        name="`SERVICE wikibase:box`",
        section="Page-Listed SERVICE Extensions",
        pattern=r"SERVICE\s+wikibase:box\b",
        note="Geospatial service extension documented on the page.",
    ),
    Feature(
        name="`SERVICE wikibase:label`",
        section="Page-Listed SERVICE Extensions",
        pattern=r"SERVICE\s+wikibase:label\b",
        note="Label service documented on the page.",
    ),
    Feature(
        name="`SERVICE bd:slice`",
        section="Page-Listed SERVICE Extensions",
        pattern=r"SERVICE\s+bd:slice\b",
        note="Slice service documented on the page.",
    ),
    Feature(
        name="`SERVICE wikibase:mwapi`",
        section="Page-Listed SERVICE Extensions",
        pattern=r"SERVICE\s+wikibase:mwapi\b",
        note="MediaWiki API integration service documented on the page.",
    ),
    Feature(
        name="`SERVICE gas:service`",
        section="Page-Listed SERVICE Extensions",
        pattern=r"SERVICE\s+gas:service\b",
        note="Graph analytics service documented on the page.",
    ),
    Feature(
        name="`SERVICE bd:sample`",
        section="Page-Listed SERVICE Extensions",
        pattern=r"SERVICE\s+bd:sample\b",
        note="Sampling service documented on the page.",
    ),
    Feature(
        name="`hint:Query ...` query hints",
        section="Supporting Blazegraph-Specific Syntax",
        pattern=r"\bhint:Query\b",
        note="The page mentions query hints in the named sub-query discussion; they are not a top-level section, but they are Blazegraph-specific syntax used in the example set.",
    ),
    Feature(
        name="`bd:serviceParam`",
        section="Supporting Blazegraph-Specific Syntax",
        pattern=r"\bbd:serviceParam\b",
        note="This parameter mechanism is used to configure Blazegraph/WDQS SERVICE extensions throughout the examples.",
    ),
]


def parse_args() -> argparse.Namespace:
    parser = argparse.ArgumentParser(description="Report Blazegraph-specific feature usage in examples/")
    parser.add_argument("--examples-dir", default="examples", help="Directory containing .rq files")
    parser.add_argument(
        "--output-md",
        default="blazegraph_usage_report.md",
        help="Markdown report file to write",
    )
    parser.add_argument(
        "--output-html",
        default="blazegraph_usage_report.html",
        help="HTML report file to write",
    )
    return parser.parse_args()


def find_matches(examples_dir: Path, pattern: str) -> list[Path]:
    rx = re.compile(pattern)
    matches = []
    for path in sorted(examples_dir.rglob("*.rq")):
        if rx.search(path.read_text(encoding="utf-8")):
            matches.append(path)
    return matches


def build_report(examples_dir: Path) -> str:
    sections: list[str] = []
    total_queries = sum(1 for _ in examples_dir.rglob("*.rq"))
    section_order = []
    grouped: dict[str, list[tuple[Feature, list[Path]]]] = {}

    for feature in FEATURES:
        if feature.section not in grouped:
            grouped[feature.section] = []
            section_order.append(feature.section)
        grouped[feature.section].append((feature, find_matches(examples_dir, feature.pattern)))

    sections.append("# Blazegraph Feature Usage Report")
    sections.append("")
    sections.append(f"Source page reviewed: `{PAGE_URL}`")
    sections.append("")
    sections.append(f"Scanned local example queries: `{total_queries}` `.rq` files under `{examples_dir}`.")
    sections.append("")
    sections.append("This report uses the feature inventory described on the referenced Wikitech page, then maps each feature to matching files in the local `examples/` tree.")
    sections.append("")
    sections.append("## Summary Table")
    sections.append("")
    sections.append("| Section | Feature | Matches |")
    sections.append("| --- | --- | ---: |")
    for section_name in section_order:
        for feature, matches in grouped[section_name]:
            sections.append(f"| {section_name} | {feature.name} | {len(matches)} |")
    sections.append("")

    for section_name in section_order:
        feature_rows = grouped[section_name]
        section_total = sum(len(matches) for _, matches in feature_rows)
        sections.append(f"## {section_name}")
        sections.append("")
        sections.append(f"- Features in this section: {len(feature_rows)}")
        sections.append(f"- Total matches across this section: {section_total}")
        sections.append("")

        for feature, matches in feature_rows:
            sections.append(f"### {feature.name}")
            sections.append("")
            sections.append(f"- Local matches: {len(matches)}")
            sections.append(f"- Notes: {feature.note}")
            sections.append("")
            if matches:
                sections.append("Matching files:")
                sections.extend(f"- `{path.as_posix()}`" for path in matches)
            else:
                sections.append("Matching files:")
                sections.append("- None in the current `examples/` tree")
            sections.append("")

    return "\n".join(sections)


def build_report_html(examples_dir: Path) -> str:
    total_queries = sum(1 for _ in examples_dir.rglob("*.rq"))
    section_order = []
    grouped: dict[str, list[tuple[Feature, list[Path]]]] = {}

    for feature in FEATURES:
        if feature.section not in grouped:
            grouped[feature.section] = []
            section_order.append(feature.section)
        grouped[feature.section].append((feature, find_matches(examples_dir, feature.pattern)))

    parts: list[str] = []
    parts.append("<!DOCTYPE html>")
    parts.append('<html lang="en">')
    parts.append("<head>")
    parts.append('<meta charset="utf-8">')
    parts.append('<meta name="viewport" content="width=device-width, initial-scale=1">')
    parts.append("<title>Blazegraph Feature Usage Report</title>")
    parts.append("<style>")
    parts.append(
        """
        body { font-family: -apple-system, BlinkMacSystemFont, "Segoe UI", sans-serif; margin: 2rem auto; max-width: 1100px; padding: 0 1rem; line-height: 1.5; color: #1f2937; }
        h1, h2, h3 { color: #111827; }
        code { background: #f3f4f6; padding: 0.1rem 0.3rem; border-radius: 4px; }
        table { border-collapse: collapse; width: 100%; margin: 1rem 0 2rem; }
        th, td { border: 1px solid #d1d5db; padding: 0.5rem 0.65rem; text-align: left; vertical-align: top; }
        th { background: #f9fafb; }
        .meta { margin-bottom: 1.5rem; }
        .feature { margin-bottom: 2rem; }
        .count { font-weight: 600; }
        ul.files { margin-top: 0.5rem; }
        li.none { color: #6b7280; }
        a { color: #0f766e; text-decoration: none; }
        a:hover { text-decoration: underline; }
        """
    )
    parts.append("</style>")
    parts.append("</head>")
    parts.append("<body>")
    parts.append("<h1>Blazegraph Feature Usage Report</h1>")
    parts.append('<div class="meta">')
    parts.append(
        f"<p>Source page reviewed: <a href=\"{html.escape(PAGE_URL)}\">{html.escape(PAGE_URL)}</a></p>"
    )
    parts.append(
        f"<p>Scanned local example queries: <span class=\"count\">{total_queries}</span> <code>.rq</code> files under <code>{html.escape(examples_dir.as_posix())}</code>.</p>"
    )
    parts.append(
        "<p>This report uses the feature inventory described on the referenced Wikitech page, then maps each feature to matching files in the local <code>examples/</code> tree.</p>"
    )
    parts.append("</div>")

    parts.append("<h2>Summary Table</h2>")
    parts.append("<table>")
    parts.append("<thead><tr><th>Section</th><th>Feature</th><th>Matches</th></tr></thead>")
    parts.append("<tbody>")
    for section_name in section_order:
        for feature, matches in grouped[section_name]:
            parts.append(
                "<tr>"
                f"<td>{html.escape(section_name)}</td>"
                f"<td>{html.escape(feature.name)}</td>"
                f"<td>{len(matches)}</td>"
                "</tr>"
            )
    parts.append("</tbody>")
    parts.append("</table>")

    for section_name in section_order:
        feature_rows = grouped[section_name]
        section_total = sum(len(matches) for _, matches in feature_rows)
        parts.append(f"<h2>{html.escape(section_name)}</h2>")
        parts.append(f"<p>Features in this section: <span class=\"count\">{len(feature_rows)}</span><br>Total matches across this section: <span class=\"count\">{section_total}</span></p>")

        for feature, matches in feature_rows:
            parts.append('<div class="feature">')
            parts.append(f"<h3>{html.escape(feature.name)}</h3>")
            parts.append(f"<p>Local matches: <span class=\"count\">{len(matches)}</span><br>Notes: {html.escape(feature.note)}</p>")
            parts.append("<p>Matching files:</p>")
            if matches:
                parts.append('<ul class="files">')
                for path in matches:
                    rel = path.as_posix()
                    parts.append(
                        f'<li><a href="{html.escape(rel)}"><code>{html.escape(rel)}</code></a></li>'
                    )
                parts.append("</ul>")
            else:
                parts.append('<ul class="files"><li class="none">None in the current <code>examples/</code> tree</li></ul>')
            parts.append("</div>")

    parts.append("</body>")
    parts.append("</html>")
    return "\n".join(parts)


def main() -> int:
    args = parse_args()
    examples_dir = Path(args.examples_dir).resolve()
    output_md = Path(args.output_md).resolve()
    output_html = Path(args.output_html).resolve()

    if not examples_dir.exists():
        raise SystemExit(f"Examples directory not found: {examples_dir}")

    report_md = build_report(examples_dir)
    report_html = build_report_html(examples_dir)
    output_md.write_text(report_md + "\n", encoding="utf-8")
    output_html.write_text(report_html + "\n", encoding="utf-8")
    print(output_md)
    print(output_html)
    return 0


if __name__ == "__main__":
    raise SystemExit(main())
