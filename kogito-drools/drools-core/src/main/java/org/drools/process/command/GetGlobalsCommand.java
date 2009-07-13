package org.drools.process.command;

import org.drools.command.Context;
import org.drools.command.impl.GenericCommand;
import org.drools.command.impl.KnowledgeCommandContext;
import org.drools.reteoo.ReteooWorkingMemory;
import org.drools.runtime.Globals;
import org.drools.runtime.StatefulKnowledgeSession;

public class GetGlobalsCommand
    implements
    GenericCommand<Globals> {

    public Globals execute(Context context) {
        StatefulKnowledgeSession ksession = ((KnowledgeCommandContext) context).getStatefulKnowledgesession();
        return (Globals) ksession.getGlobals();
    }

    public String toString() {
        return "session.getGlobalResolver()";
    }
}
