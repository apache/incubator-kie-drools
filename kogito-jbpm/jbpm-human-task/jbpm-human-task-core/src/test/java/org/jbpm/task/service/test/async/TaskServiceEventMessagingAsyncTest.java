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

import org.jbpm.task.MockUserInfo;
import org.jbpm.task.service.TaskClient;
import org.jbpm.task.service.base.async.TaskServiceEventMessagingBaseAsyncTest;
import org.jbpm.task.service.test.impl.TestTaskServer;

public class TaskServiceEventMessagingAsyncTest extends TaskServiceEventMessagingBaseAsyncTest {

    @Override
    protected void setUp() throws Exception {
        super.setUp();
        
        server = startAsyncServer(taskService);

        client = new TaskClient(createTestTaskClientConnector("client 1", (TestTaskServer) server));
        client.connect();
        
        MockUserInfo userInfo = new MockUserInfo();
        userInfo.getEmails().put(users.get("tony"), "tony@domain.com");
        userInfo.getEmails().put(users.get("steve"), "steve@domain.com");

        userInfo.getLanguages().put(users.get("tony"), "en-UK");
        userInfo.getLanguages().put(users.get("steve"), "en-UK");
        taskService.setUserinfo(userInfo);
    }

}
