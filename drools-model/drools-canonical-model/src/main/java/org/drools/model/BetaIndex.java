package org.drools.model;

import org.drools.model.functions.Function1;

public interface BetaIndex<A, B, V> extends BetaIndexN<A, V> {

    Function1<B, ?> getRightOperandExtractor();

    default int getArity() {
        return 1;
    }
}
