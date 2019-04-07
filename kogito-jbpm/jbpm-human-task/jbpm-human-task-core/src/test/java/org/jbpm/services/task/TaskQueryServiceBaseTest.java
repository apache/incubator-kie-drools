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

package org.jbpm.services.task;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNull;
import static org.junit.Assert.assertTrue;

import java.io.StringReader;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Date;
import java.util.HashMap;
import java.util.List;

import javax.naming.InitialContext;
import javax.transaction.UserTransaction;

import org.jbpm.services.task.impl.factories.TaskFactory;
import org.jbpm.services.task.impl.model.TaskDataImpl;
import org.junit.Ignore;
import org.junit.Test;
import org.kie.api.task.model.I18NText;
import org.kie.api.task.model.Status;
import org.kie.api.task.model.Task;
import org.kie.api.task.model.TaskSummary;
import org.kie.internal.task.api.TaskModelProvider;
import org.kie.internal.task.api.model.InternalI18NText;
import org.kie.internal.task.api.model.InternalTask;

public abstract class TaskQueryServiceBaseTest extends HumanTaskServicesBaseTest {
    
       
    // getTasksAssignedAsBusinessAdministrator(String userId, String language);
    
    @Test
    public void testGetTasksAssignedAsBusinessAdministratorWithUserLangNoTask() {
        List<TaskSummary> tasks = taskService.getTasksAssignedAsBusinessAdministrator("Bobba Fet", "en-UK");
        assertEquals(0, tasks.size());
    }

    @Test
    public void testGetTasksAssignedAsBusinessAdministratorByStatusWithUserLangNoTask() {
       List<Status> allActiveStatus = new ArrayList<Status>(){{
            this.add(Status.Created);
            this.add(Status.Ready);
            this.add(Status.Reserved);
            this.add(Status.InProgress);
            this.add(Status.Suspended);
          }};
        List<TaskSummary> tasks = taskService.getTasksAssignedAsBusinessAdministratorByStatus("Bobba Fet", "en-UK",allActiveStatus);
        assertEquals(0, tasks.size());
    }
    
    @Test
    public void testGetTasksAssignedAsBusinessAdministratorWithUserLangOneTask() {
        // One potential owner, should go straight to state Reserved
        String str = "(with (new Task()) { priority = 55, taskData = (with( new TaskData()) { } ), ";
        str += "peopleAssignments = (with ( new PeopleAssignments() ) { businessAdministrators = [new User('Bobba Fet')], }),";
        str += "name = 'This is my task name' })";
        Task task = TaskFactory.evalTask(new StringReader(str));
        taskService.addTask(task, new HashMap<String, Object>());
        List<TaskSummary> tasks = taskService.getTasksAssignedAsBusinessAdministrator("Bobba Fet", "en-UK");
        assertEquals(1, tasks.size());
    }
    
    
    @Test
    public void testGetTasksAssignedAsBusinessAdministratorWithUserOfGroupLangOneTask() {
        // JBPM-4862
        String str = "(with (new Task()) { priority = 55, taskData = (with( new TaskData()) { } ), ";
        str += "peopleAssignments = (with ( new PeopleAssignments() ) { businessAdministrators = [new Group('Crusaders')], }),";
        str += "name = 'This is my task name' })";
        Task task = TaskFactory.evalTask(new StringReader(str));
        taskService.addTask(task, new HashMap<String, Object>());
        List<TaskSummary> tasks = taskService.getTasksAssignedAsBusinessAdministrator("Bobba Fet", "en-UK");
        assertEquals(1, tasks.size());
    }
    
    @Test
    public void testGetTasksAssignedAsBusinessAdministratorWithUserOfWrongGroupLangOneTask() {
        // JBPM-4862
        String str = "(with (new Task()) { priority = 55, taskData = (with( new TaskData()) { } ), ";
        str += "peopleAssignments = (with ( new PeopleAssignments() ) { businessAdministrators = [new Group('Crusaders')], }),";
        str += "name = 'This is my task name' })";
        Task task = TaskFactory.evalTask(new StringReader(str));
        taskService.addTask(task, new HashMap<String, Object>());
        List<TaskSummary> tasks = taskService.getTasksAssignedAsBusinessAdministrator("nocrusadaer", "en-UK");
        assertEquals(0, tasks.size());
    }
    
    
    // getTasksAssignedAsExcludedOwner(String userId, String language);
    
    @Test
    public void testGetTasksAssignedAsExcludedOwnerWithUserLangNoTask() {
        List<TaskSummary> tasks = taskService.getTasksAssignedAsExcludedOwner("Bobba Fet");
        assertEquals(0, tasks.size());
    }
    
    @Test
    public void testGetTasksAssignedAsExcludedOwnerWithUserLangOneTask() {
        // One potential owner, should go straight to state Reserved
        String str = "(with (new Task()) { priority = 55, taskData = (with( new TaskData()) { } ), ";
        str += "peopleAssignments = (with ( new PeopleAssignments() ) { excludedOwners = [new User('Bobba Fet')],businessAdministrators = [ new User('Administrator') ], }),";
        str += "name =  'This is my task name' })";
        Task task = TaskFactory.evalTask(new StringReader(str));
        taskService.addTask(task, new HashMap<String, Object>());
        List<TaskSummary> tasks = taskService.getTasksAssignedAsExcludedOwner("Bobba Fet");
        assertEquals(1, tasks.size());
    }
    
    @Test
    public void testGetTasksAssignedAsPotentialOwnerWithExcluded() {
        // One potential owner, should go straight to state Reserved
        String str = "(with (new Task()) { priority = 55, taskData = (with( new TaskData()) { } ), ";
        str += "peopleAssignments = (with ( new PeopleAssignments() ) { potentialOwners = [new User('Bobba Fet'), new User('Darth Vader') ], excludedOwners = [new User('Darth Vader')],businessAdministrators = [ new User('Administrator') ], }),";
        str += "name =  'This is my task name' })";
        Task task = TaskFactory.evalTask(new StringReader(str));
        taskService.addTask(task, new HashMap<String, Object>());
        List<TaskSummary> tasks = taskService.getTasksAssignedAsPotentialOwner("Bobba Fet", "en-UK");
        assertEquals(1, tasks.size());
        
        tasks = taskService.getTasksAssignedAsPotentialOwner("Darth Vader", "en-UK");
        assertEquals(0, tasks.size());
    }
    
    @Test
    public void testGetTasksAssignedAsPotentialOwnerWithMultipleExcluded() {
        // One potential owner, should go straight to state Reserved
        String str = "(with (new Task()) { priority = 55, taskData = (with( new TaskData()) { } ), ";
        str += "peopleAssignments = (with ( new PeopleAssignments() ) { potentialOwners = [new User('Bobba Fet'), new User('Darth Vader'), new User('Luke Cage') ], excludedOwners = [new User('Darth Vader'), new User('Luke Cage')],businessAdministrators = [ new User('Administrator') ], }),";
        str += "name =  'This is my task name' })";
        Task task = TaskFactory.evalTask(new StringReader(str));
        taskService.addTask(task, new HashMap<String, Object>());
        List<TaskSummary> tasks = taskService.getTasksAssignedAsPotentialOwner("Bobba Fet", "en-UK");
        assertEquals(1, tasks.size());
        
        tasks = taskService.getTasksAssignedAsPotentialOwner("Darth Vader", "en-UK");
        assertEquals(0, tasks.size());
        
        tasks = taskService.getTasksAssignedAsPotentialOwner("Luke Cage", "en-UK");
        assertEquals(0, tasks.size());
    }

    @Test
    public void testGetTasksAssignedAsPotentialOwnerWithUserGroupsSingleUserExcluded() {
        String str = "(with (new Task()) { priority = 55, taskData = (with( new TaskData()) { } ), ";
        str += " peopleAssignments = (with ( new PeopleAssignments() ) { potentialOwners = [new Group('Crusaders'), ], excludedOwners = [new User('Luke Cage')],businessAdministrators = [ new User('Administrator') ], }),";
        str += "name =  'This is my task name' })";

        Task task = TaskFactory.evalTask(new StringReader(str));
        taskService.addTask(task, new HashMap<String, Object>());
        List<TaskSummary> tasks = taskService.getTasksAssignedAsPotentialOwner("Luke Cage", "en-UK");
        assertEquals(0, tasks.size());

        tasks = taskService.getTasksAssignedAsPotentialOwner("Bobba Fet", "en-UK");
        assertEquals(1, tasks.size());
    }

    @Test
    public void testGetTasksAssignedAsPotentialOwnerWithUserGroupsMultipleUsersExcluded() {
        String str = "(with (new Task()) { priority = 55, taskData = (with( new TaskData()) { } ), ";
        str += " peopleAssignments = (with ( new PeopleAssignments() ) { potentialOwners = [new Group('Crusaders'), ], excludedOwners = [new User('Luke Cage'), new User('Bobba Fet')],businessAdministrators = [ new User('Administrator') ], }),";
        str += "name =  'This is my task name' })";

        Task task = TaskFactory.evalTask(new StringReader(str));
        taskService.addTask(task, new HashMap<String, Object>());
        List<TaskSummary> tasks = taskService.getTasksAssignedAsPotentialOwner("Luke Cage", "en-UK");
        assertEquals(0, tasks.size());

        tasks = taskService.getTasksAssignedAsPotentialOwner("Bobba Fet", "en-UK");
        assertEquals(0, tasks.size());
    }
    
    // getTasksAssignedAsPotentialOwner(String userId, String language)
    
    @Test
    public void testGetTasksAssignedAsPotentialOwnerWithUserLangNoTask() {
        List<TaskSummary> tasks = taskService.getTasksAssignedAsPotentialOwner("Bobba Fet", "en-UK");
        assertEquals(0, tasks.size());
    }
    
    @Test
    public void testGetTasksAssignedAsPotentialOwnerWithUserLangOneTask() {
        // One potential owner, should go straight to state Reserved
        String str = "(with (new Task()) { priority = 55, taskData = (with( new TaskData()) { } ), ";
        str += "peopleAssignments = (with ( new PeopleAssignments() ) { potentialOwners = [new User('Bobba Fet')  ],businessAdministrators = [ new User('Administrator') ], }),";
        str += "name =  'This is my task name' })";
        Task task = TaskFactory.evalTask(new StringReader(str));
        taskService.addTask(task, new HashMap<String, Object>());
        List<TaskSummary> tasks = taskService.getTasksAssignedAsPotentialOwner("Bobba Fet", "en-UK");
        assertEquals(1, tasks.size());
        assertEquals("Bobba Fet", tasks.get(0).getActualOwnerId());
    }

    
    // getTasksAssignedAsPotentialOwner(String userId, List<String> groupIds, String language)
    
    @Test
    public void testGetTasksAssignedAsPotentialOwnerWithUserGroupsLangNoTaskNoGroupIds() {
        List<String> groupIds = new ArrayList<String>();
        groupIds.add("Crusaders");
        List<TaskSummary> tasks = taskService.getTasksAssignedAsPotentialOwner("Bobba Fet", groupIds);
        assertEquals(0, tasks.size());
    }
    
    @Test
    public void testGetTasksAssignedAsPotentialOwnerWithUserGroupsLangOneTaskOneUser() {
        // One potential owner, should go straight to state Reserved
        String str = "(with (new Task()) { priority = 55, taskData = (with( new TaskData()) { } ), ";
        str += "peopleAssignments = (with ( new PeopleAssignments() ) { potentialOwners = [new User('Bobba Fet')  ],businessAdministrators = [ new User('Administrator') ], }),";
        str += "name = 'This is my task name' })";
        Task task = TaskFactory.evalTask(new StringReader(str));
        taskService.addTask(task, new HashMap<String, Object>());
        List<String> groupIds = new ArrayList<String>();
        groupIds.add("Crusaders");
        List<TaskSummary> tasks = taskService.getTasksAssignedAsPotentialOwner("Bobba Fet", groupIds);
        assertEquals(1, tasks.size());
        assertEquals("Bobba Fet", tasks.get(0).getActualOwnerId());
    }
    
    @Test
    public void testGetTasksAssignedAsPotentialOwnerWithUserGroupsLangOneTaskOneGroup() {
        String str = "(with (new Task()) { priority = 55, taskData = (with( new TaskData()) { } ), ";
        str += "peopleAssignments = (with ( new PeopleAssignments() ) { potentialOwners = [new User('Bobba Fet'), new Group('Crusaders'), ],businessAdministrators = [ new User('Administrator') ], }),";
        str += "name =  'This is my task name' })";
        Task task = TaskFactory.evalTask(new StringReader(str));
        taskService.addTask(task, new HashMap<String, Object>());
        List<String> groupIds = new ArrayList<String>();
        groupIds.add("Crusaders");
        List<TaskSummary> tasks = taskService.getTasksAssignedAsPotentialOwner("Bobba Fet", groupIds);
        assertEquals(1, tasks.size());
        assertNull(tasks.get(0).getActualOwner());
        assertNull(tasks.get(0).getActualOwnerId());
        assertEquals(Status.Ready, tasks.get(0).getStatus());
    }
    
    @Test
    public void testGetTasksAssignedAsPotentialOwnerWithUserGroupsLangStatus() {
        String str = "(with (new Task()) { priority = 55, taskData = (with( new TaskData()) { } ), ";
        str += "peopleAssignments = (with ( new PeopleAssignments() ) { potentialOwners = [new User('Bobba Fet'), new Group('Crusaders'), ],businessAdministrators = [ new User('Administrator') ], }),";
        str += "name =  'This is my task name' })";
        Task task = TaskFactory.evalTask(new StringReader(str));
        taskService.addTask(task, new HashMap<String, Object>());
        List<String> groupIds = new ArrayList<String>();
        groupIds.add("Crusaders");
        
        List<Status> status = new ArrayList<Status>();
        status.add(Status.Ready);
        List<TaskSummary> tasks = taskService.getTasksAssignedAsPotentialOwnerByStatusByGroup("Bobba Fet", groupIds, status);
        assertEquals(1, tasks.size());
        assertNull(tasks.get(0).getActualOwner());
        assertNull(tasks.get(0).getActualOwnerId());
        assertEquals(Status.Ready, tasks.get(0).getStatus());
    }
    
    
    // getTasksAssignedAsPotentialOwner(String userId, List<String> groupIds, String language, int firstResult, int maxResults);

    @Test
    public void testGetTasksAssignedAsPotentialOwnerWithUserGroupsLangOffsetCountNoTask() {
        List<String> groupIds = new ArrayList<String>();
        groupIds.add("Crusaders");
        List<TaskSummary> tasks = taskService.getTasksAssignedAsPotentialOwner("Bobba Fet", groupIds, null, 0, 1);
        assertEquals(0, tasks.size());
    }
        
    @Test
    public void testGetTasksAssignedAsPotentialOwnerWithUserGroupsLangOffsetCountTwoTasksOneMaxResult() {
        // One potential owner, should go straight to state Reserved
        String str1 = "(with (new Task()) { priority = 55, taskData = (with( new TaskData()) { } ), ";
        str1 += "peopleAssignments = (with ( new PeopleAssignments() ) { potentialOwners = [new User('Bobba Fet') ],businessAdministrators = [ new User('Administrator') ], }),";
        str1 += "name = 'First task' })";
        String str2 = "(with (new Task()) { priority = 55, taskData = (with( new TaskData()) { } ), ";
        str2 += "peopleAssignments = (with ( new PeopleAssignments() ) { potentialOwners = [new User('Bobba Fet') ],businessAdministrators = [ new User('Administrator') ], }),";
        str2 += "name = 'Second task' })";
        Task task1 = TaskFactory.evalTask(new StringReader(str1));
        taskService.addTask(task1, new HashMap<String, Object>());
        Task task2 = TaskFactory.evalTask(new StringReader(str2));
        taskService.addTask(task2, new HashMap<String, Object>());       
        List<String> groupIds = new ArrayList<String>();
        groupIds.add("Crusaders");
        List<TaskSummary> tasks = taskService.getTasksAssignedAsPotentialOwner("Bobba Fet", groupIds, null, 0, 1);
        assertEquals(1, tasks.size());
        // FIXME tasks are returned in random order
        // assertEquals("First task", tasks.get(0).getName());
    }
    
    @Test
    public void testGetTasksAssignedAsPotentialOwnerWithUserGroupsLangOffsetCountTwoTasksTwoMaxResults() {
        // One potential owner, should go straight to state Reserved
        String str1 = "(with (new Task()) { priority = 55, taskData = (with( new TaskData()) { } ), ";
        str1 += "peopleAssignments = (with ( new PeopleAssignments() ) { potentialOwners = [new User('Bobba Fet') ],businessAdministrators = [ new User('Administrator') ], }),";
        str1 += "name =  'First task' })";
        String str2 = "(with (new Task()) { priority = 55, taskData = (with( new TaskData()) { } ), ";
        str2 += "peopleAssignments = (with ( new PeopleAssignments() ) { potentialOwners = [new User('Bobba Fet') ],businessAdministrators = [ new User('Administrator') ], }),";
        str2 += "name = 'Second task' })";
        Task task1 = TaskFactory.evalTask(new StringReader(str1));
        taskService.addTask(task1, new HashMap<String, Object>());
        Task task2 = TaskFactory.evalTask(new StringReader(str2));
        taskService.addTask(task2, new HashMap<String, Object>());       
        List<String> groupIds = new ArrayList<String>();
        groupIds.add("Crusaders");
        List<TaskSummary> tasks = taskService.getTasksAssignedAsPotentialOwner("Bobba Fet", groupIds, null, 0, 2);
        assertEquals(2, tasks.size());
        // FIXME tasks are returned in random order
        // assertEquals("First task", tasks.get(0).getName());
        // assertEquals("Second task", tasks.get(1).getName());
    }
    
    @Test
    public void testGetTasksAssignedAsPotentialOwnerWithUserGroupsLangOffsetCountTwoTasksOneOffsetOneMaxResult() {
        // One potential owner, should go straight to state Reserved
        String str1 = "(with (new Task()) { priority = 55, taskData = (with( new TaskData()) { } ), ";
        str1 += "peopleAssignments = (with ( new PeopleAssignments() ) { potentialOwners = [new User('Bobba Fet') ],businessAdministrators = [ new User('Administrator') ], }),";
        str1 += "name = 'First task' })";
        String str2 = "(with (new Task()) { priority = 55, taskData = (with( new TaskData()) { } ), ";
        str2 += "peopleAssignments = (with ( new PeopleAssignments() ) { potentialOwners = [new User('Bobba Fet') ],businessAdministrators = [ new User('Administrator') ], }),";
        str2 += "name =  'Second task' })";
        Task task1 = TaskFactory.evalTask(new StringReader(str1));
        taskService.addTask(task1, new HashMap<String, Object>());
        Task task2 = TaskFactory.evalTask(new StringReader(str2));
        taskService.addTask(task2, new HashMap<String, Object>());       
        List<String> groupIds = new ArrayList<String>();
        groupIds.add("Crusaders");
        List<TaskSummary> tasks = taskService.getTasksAssignedAsPotentialOwner("Bobba Fet", groupIds, null, 1, 1);
        // FIXME tasks are returned in random order
        // assertEquals(1, tasks.size());
        // assertEquals("Second task", tasks.get(0).getName());
    }
    
    
    // getTasksAssignedAsPotentialOwnerByStatus(String userId, List<Status> status, String language);
    
    @Test
    public void testGetTasksAssignedAsPotentialOwnerByStatusWithUserStatusLangNoTask() {
        
        List<Status> statuses = new ArrayList<Status>();
        statuses.add(Status.Created);
        statuses.add(Status.Ready);
        List<TaskSummary> tasks = taskService.getTasksAssignedAsPotentialOwnerByStatus("Bobba Fet", statuses, "en-UK");
        assertEquals(0, tasks.size());
    }
    
    @Test
    public void testGetTasksAssignedAsPotentialOwnerByStatusWithUserStatusLangOneTask() {
        // One potential owner, should go straight to state Reserved
        String str = "(with (new Task()) { priority = 55, taskData = (with( new TaskData()) { } ), ";
        str += "peopleAssignments = (with ( new PeopleAssignments() ) { potentialOwners = [new User('Bobba Fet') ],businessAdministrators = [ new User('Administrator') ], }),";
        str += "name = 'This is my task name' })";
        Task task = TaskFactory.evalTask(new StringReader(str));
        taskService.addTask(task, new HashMap<String, Object>());
        List<Status> statuses = new ArrayList<Status>();
        statuses.add(Status.Created);
        statuses.add(Status.Ready);
        List<TaskSummary> tasks = taskService.getTasksAssignedAsPotentialOwnerByStatus("Bobba Fet", statuses, "en-UK");
        assertEquals(0, tasks.size());
    }
    
    @Test
    public void testGetTasksAssignedAsPotentialOwnerByStatusWithUserStatusLangOneTaskReserved() {
        // One potential owner, should go straight to state Reserved
        String str = "(with (new Task()) { priority = 55, taskData = (with( new TaskData()) { } ), ";
        str += "peopleAssignments = (with ( new PeopleAssignments() ) { potentialOwners = [new User('Bobba Fet') ],businessAdministrators = [ new User('Administrator') ], }),";
        str += "name =  'This is my task name' })";
        Task task = TaskFactory.evalTask(new StringReader(str));
        taskService.addTask(task, new HashMap<String, Object>());
        List<Status> statuses = new ArrayList<Status>();
        statuses.add(Status.Reserved);
        List<TaskSummary> tasks = taskService.getTasksAssignedAsPotentialOwnerByStatus("Bobba Fet", statuses, "en-UK");
        assertEquals(1, tasks.size());
        assertEquals("Bobba Fet", tasks.get(0).getActualOwnerId());
    }
    
    
    // getTasksAssignedAsPotentialOwnerByStatusByGroup(String userId, List<String> groupIds, List<Status> status, String language);
    
    @Test
    public void testGetTasksAssignedAsPotentialOwnerByStatusByGroupWithUserStatusLangNoTask() {
        List<String> groupIds = new ArrayList<String>();
        groupIds.add("Crusaders");
        List<Status> statuses = new ArrayList<Status>();
        statuses.add(Status.Created);
        statuses.add(Status.Ready);
        List<TaskSummary> tasks = taskService.getTasksAssignedAsPotentialOwnerByStatusByGroup("Bobba Fet", groupIds, statuses);
        assertEquals(0, tasks.size());
    }
    
    @Test
    public void testGetTasksAssignedAsPotentialOwnerByStatusByGroupWithUserStatusLangOneTask() {
        // One potential owner, should go straight to state Reserved
        String str = "(with (new Task()) { priority = 55, taskData = (with( new TaskData()) { } ), ";
        str += "peopleAssignments = (with ( new PeopleAssignments() ) { potentialOwners = [new User('Bobba Fet') ],businessAdministrators = [ new User('Administrator') ], }),";
        str += "name =  'This is my task name' })";
        Task task = TaskFactory.evalTask(new StringReader(str));
        taskService.addTask(task, new HashMap<String, Object>());
        List<String> groupIds = new ArrayList<String>();
        groupIds.add("Crusaders");
        List<Status> statuses = new ArrayList<Status>();
        statuses.add(Status.Created);
        statuses.add(Status.Ready);
        List<TaskSummary> tasks = taskService.getTasksAssignedAsPotentialOwnerByStatusByGroup("Bobba Fet", groupIds, statuses);
        assertEquals(0, tasks.size());
    }
    
    @Test
    public void testGetTasksAssignedAsPotentialOwnerByStatusByGroupWithUserStatusLangOneTaskReserved() {
        // One potential owner, should go straight to state Reserved
        String str = "(with (new Task()) { priority = 55, taskData = (with( new TaskData()) { } ), ";
        str += "peopleAssignments = (with ( new PeopleAssignments() ) { potentialOwners = [new User('Bobba Fet') ],businessAdministrators = [ new User('Administrator') ], }),";
        str += "name =  'This is my task name' })";
        Task task = TaskFactory.evalTask(new StringReader(str));
        taskService.addTask(task, new HashMap<String, Object>());
        List<String> groupIds = new ArrayList<String>();
        groupIds.add("Crusaders");
        List<Status> statuses = new ArrayList<Status>();
        statuses.add(Status.Reserved);
        List<TaskSummary> tasks = taskService.getTasksAssignedAsPotentialOwnerByStatusByGroup("Bobba Fet", groupIds, statuses);
        assertEquals(1, tasks.size());
        assertEquals("Bobba Fet", tasks.get(0).getActualOwnerId());
    }
    
    
    // getTasksAssignedAsRecipient(String userId, String language);
    
    @Test
    public void testGetTasksAssignedAsRecipientWithUserLangNoTask() {
        List<TaskSummary> tasks = taskService.getTasksAssignedAsRecipient("Bobba Fet");
        assertEquals(0, tasks.size());
    }
    
    @Test
    public void testGetTasksAssignedAsRecipientWithUserLangOneTask() {
        // One potential owner, should go straight to state Reserved
        String str = "(with (new Task()) { priority = 55, taskData = (with( new TaskData()) { } ), ";
        str += "peopleAssignments = (with ( new PeopleAssignments() ) { recipients = [new User('Bobba Fet')],businessAdministrators = [ new User('Administrator') ], }),";
        str += "name =  'This is my task name'})";
        Task task = TaskFactory.evalTask(new StringReader(str));
        taskService.addTask(task, new HashMap<String, Object>());
        List<TaskSummary> tasks = taskService.getTasksAssignedAsRecipient("Bobba Fet");
        assertEquals(1, tasks.size());
    }
    
    
    // getTasksAssignedAsTaskInitiator(String userId, String language);
    
    @Test
    public void testGetTasksAssignedAsTaskInitiatorWithUserLangNoTask() {
        List<TaskSummary> tasks = taskService.getTasksAssignedAsTaskInitiator("Bobba Fet");
        assertEquals(0, tasks.size());
    }
    
    @Test
    public void testGetTasksAssignedAsTaskInitiatorWithUserLangOneTask() {
        // One potential owner, should go straight to state Reserved
        String str = "(with (new Task()) { priority = 55, taskData = (with( new TaskData()) { } ), ";
        str += "peopleAssignments = (with ( new PeopleAssignments() ) { taskInitiator = new User('Bobba Fet'),businessAdministrators = [ new User('Administrator') ], }),";
        str += "name =  'This is my task name' })";
        Task task = TaskFactory.evalTask(new StringReader(str));
        taskService.addTask(task, new HashMap<String, Object>());
        List<TaskSummary> tasks = taskService.getTasksAssignedAsTaskInitiator("Bobba Fet");
        assertEquals(1, tasks.size());
    }
    
    
    // getTasksAssignedAsTaskStakeholder(String userId, String language);
    
    @Test
    public void testGetTasksAssignedAsTaskStakeholderWithUserLangNoTask() {
        List<TaskSummary> tasks = taskService.getTasksAssignedAsTaskStakeholder("Bobba Fet");
        assertEquals(0, tasks.size());
    }
    
    @Test
    public void testGetTasksAssignedAsTaskStakeholderWithUserLangOneTask() {
        // One potential owner, should go straight to state Reserved
        String str = "(with (new Task()) { priority = 55, taskData = (with( new TaskData()) { } ), ";
        str += "peopleAssignments = (with ( new PeopleAssignments() ) { taskStakeholders = [new User('Bobba Fet')],businessAdministrators = [ new User('Administrator') ], }),";
        str += "name =  'This is my task name' })";
        Task task = TaskFactory.evalTask(new StringReader(str));
        taskService.addTask(task, new HashMap<String, Object>());
        List<TaskSummary> tasks = taskService.getTasksAssignedAsTaskStakeholder("Bobba Fet");
        assertEquals(1, tasks.size());
    }
    
    
    // getTasksAssignedByGroup(String groupId, String language)
    
    @Test
    public void testGetTasksAssignedByGroupWithGroupLangNoTask() {
        List<TaskSummary> tasks = taskService.getTasksAssignedByGroup("Crusaders");
        assertEquals(0, tasks.size());
    }
    
    @Test
    public void testGetTasksAssignedByGroupWithGroupLangOneTask() {
        // One potential owner, should go straight to state Reserved
        String str = "(with (new Task()) { priority = 55, taskData = (with( new TaskData()) { } ), ";
        str += "peopleAssignments = (with ( new PeopleAssignments() ) { potentialOwners = [new Group('Crusaders')  ],businessAdministrators = [ new User('Administrator') ], }),";
        str += "name = 'This is my task name' })";
        Task task = TaskFactory.evalTask(new StringReader(str));
        taskService.addTask(task, new HashMap<String, Object>());
        List<TaskSummary> tasks = taskService.getTasksAssignedByGroup("Crusaders");
        assertEquals(1, tasks.size());
    }
    
    
    // getTasksAssignedByGroups(List<String> groupsId, String language);
    
    @Test
    public void testGetTasksAssignedByGroupsWithGroupsLangNoTask() {
        List<String> groupIds = new ArrayList<String>();
        groupIds.add("Crusaders");
        List<TaskSummary> tasks = taskService.getTasksAssignedByGroups(groupIds);
        assertEquals(0, tasks.size());
    }
    
    @Test
    public void testGetTasksAssignedByGroupsWithGroupsLangOneTask() {
        // One potential owner, should go straight to state Reserved
        String str = "(with (new Task()) { priority = 55, taskData = (with( new TaskData()) { } ), ";
        str += "peopleAssignments = (with ( new PeopleAssignments() ) { potentialOwners = [new Group('Crusaders')  ],businessAdministrators = [ new User('Administrator') ], }),";
        str += "name = 'This is my task name' })";
        Task task = TaskFactory.evalTask(new StringReader(str));
        taskService.addTask(task, new HashMap<String, Object>());
        List<String> groupIds = new ArrayList<String>();
        groupIds.add("Crusaders");
        List<TaskSummary> tasks = taskService.getTasksAssignedByGroups(groupIds);
        assertEquals(1, tasks.size());
    }
    
    
    // getTasksAssignedByGroupsByExpirationDate(List<String> groupIds, String language, Date expirationDate);
    
//    @Test
//    public void testGetTasksAssignedByGroupsByExpirationDateWithGroupsLangDateNoTask() {
//        List<String> groupIds = new ArrayList<String>();
//        groupIds.add("Crusaders");
//        Date date = new Date();
//        List<TaskSummary> tasks = taskService.getTasksAssignedByGroupsByExpirationDate(groupIds, "en-UK", date);
//        assertEquals(0, tasks.size());
//    }
    
//    @Test
//    public void testGetTasksAssignedByGroupsByExpirationDateWithUserStatusDateOneTaskReserved() {
//        // One potential owner, should go straight to state Reserved
//        String str = "(with (new Task()) { priority = 55, taskData = (with( new TaskData()) { expirationTime = new Date( 10000000 ), } ), ";
//        str += "peopleAssignments = (with ( new PeopleAssignments() ) { potentialOwners = [new Group('Crusaders')  ], }),";
//        str += "names = [ new I18NText( 'en-UK', 'This is my task name')] })";
//        Task task = TaskFactory.evalTask(new StringReader(str));
//        taskService.addTask(task, new HashMap<String, Object>());
//        List<String> groupIds = new ArrayList<String>();
//        groupIds.add("Crusaders");
//        Date date = new Date(10000000);
//        List<TaskSummary> tasks = taskService.getTasksAssignedByGroupsByExpirationDate(groupIds, "en-UK", date);
//        assertEquals(1, tasks.size());
//        //assertEquals("Bobba Fet", tasks.get(0).getActualOwner().getId());
//    }
    
    
    // getTasksOwned(String userId);
    
    @Test
    public void testGetTasksOwnedWithUserNoTask() {
        List<TaskSummary> tasks = taskService.getTasksOwned("Bobba Fet", "en-UK");
        assertEquals(0, tasks.size());
    }
    
    @Test
    public void testGetTasksOwnedWithUserOneTask() {
        // One potential owner, should go straight to state Reserved
        String str = "(with (new Task()) { priority = 55, taskData = (with( new TaskData()) { } ), ";
        str += "peopleAssignments = (with ( new PeopleAssignments() ) { potentialOwners = [new User('Bobba Fet')  ],businessAdministrators = [ new User('Administrator') ], }),";
        str += "name = 'This is my task name'})";
        Task task = TaskFactory.evalTask(new StringReader(str));
        taskService.addTask(task, new HashMap<String, Object>());
        List<TaskSummary> tasks = taskService.getTasksOwned("Bobba Fet", "en-UK");
        assertEquals(1, tasks.size());
        assertEquals("Bobba Fet", tasks.get(0).getActualOwnerId());
    }
    
    
    // getTasksOwned(String userId, List<Status> status, String language);
    
    @Test
    public void testGetTasksOwnedWithUserStatusLangNoTask() {
        List<Status> statuses = new ArrayList<Status>();
        statuses.add(Status.Created);
        statuses.add(Status.Ready);
        List<TaskSummary> tasks = taskService.getTasksOwnedByStatus("Darth Vader", statuses, "en-UK");
        assertEquals(0, tasks.size());
    }
    
    @Test
    public void testGetTasksOwnedWithUserStatusLangOneTask() {
        // One potential owner, should go straight to state Reserved
        String str = "(with (new Task()) { priority = 55, taskData = (with( new TaskData()) { } ), ";
        str += "peopleAssignments = (with ( new PeopleAssignments() ) { potentialOwners = [new User('Bobba Fet')  ],businessAdministrators = [ new User('Administrator') ], }),";
        str += "name = 'This is my task name' })";
        Task task = TaskFactory.evalTask(new StringReader(str));
        taskService.addTask(task, new HashMap<String, Object>());
        List<Status> statuses = new ArrayList<Status>();
        statuses.add(Status.Reserved);
        List<TaskSummary> tasks = taskService.getTasksOwnedByStatus("Bobba Fet", statuses, "en-UK");
        assertEquals(1, tasks.size());
        assertEquals("Bobba Fet", tasks.get(0).getActualOwnerId());
    }
    
    @Test
    public void testGetTasksOwnedWithUserStatusLangOneTaskCompleted() {
        // One potential owner, should go straight to state Reserved
        String str = "(with (new Task()) { priority = 55, taskData = (with( new TaskData()) { } ), ";
        str += "peopleAssignments = (with ( new PeopleAssignments() ) { potentialOwners = [new User('Bobba Fet')  ],businessAdministrators = [ new User('Administrator') ], }),";
        str += "name =  'This is my task name' })";
        Task task = TaskFactory.evalTask(new StringReader(str));
        taskService.addTask(task, new HashMap<String, Object>());
        List<Status> statuses = new ArrayList<Status>();
        statuses.add(Status.Completed);
        List<TaskSummary> tasks = taskService.getTasksOwnedByStatus("Bobba Fet", statuses, "en-UK");
        assertEquals(0, tasks.size());
    }
    
    
    // getTasksOwnedByExpirationDate(String userId, List<Status> status, Date expirationDate);
    
    @Test
    public void testGetTasksOwnedByExpirationDateWithUserStatusDateNoTask() {
        List<Status> statuses = new ArrayList<Status>();
        statuses.add(Status.Created);
        statuses.add(Status.Ready);
        Date date = new Date();
        List<TaskSummary> tasks = taskService.getTasksOwnedByExpirationDate("Darth Vader", statuses, date);
        assertEquals(0, tasks.size());
    }
    
    @Test
    public void testGetTasksOwnedByExpirationDateWithUserStatusDateOneTaskReserved() {
        // One potential owner, should go straight to state Reserved
        String str = "(with (new Task()) { priority = 55, taskData = (with( new TaskData()) { expirationTime = new Date( 10000000 ), } ), ";
        str += "peopleAssignments = (with ( new PeopleAssignments() ) { potentialOwners = [new User('Bobba Fet')  ],businessAdministrators = [ new User('Administrator') ], }),";
        str += "name =  'This is my task name' })";
        Task task = TaskFactory.evalTask(new StringReader(str));
        taskService.addTask(task, new HashMap<String, Object>());
        List<Status> statuses = new ArrayList<Status>();
        statuses.add(Status.Reserved);
        Date date = new Date(10000000);
        List<TaskSummary> tasks = taskService.getTasksOwnedByExpirationDate("Bobba Fet", statuses, date);
        assertEquals(1, tasks.size());
        assertEquals("Bobba Fet", tasks.get(0).getActualOwnerId());
    }
    
    @Test
    public void testGetTasksOwnedByExpirationDateWithUserStatusDateOneTaskCompleted() {
        // One potential owner, should go straight to state Reserved
        String str = "(with (new Task()) { priority = 55, taskData = (with( new TaskData()) { expirationTime = new Date( 10000000 ), } ), ";
        str += "peopleAssignments = (with ( new PeopleAssignments() ) { potentialOwners = [new User('Bobba Fet')  ],businessAdministrators = [ new User('Administrator') ], }),";
        str += "name = 'This is my task name' })";
        Task task = TaskFactory.evalTask(new StringReader(str));
        taskService.addTask(task, new HashMap<String, Object>());
        List<Status> statuses = new ArrayList<Status>();
        statuses.add(Status.Completed);
        Date date = new Date(10000000);
        List<TaskSummary> tasks = taskService.getTasksOwnedByExpirationDate("Bobba Fet", statuses, date);
        assertEquals(0, tasks.size());
    }
    
    
    // getSubTasksAssignedAsPotentialOwner(long parentId, String userId, String language);

    @Ignore("not familiar with sub task concept; groupIds is not supplied to corresponding query")
    @Test
    public void testGetSubTasksAssignedAsPotentialOwnerWithParentUserLangNoTask() {
        List<TaskSummary> tasks = taskService.getSubTasksAssignedAsPotentialOwner(0, "Bobba Fet");
        assertEquals(0, tasks.size());
    }
    
    @Ignore("not familiar with sub task concept")
    @Test
    public void testGetSubTasksAssignedAsPotentialOwnerWithParentUserLangOneTask() {
        // One potential owner, should go straight to state Reserved
        String str = "(with (new Task()) { priority = 55, taskData = (with( new TaskData()) { } ), ";
        str += "peopleAssignments = (with ( new PeopleAssignments() ) { taskStakeholders = [new User('Bobba Fet')],businessAdministrators = [ new User('Administrator') ], }),";
        str += "name = 'This is my task name' })";
        Task task = TaskFactory.evalTask(new StringReader(str));
        taskService.addTask(task, new HashMap<String, Object>());
        List<TaskSummary> tasks = taskService.getSubTasksAssignedAsPotentialOwner(0, "Bobba Fet");
        assertEquals(1, tasks.size());
    }
    
    
    // getSubTasksByParent(long parentId);
    
    @Test
    public void testGetSubTasksByParentWithParentNoTask() {
        List<TaskSummary> tasks = taskService.getSubTasksByParent(0);
        assertEquals(0, tasks.size());
    }
    
    
    // getPendingSubTasksByParent(long parentId);
    
    @Test
    public void testGetPendingSubTasksByParentWithParentNoTask() {
        int count = taskService.getPendingSubTasksByParent(0);
        assertEquals(0, count);
    }
    
    
    // Task getTaskByWorkItemId(long workItemId);
    
    @Test
    public void testGetTaskByWorkItemIdWithWorkItemNoTask() {
        Task task = taskService.getTaskByWorkItemId(0);
        assertEquals(null, task);
    }
    
    
    // Task getTaskInstanceById(long taskId);
    
    @Test
    public void testGetTaskInstanceByIdWithWorkItemNoTask() {
        Task task = taskService.getTaskByWorkItemId(0);
        assertEquals(null, task);
    }
    
    @Test
    public void testGetTasksAssignedByExpirationDateOptional() {
        // One potential owner, should go straight to state Reserved
        String str = "(with (new Task()) { priority = 55, taskData = (with( new TaskData()) { } ), ";
        str += "peopleAssignments = (with ( new PeopleAssignments() ) { potentialOwners = [new User('Bobba Fet')],businessAdministrators = [ new User('Administrator') ], }),";
        str += "name =  'This is my task name' })";
        Task task = TaskFactory.evalTask(new StringReader(str));
        taskService.addTask(task, new HashMap<String, Object>());
        
        List<Status> statuses = new ArrayList<Status>();      
        statuses.add(Status.InProgress);
        statuses.add(Status.Reserved);
        statuses.add(Status.Created);
        List<TaskSummary> tasks = taskService.getTasksOwnedByExpirationDateOptional("Bobba Fet", statuses, new Date());
        assertEquals(1, tasks.size());
    }
    
    @Test
    public void testGetTasksByProcessInstanceId() {
        // One potential owner, should go straight to state Reserved
        String str = "(with (new Task()) { priority = 55, taskData = (with( new TaskData()) {processInstanceId = 99 } ), ";
        str += "peopleAssignments = (with ( new PeopleAssignments() ) { potentialOwners = [new User('Bobba Fet')],businessAdministrators = [ new User('Administrator') ], }),";
        str += "name =  'This is my task name' })";
        Task task = TaskFactory.evalTask(new StringReader(str));
        taskService.addTask(task, new HashMap<String, Object>());
        
        str = "(with (new Task()) { priority = 55, taskData = (with( new TaskData()) {processInstanceId = 100 } ), ";
        str += "peopleAssignments = (with ( new PeopleAssignments() ) { potentialOwners = [new User('Bobba Fet')],businessAdministrators = [ new User('Administrator') ], }),";
        str += "name =  'Another name' })";
        task = TaskFactory.evalTask(new StringReader(str));
        taskService.addTask(task, new HashMap<String, Object>());
        
        List<Long> tasks = taskService.getTasksByProcessInstanceId(99L);
        assertEquals(1, tasks.size());
    }
    
    @Test
    public void testGetTasksByStatusByProcessId() {
        // One potential owner, should go straight to state Reserved
        String str = "(with (new Task()) { priority = 55, taskData = (with( new TaskData()) {processInstanceId = 99 } ), ";
        str += "peopleAssignments = (with ( new PeopleAssignments() ) { potentialOwners = [new User('Bobba Fet')],businessAdministrators = [ new User('Administrator') ], }),";
        str += "name =  'This is my task name' })";
        Task task = TaskFactory.evalTask(new StringReader(str));
        taskService.addTask(task, new HashMap<String, Object>());
        
        str = "(with (new Task()) { priority = 55, taskData = (with( new TaskData()) {processInstanceId = 100 } ), ";
        str += "peopleAssignments = (with ( new PeopleAssignments() ) { potentialOwners = [new User('Bobba Fet')],businessAdministrators = [ new User('Administrator') ], }),";
        str += "name =  'Another name' })";
        task = TaskFactory.evalTask(new StringReader(str));
        taskService.addTask(task, new HashMap<String, Object>());
        
        List<Status> statuses = new ArrayList<Status>();      
        statuses.add(Status.Reserved);
        List<TaskSummary> tasks = taskService.getTasksByStatusByProcessInstanceId(99L, statuses, "en-UK");
        assertEquals(1, tasks.size());
    }
    
    @Test
    public void testGetTasksByStatusByProcessIdByTaskName() {
        // One potential owner, should go straight to state Reserved
        String str = "(with (new Task()) { priority = 55, taskData = (with( new TaskData()) {processInstanceId = 99 } ), ";
        str += "peopleAssignments = (with ( new PeopleAssignments() ) { potentialOwners = [new User('Bobba Fet')],businessAdministrators = [ new User('Administrator') ], }),";
        str += "name =  'This is my task name' })";
        Task task = TaskFactory.evalTask(new StringReader(str));
        String otherProcessId = "org.process.task.other";
        ((TaskDataImpl) task.getTaskData()).setProcessId(otherProcessId);;
        taskService.addTask(task, new HashMap<String, Object>());
        
        str = "(with (new Task()) { priority = 55, taskData = (with( new TaskData()) {processInstanceId = 100 } ), ";
        str += "peopleAssignments = (with ( new PeopleAssignments() ) { potentialOwners = [new User('Bobba Fet')],businessAdministrators = [ new User('Administrator') ], }),";
        str += "name =  'Another name' })";
        task = TaskFactory.evalTask(new StringReader(str));
        String processId = "org.process.task.test";
        ((TaskDataImpl) task.getTaskData()).setProcessId(processId);
        taskService.addTask(task, new HashMap<String, Object>());
        
        List<Status> statuses = new ArrayList<Status>();      
        statuses.add(Status.Reserved);
        List<TaskSummary> tasks = taskService.getTasksByStatusByProcessInstanceIdByTaskName(99L, statuses, "This is my task name");
        assertEquals(1, tasks.size());
        
        tasks = taskService.getTasksAssignedAsPotentialOwnerByProcessId("Bobba Fet", processId);
        assertEquals(1, tasks.size());
        
        tasks = taskService.getTasksAssignedAsPotentialOwnerByProcessId("Administrator", processId);
        assertEquals(0, tasks.size());
        
        tasks = taskService.getTasksAssignedAsPotentialOwnerByProcessId("Bobba Fet", otherProcessId);
        assertEquals(1, tasks.size());
        
        tasks = taskService.getTasksAssignedAsPotentialOwnerByProcessId("Bobba Fet", "bad.process.id");
        assertEquals(0, tasks.size());
    }
    
    @Test
    public void testGetTasksOwnedByExpirationDateBeforeSpecifiedDateNoTask() {
        List<Status> statuses = new ArrayList<Status>();   
        statuses.addAll(Arrays.asList(new Status[] {Status.Created, Status.Ready, Status.Reserved, Status.InProgress}));
        List<TaskSummary> tasks = taskService.getTasksOwnedByExpirationDateBeforeSpecifiedDate("Bobba Fet", statuses, new Date(100000005));
        assertEquals("Expecting empty list when no task available!", 0, tasks.size());
    }
    
    @Test
    public void testGetTasksOwnedByExpirationDateBeforeSpecifiedDate() {
        // should be included in result
        String str = "(with (new Task()) { priority = 55, taskData = (with( new TaskData()) {" ;
        str += "expirationTime = new java.text.SimpleDateFormat(\"yyyy-MM-dd\").parse(\"2011-10-15\") } ), ";
        str += "peopleAssignments = (with ( new PeopleAssignments() ) { potentialOwners = [new User('Bobba Fet')],businessAdministrators = [ new User('Administrator') ], }),";
        str += "name =  'This is my task name' })";
        Task task = TaskFactory.evalTask(new StringReader(str));
        taskService.addTask(task, new HashMap<String, Object>());
        
        // should be included in result
        str = "(with (new Task()) { priority = 55, taskData = (with( new TaskData()) {";
        str += "expirationTime = new java.text.SimpleDateFormat(\"yyyy-MM-dd\").parse(\"2013-04-15\") } ), ";
        str += "peopleAssignments = (with ( new PeopleAssignments() ) { potentialOwners = [new User('Bobba Fet')],businessAdministrators = [ new User('Administrator') ], }),";
        str += "name =  'This is my task name' })";
        task = TaskFactory.evalTask(new StringReader(str));
        taskService.addTask(task, new HashMap<String, Object>());
        
        // should not be included in result -> date is not before, it equals
        str = "(with (new Task()) { priority = 55, taskData = (with( new TaskData()) {";
        str += "expirationTime = new java.text.SimpleDateFormat(\"yyyy-MM-dd\").parse(\"2013-04-16\") } ), ";
        str += "peopleAssignments = (with ( new PeopleAssignments() ) { potentialOwners = [new User('Bobba Fet')],businessAdministrators = [ new User('Administrator') ], }),";
        str += "name =  'This is my task name' })";
        task = TaskFactory.evalTask(new StringReader(str));
        taskService.addTask(task, new HashMap<String, Object>());
        
        // should not be included in result -> date is after not before
        str = "(with (new Task()) { priority = 55, taskData = (with( new TaskData()) {";
        str += "expirationTime = new java.text.SimpleDateFormat(\"yyyy-MM-dd\").parse(\"2013-08-16\") } ), ";
        str += "peopleAssignments = (with ( new PeopleAssignments() ) { potentialOwners = [new User('Bobba Fet')],businessAdministrators = [ new User('Administrator') ], }),";
        str += "name =  'This is my task name' })";
        task = TaskFactory.evalTask(new StringReader(str));
        taskService.addTask(task, new HashMap<String, Object>());
        
        // should not be included in result -> userId is different
        str = "(with (new Task()) { priority = 55, taskData = (with( new TaskData()) {";
        str += "expirationTime = new java.text.SimpleDateFormat(\"yyyy-MM-dd\").parse(\"2013-01-15\") } ), ";
        str += "peopleAssignments = (with ( new PeopleAssignments() ) { potentialOwners = [new User('Darth Vader')],businessAdministrators = [ new User('Administrator') ], }),";
        str += "name =  'This is my task name' })";
        task = TaskFactory.evalTask(new StringReader(str));
        taskService.addTask(task, new HashMap<String, Object>());
        
        Date dateSpecified = createDate("2013-04-16");
        List<Status> statuses = new ArrayList<Status>();
        statuses.addAll(Arrays.asList(new Status[] {Status.Created, Status.Ready, Status.Reserved, Status.InProgress}));
        List<TaskSummary> tasks = taskService.getTasksOwnedByExpirationDateBeforeSpecifiedDate("Bobba Fet", statuses, dateSpecified);
        assertEquals(2, tasks.size());
        for(TaskSummary taskSummary : tasks) {
            assertTrue("Expected user 'Bobba Fet'!", taskSummary.getActualOwnerId().contains("Bobba Fet"));
            // the expiration date should be before the specified date
            assertTrue("Expiration date needs to be before the specified date!", taskSummary.getExpirationTime().compareTo(dateSpecified) < 0);
        }
    }
    
    @Test
    public void testModifyTaskName() {
        // JBPM-4148
        String taskName = "This is my task name";
        String str = "(with (new Task()) { priority = 55, taskData = (with( new TaskData()) { } ), ";
        str += "peopleAssignments = (with ( new PeopleAssignments() ) { potentialOwners = [new User('Bobba Fet')  ],businessAdministrators = [ new User('Administrator') ], }),";
        str += "name = '"+taskName+"' })";
        Task task = TaskFactory.evalTask(new StringReader(str));
        List<I18NText> names = new ArrayList<I18NText>();
            I18NText text = TaskModelProvider.getFactory().newI18NText();
            ((InternalI18NText) text).setLanguage("en-UK");
            ((InternalI18NText) text).setText(taskName);
            names.add(text);
            ((InternalTask)task).setNames(names);
        taskService.addTask(task, new HashMap<String, Object>());
        List<TaskSummary> tasks = taskService.getTasksAssignedAsPotentialOwner("Bobba Fet", "en-UK");
        assertEquals(1, tasks.size());
        assertEquals(taskName, tasks.get(0).getName());
        
        Task newTask = taskService.getTaskById(tasks.get(0).getId());
        List<I18NText> updatedNames = new ArrayList<I18NText>();
        I18NText updatedName = TaskModelProvider.getFactory().newI18NText();
        ((InternalI18NText) updatedName).setLanguage(newTask.getNames().get(0).getLanguage());
        ((InternalI18NText) updatedName).setText("New task name");
        updatedNames.add(updatedName);
        
        taskService.setTaskNames(newTask.getId(), updatedNames);
        
        List<TaskSummary> newTasks = taskService.getTasksAssignedAsPotentialOwner("Bobba Fet", "en-UK");
        assertEquals(1, newTasks.size());
        
        newTask = taskService.getTaskById(newTasks.get(0).getId());
        assertEquals("New task name", newTask.getNames().get(0).getText());
    }
    
    @Test
    public void testModifyTaskNameWithinTX() throws Exception{
        // JBPM-4148
        String taskName = "This is my task name";
        String str = "(with (new Task()) { priority = 55, taskData = (with( new TaskData()) { } ), ";
        str += "peopleAssignments = (with ( new PeopleAssignments() ) { potentialOwners = [new User('Bobba Fet')  ],businessAdministrators = [ new User('Administrator') ], }),";
        str += "name = '"+taskName+"' })";
        Task task = TaskFactory.evalTask(new StringReader(str));
        List<I18NText> names = new ArrayList<I18NText>();
            I18NText text = TaskModelProvider.getFactory().newI18NText();
            ((InternalI18NText) text).setLanguage("en-UK");
            ((InternalI18NText) text).setText(taskName);
            names.add(text);
            ((InternalTask)task).setNames(names);
        
        taskService.addTask(task, new HashMap<String, Object>());
        List<TaskSummary> tasks = taskService.getTasksAssignedAsPotentialOwner("Bobba Fet", "en-UK");
        assertEquals(1, tasks.size());
        assertEquals(taskName, tasks.get(0).getName());
        
        UserTransaction ut = InitialContext.doLookup("java:comp/UserTransaction");
        ut.begin();
        Task newTask = taskService.getTaskById(tasks.get(0).getId());
        ((InternalI18NText)newTask.getNames().get(0)).setText("New task name");
        ut.commit();
        
        List<TaskSummary> newTasks = taskService.getTasksAssignedAsPotentialOwner("Bobba Fet", "en-UK");
        assertEquals(1, newTasks.size());

        newTask = taskService.getTaskById(newTasks.get(0).getId());
        assertEquals("New task name", newTask.getNames().get(0).getText());
    }
    
    
    @Test
    public void testGetTasksAssignedAsPotentialOwnerSkipable() {
        // One potential owner, should go straight to state Reserved
        String str = "(with (new Task()) { priority = 55, taskData = (with( new TaskData()) {skipable=true } ), ";
        str += "peopleAssignments = (with ( new PeopleAssignments() ) { potentialOwners = [new User('Bobba Fet')  ],businessAdministrators = [ new User('Administrator') ], }),";
        str += "name = 'This is my task name' })";
        Task task = TaskFactory.evalTask(new StringReader(str));
        taskService.addTask(task, new HashMap<String, Object>());
        List<String> groupIds = new ArrayList<String>();
        groupIds.add("Crusaders");
        List<TaskSummary> tasks = taskService.getTasksAssignedAsPotentialOwner("Bobba Fet", groupIds);
        assertEquals(1, tasks.size());
        assertEquals("Bobba Fet", tasks.get(0).getActualOwnerId());
        assertEquals(true, tasks.get(0).isSkipable());
    }
    
    @Test
    public void testGetTasksAssignedAsPotentialOwnerCheckSubject() {
        // One potential owner, should go straight to state Reserved
        String str = "(with (new Task()) { subject = 'test subject', priority = 55, taskData = (with( new TaskData()) {skipable=true } ), ";
        str += "peopleAssignments = (with ( new PeopleAssignments() ) { potentialOwners = [new User('Bobba Fet')  ],businessAdministrators = [ new User('Administrator') ], }),";
        str += "name = 'This is my task name' })";
        Task task = TaskFactory.evalTask(new StringReader(str));
        
        taskService.addTask(task, new HashMap<String, Object>());
        List<String> groupIds = new ArrayList<String>();
        groupIds.add("Crusaders");
        List<TaskSummary> tasks = taskService.getTasksAssignedAsPotentialOwner("Bobba Fet", groupIds);
        assertEquals(1, tasks.size());
        assertEquals("Bobba Fet", tasks.get(0).getActualOwnerId());
        assertEquals(true, tasks.get(0).isSkipable());
        assertEquals("test subject", tasks.get(0).getSubject());
    }
}
