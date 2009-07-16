package org.drools.command.runtime.process;

import java.util.Collection;

import org.drools.command.Context;
import org.drools.command.impl.GenericCommand;
import org.drools.command.impl.KnowledgeCommandContext;
import org.drools.reteoo.ReteooWorkingMemory;
import org.drools.runtime.StatefulKnowledgeSession;
import org.drools.runtime.process.WorkItemHandler;
import org.drools.runtime.rule.FactHandle;

public class RegisterWorkItemHandlerCommand implements GenericCommand<Object> {
	
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

    public Object execute(Context context) {
        StatefulKnowledgeSession ksession = ((KnowledgeCommandContext) context).getStatefulKnowledgesession();        
        ksession.getWorkItemManager().registerWorkItemHandler(workItemName, handler);
		return null;
	}

	public String toString() {
		return "session.getWorkItemManager().registerWorkItemHandler("
			+ workItemName + ", " + handler +  ");";
	}

}