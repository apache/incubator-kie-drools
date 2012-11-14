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

package org.jbpm.task.service.mina;

import org.kie.SystemEventListenerFactory;
import org.jbpm.task.event.TaskEventListener;
import org.jbpm.task.service.TaskService;


public class MinaTaskServer extends BaseMinaTaskServer {
    private TaskService service;
    public MinaTaskServer(TaskService service) {
        super( new MinaTaskServerHandler( service, SystemEventListenerFactory.getSystemEventListener() ),
               9123 );
        this.service = service;
    }
    
    public MinaTaskServer(TaskService service, int port) {
        super( new MinaTaskServerHandler( service, SystemEventListenerFactory.getSystemEventListener() ),
               port, "127.0.0.1" );
        this.service = service;
    }

    public MinaTaskServer(TaskService service, int port, String localInterface) {
        super( new MinaTaskServerHandler( service, SystemEventListenerFactory.getSystemEventListener() ),
               port, localInterface );
        this.service = service;
        
    }

    @Override
    public void addEventListener(TaskEventListener listener) {
        this.service.addEventListener(listener);
    }
}