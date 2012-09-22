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

import static org.jbpm.task.event.entity.TaskEventType.*;

import java.io.StringReader;
import java.util.HashMap;
import java.util.Map;

import org.jbpm.eventmessaging.EventKey;
import org.jbpm.eventmessaging.Payload;
import org.jbpm.task.*;
import org.jbpm.task.event.TaskEventKey;
import org.jbpm.task.event.entity.*;
import org.jbpm.task.service.Operation;
import org.jbpm.task.service.responsehandlers.BlockingEventResponseHandler;

public abstract class TaskServiceEventMessagingBaseSyncTest extends BaseTest {

    protected TaskService client;

    // One potential owner, should go straight to state Reserved
    private static final String taskExpression =  "(with (new Task()) { " +
    		"priority = 55, " +
    		"taskData = (with( new TaskData()) { } ), " +
            "peopleAssignments = (with ( new PeopleAssignments() ) { potentialOwners = [users['bobba' ], users['darth'] ], })," +
            "names = [ new I18NText( 'en-UK', 'This is my task name')] " +
            "})";
        
    public void testClaimEvent() throws Exception {      
        Map<String, Object> vars = fillVariables();

        Task task = ( Task )  eval( new StringReader( taskExpression ), vars );
        client.addTask( task, null );
        
        long taskId = task.getId();
        
        // A Task with multiple potential owners moves to "Ready" state until someone claims it.
        
        Task task1 = client.getTask( taskId );
        assertEquals( Status.Ready , task1.getTaskData().getStatus() );         
        
        EventKey key = new TaskEventKey(TaskClaimedEvent.class, taskId );           
        BlockingEventResponseHandler handler = new BlockingEventResponseHandler(); 
        client.registerForEvent( key, false, handler );
        
        taskSession.taskOperation( Operation.Claim, taskId, users.get( "darth" ).getId(), null, null, null );          
        
        Payload payload = handler.getPayload();
        TaskUserEvent event = ( TaskUserEvent ) payload.get();
        assertNotNull( event );        
    }
    
    public void testEvents() throws Exception {
        Map<String, Object>  vars = fillVariables();

        EventKey key = new TaskEventKey(TaskCreatedEvent.class, -1 );           
        BlockingEventResponseHandler handlerCreated = new BlockingEventResponseHandler(); 
        client.registerForEvent( key, false, handlerCreated );
       
        Task task = ( Task )  eval( new StringReader( taskExpression ), vars );
        client.addTask( task, null );
        
        long taskId = task.getId();
        
        Payload payload = handlerCreated.getPayload();
        TaskUserEvent event = ( TaskUserEvent ) payload.get();
        assertNotNull( event );   
        assertTrue(event instanceof TaskCreatedEvent);
        
        Map<TaskEventType, BlockingEventResponseHandler> handlers = registerForEvents(taskId);
        
        // A Task with multiple potential owners moves to "Ready" state until someone claims it.
        Task task1 = client.getTask( taskId );
        assertEquals( Status.Ready , task1.getTaskData().getStatus() );         
        taskSession.taskOperation( Operation.Claim, taskId, users.get( "darth" ).getId(), null, null, null );          
        
        payload = handlers.get(Claim).getPayload();
        event = ( TaskUserEvent ) payload.get();
        assertNotNull( event );   
        assertTrue(event instanceof TaskClaimedEvent);
        
        taskSession.taskOperation( Operation.Release, taskId, users.get( "darth" ).getId(), null, null, null );          
        
        payload = handlers.get(Release).getPayload();
        event = ( TaskUserEvent ) payload.get();
        assertNotNull( event ); 
        assertTrue(event instanceof TaskReleasedEvent);
        
        taskSession.taskOperation( Operation.Claim, taskId, users.get( "darth" ).getId(), null, null, null );          
        
        payload = handlers.get(Claim).getPayload();
        event = ( TaskUserEvent ) payload.get();
        assertNotNull( event );   
        assertTrue(event instanceof TaskClaimedEvent);
        
        taskSession.taskOperation( Operation.Forward, taskId, users.get( "darth" ).getId(), users.get( "salaboy" ).getId(), null, null );          

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
        
        payload = handlers.get(Stop).getPayload();
        event = ( TaskUserEvent ) payload.get();
        assertNotNull( event );   
        assertTrue(event instanceof TaskStoppedEvent);
    
        taskSession.taskOperation( Operation.Resume, taskId, users.get( "salaboy" ).getId(), null, null, null );          
        
        payload = handlers.get(Release).getPayload();
        event = ( TaskUserEvent ) payload.get();
        assertNotNull( event );   
        assertTrue(event instanceof TaskReleasedEvent);
    }
    
    public void testMoreEvents() throws Exception {
        Map<String, Object>  vars = fillVariables();

        EventKey key = new TaskEventKey(TaskCreatedEvent.class, -1 );           
        BlockingEventResponseHandler handlerCreated = new BlockingEventResponseHandler(); 
        client.registerForEvent( key, false, handlerCreated );
       
        Task task = ( Task )  eval( new StringReader( taskExpression ), vars );
        client.addTask( task, null );
        
        long taskId = task.getId();
        
        Payload payload = handlerCreated.getPayload();
        TaskUserEvent event = ( TaskUserEvent ) payload.get();
        assertNotNull( event );   
        assertTrue(event instanceof TaskCreatedEvent);
        
        Map<TaskEventType, BlockingEventResponseHandler> handlers = registerForEvents(taskId);
        
        // A Task with multiple potential owners moves to "Ready" state until someone claims it.
        Task task1 = client.getTask( taskId );
        assertEquals( Status.Ready , task1.getTaskData().getStatus() );         
        taskSession.taskOperation( Operation.Claim, taskId, users.get( "darth" ).getId(), null, null, null );          
        
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
        handlers.put(Forward, handler);
        
        key = new TaskEventKey(TaskReleasedEvent.class, taskId );           
        handler = new BlockingEventResponseHandler(); 
        client.registerForEvent( key, false, handler );
        handlers.put(Release, handler);
        
        key = new TaskEventKey(TaskStartedEvent.class, taskId );           
        handler = new BlockingEventResponseHandler(); 
        client.registerForEvent( key, false, handler );
        handlers.put(Started, handler);
        
        key = new TaskEventKey(TaskStoppedEvent.class, taskId );           
        handler = new BlockingEventResponseHandler(); 
        client.registerForEvent( key, false, handler );
        handlers.put(Stop, handler);
        
        key = new TaskEventKey(TaskClaimedEvent.class, taskId );           
        handler = new BlockingEventResponseHandler(); 
        client.registerForEvent( key, false, handler );
        handlers.put(Claim, handler);
        
        key = new TaskEventKey(TaskCompletedEvent.class, taskId );           
        handler = new BlockingEventResponseHandler(); 
        client.registerForEvent( key, false, handler );
        handlers.put(Complete, handler);
        
        return handlers;
    }
}
