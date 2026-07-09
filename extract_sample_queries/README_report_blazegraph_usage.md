# Blazegraph Features in the Sample Queries

This task reviews the Wikitech page below and counts the Blazegraph-specific WDQS functionality it describes (plus a few other features such as wikibase:geoGlobe, wikibase:someValue and wikibase:globe) in the queries found in the extracted "example" directories, `wmcloud_queries/`, and `other_examples/`.

Source page:

- `https://wikitech.wikimedia.org/wiki/User:AWesterinen/Blazegraph_Features_and_Capabilities`

## Goal

Identify the Blazegraph-specific features, extensions, and services discussed on the 'Features and Capabilities' page (plus a few others), then find which queries use them.

## Inputs

- Source documentation page on Wikitech
- Wikimedia query examples in `examples/`, `advanced_examples/`, `human_examples/`, `maintenance_examples/`, and `commons_examples/`
- WMCloud query exports in `wmcloud_queries/`
- Additional query sets in immediate subdirectories of `other_examples/`

## Outputs

- Standard reports under `blazegraph_usage_reports/`:
  - `wikimedia_blazegraph_usage_report.md` and `.html`
  - `wmcloud_blazegraph_usage_report.md` and `.html`
  - `other_blazegraph_usage_report.md` and `.html`
  - `all_blazegraph_usage_report.md` and `.html`
- Report generator: `scripts/report_blazegraph_usage_in_examples.py`

## What The Report Covers

The report is split into:

- `Blazegraph Features`
- `Function Extensions`
- `SERVICE Extensions`
- `Supporting Blazegraph-Specific Syntax`

For each feature, the report includes:

- A local match count
- A file-by-file list of matching `.rq` queries across the scanned example sets when the match count is below the summary threshold

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
- `wikibase:someValue`
- `wikibase:geoGlobe`
- `wikibase:globe`

## How Matching Works

The script applies case-insensitive regex-based feature matchers. The scanner reads each query file once and checks all tracked features during that pass. Matching file paths are reported as absolute paths, so matches from the different sets are unambiguous.

In standard generation mode, the script scans the Wikimedia, WMCloud, and other query groups once each. The `all` report is built by merging those completed scan results rather than rescanning the same directories.

This is intentional to preserve exact file-level traceability. The `.rq` trees are the source of truth for the report because the final output needs exact matching file paths.

## Usage

Generate the standard report set from the project root:

```bash
python3 scripts/report_blazegraph_usage_in_examples.py --generate standard
```

Or from `scripts/`:

```bash
python3 report_blazegraph_usage_in_examples.py --generate standard
```

This writes four Markdown/HTML report pairs under `blazegraph_usage_reports/`:

```bash
wikimedia_blazegraph_usage_report.md
wikimedia_blazegraph_usage_report.html
wmcloud_blazegraph_usage_report.md
wmcloud_blazegraph_usage_report.html
other_blazegraph_usage_report.md
other_blazegraph_usage_report.html
all_blazegraph_usage_report.md
all_blazegraph_usage_report.html
```

Custom one-off reports are still supported:

```bash
python3 scripts/report_blazegraph_usage_in_examples.py --examples-dir examples advanced_examples --output-md custom.md --output-html custom.html --summary-threshold 100
```

## Script Arguments

`--generate`

- Supported value: `standard`
- Generates the four standard reports: Wikimedia, WMCloud, other, and all
- Cannot be combined with `--examples-dir`, `--output-md`, or `--output-html`

`--report-dir`

- Default: `blazegraph_usage_reports` beside `scripts/`
- Output directory for standard reports generated with `--generate standard`

`--examples-dir`

- Default for custom reports: `examples advanced_examples human_examples maintenance_examples commons_examples`, resolved under the project root beside `scripts/`
- One or more directories containing the `.rq` files to scan
- Accepts a space-separated list, so you can scan a single set (`--examples-dir examples`) or any subset of the extracted query sets

`--output-md`

- Default for custom reports: `custom_blazegraph_usage_report.md` under `blazegraph_usage_reports/`
- Output path for the generated markdown report
- Useful if you want to compare multiple runs or keep versioned snapshots

`--output-html`

- Default for custom reports: `custom_blazegraph_usage_report.html` under `blazegraph_usage_reports/`
- Output path for the generated HTML report
- Useful if you want a browser-friendly version with clickable file links

`--summary-threshold`

- Default: `100`
- Features with at least this many matches are summarized instead of listing every matching file
- Some high-volume features are always summarized even when a higher threshold is supplied

## Notes

- The report is based on the specific Wikitech page listed above, not on a broader independent taxonomy.
- Some features discussed on the page may have zero matches across the current local example sets.
- `wikibase:someValue` is tracked in the report even though the current local example sets have zero matches.

## Related Files

- `blazegraph_usage_reports/`
- `scripts/report_blazegraph_usage_in_examples.py`
- `examples/`
- `advanced_examples/`
- `human_examples/`
- `maintenance_examples/`
- `commons_examples/`
- `wmcloud_queries/`
- `other_examples/`
