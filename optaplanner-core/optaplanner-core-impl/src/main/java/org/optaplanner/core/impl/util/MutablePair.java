package org.optaplanner.core.impl.util;

public interface MutablePair<A, B> extends Pair<A, B> {

    static <A, B> MutablePair<A, B> of(A key, B value) {
        return new MutablePairImpl<>(key, value);
    }

    MutablePair<A, B> setKey(A key);

    MutablePair<A, B> setValue(B value);

}
