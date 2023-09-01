package org.drools.verifier.core.index.keys;

import org.drools.verifier.core.maps.KeyDefinition;

public class IndexKey
        extends Key {

    public static final KeyDefinition INDEX_ID = KeyDefinition.newKeyDefinition().withId("index---id").updatable().build();

    public IndexKey(int index) {
        super(INDEX_ID,
              index);
    }
}
