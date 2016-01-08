/*
 * Copyright 2010 Red Hat, Inc. and/or its affiliates.
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

package org.drools.core.marshalling.impl;

import java.io.IOException;
import java.io.OutputStream;
import java.util.Map;

import org.drools.core.common.BaseNode;
import org.drools.core.common.InternalWorkingMemory;
import org.drools.core.impl.InternalKnowledgeBase;
import org.drools.core.marshalling.impl.MarshallerWriteContext;
import org.kie.api.marshalling.ObjectMarshallingStrategyStore;
import org.kie.api.runtime.Environment;

/**
 * Extension to default <code>MarshallerWriteContext</code> that allows to pass additional
 * information to marshaller strategies, such as process instance id, task it, state
 */
public class ProcessMarshallerWriteContext extends MarshallerWriteContext {
    
    public static final int STATE_ACTIVE = 1;
    public static final int STATE_COMPLETED = 2;

    private Long processInstanceId;
    private Long taskId;
    private Long workItemId;
    private int state;
    

    public ProcessMarshallerWriteContext(OutputStream stream, 
            InternalKnowledgeBase kBase, 
            InternalWorkingMemory wm, 
            Map<Integer, BaseNode> sinks, 
            ObjectMarshallingStrategyStore resolverStrategyFactory, 
            Environment env) throws IOException {
        super(stream, kBase, wm, sinks, resolverStrategyFactory, env);
    }
    
    public Long getProcessInstanceId() {
        return processInstanceId;
    }
    
    public void setProcessInstanceId(Long processInstanceId) {
        this.processInstanceId = processInstanceId;
    }
    
    public Long getTaskId() {
        return taskId;
    }
    
    public void setTaskId(Long taskId) {
        this.taskId = taskId;
    }
    
    public Long getWorkItemId() {
        return workItemId;
    }
    
    public void setWorkItemId(Long workItemId) {
        this.workItemId = workItemId;
    }
    
    public int getState() {
        return state;
    }
    
    public void setState(int state) {
        this.state = state;
    }

}
