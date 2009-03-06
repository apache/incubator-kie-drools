package org.drools.process.command;

import org.drools.process.instance.ProcessInstance;
import org.drools.reteoo.ReteooWorkingMemory;

public class GetProcessInstanceCommand implements Command<ProcessInstance> {
	
	private Long processInstanceId;
	
	public Long getProcessInstanceId() {
		return processInstanceId;
	}
	
	public void setProcessInstanceId(Long processInstanceId) {
		this.processInstanceId = processInstanceId;
	}
	
	public ProcessInstance execute(ReteooWorkingMemory session) {
		if (processInstanceId == null) {
			return null;
		}
		return session.getProcessInstance(processInstanceId);
	}

	public String toString() {
		return "session.getProcessInstance(" + processInstanceId + ");";
	}

}
