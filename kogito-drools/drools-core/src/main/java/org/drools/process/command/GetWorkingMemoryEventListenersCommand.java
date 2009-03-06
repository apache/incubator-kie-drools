package org.drools.process.command;

import java.util.Collection;

import org.drools.event.WorkingMemoryEventListener;
import org.drools.reteoo.ReteooWorkingMemory;

public class GetWorkingMemoryEventListenersCommand
    implements
    Command<Collection<WorkingMemoryEventListener>> {

    public Collection<WorkingMemoryEventListener> execute(ReteooWorkingMemory session) {
        return session.getWorkingMemoryEventListeners();
    }

    public String toString() {
        return "session.getWorkingMemoryEventListeners();";
    }

}
