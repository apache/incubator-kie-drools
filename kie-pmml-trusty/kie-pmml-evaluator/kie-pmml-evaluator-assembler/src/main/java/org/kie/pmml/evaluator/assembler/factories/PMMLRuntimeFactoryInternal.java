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
package org.kie.pmml.evaluator.assembler.factories;

import java.io.File;
import java.util.ArrayList;
import java.util.Collection;
import java.util.List;
import java.util.Map;
import java.util.UUID;

import org.drools.compiler.builder.impl.KnowledgeBuilderImpl;
import org.drools.compiler.lang.descr.PackageDescr;
import org.drools.core.RuleBaseConfiguration;
import org.drools.core.definitions.InternalKnowledgePackage;
import org.drools.core.impl.InternalKnowledgeBase;
import org.drools.core.impl.KnowledgeBaseFactory;
import org.drools.core.impl.KnowledgeBaseImpl;
import org.drools.core.io.impl.DescrResource;
import org.drools.core.io.impl.FileSystemResource;
import org.drools.modelcompiler.ExecutableModelProject;
import org.kie.api.KieBase;
import org.kie.api.builder.ReleaseId;
import org.kie.api.definition.KiePackage;
import org.kie.api.io.ResourceType;
import org.kie.api.runtime.KieRuntimeFactory;
import org.kie.internal.builder.KnowledgeBuilderFactory;
import org.kie.internal.utils.KieHelper;
import org.kie.pmml.api.exceptions.KiePMMLException;
import org.kie.pmml.api.runtime.PMMLRuntime;
import org.kie.pmml.commons.model.HasNestedModels;
import org.kie.pmml.commons.model.KiePMMLModel;
import org.kie.pmml.evaluator.api.container.PMMLPackage;
import org.kie.pmml.evaluator.assembler.service.PMMLAssemblerService;

/**
 * <b>Factory</b> class to hide implementation details to end user
 */
public class PMMLRuntimeFactoryInternal {

    private PMMLRuntimeFactoryInternal() {
        // Avoid instantiation
    }

    public static PMMLRuntime getPMMLRuntime(final File pmmlFile) {
        KieBase kieBase = createKieBase(pmmlFile);
        return getPMMLRuntime(kieBase);
    }

    public static PMMLRuntime getPMMLRuntime(File pmmlFile, ReleaseId releaseId) {
        KnowledgeBuilderImpl kbuilderImpl = (KnowledgeBuilderImpl) KnowledgeBuilderFactory.newKnowledgeBuilder();
        kbuilderImpl.setReleaseId(releaseId);
        return getPMMLRuntime(pmmlFile, kbuilderImpl);
    }

    public static PMMLRuntime getPMMLRuntime(KieBase kieBase) {
        final KieRuntimeFactory kieRuntimeFactory = KieRuntimeFactory.of(kieBase);
        return kieRuntimeFactory.get(PMMLRuntime.class);
    }

    public static PMMLRuntime getPMMLRuntime(String pmmlFileName, String pmmlModelName, KieBase kieBase) {
        RuleBaseConfiguration ruleBaseConfiguration =
                new RuleBaseConfiguration(((InternalKnowledgeBase) kieBase).getRootClassLoader());
        KnowledgeBaseImpl kieBaseNew = (KnowledgeBaseImpl) KnowledgeBaseFactory.newKnowledgeBase(ruleBaseConfiguration);
        KiePackage kiePackage = getKiePackageByModelName(pmmlModelName, kieBase);
        kieBaseNew.addPackage(kiePackage);
        List<KiePackage> nestedKiePackages = getNestedKiePackages((InternalKnowledgePackage) kiePackage, kieBase);
        if (!nestedKiePackages.isEmpty()) {
            kieBaseNew.addPackages(nestedKiePackages);
        }
        ((InternalKnowledgePackage) kiePackage).getResourceTypePackages().get(ResourceType.PMML);

        return getPMMLRuntime(kieBaseNew);
    }

    protected static KieBase createKieBase(final File pmmlFile) {
        KieHelper kieHelper = new KieHelper();
        FileSystemResource fileSystemResource = new FileSystemResource(pmmlFile);
        kieHelper.addResource(fileSystemResource);
        return kieHelper.build(ExecutableModelProject.class);
    }

    protected static KieBase createKieBase(KnowledgeBuilderImpl kbuilderImpl) {
        KieHelper kieHelper = new KieHelper();
        kbuilderImpl.getPackageNames().stream()
                .flatMap(name -> kbuilderImpl.getPackageDescrs(name).stream())
                .forEach(pDescr -> kieHelper.addResource(createDescrResource(pDescr)));
        KieBase kieBase = kieHelper.build(ExecutableModelProject.class);
        // Temporarily because all this path has to be removed
        // Needed for non-drools models
        kbuilderImpl.getKnowledgePackages().forEach(kBuilderPackage -> {
            if (kieBase.getKiePackage(kBuilderPackage.getName()) == null) {
                ((InternalKnowledgeBase) kieBase).addPackage(kBuilderPackage);
            }
        });
        // Temporarily because all this path has to be removed
        // Basically, get PMMLPackage from kbuilderImpl' InternalKnowledgePackages and put inside kieBase' ones
        kieBase.getKiePackages()
                .forEach(kiePackage -> {
                    PMMLPackage pmmlPackage =
                            (PMMLPackage) ((InternalKnowledgePackage) kiePackage).getResourceTypePackages().get(ResourceType.PMML);
                    if (pmmlPackage == null) {
                        InternalKnowledgePackage kBuilderPackage = kbuilderImpl.getPackage(kiePackage.getName());
                        if (kBuilderPackage.getResourceTypePackages().get(ResourceType.PMML) != null) {
                            ((InternalKnowledgePackage) kiePackage).getResourceTypePackages()
                                    .put(ResourceType.PMML, kBuilderPackage.getResourceTypePackages().get(ResourceType.PMML));
                        }
                    }
                });
        return kieBase;
    }

    protected static PMMLRuntime getPMMLRuntime(File pmmlFile, KnowledgeBuilderImpl kbuilderImpl) {
        FileSystemResource fileSystemResource = new FileSystemResource(pmmlFile);
        new PMMLAssemblerService().addResource(kbuilderImpl, fileSystemResource, ResourceType.PMML, null);
        KieBase kieBase = createKieBase(kbuilderImpl);
        return getPMMLRuntime(kieBase);
    }

    protected static DescrResource createDescrResource(PackageDescr pDescr) {
        DescrResource resource = new DescrResource(pDescr);
        resource.setSourcePath("src/main/resources/file_" + UUID.randomUUID() + ".descr");
        return resource;
    }

    protected static KiePackage getKiePackageByModelName(String modelName, KieBase kieBase) {
        return kieBase.getKiePackages().stream()
                .filter(kpkg -> {
                    PMMLPackage pmmlPackage =
                            (PMMLPackage) ((InternalKnowledgePackage) kpkg).getResourceTypePackages().get(ResourceType.PMML);
                    return pmmlPackage != null && pmmlPackage.getModelByName(modelName) != null;
                })
                .findFirst()
                .orElseThrow(() -> new KiePMMLException("Failed to find model " + modelName));
    }

    protected static List<KiePackage> getNestedKiePackages(final InternalKnowledgePackage kiePackage, final KieBase kieBase) {
        PMMLPackage pmmlPackage = (PMMLPackage) kiePackage.getResourceTypePackages().get(ResourceType.PMML);
        final Map<String, KiePMMLModel> kiePmmlModels = pmmlPackage.getAllModels();
        final List<KiePackage> toReturn = new ArrayList<>();
        populateNestedKiePackageList(kiePmmlModels.values(), toReturn, kieBase);
        return toReturn;
    }

    protected static void populateNestedKiePackageList(final Collection<KiePMMLModel> kiePmmlModels,
                                              final List<KiePackage> toPopulate,
                                              final KieBase kieBase) {
        kiePmmlModels.forEach(kiePmmlModel -> {
            if (kiePmmlModel instanceof HasNestedModels) {
                List<KiePMMLModel> nestedModels = ((HasNestedModels) kiePmmlModel).getNestedModels();
                nestedModels.forEach(nestedModel -> toPopulate.add(getKiePackageByFullClassName(nestedModel.getClass().getName(), kieBase)));
                populateNestedKiePackageList(nestedModels, toPopulate, kieBase);
            }
        });
    }

    protected static  KiePackage getKiePackageByFullClassName(String fullClassName, KieBase kieBase) {
        return kieBase.getKiePackages().stream()
                .filter(kpkg -> {
                    PMMLPackage pmmlPackage =
                            (PMMLPackage) ((InternalKnowledgePackage) kpkg).getResourceTypePackages().get(ResourceType.PMML);
                    return pmmlPackage != null && pmmlPackage.getModelByFullClassName(fullClassName) != null;
                })
                .findFirst()
                .orElseThrow(() -> new KiePMMLException("Failed to find model " + fullClassName));
    }

}
