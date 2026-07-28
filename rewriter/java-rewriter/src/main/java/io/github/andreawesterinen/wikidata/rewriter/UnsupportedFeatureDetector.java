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

/** Classifies cataloged proprietary features that are not implemented yet. */
final class UnsupportedFeatureDetector {
    private static final String HINT_NAMESPACE = "http://www.bigdata.com/queryHints#";
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

    private String feature;

    /** Returns an unsupported result for non-standard grammar before ARQ parsing. */
    static RewriteResult detectPreParse(String queryText) {
        String syntax = maskCommentsStringsAndIris(queryText);
        if (NAMED_SUBQUERY_DEFINITION.matcher(syntax).find()
                || NAMED_SUBQUERY_INCLUDE.matcher(syntax).find()) {
            return unsupported("pre-ARQ", "named subquery");
        }
        return null;
    }

    /** Returns an unsupported result for a parsed, cataloged proprietary feature. */
    static RewriteResult detectPostParse(Query query) {
        UnsupportedFeatureDetector detector = new UnsupportedFeatureDetector();
        detector.inspectQuery(query);
        return detector.feature == null
                ? null
                : unsupported("post-ARQ", detector.feature);
    }

    private void inspectQuery(Query query) {
        if (feature != null) {
            return;
        }
        inspectExpressions(query);
        if (query.getQueryPattern() == null) {
            return;
        }
        ElementWalker.walk(query.getQueryPattern(), new ElementVisitorBase() {
            @Override
            public void visit(ElementService service) {
                inspectService(service.getServiceNode());
            }

            @Override
            public void visit(ElementTriplesBlock block) {
                block.patternElts().forEachRemaining(UnsupportedFeatureDetector.this::inspectTriple);
            }

            @Override
            public void visit(ElementPathBlock block) {
                block.patternElts().forEachRemaining(UnsupportedFeatureDetector.this::inspectPath);
            }

            @Override
            public void visit(ElementFilter filter) {
                inspectExpression(filter.getExpr());
            }

            @Override
            public void visit(ElementBind bind) {
                inspectExpression(bind.getExpr());
            }

            @Override
            public void visit(ElementAssign assign) {
                inspectExpression(assign.getExpr());
            }

            @Override
            public void visit(ElementSubQuery subquery) {
                inspectQuery(subquery.getQuery());
            }
        });
    }

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

    private void inspectExpressionList(org.apache.jena.sparql.core.VarExprList expressions) {
        if (feature == null && expressions != null) {
            Walker.walk(expressions, expressionVisitor());
        }
    }

    private void inspectExpression(Expr expression) {
        if (feature == null && expression != null) {
            Walker.walk(expression, expressionVisitor());
        }
    }

    private ExprVisitorBase expressionVisitor() {
        return new ExprVisitorBase() {
            @Override
            public void visit(ExprFunction0 function) {
                inspectFunction(function);
            }

            @Override
            public void visit(ExprFunction1 function) {
                inspectFunction(function);
            }

            @Override
            public void visit(ExprFunction2 function) {
                inspectFunction(function);
            }

            @Override
            public void visit(ExprFunction3 function) {
                inspectFunction(function);
            }

            @Override
            public void visit(ExprFunctionN function) {
                inspectFunction(function);
            }

            @Override
            public void visit(ExprFunctionOp function) {
                inspectFunction(function);
            }
        };
    }

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

    private void inspectTriple(Triple triple) {
        inspectHintNode(triple.getSubject());
        inspectHintNode(triple.getPredicate());
        inspectHintNode(triple.getObject());
    }

    private void inspectPath(TriplePath path) {
        inspectHintNode(path.getSubject());
        inspectHintNode(path.getPredicate());
        inspectHintNode(path.getObject());
    }

    private void inspectHintNode(Node node) {
        if (feature == null && node != null && node.isURI()
                && node.getURI().startsWith(HINT_NAMESPACE)) {
            feature = "query hint";
        }
    }

    private static RewriteResult unsupported(String phase, String feature) {
        RewriteResult result = new RewriteResult("skipped_unsupported", null);
        JsonObject diagnostic = new JsonObject();
        diagnostic.put("code", "unsupported_proprietary_feature");
        diagnostic.put("phase", phase);
        diagnostic.put("message", "The query uses an unimplemented " + feature + ".");
        diagnostic.put("feature", feature);
        diagnostic.put("retry_classification", "after_catalog_change");
        diagnostic.put("diagnostic_reference",
                "README-rewrites.md#known-unimplemented-features");
        result.errors.add(diagnostic);
        return result;
    }

    /** Masks lexical regions in which proprietary grammar text is inert. */
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

    private static int maskUntilLineEnd(String text, StringBuilder masked, int start) {
        int index = start;
        while (index < text.length() && text.charAt(index) != '\n'
                && text.charAt(index) != '\r') {
            masked.setCharAt(index++, ' ');
        }
        return index;
    }

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

    private static Map<String, String> functions() {
        Map<String, String> values = new LinkedHashMap<String, String>();
        values.put(GEOF_NAMESPACE + "globe", "geof:globe function");
        values.put(GEOF_NAMESPACE + "latitude", "geof:latitude function");
        values.put(GEOF_NAMESPACE + "longitude", "geof:longitude function");
        values.put(GEOF_NAMESPACE + "distance", "geof:distance function");
        values.put("http://wikiba.se/ontology#decodeUri", "wikibase:decodeUri function");
        return Collections.unmodifiableMap(values);
    }
}
