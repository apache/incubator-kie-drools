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

package org.drools.core.process.instance.impl;

import java.io.Externalizable;
import java.io.IOException;
import java.io.ObjectInput;
import java.io.ObjectOutput;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;
import java.util.Set;
import java.util.UUID;
import java.util.concurrent.ConcurrentHashMap;

import org.drools.core.WorkItemHandlerNotFoundException;
import org.drools.core.common.InternalKnowledgeRuntime;
import org.drools.core.process.instance.WorkItem;
import org.drools.core.process.instance.WorkItemManager;
import org.kie.api.runtime.process.ProcessInstance;
import org.kie.api.runtime.process.WorkItemHandler;
import org.kie.internal.runtime.Closeable;
import org.kie.kogito.process.workitem.Policy;

import static org.kie.api.runtime.process.WorkItem.ABORTED;
import static org.kie.api.runtime.process.WorkItem.COMPLETED;

public class DefaultWorkItemManager implements WorkItemManager, Externalizable {

    private static final long serialVersionUID = 510l;

    private Map<String, WorkItem> workItems = new ConcurrentHashMap<>();
    private InternalKnowledgeRuntime kruntime;
    private Map<String, WorkItemHandler> workItemHandlers = new HashMap<>();

    public DefaultWorkItemManager(InternalKnowledgeRuntime kruntime) {
        this.kruntime = kruntime;
    }

    /**
     * Do not use this constructor. It should be used just by deserialization.
     */
    public DefaultWorkItemManager() {
    }

    @SuppressWarnings("unchecked")
    public void readExternal(ObjectInput in) throws IOException, ClassNotFoundException {        
        workItems = (Map<String, WorkItem>) in.readObject();
        kruntime = (InternalKnowledgeRuntime) in.readObject();
        workItemHandlers = (Map<String, WorkItemHandler>) in.readObject();
    }

    public void writeExternal(ObjectOutput out) throws IOException {
        out.writeObject(workItems);
        out.writeObject(kruntime);
        out.writeObject(workItemHandlers);
    }

    public void internalExecuteWorkItem(WorkItem workItem) {
        ((WorkItemImpl) workItem).setId(UUID.randomUUID().toString());
        internalAddWorkItem(workItem);
        WorkItemHandler handler = this.workItemHandlers.get(workItem.getName());
        if (handler != null) {
            handler.executeWorkItem(workItem, this);
        } else throw new WorkItemHandlerNotFoundException(workItem.getName() );
    }

    public void internalAddWorkItem(WorkItem workItem) {
        workItems.put(workItem.getId(), workItem);
    }

    public void internalAbortWorkItem(String id) {
        WorkItemImpl workItem = (WorkItemImpl) workItems.get(id);
        // work item may have been aborted
        if (workItem != null) {
            WorkItemHandler handler = this.workItemHandlers.get(workItem.getName());
            if (handler != null) {
                handler.abortWorkItem(workItem, this);
            } else {
                workItems.remove( workItem.getId() );
                throw new WorkItemHandlerNotFoundException(workItem.getName() );
            }
            workItems.remove(workItem.getId());
        }
    }

    public void retryWorkItem(String workItemId) {
    	WorkItem workItem = workItems.get(workItemId);
    	retryWorkItem(workItem);
    }

    public void retryWorkItemWithParams(String workItemId,Map<String,Object> map) {
        WorkItem workItem = workItems.get(workItemId);
        
        if ( workItem != null ) {
            workItem.setParameters( map );
            
            retryWorkItem( workItem );
        }
    }
    
    private void retryWorkItem(WorkItem workItem) {
        if (workItem != null) {
            WorkItemHandler handler = this.workItemHandlers.get(workItem.getName());
            if (handler != null) {
                handler.executeWorkItem(workItem, this);
            } else throw new WorkItemHandlerNotFoundException(workItem.getName() );
        }
    }
    
    public Set<WorkItem> getWorkItems() {
        return new HashSet<>(workItems.values());
    }

    public WorkItem getWorkItem(String id) {
        return workItems.get(id);
    }

    public void completeWorkItem(String id, Map<String, Object> results, Policy<?>... policies) {
        WorkItem workItem = workItems.get(id);
        // work item may have been aborted
        if (workItem != null) {
            (workItem).setResults(results);
            ProcessInstance processInstance = kruntime.getProcessInstance(workItem.getProcessInstanceId());
            (workItem).setState(COMPLETED);
            // process instance may have finished already
            if (processInstance != null) {
                processInstance.signalEvent("workItemCompleted", workItem);
            }
            workItems.remove(id);
        }
    }

    public void abortWorkItem(String id, Policy<?>... policies) {
        WorkItemImpl workItem = (WorkItemImpl) workItems.get(id);
        // work item may have been aborted
        if (workItem != null) {
            ProcessInstance processInstance = kruntime.getProcessInstance(workItem.getProcessInstanceId());
            workItem.setState(ABORTED);
            // process instance may have finished already
            if (processInstance != null) {
                processInstance.signalEvent("workItemAborted", workItem);
            }
            workItems.remove(id);
        }
    }

    public void registerWorkItemHandler(String workItemName, WorkItemHandler handler) {
        this.workItemHandlers.put(workItemName, handler);
    }

    public void clear() {
        this.workItems.clear();
    }
    
    public void signalEvent(String type, Object event) { 
        this.kruntime.signalEvent(type, event);
    } 
    
    public void signalEvent(String type, Object event, String processInstanceId) { 
        this.kruntime.signalEvent(type, event, processInstanceId);
    }

    @Override
    public void dispose() {
        if (workItemHandlers != null) {
            for (Map.Entry<String, WorkItemHandler> handlerEntry : workItemHandlers.entrySet()) {
                if (handlerEntry.getValue() instanceof Closeable) {
                    ((Closeable) handlerEntry.getValue()).close();
                }
            }
        }
    }
    
    @Override
    public void retryWorkItem( String workItemID, Map<String, Object> params ) {
       if(params==null || params.isEmpty()){
           retryWorkItem(workItemID);
       }else{
           this.retryWorkItemWithParams( workItemID, params );
       }
        
    }

    @Override
    public void internalCompleteWorkItem(WorkItem workItem) {
        
    }
}
