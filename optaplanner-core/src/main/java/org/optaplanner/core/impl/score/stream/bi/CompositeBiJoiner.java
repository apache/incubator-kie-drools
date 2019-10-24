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

import java.util.Arrays;
import java.util.List;
import java.util.function.Function;
import java.util.stream.IntStream;

import org.optaplanner.core.impl.score.stream.common.JoinerType;

public final class CompositeBiJoiner<A, B> extends AbstractBiJoiner<A, B> {

    private final List<SingleBiJoiner<A, B>> joinerList;
    private final Function<A, ?>[] leftMappings;
    private final Function<B, ?>[] rightMappings;

    public CompositeBiJoiner(List<SingleBiJoiner<A, B>> joinerList) {
        if (joinerList.isEmpty()) {
            throw new IllegalArgumentException("The joinerList (" + joinerList + ") must not be empty.");
        }
        this.joinerList = joinerList;
        this.leftMappings = joinerList.stream()
                .map(SingleBiJoiner::getLeftMapping)
                .toArray(Function[]::new);
        this.rightMappings = joinerList.stream()
                .map(SingleBiJoiner::getRightMapping)
                .toArray(Function[]::new);
    }

    public List<SingleBiJoiner<A, B>> getJoinerList() {
        return joinerList;
    }

    // ************************************************************************
    // Builders
    // ************************************************************************

    @Override
    public Function<A, Object> getLeftMapping(int joinerId) {
        return (Function<A, Object>) leftMappings[joinerId];
    }

    @Override
    public Function<A, Object[]> getLeftCombinedMapping() {
        final Function<A, Object>[] mappings = IntStream.range(0, joinerList.size())
                .mapToObj(this::getLeftMapping)
                .toArray(Function[]::new);
        return (A a) -> Arrays.stream(mappings)
                .map(f -> f.apply(a))
                .toArray();
    }

    @Override
    public JoinerType[] getJoinerTypes() {
        return joinerList.stream()
                .map(SingleBiJoiner::getJoinerType)
                .toArray(JoinerType[]::new);
    }

    @Override
    public Function<B, Object> getRightMapping(int joinerId) {
        return (Function<B, Object>) rightMappings[joinerId];
    }

    @Override
    public Function<B, Object[]> getRightCombinedMapping() {
        final Function<B, Object>[] mappings = IntStream.range(0, joinerList.size())
                .mapToObj(this::getRightMapping)
                .toArray(Function[]::new);
        return (B b) -> Arrays.stream(mappings)
                .map(f -> f.apply(b))
                .toArray();
    }
}
