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

public final class SingleQuadJoiner<A, B, C, D> extends AbstractQuadJoiner<A, B, C, D> {

    private final TriFunction<A, B, C, ?> leftMapping;
    private final JoinerType joinerType;
    private final Function<D, ?> rightMapping;

    public SingleQuadJoiner(TriFunction<A, B, C, ?> leftMapping, JoinerType joinerType, Function<D, ?> rightMapping) {
        this.leftMapping = leftMapping;
        this.joinerType = joinerType;
        this.rightMapping = rightMapping;
    }

    public TriFunction<A, B, C, ?> getLeftMapping() {
        return leftMapping;
    }

    public JoinerType getJoinerType() {
        return joinerType;
    }

    public Function<D, ?> getRightMapping() {
        return rightMapping;
    }

    // ************************************************************************
    // Builders
    // ************************************************************************

    @Override
    public TriFunction<A, B, C, Object> getLeftMapping(int index) {
        return (TriFunction<A, B, C, Object>) getLeftMapping();
    }

    @Override
    public TriFunction<A, B, C, Object[]> getLeftCombinedMapping() {
        return (A a, B b, C c) -> new Object[] { getLeftMapping().apply(a, b, c) };
    }

    @Override
    public JoinerType[] getJoinerTypes() {
        return new JoinerType[] { joinerType };
    }

    @Override
    public Function<D, Object> getRightMapping(int index) {
        return (Function<D, Object>) getRightMapping();
    }

    @Override
    public Function<D, Object[]> getRightCombinedMapping() {
        return (D d) -> new Object[] { getRightMapping().apply(d) };
    }
}
