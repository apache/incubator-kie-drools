/**
 * 
 */
package org.drools.util;

import org.drools.common.InternalFactHandle;

public class TupleEntry extends BaseEntry {
    private InternalFactHandle handle;

    public TupleEntry(InternalFactHandle handle) {
        this.handle = handle;
    }

    public String toString() {
        return this.handle.getObject().toString();
    }

    public int hashCode() {
        return this.handle.hashCode();
    }

    public boolean equals(Object object) {
        // Inside the rete network we know that it will always be a TupleEntry
        // We know it will never be null
        TupleEntry other = (TupleEntry) object;
        return (this.handle.getId() == other.handle.getId());
    }
}