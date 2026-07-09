#!/usr/bin/env python3

from __future__ import annotations

import argparse
import html
import re
from dataclasses import dataclass
from pathlib import Path


PAGE_URL = "https://wikitech.wikimedia.org/wiki/User:AWesterinen/Blazegraph_Features_and_Capabilities"
SCRIPT_DIR = Path(__file__).resolve().parent
PROJECT_ROOT = SCRIPT_DIR.parent

DEFAULT_EXAMPLE_DIRS = [
    "examples",
    "advanced_examples",
    "human_examples",
    "maintenance_examples",
    "commons_examples",
]
DEFAULT_SUMMARY_THRESHOLD = 100
DEFAULT_REPORT_DIR = PROJECT_ROOT / "blazegraph_usage_reports"

WIKIMEDIA_EXAMPLE_DIRS = [
    "examples",
    "advanced_examples",
    "human_examples",
    "maintenance_examples",
    "commons_examples",
]
WMCLOUD_EXAMPLE_DIRS = ["wmcloud_queries"]


@dataclass(frozen=True)
class Feature:
    name: str
    section: str
    pattern: str
    summarize: bool = False


@dataclass
class ReportData:
    title: str
    examples_dirs: list[Path]
    total_queries: int
    section_order: list[str]
    grouped: dict[str, list[tuple[Feature, list[Path]]]]
    summary_threshold: int


FEATURES = [
    Feature(
        name="Named sub-queries (`WITH { ... } AS %name`, `INCLUDE %name`)",
        section="Blazegraph Features",
        pattern=r"\bWITH\s*\{|\bINCLUDE\s*%",
    ),
    Feature(
        name="`geof:globe()`",
        section="Function Extensions",
        pattern=r"\bgeof:globe\s*\(",
    ),
    Feature(
        name="`geof:latitude()`",
        section="Function Extensions",
        pattern=r"\bgeof:latitude\s*\(",
    ),
    Feature(
        name="`geof:longitude()`",
        section="Function Extensions",
        pattern=r"\bgeof:longitude\s*\(",
    ),
    Feature(
        name="`geof:distance()`",
        section="Function Extensions",
        pattern=r"\bgeof:distance\s*\(",
    ),
    Feature(
        name="`wikibase:decodeUri()`",
        section="Function Extensions",
        pattern=r"\bwikibase:decodeUri\s*\(",
    ),
    Feature(
        name="`SERVICE wikibase:around`",
        section="SERVICE Extensions",
        pattern=r"SERVICE\s+wikibase:around\b",
    ),
    Feature(
        name="`SERVICE wikibase:box`",
        section="SERVICE Extensions",
        pattern=r"SERVICE\s+wikibase:box\b",
    ),
    Feature(
        name="`SERVICE wikibase:label`",
        section="SERVICE Extensions",
        pattern=r"SERVICE\s+wikibase:label\b",
        summarize=True,
    ),
    Feature(
        name="`SERVICE bd:slice`",
        section="SERVICE Extensions",
        pattern=r"SERVICE\s+bd:slice\b",
    ),
    Feature(
        name="`SERVICE wikibase:mwapi`",
        section="SERVICE Extensions",
        pattern=r"SERVICE\s+wikibase:mwapi\b",
    ),
    Feature(
        name="`SERVICE gas:service`",
        section="SERVICE Extensions",
        pattern=r"SERVICE\s+gas:service\b",
    ),
    Feature(
        name="`SERVICE bd:sample`",
        section="SERVICE Extensions",
        pattern=r"SERVICE\s+bd:sample\b",
    ),
    Feature(
        name="`hint:Query ...` query hints",
        section="Supporting Blazegraph-Specific Syntax",
        pattern=r"\bhint:Query\b",
    ),
    Feature(
        name="`bd:serviceParam`",
        section="Supporting Blazegraph-Specific Syntax",
        pattern=r"\bbd:serviceParam\b",
        summarize=True,
    ),
    Feature(
        name="`wikibase:someValue`",
        section="Supporting Blazegraph-Specific Syntax",
        pattern=r"\bwikibase:someValue\b",
    ),
    Feature(
        name="`wikibase:geoGlobe`",
        section="Supporting Blazegraph-Specific Syntax",
        pattern=r"\bwikibase:geoGlobe\b",
    ),
    Feature(
        name="`wikibase:globe`",
        section="Supporting Blazegraph-Specific Syntax",
        pattern=r"\bwikibase:globe\b",
    ),
]


def parse_args() -> argparse.Namespace:
    parser = argparse.ArgumentParser(description="Report Blazegraph-specific feature usage across the example query sets")
    parser.add_argument(
        "--generate",
        choices=["standard"],
        help="Generate the standard report set: wikimedia, wmcloud, other, and all",
    )
    parser.add_argument(
        "--report-dir",
        help="Directory for generated standard reports (defaults to blazegraph_usage_reports beside scripts/)",
    )
    parser.add_argument(
        "--examples-dir",
        dest="examples_dirs",
        nargs="+",
        help="One or more directories containing .rq files (defaults to all example sets beside scripts/)",
    )
    parser.add_argument(
        "--output-md",
        help="Markdown report file to write (defaults to the project root beside scripts/)",
    )
    parser.add_argument(
        "--output-html",
        help="HTML report file to write (defaults to the project root beside scripts/)",
    )
    parser.add_argument(
        "--summary-threshold",
        type=int,
        default=DEFAULT_SUMMARY_THRESHOLD,
        help="Summarize matching files instead of listing them when match count is at or above this value",
    )
    return parser.parse_args()


def project_dirs(names: list[str]) -> list[Path]:
    return [PROJECT_ROOT / name for name in names]


def default_example_dirs() -> list[Path]:
    return project_dirs(DEFAULT_EXAMPLE_DIRS)


def resolve_example_dirs(examples_dirs: list[str] | None) -> list[Path]:
    if examples_dirs is None:
        return default_example_dirs()
    return [Path(d).resolve() for d in examples_dirs]


def resolve_output_path(output_path: str | None, default_name: str) -> Path:
    if output_path is None:
        return DEFAULT_REPORT_DIR / default_name
    return Path(output_path).resolve()


def resolve_report_dir(report_dir: str | None) -> Path:
    if report_dir is None:
        return DEFAULT_REPORT_DIR
    return Path(report_dir).resolve()


def other_example_dirs() -> list[Path]:
    other_root = PROJECT_ROOT / "other_examples"
    if not other_root.exists():
        raise RuntimeError(f"Other examples directory not found: {other_root}")
    return sorted(path for path in other_root.iterdir() if path.is_dir())


def standard_report_specs() -> list[tuple[str, str, list[Path]]]:
    wikimedia_dirs = project_dirs(WIKIMEDIA_EXAMPLE_DIRS)
    wmcloud_dirs = project_dirs(WMCLOUD_EXAMPLE_DIRS)
    other_dirs = other_example_dirs()
    return [
        ("wikimedia", "Wikimedia Queries", wikimedia_dirs),
        ("wmcloud", "WMCloud Queries", wmcloud_dirs),
        ("other", "Other Queries", other_dirs),
        ("all", "All Queries", wikimedia_dirs + wmcloud_dirs + other_dirs),
    ]


def all_rq_files(examples_dirs: list[Path]) -> list[Path]:
    files: list[Path] = []
    for examples_dir in examples_dirs:
        files.extend(sorted(examples_dir.rglob("*.rq")))
    return files


def format_dirs(examples_dirs: list[Path]) -> str:
    return ", ".join(f"`{d.as_posix()}`" for d in examples_dirs)


def summary_text(count: int) -> str:
    return f"{count} matching files (not listed individually)"


def should_summarize(feature: Feature, match_count: int, summary_threshold: int) -> bool:
    return feature.summarize or match_count >= summary_threshold


def render_inline_markdown_html(value: str) -> str:
    parts: list[str] = []
    pos = 0
    for match in re.finditer(r"`([^`]*)`", value):
        parts.append(html.escape(value[pos:match.start()]))
        parts.append(f"<code>{html.escape(match.group(1))}</code>")
        pos = match.end()
    parts.append(html.escape(value[pos:]))
    return "".join(parts)


def collect_report_data(
    examples_dirs: list[Path],
    title: str = "Blazegraph Feature Usage Report",
    summary_threshold: int = DEFAULT_SUMMARY_THRESHOLD,
) -> ReportData:
    files = all_rq_files(examples_dirs)
    section_order = []
    grouped: dict[str, list[tuple[Feature, list[Path]]]] = {}
    matches_by_feature: dict[Feature, list[Path]] = {feature: [] for feature in FEATURES}
    compiled_features = [
        (feature, re.compile(feature.pattern, flags=re.IGNORECASE))
        for feature in FEATURES
    ]

    for path in files:
        text = path.read_text(encoding="utf-8")
        for feature, rx in compiled_features:
            if rx.search(text):
                matches_by_feature[feature].append(path)

    for feature in FEATURES:
        if feature.section not in grouped:
            grouped[feature.section] = []
            section_order.append(feature.section)
        grouped[feature.section].append((feature, matches_by_feature[feature]))

    return ReportData(
        title=title,
        examples_dirs=examples_dirs,
        total_queries=len(files),
        section_order=section_order,
        grouped=grouped,
        summary_threshold=summary_threshold,
    )


def merge_report_data(title: str, report_parts: list[ReportData], summary_threshold: int) -> ReportData:
    if not report_parts:
        return ReportData(
            title=title,
            examples_dirs=[],
            total_queries=0,
            section_order=[],
            grouped={},
            summary_threshold=summary_threshold,
        )

    examples_dirs: list[Path] = []
    total_queries = 0
    section_order: list[str] = []
    grouped: dict[str, list[tuple[Feature, list[Path]]]] = {}
    matches_by_feature: dict[Feature, list[Path]] = {feature: [] for feature in FEATURES}

    for report_part in report_parts:
        examples_dirs.extend(report_part.examples_dirs)
        total_queries += report_part.total_queries
        for section_name in report_part.section_order:
            if section_name not in grouped:
                grouped[section_name] = []
                section_order.append(section_name)
        for feature_rows in report_part.grouped.values():
            for feature, matches in feature_rows:
                matches_by_feature[feature].extend(matches)

    for feature in FEATURES:
        if feature.section not in grouped:
            grouped[feature.section] = []
            section_order.append(feature.section)
        grouped[feature.section].append((feature, matches_by_feature[feature]))

    return ReportData(
        title=title,
        examples_dirs=examples_dirs,
        total_queries=total_queries,
        section_order=section_order,
        grouped=grouped,
        summary_threshold=summary_threshold,
    )


def build_report(report_data: ReportData) -> str:
    sections: list[str] = []

    sections.append(f"# {report_data.title}")
    sections.append("")
    sections.append(f"Source page reviewed: `{PAGE_URL}`")
    sections.append("")
    sections.append(f"Scanned local example queries: `{report_data.total_queries}` `.rq` files under {format_dirs(report_data.examples_dirs)}.")
    sections.append("")
    sections.append("This report uses the feature inventory described on the referenced Wikitech page, then maps each feature to matching files in the local example trees.")
    sections.append("")
    sections.append("## Summary Table")
    sections.append("")
    sections.append("| Section | Feature | Matches |")
    sections.append("| --- | --- | ---: |")
    for section_name in report_data.section_order:
        for feature, matches in report_data.grouped[section_name]:
            sections.append(f"| {section_name} | {feature.name} | {len(matches)} |")
    sections.append("")

    for section_name in report_data.section_order:
        feature_rows = report_data.grouped[section_name]
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
            sections.append("")
            if matches and should_summarize(feature, len(matches), report_data.summary_threshold):
                sections.append("Matching files:")
                sections.append(f"- {summary_text(len(matches))}")
            elif matches:
                sections.append("Matching files:")
                sections.extend(f"- `{path.as_posix()}`" for path in matches)
            else:
                sections.append("Matching files:")
                sections.append("- None in the current example trees")
            sections.append("")

    return "\n".join(sections)


def build_report_html(report_data: ReportData) -> str:
    parts: list[str] = []
    parts.append("<!DOCTYPE html>")
    parts.append('<html lang="en">')
    parts.append("<head>")
    parts.append('<meta charset="utf-8">')
    parts.append('<meta name="viewport" content="width=device-width, initial-scale=1">')
    parts.append(f"<title>{html.escape(report_data.title)}</title>")
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
    parts.append(f"<h1>{html.escape(report_data.title)}</h1>")
    parts.append('<div class="meta">')
    parts.append(
        f"<p>Source page reviewed: <a href=\"{html.escape(PAGE_URL)}\">{html.escape(PAGE_URL)}</a></p>"
    )
    dirs_html = ", ".join(f"<code>{html.escape(d.as_posix())}</code>" for d in report_data.examples_dirs)
    parts.append(
        f"<p>Scanned local example queries: <span class=\"count\">{report_data.total_queries}</span> <code>.rq</code> files under {dirs_html}.</p>"
    )
    parts.append(
        "<p>This report uses the feature inventory described on the referenced Wikitech page, then maps each feature to matching files in the local example trees.</p>"
    )
    parts.append("</div>")

    parts.append("<h2>Summary Table</h2>")
    parts.append("<table>")
    parts.append("<thead><tr><th>Section</th><th>Feature</th><th>Matches</th></tr></thead>")
    parts.append("<tbody>")
    for section_name in report_data.section_order:
        for feature, matches in report_data.grouped[section_name]:
            parts.append(
                "<tr>"
                f"<td>{html.escape(section_name)}</td>"
                f"<td>{render_inline_markdown_html(feature.name)}</td>"
                f"<td>{len(matches)}</td>"
                "</tr>"
            )
    parts.append("</tbody>")
    parts.append("</table>")

    for section_name in report_data.section_order:
        feature_rows = report_data.grouped[section_name]
        section_total = sum(len(matches) for _, matches in feature_rows)
        parts.append(f"<h2>{html.escape(section_name)}</h2>")
        parts.append(f"<p>Features in this section: <span class=\"count\">{len(feature_rows)}</span><br>Total matches across this section: <span class=\"count\">{section_total}</span></p>")

        for feature, matches in feature_rows:
            parts.append('<div class="feature">')
            parts.append(f"<h3>{render_inline_markdown_html(feature.name)}</h3>")
            parts.append(f"<p>Local matches: <span class=\"count\">{len(matches)}</span></p>")
            parts.append("<p>Matching files:</p>")
            if matches and should_summarize(feature, len(matches), report_data.summary_threshold):
                parts.append(f'<ul class="files"><li>{html.escape(summary_text(len(matches)))}</li></ul>')
            elif matches:
                parts.append('<ul class="files">')
                for path in matches:
                    rel = path.as_posix()
                    parts.append(
                        f'<li><a href="{html.escape(rel)}"><code>{html.escape(rel)}</code></a></li>'
                    )
                parts.append("</ul>")
            else:
                parts.append('<ul class="files"><li class="none">None in the current example trees</li></ul>')
            parts.append("</div>")

    parts.append("</body>")
    parts.append("</html>")
    return "\n".join(parts)


def validate_examples_dirs(examples_dirs: list[Path]) -> None:
    missing = [d for d in examples_dirs if not d.exists()]
    if missing:
        formatted = ", ".join(str(d) for d in missing)
        raise RuntimeError(f"Examples directory not found: {formatted}")


def write_report_pair(report_data: ReportData, output_md: Path, output_html: Path) -> None:
    output_md.parent.mkdir(parents=True, exist_ok=True)
    output_html.parent.mkdir(parents=True, exist_ok=True)
    output_md.write_text(build_report(report_data) + "\n", encoding="utf-8")
    output_html.write_text(build_report_html(report_data) + "\n", encoding="utf-8")


def generate_standard_reports(report_dir: Path, summary_threshold: int) -> list[Path]:
    written: list[Path] = []
    report_parts: list[ReportData] = []
    for slug, title_suffix, examples_dirs in standard_report_specs()[:3]:
        validate_examples_dirs(examples_dirs)
        report_data = collect_report_data(
            examples_dirs,
            title=f"Blazegraph Feature Usage Report: {title_suffix}",
            summary_threshold=summary_threshold,
        )
        report_parts.append(report_data)
        output_md = report_dir / f"{slug}_blazegraph_usage_report.md"
        output_html = report_dir / f"{slug}_blazegraph_usage_report.html"
        write_report_pair(report_data, output_md, output_html)
        written.extend([output_md, output_html])

    all_report_data = merge_report_data(
        "Blazegraph Feature Usage Report: All Queries",
        report_parts,
        summary_threshold,
    )
    output_md = report_dir / "all_blazegraph_usage_report.md"
    output_html = report_dir / "all_blazegraph_usage_report.html"
    write_report_pair(all_report_data, output_md, output_html)
    written.extend([output_md, output_html])
    return written


def main() -> int:
    args = parse_args()
    if args.summary_threshold < 1:
        raise SystemExit("--summary-threshold must be at least 1")

    if args.generate:
        if args.examples_dirs or args.output_md or args.output_html:
            raise SystemExit("--generate cannot be combined with --examples-dir, --output-md, or --output-html")
        try:
            written = generate_standard_reports(resolve_report_dir(args.report_dir), args.summary_threshold)
        except RuntimeError as exc:
            raise SystemExit(str(exc)) from exc
        for path in written:
            print(path)
        return 0

    examples_dirs = resolve_example_dirs(args.examples_dirs)
    output_md = resolve_output_path(args.output_md, "custom_blazegraph_usage_report.md")
    output_html = resolve_output_path(args.output_html, "custom_blazegraph_usage_report.html")

    try:
        validate_examples_dirs(examples_dirs)
    except RuntimeError as exc:
        raise SystemExit(str(exc)) from exc

    report_data = collect_report_data(examples_dirs, summary_threshold=args.summary_threshold)
    write_report_pair(report_data, output_md, output_html)
    print(output_md)
    print(output_html)
    return 0


if __name__ == "__main__":
    raise SystemExit(main())
