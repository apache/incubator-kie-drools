/*
 * Copyright 2012 JBoss Inc
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
package org.jbpm.process.workitem.wsht.hornetq;

import java.io.StringReader;
import java.util.Map;

import org.jbpm.process.workitem.wsht.hornetq.AsyncHornetQHTWorkItemHandler;
import org.jbpm.task.AsyncTaskService;
import org.jbpm.task.BaseTest;
import org.jbpm.task.Status;
import org.jbpm.task.Task;
import org.jbpm.task.TestStatefulKnowledgeSession;
import org.jbpm.task.service.TaskServer;
import org.jbpm.task.service.hornetq.HornetQTaskServer;
import org.jbpm.task.service.responsehandlers.BlockingAddTaskResponseHandler;
import org.jbpm.task.service.responsehandlers.BlockingGetTaskResponseHandler;

public class HornetQErrorRecoveryTest extends BaseTest {

	private TaskServer server;
	private AsyncHornetQHTWorkItemHandler handler;
	private AsyncTaskService client;
	private Thread thread;

    @Override
    protected void setUp() throws Exception {
        super.setUp();
        server = new HornetQTaskServer(taskService, 5153);
        System.out.println("Waiting for the HornetQTask Server to come up");
        try {
            startTaskServerThread(server, false);
        } catch (Exception e) {
            startTaskServerThread(server, true);
        }
        handler = new AsyncHornetQHTWorkItemHandler(new TestStatefulKnowledgeSession());
        client = handler.getClient();
        client.connect();

    }

    protected void tearDown() throws Exception {
    	handler.dispose();
        client.disconnect();
        server.stop();
        super.tearDown();
    }
    
    public void testRecoveryOfTaskServer() {
    	Map<String, Object> vars = fillVariables(users, groups);
        
        // One potential owner, should go straight to state Reserved
        String str = "(with (new Task()) { priority = 55, taskData = (with( new TaskData()) { } ), ";
        str += "peopleAssignments = (with ( new PeopleAssignments() ) { }),";                        
        str += "names = [ new I18NText( 'en-UK', 'This is my task name')] })";
            
        BlockingAddTaskResponseHandler addTaskResponseHandler = new BlockingAddTaskResponseHandler();
        Task task = ( Task )  eval( new StringReader( str ), vars );
        client.addTask( task, null, addTaskResponseHandler );
        
        long taskId = addTaskResponseHandler.getTaskId();
        
        // Task should remain in Created state with no actual owner
        BlockingGetTaskResponseHandler getTaskResponseHandler = new BlockingGetTaskResponseHandler(); 
        client.getTask( taskId, getTaskResponseHandler );
        Task task1 = getTaskResponseHandler.getTask();
        assertEquals( task1.getTaskData().getStatus(), Status.Created );     
        assertNull( task1.getTaskData().getActualOwner() );    
        
        // querying for not existing task which will generate error on the task server and not return any data
        getTaskResponseHandler = new BlockingGetTaskResponseHandler();
        client.getTaskByWorkItemId(1000, getTaskResponseHandler);
        try {
	        task1 = getTaskResponseHandler.getTask();
	        fail("There is no such task, should fail");
        } catch (Exception e) {
			assertTrue(e.getMessage().indexOf("QueryTaskByWorkItemId") != -1);
		}
        // after failure in the previous step hornetq should still accept incoming requests
        getTaskResponseHandler = new BlockingGetTaskResponseHandler(); 
        client.getTask( taskId, getTaskResponseHandler );
        task1 = getTaskResponseHandler.getTask();
        assertEquals( task1.getTaskData().getStatus(), Status.Created );     
        assertNull( task1.getTaskData().getActualOwner() );
    }
    
    public void testRecoveryOfLostConnectionToTaskServer() throws Exception {
    	Map<String, Object> vars = fillVariables(users, groups);
        
        // One potential owner, should go straight to state Reserved
        String str = "(with (new Task()) { priority = 55, taskData = (with( new TaskData()) { } ), ";
        str += "peopleAssignments = (with ( new PeopleAssignments() ) { }),";                        
        str += "names = [ new I18NText( 'en-UK', 'This is my task name')] })";
            
        BlockingAddTaskResponseHandler addTaskResponseHandler = new BlockingAddTaskResponseHandler();
        Task task = ( Task )  eval( new StringReader( str ), vars );
        client.addTask( task, null, addTaskResponseHandler );
        
        long taskId = addTaskResponseHandler.getTaskId();
        
        // Task should remain in Created state with no actual owner
        BlockingGetTaskResponseHandler getTaskResponseHandler = new BlockingGetTaskResponseHandler(); 
        client.getTask( taskId, getTaskResponseHandler );
        Task task1 = getTaskResponseHandler.getTask();
        assertEquals( task1.getTaskData().getStatus(), Status.Created );     
        assertNull( task1.getTaskData().getActualOwner() );    
        
        server.stop();
        
        server = new HornetQTaskServer(taskService, 5153);
        System.out.println("Waiting for the HornetQTask Server to come up");
        try {
            startTaskServerThread(server, false);
        } catch (Exception e) {
            startTaskServerThread(server, true);
        }
        
        try {
	        getTaskResponseHandler = new BlockingGetTaskResponseHandler(); 
	        client.getTask( taskId, getTaskResponseHandler );
	        task1 = getTaskResponseHandler.getTask();
	        assertEquals( task1.getTaskData().getStatus(), Status.Created );     
	        assertNull( task1.getTaskData().getActualOwner() );
        
        } catch (Exception e) {
			fail("Should not happen " + e.getMessage());
		}
    }
}
