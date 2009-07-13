package org.drools.process.command;

import org.drools.command.Context;
import org.drools.command.impl.GenericCommand;
import org.drools.command.impl.KnowledgeCommandContext;
import org.drools.reteoo.ReteooWorkingMemory;
import org.drools.runtime.StatefulKnowledgeSession;

public class HaltCommand
    implements
    GenericCommand<Void> {

    public Void execute(Context context) {
        StatefulKnowledgeSession ksession = ((KnowledgeCommandContext) context).getStatefulKnowledgesession();
        ksession.halt();
        return null;
    }

    public String toString() {
        return "session.halt();";
    }
}
