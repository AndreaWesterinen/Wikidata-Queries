package io.github.andreawesterinen.wikidata.rewriter.rules_tests;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertNull;
import static org.junit.jupiter.api.Assertions.assertTrue;

import org.apache.jena.query.Query;
import org.apache.jena.query.QueryFactory;
import org.apache.jena.query.Syntax;
import org.junit.jupiter.api.Test;

import io.github.andreawesterinen.wikidata.rewriter.RewriteResult;
import io.github.andreawesterinen.wikidata.rewriter.rules.WikibaseLabelRewriter;

/** Tests label-rule decisions directly against Jena query objects. */
final class WikibaseLabelRewriterTest {
    private static final String PREFIXES =
            "PREFIX bd: <http://www.bigdata.com/rdf#>\n"
            + "PREFIX rdfs: <http://www.w3.org/2000/01/rdf-schema#>\n"
            + "PREFIX skos: <http://www.w3.org/2004/02/skos/core#>\n"
            + "PREFIX wd: <http://www.wikidata.org/entity/>\n"
            + "PREFIX wdt: <http://www.wikidata.org/prop/direct/>\n"
            + "PREFIX wikibase: <http://wikiba.se/ontology#>\n";

    @Test
    void normalizesLanguagesWithoutLcaseAndAppendsMulOnce() {
        Query query = parse("SELECT ?item ?label WHERE { "
                + "?item wdt:P31 wd:Q5 . SERVICE wikibase:label { "
                + "bd:serviceParam wikibase:language \" DE, ,en\" . "
                + "bd:serviceParam wikibase:language \"de,MUL, \" . "
                + "?item rdfs:label ?label . } }");

        RewriteResult result = WikibaseLabelRewriter.rewrite(query);
        String rewritten = query.serialize(Syntax.syntaxSPARQL_11);

        assertEquals("rewritten", result.status());
        assertEquals(1, occurrences(rewritten, "= \"de\""));
        assertEquals(1, occurrences(rewritten, "= \"en\""));
        assertEquals(1, occurrences(rewritten, "= \"mul\""));
        assertFalse(rewritten.toLowerCase().contains("lcase"));
    }

    @Test
    void preservesLanguagesWithinSeparateUnionGroups() {
        Query query = parse("SELECT ?entity ?entityLabel WHERE { "
                + "{ VALUES ?entity { wd:Q42 } "
                + "SERVICE wikibase:label { "
                + "bd:serviceParam wikibase:language \"de\" . } } "
                + "UNION "
                + "{ VALUES ?entity { wd:Q1 } "
                + "SERVICE wikibase:label { "
                + "bd:serviceParam wikibase:language \"en\" . } } }");

        RewriteResult result = WikibaseLabelRewriter.rewrite(query);
        String rewritten = query.serialize(Syntax.syntaxSPARQL_11);
        String q42Branch = rewritten.substring(
                rewritten.indexOf("wd:Q42"), rewritten.indexOf("wd:Q1"));
        String q1Branch = rewritten.substring(rewritten.indexOf("wd:Q1"));

        assertEquals("rewritten", result.status());
        assertEquals(1, occurrences(rewritten, "= \"de\""));
        assertEquals(1, occurrences(rewritten, "= \"en\""));
        assertEquals(2, occurrences(rewritten, "= \"mul\""));
        assertTrue(q42Branch.contains("= \"de\""));
        assertFalse(q42Branch.contains("= \"en\""));
        assertTrue(q1Branch.contains("= \"en\""));
        assertFalse(q1Branch.contains("= \"de\""));
    }

    @Test
    void guardIsAddedOnlyForPossiblyUnboundEntity() {
        Query required = parse(manualLabel("?item wdt:P31 wd:Q5 ."));
        Query optional = parse(manualLabel("OPTIONAL { wd:Q42 wdt:P25 ?item }") );

        WikibaseLabelRewriter.rewrite(required);
        WikibaseLabelRewriter.rewrite(optional);

        assertFalse(required.serialize().contains("item__label_subject"));
        assertTrue(optional.serialize().contains("item__label_subject"));
    }

    @Test
    void aliasForEntityThatCannotBeBoundIsLeftUnbound() {
        Query query = parse("SELECT ?item ?aliases WHERE { "
                + "SERVICE wikibase:label { "
                + "bd:serviceParam wikibase:language \"en\" . "
                + "?item skos:altLabel ?aliases . } }");

        RewriteResult result = WikibaseLabelRewriter.rewrite(query);

        assertEquals("rewritten", result.status());
        assertTrue(((org.apache.jena.sparql.syntax.ElementGroup) query.getQueryPattern())
                .getElements().isEmpty());
    }

    @Test
    void automaticAskRemovesServiceWithoutGeneratingOutputs() {
        Query query = parse("ASK WHERE { ?item wdt:P31 wd:Q515 . "
                + "SERVICE wikibase:label { "
                + "bd:serviceParam wikibase:language \"en\" . } }");

        RewriteResult result = WikibaseLabelRewriter.rewrite(query);
        String rewritten = query.serialize(Syntax.syntaxSPARQL_11);

        assertEquals("rewritten", result.status());
        assertFalse(rewritten.contains("SERVICE"));
        assertTrue(rewritten.contains("wdt:P31"));
    }

    @Test
    void automaticConstructInfersOutputFromTemplate() {
        Query query = parse("CONSTRUCT { ?item rdfs:label ?itemLabel } WHERE { "
                + "?item wdt:P31 wd:Q515 . SERVICE wikibase:label { "
                + "bd:serviceParam wikibase:language \"en\" . } }");

        RewriteResult result = WikibaseLabelRewriter.rewrite(query);
        String rewritten = query.serialize(Syntax.syntaxSPARQL_11);

        assertEquals("rewritten", result.status());
        assertFalse(rewritten.contains("SERVICE"));
        assertTrue(rewritten.contains("?item__label_value"));
        assertTrue(rewritten.contains("AS ?itemLabel"));
        assertTrue(rewritten.contains("CONSTRUCT"));
    }

    @Test
    void automaticDescribeRemovesConstantOutputTargetAndService() {
        Query query = parse("DESCRIBE ?item ?itemLabel WHERE { "
                + "?item wdt:P31 wd:Q515 . SERVICE wikibase:label { "
                + "bd:serviceParam wikibase:language \"en\" . } }");

        RewriteResult result = WikibaseLabelRewriter.rewrite(query);
        String rewritten = query.serialize(Syntax.syntaxSPARQL_11);

        assertEquals("rewritten", result.status());
        assertFalse(rewritten.contains("SERVICE"));
        assertFalse(rewritten.contains("?itemLabel"));
        assertTrue(rewritten.contains("DESCRIBE ?item"));
    }

    @Test
    void laterUnsupportedServicePreventsEveryPlannedMutation() {
        Query query = parse("SELECT ?item ?label WHERE { ?item wdt:P31 wd:Q5 . "
                + "SERVICE wikibase:label { "
                + "bd:serviceParam wikibase:language \"en\" . "
                + "?item rdfs:label ?label . } "
                + "SERVICE wikibase:label { FILTER(true) } }");
        String original = query.serialize(Syntax.syntaxSPARQL_11);

        RewriteResult result = WikibaseLabelRewriter.rewrite(query);

        assertEquals("skipped_unsupported", result.status());
        assertEquals(original, query.serialize(Syntax.syntaxSPARQL_11));
    }

    @Test
    void fixedExternalIriFallsBackToItsFullString() {
        Query query = parse("SELECT ?label WHERE { SERVICE wikibase:label { "
                + "bd:serviceParam wikibase:language \"en\" . "
                + "<http://example.com/entity/Q1> rdfs:label ?label . } }");

        WikibaseLabelRewriter.rewrite(query);
        String rewritten = query.serialize(Syntax.syntaxSPARQL_11);

        assertTrue(rewritten.contains("str(<http://example.com/entity/Q1>)"));
        assertFalse(rewritten.contains("strafter(str(<http://example.com/entity/Q1>)"));
    }

    @Test
    void generatedVariablesUseDeterministicCollisionSuffixes() {
        Query query = parse("SELECT ?item ?label ?item__label_value WHERE { "
                + "?item wdt:P31 wd:Q5 . BIND(\"occupied\" AS ?item__label_value) "
                + "SERVICE wikibase:label { "
                + "bd:serviceParam wikibase:language \"en\" . "
                + "?item rdfs:label ?label . } }");

        WikibaseLabelRewriter.rewrite(query);

        assertTrue(query.serialize().contains("?item__label_value__1"));
    }

    @Test
    void recordsManualOuterAndAutomaticNestedVariantsInScopeOrder() {
        Query query = parse("SELECT ?item ?iLabel ?country ?countryLabel WHERE { "
                + "?item wdt:P31 wd:Q515 ; wdt:P17 ?country . "
                + "SERVICE wikibase:label { "
                + "bd:serviceParam wikibase:language \"de\" . "
                + "?item rdfs:label ?iLabel . } "
                + "{ SELECT ?country ?countryLabel WHERE { "
                + "?country wdt:P31 wd:Q6256 . "
                + "SERVICE wikibase:label { "
                + "bd:serviceParam wikibase:language \"en\" . } } LIMIT 1 } }");

        RewriteResult result = WikibaseLabelRewriter.rewrite(query);
        String rewritten = query.serialize(Syntax.syntaxSPARQL_11);

        assertEquals("rewritten", result.status());
        assertVariants(result, "manual", "automatic");
        assertFalse(rewritten.contains("SERVICE"));
        assertTrue(rewritten.contains("AS ?iLabel"));
        assertTrue(rewritten.contains("AS ?countryLabel"));
    }

    @Test
    void recordsAutomaticOuterAndManualNestedVariantsInScopeOrder() {
        Query query = parse("SELECT ?item ?itemLabel ?country ?cLabel WHERE { "
                + "?item wdt:P31 wd:Q515 ; wdt:P17 ?country . "
                + "SERVICE wikibase:label { "
                + "bd:serviceParam wikibase:language \"de\" . } "
                + "{ SELECT ?country ?cLabel WHERE { "
                + "?country wdt:P31 wd:Q6256 . "
                + "SERVICE wikibase:label { "
                + "bd:serviceParam wikibase:language \"en\" . "
                + "?country rdfs:label ?cLabel . } } LIMIT 1 } }");

        RewriteResult result = WikibaseLabelRewriter.rewrite(query);
        String rewritten = query.serialize(Syntax.syntaxSPARQL_11);

        assertEquals("rewritten", result.status());
        assertVariants(result, "automatic", "manual");
        assertFalse(rewritten.contains("SERVICE"));
        assertTrue(rewritten.contains("AS ?itemLabel"));
        assertTrue(rewritten.contains("AS ?cLabel"));
    }

    @Test
    void automaticOutputProjectedByNestedScopeIsNotRegeneratedOutsideIt() {
        Query query = parse("SELECT ?item ?itemLabel ?country ?countryLabel WHERE { "
                + "?item wdt:P31 wd:Q515 ; wdt:P17 ?country . "
                + "SERVICE wikibase:label { "
                + "bd:serviceParam wikibase:language \"de\" . } "
                + "{ SELECT ?country ?countryLabel WHERE { "
                + "?country wdt:P31 wd:Q6256 . "
                + "SERVICE wikibase:label { "
                + "bd:serviceParam wikibase:language \"en\" . } } } } ");

        WikibaseLabelRewriter.rewrite(query);
        String rewritten = query.serialize(Syntax.syntaxSPARQL_11);

        assertEquals(1, occurrences(rewritten, "AS ?countryLabel"));
        assertEquals(1, occurrences(rewritten, "= \"en\""));
        assertEquals(1, occurrences(rewritten, "= \"de\""));
    }

    @Test
    void queryWithoutLabelServiceDoesNotMatchRule() {
        Query query = parse("SELECT * WHERE { ?s ?p ?o }");

        assertNull(WikibaseLabelRewriter.rewrite(query));
    }

    private static Query parse(String body) {
        return QueryFactory.create(PREFIXES + body, Syntax.syntaxSPARQL_11);
    }

    private static String manualLabel(String preceding) {
        return "SELECT ?item ?label WHERE { " + preceding
                + " SERVICE wikibase:label { "
                + "bd:serviceParam wikibase:language \"en\" . "
                + "?item rdfs:label ?label . } }";
    }

    private static void assertVariants(RewriteResult result,
            String... expected) {
        assertEquals(expected.length, result.rewrites().size());
        for (int index = 0; index < expected.length; index++) {
            assertEquals(expected[index], result.rewrites().get(index)
                    .getAsObject().get("variant_id").getAsString().value());
        }
    }

    private static int occurrences(String value, String needle) {
        int count = 0;
        int offset = 0;
        while ((offset = value.indexOf(needle, offset)) >= 0) {
            count++;
            offset += needle.length();
        }
        return count;
    }
}
