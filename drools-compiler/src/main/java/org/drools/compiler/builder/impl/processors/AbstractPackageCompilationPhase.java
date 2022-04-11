package org.drools.compiler.builder.impl.processors;

import org.drools.compiler.builder.impl.BuildResultAccumulator;
import org.drools.compiler.builder.impl.BuildResultAccumulatorImpl;
import org.drools.compiler.compiler.PackageRegistry;
import org.drools.drl.ast.descr.PackageDescr;
import org.kie.internal.builder.KnowledgeBuilderResult;
import org.kie.internal.builder.ResultSeverity;

import java.util.ArrayList;
import java.util.Collection;

public abstract class AbstractPackageCompilationPhase implements CompilationPhase {
    protected final PackageRegistry pkgRegistry;
    protected final PackageDescr packageDescr;
    protected final BuildResultAccumulator results;

    public AbstractPackageCompilationPhase(PackageRegistry pkgRegistry, PackageDescr packageDescr, BuildResultAccumulator buildResultAccumulator) {
        this.pkgRegistry = pkgRegistry;
        this.packageDescr = packageDescr;
        this.results = buildResultAccumulator;
    }

    public AbstractPackageCompilationPhase(PackageRegistry pkgRegistry, PackageDescr packageDescr) {
        this(pkgRegistry, packageDescr, new BuildResultAccumulatorImpl());
    }

    public abstract void process();

    protected BuildResultAccumulator getBuildResultAccumulator() {
        return this.results;
    }

    public Collection<KnowledgeBuilderResult> getResults() {
        return results.getResults(ResultSeverity.INFO, ResultSeverity.WARNING, ResultSeverity.ERROR);
    }
}
