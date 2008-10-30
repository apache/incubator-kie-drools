/**
 * 
 */
package org.drools.base;

import org.drools.runtime.rule.FactHandle;
import org.drools.WorkingMemory;

public class JavaFactRegistryEntry {
    private WorkingMemory workingMemory;
    private FactHandle    handle;

    public JavaFactRegistryEntry(final WorkingMemory workingMemory,
                                 final FactHandle handle) {
        super();
        this.workingMemory = workingMemory;
        this.handle = handle;
    }

    public FactHandle getFactHandle() {
        return this.handle;
    }

    public WorkingMemory getWorkingMemory() {
        return this.workingMemory;
    }

}