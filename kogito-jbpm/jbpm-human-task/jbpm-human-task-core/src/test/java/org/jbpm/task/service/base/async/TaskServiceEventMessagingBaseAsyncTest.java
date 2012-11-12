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

import static org.jbpm.task.event.entity.TaskEventType.Claim;
import static org.jbpm.task.event.entity.TaskEventType.Complete;
import static org.jbpm.task.event.entity.TaskEventType.Forward;
import static org.jbpm.task.event.entity.TaskEventType.Release;
import static org.jbpm.task.event.entity.TaskEventType.Started;
import static org.jbpm.task.event.entity.TaskEventType.Stop;

import java.io.StringReader;
import java.util.HashMap;
import java.util.Map;

import org.jbpm.eventmessaging.EventKey;
import org.jbpm.eventmessaging.Payload;
import org.jbpm.task.AsyncTaskService;
import org.jbpm.task.BaseTest;
import org.jbpm.task.Status;
import org.jbpm.task.Task;
import org.jbpm.task.event.TaskEventKey;
import org.jbpm.task.event.entity.TaskClaimedEvent;
import org.jbpm.task.event.entity.TaskCompletedEvent;
import org.jbpm.task.event.entity.TaskCreatedEvent;
import org.jbpm.task.event.entity.TaskEventType;
import org.jbpm.task.event.entity.TaskForwardedEvent;
import org.jbpm.task.event.entity.TaskReleasedEvent;
import org.jbpm.task.event.entity.TaskStartedEvent;
import org.jbpm.task.event.entity.TaskStoppedEvent;
import org.jbpm.task.event.entity.TaskUserEvent;
import org.jbpm.task.service.Operation;
import org.jbpm.task.service.TaskServer;
import org.jbpm.task.service.responsehandlers.BlockingAddTaskResponseHandler;
import org.jbpm.task.service.responsehandlers.BlockingEventResponseHandler;
import org.jbpm.task.service.responsehandlers.BlockingGetTaskResponseHandler;

public abstract class TaskServiceEventMessagingBaseAsyncTest extends BaseTest {

    protected TaskServer server;
    protected AsyncTaskService client;

    protected void tearDown() throws Exception {
        client.disconnect();
        server.stop();
        super.tearDown();
    }    
    
    // One potential owner, should go straight to state Reserved
    private static final String taskExpression =  "(with (new Task()) { " +
        "priority = 55, " +
        "taskData = (with( new TaskData()) { } ), " +
        "peopleAssignments = (with ( new PeopleAssignments() ) { potentialOwners = [users['bobba' ], users['darth'] ], })," +
        "names = [ new I18NText( 'en-UK', 'This is my task name')] " +
        "})";

    public void testClaimEvent() throws Exception {      
        Map<String, Object>  vars = fillVariables();     

        BlockingAddTaskResponseHandler addTaskResponseHandler = new BlockingAddTaskResponseHandler();
        Task task = ( Task )  eval( new StringReader( taskExpression ), vars );
        client.addTask( task, null, addTaskResponseHandler );
        
        long taskId = addTaskResponseHandler.getTaskId();
        
        // A Task with multiple potential owners moves to "Ready" state until someone claims it.
        BlockingGetTaskResponseHandler getTaskResponseHandler = new BlockingGetTaskResponseHandler(); 
        client.getTask( taskId, getTaskResponseHandler );
        Task task1 = getTaskResponseHandler.getTask();
        assertEquals( Status.Ready , task1.getTaskData().getStatus() );         
        
        EventKey key = new TaskEventKey(TaskClaimedEvent.class, taskId );           
        BlockingEventResponseHandler handler = new BlockingEventResponseHandler(); 
        client.registerForEvent( key, false, handler );
        Thread.sleep( 3000 );
        
        taskSession.taskOperation( Operation.Claim, taskId, users.get( "darth" ).getId(), null, null, null );          
        handler.waitTillDone( 5000 );
        
        Payload payload = handler.getPayload();
        TaskUserEvent event = ( TaskUserEvent ) payload.get();
        assertNotNull( event );        
    }
    
    public void testEvents() throws Exception {      
        Map<String, Object>  vars = fillVariables();            

        EventKey key = new TaskEventKey(TaskCreatedEvent.class, -1 );           
        BlockingEventResponseHandler handlerCreated = new BlockingEventResponseHandler(); 
        client.registerForEvent( key, false, handlerCreated );
        Thread.sleep( 3000 );
        
        BlockingAddTaskResponseHandler addTaskResponseHandler = new BlockingAddTaskResponseHandler();
        Task task = ( Task )  eval( new StringReader( taskExpression ), vars );
        client.addTask( task, null, addTaskResponseHandler );
        
        long taskId = addTaskResponseHandler.getTaskId();
        
        Payload payload = handlerCreated.getPayload();
        TaskUserEvent event = ( TaskUserEvent ) payload.get();
        assertNotNull( event );   
        assertTrue(event instanceof TaskCreatedEvent);
        
        // A Task with multiple potential owners moves to "Ready" state until someone claims it.
        BlockingGetTaskResponseHandler getTaskResponseHandler = new BlockingGetTaskResponseHandler(); 
        client.getTask( taskId, getTaskResponseHandler );
        Task task1 = getTaskResponseHandler.getTask();
        assertEquals( Status.Ready , task1.getTaskData().getStatus() );         
        
        Map<TaskEventType, BlockingEventResponseHandler> handlers = registerForEvents(taskId);
        
        taskSession.taskOperation( Operation.Claim, taskId, users.get( "darth" ).getId(), null, null, null );          
        handlers.get(Claim).waitTillDone( 5000 );
        
        payload = handlers.get(Claim).getPayload();
        event = ( TaskUserEvent ) payload.get();
        assertNotNull( event );   
        assertTrue(event instanceof TaskClaimedEvent);
        
        taskSession.taskOperation( Operation.Release, taskId, users.get( "darth" ).getId(), null, null, null );          
        handlers.get(Release).waitTillDone( 5000 );
        
        payload = handlers.get(Release).getPayload();
        event = ( TaskUserEvent ) payload.get();
        assertNotNull( event ); 
        assertTrue(event instanceof TaskReleasedEvent);
        
        taskSession.taskOperation( Operation.Claim, taskId, users.get( "darth" ).getId(), null, null, null );          
        handlers.get(Claim).waitTillDone( 5000 );
        
        payload = handlers.get(Claim).getPayload();
        event = ( TaskUserEvent ) payload.get();
        assertNotNull( event );   
        assertTrue(event instanceof TaskClaimedEvent);
        
        taskSession.taskOperation( Operation.Forward, taskId, users.get( "darth" ).getId(), users.get( "salaboy" ).getId(), null, null );          
        handlers.get(Forward).waitTillDone( 5000 );
        
        payload = handlers.get(Forward).getPayload();
        event = ( TaskUserEvent ) payload.get();
        assertNotNull( event );   
        assertTrue(event instanceof TaskForwardedEvent);
        
        taskSession.taskOperation( Operation.Start, taskId, users.get( "salaboy" ).getId(), null, null, null );          
        handlers.get(Started).waitTillDone( 5000 );
        
        payload = handlers.get(Started).getPayload();
        event = ( TaskUserEvent ) payload.get();
        assertNotNull( event );   
        assertTrue(event instanceof TaskStartedEvent);
        
        taskSession.taskOperation( Operation.Stop, taskId, users.get( "salaboy" ).getId(), null, null, null );          
        handlers.get(Stop).waitTillDone( 5000 );
        
        payload = handlers.get(Stop).getPayload();
        event = ( TaskUserEvent ) payload.get();
        assertNotNull( event );   
        assertTrue(event instanceof TaskStoppedEvent);
    }
    
    public void testMoreEvents() throws Exception {      
        Map<String, Object>  vars = fillVariables();            

        // One potential owner, should go straight to state Reserved
        String str = "(with (new Task()) { priority = 55, taskData = (with( new TaskData()) { } ), ";
        str += "peopleAssignments = (with ( new PeopleAssignments() ) { potentialOwners = [users['bobba' ], users['darth'] ], }),";                        
        str += "names = [ new I18NText( 'en-UK', 'This is my task name')] })";
            
        EventKey key = new TaskEventKey(TaskCreatedEvent.class, -1 );           
        BlockingEventResponseHandler handlerCreated = new BlockingEventResponseHandler(); 
        client.registerForEvent( key, false, handlerCreated );
        Thread.sleep( 3000 );
        
        BlockingAddTaskResponseHandler addTaskResponseHandler = new BlockingAddTaskResponseHandler();
        Task task = ( Task )  eval( new StringReader( str ), vars );
        client.addTask( task, null, addTaskResponseHandler );
        
        long taskId = addTaskResponseHandler.getTaskId();
        
        Payload payload = handlerCreated.getPayload();
        TaskUserEvent event = ( TaskUserEvent ) payload.get();
        assertNotNull( event );   
        assertTrue(event instanceof TaskCreatedEvent);
        
        // A Task with multiple potential owners moves to "Ready" state until someone claims it.
        BlockingGetTaskResponseHandler getTaskResponseHandler = new BlockingGetTaskResponseHandler(); 
        client.getTask( taskId, getTaskResponseHandler );
        Task task1 = getTaskResponseHandler.getTask();
        assertEquals( Status.Ready , task1.getTaskData().getStatus() );         
        
        Map<TaskEventType, BlockingEventResponseHandler> handlers = registerForEvents(taskId);
        
        taskSession.taskOperation( Operation.Claim, taskId, users.get( "darth" ).getId(), null, null, null );          
        handlers.get(Claim).waitTillDone( 5000 );
        
        payload = handlers.get(Claim).getPayload();
        event = ( TaskUserEvent ) payload.get();
        assertNotNull( event );   
        assertTrue(event instanceof TaskClaimedEvent);
        
        taskSession.taskOperation( Operation.Start, taskId, users.get( "darth" ).getId(), null, null, null );          
        handlers.get(Started).waitTillDone( 5000 );
        
        payload = handlers.get(Started).getPayload();
        event = ( TaskUserEvent ) payload.get();
        assertNotNull( event );   
        assertTrue(event instanceof TaskStartedEvent);
        
        taskSession.taskOperation( Operation.Complete, taskId, users.get( "darth" ).getId(), null, null, null );          
        handlers.get(Complete).waitTillDone( 5000 );
        
        payload = handlers.get(Complete).getPayload();
        event = ( TaskUserEvent ) payload.get();
        assertNotNull( event );   
        assertTrue(event instanceof TaskCompletedEvent);
    }
    
    private Map<TaskEventType,BlockingEventResponseHandler> registerForEvents(long taskId) throws Exception { 
        HashMap<TaskEventType, BlockingEventResponseHandler> handlers = new HashMap<TaskEventType, BlockingEventResponseHandler>();
        
        TaskEventKey key = new TaskEventKey(TaskForwardedEvent.class, taskId );           
        BlockingEventResponseHandler handler = new BlockingEventResponseHandler(); 
        client.registerForEvent( key, false, handler );
        Thread.sleep( 3000 );
        handlers.put(Forward, handler);
        
        key = new TaskEventKey(TaskReleasedEvent.class, taskId );           
        handler = new BlockingEventResponseHandler(); 
        client.registerForEvent( key, false, handler );
        Thread.sleep( 3000 );
        handlers.put(Release, handler);
        
        key = new TaskEventKey(TaskStartedEvent.class, taskId );           
        handler = new BlockingEventResponseHandler(); 
        client.registerForEvent( key, false, handler );
        Thread.sleep( 3000 );
        handlers.put(Started, handler);
        
        key = new TaskEventKey(TaskStoppedEvent.class, taskId );           
        handler = new BlockingEventResponseHandler(); 
        client.registerForEvent( key, false, handler );
        Thread.sleep( 3000 );
        handlers.put(Stop, handler);
        
        key = new TaskEventKey(TaskClaimedEvent.class, taskId );           
        handler = new BlockingEventResponseHandler(); 
        client.registerForEvent( key, false, handler );
        Thread.sleep( 3000 );
        handlers.put(Claim, handler);
        
        key = new TaskEventKey(TaskCompletedEvent.class, taskId );           
        handler = new BlockingEventResponseHandler(); 
        client.registerForEvent( key, false, handler );
        Thread.sleep( 3000 );
        handlers.put(Complete, handler);
        
        return handlers;
    }

}
