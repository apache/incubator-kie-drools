package org.drools.process.command;

import org.drools.KnowledgeBase;
import org.drools.RuleBase;
import org.drools.impl.KnowledgeBaseImpl;
import org.drools.reteoo.ReteooWorkingMemory;

public class GetKnowledgeBaseCommand
    implements
    Command<KnowledgeBase> {

    public GetKnowledgeBaseCommand() {
    }

    public KnowledgeBase execute(ReteooWorkingMemory session) {
        RuleBase ruleBase = session.getRuleBase();

        return new KnowledgeBaseImpl( ruleBase );
    }

    public String toString() {
        return "session.getRuleBase();";
    }

}
