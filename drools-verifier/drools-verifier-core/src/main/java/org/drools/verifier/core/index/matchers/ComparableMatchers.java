package org.drools.verifier.core.index.matchers;

import org.drools.verifier.core.index.query.Matchers;
import org.drools.verifier.core.maps.KeyDefinition;

public class ComparableMatchers
        extends Matchers {

    public ComparableMatchers(final KeyDefinition keyDefinition) {
        super(keyDefinition);
    }

    public FromMatcher greaterThan(final Comparable i) {
        return new FromMatcher(keyDefinition,
                               i);
    }

    public ToMatcher lessThan(final Comparable i) {
        return new ToMatcher(keyDefinition,
                             i);
    }
}
