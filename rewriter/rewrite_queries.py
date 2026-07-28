#!/usr/bin/env python3
"""CLI script to recursively rewrite multiple SPARQL query files using one Java process."""

from __future__ import annotations

import argparse
from pathlib import Path
import sys

PYTHON_ROOT = Path(__file__).resolve().parent / "python"
sys.path.insert(0, str(PYTHON_ROOT))

from sparql_rewriter import RewriterError, rewrite_queries  # noqa: E402


def discover_queries(input_root: Path, output_root: Path) -> list[Path]:
    """Find input .rq files recursively, excluding generated output files."""

    resolved_output = output_root.resolve()
    queries = [
        path
        for path in sorted(input_root.rglob("*.rq"))
        if path.is_file()
        and not path.name.endswith(".rewritten.rq")
        and not path.resolve().is_relative_to(resolved_output)]
    if not queries:
        raise OSError(f"No input .rq files found beneath: {input_root}")
    return queries


def destination_path(original: Path, input_root: Path, output_root: Path) -> Path:
    """Return the mirrored output path for an input query."""

    relative = original.relative_to(input_root)
    return output_root / relative.with_name(f"{relative.stem}.rewritten.rq")


def main() -> int:
    """Rewrite every eligible query under a directory into an output tree."""

    parser = argparse.ArgumentParser(
        description="Recursively rewrite ordinary SPARQL .rq files.")
    parser.add_argument("queries", type=Path, help="Directory containing input .rq files")
    parser.add_argument(
        "--output-dir",
        type=Path,
        required=True,
        help="Directory for rewritten .rq files")
    parser.add_argument(
        "--rewriter-jar",
        type=Path,
        default=Path(__file__).resolve().parent
        / "java-rewriter"
        / "target"
        / "sparql-rewriter.jar")
    args = parser.parse_args()

    try:
        paths = discover_queries(args.queries, args.output_dir)
        queries = [
            (
                path.relative_to(args.queries).as_posix(),
                path.read_text(encoding="utf-8"),
            )
            for path in paths]
        results = dict(rewrite_queries(queries, args.rewriter_jar))
    except (OSError, RewriterError) as error:
        print(str(error), file=sys.stderr)
        return 1

    written_count = 0
    classified_count = 0
    for original in paths:
        query_id = original.relative_to(args.queries).as_posix()
        result = results[query_id]
        rewritten = result["rewritten_query"]
        if rewritten is None:
            classified_count += 1
            print(f"{result['rewrite_status'].upper()} {query_id}")
            continue
        output = destination_path(original, args.queries, args.output_dir)
        output.parent.mkdir(parents=True, exist_ok=True)
        output.write_text(rewritten, encoding="utf-8")
        written_count += 1
        print(f"WROTE {output}")

    print(f"{written_count} written; {classified_count} classified without output")
    return 0


if __name__ == "__main__":
    raise SystemExit(main())
