package org.drools.modelcompiler.builder.processors;

import org.drools.compiler.builder.PackageRegistryManager;
import org.drools.compiler.builder.impl.BuildResultCollector;
import org.drools.compiler.builder.impl.BuildResultCollectorImpl;
import org.drools.compiler.builder.impl.KnowledgeBuilderConfigurationImpl;
import org.drools.compiler.builder.impl.KnowledgeBuilderImpl;
import org.drools.compiler.builder.impl.processors.CompilationPhase;
import org.drools.compiler.builder.impl.processors.IteratingPhase;
import org.drools.compiler.builder.impl.processors.RuleValidator;
import org.drools.compiler.builder.impl.processors.SinglePackagePhaseFactory;
import org.drools.compiler.lang.descr.CompositePackageDescr;
import org.drools.modelcompiler.builder.CanonicalModelBuildContext;
import org.drools.modelcompiler.builder.PackageModelManager;
import org.kie.internal.builder.KnowledgeBuilderResult;

import java.util.Collection;
import java.util.List;

import static java.util.Arrays.asList;

public class ModelRuleCompilationPhase implements CompilationPhase {

    private final PackageModelManager packageModelManager;
    private final PackageRegistryManager pkgRegistryManager;
    private final KnowledgeBuilderConfigurationImpl configuration;
    private final Collection<CompositePackageDescr> packages;
    private final BuildResultCollector results;
    private KnowledgeBuilderImpl assemblerContext;

    public ModelRuleCompilationPhase(
            PackageModelManager packageModelManager,
            PackageRegistryManager pkgRegistryManager,
            KnowledgeBuilderConfigurationImpl configuration,
            Collection<CompositePackageDescr> packages) {
        this.packageModelManager = packageModelManager;
        this.pkgRegistryManager = pkgRegistryManager;
        this.configuration = configuration;
        this.packages = packages;
        this.results = new BuildResultCollectorImpl();
    }

    @Override
    public void process() {
        List<CompilationPhase> phases = asList(new DeclaredTypeDeregistrationPhase(packages, pkgRegistryManager),
                iteratingPhase((reg, acc) -> new RuleValidator(reg, acc, configuration)), // validateUniqueRuleNames
                iteratingPhase((reg, acc) -> new ModelGeneratorPhase(reg, acc, packageModelManager.getPackageModel(acc, reg, acc.getName()), assemblerContext)));


        for (CompilationPhase phase : phases) {
            phase.process();
            this.results.addAll(phase.getResults());
            if (this.results.hasErrors()) {
                break;
            }
        }
    }

    private IteratingPhase iteratingPhase(SinglePackagePhaseFactory phaseFactory) {
        return new IteratingPhase(packages, pkgRegistryManager, phaseFactory);
    }


    @Override
    public Collection<? extends KnowledgeBuilderResult> getResults() {
        return results.getAllResults();
    }
}
