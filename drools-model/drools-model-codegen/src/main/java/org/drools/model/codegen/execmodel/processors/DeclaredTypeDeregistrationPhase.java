package org.drools.model.codegen.execmodel.processors;

import org.drools.compiler.builder.PackageRegistryManager;
import org.drools.compiler.builder.impl.processors.CompilationPhase;
import org.drools.compiler.lang.descr.CompositePackageDescr;
import org.kie.internal.builder.KnowledgeBuilderResult;

import java.util.Collection;
import java.util.Collections;

public class DeclaredTypeDeregistrationPhase implements CompilationPhase {

    Collection<CompositePackageDescr> packages;
    PackageRegistryManager packageRegistryManager;

    public DeclaredTypeDeregistrationPhase(Collection<CompositePackageDescr> packages, PackageRegistryManager packageRegistryManager) {
        this.packages = packages;
        this.packageRegistryManager = packageRegistryManager;
    }

    @Override
    public void process() {
        for (CompositePackageDescr packageDescr : packages) {
            packageRegistryManager.getOrCreatePackageRegistry(packageDescr).getPackage().getTypeDeclarations().clear();
        }
    }

    @Override
    public Collection<? extends KnowledgeBuilderResult> getResults() {
        return Collections.emptyList();
    }
}
