package io.github.andreawesterinen.wikidata.rewriter.rules;

import java.util.ArrayList;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;

import org.apache.jena.atlas.json.JsonObject;
import org.apache.jena.riot.RiotException;
import org.apache.jena.riot.tokens.Token;
import org.apache.jena.riot.tokens.TokenType;
import org.apache.jena.riot.tokens.Tokenizer;
import org.apache.jena.riot.tokens.TokenizerText;

import io.github.andreawesterinen.wikidata.rewriter.RewriteResult;

/** Removes Blazegraph query-hint triples before standards parsing. */
public final class QueryHintRewriter {
    private static final String RULE_ID = "rewrite-query-hint";
    private static final String VARIANT_ID = "default";
    private static final String HINT_NAMESPACE = "http://www.bigdata.com/queryHints#";

    private QueryHintRewriter() {
    }

    /**
     * Removes every complete statement with a supported query-hint subject.
     *
     * @param queryText original query text
     * @return rewritten step, or {@code null} when no supported hint can be inspected
     */
    public static RewriteResult rewrite(String queryText) {
        List<LocatedToken> tokens;
        try {
            tokens = tokenize(queryText);
        } catch (RiotException error) {
            return null;
        }
        Map<String, String> prefixes = prefixMappings(tokens);
        List<Span> spans = new ArrayList<Span>();
        for (int index = 0; index < tokens.size(); index++) {
            if (!isSubjectPosition(tokens, index)
                    || !isHintSubject(tokens.get(index).token, prefixes)) {
                continue;
            }
            int endIndex = statementEnd(tokens, index);
            int endOffset = endIndex < tokens.size()
                    ? tokens.get(endIndex).endOffset(queryText)
                    : queryText.length();
            if (endIndex < tokens.size()
                    && tokens.get(endIndex).token.hasType(TokenType.RBRACE)) {
                endOffset = tokens.get(endIndex).offset;
            }
            spans.add(new Span(tokens.get(index).offset, endOffset));
            index = endIndex;
        }
        if (spans.isEmpty()) {
            return null;
        }

        StringBuilder rewritten = new StringBuilder(queryText);
        for (Span span : spans) {
            for (int index = span.start; index < span.end; index++) {
                char character = rewritten.charAt(index);
                if (character != '\n' && character != '\r') {
                    rewritten.setCharAt(index, ' ');
                }
            }
        }
        RewriteResult result = new RewriteResult("rewritten", rewritten.toString());
        JsonObject record = new JsonObject();
        record.put("rule_id", RULE_ID);
        record.put("variant_id", VARIANT_ID);
        result.addRewrite(record);
        return result;
    }

    private static List<LocatedToken> tokenize(String queryText) {
        int[] lineOffsets = lineOffsets(queryText);
        List<LocatedToken> tokens = new ArrayList<LocatedToken>();
        Tokenizer tokenizer = TokenizerText.fromString(queryText);
        try {
            while (tokenizer.hasNext()) {
                Token token = tokenizer.next();
                int line = Math.toIntExact(token.getLine());
                int column = Math.toIntExact(token.getColumn());
                int offset = lineOffsets[line - 1] + column - 1;
                tokens.add(new LocatedToken(token, offset));
            }
        } finally {
            tokenizer.close();
        }
        return tokens;
    }

    private static int[] lineOffsets(String text) {
        int lines = 1;
        for (int index = 0; index < text.length(); index++) {
            if (isLineEnd(text, index)) {
                lines++;
            }
        }
        int[] offsets = new int[lines];
        int line = 1;
        for (int index = 0; index < text.length(); index++) {
            if (isLineEnd(text, index)) {
                offsets[line++] = index + 1;
            }
        }
        return offsets;
    }

    private static boolean isLineEnd(String text, int index) {
        char character = text.charAt(index);
        return character == '\n'
                || (character == '\r'
                    && (index + 1 == text.length() || text.charAt(index + 1) != '\n'));
    }

    private static Map<String, String> prefixMappings(List<LocatedToken> tokens) {
        Map<String, String> prefixes = new LinkedHashMap<String, String>();
        prefixes.put("hint", HINT_NAMESPACE);
        for (int index = 0; index + 2 < tokens.size(); index++) {
            Token directive = tokens.get(index).token;
            Token name = tokens.get(index + 1).token;
            Token iri = tokens.get(index + 2).token;
            if (isPrefixKeyword(directive)
                    && name.hasType(TokenType.PREFIXED_NAME)
                    && iri.hasType(TokenType.IRI)) {
                prefixes.put(name.getImage(), iri.getImage());
            }
        }
        return prefixes;
    }

    private static boolean isPrefixKeyword(Token token) {
        return (token.hasType(TokenType.KEYWORD) || token.hasType(TokenType.DIRECTIVE))
                && "PREFIX".equalsIgnoreCase(token.getImage());
    }

    private static boolean isHintSubject(Token token, Map<String, String> prefixes) {
        String iri = null;
        if (token.hasType(TokenType.IRI)) {
            iri = token.getImage();
        } else if (token.hasType(TokenType.PREFIXED_NAME)
                && prefixes.containsKey(token.getImage())) {
            iri = prefixes.get(token.getImage()) + token.getImage2();
        }
        return (HINT_NAMESPACE + "Query").equals(iri)
                || (HINT_NAMESPACE + "Prior").equals(iri)
                || (HINT_NAMESPACE + "Group").equals(iri)
                || (HINT_NAMESPACE + "GroupAndSubGroups").equals(iri)
                || (HINT_NAMESPACE + "SubQuery").equals(iri);
    }

    private static boolean isSubjectPosition(List<LocatedToken> tokens, int index) {
        if (index == 0) {
            return false;
        }
        TokenType previous = tokens.get(index - 1).token.getType();
        return previous == TokenType.LBRACE
                || previous == TokenType.DOT
                || previous == TokenType.RBRACE
                || previous == TokenType.RPAREN;
    }

    private static int statementEnd(List<LocatedToken> tokens, int start) {
        int parentheses = 0;
        int brackets = 0;
        int embeddedTriples = 0;
        for (int index = start + 1; index < tokens.size(); index++) {
            TokenType type = tokens.get(index).token.getType();
            boolean topLevel = parentheses == 0 && brackets == 0 && embeddedTriples == 0;
            if (topLevel && (type == TokenType.DOT || type == TokenType.RBRACE)) {
                return index;
            }
            if (type == TokenType.LPAREN) {
                parentheses++;
            } else if (type == TokenType.RPAREN && parentheses > 0) {
                parentheses--;
            } else if (type == TokenType.LBRACKET) {
                brackets++;
            } else if (type == TokenType.RBRACKET && brackets > 0) {
                brackets--;
            } else if (type == TokenType.LT2) {
                embeddedTriples++;
            } else if (type == TokenType.GT2 && embeddedTriples > 0) {
                embeddedTriples--;
            }
        }
        return tokens.size();
    }

    private static final class LocatedToken {
        private final Token token;
        private final int offset;

        private LocatedToken(Token token, int offset) {
            this.token = token;
            this.offset = offset;
        }

        private int endOffset(String text) {
            if (token.hasType(TokenType.DOT)) {
                return offset + 1;
            }
            return Math.min(offset + token.text().length(), text.length());
        }
    }

    private static final class Span {
        private final int start;
        private final int end;

        private Span(int start, int end) {
            this.start = start;
            this.end = end;
        }
    }

}
