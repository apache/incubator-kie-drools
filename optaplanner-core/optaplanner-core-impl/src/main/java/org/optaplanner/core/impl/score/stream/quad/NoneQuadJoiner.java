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

package org.optaplanner.core.impl.score.stream.quad;

import java.util.function.Function;

import org.optaplanner.core.api.function.TriFunction;
import org.optaplanner.core.impl.score.stream.common.JoinerType;

public final class NoneQuadJoiner<A, B, C, D> extends AbstractQuadJoiner<A, B, C, D> {

    private static final Object[] EMPTY_OBJECT_ARRAY = new Object[0];
    private static final JoinerType[] EMPTY_JOINER_ARRAY = new JoinerType[0];

    // ************************************************************************
    // Builders
    // ************************************************************************

    @Override
    public TriFunction<A, B, C, Object> getLeftMapping(int index) {
        throw new UnsupportedOperationException("Impossible state: getLeftMapping() is never called on a NoneQuadJoiner.");
    }

    @Override
    public TriFunction<A, B, C, Object[]> getLeftCombinedMapping() {
        return (A a, B b, C c) -> EMPTY_OBJECT_ARRAY;
    }

    @Override
    public JoinerType[] getJoinerTypes() {
        return EMPTY_JOINER_ARRAY;
    }

    @Override
    public Function<D, Object> getRightMapping(int index) {
        throw new UnsupportedOperationException("Impossible state: getRightMapping() is never called on a NoneQuadJoiner.");
    }

    @Override
    public Function<D, Object[]> getRightCombinedMapping() {
        return (D d) -> EMPTY_OBJECT_ARRAY;
    }

}
