package org.drools.core.common;

import org.drools.core.reteoo.TerminalNode;
import org.drools.core.spi.Activation;

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
                   InternalWorkingMemory workingMemory,
                   TerminalNode rtn );

}
