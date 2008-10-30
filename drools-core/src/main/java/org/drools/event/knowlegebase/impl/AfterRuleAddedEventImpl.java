package org.drools.event.knowlegebase.impl;

import org.drools.KnowledgeBase;
import org.drools.event.knowledgebase.AfterRuleAddedEvent;
import org.drools.knowledge.definitions.rule.Rule;

public class AfterRuleAddedEventImpl extends KnowledgeBaseEventImpl implements AfterRuleAddedEvent {
    private Rule rule;
    
    public AfterRuleAddedEventImpl(KnowledgeBase knowledgeBase, Rule rule) {
        super( knowledgeBase );
        this.rule = rule;
    }

    public Rule getRule() {
        return this.rule;
    }
}
