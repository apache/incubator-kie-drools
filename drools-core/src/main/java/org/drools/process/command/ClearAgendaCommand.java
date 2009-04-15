package org.drools.process.command;

import org.drools.common.InternalAgenda;
import org.drools.reteoo.ReteooWorkingMemory;
import org.drools.runtime.rule.impl.AgendaImpl;

public class ClearAgendaCommand implements Command<Object> {

	public Object execute(ReteooWorkingMemory session) {
		new AgendaImpl((InternalAgenda) session.getAgenda()).clear();
		return null;
	}

	public String toString() {
		return "session.getAgenda().clear();";
	}
	
}
