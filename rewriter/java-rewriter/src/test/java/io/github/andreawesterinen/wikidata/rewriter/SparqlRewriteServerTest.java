package io.github.andreawesterinen.wikidata.rewriter;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;

import org.apache.jena.atlas.json.JSON;
import org.apache.jena.atlas.json.JsonObject;
import org.junit.jupiter.api.Test;

/** Tests JSONL request validation and response envelopes without a subprocess. */
final class SparqlRewriteServerTest {
    @Test
    void successfulEnvelopeContainsHashAndRewriteResult() {
        String query = "SELECT * WHERE { ?s ?p ?o }";
        JsonObject response = SparqlRewriteServer.handle(request(1, query));

        assertEquals("ok", string(response, "status"));
        assertEquals("r1", string(response, "request_id"));
        assertEquals("q1", string(response, "query_id"));
        assertEquals(64, string(response, "query_sha256").length());
        assertEquals("unchanged", string(response.getObj("result"), "rewrite_status"));
    }

    @Test
    void unsupportedProtocolVersionIsProtocolError() {
        JsonObject response = SparqlRewriteServer.handle(
                request(2, "SELECT * WHERE { ?s ?p ?o }"));

        assertEquals("protocol_error", string(response, "status"));
        assertEquals("unsupported_protocol_version",
                string(response.getObj("diagnostic"), "code"));
    }

    @Test
    void missingQueryIsProtocolErrorWithRequestIdentity() {
        JsonObject request = new JsonObject();
        request.put("protocol_version", 1);
        request.put("request_id", "r1");
        request.put("query_id", "q1");

        JsonObject response = SparqlRewriteServer.handle(JSON.toStringFlat(request));

        assertEquals("protocol_error", string(response, "status"));
        assertEquals("invalid_request", string(response.getObj("diagnostic"), "code"));
        assertEquals("r1", string(response, "request_id"));
        assertEquals("q1", string(response, "query_id"));
    }

    @Test
    void parseErrorRemainsSuccessfulTransportResult() {
        JsonObject response = SparqlRewriteServer.handle(request(1, "SELECT WHERE {"));

        assertEquals("ok", string(response, "status"));
        assertEquals("parse_error", string(response.getObj("result"), "rewrite_status"));
        assertTrue(response.getObj("result").get("rewritten_query").isNull());
    }

    private static String request(int version, String query) {
        JsonObject request = new JsonObject();
        request.put("protocol_version", version);
        request.put("request_id", "r1");
        request.put("query_id", "q1");
        request.put("query", query);
        return JSON.toStringFlat(request);
    }

    private static String string(JsonObject object, String key) {
        return object.get(key).getAsString().value();
    }
}
