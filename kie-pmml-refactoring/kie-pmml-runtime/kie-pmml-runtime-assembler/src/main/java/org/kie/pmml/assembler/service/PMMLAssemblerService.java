/*
 * Copyright 2020 Red Hat, Inc. and/or its affiliates.
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *     http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package org.kie.pmml.assembler.service;

import java.util.Collection;
import java.util.List;
import java.util.stream.Collectors;

import org.drools.compiler.builder.impl.KnowledgeBuilderImpl;
import org.drools.compiler.compiler.PackageRegistry;
import org.drools.compiler.lang.descr.PackageDescr;
import org.drools.core.definitions.InternalKnowledgePackage;
import org.drools.core.definitions.ResourceTypePackageRegistry;
import org.kie.api.builder.ReleaseId;
import org.kie.api.internal.assembler.KieAssemblerService;
import org.kie.api.io.Resource;
import org.kie.api.io.ResourceConfiguration;
import org.kie.api.io.ResourceType;
import org.kie.api.io.ResourceWithConfiguration;
import org.kie.pmml.api.model.KiePMMLModel;
import org.kie.pmml.assembler.container.PMMLPackageImpl;
import org.kie.pmml.compiler.executor.PMMLCompiler;
import org.kie.pmml.compiler.executor.PMMLCompilerImpl;
import org.kie.pmml.runtime.api.container.PMMLPackage;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import static org.kie.pmml.api.Constants.RELEASE_ID;
import static org.kie.pmml.api.interfaces.FunctionalWrapperFactory.throwingFunctionWrapper;

public class PMMLAssemblerService implements KieAssemblerService {

    public static final String PMML_COMPILER_CACHE_KEY = "PMML_COMPILER_CACHE_KEY";
    private static final Logger logger = LoggerFactory.getLogger(PMMLAssemblerService.class);

    @Override
    public ResourceType getResourceType() {
        return ResourceType.PMML;
    }

    @Override
    public void addResources(Object kbuilder, Collection<ResourceWithConfiguration> resources, ResourceType type) throws Exception {
        KnowledgeBuilderImpl kbuilderImpl = (KnowledgeBuilderImpl) kbuilder;
        addModels(kbuilderImpl, getKiePMMLModelsFromResourcesWithConfigurations(kbuilderImpl, resources));
    }

    @Override
    public void addResource(Object kbuilder, Resource resource, ResourceType type, ResourceConfiguration configuration) throws Exception {
        logger.warn("invoked legacy addResource (no control on the order of the assembler compilation): {}", resource.getSourcePath());
        KnowledgeBuilderImpl kbuilderImpl = (KnowledgeBuilderImpl) kbuilder;
        addModels(kbuilderImpl, getKiePMMLModelsFromResource(kbuilderImpl, resource));
    }

    protected void addModels(KnowledgeBuilderImpl kbuilderImpl, List<KiePMMLModel> toAdd) {
        // TODO {gcardosi} verify correct creation/adding of PMMLPackage
        PackageRegistry pkgReg = kbuilderImpl.getOrCreatePackageRegistry( new PackageDescr() );
        InternalKnowledgePackage kpkgs = pkgReg.getPackage();
        ResourceTypePackageRegistry rpkg = kpkgs.getResourceTypePackages();
        PMMLPackage pmmlPkg = rpkg.computeIfAbsent(ResourceType.PMML, rtp -> new PMMLPackageImpl());
        pmmlPkg.addAll(toAdd);
    }

    protected List<KiePMMLModel> getKiePMMLModelsFromResourcesWithConfigurations(KnowledgeBuilderImpl kbuilderImpl, Collection<ResourceWithConfiguration> resourceWithConfigurations) throws Exception {
        return resourceWithConfigurations.stream()
                .map(ResourceWithConfiguration::getResource)
                .flatMap(throwingFunctionWrapper(resource -> getKiePMMLModelsFromResource(kbuilderImpl, resource).stream()))
                .collect(Collectors.toList());
    }

    protected List<KiePMMLModel> getKiePMMLModelsFromResource(KnowledgeBuilderImpl kbuilderImpl, Resource resource) throws Exception {
        PMMLCompiler pmmlCompiler = kbuilderImpl.getCachedOrCreate(PMML_COMPILER_CACHE_KEY, () -> getCompiler(kbuilderImpl));
        // TODO {gcardosi} replace with dynamically generated one
        logger.info("getKiePMMLModelsFromResource releaseId {}", RELEASE_ID);
        return pmmlCompiler.getResults(resource.getInputStream(), RELEASE_ID);
    }

    private PMMLCompiler getCompiler(KnowledgeBuilderImpl kbuilderImpl) {
        // to retrieve model implementations
        return new PMMLCompilerImpl();
    }
}
