package org.drools.process.command;

import org.drools.reteoo.ReteooWorkingMemory;
import org.drools.runtime.rule.Agenda;

public class GetAgendaCommand
    implements
    Command<Agenda> {

    public Agenda execute(ReteooWorkingMemory session) {
        return session.getAgenda();
    }

    public String toString() {
        return "session.getAgenda();";
    }
}
