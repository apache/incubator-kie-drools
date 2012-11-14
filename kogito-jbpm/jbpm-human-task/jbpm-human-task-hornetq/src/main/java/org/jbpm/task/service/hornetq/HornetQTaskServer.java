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

import org.kie.SystemEventListenerFactory;
import org.hornetq.core.config.Configuration;
import org.jbpm.task.event.TaskEventListener;
import org.jbpm.task.service.TaskService;

public class HornetQTaskServer extends BaseHornetQTaskServer implements Runnable {
    private TaskService service;
    public HornetQTaskServer(TaskService service, int port) {
        super(new HornetQTaskServerHandler(service, SystemEventListenerFactory.getSystemEventListener()), port, false);
        this.service = service;
    }
    
    public HornetQTaskServer(TaskService service, String host, int port) {
        super(new HornetQTaskServerHandler(service, SystemEventListenerFactory.getSystemEventListener()), host, port, false);
        this.service = service;
    }

    public HornetQTaskServer(TaskService service, int port, Configuration configuration) {
        super(new HornetQTaskServerHandler(service, SystemEventListenerFactory.getSystemEventListener()), port, configuration, false);
        this.service = service;
    }
    
    public HornetQTaskServer(TaskService service, String host, int port, Configuration configuration) {
        super(new HornetQTaskServerHandler(service, SystemEventListenerFactory.getSystemEventListener()), host, port, configuration, false);
        this.service = service;
    }

    @Override
    public void addEventListener(TaskEventListener listener) {
        this.service.addEventListener(listener);
        
    }
}