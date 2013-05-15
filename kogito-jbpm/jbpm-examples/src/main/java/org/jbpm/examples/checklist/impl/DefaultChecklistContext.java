package org.jbpm.examples.checklist.impl;

import org.jbpm.examples.checklist.ChecklistContext;

public class DefaultChecklistContext implements ChecklistContext {
	
	private String name;
	private long processInstanceId;
	
	public String getName() {
		return name;
	}
	
	public void setName(String name) {
		this.name = name;
	}
	
	public long getProcessInstanceId() {
		return processInstanceId;
	}
	
	public void setProcessInstanceId(long processInstanceId) {
		this.processInstanceId = processInstanceId;
	}

}