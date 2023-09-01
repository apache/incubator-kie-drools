package org.drools.reliability.core;

import java.io.Serializable;

import org.drools.core.common.InternalWorkingMemoryEntryPoint;
import org.kie.api.runtime.rule.FactHandle;

public abstract class BaseStoredObject implements StoredObject,
                                                  Serializable {

    protected final boolean propagated;

    protected BaseStoredObject(boolean propagated) {
        this.propagated = propagated;
    }

    @Override
    public boolean isPropagated() {
        return propagated;
    }

    @Override
    public long repropagate(InternalWorkingMemoryEntryPoint ep) {
        FactHandle factHandle = ep.insert(getObject());
        return factHandle.getId();
    }
}