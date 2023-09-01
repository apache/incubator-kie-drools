package org.drools.verifier.core.index.matchers;

import org.drools.verifier.core.maps.KeyDefinition;

public class KeyMatcher {

    protected final KeyDefinition keyDefinition;

    public KeyMatcher(final KeyDefinition keyDefinition) {
        this.keyDefinition = keyDefinition;
    }

    public KeyDefinition getKeyDefinition() {
        return keyDefinition;
    }
}
