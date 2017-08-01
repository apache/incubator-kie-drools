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
import javax.inject.Inject;

import org.drools.compiler.kie.builder.impl.InternalKieModule;
import org.jboss.arquillian.container.test.api.Deployment;
import org.jboss.arquillian.junit.Arquillian;
import org.jboss.shrinkwrap.api.ShrinkWrap;
import org.jboss.shrinkwrap.api.spec.WebArchive;
import org.jbpm.kie.services.impl.KModuleDeploymentUnit;
import org.jbpm.runtime.manager.impl.deploy.DeploymentDescriptorImpl;
import org.jbpm.services.api.model.DeploymentUnit;
import org.jbpm.services.api.model.ProcessInstanceDesc;
import org.jbpm.services.api.model.UserTaskInstanceDesc;
import org.jbpm.services.ejb.api.DeploymentServiceEJBLocal;
import org.jbpm.services.ejb.api.ProcessServiceEJBLocal;
import org.jbpm.services.ejb.api.RuntimeDataServiceEJBLocal;
import org.jbpm.services.ejb.impl.tx.TransactionalCommandServiceEJBImpl;
import org.jbpm.services.ejb.test.identity.TestIdentityProvider;
import org.jbpm.shared.services.impl.TransactionalCommandService;
import org.jbpm.shared.services.impl.commands.UpdateStringCommand;
import org.junit.After;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.kie.api.KieServices;
import org.kie.api.builder.ReleaseId;
import org.kie.api.runtime.process.ProcessInstance;
import org.kie.api.runtime.query.QueryContext;
import org.kie.api.task.model.Status;
import org.kie.api.task.model.TaskSummary;
import org.kie.internal.KieInternalServices;
import org.kie.internal.process.CorrelationKey;
import org.kie.internal.query.QueryFilter;
import org.kie.internal.runtime.conf.DeploymentDescriptor;
import org.kie.scanner.KieMavenRepository;

import static org.junit.Assert.*;
import static org.kie.scanner.KieMavenRepository.getKieMavenRepository;

@RunWith(Arquillian.class)
public class RuntimeDataServiceImplSecurityTest extends AbstractTestSupport {

	@Deployment
	public static WebArchive createDeployment() {
		File archive = new File("target/sample-war-ejb-app.war");
		if (!archive.exists()) {
			throw new IllegalStateException("There is no archive yet generated, run maven build or mvn assembly:assembly");
		}
		WebArchive war = ShrinkWrap.createFromZipFile(WebArchive.class, archive);
		war.addPackage("org.jbpm.services.ejb.test"); // test cases
		war.addPackage("org.jbpm.services.ejb.test.identity"); // test identity provider

		// deploy test kjar
		deployKjar();
		
		return war;
	}
	
	private Long processInstanceId = null;
    private KModuleDeploymentUnit deploymentUnit = null;
    
    @Before
    public void prepare() {
    	assertNotNull(deploymentService);
        
        deploymentUnit = new KModuleDeploymentUnit(GROUP_ID, ARTIFACT_ID, VERSION);
        
        deploymentService.deploy(deploymentUnit);
        units.add(deploymentUnit);
    	assertNotNull(processService);
    }
	
	protected static void deployKjar() {
		KieServices ks = KieServices.Factory.get();
        ReleaseId releaseId = ks.newReleaseId(GROUP_ID, ARTIFACT_ID, VERSION);
        List<String> processes = new ArrayList<String>();
        processes.add("processes/EmptyHumanTask.bpmn");
        processes.add("processes/humanTask.bpmn");
        processes.add("processes/SimpleHTProcess.bpmn2");
        
        DeploymentDescriptor customDescriptor = new DeploymentDescriptorImpl("org.jbpm.domain");
		customDescriptor.getBuilder()
		.addRequiredRole("view:managers");
		
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
        repository.installArtifact(releaseId, kJar1, pom);
	}
	
	private List<DeploymentUnit> units = new ArrayList<DeploymentUnit>();
	
    @After
    public void cleanup() {
    	if (processInstanceId != null) {
	    	// let's abort process instance to leave the system in clear state
	    	processService.abortProcessInstance(processInstanceId);
	    	
	    	ProcessInstance pi = processService.getProcessInstance(processInstanceId);    	
	    	assertNull(pi);
    	}
    	int deleted = 0;
        deleted += commandService.execute(new UpdateStringCommand("delete from  NodeInstanceLog nid"));
        deleted += commandService.execute(new UpdateStringCommand("delete from  ProcessInstanceLog pid"));        
        deleted += commandService.execute(new UpdateStringCommand("delete from  VariableInstanceLog vsd"));
        deleted += commandService.execute(new UpdateStringCommand("delete from  AuditTaskImpl vsd"));
        System.out.println("Deleted " + deleted);
    	cleanupSingletonSessionId();
        if (units != null && !units.isEmpty()) {
            for (DeploymentUnit unit : units) {
                deploymentService.undeploy(unit);
            }
            units.clear();
        }
    }
	
	@EJB
	private DeploymentServiceEJBLocal deploymentService;
	
	@EJB
	private ProcessServiceEJBLocal processService;
	
	@EJB
	private RuntimeDataServiceEJBLocal runtimeDataService;
	
	@EJB(beanInterface=TransactionalCommandServiceEJBImpl.class)
	private TransactionalCommandService commandService;
	
	@Inject
	private TestIdentityProvider identityProvider;
	
	@Test
    public void testGetProcessInstances() {
    	
    	// let's grant managers role so process can be started
    	List<String> roles = new ArrayList<String>();
    	roles.add("managers");
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
    public void testGetProcessInstancesByDeploymentIdAndState() {
    	
    	// let's grant managers role so process can be started
    	List<String> roles = new ArrayList<String>();
    	roles.add("managers");
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
    
    
    /*
     * same tests but for user who does not have access rights to that
     */
  
    @Test
    public void testGetProcessInstancesNoAccess() {
    	List<String> roles = new ArrayList<String>();
    	identityProvider.setRoles(roles);
    	
    	Collection<ProcessInstanceDesc> instances = runtimeDataService.getProcessInstances(new QueryContext());
    	assertNotNull(instances);
    	assertEquals(0, instances.size());
    	
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
    	List<String> roles = new ArrayList<String>();
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
    	assertEquals(0, instances.size());
    }
    
    
    @Test
    public void testGetProcessInstancesByDeploymentIdAndStateNoAccess() {
    	List<String> roles = new ArrayList<String>();
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
    	assertEquals(0, instances.size());
    }
    
    @Test
    public void testGetProcessInstancesByProcessIdNoAccess() {
    	List<String> roles = new ArrayList<String>();
    	identityProvider.setRoles(roles);
    	
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
    	List<String> roles = new ArrayList<String>();
    	identityProvider.setRoles(roles);
    	
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
    	List<String> roles = new ArrayList<String>();
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
    	assertEquals(0, instances.size());
    }
    
    @Test
    public void testGetProcessInstancesByPartialProcessIdAndStateNoAccess() {
    	List<String> roles = new ArrayList<String>();
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
    	assertEquals(0, instances.size());
    }
    
    @Test
    public void testGetProcessInstanceByCorrelationKey() {
        Collection<ProcessInstanceDesc> instances = runtimeDataService.getProcessInstances(new QueryContext());
        assertNotNull(instances);
        assertEquals(0, instances.size());
        
        CorrelationKey key = KieInternalServices.Factory.get().newCorrelationKeyFactory().newCorrelationKey("my business key");
        
        processInstanceId = processService.startProcess(deploymentUnit.getIdentifier(), "org.jbpm.writedocument", key);
        assertNotNull(processInstanceId);
        
        ProcessInstanceDesc instance = runtimeDataService.getProcessInstanceByCorrelationKey(key);
        assertNotNull(instance);
        assertEquals(1, (int)instance.getState());
        assertEquals("org.jbpm.writedocument", instance.getProcessId());
        assertEquals("my business key", instance.getCorrelationKey());
        
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
                
        instance = runtimeDataService.getProcessInstanceByCorrelationKey(key);
        processInstanceId = null;
        assertNull(instance);
        
    }
    
    @Test
    public void testGetProcessInstancesByCorrelationKey() {
        // let's grant managers role so process can be started
        List<String> roles = new ArrayList<String>();
        roles.add("managers");
        identityProvider.setRoles(roles);
        Collection<ProcessInstanceDesc> instances = runtimeDataService.getProcessInstances(new QueryContext());
        assertNotNull(instances);
        assertEquals(0, instances.size());
        
        CorrelationKey key = KieInternalServices.Factory.get().newCorrelationKeyFactory().newCorrelationKey("my business key");
        
        processInstanceId = processService.startProcess(deploymentUnit.getIdentifier(), "org.jbpm.writedocument", key);
        assertNotNull(processInstanceId);
        
        Collection<ProcessInstanceDesc> keyedInstances = runtimeDataService.getProcessInstancesByCorrelationKey(key, new QueryContext());
        assertNotNull(keyedInstances);
        assertEquals(1, keyedInstances.size());
        
        ProcessInstanceDesc instance = keyedInstances.iterator().next();
        
        assertNotNull(instance);
        assertEquals(1, (int)instance.getState());
        assertEquals("org.jbpm.writedocument", instance.getProcessId());
        assertEquals("my business key", instance.getCorrelationKey());
        
        List<UserTaskInstanceDesc> tasks = instance.getActiveTasks();
        assertNull(tasks);
        
        processService.abortProcessInstance(processInstanceId);
                
        instance = runtimeDataService.getProcessInstanceByCorrelationKey(key);
        processInstanceId = null;
        assertNull(instance);
        
        keyedInstances = runtimeDataService.getProcessInstancesByCorrelationKey(key, new QueryContext());
        assertNotNull(keyedInstances);
        assertEquals(1, keyedInstances.size());
        
        instance = keyedInstances.iterator().next();
        assertEquals(3, (int)instance.getState());
        assertEquals("org.jbpm.writedocument", instance.getProcessId());
        assertEquals("my business key", instance.getCorrelationKey());
        
    }
    
    @Test
    public void testGetProcessInstancesByPartialCorrelationKey() {
        // let's grant managers role so process can be started
        List<String> roles = new ArrayList<String>();
        roles.add("managers");
        identityProvider.setRoles(roles);
        Collection<ProcessInstanceDesc> instances = runtimeDataService.getProcessInstances(new QueryContext());
        assertNotNull(instances);
        assertEquals(0, instances.size());
        
        List<String> props = new ArrayList<String>();
        props.add("first");
        props.add("second");
        props.add("third");
        
        List<String> partial1props = new ArrayList<String>();
        partial1props.add("first");
        partial1props.add("second");
        
        List<String> partial2props = new ArrayList<String>();
        partial2props.add("first");        
        
        
        CorrelationKey key = KieInternalServices.Factory.get().newCorrelationKeyFactory().newCorrelationKey(props);
        CorrelationKey partialKey1 = KieInternalServices.Factory.get().newCorrelationKeyFactory().newCorrelationKey(partial1props);
        CorrelationKey partialKey2 = KieInternalServices.Factory.get().newCorrelationKeyFactory().newCorrelationKey(partial2props);
        
        processInstanceId = processService.startProcess(deploymentUnit.getIdentifier(), "org.jbpm.writedocument", key);
        assertNotNull(processInstanceId);
        
        Collection<ProcessInstanceDesc> keyedInstances = runtimeDataService.getProcessInstancesByCorrelationKey(key, new QueryContext());
        assertNotNull(keyedInstances);
        assertEquals(1, keyedInstances.size());
        
        ProcessInstanceDesc instance = keyedInstances.iterator().next();
        
        assertNotNull(instance);
        assertEquals(1, (int)instance.getState());
        assertEquals("org.jbpm.writedocument", instance.getProcessId());
        assertEquals("first:second:third", instance.getCorrelationKey());
        
        List<UserTaskInstanceDesc> tasks = instance.getActiveTasks();
        assertNull(tasks);
        // search by partial key 1
        keyedInstances = runtimeDataService.getProcessInstancesByCorrelationKey(partialKey1, new QueryContext());
        assertNotNull(keyedInstances);
        assertEquals(1, keyedInstances.size());
        
        instance = keyedInstances.iterator().next();
        
        assertNotNull(instance);
        assertEquals(1, (int)instance.getState());
        assertEquals("org.jbpm.writedocument", instance.getProcessId());
        assertEquals("first:second:third", instance.getCorrelationKey());
        
        // search by partial key 2
        keyedInstances = runtimeDataService.getProcessInstancesByCorrelationKey(partialKey2, new QueryContext());
        assertNotNull(keyedInstances);
        assertEquals(1, keyedInstances.size());
        
        instance = keyedInstances.iterator().next();
        
        assertNotNull(instance);
        assertEquals(1, (int)instance.getState());
        assertEquals("org.jbpm.writedocument", instance.getProcessId());
        assertEquals("first:second:third", instance.getCorrelationKey());
        
        
        processService.abortProcessInstance(processInstanceId);
        processInstanceId = null;
        
    }
 
}
