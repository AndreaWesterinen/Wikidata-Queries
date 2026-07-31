package io.github.andreawesterinen.wikidata.rewriter.rules_tests;

import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertNull;
import static org.junit.jupiter.api.Assertions.assertTrue;

import org.junit.jupiter.api.Test;

import io.github.andreawesterinen.wikidata.rewriter.RewriteResult;
import io.github.andreawesterinen.wikidata.rewriter.rules.QueryHintRewriter;

/** Tests source-aware removal of Blazegraph query-hint triples. */
final class QueryHintRewriterTest {
    @Test
    void removesEverySupportedSubjectWithAnyPredicates() {
        String query = "SELECT * WHERE {\n"
                + "  hint:Query hint:optimizer \"None\" ; hint:custom 7, 8 .\n"
                + "  ?s ?p ?o .\n"
                + "  hint:Prior <http://example.com/predicate> true .\n"
                + "  hint:Group hint:optimizer \"Runtime\" .\n"
                + "  hint:GroupAndSubGroups hint:analytic true .\n"
                + "  { SELECT * WHERE {\n"
                + "      hint:SubQuery hint:optimizer \"None\" .\n"
                + "      ?nested ?predicate ?object .\n"
                + "  } }\n"
                + "}";

        RewriteResult result = QueryHintRewriter.rewrite(query);

        assertFalse(result.rewrittenQuery().contains("hint:Query"));
        assertFalse(result.rewrittenQuery().contains("hint:Prior"));
        assertFalse(result.rewrittenQuery().contains("hint:Group"));
        assertFalse(result.rewrittenQuery().contains("hint:GroupAndSubGroups"));
        assertFalse(result.rewrittenQuery().contains("hint:SubQuery"));
        assertTrue(result.rewrittenQuery().contains("?s ?p ?o"));
        assertTrue(result.rewrittenQuery().contains("?nested ?predicate ?object"));
    }

    @Test
    void resolvesAliasesAndFullIriSubjects() {
        String query = "PREFIX engine: <http://www.bigdata.com/queryHints#>\n"
                + "SELECT * WHERE {\n"
                + "  engine:Query engine:optimizer \"None\" .\n"
                + "  <http://www.bigdata.com/queryHints#Prior> engine:runFirst true\n"
                + "}";

        RewriteResult result = QueryHintRewriter.rewrite(query);

        assertFalse(result.rewrittenQuery().contains("engine:Query"));
        assertFalse(result.rewrittenQuery().contains("queryHints#Prior"));
    }

    @Test
    void preservesReboundPrefixesAndNonSubjectTerms() {
        String rebound = "PREFIX hint: <http://example.com/>\n"
                + "SELECT * WHERE { hint:Query hint:optimizer \"None\" . }";
        String object = "SELECT * WHERE { ?s ?p hint:Query . }";

        assertNull(QueryHintRewriter.rewrite(rebound));
        assertNull(QueryHintRewriter.rewrite(object));
    }

    @Test
    void ignoresHintTextInCommentsAndStrings() {
        String query = "SELECT ?text WHERE {\n"
                + "  BIND(\"hint:Query hint:optimizer true .\" AS ?text)\n"
                + "  # hint:Prior hint:runFirst true .\n"
                + "}";

        assertNull(QueryHintRewriter.rewrite(query));
    }

    @Test
    void removesHintsInsideNestedScopes() {
        String query = "SELECT * WHERE { OPTIONAL {\n"
                + "  hint:Prior hint:runFirst true .\n"
                + "  ?s ?p ?o .\n"
                + "} }";

        RewriteResult result = QueryHintRewriter.rewrite(query);

        assertFalse(result.rewrittenQuery().contains("hint:Prior"));
        assertTrue(result.rewrittenQuery().contains("?s ?p ?o"));
    }

    @Test
    void preservesOffsetsAfterNonBmpCharacters() {
        String query = "SELECT * WHERE { BIND(\"😀\" AS ?text) "
                + "hint:Query hint:optimizer \"None\" . ?s ?p ?o }";

        RewriteResult result = QueryHintRewriter.rewrite(query);

        assertTrue(result.rewrittenQuery().contains("😀"));
        assertFalse(result.rewrittenQuery().contains("hint:Query"));
        assertTrue(result.rewrittenQuery().contains("?s ?p ?o"));
    }
}
