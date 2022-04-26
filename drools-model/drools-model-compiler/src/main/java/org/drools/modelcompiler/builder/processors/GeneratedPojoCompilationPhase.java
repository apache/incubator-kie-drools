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

package org.drools.modelcompiler.builder.processors;

import org.drools.compiler.builder.impl.BuildResultAccumulator;
import org.drools.compiler.builder.impl.BuildResultAccumulatorImpl;
import org.drools.compiler.builder.impl.processors.CompilationPhase;
import org.drools.modelcompiler.builder.CanonicalModelBuildContext;
import org.drools.modelcompiler.builder.GeneratedClassWithPackage;
import org.drools.modelcompiler.builder.PackageModel;
import org.drools.modelcompiler.builder.PackageModelManager;
import org.kie.internal.builder.KnowledgeBuilderResult;

import java.util.Collection;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

import static org.drools.modelcompiler.builder.generator.declaredtype.POJOGenerator.compileType;

public class GeneratedPojoCompilationPhase implements CompilationPhase {
    private final PackageModelManager packageModels;
    private final CanonicalModelBuildContext buildContext;
    private final BuildResultAccumulator results = new BuildResultAccumulatorImpl();
    private final ClassLoader classLoader;

    public GeneratedPojoCompilationPhase(PackageModelManager packageModels, CanonicalModelBuildContext buildContext, ClassLoader classLoader) {
        this.packageModels = packageModels;
        this.buildContext = buildContext;
        this.classLoader = classLoader;
    }

    @Override
    public void process() {

        List<GeneratedClassWithPackage> allGeneratedPojos =
                packageModels.values().stream()
                        .flatMap(p -> p.getGeneratedPOJOsSource().stream()
                                .map(c -> new GeneratedClassWithPackage(c, p.getName(), p.getImports(), p.getStaticImports())))
                        .collect( Collectors.toList());

        Map<String, Class<?>> allCompiledClasses = compileType(results, classLoader, allGeneratedPojos);
        buildContext.registerGeneratedPojos(allGeneratedPojos, allCompiledClasses);
    }

    @Override
    public Collection<? extends KnowledgeBuilderResult> getResults() {
        return results.getAllResults();
    }
}
