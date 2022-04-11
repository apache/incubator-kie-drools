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

import org.drools.compiler.builder.DroolsAssemblerContext;
import org.drools.compiler.builder.PackageRegistryManager;
import org.drools.compiler.builder.impl.BuildResultAccumulator;
import org.drools.compiler.builder.impl.GlobalVariableContext;
import org.drools.compiler.builder.impl.KnowledgeBuilderConfigurationImpl;
import org.drools.compiler.builder.impl.TypeDeclarationBuilder;
import org.drools.compiler.compiler.PackageRegistry;
import org.drools.compiler.lang.descr.CompositePackageDescr;
import org.drools.core.addon.TypeResolver;
import org.drools.core.util.StringUtils;
import org.drools.kiesession.rulebase.InternalKnowledgeBase;
import org.kie.internal.builder.KnowledgeBuilderResult;
import org.kie.internal.builder.ResultSeverity;

import java.util.ArrayList;
import java.util.Collection;
import java.util.HashMap;
import java.util.Map;
import java.util.function.Supplier;

import static java.util.Arrays.asList;

public class CompositePackageCompilationPhase implements CompilationPhase {
    private final Collection<CompositePackageDescr> packages;
    private final PackageRegistryManager pkgRegistryManager;
    private final TypeDeclarationBuilder typeBuilder;
    private GlobalVariableContext globalVariableContext;
    private DroolsAssemblerContext droolsAssemblerContext;
    private final InternalKnowledgeBase kBase;
    private final KnowledgeBuilderConfigurationImpl configuration;

    private final BuildResultAccumulator buildResultAccumulator;

    public CompositePackageCompilationPhase(
            Collection<CompositePackageDescr> packages,
            PackageRegistryManager pkgRegistryManager,
            TypeDeclarationBuilder typeBuilder,
            GlobalVariableContext globalVariableContext,
            DroolsAssemblerContext droolsAssemblerContext,
            BuildResultAccumulator buildResultAccumulator,
            InternalKnowledgeBase kBase,
            KnowledgeBuilderConfigurationImpl configuration) {
        this.packages = packages;
        this.pkgRegistryManager = pkgRegistryManager;
        this.typeBuilder = typeBuilder;
        this.globalVariableContext = globalVariableContext;
        this.droolsAssemblerContext = droolsAssemblerContext;
        this.buildResultAccumulator = buildResultAccumulator;
        this.kBase = kBase;
        this.configuration = configuration;
    }


    @Override
    public void process() {
        Map<String, Supplier<AnnotationNormalizer>> annotationNormalizers =
                initAnnotationNormalizers();

        Collection<CompilationPhase> phases = asList(
                iteratingPhase((pkgRegistry, packageDescr) ->
                        new TypeDeclarationAnnotationNormalizer(annotationNormalizers.get(packageDescr.getNamespace()).get(), packageDescr)),
                new TypeDeclarationCompositeCompilationPhase(packages, typeBuilder),
                iteratingPhase(ImportCompilationPhase::new),
                iteratingPhase(EntryPointDeclarationCompilationPhase::new),

                // begin OtherDeclarationCompilationPhase
                iteratingPhase(AccumulateFunctionCompilationPhase::new),
                iteratingPhase((reg, desc) -> new WindowDeclarationCompilationPhase(reg, desc, droolsAssemblerContext)),
                iteratingPhase((reg, desc) -> new FunctionCompilationPhase(reg, desc, configuration)),
                iteratingPhase((reg, desc) -> new GlobalCompilationPhase(reg, desc, kBase, globalVariableContext, desc.getFilter())),
                // end OtherDeclarationCompilationPhase

                iteratingPhase((pkgRegistry, packageDescr) ->
                        new RuleAnnotationNormalizer(annotationNormalizers.get(packageDescr.getNamespace()).get(), packageDescr))
        );

        for (CompilationPhase phase : phases) {
            phase.process();
            phase.getResults().forEach(this.buildResultAccumulator::addBuilderResult);
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

    private IteratingPhase iteratingPhase(SinglePackagePhaseFactory phaseFactory) {
        return new IteratingPhase(packages, pkgRegistryManager, phaseFactory);
    }

    @Override
    public Collection<? extends KnowledgeBuilderResult> getResults() {
        return buildResultAccumulator.getResults(ResultSeverity.INFO, ResultSeverity.WARNING, ResultSeverity.ERROR);
    }
}

interface SinglePackagePhaseFactory {
    CompilationPhase create(PackageRegistry pkgRegistry, CompositePackageDescr packageDescr);
}

class IteratingPhase implements CompilationPhase {
    private final Collection<CompositePackageDescr> packages;
    private final PackageRegistryManager pkgRegistryManager;
    private final SinglePackagePhaseFactory phaseFactory;

    private final Collection<KnowledgeBuilderResult> results = new ArrayList<>();

    public IteratingPhase(Collection<CompositePackageDescr> packages, PackageRegistryManager pkgRegistryManager, SinglePackagePhaseFactory phaseFactory) {
        this.packages = packages;
        this.pkgRegistryManager = pkgRegistryManager;
        this.phaseFactory = phaseFactory;
    }

    @Override
    public void process() {
        for (CompositePackageDescr compositePackageDescr : packages) {
            CompilationPhase phase = phaseFactory.create(
                    pkgRegistryManager.getPackageRegistry(compositePackageDescr.getNamespace()),
                    compositePackageDescr);
            phase.process();
            results.addAll(phase.getResults());
        }
    }

    @Override
    public Collection<? extends KnowledgeBuilderResult> getResults() {
        return results;
    }
}