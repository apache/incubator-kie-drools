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
package org.drools.compiler.builder.impl.processors;

import java.util.Collection;
import java.util.HashMap;
import java.util.Map;
import java.util.function.Supplier;

import org.drools.compiler.builder.PackageRegistryManager;
import org.drools.compiler.builder.impl.BuildResultCollector;
import org.drools.compiler.builder.impl.GlobalVariableContext;
import org.drools.compiler.builder.impl.KnowledgeBuilderConfigurationImpl;
import org.drools.compiler.builder.impl.TypeDeclarationBuilder;
import org.drools.compiler.builder.impl.TypeDeclarationContext;
import org.drools.compiler.compiler.PackageRegistry;
import org.drools.compiler.lang.descr.CompositePackageDescr;
import org.drools.kiesession.rulebase.InternalKnowledgeBase;
import org.drools.util.StringUtils;
import org.drools.util.TypeResolver;
import org.kie.internal.builder.KnowledgeBuilderResult;
import org.kie.internal.builder.ResultSeverity;
import org.kie.internal.builder.conf.LanguageLevelOption;

import static java.util.Arrays.asList;

public class CompositePackageCompilationPhase implements CompilationPhase {
    private final Collection<CompositePackageDescr> packages;
    private final PackageRegistryManager pkgRegistryManager;
    private final TypeDeclarationBuilder typeBuilder;
    private GlobalVariableContext globalVariableContext;
    private TypeDeclarationContext typeDeclarationContext;
    private final InternalKnowledgeBase kBase;
    private final KnowledgeBuilderConfigurationImpl configuration;

    private final BuildResultCollector buildResultCollector;

    public CompositePackageCompilationPhase(
            Collection<CompositePackageDescr> packages,
            PackageRegistryManager pkgRegistryManager,
            TypeDeclarationBuilder typeBuilder,
            GlobalVariableContext globalVariableContext,
            TypeDeclarationContext typeDeclarationContext,
            BuildResultCollector buildResultCollector,
            InternalKnowledgeBase kBase,
            KnowledgeBuilderConfigurationImpl configuration) {
        this.packages = packages;
        this.pkgRegistryManager = pkgRegistryManager;
        this.typeBuilder = typeBuilder;
        this.globalVariableContext = globalVariableContext;
        this.typeDeclarationContext = typeDeclarationContext;
        this.buildResultCollector = buildResultCollector;
        this.kBase = kBase;
        this.configuration = configuration;
    }


    @Override
    public void process() {

        // initPackageRegistries(packages);
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
                iteratingPhase((reg, desc) -> new WindowDeclarationCompilationPhase(reg, desc, typeDeclarationContext)),
                iteratingPhase((reg, desc) -> new FunctionCompilationPhase(reg, desc, configuration)),
                iteratingPhase((reg, desc) -> GlobalCompilationPhase.of(reg, desc, kBase, globalVariableContext, desc.getFilter())),
                // end OtherDeclarationCompilationPhase

                iteratingPhase((pkgRegistry, packageDescr) ->
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
        boolean isStrict = configuration.getOption(LanguageLevelOption.KEY).useJavaAnnotations();
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
        return buildResultCollector.getResults(ResultSeverity.INFO, ResultSeverity.WARNING, ResultSeverity.ERROR);
    }
}

