package org.drools.persistence.session;

import org.drools.runtime.process.WorkItem;
import org.drools.runtime.process.WorkItemHandler;
import org.drools.runtime.process.WorkItemManager;

public class VariableCheckerTestWorkItemHandler implements WorkItemHandler {

	private static VariableCheckerTestWorkItemHandler INSTANCE = new VariableCheckerTestWorkItemHandler();
	
	private WorkItem workItem;
	
	private VariableCheckerTestWorkItemHandler() {
	}
	
	public static VariableCheckerTestWorkItemHandler getInstance() {
		return INSTANCE;
	}
	
	public void executeWorkItem(WorkItem workItem, WorkItemManager manager) {
		this.workItem = workItem;
	}

	public void abortWorkItem(WorkItem workItem, WorkItemManager manager) {
	}
	
	public WorkItem getWorkItem() {
		WorkItem result = workItem;
		workItem = null;
		return result;
	}

}
