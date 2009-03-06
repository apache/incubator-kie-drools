package org.drools.process.command;

import java.util.Collection;

import org.drools.event.AgendaEventListener;
import org.drools.reteoo.ReteooWorkingMemory;

public class GetAgendaEventListenersCommand
    implements
    Command<Collection<AgendaEventListener>> {

    public Collection<AgendaEventListener> execute(ReteooWorkingMemory session) {
        return session.getAgendaEventListeners();
    }

    public String toString() {
        return "session.getAgendaEventListeners();";
    }
}
