package org.drools.reliability.infinispan;

import org.drools.core.common.Storage;
import org.drools.reliability.core.SimpleReliableObjectStore;
import org.drools.reliability.core.SimpleReliableObjectStoreFactory;
import org.drools.reliability.core.SimpleSerializationReliableObjectStore;
import org.drools.reliability.core.StorageManagerFactory;
import org.drools.reliability.core.StoredObject;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class SimpleInfinispanReliableObjectStoreFactory implements SimpleReliableObjectStoreFactory {

    private static final Logger LOG = LoggerFactory.getLogger(SimpleInfinispanReliableObjectStoreFactory.class);

    static final SimpleInfinispanReliableObjectStoreFactory INSTANCE = new SimpleInfinispanReliableObjectStoreFactory();

    public static SimpleInfinispanReliableObjectStoreFactory get() {
        return INSTANCE;
    }

    public SimpleReliableObjectStore createSimpleReliableObjectStore(Storage<Long, StoredObject> storage) {
        if (((InfinispanStorageManager)StorageManagerFactory.get().getStorageManager()).isProtoStream()) {
            LOG.debug("Using SimpleProtoStreamReliableObjectStore");
            return new SimpleProtoStreamReliableObjectStore(storage);
        } else {
            LOG.debug("Using SimpleSerializationReliableObjectStore");
            return new SimpleSerializationReliableObjectStore(storage);
        }
    }
}
