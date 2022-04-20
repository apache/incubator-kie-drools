package org.drools.modelcompiler.builder.processors;

import org.drools.compiler.builder.PackageRegistryManager;
import org.drools.compiler.builder.impl.BuildResultAccumulator;
import org.drools.compiler.builder.impl.BuildResultAccumulatorImpl;
import org.drools.compiler.builder.impl.KnowledgeBuilderConfigurationImpl;
import org.drools.compiler.builder.impl.processors.CompilationPhase;
import org.drools.compiler.builder.impl.processors.IterablePhaseFactory;
import org.drools.compiler.builder.impl.processors.IteratingPhase;
import org.drools.compiler.compiler.PackageRegistry;
import org.drools.compiler.lang.descr.CompositePackageDescr;
import org.drools.core.definitions.InternalKnowledgePackage;
import org.drools.modelcompiler.KiePackagesBuilder;
import org.drools.modelcompiler.builder.CanonicalModelBuildContext;
import org.drools.modelcompiler.builder.PackageModel;
import org.drools.modelcompiler.builder.PackageModelManager;
import org.drools.modelcompiler.builder.generator.declaredtype.POJOGenerator;
import org.kie.internal.builder.KnowledgeBuilderResult;

import java.util.Collection;
import java.util.List;

import static java.util.Arrays.asList;

public class DeclaredTypeCompilationPhase implements CompilationPhase {

    private final PackageModelManager packageModelManager;
    private final PackageRegistryManager pkgRegistryManager;
    private final CanonicalModelBuildContext buildContext;
    private final KnowledgeBuilderConfigurationImpl buildConfiguration;
    private final Collection<CompositePackageDescr> packages;
    private final BuildResultAccumulator results;

    public DeclaredTypeCompilationPhase(
            PackageModelManager packageModelManager,
            PackageRegistryManager pkgRegistryManager,
            CanonicalModelBuildContext buildContext,
            KnowledgeBuilderConfigurationImpl buildConfiguration,
            Collection<CompositePackageDescr> packages) {
        this.packageModelManager = packageModelManager;
        this.pkgRegistryManager = pkgRegistryManager;
        this.buildContext = buildContext;
        this.buildConfiguration = buildConfiguration;
        this.packages = packages;
        this.results = new BuildResultAccumulatorImpl();
    }

    @Override
    public void process() {
        List<CompilationPhase> phases = asList(
                iteratingPhase((reg, acc) -> new TypeDeclarationRegistrationPhase(reg, acc, pkgRegistryManager)),
                iteratingPhase((reg, acc) ->
                        new POJOGenerator(
                                results, reg.getPackage(), acc, packageModelManager.getPackageModel(acc, reg, reg.getPackage().getName()))),
                new GeneratedPojoCompilationPhase(
                        packageModelManager, buildContext, buildConfiguration.getClassLoader()),
                new PojoStoragePhase(buildContext, pkgRegistryManager, packages)
        );

        for (CompilationPhase phase : phases) {
            phase.process();
            this.results.addAll(phase.getResults());
            if (this.results.hasErrors()) {
                break;
            }
        }

    }

    private IteratingPhase iteratingPhase(IterablePhaseFactory phaseFactory) {
        return new IteratingPhase(packages, pkgRegistryManager, phaseFactory);
    }

    @Override
    public Collection<? extends KnowledgeBuilderResult> getResults() {
        return results.getAllResults();
    }

    //
//
//    private void registerTypeDeclarations( ) {
//        for (CompositePackageDescr packageDescr : packages) {
//            PackageRegistry pkgRegistry = pkgRegistryManager.getOrCreatePackageRegistry(packageDescr);
//            TypeDeclarationRegistrationPhase typeDeclarationRegistrationPhase =
//                    new TypeDeclarationRegistrationPhase(pkgRegistry, packageDescr, pkgRegistryManager);
//            typeDeclarationRegistrationPhase.process();
//            this.results.addAll(typeDeclarationRegistrationPhase.getResults());
//        }
//    }
//
//    private void buildDeclaredTypes( Collection<CompositePackageDescr> packages ) {
//        for (CompositePackageDescr packageDescr : packages) {
//            PackageRegistry pkgRegistry = pkgRegistryManager.getPackageRegistry(packageDescr.getNamespace());
//            InternalKnowledgePackage pkg = pkgRegistry.getPackage();
//            PackageModel model = packageModelManager.getPackageModel(packageDescr, pkgRegistry, pkg.getName());
//            model.addImports(pkg.getTypeResolver().getImports());
//            new POJOGenerator(results, pkg, packageDescr, model).process();
//        }
//    }
//
//    private void compileGeneratedPojos(PackageModelManager packageModels) {
//        GeneratedPojoCompilationPhase generatedPojoCompilationPhase =
//                new GeneratedPojoCompilationPhase(
//                        packageModels, buildContext, buildConfiguration.getClassLoader());
//
//        generatedPojoCompilationPhase.process();
//        this.results.addAll(generatedPojoCompilationPhase.getResults());
//    }
//    private void storeGeneratedPojosInPackages(Collection<CompositePackageDescr> packages) {
//        PojoStoragePhase pojoStoragePhase =
//                new PojoStoragePhase(buildContext, pkgRegistryManager, packages);
//        pojoStoragePhase.process();
//        results.addAll(pojoStoragePhase.getResults());
//    }


}
