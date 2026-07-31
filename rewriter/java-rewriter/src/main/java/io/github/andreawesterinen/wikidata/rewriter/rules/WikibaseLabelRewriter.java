package io.github.andreawesterinen.wikidata.rewriter.rules;

import java.util.ArrayList;
import java.util.Collections;
import java.util.HashSet;
import java.util.IdentityHashMap;
import java.util.Iterator;
import java.util.LinkedHashSet;
import java.util.List;
import java.util.Locale;
import java.util.Map;
import java.util.Set;

import org.apache.jena.atlas.json.JsonObject;
import org.apache.jena.graph.Node;
import org.apache.jena.graph.Triple;
import org.apache.jena.query.Query;
import org.apache.jena.query.QueryFactory;
import org.apache.jena.query.Syntax;
import org.apache.jena.sparql.core.TriplePath;
import org.apache.jena.sparql.core.Var;
import org.apache.jena.sparql.syntax.Element;
import org.apache.jena.sparql.syntax.ElementGroup;
import org.apache.jena.sparql.syntax.ElementMinus;
import org.apache.jena.sparql.syntax.ElementNamedGraph;
import org.apache.jena.sparql.syntax.ElementOptional;
import org.apache.jena.sparql.syntax.ElementPathBlock;
import org.apache.jena.sparql.syntax.ElementService;
import org.apache.jena.sparql.syntax.ElementSubQuery;
import org.apache.jena.sparql.syntax.ElementTriplesBlock;
import org.apache.jena.sparql.syntax.ElementUnion;
import org.apache.jena.sparql.syntax.PatternVars;
import org.apache.jena.sparql.util.FmtUtils;

import io.github.andreawesterinen.wikidata.rewriter.BindingAnalysis;
import io.github.andreawesterinen.wikidata.rewriter.RewriteResult;

/** Rewrites every supported wikibase:label service through Jena syntax objects. */
public final class WikibaseLabelRewriter {
    private static final String LABEL_SERVICE = "http://wikiba.se/ontology#label";
    private static final String SERVICE_PARAM = "http://www.bigdata.com/rdf#serviceParam";
    private static final String LANGUAGE_PARAM = "http://wikiba.se/ontology#language";
    private static final String RDFS_LABEL = "http://www.w3.org/2000/01/rdf-schema#label";
    private static final String DESCRIPTION = "http://schema.org/description";
    private static final String ALT_LABEL = "http://www.w3.org/2004/02/skos/core#altLabel";
    private static final String WIKIDATA_ENTITY = "http://www.wikidata.org/entity/";
    private static final String RULE_ID = "rewrite-wikibase-label-service";

    /** Prevents instantiation of the static label-service rewriter. */
    private WikibaseLabelRewriter() {
    }

    /**
     * Rewrites every supported label SERVICE in a parsed query.
     *
     * All scopes are planned before mutation so a failure cannot leave the
     * query partially rewritten.
     *
     * @param root complete parsed query
     * @return rewrite result, or {@code null} when no label SERVICE is present
     */
    public static RewriteResult rewrite(Query root) {
        List<Query> scopes = new ArrayList<Query>();
        collectScopes(root, scopes);
        List<ScopePlan> plans = new ArrayList<ScopePlan>();
        LinkedHashSet<String> variants = new LinkedHashSet<String>();
        Set<String> variables = allVariables(root);
        try {
            for (Query scope : scopes) {
                ScopePlan plan = planScope(scope, variables, variants);
                if (plan != null) {
                    plans.add(plan);
                }
            }
        } catch (RuleFailure failure) {
            RewriteResult result = new RewriteResult(failure.status, null);
            if (failure.includeRewrite && failure.variant != null) {
                result.addRewrite(rewriteRecord(failure.variant));
            }
            result.addError(errorRecord(failure));
            return result;
        }
        if (plans.isEmpty()) {
            return null;
        }
        for (ScopePlan plan : plans) {
            plan.apply();
        }
        RewriteResult result = new RewriteResult("rewritten", null);
        for (String variant : variants) {
            result.addRewrite(rewriteRecord(variant));
        }
        return result;
    }

    /**
     * Plans replacements in one query scope without entering subqueries.
     *
     * @param query query or subquery scope to plan
     * @param variables variable names reserved throughout the complete query
     * @param variants variants encountered across all planned scopes
     * @return immutable-location replacement plan, or {@code null} if unused
     */
    private static ScopePlan planScope(Query query, Set<String> variables,
            LinkedHashSet<String> variants) {
        List<ServiceOccurrence> services = new ArrayList<ServiceOccurrence>();
        scan(query.getQueryPattern(), "where", services);
        if (services.isEmpty()) {
            return null;
        }

        List<ServiceInfo> infos = new ArrayList<ServiceInfo>();
        Map<ElementGroup, List<String>> languageValuesByGroup =
                new IdentityHashMap<ElementGroup, List<String>>();
        for (ServiceOccurrence occurrence : services) {
            ServiceInfo info = classify(occurrence);
            infos.add(info);
            List<String> languageValues = languageValuesByGroup.get(occurrence.parent);
            if (languageValues == null) {
                languageValues = new ArrayList<String>();
                languageValuesByGroup.put(occurrence.parent, languageValues);
            }
            languageValues.addAll(info.languages);
        }
        String uniformVariant = uniformVariant(infos);
        Map<ElementGroup, List<String>> languagesByGroup =
                new IdentityHashMap<ElementGroup, List<String>>();
        for (ServiceInfo info : infos) {
            ElementGroup group = info.occurrence.parent;
            if (languagesByGroup.containsKey(group)) {
                continue;
            }
            List<String> languageValues = languageValuesByGroup.get(group);
            if (languageValues.isEmpty()) {
                throw failure("wikibase_label_missing_language_parameter",
                        uniformVariant, true, info.occurrence.path);
            }
            List<String> languages = normalizeLanguages(languageValues);
            if (languages.isEmpty()) {
                throw failure("wikibase_label_malformed_language_parameter",
                        uniformVariant, true, info.occurrence.path);
            }
            languagesByGroup.put(group, languages);
        }

        List<Output> automatic = automaticOutputs(query);
        Set<String> patternVariables = variablesOutsideLabelServices(query.getQueryPattern());
        Set<String> nestedOutputs = nestedOutputVariables(query.getQueryPattern());
        Set<ElementGroup> automaticGroups = Collections.newSetFromMap(
                new IdentityHashMap<ElementGroup, Boolean>());
        ScopePlan plan = new ScopePlan(query);
        if (query.isDescribeType()) {
            List<Output> describeOutputs = new ArrayList<Output>();
            for (Var variable : query.getProjectVars()) {
                addAutomaticOutput(variable, describeOutputs);
            }
            for (Output output : describeOutputs) {
                plan.describeVariables.add(Var.alloc(output.output));
            }
        }
        for (ServiceInfo info : infos) {
            variants.add(info.variant);
            List<String> languages = languagesByGroup.get(info.occurrence.parent);
            List<Output> outputs;
            if ("manual".equals(info.variant)) {
                outputs = manualOutputs(info);
            } else {
                if (query.isAskType() || query.isDescribeType()) {
                    outputs = new ArrayList<Output>();
                } else if (automaticGroups.contains(info.occurrence.parent)) {
                    outputs = new ArrayList<Output>();
                } else {
                    outputs = validAutomaticOutputs(
                            automatic, patternVariables, nestedOutputs);
                    automaticGroups.add(info.occurrence.parent);
                }
            }

            Set<String> emittedGuards = new HashSet<String>();
            StringBuilder replacement = new StringBuilder();
            BindingAnalysis.Bindings bindings =
                    BindingAnalysis.before(query, info.occurrence.service,
                            info.occurrence.parent, info.occurrence.index);
            for (Output output : outputs) {
                String entity = variableName(output.entity);
                if ("alias".equals(output.type) && entity != null
                        && !bindings.isPossiblyBound(entity)) {
                    continue;
                }
                prepareOutput(output, info.occurrence, bindings, variables);
                if (output.guard != null && emittedGuards.add(output.guard)) {
                    replacement.append(guardBind(output));
                }
                if (output.prebound) {
                    replacement.append(preboundBlock(output, languages));
                } else if ("label".equals(output.type)) {
                    replacement.append(labelBlocks(output, labelLanguages(languages)));
                } else if ("description".equals(output.type)) {
                    replacement.append(descriptionBlocks(
                            output, descriptionLanguages(languages)));
                } else {
                    replacement.append(aliasBlocks(output, labelLanguages(languages)));
                }
            }
            plan.replacements.add(new Replacement(
                    info.occurrence, parseElements(replacement.toString())));
        }
        return plan;
    }

    /**
     * Validates one requested output and assigns its generated variables.
     *
     * @param output output mapping being prepared
     * @param occurrence containing label SERVICE occurrence
     * @param bindings bindings reaching the SERVICE
     * @param variables names already reserved in the complete query
     */
    private static void prepareOutput(Output output, ServiceOccurrence occurrence,
            BindingAnalysis.Bindings bindings, Set<String> variables) {
        String entity = variableName(output.entity);
        List<Element> preceding = occurrence.preceding();
        if (entity != null && !bindings.isDefinitelyBound(entity)
                && !"alias".equals(output.type)) {
            output.guard = allocate(entity + "__label_subject", variables);
            variables.add(output.guard);
        }
        if ("alias".equals(output.type) && entity != null) {
            output.candidates = candidates(preceding, entity);
            if (output.candidates.isEmpty()) {
                throw failure("wikibase_label_alias_entity_not_safely_constrained",
                        output.variant, true, occurrence.path);
            }
        }
        if ("alias".equals(output.type) && entity == null) {
            output.fixedCandidate = allocate(output.output + "__entity_candidate", variables);
            variables.add(output.fixedCandidate);
        }
        output.value = allocate(output.base + "__" + output.type + "_value", variables);
        variables.add(output.value);
        output.prebound = bindings.isPossiblyBound(output.output);
        if (output.prebound) {
            output.candidates = candidates(preceding, entity == null ? "" : entity);
        }
    }

    /**
     * Classifies and validates one label SERVICE body.
     *
     * @param occurrence label SERVICE occurrence to inspect
     * @return parsed language parameters, manual triples, and selected variant
     */
    private static ServiceInfo classify(ServiceOccurrence occurrence) {
        Element body = occurrence.service.getElement();
        if (!(body instanceof ElementGroup)) {
            throw failure("wikibase_label_unsupported_service_content",
                    null, false, occurrence.path);
        }
        List<Triple> triples = new ArrayList<Triple>();
        for (Element element : ((ElementGroup) body).getElements()) {
            if (element instanceof ElementTriplesBlock) {
                Iterator<Triple> iterator = ((ElementTriplesBlock) element).patternElts();
                while (iterator.hasNext()) {
                    triples.add(iterator.next());
                }
            } else if (element instanceof ElementPathBlock) {
                Iterator<TriplePath> iterator = ((ElementPathBlock) element).patternElts();
                while (iterator.hasNext()) {
                    TriplePath path = iterator.next();
                    if (!path.isTriple()) {
                        throw failure("wikibase_label_unsupported_service_content",
                                null, false, occurrence.path);
                    }
                    triples.add(path.asTriple());
                }
            } else {
                throw failure("wikibase_label_unsupported_service_content",
                        null, false, occurrence.path);
            }
        }

        ServiceInfo info = new ServiceInfo(occurrence);
        for (Triple triple : triples) {
            if (uriEquals(triple.getSubject(), SERVICE_PARAM)
                    && uriEquals(triple.getPredicate(), LANGUAGE_PARAM)) {
                info.languageNodes.add(triple.getObject());
            } else if (outputType(triple.getPredicate()) != null) {
                info.manual.add(triple);
            } else {
                throw failure("wikibase_label_unsupported_service_content",
                        null, false, occurrence.path);
            }
        }
        info.variant = info.manual.isEmpty() ? "automatic" : "manual";
        for (Node language : info.languageNodes) {
            if (!language.isLiteral()) {
                throw failure("wikibase_label_malformed_language_parameter",
                        info.variant, true, occurrence.path);
            }
            info.languages.add(language.getLiteralLexicalForm());
        }
        return info;
    }

    /**
     * Converts supported manual triples into distinct output mappings.
     *
     * @param info classified manual SERVICE information
     * @return mappings in SERVICE order, using the first mapping per output
     */
    private static List<Output> manualOutputs(ServiceInfo info) {
        List<Output> outputs = new ArrayList<Output>();
        Set<String> assigned = new HashSet<String>();
        for (Triple triple : info.manual) {
            Node entity = triple.getSubject();
            Node target = triple.getObject();
            if (!target.isVariable()) {
                throw failure("wikibase_label_manual_output_not_variable",
                        "manual", true, info.occurrence.path);
            }
            if (!entity.isVariable() && !entity.isURI()) {
                throw failure("wikibase_label_invalid_entity_term",
                        "manual", true, info.occurrence.path);
            }
            String output = Var.alloc(target).getVarName();
            if (!assigned.add(output)) {
                continue;
            }
            String type = outputType(triple.getPredicate());
            outputs.add(new Output(entity, output, type,
                    internalBase(entity, output, type), "manual"));
        }
        return outputs;
    }

    /**
     * Derives automatic outputs from SELECT projections or CONSTRUCT templates.
     *
     * @param query query or subquery containing the automatic label service
     * @return automatic output mappings in projection or template order
     */
    private static List<Output> automaticOutputs(Query query) {
        List<Output> outputs = new ArrayList<Output>();
        if (query.isSelectType() && !query.isQueryResultStar()) {
            for (Var variable : query.getProjectVars()) {
                addAutomaticOutput(variable, outputs);
            }
        } else if (query.isConstructType() && query.getConstructTemplate() != null) {
            Set<String> seen = new LinkedHashSet<String>();
            for (Triple triple : query.getConstructTemplate().getTriples()) {
                Node[] nodes = {triple.getSubject(), triple.getPredicate(), triple.getObject()};
                for (Node node : nodes) {
                    if (node.isVariable()
                            && seen.add(Var.alloc(node).getVarName())) {
                        addAutomaticOutput(Var.alloc(node), outputs);
                    }
                }
            }
        }
        return outputs;
    }

    /** Adds one recognized suffix mapping, or ignores an unrelated variable. */
    private static void addAutomaticOutput(Var variable, List<Output> outputs) {
        String output = variable.getVarName();
        String base = null;
        String type = null;
        if (output.endsWith("AltLabel") && output.length() > 8) {
            base = output.substring(0, output.length() - 8);
            type = "alias";
        } else if (output.endsWith("Description") && output.length() > 11) {
            base = output.substring(0, output.length() - 11);
            type = "description";
        } else if (output.endsWith("Label") && output.length() > 5) {
            base = output.substring(0, output.length() - 5);
            type = "label";
        }
        if (base == null) {
            return;
        }
        outputs.add(new Output(Var.alloc(base), output, type, base, "automatic"));
    }

    /**
     * Keeps automatic outputs whose entity variables occur outside label services.
     *
     * @param outputs projected automatic output mappings
     * @param patternVariables variables used by ordinary patterns in this scope
     * @param nestedOutputs variables produced by nested query scopes
     * @return independent copies of valid automatic output mappings
     */
    private static List<Output> validAutomaticOutputs(
            List<Output> outputs, Set<String> patternVariables,
            Set<String> nestedOutputs) {
        List<Output> result = new ArrayList<Output>();
        for (Output output : outputs) {
            if (patternVariables.contains(output.base)
                    && !nestedOutputs.contains(output.output)) {
                result.add(output.copy());
            }
        }
        return result;
    }

    /** Returns variables projected by nested query scopes. */
    private static Set<String> nestedOutputVariables(Element element) {
        Set<String> result = new HashSet<String>();
        collectNestedOutputs(element, result);
        return result;
    }

    /** Collects projected variables without treating them as outputs of this scope. */
    private static void collectNestedOutputs(Element element, Set<String> result) {
        if (element == null) {
            return;
        }
        if (element instanceof ElementSubQuery) {
            Query nested = ((ElementSubQuery) element).getQuery();
            for (Var variable : nested.getProjectVars()) {
                result.add(variable.getVarName());
            }
            collectNestedOutputs(nested.getQueryPattern(), result);
        } else if (element instanceof ElementGroup) {
            for (Element child : ((ElementGroup) element).getElements()) {
                collectNestedOutputs(child, result);
            }
        } else if (element instanceof ElementOptional) {
            collectNestedOutputs(((ElementOptional) element).getOptionalElement(), result);
        } else if (element instanceof ElementUnion) {
            for (Element child : ((ElementUnion) element).getElements()) {
                collectNestedOutputs(child, result);
            }
        } else if (element instanceof ElementMinus) {
            collectNestedOutputs(((ElementMinus) element).getMinusElement(), result);
        } else if (element instanceof ElementNamedGraph) {
            collectNestedOutputs(((ElementNamedGraph) element).getElement(), result);
        } else if (element instanceof ElementService) {
            collectNestedOutputs(((ElementService) element).getElement(), result);
        }
    }

    /**
     * Builds a guard binding for a possibly unbound entity variable.
     *
     * @param output prepared output mapping containing a guard variable
     * @return SPARQL BIND fragment
     */
    private static String guardBind(Output output) {
        return "BIND(IF(BOUND(?" + output.base + "), ?" + output.base
                + ", \"unbound\") AS ?" + output.guard + ")\n";
    }

    /**
     * Builds ordered label lookups and the required label fallback.
     *
     * @param output prepared label output mapping
     * @param languages normalized label-language priority
     * @return SPARQL graph-pattern fragment
     */
    private static String labelBlocks(Output output, List<String> languages) {
        String subject = output.guard == null ? term(output.entity) : "?" + output.guard;
        StringBuilder result = new StringBuilder();
        for (String language : languages) {
            result.append(lookup(subject, RDFS_LABEL, output.value, language));
        }
        String entity = term(output.entity);
        String fallback = output.entity.isURI()
                && !output.entity.getURI().startsWith(WIKIDATA_ENTITY)
                ? "STR(" + entity + ")"
                : "STRAFTER(STR(" + entity + "), \"entity/\")";
        result.append("BIND(IF(BOUND(?").append(output.value)
                .append("), STR(?").append(output.value).append("), ")
                .append(fallback).append(") AS ?").append(output.output).append(")\n");
        return result.toString();
    }

    /**
     * Builds ordered description lookups without a fallback value.
     *
     * @param output prepared description output mapping
     * @param languages normalized description-language priority
     * @return SPARQL graph-pattern fragment
     */
    private static String descriptionBlocks(Output output, List<String> languages) {
        String subject = output.guard == null ? term(output.entity) : "?" + output.guard;
        StringBuilder result = new StringBuilder();
        for (String language : languages) {
            result.append(lookup(subject, DESCRIPTION, output.value, language));
        }
        result.append("BIND(STR(?").append(output.value).append(") AS ?")
                .append(output.output).append(")\n");
        return result.toString();
    }

    /**
     * Builds one language-filtered OPTIONAL scalar lookup.
     *
     * @param subject rendered lookup subject
     * @param predicate resolved label or description predicate IRI
     * @param value generated value-variable name
     * @param language normalized language token
     * @return SPARQL OPTIONAL fragment
     */
    private static String lookup(String subject, String predicate, String value,
            String language) {
        return "OPTIONAL { " + subject + " <" + predicate + "> ?" + value
                + " . FILTER(LANG(?" + value + ") = " + literal(language) + ") }\n";
    }

    /**
     * Builds ordered, candidate-constrained alias aggregation subqueries.
     *
     * @param output prepared alias output mapping
     * @param languages normalized alias-language priority
     * @return SPARQL graph-pattern fragment
     */
    private static String aliasBlocks(Output output, List<String> languages) {
        StringBuilder result = new StringBuilder();
        for (String language : languages) {
            String entity;
            String candidate;
            if (output.fixedCandidate != null) {
                entity = output.fixedCandidate;
                candidate = "VALUES ?" + entity + " { " + term(output.entity) + " }";
            } else {
                entity = variableName(output.entity);
                candidate = candidateSubquery(entity, output.candidates);
            }
            result.append("OPTIONAL { SELECT ?").append(entity)
                    .append(" (GROUP_CONCAT(DISTINCT ?").append(output.value)
                    .append("; SEPARATOR=\", \") AS ?").append(output.output)
                    .append(") WHERE { ").append(candidate).append(" ?")
                    .append(entity).append(" <").append(ALT_LABEL).append("> ?")
                    .append(output.value).append(" . FILTER(LANG(?")
                    .append(output.value).append(") = ").append(literal(language))
                    .append(") } GROUP BY ?").append(entity).append(" }\n");
        }
        return result.toString();
    }

    /**
     * Preserves an already-bound scalar output through a candidate relation.
     *
     * @param output prepared label or description output mapping
     * @param languages normalized scope language list
     * @return SPARQL OPTIONAL subquery, or an empty string if unavailable
     */
    private static String preboundBlock(Output output, List<String> languages) {
        String entity = variableName(output.entity);
        if (entity == null || output.candidates == null || output.candidates.isEmpty()) {
            return "";
        }
        String body;
        if ("label".equals(output.type)) {
            body = labelBlocks(output, labelLanguages(languages));
        } else {
            body = descriptionBlocks(output, descriptionLanguages(languages));
        }
        return "OPTIONAL { SELECT ?" + entity + " ?" + output.output + " WHERE { "
                + candidateSubquery(entity, output.candidates) + body + " } }\n";
    }

    /**
     * Builds a distinct candidate-entity relation from preceding patterns.
     *
     * @param entity candidate entity-variable name
     * @param elements preceding in-scope binding patterns
     * @return SPARQL subquery fragment
     */
    private static String candidateSubquery(String entity, List<Element> elements) {
        StringBuilder body = new StringBuilder();
        for (Element element : elements) {
            body.append(element.toString()).append('\n');
        }
        return "{ SELECT DISTINCT ?" + entity + " WHERE { " + body + " } }\n";
    }

    /**
     * Parses generated graph-pattern text into typed Jena elements.
     *
     * @param text generated graph-pattern fragment
     * @return parsed top-level elements in source order
     */
    private static List<Element> parseElements(String text) {
        if (text.trim().isEmpty()) {
            return new ArrayList<Element>();
        }
        Query parsed = QueryFactory.create(
                "SELECT * WHERE {\n" + text + "}\n", Syntax.syntaxSPARQL_11);
        return new ArrayList<Element>(
                ((ElementGroup) parsed.getQueryPattern()).getElements());
    }

    /**
     * Normalizes concatenated language parameters while preserving priority.
     *
     * @param values language-parameter lexical values in source order
     * @return trimmed, deduplicated, normalized language tokens
     */
    private static List<String> normalizeLanguages(List<String> values) {
        List<String> result = new ArrayList<String>();
        Set<String> seen = new HashSet<String>();
        for (String value : values) {
            for (String token : value.split(",", -1)) {
                String stripped = token.trim();
                if (stripped.isEmpty()) {
                    continue;
                }
                String normalized = "[AUTO_LANGUAGE]".equals(stripped)
                        ? stripped : stripped.toLowerCase(Locale.ROOT);
                if (seen.add(normalized.toLowerCase(Locale.ROOT))) {
                    result.add(normalized);
                }
            }
        }
        return result;
    }

    /**
     * Adds the terminal {@code mul} fallback when it was not explicitly listed.
     *
     * @param languages normalized scope language list
     * @return independent label and alias language list
     */
    private static List<String> labelLanguages(List<String> languages) {
        List<String> result = new ArrayList<String>(languages);
        for (String language : languages) {
            if ("mul".equalsIgnoreCase(language)) {
                return result;
            }
        }
        result.add("mul");
        return result;
    }

    /**
     * Removes {@code mul}, which is not valid for descriptions.
     *
     * @param languages normalized scope language list
     * @return independent description language list
     */
    private static List<String> descriptionLanguages(List<String> languages) {
        List<String> result = new ArrayList<String>();
        for (String language : languages) {
            if (!"mul".equalsIgnoreCase(language)) {
                result.add(language);
            }
        }
        return result;
    }

    /**
     * Finds label services in source order without entering subqueries.
     *
     * @param element current syntax subtree
     * @param path structural path of the current subtree
     * @param result destination occurrence list
     */
    private static void scan(Element element, String path, List<ServiceOccurrence> result) {
        if (element == null) {
            return;
        }
        if (element instanceof ElementGroup) {
            ElementGroup group = (ElementGroup) element;
            for (int i = 0; i < group.getElements().size(); i++) {
                Element child = group.getElements().get(i);
                String childPath = path + ".elements[" + i + "]";
                if (child instanceof ElementService && isLabelService((ElementService) child)) {
                    result.add(new ServiceOccurrence((ElementService) child, group, i, childPath));
                } else {
                    scan(child, childPath, result);
                }
            }
        } else if (element instanceof ElementOptional) {
            scan(((ElementOptional) element).getOptionalElement(), path + ".element", result);
        } else if (element instanceof ElementUnion) {
            List<Element> branches = ((ElementUnion) element).getElements();
            for (int i = 0; i < branches.size(); i++) {
                scan(branches.get(i), path + ".elements[" + i + "]", result);
            }
        } else if (element instanceof ElementMinus) {
            scan(((ElementMinus) element).getMinusElement(), path + ".element", result);
        } else if (element instanceof ElementNamedGraph) {
            scan(((ElementNamedGraph) element).getElement(), path + ".element", result);
        } else if (element instanceof ElementSubQuery) {
            return;
        } else if (element instanceof ElementService) {
            scan(((ElementService) element).getElement(), path + ".element", result);
        }
    }

    /**
     * Collects a query scope followed by its nested scopes in source order.
     *
     * @param query current query scope
     * @param result destination scope list
     */
    private static void collectScopes(Query query, List<Query> result) {
        result.add(query);
        collectNested(query.getQueryPattern(), result);
    }

    /**
     * Finds subqueries under a syntax subtree in source order.
     *
     * @param element current syntax subtree
     * @param result destination scope list
     */
    private static void collectNested(Element element, List<Query> result) {
        if (element == null) {
            return;
        }
        if (element instanceof ElementSubQuery) {
            collectScopes(((ElementSubQuery) element).getQuery(), result);
        } else if (element instanceof ElementGroup) {
            for (Element child : ((ElementGroup) element).getElements()) {
                collectNested(child, result);
            }
        } else if (element instanceof ElementOptional) {
            collectNested(((ElementOptional) element).getOptionalElement(), result);
        } else if (element instanceof ElementUnion) {
            for (Element child : ((ElementUnion) element).getElements()) {
                collectNested(child, result);
            }
        } else if (element instanceof ElementMinus) {
            collectNested(((ElementMinus) element).getMinusElement(), result);
        } else if (element instanceof ElementNamedGraph) {
            collectNested(((ElementNamedGraph) element).getElement(), result);
        } else if (element instanceof ElementService) {
            collectNested(((ElementService) element).getElement(), result);
        }
    }

    /**
     * Collects variables used outside label services in one query scope.
     *
     * @param element query-pattern root
     * @return variable names eligible as automatic entity variables
     */
    private static Set<String> variablesOutsideLabelServices(Element element) {
        Set<String> result = new HashSet<String>();
        collectVariables(element, result);
        return result;
    }

    /**
     * Recursively collects variables while excluding label services and subqueries.
     *
     * @param element current syntax subtree
     * @param result destination variable-name set
     */
    private static void collectVariables(Element element, Set<String> result) {
        if (element == null || element instanceof ElementSubQuery) {
            return;
        }
        if (element instanceof ElementService && isLabelService((ElementService) element)) {
            return;
        }
        if (element instanceof ElementGroup) {
            for (Element child : ((ElementGroup) element).getElements()) {
                collectVariables(child, result);
            }
        } else if (element instanceof ElementOptional) {
            collectVariables(((ElementOptional) element).getOptionalElement(), result);
        } else if (element instanceof ElementUnion) {
            for (Element child : ((ElementUnion) element).getElements()) {
                collectVariables(child, result);
            }
        } else {
            for (Var variable : PatternVars.vars(element)) {
                result.add(variable.getVarName());
            }
        }
    }

    /**
     * Collects every variable name reserved in a complete query.
     *
     * @param query root query
     * @return variable names from the root and nested scopes
     */
    private static Set<String> allVariables(Query query) {
        Set<String> result = new HashSet<String>();
        collectQueryVariables(query, result);
        return result;
    }

    /**
     * Collects variables from a query and every nested subquery.
     *
     * @param query current query scope
     * @param result destination variable-name set used for collision checks
     */
    private static void collectQueryVariables(Query query, Set<String> result) {
        for (Var variable : PatternVars.vars(query.getQueryPattern())) {
            result.add(variable.getVarName());
        }
        for (Var variable : query.getProjectVars()) {
            result.add(variable.getVarName());
        }
        collectNestedQueryVariables(query.getQueryPattern(), result);
    }

    /**
     * Finds nested scopes whose variables must participate in collision checks.
     *
     * @param element current syntax subtree
     * @param result destination variable-name set
     */
    private static void collectNestedQueryVariables(Element element, Set<String> result) {
        if (element == null) {
            return;
        }
        if (element instanceof ElementSubQuery) {
            collectQueryVariables(((ElementSubQuery) element).getQuery(), result);
        } else if (element instanceof ElementGroup) {
            for (Element child : ((ElementGroup) element).getElements()) {
                collectNestedQueryVariables(child, result);
            }
        } else if (element instanceof ElementOptional) {
            collectNestedQueryVariables(((ElementOptional) element).getOptionalElement(), result);
        } else if (element instanceof ElementUnion) {
            for (Element child : ((ElementUnion) element).getElements()) {
                collectNestedQueryVariables(child, result);
            }
        } else if (element instanceof ElementMinus) {
            collectNestedQueryVariables(((ElementMinus) element).getMinusElement(), result);
        } else if (element instanceof ElementNamedGraph) {
            collectNestedQueryVariables(((ElementNamedGraph) element).getElement(), result);
        } else if (element instanceof ElementService) {
            collectNestedQueryVariables(((ElementService) element).getElement(), result);
        }
    }

    /**
     * Selects preceding patterns that may bind an entity variable.
     *
     * @param elements preceding elements in the containing group
     * @param variable entity-variable name
     * @return candidate-binding elements in source order
     */
    private static List<Element> candidates(List<Element> elements, String variable) {
        List<Element> result = new ArrayList<Element>();
        if (variable == null || variable.isEmpty()) {
            return result;
        }
        Var target = Var.alloc(variable);
        for (Element element : elements) {
            if (PatternVars.vars(element).contains(target)) {
                result.add(element);
            }
        }
        return result;
    }

    /**
     * Tests whether a SERVICE endpoint resolves to {@code wikibase:label}.
     *
     * @param service SERVICE element to classify
     * @return {@code true} for the canonical label-service IRI
     */
    private static boolean isLabelService(ElementService service) {
        return uriEquals(service.getServiceNode(), LABEL_SERVICE);
    }

    /**
     * Compares an RDF node with a resolved IRI.
     *
     * @param node RDF node to inspect
     * @param iri expected absolute IRI
     * @return {@code true} when the node is that IRI
     */
    private static boolean uriEquals(Node node, String iri) {
        return node != null && node.isURI() && iri.equals(node.getURI());
    }

    /**
     * Classifies a supported manual output predicate.
     *
     * @param predicate resolved predicate node
     * @return {@code label}, {@code description}, {@code alias}, or {@code null}
     */
    private static String outputType(Node predicate) {
        if (uriEquals(predicate, RDFS_LABEL)) {
            return "label";
        }
        if (uriEquals(predicate, DESCRIPTION)) {
            return "description";
        }
        if (uriEquals(predicate, ALT_LABEL)) {
            return "alias";
        }
        return null;
    }

    /**
     * Extracts a variable name without its sigil.
     *
     * @param node RDF term or variable node
     * @return variable name, or {@code null} for a non-variable
     */
    private static String variableName(Node node) {
        return node != null && node.isVariable() ? Var.alloc(node).getVarName() : null;
    }

    /**
     * Chooses the base name for generated variables associated with an output.
     *
     * @param entity source entity term
     * @param output output-variable name
     * @param type output type
     * @return illustrative internal base name
     */
    private static String internalBase(Node entity, String output, String type) {
        String variable = variableName(entity);
        if (variable != null) {
            return variable;
        }
        String suffix = "label".equals(type) ? "Label"
                : "description".equals(type) ? "Description" : "AltLabel";
        return output.endsWith(suffix) && output.length() > suffix.length()
                ? output.substring(0, output.length() - suffix.length()) : output;
    }

    /**
     * Allocates the first available collision-free variable name.
     *
     * @param base preferred variable name
     * @param variables names already reserved in the complete query
     * @return available variable name
     */
    private static String allocate(String base, Set<String> variables) {
        if (!variables.contains(base)) {
            return base;
        }
        int suffix = 1;
        while (variables.contains(base + "__" + suffix)) {
            suffix++;
        }
        return base + "__" + suffix;
    }

    /**
     * Renders an RDF term using Jena's SPARQL formatter.
     *
     * @param node RDF term to render
     * @return SPARQL term text
     */
    private static String term(Node node) {
        return FmtUtils.stringForNode(node);
    }

    /**
     * Renders a string literal with SPARQL escaping.
     *
     * @param value unescaped lexical value
     * @return escaped SPARQL string literal
     */
    private static String literal(String value) {
        return FmtUtils.stringForString(value);
    }

    /**
     * Returns the shared variant when every SERVICE has the same variant.
     *
     * @param infos classified services in one scope
     * @return shared variant, or {@code null} for mixed variants
     */
    private static String uniformVariant(List<ServiceInfo> infos) {
        String variant = null;
        for (ServiceInfo info : infos) {
            if (variant == null) {
                variant = info.variant;
            } else if (!variant.equals(info.variant)) {
                return null;
            }
        }
        return variant;
    }

    /**
     * Builds one applied-rewrite result record.
     *
     * @param variant applied label-service variant
     * @return JSON rewrite record
     */
    private static JsonObject rewriteRecord(String variant) {
        JsonObject record = new JsonObject();
        record.put("rule_id", RULE_ID);
        record.put("variant_id", variant);
        return record;
    }

    /**
     * Converts an internal rule failure to the public error schema.
     *
     * @param failure classified rule failure
     * @return JSON error record
     */
    private static JsonObject errorRecord(RuleFailure failure) {
        JsonObject error = new JsonObject();
        error.put("code", failure.code);
        error.put("phase", "post-ARQ");
        error.put("rule_id", RULE_ID);
        if (failure.variant != null) {
            error.put("variant_id", failure.variant);
        }
        error.put("message", failure.message);
        error.put("source_location", failure.path);
        error.put("retry_classification", failure.retry);
        error.put("diagnostic_reference", failure.reference);
        return error;
    }

    /**
     * Creates the contract-defined failure for an error code.
     *
     * @param code stable label-service error code
     * @param variant selected variant, or {@code null} if unknown
     * @param includeRewrite whether to include a rewrite record
     * @param path structural source location
     * @return throwable rule failure
     */
    private static RuleFailure failure(String code, String variant,
            boolean includeRewrite, String path) {
        if ("wikibase_label_missing_language_parameter".equals(code)) {
            return new RuleFailure(code, "blazegraph_error",
                    "The label service has no wikibase:language parameter.",
                    "after_input_or_parser_change", "docs/services/wikibase-label.md#language-list-normalization",
                    variant, includeRewrite, path);
        }
        if ("wikibase_label_alias_entity_not_safely_constrained".equals(code)) {
            return new RuleFailure(code, "skipped_unsupported",
                    "The alias entity variable cannot be safely constrained from preceding in-scope binding patterns.",
                    "after_catalog_change", "docs/services/wikibase-label.md#alias-aggregation",
                    variant, includeRewrite, path);
        }
        if ("wikibase_label_manual_output_not_variable".equals(code)) {
            return new RuleFailure(code, "skipped_unsupported",
                    "A manual label-service output is not a variable.",
                    "after_catalog_change", "docs/services/wikibase-label.md#manual-and-automatic-modes",
                    variant, includeRewrite, path);
        }
        if ("wikibase_label_malformed_language_parameter".equals(code)) {
            return new RuleFailure(code, "skipped_unsupported",
                    "The wikibase:language parameter is not a supported literal comma-separated list.",
                    "after_catalog_change", "docs/services/wikibase-label.md#language-list-normalization",
                    variant, includeRewrite, path);
        }
        if ("wikibase_label_invalid_entity_term".equals(code)) {
            return new RuleFailure(code, "skipped_unsupported",
                    "A manual label-service entity term is not a variable or fixed IRI.",
                    "after_catalog_change", "docs/services/wikibase-label.md#unsupported-cases",
                    variant, includeRewrite, path);
        }
        return new RuleFailure(code, "skipped_unsupported",
                "The label service contains unsupported content.",
                "after_catalog_change", "docs/services/wikibase-label.md#unsupported-cases",
                variant, includeRewrite, path);
    }

    private static final class ServiceOccurrence {
        final ElementService service;
        final ElementGroup parent;
        final int index;
        final String path;

        /**
         * Records one label SERVICE and its exact containing-group location.
         *
         * @param service label SERVICE element
         * @param parent directly containing group
         * @param index position in the containing group
         * @param path structural source location
         */
        ServiceOccurrence(ElementService service, ElementGroup parent, int index, String path) {
            this.service = service;
            this.parent = parent;
            this.index = index;
            this.path = path;
        }

        /**
         * Copies all sibling elements preceding this SERVICE.
         *
         * @return preceding elements in source order
         */
        List<Element> preceding() {
            return new ArrayList<Element>(parent.getElements().subList(0, index));
        }
    }

    private static final class ServiceInfo {
        final ServiceOccurrence occurrence;
        final List<Node> languageNodes = new ArrayList<Node>();
        final List<String> languages = new ArrayList<String>();
        final List<Triple> manual = new ArrayList<Triple>();
        String variant;

        /**
         * Creates classification state for one SERVICE occurrence.
         *
         * @param occurrence source SERVICE occurrence
         */
        ServiceInfo(ServiceOccurrence occurrence) {
            this.occurrence = occurrence;
        }
    }

    private static final class Output {
        final Node entity;
        final String output;
        final String type;
        final String base;
        final String variant;
        String value;
        String guard;
        List<Element> candidates;
        boolean prebound;
        String fixedCandidate;

        /**
         * Defines one requested label-service output.
         *
         * @param entity entity term to describe
         * @param output output-variable name
         * @param type label, description, or alias
         * @param base preferred generated-variable base
         * @param variant manual or automatic variant
         */
        Output(Node entity, String output, String type, String base, String variant) {
            this.entity = entity;
            this.output = output;
            this.type = type;
            this.base = base;
            this.variant = variant;
        }

        /**
         * Copies the immutable output definition without preparation state.
         *
         * @return unprepared output mapping
         */
        Output copy() {
            return new Output(entity, output, type, base, variant);
        }
    }

    private static final class Replacement {
        final ServiceOccurrence occurrence;
        final List<Element> elements;

        /**
         * Defines parsed elements that replace one SERVICE occurrence.
         *
         * @param occurrence SERVICE occurrence to replace
         * @param elements parsed replacement elements
         */
        Replacement(ServiceOccurrence occurrence, List<Element> elements) {
            this.occurrence = occurrence;
            this.elements = elements;
        }
    }

    private static final class ScopePlan {
        final Query query;
        final List<Replacement> replacements = new ArrayList<Replacement>();
        final List<Var> describeVariables = new ArrayList<Var>();

        /** Creates an atomic plan for one query scope. */
        ScopePlan(Query query) {
            this.query = query;
        }

        /** Applies replacements from right to left within each containing group. */
        void apply() {
            for (Var variable : describeVariables) {
                query.getProject().remove(variable);
            }
            Map<ElementGroup, List<Replacement>> byParent =
                    new IdentityHashMap<ElementGroup, List<Replacement>>();
            for (Replacement replacement : replacements) {
                byParent.computeIfAbsent(replacement.occurrence.parent,
                        ignored -> new ArrayList<Replacement>()).add(replacement);
            }
            for (List<Replacement> siblings : byParent.values()) {
                siblings.sort((left, right) -> Integer.compare(
                        right.occurrence.index, left.occurrence.index));
                for (Replacement replacement : siblings) {
                    List<Element> target = replacement.occurrence.parent.getElements();
                    target.remove(replacement.occurrence.index);
                    target.addAll(replacement.occurrence.index, replacement.elements);
                }
            }
        }
    }

    private static final class RuleFailure extends RuntimeException {
        private static final long serialVersionUID = 1L;
        final String code;
        final String status;
        final String message;
        final String retry;
        final String reference;
        final String variant;
        final boolean includeRewrite;
        final String path;

        /**
         * Carries a label-rule failure to the public result boundary.
         *
         * @param code stable error code
         * @param status rewrite status
         * @param message human-readable diagnostic
         * @param retry retry classification
         * @param reference diagnostic documentation link
         * @param variant selected variant, or {@code null}
         * @param includeRewrite whether the result includes a rewrite record
         * @param path structural source location
         */
        RuleFailure(String code, String status, String message, String retry,
                String reference, String variant, boolean includeRewrite, String path) {
            super(code);
            this.code = code;
            this.status = status;
            this.message = message;
            this.retry = retry;
            this.reference = reference;
            this.variant = variant;
            this.includeRewrite = includeRewrite;
            this.path = path;
        }
    }
}
