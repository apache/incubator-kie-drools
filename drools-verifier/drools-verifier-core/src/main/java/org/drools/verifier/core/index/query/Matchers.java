package org.drools.verifier.core.index.query;

import org.drools.verifier.core.index.matchers.ExactMatcher;
import org.drools.verifier.core.index.matchers.KeyMatcher;
import org.drools.verifier.core.index.matchers.Matcher;
import org.drools.verifier.core.maps.KeyDefinition;

public class Matchers
        extends KeyMatcher {

    public Matchers(final KeyDefinition keyDefinition) {
        super(keyDefinition);
    }

    public ExactMatcher is(final Comparable comparable) {
        return new ExactMatcher(keyDefinition,
                                comparable);
    }

    public Matcher any() {
        return new Matcher(keyDefinition);
    }

    public Matcher isNot(final Comparable comparable) {
        return new ExactMatcher(keyDefinition,
                                comparable,
                                true);
    }
}
