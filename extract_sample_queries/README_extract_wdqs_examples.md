# Wikidata Query Examples Extractor

This project extracts the example queries used by the Wikidata Query Service UI and writes them to local files for review and update.

Only the query itself is written, no surrounding, explanatory text.

## What It Produces

Running the extractor creates an `examples/` directory (by default) containing:

- One subdirectory per example category
- One `.rq` file per example query
- One combined text export at `examples/all_examples.txt`

Names are sanitized for filesystem use:

- Punctuation is removed
- Spaces become underscores
- Names are transliterated to ASCII when possible
- Duplicate names get numeric suffixes

## Source

The extractor reads the rendered examples page used by the query service:

- Query page: `https://query.wikidata.org/`
- Examples source page: English `Wikidata:SPARQL_query_service/queries/examples`
- MediaWiki parse API: `https://www.wikidata.org/w/api.php`

The script shells out to `curl` once per run to fetch the live parsed HTML, then does the rest in Python.

## Script

Main script:

- `scripts/extract_wdqs_examples.py`

## Usage

From the project root:

```bash
python3 scripts/extract_wdqs_examples.py --output-dir examples
```

Optional arguments:

```bash
python3 scripts/extract_wdqs_examples.py --output-dir examples --page-title 'Wikidata:SPARQL_query_service/queries/examples'
```

### Optional Arguments

`--output-dir`

- Default: `examples`
- Sets the destination directory for the generated category folders, `.rq` files, and `all_examples.txt`
- This exists so you can write to a different target when comparing runs, testing changes, or keeping multiple snapshots side by side

Example:

```bash
python3 scripts/extract_wdqs_examples.py --output-dir examples_snapshot
```

`--page-title`

- Default: `Wikidata:SPARQL_query_service/queries/examples`
- Overrides the English MediaWiki page title that the extractor reads from
- This exists so you can point the same extraction logic at a different examples page, a sandbox page, or a future replacement page without changing the script

Example:

```bash
python3 scripts/extract_wdqs_examples.py --output-dir examples_test --page-title 'Wikidata:SPARQL_query_service/queries/examples/sandbox'
```

## Behavior

- Fetches the live examples page
- Parses rendered headings and SPARQL blocks
- Uses the heading one level above a query title as that query's category
- Writes output to a staging directory first
- Replaces the target output directory atomically
- Prints a collision summary after completion

## Current Output

At the time of the last run in this workspace, the extractor generated:

- `55` category directories
- `395` `.rq` files
- `examples/all_examples.txt`

## Topic-Specific Example Subpages

In addition to the default `examples/` set, three additional WDQS example pages were extracted into their own sibling directories. Each was produced with the same script, overriding `--page-title` to point at the page and `--output-dir` to write to a new directory:

- `advanced/` — 84 queries in 19 categories
  ```bash
  python3 scripts/extract_wdqs_examples.py \
    --page-title 'Wikidata:SPARQL_query_service/queries/examples/advanced' \
    --output-dir advanced_examples
  ```
- `human/` — 14 queries in 3 categories
  ```bash
  python3 scripts/extract_wdqs_examples.py \
    --page-title 'Wikidata:SPARQL_query_service/queries/examples/human' \
    --output-dir human_examples
  ```
- `maintenance/` — 64 queries in 3 categories
  ```bash
  python3 scripts/extract_wdqs_examples.py \
    --page-title 'Wikidata:SPARQL_query_service/queries/examples/maintenance' \
    --output-dir maintenance_examples
  ```

Each directory uses the same layout as `examples/`: one folder per category, one `.rq` file per query, and a combined `all_examples.txt`.

## Notes

- Re-running the script replaces the existing output directory, so removed upstream queries do not leave stale files behind.
- The script depends on `python3` and `curl`.

## Troubleshooting

`curl: command not found`

- The script shells out to `curl` once per run to fetch the live parsed examples page
- Install `curl` or make sure it is available in your shell `PATH`

`Failed to fetch live examples page via curl`

- This usually means the remote request failed, the network is unavailable, or the upstream endpoint rejected the request
- Retry the command first
- If the failure persists, check whether `https://www.wikidata.org/w/api.php` is reachable from your environment

`Received invalid JSON from the live examples API`

- The script expected a JSON response from the MediaWiki parse API and got something else
- This can happen if the upstream service returns an error page, rate-limit page, or other unexpected response
- Re-run the script and inspect the API response manually if it keeps happening

`The live examples API response did not include parse.text`

- The upstream API response format changed or the requested page was not parsed successfully
- Check the `--page-title` value first
- If those are correct, the upstream API contract may have changed

The script runs successfully but output looks wrong or incomplete

- The extractor depends on the current rendered heading and code-block structure of the live examples page
- If Wikidata changes that structure, the parser may still run but group categories incorrectly or miss queries
- Compare the generated files with the live rendered page and update the parser if the HTML structure changed

## Validation

After a run, you can do a quick sanity check from the project root.

Count category directories:

```bash
python3 -c 'from pathlib import Path; base=Path("examples"); print(sum(1 for p in base.iterdir() if p.is_dir()))'
```

Count generated query files:

```bash
python3 -c 'from pathlib import Path; print(sum(1 for p in Path("examples").rglob("*.rq")))'
```

Check that the combined export exists:

```bash
test -f examples/all_examples.txt && echo ok
```

Inspect a known sample query:

```bash
python3 -c 'from pathlib import Path; print(Path("examples/Simple_queries/Cats.rq").read_text()[:300])'
```

List a few generated files:

```bash
find examples -maxdepth 2 -type f | sort | sed -n '1,20p'
```
