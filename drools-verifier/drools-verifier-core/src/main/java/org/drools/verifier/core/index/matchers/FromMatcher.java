package org.drools.verifier.core.index.matchers;

import org.drools.verifier.core.index.keys.Value;
import org.drools.verifier.core.maps.KeyDefinition;

public class FromMatcher
        extends Matcher {

    private final Value from;
    private final boolean includeSetValue;

    public FromMatcher(final KeyDefinition keyDefinition,
                       final Comparable from) {
        this(keyDefinition,
             from,
             false);
    }

    public FromMatcher(final KeyDefinition keyDefinition,
                       final Comparable from,
                       final boolean includeSetValue) {
        super(keyDefinition);
        this.from = new Value(from);
        this.includeSetValue = includeSetValue;
    }

    public Value getFrom() {
        return from;
    }

    public boolean includeValue() {
        return includeSetValue;
    }
}
