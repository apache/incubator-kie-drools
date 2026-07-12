/*
 * Licensed to the Apache Software Foundation (ASF) under one
 * or more contributor license agreements.  See the NOTICE file
 * distributed with this work for additional information
 * regarding copyright ownership.  The ASF licenses this file
 * to you under the Apache License, Version 2.0 (the
 * "License"); you may not use this file except in compliance
 * with the License.  You may obtain a copy of the License at
 *
 *   http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing,
 * software distributed under the License is distributed on an
 * "AS IS" BASIS, WITHOUT WARRANTIES OR CONDITIONS OF ANY
 * KIND, either express or implied.  See the License for the
 * specific language governing permissions and limitations
 * under the License.
 */

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
