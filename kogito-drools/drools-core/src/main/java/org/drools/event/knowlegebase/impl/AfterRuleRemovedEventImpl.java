package org.drools.event.knowlegebase.impl;

import org.drools.KnowledgeBase;
import org.drools.event.knowledgebase.AfterRuleRemovedEvent;
import org.drools.knowledge.definitions.rule.Rule;

public class AfterRuleRemovedEventImpl extends KnowledgeBaseEventImpl implements AfterRuleRemovedEvent {
    private Rule rule;
    
    public AfterRuleRemovedEventImpl(KnowledgeBase knowledgeBase, Rule rule) {
        super( knowledgeBase );
        this.rule = rule;
    }

    public Rule getRule() {
        return this.rule;
    }
}
