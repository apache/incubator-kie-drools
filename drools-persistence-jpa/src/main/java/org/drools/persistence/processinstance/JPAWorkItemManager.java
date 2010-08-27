package org.drools.persistence.processinstance;

import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;
import java.util.Set;

import javax.persistence.EntityManager;

import org.drools.WorkingMemory;
import org.drools.process.instance.WorkItem;
import org.drools.process.instance.WorkItemManager;
import org.drools.process.instance.impl.WorkItemImpl;
import org.drools.runtime.Environment;
import org.drools.runtime.EnvironmentName;
import org.drools.runtime.process.ProcessInstance;
import org.drools.runtime.process.WorkItemHandler;

public class JPAWorkItemManager implements WorkItemManager {

    private WorkingMemory workingMemory;
	private Map<String, WorkItemHandler> workItemHandlers = new HashMap<String, WorkItemHandler>();
    private transient Map<Long, WorkItemInfo> workItems;
    
    public JPAWorkItemManager(WorkingMemory workingMemory) {
    	this.workingMemory = workingMemory;
    }
    
	public void internalExecuteWorkItem(WorkItem workItem) {
        Environment env = this.workingMemory.getEnvironment();
        EntityManager em = (EntityManager) env.get(EnvironmentName.CMD_SCOPED_ENTITY_MANAGER);
	    
        WorkItemInfo workItemInfo = new WorkItemInfo(workItem, env);
        em.persist(workItemInfo);
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
	        System.err.println("Could not find work item handler for " + workItem.getName());
	    }
	}

	public void internalAbortWorkItem(long id) {
        Environment env = this.workingMemory.getEnvironment();
        EntityManager em = (EntityManager) env.get(EnvironmentName.CMD_SCOPED_ENTITY_MANAGER);
	    
        WorkItemInfo workItemInfo = em.find(WorkItemInfo.class, id);
        // work item may have been aborted
        if (workItemInfo != null) {
            WorkItemImpl workItem = (WorkItemImpl) workItemInfo.getWorkItem(env);
            WorkItemHandler handler = (WorkItemHandler) this.workItemHandlers.get(workItem.getName());
            if (handler != null) {
                handler.abortWorkItem(workItem, this);
            } else {
                System.err.println("Could not find work item handler for " + workItem.getName());
            }
            if (workItems != null) {
            	workItems.remove(id);
            }
            em.remove(workItemInfo);
        }
	}

	public void internalAddWorkItem(WorkItem workItem) {
	}

    public void completeWorkItem(long id, Map<String, Object> results) {
        Environment env = this.workingMemory.getEnvironment();
        EntityManager em = (EntityManager) env.get(EnvironmentName.CMD_SCOPED_ENTITY_MANAGER);
        
        WorkItemInfo workItemInfo = null;
        if (this.workItems != null) {
	    	workItemInfo = this.workItems.get(id);
	    	if (workItemInfo != null) {
	    		workItemInfo = em.merge(workItemInfo);
	    	}
    	}
        
        if (workItemInfo == null) {
        	workItemInfo = em.find(WorkItemInfo.class, id);
        }
        
    	// work item may have been aborted
        if (workItemInfo != null) {
            WorkItem workItem = (WorkItemImpl) workItemInfo.getWorkItem(env);
            workItem.setResults(results);
            ProcessInstance processInstance = workingMemory.getProcessInstance(workItem.getProcessInstanceId());
            workItem.setState(WorkItem.COMPLETED);
            // process instance may have finished already
            if (processInstance != null) {
                processInstance.signalEvent("workItemCompleted", workItem);
            }
            em.remove(workItemInfo);
            if (workItems != null) {
            	this.workItems.remove(workItem.getId());
            }
            workingMemory.fireAllRules();
    	}
    }

    public void abortWorkItem(long id) {
        Environment env = this.workingMemory.getEnvironment();
        EntityManager em = (EntityManager) env.get(EnvironmentName.CMD_SCOPED_ENTITY_MANAGER);
        
        WorkItemInfo workItemInfo = null;
        if (this.workItems != null) {
	    	workItemInfo = this.workItems.get(id);
	    	em.merge(workItemInfo);
    	}
        
        if (workItemInfo == null) {
        	workItemInfo = em.find(WorkItemInfo.class, id);
        }
        
    	// work item may have been aborted
        if (workItemInfo != null) {
            WorkItem workItem = (WorkItemImpl) workItemInfo.getWorkItem(env);
            ProcessInstance processInstance = workingMemory.getProcessInstance(workItem.getProcessInstanceId());
            workItem.setState(WorkItem.ABORTED);
            // process instance may have finished already
            if (processInstance != null) {
                processInstance.signalEvent("workItemAborted", workItem);
            }
            em.remove(workItemInfo);
            if (workItems != null) {
            	workItems.remove(workItem.getId());
            }
            workingMemory.fireAllRules();
        }
    }

	public WorkItem getWorkItem(long id) {
        Environment env = this.workingMemory.getEnvironment();
        EntityManager em = (EntityManager) env.get(EnvironmentName.CMD_SCOPED_ENTITY_MANAGER);

        WorkItemInfo workItemInfo = null;
        if (this.workItems != null) {
	    	workItemInfo = this.workItems.get(id);
    	}
        
        if (workItemInfo == null && em != null) {

        	workItemInfo = em.find(WorkItemInfo.class, id);
        }

        if (workItemInfo == null) {
            return null;
        }
        return workItemInfo.getWorkItem(env);
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
}
