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

package org.jbpm.process.instance;

import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;
import java.util.Set;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.atomic.AtomicLong;

import org.drools.core.WorkItemHandlerNotFoundException;
import org.drools.core.process.instance.WorkItem;
import org.drools.core.process.instance.WorkItemManager;
import org.drools.core.process.instance.impl.WorkItemImpl;
import org.kie.api.runtime.process.ProcessInstance;
import org.kie.api.runtime.process.WorkItemHandler;
import org.kie.api.runtime.process.WorkItemNotFoundException;
import org.kie.internal.runtime.Closeable;
import org.kie.kogito.signal.SignalManager;

public class LightWorkItemManager implements WorkItemManager{

    private static final long serialVersionUID = 510l;

    private AtomicLong workItemCounter = new AtomicLong(0);
    private Map<Long, WorkItem> workItems = new ConcurrentHashMap<Long, WorkItem>();
    private Map<String, WorkItemHandler> workItemHandlers = new HashMap<String, WorkItemHandler>();

    private final ProcessInstanceManager processInstanceManager;
    private final SignalManager signalManager;

    public LightWorkItemManager(ProcessInstanceManager processInstanceManager, SignalManager signalManager) {
        this.processInstanceManager = processInstanceManager;
        this.signalManager = signalManager;
    }

    public void internalExecuteWorkItem(WorkItem workItem) {
        ((WorkItemImpl) workItem).setId(workItemCounter.incrementAndGet());
        internalAddWorkItem(workItem);
        WorkItemHandler handler = this.workItemHandlers.get(workItem.getName());
        if (handler != null) {
            handler.executeWorkItem(workItem, this);
        } else throw new WorkItemHandlerNotFoundException( "Could not find work item handler for " + workItem.getName(),
                                                    workItem.getName() );
    }    

    public void internalAddWorkItem(WorkItem workItem) {
        workItems.put(workItem.getId(), workItem);
        // fix to reset workItemCounter after deserialization
        if (workItem.getId() > workItemCounter.get()) {
            workItemCounter.set(workItem.getId());
        }
    }

    public void internalAbortWorkItem(long id) {
        WorkItemImpl workItem = (WorkItemImpl) workItems.get(id);
        // work item may have been aborted
        if (workItem != null) {
            WorkItemHandler handler = this.workItemHandlers.get(workItem.getName());
            if (handler != null) {
                handler.abortWorkItem(workItem, this);
            } else {
                workItems.remove( workItem.getId() );
                throw new WorkItemHandlerNotFoundException( "Could not find work item handler for " + workItem.getName(),
                                                                 workItem.getName() );
            }
            workItems.remove(workItem.getId());
        }
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
            } else throw new WorkItemHandlerNotFoundException( "Could not find work item handler for " + workItem.getName(),
                                                        workItem.getName() );
        }
    }
    
    public Set<WorkItem> getWorkItems() {
        return new HashSet<WorkItem>(workItems.values());
    }

    public WorkItem getWorkItem(long id) {
        return workItems.get(id);
    }

    public void completeWorkItem(long id, Map<String, Object> results) {
        WorkItem workItem = workItems.get(id);
        // work item may have been aborted
        if (workItem != null) {
            (workItem).setResults(results);
            ProcessInstance processInstance = processInstanceManager.getProcessInstance(workItem.getProcessInstanceId());
            (workItem).setState(WorkItem.COMPLETED);
            // process instance may have finished already
            if (processInstance != null) {
                processInstance.signalEvent("workItemCompleted", workItem);
            }
            workItems.remove(id);
        } else {
            throw new WorkItemNotFoundException("Work Item (" + id + ") does not exist", id);
        }
    }

    public void abortWorkItem(long id) {
        WorkItemImpl workItem = (WorkItemImpl) workItems.get(id);
        // work item may have been aborted
        if (workItem != null) {
            ProcessInstance processInstance = processInstanceManager.getProcessInstance(workItem.getProcessInstanceId());
            workItem.setState(WorkItem.ABORTED);
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
        this.signalManager.signalEvent(type, event);
    } 
    
    public void signalEvent(String type, Object event, long processInstanceId) { 
        this.signalManager.signalEvent(processInstanceId, type, event);
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
