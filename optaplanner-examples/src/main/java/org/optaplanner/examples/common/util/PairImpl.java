package org.optaplanner.examples.common.util;

import java.util.Objects;

final class PairImpl<A, B> implements Pair<A, B> {

    private final A key;
    private final B value;

    public PairImpl(A key, B value) {
        this.key = key;
        this.value = value;
    }

    @Override
    public A getKey() {
        return key;
    }

    @Override
    public B getValue() {
        return value;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o)
            return true;
        if (o == null || getClass() != o.getClass())
            return false;
        PairImpl<?, ?> that = (PairImpl<?, ?>) o;
        return Objects.equals(key, that.key) && Objects.equals(value, that.value);
    }

    @Override
    public int hashCode() {
        return Objects.hash(key, value);
    }

    @Override
    public String toString() {
        return "(" + key + ", " + value + ")";
    }
}
