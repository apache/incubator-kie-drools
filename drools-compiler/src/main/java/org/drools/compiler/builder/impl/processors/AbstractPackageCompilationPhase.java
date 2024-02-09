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

import org.drools.compiler.builder.impl.BuildResultCollector;
import org.drools.compiler.builder.impl.BuildResultCollectorImpl;
import org.drools.compiler.compiler.PackageRegistry;
import org.drools.drl.ast.descr.PackageDescr;
import org.kie.internal.builder.KnowledgeBuilderResult;

public abstract class AbstractPackageCompilationPhase implements CompilationPhase {
    protected final PackageRegistry pkgRegistry;
    protected final PackageDescr packageDescr;
    protected final BuildResultCollector results;

    public AbstractPackageCompilationPhase(PackageRegistry pkgRegistry, PackageDescr packageDescr, BuildResultCollector buildResultCollector) {
        this.pkgRegistry = pkgRegistry;
        this.packageDescr = packageDescr;
        this.results = buildResultCollector;
    }

    public AbstractPackageCompilationPhase(PackageRegistry pkgRegistry, PackageDescr packageDescr) {
        this(pkgRegistry, packageDescr, new BuildResultCollectorImpl());
    }

    public abstract void process();

    protected BuildResultCollector getBuildResultAccumulator() {
        return this.results;
    }

    public Collection<? extends KnowledgeBuilderResult> getResults() {
        return results.getAllResults();
    }
}
