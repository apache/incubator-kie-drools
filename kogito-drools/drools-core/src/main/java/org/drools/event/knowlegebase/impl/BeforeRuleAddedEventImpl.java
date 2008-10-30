package org.drools.event.knowlegebase.impl;

import org.drools.KnowledgeBase;
import org.drools.event.knowledgebase.BeforeRuleAddedEvent;
import org.drools.knowledge.definitions.rule.Rule;

public class BeforeRuleAddedEventImpl extends KnowledgeBaseEventImpl implements BeforeRuleAddedEvent {
    private Rule rule;
    
    public BeforeRuleAddedEventImpl(KnowledgeBase knowledgeBase, Rule rule) {
        super( knowledgeBase );
        this.rule = rule;
    }

    public Rule getRule() {
        return this.rule;
    }

}
