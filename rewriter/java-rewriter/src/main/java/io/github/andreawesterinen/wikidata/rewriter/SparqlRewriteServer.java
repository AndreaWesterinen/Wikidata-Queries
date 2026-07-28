package io.github.andreawesterinen.wikidata.rewriter;

import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.io.OutputStreamWriter;
import java.io.PrintWriter;
import java.nio.charset.StandardCharsets;
import java.security.MessageDigest;

import org.apache.jena.atlas.json.JSON;
import org.apache.jena.atlas.json.JsonObject;
import org.apache.jena.atlas.json.JsonValue;

/** Runs the Java rewrite engine for a persistent stream of JSONL requests. */
public final class SparqlRewriteServer {
    private static final int PROTOCOL_VERSION = 1;

    private SparqlRewriteServer() {
    }

    /** Processes rewrite requests from standard input until the stream closes. */
    public static void main(String[] args) throws Exception {
        BufferedReader input = new BufferedReader(
                new InputStreamReader(System.in, StandardCharsets.UTF_8));
        PrintWriter output = new PrintWriter(
                new OutputStreamWriter(System.out, StandardCharsets.UTF_8), true);
        String line;
        while ((line = input.readLine()) != null) {
            if (!line.trim().isEmpty()) {
                output.println(JSON.toStringFlat(handle(line)));
            }
        }
    }

    /** Handles one JSONL protocol request line and produces one response. */
    static JsonObject handle(String line) {
        String requestId = null;
        String queryId = null;
        try {
            JsonObject request = JSON.parse(line);
            requestId = requiredString(request, "request_id");
            queryId = requiredString(request, "query_id");
            if (requiredLong(request, "protocol_version") != PROTOCOL_VERSION) {
                return failure(requestId, queryId, "protocol_error",
                        "unsupported_protocol_version", "Only protocol version 1 is supported.");
            }

            String queryText = requiredString(request, "query");
            RewriteResult result = SparqlRewriteEngine.rewrite(queryText);
            JsonObject response = envelope(requestId, queryId, "ok");
            response.put("query_sha256", sha256(queryText));
            response.put("result", result.toJson(queryId));
            return response;
        } catch (IllegalArgumentException e) {
            return failure(requestId, queryId, "protocol_error", "invalid_request",
                    e.getMessage());
        } catch (RuntimeException e) {
            return failure(requestId, queryId, "engine_error", "rewrite_engine_failure",
                    e.getMessage() == null ? "Unexpected rewrite-engine failure."
                            : e.getMessage());
        }
    }

    /** Creates the common response envelope for successful and failed requests. */
    private static JsonObject envelope(String requestId, String queryId, String status) {
        JsonObject response = new JsonObject();
        response.put("protocol_version", PROTOCOL_VERSION);
        if (requestId != null) {
            response.put("request_id", requestId);
        }
        if (queryId != null) {
            response.put("query_id", queryId);
        }
        response.put("status", status);
        return response;
    }

    /** Creates a structured protocol, parse, or engine failure response. */
    private static JsonObject failure(String requestId, String queryId, String status,
            String code, String message) {
        JsonObject response = envelope(requestId, queryId, status);
        JsonObject diagnostic = new JsonObject();
        diagnostic.put("code", code);
        diagnostic.put("message", message == null ? "" : message);
        response.put("diagnostic", diagnostic);
        return response;
    }

    /** Reads a required string field or rejects the request. */
    private static String requiredString(JsonObject object, String key) {
        JsonValue value = object.get(key);
        if (value == null || !value.isString()) {
            throw new IllegalArgumentException("Missing or non-string field: " + key);
        }
        return value.getAsString().value();
    }

    /** Reads a required numeric field or rejects the request. */
    private static long requiredLong(JsonObject object, String key) {
        JsonValue value = object.get(key);
        if (value == null || !value.isNumber()) {
            throw new IllegalArgumentException("Missing or non-numeric field: " + key);
        }
        return value.getAsNumber().value().longValue();
    }

    /** Calculates the lowercase SHA-256 digest for the exact query text. */
    private static String sha256(String text) {
        try {
            byte[] digest = MessageDigest.getInstance("SHA-256")
                    .digest(text.getBytes(StandardCharsets.UTF_8));
            StringBuilder result = new StringBuilder();
            for (byte value : digest) {
                result.append(String.format("%02x", value & 0xff));
            }
            return result.toString();
        } catch (Exception e) {
            throw new IllegalStateException(e);
        }
    }
}
