package org.drools.process.instance;

import java.io.Serializable;
import java.io.Externalizable;
import java.io.IOException;
import java.io.ObjectOutput;
import java.io.ObjectInput;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;
import java.util.Set;

import org.drools.WorkingMemory;
import org.drools.process.instance.impl.WorkItemImpl;

/**
 *
 * @author <a href="mailto:kris_verlaenen@hotmail.com">Kris Verlaenen</a>
 */
public class WorkItemManager implements Externalizable {

    private static final long serialVersionUID = 400L;

    private long workItemCounter;
	private Map<Long, WorkItem> workItems = new HashMap<Long, WorkItem>();
	private WorkingMemory workingMemory;
	private Map<String, WorkItemHandler> workItemHandlers = new HashMap<String, WorkItemHandler>();

    public WorkItemManager() {

    }
    public WorkItemManager(WorkingMemory workingMemory) {
	    this.workingMemory = workingMemory;
	}

    public void readExternal(ObjectInput in) throws IOException, ClassNotFoundException {
        workItemCounter = in.readLong();
        workItems   = (Map<Long, WorkItem>)in.readObject();
        workingMemory   = (WorkingMemory)in.readObject();
        workItemHandlers   = (Map<String, WorkItemHandler>)in.readObject();
    }

    public void writeExternal(ObjectOutput out) throws IOException {
        out.writeLong(workItemCounter);
        out.writeObject(workItems);
        out.writeObject(workingMemory);
        out.writeObject(workItemHandlers);
    }

	public void executeWorkItem(WorkItem workItem) {
	    ((WorkItemImpl) workItem).setId(++workItemCounter);
	    workItems.put(new Long(workItem.getId()), workItem);
	    WorkItemHandler handler = (WorkItemHandler) this.workItemHandlers.get(workItem.getName());
	    if (handler != null) {
	        handler.executeWorkItem(workItem, this);
	    } else {
	        System.err.println("Could not find work item handler for " + workItem.getName());
	    }
	}

	public Set<WorkItem> getWorkItems() {
	    return new HashSet<WorkItem>(workItems.values());

	}

    public void completeWorkItem(long id, Map<String, Object> results) {
        WorkItemImpl workItem = (WorkItemImpl) workItems.get(new Long(id));
        if (workItem == null) {
            throw new IllegalArgumentException(
                "Could not find work item with id " + id);
        }
        workItem.setResults(results);
        ProcessInstance processInstance = workingMemory.getProcessInstance(workItem.getProcessInstanceId());
        if (processInstance == null) {
            throw new IllegalArgumentException(
                "Could not find processInstance with id " + workItem.getProcessInstanceId());
        }
        workItem.setState(WorkItem.COMPLETED);
        processInstance.workItemCompleted(workItem);
        workItems.remove(new Long(id));
    }

    public void abortWorkItem(long id) {
        WorkItemImpl workItem = (WorkItemImpl) workItems.get(new Long(id));
        if (workItem == null) {
            throw new IllegalArgumentException(
                "Could not find work item with id " + id);
        }
        ProcessInstance processInstance = workingMemory.getProcessInstance(workItem.getProcessInstanceId());
        if (processInstance == null) {
            throw new IllegalArgumentException(
                "Could not find processInstance with id " + workItem.getProcessInstanceId());
        }
        workItem.setState(WorkItem.ABORTED);
        processInstance.workItemAborted(workItem);
        workItems.remove(new Long(id));
    }

    public void registerWorkItemHandler(String workItemName, WorkItemHandler handler) {
        this.workItemHandlers.put(workItemName, handler);
    }

}
