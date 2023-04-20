package org.drools.reliability.core;

import org.drools.core.common.Storage;

public class SimpleSerializationReliableObjectStoreFactory implements SimpleReliableObjectStoreFactory {

    static final SimpleSerializationReliableObjectStoreFactory INSTANCE = new SimpleSerializationReliableObjectStoreFactory();

    public static SimpleSerializationReliableObjectStoreFactory get() {
        return INSTANCE;
    }

    public SimpleReliableObjectStore createSimpleReliableObjectStore(Storage<Long, StoredObject> storage) {
        return new SimpleSerializationReliableObjectStore(storage);
    }
}
