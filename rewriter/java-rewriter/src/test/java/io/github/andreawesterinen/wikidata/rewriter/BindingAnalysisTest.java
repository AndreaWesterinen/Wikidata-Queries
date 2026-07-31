package io.github.andreawesterinen.wikidata.rewriter;

import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertSame;
import static org.junit.jupiter.api.Assertions.assertTrue;

import org.apache.jena.query.Query;
import org.apache.jena.query.QueryFactory;
import org.apache.jena.query.Syntax;
import org.apache.jena.sparql.syntax.Element;
import org.apache.jena.sparql.syntax.ElementGroup;
import org.apache.jena.sparql.syntax.ElementService;
import org.junit.jupiter.api.Test;

final class BindingAnalysisTest {
    private static final String LABEL = "http://wikiba.se/ontology#label";

    /** Tests bound/possibly bound/unbound analysis at SERVICE occurrences. */
    @Test
    void requiredTripleDefinitelyBindsEntity() {
        Target target = target("?item <http://example.com/p> <http://example.com/o> .");

        BindingAnalysis.Bindings bindings = BindingAnalysis.before(
                target.query, target.service, target.parent, target.index);

        assertTrue(bindings.definite.contains("item"));
        assertTrue(bindings.possible.contains("item"));
    }

    @Test
    void optionalTripleOnlyPossiblyBindsEntity() {
        Target target = target(
                "OPTIONAL { <http://example.com/s> <http://example.com/p> ?item }");

        BindingAnalysis.Bindings bindings = BindingAnalysis.before(
                target.query, target.service, target.parent, target.index);

        assertFalse(bindings.definite.contains("item"));
        assertTrue(bindings.possible.contains("item"));
    }

    @Test
    void unionRequiresBindingInEveryBranchForDefiniteResult() {
        Target target = target(
                "{ ?item <http://example.com/p> ?left } UNION "
                + "{ ?item <http://example.com/q> ?right }");

        BindingAnalysis.Bindings bindings = BindingAnalysis.before(
                target.query, target.service, target.parent, target.index);

        assertTrue(bindings.definite.contains("item"));
        assertFalse(bindings.definite.contains("left"));
        assertTrue(bindings.possible.contains("left"));
        assertTrue(bindings.possible.contains("right"));
    }

    @Test
    void valuesUndefAndBindRemainConservative() {
        Target target = target(
                "VALUES ?item { <http://example.com/a> UNDEF } "
                + "BIND(<http://example.com/b> AS ?bound)");

        BindingAnalysis.Bindings bindings = BindingAnalysis.before(
                target.query, target.service, target.parent, target.index);

        assertFalse(bindings.definite.contains("item"));
        assertFalse(bindings.definite.contains("bound"));
        assertTrue(bindings.possible.contains("item"));
        assertTrue(bindings.possible.contains("bound"));
    }

    @Test
    void projectedSubqueryBindingIsVisible() {
        Target target = target(
                "{ SELECT ?item WHERE { VALUES ?item { <http://example.com/a> } } }");

        BindingAnalysis.Bindings bindings = BindingAnalysis.before(
                target.query, target.service, target.parent, target.index);

        assertTrue(bindings.definite.contains("item"));
        assertTrue(bindings.possible.contains("item"));
    }
	
	/** Verifies the special compilation lifecycle. */
    @Test
    void compilationMarkerAvoidsCollisionAndRestoresSyntax() {
        Target target = target(
                "SERVICE <urn:wdqs-rewriter:binding-target> { ?x ?p ?o } ");
        Element original = target.parent.getElements().get(target.index);

        BindingAnalysis.before(target.query, target.service, target.parent, target.index);

        assertSame(original, target.parent.getElements().get(target.index));
    }

    private static Target target(String preceding) {
        Query query = QueryFactory.create(
                "SELECT * WHERE { " + preceding + " SERVICE <" + LABEL
                + "> { ?s ?p ?o } }", Syntax.syntaxSPARQL_11);
        ElementGroup group = (ElementGroup) query.getQueryPattern();
        for (int index = 0; index < group.getElements().size(); index++) {
            Element element = group.getElements().get(index);
            if (element instanceof ElementService
                    && LABEL.equals(((ElementService) element).getServiceNode().getURI())) {
                return new Target(query, group, index, (ElementService) element);
            }
        }
        throw new AssertionError("Test query has no label service.");
    }

    private static final class Target {
        final Query query;
        final ElementGroup parent;
        final int index;
        final ElementService service;

        Target(Query query, ElementGroup parent, int index, ElementService service) {
            this.query = query;
            this.parent = parent;
            this.index = index;
            this.service = service;
        }
    }
}
