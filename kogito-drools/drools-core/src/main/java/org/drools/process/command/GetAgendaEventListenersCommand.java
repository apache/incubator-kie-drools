package org.drools.process.command;

import java.util.Collection;

import org.drools.StatefulSession;
import org.drools.event.AgendaEventListener;

public class GetAgendaEventListenersCommand
    implements
    Command<Collection<AgendaEventListener>> {

    public Collection<AgendaEventListener> execute(StatefulSession session) {
        return session.getAgendaEventListeners();
    }

    public String toString() {
        return "session.getAgendaEventListeners();";
    }
}
