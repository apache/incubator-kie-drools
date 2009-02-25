package org.drools.process.command;

import org.drools.StatefulSession;
import org.drools.event.AgendaEventListener;
import org.drools.event.RuleFlowEventListener;
import org.drools.event.WorkingMemoryEventListener;

public class RemoveEventListenerCommand
    implements
    Command<Object> {

    private WorkingMemoryEventListener workingMemoryEventListener = null;
    private AgendaEventListener        agendaEventListener        = null;
    private RuleFlowEventListener      ruleFlowEventListener      = null;

    public RemoveEventListenerCommand(WorkingMemoryEventListener listener) {
        this.workingMemoryEventListener = listener;
    }

    public RemoveEventListenerCommand(AgendaEventListener listener) {
        this.agendaEventListener = listener;
    }

    public RemoveEventListenerCommand(RuleFlowEventListener listener) {
        this.ruleFlowEventListener = listener;
    }

    public Object execute(StatefulSession session) {
        if ( workingMemoryEventListener != null ) {
            session.removeEventListener( workingMemoryEventListener );
        } else if ( agendaEventListener != null ) {
            session.removeEventListener( agendaEventListener );
        } else {
            session.removeEventListener( ruleFlowEventListener );
        }
        return null;
    }

    public String toString() {
        if ( workingMemoryEventListener != null ) {
            return "session.removeEventListener( " + workingMemoryEventListener + " );";
        } else if ( agendaEventListener != null ) {
            return "session.removeEventListener( " + agendaEventListener + " );";
        } else {
            return "session.removeEventListener( " + ruleFlowEventListener + " );";
        }
    }
}
