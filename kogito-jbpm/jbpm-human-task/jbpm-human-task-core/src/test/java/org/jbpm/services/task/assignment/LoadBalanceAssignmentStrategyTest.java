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
package org.jbpm.services.task.assignment;

import java.io.StringReader;
import java.util.Collection;
import java.util.Collections;
import java.util.Optional;
import java.util.function.Consumer;

import javax.persistence.EntityManagerFactory;
import javax.persistence.Persistence;

import org.jbpm.services.task.HumanTaskServiceFactory;
import org.jbpm.services.task.assignment.impl.strategy.LoadBalanceAssignmentStrategy;
import org.jbpm.services.task.impl.factories.TaskFactory;
import org.jbpm.test.util.PoolingDataSource;
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

public class LoadBalanceAssignmentStrategyTest extends AbstractAssignmentTest {
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
    private static final String NO_POTENTIAL_OWNER_ASSIGNMENTS = "peopleAssignments = (with (new PeopleAssignments()) { potentialOwners = [],"
            + " businessAdministrators = [new User('Administrator')], } ),";
    private static final String NOT_EXISTING_USER_ASSIGNMENTS = "peopleAssignments = (with (new PeopleAssignments()) { potentialOwners = [new User('Nobody')],"
            + " businessAdministrators = [new User('Administrator')], } ),";
    private static final String NO_USERS_IN_GROUP_ASSIGNMENTS = "peopleAssignments = (with (new PeopleAssignments()) { potentialOwners = [new Group('Nobodies')],"
            + " businessAdministrators = [new User('Administrator')], } ),";
    private static final String SINGLE_USER_ASSIGNMENTS = "peopleAssignments = (with (new PeopleAssignments()) { potentialOwners = [new User('Darth Vader')],"
            + " businessAdministrators = [new User('Administrator')], } ),";
    private static final String SINGLE_USER_IN_GROUP_ASSIGNMENTS = "peopleAssignments = (with (new PeopleAssignments()) { potentialOwners = [new Group('Wrong Crusaders')],"
            + " businessAdministrators = [new User('Administrator')], } ),";
    private static final String EXCLUDED_USER_FROM_USERS_ASSIGNMENTS = ""
            + "peopleAssignments = (with (new PeopleAssignments()) { potentialOwners = [new User('Bobba Fet'), new User('Darth Vader'), new User('Luke Cage')],"
            + " excludedOwners = [new User('Darth Vader')], businessAdministrators = [new User('Administrator')], } ),";
    private static final String EXCLUDED_USER_FROM_GROUP_ASSIGNMENTS = ""
            + "peopleAssignments = (with (new PeopleAssignments()) { potentialOwners = [new Group('Crusaders')],"
            + " excludedOwners = [new User('Tony Stark')], businessAdministrators = [new User('Administrator')], } ),";
    private static final String EXCLUDED_GROUP_FROM_USERS_ASSIGNMENTS = ""
            + "peopleAssignments = (with (new PeopleAssignments()) { potentialOwners = [new User('Bobba Fet'), new User('Darth Vader'), new User('Luke Cage')],"
            + " excludedOwners = [new Group('Wrong Crusaders')], businessAdministrators = [new User('Administrator')], } ),";
    private static final String EXCLUDED_GROUP_FROM_GROUP_ASSIGNMENTS = ""
            + "peopleAssignments = (with (new PeopleAssignments()) { potentialOwners = [new Group('Crusaders')],"
            + " excludedOwners = [new Group('Knights Templer')], businessAdministrators = [new User('Administrator')], } ),";
    private static final String DARTH_VADER = "Darth Vader";
    private static final String BOBBA_FET = "Bobba Fet";
    private static final String LUKE_CAGE = "Luke Cage";
    private static final String TONY_STARK = "Tony Stark";
    private static final String NOBODIES = "Nobodies";
    private static final String CRUSADERS = "Crusaders";
    private static final String WRONG_CRUSADERS = "Wrong Crusaders";
    private PoolingDataSource pds;
    private EntityManagerFactory emf;
    private ListMultimap<String, Task> tasks;
	private Consumer<Task> complete = (task) -> {
		completeTask(task);
		this.tasks.get(task.getTaskData().getActualOwner().getId()).remove(task);
	};

    private static String createTaskString(String peopleAssignments, String taskName) {
        return "(" + BASE_TASK_INFO + peopleAssignments + "name = '" + taskName + "'})";
    }
	
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

    @Test
    public void testNotExistingUser() {
        final String taskString = createTaskString(NOT_EXISTING_USER_ASSIGNMENTS, "NotExistingUserLoadBalancingTask");

        Task task = TaskFactory.evalTask(new StringReader(taskString));
        assertPotentialOwners(task, 1, "Nobody");

        taskService.addTask(task, Collections.emptyMap());
        assertPotentialOwners(task, 0);
        assertNoActualOwner(task);
    }

    @Test
    public void testNoUsersInGroup() {
        final String taskString = createTaskString(NO_USERS_IN_GROUP_ASSIGNMENTS, "NoUsersInGroupLoadBalancingTask");

        Task task = TaskFactory.evalTask(new StringReader(taskString));
        assertPotentialOwners(task, 1, NOBODIES);

        taskService.addTask(task, Collections.emptyMap());
        assertPotentialOwners(task, 0);
        assertNoActualOwner(task);
    }

    @Test
    public void testSingleUser() {
        final String taskString = createTaskString(SINGLE_USER_ASSIGNMENTS, "SingleUserLoadBalancingTask");

        createAndAssertTask(taskString, DARTH_VADER, 1, DARTH_VADER);
        createAndAssertTask(taskString, DARTH_VADER, 1, DARTH_VADER);
        createAndAssertTask(taskString, DARTH_VADER, 1, DARTH_VADER);
    }

    @Test
    public void testSingleUserInGroup() {
        final String taskString = createTaskString(SINGLE_USER_IN_GROUP_ASSIGNMENTS, "SingleUserInGroupLoadBalancingTask");

        createAndAssertTask(taskString, DARTH_VADER, 1, WRONG_CRUSADERS);
        createAndAssertTask(taskString, DARTH_VADER, 1, WRONG_CRUSADERS);
        createAndAssertTask(taskString, DARTH_VADER, 1, WRONG_CRUSADERS);
    }

    @Test
    public void testReleaseTask() {
        final String taskString = createTaskString(MULTI_ACTOR_ASSIGNMENTS, "MultiActorLoadBalancingTask");

        long bobbasTaskId = createAndAssertTask(taskString, BOBBA_FET, 3, BOBBA_FET, DARTH_VADER, LUKE_CAGE);
        long vaderTaskId = createAndAssertTask(taskString, DARTH_VADER, 3, BOBBA_FET);

        taskService.release(bobbasTaskId, BOBBA_FET);
        Task bobbasTask = taskService.getTaskById(bobbasTaskId);
        Task vaderTask = taskService.getTaskById(vaderTaskId);

        assertActualOwner(bobbasTask, LUKE_CAGE);
        assertActualOwner(vaderTask, DARTH_VADER);
    }

    @Test
    public void testExcludedUserFromUsers() {
        final String taskString = createTaskString(EXCLUDED_USER_FROM_USERS_ASSIGNMENTS, "ExcludedUsersFromUsersLoadBalancingTask");

        createAndAssertTask(taskString, BOBBA_FET, 3, BOBBA_FET, DARTH_VADER, LUKE_CAGE);
        createAndAssertTask(taskString, LUKE_CAGE, 3, BOBBA_FET, DARTH_VADER, LUKE_CAGE);
        createAndAssertTask(taskString, BOBBA_FET, 3, BOBBA_FET, DARTH_VADER, LUKE_CAGE);
    }

    @Test
    public void testExcludedUserFromGroup() {
        final String taskString = createTaskString(EXCLUDED_USER_FROM_GROUP_ASSIGNMENTS, "ExcludedUsersFromGroupLoadBalancingTask");

        createAndAssertTask(taskString, LUKE_CAGE, 1, CRUSADERS);
        createAndAssertTask(taskString, BOBBA_FET, 1, CRUSADERS);
        createAndAssertTask(taskString, LUKE_CAGE, 1, CRUSADERS);
    }
	
    @Test
    public void testExcludedGroupFromUsers() {
        final String taskString = createTaskString(EXCLUDED_GROUP_FROM_USERS_ASSIGNMENTS, "ExcludedGroupFromUsersLoadBalancingTask");

        createAndAssertTask(taskString, BOBBA_FET, 3, BOBBA_FET, DARTH_VADER, LUKE_CAGE);
        createAndAssertTask(taskString, LUKE_CAGE, 3, BOBBA_FET, DARTH_VADER, LUKE_CAGE);
        createAndAssertTask(taskString, BOBBA_FET, 3, BOBBA_FET, DARTH_VADER, LUKE_CAGE);
    }

    @Test
    public void testExcludedGroupFromGroup() {
        final String taskString = createTaskString(EXCLUDED_GROUP_FROM_GROUP_ASSIGNMENTS, "ExcludedGroupFromGroupLoadBalancingTask");

        createAndAssertTask(taskString, LUKE_CAGE, 1, CRUSADERS);
        createAndAssertTask(taskString, TONY_STARK, 1, CRUSADERS);
        createAndAssertTask(taskString, LUKE_CAGE, 1, CRUSADERS);
    }

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
