package io.github.andreawesterinen.wikidata.rewriter;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertNull;
import static org.junit.jupiter.api.Assertions.assertSame;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.api.Assertions.assertTrue;

import java.util.Arrays;
import java.util.Collections;

import org.apache.jena.atlas.json.JsonObject;
import org.apache.jena.riot.RiotException;
import org.junit.jupiter.api.Test;

/** Tests the ordered pre-parse rule handoff. */
final class PreParseRuleTest {
    @Test
    void appliesRulesOnceInOrderAndSkipsNonMatches() {
        String original = "SELECT * WHERE { ?s ?p ?o }";
        String afterFirst = "SELECT * WHERE { ?first ?p ?o }";
        boolean[] noMatchCalled = {false};

        PreParseRule first = query -> step(
                query.replace("?s", "?first"), "first");
        PreParseRule noMatch = query -> {
            noMatchCalled[0] = true;
            assertEquals(afterFirst, query);
            return null;
        };
        PreParseRule last = query -> {
            assertEquals(afterFirst, query);
            return step(query.replace("?o", "?last"), "last");
        };

        RewriteResult result = SparqlRewriteEngine.rewrite(
                original, Arrays.asList(first, noMatch, last));

        assertTrue(noMatchCalled[0]);
        assertEquals("rewritten", result.status());
        assertTrue(result.rewrittenQuery().contains("?first  ?p  ?last"));
        assertEquals(2, result.rewrites().size());
        assertEquals("first", ruleId(result, 0));
        assertEquals("last", ruleId(result, 1));
    }

    @Test
    void returnsTerminalResultWithoutCallingLaterRules() {
        RewriteResult terminal = new RewriteResult("skipped_unsupported", null);
        boolean[] laterCalled = {false};
        PreParseRule later = query -> {
            laterCalled[0] = true;
            return null;
        };

        RewriteResult result = SparqlRewriteEngine.rewrite(
                "SELECT * WHERE { ?s ?p ?o }",
                Arrays.asList(query -> terminal, later));

        assertSame(terminal, result);
        assertFalse(laterCalled[0]);
        assertNull(result.rewrittenQuery());
    }

    @Test
    void tokenizerFailureReturnsOriginalQueryParseDiagnostic() {
        PreParseRule rule = query -> {
            throw new RiotException("invalid token");
        };

        RewriteResult result = SparqlRewriteEngine.rewrite(
                "SELECT * WHERE { ?s ?p ?o }",
                Collections.singletonList(rule));

        assertEquals("parse_error", result.status());
        assertEquals("original_query_parse_error", result.errors().get(0)
                .getAsObject().get("code").getAsString().value());
    }

	/** Tests that successful pre-parse results actually return a rewritten rule. */
    @Test
    void rejectsSuccessfulResultWithoutQueryText() {
        PreParseRule rule = query -> new RewriteResult("rewritten", null);

        assertThrows(IllegalStateException.class, () ->
                SparqlRewriteEngine.rewrite(
                        "SELECT * WHERE { ?s ?p ?o }",
                        Collections.singletonList(rule)));
    }

    @Test
    void rejectsSuccessfulResultWithUnchangedQueryText() {
        PreParseRule rule = query -> new RewriteResult("rewritten", query);

        assertThrows(IllegalStateException.class, () ->
                SparqlRewriteEngine.rewrite(
                        "SELECT * WHERE { ?s ?p ?o }",
                        Collections.singletonList(rule)));
    }

    private static RewriteResult step(String rewrittenQuery, String ruleId) {
        RewriteResult result = new RewriteResult("rewritten", rewrittenQuery);
        JsonObject rewrite = new JsonObject();
        rewrite.put("rule_id", ruleId);
        rewrite.put("variant_id", "test");
        result.addRewrite(rewrite);
        return result;
    }

    private static String ruleId(RewriteResult result, int index) {
        return result.rewrites().get(index).getAsObject()
                .get("rule_id").getAsString().value();
    }
}
