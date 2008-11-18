package org.drools.runtime;

import org.drools.event.KnowledgeRuntimeEventManager;
import org.drools.runtime.process.StatelessProcessSession;
import org.drools.runtime.rule.StatelessRuleSession;

public interface StatelessKnowledgeSession extends  StatelessRuleSession, StatelessProcessSession, KnowledgeRuntimeEventManager {
   
    /**
     * Delegate used to resolve any global names not found in the global map.
     * @param globalResolver
     */
    void setGlobalResolver(GlobalResolver globalResolver);
    
    /**
     * Sets a global value
     * @param identifer
     * @param value
     */
    void setGlobal(String identifer, Object value); 
}
