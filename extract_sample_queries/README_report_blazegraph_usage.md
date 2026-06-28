# Blazegraph Features in the Sample Queries

This task reviews the Wikitech page below and maps the Blazegraph-specific WDQS functionality it describes to the local query examples under `examples/`.

Source page:

- `https://wikitech.wikimedia.org/wiki/User:AWesterinen/Blazegraph_Features_and_Capabilities`

## Goal

Identify the Blazegraph-specific features, extensions, and services discussed on the source page, then find which local example queries use them.

## Inputs

- Source documentation page on Wikitech
- Local extracted query examples in `examples/`

## Outputs

- Main reports:
  - `blazegraph_usage_report.md`
  - `blazegraph_usage_report.html`
- Report generator: `scripts/report_blazegraph_usage_in_examples.py`

## What The Report Covers

The report is split into:

- `Page-Listed Blazegraph Features`
- `Page-Listed Function Extensions`
- `Page-Listed SERVICE Extensions`
- `Supporting Blazegraph-Specific Syntax`

For each feature, the report includes:

- A local match count
- A short note about why the feature is included
- A file-by-file list of matching `.rq` queries under `examples/`

It also includes a summary table at the top for quick scanning.

## Currently Tracked Features

Page-listed features and extensions:

- Stored queries using `SERVICE <http://www.bigdata.com/rdf/stored-query#...>`
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

The script scans every `.rq` file under `examples/` and applies regex-based feature matchers.

This is intentional to preserve exact file-level traceability. `all_examples.txt` is useful for quick one-file searching, but the `.rq` tree is the source of truth for the report because the final output needs exact matching file paths.

## Usage

Run from the project root:

```bash
python3 scripts/report_blazegraph_usage_in_examples.py
```

This writes:

```bash
blazegraph_usage_report.md
blazegraph_usage_report.html
```

Optional arguments:

```bash
python3 scripts/report_blazegraph_usage_in_examples.py --examples-dir examples --output-md blazegraph_usage_report.md --output-html blazegraph_usage_report.html
```

## Script Arguments

`--examples-dir`

- Default: `examples`
- Directory containing the `.rq` files to scan
- Useful if you want to run the report against a different extracted query set

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
- Some features discussed on the page may have zero matches in the current local `examples/` tree.
- Stored queries are also explicitly tracked, although none are currently in the sample set.

## Related Files

- `blazegraph_usage_report.md`
- `blazegraph_usage_report.html`
- `scripts/report_blazegraph_usage_in_examples.py`
- `examples/`
