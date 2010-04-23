package org.drools.command.runtime.process;

import org.drools.command.Context;
import org.drools.command.impl.GenericCommand;
import org.drools.command.impl.KnowledgeCommandContext;
import org.drools.runtime.StatefulKnowledgeSession;
import org.drools.runtime.process.ProcessInstance;

public class GetProcessInstanceCommand implements GenericCommand<ProcessInstance> {
	
	private Long processInstanceId;
	
	public Long getProcessInstanceId() {
		return processInstanceId;
	}
	
	public void setProcessInstanceId(Long processInstanceId) {
		this.processInstanceId = processInstanceId;
	}
	
    public ProcessInstance execute(Context context) {
        StatefulKnowledgeSession ksession = ((KnowledgeCommandContext) context).getStatefulKnowledgesession();
		if (processInstanceId == null) {
			return null;
		}
		return ksession.getProcessInstance(processInstanceId);
	}

	public String toString() {
		return "session.getProcessInstance(" + processInstanceId + ");";
	}

}
