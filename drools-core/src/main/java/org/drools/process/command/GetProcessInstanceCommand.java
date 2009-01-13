package org.drools.process.command;

import java.util.Map;

import org.drools.StatefulSession;
import org.drools.process.instance.ProcessInstance;

public class GetProcessInstanceCommand implements Command<ProcessInstance> {
	
	private Long processInstanceId;
	
	public Long getProcessInstanceId() {
		return processInstanceId;
	}
	
	public void setProcessInstanceId(Long processInstanceId) {
		this.processInstanceId = processInstanceId;
	}
	
	public ProcessInstance execute(StatefulSession session) {
		if (processInstanceId == null) {
			return null;
		}
		return session.getProcessInstance(processInstanceId);
	}

	public String toString() {
		return "session.getProcessInstance(" + processInstanceId + ");";
	}

}
