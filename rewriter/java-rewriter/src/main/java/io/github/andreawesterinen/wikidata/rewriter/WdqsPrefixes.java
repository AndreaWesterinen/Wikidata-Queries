package io.github.andreawesterinen.wikidata.rewriter;

import java.util.Arrays;
import java.util.Collections;
import java.util.LinkedHashMap;
import java.util.LinkedHashSet;
import java.util.Map;
import java.util.Set;

import org.apache.jena.query.Query;
import org.apache.jena.riot.tokens.Token;
import org.apache.jena.riot.tokens.TokenType;
import org.apache.jena.riot.tokens.Tokenizer;
import org.apache.jena.riot.tokens.TokenizerText;

/** Applies the WDQS input and standards-compliant output prefix policies. */
final class WdqsPrefixes {
    private static final Map<String, String> DEFAULTS = defaults();
    private static final Set<String> INVALID_OUTPUT_PREFIXES =
            Collections.unmodifiableSet(new LinkedHashSet<String>(
                    Arrays.asList("bd", "gas", "hint", "mwapi")));

    private WdqsPrefixes() {
    }

    /** Prepends only used defaults that the query does not declare itself. */
    static String addMissingUsed(String queryText) {
        Set<String> used = new LinkedHashSet<String>();
        Set<String> declared = new LinkedHashSet<String>();
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
                    declared.add(token.getImage());
                    expectingDeclaration = false;
                    continue;
                }
                expectingDeclaration = false;
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

    /** Removes WDQS engine prefixes that are not valid in rewritten SPARQL. */
    static void removeInvalidOutputPrefixes(Query query) {
        for (String prefix : INVALID_OUTPUT_PREFIXES) {
            query.getPrefixMapping().removeNsPrefix(prefix);
        }
    }

    private static boolean isPrefixKeyword(Token token) {
        return (token.hasType(TokenType.KEYWORD) || token.hasType(TokenType.DIRECTIVE))
                && "PREFIX".equalsIgnoreCase(token.getImage());
    }

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
