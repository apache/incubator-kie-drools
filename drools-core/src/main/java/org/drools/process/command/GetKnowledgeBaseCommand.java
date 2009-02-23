package org.drools.process.command;

import org.drools.KnowledgeBase;
import org.drools.RuleBase;
import org.drools.StatefulSession;
import org.drools.impl.KnowledgeBaseImpl;

public class GetKnowledgeBaseCommand
    implements
    Command<KnowledgeBase> {

    public GetKnowledgeBaseCommand() {
    }

    public KnowledgeBase execute(StatefulSession session) {
        RuleBase ruleBase = session.getRuleBase();

        return new KnowledgeBaseImpl( ruleBase );
    }

    public String toString() {
        return "session.getRuleBase();";
    }

}
