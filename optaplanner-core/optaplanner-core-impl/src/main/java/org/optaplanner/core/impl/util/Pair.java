package org.optaplanner.core.impl.util;

public interface Pair<A, B> {

    static <A, B> Pair<A, B> of(A key, B value) {
        return new MutablePairImpl<>(key, value);
    }

    A getKey();

    B getValue();

}
