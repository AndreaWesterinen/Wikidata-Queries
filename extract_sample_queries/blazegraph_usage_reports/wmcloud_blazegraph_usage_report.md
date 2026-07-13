# Blazegraph Feature Usage Report: WMCloud Queries

Source page reviewed: `https://wikitech.wikimedia.org/wiki/User:AWesterinen/Blazegraph_Features_and_Capabilities`

Scanned local example queries: `482255` `.rq` files under `/Users/arwest/Hold/Wikidata-Queries/extract_sample_queries/wmcloud_queries`.

This report uses the feature inventory described on the referenced Wikitech page, then maps each feature to matching files in the local example trees.

## Summary Table

| Section | Feature | Matches |
| --- | --- | ---: |
| Blazegraph Features | Named sub-queries (`WITH { ... } AS %name`, `INCLUDE %name`) | 68469 |
| Function Extensions | `geof:globe()` | 0 |
| Function Extensions | `geof:latitude()` | 7 |
| Function Extensions | `geof:longitude()` | 6 |
| Function Extensions | `geof:distance()` | 66 |
| Function Extensions | `wikibase:decodeUri()` | 289 |
| SERVICE Extensions | `SERVICE wikibase:around` | 95 |
| SERVICE Extensions | `SERVICE wikibase:box` | 16 |
| SERVICE Extensions | `SERVICE wikibase:label` | 349084 |
| SERVICE Extensions | `SERVICE bd:slice` | 26 |
| SERVICE Extensions | `SERVICE wikibase:mwapi` | 35411 |
| SERVICE Extensions | `SERVICE gas:service` | 719 |
| SERVICE Extensions | `SERVICE bd:sample` | 11753 |
| Supporting Blazegraph-Specific Syntax | `hint:Query ...` query hints | 60468 |

## Miscellaneous

| Category | Detail | Matches |
| --- | --- | ---: |
| Wikidata RDF Pseudo-Value | `wikibase:someValue` | 0 |
| Wikidata RDF Predicates | `wikibase:geoGlobe` | 16 |
| Federated SERVICE endpoint | `https://qlever.dev/api/wikimedia-commons` | 0 |
| Federated SERVICE endpoint | Other `SERVICE <...>` endpoint | 97 |
| `wikibase:api` value | `Generator` | 22595 |
| `wikibase:api` value | `Categories` | 6 |
| `wikibase:api` value | `Search` | 996 |
| `wikibase:api` value | `EntitySearch` | 3 |
| `mwapi:generator` value for `wikibase:api` `Generator` | `allpages` | 22351 |
| `mwapi:generator` value for `wikibase:api` `Generator` | `categorymembers` | 21 |
| `mwapi:generator` value for `wikibase:api` `Generator` | `links` | 3 |
| `mwapi:generator` value for `wikibase:api` `Generator` | `random` | 10 |
| `mwapi:generator` value for `wikibase:api` `Generator` | `recentchanges` | 1 |
| `mwapi:generator` value for `wikibase:api` `Generator` | `search` | 212 |

### Wikidata RDF Pseudo-Value: `wikibase:someValue`

- Local matches: 0

Matching files:
- None in the current example trees

### Wikidata RDF Predicates: `wikibase:geoGlobe`

- Local matches: 16

Matching files:
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

- Local matches: 97

Matching files:
- `/Users/arwest/Hold/Wikidata-Queries/extract_sample_queries/wmcloud_queries/99of9_47.rq`
- `/Users/arwest/Hold/Wikidata-Queries/extract_sample_queries/wmcloud_queries/CamelCaseNickStolpersteineHamburgAltona.rq`
- `/Users/arwest/Hold/Wikidata-Queries/extract_sample_queries/wmcloud_queries/CamelCaseNickStolpersteineHamburgBergedorf.rq`
- `/Users/arwest/Hold/Wikidata-Queries/extract_sample_queries/wmcloud_queries/CamelCaseNickStolpersteineHamburgEimsbuttel.rq`
- `/Users/arwest/Hold/Wikidata-Queries/extract_sample_queries/wmcloud_queries/CamelCaseNickStolpersteineHamburgHarburg.rq`
- `/Users/arwest/Hold/Wikidata-Queries/extract_sample_queries/wmcloud_queries/CamelCaseNickStolpersteineHamburgMitte.rq`
- `/Users/arwest/Hold/Wikidata-Queries/extract_sample_queries/wmcloud_queries/CamelCaseNickStolpersteineHamburgNord.rq`
- `/Users/arwest/Hold/Wikidata-Queries/extract_sample_queries/wmcloud_queries/CamelCaseNickStolpersteineHamburgWandsbek.rq`
- `/Users/arwest/Hold/Wikidata-Queries/extract_sample_queries/wmcloud_queries/Charles_MatthewsQueries_66.rq`
- `/Users/arwest/Hold/Wikidata-Queries/extract_sample_queries/wmcloud_queries/Charles_MatthewsQueries_69.rq`
- `/Users/arwest/Hold/Wikidata-Queries/extract_sample_queries/wmcloud_queries/GhuronArchives2019_2.rq`
- `/Users/arwest/Hold/Wikidata-Queries/extract_sample_queries/wmcloud_queries/MultichillKampen.rq`
- `/Users/arwest/Hold/Wikidata-Queries/extract_sample_queries/wmcloud_queries/MultichillMissende_kampen.rq`
- `/Users/arwest/Hold/Wikidata-Queries/extract_sample_queries/wmcloud_queries/MultichillRijksmonumentcomplex_missing_links.rq`
- `/Users/arwest/Hold/Wikidata-Queries/extract_sample_queries/wmcloud_queries/NikkiTGN.rq`
- `/Users/arwest/Hold/Wikidata-Queries/extract_sample_queries/wmcloud_queries/NikkiTGN_2.rq`
- `/Users/arwest/Hold/Wikidata-Queries/extract_sample_queries/wmcloud_queries/NikkiTGN_3.rq`
- `/Users/arwest/Hold/Wikidata-Queries/extract_sample_queries/wmcloud_queries/NurniRPON_12.rq`
- `/Users/arwest/Hold/Wikidata-Queries/extract_sample_queries/wmcloud_queries/NurniRPON_13.rq`
- `/Users/arwest/Hold/Wikidata-Queries/extract_sample_queries/wmcloud_queries/NurniRPON_15.rq`
- `/Users/arwest/Hold/Wikidata-Queries/extract_sample_queries/wmcloud_queries/NurniRPON_20.rq`
- `/Users/arwest/Hold/Wikidata-Queries/extract_sample_queries/wmcloud_queries/NurniRPON_30.rq`
- `/Users/arwest/Hold/Wikidata-Queries/extract_sample_queries/wmcloud_queries/NurniRPON_31.rq`
- `/Users/arwest/Hold/Wikidata-Queries/extract_sample_queries/wmcloud_queries/NurniRPON_34.rq`
- `/Users/arwest/Hold/Wikidata-Queries/extract_sample_queries/wmcloud_queries/NurniRPON_35.rq`
- `/Users/arwest/Hold/Wikidata-Queries/extract_sample_queries/wmcloud_queries/NurniRPON_36.rq`
- `/Users/arwest/Hold/Wikidata-Queries/extract_sample_queries/wmcloud_queries/NurniRPON_39.rq`
- `/Users/arwest/Hold/Wikidata-Queries/extract_sample_queries/wmcloud_queries/P1006Mismatches.rq`
- `/Users/arwest/Hold/Wikidata-Queries/extract_sample_queries/wmcloud_queries/P245_36.rq`
- `/Users/arwest/Hold/Wikidata-Queries/extract_sample_queries/wmcloud_queries/P245_5.rq`
- `/Users/arwest/Hold/Wikidata-Queries/extract_sample_queries/wmcloud_queries/P245_6.rq`
- `/Users/arwest/Hold/Wikidata-Queries/extract_sample_queries/wmcloud_queries/P7135.rq`
- `/Users/arwest/Hold/Wikidata-Queries/extract_sample_queries/wmcloud_queries/P7135_2.rq`
- `/Users/arwest/Hold/Wikidata-Queries/extract_sample_queries/wmcloud_queries/P981_22.rq`
- `/Users/arwest/Hold/Wikidata-Queries/extract_sample_queries/wmcloud_queries/Project_chatArchive201810_3.rq`
- `/Users/arwest/Hold/Wikidata-Queries/extract_sample_queries/wmcloud_queries/Project_chatArchive201810_4.rq`
- `/Users/arwest/Hold/Wikidata-Queries/extract_sample_queries/wmcloud_queries/Project_chatArchive201811_10.rq`
- `/Users/arwest/Hold/Wikidata-Queries/extract_sample_queries/wmcloud_queries/Project_chatArchive201903_6.rq`
- `/Users/arwest/Hold/Wikidata-Queries/extract_sample_queries/wmcloud_queries/Project_chatArchive201904.rq`
- `/Users/arwest/Hold/Wikidata-Queries/extract_sample_queries/wmcloud_queries/Project_chatArchive202007_2.rq`
- `/Users/arwest/Hold/Wikidata-Queries/extract_sample_queries/wmcloud_queries/Properties_for_deletionArchive2019_6.rq`
- `/Users/arwest/Hold/Wikidata-Queries/extract_sample_queries/wmcloud_queries/Request_a_queryArchive201902_7.rq`
- `/Users/arwest/Hold/Wikidata-Queries/extract_sample_queries/wmcloud_queries/Request_a_queryArchive201904_2.rq`
- `/Users/arwest/Hold/Wikidata-Queries/extract_sample_queries/wmcloud_queries/Request_a_queryArchive201904_3.rq`
- `/Users/arwest/Hold/Wikidata-Queries/extract_sample_queries/wmcloud_queries/Requests_for_permissionsBotEuropeanCommissionBot_1.rq`
- `/Users/arwest/Hold/Wikidata-Queries/extract_sample_queries/wmcloud_queries/SPARQL_federation_input_3.rq`
- `/Users/arwest/Hold/Wikidata-Queries/extract_sample_queries/wmcloud_queries/SPARQL_query_serviceFederation_report.rq`
- `/Users/arwest/Hold/Wikidata-Queries/extract_sample_queries/wmcloud_queries/Salgo60ListeriaNobelData.rq`
- `/Users/arwest/Hold/Wikidata-Queries/extract_sample_queries/wmcloud_queries/Salgo60ListeriaNobelData2.rq`
- `/Users/arwest/Hold/Wikidata-Queries/extract_sample_queries/wmcloud_queries/Salgo60ListeriaNobelData2_2.rq`
- `/Users/arwest/Hold/Wikidata-Queries/extract_sample_queries/wmcloud_queries/Salgo60ListeriaNobelData2_3.rq`
- `/Users/arwest/Hold/Wikidata-Queries/extract_sample_queries/wmcloud_queries/Salgo60ListeriaNobelData2_4.rq`
- `/Users/arwest/Hold/Wikidata-Queries/extract_sample_queries/wmcloud_queries/Salgo60ListeriaNobelData2_5.rq`
- `/Users/arwest/Hold/Wikidata-Queries/extract_sample_queries/wmcloud_queries/Salgo60ListeriaNobelData3.rq`
- `/Users/arwest/Hold/Wikidata-Queries/extract_sample_queries/wmcloud_queries/Salgo60ListeriaNobelData3_2.rq`
- `/Users/arwest/Hold/Wikidata-Queries/extract_sample_queries/wmcloud_queries/Salgo60ListeriaNobelData4.rq`
- `/Users/arwest/Hold/Wikidata-Queries/extract_sample_queries/wmcloud_queries/Salgo60ListeriaNobelData_2.rq`
- `/Users/arwest/Hold/Wikidata-Queries/extract_sample_queries/wmcloud_queries/Salgo60ListeriaNobelData_4.rq`
- `/Users/arwest/Hold/Wikidata-Queries/extract_sample_queries/wmcloud_queries/Salgo60ListeriaNobelData_5.rq`
- `/Users/arwest/Hold/Wikidata-Queries/extract_sample_queries/wmcloud_queries/Salgo60ListeriaNobelData_6.rq`
- `/Users/arwest/Hold/Wikidata-Queries/extract_sample_queries/wmcloud_queries/Salgo60ListeriaNobelData_7.rq`
- `/Users/arwest/Hold/Wikidata-Queries/extract_sample_queries/wmcloud_queries/ScienceSource_projectScienceSourceIngest_notebook.rq`
- `/Users/arwest/Hold/Wikidata-Queries/extract_sample_queries/wmcloud_queries/Senator2029Archive_2_5.rq`
- `/Users/arwest/Hold/Wikidata-Queries/extract_sample_queries/wmcloud_queries/Senator2029Archive_2_9.rq`
- `/Users/arwest/Hold/Wikidata-Queries/extract_sample_queries/wmcloud_queries/Status_updates2017_07_17_4.rq`
- `/Users/arwest/Hold/Wikidata-Queries/extract_sample_queries/wmcloud_queries/Status_updates2017_09_04_2.rq`
- `/Users/arwest/Hold/Wikidata-Queries/extract_sample_queries/wmcloud_queries/Status_updates2018_08_20.rq`
- `/Users/arwest/Hold/Wikidata-Queries/extract_sample_queries/wmcloud_queries/Status_updates2018_08_20_6.rq`
- `/Users/arwest/Hold/Wikidata-Queries/extract_sample_queries/wmcloud_queries/Status_updates2018_10_08_2.rq`
- `/Users/arwest/Hold/Wikidata-Queries/extract_sample_queries/wmcloud_queries/Status_updates2018_10_15_2.rq`
- `/Users/arwest/Hold/Wikidata-Queries/extract_sample_queries/wmcloud_queries/Status_updates2019_02_25_2.rq`
- `/Users/arwest/Hold/Wikidata-Queries/extract_sample_queries/wmcloud_queries/Status_updates2019_03_11_7.rq`
- `/Users/arwest/Hold/Wikidata-Queries/extract_sample_queries/wmcloud_queries/Status_updates2019_05_06.rq`
- `/Users/arwest/Hold/Wikidata-Queries/extract_sample_queries/wmcloud_queries/Status_updates2019_06_10.rq`
- `/Users/arwest/Hold/Wikidata-Queries/extract_sample_queries/wmcloud_queries/Status_updates2019_06_10_2.rq`
- `/Users/arwest/Hold/Wikidata-Queries/extract_sample_queries/wmcloud_queries/Tagishsimon_43.rq`
- `/Users/arwest/Hold/Wikidata-Queries/extract_sample_queries/wmcloud_queries/Tagishsimon_44.rq`
- `/Users/arwest/Hold/Wikidata-Queries/extract_sample_queries/wmcloud_queries/Tuhna9a1j01kadj0_4.rq`
- `/Users/arwest/Hold/Wikidata-Queries/extract_sample_queries/wmcloud_queries/Txixwssuzxvnr7vz_2.rq`
- `/Users/arwest/Hold/Wikidata-Queries/extract_sample_queries/wmcloud_queries/Uj6jbz90ryua8uon.rq`
- `/Users/arwest/Hold/Wikidata-Queries/extract_sample_queries/wmcloud_queries/Uj6jbz90ryua8uon_5.rq`
- `/Users/arwest/Hold/Wikidata-Queries/extract_sample_queries/wmcloud_queries/Um9onijmqglsrc9y_2.rq`
- `/Users/arwest/Hold/Wikidata-Queries/extract_sample_queries/wmcloud_queries/Umnaj9hiixrk13xb_2.rq`
- `/Users/arwest/Hold/Wikidata-Queries/extract_sample_queries/wmcloud_queries/Uuvpkyxjdog22qjc_2.rq`
- `/Users/arwest/Hold/Wikidata-Queries/extract_sample_queries/wmcloud_queries/Uvqw5w1ideq1lxj3_7.rq`
- `/Users/arwest/Hold/Wikidata-Queries/extract_sample_queries/wmcloud_queries/V1fqumpmlosgrx2q.rq`
- `/Users/arwest/Hold/Wikidata-Queries/extract_sample_queries/wmcloud_queries/V1fqumpmlosgrx2q_3.rq`
- `/Users/arwest/Hold/Wikidata-Queries/extract_sample_queries/wmcloud_queries/V1fqumpn8fc6vbnp.rq`
- `/Users/arwest/Hold/Wikidata-Queries/extract_sample_queries/wmcloud_queries/V1fqumpn8fc6vbnp_3.rq`
- `/Users/arwest/Hold/Wikidata-Queries/extract_sample_queries/wmcloud_queries/WikiProject_Alba_amicorum_National_Library_of_the_NetherlandsSource_dataEuropeana.rq`
- `/Users/arwest/Hold/Wikidata-Queries/extract_sample_queries/wmcloud_queries/WikiProject_Cultural_heritageReportsWLM_on_WD_Italy_32.rq`
- `/Users/arwest/Hold/Wikidata-Queries/extract_sample_queries/wmcloud_queries/WikiProject_Cultural_heritageReportsWLM_on_WD_Italy_33.rq`
- `/Users/arwest/Hold/Wikidata-Queries/extract_sample_queries/wmcloud_queries/WikiProject_Dutch_Literary_AwardsAuthorsPierre_Kemp_14.rq`
- `/Users/arwest/Hold/Wikidata-Queries/extract_sample_queries/wmcloud_queries/WikiProject_Dutch_Literary_AwardsAuthorsTheun_de_Vries_3.rq`
- `/Users/arwest/Hold/Wikidata-Queries/extract_sample_queries/wmcloud_queries/WikiProject_Dutch_National_Thesaurus_for_Author_NamesApproachStep2_8.rq`
- `/Users/arwest/Hold/Wikidata-Queries/extract_sample_queries/wmcloud_queries/WikiProject_Figure_skatingTodo_43.rq`
- `/Users/arwest/Hold/Wikidata-Queries/extract_sample_queries/wmcloud_queries/Wikidata_at_the_34th_Chaos_Communication_Congress_10.rq`

### `wikibase:api` value: `Generator`

- Local matches: 22595

Matching files:
- 22595 matching files (not listed individually)

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

- Local matches: 996

Matching files:
- 996 matching files (not listed individually)

### `wikibase:api` value: `EntitySearch`

- Local matches: 3

Matching files:
- `/Users/arwest/Hold/Wikidata-Queries/extract_sample_queries/wmcloud_queries/Project_chatArchive201908_4.rq`
- `/Users/arwest/Hold/Wikidata-Queries/extract_sample_queries/wmcloud_queries/Request_a_queryArchive201706_8.rq`
- `/Users/arwest/Hold/Wikidata-Queries/extract_sample_queries/wmcloud_queries/Request_a_queryArchive202210_2.rq`

### `mwapi:generator` value for `wikibase:api` `Generator`: `allpages`

- Local matches: 22351

Matching files:
- 22351 matching files (not listed individually)

### `mwapi:generator` value for `wikibase:api` `Generator`: `categorymembers`

- Local matches: 21

Matching files:
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

### `mwapi:generator` value for `wikibase:api` `Generator`: `search`

- Local matches: 212

Matching files:
- 212 matching files (not listed individually)

## Blazegraph Features

- Features in this section: 1
- Total matches across this section: 68469

### Named sub-queries (`WITH { ... } AS %name`, `INCLUDE %name`)

- Local matches: 68469

Matching files:
- 68469 matching files (not listed individually)

## Function Extensions

- Features in this section: 5
- Total matches across this section: 368

### `geof:globe()`

- Local matches: 0

Matching files:
- None in the current example trees

### `geof:latitude()`

- Local matches: 7

Matching files:
- `/Users/arwest/Hold/Wikidata-Queries/extract_sample_queries/wmcloud_queries/Charles_MatthewsQueries_50.rq`
- `/Users/arwest/Hold/Wikidata-Queries/extract_sample_queries/wmcloud_queries/EventsWikidata_Zurich_Training2019Showcase_queries.rq`
- `/Users/arwest/Hold/Wikidata-Queries/extract_sample_queries/wmcloud_queries/EventsWikidata_Zurich_Training2019Showcase_queries_2.rq`
- `/Users/arwest/Hold/Wikidata-Queries/extract_sample_queries/wmcloud_queries/Status_updates2017_09_18_2.rq`
- `/Users/arwest/Hold/Wikidata-Queries/extract_sample_queries/wmcloud_queries/TweetsFactsAndQueriesvshlaeumqueries.rq`
- `/Users/arwest/Hold/Wikidata-Queries/extract_sample_queries/wmcloud_queries/WikiProject_Netherlands_Public_LibrariesMaps_5.rq`
- `/Users/arwest/Hold/Wikidata-Queries/extract_sample_queries/wmcloud_queries/WikiProject_Netherlands_Public_LibrariesMaps_7.rq`

### `geof:longitude()`

- Local matches: 6

Matching files:
- `/Users/arwest/Hold/Wikidata-Queries/extract_sample_queries/wmcloud_queries/Charles_MatthewsQueries_50.rq`
- `/Users/arwest/Hold/Wikidata-Queries/extract_sample_queries/wmcloud_queries/EventsWikidata_Zurich_Training2019Showcase_queries.rq`
- `/Users/arwest/Hold/Wikidata-Queries/extract_sample_queries/wmcloud_queries/EventsWikidata_Zurich_Training2019Showcase_queries_2.rq`
- `/Users/arwest/Hold/Wikidata-Queries/extract_sample_queries/wmcloud_queries/Status_updates2017_09_18_2.rq`
- `/Users/arwest/Hold/Wikidata-Queries/extract_sample_queries/wmcloud_queries/TweetsFactsAndQueriesvshlaeumqueries.rq`
- `/Users/arwest/Hold/Wikidata-Queries/extract_sample_queries/wmcloud_queries/WikiProject_Netherlands_Public_LibrariesMaps_6.rq`

### `geof:distance()`

- Local matches: 66

Matching files:
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

- Local matches: 289

Matching files:
- 289 matching files (not listed individually)

## SERVICE Extensions

- Features in this section: 7
- Total matches across this section: 397104

### `SERVICE wikibase:around`

- Local matches: 95

Matching files:
- `/Users/arwest/Hold/Wikidata-Queries/extract_sample_queries/wmcloud_queries/99of9_50.rq`
- `/Users/arwest/Hold/Wikidata-Queries/extract_sample_queries/wmcloud_queries/99of9_51.rq`
- `/Users/arwest/Hold/Wikidata-Queries/extract_sample_queries/wmcloud_queries/99of9_55.rq`
- `/Users/arwest/Hold/Wikidata-Queries/extract_sample_queries/wmcloud_queries/99of9_56.rq`
- `/Users/arwest/Hold/Wikidata-Queries/extract_sample_queries/wmcloud_queries/99of9_57.rq`
- `/Users/arwest/Hold/Wikidata-Queries/extract_sample_queries/wmcloud_queries/99of9_58.rq`
- `/Users/arwest/Hold/Wikidata-Queries/extract_sample_queries/wmcloud_queries/99of9_59.rq`
- `/Users/arwest/Hold/Wikidata-Queries/extract_sample_queries/wmcloud_queries/99of9_86.rq`
- `/Users/arwest/Hold/Wikidata-Queries/extract_sample_queries/wmcloud_queries/Alexmar983.rq`
- `/Users/arwest/Hold/Wikidata-Queries/extract_sample_queries/wmcloud_queries/Asia.rq`
- `/Users/arwest/Hold/Wikidata-Queries/extract_sample_queries/wmcloud_queries/Benoit_Soubeyran.rq`
- `/Users/arwest/Hold/Wikidata-Queries/extract_sample_queries/wmcloud_queries/Benoit_Soubeyran_2.rq`
- `/Users/arwest/Hold/Wikidata-Queries/extract_sample_queries/wmcloud_queries/Bot_requestsArchive201611.rq`
- `/Users/arwest/Hold/Wikidata-Queries/extract_sample_queries/wmcloud_queries/Conny_9.rq`
- `/Users/arwest/Hold/Wikidata-Queries/extract_sample_queries/wmcloud_queries/Daniel_Mietchen_3.rq`
- `/Users/arwest/Hold/Wikidata-Queries/extract_sample_queries/wmcloud_queries/DiracHaitiQuisqueyafr.rq`
- `/Users/arwest/Hold/Wikidata-Queries/extract_sample_queries/wmcloud_queries/DnaX.rq`
- `/Users/arwest/Hold/Wikidata-Queries/extract_sample_queries/wmcloud_queries/Holapaco77.rq`
- `/Users/arwest/Hold/Wikidata-Queries/extract_sample_queries/wmcloud_queries/Jiri_Komarek.rq`
- `/Users/arwest/Hold/Wikidata-Queries/extract_sample_queries/wmcloud_queries/L0ll0.rq`
- `/Users/arwest/Hold/Wikidata-Queries/extract_sample_queries/wmcloud_queries/LukeWiller_2.rq`
- `/Users/arwest/Hold/Wikidata-Queries/extract_sample_queries/wmcloud_queries/Manuelarosi.rq`
- `/Users/arwest/Hold/Wikidata-Queries/extract_sample_queries/wmcloud_queries/Mezi_bajtyArchive2017.rq`
- `/Users/arwest/Hold/Wikidata-Queries/extract_sample_queries/wmcloud_queries/Nicola_Quirico.rq`
- `/Users/arwest/Hold/Wikidata-Queries/extract_sample_queries/wmcloud_queries/Nortix08.rq`
- `/Users/arwest/Hold/Wikidata-Queries/extract_sample_queries/wmcloud_queries/Nortix08Stolperstein_Query_3.rq`
- `/Users/arwest/Hold/Wikidata-Queries/extract_sample_queries/wmcloud_queries/P10271_27.rq`
- `/Users/arwest/Hold/Wikidata-Queries/extract_sample_queries/wmcloud_queries/P10271_28.rq`
- `/Users/arwest/Hold/Wikidata-Queries/extract_sample_queries/wmcloud_queries/P625_22.rq`
- `/Users/arwest/Hold/Wikidata-Queries/extract_sample_queries/wmcloud_queries/P625_23.rq`
- `/Users/arwest/Hold/Wikidata-Queries/extract_sample_queries/wmcloud_queries/P625_24.rq`
- `/Users/arwest/Hold/Wikidata-Queries/extract_sample_queries/wmcloud_queries/Pampuco.rq`
- `/Users/arwest/Hold/Wikidata-Queries/extract_sample_queries/wmcloud_queries/Paolobon140.rq`
- `/Users/arwest/Hold/Wikidata-Queries/extract_sample_queries/wmcloud_queries/PaucabotVisuals_19.rq`
- `/Users/arwest/Hold/Wikidata-Queries/extract_sample_queries/wmcloud_queries/PaucabotVisuals_26.rq`
- `/Users/arwest/Hold/Wikidata-Queries/extract_sample_queries/wmcloud_queries/PaucabotVisuals_6.rq`
- `/Users/arwest/Hold/Wikidata-Queries/extract_sample_queries/wmcloud_queries/PaucabotWork_15.rq`
- `/Users/arwest/Hold/Wikidata-Queries/extract_sample_queries/wmcloud_queries/PaucabotWork_16.rq`
- `/Users/arwest/Hold/Wikidata-Queries/extract_sample_queries/wmcloud_queries/Project_chatArchive201801_5.rq`
- `/Users/arwest/Hold/Wikidata-Queries/extract_sample_queries/wmcloud_queries/Project_chatArchive201807_10.rq`
- `/Users/arwest/Hold/Wikidata-Queries/extract_sample_queries/wmcloud_queries/Project_chatArchive202111.rq`
- `/Users/arwest/Hold/Wikidata-Queries/extract_sample_queries/wmcloud_queries/Property_proposalmonument_in_the_near.rq`
- `/Users/arwest/Hold/Wikidata-Queries/extract_sample_queries/wmcloud_queries/Property_proposalopening_hours_v3.rq`
- `/Users/arwest/Hold/Wikidata-Queries/extract_sample_queries/wmcloud_queries/Q100000001.rq`
- `/Users/arwest/Hold/Wikidata-Queries/extract_sample_queries/wmcloud_queries/Request_a_queryArchive201609_7.rq`
- `/Users/arwest/Hold/Wikidata-Queries/extract_sample_queries/wmcloud_queries/Request_a_queryArchive201702.rq`
- `/Users/arwest/Hold/Wikidata-Queries/extract_sample_queries/wmcloud_queries/Request_a_queryArchive201810_15.rq`
- `/Users/arwest/Hold/Wikidata-Queries/extract_sample_queries/wmcloud_queries/Request_a_queryArchive201903_5.rq`
- `/Users/arwest/Hold/Wikidata-Queries/extract_sample_queries/wmcloud_queries/Request_a_queryArchive201903_6.rq`
- `/Users/arwest/Hold/Wikidata-Queries/extract_sample_queries/wmcloud_queries/Request_a_queryArchive201903_7.rq`
- `/Users/arwest/Hold/Wikidata-Queries/extract_sample_queries/wmcloud_queries/Request_a_queryArchive202005.rq`
- `/Users/arwest/Hold/Wikidata-Queries/extract_sample_queries/wmcloud_queries/Requests_for_deletionsArchive20160529.rq`
- `/Users/arwest/Hold/Wikidata-Queries/extract_sample_queries/wmcloud_queries/Requests_for_deletionsArchive20200620.rq`
- `/Users/arwest/Hold/Wikidata-Queries/extract_sample_queries/wmcloud_queries/SPARQL_query_serviceWikidata_Query_HelpResult_Views_5.rq`
- `/Users/arwest/Hold/Wikidata-Queries/extract_sample_queries/wmcloud_queries/SPARQL_query_serviceWikidata_Query_HelpResult_Views_6.rq`
- `/Users/arwest/Hold/Wikidata-Queries/extract_sample_queries/wmcloud_queries/Salgo60Gamla_stan.rq`
- `/Users/arwest/Hold/Wikidata-Queries/extract_sample_queries/wmcloud_queries/Salgo60Visby.rq`
- `/Users/arwest/Hold/Wikidata-Queries/extract_sample_queries/wmcloud_queries/Status_updates2016_05_14_3.rq`
- `/Users/arwest/Hold/Wikidata-Queries/extract_sample_queries/wmcloud_queries/Status_updates2016_06_18_2.rq`
- `/Users/arwest/Hold/Wikidata-Queries/extract_sample_queries/wmcloud_queries/Status_updates2016_10_08_4.rq`
- `/Users/arwest/Hold/Wikidata-Queries/extract_sample_queries/wmcloud_queries/Status_updates2017_09_11_2.rq`
- `/Users/arwest/Hold/Wikidata-Queries/extract_sample_queries/wmcloud_queries/Status_updates2017_11_13_2.rq`
- `/Users/arwest/Hold/Wikidata-Queries/extract_sample_queries/wmcloud_queries/Status_updates2018_05_14.rq`
- `/Users/arwest/Hold/Wikidata-Queries/extract_sample_queries/wmcloud_queries/Status_updates2018_05_14_2.rq`
- `/Users/arwest/Hold/Wikidata-Queries/extract_sample_queries/wmcloud_queries/Status_updates2018_05_21_4.rq`
- `/Users/arwest/Hold/Wikidata-Queries/extract_sample_queries/wmcloud_queries/Status_updates2018_06_04_3.rq`
- `/Users/arwest/Hold/Wikidata-Queries/extract_sample_queries/wmcloud_queries/Status_updates2018_07_09_5.rq`
- `/Users/arwest/Hold/Wikidata-Queries/extract_sample_queries/wmcloud_queries/Status_updates2018_12_10.rq`
- `/Users/arwest/Hold/Wikidata-Queries/extract_sample_queries/wmcloud_queries/Syced_4.rq`
- `/Users/arwest/Hold/Wikidata-Queries/extract_sample_queries/wmcloud_queries/T42pps9ezjnn4p9y_3.rq`
- `/Users/arwest/Hold/Wikidata-Queries/extract_sample_queries/wmcloud_queries/T68ew9dhb05axtn6_2.rq`
- `/Users/arwest/Hold/Wikidata-Queries/extract_sample_queries/wmcloud_queries/Tagishsimon_154.rq`
- `/Users/arwest/Hold/Wikidata-Queries/extract_sample_queries/wmcloud_queries/Tagishsimon_46.rq`
- `/Users/arwest/Hold/Wikidata-Queries/extract_sample_queries/wmcloud_queries/Tagishsimon_52.rq`
- `/Users/arwest/Hold/Wikidata-Queries/extract_sample_queries/wmcloud_queries/Tagishsimon_67.rq`
- `/Users/arwest/Hold/Wikidata-Queries/extract_sample_queries/wmcloud_queries/Tagishsimon_86.rq`
- `/Users/arwest/Hold/Wikidata-Queries/extract_sample_queries/wmcloud_queries/Tagishsimon_95.rq`
- `/Users/arwest/Hold/Wikidata-Queries/extract_sample_queries/wmcloud_queries/Td65rdfyzv8hmhm7_4.rq`
- `/Users/arwest/Hold/Wikidata-Queries/extract_sample_queries/wmcloud_queries/Txyifske8x1r0csm.rq`
- `/Users/arwest/Hold/Wikidata-Queries/extract_sample_queries/wmcloud_queries/U1utwpo0y8l84zza_2.rq`
- `/Users/arwest/Hold/Wikidata-Queries/extract_sample_queries/wmcloud_queries/Ud4a97c3og509cyk.rq`
- `/Users/arwest/Hold/Wikidata-Queries/extract_sample_queries/wmcloud_queries/Ud4a97c3og509cyk_2.rq`
- `/Users/arwest/Hold/Wikidata-Queries/extract_sample_queries/wmcloud_queries/Udld74ie5zb30w9h_4.rq`
- `/Users/arwest/Hold/Wikidata-Queries/extract_sample_queries/wmcloud_queries/Uef2ivzfa6xpss2n_2.rq`
- `/Users/arwest/Hold/Wikidata-Queries/extract_sample_queries/wmcloud_queries/Ugmoa7208bwsjzen_5.rq`
- `/Users/arwest/Hold/Wikidata-Queries/extract_sample_queries/wmcloud_queries/Unkiksh9h0k8vmsc.rq`
- `/Users/arwest/Hold/Wikidata-Queries/extract_sample_queries/wmcloud_queries/Upozl10vt4ljuzm1.rq`
- `/Users/arwest/Hold/Wikidata-Queries/extract_sample_queries/wmcloud_queries/Uq471ncru2dspwt1.rq`
- `/Users/arwest/Hold/Wikidata-Queries/extract_sample_queries/wmcloud_queries/VIGNERONCeltic_Knot_Translathon_2.rq`
- `/Users/arwest/Hold/Wikidata-Queries/extract_sample_queries/wmcloud_queries/Vitplister_4.rq`
- `/Users/arwest/Hold/Wikidata-Queries/extract_sample_queries/wmcloud_queries/WikiProject_Netherlands_Public_LibrariesMaps_2.rq`
- `/Users/arwest/Hold/Wikidata-Queries/extract_sample_queries/wmcloud_queries/WikiProject_UK_and_IrelandScotland_2.rq`
- `/Users/arwest/Hold/Wikidata-Queries/extract_sample_queries/wmcloud_queries/WikiProject_Younger_Dryas_impact_hypothesis.rq`
- `/Users/arwest/Hold/Wikidata-Queries/extract_sample_queries/wmcloud_queries/WikiProject_sum_of_all_paintingsCzech_streets_named_after_painters.rq`
- `/Users/arwest/Hold/Wikidata-Queries/extract_sample_queries/wmcloud_queries/WikiProject_sum_of_all_paintingsDutch_streets_named_after_painters_2.rq`

### `SERVICE wikibase:box`

- Local matches: 16

Matching files:
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

- Local matches: 349084

Matching files:
- 349084 matching files (not listed individually)

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

- Local matches: 35411

Matching files:
- 35411 matching files (not listed individually)

### `SERVICE gas:service`

- Local matches: 719

Matching files:
- 719 matching files (not listed individually)

### `SERVICE bd:sample`

- Local matches: 11753

Matching files:
- 11753 matching files (not listed individually)

## Supporting Blazegraph-Specific Syntax

- Features in this section: 1
- Total matches across this section: 60468

### `hint:Query ...` query hints

- Local matches: 60468

Matching files:
- 60468 matching files (not listed individually)

