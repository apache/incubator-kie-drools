package org.drools.runtime.rule;

import java.util.Collection;

import org.drools.knowledge.definitions.rule.Rule;

public interface Activation {
    /**
     * 
     * @return
     *     The Rule that was activated.
     */
    Rule getRule();
    
    /**
     * 
     * @return 
     *     The PropagationContext that created this Activation
     */
    PropagationContext getPropagationContext();   
    
    /**
     * 
     * @return
     *     The matched FactHandles for this activation
     */
    Collection<FactHandle> getFactHandles();        
}
