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

package org.jbpm.process.workitem.wsht.test;

import static org.jbpm.task.service.test.impl.TestServerUtil.startAsyncServer;

import org.jbpm.process.workitem.wsht.WSHumanTaskHandlerBaseUserGroupCallbackTest;
import org.jbpm.task.TestStatefulKnowledgeSession;
import org.jbpm.task.service.TaskServer;
import org.jbpm.task.service.test.impl.AsyncTestHTWorkItemHandler;

public class WSHumanTaskHandlerTestUserGroupCallbackTest extends WSHumanTaskHandlerBaseUserGroupCallbackTest {

    private TaskServer server;

    @Override
    protected void setUp() throws Exception {
        super.setUp();
        server = startAsyncServer(taskService, 1);
        while (!server.isRunning()) {
            Thread.sleep(50);
        }
        AsyncTestHTWorkItemHandler handler = new AsyncTestHTWorkItemHandler(ksession, server);
        setClient(handler.getClient());
        setHandler(handler);
    }

    protected void tearDown() throws Exception {
        ((AsyncTestHTWorkItemHandler) getHandler()).dispose();
        getClient().disconnect();
        server.stop();
        super.tearDown();
    }
}
