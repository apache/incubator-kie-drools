package org.drools.event.knowlegebase.impl;

import org.drools.KnowledgeBase;
import org.drools.event.knowledgebase.AfterKnowledgeBaseLockedEvent;
import org.drools.knowledge.definitions.rule.Rule;

public class AfterKnowledgeBaseLockedEventImpl extends KnowledgeBaseEventImpl implements AfterKnowledgeBaseLockedEvent {

    public AfterKnowledgeBaseLockedEventImpl(KnowledgeBase knowledgeBase) {
        super( knowledgeBase );
        // TODO Auto-generated constructor stub
    }

    public Rule getRule() {
        // TODO Auto-generated method stub
        return null;
    }

}
