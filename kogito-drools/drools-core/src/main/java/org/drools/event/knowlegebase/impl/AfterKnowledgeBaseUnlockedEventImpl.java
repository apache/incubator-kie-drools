package org.drools.event.knowlegebase.impl;

import org.drools.KnowledgeBase;
import org.drools.event.knowledgebase.AfterKnowledgeBaseUnlockedEvent;

public class AfterKnowledgeBaseUnlockedEventImpl extends KnowledgeBaseEventImpl implements  AfterKnowledgeBaseUnlockedEvent {

    public AfterKnowledgeBaseUnlockedEventImpl(KnowledgeBase knowledgeBase) {
        super( knowledgeBase );
    }

}
