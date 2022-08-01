package org.optaplanner.core.impl.util;

/**
 * An immutable tuple of three values.
 * Two instances {@link #equals(Object) are equal} if all three values in the first instance are equal to their counterpart in
 * the other instance.
 *
 * @param <A>
 * @param <B>
 * @param <C>
 */
public interface Triple<A, B, C> {

    static <A, B, C> Triple<A, B, C> of(A a, B b, C c) {
        return new TripleImpl<>(a, b, c);
    }

    A getA();

    B getB();

    C getC();

}
