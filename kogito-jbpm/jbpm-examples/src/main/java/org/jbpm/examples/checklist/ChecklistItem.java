package org.jbpm.examples.checklist;

public interface ChecklistItem {
	
	String getName();
	
	Status getStatus();
	
	Long getTaskId();
	
	String getActors();
	
	long getPriority();
	
	String getProcessId();
	
	Long getProcessInstanceId();
	
	String getOrderingNb();
	
	public enum Status {
		Pending,
		Created,
		Reserved,
		InProgress,
		Completed,
		Aborted
	}
}
