package org.jbpm.persistence.session.objects;

import org.kie.runtime.process.WorkItem;
import org.kie.runtime.process.WorkItemHandler;
import org.kie.runtime.process.WorkItemManager;

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
