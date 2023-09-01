package org.drools.verifier.core.maps.util;

import org.drools.verifier.core.relations.Conflict;

public interface HasConflicts<T> {

    Conflict hasConflicts();
}
