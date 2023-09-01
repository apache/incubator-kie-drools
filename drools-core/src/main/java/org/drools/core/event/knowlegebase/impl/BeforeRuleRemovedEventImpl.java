package org.drools.core.event.knowlegebase.impl;

import org.kie.api.KieBase;
import org.kie.api.definition.rule.Rule;
import org.kie.api.event.kiebase.BeforeRuleRemovedEvent;

public class BeforeRuleRemovedEventImpl extends KnowledgeBaseEventImpl implements BeforeRuleRemovedEvent {
    private Rule rule;
    
    public BeforeRuleRemovedEventImpl(KieBase knowledgeBase, Rule rule) {
        super( knowledgeBase );
        this.rule = rule;
    }

    public Rule getRule() {
        return this.rule;
    }

    @Override
    public String toString() {
        return "==>[BeforeRuleRemovedEventImpl: getRule()=" + getRule() + ", getKieBase()=" + getKieBase()
                + "]";
    }

}
