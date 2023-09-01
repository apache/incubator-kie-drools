package org.drools.core.event.knowlegebase.impl;

import org.kie.api.KieBase;
import org.kie.api.definition.rule.Rule;
import org.kie.api.event.kiebase.AfterKieBaseLockedEvent;

public class AfterKnowledgeBaseLockedEventImpl extends KnowledgeBaseEventImpl implements AfterKieBaseLockedEvent {

    public AfterKnowledgeBaseLockedEventImpl(KieBase knowledgeBase) {
        super( knowledgeBase );
    }

    public Rule getRule() {
        // TODO Auto-generated method stub
        return null;
    }

}
