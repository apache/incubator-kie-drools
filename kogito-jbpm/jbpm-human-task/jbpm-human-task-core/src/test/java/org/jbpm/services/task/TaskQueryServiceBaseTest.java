/**
 * Copyright 2013 JBoss Inc
 *
 * Licensed under the Apache License, Version 2.0 (the "License"); you may not
 * use this file except in compliance with the License. You may obtain a copy of
 * the License at
 *
 * http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS, WITHOUT
 * WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied. See the
 * License for the specific language governing permissions and limitations under
 * the License.
 */

package org.jbpm.services.task;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertNull;
import static org.junit.Assert.assertTrue;

import java.io.StringReader;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import javax.naming.InitialContext;
import javax.transaction.UserTransaction;

import org.jbpm.services.task.impl.factories.TaskFactory;
import org.junit.Ignore;
import org.junit.Test;
import org.kie.api.task.model.I18NText;
import org.kie.api.task.model.Status;
import org.kie.api.task.model.Task;
import org.kie.api.task.model.TaskSummary;
import org.kie.internal.task.api.TaskModelProvider;
import org.kie.internal.task.api.TaskQueryService;
import org.kie.internal.task.api.model.InternalI18NText;
import org.kie.internal.task.api.model.InternalTaskData;

public abstract class TaskQueryServiceBaseTest extends HumanTaskServicesBaseTest {
    
       
    // getTasksAssignedAsBusinessAdministrator(String userId, String language);
    
    @Test
    public void testGetTasksAssignedAsBusinessAdministratorWithUserLangNoTask() {
        List<TaskSummary> tasks = taskService.getTasksAssignedAsBusinessAdministrator("Bobba Fet", "en-UK");
        assertEquals(0, tasks.size());
    }

    @Test
    public void testGetTasksAssignedAsBusinessAdministratorWithUserLangOneTask() {
        // One potential owner, should go straight to state Reserved
        String str = "(with (new Task()) { priority = 55, taskData = (with( new TaskData()) { } ), ";
        str += "peopleAssignments = (with ( new PeopleAssignments() ) { businessAdministrators = [new User('Bobba Fet')], }),";
        str += "names = [ new I18NText( 'en-UK', 'This is my task name')] })";
        Task task = TaskFactory.evalTask(new StringReader(str));
        taskService.addTask(task, new HashMap<String, Object>());
        List<TaskSummary> tasks = taskService.getTasksAssignedAsBusinessAdministrator("Bobba Fet", "en-UK");
        assertEquals(1, tasks.size());
    }
    
    
    // getTasksAssignedAsExcludedOwner(String userId, String language);
    
    @Test
    public void testGetTasksAssignedAsExcludedOwnerWithUserLangNoTask() {
        List<TaskSummary> tasks = taskService.getTasksAssignedAsExcludedOwner("Bobba Fet", "en-UK");
        assertEquals(0, tasks.size());
    }
    
    @Test
    public void testGetTasksAssignedAsExcludedOwnerWithUserLangOneTask() {
        // One potential owner, should go straight to state Reserved
        String str = "(with (new Task()) { priority = 55, taskData = (with( new TaskData()) { } ), ";
        str += "peopleAssignments = (with ( new PeopleAssignments() ) { excludedOwners = [new User('Bobba Fet')],businessAdministrators = [ new User('Administrator') ], }),";
        str += "names = [ new I18NText( 'en-UK', 'This is my task name')] })";
        Task task = TaskFactory.evalTask(new StringReader(str));
        taskService.addTask(task, new HashMap<String, Object>());
        List<TaskSummary> tasks = taskService.getTasksAssignedAsExcludedOwner("Bobba Fet", "en-UK");
        assertEquals(1, tasks.size());
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
        str += "names = [ new I18NText( 'en-UK', 'This is my task name')] })";
        Task task = TaskFactory.evalTask(new StringReader(str));
        taskService.addTask(task, new HashMap<String, Object>());
        List<TaskSummary> tasks = taskService.getTasksAssignedAsPotentialOwner("Bobba Fet", "en-UK");
        assertEquals(1, tasks.size());
        assertEquals("Bobba Fet", tasks.get(0).getActualOwner().getId());
    }

    
    // getTasksAssignedAsPotentialOwner(String userId, List<String> groupIds, String language)
    
    @Test
    public void testGetTasksAssignedAsPotentialOwnerWithUserGroupsLangNoTaskNoGroupIds() {
        List<String> groupIds = new ArrayList<String>();
        groupIds.add("Crusaders");
        List<TaskSummary> tasks = taskService.getTasksAssignedAsPotentialOwner("Bobba Fet", groupIds, "en-UK");
        assertEquals(0, tasks.size());
    }
    
    @Test
    public void testGetTasksAssignedAsPotentialOwnerWithUserGroupsLangOneTaskOneUser() {
        // One potential owner, should go straight to state Reserved
        String str = "(with (new Task()) { priority = 55, taskData = (with( new TaskData()) { } ), ";
        str += "peopleAssignments = (with ( new PeopleAssignments() ) { potentialOwners = [new User('Bobba Fet')  ],businessAdministrators = [ new User('Administrator') ], }),";
        str += "names = [ new I18NText( 'en-UK', 'This is my task name')] })";
        Task task = TaskFactory.evalTask(new StringReader(str));
        taskService.addTask(task, new HashMap<String, Object>());
        List<String> groupIds = new ArrayList<String>();
        groupIds.add("Crusaders");
        List<TaskSummary> tasks = taskService.getTasksAssignedAsPotentialOwner("Bobba Fet", groupIds, "en-UK");
        assertEquals(1, tasks.size());
        assertEquals("Bobba Fet", tasks.get(0).getActualOwner().getId());
    }
    
    @Test
    public void testGetTasksAssignedAsPotentialOwnerWithUserGroupsLangOneTaskOneGroup() {
        String str = "(with (new Task()) { priority = 55, taskData = (with( new TaskData()) { } ), ";
        str += "peopleAssignments = (with ( new PeopleAssignments() ) { potentialOwners = [new User('Bobba Fet'), new Group('Crusaders'), ],businessAdministrators = [ new User('Administrator') ], }),";
        str += "names = [ new I18NText( 'en-UK', 'This is my task name')] })";
        Task task = TaskFactory.evalTask(new StringReader(str));
        taskService.addTask(task, new HashMap<String, Object>());
        List<String> groupIds = new ArrayList<String>();
        groupIds.add("Crusaders");
        List<TaskSummary> tasks = taskService.getTasksAssignedAsPotentialOwner("Bobba Fet", groupIds, "en-UK");
        assertEquals(1, tasks.size());
        assertNull(tasks.get(0).getActualOwner());
        assertEquals(Status.Ready, tasks.get(0).getStatus());
    }
    
    @Test
    public void testGetTasksAssignedAsPotentialOwnerWithUserGroupsLangStatus() {
        String str = "(with (new Task()) { priority = 55, taskData = (with( new TaskData()) { } ), ";
        str += "peopleAssignments = (with ( new PeopleAssignments() ) { potentialOwners = [new User('Bobba Fet'), new Group('Crusaders'), ],businessAdministrators = [ new User('Administrator') ], }),";
        str += "names = [ new I18NText( 'en-UK', 'This is my task name')] })";
        Task task = TaskFactory.evalTask(new StringReader(str));
        taskService.addTask(task, new HashMap<String, Object>());
        List<String> groupIds = new ArrayList<String>();
        groupIds.add("Crusaders");
        
        List<Status> status = new ArrayList<Status>();
        status.add(Status.Ready);
        List<TaskSummary> tasks = taskService.getTasksAssignedAsPotentialOwnerByStatusByGroup("Bobba Fet", groupIds, status, "en-UK");
        assertEquals(1, tasks.size());
        assertNull(tasks.get(0).getActualOwner());
        assertEquals(Status.Ready, tasks.get(0).getStatus());
    }
    
    
    // getTasksAssignedAsPotentialOwner(String userId, List<String> groupIds, String language, int firstResult, int maxResults);

    @Test
    public void testGetTasksAssignedAsPotentialOwnerWithUserGroupsLangOffsetCountNoTask() {
        List<String> groupIds = new ArrayList<String>();
        groupIds.add("Crusaders");
        List<TaskSummary> tasks = taskService.getTasksAssignedAsPotentialOwner("Bobba Fet", groupIds, "en-UK", 0, 1);
        assertEquals(0, tasks.size());
    }
        
    @Test
    public void testGetTasksAssignedAsPotentialOwnerWithUserGroupsLangOffsetCountTwoTasksOneMaxResult() {
        // One potential owner, should go straight to state Reserved
        String str1 = "(with (new Task()) { priority = 55, taskData = (with( new TaskData()) { } ), ";
        str1 += "peopleAssignments = (with ( new PeopleAssignments() ) { potentialOwners = [new User('Bobba Fet') ],businessAdministrators = [ new User('Administrator') ], }),";
        str1 += "names = [ new I18NText( 'en-UK', 'First task')] })";
        String str2 = "(with (new Task()) { priority = 55, taskData = (with( new TaskData()) { } ), ";
        str2 += "peopleAssignments = (with ( new PeopleAssignments() ) { potentialOwners = [new User('Bobba Fet') ],businessAdministrators = [ new User('Administrator') ], }),";
        str2 += "names = [ new I18NText( 'en-UK', 'Second task')] })";
        Task task1 = TaskFactory.evalTask(new StringReader(str1));
        taskService.addTask(task1, new HashMap<String, Object>());
        Task task2 = TaskFactory.evalTask(new StringReader(str2));
        taskService.addTask(task2, new HashMap<String, Object>());       
        List<String> groupIds = new ArrayList<String>();
        groupIds.add("Crusaders");
        List<TaskSummary> tasks = taskService.getTasksAssignedAsPotentialOwner("Bobba Fet", groupIds, "en-UK", 0, 1);
        assertEquals(1, tasks.size());
        // FIXME tasks are returned in random order
        // assertEquals("First task", tasks.get(0).getName());
    }
    
    @Test
    public void testGetTasksAssignedAsPotentialOwnerWithUserGroupsLangOffsetCountTwoTasksTwoMaxResults() {
        // One potential owner, should go straight to state Reserved
        String str1 = "(with (new Task()) { priority = 55, taskData = (with( new TaskData()) { } ), ";
        str1 += "peopleAssignments = (with ( new PeopleAssignments() ) { potentialOwners = [new User('Bobba Fet') ],businessAdministrators = [ new User('Administrator') ], }),";
        str1 += "names = [ new I18NText( 'en-UK', 'First task')] })";
        String str2 = "(with (new Task()) { priority = 55, taskData = (with( new TaskData()) { } ), ";
        str2 += "peopleAssignments = (with ( new PeopleAssignments() ) { potentialOwners = [new User('Bobba Fet') ],businessAdministrators = [ new User('Administrator') ], }),";
        str2 += "names = [ new I18NText( 'en-UK', 'Second task')] })";
        Task task1 = TaskFactory.evalTask(new StringReader(str1));
        taskService.addTask(task1, new HashMap<String, Object>());
        Task task2 = TaskFactory.evalTask(new StringReader(str2));
        taskService.addTask(task2, new HashMap<String, Object>());       
        List<String> groupIds = new ArrayList<String>();
        groupIds.add("Crusaders");
        List<TaskSummary> tasks = taskService.getTasksAssignedAsPotentialOwner("Bobba Fet", groupIds, "en-UK", 0, 2);
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
        str1 += "names = [ new I18NText( 'en-UK', 'First task')] })";
        String str2 = "(with (new Task()) { priority = 55, taskData = (with( new TaskData()) { } ), ";
        str2 += "peopleAssignments = (with ( new PeopleAssignments() ) { potentialOwners = [new User('Bobba Fet') ],businessAdministrators = [ new User('Administrator') ], }),";
        str2 += "names = [ new I18NText( 'en-UK', 'Second task')] })";
        Task task1 = TaskFactory.evalTask(new StringReader(str1));
        taskService.addTask(task1, new HashMap<String, Object>());
        Task task2 = TaskFactory.evalTask(new StringReader(str2));
        taskService.addTask(task2, new HashMap<String, Object>());       
        List<String> groupIds = new ArrayList<String>();
        groupIds.add("Crusaders");
        List<TaskSummary> tasks = taskService.getTasksAssignedAsPotentialOwner("Bobba Fet", groupIds, "en-UK", 1, 1);
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
        str += "names = [ new I18NText( 'en-UK', 'This is my task name')] })";
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
        str += "names = [ new I18NText( 'en-UK', 'This is my task name')] })";
        Task task = TaskFactory.evalTask(new StringReader(str));
        taskService.addTask(task, new HashMap<String, Object>());
        List<Status> statuses = new ArrayList<Status>();
        statuses.add(Status.Reserved);
        List<TaskSummary> tasks = taskService.getTasksAssignedAsPotentialOwnerByStatus("Bobba Fet", statuses, "en-UK");
        assertEquals(1, tasks.size());
        assertEquals("Bobba Fet", tasks.get(0).getActualOwner().getId());
    }
    
    
    // getTasksAssignedAsPotentialOwnerByStatusByGroup(String userId, List<String> groupIds, List<Status> status, String language);
    
    @Test
    public void testGetTasksAssignedAsPotentialOwnerByStatusByGroupWithUserStatusLangNoTask() {
        List<String> groupIds = new ArrayList<String>();
        groupIds.add("Crusaders");
        List<Status> statuses = new ArrayList<Status>();
        statuses.add(Status.Created);
        statuses.add(Status.Ready);
        List<TaskSummary> tasks = taskService.getTasksAssignedAsPotentialOwnerByStatusByGroup("Bobba Fet", groupIds, statuses, "en-UK");
        assertEquals(0, tasks.size());
    }
    
    @Test
    public void testGetTasksAssignedAsPotentialOwnerByStatusByGroupWithUserStatusLangOneTask() {
        // One potential owner, should go straight to state Reserved
        String str = "(with (new Task()) { priority = 55, taskData = (with( new TaskData()) { } ), ";
        str += "peopleAssignments = (with ( new PeopleAssignments() ) { potentialOwners = [new User('Bobba Fet') ],businessAdministrators = [ new User('Administrator') ], }),";
        str += "names = [ new I18NText( 'en-UK', 'This is my task name')] })";
        Task task = TaskFactory.evalTask(new StringReader(str));
        taskService.addTask(task, new HashMap<String, Object>());
        List<String> groupIds = new ArrayList<String>();
        groupIds.add("Crusaders");
        List<Status> statuses = new ArrayList<Status>();
        statuses.add(Status.Created);
        statuses.add(Status.Ready);
        List<TaskSummary> tasks = taskService.getTasksAssignedAsPotentialOwnerByStatusByGroup("Bobba Fet", groupIds, statuses, "en-UK");
        assertEquals(0, tasks.size());
    }
    
    @Test
    public void testGetTasksAssignedAsPotentialOwnerByStatusByGroupWithUserStatusLangOneTaskReserved() {
        // One potential owner, should go straight to state Reserved
        String str = "(with (new Task()) { priority = 55, taskData = (with( new TaskData()) { } ), ";
        str += "peopleAssignments = (with ( new PeopleAssignments() ) { potentialOwners = [new User('Bobba Fet') ],businessAdministrators = [ new User('Administrator') ], }),";
        str += "names = [ new I18NText( 'en-UK', 'This is my task name')] })";
        Task task = TaskFactory.evalTask(new StringReader(str));
        taskService.addTask(task, new HashMap<String, Object>());
        List<String> groupIds = new ArrayList<String>();
        groupIds.add("Crusaders");
        List<Status> statuses = new ArrayList<Status>();
        statuses.add(Status.Reserved);
        List<TaskSummary> tasks = taskService.getTasksAssignedAsPotentialOwnerByStatusByGroup("Bobba Fet", groupIds, statuses, "en-UK");
        assertEquals(1, tasks.size());
        assertEquals("Bobba Fet", tasks.get(0).getActualOwner().getId());
    }
    
    
    // getTasksAssignedAsRecipient(String userId, String language);
    
    @Test
    public void testGetTasksAssignedAsRecipientWithUserLangNoTask() {
        List<TaskSummary> tasks = taskService.getTasksAssignedAsRecipient("Bobba Fet", "en-UK");
        assertEquals(0, tasks.size());
    }
    
    @Test
    public void testGetTasksAssignedAsRecipientWithUserLangOneTask() {
        // One potential owner, should go straight to state Reserved
        String str = "(with (new Task()) { priority = 55, taskData = (with( new TaskData()) { } ), ";
        str += "peopleAssignments = (with ( new PeopleAssignments() ) { recipients = [new User('Bobba Fet')],businessAdministrators = [ new User('Administrator') ], }),";
        str += "names = [ new I18NText( 'en-UK', 'This is my task name')] })";
        Task task = TaskFactory.evalTask(new StringReader(str));
        taskService.addTask(task, new HashMap<String, Object>());
        List<TaskSummary> tasks = taskService.getTasksAssignedAsRecipient("Bobba Fet", "en-UK");
        assertEquals(1, tasks.size());
    }
    
    
    // getTasksAssignedAsTaskInitiator(String userId, String language);
    
    @Test
    public void testGetTasksAssignedAsTaskInitiatorWithUserLangNoTask() {
        List<TaskSummary> tasks = taskService.getTasksAssignedAsTaskInitiator("Bobba Fet", "en-UK");
        assertEquals(0, tasks.size());
    }
    
    @Test
    public void testGetTasksAssignedAsTaskInitiatorWithUserLangOneTask() {
        // One potential owner, should go straight to state Reserved
        String str = "(with (new Task()) { priority = 55, taskData = (with( new TaskData()) { } ), ";
        str += "peopleAssignments = (with ( new PeopleAssignments() ) { taskInitiator = new User('Bobba Fet'),businessAdministrators = [ new User('Administrator') ], }),";
        str += "names = [ new I18NText( 'en-UK', 'This is my task name')] })";
        Task task = TaskFactory.evalTask(new StringReader(str));
        taskService.addTask(task, new HashMap<String, Object>());
        List<TaskSummary> tasks = taskService.getTasksAssignedAsTaskInitiator("Bobba Fet", "en-UK");
        assertEquals(1, tasks.size());
    }
    
    
    // getTasksAssignedAsTaskStakeholder(String userId, String language);
    
    @Test
    public void testGetTasksAssignedAsTaskStakeholderWithUserLangNoTask() {
        List<TaskSummary> tasks = taskService.getTasksAssignedAsTaskStakeholder("Bobba Fet", "en-UK");
        assertEquals(0, tasks.size());
    }
    
    @Test
    public void testGetTasksAssignedAsTaskStakeholderWithUserLangOneTask() {
        // One potential owner, should go straight to state Reserved
        String str = "(with (new Task()) { priority = 55, taskData = (with( new TaskData()) { } ), ";
        str += "peopleAssignments = (with ( new PeopleAssignments() ) { taskStakeholders = [new User('Bobba Fet')],businessAdministrators = [ new User('Administrator') ], }),";
        str += "names = [ new I18NText( 'en-UK', 'This is my task name')] })";
        Task task = TaskFactory.evalTask(new StringReader(str));
        taskService.addTask(task, new HashMap<String, Object>());
        List<TaskSummary> tasks = taskService.getTasksAssignedAsTaskStakeholder("Bobba Fet", "en-UK");
        assertEquals(1, tasks.size());
    }
    
    
    // getTasksAssignedByGroup(String groupId, String language)
    
    @Test
    public void testGetTasksAssignedByGroupWithGroupLangNoTask() {
        List<TaskSummary> tasks = taskService.getTasksAssignedByGroup("Crusaders", "en-UK");
        assertEquals(0, tasks.size());
    }
    
    @Test
    public void testGetTasksAssignedByGroupWithGroupLangOneTask() {
        // One potential owner, should go straight to state Reserved
        String str = "(with (new Task()) { priority = 55, taskData = (with( new TaskData()) { } ), ";
        str += "peopleAssignments = (with ( new PeopleAssignments() ) { potentialOwners = [new Group('Crusaders')  ],businessAdministrators = [ new User('Administrator') ], }),";
        str += "names = [ new I18NText( 'en-UK', 'This is my task name')] })";
        Task task = TaskFactory.evalTask(new StringReader(str));
        taskService.addTask(task, new HashMap<String, Object>());
        List<TaskSummary> tasks = taskService.getTasksAssignedByGroup("Crusaders", "en-UK");
        assertEquals(1, tasks.size());
    }
    
    
    // getTasksAssignedByGroups(List<String> groupsId, String language);
    
    @Test
    public void testGetTasksAssignedByGroupsWithGroupsLangNoTask() {
        List<String> groupIds = new ArrayList<String>();
        groupIds.add("Crusaders");
        List<TaskSummary> tasks = taskService.getTasksAssignedByGroups(groupIds, "en-UK");
        assertEquals(0, tasks.size());
    }
    
    @Test
    public void testGetTasksAssignedByGroupsWithGroupsLangOneTask() {
        // One potential owner, should go straight to state Reserved
        String str = "(with (new Task()) { priority = 55, taskData = (with( new TaskData()) { } ), ";
        str += "peopleAssignments = (with ( new PeopleAssignments() ) { potentialOwners = [new Group('Crusaders')  ],businessAdministrators = [ new User('Administrator') ], }),";
        str += "names = [ new I18NText( 'en-UK', 'This is my task name')] })";
        Task task = TaskFactory.evalTask(new StringReader(str));
        taskService.addTask(task, new HashMap<String, Object>());
        List<String> groupIds = new ArrayList<String>();
        groupIds.add("Crusaders");
        List<TaskSummary> tasks = taskService.getTasksAssignedByGroups(groupIds, "en-UK");
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
        str += "names = [ new I18NText( 'en-UK', 'This is my task name')] })";
        Task task = TaskFactory.evalTask(new StringReader(str));
        taskService.addTask(task, new HashMap<String, Object>());
        List<TaskSummary> tasks = taskService.getTasksOwned("Bobba Fet", "en-UK");
        assertEquals(1, tasks.size());
        assertEquals("Bobba Fet", tasks.get(0).getActualOwner().getId());
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
        str += "names = [ new I18NText( 'en-UK', 'This is my task name')] })";
        Task task = TaskFactory.evalTask(new StringReader(str));
        taskService.addTask(task, new HashMap<String, Object>());
        List<Status> statuses = new ArrayList<Status>();
        statuses.add(Status.Reserved);
        List<TaskSummary> tasks = taskService.getTasksOwnedByStatus("Bobba Fet", statuses, "en-UK");
        assertEquals(1, tasks.size());
        assertEquals("Bobba Fet", tasks.get(0).getActualOwner().getId());
    }
    
    @Test
    public void testGetTasksOwnedWithUserStatusLangOneTaskCompleted() {
        // One potential owner, should go straight to state Reserved
        String str = "(with (new Task()) { priority = 55, taskData = (with( new TaskData()) { } ), ";
        str += "peopleAssignments = (with ( new PeopleAssignments() ) { potentialOwners = [new User('Bobba Fet')  ],businessAdministrators = [ new User('Administrator') ], }),";
        str += "names = [ new I18NText( 'en-UK', 'This is my task name')] })";
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
        str += "names = [ new I18NText( 'en-UK', 'This is my task name')] })";
        Task task = TaskFactory.evalTask(new StringReader(str));
        taskService.addTask(task, new HashMap<String, Object>());
        List<Status> statuses = new ArrayList<Status>();
        statuses.add(Status.Reserved);
        Date date = new Date(10000000);
        List<TaskSummary> tasks = taskService.getTasksOwnedByExpirationDate("Bobba Fet", statuses, date);
        assertEquals(1, tasks.size());
        assertEquals("Bobba Fet", tasks.get(0).getActualOwner().getId());
    }
    
    @Test
    public void testGetTasksOwnedByExpirationDateWithUserStatusDateOneTaskCompleted() {
        // One potential owner, should go straight to state Reserved
        String str = "(with (new Task()) { priority = 55, taskData = (with( new TaskData()) { expirationTime = new Date( 10000000 ), } ), ";
        str += "peopleAssignments = (with ( new PeopleAssignments() ) { potentialOwners = [new User('Bobba Fet')  ],businessAdministrators = [ new User('Administrator') ], }),";
        str += "names = [ new I18NText( 'en-UK', 'This is my task name')] })";
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
        List<TaskSummary> tasks = taskService.getSubTasksAssignedAsPotentialOwner(0, "Bobba Fet", "en-UK");
        assertEquals(0, tasks.size());
    }
    
    @Ignore("not familiar with sub task concept")
    @Test
    public void testGetSubTasksAssignedAsPotentialOwnerWithParentUserLangOneTask() {
        // One potential owner, should go straight to state Reserved
        String str = "(with (new Task()) { priority = 55, taskData = (with( new TaskData()) { } ), ";
        str += "peopleAssignments = (with ( new PeopleAssignments() ) { taskStakeholders = [new User('Bobba Fet')],businessAdministrators = [ new User('Administrator') ], }),";
        str += "names = [ new I18NText( 'en-UK', 'This is my task name')] })";
        Task task = TaskFactory.evalTask(new StringReader(str));
        taskService.addTask(task, new HashMap<String, Object>());
        List<TaskSummary> tasks = taskService.getSubTasksAssignedAsPotentialOwner(0, "Bobba Fet", "en-UK");
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
        str += "names = [ new I18NText( 'en-UK', 'This is my task name')] })";
        Task task = TaskFactory.evalTask(new StringReader(str));
        taskService.addTask(task, new HashMap<String, Object>());
        
        List<Status> statuses = new ArrayList<Status>();      
        statuses.add(Status.InProgress);
        statuses.add(Status.Reserved);
        statuses.add(Status.Created);
        List<TaskSummary> tasks = taskService.getTasksOwnedByExpirationDateOptional("Bobba Fet", statuses, new Date());
        assertEquals(1, tasks.size());
    }
    
//    @Test
//    public void testGetTasksAssignedByGroupsByExpirationDateOptional() {
//        // One potential owner, should go straight to state Reserved
//        String str = "(with (new Task()) { priority = 55, taskData = (with( new TaskData()) { } ), ";
//        str += "peopleAssignments = (with ( new PeopleAssignments() ) { potentialOwners = [new Group('Crusaders')  ], }),";
//        str += "names = [ new I18NText( 'en-UK', 'This is my task name')] })";
//        Task task = TaskFactory.evalTask(new StringReader(str));
//        taskService.addTask(task, new HashMap<String, Object>());
//        List<String> groupIds = new ArrayList<String>();
//        groupIds.add("Crusaders");
//        
//        List<TaskSummary> tasks = taskService.getTasksAssignedByGroupsByExpirationDateOptional(groupIds, "en-UK", new Date());
//        assertEquals(1, tasks.size());
//    }
    
    @Test
    public void testGetTasksByProcessInstanceId() {
        // One potential owner, should go straight to state Reserved
        String str = "(with (new Task()) { priority = 55, taskData = (with( new TaskData()) {processInstanceId = 99 } ), ";
        str += "peopleAssignments = (with ( new PeopleAssignments() ) { potentialOwners = [new User('Bobba Fet')],businessAdministrators = [ new User('Administrator') ], }),";
        str += "names = [ new I18NText( 'en-UK', 'This is my task name')] })";
        Task task = TaskFactory.evalTask(new StringReader(str));
        taskService.addTask(task, new HashMap<String, Object>());
        
        str = "(with (new Task()) { priority = 55, taskData = (with( new TaskData()) {processInstanceId = 100 } ), ";
        str += "peopleAssignments = (with ( new PeopleAssignments() ) { potentialOwners = [new User('Bobba Fet')],businessAdministrators = [ new User('Administrator') ], }),";
        str += "names = [ new I18NText( 'en-UK', 'Another name')] })";
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
        str += "names = [ new I18NText( 'en-UK', 'This is my task name')] })";
        Task task = TaskFactory.evalTask(new StringReader(str));
        taskService.addTask(task, new HashMap<String, Object>());
        
        str = "(with (new Task()) { priority = 55, taskData = (with( new TaskData()) {processInstanceId = 100 } ), ";
        str += "peopleAssignments = (with ( new PeopleAssignments() ) { potentialOwners = [new User('Bobba Fet')],businessAdministrators = [ new User('Administrator') ], }),";
        str += "names = [ new I18NText( 'en-UK', 'Another name')] })";
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
        str += "names = [ new I18NText( 'en-UK', 'This is my task name')] })";
        Task task = TaskFactory.evalTask(new StringReader(str));
        taskService.addTask(task, new HashMap<String, Object>());
        
        str = "(with (new Task()) { priority = 55, taskData = (with( new TaskData()) {processInstanceId = 100 } ), ";
        str += "peopleAssignments = (with ( new PeopleAssignments() ) { potentialOwners = [new User('Bobba Fet')],businessAdministrators = [ new User('Administrator') ], }),";
        str += "names = [ new I18NText( 'en-UK', 'Another name')] })";
        task = TaskFactory.evalTask(new StringReader(str));
        taskService.addTask(task, new HashMap<String, Object>());
        
        List<Status> statuses = new ArrayList<Status>();      
        statuses.add(Status.Reserved);
        List<TaskSummary> tasks = taskService.getTasksByStatusByProcessInstanceIdByTaskName(99L, statuses, "This is my task name", "en-UK");
        assertEquals(1, tasks.size());
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
        str += "names = [ new I18NText( 'en-UK', 'This is my task name')] })";
        Task task = TaskFactory.evalTask(new StringReader(str));
        taskService.addTask(task, new HashMap<String, Object>());
        
        // should be included in result
        str = "(with (new Task()) { priority = 55, taskData = (with( new TaskData()) {";
        str += "expirationTime = new java.text.SimpleDateFormat(\"yyyy-MM-dd\").parse(\"2013-04-15\") } ), ";
        str += "peopleAssignments = (with ( new PeopleAssignments() ) { potentialOwners = [new User('Bobba Fet')],businessAdministrators = [ new User('Administrator') ], }),";
        str += "names = [ new I18NText( 'en-UK', 'This is my task name')] })";
        task = TaskFactory.evalTask(new StringReader(str));
        taskService.addTask(task, new HashMap<String, Object>());
        
        // should not be included in result -> date is not before, it equals
        str = "(with (new Task()) { priority = 55, taskData = (with( new TaskData()) {";
        str += "expirationTime = new java.text.SimpleDateFormat(\"yyyy-MM-dd\").parse(\"2013-04-16\") } ), ";
        str += "peopleAssignments = (with ( new PeopleAssignments() ) { potentialOwners = [new User('Bobba Fet')],businessAdministrators = [ new User('Administrator') ], }),";
        str += "names = [ new I18NText( 'en-UK', 'This is my task name')] })";
        task = TaskFactory.evalTask(new StringReader(str));
        taskService.addTask(task, new HashMap<String, Object>());
        
        // should not be included in result -> date is after not before
        str = "(with (new Task()) { priority = 55, taskData = (with( new TaskData()) {";
        str += "expirationTime = new java.text.SimpleDateFormat(\"yyyy-MM-dd\").parse(\"2013-08-16\") } ), ";
        str += "peopleAssignments = (with ( new PeopleAssignments() ) { potentialOwners = [new User('Bobba Fet')],businessAdministrators = [ new User('Administrator') ], }),";
        str += "names = [ new I18NText( 'en-UK', 'This is my task name')] })";
        task = TaskFactory.evalTask(new StringReader(str));
        taskService.addTask(task, new HashMap<String, Object>());
        
        // should not be included in result -> userId is different
        str = "(with (new Task()) { priority = 55, taskData = (with( new TaskData()) {";
        str += "expirationTime = new java.text.SimpleDateFormat(\"yyyy-MM-dd\").parse(\"2013-01-15\") } ), ";
        str += "peopleAssignments = (with ( new PeopleAssignments() ) { potentialOwners = [new User('Darth Vader')],businessAdministrators = [ new User('Administrator') ], }),";
        str += "names = [ new I18NText( 'en-UK', 'This is my task name')] })";
        task = TaskFactory.evalTask(new StringReader(str));
        taskService.addTask(task, new HashMap<String, Object>());
        
        Date dateSpecified = createDate("2013-04-16");
        List<Status> statuses = new ArrayList<Status>();
        statuses.addAll(Arrays.asList(new Status[] {Status.Created, Status.Ready, Status.Reserved, Status.InProgress}));
        List<TaskSummary> tasks = taskService.getTasksOwnedByExpirationDateBeforeSpecifiedDate("Bobba Fet", statuses, dateSpecified);
        assertEquals(2, tasks.size());
        for(TaskSummary taskSummary : tasks) {
            assertTrue("Expected user 'Bobba Fet'!", taskSummary.getActualOwner().toString().contains("Bobba Fet"));
            // the expiration date should be before the specified date
            assertTrue("Expiration date needs to be before the specified date!", taskSummary.getExpirationTime().compareTo(dateSpecified) < 0);
        }
    }
    
    @Test
    public void testGetTasksByVariousFields() {
        Task [] tasks = new Task[12];
        List<Long> workItemIds = new ArrayList<Long>();
        List<Long> taskIds = new ArrayList<Long>();
        List<String> busAdmins = new ArrayList<String>();
        List<String> potOwners = new ArrayList<String>();
        List<Status> statuses = new ArrayList<Status>();      
        statuses.add(Status.Reserved);
       
        long workItemId = 23;
        String busAdmin = "Wintermute";
        String potOwner = "Maelcum";
        String str = "(with (new Task()) { priority = 55, taskData = (with( new TaskData()) { } ), ";
        str += "peopleAssignments = (with ( new PeopleAssignments() ) { "
                + "businessAdministrators = [new User('" + busAdmin + "')],"
                + "potentialOwners = [new User('" + potOwner + "')]"
                + " }),";
        str += "names = [ new I18NText( 'en-UK', 'This is my task name')] })";
        Task taskImpl = TaskFactory.evalTask(new StringReader(str));
        ((InternalTaskData) taskImpl.getTaskData()).setWorkItemId(workItemId);
        ((InternalTaskData) taskImpl.getTaskData()).setProcessInstanceId(workItemId);
        taskService.addTask(taskImpl, new HashMap<String, Object>());
        taskIds.add(taskImpl.getId());
        assertEquals( potOwner, taskImpl.getTaskData().getActualOwner().getId() );
        tasks[0] = taskImpl;
        
        workItemIds.add(workItemId);
        busAdmins.add(busAdmin);
        potOwners.add(potOwner);
        
        List<TaskSummary> results = taskService.getTasksByVariousFields(workItemIds, null, null, null, null, null, null, null, false);
        assertEquals("List of tasks", 1, results.size());
        
        workItemId = 25;
        busAdmin = "Neuromancer";
        potOwner = "Hideo";
        str = "(with (new Task()) { priority = 55, taskData = (with( new TaskData()) { } ), ";
        str += "peopleAssignments = (with ( new PeopleAssignments() ) { "
                + "businessAdministrators = [new User('" + busAdmin + "')],"
                + "potentialOwners = [new User('" + potOwner + "')]"
                + " }),";
        str += "names = [ new I18NText( 'en-UK', 'This is my task name')] })";
        taskImpl = TaskFactory.evalTask(new StringReader(str));
        ((InternalTaskData) taskImpl.getTaskData()).setWorkItemId(workItemId);
        ((InternalTaskData) taskImpl.getTaskData()).setProcessInstanceId(workItemId);
        taskService.addTask(taskImpl, new HashMap<String, Object>());
        taskIds.add(taskImpl.getId());
        assertEquals( potOwner, taskImpl.getTaskData().getActualOwner().getId() );
        
        // start task
        taskService.start(taskImpl.getId(), potOwner);
        tasks[1] = (Task) taskService.getTaskById(taskImpl.getId());
        statuses.add(tasks[1].getTaskData().getStatus());
        
        workItemIds.add(workItemId);
        busAdmins.add(busAdmin);
        potOwners.add(potOwner);

        // Add one more task, just to make sure things are working wel
        workItemId = 57;
        busAdmin = "reviewer";
        potOwner = "translator";
        str = "(with (new Task()) { priority = 55, taskData = (with( new TaskData()) { } ), ";
        str += "peopleAssignments = (with ( new PeopleAssignments() ) { "
                + "businessAdministrators = [new User('" + busAdmin + "')],"
                + "potentialOwners = [new User('" + potOwner + "')]"
                + " }),";
        str += "names = [ new I18NText( 'nl-NL', 'Koude Bevel')] })";
        taskImpl = TaskFactory.evalTask(new StringReader(str));
        ((InternalTaskData) taskImpl.getTaskData()).setWorkItemId(workItemId);
        ((InternalTaskData) taskImpl.getTaskData()).setProcessInstanceId(workItemId);
        taskService.addTask(taskImpl, new HashMap<String, Object>());
        assertEquals( potOwner, taskImpl.getTaskData().getActualOwner().getId() );
        taskService.start(taskImpl.getId(), potOwner);
        taskService.fail(taskImpl.getId(), busAdmin, null);
        
        results = taskService.getTasksByVariousFields(null, null, null, null, null, null, null, null, false);
        assertEquals("List of tasks", 3, results.size());
        
        results = taskService.getTasksByVariousFields(workItemIds, null, null, null, null, null, null, null, false);
        assertEquals("List of tasks", 2, results.size());
        results = taskService.getTasksByVariousFields(null, taskIds, null, null, null, null, null, null, false );
        assertEquals("List of tasks", 2, results.size());
        results = taskService.getTasksByVariousFields(null, null, workItemIds, null, null, null, null, null, false);
        assertEquals("List of tasks", 2, results.size());
        results = taskService.getTasksByVariousFields(null, null, null, busAdmins, null, null, null, null, false);
        assertEquals("List of tasks", 2, results.size());
        results = taskService.getTasksByVariousFields(null, null, null, null, potOwners, null, null, null, false);
        assertEquals("List of tasks", 2, results.size());
        results = taskService.getTasksByVariousFields(null, null, null, null, null, potOwners, null, null, false);
        assertEquals("List of tasks", 2, results.size());
        results = taskService.getTasksByVariousFields(null, null, null, null, null, null, statuses, null, false);
        assertEquals("List of tasks", 2, results.size());
        
        // work item id and/or task id 
        List<Long> testLongList = new ArrayList<Long>();
        testLongList.add(workItemIds.get(0));
        List<Long> testLongListTwo = new ArrayList<Long>();
        testLongListTwo.add(taskIds.get(1));
        results = taskService.getTasksByVariousFields(testLongList, testLongListTwo, null, null, null, null, null, null, false);
        assertEquals("List of tasks", 0, results.size());
        results = taskService.getTasksByVariousFields(testLongList, testLongListTwo, null, null, null, null, null, null, true);
        assertEquals("List of tasks", 2, results.size());
        
        // task id and/or process instance id 
        testLongList.clear();
        testLongList.add(workItemIds.get(1));
        results = taskService.getTasksByVariousFields(null, testLongListTwo, testLongList, null, null, null, null, null, true);
        assertEquals("List of tasks", 1, results.size());
        results = taskService.getTasksByVariousFields(null, testLongListTwo, testLongList, null, null, null, null, null, false);
        assertEquals("List of tasks", 1, results.size());
        
        // process instance id and/or bus admin 
        List<String> testStringList = new ArrayList<String>();
        testStringList.add(busAdmins.get(0));
        results = taskService.getTasksByVariousFields(null, testLongListTwo, null, testStringList, null, null, null, null, false);
        assertEquals("List of tasks", 0, results.size());
        results = taskService.getTasksByVariousFields(null, testLongListTwo, null, testStringList, null, null, null, null, true);
        assertEquals("List of tasks", 2, results.size());

        // bus admin and/or pot owner 
        testStringList.clear();
        testStringList.add(busAdmins.get(1));
        List<String> testStringListTwo = new ArrayList<String>();
        testStringListTwo.add(potOwners.get(0));
        results = taskService.getTasksByVariousFields(null, null, null, testStringList, testStringListTwo, null, null, null, false);
        assertEquals("List of tasks", 0, results.size());
        results = taskService.getTasksByVariousFields(null, null, null, testStringList, testStringListTwo, null, null, null, true);
        assertEquals("List of tasks", 2, results.size());
        
        // pot owner and/or task owner
        testStringList.clear();
        testStringList.add(tasks[1].getTaskData().getActualOwner().getId());
        results = taskService.getTasksByVariousFields(null, null, null, null, testStringListTwo, testStringList, null, null, false);
        assertEquals("List of tasks", 0, results.size());
        results = taskService.getTasksByVariousFields(null, null, null, null, testStringListTwo, testStringList, null, null, true);
        assertEquals("List of tasks", 2, results.size());
        
        // task owner and/or status
        List<Status> testStatusList = new ArrayList<Status>();
        testStatusList.add(statuses.get(0));
        results = taskService.getTasksByVariousFields(null, null, null, null, null, testStringList, testStatusList, null, false);
        assertEquals("List of tasks", 0, results.size());
        results = taskService.getTasksByVariousFields(null, null, null, null, null, testStringList, testStatusList, null, true);
        assertEquals("List of tasks", 2, results.size());
        
        // status and/or language
        List<String> testLangList = new ArrayList<String>();
        testLangList.add("en-UK");
        results = taskService.getTasksByVariousFields(null, null, null, null, null, null, testStatusList, testLangList, true);
        assertEquals("List of tasks", 2, results.size());
        testLangList.clear();
        testLangList.add("nl-BE");
        results = taskService.getTasksByVariousFields(null, null, null, null, null, testStringList, testStatusList, null, false);
        assertEquals("List of tasks", 0, results.size());
    }
    
    @Test
    public void testModifyTaskName() {
        // JBPM-4148
        String str = "(with (new Task()) { priority = 55, taskData = (with( new TaskData()) { } ), ";
        str += "peopleAssignments = (with ( new PeopleAssignments() ) { potentialOwners = [new User('Bobba Fet')  ],businessAdministrators = [ new User('Administrator') ], }),";
        str += "names = [ new I18NText( 'en-UK', 'This is my task name')] })";
        Task task = TaskFactory.evalTask(new StringReader(str));
        taskService.addTask(task, new HashMap<String, Object>());
        List<TaskSummary> tasks = taskService.getTasksAssignedAsPotentialOwner("Bobba Fet", "en-UK");
        assertEquals(1, tasks.size());
        assertEquals("This is my task name", tasks.get(0).getName());
        
        Task newTask = taskService.getTaskById(tasks.get(0).getId());
        List<I18NText> updatedNames = new ArrayList<I18NText>();
        I18NText updatedName = TaskModelProvider.getFactory().newI18NText();
        ((InternalI18NText) updatedName).setLanguage(newTask.getNames().get(0).getLanguage());
        ((InternalI18NText) updatedName).setText("New task name");
        updatedNames.add(updatedName);
        
        taskService.setTaskNames(newTask.getId(), updatedNames);
        
        List<TaskSummary> newTasks = taskService.getTasksAssignedAsPotentialOwner("Bobba Fet", "en-UK");
        assertEquals(1, newTasks.size());
        assertEquals("New task name", newTasks.get(0).getName());
    }
    
    @Test
    public void testModifyTaskNameWithinTX() throws Exception{
        // JBPM-4148
        String str = "(with (new Task()) { priority = 55, taskData = (with( new TaskData()) { } ), ";
        str += "peopleAssignments = (with ( new PeopleAssignments() ) { potentialOwners = [new User('Bobba Fet')  ],businessAdministrators = [ new User('Administrator') ], }),";
        str += "names = [ new I18NText( 'en-UK', 'This is my task name')] })";
        Task task = TaskFactory.evalTask(new StringReader(str));
        taskService.addTask(task, new HashMap<String, Object>());
        List<TaskSummary> tasks = taskService.getTasksAssignedAsPotentialOwner("Bobba Fet", "en-UK");
        assertEquals(1, tasks.size());
        assertEquals("This is my task name", tasks.get(0).getName());
        
        UserTransaction ut = InitialContext.doLookup("java:comp/UserTransaction");
        ut.begin();
        Task newTask = taskService.getTaskById(tasks.get(0).getId());
        ((InternalI18NText)newTask.getNames().get(0)).setText("New task name");
        ut.commit();
        
        List<TaskSummary> newTasks = taskService.getTasksAssignedAsPotentialOwner("Bobba Fet", "en-UK");
        assertEquals(1, newTasks.size());
        assertEquals("New task name", newTasks.get(0).getName());
    }
    
    
    @Test
    public void testGetTasksByVariousFieldsWithUserGroupCallback() {
        
        String potOwner = "Bobba Fet";
        List<String> potOwners = new ArrayList<String>();                     
        potOwners.add(potOwner);
        String str = "(with (new Task()) { priority = 55, taskData = (with( new TaskData()) { } ), ";
        str += "peopleAssignments = (with ( new PeopleAssignments() ) { "
                + "businessAdministrators = [new Group('Administrators')],"
                + "potentialOwners = [new Group('Crusaders')]"
                + " }),";
        str += "names = [ new I18NText( 'en-UK', 'This is my task name')] })";
        Task task = TaskFactory.evalTask(new StringReader(str));
        ((InternalTaskData) task.getTaskData()).setWorkItemId(1);
        ((InternalTaskData) task.getTaskData()).setProcessInstanceId(1);
        taskService.addTask(task, new HashMap<String, Object>());
        
        List<TaskSummary> results = taskService.getTasksByVariousFields(null, null, null, null, potOwners, null, null, null, false);
        
        assertNotNull(results);
        assertEquals(1, results.size());
    }
    
    @Test
    public void testGetTasksByVariousFieldsWithUserGroupCallbackAdmin() {
        
        String potOwner = "Administrator";
        List<String> busAdmins = new ArrayList<String>();
        busAdmins.add(potOwner);
        String str = "(with (new Task()) { priority = 55, taskData = (with( new TaskData()) { } ), ";
        str += "peopleAssignments = (with ( new PeopleAssignments() ) { "
                + "businessAdministrators = [new Group('Administrators')],"
                + "potentialOwners = [new Group('Crusaders')]"
                + " }),";
        str += "names = [ new I18NText( 'en-UK', 'This is my task name')] })";
        Task task = TaskFactory.evalTask(new StringReader(str));
        ((InternalTaskData) task.getTaskData()).setWorkItemId(1);
        ((InternalTaskData) task.getTaskData()).setProcessInstanceId(1);
        taskService.addTask(task, new HashMap<String, Object>());
        
        List<TaskSummary> results = taskService.getTasksByVariousFields(null, null, null, busAdmins, null, null, null, null, false);
        
        assertNotNull(results);
        assertEquals(1, results.size());
    }
    
    @Test
    public void testGetTasksByVariousFieldsWithUserGroupCallbackByParams() {
    	Map<String, List<?>> parameters = new HashMap<String, List<?>>();
        String potOwner = "Bobba Fet";
        List<String> potOwners = new ArrayList<String>();                     
        potOwners.add(potOwner);
        String str = "(with (new Task()) { priority = 55, taskData = (with( new TaskData()) { } ), ";
        str += "peopleAssignments = (with ( new PeopleAssignments() ) { "
                + "businessAdministrators = [new Group('Administrators')],"
                + "potentialOwners = [new Group('Crusaders')]"
                + " }),";
        str += "names = [ new I18NText( 'en-UK', 'This is my task name')] })";
        Task task = TaskFactory.evalTask(new StringReader(str));
        ((InternalTaskData) task.getTaskData()).setWorkItemId(1);
        ((InternalTaskData) task.getTaskData()).setProcessInstanceId(1);
        taskService.addTask(task, new HashMap<String, Object>());
        parameters.put(TaskQueryService.POTENTIAL_OWNER_ID_LIST, potOwners);
        List<TaskSummary> results = taskService.getTasksByVariousFields(parameters, false);
        
        assertNotNull(results);
        assertEquals(1, results.size());
    }
    
    @Test
    public void testGetTasksByVariousFieldsWithUserGroupCallbackAdminByParams() {
        Map<String, List<?>> parameters = new HashMap<String, List<?>>();
        String potOwner = "Administrator";
        List<String> busAdmins = new ArrayList<String>();
        busAdmins.add(potOwner);
        String str = "(with (new Task()) { priority = 55, taskData = (with( new TaskData()) { } ), ";
        str += "peopleAssignments = (with ( new PeopleAssignments() ) { "
                + "businessAdministrators = [new Group('Administrators')],"
                + "potentialOwners = [new Group('Crusaders')]"
                + " }),";
        str += "names = [ new I18NText( 'en-UK', 'This is my task name')] })";
        Task task = TaskFactory.evalTask(new StringReader(str));
        ((InternalTaskData) task.getTaskData()).setWorkItemId(1);
        ((InternalTaskData) task.getTaskData()).setProcessInstanceId(1);
        taskService.addTask(task, new HashMap<String, Object>());
        
        parameters.put(TaskQueryService.BUSINESS_ADMIN_ID_LIST, busAdmins);
        List<TaskSummary> results = taskService.getTasksByVariousFields(parameters, false);
        
        assertNotNull(results);
        assertEquals(1, results.size());
    }
}
