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
package org.jbpm.task.wih;

import java.util.HashMap;
import java.util.List;
import java.util.Map;
import javax.inject.Inject;

import org.drools.process.instance.impl.WorkItemImpl;
import org.kie.runtime.process.WorkItemHandler;
import org.kie.runtime.process.WorkItemManager;
import org.jbpm.task.AccessType;
import org.jbpm.task.Status;
import org.jbpm.task.Task;
import org.jbpm.task.api.TaskServiceEntryPoint;
import org.jbpm.task.exception.PermissionDeniedException;
import org.jbpm.task.query.TaskSummary;
import org.jbpm.task.test.MyObject;
import org.jbpm.task.test.TestStatefulKnowledgeSession;
import org.jbpm.task.utils.ContentMarshallerHelper;
import org.junit.Test;
import static org.junit.Assert.*;
import org.junit.Ignore;

public abstract class HTWorkItemHandlerBaseTest {

    private static final int DEFAULT_WAIT_TIME = 5000;
    private static final int MANAGER_COMPLETION_WAIT_TIME = DEFAULT_WAIT_TIME;
    private static final int MANAGER_ABORT_WAIT_TIME = DEFAULT_WAIT_TIME;
    
    private WorkItemHandler handler;
    protected TestStatefulKnowledgeSession ksession = new TestStatefulKnowledgeSession();

    @Inject
    protected TaskServiceEntryPoint taskService; 
 
    @Test
    public void testTask() throws Exception {
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

        
        List<TaskSummary> tasks = taskService.getTasksAssignedAsPotentialOwner("Darth Vader", "en-UK");
        assertEquals(1, tasks.size());
        TaskSummary task = tasks.get(0);
        assertEquals("TaskName", task.getName());
        assertEquals(10, task.getPriority());
        assertEquals("Comment", task.getDescription());
        assertEquals(Status.Reserved, task.getStatus());
        assertEquals("Darth Vader", task.getActualOwner().getId());
        assertEquals(10, task.getProcessInstanceId());

        taskService.start(task.getId(), "Darth Vader");
        taskService.complete(task.getId(), "Darth Vader", null);

        assertTrue(manager.waitTillCompleted(MANAGER_COMPLETION_WAIT_TIME));
    }
    @Test
    public void testTaskMultipleActors() throws Exception {
        TestWorkItemManager manager = new TestWorkItemManager();
        ksession.setWorkItemManager(manager);
        WorkItemImpl workItem = new WorkItemImpl();
        workItem.setName("Human Task");
        workItem.setParameter("TaskName", "TaskName");
        workItem.setParameter("Comment", "Comment");
        workItem.setParameter("Priority", "10");
        workItem.setParameter("ActorId", "Darth Vader, Dalai Lama");
        getHandler().executeWorkItem(workItem, manager);

        
        List<TaskSummary> tasks = taskService.getTasksAssignedAsPotentialOwner("Darth Vader", "en-UK");
        assertEquals(1, tasks.size());
        TaskSummary task = tasks.get(0);
        assertEquals("TaskName", task.getName());
        assertEquals(10, task.getPriority());
        assertEquals("Comment", task.getDescription());
        assertEquals(Status.Ready, task.getStatus());

        taskService.claim(task.getId(), "Darth Vader");

        taskService.start(task.getId(), "Darth Vader");

        taskService.complete(task.getId(), "Darth Vader", null);
        
        assertTrue(manager.waitTillCompleted(MANAGER_COMPLETION_WAIT_TIME));
    }
    @Ignore
    @Test // FIX UserGROUP CALLBACK
    public void testTaskGroupActors() throws Exception {

    	TestWorkItemManager manager = new TestWorkItemManager();
        ksession.setWorkItemManager(manager);
        WorkItemImpl workItem = new WorkItemImpl();
        workItem.setName("Human Task");
        workItem.setParameter("TaskName", "TaskName");
        workItem.setParameter("Comment", "Comment");
        workItem.setParameter("Priority", "10");
        workItem.setParameter("GroupId", "Crusaders");
        getHandler().executeWorkItem(workItem, manager);

        List<TaskSummary> tasks = taskService.getTasksAssignedAsPotentialOwner("Luke", "en-UK");
        assertEquals(1, tasks.size());
        TaskSummary taskSummary = tasks.get(0);
        assertEquals("TaskName", taskSummary.getName());
        assertEquals(10, taskSummary.getPriority());
        assertEquals("Comment", taskSummary.getDescription());
        assertEquals(Status.Ready, taskSummary.getStatus());

        PermissionDeniedException denied = null;
        try {
            taskService.claim(taskSummary.getId(), "Darth Vader");
        } catch (PermissionDeniedException e) {
            denied = e;
        }

        assertNotNull("Should get permissed denied exception", denied);

        //Check if the parent task is InProgress
        
        
        Task task = taskService.getTaskById(taskSummary.getId());
        assertEquals(Status.Ready, task.getTaskData().getStatus());
    }
    
    
    @Test
    @Ignore // FIX USER GROUP CALLBACK STUFF
    public void testTaskSingleAndGroupActors() throws Exception {
//    	Properties userGroups = new Properties();
//        userGroups.setProperty("Darth Vader", "Crusaders");
//        
//        UserGroupCallbackManager.getInstance().setCallback(new DefaultUserGroupCallbackImpl(userGroups));
        TestWorkItemManager manager = new TestWorkItemManager();
        ksession.setWorkItemManager(manager);
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
        
        List<TaskSummary> tasks = taskService.getTasksAssignedAsPotentialOwner("Darth Vader", "en-UK");
        assertEquals(2, tasks.size());
    }
    @Test
    public void testTaskFail() throws Exception {
        TestWorkItemManager manager = new TestWorkItemManager();
        ksession.setWorkItemManager(manager);
        WorkItemImpl workItem = new WorkItemImpl();
        workItem.setName("Human Task");
        workItem.setParameter("TaskName", "TaskName");
        workItem.setParameter("Comment", "Comment");
        workItem.setParameter("Priority", "10");
        workItem.setParameter("ActorId", "Darth Vader");
        getHandler().executeWorkItem(workItem, manager);

        
        
        List<TaskSummary> tasks = taskService.getTasksAssignedAsPotentialOwner("Darth Vader", "en-UK");
        assertEquals(1, tasks.size());
        TaskSummary task = tasks.get(0);
        assertEquals("TaskName", task.getName());
        assertEquals(10, task.getPriority());
        assertEquals("Comment", task.getDescription());
        assertEquals(Status.Reserved, task.getStatus());
        assertEquals("Darth Vader", task.getActualOwner().getId());

        taskService.start(task.getId(), "Darth Vader");
        
        taskService.fail(task.getId(), "Darth Vader", null);
        
        assertTrue(manager.waitTillAborted(MANAGER_ABORT_WAIT_TIME));
    }
    @Test
    public void testTaskSkip() throws Exception {
        TestWorkItemManager manager = new TestWorkItemManager();
        ksession.setWorkItemManager(manager);
        WorkItemImpl workItem = new WorkItemImpl();
        workItem.setName("Human Task");
        workItem.setParameter("TaskName", "TaskName");
        workItem.setParameter("Comment", "Comment");
        workItem.setParameter("Priority", "10");
        workItem.setParameter("ActorId", "Darth Vader");
        getHandler().executeWorkItem(workItem, manager);

        
        
        List<TaskSummary> tasks = taskService.getTasksAssignedAsPotentialOwner("Darth Vader", "en-UK");
        assertEquals(1, tasks.size());
        TaskSummary task = tasks.get(0);
        assertEquals("TaskName", task.getName());
        assertEquals(10, task.getPriority());
        assertEquals("Comment", task.getDescription());
        assertEquals(Status.Reserved, task.getStatus());
        assertEquals("Darth Vader", task.getActualOwner().getId());

        taskService.skip(task.getId(), "Darth Vader");
        
        assertTrue(manager.waitTillAborted(MANAGER_ABORT_WAIT_TIME));
    }
    
    @Test
    public void testTaskExit() throws Exception {
        TestWorkItemManager manager = new TestWorkItemManager();
        ksession.setWorkItemManager(manager);
        WorkItemImpl workItem = new WorkItemImpl();
        workItem.setName("Human Task");
        workItem.setParameter("TaskName", "TaskName");
        workItem.setParameter("Comment", "Comment");
        workItem.setParameter("Priority", "10");
        workItem.setParameter("ActorId", "Darth Vader");
        getHandler().executeWorkItem(workItem, manager);
        
        Task task = taskService.getTaskByWorkItemId(workItem.getId());

        taskService.exit(task.getId(), "Administrator");
        
        task = taskService.getTaskByWorkItemId(workItem.getId());
        assertEquals("TaskName", task.getNames().get(0).getText());
        assertEquals(10, task.getPriority());
        assertEquals("Comment", task.getDescriptions().get(0).getText());
        assertEquals(Status.Exited, task.getTaskData().getStatus());
        
        List<TaskSummary> tasks = taskService.getTasksAssignedAsPotentialOwner("Darth Vader", "en-UK");
        assertEquals(0, tasks.size());
    }
    @Test
    public void testTaskExitNonAdministrator() throws Exception {
        TestWorkItemManager manager = new TestWorkItemManager();
        ksession.setWorkItemManager(manager);
        WorkItemImpl workItem = new WorkItemImpl();
        workItem.setName("Human Task");
        workItem.setParameter("TaskName", "TaskName");
        workItem.setParameter("Comment", "Comment");
        workItem.setParameter("Priority", "10");
        workItem.setParameter("ActorId", "Darth Vader");
        getHandler().executeWorkItem(workItem, manager);
        
        Task task = taskService.getTaskByWorkItemId(workItem.getId());

        try {
            taskService.exit(task.getId(), "Darth Vader");
            fail("Should not allow to exit task for non administrators");
        } catch (PermissionDeniedException e) {
            
        }
        
        List<TaskSummary> tasks = taskService.getTasksAssignedAsPotentialOwner("Darth Vader", "en-UK");
        assertEquals(1, tasks.size());
        TaskSummary taskSummary = tasks.get(0);
        assertEquals("TaskName", taskSummary.getName());
        assertEquals(10, taskSummary.getPriority());
        assertEquals("Comment", taskSummary.getDescription());
        assertEquals(Status.Reserved, taskSummary.getStatus());
        assertEquals("Darth Vader", taskSummary.getActualOwner().getId());
    }
    @Test
    public void testTaskAbortSkippable() throws Exception {
        TestWorkItemManager manager = new TestWorkItemManager();
        ksession.setWorkItemManager(manager);
        WorkItemImpl workItem = new WorkItemImpl();
        workItem.setName("Human Task");
        workItem.setParameter("TaskName", "TaskName");
        workItem.setParameter("Comment", "Comment");
        workItem.setParameter("Priority", "10");
        workItem.setParameter("ActorId", "Darth Vader");
        getHandler().executeWorkItem(workItem, manager);

        

        getHandler().abortWorkItem(workItem, manager);

        List<TaskSummary> tasks = taskService.getTasksAssignedAsPotentialOwner("Darth Vader", "en-UK");
        assertEquals(0, tasks.size());
    }
    @Test
    public void testTaskAbortNotSkippable() throws Exception {
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

        
        List<TaskSummary> tasks = taskService.getTasksAssignedAsPotentialOwner("Darth Vader", "en-UK");
        assertEquals(1, tasks.size());

        getHandler().abortWorkItem(workItem, manager);

        // aborting work item will exit task and not skip it
        tasks = taskService.getTasksAssignedAsPotentialOwner("Darth Vader", "en-UK");
        assertEquals(0, tasks.size());
    }
    @Test
    public void testTaskData() throws Exception {
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

        List<TaskSummary> tasks = taskService.getTasksAssignedAsPotentialOwner("Darth Vader", "en-UK");
        assertEquals(1, tasks.size());
        TaskSummary taskSummary = tasks.get(0);
        assertEquals("TaskName", taskSummary.getName());
        assertEquals(10, taskSummary.getPriority());
        assertEquals("Comment", taskSummary.getDescription());
        assertEquals(Status.Reserved, taskSummary.getStatus());
        assertEquals("Darth Vader", taskSummary.getActualOwner().getId());
        
        Task task = taskService.getTaskById(taskSummary.getId());
        assertEquals(AccessType.Inline, task.getTaskData().getDocumentAccessType());
        assertEquals(task.getTaskData().getProcessSessionId(), TestStatefulKnowledgeSession.testSessionId);
        long contentId = task.getTaskData().getDocumentContentId();
        assertTrue(contentId != -1);

        Object data = ContentMarshallerHelper.unmarshall(
                                                            taskService.getContentById(contentId).getContent(), 
                                                            ksession.getEnvironment());
        assertEquals("This is the content", data);

        taskService.start(task.getId(), "Darth Vader");
       
        Map<String, Object> results = new HashMap<String, Object>();
        results.put("Result", "This is the result");
//        ContentData result = ContentMarshallerHelper.marshal(, 
//                                                                ksession.getEnvironment());
        taskService.complete(task.getId(), "Darth Vader", results);
        
        assertTrue(manager.waitTillCompleted(MANAGER_COMPLETION_WAIT_TIME));
        results = manager.getResults();
        assertNotNull(results);
        assertEquals("Darth Vader", results.get("ActorId"));
        assertEquals("This is the result", results.get("Result"));
    }
    @Test
    public void testTaskDataAutomaticMapping() throws Exception {
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

       

       
        
        List<TaskSummary> tasks = taskService.getTasksAssignedAsPotentialOwner("Darth Vader", "en-UK");
        assertEquals(1, tasks.size());
        TaskSummary taskSummary = tasks.get(0);
        assertEquals("TaskName", taskSummary.getName());
        assertEquals(10, taskSummary.getPriority());
        assertEquals("Comment", taskSummary.getDescription());
        assertEquals(Status.Reserved, taskSummary.getStatus());
        assertEquals("Darth Vader", taskSummary.getActualOwner().getId());



        
        
        Task task = taskService.getTaskById(taskSummary.getId());
        assertEquals(AccessType.Inline, task.getTaskData().getDocumentAccessType());
        long contentId = task.getTaskData().getDocumentContentId();
        assertTrue(contentId != -1);
        
        

        Map<String, Object> data = (Map<String, Object>) ContentMarshallerHelper.unmarshall(
                                                            taskService.getContentById(contentId).getContent(),  
                                                            ksession.getEnvironment());
      
        //Checking that the input parameters are being copied automatically if the Content Element doesn't exist
        assertEquals("MyObjectValue", ((MyObject) data.get("MyObject")).getValue());
        assertEquals("10", data.get("Priority"));
        assertEquals("MyObjectValue", ((MyObject) ((Map<String, Object>) data.get("MyMap")).get("MyObjectInsideTheMap")).getValue());

        taskService.start(task.getId(), "Darth Vader");

        Map<String, Object> results = new HashMap<String, Object>();
        results.put("Result", "This is the result");

                
        taskService.complete(task.getId(), "Darth Vader", results);

        assertTrue(manager.waitTillCompleted(MANAGER_COMPLETION_WAIT_TIME));
        results = manager.getResults();
        assertNotNull(results);
        assertEquals("Darth Vader", results.get("ActorId"));
        assertEquals("This is the result", results.get("Result"));
    }
    
    
//    @Test
//    public void testTaskCreateFailedWithLog() throws Exception {
//        TestWorkItemManager manager = new TestWorkItemManager();
//        
//        if (handler instanceof GenericHTWorkItemHandler) {
//            ((GenericHTWorkItemHandler) handler).setAction(OnErrorAction.LOG);
//        }
//        
//        ksession.setWorkItemManager(manager);
//        WorkItemImpl workItem = new WorkItemImpl();
//        workItem.setName("Human Task");
//        workItem.setParameter("TaskName", "TaskName");
//        workItem.setParameter("Comment", "Comment");
//        workItem.setParameter("Priority", "10");
//        workItem.setParameter("ActorId", "DoesNotExist");
//        workItem.setProcessInstanceId(10);
//        
//        
//        handler.executeWorkItem(workItem, manager);
//        assertFalse(manager.isAborted());
//    }
//    @Test
//    public void testTaskCreateFailedWithAbort() throws Exception {
//        TestWorkItemManager manager = new TestWorkItemManager();
//        
//        if (handler instanceof GenericHTWorkItemHandler) {
//            ((GenericHTWorkItemHandler) handler).setAction(OnErrorAction.ABORT);
//        }
//        ksession.setWorkItemManager(manager);
//        WorkItemImpl workItem = new WorkItemImpl();
//        workItem.setName("Human Task");
//        workItem.setParameter("TaskName", "TaskName");
//        workItem.setParameter("Comment", "Comment");
//        workItem.setParameter("Priority", "10");
//        workItem.setParameter("ActorId", "DoesNotExist");
//        workItem.setProcessInstanceId(10);
//        
//        
//        handler.executeWorkItem(workItem, manager);
//        assertTrue(manager.isAborted());
//    }
//    @Test
//    public void testTaskCreateFailedWithRethrow() throws Exception {
//        TestWorkItemManager manager = new TestWorkItemManager();
//        
//        if (handler instanceof GenericHTWorkItemHandler) {
//            ((GenericHTWorkItemHandler) handler).setAction(OnErrorAction.RETHROW);
//        }
//        ksession.setWorkItemManager(manager);
//        WorkItemImpl workItem = new WorkItemImpl();
//        workItem.setName("Human Task");
//        workItem.setParameter("TaskName", "TaskName");
//        workItem.setParameter("Comment", "Comment");
//        workItem.setParameter("Priority", "10");
//        workItem.setParameter("ActorId", "DoesNotExist");
//        workItem.setProcessInstanceId(10);
//        
//        try {
//            handler.executeWorkItem(workItem, manager);
//            fail("Should fail due to OnErroAction set to rethrow");
//        } catch (Exception e) {
//            // do nothing
//            
//        }
//    }
//

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
