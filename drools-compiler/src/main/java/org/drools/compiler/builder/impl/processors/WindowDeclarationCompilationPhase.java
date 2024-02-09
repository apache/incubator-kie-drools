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

import org.drools.base.definitions.InternalKnowledgePackage;
import org.drools.base.rule.Pattern;
import org.drools.base.rule.WindowDeclaration;
import org.drools.compiler.builder.impl.TypeDeclarationContext;
import org.drools.compiler.compiler.DescrBuildError;
import org.drools.compiler.compiler.DialectCompiletimeRegistry;
import org.drools.compiler.compiler.PackageRegistry;
import org.drools.compiler.rule.builder.RuleBuildContext;
import org.drools.compiler.rule.builder.RuleConditionBuilder;
import org.drools.drl.ast.descr.AttributeDescr;
import org.drools.drl.ast.descr.PackageDescr;
import org.drools.drl.ast.descr.RuleDescr;
import org.drools.drl.ast.descr.WindowDeclarationDescr;
import org.drools.drl.parser.DroolsError;

public class WindowDeclarationCompilationPhase extends AbstractPackageCompilationPhase {

    private final TypeDeclarationContext kBuilder;

    public WindowDeclarationCompilationPhase(PackageRegistry pkgRegistry, PackageDescr packageDescr, TypeDeclarationContext kBuilder) {
        super(pkgRegistry, packageDescr);
        this.kBuilder = kBuilder;
    }

    public void process() {
        for (WindowDeclarationDescr wd : packageDescr.getWindowDeclarations()) {
            WindowDeclaration window = new WindowDeclaration(wd.getName(), packageDescr.getName());
            // TODO: process annotations

            // process pattern
            InternalKnowledgePackage pkg = pkgRegistry.getPackage();
            DialectCompiletimeRegistry ctr = pkgRegistry.getDialectCompiletimeRegistry();
            RuleDescr dummy = new RuleDescr(wd.getName() + " Window Declaration");
            dummy.setResource(packageDescr.getResource());
            dummy.addAttribute(new AttributeDescr("dialect", "java"));
            RuleBuildContext context = new RuleBuildContext(kBuilder,
                    dummy,
                    ctr,
                    pkg,
                    ctr.getDialect(pkgRegistry.getDialect()));
            final RuleConditionBuilder builder = (RuleConditionBuilder) context.getDialect().getBuilder(wd.getPattern().getClass());
            if (builder != null) {
                final Pattern pattern = (Pattern) builder.build(context,
                        wd.getPattern(),
                        null);

                if (pattern.getXpathConstraint() != null) {
                    context.addError(new DescrBuildError(wd,
                            context.getParentDescr(),
                            null,
                            "OOpath expression " + pattern.getXpathConstraint() + " not allowed in window declaration\n"));
                }

                window.setPattern(pattern);
            } else {
                throw new RuntimeException(
                        "BUG: assembler not found for descriptor class " + wd.getPattern().getClass());
            }

            if (!context.getErrors().isEmpty()) {
                for (DroolsError error : context.getErrors()) {
                    this.results.add(error);
                }
            } else {
                pkgRegistry.getPackage().addWindowDeclaration(window);
            }
        }
    }
}
