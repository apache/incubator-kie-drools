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

import java.util.HashSet;
import java.util.Set;

import org.drools.base.definitions.InternalKnowledgePackage;
import org.drools.base.definitions.rule.impl.RuleImpl;
import org.drools.compiler.compiler.DuplicateRule;
import org.drools.compiler.compiler.PackageRegistry;
import org.drools.drl.ast.descr.PackageDescr;
import org.drools.drl.ast.descr.RuleDescr;
import org.drools.drl.parser.ParserError;
import org.kie.api.io.Resource;
import org.kie.internal.builder.KnowledgeBuilderConfiguration;

public class RuleValidator extends AbstractPackageCompilationPhase {

    private final KnowledgeBuilderConfiguration configuration;

    public RuleValidator(PackageRegistry pkgRegistry, PackageDescr packageDescr, KnowledgeBuilderConfiguration configuration) {
        super(pkgRegistry, packageDescr);
        this.configuration = configuration;
    }

    public void process() {
        final Set<String> names = new HashSet<>();
        InternalKnowledgePackage pkg = null;
        if (pkgRegistry != null) {
            pkg = pkgRegistry.getPackage();
        }
        for (final RuleDescr rule : packageDescr.getRules()) {
            validateRule(packageDescr, rule);

            final String name = rule.getUnitQualifiedName();
            if (names.contains(name)) {
                this.results.add(new ParserError(rule.getResource(),
                        "Duplicate rule name: " + name,
                        rule.getLine(),
                        rule.getColumn(),
                        packageDescr.getNamespace()));
            }
            if (pkg != null) {
                RuleImpl duplicatedRule = pkg.getRule(name);
                if (duplicatedRule != null) {
                    Resource resource = rule.getResource();
                    Resource duplicatedResource = duplicatedRule.getResource();
                    if (resource == null || duplicatedResource == null || duplicatedResource.getSourcePath() == null ||
                            duplicatedResource.getSourcePath().equals(resource.getSourcePath())) {
                        this.results.add(new DuplicateRule(rule,
                                packageDescr,
                                this.configuration));
                    } else {
                        this.results.add(new ParserError(rule.getResource(),
                                "Duplicate rule name: " + name,
                                rule.getLine(),
                                rule.getColumn(),
                                packageDescr.getNamespace()));
                    }
                }
            }
            names.add(name);

            if (rule.getUnit() != null &&
                    (rule.getAttributes().get("agenda-group") != null || rule.getAttributes().get("ruleflow-group") != null)) {
                this.results.add(new ParserError(rule.getResource(),
                        "Rule " + rule.getName() + " belongs to unit " + rule.getUnit().getTarget() + " and cannot have an agenda-group or a ruleflow-group",
                        rule.getLine(),
                        rule.getColumn(),
                        packageDescr.getNamespace()));
            }
        }
    }

    private void validateRule(PackageDescr packageDescr,
                              RuleDescr rule) {
        if (rule.hasErrors()) {
            for (String error : rule.getErrors()) {
                this.results.add(new ParserError(rule.getResource(),
                        error + " in rule " + rule.getName(),
                        rule.getLine(),
                        rule.getColumn(),
                        packageDescr.getNamespace()));
            }
        }
    }

}
