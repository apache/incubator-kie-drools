package org.drools.verifier.core.index.matchers;

import org.drools.verifier.core.index.keys.Value;
import org.drools.verifier.core.maps.KeyDefinition;

public class ExactMatcher
        extends Matcher {

    private final Value value;

    private final boolean negate;

    public ExactMatcher(final KeyDefinition keyDefinition,
                        final Comparable value) {
        this(keyDefinition,
             value,
             false);
    }

    public ExactMatcher(final KeyDefinition keyDefinition,
                        final Comparable value,
                        final boolean negate) {
        super(keyDefinition);
        this.value = new Value(value);
        this.negate = negate;
    }

    public Value getValue() {
        return value;
    }

    public boolean isNegate() {
        return negate;
    }
}
