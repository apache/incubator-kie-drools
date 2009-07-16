package org.drools.command.runtime.process;

import java.util.Collection;

import org.drools.command.Context;
import org.drools.command.impl.GenericCommand;
import org.drools.command.impl.KnowledgeCommandContext;
import org.drools.process.instance.ProcessInstance;
import org.drools.reteoo.ReteooWorkingMemory;
import org.drools.runtime.StatefulKnowledgeSession;
import org.drools.runtime.rule.FactHandle;

public class SetProcessInstanceStateCommand implements GenericCommand<Object> {
	
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

    public Object execute(Context context) {
        StatefulKnowledgeSession ksession = ((KnowledgeCommandContext) context).getStatefulKnowledgesession();
		if (processInstanceId == null) {
			return null;
		}
		((ProcessInstance) ksession.getProcessInstance(processInstanceId)).setState(state);
		return null;
	}

	public String toString() {
		return "session.getProcessInstance(" + processInstanceId + ");";
	}

}
