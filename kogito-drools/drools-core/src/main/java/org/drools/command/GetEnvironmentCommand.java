package org.drools.command;

import org.drools.command.Context;
import org.drools.command.impl.GenericCommand;
import org.drools.command.impl.KnowledgeCommandContext;
import org.drools.reteoo.ReteooWorkingMemory;
import org.drools.runtime.Environment;
import org.drools.runtime.StatefulKnowledgeSession;

public class GetEnvironmentCommand
    implements
    GenericCommand<Environment> {

    public Environment execute(Context context) {
        StatefulKnowledgeSession ksession = ((KnowledgeCommandContext) context).getStatefulKnowledgesession();
        return ksession.getEnvironment();
    }

    public String toString() {
        return "session.getEnvironment();";
    }

}
