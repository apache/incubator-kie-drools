package org.drools.commands.runtime.rule;

import java.util.Collection;
import javax.xml.bind.annotation.XmlAttribute;

import org.kie.api.command.ExecutableCommand;
import org.kie.api.event.rule.AgendaEventListener;
import org.kie.api.runtime.Context;
import org.kie.api.runtime.ExecutionResults;
import org.kie.api.runtime.KieSession;
import org.kie.internal.command.RegistryContext;

public class GetAgendaEventListenersCommand
    implements
    ExecutableCommand<Collection<AgendaEventListener>> {

    @XmlAttribute(name="out-identifier")
    private String outIdentifier;

    public String getOutIdentifier() {
        return outIdentifier;
    }

    public void setOutIdentifier(String outIdentifier) {
        this.outIdentifier = outIdentifier;
    }

    public Collection<AgendaEventListener> execute(Context context) {
        KieSession ksession = ((RegistryContext) context).lookup( KieSession.class );
        final Collection<AgendaEventListener> agendaEventListeners = ksession.getAgendaEventListeners();

        if ( this.outIdentifier != null ) {
            ((RegistryContext) context).lookup(ExecutionResults.class).setResult(this.outIdentifier, agendaEventListeners);
        }

        return agendaEventListeners;
    }

    public String toString() {
        return "session.getAgendaEventListeners();";
    }
}
