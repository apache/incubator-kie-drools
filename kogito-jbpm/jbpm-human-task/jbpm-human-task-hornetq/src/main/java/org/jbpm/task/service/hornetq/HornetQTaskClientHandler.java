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

package org.jbpm.task.service.hornetq;

import java.util.HashMap;
import java.util.Map;

import org.hornetq.api.core.client.ClientProducer;
import org.hornetq.api.core.client.ClientSession;
import org.jbpm.task.service.BaseClientHandler;
import org.jbpm.task.service.TaskClient;
import org.jbpm.task.service.TaskClientHandler;
import org.kie.SystemEventListener;

public class HornetQTaskClientHandler extends BaseClientHandler {
    
	private TaskClientHandler handler;
	private Map<String, ClientProducer> producers;
	
    public HornetQTaskClientHandler(SystemEventListener systemEventListener) {
        this.handler = new TaskClientHandler(responseHandlers, systemEventListener);
        this.producers = new HashMap<String, ClientProducer>();
    }

    public TaskClient getClient() {
        return handler.getClient();
    }

    public void setClient(TaskClient client) {
        handler.setClient(client);
    }

    public void exceptionCaught(ClientSession session, Throwable cause) throws Exception {
//    	handler.exceptionCaught(new HornetQSessionWriter(session, message), cause);
    }

    public void messageReceived(ClientSession session, Object message, String producerId) throws Exception {
    	ClientProducer producer = producers.get(producerId);
    	if (producer==null) {
    		producer = session.createProducer(producerId);
    	}
		handler.messageReceived(new HornetQSessionWriter(session, producer), message);
    }

}