package org.drools.runtime.rule;

import org.drools.definition.rule.Rule;
import org.drools.runtime.KnowledgeRuntime;

public interface RuleContext {
    Rule getRule();
    
    Activation getActivation();
    
    KnowledgeRuntime getKnowledgeRuntime();            
}
