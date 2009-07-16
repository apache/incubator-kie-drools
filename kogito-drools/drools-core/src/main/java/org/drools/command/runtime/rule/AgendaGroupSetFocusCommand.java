package org.drools.command.runtime.rule;

import org.drools.command.Context;
import org.drools.command.impl.GenericCommand;
import org.drools.command.impl.KnowledgeCommandContext;
import org.drools.common.InternalAgenda;
import org.drools.reteoo.ReteooWorkingMemory;
import org.drools.runtime.StatefulKnowledgeSession;
import org.drools.runtime.rule.impl.AgendaImpl;

public class AgendaGroupSetFocusCommand implements GenericCommand<Object> {

	private String name;
	
	public String getName() {
		return name;
	}

	public void setName(String name) {
		this.name = name;
	}

    public Void execute(Context context) {
        StatefulKnowledgeSession ksession = ((KnowledgeCommandContext) context).getStatefulKnowledgesession();
        ksession.getAgenda().getAgendaGroup( this.name ).setFocus();
		return null;
	}

	public String toString() {
		return "session.getAgenda().getAgendaGroup(" + name + ").setFocus();";
	}
	
}
