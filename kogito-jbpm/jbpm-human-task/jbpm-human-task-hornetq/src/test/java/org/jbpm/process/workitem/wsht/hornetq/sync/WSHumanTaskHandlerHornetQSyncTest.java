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
package org.jbpm.process.workitem.wsht.hornetq.sync;

import org.jbpm.process.workitem.wsht.HornetQHTWorkItemHandler;
import org.jbpm.process.workitem.wsht.sync.WSHumanTaskHandlerBaseSyncTest;
import org.jbpm.task.service.SyncTaskServiceWrapper;
import org.jbpm.task.service.TaskServer;
import org.jbpm.task.service.hornetq.AsyncHornetQTaskClient;
import org.jbpm.task.service.hornetq.HornetQTaskServer;

public class WSHumanTaskHandlerHornetQSyncTest extends WSHumanTaskHandlerBaseSyncTest {

    private TaskServer server;

    @Override
    protected void setUp() throws Exception {
        super.setUp();
        server = new HornetQTaskServer(taskService, 5153);
        System.out.println("Waiting for the HornetQTask Server to come up");
        try {
            startTaskServerThread(server, false);
        } catch (Exception e) {
            startTaskServerThread(server, true);
        }

        setClient(new SyncTaskServiceWrapper(new AsyncHornetQTaskClient("client1")));
        getClient().connect();
        setHandler(new HornetQHTWorkItemHandler(ksession));
        
    }

    protected void tearDown() throws Exception {
        ((HornetQHTWorkItemHandler) getHandler()).dispose();
        getClient().disconnect();
        server.stop();
        super.tearDown();
    }
}
