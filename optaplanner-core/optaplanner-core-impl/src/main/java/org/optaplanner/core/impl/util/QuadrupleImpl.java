package org.optaplanner.core.impl.util;

import java.util.Objects;

final class QuadrupleImpl<A, B, C, D> implements Quadruple<A, B, C, D> {

    private final A a;
    private final B b;
    private final C c;
    private final D d;

    QuadrupleImpl(A a, B b, C c, D d) {
        this.a = a;
        this.b = b;
        this.c = c;
        this.d = d;
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
    public D getD() {
        return d;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o)
            return true;
        if (o == null || getClass() != o.getClass())
            return false;
        QuadrupleImpl<A, B, C, D> that = (QuadrupleImpl<A, B, C, D>) o;
        return Objects.equals(a, that.a)
                && Objects.equals(b, that.b)
                && Objects.equals(c, that.c)
                && Objects.equals(d, that.d);
    }

    @Override
    public int hashCode() { // Not using Objects.hash(Object...) as that would create an array on the hot path.
        int result = Objects.hashCode(a);
        result = 31 * result + Objects.hashCode(b);
        result = 31 * result + Objects.hashCode(c);
        result = 31 * result + Objects.hashCode(d);
        return result;
    }

    @Override
    public String toString() {
        return "(" + a + ", " + b + ", " + c + ", " + d + ")";
    }

}
