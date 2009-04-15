package org.drools.process.command;

import org.drools.common.InternalAgenda;
import org.drools.reteoo.ReteooWorkingMemory;
import org.drools.runtime.rule.Agenda;
import org.drools.runtime.rule.impl.AgendaImpl;

public class GetAgendaCommand
    implements
    Command<Agenda> {

    public Agenda execute(ReteooWorkingMemory session) {
        return new AgendaImpl( (InternalAgenda) session.getAgenda() );
    }

    public String toString() {
        return "ksession.getAgenda();";
    }
}
