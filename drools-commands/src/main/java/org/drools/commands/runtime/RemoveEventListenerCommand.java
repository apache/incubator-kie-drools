package org.drools.commands.runtime;

import org.kie.api.command.ExecutableCommand;
import org.kie.api.event.process.ProcessEventListener;
import org.kie.api.event.rule.AgendaEventListener;
import org.kie.api.event.rule.RuleRuntimeEventListener;
import org.kie.api.runtime.Context;
import org.kie.api.runtime.KieSession;
import org.kie.internal.command.RegistryContext;

public class RemoveEventListenerCommand
    implements
    ExecutableCommand<Void> {

    private RuleRuntimeEventListener   ruleRuntimeEventlistener = null;
    private AgendaEventListener        agendaEventListener        = null;
    private ProcessEventListener       processEventListener       = null;
    
    public RemoveEventListenerCommand() {
    }

    public RemoveEventListenerCommand(RuleRuntimeEventListener listener) {
        this.ruleRuntimeEventlistener = listener;
    }

    public RemoveEventListenerCommand(AgendaEventListener listener) {
        this.agendaEventListener = listener;
    }

    public RemoveEventListenerCommand(ProcessEventListener listener) {
        this.processEventListener = listener;
    }

    public Void execute(Context context) {
        KieSession ksession = ((RegistryContext) context).lookup( KieSession.class );
        if ( ruleRuntimeEventlistener != null ) {
            ksession.removeEventListener( ruleRuntimeEventlistener );
        } else if ( agendaEventListener != null ) {
            ksession.removeEventListener( agendaEventListener );
        } else {
            ksession.removeEventListener( processEventListener );
        }
        return null;
    }

    public String toString() {
        if ( ruleRuntimeEventlistener != null ) {
            return "session.removeEventListener( " + ruleRuntimeEventlistener + " );";
        } else if ( agendaEventListener != null ) {
            return "session.removeEventListener( " + agendaEventListener + " );";
        } else {
            return "session.removeEventListener( " + processEventListener + " );";
        }
    }
}
