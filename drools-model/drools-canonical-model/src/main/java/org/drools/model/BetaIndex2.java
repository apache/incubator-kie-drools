package org.drools.model;

import org.drools.model.functions.Function2;

public interface BetaIndex2<A, B, C, V> extends BetaIndexN<A, V> {

    Function2<B, C, ?> getRightOperandExtractor();

    default int getArity() {
        return 2;
    }
}
