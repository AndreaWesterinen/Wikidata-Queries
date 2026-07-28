#!/usr/bin/env python3
"""CLI script to rewrite one SPARQL query file."""

from __future__ import annotations

import argparse
from pathlib import Path
import sys

PYTHON_ROOT = Path(__file__).resolve().parent / "python"
sys.path.insert(0, str(PYTHON_ROOT))

from sparql_rewriter import RewriterError, rewrite_query  # noqa: E402


def main() -> int:
    """Parse CLI arguments, rewrite the input, and print the result."""

    parser = argparse.ArgumentParser(
        description="Rewrite one Blazegraph SPARQL .rq file and print SPARQL 1.1.")
    parser.add_argument("query", type=Path, help="Path to the original .rq query")
    parser.add_argument(
        "--rewriter-jar",
        type=Path,
        default=Path(__file__).resolve().parent
        / "java-rewriter"
        / "target"
        / "sparql-rewriter.jar")
    args = parser.parse_args()

    try:
        original = args.query.read_text(encoding="utf-8")
        result = rewrite_query(original, args.query.stem, args.rewriter_jar)
    except (OSError, RewriterError) as error:
        print(str(error), file=sys.stderr)
        return 1

    rewritten = result["rewritten_query"]
    if rewritten is None:
        for error in result["errors"]:
            print(f"{result['rewrite_status']}: {error['code']}: {error['message']}", file=sys.stderr)
        return 2
    sys.stdout.write(rewritten)
    return 0


if __name__ == "__main__":
    raise SystemExit(main())
