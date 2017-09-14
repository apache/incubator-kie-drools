package org.drools.model;

import org.drools.model.functions.Function1;

public interface BetaIndex<A, B, V> extends Index<A, V> {

    Function1<B, V> getRightOperandExtractor();
}