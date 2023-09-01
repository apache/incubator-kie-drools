package org.drools.core.event.knowlegebase.impl;

import org.kie.api.KieBase;
import org.kie.api.definition.rule.Rule;
import org.kie.api.event.kiebase.AfterRuleAddedEvent;

public class AfterRuleAddedEventImpl extends KnowledgeBaseEventImpl implements AfterRuleAddedEvent {
    private Rule rule;
    
    public AfterRuleAddedEventImpl(KieBase knowledgeBase, Rule rule) {
        super( knowledgeBase );
        this.rule = rule;
    }

    public Rule getRule() {
        return this.rule;
    }

    @Override
    public String toString() {
        return "==>[AfterRuleAddedEventImpl: getRule()=" + getRule() + ", getKieBase()=" + getKieBase()
                + "]";
    }
    
}
