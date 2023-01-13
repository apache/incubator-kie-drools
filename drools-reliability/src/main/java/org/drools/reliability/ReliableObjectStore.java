package org.drools.reliability;

import org.drools.core.common.InternalFactHandle;
import org.drools.core.common.MapObjectStore;
import org.infinispan.Cache;

public class ReliableObjectStore extends MapObjectStore {

    public ReliableObjectStore(Cache<Object, InternalFactHandle> fhCache) {
        super(fhCache);
    }

}
