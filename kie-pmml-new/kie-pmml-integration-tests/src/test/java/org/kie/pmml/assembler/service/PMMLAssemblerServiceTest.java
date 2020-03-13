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

import java.util.Arrays;
import java.util.Collection;

import org.drools.compiler.builder.impl.KnowledgeBuilderImpl;
import org.drools.core.RuleBaseConfiguration;
import org.drools.core.builder.conf.impl.ResourceConfigurationImpl;
import org.drools.core.definitions.InternalKnowledgePackage;
import org.drools.core.impl.KnowledgeBaseImpl;
import org.drools.core.io.impl.InputStreamResource;
import org.junit.Before;
import org.junit.Test;
import org.kie.api.io.Resource;
import org.kie.api.io.ResourceType;
import org.kie.api.io.ResourceWithConfiguration;
import org.kie.internal.io.ResourceWithConfigurationImpl;
import org.kie.pmml.evaluator.api.container.PMMLPackage;
import org.kie.pmml.evaluator.assembler.service.PMMLAssemblerService;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotNull;
import static org.kie.test.util.filesystem.FileUtils.getFileInputStream;

public class PMMLAssemblerServiceTest {

    private static final PMMLAssemblerService pmmlAssemblerService = new PMMLAssemblerService();

    private KnowledgeBuilderImpl knowledgeBuilder = new KnowledgeBuilderImpl();
    private Resource firstSampleResource;

    @Before
    public void setUp() throws Exception {
        knowledgeBuilder = new KnowledgeBuilderImpl(new KnowledgeBaseImpl("TESTING", new RuleBaseConfiguration()));
        firstSampleResource = new InputStreamResource(getFileInputStream("FirstLinearRegressionSample.pmml"));
    }

    @Test
    public void addResources() throws Exception {
        Resource secondSampleResource = new InputStreamResource(getFileInputStream("SecondLinearRegressionSample.pmml"));
        ResourceWithConfiguration firstResourceWithConfiguration = new ResourceWithConfigurationImpl(firstSampleResource, new ResourceConfigurationImpl(), o -> {
        }, o -> {
        });
        ResourceWithConfiguration secondResourceWithConfiguration = new ResourceWithConfigurationImpl(secondSampleResource, new ResourceConfigurationImpl(), o -> {
        }, o -> {
        });
        Collection<ResourceWithConfiguration> resources = Arrays.asList(firstResourceWithConfiguration, secondResourceWithConfiguration);
        pmmlAssemblerService.addResources(knowledgeBuilder, resources, ResourceType.PMML);
        assertNotNull(knowledgeBuilder.getKnowledgeBase().getKiePackages());
        knowledgeBuilder.getKnowledgeBase().getKiePackages().forEach(kpkg -> {
            assertNotNull(((InternalKnowledgePackage) kpkg).getResourceTypePackages().get(ResourceType.PMML));
            PMMLPackage pmmlPackage = (PMMLPackage) ((InternalKnowledgePackage) kpkg).getResourceTypePackages().get(ResourceType.PMML);
            assertNotNull(pmmlPackage.getAllModels());
            assertEquals(4, pmmlPackage.getAllModels().size());
            assertNotNull(pmmlPackage.getAllModels().get("First sample for first linear regression"));
            assertNotNull(pmmlPackage.getModelByName("First sample for first linear regression"));
            assertNotNull(pmmlPackage.getAllModels().get("Second sample for first linear regression"));
            assertNotNull(pmmlPackage.getModelByName("Second sample for first linear regression"));
            assertNotNull(pmmlPackage.getAllModels().get("First sample for second linear regression"));
            assertNotNull(pmmlPackage.getModelByName("First sample for second linear regression"));
            assertNotNull(pmmlPackage.getAllModels().get("Second sample for second linear regression"));
            assertNotNull(pmmlPackage.getModelByName("Second sample for second linear regression"));
        });
    }

    @Test
    public void addResource() throws Exception {
        pmmlAssemblerService.addResource(knowledgeBuilder, firstSampleResource, ResourceType.PMML, new ResourceConfigurationImpl());
        assertNotNull(knowledgeBuilder.getKnowledgeBase().getKiePackages());
        knowledgeBuilder.getKnowledgeBase().getKiePackages().forEach(kpkg -> {
            assertNotNull(((InternalKnowledgePackage) kpkg).getResourceTypePackages().get(ResourceType.PMML));
            PMMLPackage pmmlPackage = (PMMLPackage) ((InternalKnowledgePackage) kpkg).getResourceTypePackages().get(ResourceType.PMML);
            assertNotNull(pmmlPackage.getAllModels());
            assertEquals(2, pmmlPackage.getAllModels().size());
            assertNotNull(pmmlPackage.getAllModels().get("First sample for first linear regression"));
            assertNotNull(pmmlPackage.getModelByName("First sample for first linear regression"));
            assertNotNull(pmmlPackage.getAllModels().get("Second sample for first linear regression"));
            assertNotNull(pmmlPackage.getModelByName("Second sample for first linear regression"));
        });
    }
}