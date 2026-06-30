# Blazegraph Features in the Sample Queries

This task reviews the Wikitech page below and maps the Blazegraph-specific WDQS functionality it describes to the local query examples under `examples/`, `advanced_examples/`, `human_examples/`, and `maintenance_examples/`.

Source page:

- `https://wikitech.wikimedia.org/wiki/User:AWesterinen/Blazegraph_Features_and_Capabilities`

## Goal

Identify the Blazegraph-specific features, extensions, and services discussed on the 'Features and Capabilities' page, then find which local example queries use them.

## Inputs

- Source documentation page on Wikitech
- Local extracted query examples in `examples/`, `advanced_examples/`, `human_examples/`, and `maintenance_examples/`

## Outputs

- Main reports:
  - `blazegraph_usage_report.md`
  - `blazegraph_usage_report.html`
- Report generator: `scripts/report_blazegraph_usage_in_examples.py`

## What The Report Covers

The report is split into:

- `Blazegraph Features`
- `Function Extensions`
- `SERVICE Extensions`
- `Supporting Blazegraph-Specific Syntax`

For each feature, the report includes:

- A local match count
- A file-by-file list of matching `.rq` queries across the scanned example sets (if <100 queries)

It also includes a summary table at the top for quick scanning.

## Currently Tracked Features

Features and extensions:

- Named sub-queries using `WITH { ... } AS %name` and `INCLUDE %name`
- `geof:globe()`
- `geof:latitude()`
- `geof:longitude()`
- `geof:distance()`
- `wikibase:decodeUri()`
- `SERVICE wikibase:around`
- `SERVICE wikibase:box`
- `SERVICE wikibase:label`
- `SERVICE bd:slice`
- `SERVICE wikibase:mwapi`
- `SERVICE gas:service`
- `SERVICE bd:sample`

Supporting syntax tracked separately:

- `hint:Query`
- `bd:serviceParam`

## How Matching Works

By default the script scans every `.rq` file under all four example sets (`examples/`, `advanced_examples/`, `human_examples/`, and `maintenance_examples/`) and applies regex-based feature matchers. Matching file paths are reported as absolute paths, so matches from the different sets are unambiguous.

This is intentional to preserve exact file-level traceability. `all_examples.txt` is useful for quick one-file searching, but the `.rq` trees are the source of truth for the report because the final output needs exact matching file paths.

## Usage

Run from the project root:

```bash
python3 scripts/report_blazegraph_usage_in_examples.py
```

With no arguments this scans all four example sets (`examples/`, `advanced_examples/`, `human_examples/`, `maintenance_examples/`) and writes:

```bash
blazegraph_usage_report.md
blazegraph_usage_report.html
```

Optional arguments:

```bash
python3 scripts/report_blazegraph_usage_in_examples.py --examples-dir examples advanced_examples --output-md blazegraph_usage_report.md --output-html blazegraph_usage_report.html
```

## Script Arguments

`--examples-dir`

- Default: `examples advanced_examples human_examples maintenance_examples`
- One or more directories containing the `.rq` files to scan
- Accepts a space-separated list, so you can scan a single set (`--examples-dir examples`) or any subset of the extracted query sets

`--output-md`

- Default: `blazegraph_usage_report.md`
- Output path for the generated markdown report
- Useful if you want to compare multiple runs or keep versioned snapshots

`--output-html`

- Default: `blazegraph_usage_report.html`
- Output path for the generated HTML report
- Useful if you want a browser-friendly version with clickable file links

## Notes

- The report is based on the specific Wikitech page listed above, not on a broader independent taxonomy.
- Some features discussed on the page may have zero matches across the current local example sets.

## Related Files

- `blazegraph_usage_report.md`
- `blazegraph_usage_report.html`
- `scripts/report_blazegraph_usage_in_examples.py`
- `examples/`
- `advanced_examples/`
- `human_examples/`
- `maintenance_examples/`
