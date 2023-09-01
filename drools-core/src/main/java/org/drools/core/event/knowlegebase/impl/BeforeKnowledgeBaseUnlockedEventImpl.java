package org.drools.core.event.knowlegebase.impl;

import org.kie.api.KieBase;
import org.kie.api.event.kiebase.BeforeKieBaseUnlockedEvent;

public class BeforeKnowledgeBaseUnlockedEventImpl extends KnowledgeBaseEventImpl implements BeforeKieBaseUnlockedEvent {

    public BeforeKnowledgeBaseUnlockedEventImpl(KieBase knowledgeBase) {
        super( knowledgeBase );
    }

}
