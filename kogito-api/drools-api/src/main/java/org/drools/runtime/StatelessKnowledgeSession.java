package org.drools.runtime;

import org.drools.event.process.ProcessEventManager;
import org.drools.event.rule.WorkingMemoryEventManager;
import org.drools.runtime.process.StatelessProcessSession;
import org.drools.runtime.rule.StatelessRuleSession;

public interface StatelessKnowledgeSession extends  StatelessRuleSession, StatelessProcessSession, WorkingMemoryEventManager, ProcessEventManager {
   
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
