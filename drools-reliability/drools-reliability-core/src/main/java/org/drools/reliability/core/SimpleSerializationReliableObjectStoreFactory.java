package org.drools.reliability.core;

import org.drools.core.common.Storage;
import org.kie.api.runtime.conf.PersistedSessionOption;

public class SimpleSerializationReliableObjectStoreFactory implements SimpleReliableObjectStoreFactory {

    static int servicePriorityValue = 0; // package access for test purposes

    public SimpleReliableObjectStore createSimpleReliableObjectStore(Storage<Long, StoredObject> storage) {
        return new SimpleSerializationReliableObjectStore(storage);
    }

    public SimpleReliableObjectStore createSimpleReliableObjectStore(Storage<Long, StoredObject> storage, PersistedSessionOption persistedSessionOption) {
        switch (persistedSessionOption.getPersistenceObjectsStrategy()){
            case SIMPLE: return new SimpleSerializationReliableObjectStore(storage);
            case OBJECT_REFERENCES: return new SimpleSerializationReliableRefObjectStore(storage);
            default: throw new UnsupportedOperationException();
        }
    }

    @Override
    public int servicePriority() {
        return servicePriorityValue;
    }
}
