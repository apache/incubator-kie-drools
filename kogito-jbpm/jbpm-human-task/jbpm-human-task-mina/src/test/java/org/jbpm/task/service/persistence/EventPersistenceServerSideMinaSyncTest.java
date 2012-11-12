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

import static org.jbpm.task.event.EventPersistenceServerSideTest.doTestMultiplePersistentEvents;
import static org.jbpm.task.event.EventPersistenceServerSideTest.doTestPersistentEventHandlers;

import org.jbpm.task.BaseTest;
import org.jbpm.task.TaskService;
import org.jbpm.task.event.InternalPersistentTaskEventListener;
import org.jbpm.task.event.TaskEventsAdmin;
import org.jbpm.task.service.SyncTaskServiceWrapper;
import org.jbpm.task.service.mina.AsyncMinaTaskClient;
import org.jbpm.task.service.mina.MinaTaskServer;

public class EventPersistenceServerSideMinaSyncTest extends BaseTest {
    
    protected TaskService client;
    protected TaskEventsAdmin eventsAdmin;
    private MinaTaskServer server;
    
    public void setUp() throws Exception {
        super.setUp();
        
        server = new MinaTaskServer(taskService);
        System.out.println("Waiting for the MinaTask Server to come up");
        try {
            startTaskServerThread(server, false);
        } catch (Exception e) {
            startTaskServerThread(server, true);
        }
        
        client = new SyncTaskServiceWrapper(new AsyncMinaTaskClient());
        client.connect("127.0.0.1", 9123);
        eventsAdmin = taskService.createTaskEventsAdmin();
        // We can register an internal persistent listener to the Local Task Service
        server.addEventListener(new InternalPersistentTaskEventListener(eventsAdmin));
    }

    public void tearDown() throws Exception {
        client.disconnect();
        server.stop();
    }

   public void testPersistentEventHandlers() throws Exception  { 
       doTestPersistentEventHandlers(users, groups, client, taskSession, eventsAdmin);
   }
   
   public void testMultiPersistentEvents() throws Exception  {
       doTestMultiplePersistentEvents(users, groups, client, taskSession, eventsAdmin); 
   }
   
}
