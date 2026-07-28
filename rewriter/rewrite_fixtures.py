#!/usr/bin/env python3
"""CLI script to recursively rewrite SPARQL fixtures without overwriting goldens unless requested."""

from __future__ import annotations

import argparse
from pathlib import Path
import sys

PYTHON_ROOT = Path(__file__).resolve().parent / "python"
sys.path.insert(0, str(PYTHON_ROOT))

from sparql_rewriter import RewriterError, rewrite_queries  # noqa: E402


def discover_fixtures(root: Path) -> list[tuple[Path, Path]]:
    """Find original queries recursively and derive their sibling output paths."""

    fixtures: list[tuple[Path, Path]] = []
    for original in sorted(root.rglob("*.original.rq")):
        stem = original.name.removesuffix(".original.rq")
        rewritten = original.with_name(f"{stem}.rewritten.rq")
        fixtures.append((original, rewritten))
    if not fixtures:
        raise OSError(f"No *.original.rq fixtures found beneath: {root}")
    return fixtures


def destination_path(original: Path, fixture_root: Path, output_root: Path,
        update_golden: bool) -> Path:
    """Choose a separate generated path unless golden updates were requested."""

    stem = original.name.removesuffix(".original.rq")
    filename = f"{stem}.rewritten.rq"
    if update_golden:
        return original.with_name(filename)
    return output_root / original.relative_to(fixture_root).with_name(filename)


def main() -> int:
    """Run all fixtures and write generated output outside the catalog by default."""

    parser = argparse.ArgumentParser(
        description="Recursively rewrite every .original.rq fixture safely.")
    parser.add_argument(
        "fixtures",
        type=Path,
        nargs="?",
        default=Path(__file__).resolve().parent / "fixtures",
        help="Fixture root (default: fixtures)")
    parser.add_argument(
        "--rewriter-jar",
        type=Path,
        default=Path(__file__).resolve().parent
        / "java-rewriter"
        / "target"
        / "sparql-rewriter.jar")
    destinations = parser.add_mutually_exclusive_group()
    destinations.add_argument(
        "--output-dir",
        type=Path,
        default=Path(__file__).resolve().parent / "generated",
        help="Generated output root (default: generated)")
    destinations.add_argument(
        "--update-golden",
        action="store_true",
        help="Explicitly overwrite sibling .rewritten.rq golden files")
    args = parser.parse_args()

    try:
        fixtures = discover_fixtures(args.fixtures)
        queries = [
            (
                original.relative_to(args.fixtures).as_posix(),
                original.read_text(encoding="utf-8"),
            )
            for original, _ in fixtures]
        results = dict(rewrite_queries(queries, args.rewriter_jar))
    except (OSError, RewriterError) as error:
        print(str(error), file=sys.stderr)
        return 1

    rewritten_count = 0
    classified_count = 0
    for original, _ in fixtures:
        query_id = original.relative_to(args.fixtures).as_posix()
        result = results[query_id]
        rewritten = result["rewritten_query"]
        if rewritten is None:
            classified_count += 1
            print(f"{result['rewrite_status'].upper()} {query_id}")
            continue
        rewritten_path = destination_path(
            original,
            args.fixtures,
            args.output_dir,
            args.update_golden)
        rewritten_path.parent.mkdir(parents=True, exist_ok=True)
        rewritten_path.write_text(rewritten, encoding="utf-8")
        rewritten_count += 1
        print(f"WROTE {rewritten_path}")

    print(f"{rewritten_count} rewritten; {classified_count} classified without output")
    return 0


if __name__ == "__main__":
    raise SystemExit(main())
