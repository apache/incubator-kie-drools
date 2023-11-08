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

import org.drools.base.rule.Function;
import org.drools.compiler.compiler.DuplicateFunction;
import org.drools.compiler.compiler.PackageRegistry;
import org.drools.drl.ast.descr.FunctionDescr;
import org.drools.drl.ast.descr.FunctionImportDescr;
import org.drools.drl.ast.descr.PackageDescr;
import org.kie.internal.builder.KnowledgeBuilderConfiguration;

public class FunctionCompilationPhase extends AbstractPackageCompilationPhase {

    private final KnowledgeBuilderConfiguration configuration;

    public FunctionCompilationPhase(PackageRegistry pkgRegistry,
                                    PackageDescr packageDescr,
                                    KnowledgeBuilderConfiguration configuration) {
        super(pkgRegistry, packageDescr);
        this.configuration = configuration;
    }

    public void process() {
        for (FunctionDescr function : packageDescr.getFunctions()) {
            Function existingFunc = pkgRegistry.getPackage().getFunctions().get(function.getName());
            if (existingFunc != null && function.getNamespace().equals(existingFunc.getNamespace())) {
                this.results.add(
                        new DuplicateFunction(function,
                                this.configuration));
            }
        }

        for (final FunctionImportDescr functionImport : packageDescr.getFunctionImports()) {
            String importEntry = functionImport.getTarget();
            pkgRegistry.addStaticImport(functionImport);
            pkgRegistry.getPackage().addStaticImport(importEntry);
        }
    }
}
