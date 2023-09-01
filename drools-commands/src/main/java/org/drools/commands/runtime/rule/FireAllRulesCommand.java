package org.drools.commands.runtime.rule;

import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;
import javax.xml.bind.annotation.XmlAnyElement;
import javax.xml.bind.annotation.XmlAttribute;
import javax.xml.bind.annotation.XmlRootElement;
import javax.xml.bind.annotation.adapters.XmlJavaTypeAdapter;

import org.drools.commands.IdentifiableResult;
import org.drools.commands.jaxb.JaxbUnknownAdapter;
import org.kie.api.command.ExecutableCommand;
import org.kie.api.runtime.Context;
import org.kie.api.runtime.ExecutionResults;
import org.kie.api.runtime.KieSession;
import org.kie.api.runtime.rule.AgendaFilter;
import org.kie.internal.command.RegistryContext;

@XmlRootElement
@XmlAccessorType(XmlAccessType.NONE)
public class FireAllRulesCommand implements ExecutableCommand<Integer>, IdentifiableResult {

    @XmlAttribute
    private int          max          = -1;

    @XmlJavaTypeAdapter(JaxbUnknownAdapter.class)
    @XmlAnyElement(lax = true)
    private AgendaFilter agendaFilter = null;

    @XmlAttribute(name="out-identifier")
    private String       outIdentifier;

    public FireAllRulesCommand() {
    }

    public FireAllRulesCommand(String outIdentifer) {
        this.outIdentifier = outIdentifer;
    }

    public FireAllRulesCommand(int max) {
        this.max = max;
    }

    public FireAllRulesCommand(AgendaFilter agendaFilter) {
        this.agendaFilter = agendaFilter;
    }

    public FireAllRulesCommand(AgendaFilter agendaFilter, int max) {
        this(agendaFilter);
        this.max = max;
    }

    public FireAllRulesCommand(String outIdentifier,
                               int max,
                               AgendaFilter agendaFilter) {
        this(agendaFilter, max);
        this.outIdentifier = outIdentifier;
    }

    public int getMax() {
        return max;
    }

    public void setMax(int max) {
        this.max = max;
    }

    public AgendaFilter getAgendaFilter() {
        return agendaFilter;
    }

    public void setAgendaFilter(AgendaFilter agendaFilter) {
        this.agendaFilter = agendaFilter;
    }

    public String getOutIdentifier() {
        return outIdentifier;
    }

    public void setOutIdentifier(String outIdentifier) {
        this.outIdentifier = outIdentifier;
    }

    public Integer execute(Context context) {
        KieSession ksession = ((RegistryContext)context).lookup( KieSession.class );
        int fired;
        if ( max != -1 && agendaFilter != null ) {
            fired = ksession.fireAllRules( agendaFilter, max );
        } else if ( max != -1 ) {
            fired = ksession.fireAllRules( max );
        } else if ( agendaFilter != null ) {
            fired = ksession.fireAllRules( agendaFilter );
        } else {
            fired = ksession.fireAllRules();
        }

        if ( this.outIdentifier != null ) {
            ((RegistryContext) context).lookup(ExecutionResults.class).setResult(this.outIdentifier, fired);
        }
        return fired;
    }

    public String toString() {
        if ( max != -1 && agendaFilter != null ) {
            return "session.fireAllRules( " + agendaFilter + ", " + max + " );";
        } else if ( max != -1 ) {
            return "session.fireAllRules( " + max + " );";
        } else if ( agendaFilter != null ) {
            return "session.fireAllRules( " + agendaFilter + " );";
        } else {
            return "session.fireAllRules();";
        }
    }

    @Override
    public boolean autoFireAllRules() {
        return false;
    }
}
