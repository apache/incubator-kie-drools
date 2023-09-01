package org.drools.core.event.knowlegebase.impl;

import org.kie.api.KieBase;
import org.kie.api.event.kiebase.BeforeKieBaseLockedEvent;

public class BeforeKnowledgeBaseLockedEventImpl extends KnowledgeBaseEventImpl implements BeforeKieBaseLockedEvent {

    public BeforeKnowledgeBaseLockedEventImpl(KieBase knowledgeBase) {
        super( knowledgeBase );
    }

}
