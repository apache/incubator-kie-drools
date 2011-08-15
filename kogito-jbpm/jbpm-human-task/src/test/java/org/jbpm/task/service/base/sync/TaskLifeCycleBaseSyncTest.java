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
package org.jbpm.task.service.base.sync;

import java.io.StringReader;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.jbpm.eventmessaging.EventKey;
import org.jbpm.task.BaseTest;
import org.jbpm.task.Status;
import org.jbpm.task.Task;
import org.jbpm.task.event.TaskCompletedEvent;
import org.jbpm.task.event.TaskEventKey;
import org.jbpm.task.query.TaskSummary;
import org.jbpm.task.TaskService;
import org.jbpm.task.service.TaskServer;

public abstract class TaskLifeCycleBaseSyncTest extends BaseTest {

    protected TaskServer server;
    protected TaskService client;

    @SuppressWarnings("unchecked")
    public void testLifeCycle() throws Exception {
        Map<String, Object> vars = new HashMap();
        vars.put("users", users);
        vars.put("groups", groups);
        vars.put("now", new Date());

        // One potential owner, should go straight to state Reserved
        String str = "(with (new Task()) { priority = 55, taskData = (with( new TaskData()) { workItemId = 1 } ), ";
        str += "peopleAssignments = (with ( new PeopleAssignments() ) { potentialOwners = [users['bobba']], }),";
//        str += "descriptions = [ new I18NText( 'en-UK', 'This is my description')], ";
//        str += "subjects = [ new I18NText( 'en-UK', 'This is my subject')], ";
        str += "names = [ new I18NText( 'en-UK', 'This is my task name')] })";


        Task task = (Task) eval(new StringReader(str), vars);
        client.addTask(task, null);

        long taskId = task.getId();

        EventKey key = new TaskEventKey(TaskCompletedEvent.class, taskId);

        client.registerForEvent(key, true);



        List<TaskSummary> tasks = client.getTasksAssignedAsPotentialOwner(users.get("bobba").getId(), "en-UK");
        assertEquals(1, tasks.size());
        assertEquals(Status.Reserved, tasks.get(0).getStatus());


        client.start(taskId, users.get("bobba").getId());



        tasks = client.getTasksAssignedAsPotentialOwner(users.get("bobba").getId(), "en-UK");
        assertEquals(1, tasks.size());
        assertEquals(Status.InProgress, tasks.get(0).getStatus());


        client.complete(taskId, users.get("bobba").getId(), null);



        tasks = client.getTasksAssignedAsPotentialOwner(users.get("bobba").getId(), "en-UK");
        assertEquals(0, tasks.size());

//        Payload payload = handler.getPayload();
//        TaskCompletedEvent event = ( TaskCompletedEvent ) payload.get();
//        assertNotNull( event );



        Task task1 = client.getTask(taskId);
        assertEquals(Status.Completed, task1.getTaskData().getStatus());
    }

    @SuppressWarnings("unchecked")
    public void testLifeCycleMultipleTasks() throws Exception {
        Map<String, Object> vars = new HashMap();
        vars.put("users", users);
        vars.put("groups", groups);
        vars.put("now", new Date());

        // One potential owner, should go straight to state Reserved
        String str = "(with (new Task()) { priority = 55, taskData = (with( new TaskData()) { workItemId = 1 } ), ";
        str += "peopleAssignments = (with ( new PeopleAssignments() ) { potentialOwners = [users['bobba']], }),";
        str += "descriptions = [ new I18NText( 'en-UK', 'This is my description')], ";
        str += "subjects = [ new I18NText( 'en-UK', 'This is my subject')], ";
        str += "names = [ new I18NText( 'en-UK', 'This is my task name')] })";


        Task task = (Task) eval(new StringReader(str), vars);
        client.addTask(task, null);
        long taskId = task.getId();

        EventKey key = new TaskEventKey(TaskCompletedEvent.class, taskId);

        client.registerForEvent(key, true);



        List<TaskSummary> tasks = client.getTasksAssignedAsPotentialOwner(users.get("bobba").getId(), "en-UK");
        assertEquals(1, tasks.size());
        assertEquals(Status.Reserved, tasks.get(0).getStatus());


        client.start(taskId, users.get("bobba").getId());



        tasks = client.getTasksAssignedAsPotentialOwner(users.get("bobba").getId(), "en-UK");
        assertEquals(1, tasks.size());
        assertEquals(Status.InProgress, tasks.get(0).getStatus());


        Task task2 = (Task) eval(new StringReader(str), vars);
        client.addTask(task2, null);
        long taskId2 = task2.getId();

        EventKey key2 = new TaskEventKey(TaskCompletedEvent.class, taskId2);
        client.registerForEvent(key2, true);



        tasks = client.getTasksAssignedAsPotentialOwner(users.get("bobba").getId(), "en-UK");
        assertEquals(2, tasks.size());


        client.complete(taskId, users.get("bobba").getId(), null);


        client.start(taskId2, users.get("bobba").getId());



        tasks = client.getTasksAssignedAsPotentialOwner(users.get("bobba").getId(), "en-UK");
        assertEquals(1, tasks.size());

//        Payload payload = handler.getPayload();
//        TaskCompletedEvent event = ( TaskCompletedEvent ) payload.get();
//        assertNotNull( event );



        task = client.getTask(taskId);
        assertEquals(Status.Completed, task.getTaskData().getStatus());


        client.complete(taskId2, users.get("bobba").getId(), null);

//        payload = handler.getPayload();
//        event = ( TaskCompletedEvent ) payload.get();
//        assertNotNull( event );



        task2 = client.getTask(taskId2);
        assertEquals(Status.Completed, task2.getTaskData().getStatus());
    }
}
