package io.github.andreawesterinen.wikidata.rewriter;

import org.apache.jena.atlas.json.JsonArray;
import org.apache.jena.atlas.json.JsonNull;
import org.apache.jena.atlas.json.JsonObject;
import org.apache.jena.atlas.json.JsonValue;

/** Mutable result assembled through explicit operations for a query. */
public final class RewriteResult {
    private final JsonArray rewrites = new JsonArray();
    private final JsonArray warnings = new JsonArray();
    private final JsonArray errors = new JsonArray();
    private String status;
    private String rewrittenQuery;

    public RewriteResult(String status, String rewrittenQuery) {
        this.status = status;
        this.rewrittenQuery = rewrittenQuery;
    }

    /** Adds one ordered rule-occurrence record. */
    public void addRewrite(JsonValue rewrite) {
        rewrites.add(rewrite);
    }

    /** Adds one ordered warning record. */
    public void addWarning(JsonValue warning) {
        warnings.add(warning);
    }

    /** Adds one ordered error record. */
    public void addError(JsonValue error) {
        errors.add(error);
    }

    /** Returns a copy of the ordered rule-occurrence records. */
    public JsonArray rewrites() {
        return copyOf(rewrites);
    }

    /** Returns a copy of the ordered warning records. */
    public JsonArray warnings() {
        return copyOf(warnings);
    }

    /** Returns a copy of the ordered error records. */
    public JsonArray errors() {
        return copyOf(errors);
    }

    /** Returns this step's rewrite status. */
    public String status() {
        return status;
    }

    /** Returns the rewritten query, or {@code null} when none was produced. */
    public String rewrittenQuery() {
        return rewrittenQuery;
    }

    /** Reports whether at least one rewrite was applied. */
    boolean hasRewrites() {
        return !rewrites.isEmpty();
    }

    /** Replaces the current terminal status and rewritten query. */
    void setOutcome(String newStatus, String newRewrittenQuery) {
        status = newStatus;
        rewrittenQuery = newRewrittenQuery;
    }

    /** Appends the ordered records from one successful rule step. */
    void append(RewriteResult step) {
        rewrites.addAll(step.rewrites);
        warnings.addAll(step.warnings);
        errors.addAll(step.errors);
    }

    /** Serializes the public single-query rewrite result. */
    JsonObject toJson(String queryId) {
        JsonObject result = new JsonObject();
        result.put("schema_version", 1);
        result.put("contract_version", 1);
        result.put("query_id", queryId);
        result.put("rewrites", copyOf(rewrites));
        result.put("rewrite_status", status);
        if (rewrittenQuery == null) {
            result.put("rewritten_query", JsonNull.instance);
        } else {
            result.put("rewritten_query", rewrittenQuery);
        }
        result.put("warnings", copyOf(warnings));
        result.put("errors", copyOf(errors));
        return result;
    }

    private static JsonArray copyOf(JsonArray source) {
        JsonArray copy = new JsonArray();
        copy.addAll(source);
        return copy;
    }
}
