#!/usr/bin/env python3

from __future__ import annotations

import argparse
import csv
import re
import shutil
import tempfile
import unicodedata
from collections import Counter
from dataclasses import dataclass
from pathlib import Path


SCRIPT_DIR = Path(__file__).resolve().parent
PROJECT_ROOT = SCRIPT_DIR.parent
DEFAULT_INPUT_CSV = PROJECT_ROOT / "wmcloud_data" / "large_sparql_queries_dataset.csv"
DEFAULT_OUTPUT_DIR = PROJECT_ROOT / "wmcloud_queries"
REQUIRED_COLUMNS = ["page_id", "page_title", "query"]
MAX_NAME_LENGTH = 120


@dataclass
class WriteStats:
    row_count: int
    query_count: int
    duplicate_file_names: int
    missing_title_count: int
    skipped_empty_queries: int


def parse_args() -> argparse.Namespace:
    parser = argparse.ArgumentParser(description="Extract WMCloud SPARQL query rows into .rq files.")
    parser.add_argument(
        "--input-csv",
        default=str(DEFAULT_INPUT_CSV),
        help="CSV file containing page_id, page_title, and query columns",
    )
    parser.add_argument(
        "--output-dir",
        default=str(DEFAULT_OUTPUT_DIR),
        help="Directory to replace with extracted .rq files",
    )
    return parser.parse_args()


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


def dedupe_path_name(name: str, seen: Counter[str], used_names: set[str]) -> tuple[str, bool]:
    key = name.casefold()
    seen[key] += 1
    candidate = name if seen[key] == 1 else f"{name}_{seen[key]}"
    duplicated = seen[key] != 1
    while candidate.casefold() in used_names:
        seen[key] += 1
        candidate = f"{name}_{seen[key]}"
        duplicated = True
    used_names.add(candidate.casefold())
    return candidate, duplicated


def validate_columns(fieldnames: list[str] | None) -> None:
    if fieldnames != REQUIRED_COLUMNS:
        found = ", ".join(fieldnames or [])
        expected = ", ".join(REQUIRED_COLUMNS)
        raise RuntimeError(f"Expected CSV columns {expected}; found {found}")


def title_for_file_name(page_title: str, page_id: str) -> tuple[str, bool]:
    if page_title.strip():
        return page_title, False
    if page_id.strip():
        return f"Page {page_id}", True
    return "Untitled", True


def write_queries_atomically(input_csv: Path, output_dir: Path) -> WriteStats:
    input_csv = input_csv.resolve()
    output_dir = output_dir.resolve()
    if output_dir == output_dir.parent:
        raise RuntimeError(f"Refusing to replace unsafe output directory: {output_dir}")
    if not input_csv.exists():
        raise RuntimeError(f"Input CSV not found: {input_csv}")
    output_dir.parent.mkdir(parents=True, exist_ok=True)

    file_name_seen: Counter[str] = Counter()
    used_file_names: set[str] = set()
    row_count = 0
    query_count = 0
    duplicate_file_names = 0
    missing_title_count = 0
    skipped_empty_queries = 0

    with input_csv.open(newline="", encoding="utf-8-sig") as handle:
        reader = csv.DictReader(handle)
        validate_columns(reader.fieldnames)
        staging_dir = Path(tempfile.mkdtemp(prefix=f".{output_dir.name}_tmp_", dir=output_dir.parent))
        for row in reader:
            row_count += 1
            query = normalize_query(row["query"])
            if not query:
                skipped_empty_queries += 1
                continue

            title, missing_title = title_for_file_name(row["page_title"], row["page_id"])
            if missing_title:
                missing_title_count += 1
            base_name = sanitize_name(title, "Untitled")
            file_name, duplicated = dedupe_path_name(base_name, file_name_seen, used_file_names)
            if duplicated:
                duplicate_file_names += 1
            (staging_dir / f"{file_name}.rq").write_text(query + "\n", encoding="utf-8")
            query_count += 1

    if output_dir.exists():
        shutil.rmtree(output_dir)
    staging_dir.replace(output_dir)
    return WriteStats(
        row_count=row_count,
        query_count=query_count,
        duplicate_file_names=duplicate_file_names,
        missing_title_count=missing_title_count,
        skipped_empty_queries=skipped_empty_queries,
    )


def main() -> int:
    args = parse_args()
    input_csv = Path(args.input_csv).resolve()
    output_dir = Path(args.output_dir).resolve()
    stats = write_queries_atomically(input_csv, output_dir)
    print(f"Wrote {stats.query_count} queries from {stats.row_count} CSV rows under {output_dir}")
    print(
        "Collision summary: "
        f"{stats.duplicate_file_names} duplicate file names, "
        f"{stats.missing_title_count} missing page titles, "
        f"{stats.skipped_empty_queries} skipped empty queries"
    )
    return 0


if __name__ == "__main__":
    raise SystemExit(main())
