package org.drools.verifier.core.index.matchers;

import org.drools.verifier.core.maps.KeyDefinition;

public class Matcher {

    protected final KeyDefinition keyDefinition;

    public Matcher(final KeyDefinition keyDefinition) {
        this.keyDefinition = keyDefinition;
    }

    public KeyDefinition getKeyDefinition() {
        return keyDefinition;
    }
}
