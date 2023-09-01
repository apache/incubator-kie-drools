package org.drools.core.event.knowlegebase.impl;

import org.kie.api.KieBase;
import org.kie.api.definition.rule.Rule;
import org.kie.api.event.kiebase.AfterRuleRemovedEvent;

public class AfterRuleRemovedEventImpl extends KnowledgeBaseEventImpl implements AfterRuleRemovedEvent {
    private Rule rule;
    
    public AfterRuleRemovedEventImpl(KieBase knowledgeBase, Rule rule) {
        super( knowledgeBase );
        this.rule = rule;
    }

    public Rule getRule() {
        return this.rule;
    }

    @Override
    public String toString() {
        return "==>[AfterRuleRemovedEventImpl: getRule()=" + getRule() + ", getKieBase()=" + getKieBase()
                + "]";
    }
}
