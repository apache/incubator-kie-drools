package org.drools.runtime.rule;

import org.drools.knowledge.definitions.rule.Rule;
import org.drools.runtime.KnowledgeRuntime;

public interface RuleContext {
    Rule getRule();
    
    Activation getActivation();
    
    KnowledgeRuntime getKnowledgeRuntime();            
}
