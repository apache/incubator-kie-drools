package org.drools.core.event.knowlegebase.impl;

import org.kie.api.KieBase;
import org.kie.api.event.kiebase.AfterKieBaseUnlockedEvent;

public class AfterKnowledgeBaseUnlockedEventImpl extends KnowledgeBaseEventImpl implements  AfterKieBaseUnlockedEvent {

    public AfterKnowledgeBaseUnlockedEventImpl(KieBase knowledgeBase) {
        super( knowledgeBase );
    }

}
