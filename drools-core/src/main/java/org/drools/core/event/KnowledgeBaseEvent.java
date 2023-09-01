package org.drools.core.event;

import java.util.EventObject;

import org.drools.base.definitions.InternalKnowledgePackage;
import org.drools.base.definitions.rule.impl.RuleImpl;
import org.drools.core.impl.InternalRuleBase;

public class KnowledgeBaseEvent extends EventObject {

    private static final long serialVersionUID = 510l;
    private final InternalRuleBase kBase;
    private final InternalKnowledgePackage pkg;
    private final RuleImpl rule;
    private final String function;

    public KnowledgeBaseEvent(final InternalRuleBase kBase) {
        super( kBase );
        this.kBase = kBase;
        this.pkg = null;
        this.rule = null;
        this.function = null;
    }

    public KnowledgeBaseEvent(final InternalRuleBase kBase,
                              final InternalKnowledgePackage pkg) {
        super( kBase );
        this.kBase = kBase;
        this.pkg = pkg;
        this.rule = null;
        this.function = null;
    }

    public KnowledgeBaseEvent(final InternalRuleBase kBase,
                              final InternalKnowledgePackage pkg,
                              final RuleImpl rule) {
        super( kBase );
        this.kBase = kBase;
        this.pkg = pkg;
        this.rule = rule;
        this.function = null;
    }

    public KnowledgeBaseEvent(final InternalRuleBase kBase,
                              final InternalKnowledgePackage pkg,
                              final String function) {
        super( kBase );
        this.kBase = kBase;
        this.pkg = pkg;
        this.rule = null;
        this.function = function;
    }

    public InternalRuleBase getKnowledgeBase() {
        return this.kBase;
    }

    public InternalKnowledgePackage getPackage() {
        return this.pkg;
    }

    public RuleImpl getRule() {
        return this.rule;
    }

    public String getFunction() {
        return this.function;
    }

}
