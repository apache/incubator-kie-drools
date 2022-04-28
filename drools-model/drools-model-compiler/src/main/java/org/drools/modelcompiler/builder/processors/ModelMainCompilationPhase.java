/*
 * Copyright 2022 Red Hat, Inc. and/or its affiliates.
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *      http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 *
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package org.drools.modelcompiler.builder.processors;

import org.drools.compiler.builder.DroolsAssemblerContext;
import org.drools.compiler.builder.PackageRegistryManager;
import org.drools.compiler.builder.impl.BuildResultCollector;
import org.drools.compiler.builder.impl.BuildResultCollectorImpl;
import org.drools.compiler.builder.impl.GlobalVariableContext;
import org.drools.compiler.builder.impl.processors.AccumulateFunctionCompilationPhase;
import org.drools.compiler.builder.impl.processors.CompilationPhase;
import org.drools.compiler.builder.impl.processors.FunctionCompilationPhase;
import org.drools.compiler.builder.impl.processors.GlobalCompilationPhase;
import org.drools.compiler.builder.impl.processors.SinglePackagePhaseFactory;
import org.drools.compiler.builder.impl.processors.IteratingPhase;
import org.drools.compiler.builder.impl.processors.WindowDeclarationCompilationPhase;
import org.drools.compiler.lang.descr.CompositePackageDescr;
import org.drools.kiesession.rulebase.InternalKnowledgeBase;
import org.kie.internal.builder.KnowledgeBuilderConfiguration;
import org.kie.internal.builder.KnowledgeBuilderResult;

import java.util.ArrayList;
import java.util.Collection;
import java.util.List;

public class ModelMainCompilationPhase implements CompilationPhase {

    private final PackageRegistryManager pkgRegistryManager;
    private final Collection<CompositePackageDescr> packages;

    private final KnowledgeBuilderConfiguration configuration;
    private final boolean hasMvel;
    private final InternalKnowledgeBase kBase;
    private final DroolsAssemblerContext assemblerContext;
    private final GlobalVariableContext globalVariableContext;

    private final BuildResultCollector results = new BuildResultCollectorImpl();

    public ModelMainCompilationPhase(
            PackageRegistryManager pkgRegistryManager,
            Collection<CompositePackageDescr> packages,
            KnowledgeBuilderConfiguration configuration,
            boolean hasMvel,
            InternalKnowledgeBase kBase,
            DroolsAssemblerContext assemblerContext,
            GlobalVariableContext globalVariableContext) {
        this.pkgRegistryManager = pkgRegistryManager;
        this.packages = packages;
        this.configuration = configuration;
        this.hasMvel = hasMvel;
        this.kBase = kBase;
        this.assemblerContext = assemblerContext;
        this.globalVariableContext = globalVariableContext;
    }

    @Override
    public void process() {
        List<CompilationPhase> phases = new ArrayList<>();
        phases.add(iteratingPhase(AccumulateFunctionCompilationPhase::new));
        if (hasMvel) {
            phases.add(iteratingPhase((reg, acc) -> new WindowDeclarationCompilationPhase(reg, acc, assemblerContext)));
        }
        phases.add(iteratingPhase((reg, acc) -> new FunctionCompilationPhase(reg, acc, configuration)));
        phases.add(iteratingPhase((reg, acc) -> new GlobalCompilationPhase(reg, acc, kBase, globalVariableContext, acc.getFilter())));

        for (CompilationPhase phase : phases) {
            phase.process();
            this.results.addAll(phase.getResults());
            if (this.results.hasErrors()) {
                break;
            }
        }
    }


    @Override
    public Collection<? extends KnowledgeBuilderResult> getResults() {
        return this.results.getAllResults();
    }

    private IteratingPhase iteratingPhase(SinglePackagePhaseFactory phaseFactory) {
        return new IteratingPhase(packages, pkgRegistryManager, phaseFactory);
    }
}
