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
package org.jbpm.process.workitem.wsht.async;


import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Properties;

import org.drools.process.instance.impl.WorkItemImpl;
import org.jbpm.process.workitem.wsht.AsyncGenericHTWorkItemHandler;
import org.jbpm.process.workitem.wsht.MyObject;
import org.jbpm.task.AccessType;
import org.jbpm.task.AsyncTaskService;
import org.jbpm.task.BaseTest;
import org.jbpm.task.Status;
import org.jbpm.task.Task;
import org.jbpm.task.TestStatefulKnowledgeSession;
import org.jbpm.task.identity.DefaultUserGroupCallbackImpl;
import org.jbpm.task.identity.UserGroupCallbackManager;
import org.jbpm.task.query.TaskSummary;
import org.jbpm.task.service.ContentData;
import org.jbpm.task.service.PermissionDeniedException;
import org.jbpm.task.service.responsehandlers.BlockingGetContentResponseHandler;
import org.jbpm.task.service.responsehandlers.BlockingGetTaskResponseHandler;
import org.jbpm.task.service.responsehandlers.BlockingTaskOperationResponseHandler;
import org.jbpm.task.service.responsehandlers.BlockingTaskSummaryResponseHandler;
import org.jbpm.task.utils.ContentMarshallerHelper;
import org.jbpm.task.utils.OnErrorAction;
import org.kie.runtime.process.WorkItemHandler;
import org.kie.runtime.process.WorkItemManager;

public abstract class WSHumanTaskHandlerBaseAsyncTest extends BaseTest {

    private static final int DEFAULT_WAIT_TIME = 5000;
    private static final int MANAGER_COMPLETION_WAIT_TIME = DEFAULT_WAIT_TIME;
    private static final int MANAGER_ABORT_WAIT_TIME = DEFAULT_WAIT_TIME;
    private AsyncTaskService client;
    private WorkItemHandler handler;
    protected TestStatefulKnowledgeSession ksession = new TestStatefulKnowledgeSession();

    protected TestStatefulKnowledgeSession getSession() {
        return ksession;
    }

    protected void setSession(TestStatefulKnowledgeSession ksession) {
        this.ksession = ksession;
    }

    public void setClient(AsyncTaskService client) {
        this.client = client;
    }

    public AsyncTaskService getClient() {
        return client;
    }

    
    public void FIXME_testTask() throws Exception {
        printTestName();
        
        TestWorkItemManager manager = new TestWorkItemManager();
        ksession.setWorkItemManager(manager);
        WorkItemImpl workItem = new WorkItemImpl();
        workItem.setName("Human Task");
        workItem.setParameter("TaskName", "TaskName");
        workItem.setParameter("Comment", "Comment");
        workItem.setParameter("Priority", "10");
        workItem.setParameter("ActorId", "Darth Vader");
        workItem.setProcessInstanceId(10);
        handler.executeWorkItem(workItem, manager);

        Thread.sleep(500);

        BlockingTaskSummaryResponseHandler responseHandler = new BlockingTaskSummaryResponseHandler();
        client.getTasksAssignedAsPotentialOwner("Darth Vader", "en-UK", responseHandler);
        List<TaskSummary> tasks = responseHandler.getResults();
        assertEquals(1, tasks.size());
        TaskSummary task = tasks.get(0);
        assertEquals("TaskName", task.getName());
        assertEquals(10, task.getPriority());
        assertEquals("Comment", task.getDescription());
        assertEquals(Status.Reserved, task.getStatus());
        assertEquals("Darth Vader", task.getActualOwner().getId());
        assertEquals(10, task.getProcessInstanceId());

        System.out.println("Starting task " + task.getId());
        BlockingTaskOperationResponseHandler operationResponseHandler = new BlockingTaskOperationResponseHandler();
        client.start(task.getId(), "Darth Vader", operationResponseHandler);
        operationResponseHandler.waitTillDone(DEFAULT_WAIT_TIME);
        System.out.println("Started task " + task.getId());

        System.out.println("Completing task " + task.getId());
        operationResponseHandler = new BlockingTaskOperationResponseHandler();
        client.complete(task.getId(), "Darth Vader", null, operationResponseHandler);
        operationResponseHandler.waitTillDone(15000);
        System.out.println("Completed task " + task.getId());

        assertTrue(manager.waitTillCompleted(MANAGER_COMPLETION_WAIT_TIME));
    }

    public void FIXME_testTaskMultipleActors() throws Exception {
        printTestName();
        
        TestWorkItemManager manager = new TestWorkItemManager();
        ksession.setWorkItemManager(manager);
        WorkItemImpl workItem = new WorkItemImpl();
        workItem.setName("Human Task");
        workItem.setParameter("TaskName", "TaskName");
        workItem.setParameter("Comment", "Comment");
        workItem.setParameter("Priority", "10");
        workItem.setParameter("ActorId", "Darth Vader, Dalai Lama");
        getHandler().executeWorkItem(workItem, manager);

        Thread.sleep(500);

        BlockingTaskSummaryResponseHandler responseHandler = new BlockingTaskSummaryResponseHandler();
        getClient().getTasksAssignedAsPotentialOwner("Darth Vader", "en-UK", responseHandler);
        List<TaskSummary> tasks = responseHandler.getResults();
        assertEquals(1, tasks.size());
        TaskSummary task = tasks.get(0);
        assertEquals("TaskName", task.getName());
        assertEquals(10, task.getPriority());
        assertEquals("Comment", task.getDescription());
        assertEquals(Status.Ready, task.getStatus());

        System.out.println("Claiming task " + task.getId());
        BlockingTaskOperationResponseHandler operationResponseHandler = new BlockingTaskOperationResponseHandler();
        getClient().claim(task.getId(), "Darth Vader", operationResponseHandler);
        operationResponseHandler.waitTillDone(DEFAULT_WAIT_TIME);
        System.out.println("Claimed task " + task.getId());

        System.out.println("Starting task " + task.getId());
        operationResponseHandler = new BlockingTaskOperationResponseHandler();
        getClient().start(task.getId(), "Darth Vader", operationResponseHandler);
        operationResponseHandler.waitTillDone(DEFAULT_WAIT_TIME);
        System.out.println("Started task " + task.getId());

        System.out.println("Completing task " + task.getId());
        operationResponseHandler = new BlockingTaskOperationResponseHandler();
        getClient().complete(task.getId(), "Darth Vader", null, operationResponseHandler);
        operationResponseHandler.waitTillDone(DEFAULT_WAIT_TIME);
        System.out.println("Completed task " + task.getId());

        assertTrue(manager.waitTillCompleted(MANAGER_COMPLETION_WAIT_TIME));
    }

    public void testTaskGroupActors() throws Exception {
        printTestName();
        
    	Properties userGroups = new Properties();
        userGroups.setProperty("Luke", "Crusaders");
        userGroups.setProperty("Darth Vader", "");
        
        UserGroupCallbackManager.getInstance().setCallback(new DefaultUserGroupCallbackImpl(userGroups));
        
        TestWorkItemManager manager = new TestWorkItemManager();
        ksession.setWorkItemManager(manager);
        WorkItemImpl workItem = new WorkItemImpl();
        workItem.setName("Human Task");
        workItem.setParameter("TaskName", "TaskName");
        workItem.setParameter("Comment", "Comment");
        workItem.setParameter("Priority", "10");
        workItem.setParameter("GroupId", "Crusaders");
        getHandler().executeWorkItem(workItem, manager);

        Thread.sleep(500);

        BlockingTaskSummaryResponseHandler responseHandler = new BlockingTaskSummaryResponseHandler();
        
        getClient().getTasksAssignedAsPotentialOwner("Luke", "en-UK", responseHandler);
        List<TaskSummary> tasks = responseHandler.getResults();
        assertEquals(1, tasks.size());
        TaskSummary taskSummary = tasks.get(0);
        assertEquals("TaskName", taskSummary.getName());
        assertEquals(10, taskSummary.getPriority());
        assertEquals("Comment", taskSummary.getDescription());
        assertEquals(Status.Ready, taskSummary.getStatus());

        System.out.println("Claiming task " + taskSummary.getId());
        BlockingTaskOperationResponseHandler operationResponseHandler = new BlockingTaskOperationResponseHandler();
        getClient().claim(taskSummary.getId(), "Darth Vader", operationResponseHandler);
        PermissionDeniedException denied = null;
        try {
            operationResponseHandler.waitTillDone(DEFAULT_WAIT_TIME);
        } catch (PermissionDeniedException e) {
            denied = e;
        }

        assertNotNull("Should get permissed denied exception", denied);
        System.out.println("Claimed task " + taskSummary.getId());

        //Check if the parent task is InProgress
        BlockingGetTaskResponseHandler getTaskResponseHandler = new BlockingGetTaskResponseHandler();
        getClient().getTask(taskSummary.getId(), getTaskResponseHandler);
        Task task = getTaskResponseHandler.getTask();
        assertEquals(Status.Ready, task.getTaskData().getStatus());
    }

    public void FIXME_testTaskSingleAndGroupActors() throws Exception {
        printTestName();
        
    	Properties userGroups = new Properties();
        userGroups.setProperty("Darth Vader", "Crusaders");
        
        UserGroupCallbackManager.getInstance().setCallback(new DefaultUserGroupCallbackImpl(userGroups));
        TestWorkItemManager manager = new TestWorkItemManager();
        ksession.setWorkItemManager(manager);
        WorkItemImpl workItem = new WorkItemImpl();
        workItem.setName("Human Task One");
        workItem.setParameter("TaskName", "TaskNameOne");
        workItem.setParameter("Comment", "Comment");
        workItem.setParameter("Priority", "10");
        workItem.setParameter("GroupId", "Crusaders");
        getHandler().executeWorkItem(workItem, manager);

        Thread.sleep(500);

        workItem = new WorkItemImpl();
        workItem.setName("Human Task Two");
        workItem.setParameter("TaskName", "TaskNameTwo");
        workItem.setParameter("Comment", "Comment");
        workItem.setParameter("Priority", "10");
        workItem.setParameter("ActorId", "Darth Vader");
        getHandler().executeWorkItem(workItem, manager);

        Thread.sleep(500);

        BlockingTaskSummaryResponseHandler responseHandler = new BlockingTaskSummaryResponseHandler();

        getClient().getTasksAssignedAsPotentialOwner("Darth Vader", "en-UK", responseHandler);
        List<TaskSummary> tasks = responseHandler.getResults();
        assertEquals(2, tasks.size());
    }

    public void testTaskFail() throws Exception {
        printTestName();
        
        TestWorkItemManager manager = new TestWorkItemManager();
        ksession.setWorkItemManager(manager);
        WorkItemImpl workItem = new WorkItemImpl();
        workItem.setName("Human Task");
        workItem.setParameter("TaskName", "TaskName");
        workItem.setParameter("Comment", "Comment");
        workItem.setParameter("Priority", "10");
        workItem.setParameter("ActorId", "Darth Vader");
        getHandler().executeWorkItem(workItem, manager);

        Thread.sleep(500);

        BlockingTaskSummaryResponseHandler responseHandler = new BlockingTaskSummaryResponseHandler();
        getClient().getTasksAssignedAsPotentialOwner("Darth Vader", "en-UK", responseHandler);
        List<TaskSummary> tasks = responseHandler.getResults();
        assertEquals(1, tasks.size());
        TaskSummary task = tasks.get(0);
        assertEquals("TaskName", task.getName());
        assertEquals(10, task.getPriority());
        assertEquals("Comment", task.getDescription());
        assertEquals(Status.Reserved, task.getStatus());
        assertEquals("Darth Vader", task.getActualOwner().getId());

        System.out.println("Starting task " + task.getId());
        BlockingTaskOperationResponseHandler operationResponseHandler = new BlockingTaskOperationResponseHandler();
        getClient().start(task.getId(), "Darth Vader", operationResponseHandler);
        operationResponseHandler.waitTillDone(DEFAULT_WAIT_TIME);
        System.out.println("Started task " + task.getId());

        System.out.println("Failing task " + task.getId());
        operationResponseHandler = new BlockingTaskOperationResponseHandler();
        getClient().fail(task.getId(), "Darth Vader", null, operationResponseHandler);
        operationResponseHandler.waitTillDone(DEFAULT_WAIT_TIME);
        System.out.println("Failed task " + task.getId());

        assertTrue(manager.waitTillAborted(MANAGER_ABORT_WAIT_TIME));
    }

    public void testTaskSkip() throws Exception {
        printTestName();
        
        TestWorkItemManager manager = new TestWorkItemManager();
        ksession.setWorkItemManager(manager);
        WorkItemImpl workItem = new WorkItemImpl();
        workItem.setName("Human Task");
        workItem.setParameter("TaskName", "TaskName");
        workItem.setParameter("Comment", "Comment");
        workItem.setParameter("Priority", "10");
        workItem.setParameter("ActorId", "Darth Vader");
        getHandler().executeWorkItem(workItem, manager);

        Thread.sleep(500);

        BlockingTaskSummaryResponseHandler responseHandler = new BlockingTaskSummaryResponseHandler();
        getClient().getTasksAssignedAsPotentialOwner("Darth Vader", "en-UK", responseHandler);
        List<TaskSummary> tasks = responseHandler.getResults();
        assertEquals(1, tasks.size());
        TaskSummary task = tasks.get(0);
        assertEquals("TaskName", task.getName());
        assertEquals(10, task.getPriority());
        assertEquals("Comment", task.getDescription());
        assertEquals(Status.Reserved, task.getStatus());
        assertEquals("Darth Vader", task.getActualOwner().getId());

        System.out.println("Skipping task " + task.getId());
        BlockingTaskOperationResponseHandler operationResponseHandler = new BlockingTaskOperationResponseHandler();
        getClient().skip(task.getId(), "Darth Vader", operationResponseHandler);
        operationResponseHandler.waitTillDone(DEFAULT_WAIT_TIME);
        System.out.println("Skipped task " + task.getId());

        assertTrue(manager.waitTillAborted(MANAGER_ABORT_WAIT_TIME));
    }

    public void testTaskAbortSkippable() throws Exception {
        printTestName();
        
        TestWorkItemManager manager = new TestWorkItemManager();
        ksession.setWorkItemManager(manager);
        WorkItemImpl workItem = new WorkItemImpl();
        workItem.setName("Human Task");
        workItem.setParameter("TaskName", "TaskName");
        workItem.setParameter("Comment", "Comment");
        workItem.setParameter("Priority", "10");
        workItem.setParameter("ActorId", "Darth Vader");
        getHandler().executeWorkItem(workItem, manager);

        Thread.sleep(500);

        getHandler().abortWorkItem(workItem, manager);

        Thread.sleep(500);

        BlockingTaskSummaryResponseHandler responseHandler = new BlockingTaskSummaryResponseHandler();
        getClient().getTasksAssignedAsPotentialOwner("Darth Vader", "en-UK", responseHandler);
        List<TaskSummary> tasks = responseHandler.getResults();
        assertEquals(0, tasks.size());
    }

    public void testTaskAbortNotSkippable() throws Exception {
        printTestName();
        
        TestWorkItemManager manager = new TestWorkItemManager();
        ksession.setWorkItemManager(manager);
        WorkItemImpl workItem = new WorkItemImpl();
        workItem.setName("Human Task");
        workItem.setParameter("TaskName", "TaskName");
        workItem.setParameter("Comment", "Comment");
        workItem.setParameter("Priority", "10");
        workItem.setParameter("ActorId", "Darth Vader");
        workItem.setParameter("Skippable", "false");
        getHandler().executeWorkItem(workItem, manager);

        Thread.sleep(500);

        BlockingTaskSummaryResponseHandler responseHandler = new BlockingTaskSummaryResponseHandler();
        getClient().getTasksAssignedAsPotentialOwner("Darth Vader", "en-UK", responseHandler);
        List<TaskSummary> tasks = responseHandler.getResults();
        assertEquals(1, tasks.size());

        getHandler().abortWorkItem(workItem, manager);

        Thread.sleep(500);

        responseHandler = new BlockingTaskSummaryResponseHandler();
        getClient().getTasksAssignedAsPotentialOwner("Darth Vader", "en-UK", responseHandler);
        tasks = responseHandler.getResults();
        assertEquals(0, tasks.size());
    }
    
    public void testTaskExit() throws Exception {
        printTestName();
        
        TestWorkItemManager manager = new TestWorkItemManager();
        ksession.setWorkItemManager(manager);
        WorkItemImpl workItem = new WorkItemImpl();
        workItem.setName("Human Task");
        workItem.setParameter("TaskName", "TaskName");
        workItem.setParameter("Comment", "Comment");
        workItem.setParameter("Priority", "10");
        workItem.setParameter("ActorId", "Darth Vader");
        getHandler().executeWorkItem(workItem, manager);

        Thread.sleep(500);

        BlockingTaskSummaryResponseHandler responseHandler = new BlockingTaskSummaryResponseHandler();
        getClient().getTasksAssignedAsPotentialOwner("Darth Vader", "en-UK", responseHandler);
        List<TaskSummary> tasks = responseHandler.getResults();
        assertEquals(1, tasks.size());
        TaskSummary task = tasks.get(0);
        assertEquals("TaskName", task.getName());
        assertEquals(10, task.getPriority());
        assertEquals("Comment", task.getDescription());
        assertEquals(Status.Reserved, task.getStatus());
        assertEquals("Darth Vader", task.getActualOwner().getId());

        BlockingTaskOperationResponseHandler operationResponseHandler = new BlockingTaskOperationResponseHandler();
        getClient().exit(task.getId(), "Administrator", operationResponseHandler);
        operationResponseHandler.waitTillDone(DEFAULT_WAIT_TIME);
        
        BlockingGetTaskResponseHandler getTaskResponseHandler = new BlockingGetTaskResponseHandler();
        getClient().getTask(task.getId(), getTaskResponseHandler);
        Task taskInstance = getTaskResponseHandler.getTask();
        assertEquals("TaskName", taskInstance.getNames().get(0).getText());
        assertEquals(Status.Exited, taskInstance.getTaskData().getStatus());

    }

    public void testTaskData() throws Exception {
        printTestName();
        
        TestWorkItemManager manager = new TestWorkItemManager();
        ksession.setWorkItemManager(manager);
        WorkItemImpl workItem = new WorkItemImpl();
        workItem.setName("Human Task");
        workItem.setParameter("TaskName", "TaskName");
        workItem.setParameter("Comment", "Comment");
        workItem.setParameter("Priority", "10");
        workItem.setParameter("ActorId", "Darth Vader");
        workItem.setParameter("Content", "This is the content");
        getHandler().executeWorkItem(workItem, manager);

        Thread.sleep(500);

        BlockingTaskSummaryResponseHandler responseHandler = new BlockingTaskSummaryResponseHandler();
        getClient().getTasksAssignedAsPotentialOwner("Darth Vader", "en-UK", responseHandler);
        List<TaskSummary> tasks = responseHandler.getResults();
        assertEquals(1, tasks.size());
        TaskSummary taskSummary = tasks.get(0);
        assertEquals("TaskName", taskSummary.getName());
        assertEquals(10, taskSummary.getPriority());
        assertEquals("Comment", taskSummary.getDescription());
        assertEquals(Status.Reserved, taskSummary.getStatus());
        assertEquals("Darth Vader", taskSummary.getActualOwner().getId());

        BlockingGetTaskResponseHandler getTaskResponseHandler = new BlockingGetTaskResponseHandler();
        getClient().getTask(taskSummary.getId(), getTaskResponseHandler);
        Task task = getTaskResponseHandler.getTask();
        assertEquals(AccessType.Inline, task.getTaskData().getDocumentAccessType());
        assertEquals(task.getTaskData().getProcessSessionId(), ksession.getId());
        long contentId = task.getTaskData().getDocumentContentId();
        assertTrue(contentId != -1);
        BlockingGetContentResponseHandler getContentResponseHandler = new BlockingGetContentResponseHandler();
        getClient().getContent(contentId, getContentResponseHandler);
        
        Object data = ContentMarshallerHelper.unmarshall(
                                                            getContentResponseHandler.getContent().getContent(),   
                                                            ksession.getEnvironment());
        
        assertEquals("This is the content", data);

        System.out.println("Starting task " + task.getId());
        BlockingTaskOperationResponseHandler operationResponseHandler = new BlockingTaskOperationResponseHandler();
        getClient().start(task.getId(), "Darth Vader", operationResponseHandler);
        operationResponseHandler.waitTillDone(DEFAULT_WAIT_TIME);
        System.out.println("Started task " + task.getId());

        System.out.println("Completing task " + task.getId());
        operationResponseHandler = new BlockingTaskOperationResponseHandler();
        ContentData result = ContentMarshallerHelper.marshal("This is the result", 
                                                                ksession.getEnvironment());

                
        getClient().complete(task.getId(), "Darth Vader", result, operationResponseHandler);
        operationResponseHandler.waitTillDone(DEFAULT_WAIT_TIME);
        System.out.println("Completed task " + task.getId());

        assertTrue(manager.waitTillCompleted(MANAGER_COMPLETION_WAIT_TIME));
        Map<String, Object> results = manager.getResults();
        assertNotNull(results);
        assertEquals("Darth Vader", results.get("ActorId"));
        assertEquals("This is the result", results.get("Result"));
    }

    public void testTaskDataAutomaticMapping() throws Exception {
        printTestName();
        
        TestWorkItemManager manager = new TestWorkItemManager();
        ksession.setWorkItemManager(manager);
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

        Thread.sleep(500);

        BlockingTaskSummaryResponseHandler responseHandler = new BlockingTaskSummaryResponseHandler();
        getClient().getTasksAssignedAsPotentialOwner("Darth Vader", "en-UK", responseHandler);
        List<TaskSummary> tasks = responseHandler.getResults();
        assertEquals(1, tasks.size());
        TaskSummary taskSummary = tasks.get(0);
        assertEquals("TaskName", taskSummary.getName());
        assertEquals(10, taskSummary.getPriority());
        assertEquals("Comment", taskSummary.getDescription());
        assertEquals(Status.Reserved, taskSummary.getStatus());
        assertEquals("Darth Vader", taskSummary.getActualOwner().getId());



        BlockingGetTaskResponseHandler getTaskResponseHandler = new BlockingGetTaskResponseHandler();
        getClient().getTask(taskSummary.getId(), getTaskResponseHandler);
        Task task = getTaskResponseHandler.getTask();
        assertEquals(AccessType.Inline, task.getTaskData().getDocumentAccessType());
        long contentId = task.getTaskData().getDocumentContentId();
        assertTrue(contentId != -1);
        BlockingGetContentResponseHandler getContentResponseHandler = new BlockingGetContentResponseHandler();
        getClient().getContent(contentId, getContentResponseHandler);
        
         
        
        Map<String, Object> data = (Map<String, Object>) ContentMarshallerHelper.unmarshall(
                                                            getContentResponseHandler.getContent().getContent(),   
                                                            ksession.getEnvironment());
        
        //Checking that the input parameters are being copied automatically if the Content Element doesn't exist
        assertEquals("MyObjectValue", ((MyObject) data.get("MyObject")).getValue());
        assertEquals("10", data.get("Priority"));
        assertEquals("MyObjectValue", ((MyObject) ((Map<String, Object>) data.get("MyMap")).get("MyObjectInsideTheMap")).getValue());

        System.out.println("Starting task " + task.getId());
        BlockingTaskOperationResponseHandler operationResponseHandler = new BlockingTaskOperationResponseHandler();
        getClient().start(task.getId(), "Darth Vader", operationResponseHandler);
        operationResponseHandler.waitTillDone(DEFAULT_WAIT_TIME);
        System.out.println("Started task " + task.getId());

        System.out.println("Completing task " + task.getId());
        operationResponseHandler = new BlockingTaskOperationResponseHandler();

        ContentData result = ContentMarshallerHelper.marshal("This is the result", 
                                                                ksession.getEnvironment());
        getClient().complete(task.getId(), "Darth Vader", result, operationResponseHandler);
        operationResponseHandler.waitTillDone(DEFAULT_WAIT_TIME);
        System.out.println("Completed task " + task.getId());

        assertTrue(manager.waitTillCompleted(MANAGER_COMPLETION_WAIT_TIME));
        Map<String, Object> results = manager.getResults();
        assertNotNull(results);
        assertEquals("Darth Vader", results.get("ActorId"));
        assertEquals("This is the result", results.get("Result"));
    }
    
    public void testTaskCreateFailedWithLog() throws Exception {
        printTestName();
        
        TestWorkItemManager manager = new TestWorkItemManager();
        if (handler instanceof AsyncGenericHTWorkItemHandler) {
            ((AsyncGenericHTWorkItemHandler) handler).setAction(OnErrorAction.LOG);
        }
        ksession.setWorkItemManager(manager);
        WorkItemImpl workItem = new WorkItemImpl();
        workItem.setName("Human Task");
        workItem.setParameter("TaskName", "TaskName");
        workItem.setParameter("Comment", "Comment");
        workItem.setParameter("Priority", "10");
        workItem.setParameter("ActorId", "DoesNotExist");
        workItem.setProcessInstanceId(10);
        manager.registerWorkItemHandler("Human Task", getHandler());
        
        getHandler().executeWorkItem(workItem, manager);
        assertFalse(manager.waitTillCompleted(1000));
        assertFalse(manager.isAborted());
    }
    
    public void testTaskCreateFailedWithAbort() throws Exception {
        printTestName();
        
        TestWorkItemManager manager = new TestWorkItemManager();
        if (handler instanceof AsyncGenericHTWorkItemHandler) {
            ((AsyncGenericHTWorkItemHandler) handler).setAction(OnErrorAction.ABORT);
        }
        ksession.setWorkItemManager(manager);
        WorkItemImpl workItem = new WorkItemImpl();
        workItem.setName("Human Task");
        workItem.setParameter("TaskName", "TaskName");
        workItem.setParameter("Comment", "Comment");
        workItem.setParameter("Priority", "10");
        workItem.setParameter("ActorId", "DoesNotExist");
        workItem.setProcessInstanceId(10);
        
        
        getHandler().executeWorkItem(workItem, manager);
        assertTrue(manager.waitTillAborted(1000));
        assertTrue(manager.isAborted());
    }
    
    public void testTaskWithCustomLocale() throws Exception {
        TestWorkItemManager manager = new TestWorkItemManager();
        WorkItemImpl workItem = new WorkItemImpl();
        workItem.setName("Human Task");
        workItem.setParameter("TaskName", "TaskName");
        workItem.setParameter("Comment", "Comment");
        workItem.setParameter("Priority", "10");
        workItem.setParameter("ActorId", "Darth Vader");
        workItem.setParameter("Locale", "de-DE");
        workItem.setProcessInstanceId(10);
        handler.executeWorkItem(workItem, manager);

        Thread.sleep(500);

        BlockingTaskSummaryResponseHandler responseHandler = new BlockingTaskSummaryResponseHandler();
        client.getTasksAssignedAsPotentialOwner("Darth Vader", "de-DE", responseHandler);
        List<TaskSummary> tasks = responseHandler.getResults();
        assertEquals(1, tasks.size());
        TaskSummary task = tasks.get(0);
        assertEquals("TaskName", task.getName());
        assertEquals(10, task.getPriority());
        assertEquals("Comment", task.getDescription());
        assertEquals(Status.Reserved, task.getStatus());
        assertEquals("Darth Vader", task.getActualOwner().getId());
        assertEquals(10, task.getProcessInstanceId());

        BlockingTaskOperationResponseHandler operationResponseHandler = new BlockingTaskOperationResponseHandler();
        client.start(task.getId(), "Darth Vader", operationResponseHandler);
        operationResponseHandler.waitTillDone(DEFAULT_WAIT_TIME);

        operationResponseHandler = new BlockingTaskOperationResponseHandler();
        client.complete(task.getId(), "Darth Vader", null, operationResponseHandler);
        operationResponseHandler.waitTillDone(15000);

    }   

    public void TODOtestOnAllSubTasksEndParentEndStrategy() throws Exception {
        printTestName();

        TestWorkItemManager manager = new TestWorkItemManager();
        ksession.setWorkItemManager(manager);
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


        Thread.sleep(500);

        //Test if the task is succesfully created
        BlockingTaskSummaryResponseHandler responseHandler = new BlockingTaskSummaryResponseHandler();
        getClient().getTasksAssignedAsPotentialOwner("Darth Vader", "en-UK", responseHandler);
        List<TaskSummary> tasks = responseHandler.getResults();
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

        Thread.sleep(500);

        //Create the child task2
        workItem = new WorkItemImpl();
        workItem.setName("Human Task2");
        workItem.setParameter("TaskName", "TaskNameChild2");
        workItem.setParameter("Comment", "CommentChild2");
        workItem.setParameter("Priority", "10");
        workItem.setParameter("ActorId", "Darth Vader");
        workItem.setParameter("ParentId", task.getId());
        getHandler().executeWorkItem(workItem, manager);

        Thread.sleep(500);

        //Start the parent task
        System.out.println("Starting task " + task.getId());
        BlockingTaskOperationResponseHandler operationResponseHandler = new BlockingTaskOperationResponseHandler();
        getClient().start(task.getId(), "Darth Vader", operationResponseHandler);
        operationResponseHandler.waitTillDone(DEFAULT_WAIT_TIME);
        System.out.println("Started task " + task.getId());

        //Check if the parent task is InProgress
        BlockingGetTaskResponseHandler getTaskResponseHandler = new BlockingGetTaskResponseHandler();
        getClient().getTask(task.getId(), getTaskResponseHandler);
        Task parentTask = getTaskResponseHandler.getTask();
        assertEquals(Status.InProgress, parentTask.getTaskData().getStatus());
        assertEquals(users.get("darth"), parentTask.getTaskData().getActualOwner());

        //Get all the subtask created for the parent task based on the potential owner
        responseHandler = new BlockingTaskSummaryResponseHandler();
        getClient().getSubTasksAssignedAsPotentialOwner(parentTask.getId(), "Darth Vader", "en-UK", responseHandler);
        List<TaskSummary> subTasks = responseHandler.getResults();
        assertEquals(2, subTasks.size());
        TaskSummary subTaskSummary1 = subTasks.get(0);
        TaskSummary subTaskSummary2 = subTasks.get(1);
        assertNotNull(subTaskSummary1);
        assertNotNull(subTaskSummary2);

        //Starting the sub task 1
        System.out.println("Starting sub task " + subTaskSummary1.getId());
        operationResponseHandler = new BlockingTaskOperationResponseHandler();
        getClient().start(subTaskSummary1.getId(), "Darth Vader", operationResponseHandler);
        operationResponseHandler.waitTillDone(DEFAULT_WAIT_TIME);
        System.out.println("Started sub task " + subTaskSummary1.getId());

        //Starting the sub task 2
        System.out.println("Starting sub task " + subTaskSummary2.getId());
        operationResponseHandler = new BlockingTaskOperationResponseHandler();
        getClient().start(subTaskSummary2.getId(), "Darth Vader", operationResponseHandler);
        operationResponseHandler.waitTillDone(DEFAULT_WAIT_TIME);
        System.out.println("Started sub task " + subTaskSummary2.getId());

        //Check if the child task 1 is InProgress
        getTaskResponseHandler = new BlockingGetTaskResponseHandler();
        getClient().getTask(subTaskSummary1.getId(), getTaskResponseHandler);
        Task subTask1 = getTaskResponseHandler.getTask();
        assertEquals(Status.InProgress, subTask1.getTaskData().getStatus());
        assertEquals(users.get("darth"), subTask1.getTaskData().getActualOwner());

        //Check if the child task 2 is InProgress
        getTaskResponseHandler = new BlockingGetTaskResponseHandler();
        getClient().getTask(subTaskSummary2.getId(), getTaskResponseHandler);
        Task subTask2 = getTaskResponseHandler.getTask();
        assertEquals(Status.InProgress, subTask2.getTaskData().getStatus());
        assertEquals(users.get("darth"), subTask2.getTaskData().getActualOwner());

        // Complete the child task 1
        System.out.println("Completing sub task " + subTask1.getId());
        operationResponseHandler = new BlockingTaskOperationResponseHandler();
        getClient().complete(subTask1.getId(), "Darth Vader", null, operationResponseHandler);
        operationResponseHandler.waitTillDone(DEFAULT_WAIT_TIME);
        System.out.println("Completed sub task " + subTask1.getId());

        // Complete the child task 2
        System.out.println("Completing sub task " + subTask2.getId());
        operationResponseHandler = new BlockingTaskOperationResponseHandler();
        getClient().complete(subTask2.getId(), "Darth Vader", null, operationResponseHandler);
        operationResponseHandler.waitTillDone(DEFAULT_WAIT_TIME);
        System.out.println("Completed sub task " + subTask2.getId());

        //Check if the child task 1 is Completed

        getTaskResponseHandler = new BlockingGetTaskResponseHandler();
        getClient().getTask(subTask1.getId(), getTaskResponseHandler);
        subTask1 = getTaskResponseHandler.getTask();
        assertEquals(Status.Completed, subTask1.getTaskData().getStatus());
        assertEquals(users.get("darth"), subTask1.getTaskData().getActualOwner());

        //Check if the child task 2 is Completed

        getTaskResponseHandler = new BlockingGetTaskResponseHandler();
        getClient().getTask(subTask2.getId(), getTaskResponseHandler);
        subTask2 = getTaskResponseHandler.getTask();
        assertEquals(Status.Completed, subTask2.getTaskData().getStatus());
        assertEquals(users.get("darth"), subTask2.getTaskData().getActualOwner());

        // Check is the parent task is Complete
        getTaskResponseHandler = new BlockingGetTaskResponseHandler();
        getClient().getTask(parentTask.getId(), getTaskResponseHandler);
        parentTask = getTaskResponseHandler.getTask();
        assertEquals(Status.Completed, parentTask.getTaskData().getStatus());
        assertEquals(users.get("darth"), parentTask.getTaskData().getActualOwner());

        assertTrue(manager.waitTillCompleted(MANAGER_COMPLETION_WAIT_TIME));
    }

    public void TODOtestOnParentAbortAllSubTasksEndStrategy() throws Exception {
        printTestName();

        TestWorkItemManager manager = new TestWorkItemManager();
        ksession.setWorkItemManager(manager);
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


        Thread.sleep(500);

        //Test if the task is succesfully created
        BlockingTaskSummaryResponseHandler responseHandler = new BlockingTaskSummaryResponseHandler();
        getClient().getTasksAssignedAsPotentialOwner("Darth Vader", "en-UK", responseHandler);
        List<TaskSummary> tasks = responseHandler.getResults();
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

        Thread.sleep(500);

        //Create the child task2
        workItem = new WorkItemImpl();
        workItem.setName("Human Task2");
        workItem.setParameter("TaskName", "TaskNameChild2");
        workItem.setParameter("Comment", "CommentChild2");
        workItem.setParameter("Priority", "10");
        workItem.setParameter("ActorId", "Darth Vader");
        workItem.setParameter("ParentId", task.getId());
        getHandler().executeWorkItem(workItem, manager);

        Thread.sleep(500);

        //Start the parent task
        System.out.println("Starting task " + task.getId());
        BlockingTaskOperationResponseHandler operationResponseHandler = new BlockingTaskOperationResponseHandler();
        getClient().start(task.getId(), "Darth Vader", operationResponseHandler);
        operationResponseHandler.waitTillDone(DEFAULT_WAIT_TIME);
        System.out.println("Started task " + task.getId());

        //Check if the parent task is InProgress
        BlockingGetTaskResponseHandler getTaskResponseHandler = new BlockingGetTaskResponseHandler();
        getClient().getTask(task.getId(), getTaskResponseHandler);
        Task parentTask = getTaskResponseHandler.getTask();
        assertEquals(Status.InProgress, parentTask.getTaskData().getStatus());
        assertEquals(users.get("darth"), parentTask.getTaskData().getActualOwner());

        //Get all the subtask created for the parent task based on the potential owner
        responseHandler = new BlockingTaskSummaryResponseHandler();
        getClient().getSubTasksAssignedAsPotentialOwner(parentTask.getId(), "Darth Vader", "en-UK", responseHandler);
        List<TaskSummary> subTasks = responseHandler.getResults();
        assertEquals(2, subTasks.size());
        TaskSummary subTaskSummary1 = subTasks.get(0);
        TaskSummary subTaskSummary2 = subTasks.get(1);
        assertNotNull(subTaskSummary1);
        assertNotNull(subTaskSummary2);

        //Starting the sub task 1
        System.out.println("Starting sub task " + subTaskSummary1.getId());
        operationResponseHandler = new BlockingTaskOperationResponseHandler();
        getClient().start(subTaskSummary1.getId(), "Darth Vader", operationResponseHandler);
        operationResponseHandler.waitTillDone(DEFAULT_WAIT_TIME);
        System.out.println("Started sub task " + subTaskSummary1.getId());

        //Starting the sub task 2
        System.out.println("Starting sub task " + subTaskSummary2.getId());
        operationResponseHandler = new BlockingTaskOperationResponseHandler();
        getClient().start(subTaskSummary2.getId(), "Darth Vader", operationResponseHandler);
        operationResponseHandler.waitTillDone(DEFAULT_WAIT_TIME);
        System.out.println("Started sub task " + subTaskSummary2.getId());

        //Check if the child task 1 is InProgress
        getTaskResponseHandler = new BlockingGetTaskResponseHandler();
        getClient().getTask(subTaskSummary1.getId(), getTaskResponseHandler);
        Task subTask1 = getTaskResponseHandler.getTask();
        assertEquals(Status.InProgress, subTask1.getTaskData().getStatus());
        assertEquals(users.get("darth"), subTask1.getTaskData().getActualOwner());

        //Check if the child task 2 is InProgress
        getTaskResponseHandler = new BlockingGetTaskResponseHandler();
        getClient().getTask(subTaskSummary2.getId(), getTaskResponseHandler);
        Task subTask2 = getTaskResponseHandler.getTask();
        assertEquals(Status.InProgress, subTask2.getTaskData().getStatus());
        assertEquals(users.get("darth"), subTask2.getTaskData().getActualOwner());

        // Complete the parent task
        System.out.println("Completing parent task " + parentTask.getId());
        operationResponseHandler = new BlockingTaskOperationResponseHandler();
        getClient().skip(parentTask.getId(), "Darth Vader", operationResponseHandler);
        operationResponseHandler.waitTillDone(DEFAULT_WAIT_TIME);
        System.out.println("Completed parent task " + parentTask.getId());

        //Check if the child task 1 is Completed
        getTaskResponseHandler = new BlockingGetTaskResponseHandler();
        getClient().getTask(subTaskSummary1.getId(), getTaskResponseHandler);
        subTask1 = getTaskResponseHandler.getTask();
        assertEquals(Status.Completed, subTask1.getTaskData().getStatus());
        assertEquals(users.get("darth"), subTask1.getTaskData().getActualOwner());

        //Check if the child task 2 is Completed
        getTaskResponseHandler = new BlockingGetTaskResponseHandler();
        getClient().getTask(subTaskSummary2.getId(), getTaskResponseHandler);
        subTask2 = getTaskResponseHandler.getTask();
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
