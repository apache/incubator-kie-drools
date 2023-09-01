package org.drools.verifier.core.index.matchers;

import org.drools.verifier.core.index.query.Matchers;

public class UUIDMatcher {

    public static Matchers uuid() {
        return new UUIDMatchers();
    }
}
