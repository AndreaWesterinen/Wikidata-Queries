package io.github.andreawesterinen.wikidata.rewriter.rules;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertNull;
import static org.junit.jupiter.api.Assertions.assertTrue;

import org.apache.jena.query.Query;
import org.apache.jena.query.QueryFactory;
import org.apache.jena.query.Syntax;
import org.junit.jupiter.api.Test;

import io.github.andreawesterinen.wikidata.rewriter.RewriteResult;

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
    void guardIsAddedOnlyForPossiblyUnboundEntity() {
        Query required = parse(manualLabel("?item wdt:P31 wd:Q5 ."));
        Query optional = parse(manualLabel("OPTIONAL { wd:Q42 wdt:P25 ?item }") );

        WikibaseLabelRewriter.rewrite(required);
        WikibaseLabelRewriter.rewrite(optional);

        assertFalse(required.serialize().contains("item__label_subject"));
        assertTrue(optional.serialize().contains("item__label_subject"));
    }

    @Test
    void unsafeAliasReturnsTerminalStatusWithoutMutatingQuery() {
        Query query = parse("SELECT ?item ?aliases WHERE { "
                + "SERVICE wikibase:label { "
                + "bd:serviceParam wikibase:language \"en\" . "
                + "?item skos:altLabel ?aliases . } }");
        String original = query.serialize(Syntax.syntaxSPARQL_11);

        RewriteResult result = WikibaseLabelRewriter.rewrite(query);

        assertEquals("skipped_unsupported", result.status());
        assertEquals("wikibase_label_alias_entity_not_safely_constrained",
                result.errors().get(0).getAsObject().get("code").getAsString().value());
        assertEquals(original, query.serialize(Syntax.syntaxSPARQL_11));
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
