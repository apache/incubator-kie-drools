package org.drools.process.command;

import org.drools.process.instance.ProcessInstance;
import org.drools.reteoo.ReteooWorkingMemory;

public class SetProcessInstanceStateCommand implements Command<Object> {
	
	private Long processInstanceId;
	private int state;
	
	public Long getProcessInstanceId() {
		return processInstanceId;
	}
	
	public void setProcessInstanceId(Long processInstanceId) {
		this.processInstanceId = processInstanceId;
	}
	
	public int getState() {
		return state;
	}

	public void setState(int state) {
		this.state = state;
	}

	public Object execute(ReteooWorkingMemory session) {
		if (processInstanceId == null) {
			return null;
		}
		((ProcessInstance) session.getProcessInstance(processInstanceId)).setState(state);
		return null;
	}

	public String toString() {
		return "session.getProcessInstance(" + processInstanceId + ");";
	}

}
