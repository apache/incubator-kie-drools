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

import java.util.List;

import org.drools.compiler.builder.impl.AssetFilter;
import org.drools.compiler.builder.impl.KnowledgeBuilderImpl;
import org.drools.compiler.builder.impl.TypeDeclarationBuilder;
import org.drools.compiler.compiler.PackageRegistry;
import org.drools.drl.ast.descr.PackageDescr;
import org.drools.kiesession.rulebase.InternalKnowledgeBase;
import org.kie.api.io.Resource;
import org.kie.internal.builder.KnowledgeBuilderConfiguration;
import org.kie.internal.builder.conf.LanguageLevelOption;

import static java.util.Arrays.asList;

public final class PackageCompilationPhase extends AbstractPackageCompilationPhase {
    private final KnowledgeBuilderImpl knowledgeBuilder;
    private final InternalKnowledgeBase kBase;
    private final KnowledgeBuilderConfiguration configuration;
    private final TypeDeclarationBuilder typeBuilder;
    private final AssetFilter filterCondition;
    private final Resource currentResource;

    public PackageCompilationPhase(
            KnowledgeBuilderImpl knowledgeBuilder,
            InternalKnowledgeBase kBase,
            KnowledgeBuilderConfiguration configuration,
            TypeDeclarationBuilder typeBuilder,
            AssetFilter filterCondition,
            PackageRegistry pkgRegistry,
            PackageDescr packageDescr,
            Resource currentResource) {
        super(pkgRegistry, packageDescr);
        this.knowledgeBuilder = knowledgeBuilder;
        this.kBase = kBase;
        this.configuration = configuration;
        this.typeBuilder = typeBuilder;
        this.filterCondition = filterCondition;
        this.currentResource = currentResource;
    }

    public void process() {
        AnnotationNormalizer annotationNormalizer =
                AnnotationNormalizer.of(
                        pkgRegistry.getTypeResolver(),
                        configuration.getOption(LanguageLevelOption.KEY).useJavaAnnotations());

        List<CompilationPhase> phases = asList(
                new ImportCompilationPhase(pkgRegistry, packageDescr),
                new TypeDeclarationAnnotationNormalizer(annotationNormalizer, packageDescr),
                new EntryPointDeclarationCompilationPhase(pkgRegistry, packageDescr),
                new AccumulateFunctionCompilationPhase(pkgRegistry, packageDescr),
                new TypeDeclarationCompilationPhase(packageDescr, typeBuilder, pkgRegistry, currentResource),
                new WindowDeclarationCompilationPhase(pkgRegistry, packageDescr, knowledgeBuilder),
                new FunctionCompilationPhase(pkgRegistry, packageDescr, configuration),
                GlobalCompilationPhase.of(pkgRegistry, packageDescr, kBase, knowledgeBuilder, filterCondition),
                new RuleAnnotationNormalizer(annotationNormalizer, packageDescr));

        phases.forEach(CompilationPhase::process);
        phases.forEach(p -> results.addAll(p.getResults()));

    }

}


