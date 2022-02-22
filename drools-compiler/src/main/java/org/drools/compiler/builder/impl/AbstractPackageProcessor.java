package org.drools.compiler.builder.impl;

import org.drools.compiler.compiler.PackageRegistry;
import org.drools.drl.ast.descr.PackageDescr;
import org.kie.internal.builder.KnowledgeBuilderResult;

import java.util.ArrayList;
import java.util.Collection;

public abstract class AbstractPackageProcessor {
    protected final PackageRegistry pkgRegistry;
    protected final PackageDescr packageDescr;
    protected final Collection<KnowledgeBuilderResult> results;

    public AbstractPackageProcessor(PackageRegistry pkgRegistry, PackageDescr packageDescr) {
        this.pkgRegistry = pkgRegistry;
        this.packageDescr = packageDescr;
        this.results = new ArrayList<>();
    }

    public abstract void process();
    public Collection<KnowledgeBuilderResult> getResults() {
        return results;
    }
}
