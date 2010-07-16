package org.drools.event.knowlegebase.impl;

import org.drools.KnowledgeBase;
import org.drools.definition.rule.Rule;
import org.drools.event.knowledgebase.AfterRuleAddedEvent;

public class AfterRuleAddedEventImpl extends KnowledgeBaseEventImpl implements AfterRuleAddedEvent {
    private Rule rule;
    
    public AfterRuleAddedEventImpl(KnowledgeBase knowledgeBase, Rule rule) {
        super( knowledgeBase );
        this.rule = rule;
    }

    public Rule getRule() {
        return this.rule;
    }

	@Override
	public String toString() {
		return "==>[AfterRuleAddedEventImpl: getRule()=" + getRule() + ", getKnowledgeBase()=" + getKnowledgeBase()
				+ "]";
	}
    
}
