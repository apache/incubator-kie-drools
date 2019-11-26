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

package org.optaplanner.core.impl.score.stream.quad;

import java.util.ArrayList;
import java.util.List;
import java.util.function.BiFunction;
import java.util.function.Function;

import org.optaplanner.core.api.function.TriFunction;
import org.optaplanner.core.api.score.stream.quad.QuadJoiner;
import org.optaplanner.core.api.score.stream.tri.TriJoiner;
import org.optaplanner.core.impl.score.stream.common.AbstractJoiner;

public abstract class AbstractQuadJoiner<A, B, C, D> extends AbstractJoiner implements QuadJoiner<A, B, C, D> {

    @SafeVarargs
    public final static <A, B, C, D> QuadJoiner<A, B, C, D> merge(QuadJoiner<A, B, C, D>... joiners) {
        List<SingleQuadJoiner<A, B, C, D>> joinerList = new ArrayList<>();
        for (QuadJoiner<A, B, C, D> joiner : joiners) {
            if (joiner instanceof NoneQuadJoiner) {
                // Ignore it
            } else if (joiner instanceof SingleQuadJoiner) {
                joinerList.add((SingleQuadJoiner<A, B, C, D>) joiner);
            } else if (joiner instanceof CompositeQuadJoiner) {
                joinerList.addAll(((CompositeQuadJoiner<A, B, C, D>) joiner).getJoinerList());
            } else {
                throw new IllegalArgumentException("The joiner class (" + joiner.getClass() + ") is not supported.");
            }
        }
        if (joinerList.isEmpty()) {
            return new NoneQuadJoiner<>();
        } else if (joinerList.size() == 1) {
            return joinerList.get(0);
        }
        return new CompositeQuadJoiner<>(joinerList);
    }

    public abstract TriFunction<A, B, C, Object> getLeftMapping(int joinerId);

    public abstract TriFunction<A, B, C, Object[]> getLeftCombinedMapping();

    public abstract Function<D, Object> getRightMapping(int joinerId);

    public abstract Function<D, Object[]> getRightCombinedMapping();

}
