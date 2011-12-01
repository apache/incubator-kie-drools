package org.drools.persistence.jpa.processinstance;

import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;
import java.util.Set;

import javax.persistence.EntityManager;

import org.drools.WorkItemHandlerNotFoundException;
import org.drools.common.InternalKnowledgeRuntime;
import org.drools.common.InternalRuleBase;
import org.drools.impl.KnowledgeBaseImpl;
import org.drools.persistence.PersistenceContext;
import org.drools.persistence.PersistenceContextManager;
import org.drools.persistence.info.WorkItemInfo;
import org.drools.process.instance.WorkItem;
import org.drools.process.instance.WorkItemManager;
import org.drools.process.instance.impl.WorkItemImpl;
import org.drools.runtime.Environment;
import org.drools.runtime.EnvironmentName;
import org.drools.runtime.process.ProcessInstance;
import org.drools.runtime.process.WorkItemHandler;

public class JPAWorkItemManager implements WorkItemManager {

    private InternalKnowledgeRuntime kruntime;
    private Map<String, WorkItemHandler> workItemHandlers = new HashMap<String, WorkItemHandler>();
    private transient Map<Long, WorkItemInfo> workItems;
    
    public JPAWorkItemManager(InternalKnowledgeRuntime kruntime) {
        this.kruntime = kruntime;
    }
    
    public void internalExecuteWorkItem(WorkItem workItem) {
        Environment env = this.kruntime.getEnvironment();
//        EntityManager em = (EntityManager) env.get(EnvironmentName.CMD_SCOPED_ENTITY_MANAGER);

        WorkItemInfo workItemInfo = new WorkItemInfo(workItem, env);
//        em.persist(workItemInfo);
        
        PersistenceContext context = ((PersistenceContextManager) env.get( EnvironmentName.PERSISTENCE_CONTEXT_MANAGER )).getCommandScopedPersistenceContext();
        context.persist( workItemInfo );

        ((WorkItemImpl) workItem).setId(workItemInfo.getId());
        workItemInfo.update();
        
        if (this.workItems == null) {
            this.workItems = new HashMap<Long, WorkItemInfo>();
        }
        workItems.put(workItem.getId(), workItemInfo);
        
        WorkItemHandler handler = (WorkItemHandler) this.workItemHandlers.get(workItem.getName());
        if (handler != null) {
            handler.executeWorkItem(workItem, this);
        } else {
            throwWorkItemNotFoundException( workItem );
        }
    }

    private void throwWorkItemNotFoundException(WorkItem workItem) {
        throw new WorkItemHandlerNotFoundException( "Could not find work item handler for " + workItem.getName(),
                                                    workItem.getName() );
    }

    public void internalAbortWorkItem(long id) {
        Environment env = this.kruntime.getEnvironment();
        //EntityManager em = (EntityManager) env.get(EnvironmentName.CMD_SCOPED_ENTITY_MANAGER);
        PersistenceContext context = ((PersistenceContextManager) env.get( EnvironmentName.PERSISTENCE_CONTEXT_MANAGER )).getCommandScopedPersistenceContext();

        
        WorkItemInfo workItemInfo = context.findWorkItemInfo( id );
        // work item may have been aborted
        if (workItemInfo != null) {
            WorkItemImpl workItem = (WorkItemImpl) internalGetWorkItem(workItemInfo);
            WorkItemHandler handler = (WorkItemHandler) this.workItemHandlers.get(workItem.getName());
            if (handler != null) {
                handler.abortWorkItem(workItem, this);
            } else {
                if ( workItems != null ) {
                    workItems.remove( id );
                    throwWorkItemNotFoundException( workItem );
                }
            }
            if (workItems != null) {
                workItems.remove(id);
            }
            context.remove(workItemInfo);
        }
    }

    public void internalAddWorkItem(WorkItem workItem) {
    }

    public void completeWorkItem(long id, Map<String, Object> results) {
        Environment env = this.kruntime.getEnvironment();
//        EntityManager em = (EntityManager) env.get(EnvironmentName.CMD_SCOPED_ENTITY_MANAGER);
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
        
        // work item may have been aborted
        if (workItemInfo != null) {
            WorkItem workItem = internalGetWorkItem(workItemInfo);
            workItem.setResults(results);
            ProcessInstance processInstance = kruntime.getProcessInstance(workItem.getProcessInstanceId());
            workItem.setState(WorkItem.COMPLETED);
            // process instance may have finished already
            if (processInstance != null) {
                processInstance.signalEvent("workItemCompleted", workItem);
            }
            context.remove(workItemInfo);
            if (workItems != null) {
                this.workItems.remove(workItem.getId());
            }
        }
    }

    public void abortWorkItem(long id) {
        Environment env = this.kruntime.getEnvironment();
//        EntityManager em = (EntityManager) env.get(EnvironmentName.CMD_SCOPED_ENTITY_MANAGER);
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
        
        // work item may have been aborted
        if (workItemInfo != null) {
            WorkItem workItem = (WorkItemImpl) internalGetWorkItem(workItemInfo);
            ProcessInstance processInstance = kruntime.getProcessInstance(workItem.getProcessInstanceId());
            workItem.setState(WorkItem.ABORTED);
            // process instance may have finished already
            if (processInstance != null) {
                processInstance.signalEvent("workItemAborted", workItem);
            }
            context.remove(workItemInfo);
            if (workItems != null) {
                workItems.remove(workItem.getId());
            }
        }
    }

    public WorkItem getWorkItem(long id) {
        Environment env = this.kruntime.getEnvironment();
//        EntityManager em = (EntityManager) env.get(EnvironmentName.CMD_SCOPED_ENTITY_MANAGER);
        PersistenceContext context = ((PersistenceContextManager) env.get( EnvironmentName.PERSISTENCE_CONTEXT_MANAGER )).getCommandScopedPersistenceContext();
        
        WorkItemInfo workItemInfo = null;
        if (this.workItems != null) {
            workItemInfo = this.workItems.get(id);
        }
        
        if (workItemInfo == null && context != null) {

            workItemInfo = context.findWorkItemInfo( id );
        }

        if (workItemInfo == null) {
            return null;
        }
        return internalGetWorkItem(workItemInfo);
    }

    private WorkItem internalGetWorkItem(WorkItemInfo workItemInfo) { 
        Environment env = kruntime.getEnvironment();
        InternalRuleBase ruleBase = (InternalRuleBase) ((KnowledgeBaseImpl) kruntime.getKnowledgeBase()).getRuleBase();
        WorkItem workItem = workItemInfo.getWorkItem(env, ruleBase); 
        return workItem;
    }
    
    public Set<WorkItem> getWorkItems() {
        return new HashSet<WorkItem>();
    }

    public void registerWorkItemHandler(String workItemName, WorkItemHandler handler) {
        this.workItemHandlers.put(workItemName, handler);
    }

    public void clearWorkItems() {
        if (workItems != null) {
            workItems.clear();
        }
    }

    public void clear() {
        clearWorkItems();
    }
}
