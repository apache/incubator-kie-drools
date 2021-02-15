/*
 * Copyright 2021 Red Hat, Inc. and/or its affiliates.
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *       http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package org.kie.kogito.process.workitems.impl;

import java.io.IOException;
import java.io.ObjectOutput;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;
import java.util.Set;
import java.util.UUID;
import java.util.concurrent.ConcurrentHashMap;

import org.drools.core.process.instance.WorkItem;
import org.kie.api.runtime.process.ProcessInstance;
import org.kie.api.runtime.process.WorkItemHandler;
import org.kie.internal.runtime.Closeable;
import org.kie.kogito.internal.process.runtime.KogitoProcessInstance;
import org.kie.kogito.internal.process.runtime.KogitoProcessRuntime;
import org.kie.kogito.process.workitem.Policy;
import org.kie.kogito.process.workitems.KogitoWorkItem;
import org.kie.kogito.process.workitems.KogitoWorkItemHandlerNotFoundException;
import org.kie.kogito.process.workitems.KogitoWorkItemManager;

import static org.kie.kogito.internal.process.runtime.KogitoWorkItem.ABORTED;
import static org.kie.kogito.internal.process.runtime.KogitoWorkItem.COMPLETED;

public class KogitoDefaultWorkItemManager implements KogitoWorkItemManager {

    private Map<String, KogitoWorkItem> workItems = new ConcurrentHashMap<>();
    private KogitoProcessRuntime kruntime;
    private Map<String, WorkItemHandler> workItemHandlers = new HashMap<>();

    public KogitoDefaultWorkItemManager( KogitoProcessRuntime kruntime) {
        this.kruntime = kruntime;
    }


    public void writeExternal(ObjectOutput out) throws IOException {
        out.writeObject(workItems);
        out.writeObject(kruntime);
        out.writeObject(workItemHandlers);
    }

    public void internalExecuteWorkItem(KogitoWorkItem workItem) {
        (( KogitoWorkItemImpl ) workItem).setId(UUID.randomUUID().toString());
        internalAddWorkItem(workItem);
        WorkItemHandler handler = this.workItemHandlers.get(workItem.getName());
        if (handler != null) {
            handler.executeWorkItem(workItem, this);
        } else throw new KogitoWorkItemHandlerNotFoundException(workItem.getName() );
    }

    public void internalAddWorkItem( KogitoWorkItem workItem) {
        workItems.put(workItem.getStringId(), workItem);
    }

    public void internalAbortWorkItem(String id) {
        KogitoWorkItemImpl workItem = ( KogitoWorkItemImpl ) workItems.get(id);
        // work item may have been aborted
        if (workItem != null) {
            WorkItemHandler handler = this.workItemHandlers.get(workItem.getName());
            if (handler != null) {
                handler.abortWorkItem(workItem, this);
            } else {
                workItems.remove( workItem.getStringId() );
                throw new KogitoWorkItemHandlerNotFoundException(workItem.getName() );
            }
            workItems.remove(workItem.getStringId());
        }
    }

    public void retryWorkItem(String workItemId) {
    	KogitoWorkItem workItem = workItems.get(workItemId);
    	retryWorkItem(workItem);
    }

    public void retryWorkItemWithParams(String workItemId,Map<String,Object> map) {
        KogitoWorkItem workItem = workItems.get(workItemId);

        if ( workItem != null ) {
            workItem.setParameters( map );

            retryWorkItem( workItem );
        }
    }

    private void retryWorkItem( KogitoWorkItem workItem) {
        if (workItem != null) {
            WorkItemHandler handler = this.workItemHandlers.get(workItem.getName());
            if (handler != null) {
                handler.executeWorkItem(workItem, this);
            } else throw new KogitoWorkItemHandlerNotFoundException(workItem.getName() );
        }
    }

    public KogitoWorkItem getWorkItem( String id) {
        return workItems.get(id);
    }

    public void completeWorkItem(String id, Map<String, Object> results, Policy<?>... policies) {
        KogitoWorkItem workItem = workItems.get(id);
        // work item may have been aborted
        if (workItem != null) {
            workItem.setResults(results);
            KogitoProcessInstance processInstance = kruntime.getProcessInstance(workItem.getProcessInstanceStringId());
            workItem.setState(COMPLETED);
            // process instance may have finished already
            if (processInstance != null) {
                processInstance.signalEvent("workItemCompleted", workItem);
            }
            workItems.remove(id);
        }
    }

    public void abortWorkItem(String id, Policy<?>... policies) {
        KogitoWorkItemImpl workItem = ( KogitoWorkItemImpl ) workItems.get(id);
        // work item may have been aborted
        if (workItem != null) {
            ProcessInstance processInstance = kruntime.getProcessInstance(workItem.getProcessInstanceStringId());
            workItem.setState(ABORTED);
            // process instance may have finished already
            if (processInstance != null) {
                processInstance.signalEvent("workItemAborted", workItem);
            }
            workItems.remove(id);
        }
    }

    @Override
    public void completeWorkItem( long l, Map<String, Object> map ) {
        throw new UnsupportedOperationException();
    }

    @Override
    public void abortWorkItem( long l ) {
        throw new UnsupportedOperationException();
    }

    @Override
    public Set<WorkItem> getWorkItems() {
        return new HashSet<>(workItems.values());
    }

    public void registerWorkItemHandler( String workItemName, WorkItemHandler handler) {
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

    public void dispose() {
        if (workItemHandlers != null) {
            for (Map.Entry<String, WorkItemHandler> handlerEntry : workItemHandlers.entrySet()) {
                if (handlerEntry.getValue() instanceof Closeable) {
                    ((Closeable) handlerEntry.getValue()).close();
                }
            }
        }
    }

    public void retryWorkItem( String workItemID, Map<String, Object> params ) {
       if (params==null || params.isEmpty()) {
           retryWorkItem(workItemID);
       } else {
           this.retryWorkItemWithParams( workItemID, params );
       }
    }

    @Override
    public void internalCompleteWorkItem( KogitoWorkItem workItem) {

    }
}
