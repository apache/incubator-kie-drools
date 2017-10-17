/*
 * Copyright 2005 JBoss Inc
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *      http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package org.drools.modelcompiler.builder;

import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

import org.drools.compiler.builder.impl.KnowledgeBuilderImpl;
import org.drools.compiler.compiler.PackageRegistry;
import org.drools.compiler.lang.descr.PackageDescr;

import static org.drools.modelcompiler.builder.generator.ModelGenerator.generateModel;

public class ModelBuilderImpl extends KnowledgeBuilderImpl {

    private final List<PackageModel> packageModels = new ArrayList<>();

    @Override
    protected void compileKnowledgePackages( PackageDescr packageDescr, PackageRegistry pkgRegistry ) {
        super.compileKnowledgePackages( packageDescr, pkgRegistry );
        List<RuleDescrImpl> collect = packageDescr.getRules().stream()
            .map(descr -> new RuleDescrImpl(descr, pkgRegistry.getPackage().getRule(descr.getName())))
            .collect(Collectors.toList());
        packageModels.add(
                generateModel(pkgRegistry.getPackage(),
                              collect,
                              packageDescr.getFunctions(),
                              packageDescr.getTypeDeclarations(),
                              packageDescr.getWindowDeclarations()
                ));
    }

    public List<PackageModel> getPackageModels() {
        return packageModels;
    }
}
