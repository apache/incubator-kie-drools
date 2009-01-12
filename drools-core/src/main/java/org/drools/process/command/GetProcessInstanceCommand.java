package org.drools.process.command;

import org.drools.WorkingMemory;
import org.drools.process.instance.ProcessInstance;

public class GetProcessInstanceCommand implements Command<ProcessInstance> {
	
	private Long processInstanceId;
	
	public Long getProcessInstanceId() {
		return processInstanceId;
	}
	
	public void setProcessInstanceId(Long processInstanceId) {
		this.processInstanceId = processInstanceId;
	}
	
	public ProcessInstance execute(WorkingMemory workingMemory) {
		if (processInstanceId == null) {
			return null;
		}
		return workingMemory.getProcessInstance(processInstanceId);
	}

}
