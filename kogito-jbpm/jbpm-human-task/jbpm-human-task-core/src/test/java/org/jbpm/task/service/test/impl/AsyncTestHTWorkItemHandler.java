/*
 * Copyright 2012 JBoss by Red Hat.
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

import static org.jbpm.task.service.test.impl.TestServerUtil.createTestTaskClientConnector;

import org.jbpm.process.workitem.wsht.AsyncGenericHTWorkItemHandler;
import org.jbpm.task.service.TaskClient;
import org.jbpm.task.service.TaskServer;
import org.kie.runtime.KnowledgeRuntime;
/**
 *
 * This class provides the default configurations for a test WorkItem Handler
 */
public class AsyncTestHTWorkItemHandler extends AsyncGenericHTWorkItemHandler {
    
    private String connectorName = "AsyncTestHTWorkItemHandler";
    
    public AsyncTestHTWorkItemHandler(KnowledgeRuntime session, TaskServer server) {
        super(session);
        init(server);
    }

    private void init(TaskServer server) {
        this.setIpAddress("127.0.0.1");
        this.setPort(9);
        
        if(getClient() == null){
            setClient( new TaskClient(createTestTaskClientConnector("client 1", (TestTaskServer) server)));
        }
    }

    public String getConnectorName() {
        return connectorName;
    }

    public void setConnectorName(String connectorName) {
        this.connectorName = connectorName;
    }
    
}
