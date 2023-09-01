package org.drools.reliability.infinispan;

import org.drools.core.common.Storage;
import org.drools.reliability.core.ReliableGlobalResolver;
import org.drools.reliability.core.ReliableGlobalResolverFactory;
import org.drools.reliability.core.StorageManagerFactory;
import org.drools.reliability.infinispan.proto.ProtoStreamReliableGlobalResolver;

public class InfinispanReliableGlobalResolverFactory implements ReliableGlobalResolverFactory {

    static int servicePriorityValue = 0; // package access for test purposes

    @Override
    public ReliableGlobalResolver createReliableGlobalResolver(Storage<String, Object> storage) {
        if (((InfinispanStorageManager) StorageManagerFactory.get().getStorageManager()).isProtoStream()) {
            return new ProtoStreamReliableGlobalResolver(storage);
        } else {
            return new ReliableGlobalResolver(storage);
        }
    }

    @Override
    public int servicePriority() {
        return servicePriorityValue;
    }
}
