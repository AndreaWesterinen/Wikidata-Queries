package io.github.andreawesterinen.wikidata.rewriter;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertNull;

import org.apache.jena.query.Query;
import org.apache.jena.query.QueryFactory;
import org.apache.jena.query.Syntax;
import org.junit.jupiter.api.Test;


final class UnsupportedFeatureDetectorTest {
	/** Tests that named-subquery text inside comments, strings, and IRIs is ignored. */
    @Test
    void ignoresNamedSubqueryTextInInertLexicalRegions() {
        String query = "SELECT ?double ?single ?long WHERE {\n"
                + "  BIND(\"WITH { INCLUDE %double }\" AS ?double)\n"
                + "  BIND('INCLUDE %single' AS ?single)\n"
                + "  BIND(\"\"\"WITH { INCLUDE %long }\"\"\" AS ?long)\n"
                + "  <WITH { INCLUDE %iri }> ?p ?o .\n"
                + "  # WITH { INCLUDE %comment }\n"
                + "}";

        assertNull(UnsupportedFeatureDetector.detectPreParse(query));
    }

	/** Tests direct classification of cataloged Blazegraph services and functions. */
    @Test
    void classifiesEveryCatalogedService() {
        String[][] services = {
            {"http://www.bigdata.com/rdf/gas#service", "GAS service"},
            {"http://wikiba.se/ontology#box", "wikibase:box service"},
            {"http://wikiba.se/ontology#around", "wikibase:around service"},
            {"http://www.bigdata.com/rdf#sample", "bd:sample service"},
            {"http://www.bigdata.com/rdf#slice", "bd:slice service"},
            {"http://wikiba.se/ontology#mwapi", "wikibase:mwapi service"}
        };

        for (String[] service : services) {
            Query query = parse("SELECT * WHERE { SERVICE <" + service[0]
                    + "> { ?s ?p ?o } }");
            assertUnsupported(UnsupportedFeatureDetector.detectPostParse(query),
                    "post-ARQ", service[1]);
        }
    }

    @Test
    void classifiesEveryCatalogedFunction() {
        String[][] functions = {
            {"http://www.opengis.net/def/function/geosparql/globe", "", "geof:globe function"},
            {"http://www.opengis.net/def/function/geosparql/latitude", "?x",
                    "geof:latitude function"},
            {"http://www.opengis.net/def/function/geosparql/longitude", "?x, ?y",
                    "geof:longitude function"},
            {"http://www.opengis.net/def/function/geosparql/distance", "?x, ?y",
                    "geof:distance function"},
            {"http://wikiba.se/ontology#decodeUri", "?x, ?y, ?z",
                    "wikibase:decodeUri function"}
        };

        for (String[] function : functions) {
            Query query = parse("SELECT (<" + function[0] + ">(" + function[1]
                    + ") AS ?value) WHERE {}");
            assertUnsupported(UnsupportedFeatureDetector.detectPostParse(query),
                    "post-ARQ", function[2]);
        }
    }

	/** Tests identification of unknown services and functions. */
    @Test
    void classifiesUnknownProprietaryServicesAndWikibaseFunctions() {
        Query service = parse("SELECT * WHERE { SERVICE "
                + "<http://www.bigdata.com/rdf#unknown> { ?s ?p ?o } }");
        Query function = parse("SELECT "
                + "(<http://wikiba.se/ontology#unknown>(?a, ?b, ?c, ?d) AS ?value) "
                + "WHERE {}");

        assertUnsupported(UnsupportedFeatureDetector.detectPostParse(service),
                "post-ARQ", "unknown proprietary service");
        assertUnsupported(UnsupportedFeatureDetector.detectPostParse(function),
                "post-ARQ", "unknown Wikibase function");
    }

    /** Tests that implemented SERVICE terms remain available (to be handled). */
    @Test
    void doesNotClassifyImplementedLabelServiceAsUnsupported() {
        Query label = parse("SELECT * WHERE { SERVICE "
                + "<http://wikiba.se/ontology#label> { ?s ?p ?o } }");

        assertNull(UnsupportedFeatureDetector.detectPostParse(label));
    }

    /** Tests that an external SERVICE is not discarded/errored. */
    @Test
    void doesNotClassifyStandardExternalServiceAsProprietary() {
        Query external = parse("SELECT * WHERE { SERVICE "
                + "<https://example.com/sparql> { ?s ?p ?o } }");

        assertNull(UnsupportedFeatureDetector.detectPostParse(external));
    }

	/** Tests that a Blazegraph IRI that should be removed in preparse is caught. */
    @Test
    void classifiesRepresentativeRemainingProprietaryTerm() {
        Query query = parse("SELECT * WHERE { "
                + "?s <http://www.bigdata.com/queryHints#unknown> ?o }");

        assertUnsupported(UnsupportedFeatureDetector.detectPostParse(query),
                "post-ARQ", "query hint");
	}
		
	/** Tests that issues that occur in nested queries are caught. */
    @Test
    void traversesNestedQueriesAndExpressionElements() {
        Query query = parse("SELECT * WHERE { { SELECT ?value WHERE { "
                + "BIND(<http://wikiba.se/ontology#unknown>(?item) AS ?value) "
                + "FILTER(<http://wikiba.se/ontology#other>(?value)) "
                + "} } }");

        assertUnsupported(UnsupportedFeatureDetector.detectPostParse(query),
                "post-ARQ", "unknown Wikibase function");
    }

    private static Query parse(String queryText) {
        return QueryFactory.create(queryText, Syntax.syntaxSPARQL_11);
    }

    private static void assertUnsupported(
            RewriteResult result, String phase, String feature) {
        assertNotNull(result);
        assertEquals("skipped_unsupported", result.status());
        assertNull(result.rewrittenQuery());
        assertEquals(1, result.errors().size());
        assertEquals("unsupported_proprietary_feature", result.errors().get(0)
                .getAsObject().get("code").getAsString().value());
        assertEquals(phase, result.errors().get(0).getAsObject()
                .get("phase").getAsString().value());
        assertEquals(feature, result.errors().get(0).getAsObject()
                .get("feature").getAsString().value());
        assertEquals("after_catalog_change", result.errors().get(0).getAsObject()
                .get("retry_classification").getAsString().value());
    }
}
