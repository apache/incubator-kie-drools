package org.drools.commands.runtime;

import org.kie.api.command.ExecutableCommand;
import org.kie.api.event.process.ProcessEventListener;
import org.kie.api.event.rule.AgendaEventListener;
import org.kie.api.event.rule.RuleRuntimeEventListener;
import org.kie.api.runtime.Context;
import org.kie.api.runtime.KieSession;
import org.kie.internal.command.RegistryContext;

public class AddEventListenerCommand
    implements
    ExecutableCommand<Void> {

    private RuleRuntimeEventListener   ruleRuntimeEventlistener = null;
    private AgendaEventListener        agendaEventlistener        = null;
    private ProcessEventListener       processEventListener       = null;

    public AddEventListenerCommand(RuleRuntimeEventListener listener) {
        this.ruleRuntimeEventlistener = listener;
    }

    public AddEventListenerCommand(AgendaEventListener listener) {
        this.agendaEventlistener = listener;
    }
    
    public AddEventListenerCommand(ProcessEventListener listener) {
        this.processEventListener = listener;
    }


    public Void execute(Context context) {
        KieSession ksession = ((RegistryContext) context).lookup( KieSession.class );

        if ( ruleRuntimeEventlistener != null ) {
            ksession.addEventListener( ruleRuntimeEventlistener );
        } else if ( agendaEventlistener != null ) {
            ksession.addEventListener( agendaEventlistener );
        } else {
            ksession.addEventListener( processEventListener );
        }
        return null;
    }

    public String toString() {
        if ( ruleRuntimeEventlistener != null ) {
            return "session.addEventListener( " + ruleRuntimeEventlistener + " );";
        } else if ( agendaEventlistener != null ) {
            return "session.addEventListener( " + agendaEventlistener + " );";
        }  else  if ( processEventListener != null ) {
            return "session.addEventListener( " + processEventListener + " );";
        }
        
        return "AddEventListenerCommand";
    }
}
