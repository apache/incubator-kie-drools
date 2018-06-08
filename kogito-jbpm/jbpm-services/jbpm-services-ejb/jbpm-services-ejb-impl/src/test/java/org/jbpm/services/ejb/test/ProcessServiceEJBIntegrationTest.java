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

package org.jbpm.services.ejb.test;

import java.io.File;
import java.io.FileOutputStream;
import java.util.ArrayList;
import java.util.Collection;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import javax.ejb.EJB;

import org.drools.compiler.kie.builder.impl.InternalKieModule;
import org.drools.core.command.runtime.process.GetProcessInstanceCommand;
import org.jboss.arquillian.container.test.api.Deployment;
import org.jboss.arquillian.junit.Arquillian;
import org.jboss.shrinkwrap.api.ShrinkWrap;
import org.jboss.shrinkwrap.api.spec.WebArchive;
import org.jbpm.kie.services.impl.KModuleDeploymentUnit;
import org.jbpm.services.api.model.DeploymentUnit;
import org.jbpm.services.api.model.NodeInstanceDesc;
import org.jbpm.services.ejb.api.DefinitionServiceEJBLocal;
import org.jbpm.services.ejb.api.DeploymentServiceEJBLocal;
import org.jbpm.services.ejb.api.ProcessServiceEJBLocal;
import org.jbpm.services.ejb.api.RuntimeDataServiceEJBLocal;
import org.junit.After;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.kie.api.KieServices;
import org.kie.api.builder.ReleaseId;
import org.kie.api.runtime.process.ProcessInstance;
import org.kie.api.runtime.process.WorkItem;
import org.kie.api.runtime.query.QueryContext;
import org.kie.internal.KieInternalServices;
import org.kie.internal.process.CorrelationKey;
import org.kie.scanner.KieMavenRepository;

import static org.junit.Assert.*;
import static org.kie.scanner.KieMavenRepository.getKieMavenRepository;

@RunWith(Arquillian.class)
public class ProcessServiceEJBIntegrationTest extends AbstractTestSupport {

	@Deployment
	public static WebArchive createDeployment() {
		File archive = new File("target/sample-war-ejb-app.war");
		if (!archive.exists()) {
			throw new IllegalStateException("There is no archive yet generated, run maven build or mvn assembly:assembly");
		}
		WebArchive war = ShrinkWrap.createFromZipFile(WebArchive.class, archive);
		war.addPackage("org.jbpm.services.ejb.test"); // test cases

		// deploy test kjar
		deployKjar();
		
		return war;
	}
	
	protected static void deployKjar() {
		KieServices ks = KieServices.Factory.get();
        ReleaseId releaseId = ks.newReleaseId(GROUP_ID, ARTIFACT_ID, VERSION);
        List<String> processes = new ArrayList<String>();
        processes.add("processes/customtask.bpmn");
        processes.add("processes/humanTask.bpmn");
        processes.add("processes/signal.bpmn");
		processes.add("processes/signalWithExpression.bpmn2");
        processes.add("processes/import.bpmn");
        
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
        repository.installArtifact(releaseId, kJar1, pom);
	}
	
	private List<DeploymentUnit> units = new ArrayList<DeploymentUnit>();
	
    @After
    public void cleanup() {

    	cleanupSingletonSessionId();
        if (units != null && !units.isEmpty()) {
            for (DeploymentUnit unit : units) {
                deploymentService.undeploy(unit);
            }
            units.clear();
        }
    }


	@EJB
	private DefinitionServiceEJBLocal bpmn2Service;
	
	@EJB
	private DeploymentServiceEJBLocal deploymentService;
	
	@EJB
	private ProcessServiceEJBLocal processService;
	
	@EJB
	private RuntimeDataServiceEJBLocal runtimeDataService;
	
    @Test
    public void testStartProcess() {
    	assertNotNull(deploymentService);
        
        KModuleDeploymentUnit deploymentUnit = new KModuleDeploymentUnit(GROUP_ID, ARTIFACT_ID, VERSION);
        
        deploymentService.deploy(deploymentUnit);
        units.add(deploymentUnit);
        
        boolean isDeployed = deploymentService.isDeployed(deploymentUnit.getIdentifier());
    	assertTrue(isDeployed);
    	
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
        
        boolean isDeployed = deploymentService.isDeployed(deploymentUnit.getIdentifier());
    	assertTrue(isDeployed);
    	
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
        
        boolean isDeployed = deploymentService.isDeployed(deploymentUnit.getIdentifier());
    	assertTrue(isDeployed);
    	
    	assertNotNull(processService);
    	
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
    	
    	long processInstanceId = processService.startProcess(deploymentUnit.getIdentifier(), "signal");
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
    	long processInstanceId1 = processService.startProcess(deploymentUnit.getIdentifier(), "signal");
    	assertNotNull(processInstanceId1);
    	
    	ProcessInstance pi = processService.getProcessInstance(processInstanceId1);    	
    	assertNotNull(pi);
    	// then start second instance
    	long processInstanceId2 = processService.startProcess(deploymentUnit.getIdentifier(), "signal");
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
	public void testStartAndSignalProcessesWithExpression() {
		assertNotNull(deploymentService);

		KModuleDeploymentUnit deploymentUnit = new KModuleDeploymentUnit(GROUP_ID, ARTIFACT_ID, VERSION);

		deploymentService.deploy(deploymentUnit);
		units.add(deploymentUnit);

		boolean isDeployed = deploymentService.isDeployed(deploymentUnit.getIdentifier());
		assertTrue(isDeployed);

		assertNotNull(processService);
		// first start first instance
		long processInstanceId1 = processService.startProcess(deploymentUnit.getIdentifier(), "org.jbpm.signalWithExpression");
		assertNotNull(processInstanceId1);

		ProcessInstance pi = processService.getProcessInstance(processInstanceId1);
		assertNotNull(pi);
		// then start second instance
		long processInstanceId2 = processService.startProcess(deploymentUnit.getIdentifier(), "org.jbpm.signalWithExpression");
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
        
        long processInstanceId = processService.startProcess(deploymentUnit.getIdentifier(), "signal");
        assertNotNull(processInstanceId);
        
        long processInstanceId2 = processService.startProcess(deploymentUnit.getIdentifier(), "signal");
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
    		assertEquals("org.jbpm.services.api.DeploymentNotFoundException: Deployments org.jbpm.test:test-module:1.0.0 is not active", e.getMessage());
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
}
