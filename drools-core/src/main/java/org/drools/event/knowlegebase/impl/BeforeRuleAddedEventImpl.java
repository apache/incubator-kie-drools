package org.drools.event.knowlegebase.impl;

import org.drools.KnowledgeBase;
import org.drools.definition.rule.Rule;
import org.drools.event.knowledgebase.BeforeRuleAddedEvent;

public class BeforeRuleAddedEventImpl extends KnowledgeBaseEventImpl implements BeforeRuleAddedEvent {
    private Rule rule;
    
    public BeforeRuleAddedEventImpl(KnowledgeBase knowledgeBase, Rule rule) {
        super( knowledgeBase );
        this.rule = rule;
    }

    public Rule getRule() {
        return this.rule;
    }

	@Override
	public String toString() {
		return "==>[BeforeRuleAddedEventImpl: getRule()=" + getRule() + ", getKnowledgeBase()=" + getKnowledgeBase()
				+ "]";
	}

}
