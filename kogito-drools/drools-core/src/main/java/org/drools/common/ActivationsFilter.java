package org.drools.common;

import org.drools.reteoo.RuleTerminalNode;
import org.drools.spi.Activation;
import org.drools.spi.PropagationContext;

/**
 * A filter interface for agenda activations
 */
public interface ActivationsFilter {

    /**
     * Returns true if a new activation should be created for the given propagation
     * or false otherwise
     * 
     * @param tuple
     * @param context
     * @param workingMemory
     * @param rtn
     * @return
     */
    boolean accept(Activation activation,
                   PropagationContext context,
                   InternalWorkingMemory workingMemory,
                   RuleTerminalNode rtn );

}
