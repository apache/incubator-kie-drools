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

import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Collection;
import java.util.List;
import java.util.stream.Collectors;

import org.drools.compiler.builder.impl.KnowledgeBuilderImpl;
import org.kie.api.io.Resource;
import org.kie.api.io.ResourceWithConfiguration;
import org.kie.pmml.api.exceptions.ExternalException;
import org.kie.pmml.api.exceptions.KiePMMLException;
import org.kie.pmml.commons.HasRule;
import org.kie.pmml.commons.model.HasNestedModels;
import org.kie.pmml.commons.model.HasSourcesMap;
import org.kie.pmml.commons.model.KiePMMLModel;
import org.kie.pmml.compiler.executor.PMMLCompiler;
import org.kie.pmml.compiler.executor.PMMLCompilerImpl;
import org.kie.pmml.evaluator.assembler.factories.PMMLRuleMapperFactory;
import org.kie.pmml.evaluator.assembler.factories.PMMLRuleMappersFactory;
import org.kie.pmml.evaluator.assembler.implementations.HasKnowledgeBuilderImpl;

import static org.kie.pmml.evaluator.assembler.factories.PMMLRuleMapperFactory.KIE_PMML_RULE_MAPPER_CLASS_NAME;
import static org.kie.pmml.evaluator.assembler.factories.PMMLRuleMappersFactory.KIE_PMML_RULE_MAPPERS_CLASS_NAME;
import static org.kie.pmml.evaluator.assembler.service.PMMLAssemblerService.PMML_COMPILER_CACHE_KEY;
import static org.kie.pmml.evaluator.assembler.service.PMMLAssemblerService.getFactoryClassNamePackageName;

/**
 * Class meant to <b>compile</b> resources
 */
public class PMMLCompilerService {

    static final String RULES_FILE_NAME = "Rules";

    private PMMLCompilerService() {
        // Avoid instantiation
    }

    /**
     * @param kbuilderImpl
     * @param resourceWithConfigurations
     * @return
     * @throws KiePMMLException if any <code>KiePMMLInternalException</code> has been thrown during execution
     * @throws ExternalException if any other kind of <code>Exception</code> has been thrown during execution
     */
    public static List<KiePMMLModel> getKiePMMLModelsFromResourcesWithConfigurationsWithSources(KnowledgeBuilderImpl kbuilderImpl, Collection<ResourceWithConfiguration> resourceWithConfigurations) {
        return resourceWithConfigurations.stream()
                .map(ResourceWithConfiguration::getResource)
                .flatMap(resource -> getKiePMMLModelsFromResourceWithSources(kbuilderImpl, resource).stream())
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
    public static List<KiePMMLModel> getKiePMMLModelsCompiledFromResource(KnowledgeBuilderImpl kbuilderImpl,
                                                                          Resource resource) {
        PMMLCompiler pmmlCompiler = kbuilderImpl.getCachedOrCreate(PMML_COMPILER_CACHE_KEY,
                                                                   PMMLCompilerService::getCompiler);
        try {
            String packageName = getFactoryClassNamePackageName(resource)[1];
            return pmmlCompiler.getKiePMMLModels(packageName, resource.getInputStream(),
                                                 getFileName(resource.getSourcePath()),
                                                 new HasKnowledgeBuilderImpl(kbuilderImpl));
        } catch (IOException e) {
            throw new ExternalException("ExternalException", e);
        }
    }

    /**
     * @param kbuilderImpl
     * @param resource
     * @return
     */
    public static List<KiePMMLModel> getKiePMMLModelsFromResourceWithSources(KnowledgeBuilderImpl kbuilderImpl,
                                                                             Resource resource) {
        PMMLCompiler pmmlCompiler = kbuilderImpl.getCachedOrCreate(PMML_COMPILER_CACHE_KEY,
                                                                   PMMLCompilerService::getCompiler);
        String[] classNamePackageName = getFactoryClassNamePackageName(resource);
        String factoryClassName = classNamePackageName[0];
        String packageName = classNamePackageName[1];
        try {
            final List<KiePMMLModel> toReturn = pmmlCompiler.getKiePMMLModelsWithSources(factoryClassName, packageName,
                                                                                         resource.getInputStream(),
                                                                                         getFileName(resource.getSourcePath()),
                                                                                         new HasKnowledgeBuilderImpl(kbuilderImpl));
            populateWithPMMLRuleMappers(toReturn, resource);
            return toReturn;
        } catch (IOException e) {
            throw new ExternalException("ExternalException", e);
        }
    }

    static void populateWithPMMLRuleMappers(final  List<KiePMMLModel> toReturn, final Resource resource) {
        for (KiePMMLModel kiePMMLModel : toReturn) {
            final List<String> generatedRuleMappers = new ArrayList<>();
            addPMMLRuleMapper(kiePMMLModel, generatedRuleMappers, resource.getSourcePath());
            addPMMLRuleMappers(kiePMMLModel, generatedRuleMappers, resource.getSourcePath());
        }
    }

    static void addPMMLRuleMapper(final KiePMMLModel kiePMMLModel, final List<String> generatedRuleMappers,
                                  final String sourcePath) {
        if (!(kiePMMLModel instanceof HasSourcesMap)) {
            String errorMessage = String.format("Expecting HasSourcesMap instance, retrieved %s inside %s",
                                                kiePMMLModel.getClass().getName(),
                                                sourcePath);
            throw new KiePMMLException(errorMessage);
        }
        if (kiePMMLModel instanceof HasRule) {
            String pkgUUID = ((HasRule)kiePMMLModel).getPkgUUID();
            String rulesFileName = kiePMMLModel.getKModulePackageName() + "." + RULES_FILE_NAME + pkgUUID;
            String pmmlRuleMapper = kiePMMLModel.getKModulePackageName() + "." + KIE_PMML_RULE_MAPPER_CLASS_NAME;
            String ruleMapperSource = PMMLRuleMapperFactory.getPMMLRuleMapperSource(rulesFileName);
            ((HasRule) kiePMMLModel).addSourceMap(pmmlRuleMapper, ruleMapperSource);
            generatedRuleMappers.add(pmmlRuleMapper);
        }
        if (kiePMMLModel instanceof HasNestedModels) {
            for (KiePMMLModel nestedModel : ((HasNestedModels) kiePMMLModel).getNestedModels()) {
                addPMMLRuleMapper(nestedModel, generatedRuleMappers, sourcePath);
            }
        }
    }

    static void addPMMLRuleMappers(final KiePMMLModel kiePMMLModel, final List<String> generatedRuleMappers,
                                   final String sourcePath) {
        if (!(kiePMMLModel instanceof HasSourcesMap)) {
            String errorMessage = String.format("Expecting HasSourcesMap instance, retrieved %s inside %s",
                                                kiePMMLModel.getClass().getName(),
                                                sourcePath);
            throw new KiePMMLException(errorMessage);
        }
        if (generatedRuleMappers.isEmpty()) {
            return;
        }
        String predictionRuleMapper = kiePMMLModel.getKModulePackageName() + "." + KIE_PMML_RULE_MAPPERS_CLASS_NAME;
        String ruleMapperSource =
                PMMLRuleMappersFactory.getPMMLRuleMappersSource(kiePMMLModel.getKModulePackageName(),
                                                                generatedRuleMappers);
        ((HasSourcesMap) kiePMMLModel).addSourceMap(predictionRuleMapper, ruleMapperSource);
    }

    static PMMLCompiler getCompiler() {
        // to retrieve model implementations
        return new PMMLCompilerImpl();
    }

    static String getFileName(final String fullPath) {
        String toReturn = fullPath;
        if (fullPath.contains(File.separator)) {
            toReturn = fullPath.substring(fullPath.lastIndexOf(File.separator) + 1);
        } else if (fullPath.contains("/")) {
            toReturn = fullPath.substring(fullPath.lastIndexOf('/') + 1);
        }
        return toReturn;
    }
}
