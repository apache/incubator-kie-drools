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
import java.util.Collections;
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
import org.kie.pmml.compiler.commons.factories.KiePMMLModelFactory;
import org.kie.pmml.compiler.executor.PMMLCompiler;
import org.kie.pmml.compiler.executor.PMMLCompilerImpl;
import org.kie.pmml.evaluator.api.container.PMMLPackage;
import org.kie.pmml.evaluator.assembler.container.PMMLPackageImpl;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import static org.kie.pmml.commons.utils.KiePMMLModelUtils.getSanitizedClassName;
import static org.kie.pmml.commons.utils.KiePMMLModelUtils.getSanitizedPackageName;

public class PMMLAssemblerService implements KieAssemblerService {

    public static final String PMML_COMPILER_CACHE_KEY = "PMML_COMPILER_CACHE_KEY";
    private static final Logger logger = LoggerFactory.getLogger(PMMLAssemblerService.class);

    private static boolean isBuildFromMaven() {
        final String property = System.getProperty("kie-maven-plugin-launcher");
        return property != null && property.equals("true");
    }

    @Override
    public ResourceType getResourceType() {
        return ResourceType.PMML;
    }

    @Override
    public void addResources(Object kbuilder, Collection<ResourceWithConfiguration> resources, ResourceType type) {
        KnowledgeBuilderImpl kbuilderImpl = (KnowledgeBuilderImpl) kbuilder;
        if (isBuildFromMaven()) {
            addModels(kbuilderImpl, getKiePMMLModelsFromResourcesWithConfigurationsFromPlugin(kbuilderImpl, resources));
        } else {
            addModels(kbuilderImpl, getKiePMMLModelsFromResourcesWithConfigurations(kbuilderImpl, resources));
        }
    }

    @Override
    public void addResource(Object kbuilder, Resource resource, ResourceType type, ResourceConfiguration configuration) {
        logger.warn("invoked legacy addResource (no control on the order of the assembler compilation): {}", resource.getSourcePath());
        KnowledgeBuilderImpl kbuilderImpl = (KnowledgeBuilderImpl) kbuilder;
        if (isBuildFromMaven()) {
            addModels(kbuilderImpl, getKiePMMLModelsFromResourceFromPlugin(kbuilderImpl, resource));
        } else {
            addModels(kbuilderImpl, getKiePMMLModelsFromResource(kbuilderImpl, resource));
        }
    }

    protected void addModels(KnowledgeBuilderImpl kbuilderImpl, List<KiePMMLModel> toAdd) {
        for (KiePMMLModel kiePMMLModel : toAdd) {
            PackageDescr pkgDescr = new PackageDescr(kiePMMLModel.getKModulePackageName());
            PackageRegistry pkgReg = kbuilderImpl.getOrCreatePackageRegistry(pkgDescr);
            InternalKnowledgePackage kpkgs = pkgReg.getPackage();
            ResourceTypePackageRegistry rpkg = kpkgs.getResourceTypePackages();
            PMMLPackage pmmlPkg = rpkg.computeIfAbsent(ResourceType.PMML, rtp -> new PMMLPackageImpl());
            pmmlPkg.addAll(Collections.singletonList(kiePMMLModel));
        }
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
     * @param resourceWithConfigurations
     * @return
     * @throws KiePMMLException if any <code>KiePMMLInternalException</code> has been thrown during execution
     * @throws ExternalException if any other kind of <code>Exception</code> has been thrown during execution
     */
    protected List<KiePMMLModel> getKiePMMLModelsFromResourcesWithConfigurationsFromPlugin(KnowledgeBuilderImpl kbuilderImpl, Collection<ResourceWithConfiguration> resourceWithConfigurations) {
        return resourceWithConfigurations.stream()
                .map(ResourceWithConfiguration::getResource)
                .flatMap(resource -> getKiePMMLModelsFromResourceFromPlugin(kbuilderImpl, resource).stream())
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
        String[] classNamePackageName = getFactoryClassNamePackageName(resource);
        String factoryClassName = classNamePackageName[0];
        String packageName = classNamePackageName[1];
        String fullFactoryClassName = packageName + "." + factoryClassName;
        try {
            final Class<? extends KiePMMLModelFactory> aClass = (Class<? extends KiePMMLModelFactory>) kbuilderImpl.getRootClassLoader().loadClass(fullFactoryClassName);
            return aClass.newInstance().getKiePMMLModels();
        } catch (ClassNotFoundException e) {
            logger.info(String.format("%s class not found in rootClassLoader, going to compile model", fullFactoryClassName));
        } catch (Exception e) {
            throw new KiePMMLException("Exception while instantiating " + fullFactoryClassName, e);
        }
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
    protected List<KiePMMLModel> getKiePMMLModelsFromResourceFromPlugin(KnowledgeBuilderImpl kbuilderImpl, Resource resource) {
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

    /**
     * Returns an array where the first item is the <b>factory class</b> name and the second item is the <b>package</b> name,
     * built starting from the given <code>Resource</code>
     * @param resource
     * @return
     */
    private String[] getFactoryClassNamePackageName(Resource resource) {
        String sourcePath = resource.getSourcePath();
        String fileName = sourcePath.substring(sourcePath.lastIndexOf('/') + 1);
        fileName = fileName.replace(".pmml", "");
        String packageName = getSanitizedPackageName(fileName);
        String factoryClassName = getSanitizedClassName(fileName + "Factory");
        return new String[]{factoryClassName, packageName};
    }

    private PMMLCompiler getCompiler(KnowledgeBuilderImpl kbuilderImpl) {
        // to retrieve model implementations
        return new PMMLCompilerImpl();
    }
}
