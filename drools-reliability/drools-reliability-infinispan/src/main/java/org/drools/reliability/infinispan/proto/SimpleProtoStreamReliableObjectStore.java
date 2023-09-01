package org.drools.reliability.infinispan.proto;

import org.drools.core.common.Storage;
import org.drools.reliability.core.SimpleSerializationReliableObjectStore;
import org.drools.reliability.core.StoredEvent;
import org.drools.reliability.core.StoredObject;

public class SimpleProtoStreamReliableObjectStore extends SimpleSerializationReliableObjectStore {

    public SimpleProtoStreamReliableObjectStore() {
        throw new UnsupportedOperationException("This constructor should never be called");
    }

    public SimpleProtoStreamReliableObjectStore(Storage<Long, StoredObject> storage) {
        super(storage);
    }

    @Override
    protected StoredObject createStoredObject(boolean propagated, Object object) {
        return new ProtoStreamStoredObject(object, propagated);
    }

    @Override
    protected StoredEvent createStoredEvent(boolean propagated, Object object, long timestamp, long duration) {
        return new ProtoStreamStoredEvent(object, propagated, timestamp, duration);
    }
}
