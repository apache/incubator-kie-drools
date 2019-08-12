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

import org.optaplanner.core.impl.score.stream.bavet.common.BavetJoinBridgeTuple;
import org.optaplanner.core.impl.score.stream.common.AbstractJoiner;
import org.optaplanner.core.impl.score.stream.common.JoinerType;

public class BavetIndexFactory {

    private final JoinerType[] joinerTypes;

    public BavetIndexFactory(AbstractJoiner joiner) {
        joinerTypes = joiner.getJoinerTypes();
        for (int i = 0; i < joinerTypes.length; i++) {
            if (joinerTypes[i] != JoinerType.EQUAL && i != (joinerTypes.length - 1)) {
                throw new IllegalArgumentException("The joinerType (" + joinerTypes[i]
                        + ") is currently only supported as the last joinerType.\n"
                        + ((joinerTypes[i + 1] == JoinerType.EQUAL)
                        ? "Maybe move the next joinerType (" + joinerTypes[i + 1]
                        + ") before this joinerType (" + joinerTypes[i] + ")."
                        : "Maybe put the next joinerType (" + joinerTypes[i + 1]
                        + ") in a filter() predicate after the join() call for now."));
            }
        }
    }

    public <Tuple_ extends BavetJoinBridgeTuple> BavetIndex<Tuple_> buildIndex(boolean isLeftBridge) {
        if (joinerTypes.length == 0) {
            return new BavetNoneIndex<>();
        }
        JoinerType lastJoinerType = joinerTypes[joinerTypes.length - 1];
        if (lastJoinerType == JoinerType.EQUAL) {
            return new BavetEqualsIndex<>();
        } else {
            // Use flip() to model A < B as B > A
            return new BavetEqualsAndComparisonIndex<>(isLeftBridge ? lastJoinerType : lastJoinerType.flip());
        }
    }

}
