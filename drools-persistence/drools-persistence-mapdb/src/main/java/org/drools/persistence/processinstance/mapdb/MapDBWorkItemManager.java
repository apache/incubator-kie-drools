package org.drools.persistence.processinstance.mapdb;

import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;
import java.util.Set;

import org.drools.core.WorkItemHandlerNotFoundException;
import org.drools.core.common.InternalKnowledgeRuntime;
import org.drools.core.impl.InternalKnowledgeBase;
import org.drools.core.process.instance.WorkItem;
import org.drools.core.process.instance.WorkItemManager;
import org.drools.core.process.instance.impl.WorkItemImpl;
import org.drools.persistence.PersistenceContext;
import org.drools.persistence.PersistenceContextManager;
import org.drools.persistence.processinstance.InternalWorkItemManager;
import org.kie.api.runtime.Environment;
import org.kie.api.runtime.EnvironmentName;
import org.kie.api.runtime.process.ProcessInstance;
import org.kie.api.runtime.process.WorkItemHandler;
import org.kie.internal.runtime.Closeable;

public class MapDBWorkItemManager implements WorkItemManager, InternalWorkItemManager {

	private InternalKnowledgeRuntime kruntime;
    private Map<String, WorkItemHandler> workItemHandlers = new HashMap<String, WorkItemHandler>();
    private transient Map<Long, MapDBWorkItem> workItems;
	
	public MapDBWorkItemManager(InternalKnowledgeRuntime kruntime) {
		this.kruntime = kruntime;
	}

	private PersistenceContext getPersistenceContext() {
        Environment env = this.kruntime.getEnvironment();
        PersistenceContext context = ((PersistenceContextManager) env.get( EnvironmentName.PERSISTENCE_CONTEXT_MANAGER )).getCommandScopedPersistenceContext();
        return context;
    }

    private void throwWorkItemNotFoundException( WorkItem workItem ) {
        throw new WorkItemHandlerNotFoundException( "Could not find work item handler for " + workItem.getName(),
                workItem.getName() );
    }

	@Override
	public void completeWorkItem(long id, Map<String, Object> results) {
        PersistenceContext context = getPersistenceContext();

        MapDBWorkItem workItemInfo = null;
        if ( this.workItems != null ) {
            workItemInfo = this.workItems.get( id );
            if ( workItemInfo != null ) {
            	workItemInfo.transform();
                workItemInfo = (MapDBWorkItem) context.merge( workItemInfo );
            }
        }

        if ( workItemInfo == null ) {
            workItemInfo = (MapDBWorkItem) context.findWorkItem( id );
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

	private WorkItem internalGetWorkItem(MapDBWorkItem workItemInfo) {
		Environment env = kruntime.getEnvironment();
		WorkItem workItem = workItemInfo.getWorkItem( env, (InternalKnowledgeBase) kruntime.getKieBase() );
        return workItem;
	}

	@Override
	public void abortWorkItem(long id) {
        PersistenceContext context = getPersistenceContext();

        MapDBWorkItem workItemInfo = null;
        if ( this.workItems != null ) {
            workItemInfo = this.workItems.get( id );
            if ( workItemInfo != null ) {
                workItemInfo = (MapDBWorkItem) context.merge( workItemInfo );
            }
        }

        if ( workItemInfo == null ) {
            workItemInfo = (MapDBWorkItem) context.findWorkItem( id );
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

	@Override
	public void registerWorkItemHandler(String workItemName,
			WorkItemHandler handler) {
		this.workItemHandlers.put( workItemName, handler );
	}

	@Override
	public void clearWorkItems() {
        if ( workItems != null ) {
            workItems.clear();
        }
	}

	@Override
	public void internalExecuteWorkItem(WorkItem workItem) {
		Environment env = this.kruntime.getEnvironment();
        MapDBWorkItem workItemInfo = new MapDBWorkItem( workItem, env );

        PersistenceContext context = getPersistenceContext();
        workItemInfo = (MapDBWorkItem) context.persist( workItemInfo );

        ((WorkItemImpl) workItem).setId( workItemInfo.getId() );

        if ( this.workItems == null ) {
            this.workItems = new HashMap<Long, MapDBWorkItem>();
        }
        workItems.put( workItem.getId(), workItemInfo );

        WorkItemHandler handler = (WorkItemHandler) this.workItemHandlers.get( workItem.getName() );
        if ( handler != null ) {
            handler.executeWorkItem( workItem, this );
        } else {
            throwWorkItemNotFoundException( workItem );
        }
		
	}

	@Override
	public void internalAddWorkItem(WorkItem workItem) {
	}

	@Override
	public void internalAbortWorkItem(long id) {
        PersistenceContext context = getPersistenceContext();

        MapDBWorkItem workItemInfo = (MapDBWorkItem) context.findWorkItem( id );
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

	@Override
	public Set<WorkItem> getWorkItems() {
		return new HashSet<WorkItem>();
	}

	@Override
	public WorkItem getWorkItem(long id) {
        PersistenceContext context = getPersistenceContext();

        MapDBWorkItem workItemInfo = null;
        if ( this.workItems != null ) {
            workItemInfo = this.workItems.get( id );
        }

        if ( workItemInfo == null && context != null ) {
            workItemInfo = (MapDBWorkItem) context.findWorkItem( id );
        }

        if ( workItemInfo == null ) {
            return null;
        }
        return internalGetWorkItem( workItemInfo );
	}

	@Override
	public void clear() {
        clearWorkItems();
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

    public void retryWorkItemWithParams( long workItemId, Map<String, Object> map ) {
        Environment env = this.kruntime.getEnvironment();
        WorkItem workItem = getWorkItem( workItemId );
        if ( workItem != null ) {
            workItem.setParameters( map );
            MapDBWorkItem workItemInfo = new MapDBWorkItem( workItem, env );
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

	@Override
	public void retryWorkItem(Long workItemID, Map<String, Object> params) {
        if ( params == null || params.isEmpty() ) {
            retryWorkItem( workItemID );
        } else {
            this.retryWorkItemWithParams( workItemID, params );
        }
	}

}
