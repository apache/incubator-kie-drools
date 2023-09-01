package org.drools.verifier.core.relations;

import java.util.HashMap;

import org.drools.verifier.core.index.keys.UUIDKey;

public class Conflicts {

    public final HashMap<UUIDKey, Conflict> keyMap = new HashMap<>();
    private boolean record;

    public Conflicts(final boolean record) {
        this.record = record;
    }

    public void add(final Conflict conflict) {
        if (record) {
            keyMap.put(conflict.otherUUID(), conflict);
        }
    }

    public Conflict get(final UUIDKey otherUUID) {
        return keyMap.get(otherUUID);
    }

    public void remove(final Conflict first) {
        if (record) {
            keyMap.remove(first.otherUUID());
        }
    }
}
