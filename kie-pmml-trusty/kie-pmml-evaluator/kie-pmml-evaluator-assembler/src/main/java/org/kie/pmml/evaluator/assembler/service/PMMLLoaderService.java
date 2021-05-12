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

import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;
import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

import org.drools.compiler.builder.impl.KnowledgeBuilderImpl;
import org.drools.core.definitions.InternalKnowledgePackage;
import org.drools.model.Model;
import org.drools.modelcompiler.builder.KieBaseBuilder;
import org.kie.api.definition.KiePackage;
import org.kie.api.io.Resource;
import org.kie.api.io.ResourceWithConfiguration;
import org.kie.pmml.api.exceptions.ExternalException;
import org.kie.pmml.api.exceptions.KiePMMLException;
import org.kie.pmml.commons.model.HasNestedModels;
import org.kie.pmml.commons.model.KiePMMLModel;
import org.kie.pmml.compiler.commons.factories.KiePMMLModelFactory;
import org.kie.pmml.evaluator.assembler.rulemapping.PMMLRuleMapper;
import org.kie.pmml.evaluator.assembler.rulemapping.PMMLRuleMappers;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import static org.kie.pmml.evaluator.assembler.factories.PMMLRuleMappersFactory.KIE_PMML_RULE_MAPPERS_CLASS_NAME;
import static org.kie.pmml.evaluator.assembler.service.PMMLAssemblerService.getFactoryClassNamePackageName;

/**
 * Class meant to <b>load</b> resources
 */
public class PMMLLoaderService {

    private static final Logger logger = LoggerFactory.getLogger(PMMLLoaderService.class);

    private PMMLLoaderService() {
        // Avoid instantiation
    }

    /**
     * @param kbuilderImpl
     * @param resourceWithConfigurations
     * @return
     * @throws KiePMMLException if any <code>KiePMMLInternalException</code> has been thrown during execution
     * @throws ExternalException if any other kind of <code>Exception</code> has been thrown during execution
     */
    public static List<KiePMMLModel> getKiePMMLModelsLoadedFromResourcesWithConfigurations(KnowledgeBuilderImpl kbuilderImpl, Collection<ResourceWithConfiguration> resourceWithConfigurations) {
        return resourceWithConfigurations.stream()
                .map(ResourceWithConfiguration::getResource)
                .flatMap(resource -> getKiePMMLModelsLoadedFromResource(kbuilderImpl, resource).stream())
                .collect(Collectors.toList());
    }

    /**
     * @param kbuilderImpl
     * @param resource
     * @return
     * @throws KiePMMLException if any <code>KiePMMLInternalException</code> has been thrown during execution
     * @throws ExternalException if any other kind of <code>Exception</code> has been thrown during execution
     */
    public static List<KiePMMLModel> getKiePMMLModelsLoadedFromResource(final KnowledgeBuilderImpl kbuilderImpl,
                                                                        final Resource resource) {
        try {
            final KiePMMLModelFactory aClass = loadKiePMMLModelFactory(kbuilderImpl,
                                                                       resource);
            return getKiePMMLModelsLoadedFromFactory(kbuilderImpl, aClass);
        } catch (ClassNotFoundException e) {
            logger.info(String.format("KiePMMLModelFactory class for %s not found in rootClassLoader, going to compile model",
                                      resource.getSourcePath()));
        } catch (Exception e) {
            throw new KiePMMLException("Exception while instantiating KiePMMLModelFactory for " + resource.getSourcePath(), e);
        }
        return Collections.emptyList();
    }

    public static KiePMMLModelFactory loadKiePMMLModelFactory(final KnowledgeBuilderImpl kbuilderImpl,
                                                              final Resource resource) throws Exception {
        String[] classNamePackageName = getFactoryClassNamePackageName(resource);
        String factoryClassName = classNamePackageName[0];
        String packageName = classNamePackageName[1];
        String fullFactoryClassName = packageName + "." + factoryClassName;
        return loadKiePMMLModelFactory(kbuilderImpl.getRootClassLoader(),
                                       fullFactoryClassName);
    }

    public static List<PMMLRuleMapper> getPMMLRuleMappers(final KnowledgeBuilderImpl kbuilderImpl,
                                                              final KiePMMLModelFactory modelFactory) {
        final List<PMMLRuleMapper> toReturn = new ArrayList<>();
        for (KiePMMLModel kiePMMLModel : modelFactory.getKiePMMLModels()) {
            toReturn.addAll(loadPMMLRuleMappers(kbuilderImpl.getRootClassLoader(), kiePMMLModel.getKModulePackageName()));
            if (kiePMMLModel instanceof HasNestedModels) {
                populatePMMLRuleMappers(kbuilderImpl, ((HasNestedModels)kiePMMLModel).getNestedModels(), toReturn);
            }
        }
        return toReturn;

    }

    static List<KiePMMLModel> getKiePMMLModelsLoadedFromFactory(final KnowledgeBuilderImpl kbuilderImpl,
                                                                final KiePMMLModelFactory kiePMMLModelFactory) {
        final List<KiePMMLModel> toReturn = kiePMMLModelFactory.getKiePMMLModels();
        final List<PMMLRuleMapper> pmmlRuleMappers = new ArrayList<>();
        populatePMMLRuleMappers(kbuilderImpl, toReturn, pmmlRuleMappers);
        loadPMMLRuleMappers(kbuilderImpl, pmmlRuleMappers);
        return toReturn;
    }

    static void populatePMMLRuleMappers(final KnowledgeBuilderImpl kbuilderImpl,
                                        final List<KiePMMLModel> kiePMMLModels,
                                        final List<PMMLRuleMapper> toPopulate) {
        for (KiePMMLModel kiePMMLModel : kiePMMLModels) {
            toPopulate.addAll(loadPMMLRuleMappers(kbuilderImpl.getRootClassLoader(), kiePMMLModel.getKModulePackageName()));
            if (kiePMMLModel instanceof HasNestedModels) {
                populatePMMLRuleMappers(kbuilderImpl, ((HasNestedModels)kiePMMLModel).getNestedModels(), toPopulate);
            }
        }
    }

    static void loadPMMLRuleMappers(final KnowledgeBuilderImpl kbuilderImpl,
                                    final List<PMMLRuleMapper> pmmlRuleMappers) {
        if (!pmmlRuleMappers.isEmpty()) {
            List<Model> models =
                    pmmlRuleMappers.stream()
                            .map(PMMLRuleMapper::getModel)
                            .collect(Collectors.toList());
            KnowledgeBuilderImpl temp = new KnowledgeBuilderImpl(KieBaseBuilder.createKieBaseFromModel(models));
            for (KiePackage kiePackage : temp.getKnowledgeBase().getKiePackages()) {
                kbuilderImpl.addPackage((InternalKnowledgePackage) kiePackage);
            }
        }
    }

    private static KiePMMLModelFactory loadKiePMMLModelFactory(ClassLoader classLoader, String fullFactoryClassName) throws Exception {
        final Class<? extends KiePMMLModelFactory> aClass =
                (Class<? extends KiePMMLModelFactory>) classLoader.loadClass(fullFactoryClassName);
        return aClass.getDeclaredConstructor().newInstance();
    }

    private static List<PMMLRuleMapper> loadPMMLRuleMappers(final ClassLoader classLoader,
                                                            final String packageName) {
        Optional<PMMLRuleMappers> pmmlRuleMappers = loadPMMLRuleMappersClass(classLoader, packageName);
        return pmmlRuleMappers.map(PMMLRuleMappers::getPMMLRuleMappers).orElse(Collections.emptyList());
    }

    private static Optional<PMMLRuleMappers> loadPMMLRuleMappersClass(final ClassLoader classLoader,
                                                                      final String packageName) {
        String fullPMMLRuleMappersClassName = packageName + "." + KIE_PMML_RULE_MAPPERS_CLASS_NAME;
        try {
            PMMLRuleMappers pmmlRuleMappers =
                    (PMMLRuleMappers) classLoader.loadClass(fullPMMLRuleMappersClassName).getDeclaredConstructor().newInstance();
            return Optional.of(pmmlRuleMappers);
        } catch (ClassNotFoundException e) {
            logger.debug("{} class not found in rootClassLoader", fullPMMLRuleMappersClassName);
            return Optional.empty();
        } catch (Exception e) {
            throw new KiePMMLException(String.format("%s class not instantiable",
                                                     fullPMMLRuleMappersClassName), e);
        }
    }
}
