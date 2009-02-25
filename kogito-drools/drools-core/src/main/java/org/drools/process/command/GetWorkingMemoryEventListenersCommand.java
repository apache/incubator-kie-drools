package org.drools.process.command;

import java.util.Collection;

import org.drools.StatefulSession;
import org.drools.event.WorkingMemoryEventListener;

public class GetWorkingMemoryEventListenersCommand
    implements
    Command<Collection<WorkingMemoryEventListener>> {

    public Collection<WorkingMemoryEventListener> execute(StatefulSession session) {
        return session.getWorkingMemoryEventListeners();
    }

    public String toString() {
        return "session.getWorkingMemoryEventListeners();";
    }

}
