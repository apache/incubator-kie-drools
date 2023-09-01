package org.drools.verifier.core.relations;

import java.util.HashMap;

import org.drools.verifier.core.index.keys.UUIDKey;

public class SubsumptionBlockers {

    public final HashMap<UUIDKey, SubsumptionBlocker> keyMap = new HashMap<>();
    private boolean record;

    public SubsumptionBlockers(final boolean record) {
        this.record = record;
    }

    public void add(final SubsumptionBlocker blocker) {
        if (record) {
            keyMap.put(blocker.otherUUID(), blocker);
        }
    }

    public SubsumptionBlocker get(final UUIDKey uuidKey) {
        return keyMap.get(uuidKey);
    }

    public void remove(final SubsumptionBlocker blocker) {
        if (record) {
            keyMap.remove(blocker.otherUUID());
        }
    }

    public int size() {
        return keyMap.size();
    }
}
