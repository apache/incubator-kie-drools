package org.drools.process.command;

import org.drools.impl.StatefulKnowledgeSessionImpl;
import org.drools.reteoo.ReteooWorkingMemory;
import org.drools.runtime.rule.AgendaFilter;

public class FireAllRulesCommand
    implements
    Command<Integer> {

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

    public Integer execute(ReteooWorkingMemory session) {
        if ( max != -1 ) {
            return session.fireAllRules( max );
        } else if ( agendaFilter != null ) {
            return session.fireAllRules( new StatefulKnowledgeSessionImpl.AgendaFilterWrapper( agendaFilter ) );
        } else {
            return session.fireAllRules();
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
