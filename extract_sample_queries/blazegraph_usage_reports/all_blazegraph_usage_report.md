# Blazegraph Feature Usage Report: All Queries

Source page reviewed: `https://wikitech.wikimedia.org/wiki/User:AWesterinen/Blazegraph_Features_and_Capabilities`

Scanned local example queries: `482883` `.rq` files under `/Users/arwest/Hold/Wikidata-Queries/extract_sample_queries/examples`, `/Users/arwest/Hold/Wikidata-Queries/extract_sample_queries/advanced_examples`, `/Users/arwest/Hold/Wikidata-Queries/extract_sample_queries/human_examples`, `/Users/arwest/Hold/Wikidata-Queries/extract_sample_queries/maintenance_examples`, `/Users/arwest/Hold/Wikidata-Queries/extract_sample_queries/commons_examples`, `/Users/arwest/Hold/Wikidata-Queries/extract_sample_queries/wmcloud_queries`, `/Users/arwest/Hold/Wikidata-Queries/extract_sample_queries/other_examples/phab_issues`, `/Users/arwest/Hold/Wikidata-Queries/extract_sample_queries/other_examples/submitted`.

This report uses the feature inventory described on the referenced Wikitech page, then maps each feature to matching files in the local example trees.

## Summary Table

| Section | Feature | Matches |
| --- | --- | ---: |
| Blazegraph Features | Named sub-queries (`WITH { ... } AS %name`, `INCLUDE %name`) | 68525 |
| Function Extensions | `geof:globe()` | 0 |
| Function Extensions | `geof:latitude()` | 8 |
| Function Extensions | `geof:longitude()` | 7 |
| Function Extensions | `geof:distance()` | 77 |
| Function Extensions | `wikibase:decodeUri()` | 294 |
| SERVICE Extensions | `SERVICE wikibase:around` | 104 |
| SERVICE Extensions | `SERVICE wikibase:box` | 17 |
| SERVICE Extensions | `SERVICE wikibase:label` | 349532 |
| SERVICE Extensions | `SERVICE bd:slice` | 27 |
| SERVICE Extensions | `SERVICE wikibase:mwapi` | 35427 |
| SERVICE Extensions | `SERVICE gas:service` | 722 |
| SERVICE Extensions | `SERVICE bd:sample` | 11759 |
| Supporting Blazegraph-Specific Syntax | `hint:Query ...` query hints | 60490 |
| Supporting Blazegraph-Specific Syntax | `bd:serviceParam` | 374309 |
| Supporting Blazegraph-Specific Syntax | `wikibase:someValue` | 0 |
| Supporting Blazegraph-Specific Syntax | `wikibase:geoGlobe` | 19 |
| Supporting Blazegraph-Specific Syntax | `wikibase:globe` | 0 |

## Blazegraph Features

- Features in this section: 1
- Total matches across this section: 68525

### Named sub-queries (`WITH { ... } AS %name`, `INCLUDE %name`)

- Local matches: 68525

Matching files:
- 68525 matching files (not listed individually)

## Function Extensions

- Features in this section: 5
- Total matches across this section: 386

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

- Local matches: 294

Matching files:
- 294 matching files (not listed individually)

## SERVICE Extensions

- Features in this section: 7
- Total matches across this section: 397588

### `SERVICE wikibase:around`

- Local matches: 104

Matching files:
- 104 matching files (not listed individually)

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

- Local matches: 349532

Matching files:
- 349532 matching files (not listed individually)

### `SERVICE bd:slice`

- Local matches: 27

Matching files:
- `/Users/arwest/Hold/Wikidata-Queries/extract_sample_queries/commons_examples/Uncategorized/Most_frequently_used_depicts_statements_from_a_slice_of_media_files_that_dont_have_translations_in_Spanish.rq`
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

- Local matches: 35427

Matching files:
- 35427 matching files (not listed individually)

### `SERVICE gas:service`

- Local matches: 722

Matching files:
- 722 matching files (not listed individually)

### `SERVICE bd:sample`

- Local matches: 11759

Matching files:
- 11759 matching files (not listed individually)

## Supporting Blazegraph-Specific Syntax

- Features in this section: 5
- Total matches across this section: 434818

### `hint:Query ...` query hints

- Local matches: 60490

Matching files:
- 60490 matching files (not listed individually)

### `bd:serviceParam`

- Local matches: 374309

Matching files:
- 374309 matching files (not listed individually)

### `wikibase:someValue`

- Local matches: 0

Matching files:
- None in the current example trees

### `wikibase:geoGlobe`

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

### `wikibase:globe`

- Local matches: 0

Matching files:
- None in the current example trees

