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

import static org.kie.api.pmml.PMMLConstants.NEW;
import static org.kie.internal.pmml.PMMLImplementationsUtil.isjPMMLAvailableToClassLoader;
import static org.kie.internal.pmml.PMMLImplementationsUtil.toEnable;
import static org.kie.pmml.commons.utils.KiePMMLModelUtils.getSanitizedClassName;
import static org.kie.pmml.commons.utils.KiePMMLModelUtils.getSanitizedPackageName;
import static org.kie.pmml.evaluator.assembler.service.PMMLCompilerService.getKiePMMLModelsCompiledFromResource;
import static org.kie.pmml.evaluator.assembler.service.PMMLCompilerService.getKiePMMLModelsCompiledFromResourcesWithConfigurations;
import static org.kie.pmml.evaluator.assembler.service.PMMLCompilerService.getKiePMMLModelsFromResourceWithSources;
import static org.kie.pmml.evaluator.assembler.service.PMMLCompilerService.getKiePMMLModelsFromResourcesWithConfigurationsWithSources;
import static org.kie.pmml.evaluator.assembler.service.PMMLLoaderService.getKiePMMLModelsLoadedFromResource;
import static org.kie.pmml.evaluator.assembler.service.PMMLLoaderService.getKiePMMLModelsLoadedFromResourcesWithConfigurations;

public class PMMLAssemblerService implements KieAssemblerService {

    public static final String PMML_COMPILER_CACHE_KEY = "PMML_COMPILER_CACHE_KEY";
    private static final Logger logger = LoggerFactory.getLogger(PMMLAssemblerService.class);

    private static final String FOLDER_SEPARATOR = "/";

    private static final String WINDOWS_FOLDER_SEPARATOR = "\\";

    private static boolean isBuildFromMaven() {
        final String property = System.getProperty("kie-maven-plugin-launcher", "false");
        return property.equals("true");
    }

    /**
     * Returns an array where the first item is the <b>factory class</b> name and the second item is the <b>package</b> name,
     * built starting from the given <code>Resource</code>
     * @param resource
     * @return
     */
    public static String[] getFactoryClassNamePackageName(Resource resource) {
        String sourcePath = resource.getSourcePath();
        if (sourcePath == null || sourcePath.isEmpty()) {
            throw new IllegalArgumentException("Missing required sourcePath in resource " + resource + " -> " + resource.getClass().getName());
        }
        return getFactoryClassNamePackageName(sourcePath);
    }

    /**
     * Returns an array where the first item is the <b>factory class</b> name and the second item is the <b>package</b> name,
     * built starting from the given <b>sourcePath</b> <code>String</code>
     * @param sourcePath
     * @return
     */
    static String[] getFactoryClassNamePackageName(String sourcePath) {
        sourcePath = sourcePath.replace(WINDOWS_FOLDER_SEPARATOR, FOLDER_SEPARATOR);
        String fileName = sourcePath.substring(sourcePath.lastIndexOf(FOLDER_SEPARATOR) + 1);
        fileName = fileName.replace(".pmml", "");
        String packageName = getSanitizedPackageName(fileName);
        String factoryClassName = getSanitizedClassName(fileName + "Factory");
        return new String[]{factoryClassName, packageName};
    }

    @Override
    public ResourceType getResourceType() {
        return NEW.equals(toEnable(Thread.currentThread().getContextClassLoader())) ? ResourceType.PMML : ResourceType.NOOP;
    }

    @Override
    public void addResourcesAfterRules(Object kbuilder, Collection<ResourceWithConfiguration> resources, ResourceType type) {
        KnowledgeBuilderImpl kbuilderImpl = (KnowledgeBuilderImpl) kbuilder;
        if (isjPMMLAvailableToClassLoader(kbuilderImpl.getRootClassLoader())) {
            return;
        }
        if (isBuildFromMaven()) {
            addModels(kbuilderImpl, getKiePMMLModelsFromResourcesWithConfigurationsWithSources(kbuilderImpl, resources));
        } else {
            List<KiePMMLModel> toAdd = getKiePMMLModelsLoadedFromResourcesWithConfigurations(kbuilderImpl, resources);
            if (toAdd.isEmpty()) {
                toAdd = getKiePMMLModelsCompiledFromResourcesWithConfigurations(kbuilderImpl, resources);
            }
            addModels(kbuilderImpl, toAdd);
        }
    }

    @Override
    public void addResourceAfterRules(Object kbuilder, Resource resource, ResourceType type, ResourceConfiguration configuration) {
        logger.warn("invoked legacy addResourceAfterRules (no control on the order of the assembler compilation): {}", resource.getSourcePath());
        KnowledgeBuilderImpl kbuilderImpl = (KnowledgeBuilderImpl) kbuilder;
        if (isjPMMLAvailableToClassLoader(kbuilderImpl.getRootClassLoader())) {
            return;
        }
        if (isBuildFromMaven()) {
            addModels(kbuilderImpl, getKiePMMLModelsFromResourceWithSources(kbuilderImpl, resource));
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
                addModels(kbuilderImpl, ((HasNestedModels) kiePMMLModel).getNestedModels());
            }
        }
    }
}
