package io.github.andreawesterinen.wikidata.rewriter;

import java.util.Collections;
import java.util.LinkedHashMap;
import java.util.Map;
import java.util.regex.Pattern;

import org.apache.jena.atlas.json.JsonObject;
import org.apache.jena.graph.Node;
import org.apache.jena.graph.Triple;
import org.apache.jena.query.Query;
import org.apache.jena.query.SortCondition;
import org.apache.jena.sparql.algebra.walker.Walker;
import org.apache.jena.sparql.core.TriplePath;
import org.apache.jena.sparql.expr.Expr;
import org.apache.jena.sparql.expr.ExprFunction;
import org.apache.jena.sparql.expr.ExprFunction0;
import org.apache.jena.sparql.expr.ExprFunction1;
import org.apache.jena.sparql.expr.ExprFunction2;
import org.apache.jena.sparql.expr.ExprFunction3;
import org.apache.jena.sparql.expr.ExprFunctionN;
import org.apache.jena.sparql.expr.ExprFunctionOp;
import org.apache.jena.sparql.expr.ExprVisitorBase;
import org.apache.jena.sparql.syntax.ElementAssign;
import org.apache.jena.sparql.syntax.ElementBind;
import org.apache.jena.sparql.syntax.ElementFilter;
import org.apache.jena.sparql.syntax.ElementPathBlock;
import org.apache.jena.sparql.syntax.ElementService;
import org.apache.jena.sparql.syntax.ElementSubQuery;
import org.apache.jena.sparql.syntax.ElementTriplesBlock;
import org.apache.jena.sparql.syntax.ElementVisitorBase;
import org.apache.jena.sparql.syntax.ElementWalker;

/** Classifies cataloged proprietary features that are not yet implemented. */
final class UnsupportedFeatureDetector {
    private static final String WIKIBASE_NAMESPACE = "http://wikiba.se/ontology#";
    private static final String BIGDATA_NAMESPACE = "http://www.bigdata.com/rdf#";
    private static final String GAS_NAMESPACE = "http://www.bigdata.com/rdf/gas#";
    private static final String GEOF_NAMESPACE =
            "http://www.opengis.net/def/function/geosparql/";
    private static final String LABEL_SERVICE = WIKIBASE_NAMESPACE + "label";
    private static final Pattern NAMED_SUBQUERY_DEFINITION = Pattern.compile(
            "(?i)\\bWITH\\s*\\{");
    private static final Pattern NAMED_SUBQUERY_INCLUDE = Pattern.compile(
            "(?i)\\bINCLUDE\\s*%[A-Za-z_]");
    private static final Map<String, String> SERVICES = services();
    private static final Map<String, String> FUNCTIONS = functions();
    private static final Map<String, String> TERM_NAMESPACES = termNamespaces();

    private String feature;

    /** Creates an empty detector for one parsed query traversal. */
    private UnsupportedFeatureDetector() {
    }

    /**
     * Detects cataloged non-standard grammar before ARQ parsing.
     *
     * @param queryText original query text
     * @return unsupported result, or {@code null} when no feature is found
     */
    static RewriteResult detectPreParse(String queryText) {
        String syntax = maskCommentsStringsAndIris(queryText);
        if (NAMED_SUBQUERY_DEFINITION.matcher(syntax).find()
                || NAMED_SUBQUERY_INCLUDE.matcher(syntax).find()) {
            return unsupported("pre-ARQ", "named subquery");
        }
        return null;
    }

    /**
     * Detects parsed, cataloged proprietary features throughout a query.
     *
     * @param query complete parsed query
     * @return unsupported result, or {@code null} when no feature is found
     */
    static RewriteResult detectPostParse(Query query) {
        UnsupportedFeatureDetector detector = new UnsupportedFeatureDetector();
        detector.inspectQuery(query);
        return detector.feature == null
                ? null
                : unsupported("post-ARQ", detector.feature);
    }

    /**
     * Inspects one query scope and every nested query scope until a feature is found.
     *
     * @param query query scope to inspect
     */
    private void inspectQuery(Query query) {
        if (feature != null) {
            return;
        }
        inspectExpressions(query);
        if (query.getQueryPattern() == null) {
            return;
        }
        ElementWalker.walk(query.getQueryPattern(), new ElementVisitorBase() {
            /** Inspects one SERVICE endpoint. */
            @Override
            public void visit(ElementService service) {
                inspectService(service.getServiceNode());
            }

            /** Inspects every term in an ordinary triple block. */
            @Override
            public void visit(ElementTriplesBlock block) {
                block.patternElts().forEachRemaining(UnsupportedFeatureDetector.this::inspectTriple);
            }

            /** Inspects every triple path in a path block. */
            @Override
            public void visit(ElementPathBlock block) {
                block.patternElts().forEachRemaining(UnsupportedFeatureDetector.this::inspectPath);
            }

            /** Inspects a FILTER expression. */
            @Override
            public void visit(ElementFilter filter) {
                inspectExpression(filter.getExpr());
            }

            /** Inspects a BIND expression. */
            @Override
            public void visit(ElementBind bind) {
                inspectExpression(bind.getExpr());
            }

            /** Inspects an assignment expression. */
            @Override
            public void visit(ElementAssign assign) {
                inspectExpression(assign.getExpr());
            }

            /** Recursively inspects one nested query scope. */
            @Override
            public void visit(ElementSubQuery subquery) {
                inspectQuery(subquery.getQuery());
            }
        });
    }

    /**
     * Inspects projection, grouping, HAVING, and ordering expressions.
     *
     * @param query query scope whose solution expressions are inspected
     */
    private void inspectExpressions(Query query) {
        inspectExpressionList(query.getProject());
        inspectExpressionList(query.getGroupBy());
        for (Expr expression : query.getHavingExprs()) {
            inspectExpression(expression);
        }
        if (query.hasOrderBy()) {
            for (SortCondition condition : query.getOrderBy()) {
                inspectExpression(condition.getExpression());
            }
        }
    }

    /**
     * Inspects every expression stored in a Jena variable-expression list.
     *
     * @param expressions expression list, possibly {@code null}
     */
    private void inspectExpressionList(org.apache.jena.sparql.core.VarExprList expressions) {
        if (feature == null && expressions != null) {
            Walker.walk(expressions, expressionVisitor());
        }
    }

    /**
     * Inspects one expression tree when no earlier feature has matched.
     *
     * @param expression expression root, possibly {@code null}
     */
    private void inspectExpression(Expr expression) {
        if (feature == null && expression != null) {
            Walker.walk(expression, expressionVisitor());
        }
    }

    /**
     * Creates an expression visitor that reports every supported function arity.
     *
     * @return visitor forwarding function nodes to {@link #inspectFunction}
     */
    private ExprVisitorBase expressionVisitor() {
        return new ExprVisitorBase() {
            /** Inspects a zero-argument function. */
            @Override
            public void visit(ExprFunction0 function) {
                inspectFunction(function);
            }

            /** Inspects a one-argument function. */
            @Override
            public void visit(ExprFunction1 function) {
                inspectFunction(function);
            }

            /** Inspects a two-argument function. */
            @Override
            public void visit(ExprFunction2 function) {
                inspectFunction(function);
            }

            /** Inspects a three-argument function. */
            @Override
            public void visit(ExprFunction3 function) {
                inspectFunction(function);
            }

            /** Inspects a variable-arity function. */
            @Override
            public void visit(ExprFunctionN function) {
                inspectFunction(function);
            }

            /** Inspects a function whose argument includes graph syntax. */
            @Override
            public void visit(ExprFunctionOp function) {
                inspectFunction(function);
            }
        };
    }

    /**
     * Classifies a resolved function IRI as cataloged or unknown proprietary syntax.
     *
     * @param function parsed function expression
     */
    private void inspectFunction(ExprFunction function) {
        if (feature == null) {
            String iri = function.getFunctionIRI();
            if ((GEOF_NAMESPACE + "distance").equals(iri)
                    && function.numArgs() != 2) {
                return;
            }
            feature = FUNCTIONS.get(iri);
            if (feature == null && iri != null
                    && iri.startsWith(WIKIBASE_NAMESPACE)) {
                feature = "unknown Wikibase function";
            }
        }
    }

    /**
     * Classifies a resolved SERVICE endpoint IRI.
     *
     * @param service SERVICE endpoint node
     */
    private void inspectService(Node service) {
        if (feature == null && service != null && service.isURI()) {
            String iri = service.getURI();
            feature = SERVICES.get(iri);
            if (feature == null && !LABEL_SERVICE.equals(iri)
                    && (iri.startsWith(WIKIBASE_NAMESPACE)
                        || iri.startsWith(BIGDATA_NAMESPACE)
                        || iri.startsWith(GAS_NAMESPACE))) {
                feature = "unknown proprietary service";
            }
        }
    }

    /**
     * Inspects every term of an ordinary triple for a proprietary namespace IRI.
     *
     * @param triple triple to inspect
     */
    private void inspectTriple(Triple triple) {
        inspectProprietaryNode(triple.getSubject());
        inspectProprietaryNode(triple.getPredicate());
        inspectProprietaryNode(triple.getObject());
    }

    /**
     * Inspects the endpoint terms of a triple path for a proprietary namespace IRI.
     *
     * @param path triple path to inspect
     */
    private void inspectPath(TriplePath path) {
        inspectProprietaryNode(path.getSubject());
        inspectProprietaryNode(path.getPredicate());
        inspectProprietaryNode(path.getObject());
    }

    /**
     * Classifies a resolved RDF term through the proprietary namespace catalog.
     *
     * @param node RDF node to inspect, possibly {@code null}
     */
    private void inspectProprietaryNode(Node node) {
        if (feature == null && node != null && node.isURI()) {
            for (Map.Entry<String, String> entry : TERM_NAMESPACES.entrySet()) {
                if (node.getURI().startsWith(entry.getKey())) {
                    feature = entry.getValue();
                    return;
                }
            }
        }
    }

    /**
     * Builds a terminal unsupported-feature rewrite result.
     *
     * @param phase detection phase reported in the diagnostic
     * @param feature human-readable feature description
     * @return skipped-unsupported result containing one diagnostic
     */
    private static RewriteResult unsupported(String phase, String feature) {
        RewriteResult result = new RewriteResult("skipped_unsupported", null);
        JsonObject diagnostic = new JsonObject();
        diagnostic.put("code", "unsupported_proprietary_feature");
        diagnostic.put("phase", phase);
        diagnostic.put("message", "The query uses an unimplemented " + feature + ".");
        diagnostic.put("feature", feature);
        diagnostic.put("retry_classification", "after_catalog_change");
        diagnostic.put("diagnostic_reference",
                "README.md#current-status");
        result.addError(diagnostic);
        return result;
    }

    /**
     * Masks lexical regions in which proprietary grammar text is inert.
     *
     * @param text original query text
     * @return same-length text with comments, strings, and IRIs replaced by spaces
     */
    private static String maskCommentsStringsAndIris(String text) {
        StringBuilder masked = new StringBuilder(text);
        int index = 0;
        while (index < text.length()) {
            char current = text.charAt(index);
            if (current == '#') {
                index = maskUntilLineEnd(text, masked, index);
            } else if (current == '<') {
                index = maskQuoted(text, masked, index, '>', false);
            } else if (current == '\'' || current == '"') {
                boolean triple = index + 2 < text.length()
                        && text.charAt(index + 1) == current
                        && text.charAt(index + 2) == current;
                index = maskQuoted(text, masked, index, current, triple);
            } else {
                index++;
            }
        }
        return masked.toString();
    }

    /**
     * Replaces one comment with spaces while preserving its line ending.
     *
     * @param text original query text
     * @param masked mutable same-length masked copy
     * @param start offset of the comment marker
     * @return offset of the line ending or end of text
     */
    private static int maskUntilLineEnd(String text, StringBuilder masked, int start) {
        int index = start;
        while (index < text.length() && text.charAt(index) != '\n'
                && text.charAt(index) != '\r') {
            masked.setCharAt(index++, ' ');
        }
        return index;
    }

    /**
     * Replaces one IRI or quoted string, including escapes, with spaces.
     *
     * @param text original query text
     * @param masked mutable same-length masked copy
     * @param start offset of the opening delimiter
     * @param closing closing delimiter character
     * @param triple whether the delimiter has width three
     * @return offset immediately after the closing delimiter or end of text
     */
    private static int maskQuoted(String text, StringBuilder masked, int start,
            char closing, boolean triple) {
        int width = triple ? 3 : 1;
        int index = start;
        for (int offset = 0; offset < width && index < text.length(); offset++) {
            masked.setCharAt(index++, ' ');
        }
        while (index < text.length()) {
            if (text.charAt(index) == '\\') {
                masked.setCharAt(index++, ' ');
                if (index < text.length()) {
                    masked.setCharAt(index++, ' ');
                }
                continue;
            }
            boolean closes = text.charAt(index) == closing;
            if (triple) {
                closes = closes && index + 2 < text.length()
                        && text.charAt(index + 1) == closing
                        && text.charAt(index + 2) == closing;
            }
            for (int offset = 0; offset < (closes ? width : 1)
                    && index < text.length(); offset++) {
                masked.setCharAt(index++, ' ');
            }
            if (closes) {
                return index;
            }
        }
        return index;
    }

    /**
     * Builds the resolved SERVICE IRI catalog and diagnostic descriptions.
     *
     * @return immutable catalog in deterministic insertion order
     */
    private static Map<String, String> services() {
        Map<String, String> values = new LinkedHashMap<String, String>();
        values.put("http://www.bigdata.com/rdf/gas#service", "GAS service");
        values.put("http://wikiba.se/ontology#box", "wikibase:box service");
        values.put("http://wikiba.se/ontology#around", "wikibase:around service");
        values.put("http://www.bigdata.com/rdf#sample", "bd:sample service");
        values.put("http://www.bigdata.com/rdf#slice", "bd:slice service");
        values.put("http://wikiba.se/ontology#mwapi", "wikibase:mwapi service");
        return Collections.unmodifiableMap(values);
    }

    /**
     * Builds the resolved function IRI catalog and diagnostic descriptions.
     *
     * @return immutable catalog in deterministic insertion order
     */
    private static Map<String, String> functions() {
        Map<String, String> values = new LinkedHashMap<String, String>();
        values.put(GEOF_NAMESPACE + "globe", "geof:globe function");
        values.put(GEOF_NAMESPACE + "latitude", "geof:latitude function");
        values.put(GEOF_NAMESPACE + "longitude", "geof:longitude function");
        values.put(GEOF_NAMESPACE + "distance", "geof:distance function");
        values.put("http://wikiba.se/ontology#decodeUri", "wikibase:decodeUri function");
        return Collections.unmodifiableMap(values);
    }

    /**
     * Builds the namespace catalog for proprietary RDF terms outside functions.
     *
     * @return immutable namespace catalog in deterministic insertion order
     */
    private static Map<String, String> termNamespaces() {
        Map<String, String> values = new LinkedHashMap<String, String>();
        values.put("http://www.bigdata.com/queryHints#", "query hint");
        return Collections.unmodifiableMap(values);
    }
}
