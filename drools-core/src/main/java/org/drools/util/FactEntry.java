/**
 * 
 */
package org.drools.util;

import org.drools.common.InternalFactHandle;

public class FactEntry extends BaseEntry {
    private InternalFactHandle handle;

    public FactEntry(InternalFactHandle handle) {
        this.handle = handle;
    }

    public String toString() {
        return this.handle.getObject().toString();
    }

    public InternalFactHandle getFactHandle() {
        return this.handle;
    }

    public int hashCode() {
        return this.handle.hashCode();
    }

    public boolean equals(Object object) {
        // Inside the rete network we know that it will always be a TupleEntry
        // We know it will never be null
        FactEntry other = (FactEntry) object;
        return (this.handle.getId() == other.handle.getId());
    }
}