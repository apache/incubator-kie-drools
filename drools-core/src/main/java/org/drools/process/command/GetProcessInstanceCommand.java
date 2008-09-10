package org.drools.process.command;

import org.drools.WorkingMemory;

public class GetProcessInstanceCommand implements Command {
	
	private Long processInstanceId;
	
	public Long getProcessInstanceId() {
		return processInstanceId;
	}
	
	public void setProcessInstanceId(Long processInstanceId) {
		this.processInstanceId = processInstanceId;
	}
	
	public Object execute(WorkingMemory workingMemory) {
		if (processInstanceId == null) {
			return null;
		}
		return workingMemory.getProcessInstance(processInstanceId);
	}

}
