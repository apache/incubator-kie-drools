package org.drools.process.command;

import org.drools.event.AgendaEventListener;
import org.drools.event.RuleFlowEventListener;
import org.drools.event.WorkingMemoryEventListener;
import org.drools.reteoo.ReteooWorkingMemory;

public class AddEventListenerCommand
    implements
    Command<Object> {

    private WorkingMemoryEventListener workingMemoryEventlistener = null;
    private AgendaEventListener        agendaEventlistener        = null;
    private RuleFlowEventListener      ruleFlowEventlistener      = null;

    public AddEventListenerCommand(WorkingMemoryEventListener listener) {
        this.workingMemoryEventlistener = listener;
    }

    public AddEventListenerCommand(AgendaEventListener listener) {
        this.agendaEventlistener = listener;
    }

    public AddEventListenerCommand(RuleFlowEventListener listener) {
        this.ruleFlowEventlistener = listener;
    }

    public Object execute(ReteooWorkingMemory session) {
        if ( workingMemoryEventlistener != null ) {
            session.addEventListener( workingMemoryEventlistener );
        } else if ( agendaEventlistener != null ) {
            session.addEventListener( agendaEventlistener );
        } else {
            session.addEventListener( ruleFlowEventlistener );
        }
        return null;
    }

    public String toString() {
        if ( workingMemoryEventlistener != null ) {
            return "session.addEventListener( " + workingMemoryEventlistener + " );";
        } else if ( agendaEventlistener != null ) {
            return "session.addEventListener( " + agendaEventlistener + " );";
        } else {
            return "session.addEventListener( " + ruleFlowEventlistener + " );";
        }
    }
}
