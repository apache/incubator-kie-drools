package org.jbpm.bpmn2.objects;

import org.kie.api.runtime.process.WorkItem;
import org.kie.api.runtime.process.WorkItemHandler;
import org.kie.api.runtime.process.WorkItemManager;

public class ExceptionOnPurposeHandler implements WorkItemHandler {
	
	@Override
	public void executeWorkItem(WorkItem workItem, WorkItemManager manager) {
        throw new RuntimeException("Thrown on purpose");
	}
	
	@Override
	public void abortWorkItem(WorkItem workItem, WorkItemManager manager) {
	}
}
