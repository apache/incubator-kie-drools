/**
 * Copyright 2010 JBoss Inc
 *
 * Licensed under the Apache License, Version 2.0 (the "License"); you may not
 * use this file except in compliance with the License. You may obtain a copy of
 * the License at
 *
 * http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS, WITHOUT
 * WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied. See the
 * License for the specific language governing permissions and limitations under
 * the License.
 */
package org.jbpm.task.service.hornetq;

import java.util.HashMap;
import java.util.Map;

import org.kie.SystemEventListener;
import org.hornetq.api.core.client.ClientProducer;
import org.hornetq.api.core.client.ClientSession;
import org.jbpm.task.service.TaskServerHandler;
import org.jbpm.task.service.TaskService;

public class HornetQTaskServerHandler {

    private TaskServerHandler handler;
    private Map<String, ClientProducer> producers;

    public HornetQTaskServerHandler(TaskService service, SystemEventListener systemEventListener) {
        this.handler = new TaskServerHandler(service, systemEventListener);
        this.producers = new HashMap<String, ClientProducer>();
    }

    public void messageReceived(ClientSession session, Object message, String destination) throws Exception {
        ClientProducer producer = producers.get(destination);
        if (producer == null) {
            producer = session.createProducer(destination);
            producers.put(destination, producer);
        }
        handler.messageReceived(new HornetQSessionWriter(session, producer), message);
    }
}