package org.jbpm.examples.checklist;

public interface ChecklistItem {
	
	String getName();
	
	Status getStatus();
	
	Long getTaskId();
	
	String getType();
	
	String getActors();
	
	long getPriority();
	
	String getProcessId();
	
	Long getProcessInstanceId();
	
	String getOrderingNb();
	
	public enum Status {
		Pending,
		Optional,
		Created,
		Ready,
		Reserved,
		InProgress,
		Completed,
		Aborted
	}
}
