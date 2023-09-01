package org.drools.model.codegen.execmodel.processors;

import org.drools.compiler.builder.impl.BuildResultCollectorImpl;
import org.drools.compiler.builder.impl.TypeDeclarationContext;
import org.drools.compiler.builder.impl.processors.AbstractPackageCompilationPhase;
import org.drools.compiler.compiler.PackageRegistry;
import org.drools.drl.ast.descr.PackageDescr;
import org.drools.model.codegen.execmodel.PackageModel;
import org.kie.internal.builder.KnowledgeBuilderResult;

import java.util.Collection;

import static org.drools.model.codegen.execmodel.generator.ModelGenerator.generateModel;

public class ModelGeneratorPhase extends AbstractPackageCompilationPhase {
    private final TypeDeclarationContext typeDeclarationContext;
    private final BuildResultCollectorImpl buildResultCollector;
    private final PackageModel packageModel;

    public ModelGeneratorPhase(PackageRegistry pkgRegistry, PackageDescr packageDescr, PackageModel packageModel, TypeDeclarationContext typeDeclarationContext) {
        super(pkgRegistry, packageDescr);
        this.typeDeclarationContext = typeDeclarationContext;
        this.buildResultCollector = new BuildResultCollectorImpl();
        this.packageModel = packageModel;
    }

    @Override
    public void process() {
        PackageModel.initPackageModel(typeDeclarationContext, buildResultCollector, pkgRegistry.getPackage(), pkgRegistry.getTypeResolver(), packageDescr, packageModel );
        generateModel(typeDeclarationContext, buildResultCollector, pkgRegistry.getPackage(), packageDescr, packageModel);
    }

    @Override
    public Collection<? extends KnowledgeBuilderResult> getResults() {
        return buildResultCollector.getAllResults();
    }
}
