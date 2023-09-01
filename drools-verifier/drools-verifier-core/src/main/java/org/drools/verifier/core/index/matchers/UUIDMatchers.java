package org.drools.verifier.core.index.matchers;

import org.drools.verifier.core.index.keys.UUIDKey;
import org.drools.verifier.core.index.query.Matchers;

public class UUIDMatchers
        extends Matchers {

    public UUIDMatchers() {
        super(UUIDKey.UNIQUE_UUID);
    }
}
