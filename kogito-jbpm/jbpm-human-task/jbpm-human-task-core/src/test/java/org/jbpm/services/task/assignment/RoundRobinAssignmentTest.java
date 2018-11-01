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
import java.util.Collections;
import javax.persistence.EntityManagerFactory;
import javax.persistence.Persistence;

import org.jbpm.services.task.HumanTaskServiceFactory;
import org.jbpm.services.task.assignment.impl.strategy.RoundRobinAssignmentStrategy;
import org.jbpm.services.task.impl.factories.TaskFactory;
import org.jbpm.test.util.PoolingDataSource;
import org.junit.After;
import org.junit.Before;
import org.junit.Ignore;
import org.junit.Test;
import org.kie.api.task.model.Task;
import org.kie.internal.task.api.InternalTaskService;

public class RoundRobinAssignmentTest extends AbstractAssignmentTest {

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

    private static final String BOBBA_FET = "Bobba Fet";
    private static final String DARTH_VADER = "Darth Vader";
    private static final String LUKE_CAGE = "Luke Cage";
    private static final String TONY_STARK = "Tony Stark";
    private static final String NOBODY = "Nobody";

    private static final String NOBODIES = "Nobodies";
    private static final String CRUSADERS = "Crusaders";
    private static final String WRONG_CRUSADERS = "Wrong Crusaders";

    private PoolingDataSource pds;
    private EntityManagerFactory emf;

    @Before
    public void setup() {
        System.setProperty("org.jbpm.task.assignment.enabled", "true");

        pds = setupPoolingDataSource();
        emf = Persistence.createEntityManagerFactory("org.jbpm.services.task");

        AssignmentServiceProvider.override(new RoundRobinAssignmentStrategy());

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
    public void testMultiActor() {
        final String taskString = createTaskString(MULTI_ACTOR_ASSIGNMENTS, "MultiActorRoundRobinTask");

        createAndAssertTask(taskString, BOBBA_FET, 3, BOBBA_FET, DARTH_VADER, LUKE_CAGE);
        createAndAssertTask(taskString, DARTH_VADER, 3, BOBBA_FET, DARTH_VADER, LUKE_CAGE);
        createAndAssertTask(taskString, LUKE_CAGE, 3, BOBBA_FET, DARTH_VADER, LUKE_CAGE);

        // Expect that the "round robin" will circle back to the beginning of the list
        createAndAssertTask(taskString, BOBBA_FET, 3, BOBBA_FET, DARTH_VADER, LUKE_CAGE);
        createAndAssertTask(taskString, DARTH_VADER, 3, BOBBA_FET, DARTH_VADER, LUKE_CAGE);
        createAndAssertTask(taskString, LUKE_CAGE, 3, BOBBA_FET, DARTH_VADER, LUKE_CAGE);
    }

    @Test
    public void testMultiActorWithGroup() {
        final String taskString = createTaskString(MULTI_ACTOR_WITH_GROUP_ASSIGNMENTS, "MultiActorWithGroupRoundRobinTask");

        createAndAssertTask(taskString, BOBBA_FET, 3, BOBBA_FET, CRUSADERS, LUKE_CAGE);
        createAndAssertTask(taskString, LUKE_CAGE, 3, BOBBA_FET, CRUSADERS, LUKE_CAGE);
        // Note: Tony Stark must be a member of the Crusaders, see userinfo.properties
        createAndAssertTask(taskString, TONY_STARK, 3, BOBBA_FET, CRUSADERS, LUKE_CAGE);
    }

    @Test
    public void testMultiActorWithAddedActor() {
        final String taskString = createTaskString(MULTI_ACTOR_ASSIGNMENTS, "MultiActorWithAddsRoundRobinTask");
        final String taskString2 = createTaskString(ADD_ACTOR_ASSIGNMENTS, "MultiActorWithAddsRoundRobinTask");

        createAndAssertTask(taskString, BOBBA_FET, 3, BOBBA_FET, DARTH_VADER, LUKE_CAGE);
        createAndAssertTask(taskString2, DARTH_VADER, 4, BOBBA_FET, DARTH_VADER, LUKE_CAGE, TONY_STARK);
        createAndAssertTask(taskString2, LUKE_CAGE, 4, BOBBA_FET, DARTH_VADER, LUKE_CAGE, TONY_STARK);

        // Expect that the "round robin" will circle back to the beginning of the list
        createAndAssertTask(taskString2, BOBBA_FET, 4, BOBBA_FET, DARTH_VADER, LUKE_CAGE, TONY_STARK);

        // Because Tony was added after Bobba took the first task, he will show up after Bobba gets used the second time
        createAndAssertTask(taskString2, TONY_STARK, 4, BOBBA_FET, DARTH_VADER, LUKE_CAGE, TONY_STARK);
    }

    @Test
    public void testMultiActorWithRemovedActor() {
        final String taskString2 = createTaskString(REMOVE_ACTOR_ASSIGNMENTS, "MultiActorWithRemovedActorRoundRobinTask");
        final String taskString = createTaskString(ADD_ACTOR_ASSIGNMENTS, "MultiActorWithRemovedActorRoundRobinTask");

        createAndAssertTask(taskString, BOBBA_FET, 4, BOBBA_FET, DARTH_VADER, LUKE_CAGE, TONY_STARK);
        createAndAssertTask(taskString2, LUKE_CAGE, 3, BOBBA_FET, LUKE_CAGE, TONY_STARK);
        createAndAssertTask(taskString2, TONY_STARK, 3, BOBBA_FET, LUKE_CAGE, TONY_STARK);
        createAndAssertTask(taskString2, BOBBA_FET, 3, BOBBA_FET, LUKE_CAGE, TONY_STARK);
    }

    @Test
    @Ignore("Waits forever")
    public void testNoPotentialOwners() {
        final String taskString = createTaskString(NO_POTENTIAL_OWNER_ASSIGNMENTS, "NoPotentialOwnersRoundRobinTask");

        Task task = TaskFactory.evalTask(new StringReader(taskString));
        assertPotentialOwners(task, 0);

        taskService.addTask(task, Collections.emptyMap());
        assertPotentialOwners(task, 0);
        assertNoActualOwner(task);
    }

    @Test
    public void testNotExistingUser() {
        final String taskString = createTaskString(NOT_EXISTING_USER_ASSIGNMENTS, "NotExistingUserRoundRobinTask");

        Task task = TaskFactory.evalTask(new StringReader(taskString));
        assertPotentialOwners(task, 1, NOBODY);

        taskService.addTask(task, Collections.emptyMap());
        assertPotentialOwners(task, 0);
        assertNoActualOwner(task);
    }

    @Test
    public void testNotExistingGroup() {

    }

    @Test
    public void testNoUsersInGroup() {
        final String taskString = createTaskString(NO_USERS_IN_GROUP_ASSIGNMENTS, "NoUsersInGroupRoundRobinTask");

        Task task = TaskFactory.evalTask(new StringReader(taskString));
        assertPotentialOwners(task, 1, NOBODIES);

        taskService.addTask(task, Collections.emptyMap());
        assertPotentialOwners(task, 0);
        assertNoActualOwner(task);
    }

    @Test
    public void testSingleUser() {
        final String taskString = createTaskString(SINGLE_USER_ASSIGNMENTS, "SingleUserRoundRobinTask");

        createAndAssertTask(taskString, DARTH_VADER, 1, DARTH_VADER);
        createAndAssertTask(taskString, DARTH_VADER, 1, DARTH_VADER);
        createAndAssertTask(taskString, DARTH_VADER, 1, DARTH_VADER);
    }

    @Test
    public void testSingleUserInGroup() {
        final String taskString = createTaskString(SINGLE_USER_IN_GROUP_ASSIGNMENTS, "SingleUserInGroupRoundRobinTask");

        createAndAssertTask(taskString, DARTH_VADER, 1, WRONG_CRUSADERS);
        createAndAssertTask(taskString, DARTH_VADER, 1, WRONG_CRUSADERS);
        createAndAssertTask(taskString, DARTH_VADER, 1, WRONG_CRUSADERS);
    }

    @Test
    public void testReleaseTask() {
        final String taskString = createTaskString(MULTI_ACTOR_ASSIGNMENTS, "MultiActorRoundRobinTask");

        long taskId = createAndAssertTask(taskString, BOBBA_FET, 3, BOBBA_FET, DARTH_VADER, LUKE_CAGE);
        taskService.release(taskId, BOBBA_FET);
        Task task = taskService.getTaskById(taskId);
        assertActualOwner(task, DARTH_VADER);

        createAndAssertTask(taskString, LUKE_CAGE, 3, BOBBA_FET, DARTH_VADER, LUKE_CAGE);
    }

    @Test
    public void testExcludedUserFromUsers() {
        final String taskString = createTaskString(EXCLUDED_USER_FROM_USERS_ASSIGNMENTS, "ExcludedUsersFromUsersRoundRobinTask");

        createAndAssertTask(taskString, BOBBA_FET, 3, BOBBA_FET, DARTH_VADER, LUKE_CAGE);
        createAndAssertTask(taskString, LUKE_CAGE, 3, BOBBA_FET, DARTH_VADER, LUKE_CAGE);
        createAndAssertTask(taskString, BOBBA_FET, 3, BOBBA_FET, DARTH_VADER, LUKE_CAGE);
    }

    @Test
    public void testExcludedUserFromGroup() {
        final String taskString = createTaskString(EXCLUDED_USER_FROM_GROUP_ASSIGNMENTS, "ExcludedUsersFromGroupRoundRobinTask");

        createAndAssertTask(taskString, LUKE_CAGE, 1, CRUSADERS);
        createAndAssertTask(taskString, BOBBA_FET, 1, CRUSADERS);
        createAndAssertTask(taskString, LUKE_CAGE, 1, CRUSADERS);
    }

    @Test
    public void testExcludedGroupFromUsers() {
        final String taskString = createTaskString(EXCLUDED_GROUP_FROM_USERS_ASSIGNMENTS, "ExcludedGroupFromUsersRoundRobinTask");

        createAndAssertTask(taskString, BOBBA_FET, 3, BOBBA_FET, DARTH_VADER, LUKE_CAGE);
        createAndAssertTask(taskString, LUKE_CAGE, 3, BOBBA_FET, DARTH_VADER, LUKE_CAGE);
        createAndAssertTask(taskString, BOBBA_FET, 3, BOBBA_FET, DARTH_VADER, LUKE_CAGE);
    }

    @Test
    public void testExcludedGroupFromGroup() {
        final String taskString = createTaskString(EXCLUDED_GROUP_FROM_GROUP_ASSIGNMENTS, "ExcludedGroupFromGroupRoundRobinTask");

        createAndAssertTask(taskString, LUKE_CAGE, 1, CRUSADERS);
        createAndAssertTask(taskString, TONY_STARK, 1, CRUSADERS);
        createAndAssertTask(taskString, LUKE_CAGE, 1, CRUSADERS);
    }

    private static String createTaskString(String peopleAssignments, String taskName) {
        return "(" + BASE_TASK_INFO + peopleAssignments + "name = '" + taskName + "'})";
    }
}
