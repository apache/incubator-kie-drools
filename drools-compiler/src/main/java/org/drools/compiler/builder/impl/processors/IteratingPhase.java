package org.drools.compiler.builder.impl.processors;

import org.drools.compiler.builder.PackageRegistryManager;
import org.drools.compiler.builder.impl.BuildResultCollectorImpl;
import org.drools.compiler.compiler.PackageRegistry;
import org.drools.compiler.lang.descr.CompositePackageDescr;
import org.kie.internal.builder.KnowledgeBuilderResult;

import java.util.Collection;

/**
 * Decorates a {@link CompilationPhase} via its {@link SinglePackagePhaseFactory}.
 * Iterates over a collection of {@link CompositePackageDescr}
 * and applies the decorated phase to it.
 *
 */
public class IteratingPhase implements CompilationPhase {
    private final Collection<CompositePackageDescr> packages;
    private final PackageRegistryManager pkgRegistryManager;
    private final SinglePackagePhaseFactory phaseFactory;

    private final BuildResultCollectorImpl results = new BuildResultCollectorImpl();

    public IteratingPhase(Collection<CompositePackageDescr> packages, PackageRegistryManager pkgRegistryManager, SinglePackagePhaseFactory phaseFactory) {
        this.packages = packages;
        this.pkgRegistryManager = pkgRegistryManager;
        this.phaseFactory = phaseFactory;
    }

    @Override
    public void process() {
        for (CompositePackageDescr compositePackageDescr : packages) {
            PackageRegistry packageRegistry = pkgRegistryManager.getOrCreatePackageRegistry(compositePackageDescr);
            CompilationPhase phase = phaseFactory.create(
                    packageRegistry,
                    compositePackageDescr);
            phase.process();
            this.results.addAll(phase.getResults());
            if (this.results.hasErrors()) {
                return;
            }
        }
    }

    @Override
    public Collection<? extends KnowledgeBuilderResult> getResults() {
        return results.getAllResults();
    }
}
