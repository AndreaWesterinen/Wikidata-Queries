package io.github.andreawesterinen.wikidata.rewriter;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.Iterator;
import java.util.List;
import java.util.Set;

import org.apache.jena.graph.Node;
import org.apache.jena.graph.NodeFactory;
import org.apache.jena.graph.Triple;
import org.apache.jena.query.Query;
import org.apache.jena.sparql.algebra.Algebra;
import org.apache.jena.sparql.algebra.Op;
import org.apache.jena.sparql.algebra.Table;
import org.apache.jena.sparql.algebra.op.Op1;
import org.apache.jena.sparql.algebra.op.Op2;
import org.apache.jena.sparql.algebra.op.OpBGP;
import org.apache.jena.sparql.algebra.op.OpDiff;
import org.apache.jena.sparql.algebra.op.OpExtendAssign;
import org.apache.jena.sparql.algebra.op.OpGraph;
import org.apache.jena.sparql.algebra.op.OpGroup;
import org.apache.jena.sparql.algebra.op.OpJoin;
import org.apache.jena.sparql.algebra.op.OpLeftJoin;
import org.apache.jena.sparql.algebra.op.OpMinus;
import org.apache.jena.sparql.algebra.op.OpN;
import org.apache.jena.sparql.algebra.op.OpPath;
import org.apache.jena.sparql.algebra.op.OpProject;
import org.apache.jena.sparql.algebra.op.OpSequence;
import org.apache.jena.sparql.algebra.op.OpService;
import org.apache.jena.sparql.algebra.op.OpTable;
import org.apache.jena.sparql.algebra.op.OpUnion;
import org.apache.jena.sparql.core.Var;
import org.apache.jena.sparql.engine.binding.Binding;
import org.apache.jena.sparql.expr.ExprAggregator;
import org.apache.jena.sparql.syntax.Element;
import org.apache.jena.sparql.syntax.ElementGroup;
import org.apache.jena.sparql.syntax.ElementMinus;
import org.apache.jena.sparql.syntax.ElementNamedGraph;
import org.apache.jena.sparql.syntax.ElementOptional;
import org.apache.jena.sparql.syntax.ElementService;
import org.apache.jena.sparql.syntax.ElementSubQuery;
import org.apache.jena.sparql.syntax.ElementUnion;

/** Computes variables available when unoptimized algebra reaches a SERVICE. */
public final class BindingAnalysis {
    /** Prevents instantiation of a static binding-analysis utility. */
    private BindingAnalysis() {
    }

    /**
     * Temporarily marks the target SERVICE during algebra compilation and
     * returns variables bound before it.
     *
     * @param query complete parsed query containing the target SERVICE
     * @param service target SERVICE syntax element
     * @param parent group that directly contains the target SERVICE
     * @param index target SERVICE index within the parent group
     * @return definite and possible bindings reaching the target SERVICE
     * @throws IllegalStateException if the target cannot be located in algebra
     */
    public static Bindings before(Query query, ElementService service,
            ElementGroup parent, int index) {
        String marker = markerIri(query, 0);
        ElementService marked = new ElementService(
                NodeFactory.createURI(marker), service.getElement(), service.getSilent());
        Op root;
        parent.getElements().set(index, marked);
        try {
            root = Algebra.compile(query);
        } finally {
            parent.getElements().set(index, service);
        }
        Op target = findService(root, marker);
        if (target == null) {
            throw new IllegalStateException(
                    "The SERVICE has no exact unoptimized-algebra counterpart.");
        }
        Set<String> definite = findIncoming(
                root, target, new HashSet<String>(), true);
        Set<String> possible = findIncoming(
                root, target, new HashSet<String>(), false);
        if (definite == null || possible == null) {
            throw new IllegalStateException("The SERVICE is unreachable in algebra.");
        }
        return new Bindings(definite, possible);
    }

    /**
     * Finds the algebra SERVICE carrying the temporary marker IRI.
     *
     * @param op algebra subtree to search
     * @param marker temporary SERVICE IRI
     * @return matching SERVICE operator, or {@code null} when absent
     */
    private static Op findService(Op op, String marker) {
        if (op instanceof OpService
                && ((OpService) op).getService().isURI()
                && marker.equals(((OpService) op).getService().getURI())) {
            return op;
        }
        for (Op child : children(op)) {
            Op found = findService(child, marker);
            if (found != null) {
                return found;
            }
        }
        return null;
    }

    /**
     * Allocates a marker IRI not already used by a SERVICE.
     *
     * @param query query whose SERVICE IRIs must be avoided
     * @param initialSuffix first numeric suffix to consider
     * @return collision-free marker IRI
     */
    private static String markerIri(Query query, int initialSuffix) {
        int suffix = initialSuffix;
        while (true) {
            String marker = "urn:wdqs-rewriter:binding-target"
                    + (suffix == 0 ? "" : "-" + suffix);
            if (!containsServiceIri(query.getQueryPattern(), marker)) {
                return marker;
            }
            suffix++;
        }
    }

    /**
     * Tests whether a syntax subtree contains a SERVICE with the given IRI.
     *
     * @param element syntax subtree to inspect
     * @param iri SERVICE IRI to find
     * @return {@code true} when the IRI occurs as a SERVICE endpoint
     */
    private static boolean containsServiceIri(Element element, String iri) {
        if (element == null) {
            return false;
        }
        if (element instanceof ElementService) {
            ElementService service = (ElementService) element;
            if (service.getServiceNode().isURI()
                    && iri.equals(service.getServiceNode().getURI())) {
                return true;
            }
            return containsServiceIri(service.getElement(), iri);
        }
        if (element instanceof ElementGroup) {
            for (Element child : ((ElementGroup) element).getElements()) {
                if (containsServiceIri(child, iri)) {
                    return true;
                }
            }
        } else if (element instanceof ElementOptional) {
            return containsServiceIri(
                    ((ElementOptional) element).getOptionalElement(), iri);
        } else if (element instanceof ElementUnion) {
            for (Element child : ((ElementUnion) element).getElements()) {
                if (containsServiceIri(child, iri)) {
                    return true;
                }
            }
        } else if (element instanceof ElementMinus) {
            return containsServiceIri(
                    ((ElementMinus) element).getMinusElement(), iri);
        } else if (element instanceof ElementNamedGraph) {
            return containsServiceIri(
                    ((ElementNamedGraph) element).getElement(), iri);
        } else if (element instanceof ElementSubQuery) {
            return containsServiceIri(
                    ((ElementSubQuery) element).getQuery().getQueryPattern(), iri);
        }
        return false;
    }

    /**
     * Walks algebra in evaluation order to find bindings reaching a target.
     *
     * @param op current algebra operator
     * @param target target SERVICE operator
     * @param incoming bindings available before the current operator
     * @param definite whether to calculate definite rather than possible bindings
     * @return bindings reaching the target, or {@code null} when it is absent
     */
    private static Set<String> findIncoming(Op op, Op target,
            Set<String> incoming, boolean definite) {
        if (op == target) {
            return new HashSet<String>(incoming);
        }
        List<Op> children = children(op);
        if (op instanceof OpJoin || op instanceof OpSequence) {
            Set<String> current = new HashSet<String>(incoming);
            for (Op child : children) {
                Set<String> found = findIncoming(child, target, current, definite);
                if (found != null) {
                    return found;
                }
                current.addAll(summary(child, definite));
            }
            return null;
        }
        if (op instanceof OpLeftJoin && children.size() == 2) {
            Set<String> found = findIncoming(children.get(0), target, incoming, definite);
            if (found != null) {
                return found;
            }
            Set<String> rightIncoming = new HashSet<String>(incoming);
            rightIncoming.addAll(summary(children.get(0), definite));
            return findIncoming(children.get(1), target, rightIncoming, definite);
        }
        for (Op child : children) {
            Set<String> found = findIncoming(child, target, incoming, definite);
            if (found != null) {
                return found;
            }
        }
        return null;
    }

    /**
     * Summarizes variables produced by an algebra subtree.
     *
     * @param op algebra subtree to summarize
     * @param definite whether to return definite rather than possible bindings
     * @return variable names produced by the subtree
     */
    private static Set<String> summary(Op op, boolean definite) {
        return definite ? definitelyBound(op) : possiblyBound(op);
    }

    /**
     * Calculates variables bound on every result path from an algebra subtree.
     *
     * @param op algebra subtree to analyze
     * @return definitely bound variable names
     */
    private static Set<String> definitelyBound(Op op) {
        if (op instanceof OpBGP) {
            Set<String> result = new HashSet<String>();
            for (Triple triple : ((OpBGP) op).getPattern()) {
                addVariable(result, triple.getSubject());
                addVariable(result, triple.getPredicate());
                addVariable(result, triple.getObject());
            }
            return result;
        }
        if (op instanceof OpPath) {
            Set<String> result = new HashSet<String>();
            addVariable(result, ((OpPath) op).getTriplePath().getSubject());
            addVariable(result, ((OpPath) op).getTriplePath().getObject());
            return result;
        }
        if (op instanceof OpTable) {
            return tableBindings(((OpTable) op).getTable(), true);
        }
        List<Op> children = children(op);
        if (op instanceof OpJoin || op instanceof OpSequence) {
            Set<String> result = new HashSet<String>();
            for (Op child : children) {
                result.addAll(definitelyBound(child));
            }
            return result;
        }
        if ((op instanceof OpMinus || op instanceof OpDiff || op instanceof OpLeftJoin)
                && !children.isEmpty()) {
            return definitelyBound(children.get(0));
        }
        if (op instanceof OpUnion && !children.isEmpty()) {
            Set<String> result = definitelyBound(children.get(0));
            for (int i = 1; i < children.size(); i++) {
                result.retainAll(definitelyBound(children.get(i)));
            }
            return result;
        }
        if (children.size() == 1) {
            Set<String> result = definitelyBound(children.get(0));
            if (op instanceof OpGraph && ((OpGraph) op).getNode().isVariable()) {
                addVariable(result, ((OpGraph) op).getNode());
            }
            if (op instanceof OpProject) {
                result.retainAll(names(((OpProject) op).getVars()));
            }
            return result;
        }
        return new HashSet<String>();
    }

    /**
     * Calculates variables bound on at least one result path from a subtree.
     *
     * @param op algebra subtree to analyze
     * @return possibly bound variable names
     */
    private static Set<String> possiblyBound(Op op) {
        if (op instanceof OpBGP || op instanceof OpPath || op instanceof OpTable) {
            if (op instanceof OpBGP) {
                return definitelyBound(op);
            }
            if (op instanceof OpPath) {
                return definitelyBound(op);
            }
            return tableBindings(((OpTable) op).getTable(), false);
        }
        List<Op> children = children(op);
        Set<String> result = new HashSet<String>();
        if ((op instanceof OpMinus || op instanceof OpDiff) && !children.isEmpty()) {
            result.addAll(possiblyBound(children.get(0)));
        } else {
            for (Op child : children) {
                result.addAll(possiblyBound(child));
            }
        }
        if (op instanceof OpExtendAssign) {
            result.addAll(names(((OpExtendAssign) op).getVarExprList().getVars()));
        }
        if (op instanceof OpGroup) {
            OpGroup group = (OpGroup) op;
            result.addAll(names(group.getGroupVars().getVars()));
            for (ExprAggregator aggregate : group.getAggregators()) {
                result.add(aggregate.getVar().getVarName());
            }
        }
        if (op instanceof OpGraph && ((OpGraph) op).getNode().isVariable()) {
            addVariable(result, ((OpGraph) op).getNode());
        }
        if (op instanceof OpProject) {
            result.retainAll(names(((OpProject) op).getVars()));
        }
        return result;
    }

    /**
     * Calculates definite or possible bindings supplied by an inline table.
     *
     * @param table algebra table to analyze
     * @param definite whether to intersect rather than union row bindings
     * @return variable names bound by the table
     */
    private static Set<String> tableBindings(Table table, boolean definite) {
        Set<String> result = new HashSet<String>();
        Iterator<Binding> rows = table.rows();
        if (!rows.hasNext()) {
            return result;
        }
        Binding first = rows.next();
        for (Var variable : table.getVars()) {
            if (first.contains(variable)) {
                result.add(variable.getVarName());
            }
        }
        while (rows.hasNext()) {
            Binding row = rows.next();
            if (definite) {
                Set<String> present = new HashSet<String>();
                for (Var variable : table.getVars()) {
                    if (row.contains(variable)) {
                        present.add(variable.getVarName());
                    }
                }
                result.retainAll(present);
            } else {
                for (Var variable : table.getVars()) {
                    if (row.contains(variable)) {
                        result.add(variable.getVarName());
                    }
                }
            }
        }
        return result;
    }

    /**
     * Returns direct child operators in evaluation order.
     *
     * @param op parent algebra operator
     * @return ordered direct children, possibly empty
     */
    private static List<Op> children(Op op) {
        ArrayList<Op> result = new ArrayList<Op>();
        if (op instanceof Op1) {
            result.add(((Op1) op).getSubOp());
        } else if (op instanceof Op2) {
            result.add(((Op2) op).getLeft());
            result.add(((Op2) op).getRight());
        } else if (op instanceof OpN) {
            result.addAll(((OpN) op).getElements());
        }
        return result;
    }

    /**
     * Converts Jena variables to their names without sigils.
     *
     * @param variables Jena variables
     * @return variable-name set
     */
    private static Set<String> names(List<Var> variables) {
        Set<String> result = new HashSet<String>();
        for (Var variable : variables) {
            result.add(variable.getVarName());
        }
        return result;
    }

    /**
     * Adds a node's variable name when the node is a variable.
     *
     * @param result destination variable-name set
     * @param node RDF term or variable node to inspect
     */
    private static void addVariable(Set<String> result, Node node) {
        if (node.isVariable()) {
            result.add(Var.alloc(node).getVarName());
        }
    }

    /** Immutable variable sets at one SERVICE occurrence. */
    public static final class Bindings {
        final Set<String> definite;
        final Set<String> possible;

        /**
         * Stores definite and possible bindings for one SERVICE occurrence.
         *
         * @param definite variables bound on every evaluation path
         * @param possible variables bound on at least one evaluation path
         */
        Bindings(Set<String> definite, Set<String> possible) {
            this.definite = definite;
            this.possible = possible;
        }

        /**
         * Reports whether every evaluation path binds a variable.
         *
         * @param variable variable name without a sigil
         * @return {@code true} when the variable is definitely bound
         */
        public boolean isDefinitelyBound(String variable) {
            return definite.contains(variable);
        }

        /**
         * Reports whether at least one evaluation path may bind a variable.
         *
         * @param variable variable name without a sigil
         * @return {@code true} when the variable is possibly bound
         */
        public boolean isPossiblyBound(String variable) {
            return possible.contains(variable);
        }
    }
}
