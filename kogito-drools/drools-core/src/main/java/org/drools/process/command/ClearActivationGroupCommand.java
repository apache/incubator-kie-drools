package org.drools.process.command;

import org.drools.command.Context;
import org.drools.command.impl.GenericCommand;
import org.drools.command.impl.KnowledgeCommandContext;
import org.drools.common.InternalAgenda;
import org.drools.reteoo.ReteooWorkingMemory;
import org.drools.runtime.StatefulKnowledgeSession;
import org.drools.runtime.rule.impl.AgendaImpl;

public class ClearActivationGroupCommand implements GenericCommand<Object> {

	private String name;
	
	public String getName() {
		return name;
	}

	public void setName(String name) {
		this.name = name;
	}

    public Void execute(Context context) {
        StatefulKnowledgeSession ksession = ((KnowledgeCommandContext) context).getStatefulKnowledgesession();
        ksession.getAgenda().getActivationGroup( this.name ).clear();
		return null;
	}

	public String toString() {
		return "session.getAgenda().getActivationGroup(" + name + ").clear();";
	}
	
}
