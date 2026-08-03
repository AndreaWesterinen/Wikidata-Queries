# Blazegraph Feature Usage Report: Wikimedia Queries

Source page reviewed: `https://wikitech.wikimedia.org/wiki/User:AWesterinen/Blazegraph_Features_and_Capabilities`

Scanned local example queries: `557` `.rq` files under `/Users/arwest/Hold/Wikidata-Queries/extract_sample_queries/examples`, `/Users/arwest/Hold/Wikidata-Queries/extract_sample_queries/advanced_examples`, `/Users/arwest/Hold/Wikidata-Queries/extract_sample_queries/human_examples`, `/Users/arwest/Hold/Wikidata-Queries/extract_sample_queries/maintenance_examples`.

This report uses the feature inventory described on the referenced Wikitech page, then maps each feature to matching files in the local example trees.

## Summary Table

| Section | Feature | Matches |
| --- | --- | ---: |
| Blazegraph Features | Named sub-queries (`WITH { ... } AS %name`, `INCLUDE %name`) | 19 |
| Function Extensions | `geof:globe()` | 0 |
| Function Extensions | `geof:latitude()` | 1 |
| Function Extensions | `geof:longitude()` | 1 |
| Function Extensions | `geof:distance()` | 11 |
| Function Extensions | `wikibase:decodeUri()` | 2 |
| Function Extensions | `wikibase:isSomeValue()` | 6 |
| SERVICE Extensions | `SERVICE wikibase:around` | 7 |
| SERVICE Extensions | `SERVICE wikibase:box` | 1 |
| SERVICE Extensions | `SERVICE wikibase:label` | 419 |
| SERVICE Extensions | `SERVICE bd:slice` | 0 |
| SERVICE Extensions | `SERVICE wikibase:mwapi` | 10 |
| SERVICE Extensions | `SERVICE gas:service` | 3 |
| SERVICE Extensions | `SERVICE bd:sample` | 1 |
| Supporting Blazegraph-Specific Syntax | `hint:Query ...` query hints | 20 |

## Miscellaneous

| Category | Detail | Matches |
| --- | --- | ---: |
| Wikidata RDF Pseudo-Value | `wikibase:someValue` | 0 |
| Wikidata RDF Predicates | `wikibase:geoGlobe` | 3 |
| Federated SERVICE endpoint | `https://qlever.dev/api/wikimedia-commons` | 0 |
| Federated SERVICE endpoint | Other `SERVICE <...>` endpoint | 10 |
| `wikibase:api` value | `Generator` | 4 |
| `wikibase:api` value | `Categories` | 0 |
| `wikibase:api` value | `Search` | 2 |
| `wikibase:api` value | `EntitySearch` | 0 |
| `mwapi:generator` value for `wikibase:api` `Generator` | `categorymembers` | 1 |
| `mwapi:generator` value for `wikibase:api` `Generator` | `exturlusage` | 1 |
| `mwapi:generator` value for `wikibase:api` `Generator` | `search` | 2 |

### Wikidata RDF Pseudo-Value: `wikibase:someValue`

- Local matches: 0

Matching files:
- None in the current example trees

### Wikidata RDF Predicates: `wikibase:geoGlobe`

- Local matches: 3

Matching files:
- `/Users/arwest/Hold/Wikidata-Queries/extract_sample_queries/examples/Geography/Globes_used_to_represent_coordinates.rq`
- `/Users/arwest/Hold/Wikidata-Queries/extract_sample_queries/examples/Mountains/Mons_mountains_with_coordinates_not_located_on_Earth.rq`
- `/Users/arwest/Hold/Wikidata-Queries/extract_sample_queries/maintenance_examples/Queries_for_maintenance/Objects_that_have_globe_which_does_not_match_their_coordinates.rq`

### Federated SERVICE endpoint: `https://qlever.dev/api/wikimedia-commons`

- Local matches: 0

Matching files:
- None in the current example trees

### Federated SERVICE endpoint: Other `SERVICE <...>` endpoint

- Local matches: 10

Matching files:
- `/Users/arwest/Hold/Wikidata-Queries/extract_sample_queries/examples/External_Federation_Queries_using_Wikidata_plus_Other_sources/Getting_basic_information_for_one_item.rq`
- `/Users/arwest/Hold/Wikidata-Queries/extract_sample_queries/examples/External_Federation_Queries_using_Wikidata_plus_Other_sources/UK_Parliament_constituencies_whose_official_point_location_is_more_than_10km_from_the_location_in_Wikidata.rq`
- `/Users/arwest/Hold/Wikidata-Queries/extract_sample_queries/examples/Geography/ATMs_around_Munich_belonging_to_the_BankcardServicenetz_interbank_network_federated_query.rq`
- `/Users/arwest/Hold/Wikidata-Queries/extract_sample_queries/examples/Metabolites/Metabolitemetabolite_interactions_mostly_conversions_and_their_pKa_change_federated_query.rq`
- `/Users/arwest/Hold/Wikidata-Queries/extract_sample_queries/examples/Pathways/Biomarkers_in_Wikidata_which_interact_with_proteins_in_human_pathways_from_Wikipathways.rq`
- `/Users/arwest/Hold/Wikidata-Queries/extract_sample_queries/examples/Pathways/Known_interaction_types_in_Wikipathways_for_a_pathway_with_Identifier_WP716_federated_query.rq`
- `/Users/arwest/Hold/Wikidata-Queries/extract_sample_queries/examples/Pathways/Local_annotations_from_Wikipathways_using_Federated_query_on_a_Pathway_with_identifier_WP716_federated_query.rq`
- `/Users/arwest/Hold/Wikidata-Queries/extract_sample_queries/examples/Science/Universities_ranked_by_PageRank_on_English_Wikipedia_federated_query.rq`
- `/Users/arwest/Hold/Wikidata-Queries/extract_sample_queries/advanced_examples/Federation/Get_OpenStreetMap_nodes_with_inarisami_label_in_wikipedia_but_without_inarisami_label_in_OSM.rq`
- `/Users/arwest/Hold/Wikidata-Queries/extract_sample_queries/advanced_examples/Federation/Get_information_of_Europeana_item.rq`

### `wikibase:api` value: `Generator`

- Local matches: 4

Matching files:
- `/Users/arwest/Hold/Wikidata-Queries/extract_sample_queries/examples/Zika_corpus/Scholarly_articles_with_Zika_in_the_item_label.rq`
- `/Users/arwest/Hold/Wikidata-Queries/extract_sample_queries/advanced_examples/Mediawiki_API/Find_statements_with_references_containing_external_links_to_wipoint.rq`
- `/Users/arwest/Hold/Wikidata-Queries/extract_sample_queries/advanced_examples/Mediawiki_API/Getting_pageviews_for_all_articles_in_a_category.rq`
- `/Users/arwest/Hold/Wikidata-Queries/extract_sample_queries/advanced_examples/Samples_with_coordinates_to_illustrate_maps/Map_of_newspapers_in_the_United_States_by_presence_of_Infobox_newspaper_in_their_English_Wikipedia_article.rq`

### `wikibase:api` value: `Categories`

- Local matches: 0

Matching files:
- None in the current example trees

### `wikibase:api` value: `Search`

- Local matches: 2

Matching files:
- `/Users/arwest/Hold/Wikidata-Queries/extract_sample_queries/maintenance_examples/Queries_for_maintenance/Labels_containing_HTML_escape_sequences.rq`
- `/Users/arwest/Hold/Wikidata-Queries/extract_sample_queries/maintenance_examples/Queries_for_maintenance/String_search_All_items_having_the_string_airport_without_having_instance_of_P31_nor_subclass_of_P279.rq`

### `wikibase:api` value: `EntitySearch`

- Local matches: 0

Matching files:
- None in the current example trees

### `mwapi:generator` value for `wikibase:api` `Generator`: `categorymembers`

- Local matches: 1

Matching files:
- `/Users/arwest/Hold/Wikidata-Queries/extract_sample_queries/advanced_examples/Mediawiki_API/Getting_pageviews_for_all_articles_in_a_category.rq`

### `mwapi:generator` value for `wikibase:api` `Generator`: `exturlusage`

- Local matches: 1

Matching files:
- `/Users/arwest/Hold/Wikidata-Queries/extract_sample_queries/advanced_examples/Mediawiki_API/Find_statements_with_references_containing_external_links_to_wipoint.rq`

### `mwapi:generator` value for `wikibase:api` `Generator`: `search`

- Local matches: 2

Matching files:
- `/Users/arwest/Hold/Wikidata-Queries/extract_sample_queries/examples/Zika_corpus/Scholarly_articles_with_Zika_in_the_item_label.rq`
- `/Users/arwest/Hold/Wikidata-Queries/extract_sample_queries/advanced_examples/Samples_with_coordinates_to_illustrate_maps/Map_of_newspapers_in_the_United_States_by_presence_of_Infobox_newspaper_in_their_English_Wikipedia_article.rq`

## Blazegraph Features

- Features in this section: 1
- Total matches across this section: 19

### Named sub-queries (`WITH { ... } AS %name`, `INCLUDE %name`)

- Local matches: 19

Matching files:
- `/Users/arwest/Hold/Wikidata-Queries/extract_sample_queries/examples/Chemistry/Solubilities_of_chemicals.rq`
- `/Users/arwest/Hold/Wikidata-Queries/extract_sample_queries/examples/Cities/Largest_cities_of_the_world.rq`
- `/Users/arwest/Hold/Wikidata-Queries/extract_sample_queries/examples/Fictional_characters/Fictional_characters_whose_birthdeath_date_is_in_the_current_decade.rq`
- `/Users/arwest/Hold/Wikidata-Queries/extract_sample_queries/examples/History/Most_prolific_fathers.rq`
- `/Users/arwest/Hold/Wikidata-Queries/extract_sample_queries/examples/Lexeme_queries/The_100_most_translated_concepts_in_the_Lexeme_namespace.rq`
- `/Users/arwest/Hold/Wikidata-Queries/extract_sample_queries/examples/Metabolites/Metabolitemetabolite_interactions_mostly_conversions_and_their_pKa_change_federated_query.rq`
- `/Users/arwest/Hold/Wikidata-Queries/extract_sample_queries/examples/Scientific_literature/Scientific_journals_with_editors_on_Twitter.rq`
- `/Users/arwest/Hold/Wikidata-Queries/extract_sample_queries/examples/Simple_queries/Items_about_authors_with_a_Wikispecies_page.rq`
- `/Users/arwest/Hold/Wikidata-Queries/extract_sample_queries/examples/Simple_queries/Items_with_a_Wikispecies_sitelink.rq`
- `/Users/arwest/Hold/Wikidata-Queries/extract_sample_queries/examples/Simple_queries/Recent_events.rq`
- `/Users/arwest/Hold/Wikidata-Queries/extract_sample_queries/examples/Space/Who_discovered_the_most_asteroids.rq`
- `/Users/arwest/Hold/Wikidata-Queries/extract_sample_queries/examples/Wikimedia_projects/People_deceased_in_2018_ordered_by_the_number_of_sitelinks.rq`
- `/Users/arwest/Hold/Wikidata-Queries/extract_sample_queries/examples/Zika_corpus/Scholarly_articles_with_Zika_in_the_item_label.rq`
- `/Users/arwest/Hold/Wikidata-Queries/extract_sample_queries/advanced_examples/Federation/Get_OpenStreetMap_nodes_with_inarisami_label_in_wikipedia_but_without_inarisami_label_in_OSM.rq`
- `/Users/arwest/Hold/Wikidata-Queries/extract_sample_queries/advanced_examples/Mediawiki_API/Find_statements_with_references_containing_external_links_to_wipoint.rq`
- `/Users/arwest/Hold/Wikidata-Queries/extract_sample_queries/advanced_examples/Showcase_Queries/All_oldest_living_US_expresidents_in_chronological_order.rq`
- `/Users/arwest/Hold/Wikidata-Queries/extract_sample_queries/maintenance_examples/Queries_for_maintenance/Performant_way_to_list_100_scholary_articles_sorted_by_linkcount.rq`
- `/Users/arwest/Hold/Wikidata-Queries/extract_sample_queries/maintenance_examples/Queries_for_maintenance/Personendaten_template_equivalent_dewiki.rq`
- `/Users/arwest/Hold/Wikidata-Queries/extract_sample_queries/maintenance_examples/Translation_of_labels_and_descriptions/Translated_labels_and_aliases_for_a_collection.rq`

## Function Extensions

- Features in this section: 6
- Total matches across this section: 21

### `geof:globe()`

- Local matches: 0

Matching files:
- None in the current example trees

### `geof:latitude()`

- Local matches: 1

Matching files:
- `/Users/arwest/Hold/Wikidata-Queries/extract_sample_queries/maintenance_examples/Queries_for_maintenance/Items_showing_two_coordinates_very_distant_each_to_other.rq`

### `geof:longitude()`

- Local matches: 1

Matching files:
- `/Users/arwest/Hold/Wikidata-Queries/extract_sample_queries/maintenance_examples/Queries_for_maintenance/Items_showing_two_coordinates_very_distant_each_to_other.rq`

### `geof:distance()`

- Local matches: 11

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

### `wikibase:decodeUri()`

- Local matches: 2

Matching files:
- `/Users/arwest/Hold/Wikidata-Queries/extract_sample_queries/examples/Wikimedia_projects/List_of_small_monuments_and_other_similar_sites_with_link_to_Commons_category_sitelink_or_P373.rq`
- `/Users/arwest/Hold/Wikidata-Queries/extract_sample_queries/advanced_examples/Mediawiki_API/Find_metadata_like_artist_and_license_for_an_image.rq`

### `wikibase:isSomeValue()`

- Local matches: 6

Matching files:
- `/Users/arwest/Hold/Wikidata-Queries/extract_sample_queries/examples/Fictional_characters/Pokemon.rq`
- `/Users/arwest/Hold/Wikidata-Queries/extract_sample_queries/examples/Music/Paintings_depicting_woodwind_instruments.rq`
- `/Users/arwest/Hold/Wikidata-Queries/extract_sample_queries/examples/Simple_queries/Humans_whose_gender_we_know_we_dont_know.rq`
- `/Users/arwest/Hold/Wikidata-Queries/extract_sample_queries/examples/Visual_arts/Painters_related_to_anonymous_works.rq`
- `/Users/arwest/Hold/Wikidata-Queries/extract_sample_queries/examples/Visual_arts/Painters_type_of_relations_with_anonymous_works.rq`
- `/Users/arwest/Hold/Wikidata-Queries/extract_sample_queries/maintenance_examples/Queries_for_maintenance/Fathers_with_nonexistent_or_unusual_gender_statements.rq`

## SERVICE Extensions

- Features in this section: 7
- Total matches across this section: 441

### `SERVICE wikibase:around`

- Local matches: 7

Matching files:
- `/Users/arwest/Hold/Wikidata-Queries/extract_sample_queries/examples/Geography/Items_geographically_located_around_the_Wikimedia_Foundation_office_sorted_by_distance.rq`
- `/Users/arwest/Hold/Wikidata-Queries/extract_sample_queries/examples/Geography/Map_of_Broadway_venues.rq`
- `/Users/arwest/Hold/Wikidata-Queries/extract_sample_queries/examples/Geography/Places_within_1km_of_the_Empire_State_Building.rq`
- `/Users/arwest/Hold/Wikidata-Queries/extract_sample_queries/examples/Geography/Things_located_where_the_equator_meets_the_prime_meridian.rq`
- `/Users/arwest/Hold/Wikidata-Queries/extract_sample_queries/advanced_examples/Samples_with_coordinates_to_illustrate_maps/Airports_within_100km_of_Berlin.rq`
- `/Users/arwest/Hold/Wikidata-Queries/extract_sample_queries/advanced_examples/Samples_with_coordinates_to_illustrate_maps/Items_around_with_user_location.rq`
- `/Users/arwest/Hold/Wikidata-Queries/extract_sample_queries/advanced_examples/Samples_with_coordinates_to_illustrate_maps/Monuments_and_other_heritage_items_located_1_km_around_the_users_location.rq`

### `SERVICE wikibase:box`

- Local matches: 1

Matching files:
- `/Users/arwest/Hold/Wikidata-Queries/extract_sample_queries/advanced_examples/Samples_with_coordinates_to_illustrate_maps/Schools_between_San_Jose_CA_and_Sacramento_CA.rq`

### `SERVICE wikibase:label`

- Local matches: 419

Matching files:
- 419 matching files (not listed individually)

### `SERVICE bd:slice`

- Local matches: 0

Matching files:
- None in the current example trees

### `SERVICE wikibase:mwapi`

- Local matches: 10

Matching files:
- `/Users/arwest/Hold/Wikidata-Queries/extract_sample_queries/examples/Zika_corpus/Scholarly_articles_with_Zika_in_the_item_label.rq`
- `/Users/arwest/Hold/Wikidata-Queries/extract_sample_queries/advanced_examples/Mediawiki_API/Filter_labels_using_EntitySearch_from_mwapi_service_to_provide_Full_Text_Search.rq`
- `/Users/arwest/Hold/Wikidata-Queries/extract_sample_queries/advanced_examples/Mediawiki_API/Find_metadata_like_artist_and_license_for_an_image.rq`
- `/Users/arwest/Hold/Wikidata-Queries/extract_sample_queries/advanced_examples/Mediawiki_API/Find_statements_with_references_containing_external_links_to_wipoint.rq`
- `/Users/arwest/Hold/Wikidata-Queries/extract_sample_queries/advanced_examples/Mediawiki_API/Getting_pageviews_for_all_articles_in_a_category.rq`
- `/Users/arwest/Hold/Wikidata-Queries/extract_sample_queries/advanced_examples/Mediawiki_API/Using_mwapi_to_base_a_query_on_articles_in_a_Wikipedia_category.rq`
- `/Users/arwest/Hold/Wikidata-Queries/extract_sample_queries/advanced_examples/Samples_with_coordinates_to_illustrate_maps/Map_of_newspapers_in_the_United_States_by_presence_of_Infobox_newspaper_in_their_English_Wikipedia_article.rq`
- `/Users/arwest/Hold/Wikidata-Queries/extract_sample_queries/maintenance_examples/Queries_for_maintenance/Articles_missing_an_item_for_their_subject_CRAN_R_packages.rq`
- `/Users/arwest/Hold/Wikidata-Queries/extract_sample_queries/maintenance_examples/Queries_for_maintenance/Labels_containing_HTML_escape_sequences.rq`
- `/Users/arwest/Hold/Wikidata-Queries/extract_sample_queries/maintenance_examples/Queries_for_maintenance/String_search_All_items_having_the_string_airport_without_having_instance_of_P31_nor_subclass_of_P279.rq`

### `SERVICE gas:service`

- Local matches: 3

Matching files:
- `/Users/arwest/Hold/Wikidata-Queries/extract_sample_queries/examples/Taxon/Asterophryinae_parent_taxon_reverse_graph.rq`
- `/Users/arwest/Hold/Wikidata-Queries/extract_sample_queries/advanced_examples/Gather_Apply_Scatter/Children_of_Genghis_Khan.rq`
- `/Users/arwest/Hold/Wikidata-Queries/extract_sample_queries/advanced_examples/Graph/Children_of_Genghis_Khan.rq`

### `SERVICE bd:sample`

- Local matches: 1

Matching files:
- `/Users/arwest/Hold/Wikidata-Queries/extract_sample_queries/advanced_examples/Sampling/Scientific_articles_without_any_description.rq`

## Supporting Blazegraph-Specific Syntax

- Features in this section: 1
- Total matches across this section: 20

### `hint:Query ...` query hints

- Local matches: 20

Matching files:
- `/Users/arwest/Hold/Wikidata-Queries/extract_sample_queries/examples/Geography/ATMs_around_Munich_belonging_to_the_BankcardServicenetz_interbank_network_federated_query.rq`
- `/Users/arwest/Hold/Wikidata-Queries/extract_sample_queries/examples/Internal_Federation_Adapting_to_the_Graph_split/Birthdays_of_authors_in_the_Wikidata_The_Making_Of_article_using_hint_to_avoid_timeout.rq`
- `/Users/arwest/Hold/Wikidata-Queries/extract_sample_queries/examples/Mountains/Highest_places_on_Earth.rq`
- `/Users/arwest/Hold/Wikidata-Queries/extract_sample_queries/examples/Museums/All_museums_in_Barcelona_with_coordinates.rq`
- `/Users/arwest/Hold/Wikidata-Queries/extract_sample_queries/examples/Music/Paintings_depicting_musical_instruments_with_some_connection_to_Hamburg.rq`
- `/Users/arwest/Hold/Wikidata-Queries/extract_sample_queries/examples/Properties/Identifier_properties_present_on_one_item_but_absent_on_another.rq`
- `/Users/arwest/Hold/Wikidata-Queries/extract_sample_queries/examples/Scientific_literature/Most_popular_subjects_of_scientific_articles.rq`
- `/Users/arwest/Hold/Wikidata-Queries/extract_sample_queries/examples/Scientists/Authors_of_scientific_articles_by_occupation.rq`
- `/Users/arwest/Hold/Wikidata-Queries/extract_sample_queries/examples/Scientists/Authors_of_scientific_articles_who_received_a_Nobel_prize.rq`
- `/Users/arwest/Hold/Wikidata-Queries/extract_sample_queries/examples/Scientists/Using_VALUES_for_extracting_scientific_articles_of_specific_authors.rq`
- `/Users/arwest/Hold/Wikidata-Queries/extract_sample_queries/examples/Wikimedia_projects/Numbers_of_Wikipedia_sitelinks_for_items_with_Art_UK_artist_ID_P1367_for_each_language.rq`
- `/Users/arwest/Hold/Wikidata-Queries/extract_sample_queries/examples/Wikimedia_projects/Wikisource_pages_for_authors_of_scientific_articles.rq`
- `/Users/arwest/Hold/Wikidata-Queries/extract_sample_queries/advanced_examples/Bubble_charts/Values_of_uses_P2283_in_scholarly_articles.rq`
- `/Users/arwest/Hold/Wikidata-Queries/extract_sample_queries/advanced_examples/Samples_with_coordinates_to_illustrate_maps/All_museums_including_subclass_of_museum_in_Washington_DC_with_coordinates.rq`
- `/Users/arwest/Hold/Wikidata-Queries/extract_sample_queries/advanced_examples/Samples_with_coordinates_to_illustrate_maps/Schools_between_San_Jose_CA_and_Sacramento_CA.rq`
- `/Users/arwest/Hold/Wikidata-Queries/extract_sample_queries/advanced_examples/Showcase_Queries/Data_of_Douglas_Adams.rq`
- `/Users/arwest/Hold/Wikidata-Queries/extract_sample_queries/advanced_examples/Showcase_Queries/Data_of_Douglas_Adams_modified_version.rq`
- `/Users/arwest/Hold/Wikidata-Queries/extract_sample_queries/maintenance_examples/Queries_for_maintenance/Aliases_of_properties_which_are_used_more_than_once.rq`
- `/Users/arwest/Hold/Wikidata-Queries/extract_sample_queries/maintenance_examples/Queries_for_maintenance/Deprecated_rank_statements.rq`
- `/Users/arwest/Hold/Wikidata-Queries/extract_sample_queries/maintenance_examples/Queries_for_maintenance/String_search_All_items_having_the_string_airport_without_having_instance_of_P31_nor_subclass_of_P279.rq`

