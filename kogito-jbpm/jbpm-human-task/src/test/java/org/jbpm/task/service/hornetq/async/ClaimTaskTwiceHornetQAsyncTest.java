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

package org.jbpm.task.service.hornetq.async;

import org.drools.SystemEventListenerFactory;
import org.jbpm.task.TaskService;
import org.jbpm.task.service.AsyncTaskServiceWrapper;
import org.jbpm.task.service.ClaimTaskTwiceTest;
import org.jbpm.task.service.TaskClient;
import org.jbpm.task.service.TaskServer;
import org.jbpm.task.service.hornetq.HornetQTaskClientConnector;
import org.jbpm.task.service.hornetq.HornetQTaskClientHandler;
import org.jbpm.task.service.hornetq.HornetQTaskServer;

/**
 * Thanks to jbride for development of the test.
 * 
 */
public class ClaimTaskTwiceHornetQAsyncTest extends ClaimTaskTwiceTest {

    // Impl 
    protected TaskServer server;
    
    protected void setUp() throws Exception {
        super.setUp();
        
        // HornetQ setup
        server = new HornetQTaskServer(taskService, 5446);
        Thread thread = new Thread(server);
        thread.start();
        System.out.println("Waiting for the HornetQTask Server to come up");
        while (!server.isRunning()) {
            System.out.print(".");
            Thread.sleep(50);
        }
    }

    protected void tearDown() throws Exception {
        super.tearDown();
        server.stop();
    }

    protected TaskService createClient(String clientName) { 
        TaskClient taskClient = new TaskClient(new HornetQTaskClientConnector(clientName, new HornetQTaskClientHandler(
                SystemEventListenerFactory.getSystemEventListener())));
        taskClient.connect("127.0.0.1", 5446);
        
        TaskService client = new AsyncTaskServiceWrapper(taskClient);
        return client;
    }
    
    protected void cleanupClient(TaskService client) throws Exception { 
        ((AsyncTaskServiceWrapper) client).disconnect();
    }
    
}
