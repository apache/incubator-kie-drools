package org.optaplanner.core.impl.util;

/**
 * An immutable tuple of four values.
 * Two instances {@link #equals(Object) are equal} if all four values in the first instance are equal to their counterpart in
 * the other instance.
 *
 * @param <A>
 * @param <B>
 * @param <C>
 * @param <D>
 */
public interface Quadruple<A, B, C, D> {

    static <A, B, C, D> Quadruple<A, B, C, D> of(A a, B b, C c, D d) {
        return new QuadrupleImpl<>(a, b, c, d);
    }

    A getA();

    B getB();

    C getC();

    D getD();

}
