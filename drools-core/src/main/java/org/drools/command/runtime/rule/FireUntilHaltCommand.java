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
    private static final long serialVersionUID = -482292109159215861L;

    private AgendaFilter agendaFilter = null;

    public FireUntilHaltCommand() {
    }

    public FireUntilHaltCommand(AgendaFilter agendaFilter) {
        this.agendaFilter = agendaFilter;
    }

    public Integer execute(Context context) {
        StatefulKnowledgeSession ksession = ((KnowledgeCommandContext) context).getStatefulKnowledgesession();
        final ReteooWorkingMemory session = ((StatefulKnowledgeSessionImpl)ksession).session;
        
        new Thread(new Runnable() {
            public void run() {
                if ( agendaFilter != null ) {
                    session.fireUntilHalt( new StatefulKnowledgeSessionImpl.AgendaFilterWrapper( agendaFilter ) );
                } else {
                    session.fireUntilHalt();
                }
            }
        }).start();

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
