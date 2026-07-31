package io.github.andreawesterinen.wikidata.rewriter;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertTrue;

import org.apache.jena.query.QueryFactory;
import org.apache.jena.query.Syntax;
import org.junit.jupiter.api.Test;

/** Tests WDQS parser defaults independently of rewrite rules. */
final class WdqsPrefixesTest {
    @Test
    void expandsOnlyPrefixedNameTokensForImplicitLabelVocabulary() {
        String serialized = "PREFIX  rdfs: <http://www.w3.org/2000/01/rdf-schema#>\n"
                + "SELECT * WHERE { ?s rdfs:label ?o . "
                + "BIND(\"rdfs:label\" AS ?text) }\n";

        String expanded = WdqsPrefixes.expandImplicitLabelOutputPrefixes(
                serialized, "SELECT * WHERE { ?s rdfs:label ?o }");

        assertFalse(expanded.contains("PREFIX  rdfs:"));
        assertTrue(expanded.contains(
                "?s <http://www.w3.org/2000/01/rdf-schema#label> ?o"));
        assertTrue(expanded.contains("\"rdfs:label\""));
    }

    @Test
    void addsUsedDefaultsAndPreservesExplicitBindings() {
        String query = "PREFIX wd: <http://example.com/custom/>\n"
                + "SELECT * WHERE { wd:Q1 wdt:P31 ?item . "
                + "BIND(\"geo:sfWithin and skos:note\" AS ?text) }\n"
                + "# schema:description\n";

        String prepared = WdqsPrefixes.addMissingUsed(query);

        assertTrue(prepared.contains(
                "PREFIX wdt: <http://www.wikidata.org/prop/direct/>"));
        assertTrue(prepared.contains("PREFIX wd: <http://example.com/custom/>"));
        assertFalse(prepared.contains("PREFIX geo:"));
        assertFalse(prepared.contains("PREFIX skos:"));
        assertFalse(prepared.contains("PREFIX schema:"));
        assertEquals(1, occurrences(prepared, "PREFIX wd:"));
        QueryFactory.create(prepared, Syntax.syntaxSPARQL_11);
    }

    @Test
    void suppliesEveryConfiguredPrefixWhenUsed() {
        String names = "wd wdt wdtn wds wdv wdref wdno wdata p ps psv psn "
                + "pq pqv pqn pr prv prn wikibase rdf rdfs owl skos schema "
                + "xsd prov geo geof bd gas hint mwapi";
        StringBuilder values = new StringBuilder("SELECT * WHERE { VALUES ?value {");
        for (String name : names.split(" ")) {
            values.append(' ').append(name).append(":term");
        }
        values.append(" } }");

        String prepared = WdqsPrefixes.addMissingUsed(values.toString());

        for (String name : names.split(" ")) {
            assertTrue(prepared.contains("PREFIX " + name + ": <"), name);
        }
        QueryFactory.create(prepared, Syntax.syntaxSPARQL_11);
    }

    @Test
    void removesInvalidEnginePrefixesFromOutputMapping() {
        org.apache.jena.query.Query query = QueryFactory.create(
                "PREFIX bd: <http://www.bigdata.com/rdf#>\n"
                + "PREFIX gas: <http://www.bigdata.com/rdf/gas#>\n"
                + "PREFIX hint: <http://www.bigdata.com/queryHints#>\n"
                + "PREFIX mwapi: <https://www.mediawiki.org/ontology#API/>\n"
                + "PREFIX customHint: <http://www.bigdata.com/queryHints#>\n"
                + "PREFIX hintRebound: <http://example.com/hint/>\n"
                + "PREFIX wd: <http://www.wikidata.org/entity/>\n"
                + "SELECT * WHERE { VALUES ?item { wd:Q1 } }",
                Syntax.syntaxSPARQL_11);

        WdqsPrefixes.removeInvalidOutputPrefixes(query);

        assertFalse(query.getPrefixMapping().getNsPrefixMap().containsKey("bd"));
        assertFalse(query.getPrefixMapping().getNsPrefixMap().containsKey("gas"));
        assertFalse(query.getPrefixMapping().getNsPrefixMap().containsKey("hint"));
        assertFalse(query.getPrefixMapping().getNsPrefixMap().containsKey("mwapi"));
        assertFalse(query.getPrefixMapping().getNsPrefixMap().containsKey("customHint"));
        assertTrue(query.getPrefixMapping().getNsPrefixMap().containsKey("hintRebound"));
        assertTrue(query.getPrefixMapping().getNsPrefixMap().containsKey("wd"));
    }

    @Test
    void removesForbiddenOutputNameEvenWhenRebound() {
        org.apache.jena.query.Query query = QueryFactory.create(
                "PREFIX hint: <http://example.com/hint/>\n"
                + "SELECT * WHERE { hint:Query hint:optimizer \"None\" }",
                Syntax.syntaxSPARQL_11);

        WdqsPrefixes.removeInvalidOutputPrefixes(query);

        assertFalse(query.getPrefixMapping().getNsPrefixMap().containsKey("hint"));
    }

    @Test
    void removesOnlyUnusedOutputPrefixes() {
        org.apache.jena.query.Query query = QueryFactory.create(
                "PREFIX wd: <http://www.wikidata.org/entity/>\n"
                + "PREFIX wikibase: <http://wikiba.se/ontology#>\n"
                + "SELECT * WHERE { VALUES ?item { wd:Q1 } }",
                Syntax.syntaxSPARQL_11);

        WdqsPrefixes.removeUnusedOutputPrefixes(query);

        assertTrue(query.getPrefixMapping().getNsPrefixMap().containsKey("wd"));
        assertFalse(query.getPrefixMapping().getNsPrefixMap().containsKey("wikibase"));
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
