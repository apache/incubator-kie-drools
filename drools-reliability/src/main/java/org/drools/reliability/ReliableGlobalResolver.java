package org.drools.reliability;

import java.util.HashSet;
import java.util.Set;

import org.drools.core.rule.accessor.GlobalResolver;
import org.infinispan.Cache;

public class ReliableGlobalResolver implements GlobalResolver {
    private final Cache<String, Object> cache;

    private final Set<String> toBeRefreshed = new HashSet<>();

    public ReliableGlobalResolver(Cache<String, Object> cache) {
        this.cache = cache;
    }

    @Override
    public Object resolveGlobal(String identifier) {
        toBeRefreshed.add(identifier);
        return cache.get(identifier);
    }

    @Override
    public void setGlobal(String identifier, Object value) {
        cache.put(identifier, value);
    }

    @Override
    public void removeGlobal(String identifier) {
        cache.remove(identifier);
    }

    @Override
    public void clear() {
        cache.clear();
    }

    public void updateCache() {
        if (!toBeRefreshed.isEmpty()) {
            toBeRefreshed.forEach( id -> cache.put(id, cache.get(id)));
            toBeRefreshed.clear();
        }
    }
}
