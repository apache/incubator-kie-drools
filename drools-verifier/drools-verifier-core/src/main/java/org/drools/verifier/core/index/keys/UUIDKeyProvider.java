package org.drools.verifier.core.index.keys;

import org.drools.verifier.core.maps.util.HasKeys;

public abstract class UUIDKeyProvider {

    public UUIDKey get(final HasKeys hasKeys) {
        return new UUIDKey(hasKeys,
                           newUUID());
    }

    protected abstract String newUUID();
}
