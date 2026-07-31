package io.github.andreawesterinen.wikidata.rewriter;

import java.util.Objects;

import org.apache.jena.atlas.json.JSON;

/** Entry point for embedding the rewrite engine. */
public final class EmbeddedSparqlRewriter {
    private EmbeddedSparqlRewriter() {
    }

    /**
     * Rewrites one query and returns its flat Single-Query Rewrite Result JSON.
     *
     * @param queryId stable identifier copied into the result
     * @param queryText complete SPARQL query
     * @return one flat JSON object
     */
    public static String rewriteJson(String queryId, String queryText) {
        Objects.requireNonNull(queryId, "queryId");
        Objects.requireNonNull(queryText, "queryText");
        return JSON.toStringFlat(
                SparqlRewriteEngine.rewrite(queryText).toJson(queryId));
    }
}
