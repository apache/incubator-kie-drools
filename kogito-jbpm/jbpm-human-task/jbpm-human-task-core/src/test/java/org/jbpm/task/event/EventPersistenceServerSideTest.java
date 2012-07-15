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
package org.jbpm.task.event;

import java.io.StringReader;
import java.util.List;
import java.util.Map;

import org.jbpm.task.BaseTest;
import org.jbpm.task.Group;
import org.jbpm.task.Status;
import org.jbpm.task.Task;
import org.jbpm.task.TaskService;
import org.jbpm.task.User;
import org.jbpm.task.event.entity.TaskEvent;
import org.jbpm.task.event.entity.TaskEventType;
import org.jbpm.task.service.Operation;
import org.jbpm.task.service.TaskServiceSession;
import org.jbpm.task.service.local.LocalTaskService;

public class EventPersistenceServerSideTest extends BaseTest {
    
    protected TaskService client;
    protected TaskEventsAdmin eventsAdmin;
    
    public void setUp() throws Exception {
        super.setUp();
        client = new LocalTaskService(taskService);
        eventsAdmin = taskService.createTaskEventsAdmin();
        // We can register an internal persistent listener to the Local Task Service
        ((LocalTaskService)client).addEventListener(new InternalPersistentTaskEventListener(eventsAdmin));
    }

    public void tearDown() throws Exception {
        client.disconnect();
    }
    
    public void testPersistentEventHandlers() { 
        doTestPersistentEventHandlers(users, groups, client, taskSession, eventsAdmin);
    }

    public void testMultiplePersistentEvents() { 
        doTestMultiplePersistentEvents(users, groups, client, taskSession, eventsAdmin);
    }

   public static void doTestPersistentEventHandlers(Map<String, User> users, Map<String, Group> groups, 
           TaskService client, TaskServiceSession taskSession, TaskEventsAdmin eventsAdmin) { 
       
       Map<String, Object> vars = BaseTest.fillVariables(users, groups);

        // One potential owner, should go straight to state Reserved
        String str = "(with (new Task()) { priority = 55, taskData = (with( new TaskData()) { } ), ";
        str += "peopleAssignments = (with ( new PeopleAssignments() ) { potentialOwners = [users['bobba' ], users['darth'] ], }),";                        
        str += "names = [ new I18NText( 'en-UK', 'This is my task name')] })";
        
        Task task = ( Task )  eval( new StringReader( str ), vars );
        client.addTask( task, null );
        
        long taskId = task.getId();

        // A Task with multiple potential owners moves to "Ready" state until someone claims it.
        Task task1 = client.getTask( taskId );
        assertEquals( Status.Ready , task1.getTaskData().getStatus() );         
        
        taskSession.taskOperation( Operation.Claim, taskId, users.get( "darth" ).getId(), null, null, null );          
        
        List<TaskEvent> eventsByTaskId = eventsAdmin.getEventsByTaskId(taskId);
        assertEquals(2, eventsByTaskId.size());
        
    }
   
   public static void doTestMultiplePersistentEvents(Map<String, User> users, Map<String, Group> groups, 
           TaskService client, TaskServiceSession taskSession, TaskEventsAdmin eventsAdmin) { 
       
       Map<String, Object> vars = fillVariables(users, groups);

        // One potential owner, should go straight to state Reserved
        String str = "(with (new Task()) { priority = 55, taskData = (with( new TaskData()) { } ), ";
        str += "peopleAssignments = (with ( new PeopleAssignments() ) { potentialOwners = [users['bobba' ], users['darth'] ], }),";                        
        str += "names = [ new I18NText( 'en-UK', 'This is my task name')] })";
            
        Task task = ( Task )  eval( new StringReader( str ), vars );
        client.addTask( task, null );
        
        long taskId = task.getId();

        // A Task with multiple potential owners moves to "Ready" state until someone claims it.
        
        Task task1 = client.getTask( taskId );
        assertEquals( Status.Ready , task1.getTaskData().getStatus() );         

        taskSession.taskOperation( Operation.Claim, taskId, users.get( "darth" ).getId(), null, null, null );          
      
        taskSession.taskOperation( Operation.Release, taskId, users.get( "darth" ).getId(), null, null, null );          
        
        taskSession.taskOperation( Operation.Claim, taskId, users.get( "darth" ).getId(), null, null, null );          
        
        taskSession.taskOperation( Operation.Forward, taskId, users.get( "darth" ).getId(), users.get( "salaboy" ).getId(), null, null );          
        
        taskSession.taskOperation( Operation.Start, taskId, users.get( "salaboy" ).getId(), null, null, null );          
        
        taskSession.taskOperation( Operation.Stop, taskId, users.get( "salaboy" ).getId(), null, null, null );          

        List<TaskEvent> eventsByTaskId = eventsAdmin.getEventsByTaskId(taskId);
        assertEquals(7, eventsByTaskId.size());
        
        List<TaskEvent> eventsByTypeByTaskId = eventsAdmin.getEventsByTypeByTaskId(taskId, TaskEventType.Claim );
        assertEquals(2, eventsByTypeByTaskId.size());   
        
        eventsByTypeByTaskId = eventsAdmin.getEventsByTypeByTaskId(taskId, TaskEventType.Release );
        assertEquals(1, eventsByTypeByTaskId.size());   
    }
}
