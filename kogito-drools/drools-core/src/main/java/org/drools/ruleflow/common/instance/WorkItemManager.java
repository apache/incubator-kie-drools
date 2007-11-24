package org.drools.ruleflow.common.instance;

import java.util.Collections;
import java.util.HashMap;
import java.util.Map;
import java.util.Set;

import org.drools.WorkingMemory;
import org.drools.ruleflow.common.instance.impl.WorkItemImpl;

public class WorkItemManager {

	private long taskInstanceCounter; 
	private Map taskInstances = new HashMap();
	private WorkingMemory workingMemory;
	private Map taskInstanceHandlers = new HashMap();
	
	public WorkItemManager(WorkingMemory workingMemory) {
	    this.workingMemory = workingMemory;
	}
	
	public void executeWorkItem(WorkItem workItem) {
	    ((WorkItemImpl) workItem).setId(++taskInstanceCounter);
	    taskInstances.put(new Long(workItem.getId()), workItem);
	    WorkItemHandler handler = (WorkItemHandler) this.taskInstanceHandlers.get(workItem.getName());
	    if (handler != null) {
	        handler.executeWorkItem(workItem, this);
	    } else {
	        System.err.println("Could not find work item handler for " + workItem.getName());
	    }
	}
	
	public Set getWorkItems() {
	    return Collections.unmodifiableSet(taskInstances.entrySet());
	}
	
    public void completeWorkItem(long id, Map results) {
        WorkItemImpl taskInstance = (WorkItemImpl) taskInstances.get(new Long(id));
        if (taskInstance == null) {
            throw new IllegalArgumentException(
                "Could not find task instance with id " + id);
        }
        taskInstance.setResults(results);
        ProcessInstance processInstance = workingMemory.getProcessInstance(taskInstance.getProcessInstanceId());
        if (processInstance == null) {
            throw new IllegalArgumentException(
                "Could not find processInstance with id " + taskInstance.getProcessInstanceId());
        }
        taskInstance.setState(WorkItem.COMPLETED);
        processInstance.taskCompleted(taskInstance);
        taskInstances.remove(new Long(id));
    }
    
    public void abortWorkItem(long id) {
        WorkItemImpl taskInstance = (WorkItemImpl) taskInstances.get(new Long(id));
        if (taskInstance == null) {
            throw new IllegalArgumentException(
                "Could not find task instance with id " + id);
        }
        ProcessInstance processInstance = workingMemory.getProcessInstance(taskInstance.getProcessInstanceId());
        if (processInstance == null) {
            throw new IllegalArgumentException(
                "Could not find processInstance with id " + taskInstance.getProcessInstanceId());
        }
        taskInstance.setState(WorkItem.ABORTED);
        processInstance.taskAborted(taskInstance);
        taskInstances.remove(new Long(id));
    }
    
    public void registerWorkItemHandler(String workItemName, WorkItemHandler handler) {
        this.taskInstanceHandlers.put(workItemName, handler);
    }

}
