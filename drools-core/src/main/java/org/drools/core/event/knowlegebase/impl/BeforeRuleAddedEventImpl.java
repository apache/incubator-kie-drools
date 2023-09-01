package org.drools.core.event.knowlegebase.impl;

import org.kie.api.KieBase;
import org.kie.api.definition.rule.Rule;
import org.kie.api.event.kiebase.BeforeRuleAddedEvent;

public class BeforeRuleAddedEventImpl extends KnowledgeBaseEventImpl implements BeforeRuleAddedEvent {
    private Rule rule;
    
    public BeforeRuleAddedEventImpl(KieBase knowledgeBase, Rule rule) {
        super( knowledgeBase );
        this.rule = rule;
    }

    public Rule getRule() {
        return this.rule;
    }

    @Override
    public String toString() {
        return "==>[BeforeRuleAddedEventImpl: getRule()=" + getRule() + ", getKieBase()=" + getKieBase()
                + "]";
    }

}
