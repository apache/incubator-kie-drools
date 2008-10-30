package org.drools.event.knowlegebase.impl;

import org.drools.KnowledgeBase;
import org.drools.event.knowledgebase.BeforeKnowledgeBaseUnlockedEvent;

public class BeforeKnowledgeBaseUnlockedEventImpl extends KnowledgeBaseEventImpl implements BeforeKnowledgeBaseUnlockedEvent {

    public BeforeKnowledgeBaseUnlockedEventImpl(KnowledgeBase knowledgeBase) {
        super( knowledgeBase );
    }

}
