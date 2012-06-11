/**
 * Copyright 2010 JBoss Inc
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *      http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package org.jbpm.task.service.base.async;

import java.io.StringReader;
import java.util.List;
import java.util.Map;

import org.jbpm.eventmessaging.EventKey;
import org.jbpm.eventmessaging.Payload;
import org.jbpm.task.AsyncTaskService;
import org.jbpm.task.BaseTest;
import org.jbpm.task.Group;
import org.jbpm.task.Status;
import org.jbpm.task.Task;
import org.jbpm.task.User;
import org.jbpm.task.event.TaskCompletedEvent;
import org.jbpm.task.event.TaskEventKey;
import org.jbpm.task.query.TaskSummary;
import org.jbpm.task.service.TaskServer;
import org.jbpm.task.service.responsehandlers.BlockingAddTaskResponseHandler;
import org.jbpm.task.service.responsehandlers.BlockingEventResponseHandler;
import org.jbpm.task.service.responsehandlers.BlockingGetTaskResponseHandler;
import org.jbpm.task.service.responsehandlers.BlockingTaskOperationResponseHandler;
import org.jbpm.task.service.responsehandlers.BlockingTaskSummaryResponseHandler;

public abstract class TaskLifeCycleBaseAsyncTest extends BaseTest {

    protected TaskServer server;
    protected AsyncTaskService client;

    public void testLifeCycle() throws Exception {    
        runTestLifeCycle(client, users, groups);
    }
    
    public static void runTestLifeCycle(AsyncTaskService client, Map<String, User> users,Map<String, Group> groups ) { 
        Map <String, Object> vars = fillVariables(users, groups);

        // One potential owner, should go straight to state Reserved
        String str = "(with (new Task()) { priority = 55, taskData = (with( new TaskData()) { workItemId = 1 } ), ";
        str += "peopleAssignments = (with ( new PeopleAssignments() ) { potentialOwners = [users['bobba']], }),";
//        str += "descriptions = [ new I18NText( 'en-UK', 'This is my description')], ";
//        str += "subjects = [ new I18NText( 'en-UK', 'This is my subject')], ";
        str += "names = [ new I18NText( 'en-UK', 'This is my task name')] })";

        BlockingAddTaskResponseHandler addTaskResponseHandler = new BlockingAddTaskResponseHandler();
        Task task = (Task) eval(new StringReader(str), vars);
        client.addTask(task, null, addTaskResponseHandler);

        long taskId = addTaskResponseHandler.getTaskId();

        EventKey key = new TaskEventKey(TaskCompletedEvent.class, taskId);
        BlockingEventResponseHandler handler = new BlockingEventResponseHandler();
        client.registerForEvent(key, false, handler);

        BlockingTaskSummaryResponseHandler taskSummaryResponseHandler = new BlockingTaskSummaryResponseHandler();
        client.getTasksAssignedAsPotentialOwner(users.get("bobba").getId(), "en-UK", taskSummaryResponseHandler);
        List<TaskSummary> tasks = taskSummaryResponseHandler.getResults();
        assertEquals(1, tasks.size());
        assertEquals(Status.Reserved, tasks.get(0).getStatus());

        BlockingTaskOperationResponseHandler responseHandler = new BlockingTaskOperationResponseHandler();
        client.start(taskId, users.get("bobba").getId(), responseHandler);

        taskSummaryResponseHandler = new BlockingTaskSummaryResponseHandler();
        client.getTasksAssignedAsPotentialOwner(users.get("bobba").getId(), "en-UK", taskSummaryResponseHandler);
        tasks = taskSummaryResponseHandler.getResults();
        assertEquals(1, tasks.size());
        assertEquals(Status.InProgress, tasks.get(0).getStatus());

        responseHandler = new BlockingTaskOperationResponseHandler();
        client.complete(taskId, users.get("bobba").getId(), null, responseHandler);

        taskSummaryResponseHandler = new BlockingTaskSummaryResponseHandler();
        client.getTasksAssignedAsPotentialOwner(users.get("bobba").getId(), "en-UK", taskSummaryResponseHandler);
        tasks = taskSummaryResponseHandler.getResults();
        assertEquals(0, tasks.size());

        Payload payload = handler.getPayload();
        TaskCompletedEvent event = (TaskCompletedEvent) payload.get();
        assertNotNull(event);

        BlockingGetTaskResponseHandler getTaskResponseHandler = new BlockingGetTaskResponseHandler();
        client.getTask(taskId, getTaskResponseHandler);
        Task task1 = getTaskResponseHandler.getTask();
        assertEquals(Status.Completed, task1.getTaskData().getStatus());
    }

    public void testLifeCycleMultipleTasks() throws Exception { 
	    runTestLifeCycle(client, users, groups);
    }

    public static void runTestLifeCycleMultipleTasks(AsyncTaskService client, Map<String, User> users,Map<String, Group> groups ) { 
        Map<String, Object> vars = fillVariables(users, groups);

        // One potential owner, should go straight to state Reserved
        String str = "(with (new Task()) { priority = 55, taskData = (with( new TaskData()) { workItemId = 1 } ), ";
        str += "peopleAssignments = (with ( new PeopleAssignments() ) { potentialOwners = [users['bobba']], }),";
        str += "descriptions = [ new I18NText( 'en-UK', 'This is my description')], ";
        str += "subjects = [ new I18NText( 'en-UK', 'This is my subject')], ";
        str += "names = [ new I18NText( 'en-UK', 'This is my task name')] })";

        // First task
        // In own scope to make sure that no objects in scope can be used in second task
        long taskId = 0;
        BlockingEventResponseHandler handler = new BlockingEventResponseHandler();
        {
            BlockingAddTaskResponseHandler addTaskResponseHandler = new BlockingAddTaskResponseHandler();
            Task task = (Task) eval(new StringReader(str), vars);
            client.addTask(task, null, addTaskResponseHandler);
            taskId = addTaskResponseHandler.getTaskId();
            assertTrue( taskId != 0 );

            EventKey key = new TaskEventKey(TaskCompletedEvent.class, taskId);
            client.registerForEvent(key, false, handler);

            BlockingTaskSummaryResponseHandler taskSummaryResponseHandler = new BlockingTaskSummaryResponseHandler();
            client.getTasksAssignedAsPotentialOwner(users.get("bobba").getId(), "en-UK", taskSummaryResponseHandler);
            List<TaskSummary> tasks = taskSummaryResponseHandler.getResults();
            assertEquals(1, tasks.size());
            assertEquals(Status.Reserved, tasks.get(0).getStatus());

            BlockingTaskOperationResponseHandler responseHandler = new BlockingTaskOperationResponseHandler();
            client.start(taskId, users.get("bobba").getId(), responseHandler);

            taskSummaryResponseHandler = new BlockingTaskSummaryResponseHandler();
            client.getTasksAssignedAsPotentialOwner(users.get("bobba").getId(), "en-UK", taskSummaryResponseHandler);
            tasks = taskSummaryResponseHandler.getResults();
            assertEquals(1, tasks.size());
            assertEquals(Status.InProgress, tasks.get(0).getStatus());
        }

        // Second task
        // In own scope to make sure that no objects in scope can be used elsewhere
        long taskId2 = 0;
        BlockingEventResponseHandler handler2 = new BlockingEventResponseHandler();
        {
            BlockingAddTaskResponseHandler addTaskResponseHandler2 = new BlockingAddTaskResponseHandler();
            Task task2 = (Task) eval(new StringReader(str), vars);
            client.addTask(task2, null, addTaskResponseHandler2);
            taskId2 = addTaskResponseHandler2.getTaskId();
            assertTrue( taskId2 != 0 );
            assertTrue( "Tasks should have different ids.", taskId != taskId2 );

            EventKey key2 = new TaskEventKey(TaskCompletedEvent.class, taskId2);
            client.registerForEvent(key2, false, handler2);

            BlockingTaskSummaryResponseHandler taskSummaryResponseHandler = new BlockingTaskSummaryResponseHandler();
            client.getTasksAssignedAsPotentialOwner(users.get("bobba").getId(), "en-UK", taskSummaryResponseHandler);
            List<TaskSummary> tasks = taskSummaryResponseHandler.getResults();
            assertEquals(2, tasks.size());

            BlockingTaskOperationResponseHandler responseHandler = new BlockingTaskOperationResponseHandler();
            client.complete(taskId, users.get("bobba").getId(), null, responseHandler);

            responseHandler = new BlockingTaskOperationResponseHandler();
            client.start(taskId2, users.get("bobba").getId(), responseHandler);

            taskSummaryResponseHandler = new BlockingTaskSummaryResponseHandler();
            client.getTasksAssignedAsPotentialOwner(users.get("bobba").getId(), "en-UK", taskSummaryResponseHandler);
            tasks = taskSummaryResponseHandler.getResults();
            assertEquals(1, tasks.size());
        }

        Payload payload = handler.getPayload();
        TaskCompletedEvent event = (TaskCompletedEvent) payload.get();
        assertNotNull(event);

        BlockingGetTaskResponseHandler getTaskResponseHandler = new BlockingGetTaskResponseHandler();
        client.getTask(taskId, getTaskResponseHandler);
        Task task = getTaskResponseHandler.getTask();
        assertEquals(Status.Completed, task.getTaskData().getStatus());

        BlockingTaskOperationResponseHandler responseHandler = new BlockingTaskOperationResponseHandler();
        client.complete(taskId2, users.get("bobba").getId(), null, responseHandler);

        payload = handler2.getPayload();
        event = (TaskCompletedEvent) payload.get();
        assertNotNull(event);

        BlockingGetTaskResponseHandler getTaskResponseHandler2 = new BlockingGetTaskResponseHandler();
        client.getTask(taskId2, getTaskResponseHandler2);
        Task task2 = getTaskResponseHandler2.getTask();
        assertEquals(Status.Completed, task2.getTaskData().getStatus());
    }
}
