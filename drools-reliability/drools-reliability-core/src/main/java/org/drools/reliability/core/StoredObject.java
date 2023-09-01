package org.drools.reliability.core;

import org.drools.core.common.InternalWorkingMemoryEntryPoint;

public interface StoredObject {

    default boolean isEvent() {
        return false;
    }

    boolean isPropagated();

    Object getObject();

    long repropagate(InternalWorkingMemoryEntryPoint ep);
}