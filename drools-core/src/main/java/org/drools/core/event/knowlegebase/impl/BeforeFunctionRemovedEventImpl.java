package org.drools.core.event.knowlegebase.impl;

import org.kie.api.KieBase;
import org.kie.api.event.kiebase.BeforeFunctionRemovedEvent;

public class BeforeFunctionRemovedEventImpl extends KnowledgeBaseEventImpl implements BeforeFunctionRemovedEvent {
    String function;
    
    public BeforeFunctionRemovedEventImpl(KieBase knowledgeBase, String function) {
        super( knowledgeBase );
        this.function = function;
    }

    public String getFunction() {
        return this.function;
    }

    @Override
    public String toString() {
        return "==>[BeforeFunctionRemovedEventImpl: getFunction()=" + getFunction() + ", getKieBase()="
                + getKieBase() + "]";
    }

}
