package org.optaplanner.core.impl.util;

/**
 * A mutable key-value tuple.
 * Two instances {@link #equals(Object) are equal} if both values in the first instance are equal to their counterpart in
 * the other instance.
 *
 * @param <A>
 * @param <B>
 */
public interface MutablePair<A, B> extends Pair<A, B> {

    static <A, B> MutablePair<A, B> of(A key, B value) {
        return new MutablePairImpl<>(key, value);
    }

    MutablePair<A, B> setKey(A key);

    MutablePair<A, B> setValue(B value);

}
