/*
 * Copyright 2020 Red Hat, Inc. and/or its affiliates.
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

package org.optaplanner.core.impl.score.stream.penta;

import java.util.Arrays;
import java.util.List;
import java.util.function.Function;

import org.optaplanner.core.api.function.QuadFunction;
import org.optaplanner.core.impl.score.stream.common.JoinerType;

public final class CompositePentaJoiner<A, B, C, D, E> extends AbstractPentaJoiner<A, B, C, D, E> {

    private final List<SinglePentaJoiner<A, B, C, D, E>> joinerList;
    private final JoinerType[] joinerTypes;
    private final QuadFunction<A, B, C, D, ?>[] leftMappings;
    private final Function<E, ?>[] rightMappings;

    CompositePentaJoiner(List<SinglePentaJoiner<A, B, C, D, E>> joinerList) {
        if (joinerList.isEmpty()) {
            throw new IllegalArgumentException("The joinerList (" + joinerList + ") must not be empty.");
        }
        this.joinerList = joinerList;
        this.joinerTypes = joinerList.stream()
                .map(SinglePentaJoiner::getJoinerType)
                .toArray(JoinerType[]::new);
        this.leftMappings = joinerList.stream()
                .map(SinglePentaJoiner::getLeftMapping)
                .toArray(QuadFunction[]::new);
        this.rightMappings = joinerList.stream()
                .map(SinglePentaJoiner::getRightMapping)
                .toArray(Function[]::new);
    }

    public List<SinglePentaJoiner<A, B, C, D, E>> getJoinerList() {
        return joinerList;
    }

    // ************************************************************************
    // Builders
    // ************************************************************************

    @Override
    public QuadFunction<A, B, C, D, Object> getLeftMapping(int index) {
        return (QuadFunction<A, B, C, D, Object>) leftMappings[index];
    }

    @Override
    public QuadFunction<A, B, C, D, Object[]> getLeftCombinedMapping() {
        return (A a, B b, C c, D d) -> Arrays.stream(leftMappings)
                .map(f -> f.apply(a, b, c, d))
                .toArray();
    }

    @Override
    public JoinerType[] getJoinerTypes() {
        return joinerTypes;
    }

    @Override
    public Function<E, Object> getRightMapping(int index) {
        return (Function<E, Object>) rightMappings[index];
    }

    @Override
    public Function<E, Object[]> getRightCombinedMapping() {
        return (E e) -> Arrays.stream(rightMappings)
                .map(f -> f.apply(e))
                .toArray();
    }

}
