/*
 * Copyright 2015 Red Hat, Inc. and/or its affiliates.
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *      http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package org.optaplanner.core.api.domain.valuerange;

import java.util.Collection;
import java.util.Iterator;
import java.util.List;
import java.util.Random;
import java.util.Set;

import org.optaplanner.core.api.domain.variable.PlanningVariable;
import org.optaplanner.core.config.solver.EnvironmentMode;
import org.optaplanner.core.impl.domain.valuerange.AbstractCountableValueRange;
import org.optaplanner.core.impl.domain.valuerange.AbstractUncountableValueRange;
import org.optaplanner.core.impl.heuristic.selector.value.ValueSelector;
import org.optaplanner.core.impl.solver.random.RandomUtils;

/**
 * A ValueRange is a set of a values for a {@link PlanningVariable}.
 * These values might be stored in memory as a {@link Collection} (usually a {@link List} or {@link Set}),
 * but if the values are numbers, they can also be stored in memory by their bounds
 * to use less memory and provide more opportunities.
 * <p>
 * Prefer using {@link CountableValueRange} (which extends this interface) whenever possible.
 * <p>
 * A ValueRange is stateful (unlike a {@link ValueSelector} which is stateless).
 * <p>
 * Implementations must be immutable.
 * <p>
 * An implementation must extend {@link AbstractCountableValueRange} or {@link AbstractUncountableValueRange}
 * to ensure backwards compatibility in future versions.
 *
 * @see ValueRangeFactory
 * @see CountableValueRange
 * @see AbstractCountableValueRange
 * @see AbstractUncountableValueRange
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
     *        so {@link EnvironmentMode#REPRODUCIBLE} works correctly. {@link RandomUtils} can be useful too.
     * @return never null
     */
    Iterator<T> createRandomIterator(Random workingRandom);

}
