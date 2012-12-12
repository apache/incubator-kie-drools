/*
 * Copyright 2012 JBoss by Red Hat.
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
package org.jbpm.process.workitem.wsht.mina;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

import org.drools.process.instance.impl.WorkItemImpl;
import org.jbpm.process.workitem.wsht.AbstractHTWorkItemHandler;
import org.jbpm.process.workitem.wsht.AsyncGenericHTWorkItemHandler;
import org.jbpm.process.workitem.wsht.AsyncMinaHTWorkItemHandler;
import org.jbpm.process.workitem.wsht.GenericHTWorkItemHandler;
import org.jbpm.process.workitem.wsht.MinaHTWorkItemHandler;
import org.jbpm.task.AsyncTaskService;
import org.jbpm.task.BaseTest;
import org.jbpm.task.Status;
import org.jbpm.task.TaskService;
import org.jbpm.task.TestStatefulKnowledgeSession;
import org.jbpm.task.query.TaskSummary;
import org.jbpm.task.service.TaskServer;
import org.jbpm.task.service.mina.MinaTaskServer;
import org.jbpm.task.service.responsehandlers.BlockingTaskOperationResponseHandler;
import org.jbpm.task.service.responsehandlers.BlockingTaskSummaryResponseHandler;
import org.jbpm.task.utils.OnErrorAction;
import org.junit.Test;
import org.kie.runtime.process.WorkItemHandler;
import org.kie.runtime.process.WorkItemManager;

public class MinaMultipleHandlersTest extends BaseTest {

    private TaskService client;
    private AsyncTaskService clientAsync;
    private TaskServer server;
    
    private List<AbstractHTWorkItemHandler> handlers = new ArrayList<AbstractHTWorkItemHandler>();
    @Override
    protected void setUp() throws Exception {
       super.setUp();
       server = new MinaTaskServer(taskService);
      
       System.out.println("Waiting for the MinaTask Server to come up");
       try {
           startTaskServerThread(server, false);
       } catch (Exception e) {
           startTaskServerThread(server, true);
       }
       
    }

    protected void tearDown() throws Exception {
        if (client != null) {
            client.disconnect();
        }
        
        if (clientAsync != null) {
            clientAsync.disconnect();
        }
        
        for (AbstractHTWorkItemHandler handler : handlers) {
            if (handler instanceof GenericHTWorkItemHandler) {
                ((GenericHTWorkItemHandler) handler).dispose();
            } else if (handler instanceof AsyncGenericHTWorkItemHandler) {
                ((AsyncGenericHTWorkItemHandler) handler).dispose();
            }
        }
        handlers.clear();
        server.stop();
        
        super.tearDown();
    }
    
    @Test
    public void testCompleteTaskMultipleSessionsSync() throws Exception {
        TestStatefulKnowledgeSession ksession = new TestStatefulKnowledgeSession();
        
        MinaHTWorkItemHandler handler = new MinaHTWorkItemHandler(ksession, true);
        handlers.add(handler);

        client = handler.getClient();
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
        
        TestStatefulKnowledgeSession ksession2 = new TestStatefulKnowledgeSession(10);
        MinaHTWorkItemHandler handler2 = new MinaHTWorkItemHandler("testConnector", null, ksession2, OnErrorAction.LOG);
        handlers.add(handler2);
        handler2.setOwningSessionOnly(true);

        TestWorkItemManager manager2 = new TestWorkItemManager();
        ksession2.setWorkItemManager(manager2);
        WorkItemImpl workItem2 = new WorkItemImpl();
        workItem2.setName("Human Task");
        workItem2.setParameter("TaskName", "TaskName");
        workItem2.setParameter("Comment", "Comment");
        workItem2.setParameter("Priority", "10");
        workItem2.setParameter("ActorId", "Darth Vader");
        workItem2.setProcessInstanceId(10);
        handler2.executeWorkItem(workItem2, manager2);

        
        List<TaskSummary> tasks = client.getTasksAssignedAsPotentialOwner("Darth Vader", "en-UK");
        assertEquals(2, tasks.size());
        TaskSummary task = tasks.get(0);
        // ensure we get first task
        if (task.getProcessSessionId() == 10) {
            task = tasks.get(1);
        }
        assertEquals("TaskName", task.getName());
        assertEquals(10, task.getPriority());
        assertEquals("Comment", task.getDescription());
        assertEquals(Status.Reserved, task.getStatus());
        assertEquals("Darth Vader", task.getActualOwner().getId());
        assertEquals(10, task.getProcessInstanceId());

        client.start(task.getId(), "Darth Vader");
        client.complete(task.getId(), "Darth Vader", null);
        
        Thread.sleep(1000);
        
        assertEquals(1, manager.getCompleteCounter());
        assertEquals(0, manager2.getCompleteCounter());
    }
    
    @Test
    public void testCompleteTaskMultipleSessionsASync() throws Exception {
        TestStatefulKnowledgeSession ksession = new TestStatefulKnowledgeSession();
        
        AsyncMinaHTWorkItemHandler handler = new AsyncMinaHTWorkItemHandler(ksession, true);
        handlers.add(handler);

        clientAsync = handler.getClient();
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
        
        TestStatefulKnowledgeSession ksession2 = new TestStatefulKnowledgeSession(10);
        AsyncMinaHTWorkItemHandler handler2 = new AsyncMinaHTWorkItemHandler("testConnector", null, ksession2, OnErrorAction.LOG);
        handler2.setOwningSessionOnly(true);
        handlers.add(handler2);

        TestWorkItemManager manager2 = new TestWorkItemManager();
        ksession2.setWorkItemManager(manager2);
        WorkItemImpl workItem2 = new WorkItemImpl();
        workItem2.setName("Human Task");
        workItem2.setParameter("TaskName", "TaskName");
        workItem2.setParameter("Comment", "Comment");
        workItem2.setParameter("Priority", "10");
        workItem2.setParameter("ActorId", "Darth Vader");
        workItem2.setProcessInstanceId(10);
        handler2.executeWorkItem(workItem2, manager2);

        Thread.sleep(1000);
        
        BlockingTaskSummaryResponseHandler reshanlder = new BlockingTaskSummaryResponseHandler();
        clientAsync.getTasksAssignedAsPotentialOwner("Darth Vader", "en-UK", reshanlder);
        List<TaskSummary> tasks = reshanlder.getResults();
        assertEquals(2, tasks.size());
        TaskSummary task = tasks.get(0);
        // ensure we get first task
        if (task.getProcessSessionId() == 10) {
            task = tasks.get(1);
        }
        assertEquals("TaskName", task.getName());
        assertEquals(10, task.getPriority());
        assertEquals("Comment", task.getDescription());
        assertEquals(Status.Reserved, task.getStatus());
        assertEquals("Darth Vader", task.getActualOwner().getId());
        assertEquals(10, task.getProcessInstanceId());


        BlockingTaskOperationResponseHandler resOpHandler = new BlockingTaskOperationResponseHandler();
        clientAsync.start(task.getId(), "Darth Vader", resOpHandler);
        resOpHandler.waitTillDone(5000);
        
        resOpHandler = new BlockingTaskOperationResponseHandler();
        clientAsync.complete(task.getId(), "Darth Vader", null, resOpHandler);
        resOpHandler.waitTillDone(5000);
        
        Thread.sleep(2000);

        assertEquals(1, manager.getCompleteCounter());
        assertEquals(0, manager2.getCompleteCounter());
        
        
    }
    
    private class TestWorkItemManager implements WorkItemManager {

        private volatile int completeCounter = 0;
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
            this.setCompleteCounter(this.getCompleteCounter() + 1);
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

        public int getCompleteCounter() {
            return completeCounter;
        }

        public void setCompleteCounter(int completeCounter) {
            this.completeCounter = completeCounter;
        }
    }
}
