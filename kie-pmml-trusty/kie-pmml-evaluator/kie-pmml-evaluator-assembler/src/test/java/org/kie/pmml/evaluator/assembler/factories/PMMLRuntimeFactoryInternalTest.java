/*
 * Copyright 2021 Red Hat, Inc. and/or its affiliates.
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
import java.util.Collections;
import java.util.List;
import java.util.stream.Collectors;

import org.drools.compiler.builder.impl.KnowledgeBuilderImpl;
import org.drools.compiler.kproject.ReleaseIdImpl;
import org.drools.compiler.lang.descr.PackageDescr;
import org.drools.core.definitions.InternalKnowledgePackage;
import org.drools.core.definitions.impl.KnowledgePackageImpl;
import org.drools.core.impl.KnowledgeBaseImpl;
import org.drools.core.io.impl.DescrResource;
import org.drools.modelcompiler.ExecutableModelProject;
import org.junit.Test;
import org.kie.api.KieBase;
import org.kie.api.builder.ReleaseId;
import org.kie.api.definition.KiePackage;
import org.kie.api.internal.io.ResourceTypePackage;
import org.kie.api.io.ResourceType;
import org.kie.internal.utils.KieHelper;
import org.kie.pmml.api.exceptions.KiePMMLException;
import org.kie.pmml.api.runtime.PMMLRuntime;
import org.kie.pmml.commons.model.HasNestedModels;
import org.kie.pmml.commons.model.KiePMMLModel;
import org.kie.pmml.commons.testingutility.KiePMMLTestingModel;
import org.kie.pmml.evaluator.api.container.PMMLPackage;
import org.kie.pmml.evaluator.assembler.container.PMMLPackageImpl;
import org.kie.pmml.evaluator.core.service.PMMLRuntimeInternalImpl;

import static org.assertj.core.api.Assertions.assertThat;
import static org.kie.test.util.filesystem.FileUtils.getFile;

public class PMMLRuntimeFactoryInternalTest {

    @Test
    public void getPMMLRuntimeFromFile() {
        File pmmlFile = getFile("MissingDataRegression.pmml");
        PMMLRuntime retrieved = PMMLRuntimeFactoryInternal.getPMMLRuntime(pmmlFile);
        commonValidatePMMLRuntime(retrieved);
    }

    @Test
    public void testGetPMMLRuntimeFromFileAndReleaseId() {
        File pmmlFile = getFile("MiningModel_Mixed.pmml");
        ReleaseId releaseId = new ReleaseIdImpl("org.dummy:dummy-artifact:1-0");
        PMMLRuntime retrieved = PMMLRuntimeFactoryInternal.getPMMLRuntime(pmmlFile, releaseId);
        commonValidatePMMLRuntime(retrieved);
    }

    @Test
    public void createKieBaseFromFile() {
        File pmmlFile = getFile("MissingDataRegression.pmml");
        KieBase retrieved = PMMLRuntimeFactoryInternal.createKieBase(pmmlFile);
        assertThat(retrieved).isNotNull();
        assertThat(retrieved.getKiePackages()).isEmpty();
    }

    @Test
    public void createKieBaseFromKnowledgeBuilder() {
        KnowledgeBuilderImpl knowledgeBuilder = new KnowledgeBuilderImpl();
        knowledgeBuilder.addPackage(new KnowledgePackageImpl("namespace_1"));
        knowledgeBuilder.addPackage(new KnowledgePackageImpl("namespace_2"));
        PMMLPackage pmmlPkg = new PMMLPackageImpl();
        pmmlPkg.addAll(Collections.singleton(new KiePMMLTestingModel("FAKE", Collections.emptyList())));
        KnowledgePackageImpl pmmlKnowledgePackage = new KnowledgePackageImpl("pmmled_package");
        pmmlKnowledgePackage.getResourceTypePackages().put(ResourceType.PMML, pmmlPkg);
        KieBase retrieved = PMMLRuntimeFactoryInternal.createKieBase(knowledgeBuilder);
        assertThat(retrieved).isNotNull();
        assertThat(retrieved.getKiePackages()).isNotEmpty();
        assertThat(retrieved.getKiePackages()).hasSameSizeAs(knowledgeBuilder.getKnowledgePackages());
        knowledgeBuilder.getKnowledgePackages()
                .forEach(kBuilderPackage -> {
                    assertThat(retrieved.getKiePackage(kBuilderPackage.getName())).isNotNull();
                    ResourceTypePackage knowledgeBuilderResourceTypePackage= ((InternalKnowledgePackage) kBuilderPackage).getResourceTypePackages().get(ResourceType.PMML);
                    if (((InternalKnowledgePackage) kBuilderPackage).getResourceTypePackages().get(ResourceType.PMML) != null) {
                        InternalKnowledgePackage retrievedKiePackage = (InternalKnowledgePackage) retrieved.getKiePackage(kBuilderPackage.getName());
                        ResourceTypePackage retrievedResourceTypePackage=  retrievedKiePackage.getResourceTypePackages().get(ResourceType.PMML);
                        assertThat(retrievedKiePackage.getResourceTypePackages().get(ResourceType.PMML)).isNotNull();
                        assertThat(retrievedResourceTypePackage).isEqualTo(knowledgeBuilderResourceTypePackage);
                    }
                });
    }

    @Test
    public void getPMMLRuntimeFromFileAndKnowledgeBuilder() {
        File pmmlFile = getFile("MiningModel_Mixed.pmml");
        KnowledgeBuilderImpl knowledgeBuilder = new KnowledgeBuilderImpl();
        PMMLRuntime retrieved = PMMLRuntimeFactoryInternal.getPMMLRuntime(pmmlFile, knowledgeBuilder);
        commonValidatePMMLRuntime(retrieved);
    }

    @Test
    public void createDescrResource() {
        PackageDescr packageDescr = new PackageDescr();
        DescrResource retrieved = PMMLRuntimeFactoryInternal.createDescrResource(packageDescr);
        assertThat(retrieved).isNotNull();
        assertThat(retrieved.getDescr()).isEqualTo(packageDescr);
        assertThat(retrieved.hasURL()).isFalse();
        String retrievedSourcePath = retrieved.getSourcePath();
        assertThat(retrievedSourcePath).startsWith("src/main/resources/file_");
        assertThat(retrievedSourcePath).endsWith(".descr");
    }

    @Test
    public void populateNestedKiePackageList() {
        // Setup kiebase
        KiePMMLModel kiePMMLModel = getKiePMMLModelWithNested("FAKE");
        KnowledgePackageImpl pmmlKnowledgePackage = getKnowledgePackageWithPMMLResourceType(kiePMMLModel);
        List<KiePackage> kiePackages =  ((HasNestedModels)kiePMMLModel)
                .getNestedModels()
                .stream()
                .map(this::getKnowledgePackageWithPMMLResourceType)
                .collect(Collectors.toList());
        KnowledgeBaseImpl kieBase = (KnowledgeBaseImpl) new KieHelper().build(ExecutableModelProject.class);
        kieBase.addPackage(pmmlKnowledgePackage);
        kieBase.addPackages(kiePackages);
        // Actual test
        final List<KiePackage> toPopulate = new ArrayList<>();
        PMMLRuntimeFactoryInternal
                .populateNestedKiePackageList(Collections.singleton(kiePMMLModel),
                                              toPopulate,
                                              kieBase);
        assertThat(toPopulate).isNotEmpty();
        assertThat(toPopulate).hasSameSizeAs(kiePackages);
    }

    @Test
    public void getKiePackageByFullClassNamePresent() {
        // Setup kiebase
        KiePMMLModel kiePMMLModel = new KiePMMLModelA("FAKE");
        KnowledgePackageImpl pmmlKnowledgePackage = getKnowledgePackageWithPMMLResourceType(kiePMMLModel);
        KnowledgeBaseImpl kieBase = (KnowledgeBaseImpl) new KieHelper().build(ExecutableModelProject.class);
        kieBase.addPackage(pmmlKnowledgePackage);
        // Actual test
        assertThat(PMMLRuntimeFactoryInternal.getKiePackageByFullClassName(kiePMMLModel.getClass().getName(), kieBase)).isNotNull();
    }

    @Test(expected = KiePMMLException.class)
    public void getKiePackageByFullClassNameNotPresent() {
        // Setup kiebase
        KnowledgePackageImpl pmmlKnowledgePackage = getKnowledgePackageWithPMMLResourceType(new KiePMMLModelA("FAKE"));
        KnowledgeBaseImpl kieBase = (KnowledgeBaseImpl) new KieHelper().build(ExecutableModelProject.class);
        kieBase.addPackage(pmmlKnowledgePackage);
        // Actual test
        PMMLRuntimeFactoryInternal.getKiePackageByFullClassName(KiePMMLModel.class.getName(), kieBase);
    }

    private void commonValidatePMMLRuntime(PMMLRuntime toValidate) {
        assertThat(toValidate).isNotNull();
        assertThat(toValidate).isInstanceOf(PMMLRuntimeInternalImpl.class);
        assertThat(((PMMLRuntimeInternalImpl)toValidate).getKnowledgeBase()).isNotNull();
    }

    private KnowledgePackageImpl getKnowledgePackageWithPMMLResourceType(KiePMMLModel kiePMMLModel) {
        PMMLPackage pmmlPkg = new PMMLPackageImpl();
        pmmlPkg.addAll(Collections.singleton(kiePMMLModel));
        KnowledgePackageImpl pmmlKnowledgePackage = new KnowledgePackageImpl(kiePMMLModel.getKModulePackageName());
        pmmlKnowledgePackage.getResourceTypePackages().put(ResourceType.PMML, pmmlPkg);
        return pmmlKnowledgePackage;
    }

    private KiePMMLModel getKiePMMLModelWithNested(String modelName) {
        List<KiePMMLModel> kiePmmlModels = new ArrayList<>();
        kiePmmlModels.add(new KiePMMLModelA("FAKE-A"));
        kiePmmlModels.add(new KiePMMLModelB("FAKE-B"));
        return new KiePMMLModelWithNested(modelName, kiePmmlModels);
    }

    private class KiePMMLModelA extends KiePMMLTestingModel {

        private static final long serialVersionUID = -8174670245229417048L;

        public KiePMMLModelA(String name) {
            super(name, Collections.emptyList());
        }

    }

    private class KiePMMLModelB extends KiePMMLTestingModel {

        private static final long serialVersionUID = 8521110750870376450L;

        public KiePMMLModelB(String name) {
            super(name, Collections.emptyList());
        }

    }

    private class KiePMMLModelWithNested extends KiePMMLTestingModel implements HasNestedModels {

        private static final long serialVersionUID = -3005462259673834598L;
        private final List<KiePMMLModel> nestedModels;

        public KiePMMLModelWithNested(String modelName, List<KiePMMLModel> nestedModels) {
            super(modelName, Collections.emptyList());
            this.nestedModels = nestedModels;
        }

        @Override
        public List<KiePMMLModel> getNestedModels() {
            return nestedModels;
        }

    }
}