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
package org.drools.model.codegen.execmodel.processors;

import java.util.Collection;
import java.util.function.Function;

import org.drools.compiler.builder.impl.BuildResultCollector;
import org.drools.compiler.builder.impl.BuildResultCollectorImpl;
import org.drools.compiler.builder.impl.processors.CompilationPhase;
import org.drools.model.codegen.execmodel.PackageModel;
import org.drools.model.codegen.execmodel.PackageSourceManager;
import org.kie.internal.builder.KnowledgeBuilderResult;

public class SourceCodeGenerationPhase<T> implements CompilationPhase {
    private final PackageModel pkgModel;
    private final PackageSourceManager<T> packageSources;
    private final Function<PackageModel, T> sourcesGenerator;
    private final BuildResultCollector results;

    private final boolean oneClassPerRule;

    public SourceCodeGenerationPhase(
            PackageModel pkgModel,
            PackageSourceManager<T> packageSources,
            Function<PackageModel, T> sourcesGenerator,
            boolean oneClassPerRule) {
        this.pkgModel = pkgModel;
        this.packageSources = packageSources;
        this.sourcesGenerator = sourcesGenerator;
        this.results = new BuildResultCollectorImpl();
        this.oneClassPerRule = oneClassPerRule;
    }

    @Override
    public void process() {
        pkgModel.setOneClassPerRule(oneClassPerRule);
        packageSources.put(pkgModel.getName(), sourcesGenerator.apply(pkgModel));
    }

    @Override
    public Collection<? extends KnowledgeBuilderResult> getResults() {
        return results.getAllResults();
    }
}
