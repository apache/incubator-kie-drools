package org.optaplanner.core.impl.util;

/**
 * A mutable tuple of four values.
 * Two instances {@link #equals(Object) are equal} if all four values in the first instance are equal to their counterpart in
 * the other instance.
 *
 * @param <A>
 * @param <B>
 * @param <C>
 * @param <D>
 */
public interface MutableQuadruple<A, B, C, D> extends Quadruple<A, B, C, D> {

    static <A, B, C, D> MutableQuadruple<A, B, C, D> of(A a, B b, C c, D d) {
        return new MutableQuadrupleImpl<>(a, b, c, d);
    }

    MutableQuadruple<A, B, C, D> setA(A a);

    MutableQuadruple<A, B, C, D> setB(B b);

    MutableQuadruple<A, B, C, D> setC(C c);

    MutableQuadruple<A, B, C, D> setD(D d);

}
