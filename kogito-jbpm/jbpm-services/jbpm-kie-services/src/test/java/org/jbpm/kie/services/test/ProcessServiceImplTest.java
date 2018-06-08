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
import java.util.Arrays;
import java.util.Collection;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import javax.naming.InitialContext;
import javax.transaction.UserTransaction;

import org.drools.compiler.kie.builder.impl.InternalKieModule;
import org.drools.core.command.runtime.process.GetProcessInstanceCommand;
import org.jbpm.kie.services.impl.KModuleDeploymentUnit;
import org.jbpm.kie.test.util.AbstractKieServicesBaseTest;
import org.jbpm.runtime.manager.impl.deploy.DeploymentDescriptorImpl;
import org.jbpm.services.api.ProcessInstanceNotFoundException;
import org.jbpm.services.api.model.DeploymentUnit;
import org.jbpm.services.api.model.NodeInstanceDesc;
import org.jbpm.services.api.model.ProcessInstanceDesc;
import org.junit.After;
import org.junit.Before;
import org.junit.Test;
import org.kie.api.KieServices;
import org.kie.api.builder.ReleaseId;
import org.kie.api.runtime.process.ProcessInstance;
import org.kie.api.runtime.process.WorkItem;
import org.kie.api.runtime.query.QueryContext;
import org.kie.internal.KieInternalServices;
import org.kie.internal.process.CorrelationKey;
import org.kie.internal.runtime.conf.DeploymentDescriptor;
import org.kie.internal.runtime.conf.RuntimeStrategy;
import org.kie.scanner.KieMavenRepository;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import static org.junit.Assert.*;
import static org.kie.scanner.KieMavenRepository.getKieMavenRepository;

public class ProcessServiceImplTest extends AbstractKieServicesBaseTest {

    private static final Logger logger = LoggerFactory.getLogger(ProcessServiceImplTest.class);

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
        processes.add("repo/processes/general/boundarysignal.bpmn2");
        processes.add("repo/processes/general/boundarysignalwithexpression.bpmn2");

        InternalKieModule kJar1 = createKieJar(ks, releaseId, processes);
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

        ReleaseId releaseId3 = ks.newReleaseId(GROUP_ID, ARTIFACT_ID, "1.1.0");

        InternalKieModule kJar3 = createKieJar(ks, releaseId3, processes);
        File pom3 = new File("target/kmodule3", "pom.xml");
        pom3.getParentFile().mkdirs();
        try {
            FileOutputStream fs = new FileOutputStream(pom3);
            fs.write(getPom(releaseId3).getBytes());
            fs.close();
        } catch (Exception e) {

        }
        repository = getKieMavenRepository();
        repository.deployArtifact(releaseId3, kJar3, pom3);
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

    	ProcessInstance pi = processService.getProcessInstance(processInstanceId);
    	assertNull(pi);
    }

    @Test
    public void testStartProcessWithParms() {
    	assertNotNull(deploymentService);

        KModuleDeploymentUnit deploymentUnit = new KModuleDeploymentUnit(GROUP_ID, ARTIFACT_ID, VERSION);

        deploymentService.deploy(deploymentUnit);
        units.add(deploymentUnit);
    	assertNotNull(processService);

    	Map<String, Object> params = new HashMap<String, Object>();
        params.put("id", "test");

    	long processInstanceId = processService.startProcess(deploymentUnit.getIdentifier(), "customtask", params);
    	assertNotNull(processInstanceId);

    	ProcessInstance pi = processService.getProcessInstance(processInstanceId);
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

    	ProcessInstance pi = processService.getProcessInstance(key);
    	assertNull(pi);
    }

    @Test
    public void testStartProcessWithParmsWithCorrelationKey() {
    	assertNotNull(deploymentService);

        KModuleDeploymentUnit deploymentUnit = new KModuleDeploymentUnit(GROUP_ID, ARTIFACT_ID, VERSION);

        deploymentService.deploy(deploymentUnit);
        units.add(deploymentUnit);
    	assertNotNull(processService);

    	Map<String, Object> params = new HashMap<String, Object>();
        params.put("id", "test");

        CorrelationKey key = KieInternalServices.Factory.get().newCorrelationKeyFactory().newCorrelationKey("my business key");

    	long processInstanceId = processService.startProcess(deploymentUnit.getIdentifier(), "customtask", key, params);
    	assertNotNull(processInstanceId);

    	ProcessInstance pi = processService.getProcessInstance(key);
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

    	ProcessInstance pi = processService.getProcessInstance(processInstanceId);
    	assertNotNull(pi);

    	processService.abortProcessInstance(processInstanceId);

    	pi = processService.getProcessInstance(processInstanceId);
    	assertNull(pi);
    }

    @Test
    public void testStartAndAbortProcesses() {
    	assertNotNull(deploymentService);

        KModuleDeploymentUnit deploymentUnit = new KModuleDeploymentUnit(GROUP_ID, ARTIFACT_ID, VERSION);

        deploymentService.deploy(deploymentUnit);
        units.add(deploymentUnit);

        boolean isDeployed = deploymentService.isDeployed(deploymentUnit.getIdentifier());
    	assertTrue(isDeployed);

    	assertNotNull(processService);
    	// first start first instance
    	long processInstanceId1 = processService.startProcess(deploymentUnit.getIdentifier(), "org.jbpm.writedocument");
    	assertNotNull(processInstanceId1);

    	ProcessInstance pi = processService.getProcessInstance(processInstanceId1);
    	assertNotNull(pi);
    	// then start second instance
    	long processInstanceId2 = processService.startProcess(deploymentUnit.getIdentifier(), "org.jbpm.writedocument");
    	assertNotNull(processInstanceId2);

    	ProcessInstance pi2 = processService.getProcessInstance(processInstanceId2);
    	assertNotNull(pi2);

    	List<Long> instances = new ArrayList<Long>();
    	instances.add(processInstanceId1);
    	instances.add(processInstanceId2);
    	// and lastly cancel both
    	processService.abortProcessInstances(instances);

    	pi = processService.getProcessInstance(processInstanceId1);
    	assertNull(pi);
    	pi2 = processService.getProcessInstance(processInstanceId2);
    	assertNull(pi2);
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

    	ProcessInstance pi = processService.getProcessInstance(processInstanceId);
    	assertNotNull(pi);

    	Collection<String> signals = processService.getAvailableSignals(processInstanceId);
    	assertNotNull(signals);
    	assertEquals(1, signals.size());
    	assertTrue(signals.contains("MySignal"));

    	processService.signalProcessInstance(processInstanceId, "MySignal", null);

    	pi = processService.getProcessInstance(processInstanceId);
    	assertNull(pi);
    }

	@Test
	public void testStartAndSignalProcessWithExpression() {
		assertNotNull(deploymentService);

		KModuleDeploymentUnit deploymentUnit = new KModuleDeploymentUnit(GROUP_ID, ARTIFACT_ID, VERSION);

		deploymentService.deploy(deploymentUnit);
		units.add(deploymentUnit);

		boolean isDeployed = deploymentService.isDeployed(deploymentUnit.getIdentifier());
		assertTrue(isDeployed);

		assertNotNull(processService);

		long processInstanceId = processService.startProcess(deploymentUnit.getIdentifier(), "org.jbpm.signalWithExpression");
		assertNotNull(processInstanceId);

		ProcessInstance pi = processService.getProcessInstance(processInstanceId);
		assertNotNull(pi);

		Collection<String> signals = processService.getAvailableSignals(processInstanceId);
		assertNotNull(signals);
		assertEquals(1, signals.size());
		assertTrue(signals.contains("MySignal"));

		processService.signalProcessInstance(processInstanceId, "MySignal", null);

		pi = processService.getProcessInstance(processInstanceId);
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

    	ProcessInstance pi = processService.getProcessInstance(processInstanceId1);
    	assertNotNull(pi);
    	// then start second instance
    	long processInstanceId2 = processService.startProcess(deploymentUnit.getIdentifier(), "org.jbpm.signal");
    	assertNotNull(processInstanceId2);

    	ProcessInstance pi2 = processService.getProcessInstance(processInstanceId2);
    	assertNotNull(pi2);

    	List<Long> instances = new ArrayList<Long>();
    	instances.add(processInstanceId1);
    	instances.add(processInstanceId2);
    	// and lastly cancel both
    	processService.signalProcessInstances(instances, "MySignal", null);

    	pi = processService.getProcessInstance(processInstanceId1);
    	assertNull(pi);
    	pi2 = processService.getProcessInstance(processInstanceId2);
    	assertNull(pi2);
    }

    @Test
    public void testStartAndSignal() {
        assertNotNull(deploymentService);

        KModuleDeploymentUnit deploymentUnit = new KModuleDeploymentUnit(GROUP_ID, ARTIFACT_ID, VERSION);

        deploymentService.deploy(deploymentUnit);
        units.add(deploymentUnit);

        boolean isDeployed = deploymentService.isDeployed(deploymentUnit.getIdentifier());
        assertTrue(isDeployed);

        assertNotNull(processService);

        long processInstanceId = processService.startProcess(deploymentUnit.getIdentifier(), "org.jbpm.signal");
        assertNotNull(processInstanceId);

        long processInstanceId2 = processService.startProcess(deploymentUnit.getIdentifier(), "org.jbpm.signal");
        assertNotNull(processInstanceId2);

        ProcessInstance pi = processService.getProcessInstance(processInstanceId);
        assertNotNull(pi);

        pi = processService.getProcessInstance(processInstanceId2);
        assertNotNull(pi);

        processService.signalEvent(deploymentUnit.getIdentifier(), "MySignal", null);

        pi = processService.getProcessInstance(processInstanceId);
        assertNull(pi);
        pi = processService.getProcessInstance(processInstanceId2);
        assertNull(pi);
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

    	ProcessInstance pi = processService.getProcessInstance(processInstanceId);
    	assertNotNull(pi);

    	// get variable by name
    	Object variableValue = processService.getProcessInstanceVariable(processInstanceId, "approval_document");
    	assertNotNull(variableValue);
    	assertTrue(variableValue instanceof String);
    	assertEquals("test", variableValue);

    	// get all variables
    	Map<String, Object> variables = processService.getProcessInstanceVariables(processInstanceId);
    	assertNotNull(variables);
    	assertEquals(2, variables.size());
    	assertTrue(variables.containsKey("approval_document"));
    	assertTrue(variables.containsKey("approval_reviewComment"));
    	assertEquals("test", variables.get("approval_document"));
    	assertEquals("need review", variables.get("approval_reviewComment"));

    	// now change single variable
    	processService.setProcessVariable(processInstanceId, "approval_reviewComment", "updated review comment");
    	// let's verify it
    	variableValue = processService.getProcessInstanceVariable(processInstanceId, "approval_reviewComment");
    	assertNotNull(variableValue);
    	assertTrue(variableValue instanceof String);
    	assertEquals("updated review comment", variableValue);

    	// and lastly let's update both variables
    	params = new HashMap<String, Object>();
    	params.put("approval_document", "updated document");
        params.put("approval_reviewComment", "final review");

        processService.setProcessVariables(processInstanceId, params);
        variables = processService.getProcessInstanceVariables(processInstanceId);
    	assertNotNull(variables);
    	assertEquals(2, variables.size());
    	assertTrue(variables.containsKey("approval_document"));
    	assertTrue(variables.containsKey("approval_reviewComment"));
    	assertEquals("updated document", variables.get("approval_document"));
    	assertEquals("final review", variables.get("approval_reviewComment"));

    	processService.abortProcessInstance(processInstanceId);

    	pi = processService.getProcessInstance(processInstanceId);
    	assertNull(pi);
    }

    @Test
    public void testStartProcessAndCompleteWorkItem() {
    	assertNotNull(deploymentService);

        KModuleDeploymentUnit deploymentUnit = new KModuleDeploymentUnit(GROUP_ID, ARTIFACT_ID, VERSION);

        deploymentService.deploy(deploymentUnit);
        units.add(deploymentUnit);

        boolean isDeployed = deploymentService.isDeployed(deploymentUnit.getIdentifier());
    	assertTrue(isDeployed);

    	assertNotNull(processService);

    	long processInstanceId = processService.startProcess(deploymentUnit.getIdentifier(), "org.jbpm.writedocument");
    	assertNotNull(processInstanceId);

    	ProcessInstance pi = processService.getProcessInstance(processInstanceId);
    	assertNotNull(pi);

    	Collection<NodeInstanceDesc> activeNodes = runtimeDataService.getProcessInstanceHistoryActive(processInstanceId, new QueryContext());
    	assertNotNull(activeNodes);
    	assertEquals(1, activeNodes.size());
    	assertEquals("Write a Document", activeNodes.iterator().next().getName());

    	Map<String, Object> outcome = new HashMap<String, Object>();
    	outcome.put("Result", "here is my first document");
    	processService.completeWorkItem(activeNodes.iterator().next().getWorkItemId(), outcome);

    	activeNodes = runtimeDataService.getProcessInstanceHistoryActive(processInstanceId, new QueryContext());
    	assertNotNull(activeNodes);
    	assertEquals(2, activeNodes.size());

    	Object variableValue = processService.getProcessInstanceVariable(processInstanceId, "approval_document");
    	assertNotNull(variableValue);
    	assertTrue(variableValue instanceof String);
    	assertEquals("here is my first document", variableValue);

    	processService.abortProcessInstance(processInstanceId);

    	pi = processService.getProcessInstance(processInstanceId);
    	assertNull(pi);
    }

    @Test
    public void testStartProcessAndAbortWorkItem() {
    	assertNotNull(deploymentService);

        KModuleDeploymentUnit deploymentUnit = new KModuleDeploymentUnit(GROUP_ID, ARTIFACT_ID, VERSION);

        deploymentService.deploy(deploymentUnit);
        units.add(deploymentUnit);

        boolean isDeployed = deploymentService.isDeployed(deploymentUnit.getIdentifier());
    	assertTrue(isDeployed);

    	assertNotNull(processService);

    	long processInstanceId = processService.startProcess(deploymentUnit.getIdentifier(), "org.jbpm.writedocument");
    	assertNotNull(processInstanceId);

    	ProcessInstance pi = processService.getProcessInstance(processInstanceId);
    	assertNotNull(pi);

    	Collection<NodeInstanceDesc> activeNodes = runtimeDataService.getProcessInstanceHistoryActive(processInstanceId, new QueryContext());
    	assertNotNull(activeNodes);
    	assertEquals(1, activeNodes.size());
    	assertEquals("Write a Document", activeNodes.iterator().next().getName());

    	processService.abortWorkItem(activeNodes.iterator().next().getWorkItemId());

    	activeNodes = runtimeDataService.getProcessInstanceHistoryActive(processInstanceId, new QueryContext());
    	assertNotNull(activeNodes);
    	assertEquals(2, activeNodes.size());

    	Object variableValue = processService.getProcessInstanceVariable(processInstanceId, "approval_document");
    	assertNull(variableValue);

    	processService.abortProcessInstance(processInstanceId);

    	pi = processService.getProcessInstance(processInstanceId);
    	assertNull(pi);
    }


    @Test
    public void testStartProcessAndGetWorkItem() {
    	assertNotNull(deploymentService);

        KModuleDeploymentUnit deploymentUnit = new KModuleDeploymentUnit(GROUP_ID, ARTIFACT_ID, VERSION);

        deploymentService.deploy(deploymentUnit);
        units.add(deploymentUnit);

        boolean isDeployed = deploymentService.isDeployed(deploymentUnit.getIdentifier());
    	assertTrue(isDeployed);

    	assertNotNull(processService);

    	long processInstanceId = processService.startProcess(deploymentUnit.getIdentifier(), "org.jbpm.writedocument");
    	assertNotNull(processInstanceId);

    	ProcessInstance pi = processService.getProcessInstance(processInstanceId);
    	assertNotNull(pi);

    	Collection<NodeInstanceDesc> activeNodes = runtimeDataService.getProcessInstanceHistoryActive(processInstanceId, new QueryContext());
    	assertNotNull(activeNodes);
    	assertEquals(1, activeNodes.size());
    	assertEquals("Write a Document", activeNodes.iterator().next().getName());

    	WorkItem wi =processService.getWorkItem(activeNodes.iterator().next().getWorkItemId());
    	assertNotNull(wi);
    	assertEquals("Human Task", wi.getName());
    	assertEquals("Write a Document", wi.getParameter("NodeName"));

    	processService.abortProcessInstance(processInstanceId);

    	pi = processService.getProcessInstance(processInstanceId);
    	assertNull(pi);
    }

    @Test
    public void testStartProcessAndGetWorkItems() {
    	assertNotNull(deploymentService);

        KModuleDeploymentUnit deploymentUnit = new KModuleDeploymentUnit(GROUP_ID, ARTIFACT_ID, VERSION);

        deploymentService.deploy(deploymentUnit);
        units.add(deploymentUnit);

        boolean isDeployed = deploymentService.isDeployed(deploymentUnit.getIdentifier());
    	assertTrue(isDeployed);

    	assertNotNull(processService);

    	long processInstanceId = processService.startProcess(deploymentUnit.getIdentifier(), "org.jbpm.writedocument");
    	assertNotNull(processInstanceId);

    	ProcessInstance pi = processService.getProcessInstance(processInstanceId);
    	assertNotNull(pi);

    	Collection<NodeInstanceDesc> activeNodes = runtimeDataService.getProcessInstanceHistoryActive(processInstanceId, new QueryContext());
    	assertNotNull(activeNodes);
    	assertEquals(1, activeNodes.size());
    	assertEquals("Write a Document", activeNodes.iterator().next().getName());

    	List<WorkItem> wis =processService.getWorkItemByProcessInstance(processInstanceId);
    	assertNotNull(wis);
    	assertEquals(1, wis.size());
    	assertEquals("Human Task", wis.get(0).getName());
    	assertEquals("Write a Document", wis.get(0).getParameter("NodeName"));

    	processService.abortProcessInstance(processInstanceId);

    	pi = processService.getProcessInstance(processInstanceId);
    	assertNull(pi);
    }

    @Test
    public void testStartProcessAndExecuteCmd() {
    	assertNotNull(deploymentService);

        KModuleDeploymentUnit deploymentUnit = new KModuleDeploymentUnit(GROUP_ID, ARTIFACT_ID, VERSION);

        deploymentService.deploy(deploymentUnit);
        units.add(deploymentUnit);

        boolean isDeployed = deploymentService.isDeployed(deploymentUnit.getIdentifier());
    	assertTrue(isDeployed);

    	assertNotNull(processService);

    	long processInstanceId = processService.startProcess(deploymentUnit.getIdentifier(), "org.jbpm.writedocument");
    	assertNotNull(processInstanceId);

    	ProcessInstance pi = processService.execute(deploymentUnit.getIdentifier(), new GetProcessInstanceCommand(processInstanceId));
    	assertNotNull(pi);

    	processService.abortProcessInstance(processInstanceId);

    	pi = processService.getProcessInstance(processInstanceId);
    	assertNull(pi);
    }

    @Test
    public void testStartProcessFromLatestDeployment() {
    	assertNotNull(deploymentService);

        KModuleDeploymentUnit deploymentUnit = new KModuleDeploymentUnit(GROUP_ID, ARTIFACT_ID, VERSION);
        KModuleDeploymentUnit deploymentUnit2 = new KModuleDeploymentUnit(GROUP_ID, ARTIFACT_ID, "1.1.0");

        deploymentService.deploy(deploymentUnit);
        units.add(deploymentUnit);

        boolean isDeployed = deploymentService.isDeployed(deploymentUnit.getIdentifier());
    	assertTrue(isDeployed);

        deploymentService.deploy(deploymentUnit2);
        units.add(deploymentUnit2);

        isDeployed = deploymentService.isDeployed(deploymentUnit2.getIdentifier());
    	assertTrue(isDeployed);

    	assertNotNull(processService);

    	long processInstanceId = processService.startProcess(GROUP_ID+":"+ARTIFACT_ID+":LATEST", "customtask");
    	assertNotNull(processInstanceId);

    	ProcessInstance pi = processService.getProcessInstance(processInstanceId);
    	assertNull(pi);

    	ProcessInstanceDesc piDesc = runtimeDataService.getProcessInstanceById(processInstanceId);
    	assertNotNull(piDesc);
    	assertEquals(deploymentUnit2.getIdentifier(), piDesc.getDeploymentId());
    }

    @Test
    public void testStartProcessAfterDeactivation() {
    	assertNotNull(deploymentService);

        KModuleDeploymentUnit deploymentUnit = new KModuleDeploymentUnit(GROUP_ID, ARTIFACT_ID, VERSION);

        deploymentService.deploy(deploymentUnit);
        units.add(deploymentUnit);

        boolean isDeployed = deploymentService.isDeployed(deploymentUnit.getIdentifier());
    	assertTrue(isDeployed);

    	assertNotNull(processService);

    	deploymentService.deactivate(deploymentUnit.getIdentifier());

    	try {
    		processService.startProcess(deploymentUnit.getIdentifier(), "customtask");
    		fail("Deployment is deactivated so cannot start new process instances");
    	} catch (Exception e) {
    		assertTrue(e.getMessage().contains("Deployments org.jbpm.test:test-module:1.0.0 is not active"));
    	}

    }

    @Test
    public void testStartProcessAndCompleteWorkItemAfterDeactivation() {
    	assertNotNull(deploymentService);

        KModuleDeploymentUnit deploymentUnit = new KModuleDeploymentUnit(GROUP_ID, ARTIFACT_ID, VERSION);

        deploymentService.deploy(deploymentUnit);
        units.add(deploymentUnit);

        boolean isDeployed = deploymentService.isDeployed(deploymentUnit.getIdentifier());
    	assertTrue(isDeployed);

    	assertNotNull(processService);

    	long processInstanceId = processService.startProcess(deploymentUnit.getIdentifier(), "org.jbpm.writedocument");
    	assertNotNull(processInstanceId);

    	ProcessInstance pi = processService.getProcessInstance(processInstanceId);
    	assertNotNull(pi);

    	deploymentService.deactivate(deploymentUnit.getIdentifier());

    	Collection<NodeInstanceDesc> activeNodes = runtimeDataService.getProcessInstanceHistoryActive(processInstanceId, new QueryContext());
    	assertNotNull(activeNodes);
    	assertEquals(1, activeNodes.size());
    	assertEquals("Write a Document", activeNodes.iterator().next().getName());

    	Map<String, Object> outcome = new HashMap<String, Object>();
    	outcome.put("Result", "here is my first document");
    	processService.completeWorkItem(activeNodes.iterator().next().getWorkItemId(), outcome);

    	activeNodes = runtimeDataService.getProcessInstanceHistoryActive(processInstanceId, new QueryContext());
    	assertNotNull(activeNodes);
    	assertEquals(2, activeNodes.size());

    	Object variableValue = processService.getProcessInstanceVariable(processInstanceId, "approval_document");
    	assertNotNull(variableValue);
    	assertTrue(variableValue instanceof String);
    	assertEquals("here is my first document", variableValue);

    	processService.abortProcessInstance(processInstanceId);

    	pi = processService.getProcessInstance(processInstanceId);
    	assertNull(pi);
    }

    @Test
    public void testStartAndAbortProcessInExternalTransactionsSingleton() throws Exception {
        testStartAndAbortProcessInExternalTransactions(RuntimeStrategy.SINGLETON);
    }

    @Test
    public void testStartAndAbortProcessInExternalTransactionsPerRequest() throws Exception {
        testStartAndAbortProcessInExternalTransactions(RuntimeStrategy.PER_REQUEST);
    }

    @Test
    public void testStartAndAbortProcessInExternalTransactionsPerProcessInstance() throws Exception {
        testStartAndAbortProcessInExternalTransactions(RuntimeStrategy.PER_PROCESS_INSTANCE);
    }


    protected void testStartAndAbortProcessInExternalTransactions(RuntimeStrategy strategy) throws Exception {
        assertNotNull(deploymentService);

        KModuleDeploymentUnit deploymentUnit = new KModuleDeploymentUnit(GROUP_ID, ARTIFACT_ID, VERSION);
        DeploymentDescriptor customDescriptor = new DeploymentDescriptorImpl("org.jbpm.domain");
        customDescriptor.getBuilder()
        .runtimeStrategy(strategy);
        deploymentUnit.setDeploymentDescriptor(customDescriptor);


        deploymentService.deploy(deploymentUnit);
        units.add(deploymentUnit);
        assertNotNull(processService);

        boolean isDeployed = deploymentService.isDeployed(deploymentUnit.getIdentifier());
        assertTrue(isDeployed);

        UserTransaction ut = InitialContext.doLookup("java:comp/UserTransaction");
        ut.begin();

        long processInstanceId = processService.startProcess(deploymentUnit.getIdentifier(), "org.jbpm.writedocument");
        assertNotNull(processInstanceId);

        processService.signalEvent(deploymentUnit.getIdentifier(), "test", null);

        ut.commit();
        // now let's start another transaction
        ut.begin();

        processService.abortProcessInstance(processInstanceId);
        ut.commit();

        try {
            processService.getProcessInstance(processInstanceId);
        } catch (ProcessInstanceNotFoundException e) {
            // expected
        }

    }

    @Test
    public void testStartAndGetProcessInExternalTransactionsSingleton() throws Exception {
        testStartAndGetProcessInExternalTransactions(RuntimeStrategy.SINGLETON);
    }

    @Test
    public void testStartAndGetProcessInExternalTransactionsPerRequest() throws Exception {
        testStartAndGetProcessInExternalTransactions(RuntimeStrategy.PER_REQUEST);
    }

    @Test
    public void testStartAndGetProcessInExternalTransactionsPerProcessInstance() throws Exception {
        testStartAndGetProcessInExternalTransactions(RuntimeStrategy.PER_PROCESS_INSTANCE);
    }

    protected void testStartAndGetProcessInExternalTransactions(RuntimeStrategy strategy) throws Exception {
        assertNotNull(deploymentService);

        KModuleDeploymentUnit deploymentUnit = new KModuleDeploymentUnit(GROUP_ID, ARTIFACT_ID, VERSION);
        DeploymentDescriptor customDescriptor = new DeploymentDescriptorImpl("org.jbpm.domain");
        customDescriptor.getBuilder()
        .runtimeStrategy(strategy);
        deploymentUnit.setDeploymentDescriptor(customDescriptor);


        deploymentService.deploy(deploymentUnit);
        units.add(deploymentUnit);
        assertNotNull(processService);

        boolean isDeployed = deploymentService.isDeployed(deploymentUnit.getIdentifier());
        assertTrue(isDeployed);

        UserTransaction ut = InitialContext.doLookup("java:comp/UserTransaction");
        ut.begin();

        long processInstanceId = processService.startProcess(deploymentUnit.getIdentifier(), "org.jbpm.writedocument");
        assertNotNull(processInstanceId);

        ProcessInstance pi = processService.getProcessInstance(processInstanceId);
        assertNotNull(pi);

        processService.abortProcessInstance(processInstanceId);
        ut.commit();

        try {
            processService.getProcessInstance(processInstanceId);
        } catch (ProcessInstanceNotFoundException e) {
            // expected
        }

    }
    
    @Test
    public void testStartProcessAndAbortThenChangeVariables() {
        assertNotNull(deploymentService);

        KModuleDeploymentUnit deploymentUnit = new KModuleDeploymentUnit(GROUP_ID, ARTIFACT_ID, VERSION);
        deploymentService.deploy(deploymentUnit);
        units.add(deploymentUnit);

        Map<String, Object> params = new HashMap<String, Object>();
        params.put("approval_document", "test");
        params.put("approval_reviewComment", "need review");

        long processInstanceId = processService.startProcess(deploymentUnit.getIdentifier(), "org.jbpm.writedocument", params);
        assertNotNull(processInstanceId);
        
        processService.abortProcessInstance(processInstanceId);

        ProcessInstance pi = processService.getProcessInstance(processInstanceId);
        assertNull(pi);
        
        try {
            processService.getProcessInstanceVariable(processInstanceId, "approval_reviewComment");
            fail("Process instance was aborted so variables do not exist");
        } catch (ProcessInstanceNotFoundException e) {
            // expected
        }
        
        try {
            processService.getProcessInstanceVariable(processInstanceId, "approval_reviewComment");
            fail("Process instance was aborted so variables do not exist");
        } catch (ProcessInstanceNotFoundException e) {
            // expected
        }
          
        params = new HashMap<String, Object>();
        params.put("approval_document", "updated document");
        params.put("approval_reviewComment", "final review");
        try {
            processService.setProcessVariables(processInstanceId, params);
            fail("Process instance was aborted so cannot be changed");
        } catch (ProcessInstanceNotFoundException e) {
            // expected
        }

        try {
            processService.setProcessVariable(processInstanceId, "approval_reviewComment", "updated review comment");
            fail("Process instance was aborted so cannot be changed");
        } catch (ProcessInstanceNotFoundException e) {
            // expected
        }
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
        
        processService.abortProcessInstance(processInstanceId);

        ProcessInstance pi = processService.getProcessInstance(processInstanceId);
        assertNull(pi);
        
        try {
            processService.abortProcessInstance(processInstanceId);
            fail("Process instance was aborted so process instance does not exist any more");
        } catch (ProcessInstanceNotFoundException e) {
            // expected
        }
    }
    
    @Test
    public void testStartProcessCallActivity() {
        assertNotNull(deploymentService);

        KModuleDeploymentUnit deploymentUnit = new KModuleDeploymentUnit(GROUP_ID, ARTIFACT_ID, VERSION);

        deploymentService.deploy(deploymentUnit);
        units.add(deploymentUnit);
        assertNotNull(processService);

        long processInstanceId = processService.startProcess(deploymentUnit.getIdentifier(), "ParentProcess");
        assertNotNull(processInstanceId);

        ProcessInstance pi = processService.getProcessInstance(processInstanceId);
        assertNotNull(pi);
        
        Collection<ProcessInstanceDesc> children = runtimeDataService.getProcessInstancesByParent(processInstanceId, null, new QueryContext());
        assertNotNull(children);
        assertEquals(1, children.size());
        
        ProcessInstanceDesc childInstance = children.iterator().next();
        assertNotNull(childInstance);
        assertEquals("org.jbpm.signal", childInstance.getProcessId());
        
        children = runtimeDataService.getProcessInstancesByParent(processInstanceId, Arrays.asList(2, 3), new QueryContext());
        assertNotNull(children);
        assertEquals(0, children.size());
        
        processService.signalProcessInstance(childInstance.getId(), "MySignal", null);
        
        children = runtimeDataService.getProcessInstancesByParent(processInstanceId, Arrays.asList(2, 3), new QueryContext());
        assertNotNull(children);
        assertEquals(1, children.size());
        
        childInstance = children.iterator().next();
        assertNotNull(childInstance);
        assertEquals("org.jbpm.signal", childInstance.getProcessId());
        
        pi = processService.getProcessInstance(processInstanceId);
        assertNull(pi);
    }
    
    @Test
    public void testStartProcessCallActivityCheckNodes() {
        assertNotNull(deploymentService);

        KModuleDeploymentUnit deploymentUnit = new KModuleDeploymentUnit(GROUP_ID, ARTIFACT_ID, VERSION);

        deploymentService.deploy(deploymentUnit);
        units.add(deploymentUnit);
        assertNotNull(processService);

        long processInstanceId = processService.startProcess(deploymentUnit.getIdentifier(), "ParentProcess");
        assertNotNull(processInstanceId);

        ProcessInstance pi = processService.getProcessInstance(processInstanceId);
        assertNotNull(pi);
        
        Collection<ProcessInstanceDesc> children = runtimeDataService.getProcessInstancesByParent(processInstanceId, null, new QueryContext());
        assertNotNull(children);
        assertEquals(1, children.size());
        
        ProcessInstanceDesc childInstance = children.iterator().next();
        assertNotNull(childInstance);
        assertEquals("org.jbpm.signal", childInstance.getProcessId());
        
        Collection<NodeInstanceDesc> activeNodes = runtimeDataService.getProcessInstanceHistoryActive(processInstanceId, new QueryContext());
        assertNotNull(activeNodes);
        assertEquals(1, activeNodes.size());        
        NodeInstanceDesc activeNode = activeNodes.iterator().next();
        assertNotNull(activeNode);
        assertEquals("SubProcessNode", activeNode.getNodeType());
        assertEquals(childInstance.getId(), activeNode.getReferenceId());
        
        processService.signalProcessInstance(childInstance.getId(), "MySignal", null);
        
        children = runtimeDataService.getProcessInstancesByParent(processInstanceId, Arrays.asList(2, 3), new QueryContext());
        assertNotNull(children);
        assertEquals(1, children.size());
        
        childInstance = children.iterator().next();
        assertNotNull(childInstance);
        assertEquals("org.jbpm.signal", childInstance.getProcessId());
        
        pi = processService.getProcessInstance(processInstanceId);
        assertNull(pi);
        
        Collection<NodeInstanceDesc> completedNodes = runtimeDataService.getProcessInstanceHistoryCompleted(processInstanceId, new QueryContext());
        assertNotNull(completedNodes);
        assertEquals(3, completedNodes.size());        
        NodeInstanceDesc completedNode = completedNodes.stream().filter(n -> n.getNodeType().equals("SubProcessNode")).findFirst().orElse(null);
        assertNotNull(completedNode);
        assertEquals("SubProcessNode", completedNode.getNodeType());
        assertEquals(childInstance.getId(), completedNode.getReferenceId());
    }

	@Test
	public void testStartAndSignalBoundary() {
		assertNotNull(deploymentService);

		KModuleDeploymentUnit deploymentUnit = new KModuleDeploymentUnit(GROUP_ID, ARTIFACT_ID, VERSION);

		deploymentService.deploy(deploymentUnit);
		units.add(deploymentUnit);

		boolean isDeployed = deploymentService.isDeployed(deploymentUnit.getIdentifier());
		assertTrue(isDeployed);

		assertNotNull(processService);

		long processInstanceId = processService.startProcess(deploymentUnit.getIdentifier(), "org.jbpm.boundarysignal");
		assertNotNull(processInstanceId);

		ProcessInstance pi = processService.getProcessInstance(processInstanceId);
		assertNotNull(pi);

		Collection<String> signals = processService.getAvailableSignals(processInstanceId);
		assertNotNull(signals);
		assertEquals(1, signals.size());
		assertTrue(signals.contains("MySignal"));

		processService.signalProcessInstance(processInstanceId, "MySignal", null);

		pi = processService.getProcessInstance(processInstanceId);
		assertNull(pi);
	}

	@Test
	public void testStartAndSignalBoundaryWithExpression() {
		assertNotNull(deploymentService);

		KModuleDeploymentUnit deploymentUnit = new KModuleDeploymentUnit(GROUP_ID, ARTIFACT_ID, VERSION);

		deploymentService.deploy(deploymentUnit);
		units.add(deploymentUnit);

		boolean isDeployed = deploymentService.isDeployed(deploymentUnit.getIdentifier());
		assertTrue(isDeployed);

		assertNotNull(processService);

		long processInstanceId = processService.startProcess(deploymentUnit.getIdentifier(), "org.jbpm.boundarysignalwithexpression");
		assertNotNull(processInstanceId);

		ProcessInstance pi = processService.getProcessInstance(processInstanceId);
		assertNotNull(pi);

		Collection<String> signals = processService.getAvailableSignals(processInstanceId);
		assertNotNull(signals);
		assertEquals(1, signals.size());
		assertTrue(signals.contains("MySignal"));

		processService.signalProcessInstance(processInstanceId, "MySignal", null);

		pi = processService.getProcessInstance(processInstanceId);
		assertNull(pi);
	}
	
    @Test
    public void testGetProcessInstanceVariablesOfAbortedProcess() {
        assertNotNull(deploymentService);

        KModuleDeploymentUnit deploymentUnit = new KModuleDeploymentUnit(GROUP_ID, ARTIFACT_ID, VERSION);

        deploymentService.deploy(deploymentUnit);
        units.add(deploymentUnit);

        boolean isDeployed = deploymentService.isDeployed(deploymentUnit.getIdentifier());
        assertTrue(isDeployed);

        assertNotNull(processService);
        long processInstanceId = processService.startProcess(deploymentUnit.getIdentifier(), "org.jbpm.writedocument");
        assertNotNull(processInstanceId);
        processService.abortProcessInstance(deploymentUnit.getIdentifier(), processInstanceId);

        try {
            processService.getProcessInstanceVariables(deploymentUnit.getIdentifier(), processInstanceId);
            fail("Getting process variables of already aborted process instance should throw ProcessInstanceNotFoundException.");
        } catch(ProcessInstanceNotFoundException e) {
            // expected
        }
    }
}
