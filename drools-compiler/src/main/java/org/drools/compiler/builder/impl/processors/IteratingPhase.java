package org.drools.compiler.builder.impl.processors;

import org.drools.compiler.builder.PackageRegistryManager;
import org.drools.compiler.compiler.PackageRegistry;
import org.drools.compiler.lang.descr.CompositePackageDescr;
import org.kie.internal.builder.KnowledgeBuilderResult;

import java.util.ArrayList;
import java.util.Collection;

/**
 * Decorates a
 * Iterates on a collection of {@link CompositePackageDescr}
 *
 */
public class IteratingPhase implements CompilationPhase {
    private final Collection<CompositePackageDescr> packages;
    private final PackageRegistryManager pkgRegistryManager;
    private final IterablePhaseFactory phaseFactory;

    private final Collection<KnowledgeBuilderResult> results = new ArrayList<>();

    public IteratingPhase(Collection<CompositePackageDescr> packages, PackageRegistryManager pkgRegistryManager, IterablePhaseFactory phaseFactory) {
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
            results.addAll(phase.getResults());
        }
    }

    @Override
    public Collection<? extends KnowledgeBuilderResult> getResults() {
        return results;
    }
}
