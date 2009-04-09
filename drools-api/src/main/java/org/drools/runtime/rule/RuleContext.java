package org.drools.runtime.rule;

import org.drools.definition.rule.Rule;
import org.drools.runtime.KnowledgeContext;

public interface RuleContext extends KnowledgeContext {
    Rule getRule();

    Activation getActivation();
}
