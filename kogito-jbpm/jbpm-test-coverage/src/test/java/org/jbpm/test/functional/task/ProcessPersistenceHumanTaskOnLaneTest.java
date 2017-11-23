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

package org.jbpm.test.functional.task;

import java.util.ArrayList;
import java.util.List;

import javax.persistence.EntityManager;

import org.jbpm.services.task.audit.impl.model.BAMTaskSummaryImpl;
import org.jbpm.services.task.lifecycle.listeners.BAMTaskEventListener;
import org.jbpm.test.JbpmTestCase;
import org.junit.Test;
import org.kie.api.runtime.KieSession;
import org.kie.api.runtime.manager.RuntimeEngine;
import org.kie.api.runtime.process.ProcessInstance;
import org.kie.api.task.TaskLifeCycleEventListener;
import org.kie.api.task.TaskService;
import org.kie.api.task.model.Status;
import org.kie.api.task.model.TaskSummary;
import org.kie.internal.task.api.EventService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import static org.junit.Assert.*;

/**
 * This is a sample file to test a process.
 */
public class ProcessPersistenceHumanTaskOnLaneTest extends JbpmTestCase {

    private static final Logger logger = LoggerFactory.getLogger(ProcessPersistenceHumanTaskOnLaneTest.class);

    public ProcessPersistenceHumanTaskOnLaneTest() {
        super(true, true);
        
    }

    @Test 
    public void testProcess() throws Exception {
        createRuntimeManager("org/jbpm/test/functional/task/HumanTaskOnLane.bpmn2");
        RuntimeEngine runtimeEngine = getRuntimeEngine();
        KieSession ksession = runtimeEngine.getKieSession();
        TaskService taskService = runtimeEngine.getTaskService();

        ProcessInstance processInstance = ksession.startProcess("UserTask");

        assertProcessInstanceActive(processInstance.getId());
        

        // simulating a system restart
        logger.debug("Reloading the environemnt to simulate system restart");
        disposeRuntimeManager();
        createRuntimeManager("org/jbpm/test/functional/task/HumanTaskOnLane.bpmn2");
        runtimeEngine = getRuntimeEngine();
        ksession = runtimeEngine.getKieSession();
        taskService = runtimeEngine.getTaskService();

        // let john execute Task 1
        String taskUser = "john";
        String locale = "en-UK";
        List<TaskSummary> list = taskService.getTasksAssignedAsPotentialOwner(taskUser, locale);
        assertEquals(1, list.size());
        
        TaskSummary task = list.get(0);
        taskService.claim(task.getId(), taskUser);
        taskService.start(task.getId(), taskUser);
        taskService.complete(task.getId(), taskUser, null);

        // simulating a system restart
        logger.debug("Reloading the environemnt to simulate system restart once again");
        
        List<Status> reservedOnly = new ArrayList<Status>();
        reservedOnly.add(Status.Reserved);
        
        list = taskService.getTasksAssignedAsPotentialOwnerByStatus(taskUser, reservedOnly, locale);
        assertEquals(1, list.size());
        
        task = list.get(0);
        taskService.start(task.getId(), taskUser);
        taskService.complete(task.getId(), taskUser, null);


        assertProcessInstanceCompleted(processInstance.getId());
    }
    
    @Test 
    public void testProcessWIthDifferentGroups() throws Exception {
        createRuntimeManager("org/jbpm/test/functional/task/HumanTaskOnLaneDifferentGroups.bpmn2");
        RuntimeEngine runtimeEngine = getRuntimeEngine();
        KieSession ksession = runtimeEngine.getKieSession();
        TaskService taskService = runtimeEngine.getTaskService();

        ProcessInstance processInstance = ksession.startProcess("UserTask");

        assertProcessInstanceActive(processInstance.getId());
        

        // simulating a system restart
        logger.debug("Reloading the environemnt to simulate system restart");
        disposeRuntimeManager();
        createRuntimeManager("org/jbpm/test/functional/task/HumanTaskOnLaneDifferentGroups.bpmn2");
        runtimeEngine = getRuntimeEngine();
        ksession = runtimeEngine.getKieSession();
        taskService = runtimeEngine.getTaskService();

        // let manager execute Task 1
        String taskUser = "manager";
        String locale = "en-UK";
        List<TaskSummary> list = taskService.getTasksAssignedAsPotentialOwner(taskUser, locale);
        assertEquals(1, list.size());
        
        TaskSummary task = list.get(0);
        taskService.claim(task.getId(), taskUser);
        taskService.start(task.getId(), taskUser);
        taskService.complete(task.getId(), taskUser, null);

        // simulating a system restart
        logger.debug("Reloading the environemnt to simulate system restart once again");
        disposeRuntimeManager();
        createRuntimeManager("org/jbpm/test/functional/task/HumanTaskOnLane.bpmn2");
        runtimeEngine = getRuntimeEngine();
        ksession = runtimeEngine.getKieSession();
        taskService = runtimeEngine.getTaskService();
        
        
        List<Status> reservedAndRegistered = new ArrayList<Status>();
        reservedAndRegistered.add(Status.Reserved);
        reservedAndRegistered.add(Status.Ready);
        // manager does not have access to the second task
        list = taskService.getTasksAssignedAsPotentialOwnerByStatus(taskUser, reservedAndRegistered, locale);
        assertEquals(0, list.size());
        
        // now try john 
        taskUser = "john";
        list = taskService.getTasksAssignedAsPotentialOwnerByStatus(taskUser, reservedAndRegistered, locale);
        assertEquals(1, list.size());
        
        task = list.get(0);
        // task is in ready state so claim is required
        assertEquals(Status.Ready, task.getStatus());
        taskService.claim(task.getId(), taskUser);
        taskService.start(task.getId(), taskUser);
        taskService.complete(task.getId(), taskUser, null);


        assertProcessInstanceCompleted(processInstance.getId());
    }

	@SuppressWarnings("unchecked")
	@Test
	public void testProcessWithBAMListener() throws Exception {
		createRuntimeManager("org/jbpm/test/functional/task/HumanTaskOnLane.bpmn2");
		RuntimeEngine runtimeEngine = getRuntimeEngine();
		KieSession ksession = runtimeEngine.getKieSession();
		TaskService taskService = runtimeEngine.getTaskService();

		((EventService<TaskLifeCycleEventListener>) taskService)
				.registerTaskEventListener(new BAMTaskEventListener(true));

		ProcessInstance processInstance = ksession.startProcess("UserTask");

		assertProcessInstanceActive(processInstance.getId());
		long task1 = -1;
		long task2 = -1;

		// simulating a system restart
		logger.debug("Reloading the environemnt to simulate system restart");
		disposeRuntimeManager();
		createRuntimeManager("org/jbpm/test/functional/task/HumanTaskOnLane.bpmn2");
		runtimeEngine = getRuntimeEngine();
		ksession = runtimeEngine.getKieSession();
		taskService = runtimeEngine.getTaskService();
		((EventService<TaskLifeCycleEventListener>) taskService)
				.registerTaskEventListener(new BAMTaskEventListener(true));

		// let john execute Task 1
		String taskUser = "john";
		String locale = "en-UK";
		List<TaskSummary> list = taskService.getTasksAssignedAsPotentialOwner(
				taskUser, locale);
		assertEquals(1, list.size());

		TaskSummary task = list.get(0);
		task1 = task.getId();
		taskService.claim(task.getId(), taskUser);
		taskService.start(task.getId(), taskUser);
		taskService.complete(task.getId(), taskUser, null);

		List<Status> reservedOnly = new ArrayList<Status>();
		reservedOnly.add(Status.Reserved);

		list = taskService.getTasksAssignedAsPotentialOwnerByStatus(taskUser,
				reservedOnly, locale);
		assertEquals(1, list.size());

		task = list.get(0);
		task2 = task.getId();
		taskService.start(task.getId(), taskUser);
		taskService.complete(task.getId(), taskUser, null);

		assertProcessInstanceCompleted(processInstance.getId());

		EntityManager em = getEmf().createEntityManager();
		List<BAMTaskSummaryImpl> bamLogs = em.createQuery(
				"from BAMTaskSummaryImpl").getResultList();
		em.close();
		assertNotNull(bamLogs);
		assertEquals(2, bamLogs.size());

		List<Long> taskIdsFromBAM = new ArrayList<Long>();
		for (BAMTaskSummaryImpl bamEntry : bamLogs) {
			taskIdsFromBAM.add(bamEntry.getTaskId());
		}

		assertTrue(taskIdsFromBAM.contains(task1));
		assertTrue(taskIdsFromBAM.contains(task2));
	}
}
