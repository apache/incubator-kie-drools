/*
 * Copyright 2020 Red Hat, Inc. and/or its affiliates.
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *       http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package org.kie.kogito.pmml;

import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.function.BiFunction;
import java.util.function.Function;
import java.util.stream.Collectors;
import java.util.stream.Stream;

import org.drools.compiler.builder.impl.KnowledgeBuilderImpl;
import org.drools.compiler.compiler.PackageRegistry;
import org.drools.compiler.lang.descr.PackageDescr;
import org.drools.core.definitions.InternalKnowledgePackage;
import org.drools.core.impl.KnowledgeBaseImpl;
import org.drools.model.Model;
import org.drools.modelcompiler.builder.KieBaseBuilder;
import org.kie.api.KieBase;
import org.kie.api.io.Resource;
import org.kie.api.io.ResourceType;
import org.kie.api.runtime.KieRuntimeFactory;
import org.kie.pmml.api.exceptions.KiePMMLException;
import org.kie.pmml.commons.model.HasNestedModels;
import org.kie.pmml.commons.model.KiePMMLModel;
import org.kie.pmml.compiler.commons.factories.KiePMMLModelFactory;
import org.kie.pmml.evaluator.api.container.PMMLPackage;
import org.kie.pmml.evaluator.assembler.container.PMMLPackageImpl;
import org.kie.pmml.evaluator.assembler.rulemapping.PMMLRuleMapper;
import org.kie.pmml.evaluator.assembler.service.PMMLCompilerService;
import org.kie.pmml.evaluator.assembler.service.PMMLLoaderService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * Utility class to replace the <b>Assembler</b> mechanism where this is not available
 */
public class KieRuntimeFactoryBuilder {

    private static final Logger logger = LoggerFactory.getLogger(KieRuntimeFactoryBuilder.class);

    private KieRuntimeFactoryBuilder() {
        // Avoid instantiation
    }

    public static Map<KieBase, KieRuntimeFactory> fromResources(final Stream<Resource> resources) {
        return commonFromResources(resources, PMMLLoaderService::getKiePMMLModelsLoadedFromResource, KieRuntimeFactoryBuilder::createPopulatedKnowledgeBuilderImpl);
    }

    public static Map<KieBase, KieRuntimeFactory> fromResourcesWithInMemoryCompilation(final Stream<Resource> resources) {
        return commonFromResources(resources, PMMLCompilerService::getKiePMMLModelsCompiledFromResource, KieRuntimeFactoryBuilder::createEmptyKnowledgeBuilderImpl);
    }

    private static Map<KieBase, KieRuntimeFactory> commonFromResources(
            final Stream<Resource> resources,
            final BiFunction<KnowledgeBuilderImpl, Resource, List<KiePMMLModel>> modelProducer,
            final Function<Resource, KnowledgeBuilderImpl> kbuilderImplProvider) {
        final Map<KieBase, KieRuntimeFactory> toReturn = new HashMap<>();
        resources.forEach(resource -> {
            final KnowledgeBuilderImpl kbuilderImpl = kbuilderImplProvider.apply(resource);
            List<KiePMMLModel> toAdd = modelProducer.apply(kbuilderImpl, resource);
            if (toAdd.isEmpty()) {
                throw new KiePMMLException("Failed to retrieve compiled models");
            }
            addModels(kbuilderImpl, toAdd);
            KieBase kieBase = kbuilderImpl.getKnowledgeBase();
            toReturn.put(kieBase, KieRuntimeFactory.of(kieBase));
        });
        return toReturn;
    }

    private static void addModels(final KnowledgeBuilderImpl kbuilderImpl, final List<KiePMMLModel> toAdd) {
        for (KiePMMLModel kiePMMLModel : toAdd) {
            PackageDescr pkgDescr = new PackageDescr(kiePMMLModel.getKModulePackageName());
            PackageRegistry pkgReg = kbuilderImpl.getOrCreatePackageRegistry(pkgDescr);
            InternalKnowledgePackage kpkgs = pkgReg.getPackage();
            PMMLPackage pmmlPkg =
                    kpkgs.getResourceTypePackages().computeIfAbsent(
                            ResourceType.PMML,
                            rtp -> new PMMLPackageImpl());
            pmmlPkg.addAll(Collections.singletonList(kiePMMLModel));
            if (kiePMMLModel instanceof HasNestedModels) {
                addModels(kbuilderImpl, ((HasNestedModels) kiePMMLModel).getNestedModels());
            }
        }
    }

    private static KnowledgeBuilderImpl createEmptyKnowledgeBuilderImpl(final Resource resource) {
        KnowledgeBaseImpl defaultKnowledgeBase = new KnowledgeBaseImpl("PMML", null);
        return new KnowledgeBuilderImpl(defaultKnowledgeBase);
    }

    private static KnowledgeBuilderImpl createPopulatedKnowledgeBuilderImpl(final Resource resource) {
        KnowledgeBuilderImpl toReturn = createEmptyKnowledgeBuilderImpl(resource);
        List<PMMLRuleMapper> pmmlRuleMappers = loadPMMLRuleMappers(toReturn, resource);
        if (!pmmlRuleMappers.isEmpty()) {
            List<Model> models =
                    pmmlRuleMappers.stream()
                            .map(PMMLRuleMapper::getModel)
                            .collect(Collectors.toList());
            toReturn = new KnowledgeBuilderImpl(KieBaseBuilder.createKieBaseFromModel(models));
        }
        return toReturn;
    }

    private static List<PMMLRuleMapper> loadPMMLRuleMappers(final KnowledgeBuilderImpl kbuilderImpl,
            final Resource resource) {
        try {
            final KiePMMLModelFactory kiePMMLModelFactory = PMMLLoaderService.loadKiePMMLModelFactory(kbuilderImpl, resource);
            return PMMLLoaderService.getPMMLRuleMappers(kbuilderImpl, kiePMMLModelFactory);
        } catch (Exception e) {
            throw new KiePMMLException("Failed to retrieve RuleMappers for " + resource.getSourcePath(), e);
        }
    }

}