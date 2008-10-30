package org.drools.event.knowlegebase.impl;

import org.drools.KnowledgeBase;
import org.drools.event.knowledgebase.BeforeKnowledgeBaseLockedEvent;

public class BeforeKnowledgeBaseLockedEventImpl extends KnowledgeBaseEventImpl implements BeforeKnowledgeBaseLockedEvent {

    public BeforeKnowledgeBaseLockedEventImpl(KnowledgeBase knowledgeBase) {
        super( knowledgeBase );
    }

}
