/**
 * Licensed to the Apache Software Foundation (ASF) under one
 * or more contributor license agreements.  See the NOTICE file
 * distributed with this work for additional information
 * regarding copyright ownership.  The ASF licenses this file
 * to you under the Apache License, Version 2.0 (the
 * "License"); you may not use this file except in compliance
 * with the License.  You may obtain a copy of the License at
 *
 *   http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing,
 * software distributed under the License is distributed on an
 * "AS IS" BASIS, WITHOUT WARRANTIES OR CONDITIONS OF ANY
 * KIND, either express or implied.  See the License for the
 * specific language governing permissions and limitations
 * under the License.
 */
package org.drools.compiler.builder.impl.processors;

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

import org.drools.base.definitions.InternalKnowledgePackage;
import org.drools.compiler.builder.impl.KnowledgeBuilderImpl;
import org.drools.compiler.builder.impl.TypeDeclarationContext;
import org.drools.compiler.compiler.DialectCompiletimeRegistry;
import org.drools.compiler.compiler.PackageRegistry;
import org.drools.compiler.compiler.RuleBuildError;
import org.drools.compiler.rule.builder.RuleBuildContext;
import org.drools.compiler.rule.builder.RuleBuilder;
import org.drools.drl.ast.descr.AttributeDescr;
import org.drools.drl.ast.descr.PackageDescr;
import org.drools.drl.ast.descr.RuleDescr;
import org.drools.util.StringUtils;
import org.kie.api.io.Resource;
import org.kie.internal.builder.KnowledgeBuilderResult;
import org.kie.internal.builder.ResourceChange;

import static org.drools.compiler.rule.builder.RuleBuildContext.descrToRule;
import static org.drools.util.StringUtils.isEmpty;

public class ImmutableRuleCompilationPhase extends AbstractPackageCompilationPhase {

    private int parallelRulesBuildThreshold;

    //This list of package level attributes is initialised with the PackageDescr's attributes added to the assembler.
    //The package level attributes are inherited by individual rules not containing explicit overriding parameters.
    //The map contains a map of AttributeDescr's keyed on the AttributeDescr's name.
    private final Map<String, AttributeDescr> packageAttributes;
    private final Resource resource;
    private final TypeDeclarationContext typeDeclarationContext;


    public ImmutableRuleCompilationPhase(
            PackageRegistry pkgRegistry,
            PackageDescr packageDescr,
            int parallelRulesBuildThreshold,
            Map<String, AttributeDescr> packageAttributes,
            Resource resource,
            TypeDeclarationContext typeDeclarationContext) {
        super(pkgRegistry, packageDescr);
        this.parallelRulesBuildThreshold = parallelRulesBuildThreshold;
        this.packageAttributes = packageAttributes;
        this.resource = resource;
        this.typeDeclarationContext = typeDeclarationContext;
    }

    public void process() {
        // ensure that rules are ordered by dependency, so that dependent rules are built later
        SortedRules sortedRules = sortRulesByDependency(packageDescr, pkgRegistry);

        if (!sortedRules.queries.isEmpty()) {
            compileAllQueries(packageDescr, pkgRegistry, sortedRules.queries);
        }
        for (List<RuleDescr> rulesLevel : sortedRules.rules) {
            compileRulesLevel(packageDescr, pkgRegistry, rulesLevel);
        }
    }

    protected boolean filterAccepts(ResourceChange.Type type, String namespace, String name) {
        return true;
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
            if (filterAccepts(ResourceChange.Type.RULE, ruleDescr.getNamespace(), ruleDescr.getName())) {
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
        RuleBuildContext context = new RuleBuildContext(typeDeclarationContext,
                ruleDescr,
                ctr,
                pkgRegistry.getPackage(),
                ctr.getDialect(pkgRegistry.getDialect()));
        RuleBuilder.preProcess(context);
        return context;
    }

    private void compileRulesLevel(PackageDescr packageDescr, PackageRegistry pkgRegistry, List<RuleDescr> rules) {
        boolean parallelRulesBuild = parallelRulesBuild(rules);
        if (parallelRulesBuild) {
            Map<String, RuleBuildContext> ruleCxts = new ConcurrentHashMap<>();
            try {
                KnowledgeBuilderImpl.ForkJoinPoolHolder.COMPILER_POOL.submit(() ->
                        rules.stream().parallel()
                                .filter(ruleDescr -> filterAccepts(ResourceChange.Type.RULE, ruleDescr.getNamespace(), ruleDescr.getName()))
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
                if (filterAccepts(ResourceChange.Type.RULE, ruleDescr.getNamespace(), ruleDescr.getName())) {
                    initRuleDescr(packageDescr, pkgRegistry, ruleDescr);
                    RuleBuildContext context = buildRuleBuilderContext(pkgRegistry, ruleDescr);
                    this.results.addAll(addRule(context));
                    pkgRegistry.getPackage().addRule(context.getRule());
                }
            }
        }
    }

    protected boolean parallelRulesBuild(List<RuleDescr> rules) {
        return parallelRulesBuildThreshold != -1 && rules.size() > parallelRulesBuildThreshold;
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
        return internalAddRule(context);
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
