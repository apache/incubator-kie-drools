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

import javax.persistence.EntityManagerFactory;
import javax.persistence.Persistence;
import org.jbpm.services.task.HumanTaskServiceFactory;
import org.jbpm.services.task.assignment.strategy.CustomStrategy;
import org.jbpm.test.util.PoolingDataSource;
import org.junit.After;
import org.junit.Before;
import org.junit.Test;
import org.kie.internal.task.api.InternalTaskService;

public class CustomAssignmentStrategyTest extends AbstractAssignmentTest {

    private static final String BASE_TASK_INFO = "with (new Task()) { priority = 55, taskData = (with (new TaskData()) { } ), ";

    private static final String MULTI_ACTOR_ASSIGNMENTS = ""
            + "peopleAssignments = (with (new PeopleAssignments()) { potentialOwners = [new User('Bobba Fet'), new User('Darth Vader'), new User('Luke Cage')],"
            + " businessAdministrators = [new User('Administrator')], } ),";

    private static final String REMOVE_ACTOR_ASSIGNMENTS = ""
            + "peopleAssignments = (with (new PeopleAssignments()) { potentialOwners = [new User('Bobba Fet'), new User('Luke Cage'), new User('Tony Stark')],"
            + " businessAdministrators = [new User('Administrator')], } ),";

    private static final String SINGLE_USER_ASSIGNMENTS = "peopleAssignments = (with (new PeopleAssignments()) { potentialOwners = [new User('Darth Vader')],"
            + " businessAdministrators = [new User('Administrator')], } ),";

    private static final String SINGLE_USER_BOBBA_FET_ASSIGNMENTS = "peopleAssignments = (with (new PeopleAssignments()) { potentialOwners = [new User('Bobba Fet')],"
            + " businessAdministrators = [new User('Administrator')], } ),";

    private static final String SINGLE_USER_IN_GROUP_ASSIGNMENTS = "peopleAssignments = (with (new PeopleAssignments()) { potentialOwners = [new Group('Wrong Crusaders')],"
            + " businessAdministrators = [new User('Administrator')], } ),";

    private static final String NO_POTENTIAL_OWNER_ASSIGNMENTS = "peopleAssignments = (with (new PeopleAssignments()) { potentialOwners = [],"
            + " businessAdministrators = [new User('Administrator')], } ),";

    private static final String EXCLUDED_USER_FROM_USERS_ASSIGNMENTS = ""
            + "peopleAssignments = (with (new PeopleAssignments()) { potentialOwners = [new User('Bobba Fet'), new User('Darth Vader'), new User('Luke Cage')],"
            + " excludedOwners = [new User('Darth Vader')], businessAdministrators = [new User('Administrator')], } ),";

    private static final String BOBBA_FET = "Bobba Fet";
    private static final String DARTH_VADER = "Darth Vader";
    private static final String LUKE_CAGE = "Luke Cage";
    private static final String TONY_STARK = "Tony Stark";

    private static final String WRONG_CRUSADERS = "Wrong Crusaders";

    private PoolingDataSource pds;
    private EntityManagerFactory emf;

    @Before
    public void setup() {
        System.setProperty("org.jbpm.task.assignment.enabled", "true");

        pds = setupPoolingDataSource();
        emf = Persistence.createEntityManagerFactory("org.jbpm.services.task");

        AssignmentServiceProvider.override(new CustomStrategy(DARTH_VADER));

        this.taskService = (InternalTaskService) HumanTaskServiceFactory.newTaskServiceConfigurator()
                .entityManagerFactory(emf)
                .getTaskService();

    }

    @After
    public void clean() {
        System.clearProperty("org.jbpm.task.assignment.enabled");
        AssignmentServiceProvider.clear();
        if (emf != null) {
            emf.close();
        }
        if (pds != null) {
            pds.close();
        }
    }

    @Test
    public void testAssignTaskWithMultiActor() {
        final String taskString = createTaskString(MULTI_ACTOR_ASSIGNMENTS, "MultiActorCustomTask");

        createAndAssertTask(taskString, DARTH_VADER, 3, BOBBA_FET, DARTH_VADER, LUKE_CAGE);
        createAndAssertTask(taskString, DARTH_VADER, 3, BOBBA_FET, DARTH_VADER, LUKE_CAGE);
        createAndAssertTask(taskString, DARTH_VADER, 3, BOBBA_FET, DARTH_VADER, LUKE_CAGE);
    }

    @Test
    public void testAssignTasksAndChangeStrategy() {
        final String taskString = createTaskString(MULTI_ACTOR_ASSIGNMENTS, "MultiActorChangeCustomStrategyTask");

        createAndAssertTask(taskString, DARTH_VADER, 3, BOBBA_FET, DARTH_VADER, LUKE_CAGE);
        createAndAssertTask(taskString, DARTH_VADER, 3, BOBBA_FET, DARTH_VADER, LUKE_CAGE);

        AssignmentServiceProvider.override(new CustomStrategy(BOBBA_FET));

        createAndAssertTask(taskString, BOBBA_FET, 3, BOBBA_FET, DARTH_VADER, LUKE_CAGE);
        createAndAssertTask(taskString, BOBBA_FET, 3, BOBBA_FET, DARTH_VADER, LUKE_CAGE);
    }

    @Test
    public void testAssignTaskMultiActorWithoutUser() {
        final String taskString = createTaskString(REMOVE_ACTOR_ASSIGNMENTS, "MultiActorWithRemovedActorCustomTask");

        createAndAssertTask(taskString, DARTH_VADER, 3, BOBBA_FET, LUKE_CAGE, TONY_STARK);
        createAndAssertTask(taskString, DARTH_VADER, 3, BOBBA_FET, LUKE_CAGE, TONY_STARK);
    }

    @Test
    public void testAssingTaskForUser() {
        final String taskString = createTaskString(SINGLE_USER_ASSIGNMENTS, "SingleUserCustomTask");

        createAndAssertTask(taskString, DARTH_VADER, 1, DARTH_VADER);
        createAndAssertTask(taskString, DARTH_VADER, 1, DARTH_VADER);
    }

    @Test
    public void testAssignTaskNotForUser() {
        final String taskString = createTaskString(SINGLE_USER_BOBBA_FET_ASSIGNMENTS, "SingleUserBobbaFetCustomTask");

        createAndAssertTask(taskString, DARTH_VADER, 1, BOBBA_FET);
        createAndAssertTask(taskString, DARTH_VADER, 1, BOBBA_FET);
    }

    @Test
    public void testAssignTaskWithNoPotentialOwners() {
        final String taskString = createTaskString(NO_POTENTIAL_OWNER_ASSIGNMENTS, "NoPotentialOwnersCustomTask");

        createAndAssertTask(taskString, DARTH_VADER, 0);
    }

    @Test
    public void testsAssignTaskForGroup() {
        final String taskString = createTaskString(SINGLE_USER_IN_GROUP_ASSIGNMENTS, "SingleUserInGroupCustomTask");

        createAndAssertTask(taskString, DARTH_VADER, 1, WRONG_CRUSADERS);
    }

    @Test
    public void testAssignTaskWithExcludedUserFromUsers() {
        final String taskString = createTaskString(EXCLUDED_USER_FROM_USERS_ASSIGNMENTS, "ExcludedUsersFromUsersCustomTask");

        createAndAssertTask(taskString, DARTH_VADER, 3, BOBBA_FET, DARTH_VADER, LUKE_CAGE);
        createAndAssertTask(taskString, DARTH_VADER, 3, BOBBA_FET, DARTH_VADER, LUKE_CAGE);
    }

    private static String createTaskString(String peopleAssignments, String taskName) {
        return "(" + BASE_TASK_INFO + peopleAssignments + "name = '" + taskName + "'})";
    }

}