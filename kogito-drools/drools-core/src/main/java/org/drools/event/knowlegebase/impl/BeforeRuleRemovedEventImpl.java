package org.drools.event.knowlegebase.impl;

import org.drools.KnowledgeBase;
import org.drools.event.knowledgebase.BeforeRuleRemovedEvent;
import org.drools.knowledge.definitions.rule.Rule;

public class BeforeRuleRemovedEventImpl extends KnowledgeBaseEventImpl implements BeforeRuleRemovedEvent {
    private Rule rule;
    
    public BeforeRuleRemovedEventImpl(KnowledgeBase knowledgeBase, Rule rule) {
        super( knowledgeBase );
        this.rule = rule;
    }

    public Rule getRule() {
        return this.rule;
    }

}
