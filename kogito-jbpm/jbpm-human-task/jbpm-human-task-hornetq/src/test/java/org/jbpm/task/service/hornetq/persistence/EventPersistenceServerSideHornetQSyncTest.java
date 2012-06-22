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
package org.jbpm.task.service.hornetq.persistence;

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
import org.jbpm.task.event.TaskEvent;
import org.jbpm.task.event.TaskEventsAdmin;
import org.jbpm.task.service.Operation;
import org.jbpm.task.service.SyncTaskServiceWrapper;
import org.jbpm.task.service.hornetq.AsyncHornetQTaskClient;
import org.jbpm.task.service.hornetq.HornetQTaskServer;
import org.junit.After;
import org.junit.AfterClass;
import org.junit.Before;
import org.junit.BeforeClass;
import static org.junit.Assert.*;

public class EventPersistenceServerSideHornetQSyncTest extends BaseTest {
    protected TaskService client;
    protected TaskEventsAdmin eventsAdmin;
    private HornetQTaskServer server;
    
    @Override
    protected EntityManagerFactory createEntityManagerFactory() {
        return Persistence.createEntityManagerFactory("org.jbpm.task.events");
    }

    public EventPersistenceServerSideHornetQSyncTest() {
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
        
        server = new HornetQTaskServer(taskService, 5445);
        Thread thread = new Thread(server);
        thread.start();
        System.out.println("Waiting for the MinaTask Server to come up");
        while (!server.isRunning()) {
        	System.out.print(".");
        	Thread.sleep( 50 );
        }
        
        client = new SyncTaskServiceWrapper(new AsyncHornetQTaskClient());
        client.connect("127.0.0.1", 5445);
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
    
        List<TaskEvent> eventsByTypeByTaskId = eventsAdmin.getEventsByTypeByTaskId(taskId, "TaskClaimedEvent");
        
        assertEquals(2, eventsByTypeByTaskId.size());   
        List<TaskEvent> taskClaimedEventsByTaskId = eventsAdmin.getTaskClaimedEventsByTaskId(taskId);
        
        
        assertEquals(2, taskClaimedEventsByTaskId.size()); 
    }
}
