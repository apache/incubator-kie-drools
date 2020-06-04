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
import org.kie.api.io.Resource;
import org.kie.api.io.ResourceWithConfiguration;
import org.kie.pmml.commons.exceptions.ExternalException;
import org.kie.pmml.commons.exceptions.KiePMMLException;
import org.kie.pmml.commons.model.KiePMMLModel;
import org.kie.pmml.compiler.executor.PMMLCompiler;
import org.kie.pmml.compiler.executor.PMMLCompilerImpl;

import static org.kie.pmml.evaluator.assembler.service.PMMLAssemblerService.PMML_COMPILER_CACHE_KEY;
import static org.kie.pmml.evaluator.assembler.service.PMMLAssemblerService.getFactoryClassNamePackageName;

/**
 * Class meant to <b>compile</b> resources
 */
public class PMMLCompilerService {

    /**
     * @param kbuilderImpl
     * @param resourceWithConfigurations
     * @return
     * @throws KiePMMLException if any <code>KiePMMLInternalException</code> has been thrown during execution
     * @throws ExternalException if any other kind of <code>Exception</code> has been thrown during execution
     */
    public static List<KiePMMLModel> getKiePMMLModelsFromResourcesWithConfigurationsFromPlugin(KnowledgeBuilderImpl kbuilderImpl, Collection<ResourceWithConfiguration> resourceWithConfigurations) {
        return resourceWithConfigurations.stream()
                .map(ResourceWithConfiguration::getResource)
                .flatMap(resource -> getKiePMMLModelsFromResourceFromPlugin(kbuilderImpl, resource).stream())
                .collect(Collectors.toList());
    }

    /**
     * @param kbuilderImpl
     * @param resourceWithConfigurations
     * @return
     * @throws KiePMMLException if any <code>KiePMMLInternalException</code> has been thrown during execution
     * @throws ExternalException if any other kind of <code>Exception</code> has been thrown during execution
     */
    public static List<KiePMMLModel> getKiePMMLModelsCompiledFromResourcesWithConfigurations(KnowledgeBuilderImpl kbuilderImpl, Collection<ResourceWithConfiguration> resourceWithConfigurations) {
        return resourceWithConfigurations.stream()
                .map(ResourceWithConfiguration::getResource)
                .flatMap(resource -> getKiePMMLModelsCompiledFromResource(kbuilderImpl, resource).stream())
                .collect(Collectors.toList());
    }

    /**
     * @param kbuilderImpl
     * @param resource
     * @return
     * @throws KiePMMLException if any <code>KiePMMLInternalException</code> has been thrown during execution
     * @throws ExternalException if any other kind of <code>Exception</code> has been thrown during execution
     */
    public static List<KiePMMLModel> getKiePMMLModelsCompiledFromResource(KnowledgeBuilderImpl kbuilderImpl, Resource resource) {
        PMMLCompiler pmmlCompiler = kbuilderImpl.getCachedOrCreate(PMML_COMPILER_CACHE_KEY, () -> getCompiler(kbuilderImpl));
        try {
            return pmmlCompiler.getModels(resource.getInputStream(), kbuilderImpl);
        } catch (IOException e) {
            throw new ExternalException("ExternalException", e);
        }
    }

    /**
     * @param kbuilderImpl
     * @param resource
     * @return
     */
    public static List<KiePMMLModel> getKiePMMLModelsFromResourceFromPlugin(KnowledgeBuilderImpl kbuilderImpl, Resource resource) {
        PMMLCompiler pmmlCompiler = kbuilderImpl.getCachedOrCreate(PMML_COMPILER_CACHE_KEY, () -> getCompiler(kbuilderImpl));
        String[] classNamePackageName = getFactoryClassNamePackageName(resource);
        String factoryClassName = classNamePackageName[0];
        String packageName = classNamePackageName[1];
        try {
            return pmmlCompiler.getModelsFromPlugin(factoryClassName, packageName, resource.getInputStream(), kbuilderImpl);
        } catch (IOException e) {
            throw new ExternalException("ExternalException", e);
        }
    }

    static PMMLCompiler getCompiler(KnowledgeBuilderImpl kbuilderImpl) {
        // to retrieve model implementations
        return new PMMLCompilerImpl();
    }
}
