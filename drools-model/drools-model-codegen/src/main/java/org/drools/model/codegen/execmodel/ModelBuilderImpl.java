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
package org.drools.model.codegen.execmodel;

import org.drools.compiler.builder.impl.KnowledgeBuilderConfigurationImpl;
import org.drools.compiler.builder.impl.KnowledgeBuilderImpl;
import org.drools.compiler.builder.impl.processors.CompilationPhase;
import org.drools.compiler.compiler.PackageRegistry;
import org.drools.compiler.kie.builder.impl.BuildContext;
import org.drools.compiler.lang.descr.CompositePackageDescr;
import org.drools.base.definitions.InternalKnowledgePackage;
import org.drools.drl.ast.descr.GlobalDescr;
import org.drools.drl.ast.descr.ImportDescr;
import org.drools.drl.ast.descr.PackageDescr;
import org.drools.model.codegen.execmodel.generator.DRLIdGenerator;
import org.drools.model.codegen.execmodel.generator.DrlxParseUtil;
import org.drools.model.codegen.execmodel.processors.DeclaredTypeCompilationPhase;
import org.drools.model.codegen.execmodel.processors.ModelMainCompilationPhase;
import org.kie.api.builder.ReleaseId;
import org.kie.api.io.Resource;
import org.kie.internal.builder.KnowledgeBuilderResult;

import java.util.Collection;
import java.util.List;
import java.util.function.Function;

import static java.util.Arrays.asList;
import static org.drools.base.util.Drools.hasMvel;

public class ModelBuilderImpl<T extends PackageSources> extends KnowledgeBuilderImpl {

    private final DRLIdGenerator exprIdGenerator = new DRLIdGenerator();

    private final Function<PackageModel, T> sourcesGenerator;
    private final PackageModelManager packageModels;
    private final boolean oneClassPerRule;
    private final PackageSourceManager<T> packageSources = new PackageSourceManager<>();

    private CompositePackageManager compositePackagesManager;

    public ModelBuilderImpl(Function<PackageModel, T> sourcesGenerator, KnowledgeBuilderConfigurationImpl configuration, ReleaseId releaseId, boolean oneClassPerRule) {
        super(configuration);
        this.sourcesGenerator = sourcesGenerator;
        this.oneClassPerRule = oneClassPerRule;
        this.packageModels = new PackageModelManager(this.getBuilderConfiguration(), releaseId, exprIdGenerator);
        this.compositePackagesManager = new CompositePackageManager();
    }

    @Override
    protected void doFirstBuildStep(Collection<CompositePackageDescr> packages) {
    }

    @Override
    protected void addPackageWithResource(final PackageDescr packageDescr, Resource resource) {
        compositePackagesManager.register(packageDescr);
        PackageRegistry pkgRegistry = getOrCreatePackageRegistry(packageDescr);
        InternalKnowledgePackage pkg = pkgRegistry.getPackage();
        for (final ImportDescr importDescr : packageDescr.getImports()) {
            pkgRegistry.addImport(importDescr);
        }
        for (GlobalDescr globalDescr : packageDescr.getGlobals()) {
            try {
                Class<?> globalType = pkg.getTypeResolver().resolveType(globalDescr.getType());
                addGlobal(globalDescr.getIdentifier(), globalType);
                pkg.addGlobal(globalDescr.getIdentifier(), globalType);
            } catch (ClassNotFoundException e) {
                throw new RuntimeException(e);
            }
        }
    }

    @Override
    protected void doSecondBuildStep(Collection<CompositePackageDescr> compositePackages) {
        Collection<CompositePackageDescr> packages = compositePackagesManager.findPackages(compositePackages);


        List<CompilationPhase> phases = asList(
                new DeclaredTypeCompilationPhase(
                        this.packageModels,
                        this.getPackageRegistryManager(),
                        this.getBuildContext(),
                        this.getBuilderConfiguration(),
                        packages),
                new ModelMainCompilationPhase<>(
                        this.packageModels,
                        this.getPackageRegistryManager(),
                        packages,
                        this.getBuilderConfiguration(),
                        hasMvel(),
                        this.getKnowledgeBase(),
                        this,
                        this.getGlobalVariableContext(),
                        this.sourcesGenerator,
                        this.packageSources,
                        oneClassPerRule));

        for (CompilationPhase phase : phases) {
            phase.process();
            Collection<? extends KnowledgeBuilderResult> results = phase.getResults();
            this.getBuildResultCollector().addAll(results);
            if (this.getBuildResultCollector().hasErrors()) {
                return;
            }
        }

        DrlxParseUtil.clearAccessorCache();
    }

    protected PackageModel getPackageModel(PackageDescr packageDescr, PackageRegistry pkgRegistry, String pkgName) {
        return packageModels.getPackageModel(packageDescr, pkgRegistry, pkgName);
    }

    public Collection<T> getPackageSources() {
        return packageSources.values();
    }

    public T getPackageSource(String packageName) {
        return packageSources.get(packageName);
    }

    @Override
    protected BuildContext createBuildContext() {
        return new CanonicalModelBuildContext();
    }

    @Override
    public CanonicalModelBuildContext getBuildContext() {
        return (CanonicalModelBuildContext) super.getBuildContext();
    }

}
