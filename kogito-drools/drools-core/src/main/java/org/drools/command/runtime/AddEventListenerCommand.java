package org.drools.command.runtime;

import org.drools.command.Context;
import org.drools.command.impl.GenericCommand;
import org.drools.command.impl.KnowledgeCommandContext;
import org.drools.event.process.ProcessEventListener;
import org.drools.event.rule.AgendaEventListener;
import org.drools.event.rule.WorkingMemoryEventListener;
import org.drools.impl.StatefulKnowledgeSessionImpl;
import org.drools.reteoo.ReteooStatefulSession;
import org.drools.reteoo.ReteooWorkingMemory;
import org.drools.runtime.StatefulKnowledgeSession;

public class AddEventListenerCommand
    implements
    GenericCommand<Object> {

    private WorkingMemoryEventListener workingMemoryEventlistener = null;
    private AgendaEventListener        agendaEventlistener        = null;
    private ProcessEventListener       processEventListener       = null;

    public AddEventListenerCommand(WorkingMemoryEventListener listener) {
        this.workingMemoryEventlistener = listener;
    }

    public AddEventListenerCommand(AgendaEventListener listener) {
        this.agendaEventlistener = listener;
    }
    
    public AddEventListenerCommand(ProcessEventListener listener) {
        this.processEventListener = listener;
    }    


    public Void execute(Context context) {
        StatefulKnowledgeSession ksession = ((KnowledgeCommandContext) context).getStatefulKnowledgesession();
        
        if ( workingMemoryEventlistener != null ) {
            ksession.addEventListener( workingMemoryEventlistener );
        } else if ( agendaEventlistener != null ) {
            ksession.addEventListener( agendaEventlistener );
        } else {
            ksession.addEventListener( processEventListener );
        }
        return null;
    }

    public String toString() {
        if ( workingMemoryEventlistener != null ) {
            return "session.addEventListener( " + workingMemoryEventlistener + " );";
        } else if ( agendaEventlistener != null ) {
            return "session.addEventListener( " + agendaEventlistener + " );";
        }  else  if ( processEventListener != null ) {
            return "session.addEventListener( " + processEventListener + " );";
        } 
        
        return "AddEventListenerCommand";
    }
}
