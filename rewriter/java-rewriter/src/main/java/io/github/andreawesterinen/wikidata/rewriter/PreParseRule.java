package io.github.andreawesterinen.wikidata.rewriter;

/** Applies one ordered source-text rule before the first SPARQL parse. */
@FunctionalInterface
interface PreParseRule {
    /**
     * Rewrites one current query representation.
     *
     * @param queryText current query text after earlier pre-parse rules
     * @return rewrite step, terminal result, or {@code null} when not applicable
     */
    RewriteResult rewrite(String queryText);
}
