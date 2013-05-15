package org.jbpm.examples.checklist.context;

import org.jbpm.examples.checklist.ChecklistContextConstraint;
import org.jbpm.examples.checklist.ChecklistItem;

public class ProcessInstanceIdContextConstraint implements ChecklistContextConstraint {
	
	private long processInstanceId;
	
	public ProcessInstanceIdContextConstraint(long processInstanceId) {
		this.processInstanceId = processInstanceId;
	}
	
	public boolean acceptsTask(ChecklistItem item) {
		return processInstanceId == item.getProcessInstanceId();
	}

}
