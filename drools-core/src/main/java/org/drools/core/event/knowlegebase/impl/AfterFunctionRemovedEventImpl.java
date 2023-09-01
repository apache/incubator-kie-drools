package org.drools.core.event.knowlegebase.impl;

import org.kie.api.KieBase;
import org.kie.api.event.kiebase.AfterFunctionRemovedEvent;

public class AfterFunctionRemovedEventImpl extends KnowledgeBaseEventImpl implements AfterFunctionRemovedEvent {
    private String function;
    
    public AfterFunctionRemovedEventImpl(KieBase knowledgeBase, String function) {
        super( knowledgeBase );
        this.function = function;
    }

    public String getFunction() {
        return this.function;
    }

    @Override
    public String toString() {
        return "==>[AfterFunctionRemovedEventImpl: getFunction()=" + getFunction() + ", getKieBase()="
                + getKieBase() + "]";
    }
    
}
