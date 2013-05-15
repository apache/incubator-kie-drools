package org.jbpm.examples.checklist.context;

import org.jbpm.examples.checklist.ChecklistContextConstraint;
import org.jbpm.examples.checklist.ChecklistItem;

public class ProcessIdContextConstraint implements ChecklistContextConstraint {
	
	private String processId;
	
	public ProcessIdContextConstraint(String processId) {
		if (processId == null) {
			throw new IllegalArgumentException("ProcessId cannot be null");
		}
		this.processId = processId;
	}
	
	public boolean acceptsTask(ChecklistItem item) {
		return processId.equals(item.getProcessId());
	}

}
