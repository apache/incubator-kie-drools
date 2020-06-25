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

import java.util.function.Function;

import org.optaplanner.core.api.function.QuadFunction;
import org.optaplanner.core.impl.score.stream.common.JoinerType;

public final class SinglePentaJoiner<A, B, C, D, E> extends AbstractPentaJoiner<A, B, C, D, E> {

    private final QuadFunction<A, B, C, D, Object> leftMapping;
    private final JoinerType joinerType;
    private final Function<E, Object> rightMapping;

    public SinglePentaJoiner(QuadFunction<A, B, C, D, ?> leftMapping, JoinerType joinerType,
            Function<E, ?> rightMapping) {
        this.leftMapping = (QuadFunction<A, B, C, D, Object>) leftMapping;
        this.joinerType = joinerType;
        this.rightMapping = (Function<E, Object>) rightMapping;
    }

    public QuadFunction<A, B, C, D, Object> getLeftMapping() {
        return leftMapping;
    }

    public JoinerType getJoinerType() {
        return joinerType;
    }

    public Function<E, Object> getRightMapping() {
        return rightMapping;
    }

    // ************************************************************************
    // Builders
    // ************************************************************************

    @Override
    public QuadFunction<A, B, C, D, Object> getLeftMapping(int index) {
        return getLeftMapping();
    }

    @Override
    public QuadFunction<A, B, C, D, Object[]> getLeftCombinedMapping() {
        return (A a, B b, C c, D d) -> new Object[] { getLeftMapping().apply(a, b, c, d) };
    }

    @Override
    public JoinerType[] getJoinerTypes() {
        return new JoinerType[] { joinerType };
    }

    @Override
    public Function<E, Object> getRightMapping(int index) {
        return getRightMapping();
    }

    @Override
    public Function<E, Object[]> getRightCombinedMapping() {
        return (E e) -> new Object[] { getRightMapping().apply(e) };
    }
}
