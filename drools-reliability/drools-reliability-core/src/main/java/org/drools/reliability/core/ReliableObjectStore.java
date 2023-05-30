package org.drools.reliability.core;

import org.drools.core.common.ObjectStore;

public interface ReliableObjectStore extends ObjectStore {
    void safepoint();
}
