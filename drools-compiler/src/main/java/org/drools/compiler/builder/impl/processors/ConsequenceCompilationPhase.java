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

package org.drools.compiler.builder.impl.processors;

import org.drools.compiler.builder.impl.PackageRegistryCompiler;
import org.drools.compiler.rule.builder.dialect.DialectError;
import org.kie.internal.builder.KnowledgeBuilderResult;

import java.util.ArrayList;
import java.util.Collection;

public class ConsequenceCompilationPhase implements CompilationPhase {
    private PackageRegistryCompiler packageRegistryCompiler;
    private Collection<KnowledgeBuilderResult> results = new ArrayList<>();

    public ConsequenceCompilationPhase(PackageRegistryCompiler packageRegistryCompiler) {
        this.packageRegistryCompiler = packageRegistryCompiler;
    }

    @Override
    public void process() {
        this.packageRegistryCompiler.compileAll();
        try {
            this.packageRegistryCompiler.reloadAll();
        } catch (Exception e) {
            results.add(new DialectError(null, "Unable to wire compiled classes, probably related to compilation failures:" + e.getMessage()));
        }
        results.addAll(this.packageRegistryCompiler.getResults());
    }

    @Override
    public Collection<? extends KnowledgeBuilderResult> getResults() {
        return results;
    }
}
