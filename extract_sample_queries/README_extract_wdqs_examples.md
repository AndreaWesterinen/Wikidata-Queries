# Wikimedia Query Examples Extractor

This project extracts example queries used by Wikimedia SPARQL query service pages and writes them to local files for review and update. Examples are extracted from the following pages:

* https://www.wikidata.org/wiki/Wikidata:SPARQL_query_service/queries/examples
* https://www.wikidata.org/wiki/Wikidata:SPARQL_query_service/queries/examples/advanced
* https://www.wikidata.org/wiki/Wikidata:SPARQL_query_service/queries/examples/human
* https://www.wikidata.org/wiki/Wikidata:SPARQL_query_service/queries/examples/maintenance
* https://commons.wikimedia.org/wiki/Commons:SPARQL_query_service/queries/examples

And also from:

* wmcloud's https://public-paws.wmcloud.org/4245849/large_sparql_queries_dataset.csv
  * Note that the CSV is not uploaded to GitHub as the file is too large, but the individual queries have been extracted
* Queries submitted from rewrite page comments and community discussions, and from phabricator issues (in other_examples)

Only the queries themselves are written. The surrounding, explanatory text is not captured.

## What It Produces

Running the extractor creates an `examples/` directory (by default) containing:

- One subdirectory per example category
- One `.rq` file per example query

Names are sanitized for filesystem use:

- Punctuation is removed
- Spaces become underscores
- Names are transliterated to ASCII when possible
- Duplicate names get numeric suffixes

## Source

The extractor reads rendered MediaWiki pages that contain SPARQL examples. It does not fetch from the query UI directly; it calls the MediaWiki parse API for the wiki that hosts the examples page, then parses the rendered HTML for headings and SPARQL code blocks.

Two values identify a source page:

- `--page-title`: the MediaWiki page title inside a wiki, such as `Wikidata:SPARQL_query_service/queries/examples`
- `--api-url`: the MediaWiki API endpoint for the wiki that hosts that page, such as `https://www.wikidata.org/w/api.php`

A page title alone is not enough to locate the page globally. The same title syntax can exist on different MediaWiki sites, and namespace prefixes such as `Wikidata:` or `Commons:` are interpreted by the host wiki. The host wiki is selected by `--api-url`.

The page URL is formed from the wiki base URL plus `/wiki/` plus the page title. The API URL is formed from the same wiki base URL plus `/w/api.php`.

Wikidata examples:

- Page title: `Wikidata:SPARQL_query_service/queries/examples`
- Page URL: `https://www.wikidata.org/wiki/Wikidata:SPARQL_query_service/queries/examples`
- API URL: `https://www.wikidata.org/w/api.php`

Commons examples:

- Page title: `Commons:SPARQL_query_service/queries/examples`
- Page URL: `https://commons.wikimedia.org/wiki/Commons:SPARQL_query_service/queries/examples`
- API URL: `https://commons.wikimedia.org/w/api.php`

The script shells out to `curl` once per run to call the parse API with the selected page title. The API returns JSON containing rendered HTML in `parse.text`; the rest of the extraction is done locally in Python.

## Script

Main script:

- `scripts/extract_wdqs_examples.py`

## Usage

From the project root:

```bash
python3 scripts/extract_wdqs_examples.py --output-dir examples
```

(Default) Optional arguments:

```bash
python3 scripts/extract_wdqs_examples.py --output-dir examples --page-title 'Wikidata:SPARQL_query_service/queries/examples' --api-url 'https://www.wikidata.org/w/api.php'
```

### Optional Arguments

`--output-dir`

- Default: `examples`
- Sets the destination directory for the generated category folders and `.rq` files
- This exists so you can write to a different target when comparing runs, testing changes, or keeping multiple snapshots side by side

Example:

```bash
python3 scripts/extract_wdqs_examples.py --output-dir examples_snapshot
```

`--page-title`

- Default: `Wikidata:SPARQL_query_service/queries/examples`
- Overrides the English MediaWiki page title that the extractor reads from
- Identifies the examples page within the wiki selected by `--api-url`
- This exists so you can point the same extraction logic at different examples pages, a sandbox page, or a future replacement page without changing the script

Example:

```bash
python3 scripts/extract_wdqs_examples.py --output-dir examples_test --page-title 'Wikidata:SPARQL_query_service/queries/examples/sandbox'
```

`--api-url`

- Default: `https://www.wikidata.org/w/api.php`
- Overrides the MediaWiki API endpoint used to fetch the rendered examples page
- Use this with `--page-title` when extracting examples from another Wikimedia wiki
- Selects the host wiki; the page title alone does not identify whether the page lives on Wikidata, Commons, or another MediaWiki site

Example:

```bash
python3 scripts/extract_wdqs_examples.py \
  --api-url 'https://commons.wikimedia.org/w/api.php' \
  --page-title 'Commons:SPARQL_query_service/queries/examples' \
  --output-dir commons_examples
```

## Run All Current Extractions

From the project root, refresh the current extracted example sets with:

```bash
python3 scripts/extract_wdqs_examples.py \
  --page-title 'Wikidata:SPARQL_query_service/queries/examples' \
  --output-dir examples

python3 scripts/extract_wdqs_examples.py \
  --page-title 'Wikidata:SPARQL_query_service/queries/examples/advanced' \
  --output-dir advanced_examples

python3 scripts/extract_wdqs_examples.py \
  --page-title 'Wikidata:SPARQL_query_service/queries/examples/human' \
  --output-dir human_examples

python3 scripts/extract_wdqs_examples.py \
  --page-title 'Wikidata:SPARQL_query_service/queries/examples/maintenance' \
  --output-dir maintenance_examples

python3 scripts/extract_wdqs_examples.py \
  --api-url 'https://commons.wikimedia.org/w/api.php' \
  --page-title 'Commons:SPARQL_query_service/queries/examples' \
  --output-dir commons_examples
```

The Wikidata subpages use the default Wikidata API endpoint. Commons uses the Commons API endpoint with the same parser.

## Behavior

- Fetches the live examples page
- Parses rendered headings and SPARQL blocks
- Uses the heading one level above a query title as that query's category
- Writes output to a staging directory first
- Replaces the target output directory atomically
- Prints a collision summary after completion

## Current Output

At the time of the last run in this workspace, the extractor generated:

- `examples/` — 395 queries in 55 categories
- `advanced_examples/` — 84 queries in 19 categories
- `human_examples/` — 14 queries in 3 categories
- `maintenance_examples/` — 64 queries in 3 categories
- `commons_examples/` — 60 queries in 13 categories, with 1 uncategorized query

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
python3 -c 'from pathlib import Path
for name in ["examples", "advanced_examples", "human_examples", "maintenance_examples", "commons_examples"]:
    base = Path(name)
    print(name, sum(1 for p in base.iterdir() if p.is_dir()))'
```

Count generated query files:

```bash
python3 -c 'from pathlib import Path
for name in ["examples", "advanced_examples", "human_examples", "maintenance_examples", "commons_examples"]:
    print(name, sum(1 for p in Path(name).rglob("*.rq")))'
```

Inspect a known sample query:

```bash
python3 -c 'from pathlib import Path; print(Path("examples/Simple_queries/Cats.rq").read_text()[:300])'
```

List a few generated files:

```bash
find examples -maxdepth 2 -type f | sort | sed -n '1,20p'
```
