package org.drools.verifier.core.maps;

import org.drools.verifier.core.index.keys.UUIDKey;

public interface RetractHandler {

    void retract(final UUIDKey uuidKey);
}
