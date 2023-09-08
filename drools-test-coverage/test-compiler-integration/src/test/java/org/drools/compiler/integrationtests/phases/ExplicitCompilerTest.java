/**
 * Licensed to the Apache Software Foundation (ASF) under one
 * or more contributor license agreements.  See the NOTICE file
 * distributed with this work for additional information
 * regarding copyright ownership.  The ASF licenses this file
 * to you under the Apache License, Version 2.0 (the
 * "License"); you may not use this file except in compliance
 * with the License.  You may obtain a copy of the License at
 *
 *   http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing,
 * software distributed under the License is distributed on an
 * "AS IS" BASIS, WITHOUT WARRANTIES OR CONDITIONS OF ANY
 * KIND, either express or implied.  See the License for the
 * specific language governing permissions and limitations
 * under the License.
 */
package org.drools.compiler.integrationtests.phases;

import org.drools.compiler.builder.impl.BuildResultCollectorImpl;
import org.drools.compiler.builder.impl.GlobalVariableContext;
import org.drools.compiler.builder.impl.GlobalVariableContextImpl;
import org.drools.compiler.builder.impl.InternalKnowledgeBaseProvider;
import org.drools.compiler.builder.impl.KnowledgeBuilderConfigurationImpl;
import org.drools.compiler.builder.impl.KnowledgeBuilderRulesConfigurationImpl;
import org.drools.compiler.builder.impl.PackageRegistryManagerImpl;
import org.drools.compiler.builder.impl.RootClassLoaderProvider;
import org.drools.compiler.builder.impl.TypeDeclarationBuilder;
import org.drools.compiler.builder.impl.TypeDeclarationContextImpl;
import org.drools.compiler.builder.impl.TypeDeclarationManagerImpl;
import org.drools.compiler.builder.impl.processors.AccumulateFunctionCompilationPhase;
import org.drools.compiler.builder.impl.processors.AnnotationNormalizer;
import org.drools.compiler.builder.impl.processors.CompilationPhase;
import org.drools.compiler.builder.impl.processors.ConsequenceCompilationPhase;
import org.drools.compiler.builder.impl.processors.EntryPointDeclarationCompilationPhase;
import org.drools.compiler.builder.impl.processors.FunctionCompilationPhase;
import org.drools.compiler.builder.impl.processors.ImmutableGlobalCompilationPhase;
import org.drools.compiler.builder.impl.processors.ImmutableRuleCompilationPhase;
import org.drools.compiler.builder.impl.processors.ImportCompilationPhase;
import org.drools.compiler.builder.impl.processors.RuleAnnotationNormalizer;
import org.drools.compiler.builder.impl.processors.RuleValidator;
import org.drools.compiler.builder.impl.processors.ImmutableFunctionCompiler;
import org.drools.compiler.builder.impl.processors.TypeDeclarationAnnotationNormalizer;
import org.drools.compiler.builder.impl.processors.TypeDeclarationCompilationPhase;
import org.drools.compiler.builder.impl.processors.WindowDeclarationCompilationPhase;
import org.drools.compiler.builder.impl.resources.DrlResourceHandler;
import org.drools.compiler.compiler.PackageRegistry;
import org.drools.base.definitions.InternalKnowledgePackage;
import org.drools.core.impl.InternalRuleBase;
import org.drools.core.impl.RuleBaseFactory;
import org.drools.drl.ast.descr.AttributeDescr;
import org.drools.drl.ast.descr.PackageDescr;
import org.drools.drl.parser.DroolsParserException;
import org.drools.kiesession.rulebase.InternalKnowledgeBase;
import org.drools.kiesession.rulebase.SessionsAwareKnowledgeBase;
import org.drools.io.ClassPathResource;
import org.junit.Test;
import org.kie.api.io.Resource;
import org.kie.api.runtime.KieSession;
import org.kie.internal.builder.KnowledgeBuilderFactory;

import java.io.IOException;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

import static java.util.Arrays.asList;
import static org.assertj.core.api.Assertions.fail;

public class ExplicitCompilerTest {

    @Test
    public void testCompile() throws DroolsParserException, IOException {
        Resource resource = new ClassPathResource("org/drools/compiler/integrationtests/phases/ExplicitCompilerTest.drl");

        int parallelRulesBuildThreshold = 0;
        InternalKnowledgeBase kBase = null;
        KnowledgeBuilderConfigurationImpl configuration = KnowledgeBuilderFactory.newKnowledgeBuilderConfiguration().as(KnowledgeBuilderConfigurationImpl.KEY);
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
        TypeDeclarationBuilder typeBuilder = new TypeDeclarationBuilder(typeDeclarationContext, results);
        typeDeclarationContext.setTypeDeclarationManager(new TypeDeclarationManagerImpl(typeBuilder, kBase));


        DrlResourceHandler handler = new DrlResourceHandler(configuration);
        final PackageDescr packageDescr = handler.process(resource);
        handler.getResults().forEach(results::addBuilderResult);


        PackageRegistry packageRegistry =
                packageRegistryManager.getOrCreatePackageRegistry(packageDescr);

        AnnotationNormalizer annotationNormalizer =
                AnnotationNormalizer.of(
                        packageRegistry.getTypeResolver(),
                        configuration.as(KnowledgeBuilderRulesConfigurationImpl.KEY).getLanguageLevel().useJavaAnnotations());



        Map<String, AttributeDescr> attributesForPackage =
                packageRegistryManager.getPackageAttributes().get(packageDescr.getNamespace());

        List<CompilationPhase> phases = asList(
                new ImportCompilationPhase(packageRegistry, packageDescr),
                new TypeDeclarationAnnotationNormalizer(annotationNormalizer, packageDescr),
                new EntryPointDeclarationCompilationPhase(packageRegistry, packageDescr),
                new AccumulateFunctionCompilationPhase(packageRegistry, packageDescr),
                new TypeDeclarationCompilationPhase(packageDescr, typeBuilder, packageRegistry, null),
                new WindowDeclarationCompilationPhase(packageRegistry, packageDescr, typeDeclarationContext),
                new FunctionCompilationPhase(packageRegistry, packageDescr, configuration),
                new ImmutableGlobalCompilationPhase(packageRegistry, packageDescr, globalVariableContext),
                new RuleAnnotationNormalizer(annotationNormalizer, packageDescr),
                /*         packageRegistry.setDialect(getPackageDialect(packageDescr)) */
                new RuleValidator(packageRegistry, packageDescr, configuration),
                new ImmutableFunctionCompiler(packageRegistry, packageDescr, rootClassLoader),
                new ImmutableRuleCompilationPhase(packageRegistry, packageDescr, parallelRulesBuildThreshold,
                        attributesForPackage, resource, typeDeclarationContext),
//                new ReteCompiler(packageRegistry, packageDescr, kBase, null), // no-op when kbase==null
                new ConsequenceCompilationPhase(packageRegistryManager)
        );


        for (CompilationPhase phase : phases) {
            phase.process();
            phase.getResults().forEach(results::addBuilderResult);
            if (results.hasErrors()) {
                results.getErrors().forEach(System.out::println);
                fail("Found compilation errors at Phase " + phase.getClass().getSimpleName());
            }
        }


        List<InternalKnowledgePackage> packages =
                packageRegistryManager.getPackageRegistry().values()
                        .stream().map(PackageRegistry::getPackage).collect(Collectors.toList());
        InternalRuleBase kbase = RuleBaseFactory.newRuleBase();
        kbase.addPackages(packages);
        SessionsAwareKnowledgeBase sessionsAwareKnowledgeBase =
                new SessionsAwareKnowledgeBase(kbase);
        KieSession kieSession = sessionsAwareKnowledgeBase.newKieSession();


        kieSession.insert("HELLO");
        kieSession.fireAllRules();


    }

}
