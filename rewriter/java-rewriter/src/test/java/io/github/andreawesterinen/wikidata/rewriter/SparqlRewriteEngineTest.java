package io.github.andreawesterinen.wikidata.rewriter;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertNull;
import static org.junit.jupiter.api.Assertions.assertSame;
import static org.junit.jupiter.api.Assertions.assertTrue;

import org.apache.jena.query.QueryFactory;
import org.apache.jena.query.Syntax;
import org.junit.jupiter.api.Test;

/** Tests single-query outcomes below the JSONL boundary. */
final class SparqlRewriteEngineTest {
    @Test
    void unchangedQueryPreservesExactInputText() {
        String query = "SELECT * WHERE { ?s ?p ?o }\n";

        RewriteResult result = SparqlRewriteEngine.rewrite(query);

        assertEquals("unchanged", result.status());
        assertSame(query, result.rewrittenQuery());
        assertEquals(0, result.rewrites().size());
    }

    @Test
    void invalidOriginalQueryIsParseError() {
        RewriteResult result = SparqlRewriteEngine.rewrite("SELECT WHERE {");

        assertEquals("parse_error", result.status());
        assertNull(result.rewrittenQuery());
        assertEquals(1, result.errors().size());
        assertEquals("original_query_parse_error", result.errors().get(0)
                .getAsObject().get("code").getAsString().value());
        assertEquals("parse", result.errors().get(0)
                .getAsObject().get("phase").getAsString().value());
        assertFalse(result.errors().get(0).getAsObject().hasKey("rule_id"));
    }

    @Test
    void parserDefaultsEnableRewriteWithoutLeakingInvalidOutputPrefixes() {
        String query = "SELECT ?item ?itemLabel WHERE { ?item wdt:P31 wd:Q5 . "
                + "SERVICE wikibase:label { "
                + "bd:serviceParam wikibase:language \"en\" . } }";

        RewriteResult result = SparqlRewriteEngine.rewrite(query);

        assertEquals("rewritten", result.status());
        assertFalse(result.rewrittenQuery().contains("bd:"));
        assertTrue(result.rewrittenQuery().contains(
                "PREFIX  wd:   <http://www.wikidata.org/entity/>"));
        assertTrue(result.rewrittenQuery().contains(
                "PREFIX  wdt:  <http://www.wikidata.org/prop/direct/>"));
        assertFalse(result.rewrittenQuery().contains("PREFIX  wikibase:"));
        assertFalse(result.rewrittenQuery().contains("PREFIX  geo:"));
    }

    @Test
    void explicitlyDeclaredEnginePrefixesAreAbsentFromRewrittenOutput() {
        String query = "PREFIX bd: <http://www.bigdata.com/rdf#>\n"
                + "PREFIX gas: <http://www.bigdata.com/rdf/gas#>\n"
                + "PREFIX hint: <http://www.bigdata.com/queryHints#>\n"
                + "PREFIX mwapi: <https://www.mediawiki.org/ontology#API/>\n"
                + "SELECT ?item ?itemLabel WHERE { ?item wdt:P31 wd:Q5 . "
                + "SERVICE wikibase:label { "
                + "bd:serviceParam wikibase:language \"en\" . } }";

        RewriteResult result = SparqlRewriteEngine.rewrite(query);

        assertEquals("rewritten", result.status());
        assertFalse(result.rewrittenQuery().contains("bd:"));
        assertFalse(result.rewrittenQuery().contains("gas:"));
        assertFalse(result.rewrittenQuery().contains("hint:"));
        assertFalse(result.rewrittenQuery().contains("mwapi:"));
        QueryFactory.create(result.rewrittenQuery(), Syntax.syntaxSPARQL_11);
    }

	/** Tests that a preparse rule executes first and before a post-parse one. */
    @Test
    void queryHintsAreRemovedBeforeParsingAndRecordedOnce() {
        String query = "SELECT * WHERE { "
                + "hint:Query hint:optimizer \"None\" . "
                + "?s ?p ?o . "
                + "hint:Prior hint:runFirst true . }";

        RewriteResult result = SparqlRewriteEngine.rewrite(query);

        assertEquals("rewritten", result.status());
        assertFalse(result.rewrittenQuery().contains("hint:"));
        assertTrue(result.rewrittenQuery().contains("?s  ?p  ?o"));
        assertEquals(1, result.rewrites().size());
        assertEquals("rewrite-query-hint", result.rewrites().get(0).getAsObject()
                .get("rule_id").getAsString().value());
        assertEquals("default", result.rewrites().get(0).getAsObject()
                .get("variant_id").getAsString().value());
    }

    @Test
    void queryHintComposesBeforeLabelServiceRewrite() {
        String query = "SELECT ?item ?itemLabel WHERE { "
                + "hint:Query hint:optimizer \"None\" . "
                + "?item wdt:P31 wd:Q5 . "
                + "SERVICE wikibase:label { "
                + "bd:serviceParam wikibase:language \"en\" . } }";

        RewriteResult result = SparqlRewriteEngine.rewrite(query);

        assertEquals("rewritten", result.status());
        assertEquals(2, result.rewrites().size());
        assertEquals("rewrite-query-hint", result.rewrites().get(0).getAsObject()
                .get("rule_id").getAsString().value());
        assertEquals("rewrite-wikibase-label-service", result.rewrites().get(1).getAsObject()
                .get("rule_id").getAsString().value());
    }

    /** Tests that FROM and FROM NAMED are not used in the top-level SELECT. */
    @Test
    void topLevelDatasetClausesAreRejected() {
        RewriteResult from = SparqlRewriteEngine.rewrite(
                "SELECT * FROM <http://example.com/default> WHERE { ?s ?p ?o }");
        RewriteResult named = SparqlRewriteEngine.rewrite(
                "SELECT * FROM NAMED <http://example.com/named> WHERE { ?s ?p ?o }");

        assertEquals("skipped_unsupported", from.status());
        assertEquals("skipped_unsupported", named.status());
        assertEquals("unsupported_dataset_clause",
                from.errors().get(0).getAsObject().get("code").getAsString().value());
        assertEquals("unsupported_dataset_clause",
                named.errors().get(0).getAsObject().get("code").getAsString().value());
    }

    /** Tests that named graphs can occur in federated SERVICE requests. */
    @Test
    void federatedNamedGraphPatternRemainsValid() {
        String query = "SELECT * WHERE { SERVICE <https://example.com/sparql> { "
                + "GRAPH <http://example.com/graph> { ?s ?p ?o } } }";

        RewriteResult result = SparqlRewriteEngine.rewrite(query);

        assertEquals("unchanged", result.status());
        assertEquals(0, result.errors().size());
    }
	
	
	/** Tests that unsupported features do not cause partial rewrites. */
    @Test
    void unsupportedFeaturePreventsPartialLabelRewrite() {
        String query = "SELECT ?item ?itemLabel WHERE { "
                + "?item wdt:P31 wd:Q5 . "
                + "SERVICE wikibase:label { "
                + "bd:serviceParam wikibase:language \"en\" . } "
                + "SERVICE wikibase:foo { ?place wdt:P625 ?location . } }";

        RewriteResult result = SparqlRewriteEngine.rewrite(query);

        assertEquals("skipped_unsupported", result.status());
        assertEquals(0, result.rewrites().size());
        assertNull(result.rewrittenQuery());
    }

	/** Tests that 'inert' references to Blazegraph features are not rewritten. */
    @Test
    void featureTextInInertLexicalRegionsDoesNotTriggerDetection() {
        String query = "SELECT ?text WHERE { "
                + "BIND(\"WITH { INCLUDE %x }\" AS ?text) "
                + "# WITH { INCLUDE %comment }\n"
                + "}";
        String rebound = "PREFIX hint: <http://example.com/hint/> "
                + "SELECT * WHERE { hint:Query hint:optimizer \"None\" }";

        assertEquals("unchanged", SparqlRewriteEngine.rewrite(query).status());
        assertEquals("unchanged", SparqlRewriteEngine.rewrite(rebound).status());
    }

    /** Tests that a feature that should be handled/removed in preparse is not maintained. */
    @Test
    void survivingProprietaryTermsAreRejectedAfterPreParseRules() {
        String query = "SELECT * WHERE { ?s hint:unknownPredicate ?o }";

        RewriteResult result = SparqlRewriteEngine.rewrite(query);

        assertEquals("skipped_unsupported", result.status());
        assertEquals("query hint", result.errors().get(0).getAsObject()
                .get("feature").getAsString().value());
    }

	/** Tests successful results. */
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

        assertEquals("rewritten", result.status());
        assertFalse(result.rewrittenQuery().contains("SERVICE"));
        QueryFactory.create(result.rewrittenQuery(), Syntax.syntaxSPARQL_11);
        int defaultPrefix = result.rewrittenQuery().indexOf("PREFIX  :");
        int rdfsPrefix = result.rewrittenQuery().indexOf("PREFIX  rdfs:");
        assertFalse(result.rewrittenQuery().contains("bd:"));
        assertTrue(defaultPrefix < rdfsPrefix);
        assertFalse(result.rewrittenQuery().contains("PREFIX  wikibase:"));
        assertEquals("manual", result.rewrites().get(0).getAsObject()
                .get("variant_id").getAsString().value());
    }

    @Test
    void nestedScopesUseTheirOwnManualAndAutomaticModes() {
        String query = "SELECT ?item ?itemLabel ?country ?cLabel WHERE { "
                + "?item wdt:P31 wd:Q515 ; wdt:P17 ?country . "
                + "SERVICE wikibase:label { "
                + "bd:serviceParam wikibase:language \"de\" . } "
                + "{ SELECT ?country ?cLabel WHERE { "
                + "?country wdt:P31 wd:Q6256 . "
                + "SERVICE wikibase:label { "
                + "bd:serviceParam wikibase:language \"en\" . "
                + "?country rdfs:label ?cLabel . } } } }";

        RewriteResult result = SparqlRewriteEngine.rewrite(query);

        assertEquals("rewritten", result.status());
        assertEquals("automatic", result.rewrites().get(0).getAsObject()
                .get("variant_id").getAsString().value());
        assertEquals("manual", result.rewrites().get(1).getAsObject()
                .get("variant_id").getAsString().value());
        assertFalse(result.rewrittenQuery().contains("PREFIX  rdfs:"));
        assertFalse(result.rewrittenQuery().contains("rdfs:label"));
        assertTrue(result.rewrittenQuery().contains(
                "?item__label_value) = \"de\""));
        assertTrue(result.rewrittenQuery().contains(
                "?country__label_value) = \"en\""));
        assertTrue(result.rewrittenQuery().contains(
                "<http://www.w3.org/2000/01/rdf-schema#label>"));
    }

}
