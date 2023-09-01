package org.drools.verifier.core.index.query;

import org.drools.verifier.core.index.select.Listen;
import org.drools.verifier.core.index.select.Select;

public abstract class Where<S extends Select, L extends Listen> {

    public abstract S select();

    public abstract L listen();
}
