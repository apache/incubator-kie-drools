package org.drools.modelcompiler.builder.processors;

import org.drools.compiler.builder.DroolsAssemblerContext;
import org.drools.compiler.builder.PackageRegistryManager;
import org.drools.compiler.builder.impl.BuildResultAccumulator;
import org.drools.compiler.builder.impl.BuildResultAccumulatorImpl;
import org.drools.compiler.builder.impl.GlobalVariableContext;
import org.drools.compiler.builder.impl.processors.AccumulateFunctionCompilationPhase;
import org.drools.compiler.builder.impl.processors.CompilationPhase;
import org.drools.compiler.builder.impl.processors.FunctionCompilationPhase;
import org.drools.compiler.builder.impl.processors.GlobalCompilationPhase;
import org.drools.compiler.builder.impl.processors.IterablePhaseFactory;
import org.drools.compiler.builder.impl.processors.IteratingPhase;
import org.drools.compiler.builder.impl.processors.WindowDeclarationCompilationPhase;
import org.drools.compiler.compiler.PackageRegistry;
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

    private final BuildResultAccumulator results = new BuildResultAccumulatorImpl();

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

    private IteratingPhase iteratingPhase(IterablePhaseFactory phaseFactory) {
        return new IteratingPhase(packages, pkgRegistryManager, phaseFactory);
    }
}
