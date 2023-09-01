package org.drools.reliability.infinispan.proto;

import org.drools.core.common.Storage;
import org.drools.reliability.core.ReliableGlobalResolver;

public class ProtoStreamReliableGlobalResolver extends ReliableGlobalResolver {

    public ProtoStreamReliableGlobalResolver(Storage<String, Object> storage) {
        super(storage);
    }

    @Override
    public Object resolveGlobal(String identifier) {
        // Use an in-memory global reference. Avoid getting a stale object from storage
        if (toBeRefreshed.containsKey(identifier)) {
            return toBeRefreshed.get(identifier);
        }
        ProtoStreamGlobal protoGlobal = (ProtoStreamGlobal)storage.get(identifier);
        Object global = protoGlobal.getObject();
        toBeRefreshed.put(identifier, global);
        return global;
    }

    @Override
    public void setGlobal(String identifier, Object value) {
        storage.put(identifier, new ProtoStreamGlobal(value));
    }

    @Override
    public void updateStorage() {
        if (!toBeRefreshed.isEmpty()) {
            toBeRefreshed.forEach((k, v) -> storage.put(k, new ProtoStreamGlobal(v)));
            toBeRefreshed.clear();
        }
    }
}
