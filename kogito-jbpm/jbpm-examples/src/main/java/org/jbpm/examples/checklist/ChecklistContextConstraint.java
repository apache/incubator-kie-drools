package org.jbpm.examples.checklist;


public interface ChecklistContextConstraint {
	
	boolean acceptsTask(ChecklistItem item);

}
