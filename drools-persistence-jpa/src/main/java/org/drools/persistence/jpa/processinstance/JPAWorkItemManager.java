/*
 * Copyright 2015 Red Hat, Inc. and/or its affiliates.
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

package org.drools.persistence.jpa.processinstance;

import org.drools.core.WorkItemHandlerNotFoundException;
import org.drools.core.common.InternalKnowledgeRuntime;
import org.drools.core.impl.InternalKnowledgeBase;
import org.drools.core.process.instance.WorkItem;
import org.drools.core.process.instance.WorkItemManager;
import org.drools.core.process.instance.impl.WorkItemImpl;
import org.drools.persistence.PersistenceContext;
import org.drools.persistence.PersistenceContextManager;
import org.drools.persistence.info.WorkItemInfo;
import org.kie.api.runtime.Environment;
import org.kie.api.runtime.EnvironmentName;
import org.kie.api.runtime.process.ProcessInstance;
import org.kie.api.runtime.process.WorkItemHandler;
import org.kie.internal.runtime.Closeable;

import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;
import java.util.Set;

public class JPAWorkItemManager implements WorkItemManager {

    protected InternalKnowledgeRuntime kruntime;
    private Map<String, WorkItemHandler> workItemHandlers = new HashMap<String, WorkItemHandler>();
    protected transient Map<Long, WorkItemInfo> workItems;
    private transient volatile boolean pessimisticLocking;

    public JPAWorkItemManager(InternalKnowledgeRuntime kruntime) {
        this.kruntime = kruntime;
        Boolean locking = (Boolean) this.kruntime.getEnvironment().get( EnvironmentName.USE_PESSIMISTIC_LOCKING );
        if ( locking != null && locking ) {
            this.pessimisticLocking = locking;
        }
    }

    public void internalExecuteWorkItem( WorkItem workItem ) {
        Environment env = this.kruntime.getEnvironment();
        WorkItemInfo workItemInfo = new WorkItemInfo( workItem, env );

        PersistenceContext context = getPersistenceContext();
        workItemInfo = context.persist( workItemInfo );

        ((WorkItemImpl) workItem).setId( workItemInfo.getId() );

        if ( this.workItems == null ) {
            this.workItems = new HashMap<Long, WorkItemInfo>();
        }
        workItems.put( workItem.getId(), workItemInfo );

        WorkItemHandler handler = (WorkItemHandler) this.workItemHandlers.get( workItem.getName() );
        if ( handler != null ) {
            handler.executeWorkItem( workItem, this );
        } else {
            throwWorkItemHandlerNotFoundException( workItem );
        }
    }

    public WorkItemHandler getWorkItemHandler( String name ) {
        return this.workItemHandlers.get( name );
    }

    public void retryWorkItemWithParams( long workItemId, Map<String, Object> map ) {
        Environment env = this.kruntime.getEnvironment();
        WorkItem workItem = getWorkItem( workItemId );
        if ( workItem != null ) {
            workItem.setParameters( map );
            WorkItemInfo workItemInfo = new WorkItemInfo( workItem, env );
            PersistenceContext context = getPersistenceContext();
            context.merge( workItemInfo );
            retryWorkItem( workItem );
        }
    }

    public void retryWorkItem( long workItemId ) {
        WorkItem workItem = getWorkItem( workItemId );
        retryWorkItem( workItem );
    }

    private void retryWorkItem( WorkItem workItem ) {
        if ( workItem != null ) {
            WorkItemHandler handler = (WorkItemHandler) this.workItemHandlers.get( workItem.getName() );
            if ( handler != null ) {
                handler.executeWorkItem( workItem, this );
            } else {
                throwWorkItemHandlerNotFoundException( workItem );
            }
        }
    }

    /**
     * This method is called by jBPM when WorkItemNodeInstances are cancelled
     */
    @Override
    public void internalAbortWorkItem( long id ) {
        PersistenceContext context = getPersistenceContext();

        WorkItemInfo workItemInfo = context.findWorkItemInfo( id );
        // work item may have been aborted
        if ( workItemInfo != null ) {
            WorkItemImpl workItem = (WorkItemImpl) internalGetWorkItem( workItemInfo );

            WorkItemHandler handler = (WorkItemHandler) this.workItemHandlers.get( workItem.getName() );
            internalAbortAndRemoveWorkItem(handler, workItemInfo, workItem);
        }
    }

    /**
     * This method is overridden by the jBPM WorkItemManager implementation
     */
    protected void internalAbortAndRemoveWorkItem( WorkItemHandler handler, WorkItemInfo workItemInfo, WorkItem workItem ) {
        if ( handler != null ) {
            handler.abortWorkItem( workItem, this );
        }
        removeWorkItem(workItemInfo, workItem.getId());
        if( workItems != null && handler == null ) {
            throwWorkItemHandlerNotFoundException( workItem );
        }
    }

    private PersistenceContext getPersistenceContext() {
        Environment env = this.kruntime.getEnvironment();
        PersistenceContext context = ((PersistenceContextManager) env.get( EnvironmentName.PERSISTENCE_CONTEXT_MANAGER )).getCommandScopedPersistenceContext();
        return context;
    }

    public void internalAddWorkItem( WorkItem workItem ) {
    }

    public void completeWorkItem(long id, Map<String, Object> results) {
        WorkItemInfo workItemInfo = getWorkItemInfo(id);

        // work item may have been aborted
        if (workItemInfo != null) {
            WorkItem workItem = internalGetWorkItem(workItemInfo);
            workItem.setResults(results);
            workItem.setState(WorkItem.COMPLETED);

            ProcessInstance processInstance = kruntime.getProcessInstance(workItem.getProcessInstanceId());
            signalEventAndRemoveWorkItem(processInstance, "workItemCompleted", workItem, workItemInfo);
        }
    }

    public void abortWorkItem(long id) {
        WorkItemInfo workItemInfo = getWorkItemInfo(id);

        // work item may have been aborted
        if (workItemInfo != null) {
            WorkItem workItem = (WorkItemImpl) internalGetWorkItem(workItemInfo);
            workItem.setState(WorkItem.ABORTED);

            ProcessInstance processInstance = kruntime.getProcessInstance(workItem.getProcessInstanceId());
            signalEventAndRemoveWorkItem(processInstance, "workItemAborted", workItem, workItemInfo);
        }
    }

    /**
     * This method is overridden by the jBPM WorkItemManager implementation
     */
    protected void signalEventAndRemoveWorkItem(ProcessInstance processInstance, String event, WorkItem workItem, WorkItemInfo workItemInfo) {
        // process instance may have finished already
        if (processInstance != null) {
            processInstance.signalEvent(event, workItem);
        }
        removeWorkItem(workItemInfo, workItem.getId());
    }

    protected void removeWorkItem(WorkItemInfo workItemInfo, long workItemId) {
        PersistenceContext context = getPersistenceContext();
        context.remove(workItemInfo);
        if (workItems != null) {
            workItems.remove(workItemId);
        }
    }

    public WorkItem getWorkItem( long id ) {
        PersistenceContext context = getPersistenceContext();

        WorkItemInfo workItemInfo = null;
        if ( this.workItems != null ) {
            workItemInfo = this.workItems.get( id );
        }

        if ( this.pessimisticLocking && workItemInfo != null ) {
            context.lock( workItemInfo );
        }

        if ( workItemInfo == null && context != null ) {
            workItemInfo = context.findWorkItemInfo( id );
        }

        if ( workItemInfo == null ) {
            return null;
        }
        return internalGetWorkItem( workItemInfo );
    }

    protected WorkItemInfo getWorkItemInfo(long id) {
        Environment env = this.kruntime.getEnvironment();
        PersistenceContext context = ((PersistenceContextManager) env.get( EnvironmentName.PERSISTENCE_CONTEXT_MANAGER )).getCommandScopedPersistenceContext();

        WorkItemInfo workItemInfo = null;
        if (this.workItems != null) {
            workItemInfo = this.workItems.get(id);
            if (workItemInfo != null) {
                workItemInfo = context.merge(workItemInfo);
            }
        }

        if (workItemInfo == null) {
            workItemInfo = context.findWorkItemInfo( id );
        }
        return workItemInfo;
    }

    protected WorkItem internalGetWorkItem( WorkItemInfo workItemInfo ) {
        Environment env = kruntime.getEnvironment();
        WorkItem workItem = workItemInfo.getWorkItem( env, (InternalKnowledgeBase) kruntime.getKieBase() );
        return workItem;
    }

    @Override
    public Set<WorkItem> getWorkItems() {
        return new HashSet<WorkItem>();
    }

    @Override
    public void registerWorkItemHandler( String workItemName, WorkItemHandler handler ) {
        this.workItemHandlers.put( workItemName, handler );
    }

    @Override
    public void clear() {
        if ( workItems != null ) {
            workItems.clear();
        }
    }

    @Override
    public void signalEvent( String type, Object event ) {
        this.kruntime.signalEvent( type, event );
    }

    @Override
    public void signalEvent( String type, Object event, long processInstanceId ) {
        this.kruntime.signalEvent( type, event, processInstanceId );
    }

    @Override
    public void dispose() {
        if ( workItemHandlers != null ) {
            for ( Map.Entry<String, WorkItemHandler> handlerEntry : workItemHandlers.entrySet() ) {
                if ( handlerEntry.getValue() instanceof Closeable ) {
                    ((Closeable) handlerEntry.getValue()).close();
                }
            }
        }
    }

    @Override
    public void retryWorkItem( Long workItemID, Map<String, Object> params ) {
        if ( params == null || params.isEmpty() ) {
            retryWorkItem( workItemID );
        } else {
            this.retryWorkItemWithParams( workItemID, params );
        }

    }
}
