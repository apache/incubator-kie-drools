package org.drools.process.command;

import org.drools.StatefulSession;
import org.drools.event.process.ProcessEventListener;
import org.drools.event.rule.AgendaEventListener;
import org.drools.event.rule.WorkingMemoryEventListener;
import org.drools.impl.StatefulKnowledgeSessionImpl;

public class RemoveEventListenerCommand
    implements
    Command<Object> {

    private WorkingMemoryEventListener workingMemoryEventListener = null;
    private AgendaEventListener        agendaEventListener        = null;
    private ProcessEventListener       processEventListener       = null;

    public RemoveEventListenerCommand(WorkingMemoryEventListener listener) {
        this.workingMemoryEventListener = listener;
    }

    public RemoveEventListenerCommand(AgendaEventListener listener) {
        this.agendaEventListener = listener;
    }

    public RemoveEventListenerCommand(ProcessEventListener listener) {
        this.processEventListener = listener;
    }

    public Object execute(StatefulSession session) {
        if ( workingMemoryEventListener != null ) {
            session.removeEventListener( new StatefulKnowledgeSessionImpl.WorkingMemoryEventListenerWrapper( workingMemoryEventListener ) );
        } else if ( agendaEventListener != null ) {
            session.removeEventListener( new StatefulKnowledgeSessionImpl.AgendaEventListenerWrapper( agendaEventListener ) );
        } else {
            session.removeEventListener( new StatefulKnowledgeSessionImpl.ProcessEventListenerWrapper( processEventListener ) );
        }
        return null;
    }

    public String toString() {
        if ( workingMemoryEventListener != null ) {
            return "session.removeEventListener( " + workingMemoryEventListener + " );";
        } else if ( agendaEventListener != null ) {
            return "session.removeEventListener( " + agendaEventListener + " );";
        } else {
            return "session.removeEventListener( " + processEventListener + " );";
        }
    }
}
