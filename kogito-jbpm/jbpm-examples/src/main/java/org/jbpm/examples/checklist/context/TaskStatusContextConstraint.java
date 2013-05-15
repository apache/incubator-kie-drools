package org.jbpm.examples.checklist.context;

import java.util.List;

import org.jbpm.examples.checklist.ChecklistContextConstraint;
import org.jbpm.examples.checklist.ChecklistItem;
import org.jbpm.examples.checklist.ChecklistItem.Status;

public class TaskStatusContextConstraint implements ChecklistContextConstraint {
	
	private List<Status> statusses;
	
	public TaskStatusContextConstraint(List<Status> statusses) {
		if (statusses == null || statusses.size() == 0) {
			throw new IllegalArgumentException("Statusses cannot be empty");
		}
		this.statusses = statusses;
	}
	
	public boolean acceptsTask(ChecklistItem item) {
		return statusses.contains(item.getStatus());
	}

}
