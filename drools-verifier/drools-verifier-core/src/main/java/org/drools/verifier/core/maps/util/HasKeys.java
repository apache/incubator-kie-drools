package org.drools.verifier.core.maps.util;

import org.drools.verifier.core.index.keys.Key;

public interface HasKeys
        extends HasUUID {

    Key[] keys();
}
