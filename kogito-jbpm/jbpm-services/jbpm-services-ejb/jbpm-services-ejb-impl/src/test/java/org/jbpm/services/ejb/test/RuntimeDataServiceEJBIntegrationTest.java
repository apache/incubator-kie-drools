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
import org.jboss.arquillian.container.test.api.Deployment;
import org.jboss.arquillian.junit.Arquillian;
import org.jboss.shrinkwrap.api.ShrinkWrap;
import org.jboss.shrinkwrap.api.spec.WebArchive;
import org.jbpm.kie.services.impl.KModuleDeploymentUnit;
import org.jbpm.services.api.RuntimeDataService.EntryType;
import org.jbpm.services.api.model.DeploymentUnit;
import org.jbpm.services.api.model.NodeInstanceDesc;
import org.jbpm.services.api.model.ProcessDefinition;
import org.jbpm.services.api.model.ProcessInstanceDesc;
import org.jbpm.services.api.model.UserTaskInstanceDesc;
import org.jbpm.services.api.model.VariableDesc;
import org.jbpm.services.ejb.api.DeploymentServiceEJBLocal;
import org.jbpm.services.ejb.api.ProcessServiceEJBLocal;
import org.jbpm.services.ejb.api.RuntimeDataServiceEJBLocal;
import org.jbpm.services.ejb.api.UserTaskServiceEJBLocal;
import org.jbpm.services.ejb.impl.tx.TransactionalCommandServiceEJBImpl;
import org.jbpm.shared.services.impl.TransactionalCommandService;
import org.jbpm.shared.services.impl.commands.UpdateStringCommand;
import org.jbpm.workflow.instance.impl.WorkflowProcessInstanceImpl;
import org.jbpm.workflow.instance.node.WorkItemNodeInstance;
import org.junit.After;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.kie.api.KieServices;
import org.kie.api.builder.ReleaseId;
import org.kie.api.runtime.process.NodeInstance;
import org.kie.api.runtime.process.ProcessInstance;
import org.kie.api.runtime.query.QueryContext;
import org.kie.api.task.model.Status;
import org.kie.api.task.model.TaskSummary;
import org.kie.internal.KieInternalServices;
import org.kie.internal.process.CorrelationKey;
import org.kie.internal.query.QueryFilter;
import org.kie.internal.task.api.AuditTask;
import org.kie.internal.task.api.model.TaskEvent;
import org.kie.scanner.KieMavenRepository;

import static org.junit.Assert.*;
import static org.kie.scanner.KieMavenRepository.getKieMavenRepository;

@RunWith(Arquillian.class)
public class RuntimeDataServiceEJBIntegrationTest extends AbstractTestSupport {

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

	@EJB
    private UserTaskServiceEJBLocal userTaskService;

	@Test
    public void testGetProcessByDeploymentId() {
    	Collection<ProcessDefinition> definitions = runtimeDataService.getProcessesByDeploymentId(deploymentUnit.getIdentifier(), new QueryContext());
    	assertNotNull(definitions);

    	assertEquals(3, definitions.size());
    	List<String> expectedProcessIds = new ArrayList<String>();
    	expectedProcessIds.add("org.jbpm.writedocument.empty");
    	expectedProcessIds.add("org.jbpm.writedocument");
    	expectedProcessIds.add("org.jboss.qa.bpms.HumanTask");

    	for (ProcessDefinition def : definitions) {
    		assertTrue(expectedProcessIds.contains(def.getId()));
    	}
    }

    @Test
    public void testGetProcessByDeploymentIdAndProcessId() {
    	ProcessDefinition definition = runtimeDataService
    			.getProcessesByDeploymentIdProcessId(deploymentUnit.getIdentifier(), "org.jbpm.writedocument");

    	assertNotNull(definition);
    	assertEquals("org.jbpm.writedocument", definition.getId());
    }

    @Test
    public void testGetProcessByFilter() {
    	Collection<ProcessDefinition> definitions = runtimeDataService.getProcessesByFilter("org.jbpm", new QueryContext());

    	assertNotNull(definitions);
    	assertEquals(2, definitions.size());
    	List<String> expectedProcessIds = new ArrayList<String>();
    	expectedProcessIds.add("org.jbpm.writedocument.empty");
    	expectedProcessIds.add("org.jbpm.writedocument");

    	for (ProcessDefinition def : definitions) {
    		assertTrue(expectedProcessIds.contains(def.getId()));
    	}
    }

    @Test
    public void testGetProcessByProcessId() {
        Collection<ProcessDefinition> definition = runtimeDataService.getProcessesById("org.jbpm.writedocument");

        assertNotNull(definition);
        assertEquals(1, definition.size());
        assertEquals("org.jbpm.writedocument", definition.iterator().next().getId());
    }

    @Test
    public void testGetProcesses() {
    	Collection<ProcessDefinition> definitions = runtimeDataService.getProcesses(new QueryContext());
    	assertNotNull(definitions);

    	assertEquals(3, definitions.size());
    	List<String> expectedProcessIds = new ArrayList<String>();
    	expectedProcessIds.add("org.jbpm.writedocument.empty");
    	expectedProcessIds.add("org.jbpm.writedocument");
    	expectedProcessIds.add("org.jboss.qa.bpms.HumanTask");

    	for (ProcessDefinition def : definitions) {
    		assertTrue(expectedProcessIds.contains(def.getId()));
    	}
    }

    @Test
    public void testGetProcessIds() {
    	Collection<String> definitions = runtimeDataService.getProcessIds(deploymentUnit.getIdentifier(), new QueryContext());
    	assertNotNull(definitions);

    	assertEquals(3, definitions.size());

    	assertTrue(definitions.contains("org.jbpm.writedocument.empty"));
    	assertTrue(definitions.contains("org.jbpm.writedocument"));
    	assertTrue(definitions.contains("org.jboss.qa.bpms.HumanTask"));

    }

    @Test
    public void testGetProcessInstances() {
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
    	Collection<ProcessInstanceDesc> instances = runtimeDataService.getProcessInstances(new QueryContext());
    	assertNotNull(instances);
    	assertEquals(0, instances.size());

    	processInstanceId = processService.startProcess(deploymentUnit.getIdentifier(), "org.jbpm.writedocument");
    	assertNotNull(processInstanceId);
    	List<Integer> states = new ArrayList<Integer>();
    	// search for active only
    	states.add(1);

    	instances = runtimeDataService.getProcessInstances(states, "anonymous", new QueryContext());
    	assertNotNull(instances);
    	assertEquals(1, instances.size());
    	assertEquals(1, (int)instances.iterator().next().getState());

    	instances = runtimeDataService.getProcessInstances(states, "wrongUser", new QueryContext());
    	assertNotNull(instances);
    	assertEquals(0, instances.size());

    	processService.abortProcessInstance(processInstanceId);
    	processInstanceId = null;

    	instances = runtimeDataService.getProcessInstances(states, "anonymous", new QueryContext());
    	assertNotNull(instances);
    	assertEquals(0, instances.size());
    }

    @Test
    public void testGetProcessInstancesByDeploymentIdAndState() {
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
    public void testGetProcessInstancesByProcessIdAndStatus() {
    	Collection<ProcessInstanceDesc> instances = runtimeDataService.getProcessInstances(new QueryContext());
    	assertNotNull(instances);
    	assertEquals(0, instances.size());

    	processInstanceId = processService.startProcess(deploymentUnit.getIdentifier(), "org.jbpm.writedocument");
    	assertNotNull(processInstanceId);

    	Long processInstanceIdToAbort = processService.startProcess(deploymentUnit.getIdentifier(), "org.jbpm.writedocument");

    	List<Integer> statuses = new ArrayList<Integer>();
    	statuses.add(ProcessInstance.STATE_ACTIVE);

    	instances = runtimeDataService.getProcessInstancesByProcessDefinition("org.jbpm.writedocument", statuses, new QueryContext());
    	assertNotNull(instances);
    	assertEquals(2, instances.size());

    	for (ProcessInstanceDesc instance : instances) {
	    	assertEquals(ProcessInstance.STATE_ACTIVE, (int)instance.getState());
	    	assertEquals("org.jbpm.writedocument", instance.getProcessId());
    	}

    	processService.abortProcessInstance(processInstanceIdToAbort);

    	instances = runtimeDataService.getProcessInstancesByProcessDefinition("org.jbpm.writedocument", statuses, new QueryContext());
    	assertNotNull(instances);
    	assertEquals(1, instances.size());
    	ProcessInstanceDesc instance2 = instances.iterator().next();
    	assertEquals(ProcessInstance.STATE_ACTIVE, (int)instance2.getState());
    	assertEquals("org.jbpm.writedocument", instance2.getProcessId());

    	processService.abortProcessInstance(processInstanceId);
    	processInstanceId = null;

    	statuses.clear();
    	statuses.add(ProcessInstance.STATE_ABORTED);

    	instances = runtimeDataService.getProcessInstancesByProcessDefinition("org.jbpm.writedocument", statuses, new QueryContext());
    	assertNotNull(instances);
    	assertEquals(2, instances.size());

    	for (ProcessInstanceDesc instance : instances) {
	    	assertEquals(ProcessInstance.STATE_ABORTED, (int)instance.getState());
	    	assertEquals("org.jbpm.writedocument", instance.getProcessId());
    	}
    }

    @Test
    public void testGetProcessInstanceById() {
    	Collection<ProcessInstanceDesc> instances = runtimeDataService.getProcessInstances(new QueryContext());
    	assertNotNull(instances);
    	assertEquals(0, instances.size());

    	processInstanceId = processService.startProcess(deploymentUnit.getIdentifier(), "org.jbpm.writedocument");
    	assertNotNull(processInstanceId);

    	ProcessInstanceDesc instance = runtimeDataService.getProcessInstanceById(processInstanceId);
    	assertNotNull(instance);
    	assertEquals(1, (int)instance.getState());
    	assertEquals("org.jbpm.writedocument", instance.getProcessId());

    	processService.abortProcessInstance(processInstanceId);

    	instance = runtimeDataService.getProcessInstanceById(processInstanceId);
    	processInstanceId = null;
    	assertNotNull(instance);
    	assertEquals(3, (int)instance.getState());
    	assertEquals("org.jbpm.writedocument", instance.getProcessId());

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
    public void testGetProcessInstancesByProcessIdAndState() {
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
    	Collection<ProcessInstanceDesc> instances = runtimeDataService.getProcessInstances(new QueryContext());
    	assertNotNull(instances);
    	assertEquals(0, instances.size());

    	processInstanceId = processService.startProcess(deploymentUnit.getIdentifier(), "org.jbpm.writedocument");
    	assertNotNull(processInstanceId);
    	List<Integer> states = new ArrayList<Integer>();
    	// search for active only
    	states.add(1);

    	instances = runtimeDataService.getProcessInstancesByProcessId(states, "org.jbpm.writedocument", "anonymous", new QueryContext());
    	assertNotNull(instances);
    	assertEquals(1, instances.size());
    	assertEquals(1, (int)instances.iterator().next().getState());

    	instances = runtimeDataService.getProcessInstancesByProcessId(states, "org.jbpm.writedocument", "wrongUser", new QueryContext());
    	assertNotNull(instances);
    	assertEquals(0, instances.size());

    	processService.abortProcessInstance(processInstanceId);
    	processInstanceId = null;

    	instances = runtimeDataService.getProcessInstancesByProcessId(states, "org.jbpm.writedocument", "anonymous", new QueryContext());
    	assertNotNull(instances);
    	assertEquals(0, instances.size());
    }

    @Test
    public void testGetProcessInstancesByProcessNameAndState() {
    	Collection<ProcessInstanceDesc> instances = runtimeDataService.getProcessInstances(new QueryContext());
    	assertNotNull(instances);
    	assertEquals(0, instances.size());

    	processInstanceId = processService.startProcess(deploymentUnit.getIdentifier(), "org.jbpm.writedocument");
    	assertNotNull(processInstanceId);
    	List<Integer> states = new ArrayList<Integer>();
    	// search for aborted only
    	states.add(3);

    	instances = runtimeDataService.getProcessInstancesByProcessName(states, "humanTaskSample", null, new QueryContext());
    	assertNotNull(instances);
    	assertEquals(0, instances.size());

    	processService.abortProcessInstance(processInstanceId);
    	processInstanceId = null;

    	instances = runtimeDataService.getProcessInstancesByProcessName(states, "humanTaskSample", null, new QueryContext());
    	assertNotNull(instances);
    	assertEquals(1, instances.size());
    	assertEquals(3, (int)instances.iterator().next().getState());
    }

    @Test
    public void testGetProcessInstancesByPartialProcessNameAndState() {
    	Collection<ProcessInstanceDesc> instances = runtimeDataService.getProcessInstances(new QueryContext());
    	assertNotNull(instances);
    	assertEquals(0, instances.size());

    	processInstanceId = processService.startProcess(deploymentUnit.getIdentifier(), "org.jbpm.writedocument");
    	assertNotNull(processInstanceId);
    	List<Integer> states = new ArrayList<Integer>();
    	// search for aborted only
    	states.add(3);

    	instances = runtimeDataService.getProcessInstancesByProcessName(states, "human%", null, new QueryContext());
    	assertNotNull(instances);
    	assertEquals(0, instances.size());

    	processService.abortProcessInstance(processInstanceId);
    	processInstanceId = null;

    	instances = runtimeDataService.getProcessInstancesByProcessName(states, "human%", null, new QueryContext());
    	assertNotNull(instances);
    	assertEquals(1, instances.size());
    	assertEquals(3, (int)instances.iterator().next().getState());
    }

    @Test
    public void testGetProcessInstancesByProcessNameAndStateAndInitiator() {
    	Collection<ProcessInstanceDesc> instances = runtimeDataService.getProcessInstances(new QueryContext());
    	assertNotNull(instances);
    	assertEquals(0, instances.size());

    	processInstanceId = processService.startProcess(deploymentUnit.getIdentifier(), "org.jbpm.writedocument");
    	assertNotNull(processInstanceId);
    	List<Integer> states = new ArrayList<Integer>();
    	// search for active only
    	states.add(1);

    	instances = runtimeDataService.getProcessInstancesByProcessName(states, "humanTaskSample", "anonymous", new QueryContext());
    	assertNotNull(instances);
    	assertEquals(1, instances.size());
    	assertEquals(1, (int)instances.iterator().next().getState());

    	instances = runtimeDataService.getProcessInstancesByProcessName(states, "humanTaskSample", "wrongUser", new QueryContext());
    	assertNotNull(instances);
    	assertEquals(0, instances.size());

    	processService.abortProcessInstance(processInstanceId);
    	processInstanceId = null;

    	instances = runtimeDataService.getProcessInstancesByProcessName(states, "humanTaskSample", "anonymous", new QueryContext());
    	assertNotNull(instances);
    	assertEquals(0, instances.size());
    }

    @Test
    public void testGetProcessInstanceHistory() {

    	processInstanceId = processService.startProcess(deploymentUnit.getIdentifier(), "org.jbpm.writedocument");
    	assertNotNull(processInstanceId);

    	// get active nodes as history view
    	Collection<NodeInstanceDesc> instances = runtimeDataService.getProcessInstanceHistoryActive(processInstanceId, new QueryContext());
    	assertNotNull(instances);
    	assertEquals(1, instances.size());

    	// get completed nodes as history view
    	instances = runtimeDataService.getProcessInstanceHistoryCompleted(processInstanceId, new QueryContext());
    	assertNotNull(instances);
    	assertEquals(1, instances.size());

    	// get both active and completed nodes as history view
    	instances = runtimeDataService.getProcessInstanceFullHistory(processInstanceId, new QueryContext());
    	assertNotNull(instances);
    	assertEquals(3, instances.size());

    	// get nodes filtered by type - start
    	instances = runtimeDataService.getProcessInstanceFullHistoryByType(processInstanceId, EntryType.START, new QueryContext());
    	assertNotNull(instances);
    	assertEquals(2, instances.size());

    	// get nodes filtered by type - end
    	instances = runtimeDataService.getProcessInstanceFullHistoryByType(processInstanceId, EntryType.END, new QueryContext());
    	assertNotNull(instances);
    	assertEquals(1, instances.size());
    }

    @Test
    public void testGetNodeInstanceForWorkItem() {

    	processInstanceId = processService.startProcess(deploymentUnit.getIdentifier(), "org.jbpm.writedocument");
    	assertNotNull(processInstanceId);

    	ProcessInstance instance = processService.getProcessInstance(processInstanceId);
    	assertNotNull(instance);

    	Collection<NodeInstance> activeNodes = ((WorkflowProcessInstanceImpl) instance).getNodeInstances();
    	assertNotNull(activeNodes);
    	assertEquals(1, activeNodes.size());

    	NodeInstance node = activeNodes.iterator().next();
    	assertNotNull(node);
    	assertTrue(node instanceof WorkItemNodeInstance);

    	Long workItemId = ((WorkItemNodeInstance) node).getWorkItemId();
    	assertNotNull(workItemId);

    	NodeInstanceDesc desc = runtimeDataService.getNodeInstanceForWorkItem(workItemId);
    	assertNotNull(desc);
    	assertEquals(processInstanceId, desc.getProcessInstanceId());
    	assertEquals("Write a Document", desc.getName());
    	assertEquals("HumanTaskNode", desc.getNodeType());
    }

    @Test
    public void testGetVariableLogs() {
    	Map<String, Object> params = new HashMap<String, Object>();
    	params.put("approval_document", "initial content");
    	processInstanceId = processService.startProcess(deploymentUnit.getIdentifier(), "org.jbpm.writedocument", params);
    	assertNotNull(processInstanceId);

    	Collection<VariableDesc> variableLogs = runtimeDataService.getVariableHistory(processInstanceId, "approval_document", new QueryContext());
    	assertNotNull(variableLogs);
    	assertEquals(1, variableLogs.size());

    	processService.setProcessVariable(processInstanceId, "approval_document", "updated content");

    	variableLogs = runtimeDataService.getVariableHistory(processInstanceId, "approval_document", new QueryContext());
    	assertNotNull(variableLogs);
    	assertEquals(2, variableLogs.size());

    	processService.setProcessVariable(processInstanceId, "approval_reviewComment", "under review - content");

    	variableLogs = runtimeDataService.getVariablesCurrentState(processInstanceId);
    	assertNotNull(variableLogs);
    	assertEquals(2, variableLogs.size());

    	for (VariableDesc vDesc : variableLogs) {
    		if (vDesc.getVariableId().equals("approval_document")) {
    			assertEquals("updated content", vDesc.getNewValue());
    		} else if (vDesc.getVariableId().equals("approval_reviewComment")) {
    			assertEquals("under review - content", vDesc.getNewValue());
    		}
    	}
    }

    @Test
    public void testGetTaskByWorkItemId() {
    	processInstanceId = processService.startProcess(deploymentUnit.getIdentifier(), "org.jbpm.writedocument");
    	assertNotNull(processInstanceId);

    	ProcessInstance instance = processService.getProcessInstance(processInstanceId);
    	assertNotNull(instance);

    	Collection<NodeInstance> activeNodes = ((WorkflowProcessInstanceImpl) instance).getNodeInstances();
    	assertNotNull(activeNodes);
    	assertEquals(1, activeNodes.size());

    	NodeInstance node = activeNodes.iterator().next();
    	assertNotNull(node);
    	assertTrue(node instanceof WorkItemNodeInstance);

    	Long workItemId = ((WorkItemNodeInstance) node).getWorkItemId();
    	assertNotNull(workItemId);

    	UserTaskInstanceDesc userTask = runtimeDataService.getTaskByWorkItemId(workItemId);
    	assertNotNull(userTask);
    	assertEquals(processInstanceId, userTask.getProcessInstanceId());
    	assertEquals("Write a Document", userTask.getName());

    }

    @Test
    public void testGetTaskById() {
    	processInstanceId = processService.startProcess(deploymentUnit.getIdentifier(), "org.jbpm.writedocument");
    	assertNotNull(processInstanceId);

    	ProcessInstance instance = processService.getProcessInstance(processInstanceId);
    	assertNotNull(instance);

    	List<Long> taskIds = runtimeDataService.getTasksByProcessInstanceId(processInstanceId);
    	assertNotNull(taskIds);
    	assertEquals(1, taskIds.size());

    	Long taskId = taskIds.get(0);

    	UserTaskInstanceDesc userTask = runtimeDataService.getTaskById(taskId);
    	assertNotNull(userTask);
    	assertEquals(processInstanceId, userTask.getProcessInstanceId());
    	assertEquals("Write a Document", userTask.getName());

    }

    @Test
    public void testGetTaskOwned() {
    	processInstanceId = processService.startProcess(deploymentUnit.getIdentifier(), "org.jboss.qa.bpms.HumanTask");
    	assertNotNull(processInstanceId);

    	ProcessInstance instance = processService.getProcessInstance(processInstanceId);
    	assertNotNull(instance);

    	List<TaskSummary> tasks = runtimeDataService.getTasksOwned("john", new QueryFilter(0, 5));
    	assertNotNull(tasks);
    	assertEquals(1, tasks.size());

    	TaskSummary userTask = tasks.get(0);

    	assertNotNull(userTask);
    	assertEquals(processInstanceId, userTask.getProcessInstanceId());
    	assertEquals("Hello", userTask.getName());
    	assertEquals("john", userTask.getActualOwnerId());
    	assertEquals("Reserved", userTask.getStatusId());
    	assertNotNull(userTask.getActualOwner());
    }

    @Test
    public void testGetTaskAssignedAsBusinessAdmin() {
    	processInstanceId = processService.startProcess(deploymentUnit.getIdentifier(), "org.jbpm.writedocument");
    	assertNotNull(processInstanceId);

    	processService.getProcessInstance(processInstanceId);


    	List<TaskSummary> tasks = runtimeDataService.getTasksAssignedAsBusinessAdministrator("Administrator", new QueryFilter(0, 5));
    	assertNotNull(tasks);
    	assertEquals(1, tasks.size());

    	TaskSummary userTask = tasks.get(0);
    	assertNotNull(userTask);
    	assertEquals(processInstanceId, userTask.getProcessInstanceId());
    	assertEquals("Write a Document", userTask.getName());

    }

    @Test
    public void testGetTaskAssignedAsBusinessAdminPaging() {

    	for (int i = 0; i < 10; i++) {

    		processService.startProcess(deploymentUnit.getIdentifier(), "org.jbpm.writedocument");
    	}

    	List<TaskSummary> tasks = runtimeDataService.getTasksAssignedAsBusinessAdministrator("Administrator", new QueryFilter(0, 5));
    	assertNotNull(tasks);
    	assertEquals(5, tasks.size());

    	TaskSummary userTask = tasks.get(0);
    	assertNotNull(userTask);
    	assertEquals("Write a Document", userTask.getName());

    	Collection<ProcessInstanceDesc> activeProcesses = runtimeDataService.getProcessInstances(new QueryContext(0,  20));
    	for (ProcessInstanceDesc pi : activeProcesses) {
    		processService.abortProcessInstance(pi.getId());
    	}
    }

    @Test
    public void testGetTaskAssignedAsBusinessAdminPagingAndFiltering() {
    	long processInstanceId = -1;
    	for (int i = 0; i < 10; i++) {

    		processInstanceId = processService.startProcess(deploymentUnit.getIdentifier(), "org.jbpm.writedocument");
    	}

    	Map<String, Object> params = new HashMap<String, Object>();
        params.put("processInstanceId", processInstanceId);
		QueryFilter qf = new QueryFilter( "t.taskData.processInstanceId = :processInstanceId",
                            params, "t.id", false);
		qf.setOffset(0);
		qf.setCount(5);

    	List<TaskSummary> tasks = runtimeDataService.getTasksAssignedAsBusinessAdministrator("Administrator", qf);
    	assertNotNull(tasks);
    	assertEquals(1, tasks.size());

    	TaskSummary userTask = tasks.get(0);
    	assertNotNull(userTask);
    	assertEquals("Write a Document", userTask.getName());
    	assertEquals(processInstanceId, (long)userTask.getProcessInstanceId());

    	Collection<ProcessInstanceDesc> activeProcesses = runtimeDataService.getProcessInstances(new QueryContext(0,  20));
    	for (ProcessInstanceDesc pi : activeProcesses) {
    		processService.abortProcessInstance(pi.getId());
    	}
    }

    @Test
    public void testGetTasksAssignedAsPotentialOwnerByStatusPagingAndFiltering() {
    	List<Long> processInstanceIds = new ArrayList<Long>();
    	for (int i = 0; i < 10; i++) {

    		processInstanceIds.add(processService.startProcess(deploymentUnit.getIdentifier(), "org.jbpm.writedocument"));
    	}

    	Map<String, Object> params = new HashMap<String, Object>();
        params.put("processInstanceId", processInstanceIds);
		QueryFilter qf = new QueryFilter( "t.taskData.processInstanceId in (:processInstanceId)",
                            params, "t.id", false);
		qf.setOffset(0);
		qf.setCount(5);

		List<Status> statuses = new ArrayList<Status>();
		statuses.add(Status.Ready);
		statuses.add(Status.Reserved);

    	List<TaskSummary> tasks = runtimeDataService.getTasksAssignedAsPotentialOwnerByStatus("salaboy", statuses, qf);
    	assertNotNull(tasks);
    	assertEquals(5, tasks.size());

    	TaskSummary userTask = tasks.get(0);
    	assertNotNull(userTask);
    	assertEquals("Write a Document", userTask.getName());

    	Collection<ProcessInstanceDesc> activeProcesses = runtimeDataService.getProcessInstances(new QueryContext(0,  20));
    	for (ProcessInstanceDesc pi : activeProcesses) {
    		processService.abortProcessInstance(pi.getId());
    	}
    }

    @Test
    public void testTasksByStatusByProcessInstanceIdPagingAndFiltering() {

    	Long pid = processService.startProcess(deploymentUnit.getIdentifier(), "org.jbpm.writedocument");

    	List<Status> statuses = new ArrayList<Status>();
		statuses.add(Status.Ready);
		statuses.add(Status.Reserved);

    	List<TaskSummary> tasks = runtimeDataService.getTasksAssignedAsPotentialOwnerByStatus("salaboy", statuses, new QueryFilter(0, 5));
    	assertNotNull(tasks);
    	assertEquals(1, tasks.size());

    	long taskId = tasks.get(0).getId();

    	userTaskService.start(taskId, "salaboy");
    	userTaskService.complete(taskId, "salaboy", null);

    	Map<String, Object> params = new HashMap<String, Object>();
        params.put("name", "Review Document");
		QueryFilter qf = new QueryFilter( "t.name = :name",
                            params, "t.id", false);
		qf.setOffset(0);
		qf.setCount(5);


    	tasks = runtimeDataService.getTasksByStatusByProcessInstanceId(pid, statuses, qf);
    	assertNotNull(tasks);
    	assertEquals(1, tasks.size());

    	TaskSummary userTask = tasks.get(0);
    	assertNotNull(userTask);
    	assertEquals("Review Document", userTask.getName());

    	tasks = runtimeDataService.getTasksByStatusByProcessInstanceId(pid, statuses, new QueryFilter(0, 5));
    	assertNotNull(tasks);
    	assertEquals(2, tasks.size());

    	Collection<ProcessInstanceDesc> activeProcesses = runtimeDataService.getProcessInstances(new QueryContext(0,  20));
    	for (ProcessInstanceDesc pi : activeProcesses) {
    		processService.abortProcessInstance(pi.getId());
    	}
    }

    @Test
    public void testGetTaskAudit() {
    	processInstanceId = processService.startProcess(deploymentUnit.getIdentifier(), "org.jbpm.writedocument");
    	assertNotNull(processInstanceId);

    	ProcessInstance instance = processService.getProcessInstance(processInstanceId);
    	assertNotNull(instance);

    	Collection<NodeInstance> activeNodes = ((WorkflowProcessInstanceImpl) instance).getNodeInstances();
    	assertNotNull(activeNodes);
    	assertEquals(1, activeNodes.size());

    	NodeInstance node = activeNodes.iterator().next();
    	assertNotNull(node);
    	assertTrue(node instanceof WorkItemNodeInstance);

    	Long workItemId = ((WorkItemNodeInstance) node).getWorkItemId();
    	assertNotNull(workItemId);

    	List<AuditTask> auditTasks = runtimeDataService.getAllAuditTask("salaboy", new QueryFilter(0, 10));
    	assertNotNull(auditTasks);
    	assertEquals(1, auditTasks.size());
    	assertEquals("Write a Document", auditTasks.get(0).getName());

    }

    @Test
    public void testGetTaskEvents() {
    	processInstanceId = processService.startProcess(deploymentUnit.getIdentifier(), "org.jbpm.writedocument");
    	assertNotNull(processInstanceId);

    	ProcessInstance instance = processService.getProcessInstance(processInstanceId);
    	assertNotNull(instance);

    	Collection<NodeInstance> activeNodes = ((WorkflowProcessInstanceImpl) instance).getNodeInstances();
    	assertNotNull(activeNodes);
    	assertEquals(1, activeNodes.size());

    	NodeInstance node = activeNodes.iterator().next();
    	assertNotNull(node);
    	assertTrue(node instanceof WorkItemNodeInstance);

    	Long workItemId = ((WorkItemNodeInstance) node).getWorkItemId();
    	assertNotNull(workItemId);

    	UserTaskInstanceDesc userTask = runtimeDataService.getTaskByWorkItemId(workItemId);
    	assertNotNull(userTask);

    	List<TaskEvent> auditTasks = runtimeDataService.getTaskEvents(userTask.getTaskId(), new QueryFilter());
    	assertNotNull(auditTasks);
    	assertEquals(1, auditTasks.size());
    	assertEquals(TaskEvent.TaskEventType.ADDED, auditTasks.get(0).getType());

    	userTaskService.start(userTask.getTaskId(), "salaboy");

    	auditTasks = runtimeDataService.getTaskEvents(userTask.getTaskId(), new QueryFilter());
    	assertNotNull(auditTasks);
    	assertEquals(2, auditTasks.size());
    	assertEquals(TaskEvent.TaskEventType.ADDED, auditTasks.get(0).getType());
    	assertEquals(TaskEvent.TaskEventType.STARTED, auditTasks.get(1).getType());

    }

    @Test
    public void testGetProcessInstancesByVariable() {
        Map<String, Object> params = new HashMap<String, Object>();
        params.put("approval_document", "initial content");
        processInstanceId = processService.startProcess(deploymentUnit.getIdentifier(), "org.jbpm.writedocument", params);
        assertNotNull(processInstanceId);

        Collection<ProcessInstanceDesc> processInstanceLogs = runtimeDataService.getProcessInstancesByVariable("approval_document", null, new QueryContext());
        assertNotNull(processInstanceLogs);
        assertEquals(1, processInstanceLogs.size());

        processService.setProcessVariable(processInstanceId, "approval_document", "updated content");

        processInstanceLogs = runtimeDataService.getProcessInstancesByVariable("approval_reviewComment", null, new QueryContext());
        assertNotNull(processInstanceLogs);
        assertEquals(0, processInstanceLogs.size());

        processService.setProcessVariable(processInstanceId, "approval_reviewComment", "under review - content");

        processInstanceLogs = runtimeDataService.getProcessInstancesByVariable("approval_reviewComment", null, new QueryContext());
        assertNotNull(processInstanceLogs);
        assertEquals(1, processInstanceLogs.size());

        processService.abortProcessInstance(processInstanceId);
        processInstanceId = null;
    }

    @Test
    public void testGetProcessInstancesByVariableAndValue() {
        Map<String, Object> params = new HashMap<String, Object>();
        params.put("approval_document", "initial content");
        processInstanceId = processService.startProcess(deploymentUnit.getIdentifier(), "org.jbpm.writedocument", params);
        assertNotNull(processInstanceId);

        Collection<ProcessInstanceDesc> processInstanceLogs = runtimeDataService.getProcessInstancesByVariableAndValue("approval_document", "initial content", null, new QueryContext());
        assertNotNull(processInstanceLogs);
        assertEquals(1, processInstanceLogs.size());

        processService.setProcessVariable(processInstanceId, "approval_document", "updated content");

        processInstanceLogs = runtimeDataService.getProcessInstancesByVariableAndValue("approval_document", "initial content", null, new QueryContext());
        assertNotNull(processInstanceLogs);
        assertEquals(0, processInstanceLogs.size());

        processInstanceLogs = runtimeDataService.getProcessInstancesByVariableAndValue("approval_document", "updated content", null, new QueryContext());
        assertNotNull(processInstanceLogs);
        assertEquals(1, processInstanceLogs.size());

        processInstanceLogs = runtimeDataService.getProcessInstancesByVariableAndValue("approval_document", "updated%", null, new QueryContext());
        assertNotNull(processInstanceLogs);
        assertEquals(1, processInstanceLogs.size());

        processService.abortProcessInstance(processInstanceId);
        processInstanceId = null;

    }


    @Test
    public void testGetTasksByVariable() {
        Map<String, Object> params = new HashMap<String, Object>();
        params.put("approval_document", "initial content");
        processInstanceId = processService.startProcess(deploymentUnit.getIdentifier(), "org.jbpm.writedocument", params);
        assertNotNull(processInstanceId);

        List<TaskSummary> tasks = runtimeDataService.getTasksAssignedAsPotentialOwner("salaboy", null);
        assertNotNull(tasks);
        assertEquals(1, tasks.size());

        List<TaskSummary> tasksByVariable = runtimeDataService.
                taskSummaryQuery("salaboy")
                .variableName("TaskName").build().getResultList();
        assertNotNull(tasksByVariable);
        assertEquals(1, tasksByVariable.size());

        tasksByVariable = runtimeDataService.getTasksByVariable("salaboy", "ReviewComment", null, new QueryContext());
        assertNotNull(tasksByVariable);
        assertEquals(0, tasksByVariable.size());


        long taskId = tasks.get(0).getId();

        Map<String, Object> output = new HashMap<String, Object>();
        output.put("ReviewComment", "document reviewed");
        userTaskService.saveContent(taskId, output);

        tasksByVariable = runtimeDataService.getTasksByVariable("salaboy", "ReviewComment", null, new QueryContext());
        assertNotNull(tasksByVariable);
        assertEquals(1, tasksByVariable.size());

        processService.abortProcessInstance(processInstanceId);
        processInstanceId = null;
    }

    @Test
    public void testGetTasksByVariableAndValue() {
        Map<String, Object> params = new HashMap<String, Object>();
        params.put("approval_document", "initial content");
        processInstanceId = processService.startProcess(deploymentUnit.getIdentifier(), "org.jbpm.writedocument", params);
        assertNotNull(processInstanceId);

        List<TaskSummary> tasks = runtimeDataService.getTasksAssignedAsPotentialOwner("salaboy", null);
        assertNotNull(tasks);
        assertEquals(1, tasks.size());

        List<TaskSummary> tasksByVariable = runtimeDataService.getTasksByVariableAndValue("salaboy", "TaskName", "Write a Document", null, new QueryContext());
        assertNotNull(tasksByVariable);
        assertEquals(1, tasksByVariable.size());

        tasksByVariable = runtimeDataService.getTasksByVariableAndValue("salaboy", "TaskName", "Write", null, new QueryContext());
        assertNotNull(tasksByVariable);
        assertEquals(0, tasksByVariable.size());


        long taskId = tasks.get(0).getId();

        Map<String, Object> output = new HashMap<String, Object>();
        output.put("ReviewComment", "document reviewed");
        userTaskService.saveContent(taskId, output);

        tasksByVariable = runtimeDataService.getTasksByVariableAndValue("salaboy", "ReviewComment", "document reviewed", null, new QueryContext());
        assertNotNull(tasksByVariable);
        assertEquals(1, tasksByVariable.size());

        tasksByVariable = runtimeDataService.getTasksByVariableAndValue("salaboy", "ReviewComment", "document%", null, new QueryContext());
        assertNotNull(tasksByVariable);
        assertEquals(1, tasksByVariable.size());

        processService.abortProcessInstance(processInstanceId);
        processInstanceId = null;

    }
}
