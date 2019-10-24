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

package org.optaplanner.core.impl.score.stream.tri;

import java.util.Arrays;
import java.util.List;
import java.util.function.BiFunction;
import java.util.function.Function;
import java.util.stream.IntStream;

import org.optaplanner.core.impl.score.stream.common.JoinerType;

public final class CompositeTriJoiner<A, B, C> extends AbstractTriJoiner<A, B, C> {

    private final List<SingleTriJoiner<A, B, C>> joinerList;
    private final BiFunction<A, B, ?>[] leftMappings;
    private final Function<C, ?>[] rightMappings;

    public CompositeTriJoiner(List<SingleTriJoiner<A, B, C>> joinerList) {
        if (joinerList.isEmpty()) {
            throw new IllegalArgumentException("The joinerList (" + joinerList + ") must not be empty.");
        }
        this.joinerList = joinerList;
        this.leftMappings = joinerList.stream()
                .map(SingleTriJoiner::getLeftMapping)
                .toArray(BiFunction[]::new);
        this.rightMappings = joinerList.stream()
                .map(SingleTriJoiner::getRightMapping)
                .toArray(Function[]::new);
    }

    public List<SingleTriJoiner<A, B, C>> getJoinerList() {
        return joinerList;
    }

    // ************************************************************************
    // Builders
    // ************************************************************************

    @Override
    public BiFunction<A, B, Object> getLeftMapping(int joinerId) {
        return (BiFunction<A, B, Object>) leftMappings[joinerId];
    }

    @Override
    public BiFunction<A, B, Object[]> getLeftCombinedMapping() {
        final BiFunction<A, B, Object>[] mappings = IntStream.range(0, joinerList.size())
                .mapToObj(this::getLeftMapping)
                .toArray(BiFunction[]::new);
        return (A a, B b) -> Arrays.stream(mappings)
                .map(f -> f.apply(a, b))
                .toArray();
    }

    @Override
    public JoinerType[] getJoinerTypes() {
        return joinerList.stream()
                .map(SingleTriJoiner::getJoinerType)
                .toArray(JoinerType[]::new);
    }

    @Override
    public Function<C, Object> getRightMapping(int joinerId) {
        return (Function<C, Object>) rightMappings[joinerId];
    }

    @Override
    public Function<C, Object[]> getRightCombinedMapping() {
        final Function<C, Object>[] mappings = IntStream.range(0, joinerList.size())
                .mapToObj(this::getRightMapping)
                .toArray(Function[]::new);
        return (C c) -> Arrays.stream(mappings)
                .map(f -> f.apply(c))
                .toArray();
    }

}
