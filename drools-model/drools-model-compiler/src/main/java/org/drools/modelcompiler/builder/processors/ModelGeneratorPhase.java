package org.drools.modelcompiler.builder.processors;

import org.drools.compiler.builder.impl.KnowledgeBuilderImpl;
import org.drools.compiler.builder.impl.processors.AbstractPackageCompilationPhase;
import org.drools.compiler.compiler.PackageRegistry;
import org.drools.drl.ast.descr.PackageDescr;
import org.drools.modelcompiler.builder.PackageModel;

import static org.drools.modelcompiler.builder.generator.ModelGenerator.generateModel;

public class ModelGeneratorPhase extends AbstractPackageCompilationPhase {
    private final KnowledgeBuilderImpl kbuilder;
    private final PackageModel packageModel;

    public ModelGeneratorPhase(PackageRegistry pkgRegistry, PackageDescr packageDescr, PackageModel packageModel, KnowledgeBuilderImpl kbuilder) {
        super(pkgRegistry, packageDescr);
        this.packageModel = packageModel;
        this.kbuilder = kbuilder;
    }

    @Override
    public void process() {
        PackageModel.initPackageModel( kbuilder, pkgRegistry.getPackage(), pkgRegistry.getTypeResolver(), packageDescr, packageModel );
        generateModel(kbuilder, pkgRegistry.getPackage(), packageDescr, packageModel);
    }

}
