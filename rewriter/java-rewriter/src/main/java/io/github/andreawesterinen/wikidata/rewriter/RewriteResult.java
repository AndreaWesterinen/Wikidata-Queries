package io.github.andreawesterinen.wikidata.rewriter;

import org.apache.jena.atlas.json.JsonArray;
import org.apache.jena.atlas.json.JsonNull;
import org.apache.jena.atlas.json.JsonObject;

/**
 * Mutable result assembled by the Java rewrite engine for one query.
 *
 * <p>The engine package mutates fields directly, while rule implementations in
 * the {@code rules} subpackage use public accessors for the values they need.
 * This inconsistent access is retained until result mutation is encapsulated
 * in a separate refactor.
 */
public final class RewriteResult {
    final JsonArray rewrites = new JsonArray();
    final JsonArray warnings = new JsonArray();
    final JsonArray errors = new JsonArray();
    String status;
    String rewrittenQuery;

    public RewriteResult(String status, String rewrittenQuery) {
        this.status = status;
        this.rewrittenQuery = rewrittenQuery;
    }

    /** Returns the ordered mutable rule-occurrence records. */
    public JsonArray rewrites() {
        return rewrites;
    }

    /** Returns the ordered mutable error records. */
    public JsonArray errors() {
        return errors;
    }

    /** Returns this step's rewrite status. */
    public String status() {
        return status;
    }

    /** Serializes the public single-query rewrite result. */
    JsonObject toJson(String queryId) {
        JsonObject result = new JsonObject();
        result.put("schema_version", 1);
        result.put("contract_version", 1);
        result.put("query_id", queryId);
        result.put("rewrites", rewrites);
        result.put("rewrite_status", status);
        if (rewrittenQuery == null) {
            result.put("rewritten_query", JsonNull.instance);
        } else {
            result.put("rewritten_query", rewrittenQuery);
        }
        result.put("warnings", warnings);
        result.put("errors", errors);
        return result;
    }
}
