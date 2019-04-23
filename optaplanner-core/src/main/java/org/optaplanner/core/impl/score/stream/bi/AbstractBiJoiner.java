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

package org.optaplanner.core.impl.score.stream.bi;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.function.Function;

import org.optaplanner.core.api.score.stream.bi.BiJoiner;
import org.optaplanner.core.api.score.stream.common.JoinerType;

public abstract class AbstractBiJoiner<A, B> implements BiJoiner<A, B> {

    public abstract Function<A, Object[]> getLeftCombinedMapping();

    public abstract JoinerType[] getJoinerTypes();

    public abstract Function<B, Object[]> getRightCombinedMapping();

    @Override
    public BiJoiner<A, B> and(BiJoiner<A, B> other) {
        List<SingleBiJoiner<A, B>> joinerList = new ArrayList<>();
        for (BiJoiner<A, B> joiner : Arrays.asList(this, other)) {
            if (joiner instanceof SingleBiJoiner) {
                joinerList.add((SingleBiJoiner<A, B>) joiner);
            } else if (joiner instanceof CompositeBiJoiner) {
                joinerList.addAll(((CompositeBiJoiner<A, B>) joiner).getJoinerList());
            } else {
                throw new IllegalArgumentException("The joiner class (" + joiner.getClass() + ") is not supported.");
            }
        }
        return new CompositeBiJoiner<>(joinerList);
    }

}
