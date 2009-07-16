package org.drools.command.runtime;

import org.drools.command.Context;
import org.drools.command.impl.GenericCommand;
import org.drools.command.impl.KnowledgeCommandContext;
import org.drools.event.RuleFlowEventListener;
import org.drools.event.process.ProcessEventListener;
import org.drools.event.rule.AgendaEventListener;
import org.drools.event.rule.WorkingMemoryEventListener;
import org.drools.runtime.StatefulKnowledgeSession;

public class RemoveEventListenerCommand
    implements
    GenericCommand<Object> {

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

    public Object execute(Context context) {
        StatefulKnowledgeSession ksession = ((KnowledgeCommandContext) context).getStatefulKnowledgesession();
        if ( workingMemoryEventListener != null ) {
            ksession.removeEventListener( workingMemoryEventListener );
        } else if ( agendaEventListener != null ) {
            ksession.removeEventListener( agendaEventListener );
        } else {
            ksession.removeEventListener( processEventListener );
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
