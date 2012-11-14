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

import org.kie.runtime.KnowledgeRuntime;
import org.jbpm.task.TaskService;
import org.jbpm.task.utils.OnErrorAction;


public class LocalHTWorkItemHandler extends GenericHTWorkItemHandler{

    private boolean registeredTaskEvents = false;
    public LocalHTWorkItemHandler(KnowledgeRuntime session) {
        super(session);
        this.setLocal(true);
    }
    
    public LocalHTWorkItemHandler(KnowledgeRuntime session, boolean owningSessionOnly) {
        super(session, owningSessionOnly);
        this.setLocal(true);
    }
    
    public LocalHTWorkItemHandler(TaskService client, KnowledgeRuntime session, boolean owningSessionOnly) {
        super(client, session, owningSessionOnly);
        this.setLocal(true);
    }
    
    public LocalHTWorkItemHandler(TaskService client, KnowledgeRuntime session) {
        super(client, session, null);
        this.setLocal(true);
    }

    public LocalHTWorkItemHandler(KnowledgeRuntime session, OnErrorAction action) {
        super(session, action);
        this.setLocal(true);
    }
    
    public LocalHTWorkItemHandler(TaskService client, KnowledgeRuntime session, OnErrorAction action) {
        super(client, session, action);
        this.setLocal(true);
    }
    
    public LocalHTWorkItemHandler(TaskService client, KnowledgeRuntime session, OnErrorAction action, ClassLoader classLoader) {
        super(client, session, action, classLoader);
        this.setLocal(true);
    }
  
    @Override
    public void connect(){
        if(!registeredTaskEvents){
            registerTaskEvents();
            this.registeredTaskEvents = true;
        }
    }
    
}
