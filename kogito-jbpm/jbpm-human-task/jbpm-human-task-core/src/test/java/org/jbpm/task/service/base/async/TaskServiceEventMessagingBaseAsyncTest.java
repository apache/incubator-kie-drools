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
import java.util.Date;
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
import org.jbpm.task.event.entity.TaskCreatedEvent;
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

    public void testClaimEvent() throws Exception {      
        Map  vars = new HashMap();     
        vars.put( "users", users );
        vars.put( "groups", groups );        
        vars.put( "now", new Date() );                

        // One potential owner, should go straight to state Reserved
        String str = "(with (new Task()) { priority = 55, taskData = (with( new TaskData()) { } ), ";
        str += "peopleAssignments = (with ( new PeopleAssignments() ) { potentialOwners = [users['bobba' ], users['darth'] ], }),";                        
        str += "names = [ new I18NText( 'en-UK', 'This is my task name')] })";
            
        BlockingAddTaskResponseHandler addTaskResponseHandler = new BlockingAddTaskResponseHandler();
        Task task = ( Task )  eval( new StringReader( str ), vars );
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
        Map  vars = new HashMap();     
        vars.put( "users", users );
        vars.put( "groups", groups );        
        vars.put( "now", new Date() );                

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
        
        key = new TaskEventKey(TaskForwardedEvent.class, taskId );           
        BlockingEventResponseHandler handlerFW = new BlockingEventResponseHandler(); 
        client.registerForEvent( key, false, handlerFW );
        Thread.sleep( 3000 );
        
        key = new TaskEventKey(TaskReleasedEvent.class, taskId );           
        BlockingEventResponseHandler handlerReleased = new BlockingEventResponseHandler(); 
        client.registerForEvent( key, false, handlerReleased );
        Thread.sleep( 3000 );
        
        key = new TaskEventKey(TaskStartedEvent.class, taskId );           
        BlockingEventResponseHandler handlerStarted = new BlockingEventResponseHandler(); 
        client.registerForEvent( key, false, handlerStarted );
        Thread.sleep( 3000 );
        
        key = new TaskEventKey(TaskStoppedEvent.class, taskId );           
        BlockingEventResponseHandler handlerStopped = new BlockingEventResponseHandler(); 
        client.registerForEvent( key, false, handlerStopped );
        Thread.sleep( 3000 );
        
        key = new TaskEventKey(TaskClaimedEvent.class, taskId );           
        BlockingEventResponseHandler handlerClaimed = new BlockingEventResponseHandler(); 
        client.registerForEvent( key, false, handlerClaimed );
        Thread.sleep( 3000 );
        
        
        // A Task with multiple potential owners moves to "Ready" state until someone claims it.
        BlockingGetTaskResponseHandler getTaskResponseHandler = new BlockingGetTaskResponseHandler(); 
        client.getTask( taskId, getTaskResponseHandler );
        Task task1 = getTaskResponseHandler.getTask();
        assertEquals( Status.Ready , task1.getTaskData().getStatus() );         
        
        
        
        taskSession.taskOperation( Operation.Claim, taskId, users.get( "darth" ).getId(), null, null, null );          
        handlerClaimed.waitTillDone( 5000 );
        
        payload = handlerClaimed.getPayload();
        event = ( TaskUserEvent ) payload.get();
        assertNotNull( event );   
        assertTrue(event instanceof TaskClaimedEvent);
        
        taskSession.taskOperation( Operation.Release, taskId, users.get( "darth" ).getId(), null, null, null );          
        handlerReleased.waitTillDone( 5000 );
        
        payload = handlerReleased.getPayload();
        event = ( TaskUserEvent ) payload.get();
        assertNotNull( event ); 
        assertTrue(event instanceof TaskReleasedEvent);
        
        
        taskSession.taskOperation( Operation.Claim, taskId, users.get( "darth" ).getId(), null, null, null );          
        handlerClaimed.waitTillDone( 5000 );
        
        payload = handlerClaimed.getPayload();
        event = ( TaskUserEvent ) payload.get();
        assertNotNull( event );   
        assertTrue(event instanceof TaskClaimedEvent);
        
        
        
        taskSession.taskOperation( Operation.Forward, taskId, users.get( "darth" ).getId(), users.get( "salaboy" ).getId(), null, null );          
        handlerClaimed.waitTillDone( 5000 );
        
        payload = handlerFW.getPayload();
        event = ( TaskUserEvent ) payload.get();
        assertNotNull( event );   
        assertTrue(event instanceof TaskForwardedEvent);
        
        taskSession.taskOperation( Operation.Start, taskId, users.get( "salaboy" ).getId(), null, null, null );          
        handlerStarted.waitTillDone( 5000 );
        
        payload = handlerStarted.getPayload();
        event = ( TaskUserEvent ) payload.get();
        assertNotNull( event );   
        assertTrue(event instanceof TaskStartedEvent);
        
        
        taskSession.taskOperation( Operation.Stop, taskId, users.get( "salaboy" ).getId(), null, null, null );          
        handlerClaimed.waitTillDone( 5000 );
        
        payload = handlerStopped.getPayload();
        event = ( TaskUserEvent ) payload.get();
        assertNotNull( event );   
        assertTrue(event instanceof TaskStoppedEvent);
    
    }

}
