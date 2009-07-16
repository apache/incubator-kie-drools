package org.drools.command.runtime.rule;

import org.drools.command.Context;
import org.drools.command.impl.GenericCommand;
import org.drools.command.impl.KnowledgeCommandContext;
import org.drools.impl.StatefulKnowledgeSessionImpl;
import org.drools.reteoo.ReteooWorkingMemory;
import org.drools.runtime.StatefulKnowledgeSession;
import org.drools.runtime.rule.AgendaFilter;

public class FireUntilHaltCommand
    implements
    GenericCommand<Object> {

    private AgendaFilter agendaFilter = null;

    public FireUntilHaltCommand() {
    }

    public FireUntilHaltCommand(AgendaFilter agendaFilter) {
        this.agendaFilter = agendaFilter;
    }

    public Integer execute(Context context) {
        StatefulKnowledgeSession ksession = ((KnowledgeCommandContext) context).getStatefulKnowledgesession();
        ReteooWorkingMemory session = ((StatefulKnowledgeSessionImpl)ksession).session;
        if ( agendaFilter != null ) {
            session.fireUntilHalt( new StatefulKnowledgeSessionImpl.AgendaFilterWrapper( agendaFilter ) );
        } else {
            session.fireUntilHalt();
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
