package org.jbpm.examples.checklist.context;

import org.jbpm.examples.checklist.ChecklistContextConstraint;
import org.jbpm.examples.checklist.ChecklistItem;

public class PotentialOwnerContextConstraint implements ChecklistContextConstraint {

	private String userId;
	
	public PotentialOwnerContextConstraint(String userId) {
		this.userId = userId;
	}
	
	public String getUserId() {
		return userId;
	}
	
	public boolean acceptsTask(ChecklistItem item) {
		String[] ss = item.getActors().split(",");
		for (String s: ss) {
			if (s.equals(userId)) {
				return true;
			}
		}
		return false;
	}

}
