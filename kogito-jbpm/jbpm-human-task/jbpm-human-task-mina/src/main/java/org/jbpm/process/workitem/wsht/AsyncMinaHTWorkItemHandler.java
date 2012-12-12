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
package org.jbpm.process.workitem.wsht;

import org.jbpm.task.AsyncTaskService;
import org.jbpm.task.service.TaskClient;
import org.jbpm.task.service.mina.MinaTaskClientConnector;
import org.jbpm.task.service.mina.MinaTaskClientHandler;
import org.jbpm.task.utils.OnErrorAction;
import org.kie.SystemEventListenerFactory;
import org.kie.runtime.KnowledgeRuntime;
/**
 *
 * This class provides the default configurations for a Mina WorkItem Handler
 */
public class AsyncMinaHTWorkItemHandler extends AsyncGenericHTWorkItemHandler{
    private String connectorName = "AsyncMinaHTWorkItemHandler";
    public AsyncMinaHTWorkItemHandler(KnowledgeRuntime session) {
        super(session);
        init();
    }
    
    public AsyncMinaHTWorkItemHandler(KnowledgeRuntime session, boolean owningSessionOnly) {
        super(session, owningSessionOnly);
        init();
    }
    
    public AsyncMinaHTWorkItemHandler(AsyncTaskService client, KnowledgeRuntime session, boolean owningSessionOnly) {
        super(session, owningSessionOnly);
        setClient(client);
        init();
    }

    public AsyncMinaHTWorkItemHandler(KnowledgeRuntime session, OnErrorAction action) {
        super(session, action);
        init();
    }
    
    public AsyncMinaHTWorkItemHandler(String connectorName, AsyncTaskService client, KnowledgeRuntime session, OnErrorAction action) {
        super(session, action);
        setClient(client);
        this.connectorName = connectorName;
        init();
    }
    public AsyncMinaHTWorkItemHandler(String connectorName, AsyncTaskService client, KnowledgeRuntime session, OnErrorAction action, ClassLoader classLoader) {
        super(session, action, classLoader);
        setClient(client);
        this.connectorName = connectorName;
        init();
    }

    private void init(){
        if(getClient() == null){
            setClient(new TaskClient(new MinaTaskClientConnector(this.connectorName,
                    new MinaTaskClientHandler(SystemEventListenerFactory.getSystemEventListener()))));
        }
        if(getPort() <= 0){
            setPort(9123);
        }
        if(getIpAddress() == null || getIpAddress().equals("")){
            setIpAddress("127.0.0.1");
        }
    }

    public String getConnectorName() {
        return connectorName;
    }

    public void setConnectorName(String connectorName) {
        this.connectorName = connectorName;
    }
    
    
   
    
}
