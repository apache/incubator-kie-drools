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

import java.util.Map;
import java.util.NavigableMap;
import java.util.TreeMap;
import java.util.function.Function;
import java.util.function.Supplier;

import org.optaplanner.constraint.streams.bavet.common.Tuple;
import org.optaplanner.constraint.streams.common.AbstractJoiner;
import org.optaplanner.core.impl.score.stream.JoinerType;

public class IndexerFactory {

    private final JoinerType[] joinerTypes;

    public IndexerFactory(AbstractJoiner joiner) {
        int joinerCount = joiner.getJoinerCount();
        joinerTypes = new JoinerType[joinerCount];
        for (int i = 0; i < joinerCount; i++) {
            JoinerType joinerType = joiner.getJoinerType(i);
            switch (joinerType) {
                case EQUAL:
                case LESS_THAN:
                case LESS_THAN_OR_EQUAL:
                case GREATER_THAN:
                case GREATER_THAN_OR_EQUAL:
                    break;
                default:
                    throw new UnsupportedOperationException("Unsupported joiner type (" + joinerType + ").");
            }
            joinerTypes[i] = joiner.getJoinerType(i);
        }
    }

    public <Tuple_ extends Tuple, Value_> Indexer<Tuple_, Value_> buildIndexer(boolean isLeftBridge) {
        /*
         * Indexers form a parent-child hierarchy, each child has exactly one parent.
         * NoneIndexer is always at the bottom of the hierarchy, never a parent unless it is the only indexer.
         * Parent indexers delegate to their children, until they reach the ultimate NoneIndexer.
         * Example 1: EQUAL+LESS_THAN joiner will become EqualsIndexer -> ComparisonIndexer -> NoneIndexer.
         *
         * Note that if creating indexer for a right bridge node, the joiner type has to be flipped.
         * (<A, B> becomes <B, A>.)
         */
        if (joinerTypes.length == 0) { // NoneJoiner results in NoneIndexer.
            return new NoneIndexer<>();
        } else if (joinerTypes.length == 1) { // Single joiner maps directly to EqualsIndexer or ComparisonIndexer.
            JoinerType joinerType = joinerTypes[0];
            if (joinerType == JoinerType.EQUAL) {
                return new EqualsIndexer<>(s -> s.getProperty(0), NoneIndexer::new);
            } else {
                return new ComparisonIndexer<>(isLeftBridge ? joinerType : joinerType.flip(), s -> s.getProperty(0),
                        NoneIndexer::new);
            }
        }
        /*
         * For more than 1 joiner, we need to build the actual hierarchy. The following rules apply:
         *
         * Rule 1: Two or more consecutive EQUAL joiners become a single EqualsIndexer.
         * Example: EQUAL+EQUAL+LESS_THAN results in EqualsIndexer -> ComparisonIndexer.
         *
         * Rule 2: Once a joiner type in the sequence changes (from EQUALS to comparison or vice versa),
         * the joiner on the right results in an indexer that is a child to the indexer of the joiner on the left.
         * Example: EQUAL+LESS_THAN+EQUAL results in EqualsIndexer -> ComparisonIndexer -> EqualsIndexer.
         *
         * The following code builds the children first, so it needs to iterate over the joiners in reverse order.
         */
        NavigableMap<Integer, JoinerType> joinerTypeMap = new TreeMap<>();
        for (int i = 1; i <= joinerTypes.length; i++) {
            JoinerType joinerType = i < joinerTypes.length ? joinerTypes[i] : null;
            JoinerType previousJoinerType = joinerTypes[i - 1];
            if (joinerType != JoinerType.EQUAL || previousJoinerType != joinerType) {
                joinerTypeMap.put(i, previousJoinerType);
            }
        }
        NavigableMap<Integer, JoinerType> descendingJoinerTypeMap = joinerTypeMap.descendingMap();
        Supplier<Indexer<Tuple_, Value_>> downstreamIndexerSupplier = NoneIndexer::new;
        for (Map.Entry<Integer, JoinerType> entry : descendingJoinerTypeMap.entrySet()) {
            Integer endingPropertyExclusive = entry.getKey();
            Integer previousEndingPropertyExclusiveOrNull = descendingJoinerTypeMap.higherKey(endingPropertyExclusive);
            int previousEndingPropertyExclusive =
                    previousEndingPropertyExclusiveOrNull == null ? 0 : previousEndingPropertyExclusiveOrNull;
            JoinerType joinerType = entry.getValue();
            Supplier<Indexer<Tuple_, Value_>> actualDownstreamIndexerSupplier = downstreamIndexerSupplier;
            if (joinerType == JoinerType.EQUAL) {
                /*
                 * Equals indexer keys may span multiple index properties, one for each EQUALS joiner.
                 *
                 * Example 1: For an EQUAL+LESS_THAN joiner, indexer key is of length 1 and starts at position 0.
                 * Example 2: For an LESS_THAN+EQUAL+EQUAL joiner, indexer key is of length 2 and starts at position 1.
                 */
                Function<IndexProperties, Object> indexerKeyFunction =
                        indexProperties -> indexProperties.getIndexerKey(previousEndingPropertyExclusive,
                                endingPropertyExclusive);
                downstreamIndexerSupplier = () -> new EqualsIndexer<>(indexerKeyFunction, actualDownstreamIndexerSupplier);
            } else {
                JoinerType actualJoinerType = isLeftBridge ? joinerType : joinerType.flip();
                /*
                 * Comparison indexers only ever have one comparison key.
                 * Its position is the next one immediately following the preceding EQUALS position,
                 * or the first one if there are no preceding EQUALS joiners.
                 *
                 * Example 1: For an EQUAL+LESS_THAN joiner, comparison key is on position 1.
                 * Example 2: For an EQUAL+EQUAL+LESS_THAN joiner: comparison key is on position 2.
                 */
                Function<IndexProperties, Comparable> comparisonIndexPropertyFunction =
                        indexProperties -> indexProperties.getProperty(previousEndingPropertyExclusive);
                downstreamIndexerSupplier = () -> new ComparisonIndexer<>(actualJoinerType,
                        comparisonIndexPropertyFunction, actualDownstreamIndexerSupplier);
            }
        }
        return downstreamIndexerSupplier.get();
    }

}
