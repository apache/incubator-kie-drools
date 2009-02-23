package org.drools.process.command;

import org.drools.StatefulSession;
import org.drools.impl.StatefulKnowledgeSessionImpl;
import org.drools.runtime.rule.AgendaFilter;

public class FireUntilHaltCommand
    implements
    Command<Object> {

    private AgendaFilter agendaFilter = null;

    public FireUntilHaltCommand() {
    }

    public FireUntilHaltCommand(AgendaFilter agendaFilter) {
        this.agendaFilter = agendaFilter;
    }

    public Object execute(StatefulSession session) {
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
