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
package org.kie.pmml.evaluator.assembler.service;

import java.io.IOException;
import java.util.Collection;
import java.util.List;
import java.util.stream.Collectors;

import org.drools.compiler.builder.impl.KnowledgeBuilderImpl;
import org.drools.compiler.compiler.PackageRegistry;
import org.drools.compiler.lang.descr.PackageDescr;
import org.drools.core.definitions.InternalKnowledgePackage;
import org.drools.core.definitions.ResourceTypePackageRegistry;
import org.kie.api.internal.assembler.KieAssemblerService;
import org.kie.api.io.Resource;
import org.kie.api.io.ResourceConfiguration;
import org.kie.api.io.ResourceType;
import org.kie.api.io.ResourceWithConfiguration;
import org.kie.pmml.commons.exceptions.ExternalException;
import org.kie.pmml.commons.exceptions.KiePMMLException;
import org.kie.pmml.commons.model.KiePMMLModel;
import org.kie.pmml.compiler.executor.PMMLCompiler;
import org.kie.pmml.compiler.executor.PMMLCompilerImpl;
import org.kie.pmml.evaluator.api.container.PMMLPackage;
import org.kie.pmml.evaluator.assembler.container.PMMLPackageImpl;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class PMMLAssemblerService implements KieAssemblerService {

    public static final String PMML_COMPILER_CACHE_KEY = "PMML_COMPILER_CACHE_KEY";
    private static final Logger logger = LoggerFactory.getLogger(PMMLAssemblerService.class);

    @Override
    public ResourceType getResourceType() {
        return ResourceType.PMML;
    }

    @Override
    public void addResources(Object kbuilder, Collection<ResourceWithConfiguration> resources, ResourceType type) {
        KnowledgeBuilderImpl kbuilderImpl = (KnowledgeBuilderImpl) kbuilder;
        addModels(kbuilderImpl, getKiePMMLModelsFromResourcesWithConfigurations(kbuilderImpl, resources));
    }

    @Override
    public void addResource(Object kbuilder, Resource resource, ResourceType type, ResourceConfiguration configuration) {
        logger.warn("invoked legacy addResource (no control on the order of the assembler compilation): {}", resource.getSourcePath());
        KnowledgeBuilderImpl kbuilderImpl = (KnowledgeBuilderImpl) kbuilder;
        addModels(kbuilderImpl, getKiePMMLModelsFromResource(kbuilderImpl, resource));
    }

    protected void addModels(KnowledgeBuilderImpl kbuilderImpl, List<KiePMMLModel> toAdd) {
        // TODO {gcardosi} verify correct creation/adding of PMMLPackage
        PackageRegistry pkgReg = kbuilderImpl.getOrCreatePackageRegistry(new PackageDescr());
        InternalKnowledgePackage kpkgs = pkgReg.getPackage();
        ResourceTypePackageRegistry rpkg = kpkgs.getResourceTypePackages();
        PMMLPackage pmmlPkg = rpkg.computeIfAbsent(ResourceType.PMML, rtp -> new PMMLPackageImpl());
        pmmlPkg.addAll(toAdd);
    }

    /**
     * @param kbuilderImpl
     * @param resourceWithConfigurations
     * @return
     * @throws KiePMMLException if any <code>KiePMMLInternalException</code> has been thrown during execution
     * @throws ExternalException if any other kind of <code>Exception</code> has been thrown during execution
     */
    protected List<KiePMMLModel> getKiePMMLModelsFromResourcesWithConfigurations(KnowledgeBuilderImpl kbuilderImpl, Collection<ResourceWithConfiguration> resourceWithConfigurations) {
        return resourceWithConfigurations.stream()
                .map(ResourceWithConfiguration::getResource)
                .flatMap(resource -> getKiePMMLModelsFromResource(kbuilderImpl, resource).stream())
                .collect(Collectors.toList());
    }

    /**
     * @param kbuilderImpl
     * @param resource
     * @return
     * @throws KiePMMLException if any <code>KiePMMLInternalException</code> has been thrown during execution
     * @throws ExternalException if any other kind of <code>Exception</code> has been thrown during execution
     */
    protected List<KiePMMLModel> getKiePMMLModelsFromResource(KnowledgeBuilderImpl kbuilderImpl, Resource resource) {
        PMMLCompiler pmmlCompiler = kbuilderImpl.getCachedOrCreate(PMML_COMPILER_CACHE_KEY, () -> getCompiler(kbuilderImpl));
        final String releaseId = kbuilderImpl.getKnowledgeBase().getResolvedReleaseId().toExternalForm();
        logger.debug("getKiePMMLModelsFromResource releaseId {}", releaseId);
        try {
            return pmmlCompiler.getModels(resource.getInputStream(), releaseId);
        } catch (IOException e) {
            throw new ExternalException("ExternalException", e);
        }
    }

    private PMMLCompiler getCompiler(KnowledgeBuilderImpl kbuilderImpl) {
        // to retrieve model implementations
        return new PMMLCompilerImpl();
    }
}
