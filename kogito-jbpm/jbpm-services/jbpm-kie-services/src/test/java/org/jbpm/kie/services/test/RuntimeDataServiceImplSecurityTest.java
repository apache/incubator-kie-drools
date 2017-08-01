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

import org.drools.compiler.kie.builder.impl.InternalKieModule;
import org.jbpm.kie.services.impl.KModuleDeploymentUnit;
import org.jbpm.kie.test.util.AbstractKieServicesBaseTest;
import org.jbpm.kie.test.util.CountDownListenerFactory;
import org.jbpm.runtime.manager.impl.deploy.DeploymentDescriptorImpl;
import org.jbpm.services.api.ProcessInstanceNotFoundException;
import org.jbpm.services.api.model.DeploymentUnit;
import org.jbpm.services.api.model.ProcessInstanceDesc;
import org.jbpm.services.api.model.UserTaskInstanceDesc;
import org.junit.After;
import org.junit.Before;
import org.junit.Test;
import org.kie.api.KieServices;
import org.kie.api.builder.ReleaseId;
import org.kie.api.runtime.process.ProcessInstance;
import org.kie.api.runtime.query.QueryContext;
import org.kie.api.task.model.Status;
import org.kie.api.task.model.TaskSummary;
import org.kie.internal.query.QueryFilter;
import org.kie.internal.runtime.conf.DeploymentDescriptor;
import org.kie.internal.runtime.conf.ObjectModel;
import org.kie.scanner.KieMavenRepository;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import static org.junit.Assert.*;
import static org.kie.scanner.KieMavenRepository.getKieMavenRepository;

public class RuntimeDataServiceImplSecurityTest extends AbstractKieServicesBaseTest {

private static final Logger logger = LoggerFactory.getLogger(KModuleDeploymentServiceTest.class);   
    
    private List<DeploymentUnit> units = new ArrayList<DeploymentUnit>();
    protected String correctUser = "testUser";
    protected String wrongUser = "wrongUser";
    
    private Long processInstanceId = null;
    private KModuleDeploymentUnit deploymentUnit = null;

       
    @Before
    public void prepare() {
    	configureServices();
    	logger.debug("Preparing kjar");
        KieServices ks = KieServices.Factory.get();
        ReleaseId releaseId = ks.newReleaseId(GROUP_ID, ARTIFACT_ID, VERSION);
        List<String> processes = new ArrayList<String>();
        processes.add("repo/processes/general/EmptyHumanTask.bpmn");
        processes.add("repo/processes/general/humanTask.bpmn");
        processes.add("repo/processes/general/BPMN2-UserTask.bpmn2");
        processes.add("repo/processes/general/timer-process.bpmn2");
        
        DeploymentDescriptor customDescriptor = new DeploymentDescriptorImpl("org.jbpm.domain");
		customDescriptor.getBuilder()
		.addEventListener(new ObjectModel("mvel", "org.jbpm.kie.test.util.CountDownListenerFactory.get(\"securityTest\", \"timer\", 1)"))
		.addRequiredRole("view:managers")
		.addRequiredRole("execute:employees");
		
        Map<String, String> resources = new HashMap<String, String>();
		resources.put("src/main/resources/" + DeploymentDescriptor.META_INF_LOCATION, customDescriptor.toXml());
        
        InternalKieModule kJar1 = createKieJar(ks, releaseId, processes, resources);
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
        
        assertNotNull(deploymentService);
        
        deploymentUnit = new KModuleDeploymentUnit(GROUP_ID, ARTIFACT_ID, VERSION);
        
        deploymentService.deploy(deploymentUnit);
        units.add(deploymentUnit);
    	assertNotNull(processService);

    	identityProvider.setRoles(Arrays.asList("employees"));
    }
    
    @After
    public void cleanup() {
    	if (processInstanceId != null) {
    		try {
		    	// let's abort process instance to leave the system in clear state
		    	processService.abortProcessInstance(processInstanceId);
		    	
		    	ProcessInstance pi = processService.getProcessInstance(processInstanceId);    	
		    	assertNull(pi);
    		} catch (ProcessInstanceNotFoundException e) {
    			// ignore it as it was already completed/aborted
    		}
    	}
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
        CountDownListenerFactory.clear();
    }
    
    
    @Test
    public void testGetProcessInstances() {
    	
    	// let's grant managers role so process can be started
    	List<String> roles = new ArrayList<String>();
    	roles.add("managers");
    	roles.add("employees");
    	identityProvider.setRoles(roles);
    	
    	Collection<ProcessInstanceDesc> instances = runtimeDataService.getProcessInstances(new QueryContext());
    	assertNotNull(instances);
    	assertEquals(0, instances.size());
    	
    	processInstanceId = processService.startProcess(deploymentUnit.getIdentifier(), "org.jbpm.writedocument");
    	assertNotNull(processInstanceId);
    	
    	instances = runtimeDataService.getProcessInstances(new QueryContext());
    	assertNotNull(instances);
    	assertEquals(1, instances.size());
    	assertEquals(1, (int)instances.iterator().next().getState());

    	processService.abortProcessInstance(processInstanceId);
    	processInstanceId = null;
    	
    	instances = runtimeDataService.getProcessInstances(new QueryContext());
    	assertNotNull(instances);
    	assertEquals(1, instances.size());
    	assertEquals(3, (int)instances.iterator().next().getState());
 
    
    }
    
    @Test
    public void testGetProcessInstancesByState() {
    	
    	// let's grant managers role so process can be started
    	List<String> roles = new ArrayList<String>();
    	roles.add("managers");
    	roles.add("employees");
    	identityProvider.setRoles(roles);
    	
    	Collection<ProcessInstanceDesc> instances = runtimeDataService.getProcessInstances(new QueryContext());
    	assertNotNull(instances);
    	assertEquals(0, instances.size());
    	
    	processInstanceId = processService.startProcess(deploymentUnit.getIdentifier(), "org.jbpm.writedocument");
    	assertNotNull(processInstanceId);
    	List<Integer> states = new ArrayList<Integer>();
    	// search for aborted only
    	states.add(3);
    	
    	instances = runtimeDataService.getProcessInstances(states, null, new QueryContext());
    	assertNotNull(instances);
    	assertEquals(0, instances.size());
    	
    	processService.abortProcessInstance(processInstanceId);
    	processInstanceId = null;
    	
    	instances = runtimeDataService.getProcessInstances(states, null, new QueryContext());
    	assertNotNull(instances);
    	assertEquals(1, instances.size());
    	assertEquals(3, (int)instances.iterator().next().getState());
    }
    
    @Test
    public void testGetProcessInstancesByStateAndInitiator() {
    	
    	// let's grant managers role so process can be started
    	List<String> roles = new ArrayList<String>();
    	roles.add("managers");
    	roles.add("employees");
    	identityProvider.setRoles(roles);
    	
    	Collection<ProcessInstanceDesc> instances = runtimeDataService.getProcessInstances(new QueryContext());
    	assertNotNull(instances);
    	assertEquals(0, instances.size());
    	
    	processInstanceId = processService.startProcess(deploymentUnit.getIdentifier(), "org.jbpm.writedocument");
    	assertNotNull(processInstanceId);
    	List<Integer> states = new ArrayList<Integer>();
    	// search for active only
    	states.add(1);
    	
    	instances = runtimeDataService.getProcessInstances(states, correctUser, new QueryContext());
    	assertNotNull(instances);
    	assertEquals(1, instances.size());
    	assertEquals(1, (int)instances.iterator().next().getState());
    	
    	instances = runtimeDataService.getProcessInstances(states, wrongUser, new QueryContext());
    	assertNotNull(instances);
    	assertEquals(0, instances.size()); 
    	
    	processService.abortProcessInstance(processInstanceId);
    	processInstanceId = null;
    	
    	instances = runtimeDataService.getProcessInstances(states, correctUser, new QueryContext());
    	assertNotNull(instances);
    	assertEquals(0, instances.size());    	
    }
    
    @Test
    public void testGetProcessInstancesByDeploymentIdAndState() {
    	
    	// let's grant managers role so process can be started
    	List<String> roles = new ArrayList<String>();
    	roles.add("managers");
    	roles.add("employees");
    	identityProvider.setRoles(roles);
    	
    	Collection<ProcessInstanceDesc> instances = runtimeDataService.getProcessInstances(new QueryContext());
    	assertNotNull(instances);
    	assertEquals(0, instances.size());
    	
    	processInstanceId = processService.startProcess(deploymentUnit.getIdentifier(), "org.jbpm.writedocument");
    	assertNotNull(processInstanceId);
    	List<Integer> states = new ArrayList<Integer>();
    	// search for aborted only
    	states.add(3);
    	
    	instances = runtimeDataService.getProcessInstancesByDeploymentId(deploymentUnit.getIdentifier(), states, new QueryContext());
    	assertNotNull(instances);
    	assertEquals(0, instances.size());
    	
    	processService.abortProcessInstance(processInstanceId);
    	processInstanceId = null;
    	
    	instances = runtimeDataService.getProcessInstancesByDeploymentId(deploymentUnit.getIdentifier(), states, new QueryContext());
    	assertNotNull(instances);
    	assertEquals(1, instances.size());
    	assertEquals(3, (int)instances.iterator().next().getState());
    }
    
    @Test
    public void testGetProcessInstancesByProcessId() {
    	
    	// let's grant managers role so process can be started
    	List<String> roles = new ArrayList<String>();
    	roles.add("managers");
    	roles.add("employees");
    	identityProvider.setRoles(roles);
    	
    	Collection<ProcessInstanceDesc> instances = runtimeDataService.getProcessInstances(new QueryContext());
    	assertNotNull(instances);
    	assertEquals(0, instances.size());
    	
    	processInstanceId = processService.startProcess(deploymentUnit.getIdentifier(), "org.jbpm.writedocument");
    	assertNotNull(processInstanceId);
    	
    	instances = runtimeDataService.getProcessInstancesByProcessDefinition("org.jbpm.writedocument", new QueryContext());
    	assertNotNull(instances);
    	assertEquals(1, instances.size());
    	
    	ProcessInstanceDesc instance = instances.iterator().next();
    	assertEquals(1, (int)instance.getState());
    	assertEquals("org.jbpm.writedocument", instance.getProcessId());
    	List<TaskSummary> taskSummaries = runtimeDataService.getTasksAssignedAsPotentialOwner("salaboy", new QueryFilter(0, 10));
    	assertNotNull(taskSummaries);
        assertEquals(1, taskSummaries.size());
    	
    	processService.abortProcessInstance(processInstanceId);
    	processInstanceId = null;
    	
    	instances = runtimeDataService.getProcessInstancesByProcessDefinition("org.jbpm.writedocument", new QueryContext());
    	assertNotNull(instances);
    	assertEquals(1, instances.size());
    	instance = instances.iterator().next();
    	assertEquals(3, (int)instance.getState());
    	assertEquals("org.jbpm.writedocument", instance.getProcessId());
    }
    
    @Test
    public void testGetProcessInstanceById() {
    	
    	// let's grant managers role so process can be started
    	List<String> roles = new ArrayList<String>();
    	roles.add("managers");
    	roles.add("employees");
    	identityProvider.setRoles(roles);
    	
    	Collection<ProcessInstanceDesc> instances = runtimeDataService.getProcessInstances(new QueryContext());
    	assertNotNull(instances);
    	assertEquals(0, instances.size());

    	
    	processInstanceId = processService.startProcess(deploymentUnit.getIdentifier(), "org.jbpm.writedocument");
    	assertNotNull(processInstanceId);
    	
    	ProcessInstanceDesc instance = runtimeDataService.getProcessInstanceById(processInstanceId);
    	assertNotNull(instance);
    	assertEquals(1, (int)instance.getState());
    	assertEquals("org.jbpm.writedocument", instance.getProcessId());
    	
    	List<UserTaskInstanceDesc> tasks = instance.getActiveTasks();
    	assertNotNull(tasks);
    	assertEquals(1, tasks.size());
    	
    	UserTaskInstanceDesc activeTask = tasks.get(0);
    	assertNotNull(activeTask);
    	assertEquals(Status.Reserved.name(), activeTask.getStatus());
    	assertEquals(instance.getId(), activeTask.getProcessInstanceId());
    	assertEquals(instance.getProcessId(), activeTask.getProcessId());
    	assertEquals("Write a Document", activeTask.getName());
    	assertEquals("salaboy", activeTask.getActualOwner());
    	assertEquals(deploymentUnit.getIdentifier(), activeTask.getDeploymentId());
    	
    	processService.abortProcessInstance(processInstanceId);
    	    	
    	instance = runtimeDataService.getProcessInstanceById(processInstanceId);
    	processInstanceId = null;
    	assertNotNull(instance);
    	assertEquals(3, (int)instance.getState());
    	assertEquals("org.jbpm.writedocument", instance.getProcessId());
    	
    }
    
    @Test
    public void testGetProcessInstancesByProcessIdAndState() {
    	
    	// let's grant managers role so process can be started
    	List<String> roles = new ArrayList<String>();
    	roles.add("managers");
    	roles.add("employees");
    	identityProvider.setRoles(roles);
    	
    	Collection<ProcessInstanceDesc> instances = runtimeDataService.getProcessInstances(new QueryContext());
    	assertNotNull(instances);
    	assertEquals(0, instances.size());
    	
    	processInstanceId = processService.startProcess(deploymentUnit.getIdentifier(), "org.jbpm.writedocument");
    	assertNotNull(processInstanceId);
    	List<Integer> states = new ArrayList<Integer>();
    	// search for aborted only
    	states.add(3);
    	
    	instances = runtimeDataService.getProcessInstancesByProcessId(states, "org.jbpm.writedocument", null, new QueryContext());
    	assertNotNull(instances);
    	assertEquals(0, instances.size());
    	
    	processService.abortProcessInstance(processInstanceId);
    	processInstanceId = null;
    	
    	instances = runtimeDataService.getProcessInstancesByProcessId(states, "org.jbpm.writedocument", null, new QueryContext());
    	assertNotNull(instances);
    	assertEquals(1, instances.size());
    	assertEquals(3, (int)instances.iterator().next().getState());
    }
    
    @Test
    public void testGetProcessInstancesByPartialProcessIdAndState() {
    	
    	// let's grant managers role so process can be started
    	List<String> roles = new ArrayList<String>();
    	roles.add("managers");
    	roles.add("employees");
    	identityProvider.setRoles(roles);
    	
    	Collection<ProcessInstanceDesc> instances = runtimeDataService.getProcessInstances(new QueryContext());
    	assertNotNull(instances);
    	assertEquals(0, instances.size());
    	
    	processInstanceId = processService.startProcess(deploymentUnit.getIdentifier(), "org.jbpm.writedocument");
    	assertNotNull(processInstanceId);
    	List<Integer> states = new ArrayList<Integer>();
    	// search for aborted only
    	states.add(3);
    	
    	instances = runtimeDataService.getProcessInstancesByProcessId(states, "org.jbpm%", null, new QueryContext());
    	assertNotNull(instances);
    	assertEquals(0, instances.size());
    	
    	processService.abortProcessInstance(processInstanceId);
    	processInstanceId = null;
    	
    	instances = runtimeDataService.getProcessInstancesByProcessId(states, "org.jbpm%", null, new QueryContext());
    	assertNotNull(instances);
    	assertEquals(1, instances.size());
    	assertEquals(3, (int)instances.iterator().next().getState());
    }
    
    @Test
    public void testGetProcessInstancesByProcessIdAndStateAndInitiator() {
    	
    	// let's grant managers role so process can be started
    	List<String> roles = new ArrayList<String>();
    	roles.add("managers");
    	roles.add("employees");
    	identityProvider.setRoles(roles);
    	
    	Collection<ProcessInstanceDesc> instances = runtimeDataService.getProcessInstances(new QueryContext());
    	assertNotNull(instances);
    	assertEquals(0, instances.size());
    	
    	processInstanceId = processService.startProcess(deploymentUnit.getIdentifier(), "org.jbpm.writedocument");
    	assertNotNull(processInstanceId);
    	List<Integer> states = new ArrayList<Integer>();
    	// search for active only
    	states.add(1);
    	
    	instances = runtimeDataService.getProcessInstancesByProcessId(states, "org.jbpm.writedocument", correctUser, new QueryContext());
    	assertNotNull(instances);
    	assertEquals(1, instances.size());
    	assertEquals(1, (int)instances.iterator().next().getState());
    	
    	instances = runtimeDataService.getProcessInstancesByProcessId(states, "org.jbpm.writedocument", wrongUser, new QueryContext());
    	assertNotNull(instances);
    	assertEquals(0, instances.size()); 
    	
    	processService.abortProcessInstance(processInstanceId);
    	processInstanceId = null;
    	
    	instances = runtimeDataService.getProcessInstancesByProcessId(states, "org.jbpm.writedocument", correctUser, new QueryContext());
    	assertNotNull(instances);
    	assertEquals(0, instances.size());    	
    }
    
    /*
     * same tests but for user who does not have access rights to that
     */
  
    @Test
    public void testGetProcessInstancesNoAccess() {
    	Collection<ProcessInstanceDesc> instances = runtimeDataService.getProcessInstances(new QueryContext());
    	assertNotNull(instances);
    	assertEquals(0, instances.size());
    	
    	// let's grant managers role so process can be started
    	List<String> roles = new ArrayList<String>();
    	roles.add("managers");
    	roles.add("employees");
    	identityProvider.setRoles(roles);
    	
    	processInstanceId = processService.startProcess(deploymentUnit.getIdentifier(), "org.jbpm.writedocument");
    	assertNotNull(processInstanceId);
    	
    	instances = runtimeDataService.getProcessInstances(new QueryContext());
    	assertNotNull(instances);
    	assertEquals(0, instances.size());

    	processService.abortProcessInstance(processInstanceId);
    	processInstanceId = null;
    	
    	instances = runtimeDataService.getProcessInstances(new QueryContext());
    	assertNotNull(instances);
    	assertEquals(0, instances.size());
 
    }
    
    @Test
    public void testGetProcessInstancesByStateNoAccess() {
    	Collection<ProcessInstanceDesc> instances = runtimeDataService.getProcessInstances(new QueryContext());
    	assertNotNull(instances);
    	assertEquals(0, instances.size());
    	
    	// let's grant managers role so process can be started
    	List<String> roles = new ArrayList<String>();
    	roles.add("managers");
    	roles.add("employees");
    	identityProvider.setRoles(roles);
    	
    	processInstanceId = processService.startProcess(deploymentUnit.getIdentifier(), "org.jbpm.writedocument");
    	assertNotNull(processInstanceId);
    	List<Integer> states = new ArrayList<Integer>();
    	// search for aborted only
    	states.add(3);
    	
    	instances = runtimeDataService.getProcessInstances(states, null, new QueryContext());
    	assertNotNull(instances);
    	assertEquals(0, instances.size());
    	
    	processService.abortProcessInstance(processInstanceId);
    	processInstanceId = null;
    	
    	instances = runtimeDataService.getProcessInstances(states, null, new QueryContext());
    	assertNotNull(instances);
    	assertEquals(0, instances.size());
    }
    
    @Test
    public void testGetProcessInstancesByStateAndInitiatorNoAccess() {
    	Collection<ProcessInstanceDesc> instances = runtimeDataService.getProcessInstances(new QueryContext());
    	assertNotNull(instances);
    	assertEquals(0, instances.size());
    	
    	// let's grant managers role so process can be started
    	List<String> roles = new ArrayList<String>();
    	roles.add("managers");
    	roles.add("employees");
    	identityProvider.setRoles(roles);
    	
    	processInstanceId = processService.startProcess(deploymentUnit.getIdentifier(), "org.jbpm.writedocument");
    	assertNotNull(processInstanceId);
    	List<Integer> states = new ArrayList<Integer>();
    	// search for active only
    	states.add(1);
    	
    	instances = runtimeDataService.getProcessInstances(states, correctUser, new QueryContext());
    	assertNotNull(instances);
    	assertEquals(0, instances.size());
    	
    	instances = runtimeDataService.getProcessInstances(states, wrongUser, new QueryContext());
    	assertNotNull(instances);
    	assertEquals(0, instances.size()); 
    	
    	processService.abortProcessInstance(processInstanceId);
    	processInstanceId = null;
    	
    	instances = runtimeDataService.getProcessInstances(states, correctUser, new QueryContext());
    	assertNotNull(instances);
    	assertEquals(0, instances.size());    	
    }
    
    @Test
    public void testGetProcessInstancesByDeploymentIdAndStateNoAccess() {
    	Collection<ProcessInstanceDesc> instances = runtimeDataService.getProcessInstances(new QueryContext());
    	assertNotNull(instances);
    	assertEquals(0, instances.size());
    	
    	processInstanceId = processService.startProcess(deploymentUnit.getIdentifier(), "org.jbpm.writedocument");
    	assertNotNull(processInstanceId);
    	List<Integer> states = new ArrayList<Integer>();
    	// search for aborted only
    	states.add(3);
    	
    	instances = runtimeDataService.getProcessInstancesByDeploymentId(deploymentUnit.getIdentifier(), states, new QueryContext());
    	assertNotNull(instances);
    	assertEquals(0, instances.size());
    	
    	processService.abortProcessInstance(processInstanceId);
    	processInstanceId = null;
    	
    	instances = runtimeDataService.getProcessInstancesByDeploymentId(deploymentUnit.getIdentifier(), states, new QueryContext());
    	assertNotNull(instances);
    	assertEquals(0, instances.size());
    }
    
    @Test
    public void testGetProcessInstancesByProcessIdNoAccess() {
    	Collection<ProcessInstanceDesc> instances = runtimeDataService.getProcessInstances(new QueryContext());
    	assertNotNull(instances);
    	assertEquals(0, instances.size());
    	
    	processInstanceId = processService.startProcess(deploymentUnit.getIdentifier(), "org.jbpm.writedocument");
    	assertNotNull(processInstanceId);
    	
    	instances = runtimeDataService.getProcessInstancesByProcessDefinition("org.jbpm.writedocument", new QueryContext());
    	assertNotNull(instances);
    	assertEquals(0, instances.size());
    	
    	processService.abortProcessInstance(processInstanceId);
    	processInstanceId = null;
    	
    	instances = runtimeDataService.getProcessInstancesByProcessDefinition("org.jbpm.writedocument", new QueryContext());
    	assertNotNull(instances);
    	assertEquals(0, instances.size());    	
    }
    
    @Test
    public void testGetProcessInstanceByIdNoAccess() {
    	Collection<ProcessInstanceDesc> instances = runtimeDataService.getProcessInstances(new QueryContext());
    	assertNotNull(instances);
    	assertEquals(0, instances.size());
    	
    	processInstanceId = processService.startProcess(deploymentUnit.getIdentifier(), "org.jbpm.writedocument");
    	assertNotNull(processInstanceId);
    	
    	ProcessInstanceDesc instance = runtimeDataService.getProcessInstanceById(processInstanceId);
    	assertNotNull(instance);
    	
    	processService.abortProcessInstance(processInstanceId);
    	    	
    	instance = runtimeDataService.getProcessInstanceById(processInstanceId);
    	processInstanceId = null;
    	assertNotNull(instance);
    	
    }
    
    @Test
    public void testGetProcessInstancesByProcessIdAndStateNoAccess() {
    	Collection<ProcessInstanceDesc> instances = runtimeDataService.getProcessInstances(new QueryContext());
    	assertNotNull(instances);
    	assertEquals(0, instances.size());
    	
    	processInstanceId = processService.startProcess(deploymentUnit.getIdentifier(), "org.jbpm.writedocument");
    	assertNotNull(processInstanceId);
    	List<Integer> states = new ArrayList<Integer>();
    	// search for aborted only
    	states.add(3);
    	
    	instances = runtimeDataService.getProcessInstancesByProcessId(states, "org.jbpm.writedocument", null, new QueryContext());
    	assertNotNull(instances);
    	assertEquals(0, instances.size());
    	
    	processService.abortProcessInstance(processInstanceId);
    	processInstanceId = null;
    	
    	instances = runtimeDataService.getProcessInstancesByProcessId(states, "org.jbpm.writedocument", null, new QueryContext());
    	assertNotNull(instances);
    	assertEquals(0, instances.size());
    }
    
    @Test
    public void testGetProcessInstancesByPartialProcessIdAndStateNoAccess() {
    	Collection<ProcessInstanceDesc> instances = runtimeDataService.getProcessInstances(new QueryContext());
    	assertNotNull(instances);
    	assertEquals(0, instances.size());
    	
    	processInstanceId = processService.startProcess(deploymentUnit.getIdentifier(), "org.jbpm.writedocument");
    	assertNotNull(processInstanceId);
    	List<Integer> states = new ArrayList<Integer>();
    	// search for aborted only
    	states.add(3);
    	
    	instances = runtimeDataService.getProcessInstancesByProcessId(states, "org.jbpm%", null, new QueryContext());
    	assertNotNull(instances);
    	assertEquals(0, instances.size());
    	
    	processService.abortProcessInstance(processInstanceId);
    	processInstanceId = null;
    	
    	instances = runtimeDataService.getProcessInstancesByProcessId(states, "org.jbpm%", null, new QueryContext());
    	assertNotNull(instances);
    	assertEquals(0, instances.size());
    }
    
    @Test
    public void testGetProcessInstancesByProcessIdAndStateAndInitiatorNoAccess() {
    	Collection<ProcessInstanceDesc> instances = runtimeDataService.getProcessInstances(new QueryContext());
    	assertNotNull(instances);
    	assertEquals(0, instances.size());
    	
    	processInstanceId = processService.startProcess(deploymentUnit.getIdentifier(), "org.jbpm.writedocument");
    	assertNotNull(processInstanceId);
    	List<Integer> states = new ArrayList<Integer>();
    	// search for active only
    	states.add(1);
    	
    	instances = runtimeDataService.getProcessInstancesByProcessId(states, "org.jbpm.writedocument", correctUser, new QueryContext());
    	assertNotNull(instances);
    	assertEquals(0, instances.size());
    	
    	instances = runtimeDataService.getProcessInstancesByProcessId(states, "org.jbpm.writedocument", wrongUser, new QueryContext());
    	assertNotNull(instances);
    	assertEquals(0, instances.size()); 
    	
    	processService.abortProcessInstance(processInstanceId);
    	processInstanceId = null;
    	
    	instances = runtimeDataService.getProcessInstancesByProcessId(states, "org.jbpm.writedocument", correctUser, new QueryContext());
    	assertNotNull(instances);
    	assertEquals(0, instances.size());    	
    }
    
    @Test(timeout=10000)
    public void testGetProcessInstancesWithTimer() throws Exception {
        
        // let's grant managers role so process can be started
        List<String> roles = new ArrayList<String>();
        roles.add("managers");
        roles.add("employees");
        identityProvider.setRoles(roles);
        
        Collection<ProcessInstanceDesc> instances = runtimeDataService.getProcessInstances(new QueryContext());
        assertNotNull(instances);
        assertEquals(0, instances.size());
        
        processInstanceId = processService.startProcess(deploymentUnit.getIdentifier(), "timerprocess");
        assertNotNull(processInstanceId);                
        
        instances = runtimeDataService.getProcessInstances(new QueryContext());
        assertNotNull(instances);
        assertEquals(1, instances.size());
        assertEquals(1, (int)instances.iterator().next().getState());
        
        identityProvider.setRoles(new ArrayList<>());
        
        CountDownListenerFactory.getExisting("securityTest").waitTillCompleted();
        
        identityProvider.setRoles(roles);
        instances = runtimeDataService.getProcessInstances(new QueryContext());
        assertNotNull(instances);
        assertEquals(1, instances.size());
        assertEquals(2, (int)instances.iterator().next().getState());
 
    
    }
    
 
}
