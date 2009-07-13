package org.drools.process.command;

import org.drools.command.Context;
import org.drools.command.impl.GenericCommand;
import org.drools.command.impl.KnowledgeCommandContext;
import org.drools.impl.StatefulKnowledgeSessionImpl;
import org.drools.reteoo.ReteooWorkingMemory;
import org.drools.runtime.StatefulKnowledgeSession;
import org.drools.runtime.rule.AgendaFilter;

public class FireAllRulesCommand
    implements
    GenericCommand<Integer> {

    private int          max          = -1;
    private AgendaFilter agendaFilter = null;

    public FireAllRulesCommand() {
    }

    public FireAllRulesCommand(int max) {
        this.max = max;
    }

    public FireAllRulesCommand(AgendaFilter agendaFilter) {
        this.agendaFilter = agendaFilter;
    }
    
    public int getMax() {
        return this.max;
    }

    public Integer execute(Context context) {
        StatefulKnowledgeSession ksession = ((KnowledgeCommandContext) context).getStatefulKnowledgesession();
        if ( max != -1 ) {
            return ksession.fireAllRules( max );
        } else if ( agendaFilter != null ) {
            return ((StatefulKnowledgeSessionImpl)ksession).session.fireAllRules( new StatefulKnowledgeSessionImpl.AgendaFilterWrapper( agendaFilter ) );
        } else {
            return ksession.fireAllRules();
        }
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
