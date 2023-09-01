package org.drools.verifier.core.relations;

import org.drools.verifier.core.maps.util.HasUUID;

public interface IsConflicting<T>
        extends HasUUID {

    boolean conflicts(final T other);

    interface Covers<T> {

        boolean covers(Comparable<T> other);
    }
}
