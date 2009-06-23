package org.drools.runtime.rule;

import org.drools.definition.rule.Rule;
import org.drools.runtime.KnowledgeContext;

public interface RuleContext extends KnowledgeContext {
    
    /**
     * Returns the active Rule for the current context
     *  
     * @return
     */
    Rule getRule();

    /**
     * Returns the current Activation for the current context
     * 
     * @return
     */
    Activation getActivation();
    
    /**
     * Logically inserts a fact into the KnowledgeSession, justified by the current
     * rule context.
     * 
     * @param object the fact to insert into the knowledge session
     */
    void insertLogical(Object object);
    
}
