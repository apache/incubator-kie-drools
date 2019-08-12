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

import java.util.List;
import java.util.function.Function;

import org.optaplanner.core.impl.score.stream.common.JoinerType;

public final class CompositeBiJoiner<A, B> extends AbstractBiJoiner<A, B> {

    private final List<SingleBiJoiner<A, B>> joinerList;

    public CompositeBiJoiner(List<SingleBiJoiner<A, B>> joinerList) {
        this.joinerList = joinerList;
        if (joinerList.isEmpty()) {
            throw new IllegalArgumentException("The joinerList (" + joinerList + ") must not be empty.");
        }
    }

    public List<SingleBiJoiner<A, B>> getJoinerList() {
        return joinerList;
    }

    // ************************************************************************
    // Builders
    // ************************************************************************

    @Override
    public Function<A, Object[]> getLeftCombinedMapping() {
        return buildCombinedMappingUni(joinerList, SingleBiJoiner::getLeftMapping);
    }

    @Override
    public JoinerType[] getJoinerTypes() {
        return joinerList.stream()
                .map(SingleBiJoiner::getJoinerType)
                .toArray(JoinerType[]::new);
    }

    @Override
    public Function<B, Object[]> getRightCombinedMapping() {
        return buildCombinedMappingUni(joinerList, SingleBiJoiner::getRightMapping);
    }

}
