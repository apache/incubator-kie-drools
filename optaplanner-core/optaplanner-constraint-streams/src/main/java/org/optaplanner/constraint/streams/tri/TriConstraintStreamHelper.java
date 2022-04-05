/*
 * Copyright 2022 Red Hat, Inc. and/or its affiliates.
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

package org.optaplanner.constraint.streams.tri;

import java.util.List;

import org.optaplanner.constraint.streams.common.AbstractConstraintStreamHelper;
import org.optaplanner.constraint.streams.quad.DefaultQuadJoiner;
import org.optaplanner.constraint.streams.quad.FilteringQuadJoiner;
import org.optaplanner.core.api.function.QuadPredicate;
import org.optaplanner.core.api.score.stream.quad.QuadConstraintStream;
import org.optaplanner.core.api.score.stream.quad.QuadJoiner;
import org.optaplanner.core.api.score.stream.uni.UniConstraintStream;

public final class TriConstraintStreamHelper<A, B, C, D>
        extends
        AbstractConstraintStreamHelper<D, QuadConstraintStream<A, B, C, D>, QuadJoiner<A, B, C, D>, QuadPredicate<A, B, C, D>> {

    private final InnerTriConstraintStream<A, B, C> stream;

    public TriConstraintStreamHelper(InnerTriConstraintStream<A, B, C> stream) {
        this.stream = stream;
    }

    @Override
    protected QuadConstraintStream<A, B, C, D> doJoin(UniConstraintStream<D> otherStream,
            List<QuadJoiner<A, B, C, D>> joiners) {
        return stream.actuallyJoin(otherStream, joiners.toArray(new DefaultQuadJoiner[0]));
    }

    @Override
    protected QuadConstraintStream<A, B, C, D> filter(QuadConstraintStream<A, B, C, D> stream,
            QuadPredicate<A, B, C, D> predicate) {
        return stream.filter(predicate);
    }

    @Override
    protected boolean isFilteringJoiner(QuadJoiner<A, B, C, D> joiner) {
        return joiner instanceof FilteringQuadJoiner;
    }

    @Override
    protected QuadPredicate<A, B, C, D> extractPredicate(QuadJoiner<A, B, C, D> joiner) {
        return ((FilteringQuadJoiner<A, B, C, D>) joiner).getFilter();
    }

    @Override
    protected QuadPredicate<A, B, C, D> mergePredicates(QuadPredicate<A, B, C, D> predicate1,
            QuadPredicate<A, B, C, D> predicate2) {
        return predicate1.and(predicate2);
    }

}
