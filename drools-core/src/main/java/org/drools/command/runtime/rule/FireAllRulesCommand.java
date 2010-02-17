package org.drools.command.runtime.rule;

import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;
import javax.xml.bind.annotation.XmlAttribute;

import org.drools.command.Context;
import org.drools.command.impl.GenericCommand;
import org.drools.command.impl.KnowledgeCommandContext;
import org.drools.impl.StatefulKnowledgeSessionImpl;
import org.drools.runtime.StatefulKnowledgeSession;
import org.drools.runtime.impl.ExecutionResultImpl;
import org.drools.runtime.rule.AgendaFilter;

@XmlAccessorType(XmlAccessType.NONE)
public class FireAllRulesCommand
    implements
    GenericCommand<Integer> {

	@XmlAttribute
    private int          max          = -1;
    private AgendaFilter agendaFilter = null;
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

    public FireAllRulesCommand(String outIdentifier,
                               int max,
                               AgendaFilter agendaFilter) {
        this.outIdentifier = outIdentifier;
        this.max = max;
        this.agendaFilter = agendaFilter;
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
        StatefulKnowledgeSession ksession = ((KnowledgeCommandContext) context).getStatefulKnowledgesession();
        int fired;
        if ( max != -1 ) {
            fired = ksession.fireAllRules( max );
        } else if ( agendaFilter != null ) {
            fired = ((StatefulKnowledgeSessionImpl) ksession).session.fireAllRules( new StatefulKnowledgeSessionImpl.AgendaFilterWrapper( agendaFilter ) );
        } else {
            fired = ksession.fireAllRules();
        }

        if ( this.outIdentifier != null ) {
            ((ExecutionResultImpl) ((KnowledgeCommandContext) context).getExecutionResults()).getResults().put( this.outIdentifier,
                                                                                                                fired );
        }
        return fired;
    }

    public String toString() {
        if ( max > 0 ) {
            return "session.fireAllRules( " + max + " );";
        } else if ( agendaFilter != null ) {
            return "session.fireAllRules( " + agendaFilter + " );";
        } else {
            return "session.fireAllRules();";
        }
    }

}
