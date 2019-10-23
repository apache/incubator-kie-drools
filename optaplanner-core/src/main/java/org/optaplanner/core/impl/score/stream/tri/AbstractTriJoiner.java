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

import java.util.ArrayList;
import java.util.List;
import java.util.function.BiFunction;
import java.util.function.Function;

import org.optaplanner.core.api.score.stream.tri.TriJoiner;
import org.optaplanner.core.impl.score.stream.common.AbstractJoiner;

public abstract class AbstractTriJoiner<A, B, C> extends AbstractJoiner implements TriJoiner<A, B, C> {

    @SafeVarargs
    public final static <A, B, C> TriJoiner<A, B, C> merge(TriJoiner<A, B, C>... joiners) {
        List<SingleTriJoiner<A, B, C>> joinerList = new ArrayList<>();
        for (TriJoiner<A, B, C> joiner : joiners) {
            if (joiner instanceof NoneTriJoiner) {
                // Ignore it
            } else if (joiner instanceof SingleTriJoiner) {
                joinerList.add((SingleTriJoiner<A, B, C>) joiner);
            } else if (joiner instanceof CompositeTriJoiner) {
                joinerList.addAll(((CompositeTriJoiner<A, B, C>) joiner).getJoinerList());
            } else {
                throw new IllegalArgumentException("The joiner class (" + joiner.getClass() + ") is not supported.");
            }
        }
        if (joinerList.isEmpty()) {
            return new NoneTriJoiner<>();
        } else if (joinerList.size() == 1) {
            return joinerList.get(0);
        }
        return new CompositeTriJoiner<>(joinerList);
    }

    public abstract BiFunction<A, B, Object> getLeftMapping(int joinerId);

    public abstract BiFunction<A, B, Object[]> getLeftCombinedMapping();

    public abstract Function<C, Object> getRightMapping(int joinerId);

    public abstract Function<C, Object[]> getRightCombinedMapping();

}
