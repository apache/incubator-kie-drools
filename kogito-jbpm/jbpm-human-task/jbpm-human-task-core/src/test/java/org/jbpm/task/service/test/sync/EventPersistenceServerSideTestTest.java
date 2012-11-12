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
package org.jbpm.task.service.test.sync;

import static org.jbpm.task.service.test.impl.TestServerUtil.createTestTaskClientConnector;
import static org.jbpm.task.service.test.impl.TestServerUtil.startServer;

import java.io.StringReader;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import javax.persistence.EntityManagerFactory;
import javax.persistence.Persistence;

import org.jbpm.task.BaseTest;
import org.jbpm.task.Status;
import org.jbpm.task.Task;
import org.jbpm.task.TaskService;
import org.jbpm.task.event.InternalPersistentTaskEventListener;
import org.jbpm.task.event.TaskEventsAdmin;
import org.jbpm.task.event.entity.TaskEvent;
import org.jbpm.task.event.entity.TaskEventType;
import org.jbpm.task.service.Operation;
import org.jbpm.task.service.SyncTaskServiceWrapper;
import org.jbpm.task.service.TaskClient;
import org.jbpm.task.service.TaskServer;
import org.jbpm.task.service.test.impl.TestTaskServer;
import org.junit.After;
import org.junit.Before;

public class EventPersistenceServerSideTestTest extends BaseTest {
    protected TaskService client;
    protected TaskEventsAdmin eventsAdmin;
    protected TaskServer server;
    
    @Override
    protected EntityManagerFactory createEntityManagerFactory() {
        return Persistence.createEntityManagerFactory("org.jbpm.task");
    }

    @Before
    public void setUp() throws Exception {
        super.setUp();
        server = startServer(taskService);

        TaskClient taskClient = new TaskClient(createTestTaskClientConnector("client 1", (TestTaskServer) server));
        client = new SyncTaskServiceWrapper(taskClient);
        client.connect();
        eventsAdmin = taskService.createTaskEventsAdmin();
        // We can register an internal persistent listener to the Local Task Service
        server.addEventListener(new InternalPersistentTaskEventListener(eventsAdmin));
    }

    @After
    public void tearDown() throws Exception {
        client.disconnect();
        server.stop();
    }

   public void testPersistentEventHandlers() throws Exception {      
        
        Map  vars = new HashMap();     
        vars.put( "users", users );
        vars.put( "groups", groups );        
        vars.put( "now", new Date() );                

        
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
   
   public void testMultiPersistentEvents() throws Exception {
       
        Map  vars = new HashMap();     
        vars.put( "users", users );
        vars.put( "groups", groups );        
        vars.put( "now", new Date() );                

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
