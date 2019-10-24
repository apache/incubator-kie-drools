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

import java.util.function.BiFunction;
import java.util.function.Function;

import org.optaplanner.core.impl.score.stream.common.JoinerType;

public final class NoneTriJoiner<A, B, C> extends AbstractTriJoiner<A, B, C> {

    private static final Object[] EMPTY_OBJECT_ARRAY = new Object[0];
    private static final JoinerType[] EMPTY_JOINER_ARRAY = new JoinerType[0];

    // ************************************************************************
    // Builders
    // ************************************************************************

    @Override
    public BiFunction<A, B, Object> getLeftMapping(int joinerId) {
        throw new UnsupportedOperationException("Impossible state: getLeftMapping() is never called on a NoneTriJoiner.");
    }

    @Override
    public BiFunction<A, B, Object[]> getLeftCombinedMapping() {
        return (A a, B b) -> EMPTY_OBJECT_ARRAY;
    }

    @Override
    public JoinerType[] getJoinerTypes() {
        return EMPTY_JOINER_ARRAY;
    }

    @Override
    public Function<C, Object> getRightMapping(int joinerId) {
        throw new UnsupportedOperationException("Impossible state: getRightMapping() is never called on a NoneTriJoiner.");
    }

    @Override
    public Function<C, Object[]> getRightCombinedMapping() {
        return (C c) -> EMPTY_OBJECT_ARRAY;
    }

}
