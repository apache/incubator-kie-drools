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
import java.util.stream.Collectors;

import org.optaplanner.core.api.score.stream.bi.BiJoiner;
import org.optaplanner.core.api.score.stream.common.JoinerType;

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
        return getCombinedMapping(SingleBiJoiner::getLeftMapping);
    }

    @Override
    public JoinerType[] getJoinerTypes() {
        return joinerList.stream()
                .map(SingleBiJoiner::getJoinerType)
                .toArray(JoinerType[]::new);
    }

    @Override
    public Function<B, Object[]> getRightCombinedMapping() {
        return getCombinedMapping(SingleBiJoiner::getRightMapping);
    }

    private <T> Function<T, Object[]> getCombinedMapping(Function<SingleBiJoiner, Function<T, ?>> mappingFunction) {
        int size = joinerList.size();
        if (size == 1) {
            Function<T, ?> mapping0 = mappingFunction.apply(joinerList.get(0));
            return (T t) -> new Object[]{
                    mapping0.apply(t)
            };
        } else if (size == 2) {
            Function<T, ?> mapping0 = mappingFunction.apply(joinerList.get(0));
            Function<T, ?> mapping1 = mappingFunction.apply(joinerList.get(1));
            return (T t) -> new Object[]{
                    mapping0.apply(t),
                    mapping1.apply(t)
            };
        } else if (size == 3) {
            Function<T, ?> mapping0 = mappingFunction.apply(joinerList.get(0));
            Function<T, ?> mapping1 = mappingFunction.apply(joinerList.get(1));
            Function<T, ?> mapping2 = mappingFunction.apply(joinerList.get(2));
            return (T t) -> new Object[]{
                    mapping0.apply(t),
                    mapping1.apply(t),
                    mapping2.apply(t)
            };
        } else {
            List<? extends Function<T, ?>> mappingList = joinerList.stream()
                    .map(mappingFunction)
                    .collect(Collectors.toList());
            return (T t) -> mappingList.stream()
                    .map(mapping -> mapping.apply(t))
                    .toArray();
        }
    }

}
