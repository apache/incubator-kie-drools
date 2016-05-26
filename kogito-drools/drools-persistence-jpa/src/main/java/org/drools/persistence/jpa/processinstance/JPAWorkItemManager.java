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

    private InternalKnowledgeRuntime kruntime;
    private Map<String, WorkItemHandler> workItemHandlers = new HashMap<String, WorkItemHandler>();
    private transient Map<Long, WorkItemInfo> workItems;
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
            throwWorkItemNotFoundException( workItem );
        }
    }

    private void throwWorkItemNotFoundException( WorkItem workItem ) {
        throw new WorkItemHandlerNotFoundException( "Could not find work item handler for " + workItem.getName(),
                workItem.getName() );
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
                throwWorkItemNotFoundException( workItem );
            }
        }
    }

    public void internalAbortWorkItem( long id ) {
        PersistenceContext context = getPersistenceContext();

        WorkItemInfo workItemInfo = context.findWorkItemInfo( id );
        // work item may have been aborted
        if ( workItemInfo != null ) {
            WorkItemImpl workItem = (WorkItemImpl) internalGetWorkItem( workItemInfo );
            WorkItemHandler handler = (WorkItemHandler) this.workItemHandlers.get( workItem.getName() );
            if ( handler != null ) {
                handler.abortWorkItem( workItem, this );
            } else {
                if ( workItems != null ) {
                    workItems.remove( id );
                    throwWorkItemNotFoundException( workItem );
                }
            }
            if ( workItems != null ) {
                workItems.remove( id );
            }
            context.remove( workItemInfo );
        }
    }

    private PersistenceContext getPersistenceContext() {
        Environment env = this.kruntime.getEnvironment();
        PersistenceContext context = ((PersistenceContextManager) env.get( EnvironmentName.PERSISTENCE_CONTEXT_MANAGER )).getCommandScopedPersistenceContext();
        return context;
    }

    public void internalAddWorkItem( WorkItem workItem ) {
    }

    public void completeWorkItem( long id, Map<String, Object> results ) {
        PersistenceContext context = getPersistenceContext();

        WorkItemInfo workItemInfo = null;
        if ( this.workItems != null ) {
            workItemInfo = this.workItems.get( id );
            if ( workItemInfo != null ) {
                workItemInfo = context.merge( workItemInfo );
            }
        }

        if ( workItemInfo == null ) {
            workItemInfo = context.findWorkItemInfo( id );
        }

        // work item may have been aborted
        if ( workItemInfo != null ) {
            WorkItem workItem = internalGetWorkItem( workItemInfo );
            workItem.setResults( results );
            ProcessInstance processInstance = kruntime.getProcessInstance( workItem.getProcessInstanceId() );
            workItem.setState( WorkItem.COMPLETED );
            // process instance may have finished already
            if ( processInstance != null ) {
                processInstance.signalEvent( "workItemCompleted", workItem );
            }
            context.remove( workItemInfo );
            if ( workItems != null ) {
                this.workItems.remove( workItem.getId() );
            }
        }
    }

    public void abortWorkItem( long id ) {
        PersistenceContext context = getPersistenceContext();

        WorkItemInfo workItemInfo = null;
        if ( this.workItems != null ) {
            workItemInfo = this.workItems.get( id );
            if ( workItemInfo != null ) {
                workItemInfo = context.merge( workItemInfo );
            }
        }

        if ( workItemInfo == null ) {
            workItemInfo = context.findWorkItemInfo( id );
        }

        // work item may have been aborted
        if ( workItemInfo != null ) {
            WorkItem workItem = (WorkItemImpl) internalGetWorkItem( workItemInfo );
            ProcessInstance processInstance = kruntime.getProcessInstance( workItem.getProcessInstanceId() );
            workItem.setState( WorkItem.ABORTED );
            // process instance may have finished already
            if ( processInstance != null ) {
                processInstance.signalEvent( "workItemAborted", workItem );
            }
            context.remove( workItemInfo );
            if ( workItems != null ) {
                workItems.remove( workItem.getId() );
            }
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

    private WorkItem internalGetWorkItem( WorkItemInfo workItemInfo ) {
        Environment env = kruntime.getEnvironment();
        WorkItem workItem = workItemInfo.getWorkItem( env, (InternalKnowledgeBase) kruntime.getKieBase() );
        return workItem;
    }

    public Set<WorkItem> getWorkItems() {
        return new HashSet<WorkItem>();
    }

    public void registerWorkItemHandler( String workItemName, WorkItemHandler handler ) {
        this.workItemHandlers.put( workItemName, handler );
    }

    public void clearWorkItems() {
        if ( workItems != null ) {
            workItems.clear();
        }
    }

    public void clear() {
        clearWorkItems();
    }

    public void signalEvent( String type, Object event ) {
        this.kruntime.signalEvent( type, event );
    }

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
