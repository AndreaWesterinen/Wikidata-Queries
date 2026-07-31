package io.github.andreawesterinen.wikidata.rewriter;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.List;

import org.apache.jena.atlas.json.JsonObject;
import org.apache.jena.query.Query;
import org.apache.jena.query.QueryFactory;
import org.apache.jena.query.QueryParseException;
import org.apache.jena.query.Syntax;
import org.apache.jena.riot.RiotException;

import io.github.andreawesterinen.wikidata.rewriter.rules.QueryHintRewriter;
import io.github.andreawesterinen.wikidata.rewriter.rules.WikibaseLabelRewriter;

/** Parses, rewrites, serializes, and validates one complete SPARQL query. */
final class SparqlRewriteEngine {
    private static final List<PreParseRule> PRE_PARSE_RULES = preParseRules();

    /** Prevents instantiation of the static rewrite engine. */
    private SparqlRewriteEngine() {
    }

    /**
     * Rewrites one complete query with every applicable implemented rule.
     *
     * @param queryText original query text
     * @return deterministic rewrite result, including status and diagnostics
     * @throws IllegalStateException if a rule reports success without a change
     */
    static RewriteResult rewrite(String queryText) {
        return rewrite(queryText, PRE_PARSE_RULES);
    }

    /**
     * Rewrites one query using the supplied pre-parse rule order.
     *
     * <p>This package-private overload makes the pre-parse handoff directly
     * testable without changing the production rule list.</p>
     *
     * @param queryText original query text
     * @param preParseRules ordered pre-parse rules to apply once each
     * @return deterministic rewrite result, including status and diagnostics
     * @throws IllegalStateException if a rule reports success without a change
     */
    static RewriteResult rewrite(String queryText, List<PreParseRule> preParseRules) {
        RewriteResult complete = new RewriteResult("unchanged", queryText);
        String currentText = queryText;
        for (PreParseRule rule : preParseRules) {
            RewriteResult step;
            try {
                step = rule.rewrite(currentText);
            } catch (RiotException error) {
                return originalParseFailure();
            }
            if (step == null) {
                continue;
            }
            if (!"rewritten".equals(step.status())) {
                return step;
            }
            String rewritten = step.rewrittenQuery();
            if (rewritten == null || rewritten.equals(currentText)) {
                throw new IllegalStateException(
                        "A pre-parse rule reported success without changing the query.");
            }
            complete.append(step);
            currentText = rewritten;
        }

        RewriteResult preParseFailure = UnsupportedFeatureDetector.detectPreParse(currentText);
        if (preParseFailure != null) {
            return preParseFailure;
        }
        Query query;
        try {
            query = parseOriginal(currentText);
        } catch (QueryParseException | RiotException error) {
            return originalParseFailure();
        }
        RewriteResult datasetFailure = rejectDataset(query);
        if (datasetFailure != null) {
            return datasetFailure;
        }

        while (true) {
            RewriteResult step = applyNextRule(query);
            if (step == null) {
                if (complete.hasRewrites()) {
                    complete.setOutcome("rewritten",
                            serialize(query, queryText));
                }
                return complete;
            }

            if (!"rewritten".equals(step.status())) {
                return step;
            }
            complete.append(step);

            String rewritten = serialize(query, queryText);
            if (rewritten.equals(currentText)) {
                throw new IllegalStateException(
                        "A rewrite rule reported success without changing the query.");
            }
            try {
                query = QueryFactory.create(rewritten, Syntax.syntaxSPARQL_11);
            } catch (QueryParseException error) {
                return validationFailure(complete, error);
            }
            currentText = rewritten;
        }
    }

    /**
     * Builds the deterministic pre-parse rule order.
     *
     * @return immutable ordered pre-parse rule list
     */
    private static List<PreParseRule> preParseRules() {
        List<PreParseRule> rules = new ArrayList<PreParseRule>();
        rules.add(QueryHintRewriter::rewrite);
        return Collections.unmodifiableList(rules);
    }

    /**
     * Parses original input with any required WDQS prefix defaults.
     *
     * @param queryText original query text
     * @return parsed query
     */
    private static Query parseOriginal(String queryText) {
        return QueryFactory.create(
                WdqsPrefixes.addMissingUsed(queryText), Syntax.syntaxSPARQL_11);
    }

    /**
     * Applies the first matching rule in the explicit order below.
     *
     * @param query current parsed query
     * @return rule result, or {@code null} when no implemented rule matches
     */
    private static RewriteResult applyNextRule(Query query) {
        RewriteResult unsupported = UnsupportedFeatureDetector.detectPostParse(query);
        if (unsupported != null) {
            return unsupported;
        }
        return WikibaseLabelRewriter.rewrite(query);
    }

    /**
     * Rejects top-level dataset clauses, which cannot select Wikidata graphs.
     *
     * <p>This does not inspect {@code GRAPH} patterns, including graph patterns
     * inside federated {@code SERVICE} clauses.</p>
     *
     * @param query parsed root query to inspect
     * @return unsupported result, or {@code null} when no dataset clause exists
     */
    private static RewriteResult rejectDataset(Query query) {
        if (!query.hasDatasetDescription()) {
            return null;
        }

        RewriteResult result = new RewriteResult("skipped_unsupported", null);
        JsonObject diagnostic = new JsonObject();
        boolean named = !query.getNamedGraphURIs().isEmpty();
        String source = named
                ? query.getNamedGraphURIs().get(0)
                : query.getGraphURIs().get(0);
        diagnostic.put("code", "unsupported_dataset_clause");
        diagnostic.put("phase", "post-ARQ");
        diagnostic.put("message", "Top-level FROM" + (named ? " NAMED" : "")
                + " <" + source + "> is unsupported because Wikidata has no "
                + "named graphs to select into a query dataset.");
        diagnostic.put("source_location", "dataset[0]");
        diagnostic.put("retry_classification", "after_contract_change");
        diagnostic.put("diagnostic_reference",
                "README.md#current-status");
        result.addError(diagnostic);
        return result;
    }

    /**
     * Serializes a query as canonical SPARQL sorted by prefix name.
     *
     * @param query parsed query to serialize
     * @return serialized query ending with a newline
     */
    private static String serialize(Query query, String originalQueryText) {
        WdqsPrefixes.removeUnusedOutputPrefixes(query);
        WdqsPrefixes.removeInvalidOutputPrefixes(query);
        String rewritten = WdqsPrefixes.expandImplicitLabelOutputPrefixes(
                query.serialize(Syntax.syntaxSPARQL_11), originalQueryText);
        rewritten = sortPrefixDeclarations(rewritten);
        if (!rewritten.endsWith("\n")) {
            rewritten += "\n";
        }
        return rewritten;
    }

    /**
     * Sorts leading serialized {@code PREFIX} declarations by prefix name.
     *
     * @param serialized Jena-serialized query text
     * @return query text with reordered prefix declarations
     */
    private static String sortPrefixDeclarations(String serialized) {
        String[] lines = serialized.split("\n", -1);
        List<Integer> slots = new ArrayList<Integer>();
        List<String> declarations = new ArrayList<String>();
        for (int index = 0; index < lines.length; index++) {
            String trimmed = lines[index].trim();
            if (trimmed.isEmpty()) {
                break;
            }
            if (trimmed.startsWith("PREFIX ")) {
                slots.add(index);
                declarations.add(lines[index]);
            } else if (!trimmed.startsWith("BASE ")) {
                break;
            }
        }
        Collections.sort(declarations, new Comparator<String>() {
            /**
             * Compares declarations by prefix name and then complete text.
             *
             * @param left first prefix declaration
             * @param right second prefix declaration
             * @return negative, zero, or positive comparison value
             */
            @Override
            public int compare(String left, String right) {
                int comparison = prefixName(left).compareTo(prefixName(right));
                return comparison != 0 ? comparison : left.compareTo(right);
            }
        });
        for (int index = 0; index < slots.size(); index++) {
            lines[slots.get(index)] = declarations.get(index);
        }
        return String.join("\n", lines);
    }

    /**
     * Extracts the prefix name from a serialized {@code PREFIX} declaration.
     *
     * @param declaration serialized prefix declaration
     * @return prefix name without the trailing colon
     * @throws IllegalStateException if the declaration has no colon
     */
    private static String prefixName(String declaration) {
        int colon = declaration.indexOf(':', "PREFIX".length());
        if (colon < 0) {
            throw new IllegalStateException(
                    "Jena serialized a malformed PREFIX declaration.");
        }
        return declaration.substring("PREFIX".length(), colon).trim();
    }

    /**
     * Converts a generated-query parse error into a validation failure.
     *
     * @param complete accumulated rewrite result to update
     * @param error generated-query parse error
     * @return the updated validation-failure result
     */
    private static RewriteResult validationFailure(
            RewriteResult complete, QueryParseException error) {
        complete.setOutcome("validation_failed", null);
        JsonObject diagnostic = new JsonObject();
        diagnostic.put("code", "rewritten_query_parse_error");
        diagnostic.put("phase", "validation");
        diagnostic.put("message", error.getMessage() == null ? "" : error.getMessage());
        diagnostic.put("retry_classification", "after_input_or_parser_change");
        diagnostic.put("diagnostic_reference",
                "docs/README_java_rewriter.md#rewrite-status-outputs");
        complete.addError(diagnostic);
        return complete;
    }

    /** Returns the public diagnostic for invalid original SPARQL input. */
    private static RewriteResult originalParseFailure() {
        RewriteResult result = new RewriteResult("parse_error", null);
        JsonObject diagnostic = new JsonObject();
        diagnostic.put("code", "original_query_parse_error");
        diagnostic.put("phase", "parse");
        diagnostic.put("message",
                "The original query could not be parsed as SPARQL 1.1.");
        diagnostic.put("retry_classification", "after_input_or_parser_change");
        diagnostic.put("diagnostic_reference",
                "docs/README_java_rewriter.md#rewrite-status-outputs");
        result.addError(diagnostic);
        return result;
    }
}
