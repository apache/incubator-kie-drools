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

import org.drools.compiler.builder.impl.BuildResultAccumulator;
import org.drools.compiler.builder.impl.BuildResultAccumulatorImpl;
import org.drools.compiler.compiler.PackageRegistry;
import org.drools.drl.ast.descr.PackageDescr;
import org.kie.internal.builder.KnowledgeBuilderResult;
import org.kie.internal.builder.ResultSeverity;

import java.util.ArrayList;
import java.util.Collection;

public abstract class AbstractPackageCompilationPhase implements CompilationPhase {
    protected final PackageRegistry pkgRegistry;
    protected final PackageDescr packageDescr;
    protected final BuildResultAccumulator results;

    public AbstractPackageCompilationPhase(PackageRegistry pkgRegistry, PackageDescr packageDescr, BuildResultAccumulator buildResultAccumulator) {
        this.pkgRegistry = pkgRegistry;
        this.packageDescr = packageDescr;
        this.results = buildResultAccumulator;
    }

    public AbstractPackageCompilationPhase(PackageRegistry pkgRegistry, PackageDescr packageDescr) {
        this(pkgRegistry, packageDescr, new BuildResultAccumulatorImpl());
    }

    public abstract void process();

    protected BuildResultAccumulator getBuildResultAccumulator() {
        return this.results;
    }

    public Collection<? extends KnowledgeBuilderResult> getResults() {
        return results.getAllResults();
    }
}
