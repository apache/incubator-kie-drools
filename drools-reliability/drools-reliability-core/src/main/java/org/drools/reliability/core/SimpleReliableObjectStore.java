package org.drools.reliability.core;


import java.util.List;

import org.drools.core.common.InternalFactHandle;
import org.drools.core.common.InternalWorkingMemory;
import org.drools.core.common.InternalWorkingMemoryEntryPoint;
import org.drools.core.common.ObjectStore;

public interface SimpleReliableObjectStore extends ObjectStore {

    List<StoredObject> reInit(InternalWorkingMemory session, InternalWorkingMemoryEntryPoint ep);

    void putIntoPersistedStorage(InternalFactHandle handle, boolean propagated);

    void removeFromPersistedStorage(Object object);
}
