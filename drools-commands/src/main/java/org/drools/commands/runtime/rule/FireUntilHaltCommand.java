package org.drools.commands.runtime.rule;

import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;
import javax.xml.bind.annotation.XmlAnyElement;
import javax.xml.bind.annotation.XmlRootElement;
import javax.xml.bind.annotation.adapters.XmlJavaTypeAdapter;

import org.drools.core.common.InternalWorkingMemory;
import org.drools.commands.jaxb.JaxbUnknownAdapter;
import org.kie.api.command.ExecutableCommand;
import org.kie.api.runtime.Context;
import org.kie.api.runtime.KieSession;
import org.kie.api.runtime.rule.AgendaFilter;
import org.kie.internal.command.RegistryContext;

@XmlRootElement
@XmlAccessorType(XmlAccessType.NONE)
public class FireUntilHaltCommand
	implements
    ExecutableCommand<Void> {
    private static final long serialVersionUID = 510l;

    @XmlJavaTypeAdapter(JaxbUnknownAdapter.class)
    @XmlAnyElement(lax = true)
    private AgendaFilter agendaFilter = null;

    public FireUntilHaltCommand() {
    }

    public FireUntilHaltCommand(AgendaFilter agendaFilter) {
        this.agendaFilter = agendaFilter;
    }

    public AgendaFilter getAgendaFilter() {
        return agendaFilter;
    }

    public void setAgendaFilter(AgendaFilter agendaFilter) {
        this.agendaFilter = agendaFilter;
    }

    public Void execute(Context context) {
        KieSession ksession = ((RegistryContext) context).lookup( KieSession.class );

        if ( !( (InternalWorkingMemory) ksession ).getAgenda().isFiring() ) {
            new Thread( () -> ksession.fireUntilHalt( agendaFilter ) ).start();
        }

        return null;
    }

    public String toString() {
        if ( agendaFilter != null ) {
            return "session.fireUntilHalt( " + agendaFilter + " );";
        } else {
            return "session.fireUntilHalt();";
        }
    }
}
