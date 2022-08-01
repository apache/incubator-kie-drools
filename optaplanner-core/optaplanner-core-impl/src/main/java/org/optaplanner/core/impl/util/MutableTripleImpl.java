package org.optaplanner.core.impl.util;

import java.util.Objects;

final class MutableTripleImpl<A, B, C> implements MutableTriple<A, B, C> {

    private A a;
    private B b;
    private C c;

    MutableTripleImpl(A a, B b, C c) {
        this.a = a;
        this.b = b;
        this.c = c;
    }

    @Override
    public MutableTriple<A, B, C> setA(A a) {
        this.a = a;
        return this;
    }

    @Override
    public MutableTriple<A, B, C> setB(B b) {
        this.b = b;
        return this;
    }

    @Override
    public MutableTriple<A, B, C> setC(C c) {
        this.c = c;
        return this;
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
        MutableTripleImpl<A, B, C> that = (MutableTripleImpl<A, B, C>) o;
        return Objects.equals(a, that.a)
                && Objects.equals(b, that.b)
                && Objects.equals(c, that.c);
    }

    @Override
    public int hashCode() { // Not using Objects.hash(Object...) as that would create an array on the hot path.
        int result = Objects.hashCode(a);
        result = 31 * result + Objects.hashCode(b);
        result = 31 * result + Objects.hashCode(c);
        return result;
    }

    @Override
    public String toString() {
        return "(" + a + ", " + b + ", " + c + ")";
    }

}
