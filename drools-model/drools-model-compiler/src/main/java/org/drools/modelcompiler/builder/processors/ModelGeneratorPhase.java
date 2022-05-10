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

import org.drools.compiler.builder.impl.TypeDeclarationContext;
import org.drools.compiler.builder.impl.processors.AbstractPackageCompilationPhase;
import org.drools.compiler.compiler.PackageRegistry;
import org.drools.drl.ast.descr.PackageDescr;
import org.drools.modelcompiler.builder.PackageModel;
import org.kie.internal.builder.KnowledgeBuilderResult;

import java.util.Collection;

import static org.drools.modelcompiler.builder.generator.ModelGenerator.generateModel;

public class ModelGeneratorPhase extends AbstractPackageCompilationPhase {
    private final TypeDeclarationContext typeDeclarationContext;
    private final PackageModel packageModel;

    public ModelGeneratorPhase(PackageRegistry pkgRegistry, PackageDescr packageDescr, PackageModel packageModel, TypeDeclarationContext typeDeclarationContext) {
        super(pkgRegistry, packageDescr);
        this.packageModel = packageModel;
        this.typeDeclarationContext = typeDeclarationContext;
    }

    @Override
    public void process() {
        PackageModel.initPackageModel(typeDeclarationContext, pkgRegistry.getPackage(), pkgRegistry.getTypeResolver(), packageDescr, packageModel );
        generateModel(typeDeclarationContext, pkgRegistry.getPackage(), packageDescr, packageModel);
    }

    @Override
    public Collection<? extends KnowledgeBuilderResult> getResults() {
        return typeDeclarationContext.getAllResults();
    }
}
