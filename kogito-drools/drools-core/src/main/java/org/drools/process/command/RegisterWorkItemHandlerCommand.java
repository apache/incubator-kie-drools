package org.drools.process.command;

import org.drools.reteoo.ReteooWorkingMemory;
import org.drools.runtime.process.WorkItemHandler;

public class RegisterWorkItemHandlerCommand implements Command<Object> {
	
	private WorkItemHandler handler;
	private String workItemName;
	
	public WorkItemHandler getHandler() {
		return handler;
	}

	public void setHandler(WorkItemHandler handler) {
		this.handler = handler;
	}

	public String getWorkItemName() {
		return workItemName;
	}

	public void setWorkItemName(String workItemName) {
		this.workItemName = workItemName;
	}

	public Object execute(ReteooWorkingMemory session) {
		session.getWorkItemManager().registerWorkItemHandler(workItemName, handler);
		return null;
	}

	public String toString() {
		return "session.getWorkItemManager().registerWorkItemHandler("
			+ workItemName + ", " + handler +  ");";
	}

}