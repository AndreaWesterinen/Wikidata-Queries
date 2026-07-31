package io.github.andreawesterinen.wikidata.rewriter;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;

import org.apache.jena.atlas.json.JSON;
import org.apache.jena.atlas.json.JsonObject;
import org.junit.jupiter.api.Test;

final class EmbeddedSparqlRewriterTest {
    @Test
    void returnsSingleQueryResultAsJson() {
        JsonObject result = JSON.parse(EmbeddedSparqlRewriter.rewriteJson(
                "probe-1", "SELECT * WHERE { ?s ?p ?o }"));

        assertEquals("probe-1", result.get("query_id").getAsString().value());
        assertEquals("unchanged",
                result.get("rewrite_status").getAsString().value());
        assertEquals("SELECT * WHERE { ?s ?p ?o }",
                result.get("rewritten_query").getAsString().value());
    }

    @Test
    void rejectsNullInputsAtPublicBoundary() {
        assertThrows(NullPointerException.class,
                () -> EmbeddedSparqlRewriter.rewriteJson(null, "ASK {}"));
        assertThrows(NullPointerException.class,
                () -> EmbeddedSparqlRewriter.rewriteJson("probe-1", null));
    }
}
