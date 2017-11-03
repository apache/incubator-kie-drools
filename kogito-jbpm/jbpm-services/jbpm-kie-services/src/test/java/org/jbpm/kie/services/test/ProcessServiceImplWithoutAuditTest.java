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

import static org.assertj.core.api.Assertions.assertThatExceptionOfType;
import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertNull;
import static org.junit.Assert.assertTrue;
import static org.kie.scanner.KieMavenRepository.getKieMavenRepository;

import java.io.File;
import java.io.FileOutputStream;
import java.util.ArrayList;
import java.util.Collection;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.drools.compiler.kie.builder.impl.InternalKieModule;
import org.jbpm.kie.services.impl.KModuleDeploymentUnit;
import org.jbpm.kie.test.util.AbstractKieServicesBaseTest;
import org.jbpm.runtime.manager.impl.deploy.DeploymentDescriptorImpl;
import org.jbpm.services.api.ProcessInstanceNotFoundException;
import org.jbpm.services.api.model.DeploymentUnit;
import org.junit.After;
import org.junit.Before;
import org.junit.Test;
import org.kie.api.KieServices;
import org.kie.api.builder.ReleaseId;
import org.kie.api.runtime.process.ProcessInstance;
import org.kie.internal.KieInternalServices;
import org.kie.internal.process.CorrelationKey;
import org.kie.internal.runtime.conf.AuditMode;
import org.kie.internal.runtime.conf.DeploymentDescriptor;
import org.kie.internal.runtime.conf.DeploymentDescriptorBuilder;
import org.kie.scanner.KieMavenRepository;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class ProcessServiceImplWithoutAuditTest extends AbstractKieServicesBaseTest {

    private static final Logger logger = LoggerFactory.getLogger(ProcessServiceImplWithoutAuditTest.class);

    private List<DeploymentUnit> units = new ArrayList<DeploymentUnit>();

    @Before
    public void prepare() {
    	configureServices();
    	logger.debug("Preparing kjar");
        KieServices ks = KieServices.Factory.get();
        ReleaseId releaseId = ks.newReleaseId(GROUP_ID, ARTIFACT_ID, VERSION);
        List<String> processes = new ArrayList<String>();
        processes.add("repo/processes/general/customtask.bpmn");
        processes.add("repo/processes/general/humanTask.bpmn");
        processes.add("repo/processes/general/import.bpmn");
        processes.add("repo/processes/general/signal.bpmn");
		processes.add("repo/processes/general/signalWithExpression.bpmn2");
        processes.add("repo/processes/general/callactivity.bpmn");
        
        DeploymentDescriptor customDescriptor = new DeploymentDescriptorImpl("org.jbpm.domain");
        DeploymentDescriptorBuilder ddBuilder = customDescriptor.getBuilder();
        ddBuilder.auditMode(AuditMode.NONE);        
        Map<String, String> extraResources = new HashMap<String, String>();        
        extraResources.put("src/main/resources/" + DeploymentDescriptor.META_INF_LOCATION, customDescriptor.toXml());

        InternalKieModule kJar1 = createKieJar(ks, releaseId, processes, extraResources);
        File pom = new File("target/kmodule", "pom.xml");
        pom.getParentFile().mkdir();
        try {
            FileOutputStream fs = new FileOutputStream(pom);
            fs.write(getPom(releaseId).getBytes());
            fs.close();
        } catch (Exception e) {
            throw new RuntimeException("Unable to save pom content", e);
        }
		KieMavenRepository repository = getKieMavenRepository();
        repository.deployArtifact(releaseId, kJar1, pom);
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

    @Test
    public void testStartProcess() {
    	assertNotNull(deploymentService);

        KModuleDeploymentUnit deploymentUnit = new KModuleDeploymentUnit(GROUP_ID, ARTIFACT_ID, VERSION);

        deploymentService.deploy(deploymentUnit);
        units.add(deploymentUnit);
    	assertNotNull(processService);

    	long processInstanceId = processService.startProcess(deploymentUnit.getIdentifier(), "customtask");
    	assertNotNull(processInstanceId);

    	ProcessInstance pi = processService.getProcessInstance(deploymentUnit.getIdentifier(), processInstanceId);
    	assertNull(pi);
    }

    @Test
    public void testStartProcessWithCorrelationKey() {
    	assertNotNull(deploymentService);

        KModuleDeploymentUnit deploymentUnit = new KModuleDeploymentUnit(GROUP_ID, ARTIFACT_ID, VERSION);

        deploymentService.deploy(deploymentUnit);
        units.add(deploymentUnit);
    	assertNotNull(processService);

    	CorrelationKey key = KieInternalServices.Factory.get().newCorrelationKeyFactory().newCorrelationKey("my business key");

    	long processInstanceId = processService.startProcess(deploymentUnit.getIdentifier(), "customtask", key);
    	assertNotNull(processInstanceId);

    	ProcessInstance pi = processService.getProcessInstance(deploymentUnit.getIdentifier(), key);
    	assertNull(pi);
    }


    @Test
    public void testStartAndAbortProcess() {
    	assertNotNull(deploymentService);

        KModuleDeploymentUnit deploymentUnit = new KModuleDeploymentUnit(GROUP_ID, ARTIFACT_ID, VERSION);

        deploymentService.deploy(deploymentUnit);
        units.add(deploymentUnit);
    	assertNotNull(processService);

    	boolean isDeployed = deploymentService.isDeployed(deploymentUnit.getIdentifier());
    	assertTrue(isDeployed);

    	long processInstanceId = processService.startProcess(deploymentUnit.getIdentifier(), "org.jbpm.writedocument");
    	assertNotNull(processInstanceId);

    	ProcessInstance pi = processService.getProcessInstance(deploymentUnit.getIdentifier(), processInstanceId);
    	assertNotNull(pi);

    	processService.abortProcessInstance(deploymentUnit.getIdentifier(), processInstanceId);

    	pi = processService.getProcessInstance(deploymentUnit.getIdentifier(), processInstanceId);
    	assertNull(pi);
    }


    @Test
    public void testStartAndSignalProcess() {
    	assertNotNull(deploymentService);

        KModuleDeploymentUnit deploymentUnit = new KModuleDeploymentUnit(GROUP_ID, ARTIFACT_ID, VERSION);

        deploymentService.deploy(deploymentUnit);
        units.add(deploymentUnit);

        boolean isDeployed = deploymentService.isDeployed(deploymentUnit.getIdentifier());
    	assertTrue(isDeployed);

    	assertNotNull(processService);

    	long processInstanceId = processService.startProcess(deploymentUnit.getIdentifier(), "org.jbpm.signal");
    	assertNotNull(processInstanceId);

    	ProcessInstance pi = processService.getProcessInstance(deploymentUnit.getIdentifier(), processInstanceId);
    	assertNotNull(pi);

    	Collection<String> signals = processService.getAvailableSignals(deploymentUnit.getIdentifier(), processInstanceId);
    	assertNotNull(signals);
    	assertEquals(1, signals.size());
    	assertTrue(signals.contains("MySignal"));

    	processService.signalProcessInstance(deploymentUnit.getIdentifier(), processInstanceId, "MySignal", null);

    	pi = processService.getProcessInstance(deploymentUnit.getIdentifier(), processInstanceId);
    	assertNull(pi);
    }

    @Test
    public void testStartAndSignalProcesses() {
    	assertNotNull(deploymentService);

        KModuleDeploymentUnit deploymentUnit = new KModuleDeploymentUnit(GROUP_ID, ARTIFACT_ID, VERSION);

        deploymentService.deploy(deploymentUnit);
        units.add(deploymentUnit);

        boolean isDeployed = deploymentService.isDeployed(deploymentUnit.getIdentifier());
    	assertTrue(isDeployed);

    	assertNotNull(processService);
    	// first start first instance
    	long processInstanceId1 = processService.startProcess(deploymentUnit.getIdentifier(), "org.jbpm.signal");
    	assertNotNull(processInstanceId1);

    	ProcessInstance pi = processService.getProcessInstance(deploymentUnit.getIdentifier(), processInstanceId1);
    	assertNotNull(pi);
    	// then start second instance
    	long processInstanceId2 = processService.startProcess(deploymentUnit.getIdentifier(), "org.jbpm.signal");
    	assertNotNull(processInstanceId2);

    	ProcessInstance pi2 = processService.getProcessInstance(deploymentUnit.getIdentifier(), processInstanceId2);
    	assertNotNull(pi2);

    	List<Long> instances = new ArrayList<Long>();
    	instances.add(processInstanceId1);
    	instances.add(processInstanceId2);
    	// and lastly cancel both
    	processService.signalProcessInstances(deploymentUnit.getIdentifier(), instances, "MySignal", null);

    	pi = processService.getProcessInstance(deploymentUnit.getIdentifier(), processInstanceId1);
    	assertNull(pi);
    	pi2 = processService.getProcessInstance(deploymentUnit.getIdentifier(), processInstanceId2);
    	assertNull(pi2);
    }

    @Test
    public void testStartProcessAndChangeVariables() {
    	assertNotNull(deploymentService);

        KModuleDeploymentUnit deploymentUnit = new KModuleDeploymentUnit(GROUP_ID, ARTIFACT_ID, VERSION);

        deploymentService.deploy(deploymentUnit);
        units.add(deploymentUnit);

        boolean isDeployed = deploymentService.isDeployed(deploymentUnit.getIdentifier());
    	assertTrue(isDeployed);

    	assertNotNull(processService);

    	Map<String, Object> params = new HashMap<String, Object>();
        params.put("approval_document", "test");
        params.put("approval_reviewComment", "need review");

    	long processInstanceId = processService.startProcess(deploymentUnit.getIdentifier(), "org.jbpm.writedocument", params);
    	assertNotNull(processInstanceId);

    	ProcessInstance pi = processService.getProcessInstance(deploymentUnit.getIdentifier(), processInstanceId);
    	assertNotNull(pi);

    	// get variable by name
    	Object variableValue = processService.getProcessInstanceVariable(deploymentUnit.getIdentifier(), processInstanceId, "approval_document");
    	assertNotNull(variableValue);
    	assertTrue(variableValue instanceof String);
    	assertEquals("test", variableValue);

    	// get all variables
    	Map<String, Object> variables = processService.getProcessInstanceVariables(deploymentUnit.getIdentifier(), processInstanceId);
    	assertNotNull(variables);
    	assertEquals(2, variables.size());
    	assertTrue(variables.containsKey("approval_document"));
    	assertTrue(variables.containsKey("approval_reviewComment"));
    	assertEquals("test", variables.get("approval_document"));
    	assertEquals("need review", variables.get("approval_reviewComment"));

    	// now change single variable
    	processService.setProcessVariable(deploymentUnit.getIdentifier(), processInstanceId, "approval_reviewComment", "updated review comment");
    	// let's verify it
    	variableValue = processService.getProcessInstanceVariable(deploymentUnit.getIdentifier(), processInstanceId, "approval_reviewComment");
    	assertNotNull(variableValue);
    	assertTrue(variableValue instanceof String);
    	assertEquals("updated review comment", variableValue);

    	// and lastly let's update both variables
    	params = new HashMap<String, Object>();
    	params.put("approval_document", "updated document");
        params.put("approval_reviewComment", "final review");

        processService.setProcessVariables(deploymentUnit.getIdentifier(), processInstanceId, params);
        variables = processService.getProcessInstanceVariables(deploymentUnit.getIdentifier(), processInstanceId);
    	assertNotNull(variables);
    	assertEquals(2, variables.size());
    	assertTrue(variables.containsKey("approval_document"));
    	assertTrue(variables.containsKey("approval_reviewComment"));
    	assertEquals("updated document", variables.get("approval_document"));
    	assertEquals("final review", variables.get("approval_reviewComment"));

    	processService.abortProcessInstance(deploymentUnit.getIdentifier(), processInstanceId);

    	pi = processService.getProcessInstance(deploymentUnit.getIdentifier(), processInstanceId);
    	assertNull(pi);
    }

    @Test
    public void testStartProcessAndAbortThenChangeVariables() {
        assertNotNull(deploymentService);

        KModuleDeploymentUnit deploymentUnit = new KModuleDeploymentUnit(GROUP_ID, ARTIFACT_ID, VERSION);
        deploymentService.deploy(deploymentUnit);
        units.add(deploymentUnit);

        final Map<String, Object> params = new HashMap<String, Object>();
        params.put("approval_document", "test");
        params.put("approval_reviewComment", "need review");

        long processInstanceId = processService.startProcess(deploymentUnit.getIdentifier(), "org.jbpm.writedocument", params);
        assertNotNull(processInstanceId);
        
        processService.abortProcessInstance(deploymentUnit.getIdentifier(), processInstanceId);

        ProcessInstance pi = processService.getProcessInstance(deploymentUnit.getIdentifier(), processInstanceId);
        assertNull(pi);
        
        assertThatExceptionOfType(ProcessInstanceNotFoundException.class).isThrownBy(() -> { 
            processService.getProcessInstanceVariable(deploymentUnit.getIdentifier(), processInstanceId, "approval_reviewComment"); });
        
          
        params.clear();
        params.put("approval_document", "updated document");
        params.put("approval_reviewComment", "final review");
        
        assertThatExceptionOfType(ProcessInstanceNotFoundException.class).isThrownBy(() -> { 
            processService.setProcessVariables(deploymentUnit.getIdentifier(), processInstanceId, params); });
        
     
        assertThatExceptionOfType(ProcessInstanceNotFoundException.class).isThrownBy(() -> { 
            processService.setProcessVariable(deploymentUnit.getIdentifier(), processInstanceId, "approval_reviewComment", "updated review comment"); });
                
    }
    
    @Test
    public void testStartProcessAndAbortAlreadyAborted() {
        assertNotNull(deploymentService);

        KModuleDeploymentUnit deploymentUnit = new KModuleDeploymentUnit(GROUP_ID, ARTIFACT_ID, VERSION);
        deploymentService.deploy(deploymentUnit);
        units.add(deploymentUnit);

        Map<String, Object> params = new HashMap<String, Object>();
        params.put("approval_document", "test");
        params.put("approval_reviewComment", "need review");

        long processInstanceId = processService.startProcess(deploymentUnit.getIdentifier(), "org.jbpm.writedocument", params);
        assertNotNull(processInstanceId);
        
        processService.abortProcessInstance(deploymentUnit.getIdentifier(), processInstanceId);

        ProcessInstance pi = processService.getProcessInstance(deploymentUnit.getIdentifier(), processInstanceId);
        assertNull(pi);
        
        assertThatExceptionOfType(ProcessInstanceNotFoundException.class).isThrownBy(() -> { 
            processService.abortProcessInstance(deploymentUnit.getIdentifier(), processInstanceId); });
        
    }
    
}
