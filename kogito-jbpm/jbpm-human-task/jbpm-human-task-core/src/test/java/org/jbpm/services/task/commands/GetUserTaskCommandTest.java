/*
 * Copyright 2018 Red Hat, Inc. and/or its affiliates.
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

package org.jbpm.services.task.commands;

import java.util.ArrayList;
import java.util.List;

import org.jbpm.services.task.exception.PermissionDeniedException;
import org.jbpm.services.task.impl.model.GroupImpl;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.kie.api.task.UserGroupCallback;
import org.kie.api.task.model.OrganizationalEntity;
import org.kie.api.task.model.PeopleAssignments;
import org.kie.api.task.model.Task;
import org.kie.internal.task.api.TaskQueryService;
import org.mockito.Mock;
import org.mockito.runners.MockitoJUnitRunner;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.fail;
import static org.mockito.Mockito.when;

@RunWith(MockitoJUnitRunner.class)
public class GetUserTaskCommandTest {

    public static final String USER_ID = "user";
    public static final Long TASK_ID = 1L;

    @Mock
    private TaskContext taskContext;

    @Mock
    private TaskQueryService taskQueryService;

    @Mock
    private UserGroupCallback userGroupCallback;

    @Mock
    private Task task;

    @Mock
    private PeopleAssignments peopleAssignments;

    private GetUserTaskCommand command;

    @Before
    public void initialize() {
        when(taskContext.getUserGroupCallback()).thenReturn(userGroupCallback);
        when(taskContext.getTaskQueryService()).thenReturn(taskQueryService);
        when(taskQueryService.getTaskInstanceById(TASK_ID)).thenReturn(task);
        when(task.getPeopleAssignments()).thenReturn(peopleAssignments);

        command = new GetUserTaskCommand(USER_ID,
                                         TASK_ID);
    }

    @Test(expected = IllegalStateException.class)
    public void testGetUnexistentClass() {
        when(taskQueryService.getTaskInstanceById(TASK_ID)).thenReturn(null);

        command.execute(taskContext);

        fail();
    }

    @Test
    public void testUserOnPotentialOwnersGroups() {
        List<String> userGroups = new ArrayList<>();
        userGroups.add("user");
        when(userGroupCallback.getGroupsForUser(USER_ID)).thenReturn(userGroups);

        List<OrganizationalEntity> potentialOwners = new ArrayList<>();
        potentialOwners.add(new GroupImpl("user"));

        when(peopleAssignments.getPotentialOwners()).thenReturn(potentialOwners);

        Task commandTask = command.execute(taskContext);

        assertEquals(task,
                     commandTask);
    }

    @Test
    public void testUserOnBusinessAdministratorGroups() {
        List<String> userGroups = new ArrayList<>();
        userGroups.add("admin");
        when(userGroupCallback.getGroupsForUser(USER_ID)).thenReturn(userGroups);

        List<OrganizationalEntity> admins = new ArrayList<>();
        admins.add(new GroupImpl("admin"));

        when(peopleAssignments.getBusinessAdministrators()).thenReturn(admins);

        Task commandTask = command.execute(taskContext);

        assertEquals(task,
                     commandTask);
    }

    @Test(expected = PermissionDeniedException.class)
    public void testUserWithoutPermission() {
        when(userGroupCallback.getGroupsForUser(USER_ID)).thenReturn(new ArrayList<String>());

        when(peopleAssignments.getBusinessAdministrators()).thenReturn(new ArrayList<OrganizationalEntity>());

        command.execute(taskContext);
        fail();
    }
}
