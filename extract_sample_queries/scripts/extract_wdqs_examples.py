#!/usr/bin/env python3

from __future__ import annotations

import argparse
import json
import re
import shutil
import subprocess
import sys
import tempfile
import unicodedata
import urllib.parse
from collections import Counter, defaultdict
from dataclasses import dataclass
from html.parser import HTMLParser
from pathlib import Path


DEFAULT_PAGE_TITLE = "Wikidata:SPARQL_query_service/queries/examples"
DEFAULT_API_URL = "https://www.wikidata.org/w/api.php"
HEADING_TAGS = {"h2", "h3", "h4", "h5", "h6", "h7"}
MAX_NAME_LENGTH = 120


@dataclass
class Example:
    category: str
    title: str
    query: str


@dataclass
class WriteStats:
    category_count: int
    example_count: int
    duplicate_category_names: int
    duplicate_file_names: int
    uncategorized_examples: int


class ExamplesHTMLParser(HTMLParser):
    def __init__(self) -> None:
        super().__init__(convert_charrefs=True)
        self.examples: list[Example] = []
        self._heading_levels: dict[int, str] = {}
        self._heading_div_depth = 0
        self._active_heading_level: int | None = None
        self._active_heading_text: list[str] = []
        self._code_div_depth = 0
        self._code_text: list[str] = []
        self._skip_depth = 0

    def handle_starttag(self, tag: str, attrs: list[tuple[str, str | None]]) -> None:
        attr_map = {key: value or "" for key, value in attrs}
        classes = set(attr_map.get("class", "").split())
        if tag == "div":
            if self._code_div_depth:
                self._code_div_depth += 1
                return
            if "mw-highlight-lang-sparql" in classes:
                self._code_div_depth = 1
                self._code_text = []
                self._skip_depth = 0
                return
            if "mw-heading" in classes or any(cls.startswith("mw-heading") for cls in classes):
                self._heading_div_depth += 1
                return
        if self._code_div_depth:
            if self._skip_depth:
                self._skip_depth += 1
                return
            if "lineno" in classes:
                self._skip_depth = 1
            return
        if self._heading_div_depth and tag in HEADING_TAGS:
            self._active_heading_level = int(tag[1])
            self._active_heading_text = []

    def handle_endtag(self, tag: str) -> None:
        if self._code_div_depth:
            if self._skip_depth:
                self._skip_depth -= 1
                return
            if tag == "div":
                self._code_div_depth -= 1
                if self._code_div_depth == 0:
                    self._finalize_query()
            return
        if self._active_heading_level is not None and tag == f"h{self._active_heading_level}":
            heading = normalize_heading("".join(self._active_heading_text))
            if heading:
                self._heading_levels[self._active_heading_level] = heading
                for level in list(self._heading_levels):
                    if level > self._active_heading_level:
                        del self._heading_levels[level]
            self._active_heading_level = None
            self._active_heading_text = []
            return
        if tag == "div" and self._heading_div_depth:
            self._heading_div_depth -= 1

    def handle_data(self, data: str) -> None:
        if self._code_div_depth:
            if not self._skip_depth:
                self._code_text.append(data)
            return
        if self._active_heading_level is not None:
            self._active_heading_text.append(data)

    def _finalize_query(self) -> None:
        query = normalize_query("".join(self._code_text))
        if not query or not self._heading_levels:
            return
        title_level = max(self._heading_levels)
        title = self._heading_levels[title_level]
        # The WDQS UI groups each query under the heading exactly one level above the query title.
        category = self._heading_levels.get(title_level - 1, "")
        self.examples.append(Example(category=category, title=title, query=query))


def normalize_heading(value: str) -> str:
    return re.sub(r"\s+", " ", value.replace("\xa0", " ")).strip()


def normalize_query(value: str) -> str:
    value = value.replace("\r\n", "\n").replace("\r", "\n").replace("\xa0", " ")
    lines = value.split("\n")
    while lines and not lines[0].strip():
        lines.pop(0)
    while lines and not lines[-1].strip():
        lines.pop()
    return "\n".join(lines)


def sanitize_name(value: str, fallback: str) -> str:
    transliterated = unicodedata.normalize("NFKD", value).encode("ascii", "ignore").decode("ascii")
    candidate = transliterated or value
    candidate = re.sub(r"[^\w\s]", "", candidate, flags=re.UNICODE)
    candidate = re.sub(r"\s+", "_", candidate.strip(), flags=re.UNICODE)
    candidate = re.sub(r"_+", "_", candidate)
    candidate = candidate.strip("_")
    if len(candidate) > MAX_NAME_LENGTH:
        candidate = candidate[:MAX_NAME_LENGTH].rstrip("_")
    return candidate or fallback


def dedupe_path_name(name: str, seen: Counter[str]) -> tuple[str, bool]:
    seen[name] += 1
    if seen[name] == 1:
        return name, False
    return f"{name}_{seen[name]}", True


def build_parse_url(page_title: str, api_url: str = DEFAULT_API_URL) -> str:
    params = {
        "action": "parse",
        "page": page_title,
        "redirects": "1",
        "prop": "text",
        "wrapoutputclass": "",
        "disableeditsection": "1",
        "smaxage": "600",
        "maxage": "600",
        "disabletoc": "1",
        "format": "json",
        "formatversion": "2",
        "origin": "*",
    }
    return f"{api_url}?{urllib.parse.urlencode(params)}"


def fetch_examples_html(page_title: str, api_url: str = DEFAULT_API_URL) -> str:
    url = build_parse_url(page_title, api_url)
    try:
        result = subprocess.run(
            [
                "curl",
                "-L",
                "--fail",
                "--silent",
                "--show-error",
                "--user-agent",
                "wdqs-example-extractor/1.0",
                "--header",
                "Accept: application/json",
                url,
            ],
            check=True,
            capture_output=True,
            text=True,
        )
    except subprocess.CalledProcessError as exc:
        stderr = exc.stderr.strip() if exc.stderr else "no curl stderr"
        raise RuntimeError(f"Failed to fetch live examples page via curl: {stderr}") from exc
    try:
        payload = json.loads(result.stdout)
    except json.JSONDecodeError as exc:
        preview = result.stdout[:400].strip()
        raise RuntimeError(f"Received invalid JSON from the live examples API: {preview}") from exc
    try:
        html = payload["parse"]["text"]
    except (KeyError, TypeError) as exc:
        raise RuntimeError("The live examples API response did not include parse.text.") from exc
    if not isinstance(html, str) or not html.strip():
        raise RuntimeError("The live examples API returned an empty parse.text payload.")
    return html


def load_examples(page_title: str, api_url: str = DEFAULT_API_URL) -> list[Example]:
    html = fetch_examples_html(page_title, api_url)
    parser = ExamplesHTMLParser()
    parser.feed(html)
    parser.close()
    if not parser.examples:
        raise RuntimeError("No example queries were extracted from the live examples page.")
    return parser.examples


def write_examples_atomically(output_dir: Path, examples: list[Example]) -> WriteStats:
    output_dir = output_dir.resolve()
    if output_dir == output_dir.parent:
        raise RuntimeError(f"Refusing to replace unsafe output directory: {output_dir}")
    output_dir.parent.mkdir(parents=True, exist_ok=True)
    staging_dir = Path(tempfile.mkdtemp(prefix=f".{output_dir.name}_tmp_", dir=output_dir.parent))
    category_name_seen: Counter[str] = Counter()
    file_name_seen_by_category: defaultdict[str, Counter[str]] = defaultdict(Counter)
    category_dir_by_title: dict[str, Path] = {}
    duplicate_category_names = 0
    duplicate_file_names = 0
    uncategorized_examples = 0
    for example in examples:
        category_title = example.category or "Uncategorized"
        if not example.category:
            uncategorized_examples += 1
        if category_title not in category_dir_by_title:
            category_name, duplicated = dedupe_path_name(
                sanitize_name(category_title, "Uncategorized"),
                category_name_seen,
            )
            if duplicated:
                duplicate_category_names += 1
            category_dir = staging_dir / category_name
            category_dir.mkdir(parents=True, exist_ok=True)
            category_dir_by_title[category_title] = category_dir
        category_dir = category_dir_by_title[category_title]
        file_name, duplicated = dedupe_path_name(
            sanitize_name(example.title, "Untitled"),
            file_name_seen_by_category[category_title],
        )
        if duplicated:
            duplicate_file_names += 1
        (category_dir / f"{file_name}.rq").write_text(example.query + "\n", encoding="utf-8")
    if output_dir.exists():
        shutil.rmtree(output_dir)
    staging_dir.replace(output_dir)
    return WriteStats(
        category_count=len(category_dir_by_title),
        example_count=len(examples),
        duplicate_category_names=duplicate_category_names,
        duplicate_file_names=duplicate_file_names,
        uncategorized_examples=uncategorized_examples,
    )


def parse_args() -> argparse.Namespace:
    parser = argparse.ArgumentParser(description="Extract live WDQS example queries.")
    parser.add_argument(
        "--output-dir",
        default="examples",
        help="Directory to populate with category folders and .rq files",
    )
    parser.add_argument(
        "--page-title",
        default=DEFAULT_PAGE_TITLE,
        help="MediaWiki page title containing the English WDQS examples.",
    )
    parser.add_argument(
        "--api-url",
        default=DEFAULT_API_URL,
        help="MediaWiki API endpoint for the wiki containing the examples page.",
    )
    return parser.parse_args()


def main() -> int:
    args = parse_args()
    output_dir = Path(args.output_dir).resolve()
    examples = load_examples(args.page_title, args.api_url)
    stats = write_examples_atomically(output_dir, examples)
    print(
        f"Wrote {stats.example_count} queries into {stats.category_count} "
        f"category directories under {output_dir}"
    )
    print(
        "Collision summary: "
        f"{stats.duplicate_category_names} duplicate category names, "
        f"{stats.duplicate_file_names} duplicate file names, "
        f"{stats.uncategorized_examples} uncategorized queries"
    )
    return 0


if __name__ == "__main__":
    raise SystemExit(main())
