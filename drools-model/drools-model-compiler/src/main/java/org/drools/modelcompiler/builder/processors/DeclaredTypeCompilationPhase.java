package org.drools.modelcompiler.builder.processors;

import org.drools.compiler.builder.PackageRegistryManager;
import org.drools.compiler.builder.impl.BuildResultAccumulator;
import org.drools.compiler.builder.impl.KnowledgeBuilderConfigurationImpl;
import org.drools.compiler.builder.impl.processors.CompilationPhase;
import org.drools.compiler.compiler.PackageRegistry;
import org.drools.compiler.lang.descr.CompositePackageDescr;
import org.drools.core.definitions.InternalKnowledgePackage;
import org.drools.modelcompiler.KiePackagesBuilder;
import org.drools.modelcompiler.builder.CanonicalModelBuildContext;
import org.drools.modelcompiler.builder.PackageModel;
import org.drools.modelcompiler.builder.PackageModelManager;
import org.drools.modelcompiler.builder.generator.declaredtype.POJOGenerator;

import java.util.Collection;

public class DeclaredTypeCompilationPhase implements CompilationPhase {

    PackageModelManager packageModelManager;
    PackageRegistryManager packageRegistryManager;
    BuildResultAccumulator results;
    private CanonicalModelBuildContext buildContext;
    private KnowledgeBuilderConfigurationImpl buildConfiguration;


    private void registerTypeDeclarations( Collection<CompositePackageDescr> packages ) {
        for (CompositePackageDescr packageDescr : packages) {
            PackageRegistryManager pkgRegistryManager = packageRegistryManager;
            PackageRegistry pkgRegistry = pkgRegistryManager.getOrCreatePackageRegistry(packageDescr);
            TypeDeclarationRegistrationPhase typeDeclarationRegistrationPhase =
                    new TypeDeclarationRegistrationPhase(pkgRegistry, packageDescr, pkgRegistryManager);
            typeDeclarationRegistrationPhase.process();
            this.results.addAll(typeDeclarationRegistrationPhase.getResults());
        }
    }

    private void buildDeclaredTypes( Collection<CompositePackageDescr> packages ) {
        for (CompositePackageDescr packageDescr : packages) {
            PackageRegistry pkgRegistry = packageRegistryManager.getPackageRegistry(packageDescr.getNamespace());
            InternalKnowledgePackage pkg = pkgRegistry.getPackage();
            PackageModel model = packageModelManager.getPackageModel(packageDescr, pkgRegistry, pkg.getName());
            model.addImports(pkg.getTypeResolver().getImports());
            new POJOGenerator(results, pkg, packageDescr, model).process();
        }
    }

    private void compileGeneratedPojos(PackageModelManager packageModels) {
        GeneratedPojoCompilationPhase generatedPojoCompilationPhase =
                new GeneratedPojoCompilationPhase(
                        packageModels, buildContext, buildConfiguration.getClassLoader());

        generatedPojoCompilationPhase.process();
        this.results.addAll(generatedPojoCompilationPhase.getResults());
    }
    private void storeGeneratedPojosInPackages(Collection<CompositePackageDescr> packages) {
        PojoStoragePhase pojoStoragePhase =
                new PojoStoragePhase(buildContext, packageRegistryManager, packages);
        pojoStoragePhase.process();
        results.addAll(pojoStoragePhase.getResults());
    }


}
