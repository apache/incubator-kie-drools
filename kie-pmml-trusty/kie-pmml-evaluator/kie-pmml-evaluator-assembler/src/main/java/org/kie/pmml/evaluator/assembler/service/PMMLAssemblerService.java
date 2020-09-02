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

import java.util.Collection;
import java.util.Collections;
import java.util.List;

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
import org.kie.pmml.commons.model.HasNestedModels;
import org.kie.pmml.commons.model.KiePMMLModel;
import org.kie.pmml.evaluator.api.container.PMMLPackage;
import org.kie.pmml.evaluator.assembler.container.PMMLPackageImpl;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import static org.kie.api.pmml.PMMLConstants.KIE_PMML_IMPLEMENTATION;
import static org.kie.api.pmml.PMMLConstants.LEGACY;
import static org.kie.api.pmml.PMMLConstants.NEW;
import static org.kie.pmml.commons.utils.KiePMMLModelUtils.getSanitizedClassName;
import static org.kie.pmml.commons.utils.KiePMMLModelUtils.getSanitizedPackageName;
import static org.kie.pmml.evaluator.assembler.service.PMMLCompilerService.getKiePMMLModelsCompiledFromResource;
import static org.kie.pmml.evaluator.assembler.service.PMMLCompilerService.getKiePMMLModelsCompiledFromResourcesWithConfigurations;
import static org.kie.pmml.evaluator.assembler.service.PMMLCompilerService.getKiePMMLModelsFromResourceFromPlugin;
import static org.kie.pmml.evaluator.assembler.service.PMMLCompilerService.getKiePMMLModelsFromResourcesWithConfigurationsFromPlugin;
import static org.kie.pmml.evaluator.assembler.service.PMMLLoaderService.getKiePMMLModelsLoadedFromResource;
import static org.kie.pmml.evaluator.assembler.service.PMMLLoaderService.getKiePMMLModelsLoadedFromResourcesWithConfigurations;

public class PMMLAssemblerService implements KieAssemblerService {

    public static final String PMML_COMPILER_CACHE_KEY = "PMML_COMPILER_CACHE_KEY";
    private static final Logger logger = LoggerFactory.getLogger(PMMLAssemblerService.class);

    private static boolean isBuildFromMaven() {
        final String property = System.getProperty("kie-maven-plugin-launcher", "false");
        return property.equals("true");
    }

    private static boolean isOtherImplementationPresent() {
        try {
            Thread.currentThread().getContextClassLoader().loadClass("org.kie.pmml.assembler.PMMLAssemblerService");
            return true;
        } catch (NoClassDefFoundError | ClassNotFoundException e) {
            return false;
        }
    }

    private static boolean isToEnable() {
        if (isjPMMLAvailableToClassLoader()) {
            return false;
        }
        if (!isOtherImplementationPresent()) {
            return true;
        } else {
            final String property = System.getProperty(KIE_PMML_IMPLEMENTATION.getName(), LEGACY.getName());
            return property.equals(NEW.getName());
        }
    }

    private static boolean isjPMMLAvailableToClassLoader() {
        try {
            Thread.currentThread().getContextClassLoader().loadClass("org.kie.dmn.jpmml.DMNjPMMLInvocationEvaluator");
            logger.info("jpmml libraries available on classpath, skipping kie-pmml parsing and compilation");
            return true;
        } catch (ClassNotFoundException e) {
            return false;
        }
    }

    /**
     * Returns an array where the first item is the <b>factory class</b> name and the second item is the <b>package</b> name,
     * built starting from the given <code>Resource</code>
     * @param resource
     * @return
     */
    public static String[] getFactoryClassNamePackageName(Resource resource) {
        String sourcePath = resource.getSourcePath();
        String fileName = sourcePath.substring(sourcePath.lastIndexOf('/') + 1);
        fileName = fileName.replace(".pmml", "");
        String packageName = getSanitizedPackageName(fileName);
        String factoryClassName = getSanitizedClassName(fileName + "Factory");
        return new String[]{factoryClassName, packageName};
    }

    @Override
    public ResourceType getResourceType() {
        return isToEnable() ? ResourceType.PMML : ResourceType.NOOP;
    }

    @Override
    public void addResources(Object kbuilder, Collection<ResourceWithConfiguration> resources, ResourceType type) {
        KnowledgeBuilderImpl kbuilderImpl = (KnowledgeBuilderImpl) kbuilder;
        if (isBuildFromMaven()) {
            addModels(kbuilderImpl, getKiePMMLModelsFromResourcesWithConfigurationsFromPlugin(kbuilderImpl, resources));
        } else {
            List<KiePMMLModel> toAdd = getKiePMMLModelsLoadedFromResourcesWithConfigurations(kbuilderImpl, resources);
            if (toAdd.isEmpty()) {
                toAdd = getKiePMMLModelsCompiledFromResourcesWithConfigurations(kbuilderImpl, resources);
            }
            addModels(kbuilderImpl, toAdd);
        }
    }

    @Override
    public void addResource(Object kbuilder, Resource resource, ResourceType type, ResourceConfiguration configuration) {
        logger.warn("invoked legacy addResource (no control on the order of the assembler compilation): {}", resource.getSourcePath());
        KnowledgeBuilderImpl kbuilderImpl = (KnowledgeBuilderImpl) kbuilder;
        if (isBuildFromMaven()) {
            addModels(kbuilderImpl, getKiePMMLModelsFromResourceFromPlugin(kbuilderImpl, resource));
        } else {
            List<KiePMMLModel> toAdd = getKiePMMLModelsLoadedFromResource(kbuilderImpl, resource);
            if (toAdd.isEmpty()) {
                toAdd = getKiePMMLModelsCompiledFromResource(kbuilderImpl, resource);
            }
            addModels(kbuilderImpl, toAdd);
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
            if (kiePMMLModel instanceof HasNestedModels) {
                addModels(kbuilderImpl, ((HasNestedModels)kiePMMLModel).getNestedModels());
            }
        }
    }
}
