package org.drools.event.knowlegebase.impl;

import org.drools.KnowledgeBase;
import org.drools.definition.rule.Rule;
import org.drools.event.knowledgebase.AfterKnowledgeBaseLockedEvent;

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
