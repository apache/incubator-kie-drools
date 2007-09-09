/**
 * 
 */
package org.drools.spi;

import java.io.Serializable;

import org.drools.WorkingMemory;

/**
 * Used to provide a strategy for the StatelessSession global exportation, so that StatelessSessionResult can have accesso to
 * globals using during the execute(...) method that returned the StatelessSessionResult.
 *
 */
public interface GlobalExporter extends Serializable {
    
    /**
     * This method is called internally by the StatelessSession, which will provide the WorkingMemory.
     * The returned GlobalResolver is used by the StatefulSessionResult
     * @param workingMemory
     * @return
     *       The GlobalResolver instance as used by the StatefulSessionResult
     */
    public GlobalResolver export(WorkingMemory workingMemory);
}