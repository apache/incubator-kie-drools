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
package org.jbpm.task.service.persistence;

import java.io.StringReader;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import javax.persistence.EntityManagerFactory;
import javax.persistence.Persistence;
import org.jbpm.eventmessaging.EventKey;
import org.jbpm.eventmessaging.Payload;
import org.jbpm.task.BaseTest;
import org.jbpm.task.Status;
import org.jbpm.task.Task;
import org.jbpm.task.TaskService;
import org.jbpm.task.event.TaskClaimedEvent;
import org.jbpm.task.event.TaskCreatedEvent;
import org.jbpm.task.event.TaskEvent;
import org.jbpm.task.event.TaskEventKey;
import org.jbpm.task.event.TaskEventsAdmin;
import org.jbpm.task.event.TaskForwardedEvent;
import org.jbpm.task.event.TaskReleasedEvent;
import org.jbpm.task.event.TaskStartedEvent;
import org.jbpm.task.event.TaskStoppedEvent;
import org.jbpm.task.event.TaskUserEvent;
import org.jbpm.task.service.Operation;
import org.jbpm.task.service.local.LocalTaskService;
import org.jbpm.task.service.responsehandlers.BlockingEventResponseHandler;
import org.junit.After;
import org.junit.AfterClass;
import org.junit.Before;
import org.junit.BeforeClass;
import static org.junit.Assert.*;

public class EventPersistenceTest extends BaseTest {
    protected TaskService client;
    
    @Override
    protected EntityManagerFactory createEntityManagerFactory() {
        return Persistence.createEntityManagerFactory("org.jbpm.task.events");
    }

    
    
    public EventPersistenceTest() {
    }

    @BeforeClass
    public static void setUpClass() {
    }

    @AfterClass
    public static void tearDownClass() {
    }

    @Before
    public void setUp() throws Exception {
        super.setUp();
        client = new LocalTaskService(taskService);
    }

    @After
    public void tearDown() {
    }

   public void testPersistentEventHandlers() throws Exception {      
        TaskEventsAdmin eventsAdmin = taskService.createTaskEventsAdmin();
        Map  vars = new HashMap();     
        vars.put( "users", users );
        vars.put( "groups", groups );        
        vars.put( "now", new Date() );                

        
        // One potential owner, should go straight to state Reserved
        String str = "(with (new Task()) { priority = 55, taskData = (with( new TaskData()) { } ), ";
        str += "peopleAssignments = (with ( new PeopleAssignments() ) { potentialOwners = [users['bobba' ], users['darth'] ], }),";                        
        str += "names = [ new I18NText( 'en-UK', 'This is my task name')] })";
         
   
        
        EventKey key = new TaskEventKey(TaskCreatedEvent.class, -1 );           
        BlockingEventResponseHandler handlerCreatedLog = new BlockingEventResponseHandler(eventsAdmin); 
        client.registerForEvent( key, false, handlerCreatedLog );
        
        Task task = ( Task )  eval( new StringReader( str ), vars );
        client.addTask( task, null );
        
        long taskId = task.getId();
        
        Payload payload = handlerCreatedLog.getPayload();
        TaskUserEvent event = ( TaskUserEvent ) payload.get();
        assertNotNull( event );   
        assertTrue(event instanceof TaskCreatedEvent);
        
        key = new TaskEventKey(TaskClaimedEvent.class, taskId );           
        BlockingEventResponseHandler handlerClaimed = new BlockingEventResponseHandler(eventsAdmin); 
        client.registerForEvent( key, false, handlerClaimed );
        
        // A Task with multiple potential owners moves to "Ready" state until someone claims it.
        
        
        Task task1 = client.getTask( taskId );
        assertEquals( Status.Ready , task1.getTaskData().getStatus() );         

        
        taskSession.taskOperation( Operation.Claim, taskId, users.get( "darth" ).getId(), null, null, null );          
        List<TaskEvent> eventsByTaskId = eventsAdmin.getEventsByTaskId(taskId);
        
        assertEquals(2, eventsByTaskId.size());
        
    }
   
   public void testMultiPersistentEvents() throws Exception {
       
        TaskEventsAdmin eventsAdmin = taskService.createTaskEventsAdmin();
        Map  vars = new HashMap();     
        vars.put( "users", users );
        vars.put( "groups", groups );        
        vars.put( "now", new Date() );                

        // One potential owner, should go straight to state Reserved
        String str = "(with (new Task()) { priority = 55, taskData = (with( new TaskData()) { } ), ";
        str += "peopleAssignments = (with ( new PeopleAssignments() ) { potentialOwners = [users['bobba' ], users['darth'] ], }),";                        
        str += "names = [ new I18NText( 'en-UK', 'This is my task name')] })";
            
        EventKey key = new TaskEventKey(TaskCreatedEvent.class, -1 );           
        BlockingEventResponseHandler handlerCreated = new BlockingEventResponseHandler(eventsAdmin); 
        client.registerForEvent( key, false, handlerCreated );
        
       
        Task task = ( Task )  eval( new StringReader( str ), vars );
        client.addTask( task, null );
        
        long taskId = task.getId();
        
        Payload payload = handlerCreated.getPayload();
        TaskUserEvent event = ( TaskUserEvent ) payload.get();
        assertNotNull( event );   
        assertTrue(event instanceof TaskCreatedEvent);
        
        key = new TaskEventKey(TaskForwardedEvent.class, taskId );           
        BlockingEventResponseHandler handlerFW = new BlockingEventResponseHandler(eventsAdmin); 
        client.registerForEvent( key, false, handlerFW );
        
        
        key = new TaskEventKey(TaskReleasedEvent.class, taskId );           
        BlockingEventResponseHandler handlerReleased = new BlockingEventResponseHandler(eventsAdmin); 
        client.registerForEvent( key, false, handlerReleased );
        
        
        key = new TaskEventKey(TaskStartedEvent.class, taskId );           
        BlockingEventResponseHandler handlerStarted = new BlockingEventResponseHandler(eventsAdmin); 
        client.registerForEvent( key, false, handlerStarted );
        
        
        key = new TaskEventKey(TaskStoppedEvent.class, taskId );           
        BlockingEventResponseHandler handlerStopped = new BlockingEventResponseHandler(eventsAdmin); 
        client.registerForEvent( key, false, handlerStopped );
        
        
        key = new TaskEventKey(TaskClaimedEvent.class, taskId );           
        BlockingEventResponseHandler handlerClaimed = new BlockingEventResponseHandler(eventsAdmin); 
        client.registerForEvent( key, false, handlerClaimed );
        
        
        
        // A Task with multiple potential owners moves to "Ready" state until someone claims it.
        
        
        Task task1 = client.getTask( taskId );
        assertEquals( Status.Ready , task1.getTaskData().getStatus() );         

        
        taskSession.taskOperation( Operation.Claim, taskId, users.get( "darth" ).getId(), null, null, null );          
        
        payload = handlerClaimed.getPayload();
        event = ( TaskUserEvent ) payload.get();
        assertNotNull( event );   
        assertTrue(event instanceof TaskClaimedEvent);
        
        taskSession.taskOperation( Operation.Release, taskId, users.get( "darth" ).getId(), null, null, null );          
        
        payload = handlerReleased.getPayload();
        event = ( TaskUserEvent ) payload.get();
        assertNotNull( event ); 
        assertTrue(event instanceof TaskReleasedEvent);
        
        
        taskSession.taskOperation( Operation.Claim, taskId, users.get( "darth" ).getId(), null, null, null );          
        
        payload = handlerClaimed.getPayload();
        event = ( TaskUserEvent ) payload.get();
        assertNotNull( event );   
        assertTrue(event instanceof TaskClaimedEvent);
        
        taskSession.taskOperation( Operation.Forward, taskId, users.get( "darth" ).getId(), users.get( "salaboy" ).getId(), null, null );          

        payload = handlerFW.getPayload();
        event = ( TaskUserEvent ) payload.get();
        assertNotNull( event );   
        assertTrue(event instanceof TaskForwardedEvent);
        
        taskSession.taskOperation( Operation.Start, taskId, users.get( "salaboy" ).getId(), null, null, null );          
        
        payload = handlerStarted.getPayload();
        event = ( TaskUserEvent ) payload.get();
        assertNotNull( event );   
        assertTrue(event instanceof TaskStartedEvent);
        
        
        taskSession.taskOperation( Operation.Stop, taskId, users.get( "salaboy" ).getId(), null, null, null );          
        
        
        payload = handlerStopped.getPayload();
        event = ( TaskUserEvent ) payload.get();
        assertNotNull( event );   
        assertTrue(event instanceof TaskStoppedEvent);
        
        List<TaskEvent> eventsByTaskId = eventsAdmin.getEventsByTaskId(taskId);
        
        assertEquals(7, eventsByTaskId.size());
    
        List<TaskEvent> eventsByTypeByTaskId = eventsAdmin.getEventsByTypeByTaskId(taskId, "TaskClaimedEvent");
        
        assertEquals(2, eventsByTypeByTaskId.size());   
        List<TaskEvent> taskClaimedEventsByTaskId = eventsAdmin.getTaskClaimedEventsByTaskId(taskId);
        
        
        assertEquals(2, taskClaimedEventsByTaskId.size()); 
    }
}
