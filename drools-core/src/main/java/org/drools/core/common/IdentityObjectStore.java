package org.drools.core.common;

import java.util.IdentityHashMap;

public class IdentityObjectStore extends MapObjectStore {

    public IdentityObjectStore() {
        super(new IdentityHashMap<>());
    }

}
