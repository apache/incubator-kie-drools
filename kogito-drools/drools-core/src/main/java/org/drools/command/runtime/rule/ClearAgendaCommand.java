package org.drools.command.runtime.rule;

import org.drools.command.Context;
import org.drools.command.impl.GenericCommand;
import org.drools.command.impl.KnowledgeCommandContext;
import org.drools.common.InternalAgenda;
import org.drools.reteoo.ReteooWorkingMemory;
import org.drools.runtime.StatefulKnowledgeSession;
import org.drools.runtime.rule.impl.AgendaImpl;

public class ClearAgendaCommand implements GenericCommand<Object> {

    public Void execute(Context context) {
        StatefulKnowledgeSession ksession = ((KnowledgeCommandContext) context).getStatefulKnowledgesession();
        ksession.getAgenda().clear();
		return null;
	}

	public String toString() {
		return "session.getAgenda().clear();";
	}
	
}
