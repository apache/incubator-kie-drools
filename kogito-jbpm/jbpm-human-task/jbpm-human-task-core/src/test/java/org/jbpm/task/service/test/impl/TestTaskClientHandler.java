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

package org.jbpm.task.service.test.impl;

import java.util.concurrent.BlockingQueue;

import org.jbpm.task.service.BaseClientHandler;
import org.jbpm.task.service.TaskClient;
import org.jbpm.task.service.TaskClientHandler;
import org.kie.SystemEventListener;

class TestTaskClientHandler extends BaseClientHandler {
    
	private TaskClientHandler handler;
	
    public TestTaskClientHandler(SystemEventListener systemEventListener) {
        this.handler = new TaskClientHandler(responseHandlers, systemEventListener);
    }

    public TaskClient getClient() {
        return handler.getClient();
    }

    public void setClient(TaskClient client) {
        handler.setClient(client);
    }

    public void messageReceived(BlockingQueue<byte []> producer, Object message) throws Exception {
		handler.messageReceived(new TestSessionWriter(producer), message);
    }

}