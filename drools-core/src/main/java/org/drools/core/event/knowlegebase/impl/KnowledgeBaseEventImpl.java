package org.drools.core.event.knowlegebase.impl;

import org.kie.api.KieBase;
import org.kie.api.event.kiebase.KieBaseEvent;

public class KnowledgeBaseEventImpl implements KieBaseEvent {
    private KieBase knowledgeBase;
    
    public KnowledgeBaseEventImpl(KieBase knowledgeBase) {
        this.knowledgeBase = knowledgeBase;
    }
    
    public KieBase getKieBase() {
        return this.knowledgeBase;
    }

    @Override
    public String toString() {
        return "==>[KnowledgeBaseEventImpl: getKieBase()=" + getKieBase() + "]";
    }

}
