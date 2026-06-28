# MWAPI → SPARQL 1.1 (`MWAPI.ipynb`)

A Jupyter notebook that reproduces Blazegraph's `SERVICE wikibase:mwapi { ... }` behavior.

Blazegraph exposes a proprietary MWAPI service that lets a query call the MediaWiki API (search, generators, CirrusSearch) inline and pipe the resulting entities straight into the graph pattern. A plain SPARQL 1.1 endpoint has no such extension. This notebook splits that single federated query into two steps:

1. **Search step (Python).** Call the MediaWiki API directly to find the relevant Wikidata entities (QIDs)
2. **Query step (SPARQL 1.1).** Inject those QIDs into a `VALUES` block and run an ordinary `SELECT` against the SPARQL endpoint

The result is a portable pattern that works against the Wikidata Query Service with no dependency on the `wikibase:mwapi` extension.

## Why

Wikidata is moving from using the Blazegraph triple store to QLever. Queries written against the `SERVICE wikibase:mwapi` extension do not port to a standard SPARQL 1.1 endpoint. Instead, this notebook performs the MediaWiki search **client-side** and hands the endpoint only what standard SPARQL understands — a `VALUES` list of entities.

## Endpoints used

| Constant | URL | Purpose |
| --- | --- | --- |
| `MW_URL` | `https://www.wikidata.org/w/api.php` | Wikidata MediaWiki API — search returns QIDs directly |
| `EN_MW_URL` | `https://en.wikipedia.org/w/api.php` | English Wikipedia/Commons API — returns article titles, mapped to QIDs |
| `WDQS_ENDPOINT` | `https://query.wikidata.org/sparql` | SPARQL 1.1 endpoint for the final query |

A descriptive `User-Agent` is set on the shared `requests.Session`, per the [Wikimedia API etiquette](https://www.mediawiki.org/wiki/API:Etiquette). Update it to identify your own tool/contact before running.

## Search functions

All searches are wrapped in `api_get()`, which adds a timeout and raises on the MediaWiki `error` field (maxlag, throttling, bad params).

### `wbsearchentities(term, lang="en")`
Wikidata label/alias search (`action=wbsearchentities`). Returns a list of QIDs, following `search-continue` to page through all matches. `language="en"` pulls in `mul` (multilingual) labels via the fallback chain.

### `cirrus_search(srsearch)`
CirrusSearch full-text search (`action=query&list=search`) in namespace 0, with `continue` handling. Addressed to `MW_URL`, namespace 0 is Wikidata, so it returns QIDs directly. Supports CirrusSearch keywords such as `haswbstatement:`, `insource:`, etc.

### `generator_to_qids(api, generator, params, max_results=100)`
Runs a MediaWiki **generator** and cross-links the resulting pages to Wikidata QIDs via `prop=pageprops&ppprop=wikibase_item`. Handles:

- **Per-generator limits** — the `GEN_LIMIT` map supplies the correct `*limit` parameter for each generator (`gcmlimit`, `gsrlimit`, `geulimit`, …)
- **Continuation** — follows `continue` until exhausted
- **Result cap** — `max_results` bounds the number of QIDs collected (`None` for all)

Useful against `EN_MW_URL` to drive queries from Wikipedia structure (categories, external-link usage, in-source citations) that has no direct Wikidata equivalent.

## Worked examples (Cell 1)

| Goal | Call |
| --- | --- |
| Entities labeled/aliased "Einstein" | `wbsearchentities("Einstein")` |
| Female astronauts (P106=Q11631, P21=Q6581072) | `cirrus_search("haswbstatement:P106=Q11631 haswbstatement:P21=Q6581072")` |
| Members of an English Wikipedia category | `generator_to_qids(EN_MW_URL, "categorymembers", {"gcmtitle": "Category:Nobel laureates in Physics", "gcmtype": "page"})` |
| Articles linking to `nature.com` | `generator_to_qids(EN_MW_URL, "exturlusage", {"geuquery": "nature.com", "geuprotocol": "https", "geunamespace": 0})` |
| Articles citing a DOI in source | `generator_to_qids(EN_MW_URL, "search", {"gsrsearch": 'insource:"10.1038/nature"', "gsrnamespace": 0})` |

## SPARQL step (Cells 2–3)

`run(qids)` builds the query, injecting the collected QIDs into a `VALUES` block:

```sparql
SELECT DISTINCT ?item ?type ?label ?typeLabel WHERE {
  VALUES ?item { wd:Q937 wd:Q… }
  ?item wdt:P31 ?type .          # apply real constraints here
  OPTIONAL { ?type rdfs:label ?typeLabel . FILTER(LANG(?typeLabel) = "en") }
  OPTIONAL { ?item rdfs:label ?label . FILTER(LANG(?label) = "en") }
  ...
}
```

This is the substitute for the Blazegraph `SERVICE wikibase:mwapi` block: the MediaWiki search supplies the bindings that the service used to produce inline. Replace the `wdt:P31` line with the real constraints for your use case.

`pretty_print(results)` renders the `sparql-results+json` response as an aligned table, shortening entity URIs back to QIDs.

## Requirements

- Python 3 with `requests`
- Jupyter (the kernel is `python3` / ipykernel)

```bash
pip install requests jupyter
jupyter notebook MWAPI.ipynb
```

Then run the cells top to bottom: define the helpers (Cell 0), run the search examples (Cell 1), then `run()` / `pretty_print()` the SPARQL query (Cells 2–3).

## Notes & limits

- `limit`/`max_results` and per-generator caps keep example runs small — raise them for production, mindful of API etiquette and `maxlag`
- Generator results depend on `pageprops.wikibase_item`; pages without a linked Wikidata item are dropped
- Very large QID sets make `VALUES` blocks unwieldy; batch the QIDs across multiple queries if needed

