package org.drools.compiler.builder.impl.processors;

import org.drools.compiler.compiler.PackageRegistry;
import org.drools.drl.ast.descr.PackageDescr;
import org.kie.internal.builder.KnowledgeBuilderResult;

import java.util.ArrayList;
import java.util.Collection;

public abstract class AbstractPackageCompilationPhase implements CompilationPhase {
    protected final PackageRegistry pkgRegistry;
    protected final PackageDescr packageDescr;
    protected final Collection<KnowledgeBuilderResult> results;

    public AbstractPackageCompilationPhase(PackageRegistry pkgRegistry, PackageDescr packageDescr) {
        this.pkgRegistry = pkgRegistry;
        this.packageDescr = packageDescr;
        this.results = new ArrayList<>();
    }

    public abstract void process();

    public Collection<KnowledgeBuilderResult> getResults() {
        return results;
    }
}
