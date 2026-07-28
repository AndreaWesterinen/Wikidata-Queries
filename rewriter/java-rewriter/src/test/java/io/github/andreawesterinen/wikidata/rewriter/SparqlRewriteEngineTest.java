package io.github.andreawesterinen.wikidata.rewriter;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertNull;
import static org.junit.jupiter.api.Assertions.assertSame;
import static org.junit.jupiter.api.Assertions.assertTrue;

import org.apache.jena.query.QueryFactory;
import org.apache.jena.query.Syntax;
import org.junit.jupiter.api.Test;

/** Tests deterministic single-query outcomes below the JSONL boundary. */
final class SparqlRewriteEngineTest {
    @Test
    void unchangedQueryPreservesExactInputText() {
        String query = "SELECT * WHERE { ?s ?p ?o }\n";

        RewriteResult result = SparqlRewriteEngine.rewrite(query);

        assertEquals("unchanged", result.status);
        assertSame(query, result.rewrittenQuery);
        assertEquals(0, result.rewrites.size());
    }

    @Test
    void invalidOriginalQueryIsParseError() {
        RewriteResult result = SparqlRewriteEngine.rewrite("SELECT WHERE {");

        assertEquals("parse_error", result.status);
        assertNull(result.rewrittenQuery);
    }

    @Test
    void parserDefaultsEnableRewriteWithoutLeakingInvalidOutputPrefixes() {
        String query = "SELECT ?item ?itemLabel WHERE { ?item wdt:P31 wd:Q5 . "
                + "SERVICE wikibase:label { "
                + "bd:serviceParam wikibase:language \"en\" . } }";

        RewriteResult result = SparqlRewriteEngine.rewrite(query);

        assertEquals("rewritten", result.status);
        assertFalse(result.rewrittenQuery.contains("bd:"));
        assertTrue(result.rewrittenQuery.contains(
                "PREFIX  wd:   <http://www.wikidata.org/entity/>"));
        assertTrue(result.rewrittenQuery.contains(
                "PREFIX  wdt:  <http://www.wikidata.org/prop/direct/>"));
        assertTrue(result.rewrittenQuery.contains(
                "PREFIX  wikibase: <http://wikiba.se/ontology#>"));
        assertFalse(result.rewrittenQuery.contains("PREFIX  geo:"));
    }

    @Test
    void explicitlyDeclaredEnginePrefixesAreAbsentFromRewrittenOutput() {
        String query = "PREFIX bd: <http://www.bigdata.com/rdf#>\n"
                + "PREFIX gas: <http://www.bigdata.com/rdf/gas#>\n"
                + "PREFIX hint: <http://www.bigdata.com/queryHints#>\n"
                + "PREFIX mwapi: <http://wikiba.se/ontology#api#>\n"
                + "SELECT ?item ?itemLabel WHERE { ?item wdt:P31 wd:Q5 . "
                + "SERVICE wikibase:label { "
                + "bd:serviceParam wikibase:language \"en\" . } }";

        RewriteResult result = SparqlRewriteEngine.rewrite(query);

        assertEquals("rewritten", result.status);
        assertFalse(result.rewrittenQuery.contains("bd:"));
        assertFalse(result.rewrittenQuery.contains("gas:"));
        assertFalse(result.rewrittenQuery.contains("hint:"));
        assertFalse(result.rewrittenQuery.contains("mwapi:"));
        QueryFactory.create(result.rewrittenQuery, Syntax.syntaxSPARQL_11);
    }

    @Test
    void topLevelDatasetClausesAreRejected() {
        RewriteResult from = SparqlRewriteEngine.rewrite(
                "SELECT * FROM <http://example.com/default> WHERE { ?s ?p ?o }");
        RewriteResult named = SparqlRewriteEngine.rewrite(
                "SELECT * FROM NAMED <http://example.com/named> WHERE { ?s ?p ?o }");

        assertEquals("skipped_unsupported", from.status);
        assertEquals("skipped_unsupported", named.status);
        assertEquals("unsupported_dataset_clause",
                from.errors.get(0).getAsObject().get("code").getAsString().value());
        assertEquals("unsupported_dataset_clause",
                named.errors.get(0).getAsObject().get("code").getAsString().value());
    }

    @Test
    void federatedNamedGraphPatternRemainsValid() {
        String query = "SELECT * WHERE { SERVICE <https://example.com/sparql> { "
                + "GRAPH <http://example.com/graph> { ?s ?p ?o } } }";

        RewriteResult result = SparqlRewriteEngine.rewrite(query);

        assertEquals("unchanged", result.status);
        assertEquals(0, result.errors.size());
    }

    @Test
    void knownUnimplementedFeaturesAreNotReportedAsUnchanged() {
        String[] queries = {
            "SELECT * WHERE { SERVICE wikibase:around { ?s ?p ?o } }",
            "SELECT (geof:distance(?left, ?right) AS ?distance) WHERE {}",
            "SELECT * WHERE { hint:Query hint:optimizer \"None\" . ?s ?p ?o }",
            "WITH { SELECT * WHERE { ?s ?p ?o } } AS %x "
                    + "SELECT * WHERE { INCLUDE %x }"
        };

        for (String query : queries) {
            RewriteResult result = SparqlRewriteEngine.rewrite(query);
            assertEquals("skipped_unsupported", result.status, query);
            assertEquals("unsupported_proprietary_feature",
                    result.errors.get(0).getAsObject().get("code").getAsString().value());
        }
    }

    @Test
    void unsupportedFeaturePreventsPartialLabelRewrite() {
        String query = "SELECT ?item ?itemLabel WHERE { "
                + "?item wdt:P31 wd:Q5 . "
                + "SERVICE wikibase:label { "
                + "bd:serviceParam wikibase:language \"en\" . } "
                + "SERVICE wikibase:around { ?place wdt:P625 ?location . } }";

        RewriteResult result = SparqlRewriteEngine.rewrite(query);

        assertEquals("skipped_unsupported", result.status);
        assertEquals(0, result.rewrites.size());
        assertNull(result.rewrittenQuery);
    }

    @Test
    void featureTextInInertLexicalRegionsDoesNotTriggerDetection() {
        String query = "SELECT ?text WHERE { "
                + "BIND(\"WITH { INCLUDE %x }\" AS ?text) "
                + "# WITH { INCLUDE %comment }\n"
                + "}";
        String rebound = "PREFIX hint: <http://example.com/hint/> "
                + "SELECT * WHERE { hint:Query hint:optimizer \"None\" }";

        assertEquals("unchanged", SparqlRewriteEngine.rewrite(query).status);
        assertEquals("unchanged", SparqlRewriteEngine.rewrite(rebound).status);
    }

    @Test
    void standardGeosparqlDistanceArityIsNotTheBlazegraphExtension() {
        String query = "SELECT (geof:distance(?left, ?right, "
                + "<http://www.opengis.net/def/uom/OGC/1.0/metre>) AS ?distance) "
                + "WHERE {}";
        String unknownService = "PREFIX custom: <http://wikiba.se/ontology#> "
                + "SELECT * WHERE { SERVICE custom:notCataloged { ?s ?p ?o } }";

        assertEquals("unchanged", SparqlRewriteEngine.rewrite(query).status);
        assertEquals("skipped_unsupported",
                SparqlRewriteEngine.rewrite(unknownService).status);
    }

    @Test
    void successfulRewriteIsReparsedAndRecordsVariant() {
        String query = "PREFIX wikibase: <http://wikiba.se/ontology#>\n"
                + "PREFIX rdfs: <http://www.w3.org/2000/01/rdf-schema#>\n"
                + "PREFIX bd: <http://www.bigdata.com/rdf#>\n"
                + "PREFIX : <http://example.com/>\n"
                + "SELECT ?item ?label WHERE { VALUES ?item { <http://example.com/i> } "
                + "SERVICE wikibase:label { "
                + "bd:serviceParam wikibase:language \"en\" . "
                + "?item rdfs:label ?label . } }";

        RewriteResult result = SparqlRewriteEngine.rewrite(query);

        assertEquals("rewritten", result.status);
        assertFalse(result.rewrittenQuery.contains("SERVICE"));
        QueryFactory.create(result.rewrittenQuery, Syntax.syntaxSPARQL_11);
        int defaultPrefix = result.rewrittenQuery.indexOf("PREFIX  :");
        int rdfsPrefix = result.rewrittenQuery.indexOf("PREFIX  rdfs:");
        int wikibasePrefix = result.rewrittenQuery.indexOf("PREFIX  wikibase:");
        assertFalse(result.rewrittenQuery.contains("bd:"));
        assertTrue(defaultPrefix < rdfsPrefix);
        assertTrue(rdfsPrefix < wikibasePrefix);
        assertEquals("manual", result.rewrites.get(0).getAsObject()
                .get("variant_id").getAsString().value());
    }

}
