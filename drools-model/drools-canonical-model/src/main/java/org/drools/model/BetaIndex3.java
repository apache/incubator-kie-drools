package org.drools.model;

import org.drools.model.functions.Function3;

public interface BetaIndex3<A, B, C, D, V> extends BetaIndexN<A, V> {

    Function3<B, C, D, ?> getRightOperandExtractor();

    default int getArity() {
        return 3;
    }
}
