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
package org.drools.model.codegen.execmodel.processors;

import java.util.ArrayList;
import java.util.Collection;
import java.util.List;
import java.util.function.Function;

import org.drools.compiler.builder.PackageRegistryManager;
import org.drools.compiler.builder.impl.BuildResultCollector;
import org.drools.compiler.builder.impl.BuildResultCollectorImpl;
import org.drools.compiler.builder.impl.GlobalVariableContext;
import org.drools.compiler.builder.impl.TypeDeclarationContext;
import org.drools.compiler.builder.impl.processors.AccumulateFunctionCompilationPhase;
import org.drools.compiler.builder.impl.processors.CompilationPhase;
import org.drools.compiler.builder.impl.processors.FunctionCompilationPhase;
import org.drools.compiler.builder.impl.processors.GlobalCompilationPhase;
import org.drools.compiler.builder.impl.processors.IteratingPhase;
import org.drools.compiler.builder.impl.processors.RuleValidator;
import org.drools.compiler.builder.impl.processors.SinglePackagePhaseFactory;
import org.drools.compiler.builder.impl.processors.WindowDeclarationCompilationPhase;
import org.drools.compiler.lang.descr.CompositePackageDescr;
import org.drools.kiesession.rulebase.InternalKnowledgeBase;
import org.drools.model.codegen.execmodel.PackageModel;
import org.drools.model.codegen.execmodel.PackageModelManager;
import org.drools.model.codegen.execmodel.PackageSourceManager;
import org.kie.internal.builder.KnowledgeBuilderConfiguration;
import org.kie.internal.builder.KnowledgeBuilderResult;

public class ModelMainCompilationPhase<T> implements CompilationPhase {

    private final PackageModelManager packageModels;
    private final PackageRegistryManager pkgRegistryManager;
    private final Collection<CompositePackageDescr> packages;

    private final KnowledgeBuilderConfiguration configuration;
    private final boolean hasMvel;
    private final InternalKnowledgeBase kBase;
    private final TypeDeclarationContext typeDeclarationContext;
    private final GlobalVariableContext globalVariableContext;

    private final BuildResultCollector results = new BuildResultCollectorImpl();
    private final Function<PackageModel, T> sourceGenerator;
    private final PackageSourceManager<T> packageSourceManager;
    private final boolean oneClassPerRule;

    public ModelMainCompilationPhase(
            PackageModelManager packageModels,
            PackageRegistryManager pkgRegistryManager,
            Collection<CompositePackageDescr> packages,
            KnowledgeBuilderConfiguration configuration,
            boolean hasMvel,
            InternalKnowledgeBase kBase,
            TypeDeclarationContext typeDeclarationContext,
            GlobalVariableContext globalVariableContext, Function<PackageModel, T> sourceGenerator, PackageSourceManager<T> packageSourceManager, boolean oneClassPerRule) {
        this.packageModels = packageModels;
        this.pkgRegistryManager = pkgRegistryManager;
        this.packages = packages;
        this.configuration = configuration;
        this.hasMvel = hasMvel;
        this.kBase = kBase;
        this.typeDeclarationContext = typeDeclarationContext;
        this.globalVariableContext = globalVariableContext;
        this.sourceGenerator = sourceGenerator;
        this.packageSourceManager = packageSourceManager;
        this.oneClassPerRule = oneClassPerRule;
    }

    @Override
    public void process() {
        List<CompilationPhase> phases = new ArrayList<>();
        phases.add(iteratingPhase(AccumulateFunctionCompilationPhase::new));
        if (hasMvel) {
            phases.add(iteratingPhase((reg, acc) -> new WindowDeclarationCompilationPhase(reg, acc, typeDeclarationContext)));
        }
        phases.add(iteratingPhase((reg, acc) -> new FunctionCompilationPhase(reg, acc, configuration)));
        phases.add(iteratingPhase((reg, acc) -> GlobalCompilationPhase.of(reg, acc, kBase, globalVariableContext, acc.getFilter())));
        phases.add(new DeclaredTypeDeregistrationPhase(packages, pkgRegistryManager));

        phases.add(iteratingPhase((reg, acc) -> new RuleValidator(reg, acc, configuration))); // validateUniqueRuleNames
        phases.add(iteratingPhase((reg, acc) -> new ModelGeneratorPhase(reg, acc, packageModels.getPackageModel(acc, reg, acc.getName()), typeDeclarationContext))); // validateUniqueRuleNames
        phases.add(iteratingPhase((reg, acc) -> new SourceCodeGenerationPhase<>(
                packageModels.getPackageModel(acc, reg, acc.getName()), packageSourceManager, sourceGenerator, oneClassPerRule))); // validateUniqueRuleNames


        for (CompilationPhase phase : phases) {
            phase.process();
            this.results.addAll(phase.getResults());
            if (this.results.hasErrors()) {
                return;
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
