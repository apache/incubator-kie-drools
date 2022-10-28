/*
 * Copyright 2022 Red Hat, Inc. and/or its affiliates.
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *      http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 *
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package org.drools.compiler.builder.impl.processors;

import org.drools.compiler.builder.PackageRegistryManager;
import org.drools.compiler.builder.impl.BuildResultCollector;
import org.drools.compiler.builder.impl.BuildResultCollectorImpl;
import org.drools.compiler.builder.impl.GlobalVariableContext;
import org.drools.compiler.builder.impl.KnowledgeBuilderConfigurationImpl;
import org.drools.compiler.builder.impl.TypeDeclarationBuilder;
import org.drools.compiler.builder.impl.TypeDeclarationContext;
import org.drools.compiler.compiler.PackageRegistry;
import org.drools.compiler.lang.descr.CompositePackageDescr;
import org.drools.util.TypeResolver;
import org.drools.util.StringUtils;
import org.drools.kiesession.rulebase.InternalKnowledgeBase;
import org.kie.internal.builder.KnowledgeBuilderResult;
import org.kie.internal.builder.ResultSeverity;

import java.util.Collection;
import java.util.HashMap;
import java.util.Iterator;
import java.util.Map;
import java.util.function.Supplier;

import static java.util.Arrays.asList;

public class CompositePackageCompilationPhase implements CompilationPhase {
    private final Collection<CompositePackageDescr> packages;
    private final PackageRegistryManager pkgRegistryManager;
    private final TypeDeclarationBuilder typeBuilder;
    private GlobalVariableContext globalVariableContext;
    private TypeDeclarationContext typeDeclarationContext;
    private final InternalKnowledgeBase kBase;
    private final KnowledgeBuilderConfigurationImpl configuration;

    private final BuildResultCollector buildResultCollector = new BuildResultCollectorImpl();

    public CompositePackageCompilationPhase(
            Collection<CompositePackageDescr> packages,
            PackageRegistryManager pkgRegistryManager,
            TypeDeclarationBuilder typeBuilder,
            GlobalVariableContext globalVariableContext,
            TypeDeclarationContext typeDeclarationContext,
            InternalKnowledgeBase kBase,
            KnowledgeBuilderConfigurationImpl configuration) {
        this.packages = packages;
        this.pkgRegistryManager = pkgRegistryManager;
        this.typeBuilder = typeBuilder;
        this.globalVariableContext = globalVariableContext;
        this.typeDeclarationContext = typeDeclarationContext;
        this.kBase = kBase;
        this.configuration = configuration;
    }


    @Override
    public void process() {

        // initPackageRegistries(packages);
        Map<String, Supplier<AnnotationNormalizer>> annotationNormalizers =
                initAnnotationNormalizers();

        IteratingPhase initialPhase = iteratingPhase("TypeDeclarationAnnotationNormalizer", (pkgRegistry, packageDescr) ->
                new TypeDeclarationAnnotationNormalizer(annotationNormalizers.get(packageDescr.getNamespace()).get(), packageDescr));
        initialPhase.process();
        initialPhase.getResults().forEach(this.buildResultCollector::add);
        if (buildResultCollector.hasErrors()) {
            // early exit
            return;
        }

        Collection<CompilationPhase> phases = asList(
                new TypeDeclarationCompositeCompilationPhase(packages, typeBuilder),
                iteratingPhase("ImportCompilationPhase", ImportCompilationPhase::new),
                iteratingPhase("EntryPointDeclarationCompilationPhase", EntryPointDeclarationCompilationPhase::new),

                // begin OtherDeclarationCompilationPhase
                iteratingPhase("AccumulateFunctionCompilationPhase", AccumulateFunctionCompilationPhase::new),
                iteratingPhase("WindowDeclarationCompilationPhase", (reg, desc) -> new WindowDeclarationCompilationPhase(reg, desc, typeDeclarationContext)),
                iteratingPhase("FunctionCompilationPhase", (reg, desc) -> new FunctionCompilationPhase(reg, desc, configuration)),
                iteratingPhase("GlobalCompilationPhase", (reg, desc) -> GlobalCompilationPhase.of(reg, desc, kBase, globalVariableContext, desc.getFilter())),
                // end OtherDeclarationCompilationPhase

                iteratingPhase("RuleAnnotationNormalizer", (pkgRegistry, packageDescr) ->
                        new RuleAnnotationNormalizer(annotationNormalizers.get(packageDescr.getNamespace()).get(), packageDescr))
        );

        for (CompilationPhase phase : phases) {
            phase.process();
            phase.getResults().forEach(this.buildResultCollector::addBuilderResult);
        }

    }

    private Map<String, Supplier<AnnotationNormalizer>> initAnnotationNormalizers() {
        // use a supplier to ensure a fresh instance
        Map<String, Supplier<AnnotationNormalizer>> annotationNormalizers = new HashMap<>();
        boolean isStrict = configuration.getLanguageLevel().useJavaAnnotations();
        for (CompositePackageDescr packageDescr : packages) {
            if (StringUtils.isEmpty(packageDescr.getName())) {
                packageDescr.setName(configuration.getDefaultPackageName());
            }
            PackageRegistry pkgRegistry = pkgRegistryManager.getOrCreatePackageRegistry(packageDescr);
            TypeResolver typeResolver = pkgRegistry.getTypeResolver();

            annotationNormalizers.put(
                    packageDescr.getNamespace(),
                    () -> AnnotationNormalizer.of(typeResolver, isStrict));
        }
        return annotationNormalizers;
    }

    private IteratingPhase iteratingPhase(String name, SinglePackagePhaseFactory phaseFactory) {
        return new IteratingPhase(name, packages, pkgRegistryManager, phaseFactory);
    }

    @Override
    public Collection<? extends KnowledgeBuilderResult> getResults() {
        return buildResultCollector.getResults(ResultSeverity.INFO, ResultSeverity.WARNING, ResultSeverity.ERROR);
    }
}

