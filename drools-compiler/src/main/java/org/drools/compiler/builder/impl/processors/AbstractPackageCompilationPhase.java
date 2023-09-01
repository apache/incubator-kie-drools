package org.drools.compiler.builder.impl.processors;

import org.drools.compiler.builder.impl.BuildResultCollector;
import org.drools.compiler.builder.impl.BuildResultCollectorImpl;
import org.drools.compiler.compiler.PackageRegistry;
import org.drools.drl.ast.descr.PackageDescr;
import org.kie.internal.builder.KnowledgeBuilderResult;

import java.util.Collection;

public abstract class AbstractPackageCompilationPhase implements CompilationPhase {
    protected final PackageRegistry pkgRegistry;
    protected final PackageDescr packageDescr;
    protected final BuildResultCollector results;

    public AbstractPackageCompilationPhase(PackageRegistry pkgRegistry, PackageDescr packageDescr, BuildResultCollector buildResultCollector) {
        this.pkgRegistry = pkgRegistry;
        this.packageDescr = packageDescr;
        this.results = buildResultCollector;
    }

    public AbstractPackageCompilationPhase(PackageRegistry pkgRegistry, PackageDescr packageDescr) {
        this(pkgRegistry, packageDescr, new BuildResultCollectorImpl());
    }

    public abstract void process();

    protected BuildResultCollector getBuildResultAccumulator() {
        return this.results;
    }

    public Collection<? extends KnowledgeBuilderResult> getResults() {
        return results.getAllResults();
    }
}
