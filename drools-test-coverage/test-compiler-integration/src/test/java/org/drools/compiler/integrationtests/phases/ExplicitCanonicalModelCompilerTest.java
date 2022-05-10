/*
 * Copyright 2022 Red Hat, Inc. and/or its affiliates.
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 *
 *      http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package org.drools.compiler.integrationtests.phases;

import org.drools.compiler.builder.PackageRegistryManager;
import org.drools.compiler.builder.impl.BuildResultCollector;
import org.drools.compiler.builder.impl.BuildResultCollectorImpl;
import org.drools.compiler.builder.impl.GlobalVariableContext;
import org.drools.compiler.builder.impl.GlobalVariableContextImpl;
import org.drools.compiler.builder.impl.InternalKnowledgeBaseProvider;
import org.drools.compiler.builder.impl.KnowledgeBuilderConfigurationImpl;
import org.drools.compiler.builder.impl.PackageRegistryManagerImpl;
import org.drools.compiler.builder.impl.RootClassLoaderProvider;
import org.drools.compiler.builder.impl.TypeDeclarationBuilder;
import org.drools.compiler.builder.impl.TypeDeclarationContext;
import org.drools.compiler.builder.impl.TypeDeclarationContextImpl;
import org.drools.compiler.builder.impl.TypeDeclarationManagerImpl;
import org.drools.compiler.builder.impl.processors.AccumulateFunctionCompilationPhase;
import org.drools.compiler.builder.impl.processors.CompilationPhase;
import org.drools.compiler.builder.impl.processors.FunctionCompilationPhase;
import org.drools.compiler.builder.impl.processors.GlobalCompilationPhase;
import org.drools.compiler.builder.impl.processors.IteratingPhase;
import org.drools.compiler.builder.impl.processors.RuleValidator;
import org.drools.compiler.builder.impl.processors.SinglePackagePhaseFactory;
import org.drools.compiler.builder.impl.processors.WindowDeclarationCompilationPhase;
import org.drools.compiler.builder.impl.resources.DrlResourceHandler;
import org.drools.compiler.compiler.PackageRegistry;
import org.drools.compiler.lang.descr.CompositePackageDescr;
import org.drools.drl.ast.descr.PackageDescr;
import org.drools.drl.parser.DroolsParserException;
import org.drools.kiesession.rulebase.InternalKnowledgeBase;
import org.drools.modelcompiler.builder.CanonicalModelBuildContext;
import org.drools.modelcompiler.builder.GeneratedFile;
import org.drools.modelcompiler.builder.PackageModelManager;
import org.drools.modelcompiler.builder.PackageSourceManager;
import org.drools.modelcompiler.builder.PackageSources;
import org.drools.modelcompiler.builder.generator.DRLIdGenerator;
import org.drools.modelcompiler.builder.generator.declaredtype.POJOGenerator;
import org.drools.modelcompiler.builder.processors.DeclaredTypeDeregistrationPhase;
import org.drools.modelcompiler.builder.processors.DeclaredTypeRegistrationPhase;
import org.drools.modelcompiler.builder.processors.GeneratedPojoCompilationPhase;
import org.drools.modelcompiler.builder.processors.ModelGeneratorPhase;
import org.drools.modelcompiler.builder.processors.PojoStoragePhase;
import org.drools.modelcompiler.builder.processors.SourceCodeGenerationPhase;
import org.drools.util.io.ClassPathResource;
import org.junit.Test;
import org.kie.api.io.Resource;
import org.kie.util.maven.support.ReleaseIdImpl;

import java.io.IOException;
import java.util.ArrayList;
import java.util.Collection;
import java.util.List;

import static java.util.Arrays.asList;
import static org.junit.Assert.assertEquals;
import static org.junit.Assert.fail;

public class ExplicitCanonicalModelCompilerTest {

    @Test
    public void testCompile() throws DroolsParserException, IOException {
        Resource resource = new ClassPathResource("org/drools/compiler/integrationtests/phases/ExplicitCompilerTest.drl");



        InternalKnowledgeBase kBase = null;
        KnowledgeBuilderConfigurationImpl configuration = new KnowledgeBuilderConfigurationImpl();
        ClassLoader rootClassLoader = configuration.getClassLoader();

        BuildResultCollectorImpl results = new BuildResultCollectorImpl();

        RootClassLoaderProvider rootClassLoaderProvider = () -> rootClassLoader;
        InternalKnowledgeBaseProvider internalKnowledgeBaseProvider = () -> kBase;

        PackageRegistryManagerImpl packageRegistryManager =
                new PackageRegistryManagerImpl(
                        configuration, rootClassLoaderProvider, internalKnowledgeBaseProvider);

        GlobalVariableContext globalVariableContext = new GlobalVariableContextImpl();

        TypeDeclarationContextImpl typeDeclarationContext =
                new TypeDeclarationContextImpl(configuration, packageRegistryManager, globalVariableContext);
        TypeDeclarationBuilder typeBuilder = new TypeDeclarationBuilder(typeDeclarationContext);
        typeDeclarationContext.setTypeDeclarationManager(new TypeDeclarationManagerImpl(typeBuilder, kBase));


        DrlResourceHandler handler = new DrlResourceHandler(configuration);
        final PackageDescr packageDescr = handler.process(resource);
        handler.getResults().forEach(results::addBuilderResult);


        CompositePackageDescr compositePackageDescr = new CompositePackageDescr(resource, packageDescr);
        Collection<CompositePackageDescr> compositePackageDescrs = asList(compositePackageDescr);
        PackageModelManager packageModelManager =
                new PackageModelManager(configuration, new ReleaseIdImpl("org.drools:dummy:1.0-SNAPSHOT"), new DRLIdGenerator());
        CanonicalModelBuildContext buildContext = new CanonicalModelBuildContext();

        PackageSourceManager<PackageSources> packageSourceManager = new PackageSourceManager<>();
        ExplicitCanonicalModelCompiler compiler =
                new ExplicitCanonicalModelCompiler(
                        compositePackageDescrs,
                        packageRegistryManager,
                        packageModelManager,
                        buildContext,
                        configuration,
                        results,
                        typeDeclarationContext,
                        globalVariableContext,
                        packageSourceManager);

        compiler.process();

        List<GeneratedFile> generatedSources = new ArrayList<>();
        for (PackageSources src : packageSourceManager.getPackageSources()) {
            src.collectGeneratedFiles(generatedSources);
        }

        assertEquals(4, generatedSources.size());

    }

}

class ExplicitCanonicalModelCompiler {

    private final Collection<CompositePackageDescr> packages;
    private final PackageRegistryManager pkgRegistryManager;
    private final PackageModelManager packageModelManager;
    private final CanonicalModelBuildContext buildContext;
    private final KnowledgeBuilderConfigurationImpl configuration;
    private final BuildResultCollector results;
    private final TypeDeclarationContext typeDeclarationContext;
    private final InternalKnowledgeBase kBase = null;
    private final GlobalVariableContext globalVariableContext;
    private final PackageSourceManager<PackageSources> packageSourceManager;
    final boolean hasMvel = false;
    final boolean oneClassPerRule = true;

    ExplicitCanonicalModelCompiler(Collection<CompositePackageDescr> packages,
                                   PackageRegistryManager pkgRegistryManager,
                                   PackageModelManager packageModelManager,
                                   CanonicalModelBuildContext buildContext,
                                   KnowledgeBuilderConfigurationImpl configuration,
                                   BuildResultCollector results,
                                   TypeDeclarationContext typeDeclarationContext,
                                   GlobalVariableContext globalVariableContext,
                                   PackageSourceManager<PackageSources> packageSourceManager) {
        this.packages = packages;
        this.pkgRegistryManager = pkgRegistryManager;
        this.packageModelManager = packageModelManager;
        this.buildContext = buildContext;
        this.configuration = configuration;
        this.results = results;
        this.typeDeclarationContext = typeDeclarationContext;
        this.globalVariableContext = globalVariableContext;
        this.packageSourceManager = packageSourceManager;
    }

    public void process() {
        List<CompilationPhase> phases = new ArrayList<>();

        phases.add(iteratingPhase((reg, acc) -> new DeclaredTypeRegistrationPhase(reg, acc, pkgRegistryManager)));
        phases.add(iteratingPhase((reg, acc) ->
                new POJOGenerator(reg.getPackage(), acc, packageModelManager.getPackageModel(acc, reg, reg.getPackage().getName()))));
        phases.add(new GeneratedPojoCompilationPhase(
                packageModelManager, buildContext, configuration.getClassLoader()));
        phases.add(new PojoStoragePhase(buildContext, pkgRegistryManager, packages));
        phases.add(iteratingPhase(AccumulateFunctionCompilationPhase::new));
        if (hasMvel) {
            phases.add(iteratingPhase((reg, acc) -> new WindowDeclarationCompilationPhase(reg, acc, typeDeclarationContext)));
        }
        phases.add(iteratingPhase((reg, acc) -> new FunctionCompilationPhase(reg, acc, configuration)));
        phases.add(iteratingPhase((reg, acc) -> new GlobalCompilationPhase(reg, acc, kBase, globalVariableContext, acc.getFilter())));
        phases.add(new DeclaredTypeDeregistrationPhase(packages, pkgRegistryManager));

        // ---

        phases.add(iteratingPhase((reg, acc) -> new RuleValidator(reg, acc, configuration))); // validateUniqueRuleNames
        phases.add(iteratingPhase((reg, acc) -> new ModelGeneratorPhase(reg, acc, packageModelManager.getPackageModel(acc, reg, acc.getName()), typeDeclarationContext))); // validateUniqueRuleNames
        phases.add(iteratingPhase((reg, acc) -> new SourceCodeGenerationPhase<>(
                packageModelManager.getPackageModel(acc, reg, acc.getName()), packageSourceManager, PackageSources::dumpSources, oneClassPerRule))); // validateUniqueRuleNames


        for (CompilationPhase phase : phases) {
            phase.process();
            this.results.addAll(phase.getResults());
            if (results.hasErrors()) {
                break;
            }
        }

    }

    private IteratingPhase iteratingPhase(SinglePackagePhaseFactory phaseFactory) {
        return new IteratingPhase(packages, pkgRegistryManager, phaseFactory);
    }
}
