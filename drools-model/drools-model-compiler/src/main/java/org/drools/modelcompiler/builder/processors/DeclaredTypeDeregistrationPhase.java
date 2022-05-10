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

package org.drools.modelcompiler.builder.processors;

import org.drools.compiler.builder.PackageRegistryManager;
import org.drools.compiler.builder.impl.processors.CompilationPhase;
import org.drools.compiler.lang.descr.CompositePackageDescr;
import org.kie.internal.builder.KnowledgeBuilderResult;

import java.util.Collection;
import java.util.Collections;

public class DeclaredTypeDeregistrationPhase implements CompilationPhase {

    Collection<CompositePackageDescr> packages;
    PackageRegistryManager packageRegistryManager;

    public DeclaredTypeDeregistrationPhase(Collection<CompositePackageDescr> packages, PackageRegistryManager packageRegistryManager) {
        this.packages = packages;
        this.packageRegistryManager = packageRegistryManager;
    }

    @Override
    public void process() {
        for (CompositePackageDescr packageDescr : packages) {
            packageRegistryManager.getOrCreatePackageRegistry(packageDescr).getPackage().getTypeDeclarations().clear();
        }
    }

    @Override
    public Collection<? extends KnowledgeBuilderResult> getResults() {
        return Collections.emptyList();
    }
}
