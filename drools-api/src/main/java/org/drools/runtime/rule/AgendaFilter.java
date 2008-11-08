package org.drools.runtime.rule;

public interface AgendaFilter {
    
    /**
     * Determine if a given activation should be fired.
     * 
     * @param activation
     *     The Activation that is requested to be fired
     * @return
     *     boolean value of "true" accepts the Activation for firing. 
     */
    boolean accept(Activation activation);
}
