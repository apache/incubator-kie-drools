package org.drools.verifier.core.maps;

import java.util.Collection;
import java.util.HashSet;

import org.drools.verifier.core.index.keys.UUIDKey;

public class UUIDKeySet
        extends HashSet<UUIDKey>
        implements RetractHandler {

    private KeyTreeMap keyTreeMap;

    public UUIDKeySet(final KeyTreeMap keyTreeMap) {
        this.keyTreeMap = keyTreeMap;
    }

    public UUIDKeySet() {
    }

    @Override
    public boolean add(final UUIDKey uuidKey) {

        uuidKey.addRetractHandler(this);

        return super.add(uuidKey);
    }

    @Override
    public boolean addAll(final Collection<? extends UUIDKey> keys) {
        for (final UUIDKey uuidKey : keys) {
            uuidKey.addRetractHandler(this);
        }

        return super.addAll(keys);
    }

    @Override
    public void retract(final UUIDKey uuidKey) {
        keyTreeMap.remove(uuidKey);
    }
}
