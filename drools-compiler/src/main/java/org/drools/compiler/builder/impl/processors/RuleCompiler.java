package org.drools.compiler.builder.impl.processors;

import org.drools.compiler.builder.DroolsAssemblerContext;
import org.drools.compiler.builder.impl.KnowledgeBuilderImpl;
import org.drools.compiler.compiler.DialectCompiletimeRegistry;
import org.drools.compiler.compiler.PackageRegistry;
import org.drools.compiler.compiler.RuleBuildError;
import org.drools.compiler.lang.descr.CompositePackageDescr;
import org.drools.compiler.rule.builder.RuleBuildContext;
import org.drools.compiler.rule.builder.RuleBuilder;
import org.drools.core.definitions.InternalKnowledgePackage;
import org.drools.core.definitions.rule.impl.RuleImpl;
import org.drools.core.util.StringUtils;
import org.drools.drl.ast.descr.AttributeDescr;
import org.drools.drl.ast.descr.PackageDescr;
import org.drools.drl.ast.descr.RuleDescr;
import org.drools.kiesession.rulebase.InternalKnowledgeBase;
import org.kie.api.io.Resource;
import org.kie.internal.builder.KnowledgeBuilderResult;
import org.kie.internal.builder.ResourceChange;

import java.security.AccessController;
import java.security.PrivilegedAction;
import java.util.ArrayList;
import java.util.Collection;
import java.util.HashMap;
import java.util.HashSet;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.ExecutionException;

import static org.drools.compiler.rule.builder.RuleBuildContext.descrToRule;
import static org.drools.core.util.StringUtils.isEmpty;

public class RuleCompiler extends AbstractPackageCompilationPhase {

    private InternalKnowledgeBase kBase;
    private int parallelRulesBuildThreshold;
    private final FilterCondition filterAccepts;
    private final FilterCondition filterAcceptsRemoval;

    //This list of package level attributes is initialised with the PackageDescr's attributes added to the assembler.
    //The package level attributes are inherited by individual rules not containing explicit overriding parameters.
    //The map contains a map of AttributeDescr's keyed on the AttributeDescr's name.
    private final Map<String, AttributeDescr> packageAttributes;
    private final Resource resource;
    private final DroolsAssemblerContext kBuilder;


    public RuleCompiler(PackageRegistry pkgRegistry, PackageDescr packageDescr, InternalKnowledgeBase kBase, int parallelRulesBuildThreshold, FilterCondition filterAccepts, FilterCondition filterAcceptsRemoval, Map<String, AttributeDescr> packageAttributes, Resource resource, DroolsAssemblerContext kBuilder) {
        super(pkgRegistry, packageDescr);
        this.kBase = kBase;
        this.parallelRulesBuildThreshold = parallelRulesBuildThreshold;
        this.filterAccepts = filterAccepts;
        this.filterAcceptsRemoval = filterAcceptsRemoval;
        this.packageAttributes = packageAttributes;
        this.resource = resource;
        this.kBuilder = kBuilder;
    }

    public void process() {
        preProcessRules(packageDescr, pkgRegistry);

        // ensure that rules are ordered by dependency, so that dependent rules are built later
        SortedRules sortedRules = sortRulesByDependency(packageDescr, pkgRegistry);

        if (!sortedRules.queries.isEmpty()) {
            compileAllQueries(packageDescr, pkgRegistry, sortedRules.queries);
        }
        for (List<RuleDescr> rulesLevel : sortedRules.rules) {
            compileRulesLevel(packageDescr, pkgRegistry, rulesLevel);
        }
    }


    private void preProcessRules(PackageDescr packageDescr, PackageRegistry pkgRegistry) {
        if (this.kBase == null) {
            return;
        }

        InternalKnowledgePackage pkg = pkgRegistry.getPackage();
        boolean needsRemoval = false;

        // first, check if any rules no longer exist
        for (org.kie.api.definition.rule.Rule rule : pkg.getRules()) {
            if (filterAcceptsRemoval.accepts(ResourceChange.Type.RULE, rule.getPackageName(), rule.getName())) {
                needsRemoval = true;
                break;
            }
        }

        if (!needsRemoval) {
            for (RuleDescr ruleDescr : packageDescr.getRules()) {
                if (filterAccepts.accepts(ResourceChange.Type.RULE, ruleDescr.getNamespace(), ruleDescr.getName())) {
                    if (pkg.getRule(ruleDescr.getName()) != null) {
                        needsRemoval = true;
                        break;
                    }
                }
            }
        }

        if (needsRemoval) {
            kBase.enqueueModification(() -> {
                Collection<RuleImpl> rulesToBeRemoved = new HashSet<>();

                for (org.kie.api.definition.rule.Rule rule : pkg.getRules()) {
                    if (filterAcceptsRemoval.accepts(ResourceChange.Type.RULE, rule.getPackageName(), rule.getName())) {
                        rulesToBeRemoved.add(((RuleImpl) rule));
                    }
                }

                rulesToBeRemoved.forEach(pkg::removeRule);

                for (RuleDescr ruleDescr : packageDescr.getRules()) {
                    if (filterAccepts.accepts(ResourceChange.Type.RULE, ruleDescr.getNamespace(), ruleDescr.getName())) {
                        RuleImpl rule = pkg.getRule(ruleDescr.getName());
                        if (rule != null) {
                            rulesToBeRemoved.add(rule);
                        }
                    }
                }

                if (!rulesToBeRemoved.isEmpty()) {
                    rulesToBeRemoved.addAll(findChildrenRulesToBeRemoved(packageDescr, rulesToBeRemoved));
                    kBase.removeRules(rulesToBeRemoved);
                }
            });
        }
    }



    private SortedRules sortRulesByDependency(PackageDescr packageDescr, PackageRegistry pkgRegistry) {
        // Using a topological sorting algorithm
        // see http://en.wikipedia.org/wiki/Topological_sorting

        InternalKnowledgePackage pkg = pkgRegistry.getPackage();

        List<RuleDescr> roots = new ArrayList<>();
        Map<String, List<RuleDescr>> children = new HashMap<>();
        LinkedHashMap<String, RuleDescr> sorted = new LinkedHashMap<>();
        List<RuleDescr> queries = new ArrayList<>();
        Set<String> compiledRules = new HashSet<>();

        for (RuleDescr ruleDescr : packageDescr.getRules()) {
            if (ruleDescr.isQuery()) {
                queries.add(ruleDescr);
            } else if (!ruleDescr.hasParent()) {
                roots.add(ruleDescr);
            } else {
                if (pkg.getRule(ruleDescr.getParentName()) != null) {
                    // The parent of this rule has been already compiled
                    compiledRules.add(ruleDescr.getParentName());
                }
                children.computeIfAbsent(ruleDescr.getParentName(), k -> new ArrayList<>()).add(ruleDescr);
            }
        }

        SortedRules sortedRules = new SortedRules();
        sortedRules.queries = queries;

        if (children.isEmpty()) { // Sorting not necessary
            if (!queries.isEmpty()) { // Build all queries first
                packageDescr.getRules().removeAll(queries);
                packageDescr.getRules().addAll(0, queries);
                sortedRules.rules.add(packageDescr.getRules().subList(queries.size(), packageDescr.getRules().size()));
            } else {
                sortedRules.rules.add(packageDescr.getRules());
            }
            return sortedRules;
        }

        for (String compiledRule : compiledRules) {
            List<RuleDescr> childz = children.remove(compiledRule);
            roots.addAll(childz);
        }

        List<RuleDescr> rulesLevel = roots;
        while (!rulesLevel.isEmpty()) {
            rulesLevel = sortRulesLevel(rulesLevel, sorted, sortedRules, children);
            sortedRules.newLevel();
        }

        reportHierarchyErrors(children, sorted);

        packageDescr.getRules().clear();
        packageDescr.getRules().addAll(queries);
        for (RuleDescr descr : sorted.values()) {
            packageDescr.getRules().add(descr);
        }
        return sortedRules;
    }

    private List<RuleDescr> sortRulesLevel(final List<RuleDescr> rulesLevel,
                                           final LinkedHashMap<String, RuleDescr> sorted, final SortedRules sortedRules,
                                           final Map<String, List<RuleDescr>> children) {
        final List<RuleDescr> nextLevel = new ArrayList<>();
        rulesLevel.forEach(ruleDescr -> {
            sortedRules.addRule(ruleDescr);
            sorted.put(ruleDescr.getName(), ruleDescr);
            final List<RuleDescr> childz = children.remove(ruleDescr.getName());
            if (childz != null) {
                nextLevel.addAll(childz);
            }
        });
        return nextLevel;
    }


    private void reportHierarchyErrors(Map<String, List<RuleDescr>> parents,
                                       Map<String, RuleDescr> sorted) {
        boolean circularDep = false;
        for (List<RuleDescr> rds : parents.values()) {
            for (RuleDescr ruleDescr : rds) {
                if (parents.get(ruleDescr.getParentName()) != null
                        && (sorted.containsKey(ruleDescr.getName()) || parents.containsKey(ruleDescr.getName()))) {
                    circularDep = true;
                    results.add(new RuleBuildError(descrToRule(ruleDescr), ruleDescr, null,
                            "Circular dependency in rules hierarchy"));
                    break;
                }
                manageUnresolvedExtension(ruleDescr, sorted.values());
            }
            if (circularDep) {
                break;
            }
        }
    }



    private void manageUnresolvedExtension(RuleDescr ruleDescr,
                                           Collection<RuleDescr> candidates) {
        List<String> candidateRules = new ArrayList<>();
        for (RuleDescr r : candidates) {
            if (StringUtils.stringSimilarity(ruleDescr.getParentName(), r.getName(), StringUtils.SIMILARITY_STRATS.DICE) >= 0.75) {
                candidateRules.add(r.getName());
            }
        }
        String msg = "Unresolved parent name " + ruleDescr.getParentName();
        if (!candidateRules.isEmpty()) {
            msg += " >> did you mean any of :" + candidateRules;
        }
        results.add(new RuleBuildError(descrToRule(ruleDescr), ruleDescr, msg,
                "Unable to resolve parent rule, please check that both rules are in the same package"));
    }

    private static class SortedRules {

        List<RuleDescr> queries;
        final List<List<RuleDescr>> rules = new ArrayList<>();
        List<RuleDescr> current = new ArrayList<>();

        SortedRules() {
            newLevel();
        }

        void addRule(RuleDescr rule) {
            current.add(rule);
        }

        void newLevel() {
            current = new ArrayList<>();
            rules.add(current);
        }
    }



    private void compileAllQueries(PackageDescr packageDescr, PackageRegistry pkgRegistry, List<RuleDescr> rules) {
        Map<String, RuleBuildContext> ruleCxts = buildRuleBuilderContexts(rules, pkgRegistry);
        for (RuleDescr ruleDescr : rules) {
            if (filterAccepts.accepts(ResourceChange.Type.RULE, ruleDescr.getNamespace(), ruleDescr.getName())) {
                initRuleDescr(packageDescr, pkgRegistry, ruleDescr);
                this.results.addAll(addRule(ruleCxts.get(ruleDescr.getName())));
            }
        }
    }


    private Map<String, RuleBuildContext> buildRuleBuilderContexts(List<RuleDescr> rules, PackageRegistry pkgRegistry) {
        Map<String, RuleBuildContext> map = new HashMap<>();
        for (RuleDescr ruleDescr : rules) {
            RuleBuildContext context = buildRuleBuilderContext(pkgRegistry, ruleDescr);
            map.put(ruleDescr.getName(), context);
            pkgRegistry.getPackage().addRule(context.getRule());
        }
        return map;
    }


    private RuleBuildContext buildRuleBuilderContext(PackageRegistry pkgRegistry, RuleDescr ruleDescr) {
        if (ruleDescr.getResource() == null) {
            ruleDescr.setResource(resource);
        }

        DialectCompiletimeRegistry ctr = pkgRegistry.getDialectCompiletimeRegistry();
        RuleBuildContext context = new RuleBuildContext(kBuilder,
                ruleDescr,
                ctr,
                pkgRegistry.getPackage(),
                ctr.getDialect(pkgRegistry.getDialect()));
        RuleBuilder.preProcess(context);
        return context;
    }

    private void compileRulesLevel(PackageDescr packageDescr, PackageRegistry pkgRegistry, List<RuleDescr> rules) {
        boolean parallelRulesBuild = this.kBase == null && parallelRulesBuildThreshold != -1 && rules.size() > parallelRulesBuildThreshold;
        if (parallelRulesBuild) {
            Map<String, RuleBuildContext> ruleCxts = new ConcurrentHashMap<>();
            try {
                KnowledgeBuilderImpl.ForkJoinPoolHolder.COMPILER_POOL.submit(() ->
                        rules.stream().parallel()
                                .filter(ruleDescr -> filterAccepts.accepts(ResourceChange.Type.RULE, ruleDescr.getNamespace(), ruleDescr.getName()))
                                .forEach(ruleDescr -> {
                                    initRuleDescr(packageDescr, pkgRegistry, ruleDescr);
                                    RuleBuildContext context = buildRuleBuilderContext(pkgRegistry, ruleDescr);
                                    ruleCxts.put(ruleDescr.getName(), context);
                                    List<? extends KnowledgeBuilderResult> results = addRule(context);
                                    if (!results.isEmpty()) {
                                        synchronized (this.results) {
                                            this.results.addAll(results);
                                        }
                                    }
                                })
                ).get();
            } catch (InterruptedException | ExecutionException e) {
                throw new RuntimeException("Rules compilation failed or interrupted", e);
            }
            for (RuleDescr ruleDescr : rules) {
                RuleBuildContext context = ruleCxts.get(ruleDescr.getName());
                if (context != null) {
                    pkgRegistry.getPackage().addRule(context.getRule());
                }
            }
        } else {
            for (RuleDescr ruleDescr : rules) {
                if (filterAccepts.accepts(ResourceChange.Type.RULE, ruleDescr.getNamespace(), ruleDescr.getName())) {
                    initRuleDescr(packageDescr, pkgRegistry, ruleDescr);
                    RuleBuildContext context = buildRuleBuilderContext(pkgRegistry, ruleDescr);
                    this.results.addAll(addRule(context));
                    pkgRegistry.getPackage().addRule(context.getRule());
                }
            }
        }
    }


    private Collection<RuleImpl> findChildrenRulesToBeRemoved(PackageDescr packageDescr, Collection<RuleImpl> rulesToBeRemoved) {
        Collection<String> childrenRuleNamesToBeRemoved = new HashSet<>();
        Collection<RuleImpl> childrenRulesToBeRemoved = new HashSet<>();
        for (RuleImpl rule : rulesToBeRemoved) {
            if (rule.hasChildren()) {
                for (RuleImpl child : rule.getChildren()) {
                    if (!rulesToBeRemoved.contains(child)) {
                        // if a rule has a child rule not marked to be removed ...
                        childrenRulesToBeRemoved.add(child);
                        childrenRuleNamesToBeRemoved.add(child.getName());
                        // ... remove the child rule but also add it back to the PackageDescr in order to readd it when also the parent rule will be readded ...
                        RuleDescr toBeReadded = new RuleDescr(child.getName());
                        toBeReadded.setNamespace(packageDescr.getNamespace());
                        packageDescr.addRule(toBeReadded);
                    }
                }
            }
        }
        // ... add a filter to the PackageDescr to also consider the readded children rules as updated together with the parent one
        if (!childrenRuleNamesToBeRemoved.isEmpty()) {
            ((CompositePackageDescr) packageDescr).addFilter((type, pkgName, assetName) -> childrenRuleNamesToBeRemoved.contains(assetName) ? KnowledgeBuilderImpl.AssetFilter.Action.UPDATE : KnowledgeBuilderImpl.AssetFilter.Action.DO_NOTHING);
        }
        return childrenRulesToBeRemoved;
    }


    private void initRuleDescr(PackageDescr packageDescr, PackageRegistry pkgRegistry, RuleDescr ruleDescr) {
        if (isEmpty(ruleDescr.getNamespace())) {
            // make sure namespace is set on components
            ruleDescr.setNamespace(packageDescr.getNamespace());
        }

        inheritPackageAttributes(packageAttributes, ruleDescr);

        if (isEmpty(ruleDescr.getDialect())) {
            ruleDescr.addAttribute(new AttributeDescr("dialect", pkgRegistry.getDialect()));
        }
    }


    //Entity rules inherit package attributes
    private void inheritPackageAttributes(Map<String, AttributeDescr> pkgAttributes,
                                          RuleDescr ruleDescr) {
        if (pkgAttributes == null) {
            return;
        }
        for (AttributeDescr attrDescr : pkgAttributes.values()) {
            ruleDescr.getAttributes().putIfAbsent(attrDescr.getName(), attrDescr);
        }
    }
    private List<? extends KnowledgeBuilderResult> addRule(RuleBuildContext context) {
        return System.getSecurityManager() == null ?
                internalAddRule(context) :
                AccessController.<List<? extends KnowledgeBuilderResult>>doPrivileged((PrivilegedAction) () -> internalAddRule(context));
    }


    private List<? extends KnowledgeBuilderResult> internalAddRule(RuleBuildContext context) {
        RuleBuilder.build(context);

        context.getRule().setResource(context.getRuleDescr().getResource());

        context.getDialect().addRule(context);

        if (context.needsStreamMode()) {
            context.getPkg().setNeedStreamMode();
        }

        if (context.getErrors().isEmpty()) {
            return context.getWarnings();
        } else if (context.getWarnings().isEmpty()) {
            return context.getErrors();
        }

        List<KnowledgeBuilderResult> result = new ArrayList<>();
        result.addAll(context.getErrors());
        result.addAll(context.getWarnings());
        return result;
    }
}
