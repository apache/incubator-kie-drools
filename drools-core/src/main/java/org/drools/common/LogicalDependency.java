package org.drools.common;

import org.drools.FactHandle;
import org.drools.spi.Activation;
import org.drools.util.AbstractBaseLinkedListNode;

public class LogicalDependency extends AbstractBaseLinkedListNode {
    private Activation justifier;
    private FactHandle factHandle;

    public LogicalDependency(Activation justifier,
                             FactHandle factHandle) {
        super();
        this.justifier = justifier;
        this.factHandle = factHandle;
    }

    public FactHandle getFactHandle() {
        return factHandle;
    }

    public Activation getJustifier() {
        return justifier;
    }

    public boolean equals(Object object) {
        if ( object == null || !(object.getClass() != this.getClass()) ) {
            return false;
        }

        if ( this == object ) {
            return true;
        }

        LogicalDependency other = (LogicalDependency) object;
        return (this.getJustifier() == other.getJustifier() && this.getFactHandle() == other.getFactHandle());
    }

    public int hashCode() {
        return this.justifier.hashCode();
    }
}