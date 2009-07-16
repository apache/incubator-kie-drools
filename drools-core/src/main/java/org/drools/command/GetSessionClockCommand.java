package org.drools.command;

import org.drools.command.Context;
import org.drools.command.impl.GenericCommand;
import org.drools.command.impl.KnowledgeCommandContext;
import org.drools.reteoo.ReteooWorkingMemory;
import org.drools.runtime.StatefulKnowledgeSession;
import org.drools.time.SessionClock;

public class GetSessionClockCommand
    implements
    GenericCommand<SessionClock> {

    public SessionClock execute(Context context) {
        StatefulKnowledgeSession ksession = ((KnowledgeCommandContext) context).getStatefulKnowledgesession();
        return ksession.getSessionClock();
    }

    public String toString() {
        return "session.getSessionClock();";
    }
}
