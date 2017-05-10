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

import java.util.Collection;
import java.util.Optional;
import java.util.function.Consumer;

import javax.persistence.EntityManagerFactory;
import javax.persistence.Persistence;

import org.jbpm.services.task.HumanTaskServiceFactory;
import org.jbpm.services.task.assignment.impl.strategy.LoadBalanceAssignmentStrategy;
import org.junit.After;
import org.junit.Before;
import org.junit.Test;
import org.kie.api.task.model.Status;
import org.kie.api.task.model.Task;
import org.kie.internal.task.api.InternalTaskService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.google.common.collect.ArrayListMultimap;
import com.google.common.collect.ListMultimap;

import bitronix.tm.resource.jdbc.PoolingDataSource;

public class LoadBalanceAssignmentStrategyTest extends AbstractAssignmentTest {
    private PoolingDataSource pds;
    private EntityManagerFactory emf;
    private ListMultimap<String, Task> tasks;
    private static final Logger logger = LoggerFactory.getLogger(LoadBalanceAssignmentStrategyTest.class);
    private static final String BASE_TASK_INFO = "with (new Task()) { priority = 55, taskData = (with (new TaskData()) { } ), ";
    private static final String MULTI_ACTOR_ASSIGNMENTS = ""
    		+ "peopleAssignments = (with (new PeopleAssignments()) { potentialOwners = [new User('Bobba Fet'), new User('Darth Vader'), new User('Luke Cage')],"
            + " businessAdministrators = [new User('Administrator')], } ),";
    private static final String MULTI_ACTOR_WITH_GROUP_ASSIGNMENTS = ""
    		+ "peopleAssignments = (with (new PeopleAssignments()) { potentialOwners = [new User('Bobba Fet'), new Group('Crusaders'), new User('Luke Cage')],"
    		+ " businessAdministrators = [new User('Administrator')], } ),";
    private static final String ADD_ACTOR_ASSIGNMENTS = ""
    		+ "peopleAssignments = (with (new PeopleAssignments()) { potentialOwners = [new User('Bobba Fet'), new User('Darth Vader'), new User('Luke Cage'), new User('Tony Stark')],"
    		+ " businessAdministrators = [new User('Administrator')], } ),";
    private static final String REMOVE_ACTOR_ASSIGNMENTS = ""
    		+ "peopleAssignments = (with (new PeopleAssignments()) { potentialOwners = [new User('Bobba Fet'), new User('Luke Cage'), new User('Tony Stark')],"
    		+ " businessAdministrators = [new User('Administrator')], } ),";
    private static final String DARTH_VADER = "Darth Vader";
    private static final String BOBBA_FET = "Bobba Fet";
    private static final String LUKE_CAGE = "Luke Cage";
    private static final String TONY_STARK = "Tony Stark";


	@Before
	public void setUp() throws Exception {
        System.setProperty("org.jbpm.task.assignment.enabled", "true");
        System.setProperty("org.jbpm.task.assignment.strategy", "LoadBalance");
        System.setProperty("org.jbpm.task.assignment.loadbalance.calculator","org.jbpm.services.task.assignment.impl.TaskCountLoadCalculator");
        System.setProperty("org.jbpm.task.assignment.loadbalance.entry.timetolive", "10"); // this has to be low in order that we update the load balances
        pds = setupPoolingDataSource();
        emf = Persistence.createEntityManagerFactory( "org.jbpm.services.task" );

        AssignmentServiceProvider.override(new LoadBalanceAssignmentStrategy());

        this.taskService = (InternalTaskService) HumanTaskServiceFactory.newTaskServiceConfigurator()
                .entityManagerFactory(emf)
                .getTaskService();
        this.tasks = ArrayListMultimap.create();
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
        final String taskString = "(" +
        		BASE_TASK_INFO +	
                 MULTI_ACTOR_ASSIGNMENTS +
                "name = 'MultiUserLoadBalanceTask'})";
        tasks.put(BOBBA_FET,createForCompletionTask(taskString, BOBBA_FET, 3, BOBBA_FET,DARTH_VADER,LUKE_CAGE));
        tasks.put(DARTH_VADER,createForCompletionTask(taskString, DARTH_VADER, 3, BOBBA_FET,DARTH_VADER,LUKE_CAGE));
        tasks.put(LUKE_CAGE,createForCompletionTask(taskString, LUKE_CAGE, 3, BOBBA_FET,DARTH_VADER,LUKE_CAGE));
        assertNumberOfNonCompletedTasks(DARTH_VADER, 1);
        assertNumberOfNonCompletedTasks(BOBBA_FET, 1);
        assertNumberOfNonCompletedTasks(LUKE_CAGE, 1);

        tasks.put(BOBBA_FET,createForCompletionTask(taskString, BOBBA_FET, 3, BOBBA_FET,DARTH_VADER,LUKE_CAGE));
        tasks.put(DARTH_VADER,createForCompletionTask(taskString, DARTH_VADER, 3, BOBBA_FET,DARTH_VADER,LUKE_CAGE));
        tasks.put(LUKE_CAGE,createForCompletionTask(taskString, LUKE_CAGE, 3, BOBBA_FET,DARTH_VADER,LUKE_CAGE));
        assertNumberOfNonCompletedTasks(DARTH_VADER, 2);
        assertNumberOfNonCompletedTasks(BOBBA_FET, 2);
        assertNumberOfNonCompletedTasks(LUKE_CAGE, 2);
        
        // Check that if we complete a task and then create a new one
        // that it gets assigned to the proper user
        getTaskToComplete(DARTH_VADER).ifPresent(complete);
        assertNumberOfNonCompletedTasks(DARTH_VADER, 1);
        tasks.put(DARTH_VADER,createForCompletionTask(taskString, DARTH_VADER, 3, BOBBA_FET,DARTH_VADER,LUKE_CAGE));
        assertNumberOfNonCompletedTasks(DARTH_VADER, 2);
        
        // Make sure that we aren't trying to complete a previously completed task
        getTaskToComplete(DARTH_VADER).ifPresent(complete);
        assertNumberOfNonCompletedTasks(DARTH_VADER, 1);
        
        logger.info("testMultipleUser completed");
	}

	@Test
	public void testMultipleUserWithGroup() {
        final String taskString = "(" +
        		BASE_TASK_INFO +
        		MULTI_ACTOR_WITH_GROUP_ASSIGNMENTS +
                "name = 'MultiUserWithGroupLoadBalanceTask'})";
        tasks.put(BOBBA_FET, createForCompletionTask(taskString, BOBBA_FET, 3, BOBBA_FET,"Crusaders",LUKE_CAGE));
        tasks.put(LUKE_CAGE, createForCompletionTask(taskString, LUKE_CAGE, 3, BOBBA_FET,"Crusaders",LUKE_CAGE));
        tasks.put(TONY_STARK, createForCompletionTask(taskString, TONY_STARK, 3, BOBBA_FET,"Crusaders",LUKE_CAGE));
        assertNumberOfNonCompletedTasks(BOBBA_FET, 1);
        assertNumberOfNonCompletedTasks(LUKE_CAGE, 1);
        assertNumberOfNonCompletedTasks(TONY_STARK, 1);
        
        tasks.put(BOBBA_FET, createForCompletionTask(taskString, BOBBA_FET, 3, BOBBA_FET,"Crusaders",LUKE_CAGE));
        tasks.put(LUKE_CAGE, createForCompletionTask(taskString, LUKE_CAGE, 3, BOBBA_FET,"Crusaders",LUKE_CAGE));
        tasks.put(TONY_STARK, createForCompletionTask(taskString, TONY_STARK, 3, BOBBA_FET,"Crusaders",LUKE_CAGE));
        assertNumberOfNonCompletedTasks(BOBBA_FET, 2);
        assertNumberOfNonCompletedTasks(LUKE_CAGE, 2);
        assertNumberOfNonCompletedTasks(TONY_STARK, 2);
        
        getTaskToComplete(LUKE_CAGE).ifPresent(complete);
        assertNumberOfNonCompletedTasks(LUKE_CAGE, 1);
        tasks.put(LUKE_CAGE, createForCompletionTask(taskString, LUKE_CAGE, 3, BOBBA_FET,"Crusaders",LUKE_CAGE));
        assertNumberOfNonCompletedTasks(LUKE_CAGE, 2);
        logger.info("testMultipleUserWithGroup completed");
	}
	
	@Test
	public void testMultipleUserWithAdd() {
        final String taskString = "(" +
        		BASE_TASK_INFO +	
                 MULTI_ACTOR_ASSIGNMENTS +
                "name = 'MultiUserWithAddLoadBalanceTask'})";
        final String taskString2 = "(" +
      		   BASE_TASK_INFO +
      		   ADD_ACTOR_ASSIGNMENTS + 
      		   "name = 'MultiUserWithAddLoadBalanceTask2'})";
		
        tasks.put(BOBBA_FET,createForCompletionTask(taskString, BOBBA_FET, 3, BOBBA_FET,DARTH_VADER,LUKE_CAGE));
        tasks.put(DARTH_VADER,createForCompletionTask(taskString, DARTH_VADER, 3, BOBBA_FET,DARTH_VADER,LUKE_CAGE));
        tasks.put(LUKE_CAGE,createForCompletionTask(taskString, LUKE_CAGE, 3, BOBBA_FET,DARTH_VADER,LUKE_CAGE));
        assertNumberOfNonCompletedTasks(DARTH_VADER, 1);
        assertNumberOfNonCompletedTasks(BOBBA_FET, 1);
        assertNumberOfNonCompletedTasks(LUKE_CAGE, 1);

        tasks.put(BOBBA_FET,createForCompletionTask(taskString, BOBBA_FET, 3, BOBBA_FET,DARTH_VADER,LUKE_CAGE));
        tasks.put(DARTH_VADER,createForCompletionTask(taskString, DARTH_VADER, 3, BOBBA_FET,DARTH_VADER,LUKE_CAGE));
        tasks.put(LUKE_CAGE,createForCompletionTask(taskString, LUKE_CAGE, 3, BOBBA_FET,DARTH_VADER,LUKE_CAGE));
        assertNumberOfNonCompletedTasks(DARTH_VADER, 2);
        assertNumberOfNonCompletedTasks(BOBBA_FET, 2);
        assertNumberOfNonCompletedTasks(LUKE_CAGE, 2);
        
        // Check that if we complete a task and then create a new one
        // that it gets assigned to the proper user
        getTaskToComplete(DARTH_VADER).ifPresent(complete);
        assertNumberOfNonCompletedTasks(DARTH_VADER, 1);
        tasks.put(DARTH_VADER,createForCompletionTask(taskString, DARTH_VADER, 3, BOBBA_FET,DARTH_VADER,LUKE_CAGE));
        assertNumberOfNonCompletedTasks(DARTH_VADER, 2);
        
        // Now add a user with no tasks and make sure that
        // the assignment goes to the new user
        tasks.put(TONY_STARK, createForCompletionTask(taskString2,TONY_STARK,4,BOBBA_FET,DARTH_VADER,LUKE_CAGE,TONY_STARK));
        assertNumberOfNonCompletedTasks(TONY_STARK,1);
	}
	
	@Test
	public void testMultipleUsersWithRemove() {
        final String taskString = "(" +
       		   BASE_TASK_INFO +
       		   ADD_ACTOR_ASSIGNMENTS + 
       		   "name = 'MultiUserWithRemoveLoadBalanceTask'})";
        final String taskString2 = "("
        		+ BASE_TASK_INFO
        		+ REMOVE_ACTOR_ASSIGNMENTS
        		+ "name = 'MultiUserWithRemoveLoadBalanceTask2'})";
		
        tasks.put(BOBBA_FET,createForCompletionTask(taskString, BOBBA_FET, 4, BOBBA_FET,DARTH_VADER,LUKE_CAGE,TONY_STARK));
        tasks.put(DARTH_VADER,createForCompletionTask(taskString, DARTH_VADER, 4, BOBBA_FET,DARTH_VADER,LUKE_CAGE,TONY_STARK));
        tasks.put(LUKE_CAGE,createForCompletionTask(taskString, LUKE_CAGE, 4, BOBBA_FET,DARTH_VADER,LUKE_CAGE,TONY_STARK));
        tasks.put(TONY_STARK,createForCompletionTask(taskString, TONY_STARK, 4, BOBBA_FET,DARTH_VADER,LUKE_CAGE,TONY_STARK));
        assertNumberOfNonCompletedTasks(DARTH_VADER, 1);
        assertNumberOfNonCompletedTasks(BOBBA_FET, 1);
        assertNumberOfNonCompletedTasks(LUKE_CAGE, 1);
        assertNumberOfNonCompletedTasks(TONY_STARK, 1);

        tasks.put(BOBBA_FET,createForCompletionTask(taskString2, BOBBA_FET, 3, BOBBA_FET,LUKE_CAGE,TONY_STARK));
        tasks.put(LUKE_CAGE,createForCompletionTask(taskString2, LUKE_CAGE, 3, BOBBA_FET,LUKE_CAGE,TONY_STARK));
        tasks.put(TONY_STARK,createForCompletionTask(taskString2, TONY_STARK, 3, BOBBA_FET,LUKE_CAGE,TONY_STARK));
        assertNumberOfNonCompletedTasks(DARTH_VADER, 1);
        assertNumberOfNonCompletedTasks(BOBBA_FET, 2);
        assertNumberOfNonCompletedTasks(LUKE_CAGE, 2);
        assertNumberOfNonCompletedTasks(TONY_STARK, 2);
        
	}
	
	private Consumer<Task> complete = (task) -> {
		completeTask(task);
		this.tasks.get(task.getTaskData().getActualOwner().getId()).remove(task);
	};
	
	
	
	private Optional<Task> getTaskToComplete(String user) {
		Optional<Task> task = Optional.empty();
		if (tasks.containsKey(user)) {
			Collection<Task> taskCollection = tasks.get(user);
			if (!taskCollection.isEmpty()) {
				task = taskCollection.stream().filter(t -> !t.getTaskData().getStatus().equals(Status.Completed)).findFirst();
			}
		}
		if (!task.isPresent()) {
			logger.warn("No task to complete found for {}",user);
		}
		return task;
	}
}
