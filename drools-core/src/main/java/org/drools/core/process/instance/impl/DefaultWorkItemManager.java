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
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.atomic.AtomicLong;

import org.drools.core.WorkItemHandlerNotFoundException;
import org.drools.core.common.InternalKnowledgeRuntime;
import org.drools.core.process.instance.WorkItem;
import org.drools.core.process.instance.WorkItemManager;
import org.kie.api.runtime.process.ProcessInstance;
import org.kie.api.runtime.process.WorkItemHandler;
import org.kie.internal.runtime.Closeable;

public class DefaultWorkItemManager implements WorkItemManager, Externalizable {

    private static final long serialVersionUID = 510l;

    private AtomicLong workItemCounter = new AtomicLong(0);
    private Map<Long, WorkItem> workItems = new ConcurrentHashMap<Long, WorkItem>();
    protected InternalKnowledgeRuntime kruntime;
    private Map<String, WorkItemHandler> workItemHandlers = new HashMap<String, WorkItemHandler>();

    public DefaultWorkItemManager(InternalKnowledgeRuntime kruntime) {
        this.kruntime = kruntime;
    }

    @SuppressWarnings("unchecked")
    public void readExternal(ObjectInput in) throws IOException, ClassNotFoundException {
        workItemCounter.set(in.readLong());
        workItems = (Map<Long, WorkItem>) in.readObject();
        kruntime = (InternalKnowledgeRuntime) in.readObject();
        workItemHandlers = (Map<String, WorkItemHandler>) in.readObject();
    }

    public void writeExternal(ObjectOutput out) throws IOException {
        out.writeLong(workItemCounter.get());
        out.writeObject(workItems);
        out.writeObject(kruntime);
        out.writeObject(workItemHandlers);
    }

    @Override
    public void internalExecuteWorkItem(WorkItem workItem) {
        ((WorkItemImpl) workItem).setId(workItemCounter.incrementAndGet());
        internalAddWorkItem(workItem);
        WorkItemHandler handler = this.workItemHandlers.get(workItem.getName());
        if (handler != null) {
            handler.executeWorkItem(workItem, this);
        } else {
            throwWorkItemHandlerNotFoundException(workItem);
        }
    }

    @Override
    public void internalAddWorkItem(WorkItem workItem) {
        workItems.put(new Long(workItem.getId()), workItem);
        // fix to reset workItemCounter after deserialization
        if (workItem.getId() > workItemCounter.get()) {
            workItemCounter.set(workItem.getId());
        }
    }

    @Override
    public void internalAbortWorkItem(long id) {
        WorkItemImpl workItem = (WorkItemImpl) workItems.get(new Long(id));
        // work item may have been aborted
        if (workItem != null) {
            WorkItemHandler handler = this.workItemHandlers.get(workItem.getName());
            internalAbortAndRemoveWorkItem(handler, workItem);
        }
    }

    protected void internalAbortAndRemoveWorkItem( WorkItemHandler handler, WorkItem workItem ) {
        if (handler != null) {
            handler.abortWorkItem(workItem, this);
        }
        removeWorkItem(workItem.getId());
        if( handler == null ) {
            throwWorkItemHandlerNotFoundException(workItem);
        }
    }

    protected void removeWorkItem(long workItemId) {
        workItems.remove(workItemId);
    }

    public WorkItemHandler getWorkItemHandler(String name) {
    	return this.workItemHandlers.get(name);
    }

    public void retryWorkItem(long workItemId) {
    	WorkItem workItem = workItems.get(workItemId);
    	retryWorkItem(workItem);
    }

    public void retryWorkItemWithParams(long workItemId,Map<String,Object> map) {
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
            } else {
                throwWorkItemHandlerNotFoundException(workItem);
            }
        }
    }
    
    public Set<WorkItem> getWorkItems() {
        return new HashSet<WorkItem>(workItems.values());
    }

    @Override
    public WorkItem getWorkItem(long id) {
        return workItems.get(id);
    }

    @Override
    public void completeWorkItem(long id, Map<String, Object> results) {
        WorkItem workItem = workItems.get(new Long(id));
        // work item may have been aborted
        if (workItem != null) {
            (workItem).setResults(results);
            (workItem).setState(WorkItem.COMPLETED);
            ProcessInstance processInstance = kruntime.getProcessInstance(workItem.getProcessInstanceId());
            signalEventAndRemoveWorkItem(processInstance, "workItemCompleted", workItem);
        }
    }

    @Override
    public void abortWorkItem(long id) {
        WorkItemImpl workItem = (WorkItemImpl) workItems.get(new Long(id));
        // work item may have been aborted
        if (workItem != null) {
            workItem.setState(WorkItem.ABORTED);
            ProcessInstance processInstance = kruntime.getProcessInstance(workItem.getProcessInstanceId());
            signalEventAndRemoveWorkItem(processInstance, "workItemAborted", workItem);
        }
    }

    protected void signalEventAndRemoveWorkItem(ProcessInstance processInstance, String event, WorkItem workItem) {
        // process instance may have finished already
        if (processInstance != null) {
            processInstance.signalEvent(event, workItem);
        }
        removeWorkItem(workItem.getId());
    }

    @Override
    public void registerWorkItemHandler(String workItemName, WorkItemHandler handler) {
        this.workItemHandlers.put(workItemName, handler);
    }

    @Override
    public void clear() {
        this.workItems.clear();
    }

    @Override
    public void signalEvent(String type, Object event) {
        this.kruntime.signalEvent(type, event);
    }

    @Override
    public void signalEvent(String type, Object event, long processInstanceId) {
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
    public void retryWorkItem( Long workItemID, Map<String, Object> params ) {
       if(params==null || params.isEmpty()){
           retryWorkItem(workItemID);
       }else{
           this.retryWorkItemWithParams( workItemID, params );
       }
        
    }
}
