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

package org.optaplanner.core.impl.score.stream.bavet.common.index;

import org.optaplanner.core.api.score.stream.common.JoinerType;
import org.optaplanner.core.impl.score.stream.bavet.uni.BavetJoinBridgeUniTuple;
import org.optaplanner.core.impl.score.stream.bi.AbstractBiJoiner;

public class BavetIndexFactory {

    private final JoinerType[] joinerTypes;

    public <B, A> BavetIndexFactory(AbstractBiJoiner<A, B> joiner) {
        joinerTypes = joiner.getJoinerTypes();
        if (joinerTypes.length == 0) {
            throw new IllegalArgumentException("The joiner (" + joiner + ") must not be empty.");
        }
        for (int i = 0; i < joinerTypes.length; i++) {
            if (joinerTypes[i] != JoinerType.EQUAL_TO && i != (joinerTypes.length - 1)) {
                throw new IllegalArgumentException("The joinerType (" + joinerTypes[i]
                        + ") is currently only supported as the last joinerType.\n"
                        + ((joinerTypes[i + 1] == JoinerType.EQUAL_TO)
                        ? "Maybe move the next joinerType (" + joinerTypes[i + 1]
                        + ") before this joinerType (" + joinerTypes[i] + ")."
                        : "Maybe put the next joinerType (" + joinerTypes[i + 1]
                        + ") in a filter() predicate after the join() call for now."));
            }
        }
    }

    public <A, Tuple_ extends BavetJoinBridgeUniTuple<A>> BavetIndex<A, Tuple_> buildIndex(boolean left) {
        JoinerType lastJoinerType = joinerTypes[joinerTypes.length - 1];
        if (lastJoinerType == JoinerType.EQUAL_TO) {
            return new BavetEqualsIndex<>();
        } else {
            return new BavetEqualsAndComparisonIndex<>(left ? lastJoinerType : lastJoinerType.opposite());
        }
    }

}
