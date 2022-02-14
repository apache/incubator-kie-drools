/*
 * Copyright 2021 Red Hat, Inc. and/or its affiliates.
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

import org.optaplanner.constraint.streams.common.AbstractConstraintStreamHelper;
import org.optaplanner.constraint.streams.quad.DefaultQuadJoiner;
import org.optaplanner.constraint.streams.quad.FilteringQuadJoiner;
import org.optaplanner.core.api.function.QuadPredicate;
import org.optaplanner.core.api.score.stream.quad.QuadConstraintStream;
import org.optaplanner.core.api.score.stream.quad.QuadJoiner;
import org.optaplanner.core.api.score.stream.tri.TriConstraintStream;
import org.optaplanner.core.api.score.stream.uni.UniConstraintStream;

public final class TriConstraintStreamHelper<A, B, C, D>
        extends
        AbstractConstraintStreamHelper<D, QuadConstraintStream<A, B, C, D>, QuadJoiner<A, B, C, D>, QuadPredicate<A, B, C, D>> {

    private final TriConstraintStream<A, B, C> stream;

    public TriConstraintStreamHelper(TriConstraintStream<A, B, C> stream) {
        this.stream = stream;
    }

    @Override
    protected QuadConstraintStream<A, B, C, D> doJoin(UniConstraintStream<D> otherStream) {
        return stream.join(otherStream);
    }

    @Override
    protected QuadConstraintStream<A, B, C, D> doJoin(UniConstraintStream<D> otherStream, QuadJoiner<A, B, C, D> joiner) {
        return stream.join(otherStream, joiner);
    }

    @Override
    protected QuadConstraintStream<A, B, C, D> doJoin(UniConstraintStream<D> otherStream, QuadJoiner<A, B, C, D>... joiners) {
        return stream.join(otherStream, joiners);
    }

    @Override
    protected QuadConstraintStream<A, B, C, D> filter(QuadConstraintStream<A, B, C, D> stream,
            QuadPredicate<A, B, C, D> predicate) {
        return stream.filter(predicate);
    }

    @Override
    protected QuadJoiner<A, B, C, D> mergeJoiners(QuadJoiner<A, B, C, D>... joiners) {
        int joinerCount = joiners.length;
        if (joinerCount == 0) {
            return DefaultQuadJoiner.NONE;
        } else if (joinerCount == 1) {
            return joiners[0];
        }
        QuadJoiner<A, B, C, D> result = joiners[0];
        for (int i = 1; i < joinerCount; i++) {
            result = result.and(joiners[i]);
        }
        return result;
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
