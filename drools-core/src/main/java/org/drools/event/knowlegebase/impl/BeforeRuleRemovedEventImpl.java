package org.drools.event.knowlegebase.impl;

import org.drools.KnowledgeBase;
import org.drools.definition.rule.Rule;
import org.drools.event.knowledgebase.BeforeRuleRemovedEvent;

public class BeforeRuleRemovedEventImpl extends KnowledgeBaseEventImpl implements BeforeRuleRemovedEvent {
    private Rule rule;
    
    public BeforeRuleRemovedEventImpl(KnowledgeBase knowledgeBase, Rule rule) {
        super( knowledgeBase );
        this.rule = rule;
    }

    public Rule getRule() {
        return this.rule;
    }

	@Override
	public String toString() {
		return "==>[BeforeRuleRemovedEventImpl: getRule()=" + getRule() + ", getKnowledgeBase()=" + getKnowledgeBase()
				+ "]";
	}

}
