# Blazegraph Feature Usage Report: All Queries

Source page reviewed: `https://wikitech.wikimedia.org/wiki/User:AWesterinen/Blazegraph_Features_and_Capabilities`

Scanned local example queries: `482823` `.rq` files under `/Users/arwest/Hold/Wikidata-Queries/extract_sample_queries/examples`, `/Users/arwest/Hold/Wikidata-Queries/extract_sample_queries/advanced_examples`, `/Users/arwest/Hold/Wikidata-Queries/extract_sample_queries/human_examples`, `/Users/arwest/Hold/Wikidata-Queries/extract_sample_queries/maintenance_examples`, `/Users/arwest/Hold/Wikidata-Queries/extract_sample_queries/wmcloud_queries`, `/Users/arwest/Hold/Wikidata-Queries/extract_sample_queries/other_examples/phab_issues`, `/Users/arwest/Hold/Wikidata-Queries/extract_sample_queries/other_examples/submitted`.

This report uses the feature inventory described on the referenced Wikitech page, then maps each feature to matching files in the local example trees.

## Summary Table

| Section | Feature | Matches |
| --- | --- | ---: |
| Blazegraph Features | Named sub-queries (`WITH { ... } AS %name`, `INCLUDE %name`) | 68493 |
| Function Extensions | `geof:globe()` | 0 |
| Function Extensions | `geof:latitude()` | 8 |
| Function Extensions | `geof:longitude()` | 7 |
| Function Extensions | `geof:distance()` | 77 |
| Function Extensions | `wikibase:decodeUri()` | 291 |
| Function Extensions | `wikibase:isSomeValue()` | 37785 |
| SERVICE Extensions | `SERVICE wikibase:around` | 103 |
| SERVICE Extensions | `SERVICE wikibase:box` | 17 |
| SERVICE Extensions | `SERVICE wikibase:label` | 349510 |
| SERVICE Extensions | `SERVICE bd:slice` | 26 |
| SERVICE Extensions | `SERVICE wikibase:mwapi` | 35421 |
| SERVICE Extensions | `SERVICE gas:service` | 722 |
| SERVICE Extensions | `SERVICE bd:sample` | 11754 |
| Supporting Blazegraph-Specific Syntax | `hint:Query ...` query hints | 60489 |

## Miscellaneous

| Category | Detail | Matches |
| --- | --- | ---: |
| Wikidata RDF Pseudo-Value | `wikibase:someValue` | 0 |
| Wikidata RDF Predicates | `wikibase:geoGlobe` | 19 |
| Federated SERVICE endpoint | `https://qlever.dev/api/wikimedia-commons` | 0 |
| Federated SERVICE endpoint | Other `SERVICE <...>` endpoint | 108 |
| `wikibase:api` value | `Generator` | 22599 |
| `wikibase:api` value | `Categories` | 6 |
| `wikibase:api` value | `Search` | 998 |
| `wikibase:api` value | `EntitySearch` | 3 |
| `mwapi:generator` value for `wikibase:api` `Generator` | `categorymembers` | 22 |
| `mwapi:generator` value for `wikibase:api` `Generator` | `exturlusage` | 1 |
| `mwapi:generator` value for `wikibase:api` `Generator` | `search` | 214 |
| `mwapi:generator` value for `wikibase:api` `Generator` | `allpages` | 22351 |
| `mwapi:generator` value for `wikibase:api` `Generator` | `links` | 3 |
| `mwapi:generator` value for `wikibase:api` `Generator` | `random` | 10 |
| `mwapi:generator` value for `wikibase:api` `Generator` | `recentchanges` | 1 |

### Wikidata RDF Pseudo-Value: `wikibase:someValue`

- Local matches: 0

Matching files:
- None in the current example trees

### Wikidata RDF Predicates: `wikibase:geoGlobe`

- Local matches: 19

Matching files:
- `/Users/arwest/Hold/Wikidata-Queries/extract_sample_queries/examples/Geography/Globes_used_to_represent_coordinates.rq`
- `/Users/arwest/Hold/Wikidata-Queries/extract_sample_queries/examples/Mountains/Mons_mountains_with_coordinates_not_located_on_Earth.rq`
- `/Users/arwest/Hold/Wikidata-Queries/extract_sample_queries/maintenance_examples/Queries_for_maintenance/Objects_that_have_globe_which_does_not_match_their_coordinates.rq`
- `/Users/arwest/Hold/Wikidata-Queries/extract_sample_queries/wmcloud_queries/Bot_requestsArchive201605.rq`
- `/Users/arwest/Hold/Wikidata-Queries/extract_sample_queries/wmcloud_queries/J_N_SquireArchive_1.rq`
- `/Users/arwest/Hold/Wikidata-Queries/extract_sample_queries/wmcloud_queries/P3037_19.rq`
- `/Users/arwest/Hold/Wikidata-Queries/extract_sample_queries/wmcloud_queries/P376_5.rq`
- `/Users/arwest/Hold/Wikidata-Queries/extract_sample_queries/wmcloud_queries/P625_7.rq`
- `/Users/arwest/Hold/Wikidata-Queries/extract_sample_queries/wmcloud_queries/P8981_16.rq`
- `/Users/arwest/Hold/Wikidata-Queries/extract_sample_queries/wmcloud_queries/Status_updates2016_12_31_3.rq`
- `/Users/arwest/Hold/Wikidata-Queries/extract_sample_queries/wmcloud_queries/Status_updates2021_06_14.rq`
- `/Users/arwest/Hold/Wikidata-Queries/extract_sample_queries/wmcloud_queries/U8uwc4p16hjv6v4l.rq`
- `/Users/arwest/Hold/Wikidata-Queries/extract_sample_queries/wmcloud_queries/Uz6pfsawzd14b38c.rq`
- `/Users/arwest/Hold/Wikidata-Queries/extract_sample_queries/wmcloud_queries/V7febdtf3f7btivn.rq`
- `/Users/arwest/Hold/Wikidata-Queries/extract_sample_queries/wmcloud_queries/Wausi3w43zyj6v7t.rq`
- `/Users/arwest/Hold/Wikidata-Queries/extract_sample_queries/wmcloud_queries/WikiProject_Astronomy_2.rq`
- `/Users/arwest/Hold/Wikidata-Queries/extract_sample_queries/wmcloud_queries/WikiProject_European_Bathing_Waters.rq`
- `/Users/arwest/Hold/Wikidata-Queries/extract_sample_queries/wmcloud_queries/WikiProject_Outdoor_Gyms.rq`
- `/Users/arwest/Hold/Wikidata-Queries/extract_sample_queries/wmcloud_queries/WikiProject_Wikidata_for_researchState_of_the_Map_2018_2.rq`

### Federated SERVICE endpoint: `https://qlever.dev/api/wikimedia-commons`

- Local matches: 0

Matching files:
- None in the current example trees

### Federated SERVICE endpoint: Other `SERVICE <...>` endpoint

- Local matches: 108

Matching files:
- 108 matching files (not listed individually)

### `wikibase:api` value: `Generator`

- Local matches: 22599

Matching files:
- 22599 matching files (not listed individually)

### `wikibase:api` value: `Categories`

- Local matches: 6

Matching files:
- `/Users/arwest/Hold/Wikidata-Queries/extract_sample_queries/wmcloud_queries/Query_pagesandbox_2_2.rq`
- `/Users/arwest/Hold/Wikidata-Queries/extract_sample_queries/wmcloud_queries/Query_pagesandbox_2_3.rq`
- `/Users/arwest/Hold/Wikidata-Queries/extract_sample_queries/wmcloud_queries/Status_updates2017_07_17_6.rq`
- `/Users/arwest/Hold/Wikidata-Queries/extract_sample_queries/wmcloud_queries/Tuhna9a1j01kadj0_6.rq`
- `/Users/arwest/Hold/Wikidata-Queries/extract_sample_queries/wmcloud_queries/WikiProject_Source_MetaDataWikidata_listsUsage_of_Template_ScholiaEnglish_WikipediaWith_CC0_images.rq`
- `/Users/arwest/Hold/Wikidata-Queries/extract_sample_queries/wmcloud_queries/WikiProject_Source_MetaDataWikidata_listsUsage_of_Template_ScholiaEnglish_WikipediaWith_CC0_images_2.rq`

### `wikibase:api` value: `Search`

- Local matches: 998

Matching files:
- 998 matching files (not listed individually)

### `wikibase:api` value: `EntitySearch`

- Local matches: 3

Matching files:
- `/Users/arwest/Hold/Wikidata-Queries/extract_sample_queries/wmcloud_queries/Project_chatArchive201908_4.rq`
- `/Users/arwest/Hold/Wikidata-Queries/extract_sample_queries/wmcloud_queries/Request_a_queryArchive201706_8.rq`
- `/Users/arwest/Hold/Wikidata-Queries/extract_sample_queries/wmcloud_queries/Request_a_queryArchive202210_2.rq`

### `mwapi:generator` value for `wikibase:api` `Generator`: `categorymembers`

- Local matches: 22

Matching files:
- `/Users/arwest/Hold/Wikidata-Queries/extract_sample_queries/advanced_examples/Mediawiki_API/Getting_pageviews_for_all_articles_in_a_category.rq`
- `/Users/arwest/Hold/Wikidata-Queries/extract_sample_queries/wmcloud_queries/99of9_31.rq`
- `/Users/arwest/Hold/Wikidata-Queries/extract_sample_queries/wmcloud_queries/Daniel_MietchenWikidata_listsCurrent_events.rq`
- `/Users/arwest/Hold/Wikidata-Queries/extract_sample_queries/wmcloud_queries/MargaretRDonaldMarieCurietimelineAndVolcanoes_155.rq`
- `/Users/arwest/Hold/Wikidata-Queries/extract_sample_queries/wmcloud_queries/MargaretRDonaldMarieCurietimelineAndVolcanoes_162.rq`
- `/Users/arwest/Hold/Wikidata-Queries/extract_sample_queries/wmcloud_queries/NurniRPON.rq`
- `/Users/arwest/Hold/Wikidata-Queries/extract_sample_queries/wmcloud_queries/NurniRPON_14.rq`
- `/Users/arwest/Hold/Wikidata-Queries/extract_sample_queries/wmcloud_queries/NurniRPON_20.rq`
- `/Users/arwest/Hold/Wikidata-Queries/extract_sample_queries/wmcloud_queries/NurniRPON_34.rq`
- `/Users/arwest/Hold/Wikidata-Queries/extract_sample_queries/wmcloud_queries/NurniRPON_35.rq`
- `/Users/arwest/Hold/Wikidata-Queries/extract_sample_queries/wmcloud_queries/NurniRPON_36.rq`
- `/Users/arwest/Hold/Wikidata-Queries/extract_sample_queries/wmcloud_queries/NurniRPON_39.rq`
- `/Users/arwest/Hold/Wikidata-Queries/extract_sample_queries/wmcloud_queries/NurniRPON_4.rq`
- `/Users/arwest/Hold/Wikidata-Queries/extract_sample_queries/wmcloud_queries/NurniRPON_6.rq`
- `/Users/arwest/Hold/Wikidata-Queries/extract_sample_queries/wmcloud_queries/NurniRPON_7.rq`
- `/Users/arwest/Hold/Wikidata-Queries/extract_sample_queries/wmcloud_queries/NurniRPON_8.rq`
- `/Users/arwest/Hold/Wikidata-Queries/extract_sample_queries/wmcloud_queries/NurniRPON_9.rq`
- `/Users/arwest/Hold/Wikidata-Queries/extract_sample_queries/wmcloud_queries/Project_chatArchive201706_3.rq`
- `/Users/arwest/Hold/Wikidata-Queries/extract_sample_queries/wmcloud_queries/Request_a_queryArchive202003_2.rq`
- `/Users/arwest/Hold/Wikidata-Queries/extract_sample_queries/wmcloud_queries/Trade.rq`
- `/Users/arwest/Hold/Wikidata-Queries/extract_sample_queries/wmcloud_queries/Ua7by38ni68ppr8s.rq`
- `/Users/arwest/Hold/Wikidata-Queries/extract_sample_queries/wmcloud_queries/WikiProject_Source_MetaDataWikidata_listsUsage_of_Template_ScholiaEnglish_WikipediaWith_CC0_images_3.rq`

### `mwapi:generator` value for `wikibase:api` `Generator`: `exturlusage`

- Local matches: 1

Matching files:
- `/Users/arwest/Hold/Wikidata-Queries/extract_sample_queries/advanced_examples/Mediawiki_API/Find_statements_with_references_containing_external_links_to_wipoint.rq`

### `mwapi:generator` value for `wikibase:api` `Generator`: `search`

- Local matches: 214

Matching files:
- 214 matching files (not listed individually)

### `mwapi:generator` value for `wikibase:api` `Generator`: `allpages`

- Local matches: 22351

Matching files:
- 22351 matching files (not listed individually)

### `mwapi:generator` value for `wikibase:api` `Generator`: `links`

- Local matches: 3

Matching files:
- `/Users/arwest/Hold/Wikidata-Queries/extract_sample_queries/wmcloud_queries/Database_reportsno_statementsfrwiki.rq`
- `/Users/arwest/Hold/Wikidata-Queries/extract_sample_queries/wmcloud_queries/Database_reportsno_statementsfrwikiold.rq`
- `/Users/arwest/Hold/Wikidata-Queries/extract_sample_queries/wmcloud_queries/MultichillCC0_data.rq`

### `mwapi:generator` value for `wikibase:api` `Generator`: `random`

- Local matches: 10

Matching files:
- `/Users/arwest/Hold/Wikidata-Queries/extract_sample_queries/wmcloud_queries/MargaretRDonaldQueries_requested.rq`
- `/Users/arwest/Hold/Wikidata-Queries/extract_sample_queries/wmcloud_queries/MargaretRDonaldQueries_requested_10.rq`
- `/Users/arwest/Hold/Wikidata-Queries/extract_sample_queries/wmcloud_queries/MargaretRDonaldQueries_requested_2.rq`
- `/Users/arwest/Hold/Wikidata-Queries/extract_sample_queries/wmcloud_queries/MargaretRDonaldQueries_requested_3.rq`
- `/Users/arwest/Hold/Wikidata-Queries/extract_sample_queries/wmcloud_queries/MargaretRDonaldQueries_requested_4.rq`
- `/Users/arwest/Hold/Wikidata-Queries/extract_sample_queries/wmcloud_queries/MargaretRDonaldQueries_requested_5.rq`
- `/Users/arwest/Hold/Wikidata-Queries/extract_sample_queries/wmcloud_queries/MargaretRDonaldQueries_requested_6.rq`
- `/Users/arwest/Hold/Wikidata-Queries/extract_sample_queries/wmcloud_queries/MargaretRDonaldQueries_requested_7.rq`
- `/Users/arwest/Hold/Wikidata-Queries/extract_sample_queries/wmcloud_queries/MargaretRDonaldQueries_requested_8.rq`
- `/Users/arwest/Hold/Wikidata-Queries/extract_sample_queries/wmcloud_queries/MargaretRDonaldQueries_requested_9.rq`

### `mwapi:generator` value for `wikibase:api` `Generator`: `recentchanges`

- Local matches: 1

Matching files:
- `/Users/arwest/Hold/Wikidata-Queries/extract_sample_queries/wmcloud_queries/Request_a_queryArchive202207.rq`

## Blazegraph Features

- Features in this section: 1
- Total matches across this section: 68493

### Named sub-queries (`WITH { ... } AS %name`, `INCLUDE %name`)

- Local matches: 68493

Matching files:
- 68493 matching files (not listed individually)

## Function Extensions

- Features in this section: 6
- Total matches across this section: 38168

### `geof:globe()`

- Local matches: 0

Matching files:
- None in the current example trees

### `geof:latitude()`

- Local matches: 8

Matching files:
- `/Users/arwest/Hold/Wikidata-Queries/extract_sample_queries/maintenance_examples/Queries_for_maintenance/Items_showing_two_coordinates_very_distant_each_to_other.rq`
- `/Users/arwest/Hold/Wikidata-Queries/extract_sample_queries/wmcloud_queries/Charles_MatthewsQueries_50.rq`
- `/Users/arwest/Hold/Wikidata-Queries/extract_sample_queries/wmcloud_queries/EventsWikidata_Zurich_Training2019Showcase_queries.rq`
- `/Users/arwest/Hold/Wikidata-Queries/extract_sample_queries/wmcloud_queries/EventsWikidata_Zurich_Training2019Showcase_queries_2.rq`
- `/Users/arwest/Hold/Wikidata-Queries/extract_sample_queries/wmcloud_queries/Status_updates2017_09_18_2.rq`
- `/Users/arwest/Hold/Wikidata-Queries/extract_sample_queries/wmcloud_queries/TweetsFactsAndQueriesvshlaeumqueries.rq`
- `/Users/arwest/Hold/Wikidata-Queries/extract_sample_queries/wmcloud_queries/WikiProject_Netherlands_Public_LibrariesMaps_5.rq`
- `/Users/arwest/Hold/Wikidata-Queries/extract_sample_queries/wmcloud_queries/WikiProject_Netherlands_Public_LibrariesMaps_7.rq`

### `geof:longitude()`

- Local matches: 7

Matching files:
- `/Users/arwest/Hold/Wikidata-Queries/extract_sample_queries/maintenance_examples/Queries_for_maintenance/Items_showing_two_coordinates_very_distant_each_to_other.rq`
- `/Users/arwest/Hold/Wikidata-Queries/extract_sample_queries/wmcloud_queries/Charles_MatthewsQueries_50.rq`
- `/Users/arwest/Hold/Wikidata-Queries/extract_sample_queries/wmcloud_queries/EventsWikidata_Zurich_Training2019Showcase_queries.rq`
- `/Users/arwest/Hold/Wikidata-Queries/extract_sample_queries/wmcloud_queries/EventsWikidata_Zurich_Training2019Showcase_queries_2.rq`
- `/Users/arwest/Hold/Wikidata-Queries/extract_sample_queries/wmcloud_queries/Status_updates2017_09_18_2.rq`
- `/Users/arwest/Hold/Wikidata-Queries/extract_sample_queries/wmcloud_queries/TweetsFactsAndQueriesvshlaeumqueries.rq`
- `/Users/arwest/Hold/Wikidata-Queries/extract_sample_queries/wmcloud_queries/WikiProject_Netherlands_Public_LibrariesMaps_6.rq`

### `geof:distance()`

- Local matches: 77

Matching files:
- `/Users/arwest/Hold/Wikidata-Queries/extract_sample_queries/examples/Distances_between_any_two_cities_or_municipalities_in_an_area/grouped_by_dist_range_colorcoded.rq`
- `/Users/arwest/Hold/Wikidata-Queries/extract_sample_queries/examples/Distances_between_any_two_cities_or_municipalities_in_an_area/grouped_per_municipality_on_xaxis_alphabetically.rq`
- `/Users/arwest/Hold/Wikidata-Queries/extract_sample_queries/examples/Distances_between_any_two_cities_or_municipalities_in_an_area/grouped_per_municipality_on_xaxis_animated_by_fixed_dist_range_groups.rq`
- `/Users/arwest/Hold/Wikidata-Queries/extract_sample_queries/examples/Distances_between_any_two_cities_or_municipalities_in_an_area/grouped_per_municipality_on_xaxis_animated_by_ranked_dist_farthest_2nd_farthest.rq`
- `/Users/arwest/Hold/Wikidata-Queries/extract_sample_queries/examples/Distances_between_any_two_cities_or_municipalities_in_an_area/grouped_per_municipality_on_xaxis_animated_per_municipality_on_xaxis.rq`
- `/Users/arwest/Hold/Wikidata-Queries/extract_sample_queries/examples/Distances_between_any_two_cities_or_municipalities_in_an_area/grouped_per_municipality_on_xaxis_animated_per_municipality_on_zaxis.rq`
- `/Users/arwest/Hold/Wikidata-Queries/extract_sample_queries/examples/Distances_between_any_two_cities_or_municipalities_in_an_area/grouped_per_municipality_on_xaxis_by_sum_of_dist.rq`
- `/Users/arwest/Hold/Wikidata-Queries/extract_sample_queries/examples/External_Federation_Queries_using_Wikidata_plus_Other_sources/UK_Parliament_constituencies_whose_official_point_location_is_more_than_10km_from_the_location_in_Wikidata.rq`
- `/Users/arwest/Hold/Wikidata-Queries/extract_sample_queries/examples/Geography/Places_in_Antarctica_more_than_3000km_away_from_the_South_Pole.rq`
- `/Users/arwest/Hold/Wikidata-Queries/extract_sample_queries/examples/Geography/Places_within_1km_of_the_Empire_State_Building.rq`
- `/Users/arwest/Hold/Wikidata-Queries/extract_sample_queries/maintenance_examples/Queries_for_maintenance/Items_showing_two_coordinates_very_distant_each_to_other.rq`
- `/Users/arwest/Hold/Wikidata-Queries/extract_sample_queries/wmcloud_queries/99of9_55.rq`
- `/Users/arwest/Hold/Wikidata-Queries/extract_sample_queries/wmcloud_queries/99of9_58.rq`
- `/Users/arwest/Hold/Wikidata-Queries/extract_sample_queries/wmcloud_queries/99of9_59.rq`
- `/Users/arwest/Hold/Wikidata-Queries/extract_sample_queries/wmcloud_queries/99of9_72.rq`
- `/Users/arwest/Hold/Wikidata-Queries/extract_sample_queries/wmcloud_queries/99of9_73.rq`
- `/Users/arwest/Hold/Wikidata-Queries/extract_sample_queries/wmcloud_queries/99of9_74.rq`
- `/Users/arwest/Hold/Wikidata-Queries/extract_sample_queries/wmcloud_queries/99of9_75.rq`
- `/Users/arwest/Hold/Wikidata-Queries/extract_sample_queries/wmcloud_queries/Administrators_noticeboardArchive201901.rq`
- `/Users/arwest/Hold/Wikidata-Queries/extract_sample_queries/wmcloud_queries/Charles_MatthewsQueries_69.rq`
- `/Users/arwest/Hold/Wikidata-Queries/extract_sample_queries/wmcloud_queries/Conny_9.rq`
- `/Users/arwest/Hold/Wikidata-Queries/extract_sample_queries/wmcloud_queries/Hitrandil_WMIT.rq`
- `/Users/arwest/Hold/Wikidata-Queries/extract_sample_queries/wmcloud_queries/Import_BLKO_from_wikisourcereportsborn_farthest_from_Vienna.rq`
- `/Users/arwest/Hold/Wikidata-Queries/extract_sample_queries/wmcloud_queries/JebPesterwitz.rq`
- `/Users/arwest/Hold/Wikidata-Queries/extract_sample_queries/wmcloud_queries/Jura1test97.rq`
- `/Users/arwest/Hold/Wikidata-Queries/extract_sample_queries/wmcloud_queries/P1334_16.rq`
- `/Users/arwest/Hold/Wikidata-Queries/extract_sample_queries/wmcloud_queries/P2632_25.rq`
- `/Users/arwest/Hold/Wikidata-Queries/extract_sample_queries/wmcloud_queries/P2659_23.rq`
- `/Users/arwest/Hold/Wikidata-Queries/extract_sample_queries/wmcloud_queries/P625_22.rq`
- `/Users/arwest/Hold/Wikidata-Queries/extract_sample_queries/wmcloud_queries/P625_23.rq`
- `/Users/arwest/Hold/Wikidata-Queries/extract_sample_queries/wmcloud_queries/P625_24.rq`
- `/Users/arwest/Hold/Wikidata-Queries/extract_sample_queries/wmcloud_queries/PaucabotVisuals_19.rq`
- `/Users/arwest/Hold/Wikidata-Queries/extract_sample_queries/wmcloud_queries/PaucabotVisuals_26.rq`
- `/Users/arwest/Hold/Wikidata-Queries/extract_sample_queries/wmcloud_queries/PaucabotVisuals_6.rq`
- `/Users/arwest/Hold/Wikidata-Queries/extract_sample_queries/wmcloud_queries/PaucabotWork_15.rq`
- `/Users/arwest/Hold/Wikidata-Queries/extract_sample_queries/wmcloud_queries/PaucabotWork_16.rq`
- `/Users/arwest/Hold/Wikidata-Queries/extract_sample_queries/wmcloud_queries/Philbarker.rq`
- `/Users/arwest/Hold/Wikidata-Queries/extract_sample_queries/wmcloud_queries/Project_chatArchive202111.rq`
- `/Users/arwest/Hold/Wikidata-Queries/extract_sample_queries/wmcloud_queries/Request_a_queryArchive201609_6.rq`
- `/Users/arwest/Hold/Wikidata-Queries/extract_sample_queries/wmcloud_queries/Request_a_queryArchive201903_5.rq`
- `/Users/arwest/Hold/Wikidata-Queries/extract_sample_queries/wmcloud_queries/Request_a_queryArchive201903_6.rq`
- `/Users/arwest/Hold/Wikidata-Queries/extract_sample_queries/wmcloud_queries/Request_a_queryArchive201903_7.rq`
- `/Users/arwest/Hold/Wikidata-Queries/extract_sample_queries/wmcloud_queries/Request_a_queryArchive202005.rq`
- `/Users/arwest/Hold/Wikidata-Queries/extract_sample_queries/wmcloud_queries/Request_a_queryArchive202209_2.rq`
- `/Users/arwest/Hold/Wikidata-Queries/extract_sample_queries/wmcloud_queries/Request_a_queryArchive202209_3.rq`
- `/Users/arwest/Hold/Wikidata-Queries/extract_sample_queries/wmcloud_queries/Requests_for_deletionsArchive20200620.rq`
- `/Users/arwest/Hold/Wikidata-Queries/extract_sample_queries/wmcloud_queries/Status_updates2016_07_02_7.rq`
- `/Users/arwest/Hold/Wikidata-Queries/extract_sample_queries/wmcloud_queries/Status_updates2016_10_15_6.rq`
- `/Users/arwest/Hold/Wikidata-Queries/extract_sample_queries/wmcloud_queries/Status_updates2016_10_15_7.rq`
- `/Users/arwest/Hold/Wikidata-Queries/extract_sample_queries/wmcloud_queries/Status_updates2017_05_01_4.rq`
- `/Users/arwest/Hold/Wikidata-Queries/extract_sample_queries/wmcloud_queries/Status_updates2017_09_11_2.rq`
- `/Users/arwest/Hold/Wikidata-Queries/extract_sample_queries/wmcloud_queries/Status_updates2017_09_11_4.rq`
- `/Users/arwest/Hold/Wikidata-Queries/extract_sample_queries/wmcloud_queries/Status_updates2018_05_28_3.rq`
- `/Users/arwest/Hold/Wikidata-Queries/extract_sample_queries/wmcloud_queries/Status_updates2018_07_16_2.rq`
- `/Users/arwest/Hold/Wikidata-Queries/extract_sample_queries/wmcloud_queries/Status_updates2018_09_03_2.rq`
- `/Users/arwest/Hold/Wikidata-Queries/extract_sample_queries/wmcloud_queries/Status_updates2019_03_04.rq`
- `/Users/arwest/Hold/Wikidata-Queries/extract_sample_queries/wmcloud_queries/Status_updates2019_06_10_2.rq`
- `/Users/arwest/Hold/Wikidata-Queries/extract_sample_queries/wmcloud_queries/Status_updates2019_09_02_2.rq`
- `/Users/arwest/Hold/Wikidata-Queries/extract_sample_queries/wmcloud_queries/Status_updates2021_06_14.rq`
- `/Users/arwest/Hold/Wikidata-Queries/extract_sample_queries/wmcloud_queries/T73wvhyx97sczye4_7.rq`
- `/Users/arwest/Hold/Wikidata-Queries/extract_sample_queries/wmcloud_queries/Tagishsimon_102.rq`
- `/Users/arwest/Hold/Wikidata-Queries/extract_sample_queries/wmcloud_queries/Tagishsimon_109.rq`
- `/Users/arwest/Hold/Wikidata-Queries/extract_sample_queries/wmcloud_queries/Tagishsimon_5.rq`
- `/Users/arwest/Hold/Wikidata-Queries/extract_sample_queries/wmcloud_queries/Tdma8f0ho8cwdmx8_6.rq`
- `/Users/arwest/Hold/Wikidata-Queries/extract_sample_queries/wmcloud_queries/Tdma8f0ho8cwdmx8_7.rq`
- `/Users/arwest/Hold/Wikidata-Queries/extract_sample_queries/wmcloud_queries/Tpsehw4tvwjzewbr_4.rq`
- `/Users/arwest/Hold/Wikidata-Queries/extract_sample_queries/wmcloud_queries/Txyifske8x1r0csm.rq`
- `/Users/arwest/Hold/Wikidata-Queries/extract_sample_queries/wmcloud_queries/Txyifske8x1r0csm_3.rq`
- `/Users/arwest/Hold/Wikidata-Queries/extract_sample_queries/wmcloud_queries/Ue1jymqb8r2t6rf0_3.rq`
- `/Users/arwest/Hold/Wikidata-Queries/extract_sample_queries/wmcloud_queries/Uk3e012ahtau6jn3_2.rq`
- `/Users/arwest/Hold/Wikidata-Queries/extract_sample_queries/wmcloud_queries/Uvbc552oqnevpp6t.rq`
- `/Users/arwest/Hold/Wikidata-Queries/extract_sample_queries/wmcloud_queries/V1fqumpmlosgrx2q_3.rq`
- `/Users/arwest/Hold/Wikidata-Queries/extract_sample_queries/wmcloud_queries/V1fqumpn8fc6vbnp_3.rq`
- `/Users/arwest/Hold/Wikidata-Queries/extract_sample_queries/wmcloud_queries/Wausi3w43zyj6v7t.rq`
- `/Users/arwest/Hold/Wikidata-Queries/extract_sample_queries/wmcloud_queries/WikiProject_European_Bathing_Waters.rq`
- `/Users/arwest/Hold/Wikidata-Queries/extract_sample_queries/wmcloud_queries/WikiProject_Netherlands_Public_LibrariesMaps_3.rq`
- `/Users/arwest/Hold/Wikidata-Queries/extract_sample_queries/wmcloud_queries/WikiProject_every_politicianWalesWikifying_the_Welsh_Assembly_3.rq`

### `wikibase:decodeUri()`

- Local matches: 291

Matching files:
- 291 matching files (not listed individually)

### `wikibase:isSomeValue()`

- Local matches: 37785

Matching files:
- 37785 matching files (not listed individually)

## SERVICE Extensions

- Features in this section: 7
- Total matches across this section: 397553

### `SERVICE wikibase:around`

- Local matches: 103

Matching files:
- 103 matching files (not listed individually)

### `SERVICE wikibase:box`

- Local matches: 17

Matching files:
- `/Users/arwest/Hold/Wikidata-Queries/extract_sample_queries/advanced_examples/Samples_with_coordinates_to_illustrate_maps/Schools_between_San_Jose_CA_and_Sacramento_CA.rq`
- `/Users/arwest/Hold/Wikidata-Queries/extract_sample_queries/wmcloud_queries/99of9_51.rq`
- `/Users/arwest/Hold/Wikidata-Queries/extract_sample_queries/wmcloud_queries/Andrew_Grayarchive_2.rq`
- `/Users/arwest/Hold/Wikidata-Queries/extract_sample_queries/wmcloud_queries/Andrew_Grayarchive_3.rq`
- `/Users/arwest/Hold/Wikidata-Queries/extract_sample_queries/wmcloud_queries/Antarctica.rq`
- `/Users/arwest/Hold/Wikidata-Queries/extract_sample_queries/wmcloud_queries/Antarctica_2.rq`
- `/Users/arwest/Hold/Wikidata-Queries/extract_sample_queries/wmcloud_queries/Antarctica_3.rq`
- `/Users/arwest/Hold/Wikidata-Queries/extract_sample_queries/wmcloud_queries/Antarctica_5.rq`
- `/Users/arwest/Hold/Wikidata-Queries/extract_sample_queries/wmcloud_queries/P17.rq`
- `/Users/arwest/Hold/Wikidata-Queries/extract_sample_queries/wmcloud_queries/P17_2.rq`
- `/Users/arwest/Hold/Wikidata-Queries/extract_sample_queries/wmcloud_queries/Property_proposalbounding_box.rq`
- `/Users/arwest/Hold/Wikidata-Queries/extract_sample_queries/wmcloud_queries/Property_proposalbounding_box_2.rq`
- `/Users/arwest/Hold/Wikidata-Queries/extract_sample_queries/wmcloud_queries/Thevrchriss.rq`
- `/Users/arwest/Hold/Wikidata-Queries/extract_sample_queries/wmcloud_queries/Utjtuppf80kqeeuw_2.rq`
- `/Users/arwest/Hold/Wikidata-Queries/extract_sample_queries/wmcloud_queries/WikiProject_FinlandVillages_and_islands_in_Aland_Islands.rq`
- `/Users/arwest/Hold/Wikidata-Queries/extract_sample_queries/wmcloud_queries/WikiProject_FinlandVillages_islands_and_lakes_in_Uusimaa.rq`
- `/Users/arwest/Hold/Wikidata-Queries/extract_sample_queries/wmcloud_queries/WikiProject_Netherlands_Public_LibrariesMaps_8.rq`

### `SERVICE wikibase:label`

- Local matches: 349510

Matching files:
- 349510 matching files (not listed individually)

### `SERVICE bd:slice`

- Local matches: 26

Matching files:
- `/Users/arwest/Hold/Wikidata-Queries/extract_sample_queries/wmcloud_queries/Database_reportsitems_with_P569_greater_than_P570_2.rq`
- `/Users/arwest/Hold/Wikidata-Queries/extract_sample_queries/wmcloud_queries/Database_reportsitems_with_P569_greater_than_P570_3.rq`
- `/Users/arwest/Hold/Wikidata-Queries/extract_sample_queries/wmcloud_queries/Database_reportsitems_with_P569_greater_than_P570_4.rq`
- `/Users/arwest/Hold/Wikidata-Queries/extract_sample_queries/wmcloud_queries/Database_reportsitems_with_P569_greater_than_P570_5.rq`
- `/Users/arwest/Hold/Wikidata-Queries/extract_sample_queries/wmcloud_queries/Database_reportsitems_with_P569_greater_than_P570_6.rq`
- `/Users/arwest/Hold/Wikidata-Queries/extract_sample_queries/wmcloud_queries/Database_reportsitems_with_P569_greater_than_P570_7.rq`
- `/Users/arwest/Hold/Wikidata-Queries/extract_sample_queries/wmcloud_queries/Database_reportsitems_with_P569_greater_than_P570_8.rq`
- `/Users/arwest/Hold/Wikidata-Queries/extract_sample_queries/wmcloud_queries/Database_reportsunmarked_supercentenarians_10.rq`
- `/Users/arwest/Hold/Wikidata-Queries/extract_sample_queries/wmcloud_queries/Database_reportsunmarked_supercentenarians_11.rq`
- `/Users/arwest/Hold/Wikidata-Queries/extract_sample_queries/wmcloud_queries/Database_reportsunmarked_supercentenarians_12.rq`
- `/Users/arwest/Hold/Wikidata-Queries/extract_sample_queries/wmcloud_queries/Database_reportsunmarked_supercentenarians_13.rq`
- `/Users/arwest/Hold/Wikidata-Queries/extract_sample_queries/wmcloud_queries/Database_reportsunmarked_supercentenarians_14.rq`
- `/Users/arwest/Hold/Wikidata-Queries/extract_sample_queries/wmcloud_queries/Database_reportsunmarked_supercentenarians_2.rq`
- `/Users/arwest/Hold/Wikidata-Queries/extract_sample_queries/wmcloud_queries/Database_reportsunmarked_supercentenarians_3.rq`
- `/Users/arwest/Hold/Wikidata-Queries/extract_sample_queries/wmcloud_queries/Database_reportsunmarked_supercentenarians_4.rq`
- `/Users/arwest/Hold/Wikidata-Queries/extract_sample_queries/wmcloud_queries/Database_reportsunmarked_supercentenarians_5.rq`
- `/Users/arwest/Hold/Wikidata-Queries/extract_sample_queries/wmcloud_queries/Database_reportsunmarked_supercentenarians_6.rq`
- `/Users/arwest/Hold/Wikidata-Queries/extract_sample_queries/wmcloud_queries/Database_reportsunmarked_supercentenarians_7.rq`
- `/Users/arwest/Hold/Wikidata-Queries/extract_sample_queries/wmcloud_queries/Database_reportsunmarked_supercentenarians_8.rq`
- `/Users/arwest/Hold/Wikidata-Queries/extract_sample_queries/wmcloud_queries/Database_reportsunmarked_supercentenarians_9.rq`
- `/Users/arwest/Hold/Wikidata-Queries/extract_sample_queries/wmcloud_queries/Tagishsimon_127.rq`
- `/Users/arwest/Hold/Wikidata-Queries/extract_sample_queries/wmcloud_queries/Tagishsimon_128.rq`
- `/Users/arwest/Hold/Wikidata-Queries/extract_sample_queries/wmcloud_queries/Tagishsimon_133.rq`
- `/Users/arwest/Hold/Wikidata-Queries/extract_sample_queries/wmcloud_queries/Tagishsimon_152.rq`
- `/Users/arwest/Hold/Wikidata-Queries/extract_sample_queries/wmcloud_queries/Tagishsimon_155.rq`
- `/Users/arwest/Hold/Wikidata-Queries/extract_sample_queries/wmcloud_queries/WdRyan_3.rq`

### `SERVICE wikibase:mwapi`

- Local matches: 35421

Matching files:
- 35421 matching files (not listed individually)

### `SERVICE gas:service`

- Local matches: 722

Matching files:
- 722 matching files (not listed individually)

### `SERVICE bd:sample`

- Local matches: 11754

Matching files:
- 11754 matching files (not listed individually)

## Supporting Blazegraph-Specific Syntax

- Features in this section: 1
- Total matches across this section: 60489

### `hint:Query ...` query hints

- Local matches: 60489

Matching files:
- 60489 matching files (not listed individually)

