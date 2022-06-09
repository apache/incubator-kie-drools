package org.optaplanner.constraint.streams.drools.common;

import java.util.Arrays;
import java.util.List;
import java.util.Objects;

final class QuadTuple<A, B, C, D> implements FactTuple {
    public final A a;
    public final B b;
    public final C c;
    public final D d;
    private final int hashCode;

    public QuadTuple(A a, B b, C c, D d) {
        this.a = a;
        this.b = b;
        this.c = c;
        this.d = d;
        this.hashCode = Objects.hash(a, b, c, d);
    }

    @Override
    public List<Object> asList() {
        return Arrays.asList(a, b, c, d);
    }

    @Override
    public boolean equals(final Object o) {
        if (this == o) {
            return true;
        }
        if (o == null || !Objects.equals(getClass(), o.getClass())) {
            return false;
        }
        final QuadTuple<?, ?, ?, ?> other = (QuadTuple<?, ?, ?, ?>) o;
        return hashCode == other.hashCode &&
                Objects.equals(a, other.a) &&
                Objects.equals(b, other.b) &&
                Objects.equals(c, other.c) &&
                Objects.equals(d, other.d);
    }

    @Override
    public int hashCode() {
        return hashCode;
    }

    @Override
    public String toString() {
        return "QuadTuple(" + a + ", " + b + ", " + c + ", " + d + ")";
    }
}
