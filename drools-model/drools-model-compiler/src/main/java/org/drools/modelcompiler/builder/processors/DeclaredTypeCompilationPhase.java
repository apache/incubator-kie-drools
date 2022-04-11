package org.drools.modelcompiler.builder.processors;

import org.drools.compiler.builder.impl.processors.AbstractPackageCompilationPhase;
import org.drools.compiler.compiler.PackageRegistry;
import org.drools.core.definitions.InternalKnowledgePackage;
import org.drools.drl.ast.descr.PackageDescr;
import org.drools.modelcompiler.builder.PackageModel;
import org.drools.modelcompiler.builder.generator.declaredtype.POJOGenerator;

public class DeclaredTypeCompilationPhase extends AbstractPackageCompilationPhase {

    private final PackageModel model;

    public DeclaredTypeCompilationPhase(PackageRegistry pkgRegistry, PackageDescr packageDescr, PackageModel packageModel) {
        super(pkgRegistry, packageDescr);
        this.model = packageModel;
    }

    @Override
    public void process() {
        InternalKnowledgePackage pkg = pkgRegistry.getPackage();
        model.addImports(pkg.getTypeResolver().getImports());
        new POJOGenerator(this.getBuildResultAccumulator(), pkg, packageDescr, model).findPOJOorGenerate();
    }

}
