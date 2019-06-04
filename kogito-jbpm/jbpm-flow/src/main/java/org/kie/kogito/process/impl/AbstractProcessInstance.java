/*
 * Copyright 2019 Red Hat, Inc. and/or its affiliates.
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 *
 *      http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package org.kie.kogito.process.impl;

import java.lang.reflect.Field;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

import org.jbpm.workflow.instance.WorkflowProcessInstance;
import org.jbpm.workflow.instance.node.WorkItemNodeInstance;
import org.kie.api.runtime.process.EventListener;
import org.kie.api.runtime.process.ProcessRuntime;
import org.kie.api.runtime.process.WorkItemNotFoundException;
import org.kie.kogito.Model;
import org.kie.kogito.process.Process;
import org.kie.kogito.process.ProcessInstance;
import org.kie.kogito.process.Signal;
import org.kie.kogito.process.WorkItem;

public abstract class AbstractProcessInstance<T extends Model> implements ProcessInstance<T> {

    private final T variables;
    private final AbstractProcess<T> process;
    private final ProcessRuntime rt;
    private org.kie.api.runtime.process.ProcessInstance legacyProcessInstance;
    
    private CompletionEventListener completionEventListener = new CompletionEventListener();

    public AbstractProcessInstance(AbstractProcess<T> process, T variables, ProcessRuntime rt) {
        this.process = process;
        this.rt = rt;
        this.variables = variables;
    }

    public void start() {
        Map<String, Object> map = bind(variables);
        String id = process.legacyProcess().getId();
        this.legacyProcessInstance = rt.createProcessInstance(id, map);
        long pid = legacyProcessInstance.getId();
        
        ((WorkflowProcessInstance)legacyProcessInstance).addEventListener("processInstanceCompleted:"+pid, completionEventListener, false);
        
        process.instances().update(pid, this);
        org.kie.api.runtime.process.ProcessInstance pi = this.rt.startProcessInstance(pid);
        unbind(variables, pi.getVariables());
        
    }

    public void abort() {
        if (legacyProcessInstance == null) {
            return;
        }
        long pid = legacyProcessInstance.getId();
        unbind(variables, legacyProcessInstance.getVariables());
        process.instances().remove(pid);
        this.rt.abortProcessInstance(pid);
 
    }

    @Override
    public <S> void send(Signal<S> signal) {
        legacyProcessInstance.signalEvent(signal.channel(), signal.payload());
       
    }

    @Override
    public Process<T> process() {
        return process;
    }

    @Override
    public T variables() {
        return variables;
    }

    @Override
    public int status() {
        return legacyProcessInstance.getState();
    }

    @Override
    public long id() {
        return legacyProcessInstance.getId();
    }

    @Override
    public WorkItem workItem(Long workItemId) {
        WorkItemNodeInstance workItemInstance = (WorkItemNodeInstance) ((WorkflowProcessInstance)legacyProcessInstance).getNodeInstances()
                .stream()
                .filter(ni -> ni instanceof WorkItemNodeInstance && ((WorkItemNodeInstance) ni).getWorkItemId() == workItemId)
                .findFirst()
                .orElseThrow(() -> new WorkItemNotFoundException("Work item with id " + workItemId + " was not found in process instance " + id(), workItemId));
        return new BaseWorkItem(workItemInstance.getWorkItem().getId(), (String)workItemInstance.getWorkItem().getParameters().getOrDefault("TaskName", workItemInstance.getNodeName()), workItemInstance.getWorkItem().getParameters());
    }

    @Override
    public List<WorkItem> workItems() {
        return ((WorkflowProcessInstance)legacyProcessInstance).getNodeInstances()
                .stream()
                .filter(ni -> ni instanceof WorkItemNodeInstance)
                .map(ni -> new BaseWorkItem(((WorkItemNodeInstance)ni).getWorkItemId(), (String)((WorkItemNodeInstance)ni).getWorkItem().getParameters().getOrDefault("TaskName", ni.getNodeName())))
                .collect(Collectors.toList());
        
    }

    @Override
    public void completeWorkItem(long id, Map<String, Object> variables) {
        this.rt.getWorkItemManager().completeWorkItem(id, variables);
        unbind(this.variables, legacyProcessInstance.getVariables());
        removeOnFinish();
    }
    
    @Override
    public void abortWorkItem(long id) {
        this.rt.getWorkItemManager().abortWorkItem(id);
        unbind(this.variables, legacyProcessInstance.getVariables());
        removeOnFinish();
    }
    
    protected void removeOnFinish() {

        if (status() != org.kie.api.runtime.process.ProcessInstance.STATE_ACTIVE) {
            process.instances().remove(legacyProcessInstance.getId());
            ((WorkflowProcessInstance)legacyProcessInstance).removeEventListener("processInstanceCompleted:"+legacyProcessInstance.getId(), completionEventListener, false);
        }
    }

    // this must be overridden at compile time
    protected Map<String, Object> bind(T variables) {
        HashMap<String, Object> vmap = new HashMap<>();
        if (variables == null) {
            return vmap;
        }
        try {
            for (Field f : variables.getClass().getDeclaredFields()) {
                f.setAccessible(true);
                Object v = null;
                v = f.get(variables);
                vmap.put(f.getName(), v);
            }
        } catch (IllegalAccessException e) {
            throw new Error(e);
        }
        vmap.put("$v", variables);
        return vmap;
    }

    protected void unbind(T variables, Map<String, Object> vmap) {
        try {
            for (Field f : variables.getClass().getDeclaredFields()) {
                f.setAccessible(true);
                f.set(variables, vmap.get(f.getName()));
            }
        } catch (IllegalAccessException e) {
            throw new Error(e);
        }
        vmap.put("$v", variables);
    }
    
    private class CompletionEventListener implements EventListener {
        
        @Override
        public void signalEvent(String type, Object event) {
            removeOnFinish();
        }
        
        @Override
        public String[] getEventTypes() {
            return new String[] {"processInstanceCompleted:"+legacyProcessInstance.getId()};
        }
    }
}
