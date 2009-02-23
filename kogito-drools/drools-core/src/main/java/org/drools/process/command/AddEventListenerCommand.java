package org.drools.process.command;

import org.drools.StatefulSession;
import org.drools.event.process.ProcessEventListener;
import org.drools.event.rule.AgendaEventListener;
import org.drools.event.rule.WorkingMemoryEventListener;
import org.drools.impl.StatefulKnowledgeSessionImpl;

public class AddEventListenerCommand
    implements
    Command<Object> {

    private WorkingMemoryEventListener workingMemoryEventlistener = null;
    private AgendaEventListener        agendaEventlistener        = null;
    private ProcessEventListener       processEventlistener       = null;

    public AddEventListenerCommand(WorkingMemoryEventListener listener) {
        this.workingMemoryEventlistener = listener;
    }

    public AddEventListenerCommand(AgendaEventListener listener) {
        this.agendaEventlistener = listener;
    }

    public AddEventListenerCommand(ProcessEventListener listener) {
        this.processEventlistener = listener;
    }

    public Object execute(StatefulSession session) {
        if ( workingMemoryEventlistener != null ) {
            session.addEventListener( new StatefulKnowledgeSessionImpl.WorkingMemoryEventListenerWrapper( workingMemoryEventlistener ) );
        } else if ( agendaEventlistener != null ) {
            session.addEventListener( new StatefulKnowledgeSessionImpl.AgendaEventListenerWrapper( agendaEventlistener ) );
        } else {
            session.addEventListener( new StatefulKnowledgeSessionImpl.ProcessEventListenerWrapper( processEventlistener ) );
        }
        return null;
    }

    public String toString() {
        if ( workingMemoryEventlistener != null ) {
            return "session.addEventListener( " + workingMemoryEventlistener + " );";
        } else if ( agendaEventlistener != null ) {
            return "session.addEventListener( " + agendaEventlistener + " );";
        } else {
            return "session.addEventListener( " + processEventlistener + " );";
        }
    }
}
