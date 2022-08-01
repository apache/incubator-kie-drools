package org.optaplanner.core.impl.util;

/**
 * An immutable key-value tuple.
 * Two instances {@link #equals(Object) are equal} if both values in the first instance are equal to their counterpart in
 * the other instance.
 *
 * @param <A>
 * @param <B>
 */
public interface Pair<A, B> {

    static <A, B> Pair<A, B> of(A key, B value) {
        return new PairImpl<>(key, value);
    }

    A getKey();

    B getValue();

}
