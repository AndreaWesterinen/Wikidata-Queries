package io.github.andreawesterinen.wikidata.rewriter;

import java.util.ArrayList;
import java.util.Collections;
import java.util.LinkedHashMap;
import java.util.LinkedHashSet;
import java.util.Map;
import java.util.List;
import java.util.Set;

import org.apache.jena.query.Query;
import org.apache.jena.riot.tokens.Token;
import org.apache.jena.riot.tokens.TokenType;
import org.apache.jena.riot.tokens.Tokenizer;
import org.apache.jena.riot.tokens.TokenizerText;

/** Applies the WDQS input and SPARQL-compliant output prefix policies. */
final class WdqsPrefixes {
    private static final Map<String, String> DEFAULTS = defaults();
    private static final Set<String> LABEL_OUTPUT_PREFIX_NAMES = labelOutputPrefixNames();
    private static final Set<String> INVALID_OUTPUT_PREFIX_NAMES = invalidOutputPrefixNames();
    private static final Set<String> INVALID_OUTPUT_NAMESPACES = invalidOutputNamespaces();

    /** Prevents instantiation of the static prefix-policy utility. */
    private WdqsPrefixes() {
    }

    /**
     * Prepends defaults that the query uses but does not declare itself.
     *
     * @param queryText original WDQS query text
     * @return original text, optionally preceded by required default declarations
     */
    static String addMissingUsed(String queryText) {
        Set<String> used = new LinkedHashSet<String>();
        Set<String> declared = declaredPrefixes(queryText);
        Tokenizer tokenizer = TokenizerText.fromString(queryText);
        try {
            while (tokenizer.hasNext()) {
                Token token = tokenizer.next();
                if (isPrefixKeyword(token)) {
                    if (tokenizer.hasNext()) {
                        tokenizer.next();
                    }
                    continue;
                }
                collectUsedPrefixes(token, used);
            }
        } finally {
            tokenizer.close();
        }

        StringBuilder additions = new StringBuilder();
        for (Map.Entry<String, String> entry : DEFAULTS.entrySet()) {
            if (used.contains(entry.getKey()) && !declared.contains(entry.getKey())) {
                additions.append("PREFIX ").append(entry.getKey()).append(": <")
                        .append(entry.getValue()).append(">\n");
            }
        }
        return additions.length() == 0 ? queryText : additions.append(queryText).toString();
    }

    /**
     * Expands label-output mappings that were supplied only to parse WDQS input.
     *
     * @param serialized Jena-serialized rewritten query
     * @param queryText original query text used to distinguish explicit declarations
     * @return standalone query with implicit label predicates rendered as full IRIs
     */
    static String expandImplicitLabelOutputPrefixes(String serialized, String queryText) {
        Set<String> declared = declaredPrefixes(queryText);
        String expanded = serialized;
        if (!declared.contains("rdfs")) {
            expanded = expandPrefixedName(expanded, "rdfs", "label",
                    "http://www.w3.org/2000/01/rdf-schema#label");
        }
        if (!declared.contains("schema")) {
            expanded = expandPrefixedName(expanded, "schema", "description",
                    "http://schema.org/description");
        }
        if (!declared.contains("skos")) {
            expanded = expandPrefixedName(expanded, "skos", "altLabel",
                    "http://www.w3.org/2004/02/skos/core#altLabel");
        }
        Set<String> used = usedPrefixes(expanded);
        for (String prefix : LABEL_OUTPUT_PREFIX_NAMES) {
            if (!declared.contains(prefix) && !used.contains(prefix)) {
                expanded = expanded.replaceFirst(
                        "(?m)^PREFIX\\s+" + prefix + ":\\s+<[^>]+>\\R", "");
            }
        }
        return expanded;
    }

    /**
     * Replaces matching QName tokens with one full IRI without changing strings.
     *
     * @param queryText serialized query to transform
     * @param prefix prefix name without a colon
     * @param localName local part of the QName
     * @param iri complete replacement IRI without angle brackets
     * @return query text with every matching QName token expanded
     */
    private static String expandPrefixedName(String queryText, String prefix,
            String localName, String iri) {
        List<Integer> offsets = new ArrayList<Integer>();
        Tokenizer tokenizer = TokenizerText.fromString(queryText);
        try {
            while (tokenizer.hasNext()) {
                Token token = tokenizer.next();
                if (token.hasType(TokenType.PREFIXED_NAME)
                        && prefix.equals(token.getImage())
                        && localName.equals(token.getImage2())) {
                    offsets.add(offsetAt(queryText, token.getLine(), token.getColumn()));
                }
            }
        } finally {
            tokenizer.close();
        }
        StringBuilder expanded = new StringBuilder(queryText);
        int length = prefix.length() + localName.length() + 1;
        for (int index = offsets.size() - 1; index >= 0; index--) {
            int offset = offsets.get(index);
            expanded.replace(offset, offset + length, "<" + iri + ">");
        }
        return expanded.toString();
    }

    /**
     * Converts a one-based token line and column to a string offset.
     *
     * @param text complete tokenized text
     * @param line one-based token line
     * @param column one-based token column
     * @return zero-based character offset
     */
    private static int offsetAt(String text, long line, long column) {
        int offset = 0;
        for (long currentLine = 1; currentLine < line; currentLine++) {
            offset = text.indexOf('\n', offset) + 1;
        }
        return offset + (int) column - 1;
    }

    /**
     * Removes forbidden WDQS engine prefix names and namespaces from output.
     *
     * @param query rewritten query whose prefix mapping is mutated
     */
    static void removeInvalidOutputPrefixes(Query query) {
        Map<String, String> mappings = new LinkedHashMap<String, String>(
                query.getPrefixMapping().getNsPrefixMap());
        for (Map.Entry<String, String> mapping : mappings.entrySet()) {
            if (INVALID_OUTPUT_PREFIX_NAMES.contains(mapping.getKey())
                    || INVALID_OUTPUT_NAMESPACES.contains(mapping.getValue())) {
                query.getPrefixMapping().removeNsPrefix(mapping.getKey());
            }
        }
    }

    /**
     * Removes declarations that are no longer used after a rewrite.
     *
     * @param query rewritten query whose prefix mapping is mutated
     */
    static void removeUnusedOutputPrefixes(Query query) {
        Set<String> used = usedPrefixes(query.serialize());
        Set<String> declared = new LinkedHashSet<String>(
                query.getPrefixMapping().getNsPrefixMap().keySet());
        for (String prefix : declared) {
            if (!used.contains(prefix)) {
                query.getPrefixMapping().removeNsPrefix(prefix);
            }
        }
    }

    /**
     * Collects QName prefix names used outside prefix declarations.
     *
     * @param queryText SPARQL text to tokenize
     * @return prefix names in first-use order
     */
    private static Set<String> usedPrefixes(String queryText) {
        Set<String> used = new LinkedHashSet<String>();
        Tokenizer tokenizer = TokenizerText.fromString(queryText);
        boolean expectingDeclaration = false;
        try {
            while (tokenizer.hasNext()) {
                Token token = tokenizer.next();
                if (isPrefixKeyword(token)) {
                    expectingDeclaration = true;
                    continue;
                }
                if (expectingDeclaration && token.hasType(TokenType.PREFIXED_NAME)) {
                    expectingDeclaration = false;
                    continue;
                }
                expectingDeclaration = false;
                collectUsedPrefixes(token, used);
            }
        } finally {
            tokenizer.close();
        }
        return used;
    }

    /**
     * Builds the prefix-name denylist for successful rewritten output, rejecting 
	 * names such as hint, even if rebound to another namespace.
     *
     * @return immutable forbidden prefix-name set
     */
    private static Set<String> invalidOutputPrefixNames() {
        Set<String> values = new LinkedHashSet<String>();
        values.add("bd");
        values.add("gas");
        values.add("hint");
        values.add("mwapi");
        return Collections.unmodifiableSet(values);
    }

    /**
     * Builds the standard label-vocabulary prefixes eligible for IRI expansion.
     *
     * @return immutable label-output prefix-name set
     */
    private static Set<String> labelOutputPrefixNames() {
        Set<String> values = new LinkedHashSet<String>();
        values.add("rdfs");
        values.add("schema");
        values.add("skos");
        return Collections.unmodifiableSet(values);
    }

    /**
     * Collects prefix names explicitly declared by the query text.
     *
     * @param queryText original or serialized SPARQL text
     * @return explicitly declared prefix names in source order
     */
    private static Set<String> declaredPrefixes(String queryText) {
        Set<String> declared = new LinkedHashSet<String>();
        Tokenizer tokenizer = TokenizerText.fromString(queryText);
        boolean expectingDeclaration = false;
        try {
            while (tokenizer.hasNext()) {
                Token token = tokenizer.next();
                if (isPrefixKeyword(token)) {
                    expectingDeclaration = true;
                } else if (expectingDeclaration
                        && token.hasType(TokenType.PREFIXED_NAME)) {
                    declared.add(token.getImage());
                    expectingDeclaration = false;
                } else {
                    expectingDeclaration = false;
                }
            }
        } finally {
            tokenizer.close();
        }
        return declared;
    }

    /**
     * Builds the namespace denylist for successful rewritten output, rejecting 
	 * proprietary namespaces, even when aliased as something like 
	 * PREFIX custom: <http://www.bigdata.com/queryHints#>.
     *
     * @return immutable forbidden namespace set
     */
    private static Set<String> invalidOutputNamespaces() {
        Set<String> values = new LinkedHashSet<String>();
        values.add("http://www.bigdata.com/rdf#");
        values.add("http://www.bigdata.com/rdf/gas#");
        values.add("http://www.bigdata.com/queryHints#");
        values.add("https://www.mediawiki.org/ontology#API/");
        return Collections.unmodifiableSet(values);
    }

    /**
     * Tests whether a token is a SPARQL PREFIX keyword or directive.
     *
     * @param token token to inspect
     * @return {@code true} for a case-insensitive PREFIX token
     */
    private static boolean isPrefixKeyword(Token token) {
        return (token.hasType(TokenType.KEYWORD) || token.hasType(TokenType.DIRECTIVE))
                && "PREFIX".equalsIgnoreCase(token.getImage());
    }

    /**
     * Recursively collects prefix names from a token and its compound subtokens.
     *
     * @param token token subtree to inspect, possibly {@code null}
     * @param used destination set of used prefix names
     */
    private static void collectUsedPrefixes(Token token, Set<String> used) {
        if (token == null) {
            return;
        }
        if (token.hasType(TokenType.PREFIXED_NAME)) {
            used.add(token.getImage());
        }
        collectUsedPrefixes(token.getSubToken1(), used);
        collectUsedPrefixes(token.getSubToken2(), used);
    }

    /**
     * Builds the supported WDQS parser-default prefix mapping.
     *
     * @return immutable insertion-ordered prefix mapping
     */
    private static Map<String, String> defaults() {
        Map<String, String> values = new LinkedHashMap<String, String>();
        values.put("wd", "http://www.wikidata.org/entity/");
        values.put("wdt", "http://www.wikidata.org/prop/direct/");
        values.put("wdtn", "http://www.wikidata.org/prop/direct-normalized/");
        values.put("wds", "http://www.wikidata.org/entity/statement/");
        values.put("wdv", "http://www.wikidata.org/value/");
        values.put("wdref", "http://www.wikidata.org/reference/");
        values.put("wdno", "http://www.wikidata.org/prop/novalue/");
        values.put("wdata", "http://www.wikidata.org/wiki/Special:EntityData/");
        values.put("p", "http://www.wikidata.org/prop/");
        values.put("ps", "http://www.wikidata.org/prop/statement/");
        values.put("psv", "http://www.wikidata.org/prop/statement/value/");
        values.put("psn", "http://www.wikidata.org/prop/statement/value-normalized/");
        values.put("pq", "http://www.wikidata.org/prop/qualifier/");
        values.put("pqv", "http://www.wikidata.org/prop/qualifier/value/");
        values.put("pqn", "http://www.wikidata.org/prop/qualifier/value-normalized/");
        values.put("pr", "http://www.wikidata.org/prop/reference/");
        values.put("prv", "http://www.wikidata.org/prop/reference/value/");
        values.put("prn", "http://www.wikidata.org/prop/reference/value-normalized/");
        values.put("wikibase", "http://wikiba.se/ontology#");
        values.put("rdf", "http://www.w3.org/1999/02/22-rdf-syntax-ns#");
        values.put("rdfs", "http://www.w3.org/2000/01/rdf-schema#");
        values.put("owl", "http://www.w3.org/2002/07/owl#");
        values.put("skos", "http://www.w3.org/2004/02/skos/core#");
        values.put("schema", "http://schema.org/");
        values.put("xsd", "http://www.w3.org/2001/XMLSchema#");
        values.put("prov", "http://www.w3.org/ns/prov#");
        values.put("geo", "http://www.opengis.net/ont/geosparql#");
        values.put("geof", "http://www.opengis.net/def/function/geosparql/");
        values.put("bd", "http://www.bigdata.com/rdf#");
        values.put("gas", "http://www.bigdata.com/rdf/gas#");
        values.put("hint", "http://www.bigdata.com/queryHints#");
        values.put("mwapi", "https://www.mediawiki.org/ontology#API/");
        return Collections.unmodifiableMap(values);
    }
}
