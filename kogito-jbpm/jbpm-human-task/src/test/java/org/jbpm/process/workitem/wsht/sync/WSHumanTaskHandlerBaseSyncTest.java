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
package org.jbpm.process.workitem.wsht.sync;

import org.jbpm.process.workitem.wsht.*;
import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.drools.process.instance.impl.WorkItemImpl;
import org.drools.runtime.process.WorkItemHandler;
import org.drools.runtime.process.WorkItemManager;
import org.jbpm.task.AccessType;
import org.jbpm.task.TaskService;
import org.jbpm.task.BaseTest;
import org.jbpm.task.Status;
import org.jbpm.task.Task;
import org.jbpm.task.query.TaskSummary;
import org.jbpm.task.service.ContentData;
import org.jbpm.task.service.PermissionDeniedException;

public abstract class WSHumanTaskHandlerBaseSyncTest extends BaseTest {

    private static final int DEFAULT_WAIT_TIME = 5000;
    private static final int MANAGER_COMPLETION_WAIT_TIME = DEFAULT_WAIT_TIME;
    private static final int MANAGER_ABORT_WAIT_TIME = DEFAULT_WAIT_TIME;
    private TaskService client;
    private WorkItemHandler handler;

    public void setClient(TaskService client) {
        this.client = client;
    }

    public TaskService getClient() {
        return client;
    }

    public void FIXME_testTask() throws Exception {
        TestWorkItemManager manager = new TestWorkItemManager();
        WorkItemImpl workItem = new WorkItemImpl();
        workItem.setName("Human Task");
        workItem.setParameter("TaskName", "TaskName");
        workItem.setParameter("Comment", "Comment");
        workItem.setParameter("Priority", "10");
        workItem.setParameter("ActorId", "Darth Vader");
        workItem.setProcessInstanceId(10);
        handler.executeWorkItem(workItem, manager);

        
        List<TaskSummary> tasks = client.getTasksAssignedAsPotentialOwner("Darth Vader", "en-UK");
        assertEquals(1, tasks.size());
        TaskSummary task = tasks.get(0);
        assertEquals("TaskName", task.getName());
        assertEquals(10, task.getPriority());
        assertEquals("Comment", task.getDescription());
        assertEquals(Status.Reserved, task.getStatus());
        assertEquals("Darth Vader", task.getActualOwner().getId());
        assertEquals(10, task.getProcessInstanceId());

        System.out.println("Starting task " + task.getId());
        
        client.start(task.getId(), "Darth Vader");
        System.out.println("Started task " + task.getId());
        System.out.println("Completing task " + task.getId());
        client.complete(task.getId(), "Darth Vader", null);
        System.out.println("Completed task " + task.getId());

        assertTrue(manager.waitTillCompleted(MANAGER_COMPLETION_WAIT_TIME));
    }

    public void FIXME_testTaskMultipleActors() throws Exception {
        TestWorkItemManager manager = new TestWorkItemManager();
        WorkItemImpl workItem = new WorkItemImpl();
        workItem.setName("Human Task");
        workItem.setParameter("TaskName", "TaskName");
        workItem.setParameter("Comment", "Comment");
        workItem.setParameter("Priority", "10");
        workItem.setParameter("ActorId", "Darth Vader, Dalai Lama");
        getHandler().executeWorkItem(workItem, manager);

        
        List<TaskSummary> tasks = getClient().getTasksAssignedAsPotentialOwner("Darth Vader", "en-UK");
        assertEquals(1, tasks.size());
        TaskSummary task = tasks.get(0);
        assertEquals("TaskName", task.getName());
        assertEquals(10, task.getPriority());
        assertEquals("Comment", task.getDescription());
        assertEquals(Status.Ready, task.getStatus());

        System.out.println("Claiming task " + task.getId());
        
        getClient().claim(task.getId(), "Darth Vader");
        System.out.println("Claimed task " + task.getId());

        System.out.println("Starting task " + task.getId());
        
        getClient().start(task.getId(), "Darth Vader");
        System.out.println("Started task " + task.getId());

        System.out.println("Completing task " + task.getId());
        
        getClient().complete(task.getId(), "Darth Vader", null);
        
        System.out.println("Completed task " + task.getId());

        assertTrue(manager.waitTillCompleted(MANAGER_COMPLETION_WAIT_TIME));
    }

    public void testTaskGroupActors() throws Exception {
        TestWorkItemManager manager = new TestWorkItemManager();
        WorkItemImpl workItem = new WorkItemImpl();
        workItem.setName("Human Task");
        workItem.setParameter("TaskName", "TaskName");
        workItem.setParameter("Comment", "Comment");
        workItem.setParameter("Priority", "10");
        workItem.setParameter("GroupId", "Crusaders");
        getHandler().executeWorkItem(workItem, manager);

        
        List<String> groupIds = new ArrayList<String>();
        groupIds.add("Crusaders");
        
        List<TaskSummary> tasks = getClient().getTasksAssignedAsPotentialOwner(null, groupIds, "en-UK");
        assertEquals(1, tasks.size());
        TaskSummary taskSummary = tasks.get(0);
        assertEquals("TaskName", taskSummary.getName());
        assertEquals(10, taskSummary.getPriority());
        assertEquals("Comment", taskSummary.getDescription());
        assertEquals(Status.Ready, taskSummary.getStatus());

        System.out.println("Claiming task " + taskSummary.getId());
        
        
        PermissionDeniedException denied = null;
        try {
            getClient().claim(taskSummary.getId(), "Darth Vader");
        } catch (PermissionDeniedException e) {
            denied = e;
        }

        assertNotNull("Should get permissed denied exception", denied);
        System.out.println("Claimed task " + taskSummary.getId());

        //Check if the parent task is InProgress
        
        
        Task task = getClient().getTask(taskSummary.getId());
        assertEquals(Status.Ready, task.getTaskData().getStatus());
    }

    public void FIXME_testTaskSingleAndGroupActors() throws Exception {
        TestWorkItemManager manager = new TestWorkItemManager();
        WorkItemImpl workItem = new WorkItemImpl();
        workItem.setName("Human Task One");
        workItem.setParameter("TaskName", "TaskNameOne");
        workItem.setParameter("Comment", "Comment");
        workItem.setParameter("Priority", "10");
        workItem.setParameter("GroupId", "Crusaders");
        getHandler().executeWorkItem(workItem, manager);
  

        workItem = new WorkItemImpl();
        workItem.setName("Human Task Two");
        workItem.setParameter("TaskName", "TaskNameTwo");
        workItem.setParameter("Comment", "Comment");
        workItem.setParameter("Priority", "10");
        workItem.setParameter("ActorId", "Darth Vader");
        getHandler().executeWorkItem(workItem, manager);

        
        List<String> groupIds = new ArrayList<String>();
        groupIds.add("Crusaders");
        
        List<TaskSummary> tasks = getClient().getTasksAssignedAsPotentialOwner("Darth Vader", groupIds, "en-UK");
        assertEquals(2, tasks.size());
    }

    public void testTaskFail() throws Exception {
        TestWorkItemManager manager = new TestWorkItemManager();
        WorkItemImpl workItem = new WorkItemImpl();
        workItem.setName("Human Task");
        workItem.setParameter("TaskName", "TaskName");
        workItem.setParameter("Comment", "Comment");
        workItem.setParameter("Priority", "10");
        workItem.setParameter("ActorId", "Darth Vader");
        getHandler().executeWorkItem(workItem, manager);

        
        
        List<TaskSummary> tasks = getClient().getTasksAssignedAsPotentialOwner("Darth Vader", "en-UK");
        assertEquals(1, tasks.size());
        TaskSummary task = tasks.get(0);
        assertEquals("TaskName", task.getName());
        assertEquals(10, task.getPriority());
        assertEquals("Comment", task.getDescription());
        assertEquals(Status.Reserved, task.getStatus());
        assertEquals("Darth Vader", task.getActualOwner().getId());

        System.out.println("Starting task " + task.getId());
        
        getClient().start(task.getId(), "Darth Vader");
        
        System.out.println("Started task " + task.getId());

        System.out.println("Failing task " + task.getId());
        
        getClient().fail(task.getId(), "Darth Vader", null);
        
        System.out.println("Failed task " + task.getId());

        assertTrue(manager.waitTillAborted(MANAGER_ABORT_WAIT_TIME));
    }

    public void testTaskSkip() throws Exception {
        TestWorkItemManager manager = new TestWorkItemManager();
        WorkItemImpl workItem = new WorkItemImpl();
        workItem.setName("Human Task");
        workItem.setParameter("TaskName", "TaskName");
        workItem.setParameter("Comment", "Comment");
        workItem.setParameter("Priority", "10");
        workItem.setParameter("ActorId", "Darth Vader");
        getHandler().executeWorkItem(workItem, manager);

        
        
        List<TaskSummary> tasks = getClient().getTasksAssignedAsPotentialOwner("Darth Vader", "en-UK");
        assertEquals(1, tasks.size());
        TaskSummary task = tasks.get(0);
        assertEquals("TaskName", task.getName());
        assertEquals(10, task.getPriority());
        assertEquals("Comment", task.getDescription());
        assertEquals(Status.Reserved, task.getStatus());
        assertEquals("Darth Vader", task.getActualOwner().getId());

        System.out.println("Skipping task " + task.getId());
        
        getClient().skip(task.getId(), "Darth Vader");
        
        System.out.println("Skipped task " + task.getId());

        assertTrue(manager.waitTillAborted(MANAGER_ABORT_WAIT_TIME));
    }

    public void testTaskAbortSkippable() throws Exception {
        TestWorkItemManager manager = new TestWorkItemManager();
        WorkItemImpl workItem = new WorkItemImpl();
        workItem.setName("Human Task");
        workItem.setParameter("TaskName", "TaskName");
        workItem.setParameter("Comment", "Comment");
        workItem.setParameter("Priority", "10");
        workItem.setParameter("ActorId", "Darth Vader");
        getHandler().executeWorkItem(workItem, manager);

        

        getHandler().abortWorkItem(workItem, manager);

        List<TaskSummary> tasks = getClient().getTasksAssignedAsPotentialOwner("Darth Vader", "en-UK");
        assertEquals(0, tasks.size());
    }

    public void testTaskAbortNotSkippable() throws Exception {
        TestWorkItemManager manager = new TestWorkItemManager();
        WorkItemImpl workItem = new WorkItemImpl();
        workItem.setName("Human Task");
        workItem.setParameter("TaskName", "TaskName");
        workItem.setParameter("Comment", "Comment");
        workItem.setParameter("Priority", "10");
        workItem.setParameter("ActorId", "Darth Vader");
        workItem.setParameter("Skippable", "false");
        getHandler().executeWorkItem(workItem, manager);

        
        List<TaskSummary> tasks = getClient().getTasksAssignedAsPotentialOwner("Darth Vader", "en-UK");
        assertEquals(1, tasks.size());

        getHandler().abortWorkItem(workItem, manager);

        
        tasks = getClient().getTasksAssignedAsPotentialOwner("Darth Vader", "en-UK");
        assertEquals(1, tasks.size());
    }

    public void testTaskData() throws Exception {
        TestWorkItemManager manager = new TestWorkItemManager();
        WorkItemImpl workItem = new WorkItemImpl();
        workItem.setName("Human Task");
        workItem.setParameter("TaskName", "TaskName");
        workItem.setParameter("Comment", "Comment");
        workItem.setParameter("Priority", "10");
        workItem.setParameter("ActorId", "Darth Vader");
        workItem.setParameter("Content", "This is the content");
        getHandler().executeWorkItem(workItem, manager);

        

        
        
        List<TaskSummary> tasks = getClient().getTasksAssignedAsPotentialOwner("Darth Vader", "en-UK");
        assertEquals(1, tasks.size());
        TaskSummary taskSummary = tasks.get(0);
        assertEquals("TaskName", taskSummary.getName());
        assertEquals(10, taskSummary.getPriority());
        assertEquals("Comment", taskSummary.getDescription());
        assertEquals(Status.Reserved, taskSummary.getStatus());
        assertEquals("Darth Vader", taskSummary.getActualOwner().getId());

        
        
        Task task = getClient().getTask(taskSummary.getId());
        assertEquals(AccessType.Inline, task.getTaskData().getDocumentAccessType());
        long contentId = task.getTaskData().getDocumentContentId();
        assertTrue(contentId != -1);
        
        
        ByteArrayInputStream bis = new ByteArrayInputStream(getClient().getContent(contentId).getContent());
        ObjectInputStream in = new ObjectInputStream(bis);
        Object data = in.readObject();
        in.close();
        assertEquals("This is the content", data);

        System.out.println("Starting task " + task.getId());
        
        getClient().start(task.getId(), "Darth Vader");
       
        System.out.println("Started task " + task.getId());

        System.out.println("Completing task " + task.getId());
        
        ContentData result = new ContentData();
        result.setAccessType(AccessType.Inline);
        result.setType("java.lang.String");
        ByteArrayOutputStream bos = new ByteArrayOutputStream();
        ObjectOutputStream out = new ObjectOutputStream(bos);
        out.writeObject("This is the result");
        out.close();
        result.setContent(bos.toByteArray());
        getClient().complete(task.getId(), "Darth Vader", result);
        
        System.out.println("Completed task " + task.getId());

        assertTrue(manager.waitTillCompleted(MANAGER_COMPLETION_WAIT_TIME));
        Map<String, Object> results = manager.getResults();
        assertNotNull(results);
        assertEquals("Darth Vader", results.get("ActorId"));
        assertEquals("This is the result", results.get("Result"));
    }

    public void testTaskDataAutomaticMapping() throws Exception {
        TestWorkItemManager manager = new TestWorkItemManager();
        WorkItemImpl workItem = new WorkItemImpl();
        workItem.setName("Human Task");
        workItem.setParameter("TaskName", "TaskName");
        workItem.setParameter("Comment", "Comment");
        workItem.setParameter("Priority", "10");
        workItem.setParameter("ActorId", "Darth Vader");
        MyObject myObject = new MyObject("MyObjectValue");
        workItem.setParameter("MyObject", myObject);
        Map<String, Object> mapParameter = new HashMap<String, Object>();
        mapParameter.put("MyObjectInsideTheMap", myObject);
        workItem.setParameter("MyMap", mapParameter);
        workItem.setParameter("MyObject", myObject);

        getHandler().executeWorkItem(workItem, manager);

       

       
        
        List<TaskSummary> tasks = getClient().getTasksAssignedAsPotentialOwner("Darth Vader", "en-UK");
        assertEquals(1, tasks.size());
        TaskSummary taskSummary = tasks.get(0);
        assertEquals("TaskName", taskSummary.getName());
        assertEquals(10, taskSummary.getPriority());
        assertEquals("Comment", taskSummary.getDescription());
        assertEquals(Status.Reserved, taskSummary.getStatus());
        assertEquals("Darth Vader", taskSummary.getActualOwner().getId());



        
        
        Task task = getClient().getTask(taskSummary.getId());
        assertEquals(AccessType.Inline, task.getTaskData().getDocumentAccessType());
        long contentId = task.getTaskData().getDocumentContentId();
        assertTrue(contentId != -1);
        
        
        ByteArrayInputStream bis = new ByteArrayInputStream(getClient().getContent(contentId).getContent());
        ObjectInputStream in = new ObjectInputStream(bis);
        Map<String, Object> data = (Map<String, Object>) in.readObject();
        in.close();
        //Checking that the input parameters are being copied automatically if the Content Element doesn't exist
        assertEquals("MyObjectValue", ((MyObject) data.get("MyObject")).getValue());
        assertEquals("10", data.get("Priority"));
        assertEquals("MyObjectValue", ((MyObject) ((Map<String, Object>) data.get("MyMap")).get("MyObjectInsideTheMap")).getValue());

        System.out.println("Starting task " + task.getId());
        
        getClient().start(task.getId(), "Darth Vader");
        System.out.println("Started task " + task.getId());

        System.out.println("Completing task " + task.getId());
        
        ContentData result = new ContentData();
        result.setAccessType(AccessType.Inline);
        result.setType("java.lang.String");
        ByteArrayOutputStream bos = new ByteArrayOutputStream();
        ObjectOutputStream out = new ObjectOutputStream(bos);
        out.writeObject("This is the result");
        out.close();
        result.setContent(bos.toByteArray());
        getClient().complete(task.getId(), "Darth Vader", result);
        System.out.println("Completed task " + task.getId());

        assertTrue(manager.waitTillCompleted(MANAGER_COMPLETION_WAIT_TIME));
        Map<String, Object> results = manager.getResults();
        assertNotNull(results);
        assertEquals("Darth Vader", results.get("ActorId"));
        assertEquals("This is the result", results.get("Result"));
    }

    public void TODOtestOnAllSubTasksEndParentEndStrategy() throws Exception {

        TestWorkItemManager manager = new TestWorkItemManager();
        //Create the parent task
        WorkItemImpl workItem = new WorkItemImpl();
        workItem.setName("Human Task");
        workItem.setParameter("TaskName", "TaskNameParent");
        workItem.setParameter("Comment", "CommentParent");
        workItem.setParameter("Priority", "10");
        workItem.setParameter("ActorId", "Darth Vader");
        //Set the subtask policy
        workItem.setParameter("SubTaskStrategies", "OnAllSubTasksEndParentEnd");
        getHandler().executeWorkItem(workItem, manager);


        //Test if the task is succesfully created
        
        
        List<TaskSummary> tasks = getClient().getTasksAssignedAsPotentialOwner("Darth Vader", "en-UK");
        assertEquals(1, tasks.size());
        TaskSummary task = tasks.get(0);
        assertEquals("TaskNameParent", task.getName());
        assertEquals(10, task.getPriority());
        assertEquals("CommentParent", task.getDescription());
        assertEquals(Status.Reserved, task.getStatus());
        assertEquals("Darth Vader", task.getActualOwner().getId());

        //Create the child task
        workItem = new WorkItemImpl();
        workItem.setName("Human Task");
        workItem.setParameter("TaskName", "TaskNameChild1");
        workItem.setParameter("Comment", "CommentChild1");
        workItem.setParameter("Priority", "10");
        workItem.setParameter("ActorId", "Darth Vader");
        workItem.setParameter("ParentId", task.getId());
        getHandler().executeWorkItem(workItem, manager);

  
        //Create the child task2
        workItem = new WorkItemImpl();
        workItem.setName("Human Task2");
        workItem.setParameter("TaskName", "TaskNameChild2");
        workItem.setParameter("Comment", "CommentChild2");
        workItem.setParameter("Priority", "10");
        workItem.setParameter("ActorId", "Darth Vader");
        workItem.setParameter("ParentId", task.getId());
        getHandler().executeWorkItem(workItem, manager);

   
        //Start the parent task
        System.out.println("Starting task " + task.getId());
        getClient().start(task.getId(), "Darth Vader");
        
        System.out.println("Started task " + task.getId());

        //Check if the parent task is InProgress
        
        
        Task parentTask = getClient().getTask(task.getId());
        assertEquals(Status.InProgress, parentTask.getTaskData().getStatus());
        assertEquals(users.get("darth"), parentTask.getTaskData().getActualOwner());

        //Get all the subtask created for the parent task based on the potential owner
        
        
        List<TaskSummary> subTasks = getClient().getSubTasksAssignedAsPotentialOwner(parentTask.getId(), "Darth Vader", "en-UK");
        assertEquals(2, subTasks.size());
        TaskSummary subTaskSummary1 = subTasks.get(0);
        TaskSummary subTaskSummary2 = subTasks.get(1);
        assertNotNull(subTaskSummary1);
        assertNotNull(subTaskSummary2);

        //Starting the sub task 1
        System.out.println("Starting sub task " + subTaskSummary1.getId());
        
        getClient().start(subTaskSummary1.getId(), "Darth Vader");
        
        System.out.println("Started sub task " + subTaskSummary1.getId());

        //Starting the sub task 2
        System.out.println("Starting sub task " + subTaskSummary2.getId());
        
        getClient().start(subTaskSummary2.getId(), "Darth Vader");
        
        System.out.println("Started sub task " + subTaskSummary2.getId());

        //Check if the child task 1 is InProgress
       
        
        Task subTask1 = getClient().getTask(subTaskSummary1.getId());
        assertEquals(Status.InProgress, subTask1.getTaskData().getStatus());
        assertEquals(users.get("darth"), subTask1.getTaskData().getActualOwner());

        //Check if the child task 2 is InProgress
        
        
        Task subTask2 = getClient().getTask(subTaskSummary2.getId());
        assertEquals(Status.InProgress, subTask2.getTaskData().getStatus());
        assertEquals(users.get("darth"), subTask2.getTaskData().getActualOwner());

        // Complete the child task 1
        System.out.println("Completing sub task " + subTask1.getId());
        
        getClient().complete(subTask1.getId(), "Darth Vader", null);
        
        System.out.println("Completed sub task " + subTask1.getId());

        // Complete the child task 2
        System.out.println("Completing sub task " + subTask2.getId());
        
        getClient().complete(subTask2.getId(), "Darth Vader", null);
       
        System.out.println("Completed sub task " + subTask2.getId());

        //Check if the child task 1 is Completed

       
        
        subTask1 = getClient().getTask(subTask1.getId());
        assertEquals(Status.Completed, subTask1.getTaskData().getStatus());
        assertEquals(users.get("darth"), subTask1.getTaskData().getActualOwner());

        //Check if the child task 2 is Completed
        subTask2 = getClient().getTask(subTask2.getId());
        assertEquals(Status.Completed, subTask2.getTaskData().getStatus());
        assertEquals(users.get("darth"), subTask2.getTaskData().getActualOwner());

        // Check is the parent task is Complete
        parentTask = getClient().getTask(parentTask.getId());
        assertEquals(Status.Completed, parentTask.getTaskData().getStatus());
        assertEquals(users.get("darth"), parentTask.getTaskData().getActualOwner());

        assertTrue(manager.waitTillCompleted(MANAGER_COMPLETION_WAIT_TIME));
    }

    public void TODOtestOnParentAbortAllSubTasksEndStrategy() throws Exception {

        TestWorkItemManager manager = new TestWorkItemManager();
        //Create the parent task
        WorkItemImpl workItem = new WorkItemImpl();
        workItem.setName("Human Task");
        workItem.setParameter("TaskName", "TaskNameParent");
        workItem.setParameter("Comment", "CommentParent");
        workItem.setParameter("Priority", "10");
        workItem.setParameter("ActorId", "Darth Vader");
        //Set the subtask policy
        workItem.setParameter("SubTaskStrategies", "OnParentAbortAllSubTasksEnd");
        getHandler().executeWorkItem(workItem, manager);


        //Test if the task is succesfully created
        
        
        List<TaskSummary> tasks = getClient().getTasksAssignedAsPotentialOwner("Darth Vader", "en-UK");
        assertEquals(1, tasks.size());
        TaskSummary task = tasks.get(0);
        assertEquals("TaskNameParent", task.getName());
        assertEquals(10, task.getPriority());
        assertEquals("CommentParent", task.getDescription());
        assertEquals(Status.Reserved, task.getStatus());
        assertEquals("Darth Vader", task.getActualOwner().getId());

        //Create the child task
        workItem = new WorkItemImpl();
        workItem.setName("Human Task");
        workItem.setParameter("TaskName", "TaskNameChild1");
        workItem.setParameter("Comment", "CommentChild1");
        workItem.setParameter("Priority", "10");
        workItem.setParameter("ActorId", "Darth Vader");
        workItem.setParameter("ParentId", task.getId());
        getHandler().executeWorkItem(workItem, manager);

        //Create the child task2
        workItem = new WorkItemImpl();
        workItem.setName("Human Task2");
        workItem.setParameter("TaskName", "TaskNameChild2");
        workItem.setParameter("Comment", "CommentChild2");
        workItem.setParameter("Priority", "10");
        workItem.setParameter("ActorId", "Darth Vader");
        workItem.setParameter("ParentId", task.getId());
        getHandler().executeWorkItem(workItem, manager);

        //Start the parent task
        System.out.println("Starting task " + task.getId());
        
        getClient().start(task.getId(), "Darth Vader");
        System.out.println("Started task " + task.getId());

        //Check if the parent task is InProgress
        
        
        Task parentTask = getClient().getTask(task.getId());
        assertEquals(Status.InProgress, parentTask.getTaskData().getStatus());
        assertEquals(users.get("darth"), parentTask.getTaskData().getActualOwner());

        //Get all the subtask created for the parent task based on the potential owner
        
        
        List<TaskSummary> subTasks = getClient().getSubTasksAssignedAsPotentialOwner(parentTask.getId(), "Darth Vader", "en-UK");
        assertEquals(2, subTasks.size());
        TaskSummary subTaskSummary1 = subTasks.get(0);
        TaskSummary subTaskSummary2 = subTasks.get(1);
        assertNotNull(subTaskSummary1);
        assertNotNull(subTaskSummary2);

        //Starting the sub task 1
        System.out.println("Starting sub task " + subTaskSummary1.getId());
        
        getClient().start(subTaskSummary1.getId(), "Darth Vader");
        
        System.out.println("Started sub task " + subTaskSummary1.getId());

        //Starting the sub task 2
        System.out.println("Starting sub task " + subTaskSummary2.getId());
        
        getClient().start(subTaskSummary2.getId(), "Darth Vader");
        
        System.out.println("Started sub task " + subTaskSummary2.getId());

        //Check if the child task 1 is InProgress
        
        
        Task subTask1 = getClient().getTask(subTaskSummary1.getId());
        assertEquals(Status.InProgress, subTask1.getTaskData().getStatus());
        assertEquals(users.get("darth"), subTask1.getTaskData().getActualOwner());

        //Check if the child task 2 is InProgress
        
        
        Task subTask2 = getClient().getTask(subTaskSummary2.getId());
        assertEquals(Status.InProgress, subTask2.getTaskData().getStatus());
        assertEquals(users.get("darth"), subTask2.getTaskData().getActualOwner());

        // Complete the parent task
        System.out.println("Completing parent task " + parentTask.getId());
        
        getClient().skip(parentTask.getId(), "Darth Vader");
        
        System.out.println("Completed parent task " + parentTask.getId());

        //Check if the child task 1 is Completed
        
        
        subTask1 = getClient().getTask(subTaskSummary1.getId());
        assertEquals(Status.Completed, subTask1.getTaskData().getStatus());
        assertEquals(users.get("darth"), subTask1.getTaskData().getActualOwner());

        //Check if the child task 2 is Completed
        
        
        subTask2 = getClient().getTask(subTaskSummary2.getId());
        assertEquals(Status.Completed, subTask2.getTaskData().getStatus());
        assertEquals(users.get("darth"), subTask2.getTaskData().getActualOwner());

        assertTrue(manager.waitTillCompleted(MANAGER_COMPLETION_WAIT_TIME));
    }

    public void setHandler(WorkItemHandler handler) {
        this.handler = handler;
    }

    public WorkItemHandler getHandler() {
        return handler;
    }

    private class TestWorkItemManager implements WorkItemManager {

        private volatile boolean completed;
        private volatile boolean aborted;
        private volatile Map<String, Object> results;

        public synchronized boolean waitTillCompleted(long time) {
            if (!isCompleted()) {
                try {
                    wait(time);
                } catch (InterruptedException e) {
                    // swallow and return state of completed
                }
            }

            return isCompleted();
        }

        public synchronized boolean waitTillAborted(long time) {
            if (!isAborted()) {
                try {
                    wait(time);
                } catch (InterruptedException e) {
                    // swallow and return state of aborted
                }
            }

            return isAborted();
        }

        public void abortWorkItem(long id) {
            setAborted(true);
        }

        public synchronized boolean isAborted() {
            return aborted;
        }

        private synchronized void setAborted(boolean aborted) {
            this.aborted = aborted;
            notifyAll();
        }

        public void completeWorkItem(long id, Map<String, Object> results) {
            this.results = results;
            setCompleted(true);
        }

        private synchronized void setCompleted(boolean completed) {
            this.completed = completed;
            notifyAll();
        }

        public synchronized boolean isCompleted() {
            return completed;
        }

        public Map<String, Object> getResults() {
            return results;
        }

        public void registerWorkItemHandler(String workItemName, WorkItemHandler handler) {
        }
    }
}
