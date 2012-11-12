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

package org.jbpm.task.service.test.async;

import static org.jbpm.task.service.test.impl.TestServerUtil.createTestTaskClientConnector;
import static org.jbpm.task.service.test.impl.TestServerUtil.startAsyncServer;

import org.jbpm.task.TaskService;
import org.jbpm.task.service.ClaimTaskTwiceTest;
import org.jbpm.task.service.SyncTaskServiceWrapper;
import org.jbpm.task.service.TaskClient;
import org.jbpm.task.service.TaskServer;
import org.jbpm.task.service.test.impl.TestTaskServer;

/**
 * Thanks to jbride for development of the test.
 * 
 */
public class ClaimTaskTwiceAsyncTest extends ClaimTaskTwiceTest {

    // Impl 
    protected TaskServer server;
    
    protected void setUp() throws Exception {
        super.setUp();
        server = startAsyncServer(taskService, clientCount);
    }

    protected void tearDown() throws Exception {
        super.tearDown();
        server.stop();
    }

    protected TaskService createClient(String clientName) { 
        TaskClient taskClient = new TaskClient(createTestTaskClientConnector("client 1", (TestTaskServer) server));
        taskClient.connect();
        
        TaskService client = new SyncTaskServiceWrapper(taskClient);
        return client;
    }
    
    protected void cleanupClient(TaskService client) throws Exception { 
        ((SyncTaskServiceWrapper) client).disconnect();
    }
    
    public void testMultipleClientsOneTask() throws Exception { 
        fixmetestMultipleClientsOneTask();
    }
}
