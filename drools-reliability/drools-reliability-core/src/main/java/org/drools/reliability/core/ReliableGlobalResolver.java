package org.drools.reliability.core;

import org.drools.core.common.Storage;
import org.drools.base.rule.accessor.GlobalResolver;

import java.util.HashMap;
import java.util.Map;

public class ReliableGlobalResolver implements GlobalResolver {
    protected final Storage<String, Object> storage;

    protected final Map<String, Object> toBeRefreshed = new HashMap<>();

    public ReliableGlobalResolver(Storage<String, Object> storage) {
        this.storage = storage;
    }

    @Override
    public Object resolveGlobal(String identifier) {
        // Use an in-memory global reference. Avoid getting a stale object from storage
        if (toBeRefreshed.containsKey(identifier)) {
            return toBeRefreshed.get(identifier);
        }
        Object global = storage.get(identifier);
        toBeRefreshed.put(identifier, global);
        return global;
    }

    @Override
    public void setGlobal(String identifier, Object value) {
        storage.put(identifier, value);
    }

    @Override
    public void removeGlobal(String identifier) {
        storage.remove(identifier);
    }

    @Override
    public void clear() {
        storage.clear();
    }

    public void updateStorage() {
        if (!toBeRefreshed.isEmpty()) {
            toBeRefreshed.forEach(storage::put);
            toBeRefreshed.clear();
        }
    }
}
