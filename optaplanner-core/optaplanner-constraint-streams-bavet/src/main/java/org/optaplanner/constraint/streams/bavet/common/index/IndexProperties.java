/*
 * Copyright 2022 Red Hat, Inc. and/or its affiliates.
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
        if (length < 1 || length > maxLength()) {
            throw new IllegalArgumentException("Impossible state: key length (" + length + ").");
        } else if (fromInclusive >= toExclusive) {
            throw new IllegalArgumentException("Impossible state: key from (" + fromInclusive + ") >= key to (" +
                    toExclusive + ").");
        }
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
