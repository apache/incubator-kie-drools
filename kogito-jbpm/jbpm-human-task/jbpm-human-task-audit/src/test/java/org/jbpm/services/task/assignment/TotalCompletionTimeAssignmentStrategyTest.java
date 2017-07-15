/*
 * Copyright 2017 Red Hat, Inc. and/or its affiliates.
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 *
 *      http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
*/
package org.jbpm.services.task.assignment;

import static org.junit.Assert.assertEquals;

import java.util.HashMap;
import java.util.Map;

import javax.persistence.EntityManagerFactory;
import javax.persistence.Persistence;

import org.assertj.core.util.Arrays;
import org.jbpm.services.task.HumanTaskServiceFactory;
import org.jbpm.services.task.assignment.impl.strategy.LoadBalanceAssignmentStrategy;
import org.jbpm.services.task.audit.JPATaskLifeCycleEventListener;
import org.jbpm.services.task.lifecycle.listeners.BAMTaskEventListener;
import org.jbpm.services.task.utils.TaskFluent;
import org.jbpm.test.util.PoolingDataSource;
import org.junit.After;
import org.junit.Before;
import org.junit.Test;
import org.kie.api.task.model.Task;
import org.kie.internal.task.api.InternalTaskService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class TotalCompletionTimeAssignmentStrategyTest extends AbstractAssignmentTest {
    private PoolingDataSource pds;
    private EntityManagerFactory emf;
    private Long taskIds[];
    private static final Logger logger = LoggerFactory.getLogger(TotalCompletionTimeAssignmentStrategyTest.class);
    private static final String DARTH_VADER = "Darth Vader";
    private static final String BOBBA_FET = "Bobba Fet";
    private static final String LUKE_CAGE = "Luke Cage";
    private static final String TONY_STARK = "Tony Stark";
    private static final String DEPLOYMENT_ID = "org.jbpm:jbpm-human-task:7.1.0";
    private static final String PROCESS_ID = "testing tasks";
    private static final Map<String,Object> data = new HashMap<>();
    

    /**
     * Creates tasks and completes them so that there is
     * BAMTaskSummary data
     */
    private void forceBAMEntries() {
    	for (int count = 0; count < 10; count++) {
    		TaskFluent task1 = new TaskFluent()
    				.setName("MultiUserLoadBalanceTask1")
    				.addPotentialUser(BOBBA_FET)
    				.addPotentialUser(DARTH_VADER)
    				.addPotentialUser(LUKE_CAGE)
    				.setAdminUser("Administrator")
    				.setDeploymentID(DEPLOYMENT_ID)
    				.setProcessId(PROCESS_ID);
    		long taskId = createTaskWithoutAssert(task1);
    		int waitTime = 100;
    		if (count%3 == 0) {
    			waitTime = 230;
    		} else if (count%2 == 0) {
    			waitTime = 180;
    		}
    		completeTask(taskId,waitTime);
    	}
    	for (int count = 0; count < 10; count++) {
    		TaskFluent task2 = new TaskFluent()
    				.setName("MultiUserLoadBalanceTask2")
    				.addPotentialUser(BOBBA_FET)
    				.addPotentialUser(DARTH_VADER)
    				.addPotentialUser(LUKE_CAGE)
    				.setAdminUser("Administrator")
    				.setDeploymentID(DEPLOYMENT_ID)
    				.setProcessId(PROCESS_ID);
    		long taskId = createTaskWithoutAssert(task2);
    		int waitTime = 1000;
    		if (count%3 == 0) {
    			waitTime = 2300;
    		} else if (count%2 == 0) {
    			waitTime = 1800;
    		}
    		completeTask(taskId,waitTime);
    	}
    }

	@Before
	public void setUp() throws Exception {
        System.setProperty("org.jbpm.task.assignment.enabled", "true");
        System.setProperty("org.jbpm.task.assignment.strategy", "LoadBalance");
        System.setProperty("org.jbpm.task.assignment.loadbalance.calculator","org.jbpm.services.task.assignment.impl.TotalCompletionTimeLoadCalculator");
        System.setProperty("org.jbpm.services.task.assignment.taskduration.timetolive", "1000"); 
        pds = setupPoolingDataSource();
        emf = Persistence.createEntityManagerFactory( "org.jbpm.services.task" );

        AssignmentServiceProvider.override(new LoadBalanceAssignmentStrategy());

        this.taskService = (InternalTaskService) HumanTaskServiceFactory.newTaskServiceConfigurator()
                .entityManagerFactory(emf)
				.listener(new JPATaskLifeCycleEventListener(true))
				.listener(new BAMTaskEventListener(true))
                .getTaskService();
        taskIds = new Long[100]; // giving ourselves lots of room
        forceBAMEntries();
	}

	@After
	public void clean() throws Exception {
        System.clearProperty("org.jbpm.task.assignment.enabled");
        System.clearProperty("org.jbpm.task.assignment.strategy");
        System.clearProperty("org.jbpm.task.assignment.loadbalance.calculator");
        AssignmentServiceProvider.clear();
        if (emf != null) {
            emf.close();
        }
        if (pds != null) {
            pds.close();
        }
	}
	

	@Test
	public void testMultipleUser() {
		String expectedOwners[] = Arrays.array(BOBBA_FET,DARTH_VADER,LUKE_CAGE);
		// Everyone gets two of these tasks to start
		for (int x = 0; x < 6; x++) {
			TaskFluent task1 = new TaskFluent()
					.setName("MultiUserLoadBalanceTask1")
					.addPotentialUser(BOBBA_FET)
					.addPotentialUser(DARTH_VADER)
					.addPotentialUser(LUKE_CAGE)
    				.setDeploymentID(DEPLOYMENT_ID)
    				.setProcessId(PROCESS_ID)
					.setAdminUser("Administrator");
			taskIds[x] = createAndAssertTask(task1,expectedOwners[x%3]);
		}
		
		// Complete Darth's first task
		completeTask(taskIds[1],500);
		
		// Darth moves to the front since
		// he has less active tasks
		TaskFluent task2 = new TaskFluent()
				.setName("MultiUserLoadBalanceTask2")
				.addPotentialUser(BOBBA_FET)
				.addPotentialUser(DARTH_VADER)
				.addPotentialUser(LUKE_CAGE)
				.setDeploymentID(DEPLOYMENT_ID)
				.setProcessId(PROCESS_ID)
				.setAdminUser("Administrator");
		createAndAssertTask(task2,DARTH_VADER);

		logger.info("testMultipleUser completed");
	}

	@Test
	public void testMultipleUserWithGroup() {
		String expectedOwners[] = Arrays.array(BOBBA_FET,LUKE_CAGE,TONY_STARK);
		// While the Crusaders (the group containing Tony Stark)
		// is added before Luke Cage, Luke will show up as the 
		// second assignee. This is because group members are always
		// added to the end of the potential owners list
		for (int x = 0; x < 3; x++) {
			TaskFluent task1 = new TaskFluent()
					.setName("MultiUserLoadBalanceTask1")
					.addPotentialUser(BOBBA_FET)
					.addPotentialGroup("Crusaders")
					.addPotentialUser(LUKE_CAGE)
    				.setDeploymentID(DEPLOYMENT_ID)
    				.setProcessId(PROCESS_ID)
					.setAdminUser("Administrator");
			taskIds[x] = createAndAssertTask(task1,expectedOwners[x]);
		}
		
		// Complete Luke's task
		completeTask(taskIds[1],1000);

		// Luke jumps to the front of the expected owners
		// because he has no active tasks
		expectedOwners = Arrays.array(LUKE_CAGE,BOBBA_FET,TONY_STARK);
		for (int x = 3; x < 6; x++) {
			TaskFluent task2 = new TaskFluent()
					.setName("MultiUserLoadBalanceTask2")
					.addPotentialUser(BOBBA_FET)
					.addPotentialGroup("Crusaders")
					.addPotentialUser(LUKE_CAGE)
    				.setDeploymentID(DEPLOYMENT_ID)
    				.setProcessId(PROCESS_ID)
					.setAdminUser("Administrator");
			taskIds[x] = createAndAssertTask(task2,expectedOwners[x-3]);
		}

		logger.info("testMultipleUserWithGroup completed");
	}
	
	@Test
	public void testMultipleUserWithAdd() {
		String expectedOwners[] = Arrays.array(BOBBA_FET,DARTH_VADER,LUKE_CAGE,TONY_STARK);
		for (int x = 0; x < 3; x++) {
			TaskFluent task1 = new TaskFluent()
					.setName("MultiUserLoadBalanceTask1")
					.addPotentialUser(BOBBA_FET)
					.addPotentialUser(DARTH_VADER)
					.addPotentialUser(LUKE_CAGE)
    				.setDeploymentID(DEPLOYMENT_ID)
    				.setProcessId(PROCESS_ID)
					.setAdminUser("Administrator");
			taskIds[x] = createAndAssertTask(task1,expectedOwners[x]);
		}
		
		// Complete Darth Vader's task
		completeTask(taskIds[1],1000);
		
		// Create a new task and see if 
		// Darth gets assigned
		TaskFluent task2 = new TaskFluent()
				.setName("MultiUserLoadBalanceTask2")
				.addPotentialUser(BOBBA_FET)
				.addPotentialUser(DARTH_VADER)
				.addPotentialUser(LUKE_CAGE)
				.setDeploymentID(DEPLOYMENT_ID)
				.setProcessId(PROCESS_ID)
				.setAdminUser("Administrator");
		createAndAssertTask(task2,DARTH_VADER);
		
		// Add Tony Stark to the list of potential
		// owners and see him assigned to the new task
		TaskFluent task3 = new TaskFluent()
				.setName("MultiUserLoadBalanceTask3")
				.addPotentialUser(BOBBA_FET)
				.addPotentialUser(DARTH_VADER)
				.addPotentialUser(LUKE_CAGE)
				.addPotentialUser(TONY_STARK)
				.setDeploymentID(DEPLOYMENT_ID)
				.setProcessId(PROCESS_ID)
				.setAdminUser("Administrator");
		createAndAssertTask(task3,TONY_STARK);

		logger.info("testMultipleUserWithAdd completed");
	}
	
	@Test
	public void testMultipleUsersWithRemove() {
		// First a set of tasks with our entire
		// crew of villains and heroes
		String expectedOwners[] = Arrays.array(BOBBA_FET,DARTH_VADER,LUKE_CAGE,TONY_STARK);
		for (int x = 0; x < 4; x++) {
			TaskFluent task1 = new TaskFluent()
					.setName("MultiUserLoadBalanceTask1")
					.addPotentialUser(BOBBA_FET)
					.addPotentialUser(DARTH_VADER)
					.addPotentialUser(LUKE_CAGE)
					.addPotentialUser(TONY_STARK)
    				.setDeploymentID(DEPLOYMENT_ID)
    				.setProcessId(PROCESS_ID)
					.setAdminUser("Administrator");
			taskIds[x] = createAndAssertTask(task1,expectedOwners[x]);
		}
		
		// Next we will complete Darth Vader's task
		completeTask(taskIds[1],1500);
		
		// Now do a series of tasks without Darth Vader
		expectedOwners = Arrays.array(BOBBA_FET,LUKE_CAGE,TONY_STARK);
		for (int x = 4; x < 7; x++) {
			TaskFluent task2 = new TaskFluent()
					.setName("MultiUserLoadBalanceTask2")
					.addPotentialUser(BOBBA_FET)
					.addPotentialUser(LUKE_CAGE)
					.addPotentialUser(TONY_STARK)
    				.setDeploymentID(DEPLOYMENT_ID)
    				.setProcessId(PROCESS_ID)
					.setAdminUser("Administrator");
			taskIds[x] = createAndAssertTask(task2,expectedOwners[x-4]);
		}
		
		logger.info("testMultipleUsersWithRemove completed");
	}
	
	private long createTaskWithoutAssert(TaskFluent tf) {
		Task task = tf.getTask();
		taskService.addTask(task, data);
		return task.getId();
	}
	
	private long createAndAssertTask(TaskFluent tf, String expectedOwner) {
		Task task = tf.getTask();
		taskService.addTask(task, data);
		long taskId = task.getId();
		assertEquals("Owner mismatch",expectedOwner,taskService.getTaskById(taskId).getTaskData().getActualOwner().getId());
		return taskId;
	}
	
	private void completeTask(long taskId, long delay) {
		Task task = taskService.getTaskById(taskId);
		String owner = task.getTaskData().getActualOwner().getId();
		logger.debug("Starting task {} with user {}",taskId,owner);
		taskService.start(taskId, owner);
		if (delay > 0) {
			try {
				Thread.sleep(delay);
			} catch (InterruptedException e) {
				// swallowing this exception
			}
		}
		taskService.complete(taskId, owner, data);
	}
	
	

}
