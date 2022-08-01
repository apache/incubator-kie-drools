package org.optaplanner.core.impl.util;

import java.util.Objects;

final class TripleImpl<A, B, C> implements Triple<A, B, C> {

    private final A a;
    private final B b;
    private final C c;

    TripleImpl(A a, B b, C c) {
        this.a = a;
        this.b = b;
        this.c = c;
    }

    @Override
    public A getA() {
        return a;
    }

    @Override
    public B getB() {
        return b;
    }

    @Override
    public C getC() {
        return c;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o)
            return true;
        if (o == null || getClass() != o.getClass())
            return false;
        TripleImpl<A, B, C> that = (TripleImpl<A, B, C>) o;
        return Objects.equals(a, that.a)
                && Objects.equals(b, that.b)
                && Objects.equals(c, that.c);
    }

    @Override
    public int hashCode() {
        return Objects.hash(a, b, c);
    }

    @Override
    public String toString() {
        return "(" + a + ", " + b + ", " + c + ")";
    }

}
