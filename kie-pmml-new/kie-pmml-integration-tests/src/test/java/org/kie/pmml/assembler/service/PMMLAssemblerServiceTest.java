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

package org.kie.pmml.assembler.service;

import java.io.File;
import java.io.FileInputStream;
import java.util.Arrays;
import java.util.Collection;
import java.util.Optional;

import org.drools.compiler.builder.impl.KnowledgeBuilderImpl;
import org.drools.core.RuleBaseConfiguration;
import org.drools.core.builder.conf.impl.ResourceConfigurationImpl;
import org.drools.core.definitions.InternalKnowledgePackage;
import org.drools.core.impl.KnowledgeBaseImpl;
import org.drools.core.io.impl.InputStreamResource;
import org.junit.Before;
import org.junit.Test;
import org.kie.api.definition.KiePackage;
import org.kie.api.io.Resource;
import org.kie.api.io.ResourceType;
import org.kie.api.io.ResourceWithConfiguration;
import org.kie.internal.io.ResourceWithConfigurationImpl;
import org.kie.pmml.evaluator.api.container.PMMLPackage;
import org.kie.pmml.evaluator.assembler.service.PMMLAssemblerService;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertNull;
import static org.junit.Assert.assertTrue;
import static org.kie.pmml.commons.utils.KiePMMLModelUtils.getSanitizedPackageName;
import static org.kie.test.util.filesystem.FileUtils.getFile;

public class PMMLAssemblerServiceTest {

    private static final PMMLAssemblerService pmmlAssemblerService = new PMMLAssemblerService();
    private static final String FIRST_SOURCE = "FirstLinearRegressionSample.pmml";
    private static final String FIRST_MODEL_FIRST_SAMPLE_NAME = "First sample for first linear regression";
    private static final String FIRST_PACKAGE_FIRST_SAMPLE_NAME = getSanitizedPackageName(FIRST_MODEL_FIRST_SAMPLE_NAME);
    private static final String FIRST_MODEL_SECOND_SAMPLE_NAME = "Second sample for first linear regression";
    private static final String FIRST_PACKAGE_SECOND_SAMPLE_NAME = getSanitizedPackageName(FIRST_MODEL_SECOND_SAMPLE_NAME);
    private static final String SECOND_SOURCE = "SecondLinearRegressionSample.pmml";
    private static final String SECOND_MODEL_FIRST_SAMPLE_NAME = "First sample for second linear regression";
    private static final String SECOND_PACKAGE_FIRST_SAMPLE_NAME = getSanitizedPackageName(SECOND_MODEL_FIRST_SAMPLE_NAME);
    private static final String SECOND_MODEL_SECOND_SAMPLE_NAME = "Second sample for second linear regression";
    private static final String SECOND_PACKAGE_SECOND_SAMPLE_NAME = getSanitizedPackageName(SECOND_MODEL_SECOND_SAMPLE_NAME);
    private KnowledgeBuilderImpl knowledgeBuilder = new KnowledgeBuilderImpl();
    private Resource firstSampleResource;

    @Before
    public void setUp() throws Exception {
        knowledgeBuilder = new KnowledgeBuilderImpl(new KnowledgeBaseImpl("TESTING", new RuleBaseConfiguration()));
        File file = getFile(FIRST_SOURCE);
        firstSampleResource = new InputStreamResource(new FileInputStream(file));
        firstSampleResource.setSourcePath(file.getPath());
    }

    @Test
    public void addResources() throws Exception {
        File file = getFile(SECOND_SOURCE);
        Resource secondSampleResource = new InputStreamResource(new FileInputStream(file));
        secondSampleResource.setSourcePath(file.getPath());
        ResourceWithConfiguration firstResourceWithConfiguration = new ResourceWithConfigurationImpl(firstSampleResource, new ResourceConfigurationImpl(), o -> {
        }, o -> {
        });
        ResourceWithConfiguration secondResourceWithConfiguration = new ResourceWithConfigurationImpl(secondSampleResource, new ResourceConfigurationImpl(), o -> {
        }, o -> {
        });
        Collection<ResourceWithConfiguration> resources = Arrays.asList(firstResourceWithConfiguration, secondResourceWithConfiguration);
        pmmlAssemblerService.addResources(knowledgeBuilder, resources, ResourceType.PMML);
        final Collection<KiePackage> retrieved = knowledgeBuilder.getKnowledgeBase().getKiePackages();
        assertNotNull(retrieved);
        assertEquals(4, retrieved.size());
        commonVerifyPackage(retrieved, FIRST_PACKAGE_FIRST_SAMPLE_NAME, FIRST_MODEL_FIRST_SAMPLE_NAME, FIRST_MODEL_SECOND_SAMPLE_NAME);
        commonVerifyPackage(retrieved, FIRST_PACKAGE_SECOND_SAMPLE_NAME, FIRST_MODEL_SECOND_SAMPLE_NAME, FIRST_MODEL_FIRST_SAMPLE_NAME);
        commonVerifyPackage(retrieved, SECOND_PACKAGE_FIRST_SAMPLE_NAME, SECOND_MODEL_FIRST_SAMPLE_NAME, SECOND_MODEL_SECOND_SAMPLE_NAME);
        commonVerifyPackage(retrieved, SECOND_PACKAGE_SECOND_SAMPLE_NAME, SECOND_MODEL_SECOND_SAMPLE_NAME, SECOND_MODEL_FIRST_SAMPLE_NAME);
    }

    @Test
    public void addResource() throws Exception {
        pmmlAssemblerService.addResource(knowledgeBuilder, firstSampleResource, ResourceType.PMML, new ResourceConfigurationImpl());
        final Collection<KiePackage> retrieved = knowledgeBuilder.getKnowledgeBase().getKiePackages();
        assertNotNull(retrieved);
        assertEquals(2, retrieved.size());
        commonVerifyPackage(retrieved, FIRST_PACKAGE_FIRST_SAMPLE_NAME, FIRST_MODEL_FIRST_SAMPLE_NAME, FIRST_MODEL_SECOND_SAMPLE_NAME);
        commonVerifyPackage(retrieved, FIRST_PACKAGE_SECOND_SAMPLE_NAME, FIRST_MODEL_SECOND_SAMPLE_NAME, FIRST_MODEL_FIRST_SAMPLE_NAME);
    }

    private static void commonVerifyPackage(final Collection<KiePackage> kiePackages, String packageName, String expectedNotNull, String expectedNull) {
        final Optional<KiePackage> second = kiePackages.stream().filter(kiePackage -> kiePackage.getName().equals(packageName)).findFirst();
        assertTrue(second.isPresent());
        second.ifPresent(kpkg -> {
            assertNotNull(((InternalKnowledgePackage) kpkg).getResourceTypePackages().get(ResourceType.PMML));
            PMMLPackage pmmlPackage = (PMMLPackage) ((InternalKnowledgePackage) kpkg).getResourceTypePackages().get(ResourceType.PMML);
            assertNotNull(pmmlPackage.getAllModels());
            assertEquals(1, pmmlPackage.getAllModels().size());
            assertNotNull(pmmlPackage.getAllModels().get(expectedNotNull));
            assertNotNull(pmmlPackage.getModelByName(expectedNotNull));
            assertNull(pmmlPackage.getAllModels().get(expectedNull));
            assertNull(pmmlPackage.getModelByName(expectedNull));

        });
    }
}