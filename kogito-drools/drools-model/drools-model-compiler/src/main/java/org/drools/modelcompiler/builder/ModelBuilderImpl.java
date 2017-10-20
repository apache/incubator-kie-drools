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

import org.drools.compiler.builder.impl.KnowledgeBuilderImpl;
import org.drools.compiler.compiler.PackageRegistry;
import org.drools.compiler.lang.descr.CompositePackageDescr;
import org.drools.compiler.lang.descr.PackageDescr;

import java.util.ArrayList;
import java.util.Collection;
import java.util.List;

import static org.drools.modelcompiler.builder.generator.ModelGenerator.generateModel;

public class ModelBuilderImpl extends KnowledgeBuilderImpl {

    private final List<PackageModel> packageModels = new ArrayList<>();

    @Override
    public void buildPackages( Collection<CompositePackageDescr> packages ) {
        initPackageRegistries(packages);
        buildOtherDeclarations(packages);
        buildRules(packages);
    }

    protected void buildRules(Collection<CompositePackageDescr> packages) {
        for (CompositePackageDescr packageDescr : packages) {
            setAssetFilter(packageDescr.getFilter());
            PackageRegistry pkgRegistry = getPackageRegistry(packageDescr.getNamespace());
            compileKnowledgePackages(packageDescr, pkgRegistry);
            setAssetFilter(null);
        }
    }

    @Override
    protected void compileKnowledgePackages( PackageDescr packageDescr, PackageRegistry pkgRegistry ) {
        packageModels.add( generateModel(pkgRegistry.getPackage(), packageDescr) );
    }

    public List<PackageModel> getPackageModels() {
        return packageModels;
    }
}
