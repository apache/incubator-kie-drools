/*
 * Copyright 2017 Red Hat, Inc. and/or its affiliates.
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
package org.jbpm.kie.services.test;

import java.io.File;
import java.io.FileOutputStream;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Set;

import org.drools.compiler.kie.builder.impl.InternalKieModule;
import org.drools.core.util.IoUtils;
import org.drools.persistence.jpa.marshaller.MappedVariable;
import org.drools.persistence.jpa.marshaller.VariableEntity;
import org.hibernate.LazyInitializationException;
import org.jbpm.kie.services.impl.KModuleDeploymentUnit;
import org.jbpm.kie.test.util.AbstractKieServicesBaseTest;
import org.jbpm.runtime.manager.impl.deploy.DeploymentDescriptorImpl;
import org.jbpm.services.api.model.DeploymentUnit;
import org.junit.After;
import org.junit.Before;
import org.junit.Test;
import org.kie.api.KieServices;
import org.kie.api.builder.ReleaseId;
import org.kie.api.runtime.process.ProcessInstance;
import org.kie.internal.runtime.conf.DeploymentDescriptor;
import org.kie.internal.runtime.conf.ObjectModel;
import org.kie.scanner.KieMavenRepository;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import static org.junit.Assert.*;
import static org.kie.scanner.KieMavenRepository.getKieMavenRepository;

public class ProcessServiceWithEntitiesTest extends AbstractKieServicesBaseTest {

    private static final Logger logger = LoggerFactory.getLogger(ProcessServiceWithEntitiesTest.class);
    private static final String CASEDETAIL_JAVA = "src/main/java/example/CaseDetail.java";
    private List<DeploymentUnit> units = new ArrayList<DeploymentUnit>();

    @Before
    public void prepare() {
        configureServices();
        logger.debug("Preparing kjar");
        KieServices ks = KieServices.Factory.get();
        ReleaseId releaseId = ks.newReleaseId(GROUP_ID, ARTIFACT_ID, VERSION);
        List<String> processes = new ArrayList<String>();
        processes.add("repo/processes/general/entityprocessvar-process.bpmn2");


        DeploymentDescriptorImpl customDescriptor = new DeploymentDescriptorImpl("org.jbpm.domain");

        List<String> remotableClasses = java.util.Arrays.asList("example.CaseDetail", "org.drools.persistence.jpa.marshaller.MappedVariable");
        customDescriptor.setClasses(remotableClasses);

        List<ObjectModel> marshallingStrategies = new ArrayList<ObjectModel>();
        marshallingStrategies.add(new ObjectModel("mvel", "new org.drools.persistence.jpa.marshaller.JPAPlaceholderResolverStrategy(\"org.jbpm.test:test-module:1.0.0\", classLoader)"));
        customDescriptor.setMarshallingStrategies(marshallingStrategies);

        Map<String, String> extraResources = new HashMap<>();
        extraResources.put(CASEDETAIL_JAVA, getCaseDetailEntitySource());
        extraResources.put("src/main/resources/" + DeploymentDescriptor.META_INF_LOCATION, customDescriptor.toXml());
        extraResources.put("src/main/resources/META-INF/persistence.xml", getPersistenceXml());

        InternalKieModule kJar1 = createKieJar(ks, releaseId, processes, extraResources);
        File pom = new File("target/kmodule", "pom.xml");
        pom.getParentFile().mkdir();
        try {
            FileOutputStream fs = new FileOutputStream(pom);
            fs.write(getPom(releaseId).getBytes());
            fs.close();
        } catch (Exception e) {

        }
        KieMavenRepository repository = getKieMavenRepository();
        repository.deployArtifact(releaseId, kJar1, pom);

    }

    @Test
    public void testStartProcessAndGetVariables() {

        assertNotNull(deploymentService);

        KModuleDeploymentUnit deploymentUnit = new KModuleDeploymentUnit(GROUP_ID, ARTIFACT_ID, VERSION);

        deploymentService.deploy(deploymentUnit);
        units.add(deploymentUnit);

        boolean isDeployed = deploymentService.isDeployed(deploymentUnit.getIdentifier());
        assertTrue(isDeployed);

        assertNotNull(processService);

        Map<String, Object> params = new HashMap<String, Object>();

        long processInstanceId = processService.startProcess(deploymentUnit.getIdentifier(), "processvarentity", params);
        assertNotNull(processInstanceId);

        ProcessInstance pi = processService.getProcessInstance(processInstanceId);
        assertNotNull(pi);

        Map<String, Object> variables = processService.getProcessInstanceVariables(processInstanceId);
        assertNotNull(variables);
        assertEquals(1, variables.size());
        assertTrue(variables.containsKey("caseDetails"));

        VariableEntity varEntity = (VariableEntity) variables.get("caseDetails");

        // make sure getting mapped variables does NOT throw LazyInitializationException
        Set<MappedVariable> mappedVariables = varEntity.getMappedVariables();
        try {
            assertEquals(1, mappedVariables.size());
            MappedVariable mappedVariable = mappedVariables.iterator().next();
            assertEquals("example.CaseDetail", mappedVariable.getVariableType());
        } catch(LazyInitializationException e ) {
            fail("Unable to retrieve mapped variables : " + e.getMessage());
        }

    }

    @After
    public void cleanup() {
        cleanupSingletonSessionId();
        if (units != null && !units.isEmpty()) {
            for (DeploymentUnit unit : units) {
                try {
                    deploymentService.undeploy(unit);
                } catch (Exception e) {
                    // do nothing in case of some failed tests to avoid next test to fail as well
                }
            }
            units.clear();
        }
        close();
    }

    private static String getPersistenceXml() {
        File entityTestPersistence = new File("src/test/resources/entity/entity-test-persistence.xml");
        assertTrue(entityTestPersistence.exists());

        return IoUtils.readFileAsString(entityTestPersistence);
    }

    private static String getCaseDetailEntitySource() {
        File entitySource = new File("src/test/resources/entity/entity-casedetail-source.txt");
        assertTrue(entitySource.exists());

        return IoUtils.readFileAsString(entitySource);
    }

}
