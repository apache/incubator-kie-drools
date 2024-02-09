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

import java.util.List;

import org.drools.compiler.builder.impl.AssetFilter;
import org.drools.compiler.builder.impl.GlobalVariableContext;
import org.drools.compiler.builder.impl.TypeDeclarationContext;
import org.drools.compiler.compiler.PackageRegistry;
import org.drools.drl.ast.descr.PackageDescr;
import org.drools.kiesession.rulebase.InternalKnowledgeBase;
import org.kie.internal.builder.KnowledgeBuilderConfiguration;

import static java.util.Arrays.asList;

public class OtherDeclarationCompilationPhase extends AbstractPackageCompilationPhase {

    private final GlobalVariableContext globalVariableContext;
    private final TypeDeclarationContext typeDeclarationContext;
    private final InternalKnowledgeBase kBase;
    private final KnowledgeBuilderConfiguration configuration;
    private final AssetFilter assetFilter;

    public OtherDeclarationCompilationPhase(
            PackageRegistry pkgRegistry,
            PackageDescr packageDescr,
            GlobalVariableContext globalVariableContext,
            TypeDeclarationContext typeDeclarationContext,
            InternalKnowledgeBase kBase,
            KnowledgeBuilderConfiguration configuration,
            AssetFilter assetFilter) {
        super(pkgRegistry, packageDescr);
        this.globalVariableContext = globalVariableContext;
        this.typeDeclarationContext = typeDeclarationContext;
        this.kBase = kBase;
        this.configuration = configuration;
        this.assetFilter = assetFilter;
    }

    @Override
    public void process() {
        List<CompilationPhase> phases = asList(
                new AccumulateFunctionCompilationPhase(pkgRegistry, packageDescr),
                new WindowDeclarationCompilationPhase(pkgRegistry, packageDescr, typeDeclarationContext),
                new FunctionCompilationPhase(pkgRegistry, packageDescr, configuration),
                GlobalCompilationPhase.of(pkgRegistry, packageDescr, kBase, globalVariableContext, assetFilter));

        phases.forEach(CompilationPhase::process);
        phases.forEach(p -> results.addAll(p.getResults()));
    }

}
