package org.optaplanner.core.impl.util;

/**
 * A mutable tuple of three values.
 * Two instances {@link #equals(Object) are equal} if all three values in the first instance are equal to their counterpart in
 * the other instance.
 *
 * @param <A>
 * @param <B>
 * @param <C>
 */
public interface MutableTriple<A, B, C> extends Triple<A, B, C> {

    static <A, B, C> MutableTriple<A, B, C> of(A a, B b, C c) {
        return new MutableTripleImpl<>(a, b, c);
    }

    MutableTriple<A, B, C> setA(A a);

    MutableTriple<A, B, C> setB(B b);

    MutableTriple<A, B, C> setC(C c);

}
