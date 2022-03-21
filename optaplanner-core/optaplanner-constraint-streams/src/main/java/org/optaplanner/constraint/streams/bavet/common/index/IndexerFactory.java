/*
 * Copyright 2019 Red Hat, Inc. and/or its affiliates.
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
            if (joinerType != JoinerType.EQUAL && i != (joinerCount - 1)) {
                JoinerType nextJoinerType = joiner.getJoinerType(i + 1);
                throw new IllegalArgumentException("The joinerType (" + joinerType
                        + ") is currently only supported as the last joinerType.\n"
                        + ((nextJoinerType == JoinerType.EQUAL)
                                ? "Maybe move the next joinerType (" + nextJoinerType
                                        + ") before this joinerType (" + joinerType + ")."
                                : "Maybe put the next joinerType (" + nextJoinerType
                                        + ") in a filter() predicate after the join() call for now."));
            }
            joinerTypes[i] = joiner.getJoinerType(i);
        }
    }

    public <Tuple_ extends Tuple, Value_> Indexer<Tuple_, Value_> buildIndexer(boolean isLeftBridge) {
        if (joinerTypes.length == 0) {
            return new NoneIndexer<>();
        }
        JoinerType lastJoinerType = joinerTypes[joinerTypes.length - 1];
        if (lastJoinerType == JoinerType.EQUAL) {
            return new EqualsIndexer<>();
        } else {
            // Use flip() to model A < B as B > A
            return new EqualsAndComparisonIndexer<>(isLeftBridge ? lastJoinerType : lastJoinerType.flip());
        }
    }

}
