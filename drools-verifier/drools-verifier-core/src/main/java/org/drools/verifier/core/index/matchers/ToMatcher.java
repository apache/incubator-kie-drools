package org.drools.verifier.core.index.matchers;

import org.drools.verifier.core.index.keys.Value;
import org.drools.verifier.core.maps.KeyDefinition;

public class ToMatcher
        extends Matcher {

    private final Value to;

    public ToMatcher(final KeyDefinition keyDefinition,
                     final Comparable to) {
        super(keyDefinition);
        this.to = new Value(to);
    }

    public Value getTo() {
        return to;
    }
}
