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

import java.util.Collection;
import java.util.HashSet;
import java.util.List;
import java.util.Map;

import org.drools.base.definitions.InternalKnowledgePackage;
import org.drools.base.definitions.rule.impl.RuleImpl;
import org.drools.compiler.builder.impl.AssetFilter;
import org.drools.compiler.builder.impl.TypeDeclarationContext;
import org.drools.compiler.compiler.PackageRegistry;
import org.drools.compiler.lang.descr.CompositePackageDescr;
import org.drools.drl.ast.descr.AttributeDescr;
import org.drools.drl.ast.descr.PackageDescr;
import org.drools.drl.ast.descr.RuleDescr;
import org.drools.kiesession.rulebase.InternalKnowledgeBase;
import org.kie.api.io.Resource;
import org.kie.internal.builder.ResourceChange;

public class RuleCompilationPhase extends ImmutableRuleCompilationPhase {

    public static CompilationPhase of(
            PackageRegistry pkgRegistry,
                                 PackageDescr packageDescr,
                                 InternalKnowledgeBase kBase,
                                 int parallelRulesBuildThreshold,
                                 AssetFilter assetFilter,
                                 Map<String, AttributeDescr> packageAttributes,
                                 Resource resource,
                                 TypeDeclarationContext typeDeclarationContext) {

        if (kBase == null) {
            return new ImmutableRuleCompilationPhase(
                    pkgRegistry,
                    packageDescr,
                    parallelRulesBuildThreshold,
                    packageAttributes,
                    resource,
                    typeDeclarationContext);
        } else {
            return new RuleCompilationPhase(
                    pkgRegistry,
                    packageDescr,
                    kBase,
                    parallelRulesBuildThreshold,
                    assetFilter,
                    packageAttributes,
                    resource,
                    typeDeclarationContext);
        }
    }

    private InternalKnowledgeBase kBase;
    private final AssetFilter assetFilter;

    private RuleCompilationPhase(
            PackageRegistry pkgRegistry,
            PackageDescr packageDescr,
            InternalKnowledgeBase kBase,
            int parallelRulesBuildThreshold,
            AssetFilter assetFilter,
            Map<String, AttributeDescr> packageAttributes,
            Resource resource,
            TypeDeclarationContext typeDeclarationContext) {
        super(pkgRegistry, packageDescr, parallelRulesBuildThreshold, packageAttributes, resource, typeDeclarationContext);
        this.kBase = kBase;
        this.assetFilter = assetFilter;
    }

    @Override
    public void process() {
        preProcessRules(packageDescr, pkgRegistry);
        super.process();
    }

    protected boolean parallelRulesBuild(List<RuleDescr> rules) {
        return false;
    }

    private void preProcessRules(PackageDescr packageDescr, PackageRegistry pkgRegistry) {
        InternalKnowledgePackage pkg = pkgRegistry.getPackage();
        boolean needsRemoval = false;

        // first, check if any rules no longer exist
        for (org.kie.api.definition.rule.Rule rule : pkg.getRules()) {
            if (filterAcceptsRemoval(ResourceChange.Type.RULE, rule.getPackageName(), rule.getName())) {
                needsRemoval = true;
                break;
            }
        }

        if (!needsRemoval) {
            for (RuleDescr ruleDescr : packageDescr.getRules()) {
                if (filterAccepts(ResourceChange.Type.RULE, ruleDescr.getNamespace(), ruleDescr.getName())) {
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
                    if (filterAcceptsRemoval(ResourceChange.Type.RULE, rule.getPackageName(), rule.getName())) {
                        rulesToBeRemoved.add(((RuleImpl) rule));
                    }
                }

                rulesToBeRemoved.forEach(pkg::removeRule);

                for (RuleDescr ruleDescr : packageDescr.getRules()) {
                    if (filterAccepts(ResourceChange.Type.RULE, ruleDescr.getNamespace(), ruleDescr.getName())) {
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

    @Override
    protected boolean filterAccepts(ResourceChange.Type type, String namespace, String name) {
        return assetFilter == null || !AssetFilter.Action.DO_NOTHING.equals(assetFilter.accept(type, namespace, name));
    }

    private boolean filterAcceptsRemoval(ResourceChange.Type type, String namespace, String name) {
        return assetFilter != null && AssetFilter.Action.REMOVE.equals(assetFilter.accept(type, namespace, name));
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
            ((CompositePackageDescr) packageDescr).addFilter((type, pkgName, assetName) -> childrenRuleNamesToBeRemoved.contains(assetName) ? AssetFilter.Action.UPDATE : AssetFilter.Action.DO_NOTHING);
        }
        return childrenRulesToBeRemoved;
    }

}
