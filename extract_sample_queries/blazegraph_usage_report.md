# Blazegraph Feature Usage Report

Source page reviewed: `https://wikitech.wikimedia.org/wiki/User:AWesterinen/Blazegraph_Features_and_Capabilities`

Scanned local example queries: `395` `.rq` files under `/Users/arwest/Hold/Wikidata-Queries/extract_sample_queries/examples`.

This report uses the feature inventory described on the referenced Wikitech page, then maps each feature to matching files in the local `examples/` tree.

## Summary Table

| Section | Feature | Matches |
| --- | --- | ---: |
| Page-Listed Blazegraph Features | Stored queries (`SERVICE <http://www.bigdata.com/rdf/stored-query#...>`)  | 0 |
| Page-Listed Blazegraph Features | Named sub-queries (`WITH { ... } AS %name`, `INCLUDE %name`) | 13 |
| Page-Listed Function Extensions | `geof:globe()` | 0 |
| Page-Listed Function Extensions | `geof:latitude()` | 0 |
| Page-Listed Function Extensions | `geof:longitude()` | 0 |
| Page-Listed Function Extensions | `geof:distance()` | 10 |
| Page-Listed Function Extensions | `wikibase:decodeUri()` | 1 |
| Page-Listed SERVICE Extensions | `SERVICE wikibase:around` | 4 |
| Page-Listed SERVICE Extensions | `SERVICE wikibase:box` | 0 |
| Page-Listed SERVICE Extensions | `SERVICE wikibase:label` | 303 |
| Page-Listed SERVICE Extensions | `SERVICE bd:slice` | 0 |
| Page-Listed SERVICE Extensions | `SERVICE wikibase:mwapi` | 1 |
| Page-Listed SERVICE Extensions | `SERVICE gas:service` | 1 |
| Page-Listed SERVICE Extensions | `SERVICE bd:sample` | 0 |
| Supporting Blazegraph-Specific Syntax | `hint:Query ...` query hints | 12 |
| Supporting Blazegraph-Specific Syntax | `bd:serviceParam` | 304 |

## Page-Listed Blazegraph Features

- Features in this section: 2
- Total matches across this section: 13

### Stored queries (`SERVICE <http://www.bigdata.com/rdf/stored-query#...>`) 

- Local matches: 0
- Notes: The Blazegraph StoredQuery page defines stored queries as custom applications exposed through a SERVICE URI. No matches were found in the current local example set.

Matching files:
- None in the current `examples/` tree

### Named sub-queries (`WITH { ... } AS %name`, `INCLUDE %name`)

- Local matches: 13
- Notes: Documented on the page as a Blazegraph-specific readability/performance feature.

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

## Page-Listed Function Extensions

- Features in this section: 5
- Total matches across this section: 11

### `geof:globe()`

- Local matches: 0
- Notes: Listed on the page as a Blazegraph-specific function for extracting globe information from coordinates.

Matching files:
- None in the current `examples/` tree

### `geof:latitude()`

- Local matches: 0
- Notes: Listed on the page as a Blazegraph-specific function for extracting latitude.

Matching files:
- None in the current `examples/` tree

### `geof:longitude()`

- Local matches: 0
- Notes: Listed on the page as a Blazegraph-specific function for extracting longitude.

Matching files:
- None in the current `examples/` tree

### `geof:distance()`

- Local matches: 10
- Notes: The page treats this as the geospatial distance function currently used in Blazegraph-backed WDQS queries.

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

### `wikibase:decodeUri()`

- Local matches: 1
- Notes: Listed on the page as a custom Blazegraph function extension.

Matching files:
- `/Users/arwest/Hold/Wikidata-Queries/extract_sample_queries/examples/Wikimedia_projects/List_of_small_monuments_and_other_similar_sites_with_link_to_Commons_category_sitelink_or_P373.rq`

## Page-Listed SERVICE Extensions

- Features in this section: 7
- Total matches across this section: 309

### `SERVICE wikibase:around`

- Local matches: 4
- Notes: Geospatial service extension documented on the page.

Matching files:
- `/Users/arwest/Hold/Wikidata-Queries/extract_sample_queries/examples/Geography/Items_geographically_located_around_the_Wikimedia_Foundation_office_sorted_by_distance.rq`
- `/Users/arwest/Hold/Wikidata-Queries/extract_sample_queries/examples/Geography/Map_of_Broadway_venues.rq`
- `/Users/arwest/Hold/Wikidata-Queries/extract_sample_queries/examples/Geography/Places_within_1km_of_the_Empire_State_Building.rq`
- `/Users/arwest/Hold/Wikidata-Queries/extract_sample_queries/examples/Geography/Things_located_where_the_equator_meets_the_prime_meridian.rq`

### `SERVICE wikibase:box`

- Local matches: 0
- Notes: Geospatial service extension documented on the page.

Matching files:
- None in the current `examples/` tree

### `SERVICE wikibase:label`

- Local matches: 303
- Notes: Label service documented on the page.

Matching files:
- `/Users/arwest/Hold/Wikidata-Queries/extract_sample_queries/examples/Biological_pathway_citation_corpora/Get_the_Pathways_citation_corpus.rq`
- `/Users/arwest/Hold/Wikidata-Queries/extract_sample_queries/examples/Biological_pathway_citation_corpora/Get_the_Reactome_citation_corups.rq`
- `/Users/arwest/Hold/Wikidata-Queries/extract_sample_queries/examples/Biological_pathway_citation_corpora/Get_the_Wikipathways_citation_corpus.rq`
- `/Users/arwest/Hold/Wikidata-Queries/extract_sample_queries/examples/Biology_and_Medicine/Biologists_with_Twitter_accounts.rq`
- `/Users/arwest/Hold/Wikidata-Queries/extract_sample_queries/examples/Biology_and_Medicine/Find_drugs_for_cancers_that_target_genes_related_to_cell_proliferation.rq`
- `/Users/arwest/Hold/Wikidata-Queries/extract_sample_queries/examples/Biology_and_Medicine/List_of_pharmaceutical_drugs_with_picture.rq`
- `/Users/arwest/Hold/Wikidata-Queries/extract_sample_queries/examples/Biology_and_Medicine/Parent_taxons_of_Blue_Whale.rq`
- `/Users/arwest/Hold/Wikidata-Queries/extract_sample_queries/examples/Biology_and_Medicine/Taxons_and_what_they_are_named_after.rq`
- `/Users/arwest/Hold/Wikidata-Queries/extract_sample_queries/examples/Biology_and_Medicine/Threatened_Species_of_Animals_as_per_IUCN_Classification.rq`
- `/Users/arwest/Hold/Wikidata-Queries/extract_sample_queries/examples/CIViC_Corpus/Get_the_CIViC_citation_corpus.rq`
- `/Users/arwest/Hold/Wikidata-Queries/extract_sample_queries/examples/Chemistry/All_CAS_registry_numbers_in_Wikidata.rq`
- `/Users/arwest/Hold/Wikidata-Queries/extract_sample_queries/examples/Chemistry/All_pKa_data_in_Wikidata_and_the_source_titles.rq`
- `/Users/arwest/Hold/Wikidata-Queries/extract_sample_queries/examples/Chemistry/Awarded_Chemistry_Nobel_Prizes.rq`
- `/Users/arwest/Hold/Wikidata-Queries/extract_sample_queries/examples/Chemistry/Boiling_points_of_alkanes.rq`
- `/Users/arwest/Hold/Wikidata-Queries/extract_sample_queries/examples/Chemistry/Chemical_compounds_in_Wikidata_sharing_the_same_CAS_registry_number.rq`
- `/Users/arwest/Hold/Wikidata-Queries/extract_sample_queries/examples/Chemistry/Chemical_elements_and_their_properties.rq`
- `/Users/arwest/Hold/Wikidata-Queries/extract_sample_queries/examples/Chemistry/Colors_of_chemical_compounds.rq`
- `/Users/arwest/Hold/Wikidata-Queries/extract_sample_queries/examples/Chemistry/Images_of_organic_acids.rq`
- `/Users/arwest/Hold/Wikidata-Queries/extract_sample_queries/examples/Chemistry/Solubilities_of_chemicals.rq`
- `/Users/arwest/Hold/Wikidata-Queries/extract_sample_queries/examples/Churches/Cathedrals_in_Paris.rq`
- `/Users/arwest/Hold/Wikidata-Queries/extract_sample_queries/examples/Churches/Churches_in_church_district_Wittenberg.rq`
- `/Users/arwest/Hold/Wikidata-Queries/extract_sample_queries/examples/Churches/Special_church_type_Spitalkirche_in_Germany.rq`
- `/Users/arwest/Hold/Wikidata-Queries/extract_sample_queries/examples/Cities/Border_cities_of_the_world.rq`
- `/Users/arwest/Hold/Wikidata-Queries/extract_sample_queries/examples/Cities/Cities_as_big_as_Eindhoven_give_or_take_1000.rq`
- `/Users/arwest/Hold/Wikidata-Queries/extract_sample_queries/examples/Cities/Cities_connected_by_the_European_route_E40.rq`
- `/Users/arwest/Hold/Wikidata-Queries/extract_sample_queries/examples/Cities/Cities_connected_by_the_TransMongolian_and_TransSiberian_Railway.rq`
- `/Users/arwest/Hold/Wikidata-Queries/extract_sample_queries/examples/Cities/Cities_connected_to_Paramaribo_Suriname_by_main_roads.rq`
- `/Users/arwest/Hold/Wikidata-Queries/extract_sample_queries/examples/Cities/Destinations_from_Antwerp_International_airport.rq`
- `/Users/arwest/Hold/Wikidata-Queries/extract_sample_queries/examples/Cities/Former_capitals.rq`
- `/Users/arwest/Hold/Wikidata-Queries/extract_sample_queries/examples/Cities/Largest_cities_of_the_world.rq`
- `/Users/arwest/Hold/Wikidata-Queries/extract_sample_queries/examples/Cities/Metro_station_of_city_with_template.rq`
- `/Users/arwest/Hold/Wikidata-Queries/extract_sample_queries/examples/Cities/Municipalities_of_the_Basque_Country_without_former_municipalities.rq`
- `/Users/arwest/Hold/Wikidata-Queries/extract_sample_queries/examples/Cities/Population_of_cities_and_towns_in_Denmark_and_their_OSM_relation_id.rq`
- `/Users/arwest/Hold/Wikidata-Queries/extract_sample_queries/examples/Cities/Show_all_Dutch_municipalities_that_share_a_border_with_Alphen_aan_den_Rijn_Q213246_ignoring_rank.rq`
- `/Users/arwest/Hold/Wikidata-Queries/extract_sample_queries/examples/Computer_Science_and_Technology/EReaders_that_support_the_mobipocket_file_format.rq`
- `/Users/arwest/Hold/Wikidata-Queries/extract_sample_queries/examples/Computer_Science_and_Technology/Erdos_Numbers_and_images_of_people_who_have_oral_histories_in_the_Computer_History_Museums_collection.rq`
- `/Users/arwest/Hold/Wikidata-Queries/extract_sample_queries/examples/Computer_Science_and_Technology/Free_and_opensource_software_written_in_Go_programming_language.rq`
- `/Users/arwest/Hold/Wikidata-Queries/extract_sample_queries/examples/Computer_Science_and_Technology/Freeware_games_for_Windows_ordered_from_recent_date.rq`
- `/Users/arwest/Hold/Wikidata-Queries/extract_sample_queries/examples/Computer_Science_and_Technology/List_of_W3C_standards.rq`
- `/Users/arwest/Hold/Wikidata-Queries/extract_sample_queries/examples/Computer_Science_and_Technology/List_of_computer_files_formats.rq`
- `/Users/arwest/Hold/Wikidata-Queries/extract_sample_queries/examples/Computer_Science_and_Technology/Oldest_software.rq`
- `/Users/arwest/Hold/Wikidata-Queries/extract_sample_queries/examples/Computer_Science_and_Technology/Return_a_bubble_chart_of_mediatypes_by_count_of_file_formats.rq`
- `/Users/arwest/Hold/Wikidata-Queries/extract_sample_queries/examples/Computer_Science_and_Technology/Software_applications_ranked_in_descending_order_by_the_number_of_writable_file_formats.rq`
- `/Users/arwest/Hold/Wikidata-Queries/extract_sample_queries/examples/Computer_Science_and_Technology/Software_written_in_Go_programming_language.rq`
- `/Users/arwest/Hold/Wikidata-Queries/extract_sample_queries/examples/Computer_Science_and_Technology/Universities_of_main_programming_language_authors.rq`
- `/Users/arwest/Hold/Wikidata-Queries/extract_sample_queries/examples/Computer_Science_and_Technology/Websites_with_OpenAPI_endpoints.rq`
- `/Users/arwest/Hold/Wikidata-Queries/extract_sample_queries/examples/Countries/Countries_sorted_by_population.rq`
- `/Users/arwest/Hold/Wikidata-Queries/extract_sample_queries/examples/Countries/Country_populations_together_with_total_city_populations.rq`
- `/Users/arwest/Hold/Wikidata-Queries/extract_sample_queries/examples/Countries/Languages_and_dialects_spoken_in_the_Netherlands_with_their_optional_Wikipedia_editions.rq`
- `/Users/arwest/Hold/Wikidata-Queries/extract_sample_queries/examples/Countries/Largest_cities_per_country.rq`
- `/Users/arwest/Hold/Wikidata-Queries/extract_sample_queries/examples/Countries/List_of_presentday_countries_and_capitals.rq`
- `/Users/arwest/Hold/Wikidata-Queries/extract_sample_queries/examples/Countries/Papers_about_Wikidata.rq`
- `/Users/arwest/Hold/Wikidata-Queries/extract_sample_queries/examples/Countries/UN_member_states.rq`
- `/Users/arwest/Hold/Wikidata-Queries/extract_sample_queries/examples/Countries/Wikidata_people_per_million_inhabitants_for_all_EU_countries.rq`
- `/Users/arwest/Hold/Wikidata-Queries/extract_sample_queries/examples/Culture/Birthplaces_of_Europeana280_artists.rq`
- `/Users/arwest/Hold/Wikidata-Queries/extract_sample_queries/examples/Culture/Common_phrases.rq`
- `/Users/arwest/Hold/Wikidata-Queries/extract_sample_queries/examples/Culture/Distribution_of_public_art_by_place.rq`
- `/Users/arwest/Hold/Wikidata-Queries/extract_sample_queries/examples/Culture/List_of_theatre_plays.rq`
- `/Users/arwest/Hold/Wikidata-Queries/extract_sample_queries/examples/Culture/Padua_University_Rectors_by_dates.rq`
- `/Users/arwest/Hold/Wikidata-Queries/extract_sample_queries/examples/Culture/Top_100_podcasts_by_number_of_statements.rq`
- `/Users/arwest/Hold/Wikidata-Queries/extract_sample_queries/examples/Demography/Average_lifespan_by_occupation.rq`
- `/Users/arwest/Hold/Wikidata-Queries/extract_sample_queries/examples/Demography/Birthplaces_of_humans_named_Antoine.rq`
- `/Users/arwest/Hold/Wikidata-Queries/extract_sample_queries/examples/Demography/People_of_same_year_of_birth_by_occupation.rq`
- `/Users/arwest/Hold/Wikidata-Queries/extract_sample_queries/examples/Demography/Population_growth_in_Suriname_from_1960_onward.rq`
- `/Users/arwest/Hold/Wikidata-Queries/extract_sample_queries/examples/Demography/Thingspeople_with_most_children.rq`
- `/Users/arwest/Hold/Wikidata-Queries/extract_sample_queries/examples/Diseases/Infectious_diseases_with_their_human_minimum_and_maximum_incubation_time_in_days.rq`
- `/Users/arwest/Hold/Wikidata-Queries/extract_sample_queries/examples/Distances_between_any_two_cities_or_municipalities_in_an_area/grouped_by_dist_range_colorcoded.rq`
- `/Users/arwest/Hold/Wikidata-Queries/extract_sample_queries/examples/Distances_between_any_two_cities_or_municipalities_in_an_area/grouped_per_municipality_on_xaxis_alphabetically.rq`
- `/Users/arwest/Hold/Wikidata-Queries/extract_sample_queries/examples/Distances_between_any_two_cities_or_municipalities_in_an_area/grouped_per_municipality_on_xaxis_animated_by_fixed_dist_range_groups.rq`
- `/Users/arwest/Hold/Wikidata-Queries/extract_sample_queries/examples/Distances_between_any_two_cities_or_municipalities_in_an_area/grouped_per_municipality_on_xaxis_animated_by_ranked_dist_farthest_2nd_farthest.rq`
- `/Users/arwest/Hold/Wikidata-Queries/extract_sample_queries/examples/Distances_between_any_two_cities_or_municipalities_in_an_area/grouped_per_municipality_on_xaxis_animated_per_municipality_on_xaxis.rq`
- `/Users/arwest/Hold/Wikidata-Queries/extract_sample_queries/examples/Distances_between_any_two_cities_or_municipalities_in_an_area/grouped_per_municipality_on_xaxis_animated_per_municipality_on_zaxis.rq`
- `/Users/arwest/Hold/Wikidata-Queries/extract_sample_queries/examples/Distances_between_any_two_cities_or_municipalities_in_an_area/grouped_per_municipality_on_xaxis_by_sum_of_dist.rq`
- `/Users/arwest/Hold/Wikidata-Queries/extract_sample_queries/examples/Dutch_general_election_2017/Candidates_for_the_Dutch_general_election_2017_living_abroad.rq`
- `/Users/arwest/Hold/Wikidata-Queries/extract_sample_queries/examples/Dutch_general_election_2017/Candidates_for_the_Dutch_general_election_2017_living_in_Antwerp_Belgium.rq`
- `/Users/arwest/Hold/Wikidata-Queries/extract_sample_queries/examples/Dutch_general_election_2017/Candidates_for_the_Dutch_general_election_in_2017.rq`
- `/Users/arwest/Hold/Wikidata-Queries/extract_sample_queries/examples/Dutch_general_election_2017/Gender_distribution_in_the_candidates_for_the_Dutch_general_election_2017.rq`
- `/Users/arwest/Hold/Wikidata-Queries/extract_sample_queries/examples/Dutch_general_election_2017/Occupations_of_candidates_of_the_Dutch_general_election_2017.rq`
- `/Users/arwest/Hold/Wikidata-Queries/extract_sample_queries/examples/Economics_and_Business/Business_listed_on_NYSE_and_NASDAQ_along_with_their_ticker_symbols.rq`
- `/Users/arwest/Hold/Wikidata-Queries/extract_sample_queries/examples/Economics_and_Business/Countries_that_have_adopted_a_cryptocurrency_as_legal_tender.rq`
- `/Users/arwest/Hold/Wikidata-Queries/extract_sample_queries/examples/Economics_and_Business/Distinct_billionaires.rq`
- `/Users/arwest/Hold/Wikidata-Queries/extract_sample_queries/examples/Economics_and_Business/Human_Development_Index_of_specified_countrys.rq`
- `/Users/arwest/Hold/Wikidata-Queries/extract_sample_queries/examples/Education/Rankings_of_universityes.rq`
- `/Users/arwest/Hold/Wikidata-Queries/extract_sample_queries/examples/External_Federation_Queries_using_Wikidata_plus_Other_sources/Getting_basic_information_for_one_item.rq`
- `/Users/arwest/Hold/Wikidata-Queries/extract_sample_queries/examples/External_Federation_Queries_using_Wikidata_plus_Other_sources/UK_Parliament_constituencies_whose_official_point_location_is_more_than_10km_from_the_location_in_Wikidata.rq`
- `/Users/arwest/Hold/Wikidata-Queries/extract_sample_queries/examples/External_identifiers/Swedish_municipalities_which_changed_their_municipality_identifier_at_some_point.rq`
- `/Users/arwest/Hold/Wikidata-Queries/extract_sample_queries/examples/Fictional_characters/Fictional_characters_whose_birthdeath_date_is_in_the_current_decade.rq`
- `/Users/arwest/Hold/Wikidata-Queries/extract_sample_queries/examples/Fictional_characters/Fictional_subjects_of_the_Marvel_Universe.rq`
- `/Users/arwest/Hold/Wikidata-Queries/extract_sample_queries/examples/Fictional_characters/Pokemon.rq`
- `/Users/arwest/Hold/Wikidata-Queries/extract_sample_queries/examples/Figshare_citations/Wikidata_statement_with_a_reference_to_data_in_Figshare_of_which_a_Wikicite_item_exists.rq`
- `/Users/arwest/Hold/Wikidata-Queries/extract_sample_queries/examples/Figshare_citations/Wikidata_statements_with_a_reference_to_a_Figshare_DOI_Q28061352.rq`
- `/Users/arwest/Hold/Wikidata-Queries/extract_sample_queries/examples/Film_and_television/Academy_award_data.rq`
- `/Users/arwest/Hold/Wikidata-Queries/extract_sample_queries/examples/Film_and_television/Actors_who_played_the_same_role_more_than_40_years_apart.rq`
- `/Users/arwest/Hold/Wikidata-Queries/extract_sample_queries/examples/Film_and_television/All_Dr_Who_performers.rq`
- `/Users/arwest/Hold/Wikidata-Queries/extract_sample_queries/examples/Film_and_television/Characters_portrayed_by_most_actors.rq`
- `/Users/arwest/Hold/Wikidata-Queries/extract_sample_queries/examples/Film_and_television/Contemporary_Indian_actresses.rq`
- `/Users/arwest/Hold/Wikidata-Queries/extract_sample_queries/examples/Film_and_television/Film_directors_ranked_by_number_of_sitelinks_multiplied_by_their_number_of_films.rq`
- `/Users/arwest/Hold/Wikidata-Queries/extract_sample_queries/examples/Film_and_television/Films_of_directors_by_their_English_Wikipedia_name.rq`
- `/Users/arwest/Hold/Wikidata-Queries/extract_sample_queries/examples/Film_and_television/List_of_actors_with_pictures_with_year_of_birth_andor_death.rq`
- `/Users/arwest/Hold/Wikidata-Queries/extract_sample_queries/examples/Film_and_television/Main_subjects_of_West_Wing_episodes.rq`
- `/Users/arwest/Hold/Wikidata-Queries/extract_sample_queries/examples/Film_and_television/Movies_and_their_narrative_location_on_a_map.rq`
- `/Users/arwest/Hold/Wikidata-Queries/extract_sample_queries/examples/Film_and_television/Movies_released_in_2017.rq`
- `/Users/arwest/Hold/Wikidata-Queries/extract_sample_queries/examples/Film_and_television/Movies_with_Bud_Spencer.rq`
- `/Users/arwest/Hold/Wikidata-Queries/extract_sample_queries/examples/Film_and_television/Number_of_handed_out_Academy_Awards_per_award_type.rq`
- `/Users/arwest/Hold/Wikidata-Queries/extract_sample_queries/examples/Film_and_television/People_that_received_both_Academy_Award_and_Nobel_Prize.rq`
- `/Users/arwest/Hold/Wikidata-Queries/extract_sample_queries/examples/Film_and_television/The_Simpsons_television_series_episodes_list_by_season.rq`
- `/Users/arwest/Hold/Wikidata-Queries/extract_sample_queries/examples/Film_and_television/Winner_of_the_Academy_Awards_by_Award_and_Time.rq`
- `/Users/arwest/Hold/Wikidata-Queries/extract_sample_queries/examples/Food_Drink/German_breweries.rq`
- `/Users/arwest/Hold/Wikidata-Queries/extract_sample_queries/examples/Food_Drink/Sandwich_ingredients.rq`
- `/Users/arwest/Hold/Wikidata-Queries/extract_sample_queries/examples/Food_Drink/Sandwiches.rq`
- `/Users/arwest/Hold/Wikidata-Queries/extract_sample_queries/examples/Geography/Buildings_in_more_than_one_country.rq`
- `/Users/arwest/Hold/Wikidata-Queries/extract_sample_queries/examples/Geography/City_gates_in_the_Dutch_province_of_Zeeland.rq`
- `/Users/arwest/Hold/Wikidata-Queries/extract_sample_queries/examples/Geography/Emergency_numbers_by_population_using_them.rq`
- `/Users/arwest/Hold/Wikidata-Queries/extract_sample_queries/examples/Geography/German_states_ordered_by_the_number_of_company_headquarters_per_million_inhabitants.rq`
- `/Users/arwest/Hold/Wikidata-Queries/extract_sample_queries/examples/Geography/Glaciers_map.rq`
- `/Users/arwest/Hold/Wikidata-Queries/extract_sample_queries/examples/Geography/How_many_states_this_US_state_borders.rq`
- `/Users/arwest/Hold/Wikidata-Queries/extract_sample_queries/examples/Geography/Items_geographically_located_around_the_Wikimedia_Foundation_office_sorted_by_distance.rq`
- `/Users/arwest/Hold/Wikidata-Queries/extract_sample_queries/examples/Geography/Map_and_list_of_municipalities_in_The_Netherlands.rq`
- `/Users/arwest/Hold/Wikidata-Queries/extract_sample_queries/examples/Geography/Map_of_Broadway_venues.rq`
- `/Users/arwest/Hold/Wikidata-Queries/extract_sample_queries/examples/Geography/Map_of_places_mentioned_in_travel_stories_with_text_in_French_accessible_online.rq`
- `/Users/arwest/Hold/Wikidata-Queries/extract_sample_queries/examples/Geography/Metro_stations_of_Paris_Metro_Line_1_Q13224_in_Paris.rq`
- `/Users/arwest/Hold/Wikidata-Queries/extract_sample_queries/examples/Geography/Places_in_Antarctica_more_than_3000km_away_from_the_South_Pole.rq`
- `/Users/arwest/Hold/Wikidata-Queries/extract_sample_queries/examples/Geography/Places_that_are_below_10_meters_above_sea_level.rq`
- `/Users/arwest/Hold/Wikidata-Queries/extract_sample_queries/examples/Geography/Places_within_1km_of_the_Empire_State_Building.rq`
- `/Users/arwest/Hold/Wikidata-Queries/extract_sample_queries/examples/Geography/Select_French_municipalities_by_INSEE_code_select_by_identifier.rq`
- `/Users/arwest/Hold/Wikidata-Queries/extract_sample_queries/examples/Geography/Streets_in_France_without_a_city.rq`
- `/Users/arwest/Hold/Wikidata-Queries/extract_sample_queries/examples/Geography/Streets_named_after_a_person.rq`
- `/Users/arwest/Hold/Wikidata-Queries/extract_sample_queries/examples/Geography/Ten_largest_islands_in_the_world.rq`
- `/Users/arwest/Hold/Wikidata-Queries/extract_sample_queries/examples/Geography/Things_located_where_the_equator_meets_the_prime_meridian.rq`
- `/Users/arwest/Hold/Wikidata-Queries/extract_sample_queries/examples/History/All_events_that_occured_on_20010911.rq`
- `/Users/arwest/Hold/Wikidata-Queries/extract_sample_queries/examples/History/Ancestors_of_WillemAlexander_of_the_Netherlands.rq`
- `/Users/arwest/Hold/Wikidata-Queries/extract_sample_queries/examples/History/Animals_that_were_executed.rq`
- `/Users/arwest/Hold/Wikidata-Queries/extract_sample_queries/examples/History/Everything_with_a_time_property_on_a_given_date.rq`
- `/Users/arwest/Hold/Wikidata-Queries/extract_sample_queries/examples/History/French_heads_of_government_by_length_of_service.rq`
- `/Users/arwest/Hold/Wikidata-Queries/extract_sample_queries/examples/History/List_of_countries_in_1754.rq`
- `/Users/arwest/Hold/Wikidata-Queries/extract_sample_queries/examples/History/List_of_popes.rq`
- `/Users/arwest/Hold/Wikidata-Queries/extract_sample_queries/examples/History/List_of_suicide_attacks.rq`
- `/Users/arwest/Hold/Wikidata-Queries/extract_sample_queries/examples/History/List_of_torture_devices.rq`
- `/Users/arwest/Hold/Wikidata-Queries/extract_sample_queries/examples/History/Most_prolific_fathers.rq`
- `/Users/arwest/Hold/Wikidata-Queries/extract_sample_queries/examples/History/People_elevated_in_the_public_domain_in_2020_life50_years.rq`
- `/Users/arwest/Hold/Wikidata-Queries/extract_sample_queries/examples/History/People_who_died_by_burning_on_a_timeline.rq`
- `/Users/arwest/Hold/Wikidata-Queries/extract_sample_queries/examples/History/People_who_lived_in_the_same_period_as_another_person.rq`
- `/Users/arwest/Hold/Wikidata-Queries/extract_sample_queries/examples/History/People_who_were_stateless_for_some_time.rq`
- `/Users/arwest/Hold/Wikidata-Queries/extract_sample_queries/examples/History/Periods_of_Japanese_history_and_what_they_were_named_after.rq`
- `/Users/arwest/Hold/Wikidata-Queries/extract_sample_queries/examples/History/Poets_who_were_through_An_Lushan_Rebellion.rq`
- `/Users/arwest/Hold/Wikidata-Queries/extract_sample_queries/examples/History/Popes_with_children.rq`
- `/Users/arwest/Hold/Wikidata-Queries/extract_sample_queries/examples/History/Population_in_Europe_after_1960.rq`
- `/Users/arwest/Hold/Wikidata-Queries/extract_sample_queries/examples/History/Presidents_and_spouses.rq`
- `/Users/arwest/Hold/Wikidata-Queries/extract_sample_queries/examples/History/Years_with_3_popes.rq`
- `/Users/arwest/Hold/Wikidata-Queries/extract_sample_queries/examples/Humans_without_children/Including_nontruthy_values.rq`
- `/Users/arwest/Hold/Wikidata-Queries/extract_sample_queries/examples/Humans_without_children/Only_truthy_values.rq`
- `/Users/arwest/Hold/Wikidata-Queries/extract_sample_queries/examples/Internal_Federation_Adapting_to_the_Graph_split/Articles_by_Lydia_Pintscher_using_the_wikibaselabel_SERVICE_in_both_graphs.rq`
- `/Users/arwest/Hold/Wikidata-Queries/extract_sample_queries/examples/Internal_Federation_Adapting_to_the_Graph_split/Birthdays_of_authors_in_the_Wikidata_The_Making_Of_article_using_hint_to_avoid_timeout.rq`
- `/Users/arwest/Hold/Wikidata-Queries/extract_sample_queries/examples/Lexeme_queries/Demonyms_on_map.rq`
- `/Users/arwest/Hold/Wikidata-Queries/extract_sample_queries/examples/Lexeme_queries/German_picture_dictionary_for_young_children.rq`
- `/Users/arwest/Hold/Wikidata-Queries/extract_sample_queries/examples/Lexeme_queries/Lexeme_languages_by_number_of_usage_examples.rq`
- `/Users/arwest/Hold/Wikidata-Queries/extract_sample_queries/examples/Lexeme_queries/Lexemes_describing_a_color.rq`
- `/Users/arwest/Hold/Wikidata-Queries/extract_sample_queries/examples/Lexeme_queries/Lexemes_that_means_water_ordered_by_language.rq`
- `/Users/arwest/Hold/Wikidata-Queries/extract_sample_queries/examples/Lexeme_queries/Pictures_of_noun_lexemes_in_English_picture_dictionary_a_la_Wikidata.rq`
- `/Users/arwest/Hold/Wikidata-Queries/extract_sample_queries/examples/Lexeme_queries/Senses_on_English_lexemes_with_an_offensive_or_profanity_style_statement.rq`
- `/Users/arwest/Hold/Wikidata-Queries/extract_sample_queries/examples/Lexeme_queries/The_100_most_translated_concepts_in_the_Lexeme_namespace.rq`
- `/Users/arwest/Hold/Wikidata-Queries/extract_sample_queries/examples/Literature/Authors_with_United_States_citizenship_without_a_Goodreads_identifier.rq`
- `/Users/arwest/Hold/Wikidata-Queries/extract_sample_queries/examples/Literature/Birth_places_of_German_poets.rq`
- `/Users/arwest/Hold/Wikidata-Queries/extract_sample_queries/examples/Literature/Books_by_a_given_Author_including_genres_and_date_of_first_publication.rq`
- `/Users/arwest/Hold/Wikidata-Queries/extract_sample_queries/examples/Literature/List_of_authors_unsuccessfully_nominated_for_Nobel_prize_in_literature.rq`
- `/Users/arwest/Hold/Wikidata-Queries/extract_sample_queries/examples/Literature/List_of_digital_libraries_in_the_world.rq`
- `/Users/arwest/Hold/Wikidata-Queries/extract_sample_queries/examples/Literature/Map_of_Libraries_in_Canada.rq`
- `/Users/arwest/Hold/Wikidata-Queries/extract_sample_queries/examples/Literature/Occupation_writer_language_Belarussian_died_more_than_50_years_so_his_books_now_in_public_domain.rq`
- `/Users/arwest/Hold/Wikidata-Queries/extract_sample_queries/examples/Literature/Poets_and_monarchs.rq`
- `/Users/arwest/Hold/Wikidata-Queries/extract_sample_queries/examples/Literature/Text_by_author_containing_caseinsensitive_title_with_optional_cover_image.rq`
- `/Users/arwest/Hold/Wikidata-Queries/extract_sample_queries/examples/Literature/Works_by_women_that_were_born_between_1800_and_1900_are_in_the_WomenWriters_database_and_are_translated.rq`
- `/Users/arwest/Hold/Wikidata-Queries/extract_sample_queries/examples/Math/Mathematical_proofs.rq`
- `/Users/arwest/Hold/Wikidata-Queries/extract_sample_queries/examples/Math/Timeline_of_death_of_mathematicans_and_their_theorems.rq`
- `/Users/arwest/Hold/Wikidata-Queries/extract_sample_queries/examples/Metabolites/Metabolitemetabolite_interactions_mostly_conversions_and_their_pKa_change_federated_query.rq`
- `/Users/arwest/Hold/Wikidata-Queries/extract_sample_queries/examples/Metabolites/Metabolites_and_the_species_where_they_are_found_in.rq`
- `/Users/arwest/Hold/Wikidata-Queries/extract_sample_queries/examples/Mountains/Highest_mountains_in_the_universe_with_units.rq`
- `/Users/arwest/Hold/Wikidata-Queries/extract_sample_queries/examples/Mountains/Highest_mountains_in_the_universe_with_units_compact_form.rq`
- `/Users/arwest/Hold/Wikidata-Queries/extract_sample_queries/examples/Mountains/Highest_places_on_Earth.rq`
- `/Users/arwest/Hold/Wikidata-Queries/extract_sample_queries/examples/Mountains/Italian_mountains_higher_than_4000_meters.rq`
- `/Users/arwest/Hold/Wikidata-Queries/extract_sample_queries/examples/Mountains/Mons_mountains_with_coordinates_not_located_on_Earth.rq`
- `/Users/arwest/Hold/Wikidata-Queries/extract_sample_queries/examples/Mountains/Mountains_over_8000_meters_elevation.rq`
- `/Users/arwest/Hold/Wikidata-Queries/extract_sample_queries/examples/Museums/All_museums_in_Barcelona_with_coordinates.rq`
- `/Users/arwest/Hold/Wikidata-Queries/extract_sample_queries/examples/Museums/Louvre_artworks_in_display_cases.rq`
- `/Users/arwest/Hold/Wikidata-Queries/extract_sample_queries/examples/Museums/Museums_in_Antwerp.rq`
- `/Users/arwest/Hold/Wikidata-Queries/extract_sample_queries/examples/Museums/Museums_in_Brittany.rq`
- `/Users/arwest/Hold/Wikidata-Queries/extract_sample_queries/examples/Music/27_club_musicians_who_died_at_age_27.rq`
- `/Users/arwest/Hold/Wikidata-Queries/extract_sample_queries/examples/Music/Composers_and_their_mostused_tonality.rq`
- `/Users/arwest/Hold/Wikidata-Queries/extract_sample_queries/examples/Music/List_tracks_of_an_album_with_links_to_Yandex_Apple_Spotify_Amazon.rq`
- `/Users/arwest/Hold/Wikidata-Queries/extract_sample_queries/examples/Music/List_tracks_of_an_album_with_links_to_Yandex_Apple_Spotify_Amazon_URLs_without_triangles_as_text.rq`
- `/Users/arwest/Hold/Wikidata-Queries/extract_sample_queries/examples/Music/Most_popular_tonality.rq`
- `/Users/arwest/Hold/Wikidata-Queries/extract_sample_queries/examples/Music/Music_composers_by_birth_place.rq`
- `/Users/arwest/Hold/Wikidata-Queries/extract_sample_queries/examples/Music/Musicians_born_in_Rotterdam_the_Netherlands.rq`
- `/Users/arwest/Hold/Wikidata-Queries/extract_sample_queries/examples/Music/Musicians_or_singers_that_have_a_genre_containing_rock.rq`
- `/Users/arwest/Hold/Wikidata-Queries/extract_sample_queries/examples/Music/Paintings_depicting_woodwind_instruments.rq`
- `/Users/arwest/Hold/Wikidata-Queries/extract_sample_queries/examples/Music/Songs_with_longest_melody.rq`
- `/Users/arwest/Hold/Wikidata-Queries/extract_sample_queries/examples/Music/Timeline_of_albums_by_Manu_Chao_and_Mano_Negra.rq`
- `/Users/arwest/Hold/Wikidata-Queries/extract_sample_queries/examples/Pathways/All_human_pathways_from_Wikipathways.rq`
- `/Users/arwest/Hold/Wikidata-Queries/extract_sample_queries/examples/Pathways/Biological_pathways_with_protein_structures_in_the_PDB_database.rq`
- `/Users/arwest/Hold/Wikidata-Queries/extract_sample_queries/examples/Pathways/Gene_and_metabolite_counts_per_pathway.rq`
- `/Users/arwest/Hold/Wikidata-Queries/extract_sample_queries/examples/Pathways/Get_known_variants_reported_in_CIViC_database_Q27612411_of_genes_reported_in_a_Wikipathways_pathway_Bladder_cancer_Q3023.rq`
- `/Users/arwest/Hold/Wikidata-Queries/extract_sample_queries/examples/Pathways/Ranking_of_most_cited_work_in_WikiPathways.rq`
- `/Users/arwest/Hold/Wikidata-Queries/extract_sample_queries/examples/Politics/Current_US_members_of_the_Senate_with_district_party_and_date_they_assumed_office.rq`
- `/Users/arwest/Hold/Wikidata-Queries/extract_sample_queries/examples/Politics/List_of_countries_by_age_of_the_head_of_government.rq`
- `/Users/arwest/Hold/Wikidata-Queries/extract_sample_queries/examples/Politics/List_of_parliament_buildings_with_pictures_by_country.rq`
- `/Users/arwest/Hold/Wikidata-Queries/extract_sample_queries/examples/Politics/Members_of_the_French_National_Assembly_born_out_of_France.rq`
- `/Users/arwest/Hold/Wikidata-Queries/extract_sample_queries/examples/Politics/Number_of_jurisdictions_by_driving_side.rq`
- `/Users/arwest/Hold/Wikidata-Queries/extract_sample_queries/examples/Politics/Timeline_of_mayors_of_Amsterdam_the_Netherlands.rq`
- `/Users/arwest/Hold/Wikidata-Queries/extract_sample_queries/examples/Properties/A_7_level_inverse_tree_of_Property_categories.rq`
- `/Users/arwest/Hold/Wikidata-Queries/extract_sample_queries/examples/Properties/All_properties_with_descriptions_and_aliases_and_types.rq`
- `/Users/arwest/Hold/Wikidata-Queries/extract_sample_queries/examples/Properties/Identifier_properties_present_on_one_item_but_absent_on_another.rq`
- `/Users/arwest/Hold/Wikidata-Queries/extract_sample_queries/examples/Properties/Most_used_properties.rq`
- `/Users/arwest/Hold/Wikidata-Queries/extract_sample_queries/examples/Properties/Properties_connecting_items_of_type_zoo_Q43501_with_items_of_type_Animalia_Q729.rq`
- `/Users/arwest/Hold/Wikidata-Queries/extract_sample_queries/examples/Properties/Properties_grouped_by_their_Wikibase_datatype_Q19798645_with_number_of_properties.rq`
- `/Users/arwest/Hold/Wikidata-Queries/extract_sample_queries/examples/Properties/Properties_grouped_by_their_parent_property.rq`
- `/Users/arwest/Hold/Wikidata-Queries/extract_sample_queries/examples/Properties/Properties_likely_missing_type_constraints.rq`
- `/Users/arwest/Hold/Wikidata-Queries/extract_sample_queries/examples/Properties/Properties_used_to_link_to_instances_of_technical_standard_Q317623.rq`
- `/Users/arwest/Hold/Wikidata-Queries/extract_sample_queries/examples/Properties/Subproperties_of_location_P276.rq`
- `/Users/arwest/Hold/Wikidata-Queries/extract_sample_queries/examples/Proteins/Get_Wikidata_UniprotId_mappings_for_homo_sapiens.rq`
- `/Users/arwest/Hold/Wikidata-Queries/extract_sample_queries/examples/Qualifiers/The_education_of_Douglas_Adams_using_time_qualifiers.rq`
- `/Users/arwest/Hold/Wikidata-Queries/extract_sample_queries/examples/Queries_for_bibliographic_citation_Wikicite/English_common_names_and_information_for_animals_given_their_scientific_names.rq`
- `/Users/arwest/Hold/Wikidata-Queries/extract_sample_queries/examples/Ranks/Ranks_in_the_historical_countries_in_which_Berlin_lay.rq`
- `/Users/arwest/Hold/Wikidata-Queries/extract_sample_queries/examples/References/The_education_of_Douglas_Adams_using_references.rq`
- `/Users/arwest/Hold/Wikidata-Queries/extract_sample_queries/examples/Rivers/Bridges_over_rivers_in_former_government_district_of_Leipzig.rq`
- `/Users/arwest/Hold/Wikidata-Queries/extract_sample_queries/examples/Rivers/Longest_river_of_each_continent.rq`
- `/Users/arwest/Hold/Wikidata-Queries/extract_sample_queries/examples/Rivers/Longest_rivers.rq`
- `/Users/arwest/Hold/Wikidata-Queries/extract_sample_queries/examples/Rivers/Rivers_in_Antarctica.rq`
- `/Users/arwest/Hold/Wikidata-Queries/extract_sample_queries/examples/Science/American_universities_founded_before_the_states_they_reside_in_were_created.rq`
- `/Users/arwest/Hold/Wikidata-Queries/extract_sample_queries/examples/Science/Biological_databases_listed_in_Wikidata_and_if_available_applicable_licenses.rq`
- `/Users/arwest/Hold/Wikidata-Queries/extract_sample_queries/examples/Science/Objects_with_most_mass.rq`
- `/Users/arwest/Hold/Wikidata-Queries/extract_sample_queries/examples/Science/Universities_ranked_by_PageRank_on_English_Wikipedia_federated_query.rq`
- `/Users/arwest/Hold/Wikidata-Queries/extract_sample_queries/examples/Scientific_literature/Galaxies_ordered_by_the_ones_that_are_most_linked_from_scientific_articles.rq`
- `/Users/arwest/Hold/Wikidata-Queries/extract_sample_queries/examples/Scientific_literature/Library_and_Information_Science_journals.rq`
- `/Users/arwest/Hold/Wikidata-Queries/extract_sample_queries/examples/Scientific_literature/Most_popular_subjects_of_scientific_articles.rq`
- `/Users/arwest/Hold/Wikidata-Queries/extract_sample_queries/examples/Scientific_literature/Scientific_journals_with_editors_on_Twitter.rq`
- `/Users/arwest/Hold/Wikidata-Queries/extract_sample_queries/examples/Scientists/Authors_of_scientific_articles_by_occupation.rq`
- `/Users/arwest/Hold/Wikidata-Queries/extract_sample_queries/examples/Scientists/Authors_of_scientific_articles_who_received_a_Nobel_prize.rq`
- `/Users/arwest/Hold/Wikidata-Queries/extract_sample_queries/examples/Scientists/Female_scientists_with_most_number_of_sitelinks_but_not_English_Wikipedia.rq`
- `/Users/arwest/Hold/Wikidata-Queries/extract_sample_queries/examples/Scientists/Inventors_killed_by_their_own_invention.rq`
- `/Users/arwest/Hold/Wikidata-Queries/extract_sample_queries/examples/Scientists/Map_of_institutions_where_Canadian_citizens_got_their_PhD.rq`
- `/Users/arwest/Hold/Wikidata-Queries/extract_sample_queries/examples/Scientists/Most_cited_female_authors.rq`
- `/Users/arwest/Hold/Wikidata-Queries/extract_sample_queries/examples/Scientists/Most_eponymous_mathematicians.rq`
- `/Users/arwest/Hold/Wikidata-Queries/extract_sample_queries/examples/Scientists/Scientists_who_have_worked_together_but_whose_Erdos_numbers_dont_reflect_that.rq`
- `/Users/arwest/Hold/Wikidata-Queries/extract_sample_queries/examples/Scientists/Using_VALUES_for_extracting_scientific_articles_of_specific_authors.rq`
- `/Users/arwest/Hold/Wikidata-Queries/extract_sample_queries/examples/Simple_queries/All_items_with_a_property.rq`
- `/Users/arwest/Hold/Wikidata-Queries/extract_sample_queries/examples/Simple_queries/All_statements_of_an_item_containing_another_item_direct_firstdegree_connections.rq`
- `/Users/arwest/Hold/Wikidata-Queries/extract_sample_queries/examples/Simple_queries/Brightest_stars_with_image.rq`
- `/Users/arwest/Hold/Wikidata-Queries/extract_sample_queries/examples/Simple_queries/Cats.rq`
- `/Users/arwest/Hold/Wikidata-Queries/extract_sample_queries/examples/Simple_queries/Cats_with_pictures.rq`
- `/Users/arwest/Hold/Wikidata-Queries/extract_sample_queries/examples/Simple_queries/Descendants_of_L_L_Zamenhof_that_speak_or_write_Esperanto.rq`
- `/Users/arwest/Hold/Wikidata-Queries/extract_sample_queries/examples/Simple_queries/Facets_of_costume_design.rq`
- `/Users/arwest/Hold/Wikidata-Queries/extract_sample_queries/examples/Simple_queries/Horses_showing_some_info_about_them.rq`
- `/Users/arwest/Hold/Wikidata-Queries/extract_sample_queries/examples/Simple_queries/Humans_born_in_New_York_City.rq`
- `/Users/arwest/Hold/Wikidata-Queries/extract_sample_queries/examples/Simple_queries/Humans_who_died_on_a_specific_date_on_the_English_Wikipedia_ordered_by_label.rq`
- `/Users/arwest/Hold/Wikidata-Queries/extract_sample_queries/examples/Simple_queries/Humans_whose_gender_we_know_we_dont_know.rq`
- `/Users/arwest/Hold/Wikidata-Queries/extract_sample_queries/examples/Simple_queries/Items_about_authors_with_a_Wikispecies_page.rq`
- `/Users/arwest/Hold/Wikidata-Queries/extract_sample_queries/examples/Simple_queries/Items_in_the_Messier_Catalog_with_image.rq`
- `/Users/arwest/Hold/Wikidata-Queries/extract_sample_queries/examples/Simple_queries/Items_with_a_Wikispecies_sitelink.rq`
- `/Users/arwest/Hold/Wikidata-Queries/extract_sample_queries/examples/Simple_queries/Map_of_hackerspaces_using_country_as_color.rq`
- `/Users/arwest/Hold/Wikidata-Queries/extract_sample_queries/examples/Simple_queries/Map_of_the_worlds_sign_languages_with_number_of_practicians.rq`
- `/Users/arwest/Hold/Wikidata-Queries/extract_sample_queries/examples/Simple_queries/Mayors_that_are_any_kind_of_domesticated_animal.rq`
- `/Users/arwest/Hold/Wikidata-Queries/extract_sample_queries/examples/Simple_queries/Mayors_that_are_either_a_dog_a_cat_or_a_chicken.rq`
- `/Users/arwest/Hold/Wikidata-Queries/extract_sample_queries/examples/Simple_queries/Popular_eye_colors_among_humans.rq`
- `/Users/arwest/Hold/Wikidata-Queries/extract_sample_queries/examples/Simple_queries/Recent_events.rq`
- `/Users/arwest/Hold/Wikidata-Queries/extract_sample_queries/examples/Simple_queries/Total_population_in_the_resund_Region.rq`
- `/Users/arwest/Hold/Wikidata-Queries/extract_sample_queries/examples/Simple_queries/Wikidata_items_with_English_spoken_text_audio.rq`
- `/Users/arwest/Hold/Wikidata-Queries/extract_sample_queries/examples/Space/Artist_Images_of_Exoplanets.rq`
- `/Users/arwest/Hold/Wikidata-Queries/extract_sample_queries/examples/Space/Birthplaces_of_astronauts.rq`
- `/Users/arwest/Hold/Wikidata-Queries/extract_sample_queries/examples/Space/List_of_space_probes_with_pictures.rq`
- `/Users/arwest/Hold/Wikidata-Queries/extract_sample_queries/examples/Space/Who_discovered_the_most_asteroids.rq`
- `/Users/arwest/Hold/Wikidata-Queries/extract_sample_queries/examples/Space/Who_discovered_the_most_planets_with_list.rq`
- `/Users/arwest/Hold/Wikidata-Queries/extract_sample_queries/examples/Sports/Mushers_with_neither_a_ranking_in_a_race_nor_a_reason_for_not_finishing_it.rq`
- `/Users/arwest/Hold/Wikidata-Queries/extract_sample_queries/examples/Sports/Winners_of_the_2024_Summer_Olympics.rq`
- `/Users/arwest/Hold/Wikidata-Queries/extract_sample_queries/examples/Subclasses/Some_classes_with_both_physical_and_nonphysical_superclasses.rq`
- `/Users/arwest/Hold/Wikidata-Queries/extract_sample_queries/examples/Taxon/Asterophryinae_parent_taxon_reverse_graph.rq`
- `/Users/arwest/Hold/Wikidata-Queries/extract_sample_queries/examples/US_presidents_causes_of_death/List_of_presidents_with_causes_of_death.rq`
- `/Users/arwest/Hold/Wikidata-Queries/extract_sample_queries/examples/Variants/Counts_of_genevariant_types_sourced_from_the_CIViC_database.rq`
- `/Users/arwest/Hold/Wikidata-Queries/extract_sample_queries/examples/Variants/PubMed_references_in_CIViCdb.rq`
- `/Users/arwest/Hold/Wikidata-Queries/extract_sample_queries/examples/Variants/Variant_counts_by_predictor_type.rq`
- `/Users/arwest/Hold/Wikidata-Queries/extract_sample_queries/examples/Variants/Variants_that_are_associated_with_renal_cell_carcinoma.rq`
- `/Users/arwest/Hold/Wikidata-Queries/extract_sample_queries/examples/Variants/Which_variant_of_which_gene_predicts_a_positive_prognosis_in_colorectal_cancer.rq`
- `/Users/arwest/Hold/Wikidata-Queries/extract_sample_queries/examples/Visual_arts/All_impressionist_painters_that_have_been_in_an_exhibition_together_with_the_amount_of_exhibitions_they_have_been_in.rq`
- `/Users/arwest/Hold/Wikidata-Queries/extract_sample_queries/examples/Visual_arts/Authority_control_properties_usage_for_painters.rq`
- `/Users/arwest/Hold/Wikidata-Queries/extract_sample_queries/examples/Visual_arts/Authority_control_properties_usage_for_paintings.rq`
- `/Users/arwest/Hold/Wikidata-Queries/extract_sample_queries/examples/Visual_arts/Eiffel_Tower_in_art.rq`
- `/Users/arwest/Hold/Wikidata-Queries/extract_sample_queries/examples/Visual_arts/Map_of_all_the_paintings_for_which_we_know_a_location_with_the_count_per_location.rq`
- `/Users/arwest/Hold/Wikidata-Queries/extract_sample_queries/examples/Visual_arts/Map_of_the_locations_of_all_paintings_by_Johannes_Vermeer_with_an_image.rq`
- `/Users/arwest/Hold/Wikidata-Queries/extract_sample_queries/examples/Visual_arts/Monuments_historiques_in_LoireAtlantique.rq`
- `/Users/arwest/Hold/Wikidata-Queries/extract_sample_queries/examples/Visual_arts/Painters_related_to_anonymous_works.rq`
- `/Users/arwest/Hold/Wikidata-Queries/extract_sample_queries/examples/Visual_arts/Painters_type_of_relations_with_anonymous_works.rq`
- `/Users/arwest/Hold/Wikidata-Queries/extract_sample_queries/examples/Visual_arts/Paintings_by_Rembrandt_in_the_Louvre_or_the_Rijkmuseum.rq`
- `/Users/arwest/Hold/Wikidata-Queries/extract_sample_queries/examples/Visual_arts/Sculptures_by_Max_Bill.rq`
- `/Users/arwest/Hold/Wikidata-Queries/extract_sample_queries/examples/Visual_arts/Ten_random_painting_images.rq`
- `/Users/arwest/Hold/Wikidata-Queries/extract_sample_queries/examples/Wikimedia_projects/All_Wikipedia_sites.rq`
- `/Users/arwest/Hold/Wikidata-Queries/extract_sample_queries/examples/Wikimedia_projects/All_languages_with_a_Wikimedia_language_code_P424.rq`
- `/Users/arwest/Hold/Wikidata-Queries/extract_sample_queries/examples/Wikimedia_projects/Canadian_subjects_with_no_English_article_in_Wikipedia.rq`
- `/Users/arwest/Hold/Wikidata-Queries/extract_sample_queries/examples/Wikimedia_projects/Countries_that_have_a_Featured_Article_on_Russian_Wikipedia.rq`
- `/Users/arwest/Hold/Wikidata-Queries/extract_sample_queries/examples/Wikimedia_projects/Countries_that_have_sitelinks_to_enwiki.rq`
- `/Users/arwest/Hold/Wikidata-Queries/extract_sample_queries/examples/Wikimedia_projects/Featured_articles_of_all_Wikimedia_projects.rq`
- `/Users/arwest/Hold/Wikidata-Queries/extract_sample_queries/examples/Wikimedia_projects/Items_with_a_GTAA_id_and_their_articles_on_the_Dutch_and_English_Wikipedia.rq`
- `/Users/arwest/Hold/Wikidata-Queries/extract_sample_queries/examples/Wikimedia_projects/Most_famous_child_of_a_librarian.rq`
- `/Users/arwest/Hold/Wikidata-Queries/extract_sample_queries/examples/Wikimedia_projects/People_born_in_Lisbon_without_articles_on_ptwiki_but_with_articles_on_other_Wikipedias.rq`
- `/Users/arwest/Hold/Wikidata-Queries/extract_sample_queries/examples/Wikimedia_projects/People_deceased_in_2018_ordered_by_the_number_of_sitelinks.rq`
- `/Users/arwest/Hold/Wikidata-Queries/extract_sample_queries/examples/Zika_corpus/Scientific_articles_that_have_subject_Zika_virus_or_fever_and_that_are_used_as_a_reference_in_another_item.rq`

### `SERVICE bd:slice`

- Local matches: 0
- Notes: Slice service documented on the page.

Matching files:
- None in the current `examples/` tree

### `SERVICE wikibase:mwapi`

- Local matches: 1
- Notes: MediaWiki API integration service documented on the page.

Matching files:
- `/Users/arwest/Hold/Wikidata-Queries/extract_sample_queries/examples/Zika_corpus/Scholarly_articles_with_Zika_in_the_item_label.rq`

### `SERVICE gas:service`

- Local matches: 1
- Notes: Graph analytics service documented on the page.

Matching files:
- `/Users/arwest/Hold/Wikidata-Queries/extract_sample_queries/examples/Taxon/Asterophryinae_parent_taxon_reverse_graph.rq`

### `SERVICE bd:sample`

- Local matches: 0
- Notes: Sampling service documented on the page.

Matching files:
- None in the current `examples/` tree

## Supporting Blazegraph-Specific Syntax

- Features in this section: 2
- Total matches across this section: 316

### `hint:Query ...` query hints

- Local matches: 12
- Notes: The page mentions query hints in the named sub-query discussion; they are not a top-level section, but they are Blazegraph-specific syntax used in the example set.

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

### `bd:serviceParam`

- Local matches: 304
- Notes: This parameter mechanism is used to configure Blazegraph/WDQS SERVICE extensions throughout the examples.

Matching files:
- `/Users/arwest/Hold/Wikidata-Queries/extract_sample_queries/examples/Biological_pathway_citation_corpora/Get_the_Pathways_citation_corpus.rq`
- `/Users/arwest/Hold/Wikidata-Queries/extract_sample_queries/examples/Biological_pathway_citation_corpora/Get_the_Reactome_citation_corups.rq`
- `/Users/arwest/Hold/Wikidata-Queries/extract_sample_queries/examples/Biological_pathway_citation_corpora/Get_the_Wikipathways_citation_corpus.rq`
- `/Users/arwest/Hold/Wikidata-Queries/extract_sample_queries/examples/Biology_and_Medicine/Biologists_with_Twitter_accounts.rq`
- `/Users/arwest/Hold/Wikidata-Queries/extract_sample_queries/examples/Biology_and_Medicine/Find_drugs_for_cancers_that_target_genes_related_to_cell_proliferation.rq`
- `/Users/arwest/Hold/Wikidata-Queries/extract_sample_queries/examples/Biology_and_Medicine/List_of_pharmaceutical_drugs_with_picture.rq`
- `/Users/arwest/Hold/Wikidata-Queries/extract_sample_queries/examples/Biology_and_Medicine/Parent_taxons_of_Blue_Whale.rq`
- `/Users/arwest/Hold/Wikidata-Queries/extract_sample_queries/examples/Biology_and_Medicine/Taxons_and_what_they_are_named_after.rq`
- `/Users/arwest/Hold/Wikidata-Queries/extract_sample_queries/examples/Biology_and_Medicine/Threatened_Species_of_Animals_as_per_IUCN_Classification.rq`
- `/Users/arwest/Hold/Wikidata-Queries/extract_sample_queries/examples/CIViC_Corpus/Get_the_CIViC_citation_corpus.rq`
- `/Users/arwest/Hold/Wikidata-Queries/extract_sample_queries/examples/Chemistry/All_CAS_registry_numbers_in_Wikidata.rq`
- `/Users/arwest/Hold/Wikidata-Queries/extract_sample_queries/examples/Chemistry/All_pKa_data_in_Wikidata_and_the_source_titles.rq`
- `/Users/arwest/Hold/Wikidata-Queries/extract_sample_queries/examples/Chemistry/Awarded_Chemistry_Nobel_Prizes.rq`
- `/Users/arwest/Hold/Wikidata-Queries/extract_sample_queries/examples/Chemistry/Boiling_points_of_alkanes.rq`
- `/Users/arwest/Hold/Wikidata-Queries/extract_sample_queries/examples/Chemistry/Chemical_compounds_in_Wikidata_sharing_the_same_CAS_registry_number.rq`
- `/Users/arwest/Hold/Wikidata-Queries/extract_sample_queries/examples/Chemistry/Chemical_elements_and_their_properties.rq`
- `/Users/arwest/Hold/Wikidata-Queries/extract_sample_queries/examples/Chemistry/Colors_of_chemical_compounds.rq`
- `/Users/arwest/Hold/Wikidata-Queries/extract_sample_queries/examples/Chemistry/Images_of_organic_acids.rq`
- `/Users/arwest/Hold/Wikidata-Queries/extract_sample_queries/examples/Chemistry/Solubilities_of_chemicals.rq`
- `/Users/arwest/Hold/Wikidata-Queries/extract_sample_queries/examples/Churches/Cathedrals_in_Paris.rq`
- `/Users/arwest/Hold/Wikidata-Queries/extract_sample_queries/examples/Churches/Churches_in_church_district_Wittenberg.rq`
- `/Users/arwest/Hold/Wikidata-Queries/extract_sample_queries/examples/Churches/Special_church_type_Spitalkirche_in_Germany.rq`
- `/Users/arwest/Hold/Wikidata-Queries/extract_sample_queries/examples/Cities/Border_cities_of_the_world.rq`
- `/Users/arwest/Hold/Wikidata-Queries/extract_sample_queries/examples/Cities/Cities_as_big_as_Eindhoven_give_or_take_1000.rq`
- `/Users/arwest/Hold/Wikidata-Queries/extract_sample_queries/examples/Cities/Cities_connected_by_the_European_route_E40.rq`
- `/Users/arwest/Hold/Wikidata-Queries/extract_sample_queries/examples/Cities/Cities_connected_by_the_TransMongolian_and_TransSiberian_Railway.rq`
- `/Users/arwest/Hold/Wikidata-Queries/extract_sample_queries/examples/Cities/Cities_connected_to_Paramaribo_Suriname_by_main_roads.rq`
- `/Users/arwest/Hold/Wikidata-Queries/extract_sample_queries/examples/Cities/Destinations_from_Antwerp_International_airport.rq`
- `/Users/arwest/Hold/Wikidata-Queries/extract_sample_queries/examples/Cities/Former_capitals.rq`
- `/Users/arwest/Hold/Wikidata-Queries/extract_sample_queries/examples/Cities/Largest_cities_of_the_world.rq`
- `/Users/arwest/Hold/Wikidata-Queries/extract_sample_queries/examples/Cities/Metro_station_of_city_with_template.rq`
- `/Users/arwest/Hold/Wikidata-Queries/extract_sample_queries/examples/Cities/Municipalities_of_the_Basque_Country_without_former_municipalities.rq`
- `/Users/arwest/Hold/Wikidata-Queries/extract_sample_queries/examples/Cities/Population_of_cities_and_towns_in_Denmark_and_their_OSM_relation_id.rq`
- `/Users/arwest/Hold/Wikidata-Queries/extract_sample_queries/examples/Cities/Show_all_Dutch_municipalities_that_share_a_border_with_Alphen_aan_den_Rijn_Q213246_ignoring_rank.rq`
- `/Users/arwest/Hold/Wikidata-Queries/extract_sample_queries/examples/Computer_Science_and_Technology/EReaders_that_support_the_mobipocket_file_format.rq`
- `/Users/arwest/Hold/Wikidata-Queries/extract_sample_queries/examples/Computer_Science_and_Technology/Erdos_Numbers_and_images_of_people_who_have_oral_histories_in_the_Computer_History_Museums_collection.rq`
- `/Users/arwest/Hold/Wikidata-Queries/extract_sample_queries/examples/Computer_Science_and_Technology/Free_and_opensource_software_written_in_Go_programming_language.rq`
- `/Users/arwest/Hold/Wikidata-Queries/extract_sample_queries/examples/Computer_Science_and_Technology/Freeware_games_for_Windows_ordered_from_recent_date.rq`
- `/Users/arwest/Hold/Wikidata-Queries/extract_sample_queries/examples/Computer_Science_and_Technology/List_of_W3C_standards.rq`
- `/Users/arwest/Hold/Wikidata-Queries/extract_sample_queries/examples/Computer_Science_and_Technology/List_of_computer_files_formats.rq`
- `/Users/arwest/Hold/Wikidata-Queries/extract_sample_queries/examples/Computer_Science_and_Technology/Oldest_software.rq`
- `/Users/arwest/Hold/Wikidata-Queries/extract_sample_queries/examples/Computer_Science_and_Technology/Return_a_bubble_chart_of_mediatypes_by_count_of_file_formats.rq`
- `/Users/arwest/Hold/Wikidata-Queries/extract_sample_queries/examples/Computer_Science_and_Technology/Software_applications_ranked_in_descending_order_by_the_number_of_writable_file_formats.rq`
- `/Users/arwest/Hold/Wikidata-Queries/extract_sample_queries/examples/Computer_Science_and_Technology/Software_written_in_Go_programming_language.rq`
- `/Users/arwest/Hold/Wikidata-Queries/extract_sample_queries/examples/Computer_Science_and_Technology/Universities_of_main_programming_language_authors.rq`
- `/Users/arwest/Hold/Wikidata-Queries/extract_sample_queries/examples/Computer_Science_and_Technology/Websites_with_OpenAPI_endpoints.rq`
- `/Users/arwest/Hold/Wikidata-Queries/extract_sample_queries/examples/Countries/Countries_sorted_by_population.rq`
- `/Users/arwest/Hold/Wikidata-Queries/extract_sample_queries/examples/Countries/Country_populations_together_with_total_city_populations.rq`
- `/Users/arwest/Hold/Wikidata-Queries/extract_sample_queries/examples/Countries/Languages_and_dialects_spoken_in_the_Netherlands_with_their_optional_Wikipedia_editions.rq`
- `/Users/arwest/Hold/Wikidata-Queries/extract_sample_queries/examples/Countries/Largest_cities_per_country.rq`
- `/Users/arwest/Hold/Wikidata-Queries/extract_sample_queries/examples/Countries/List_of_presentday_countries_and_capitals.rq`
- `/Users/arwest/Hold/Wikidata-Queries/extract_sample_queries/examples/Countries/Papers_about_Wikidata.rq`
- `/Users/arwest/Hold/Wikidata-Queries/extract_sample_queries/examples/Countries/UN_member_states.rq`
- `/Users/arwest/Hold/Wikidata-Queries/extract_sample_queries/examples/Countries/Wikidata_people_per_million_inhabitants_for_all_EU_countries.rq`
- `/Users/arwest/Hold/Wikidata-Queries/extract_sample_queries/examples/Culture/Birthplaces_of_Europeana280_artists.rq`
- `/Users/arwest/Hold/Wikidata-Queries/extract_sample_queries/examples/Culture/Common_phrases.rq`
- `/Users/arwest/Hold/Wikidata-Queries/extract_sample_queries/examples/Culture/Distribution_of_public_art_by_place.rq`
- `/Users/arwest/Hold/Wikidata-Queries/extract_sample_queries/examples/Culture/List_of_theatre_plays.rq`
- `/Users/arwest/Hold/Wikidata-Queries/extract_sample_queries/examples/Culture/Padua_University_Rectors_by_dates.rq`
- `/Users/arwest/Hold/Wikidata-Queries/extract_sample_queries/examples/Culture/Top_100_podcasts_by_number_of_statements.rq`
- `/Users/arwest/Hold/Wikidata-Queries/extract_sample_queries/examples/Demography/Average_lifespan_by_occupation.rq`
- `/Users/arwest/Hold/Wikidata-Queries/extract_sample_queries/examples/Demography/Birthplaces_of_humans_named_Antoine.rq`
- `/Users/arwest/Hold/Wikidata-Queries/extract_sample_queries/examples/Demography/People_of_same_year_of_birth_by_occupation.rq`
- `/Users/arwest/Hold/Wikidata-Queries/extract_sample_queries/examples/Demography/Population_growth_in_Suriname_from_1960_onward.rq`
- `/Users/arwest/Hold/Wikidata-Queries/extract_sample_queries/examples/Demography/Thingspeople_with_most_children.rq`
- `/Users/arwest/Hold/Wikidata-Queries/extract_sample_queries/examples/Diseases/Infectious_diseases_with_their_human_minimum_and_maximum_incubation_time_in_days.rq`
- `/Users/arwest/Hold/Wikidata-Queries/extract_sample_queries/examples/Distances_between_any_two_cities_or_municipalities_in_an_area/grouped_by_dist_range_colorcoded.rq`
- `/Users/arwest/Hold/Wikidata-Queries/extract_sample_queries/examples/Distances_between_any_two_cities_or_municipalities_in_an_area/grouped_per_municipality_on_xaxis_alphabetically.rq`
- `/Users/arwest/Hold/Wikidata-Queries/extract_sample_queries/examples/Distances_between_any_two_cities_or_municipalities_in_an_area/grouped_per_municipality_on_xaxis_animated_by_fixed_dist_range_groups.rq`
- `/Users/arwest/Hold/Wikidata-Queries/extract_sample_queries/examples/Distances_between_any_two_cities_or_municipalities_in_an_area/grouped_per_municipality_on_xaxis_animated_by_ranked_dist_farthest_2nd_farthest.rq`
- `/Users/arwest/Hold/Wikidata-Queries/extract_sample_queries/examples/Distances_between_any_two_cities_or_municipalities_in_an_area/grouped_per_municipality_on_xaxis_animated_per_municipality_on_xaxis.rq`
- `/Users/arwest/Hold/Wikidata-Queries/extract_sample_queries/examples/Distances_between_any_two_cities_or_municipalities_in_an_area/grouped_per_municipality_on_xaxis_animated_per_municipality_on_zaxis.rq`
- `/Users/arwest/Hold/Wikidata-Queries/extract_sample_queries/examples/Distances_between_any_two_cities_or_municipalities_in_an_area/grouped_per_municipality_on_xaxis_by_sum_of_dist.rq`
- `/Users/arwest/Hold/Wikidata-Queries/extract_sample_queries/examples/Dutch_general_election_2017/Candidates_for_the_Dutch_general_election_2017_living_abroad.rq`
- `/Users/arwest/Hold/Wikidata-Queries/extract_sample_queries/examples/Dutch_general_election_2017/Candidates_for_the_Dutch_general_election_2017_living_in_Antwerp_Belgium.rq`
- `/Users/arwest/Hold/Wikidata-Queries/extract_sample_queries/examples/Dutch_general_election_2017/Candidates_for_the_Dutch_general_election_in_2017.rq`
- `/Users/arwest/Hold/Wikidata-Queries/extract_sample_queries/examples/Dutch_general_election_2017/Gender_distribution_in_the_candidates_for_the_Dutch_general_election_2017.rq`
- `/Users/arwest/Hold/Wikidata-Queries/extract_sample_queries/examples/Dutch_general_election_2017/Occupations_of_candidates_of_the_Dutch_general_election_2017.rq`
- `/Users/arwest/Hold/Wikidata-Queries/extract_sample_queries/examples/Economics_and_Business/Business_listed_on_NYSE_and_NASDAQ_along_with_their_ticker_symbols.rq`
- `/Users/arwest/Hold/Wikidata-Queries/extract_sample_queries/examples/Economics_and_Business/Countries_that_have_adopted_a_cryptocurrency_as_legal_tender.rq`
- `/Users/arwest/Hold/Wikidata-Queries/extract_sample_queries/examples/Economics_and_Business/Distinct_billionaires.rq`
- `/Users/arwest/Hold/Wikidata-Queries/extract_sample_queries/examples/Economics_and_Business/Human_Development_Index_of_specified_countrys.rq`
- `/Users/arwest/Hold/Wikidata-Queries/extract_sample_queries/examples/Education/Rankings_of_universityes.rq`
- `/Users/arwest/Hold/Wikidata-Queries/extract_sample_queries/examples/External_Federation_Queries_using_Wikidata_plus_Other_sources/Getting_basic_information_for_one_item.rq`
- `/Users/arwest/Hold/Wikidata-Queries/extract_sample_queries/examples/External_Federation_Queries_using_Wikidata_plus_Other_sources/UK_Parliament_constituencies_whose_official_point_location_is_more_than_10km_from_the_location_in_Wikidata.rq`
- `/Users/arwest/Hold/Wikidata-Queries/extract_sample_queries/examples/External_identifiers/Swedish_municipalities_which_changed_their_municipality_identifier_at_some_point.rq`
- `/Users/arwest/Hold/Wikidata-Queries/extract_sample_queries/examples/Fictional_characters/Fictional_characters_whose_birthdeath_date_is_in_the_current_decade.rq`
- `/Users/arwest/Hold/Wikidata-Queries/extract_sample_queries/examples/Fictional_characters/Fictional_subjects_of_the_Marvel_Universe.rq`
- `/Users/arwest/Hold/Wikidata-Queries/extract_sample_queries/examples/Fictional_characters/Pokemon.rq`
- `/Users/arwest/Hold/Wikidata-Queries/extract_sample_queries/examples/Figshare_citations/Wikidata_statement_with_a_reference_to_data_in_Figshare_of_which_a_Wikicite_item_exists.rq`
- `/Users/arwest/Hold/Wikidata-Queries/extract_sample_queries/examples/Figshare_citations/Wikidata_statements_with_a_reference_to_a_Figshare_DOI_Q28061352.rq`
- `/Users/arwest/Hold/Wikidata-Queries/extract_sample_queries/examples/Film_and_television/Academy_award_data.rq`
- `/Users/arwest/Hold/Wikidata-Queries/extract_sample_queries/examples/Film_and_television/Actors_who_played_the_same_role_more_than_40_years_apart.rq`
- `/Users/arwest/Hold/Wikidata-Queries/extract_sample_queries/examples/Film_and_television/All_Dr_Who_performers.rq`
- `/Users/arwest/Hold/Wikidata-Queries/extract_sample_queries/examples/Film_and_television/Characters_portrayed_by_most_actors.rq`
- `/Users/arwest/Hold/Wikidata-Queries/extract_sample_queries/examples/Film_and_television/Contemporary_Indian_actresses.rq`
- `/Users/arwest/Hold/Wikidata-Queries/extract_sample_queries/examples/Film_and_television/Film_directors_ranked_by_number_of_sitelinks_multiplied_by_their_number_of_films.rq`
- `/Users/arwest/Hold/Wikidata-Queries/extract_sample_queries/examples/Film_and_television/Films_of_directors_by_their_English_Wikipedia_name.rq`
- `/Users/arwest/Hold/Wikidata-Queries/extract_sample_queries/examples/Film_and_television/List_of_actors_with_pictures_with_year_of_birth_andor_death.rq`
- `/Users/arwest/Hold/Wikidata-Queries/extract_sample_queries/examples/Film_and_television/Main_subjects_of_West_Wing_episodes.rq`
- `/Users/arwest/Hold/Wikidata-Queries/extract_sample_queries/examples/Film_and_television/Movies_and_their_narrative_location_on_a_map.rq`
- `/Users/arwest/Hold/Wikidata-Queries/extract_sample_queries/examples/Film_and_television/Movies_released_in_2017.rq`
- `/Users/arwest/Hold/Wikidata-Queries/extract_sample_queries/examples/Film_and_television/Movies_with_Bud_Spencer.rq`
- `/Users/arwest/Hold/Wikidata-Queries/extract_sample_queries/examples/Film_and_television/Number_of_handed_out_Academy_Awards_per_award_type.rq`
- `/Users/arwest/Hold/Wikidata-Queries/extract_sample_queries/examples/Film_and_television/People_that_received_both_Academy_Award_and_Nobel_Prize.rq`
- `/Users/arwest/Hold/Wikidata-Queries/extract_sample_queries/examples/Film_and_television/The_Simpsons_television_series_episodes_list_by_season.rq`
- `/Users/arwest/Hold/Wikidata-Queries/extract_sample_queries/examples/Film_and_television/Winner_of_the_Academy_Awards_by_Award_and_Time.rq`
- `/Users/arwest/Hold/Wikidata-Queries/extract_sample_queries/examples/Food_Drink/German_breweries.rq`
- `/Users/arwest/Hold/Wikidata-Queries/extract_sample_queries/examples/Food_Drink/Sandwich_ingredients.rq`
- `/Users/arwest/Hold/Wikidata-Queries/extract_sample_queries/examples/Food_Drink/Sandwiches.rq`
- `/Users/arwest/Hold/Wikidata-Queries/extract_sample_queries/examples/Geography/Buildings_in_more_than_one_country.rq`
- `/Users/arwest/Hold/Wikidata-Queries/extract_sample_queries/examples/Geography/City_gates_in_the_Dutch_province_of_Zeeland.rq`
- `/Users/arwest/Hold/Wikidata-Queries/extract_sample_queries/examples/Geography/Emergency_numbers_by_population_using_them.rq`
- `/Users/arwest/Hold/Wikidata-Queries/extract_sample_queries/examples/Geography/German_states_ordered_by_the_number_of_company_headquarters_per_million_inhabitants.rq`
- `/Users/arwest/Hold/Wikidata-Queries/extract_sample_queries/examples/Geography/Glaciers_map.rq`
- `/Users/arwest/Hold/Wikidata-Queries/extract_sample_queries/examples/Geography/How_many_states_this_US_state_borders.rq`
- `/Users/arwest/Hold/Wikidata-Queries/extract_sample_queries/examples/Geography/Items_geographically_located_around_the_Wikimedia_Foundation_office_sorted_by_distance.rq`
- `/Users/arwest/Hold/Wikidata-Queries/extract_sample_queries/examples/Geography/Map_and_list_of_municipalities_in_The_Netherlands.rq`
- `/Users/arwest/Hold/Wikidata-Queries/extract_sample_queries/examples/Geography/Map_of_Broadway_venues.rq`
- `/Users/arwest/Hold/Wikidata-Queries/extract_sample_queries/examples/Geography/Map_of_places_mentioned_in_travel_stories_with_text_in_French_accessible_online.rq`
- `/Users/arwest/Hold/Wikidata-Queries/extract_sample_queries/examples/Geography/Metro_stations_of_Paris_Metro_Line_1_Q13224_in_Paris.rq`
- `/Users/arwest/Hold/Wikidata-Queries/extract_sample_queries/examples/Geography/Places_in_Antarctica_more_than_3000km_away_from_the_South_Pole.rq`
- `/Users/arwest/Hold/Wikidata-Queries/extract_sample_queries/examples/Geography/Places_that_are_below_10_meters_above_sea_level.rq`
- `/Users/arwest/Hold/Wikidata-Queries/extract_sample_queries/examples/Geography/Places_within_1km_of_the_Empire_State_Building.rq`
- `/Users/arwest/Hold/Wikidata-Queries/extract_sample_queries/examples/Geography/Select_French_municipalities_by_INSEE_code_select_by_identifier.rq`
- `/Users/arwest/Hold/Wikidata-Queries/extract_sample_queries/examples/Geography/Streets_in_France_without_a_city.rq`
- `/Users/arwest/Hold/Wikidata-Queries/extract_sample_queries/examples/Geography/Streets_named_after_a_person.rq`
- `/Users/arwest/Hold/Wikidata-Queries/extract_sample_queries/examples/Geography/Ten_largest_islands_in_the_world.rq`
- `/Users/arwest/Hold/Wikidata-Queries/extract_sample_queries/examples/Geography/Things_located_where_the_equator_meets_the_prime_meridian.rq`
- `/Users/arwest/Hold/Wikidata-Queries/extract_sample_queries/examples/History/All_events_that_occured_on_20010911.rq`
- `/Users/arwest/Hold/Wikidata-Queries/extract_sample_queries/examples/History/Ancestors_of_WillemAlexander_of_the_Netherlands.rq`
- `/Users/arwest/Hold/Wikidata-Queries/extract_sample_queries/examples/History/Animals_that_were_executed.rq`
- `/Users/arwest/Hold/Wikidata-Queries/extract_sample_queries/examples/History/Everything_with_a_time_property_on_a_given_date.rq`
- `/Users/arwest/Hold/Wikidata-Queries/extract_sample_queries/examples/History/French_heads_of_government_by_length_of_service.rq`
- `/Users/arwest/Hold/Wikidata-Queries/extract_sample_queries/examples/History/List_of_countries_in_1754.rq`
- `/Users/arwest/Hold/Wikidata-Queries/extract_sample_queries/examples/History/List_of_popes.rq`
- `/Users/arwest/Hold/Wikidata-Queries/extract_sample_queries/examples/History/List_of_suicide_attacks.rq`
- `/Users/arwest/Hold/Wikidata-Queries/extract_sample_queries/examples/History/List_of_torture_devices.rq`
- `/Users/arwest/Hold/Wikidata-Queries/extract_sample_queries/examples/History/Most_prolific_fathers.rq`
- `/Users/arwest/Hold/Wikidata-Queries/extract_sample_queries/examples/History/People_elevated_in_the_public_domain_in_2020_life50_years.rq`
- `/Users/arwest/Hold/Wikidata-Queries/extract_sample_queries/examples/History/People_who_died_by_burning_on_a_timeline.rq`
- `/Users/arwest/Hold/Wikidata-Queries/extract_sample_queries/examples/History/People_who_lived_in_the_same_period_as_another_person.rq`
- `/Users/arwest/Hold/Wikidata-Queries/extract_sample_queries/examples/History/People_who_were_stateless_for_some_time.rq`
- `/Users/arwest/Hold/Wikidata-Queries/extract_sample_queries/examples/History/Periods_of_Japanese_history_and_what_they_were_named_after.rq`
- `/Users/arwest/Hold/Wikidata-Queries/extract_sample_queries/examples/History/Poets_who_were_through_An_Lushan_Rebellion.rq`
- `/Users/arwest/Hold/Wikidata-Queries/extract_sample_queries/examples/History/Popes_with_children.rq`
- `/Users/arwest/Hold/Wikidata-Queries/extract_sample_queries/examples/History/Population_in_Europe_after_1960.rq`
- `/Users/arwest/Hold/Wikidata-Queries/extract_sample_queries/examples/History/Presidents_and_spouses.rq`
- `/Users/arwest/Hold/Wikidata-Queries/extract_sample_queries/examples/History/Years_with_3_popes.rq`
- `/Users/arwest/Hold/Wikidata-Queries/extract_sample_queries/examples/Humans_without_children/Including_nontruthy_values.rq`
- `/Users/arwest/Hold/Wikidata-Queries/extract_sample_queries/examples/Humans_without_children/Only_truthy_values.rq`
- `/Users/arwest/Hold/Wikidata-Queries/extract_sample_queries/examples/Internal_Federation_Adapting_to_the_Graph_split/Articles_by_Lydia_Pintscher_using_the_wikibaselabel_SERVICE_in_both_graphs.rq`
- `/Users/arwest/Hold/Wikidata-Queries/extract_sample_queries/examples/Internal_Federation_Adapting_to_the_Graph_split/Birthdays_of_authors_in_the_Wikidata_The_Making_Of_article_using_hint_to_avoid_timeout.rq`
- `/Users/arwest/Hold/Wikidata-Queries/extract_sample_queries/examples/Lexeme_queries/Demonyms_on_map.rq`
- `/Users/arwest/Hold/Wikidata-Queries/extract_sample_queries/examples/Lexeme_queries/German_picture_dictionary_for_young_children.rq`
- `/Users/arwest/Hold/Wikidata-Queries/extract_sample_queries/examples/Lexeme_queries/Lexeme_languages_by_number_of_usage_examples.rq`
- `/Users/arwest/Hold/Wikidata-Queries/extract_sample_queries/examples/Lexeme_queries/Lexemes_describing_a_color.rq`
- `/Users/arwest/Hold/Wikidata-Queries/extract_sample_queries/examples/Lexeme_queries/Lexemes_that_means_water_ordered_by_language.rq`
- `/Users/arwest/Hold/Wikidata-Queries/extract_sample_queries/examples/Lexeme_queries/Pictures_of_noun_lexemes_in_English_picture_dictionary_a_la_Wikidata.rq`
- `/Users/arwest/Hold/Wikidata-Queries/extract_sample_queries/examples/Lexeme_queries/Senses_on_English_lexemes_with_an_offensive_or_profanity_style_statement.rq`
- `/Users/arwest/Hold/Wikidata-Queries/extract_sample_queries/examples/Lexeme_queries/The_100_most_translated_concepts_in_the_Lexeme_namespace.rq`
- `/Users/arwest/Hold/Wikidata-Queries/extract_sample_queries/examples/Literature/Authors_with_United_States_citizenship_without_a_Goodreads_identifier.rq`
- `/Users/arwest/Hold/Wikidata-Queries/extract_sample_queries/examples/Literature/Birth_places_of_German_poets.rq`
- `/Users/arwest/Hold/Wikidata-Queries/extract_sample_queries/examples/Literature/Books_by_a_given_Author_including_genres_and_date_of_first_publication.rq`
- `/Users/arwest/Hold/Wikidata-Queries/extract_sample_queries/examples/Literature/List_of_authors_unsuccessfully_nominated_for_Nobel_prize_in_literature.rq`
- `/Users/arwest/Hold/Wikidata-Queries/extract_sample_queries/examples/Literature/List_of_digital_libraries_in_the_world.rq`
- `/Users/arwest/Hold/Wikidata-Queries/extract_sample_queries/examples/Literature/Map_of_Libraries_in_Canada.rq`
- `/Users/arwest/Hold/Wikidata-Queries/extract_sample_queries/examples/Literature/Occupation_writer_language_Belarussian_died_more_than_50_years_so_his_books_now_in_public_domain.rq`
- `/Users/arwest/Hold/Wikidata-Queries/extract_sample_queries/examples/Literature/Poets_and_monarchs.rq`
- `/Users/arwest/Hold/Wikidata-Queries/extract_sample_queries/examples/Literature/Text_by_author_containing_caseinsensitive_title_with_optional_cover_image.rq`
- `/Users/arwest/Hold/Wikidata-Queries/extract_sample_queries/examples/Literature/Works_by_women_that_were_born_between_1800_and_1900_are_in_the_WomenWriters_database_and_are_translated.rq`
- `/Users/arwest/Hold/Wikidata-Queries/extract_sample_queries/examples/Math/Mathematical_proofs.rq`
- `/Users/arwest/Hold/Wikidata-Queries/extract_sample_queries/examples/Math/Timeline_of_death_of_mathematicans_and_their_theorems.rq`
- `/Users/arwest/Hold/Wikidata-Queries/extract_sample_queries/examples/Metabolites/Metabolitemetabolite_interactions_mostly_conversions_and_their_pKa_change_federated_query.rq`
- `/Users/arwest/Hold/Wikidata-Queries/extract_sample_queries/examples/Metabolites/Metabolites_and_the_species_where_they_are_found_in.rq`
- `/Users/arwest/Hold/Wikidata-Queries/extract_sample_queries/examples/Mountains/Highest_mountains_in_the_universe_with_units.rq`
- `/Users/arwest/Hold/Wikidata-Queries/extract_sample_queries/examples/Mountains/Highest_mountains_in_the_universe_with_units_compact_form.rq`
- `/Users/arwest/Hold/Wikidata-Queries/extract_sample_queries/examples/Mountains/Highest_places_on_Earth.rq`
- `/Users/arwest/Hold/Wikidata-Queries/extract_sample_queries/examples/Mountains/Italian_mountains_higher_than_4000_meters.rq`
- `/Users/arwest/Hold/Wikidata-Queries/extract_sample_queries/examples/Mountains/Mons_mountains_with_coordinates_not_located_on_Earth.rq`
- `/Users/arwest/Hold/Wikidata-Queries/extract_sample_queries/examples/Mountains/Mountains_over_8000_meters_elevation.rq`
- `/Users/arwest/Hold/Wikidata-Queries/extract_sample_queries/examples/Museums/All_museums_in_Barcelona_with_coordinates.rq`
- `/Users/arwest/Hold/Wikidata-Queries/extract_sample_queries/examples/Museums/Louvre_artworks_in_display_cases.rq`
- `/Users/arwest/Hold/Wikidata-Queries/extract_sample_queries/examples/Museums/Museums_in_Antwerp.rq`
- `/Users/arwest/Hold/Wikidata-Queries/extract_sample_queries/examples/Museums/Museums_in_Brittany.rq`
- `/Users/arwest/Hold/Wikidata-Queries/extract_sample_queries/examples/Music/27_club_musicians_who_died_at_age_27.rq`
- `/Users/arwest/Hold/Wikidata-Queries/extract_sample_queries/examples/Music/Composers_and_their_mostused_tonality.rq`
- `/Users/arwest/Hold/Wikidata-Queries/extract_sample_queries/examples/Music/List_tracks_of_an_album_with_links_to_Yandex_Apple_Spotify_Amazon.rq`
- `/Users/arwest/Hold/Wikidata-Queries/extract_sample_queries/examples/Music/List_tracks_of_an_album_with_links_to_Yandex_Apple_Spotify_Amazon_URLs_without_triangles_as_text.rq`
- `/Users/arwest/Hold/Wikidata-Queries/extract_sample_queries/examples/Music/Most_popular_tonality.rq`
- `/Users/arwest/Hold/Wikidata-Queries/extract_sample_queries/examples/Music/Music_composers_by_birth_place.rq`
- `/Users/arwest/Hold/Wikidata-Queries/extract_sample_queries/examples/Music/Musicians_born_in_Rotterdam_the_Netherlands.rq`
- `/Users/arwest/Hold/Wikidata-Queries/extract_sample_queries/examples/Music/Musicians_or_singers_that_have_a_genre_containing_rock.rq`
- `/Users/arwest/Hold/Wikidata-Queries/extract_sample_queries/examples/Music/Paintings_depicting_woodwind_instruments.rq`
- `/Users/arwest/Hold/Wikidata-Queries/extract_sample_queries/examples/Music/Songs_with_longest_melody.rq`
- `/Users/arwest/Hold/Wikidata-Queries/extract_sample_queries/examples/Music/Timeline_of_albums_by_Manu_Chao_and_Mano_Negra.rq`
- `/Users/arwest/Hold/Wikidata-Queries/extract_sample_queries/examples/Pathways/All_human_pathways_from_Wikipathways.rq`
- `/Users/arwest/Hold/Wikidata-Queries/extract_sample_queries/examples/Pathways/Biological_pathways_with_protein_structures_in_the_PDB_database.rq`
- `/Users/arwest/Hold/Wikidata-Queries/extract_sample_queries/examples/Pathways/Gene_and_metabolite_counts_per_pathway.rq`
- `/Users/arwest/Hold/Wikidata-Queries/extract_sample_queries/examples/Pathways/Get_known_variants_reported_in_CIViC_database_Q27612411_of_genes_reported_in_a_Wikipathways_pathway_Bladder_cancer_Q3023.rq`
- `/Users/arwest/Hold/Wikidata-Queries/extract_sample_queries/examples/Pathways/Ranking_of_most_cited_work_in_WikiPathways.rq`
- `/Users/arwest/Hold/Wikidata-Queries/extract_sample_queries/examples/Politics/Current_US_members_of_the_Senate_with_district_party_and_date_they_assumed_office.rq`
- `/Users/arwest/Hold/Wikidata-Queries/extract_sample_queries/examples/Politics/List_of_countries_by_age_of_the_head_of_government.rq`
- `/Users/arwest/Hold/Wikidata-Queries/extract_sample_queries/examples/Politics/List_of_parliament_buildings_with_pictures_by_country.rq`
- `/Users/arwest/Hold/Wikidata-Queries/extract_sample_queries/examples/Politics/Members_of_the_French_National_Assembly_born_out_of_France.rq`
- `/Users/arwest/Hold/Wikidata-Queries/extract_sample_queries/examples/Politics/Number_of_jurisdictions_by_driving_side.rq`
- `/Users/arwest/Hold/Wikidata-Queries/extract_sample_queries/examples/Politics/Timeline_of_mayors_of_Amsterdam_the_Netherlands.rq`
- `/Users/arwest/Hold/Wikidata-Queries/extract_sample_queries/examples/Properties/A_7_level_inverse_tree_of_Property_categories.rq`
- `/Users/arwest/Hold/Wikidata-Queries/extract_sample_queries/examples/Properties/All_properties_with_descriptions_and_aliases_and_types.rq`
- `/Users/arwest/Hold/Wikidata-Queries/extract_sample_queries/examples/Properties/Identifier_properties_present_on_one_item_but_absent_on_another.rq`
- `/Users/arwest/Hold/Wikidata-Queries/extract_sample_queries/examples/Properties/Most_used_properties.rq`
- `/Users/arwest/Hold/Wikidata-Queries/extract_sample_queries/examples/Properties/Properties_connecting_items_of_type_zoo_Q43501_with_items_of_type_Animalia_Q729.rq`
- `/Users/arwest/Hold/Wikidata-Queries/extract_sample_queries/examples/Properties/Properties_grouped_by_their_Wikibase_datatype_Q19798645_with_number_of_properties.rq`
- `/Users/arwest/Hold/Wikidata-Queries/extract_sample_queries/examples/Properties/Properties_grouped_by_their_parent_property.rq`
- `/Users/arwest/Hold/Wikidata-Queries/extract_sample_queries/examples/Properties/Properties_likely_missing_type_constraints.rq`
- `/Users/arwest/Hold/Wikidata-Queries/extract_sample_queries/examples/Properties/Properties_used_to_link_to_instances_of_technical_standard_Q317623.rq`
- `/Users/arwest/Hold/Wikidata-Queries/extract_sample_queries/examples/Properties/Subproperties_of_location_P276.rq`
- `/Users/arwest/Hold/Wikidata-Queries/extract_sample_queries/examples/Proteins/Get_Wikidata_UniprotId_mappings_for_homo_sapiens.rq`
- `/Users/arwest/Hold/Wikidata-Queries/extract_sample_queries/examples/Qualifiers/The_education_of_Douglas_Adams_using_time_qualifiers.rq`
- `/Users/arwest/Hold/Wikidata-Queries/extract_sample_queries/examples/Queries_for_bibliographic_citation_Wikicite/English_common_names_and_information_for_animals_given_their_scientific_names.rq`
- `/Users/arwest/Hold/Wikidata-Queries/extract_sample_queries/examples/Ranks/Ranks_in_the_historical_countries_in_which_Berlin_lay.rq`
- `/Users/arwest/Hold/Wikidata-Queries/extract_sample_queries/examples/References/The_education_of_Douglas_Adams_using_references.rq`
- `/Users/arwest/Hold/Wikidata-Queries/extract_sample_queries/examples/Rivers/Bridges_over_rivers_in_former_government_district_of_Leipzig.rq`
- `/Users/arwest/Hold/Wikidata-Queries/extract_sample_queries/examples/Rivers/Longest_river_of_each_continent.rq`
- `/Users/arwest/Hold/Wikidata-Queries/extract_sample_queries/examples/Rivers/Longest_rivers.rq`
- `/Users/arwest/Hold/Wikidata-Queries/extract_sample_queries/examples/Rivers/Rivers_in_Antarctica.rq`
- `/Users/arwest/Hold/Wikidata-Queries/extract_sample_queries/examples/Science/American_universities_founded_before_the_states_they_reside_in_were_created.rq`
- `/Users/arwest/Hold/Wikidata-Queries/extract_sample_queries/examples/Science/Biological_databases_listed_in_Wikidata_and_if_available_applicable_licenses.rq`
- `/Users/arwest/Hold/Wikidata-Queries/extract_sample_queries/examples/Science/Objects_with_most_mass.rq`
- `/Users/arwest/Hold/Wikidata-Queries/extract_sample_queries/examples/Science/Universities_ranked_by_PageRank_on_English_Wikipedia_federated_query.rq`
- `/Users/arwest/Hold/Wikidata-Queries/extract_sample_queries/examples/Scientific_literature/Galaxies_ordered_by_the_ones_that_are_most_linked_from_scientific_articles.rq`
- `/Users/arwest/Hold/Wikidata-Queries/extract_sample_queries/examples/Scientific_literature/Library_and_Information_Science_journals.rq`
- `/Users/arwest/Hold/Wikidata-Queries/extract_sample_queries/examples/Scientific_literature/Most_popular_subjects_of_scientific_articles.rq`
- `/Users/arwest/Hold/Wikidata-Queries/extract_sample_queries/examples/Scientific_literature/Scientific_journals_with_editors_on_Twitter.rq`
- `/Users/arwest/Hold/Wikidata-Queries/extract_sample_queries/examples/Scientists/Authors_of_scientific_articles_by_occupation.rq`
- `/Users/arwest/Hold/Wikidata-Queries/extract_sample_queries/examples/Scientists/Authors_of_scientific_articles_who_received_a_Nobel_prize.rq`
- `/Users/arwest/Hold/Wikidata-Queries/extract_sample_queries/examples/Scientists/Female_scientists_with_most_number_of_sitelinks_but_not_English_Wikipedia.rq`
- `/Users/arwest/Hold/Wikidata-Queries/extract_sample_queries/examples/Scientists/Inventors_killed_by_their_own_invention.rq`
- `/Users/arwest/Hold/Wikidata-Queries/extract_sample_queries/examples/Scientists/Map_of_institutions_where_Canadian_citizens_got_their_PhD.rq`
- `/Users/arwest/Hold/Wikidata-Queries/extract_sample_queries/examples/Scientists/Most_cited_female_authors.rq`
- `/Users/arwest/Hold/Wikidata-Queries/extract_sample_queries/examples/Scientists/Most_eponymous_mathematicians.rq`
- `/Users/arwest/Hold/Wikidata-Queries/extract_sample_queries/examples/Scientists/Scientists_who_have_worked_together_but_whose_Erdos_numbers_dont_reflect_that.rq`
- `/Users/arwest/Hold/Wikidata-Queries/extract_sample_queries/examples/Scientists/Using_VALUES_for_extracting_scientific_articles_of_specific_authors.rq`
- `/Users/arwest/Hold/Wikidata-Queries/extract_sample_queries/examples/Simple_queries/All_items_with_a_property.rq`
- `/Users/arwest/Hold/Wikidata-Queries/extract_sample_queries/examples/Simple_queries/All_statements_of_an_item_containing_another_item_direct_firstdegree_connections.rq`
- `/Users/arwest/Hold/Wikidata-Queries/extract_sample_queries/examples/Simple_queries/Brightest_stars_with_image.rq`
- `/Users/arwest/Hold/Wikidata-Queries/extract_sample_queries/examples/Simple_queries/Cats.rq`
- `/Users/arwest/Hold/Wikidata-Queries/extract_sample_queries/examples/Simple_queries/Cats_with_pictures.rq`
- `/Users/arwest/Hold/Wikidata-Queries/extract_sample_queries/examples/Simple_queries/Descendants_of_L_L_Zamenhof_that_speak_or_write_Esperanto.rq`
- `/Users/arwest/Hold/Wikidata-Queries/extract_sample_queries/examples/Simple_queries/Facets_of_costume_design.rq`
- `/Users/arwest/Hold/Wikidata-Queries/extract_sample_queries/examples/Simple_queries/Horses_showing_some_info_about_them.rq`
- `/Users/arwest/Hold/Wikidata-Queries/extract_sample_queries/examples/Simple_queries/Humans_born_in_New_York_City.rq`
- `/Users/arwest/Hold/Wikidata-Queries/extract_sample_queries/examples/Simple_queries/Humans_who_died_on_a_specific_date_on_the_English_Wikipedia_ordered_by_label.rq`
- `/Users/arwest/Hold/Wikidata-Queries/extract_sample_queries/examples/Simple_queries/Humans_whose_gender_we_know_we_dont_know.rq`
- `/Users/arwest/Hold/Wikidata-Queries/extract_sample_queries/examples/Simple_queries/Items_about_authors_with_a_Wikispecies_page.rq`
- `/Users/arwest/Hold/Wikidata-Queries/extract_sample_queries/examples/Simple_queries/Items_in_the_Messier_Catalog_with_image.rq`
- `/Users/arwest/Hold/Wikidata-Queries/extract_sample_queries/examples/Simple_queries/Items_with_a_Wikispecies_sitelink.rq`
- `/Users/arwest/Hold/Wikidata-Queries/extract_sample_queries/examples/Simple_queries/Map_of_hackerspaces_using_country_as_color.rq`
- `/Users/arwest/Hold/Wikidata-Queries/extract_sample_queries/examples/Simple_queries/Map_of_the_worlds_sign_languages_with_number_of_practicians.rq`
- `/Users/arwest/Hold/Wikidata-Queries/extract_sample_queries/examples/Simple_queries/Mayors_that_are_any_kind_of_domesticated_animal.rq`
- `/Users/arwest/Hold/Wikidata-Queries/extract_sample_queries/examples/Simple_queries/Mayors_that_are_either_a_dog_a_cat_or_a_chicken.rq`
- `/Users/arwest/Hold/Wikidata-Queries/extract_sample_queries/examples/Simple_queries/Popular_eye_colors_among_humans.rq`
- `/Users/arwest/Hold/Wikidata-Queries/extract_sample_queries/examples/Simple_queries/Recent_events.rq`
- `/Users/arwest/Hold/Wikidata-Queries/extract_sample_queries/examples/Simple_queries/Total_population_in_the_resund_Region.rq`
- `/Users/arwest/Hold/Wikidata-Queries/extract_sample_queries/examples/Simple_queries/Wikidata_items_with_English_spoken_text_audio.rq`
- `/Users/arwest/Hold/Wikidata-Queries/extract_sample_queries/examples/Space/Artist_Images_of_Exoplanets.rq`
- `/Users/arwest/Hold/Wikidata-Queries/extract_sample_queries/examples/Space/Birthplaces_of_astronauts.rq`
- `/Users/arwest/Hold/Wikidata-Queries/extract_sample_queries/examples/Space/List_of_space_probes_with_pictures.rq`
- `/Users/arwest/Hold/Wikidata-Queries/extract_sample_queries/examples/Space/Who_discovered_the_most_asteroids.rq`
- `/Users/arwest/Hold/Wikidata-Queries/extract_sample_queries/examples/Space/Who_discovered_the_most_planets_with_list.rq`
- `/Users/arwest/Hold/Wikidata-Queries/extract_sample_queries/examples/Sports/Mushers_with_neither_a_ranking_in_a_race_nor_a_reason_for_not_finishing_it.rq`
- `/Users/arwest/Hold/Wikidata-Queries/extract_sample_queries/examples/Sports/Winners_of_the_2024_Summer_Olympics.rq`
- `/Users/arwest/Hold/Wikidata-Queries/extract_sample_queries/examples/Subclasses/Some_classes_with_both_physical_and_nonphysical_superclasses.rq`
- `/Users/arwest/Hold/Wikidata-Queries/extract_sample_queries/examples/Taxon/Asterophryinae_parent_taxon_reverse_graph.rq`
- `/Users/arwest/Hold/Wikidata-Queries/extract_sample_queries/examples/US_presidents_causes_of_death/List_of_presidents_with_causes_of_death.rq`
- `/Users/arwest/Hold/Wikidata-Queries/extract_sample_queries/examples/Variants/Counts_of_genevariant_types_sourced_from_the_CIViC_database.rq`
- `/Users/arwest/Hold/Wikidata-Queries/extract_sample_queries/examples/Variants/PubMed_references_in_CIViCdb.rq`
- `/Users/arwest/Hold/Wikidata-Queries/extract_sample_queries/examples/Variants/Variant_counts_by_predictor_type.rq`
- `/Users/arwest/Hold/Wikidata-Queries/extract_sample_queries/examples/Variants/Variants_that_are_associated_with_renal_cell_carcinoma.rq`
- `/Users/arwest/Hold/Wikidata-Queries/extract_sample_queries/examples/Variants/Which_variant_of_which_gene_predicts_a_positive_prognosis_in_colorectal_cancer.rq`
- `/Users/arwest/Hold/Wikidata-Queries/extract_sample_queries/examples/Visual_arts/All_impressionist_painters_that_have_been_in_an_exhibition_together_with_the_amount_of_exhibitions_they_have_been_in.rq`
- `/Users/arwest/Hold/Wikidata-Queries/extract_sample_queries/examples/Visual_arts/Authority_control_properties_usage_for_painters.rq`
- `/Users/arwest/Hold/Wikidata-Queries/extract_sample_queries/examples/Visual_arts/Authority_control_properties_usage_for_paintings.rq`
- `/Users/arwest/Hold/Wikidata-Queries/extract_sample_queries/examples/Visual_arts/Eiffel_Tower_in_art.rq`
- `/Users/arwest/Hold/Wikidata-Queries/extract_sample_queries/examples/Visual_arts/Map_of_all_the_paintings_for_which_we_know_a_location_with_the_count_per_location.rq`
- `/Users/arwest/Hold/Wikidata-Queries/extract_sample_queries/examples/Visual_arts/Map_of_the_locations_of_all_paintings_by_Johannes_Vermeer_with_an_image.rq`
- `/Users/arwest/Hold/Wikidata-Queries/extract_sample_queries/examples/Visual_arts/Monuments_historiques_in_LoireAtlantique.rq`
- `/Users/arwest/Hold/Wikidata-Queries/extract_sample_queries/examples/Visual_arts/Painters_related_to_anonymous_works.rq`
- `/Users/arwest/Hold/Wikidata-Queries/extract_sample_queries/examples/Visual_arts/Painters_type_of_relations_with_anonymous_works.rq`
- `/Users/arwest/Hold/Wikidata-Queries/extract_sample_queries/examples/Visual_arts/Paintings_by_Rembrandt_in_the_Louvre_or_the_Rijkmuseum.rq`
- `/Users/arwest/Hold/Wikidata-Queries/extract_sample_queries/examples/Visual_arts/Sculptures_by_Max_Bill.rq`
- `/Users/arwest/Hold/Wikidata-Queries/extract_sample_queries/examples/Visual_arts/Ten_random_painting_images.rq`
- `/Users/arwest/Hold/Wikidata-Queries/extract_sample_queries/examples/Wikimedia_projects/All_Wikipedia_sites.rq`
- `/Users/arwest/Hold/Wikidata-Queries/extract_sample_queries/examples/Wikimedia_projects/All_languages_with_a_Wikimedia_language_code_P424.rq`
- `/Users/arwest/Hold/Wikidata-Queries/extract_sample_queries/examples/Wikimedia_projects/Canadian_subjects_with_no_English_article_in_Wikipedia.rq`
- `/Users/arwest/Hold/Wikidata-Queries/extract_sample_queries/examples/Wikimedia_projects/Countries_that_have_a_Featured_Article_on_Russian_Wikipedia.rq`
- `/Users/arwest/Hold/Wikidata-Queries/extract_sample_queries/examples/Wikimedia_projects/Countries_that_have_sitelinks_to_enwiki.rq`
- `/Users/arwest/Hold/Wikidata-Queries/extract_sample_queries/examples/Wikimedia_projects/Featured_articles_of_all_Wikimedia_projects.rq`
- `/Users/arwest/Hold/Wikidata-Queries/extract_sample_queries/examples/Wikimedia_projects/Items_with_a_GTAA_id_and_their_articles_on_the_Dutch_and_English_Wikipedia.rq`
- `/Users/arwest/Hold/Wikidata-Queries/extract_sample_queries/examples/Wikimedia_projects/Most_famous_child_of_a_librarian.rq`
- `/Users/arwest/Hold/Wikidata-Queries/extract_sample_queries/examples/Wikimedia_projects/People_born_in_Lisbon_without_articles_on_ptwiki_but_with_articles_on_other_Wikipedias.rq`
- `/Users/arwest/Hold/Wikidata-Queries/extract_sample_queries/examples/Wikimedia_projects/People_deceased_in_2018_ordered_by_the_number_of_sitelinks.rq`
- `/Users/arwest/Hold/Wikidata-Queries/extract_sample_queries/examples/Zika_corpus/Scholarly_articles_with_Zika_in_the_item_label.rq`
- `/Users/arwest/Hold/Wikidata-Queries/extract_sample_queries/examples/Zika_corpus/Scientific_articles_that_have_subject_Zika_virus_or_fever_and_that_are_used_as_a_reference_in_another_item.rq`

