package org.drools.process.command;

import org.drools.StatefulSession;
import org.drools.runtime.rule.Agenda;

public class GetAgendaCommand
    implements
    Command<Agenda> {

    public Agenda execute(StatefulSession session) {
        return session.getAgenda();
    }

    public String toString() {
        return "session.getAgenda();";
    }
}
