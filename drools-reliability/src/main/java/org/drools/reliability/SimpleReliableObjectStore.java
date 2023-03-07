package org.drools.reliability;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

import org.drools.core.common.IdentityObjectStore;
import org.drools.core.common.InternalFactHandle;
import org.infinispan.Cache;

public class SimpleReliableObjectStore extends IdentityObjectStore {

    private final Cache<Object, Boolean> cache;

    public SimpleReliableObjectStore(Cache<Object, Boolean> cache) {
        super();
        this.cache = cache;
    }

    @Override
    public void addHandle(InternalFactHandle handle, Object object) {
        super.addHandle(handle, object);
        cache.put(object, handle.hasMatches());
    }

    @Override
    public void removeHandle(InternalFactHandle handle) {
        super.removeHandle(handle);
        cache.remove(handle.getObject());
    }

    Map<Boolean, List<Object>> takeObjectsGroupedByPropagation() {
        List<Object> propagated = new ArrayList<>();
        List<Object> notPropagated = new ArrayList<>();
        for (Map.Entry<Object, Boolean> entry : cache.entrySet()) {
            if (entry.getValue()) {
                propagated.add(entry.getKey());
            } else {
                notPropagated.add(entry.getKey());
            }
        }
        cache.clear();
        return Map.of(true, propagated, false, notPropagated);
    }
}
