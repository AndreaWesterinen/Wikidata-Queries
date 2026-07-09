# Blazegraph Feature Usage Report: Wikimedia Queries

Source page reviewed: `https://wikitech.wikimedia.org/wiki/User:AWesterinen/Blazegraph_Features_and_Capabilities`

Scanned local example queries: `617` `.rq` files under `/Users/arwest/Hold/Wikidata-Queries/extract_sample_queries/examples`, `/Users/arwest/Hold/Wikidata-Queries/extract_sample_queries/advanced_examples`, `/Users/arwest/Hold/Wikidata-Queries/extract_sample_queries/human_examples`, `/Users/arwest/Hold/Wikidata-Queries/extract_sample_queries/maintenance_examples`, `/Users/arwest/Hold/Wikidata-Queries/extract_sample_queries/commons_examples`.

This report uses the feature inventory described on the referenced Wikitech page, then maps each feature to matching files in the local example trees.

## Summary Table

| Section | Feature | Matches |
| --- | --- | ---: |
| Blazegraph Features | Named sub-queries (`WITH { ... } AS %name`, `INCLUDE %name`) | 51 |
| Function Extensions | `geof:globe()` | 0 |
| Function Extensions | `geof:latitude()` | 1 |
| Function Extensions | `geof:longitude()` | 1 |
| Function Extensions | `geof:distance()` | 11 |
| Function Extensions | `wikibase:decodeUri()` | 5 |
| SERVICE Extensions | `SERVICE wikibase:around` | 8 |
| SERVICE Extensions | `SERVICE wikibase:box` | 1 |
| SERVICE Extensions | `SERVICE wikibase:label` | 441 |
| SERVICE Extensions | `SERVICE bd:slice` | 1 |
| SERVICE Extensions | `SERVICE wikibase:mwapi` | 16 |
| SERVICE Extensions | `SERVICE gas:service` | 3 |
| SERVICE Extensions | `SERVICE bd:sample` | 6 |
| Supporting Blazegraph-Specific Syntax | `hint:Query ...` query hints | 21 |
| Supporting Blazegraph-Specific Syntax | `bd:serviceParam` | 454 |
| Supporting Blazegraph-Specific Syntax | `wikibase:someValue` | 0 |
| Supporting Blazegraph-Specific Syntax | `wikibase:geoGlobe` | 3 |
| Supporting Blazegraph-Specific Syntax | `wikibase:globe` | 0 |

## Blazegraph Features

- Features in this section: 1
- Total matches across this section: 51

### Named sub-queries (`WITH { ... } AS %name`, `INCLUDE %name`)

- Local matches: 51

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
- `/Users/arwest/Hold/Wikidata-Queries/extract_sample_queries/commons_examples/Exploring_Commons_Categories/Camera_location_of_files_in_a_category.rq`
- `/Users/arwest/Hold/Wikidata-Queries/extract_sample_queries/commons_examples/Exploring_Commons_Categories/Depicts_statements_with_Dutch_labels_of_files_in_one_Commons_category.rq`
- `/Users/arwest/Hold/Wikidata-Queries/extract_sample_queries/commons_examples/Exploring_Commons_Categories/Wikidata_items_of_files_in_CategoryArtworks_with_structured_data_with_redirected_digital_representation_of_property.rq`
- `/Users/arwest/Hold/Wikidata-Queries/extract_sample_queries/commons_examples/Federation_with_Wikidata_to_group_analyse_or_add_information_to_Commons_results/Most_common_wikidata_classes_for_values_of_depicts_P180_on_Commons.rq`
- `/Users/arwest/Hold/Wikidata-Queries/extract_sample_queries/commons_examples/Federation_with_external_sources/Read_commons_categories_for_Europeana_subjects_using_federated_query.rq`
- `/Users/arwest/Hold/Wikidata-Queries/extract_sample_queries/commons_examples/Frequently_used_properties_on_Commons/Most_common_predicates_on_a_sample_of_5000_Commons_files.rq`
- `/Users/arwest/Hold/Wikidata-Queries/extract_sample_queries/commons_examples/Frequently_used_properties_on_Commons/Most_common_property_qualifier_combinations_other_than_on_depicts_P180_statements.rq`
- `/Users/arwest/Hold/Wikidata-Queries/extract_sample_queries/commons_examples/Frequently_used_properties_on_Commons/Most_common_qualifiers_used_on_depicts_statements.rq`
- `/Users/arwest/Hold/Wikidata-Queries/extract_sample_queries/commons_examples/Looking_up_labels_and_descriptions_of_Wikidata_items/Most_common_expression_gesture_or_body_pose_P6022_qualifier_values_for_depicts_P180.rq`
- `/Users/arwest/Hold/Wikidata-Queries/extract_sample_queries/commons_examples/Looking_up_labels_and_descriptions_of_Wikidata_items/colors_of_roses.rq`
- `/Users/arwest/Hold/Wikidata-Queries/extract_sample_queries/commons_examples/Maintenance_queries/Files_captured_with_a_camera_before_that_camera_was_released.rq`
- `/Users/arwest/Hold/Wikidata-Queries/extract_sample_queries/commons_examples/Maintenance_queries/Media_that_has_a_creator_set_to_some_value_where_it_could_actually_be_a_Q_number.rq`
- `/Users/arwest/Hold/Wikidata-Queries/extract_sample_queries/commons_examples/More_Queries/Count_of_number_of_Digital_Representations_P6243_by_type_of_object_represented.rq`
- `/Users/arwest/Hold/Wikidata-Queries/extract_sample_queries/commons_examples/More_Queries/Images_with_largest_number_of_depicts_P180_values_but_no_preferred_values.rq`
- `/Users/arwest/Hold/Wikidata-Queries/extract_sample_queries/commons_examples/More_Queries/Look_up_images_that_are_near_duplicates_of_another_image.rq`
- `/Users/arwest/Hold/Wikidata-Queries/extract_sample_queries/commons_examples/More_Queries/Most_frequent_providers_of_images_when_source_of_image_P7482_file_available_on_the_internet_Q74228490.rq`
- `/Users/arwest/Hold/Wikidata-Queries/extract_sample_queries/commons_examples/More_Queries/Most_frequent_qualifiers_when_source_of_image_P7482_file_available_on_the_internet_Q74228490.rq`
- `/Users/arwest/Hold/Wikidata-Queries/extract_sample_queries/commons_examples/More_Queries/Objects_with_the_largest_number_of_Digital_Representations_P6243.rq`
- `/Users/arwest/Hold/Wikidata-Queries/extract_sample_queries/commons_examples/More_Queries/Things_depicted_with_cats.rq`
- `/Users/arwest/Hold/Wikidata-Queries/extract_sample_queries/commons_examples/Retrieving_a_set_of_Wikidata_items_of_interest/All_images_depicting_Van_Gogh_artworks.rq`
- `/Users/arwest/Hold/Wikidata-Queries/extract_sample_queries/commons_examples/Retrieving_a_set_of_Wikidata_items_of_interest/Detect_depiction_of_both_a_specific_church_and_the_generic_church_building.rq`
- `/Users/arwest/Hold/Wikidata-Queries/extract_sample_queries/commons_examples/Retrieving_a_set_of_Wikidata_items_of_interest/Illustration_published_in_German_magazine_Die_Gartenlaube_using_Wikidata_federation.rq`
- `/Users/arwest/Hold/Wikidata-Queries/extract_sample_queries/commons_examples/Retrieving_a_set_of_Wikidata_items_of_interest/Images_of_objects_located_in_Helsinki_together_with_authors_of_those_objects_and_copyright_status.rq`
- `/Users/arwest/Hold/Wikidata-Queries/extract_sample_queries/commons_examples/Retrieving_a_set_of_Wikidata_items_of_interest/Map_of_files_participating_in_Wiki_Loves_Monuments_in_Sweden_colorcoded_by_year_of_competition.rq`
- `/Users/arwest/Hold/Wikidata-Queries/extract_sample_queries/commons_examples/Retrieving_a_set_of_Wikidata_items_of_interest/Media_files_depicting_former_heads_of_state_that_are_still_alive.rq`
- `/Users/arwest/Hold/Wikidata-Queries/extract_sample_queries/commons_examples/Retrieving_a_set_of_Wikidata_items_of_interest/subclasses_of_roses.rq`
- `/Users/arwest/Hold/Wikidata-Queries/extract_sample_queries/commons_examples/Simple_Queries/Cameras_used_by_naturalist_using_iNaturalist_to_identify_species.rq`
- `/Users/arwest/Hold/Wikidata-Queries/extract_sample_queries/commons_examples/Uncategorized/Most_frequently_used_depicts_statements_from_a_slice_of_media_files_that_dont_have_translations_in_Spanish.rq`
- `/Users/arwest/Hold/Wikidata-Queries/extract_sample_queries/commons_examples/Wikimedia_Commons_assessments/Featured_Pictures_depicting_Lepidoptera_and_taken_with_a_Canon_camera.rq`
- `/Users/arwest/Hold/Wikidata-Queries/extract_sample_queries/commons_examples/Wikimedia_Commons_assessments/Quality_Images_depicting_Hummingbirds.rq`
- `/Users/arwest/Hold/Wikidata-Queries/extract_sample_queries/commons_examples/Wikimedia_Commons_assessments/Quality_Images_depicting_arch_bridges_in_Italy.rq`
- `/Users/arwest/Hold/Wikidata-Queries/extract_sample_queries/commons_examples/Wikimedia_Commons_assessments/Valued_Images_depicting_athletics_competitors.rq`

## Function Extensions

- Features in this section: 5
- Total matches across this section: 18

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

- Local matches: 5

Matching files:
- `/Users/arwest/Hold/Wikidata-Queries/extract_sample_queries/examples/Wikimedia_projects/List_of_small_monuments_and_other_similar_sites_with_link_to_Commons_category_sitelink_or_P373.rq`
- `/Users/arwest/Hold/Wikidata-Queries/extract_sample_queries/advanced_examples/Mediawiki_API/Find_metadata_like_artist_and_license_for_an_image.rq`
- `/Users/arwest/Hold/Wikidata-Queries/extract_sample_queries/commons_examples/Exploring_user_uploads/World_map_of_files_authored_by_Wikimedia_Commons_user_Coyau.rq`
- `/Users/arwest/Hold/Wikidata-Queries/extract_sample_queries/commons_examples/More_Queries/Images_taken_1km_around_a_center.rq`
- `/Users/arwest/Hold/Wikidata-Queries/extract_sample_queries/commons_examples/More_Queries/Images_with_largest_number_of_depicts_P180_values_but_no_preferred_values.rq`

## SERVICE Extensions

- Features in this section: 7
- Total matches across this section: 476

### `SERVICE wikibase:around`

- Local matches: 8

Matching files:
- `/Users/arwest/Hold/Wikidata-Queries/extract_sample_queries/examples/Geography/Items_geographically_located_around_the_Wikimedia_Foundation_office_sorted_by_distance.rq`
- `/Users/arwest/Hold/Wikidata-Queries/extract_sample_queries/examples/Geography/Map_of_Broadway_venues.rq`
- `/Users/arwest/Hold/Wikidata-Queries/extract_sample_queries/examples/Geography/Places_within_1km_of_the_Empire_State_Building.rq`
- `/Users/arwest/Hold/Wikidata-Queries/extract_sample_queries/examples/Geography/Things_located_where_the_equator_meets_the_prime_meridian.rq`
- `/Users/arwest/Hold/Wikidata-Queries/extract_sample_queries/advanced_examples/Samples_with_coordinates_to_illustrate_maps/Airports_within_100km_of_Berlin.rq`
- `/Users/arwest/Hold/Wikidata-Queries/extract_sample_queries/advanced_examples/Samples_with_coordinates_to_illustrate_maps/Items_around_with_user_location.rq`
- `/Users/arwest/Hold/Wikidata-Queries/extract_sample_queries/advanced_examples/Samples_with_coordinates_to_illustrate_maps/Monuments_and_other_heritage_items_located_1_km_around_the_users_location.rq`
- `/Users/arwest/Hold/Wikidata-Queries/extract_sample_queries/commons_examples/More_Queries/Images_taken_1km_around_a_center.rq`

### `SERVICE wikibase:box`

- Local matches: 1

Matching files:
- `/Users/arwest/Hold/Wikidata-Queries/extract_sample_queries/advanced_examples/Samples_with_coordinates_to_illustrate_maps/Schools_between_San_Jose_CA_and_Sacramento_CA.rq`

### `SERVICE wikibase:label`

- Local matches: 441

Matching files:
- 441 matching files (not listed individually)

### `SERVICE bd:slice`

- Local matches: 1

Matching files:
- `/Users/arwest/Hold/Wikidata-Queries/extract_sample_queries/commons_examples/Uncategorized/Most_frequently_used_depicts_statements_from_a_slice_of_media_files_that_dont_have_translations_in_Spanish.rq`

### `SERVICE wikibase:mwapi`

- Local matches: 16

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
- `/Users/arwest/Hold/Wikidata-Queries/extract_sample_queries/commons_examples/Exploring_Commons_Categories/Camera_location_of_files_in_a_category.rq`
- `/Users/arwest/Hold/Wikidata-Queries/extract_sample_queries/commons_examples/Exploring_Commons_Categories/Depicts_statements_with_Dutch_labels_of_files_in_one_Commons_category.rq`
- `/Users/arwest/Hold/Wikidata-Queries/extract_sample_queries/commons_examples/Exploring_Commons_Categories/Wikidata_items_of_files_in_CategoryArtworks_with_structured_data_with_redirected_digital_representation_of_property.rq`
- `/Users/arwest/Hold/Wikidata-Queries/extract_sample_queries/commons_examples/Maintenance_queries/Files_in_a_category_that_do_not_have_inception_P571.rq`
- `/Users/arwest/Hold/Wikidata-Queries/extract_sample_queries/commons_examples/Simple_Queries/Cameras_used_by_naturalist_using_iNaturalist_to_identify_species.rq`
- `/Users/arwest/Hold/Wikidata-Queries/extract_sample_queries/commons_examples/Simple_Queries/Files_in_category_Spoken_English_Wikipedia.rq`

### `SERVICE gas:service`

- Local matches: 3

Matching files:
- `/Users/arwest/Hold/Wikidata-Queries/extract_sample_queries/examples/Taxon/Asterophryinae_parent_taxon_reverse_graph.rq`
- `/Users/arwest/Hold/Wikidata-Queries/extract_sample_queries/advanced_examples/Gather_Apply_Scatter/Children_of_Genghis_Khan.rq`
- `/Users/arwest/Hold/Wikidata-Queries/extract_sample_queries/advanced_examples/Graph/Children_of_Genghis_Khan.rq`

### `SERVICE bd:sample`

- Local matches: 6

Matching files:
- `/Users/arwest/Hold/Wikidata-Queries/extract_sample_queries/advanced_examples/Sampling/Scientific_articles_without_any_description.rq`
- `/Users/arwest/Hold/Wikidata-Queries/extract_sample_queries/commons_examples/Federation_with_Wikidata_to_group_analyse_or_add_information_to_Commons_results/Most_common_wikidata_classes_for_values_of_depicts_P180_on_Commons.rq`
- `/Users/arwest/Hold/Wikidata-Queries/extract_sample_queries/commons_examples/Frequently_used_properties_on_Commons/Most_common_predicates_on_a_sample_of_5000_Commons_files.rq`
- `/Users/arwest/Hold/Wikidata-Queries/extract_sample_queries/commons_examples/Looking_up_labels_and_descriptions_of_Wikidata_items/Most_common_values_for_source_of_image_P7482.rq`
- `/Users/arwest/Hold/Wikidata-Queries/extract_sample_queries/commons_examples/More_Queries/Most_frequent_providers_of_images_when_source_of_image_P7482_file_available_on_the_internet_Q74228490.rq`
- `/Users/arwest/Hold/Wikidata-Queries/extract_sample_queries/commons_examples/More_Queries/Most_frequent_qualifiers_when_source_of_image_P7482_file_available_on_the_internet_Q74228490.rq`

## Supporting Blazegraph-Specific Syntax

- Features in this section: 5
- Total matches across this section: 478

### `hint:Query ...` query hints

- Local matches: 21

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
- `/Users/arwest/Hold/Wikidata-Queries/extract_sample_queries/commons_examples/More_Queries/Images_taken_1km_around_a_center.rq`

### `bd:serviceParam`

- Local matches: 454

Matching files:
- 454 matching files (not listed individually)

### `wikibase:someValue`

- Local matches: 0

Matching files:
- None in the current example trees

### `wikibase:geoGlobe`

- Local matches: 3

Matching files:
- `/Users/arwest/Hold/Wikidata-Queries/extract_sample_queries/examples/Geography/Globes_used_to_represent_coordinates.rq`
- `/Users/arwest/Hold/Wikidata-Queries/extract_sample_queries/examples/Mountains/Mons_mountains_with_coordinates_not_located_on_Earth.rq`
- `/Users/arwest/Hold/Wikidata-Queries/extract_sample_queries/maintenance_examples/Queries_for_maintenance/Objects_that_have_globe_which_does_not_match_their_coordinates.rq`

### `wikibase:globe`

- Local matches: 0

Matching files:
- None in the current example trees

