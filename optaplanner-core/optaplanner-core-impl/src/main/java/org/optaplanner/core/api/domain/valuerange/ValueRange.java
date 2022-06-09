package org.optaplanner.core.api.domain.valuerange;

import java.util.Collection;
import java.util.Iterator;
import java.util.List;
import java.util.Random;
import java.util.Set;

import org.optaplanner.core.api.domain.variable.PlanningVariable;

/**
 * A ValueRange is a set of a values for a {@link PlanningVariable}.
 * These values might be stored in memory as a {@link Collection} (usually a {@link List} or {@link Set}),
 * but if the values are numbers, they can also be stored in memory by their bounds
 * to use less memory and provide more opportunities.
 * <p>
 * ValueRange is stateful.
 * Prefer using {@link CountableValueRange} (which extends this interface) whenever possible.
 * Implementations must be immutable.
 *
 * @see ValueRangeFactory
 * @see CountableValueRange
 */
public interface ValueRange<T> {

    /**
     * In a {@link CountableValueRange}, this must be consistent with {@link CountableValueRange#getSize()}.
     *
     * @return true if the range is empty
     */
    boolean isEmpty();

    /**
     * @param value sometimes null
     * @return true if the ValueRange contains that value
     */
    boolean contains(T value);

    /**
     * Select in random order, but without shuffling the elements.
     * Each element might be selected multiple times.
     * Scales well because it does not require caching.
     *
     * @param workingRandom never null, the {@link Random} to use when any random number is needed,
     *        so runs are reproducible.
     * @return never null
     */
    Iterator<T> createRandomIterator(Random workingRandom);

}
