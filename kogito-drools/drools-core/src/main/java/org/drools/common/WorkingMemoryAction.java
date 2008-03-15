/**
 *
 */
package org.drools.common;

import java.io.Externalizable;

public interface WorkingMemoryAction extends Externalizable {
    public void execute(InternalWorkingMemory workingMemory);
}