package org.drools.model;

import org.drools.model.functions.Function4;

public interface BetaIndex4<A, B, C, D, E, V> extends BetaIndexN<A, V> {

    Function4<B, C, D, E, ?> getRightOperandExtractor();

    default int getArity() {
        return 4;
    }
}
