/**
 * 
 */
package org.drools.common;

import java.io.Serializable;

public interface WorkingMemoryAction extends Serializable {
    public void execute(InternalWorkingMemory workingMemory);
}