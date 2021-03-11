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
import java.io.FileOutputStream;
import java.io.InputStream;
import java.util.ArrayList;
import java.util.Collection;
import java.util.List;
import java.util.Map;
import java.util.UUID;

import org.drools.compiler.builder.impl.KnowledgeBuilderImpl;
import org.drools.compiler.kie.builder.impl.InternalKieModule;
import org.drools.compiler.kie.builder.impl.KieContainerImpl;
import org.drools.compiler.lang.descr.PackageDescr;
import org.drools.core.RuleBaseConfiguration;
import org.drools.core.definitions.InternalKnowledgePackage;
import org.drools.core.impl.InternalKnowledgeBase;
import org.drools.core.impl.KnowledgeBaseFactory;
import org.drools.core.impl.KnowledgeBaseImpl;
import org.drools.core.io.impl.DescrResource;
import org.drools.core.io.impl.FileSystemResource;
import org.drools.core.io.internal.InternalResource;
import org.drools.modelcompiler.ExecutableModelProject;
import org.kie.api.KieBase;
import org.kie.api.KieServices;
import org.kie.api.builder.ReleaseId;
import org.kie.api.definition.KiePackage;
import org.kie.api.io.ResourceType;
import org.kie.api.runtime.KieRuntimeFactory;
import org.kie.internal.builder.KnowledgeBuilderFactory;
import org.kie.internal.utils.KieHelper;
import org.kie.pmml.api.exceptions.ExternalException;
import org.kie.pmml.api.exceptions.KiePMMLException;
import org.kie.pmml.api.runtime.PMMLRuntime;
import org.kie.pmml.commons.model.HasNestedModels;
import org.kie.pmml.commons.model.KiePMMLModel;
import org.kie.pmml.evaluator.api.container.PMMLPackage;
import org.kie.pmml.evaluator.assembler.service.PMMLAssemblerService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import static org.apache.commons.io.FileUtils.getFile;

/**
 * <b>Factory</b> class to hide implementation details to end user
 */
public class PMMLRuntimeFactoryInternal {

    private static final Logger logger = LoggerFactory.getLogger(PMMLRuntimeFactoryInternal.class);
    private static final KieServices KIE_SERVICES = KieServices.get();

    private PMMLRuntimeFactoryInternal() {
        // Avoid instantiation
    }

    /**
     * Retrieves a <code>PMMLRuntime</code> for the given <b>PMML</b> <code>File</code>
     * @param pmmlFile
     * @return
     */
    public static PMMLRuntime getPMMLRuntime(final File pmmlFile) {
        final KieBase kieBase = createKieBase(pmmlFile);
        return getPMMLRuntime(kieBase);
    }

    /**
     * Retrieves a <code>PMMLRuntime</code> for the <b>PMML</b> <code>File</code> with the given <b>pmmlFileName</b>
     * @param pmmlFileName
     * @return
     */
    public static PMMLRuntime getPMMLRuntime(final String pmmlFileName) {
        final File pmmlFile = getPMMLFileFromClasspath(pmmlFileName);
        return getPMMLRuntime(pmmlFile);
    }

    /**
     * Retrieves a <code>PMMLRuntime</code> for the <b>PMML</b> <code>File</code> with the given <b>pmmlFileName</b>
     * inside the given <b>kieBase</b> from a <code>KieContainer</code>
     * @param kieBase
     * @param pmmlFileName
     * @param releaseId
     *
     * @return
     */
    public static PMMLRuntime getPMMLRuntime(final String kieBase, final String pmmlFileName, final ReleaseId releaseId) {
        final File pmmlFile = getPMMLFileFromKieContainerByKieBase(pmmlFileName, kieBase, releaseId);
        return getPMMLRuntime(pmmlFile, releaseId);
    }

    /**
     * Retrieves a <code>PMMLRuntime</code> for the <b>PMML</b> <code>File</code> with the given <b>pmmlFileName</b>
     * inside the <b>default kieBase</b> from a <code>KieContainer</code>
     * @param pmmlFileName
     * @param releaseId
     *
     * @return
     */
    public static PMMLRuntime getPMMLRuntime(final String pmmlFileName, final ReleaseId releaseId) {
        final File pmmlFile = getPMMLFileFromKieContainerByDefaultKieBase(pmmlFileName, releaseId);
        return getPMMLRuntime(pmmlFile, releaseId);
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
        new PMMLAssemblerService().addResourceAfterRules(kbuilderImpl, fileSystemResource, ResourceType.PMML, null);
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

    protected static KiePackage getKiePackageByFullClassName(String fullClassName, KieBase kieBase) {
        return kieBase.getKiePackages().stream()
                .filter(kpkg -> {
                    PMMLPackage pmmlPackage =
                            (PMMLPackage) ((InternalKnowledgePackage) kpkg).getResourceTypePackages().get(ResourceType.PMML);
                    return pmmlPackage != null && pmmlPackage.getModelByFullClassName(fullClassName) != null;
                })
                .findFirst()
                .orElseThrow(() -> new KiePMMLException("Failed to find model " + fullClassName));
    }

    /**
     * Load a <code>File</code> with the given <b>pmmlFileName</b> from the
     * current <code>Classloader</code>
     * @param pmmlFileName
     * @return
     */
    private static File getPMMLFileFromClasspath(final String pmmlFileName) {
        return getFile(pmmlFileName);
    }

    /**
     * Load a <code>File</code> with the given <b>pmmlFileName</b> from the <code>kjar</code> contained in the
     * <code>KieContainer</code> with the given <code>ReleaseId</code>
     * @param pmmlFileName
     * @param kieBase the name of the Kiebase configured inside the <b>kmodule.xml</b> of the loaded <b>kjar</b>
     * @param releaseId
     * @return
     */
    private static File getPMMLFileFromKieContainerByKieBase(final String pmmlFileName, final String kieBase,
                                                      final ReleaseId releaseId) {
        KieContainerImpl kieContainer = (KieContainerImpl) KIE_SERVICES.newKieContainer(releaseId);
        InternalResource internalResource = ((InternalKieModule) (kieContainer)
                .getKieModuleForKBase(kieBase))
                .getResource(pmmlFileName);
        try (InputStream inputStream = internalResource.getInputStream()) {
            return getPMMLFile(pmmlFileName, inputStream);
        } catch (Exception e) {
            throw new ExternalException(e);
        }
    }

    /**
     * Load a <code>File</code> with the given <b>pmmlFileName</b> from the <code>kjar</code> contained in the
     * <code>KieContainer</code> with the given <code>ReleaseId</code>
     * It will use the <b>default</b> Kiebase defined inside the <b>kmodule.xml</b> of the loaded <b>kjar</b>
     * @param pmmlFileName
     * @param releaseId
     * @return
     */
    private static File getPMMLFileFromKieContainerByDefaultKieBase(final String pmmlFileName, final ReleaseId releaseId) {
        KieContainerImpl kieContainer = (KieContainerImpl) KIE_SERVICES.newKieContainer(releaseId);
        String defaultKieBase = kieContainer.getKieProject().getDefaultKieBaseModel().getName();
        return getPMMLFileFromKieContainerByKieBase(pmmlFileName, defaultKieBase, releaseId);
    }

    /**
     * Load a <code>File</code> with the given <b>fullFileName</b> from the given
     * <code>InputStream</code>
     * @param fileName <b>full path</b> of file to load
     * @param inputStream
     * @return
     */
    private static File getPMMLFile(String fileName, InputStream inputStream) {
        FileOutputStream outputStream = null;
        try {
            File toReturn = File.createTempFile(fileName, null);
            outputStream = new FileOutputStream(toReturn);
            byte[] byteArray = new byte[1024];
            int i;
            while ((i = inputStream.read(byteArray)) > 0) {
                outputStream.write(byteArray, 0, i);
            }
            return toReturn;
        } catch (Exception e) {
            throw new ExternalException(e);
        } finally {
            try {
                if (outputStream != null) {
                    outputStream.close();
                }
            } catch (Exception e) {
                logger.warn("Failed to close outputStream", e);
            }
        }
    }

}
