package org.drools.event.knowlegebase.impl;

import org.drools.KnowledgeBase;
import org.drools.definition.rule.Rule;
import org.drools.event.knowledgebase.AfterRuleRemovedEvent;

public class AfterRuleRemovedEventImpl extends KnowledgeBaseEventImpl implements AfterRuleRemovedEvent {
    private Rule rule;
    
    public AfterRuleRemovedEventImpl(KnowledgeBase knowledgeBase, Rule rule) {
        super( knowledgeBase );
        this.rule = rule;
    }

    public Rule getRule() {
        return this.rule;
    }

	@Override
	public String toString() {
		return "==>[AfterRuleRemovedEventImpl: getRule()=" + getRule() + ", getKnowledgeBase()=" + getKnowledgeBase()
				+ "]";
	}
}
