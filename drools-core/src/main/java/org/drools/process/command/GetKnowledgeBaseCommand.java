package org.drools.process.command;

import org.drools.KnowledgeBase;
import org.drools.RuleBase;
import org.drools.command.Context;
import org.drools.command.impl.GenericCommand;
import org.drools.command.impl.KnowledgeCommandContext;
import org.drools.impl.KnowledgeBaseImpl;
import org.drools.reteoo.ReteooWorkingMemory;
import org.drools.runtime.StatefulKnowledgeSession;

public class GetKnowledgeBaseCommand
    implements
    GenericCommand<KnowledgeBase> {

    public GetKnowledgeBaseCommand() {
    }

    public KnowledgeBase execute(Context context) {
        StatefulKnowledgeSession ksession = ((KnowledgeCommandContext) context).getStatefulKnowledgesession();
        return ksession.getKnowledgeBase();
    }

    public String toString() {
        return "session.getRuleBase();";
    }

}
