package org.optaplanner.constraint.streams.bavet.common.index;

import org.optaplanner.core.impl.util.Pair;
import org.optaplanner.core.impl.util.Quadruple;
import org.optaplanner.core.impl.util.Triple;

/**
 * Index properties are cached in tuples and each tuple carries its unique instance.
 * <p>
 * Index properties are shallow immutable and implement {@link Object#equals(Object)} and {@link Object#hashCode()}.
 */
public interface IndexProperties {

    /**
     * Retrieves index property at a given position.
     * 
     * @param index
     * @return never null
     * @param <Type_> {@link ComparisonIndexer} will expect this to implement {@link Comparable}.
     */
    <Type_> Type_ getProperty(int index);

    int maxLength();

    /**
     *
     * @param fromInclusive position of the first index property to be part of the key, inclusive
     * @param toExclusive position of the last index property to be part of the key, exclusive
     * @return never null;
     * @param <Type_> any type understanding that two keys may point to different tuples unless their instances are equal
     */
    default <Type_> Type_ getIndexerKey(int fromInclusive, int toExclusive) {
        int length = toExclusive - fromInclusive;
        switch (length) {
            case 1:
                return getProperty(fromInclusive);
            case 2:
                return (Type_) Pair.of(getProperty(fromInclusive), getProperty(fromInclusive + 1));
            case 3:
                return (Type_) Triple.of(getProperty(fromInclusive), getProperty(fromInclusive + 1),
                        getProperty(fromInclusive + 2));
            case 4:
                return (Type_) Quadruple.of(getProperty(fromInclusive), getProperty(fromInclusive + 1),
                        getProperty(fromInclusive + 2), getProperty(fromInclusive + 3));
            default:
                return (Type_) new IndexerKey(this, fromInclusive, toExclusive);
        }
    }

}
