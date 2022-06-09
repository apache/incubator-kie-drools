package org.optaplanner.core.impl.util;

public interface MutableQuadruple<A, B, C, D> extends Quadruple<A, B, C, D> {

    static <A, B, C, D> MutableQuadruple<A, B, C, D> of(A a, B b, C c, D d) {
        return new MutableQuadrupleImpl<>(a, b, c, d);
    }

    MutableQuadruple<A, B, C, D> setA(A a);

    MutableQuadruple<A, B, C, D> setB(B b);

    MutableQuadruple<A, B, C, D> setC(C c);

    MutableQuadruple<A, B, C, D> setD(D d);

}
