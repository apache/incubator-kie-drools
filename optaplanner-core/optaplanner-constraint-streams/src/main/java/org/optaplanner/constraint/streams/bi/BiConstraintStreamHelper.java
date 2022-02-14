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

package org.optaplanner.constraint.streams.bi;

import org.optaplanner.constraint.streams.common.AbstractConstraintStreamHelper;
import org.optaplanner.constraint.streams.tri.DefaultTriJoiner;
import org.optaplanner.constraint.streams.tri.FilteringTriJoiner;
import org.optaplanner.core.api.function.TriPredicate;
import org.optaplanner.core.api.score.stream.bi.BiConstraintStream;
import org.optaplanner.core.api.score.stream.tri.TriConstraintStream;
import org.optaplanner.core.api.score.stream.tri.TriJoiner;
import org.optaplanner.core.api.score.stream.uni.UniConstraintStream;

public final class BiConstraintStreamHelper<A, B, C>
        extends AbstractConstraintStreamHelper<C, TriConstraintStream<A, B, C>, TriJoiner<A, B, C>, TriPredicate<A, B, C>> {

    private final BiConstraintStream<A, B> stream;

    public BiConstraintStreamHelper(BiConstraintStream<A, B> stream) {
        this.stream = stream;
    }

    @Override
    protected TriConstraintStream<A, B, C> doJoin(UniConstraintStream<C> otherStream) {
        return stream.join(otherStream);
    }

    @Override
    protected TriConstraintStream<A, B, C> doJoin(UniConstraintStream<C> otherStream, TriJoiner<A, B, C> joiner) {
        return stream.join(otherStream, joiner);
    }

    @Override
    protected TriConstraintStream<A, B, C> doJoin(UniConstraintStream<C> otherStream, TriJoiner<A, B, C>... joiners) {
        return stream.join(otherStream, joiners);
    }

    @Override
    protected TriConstraintStream<A, B, C> filter(TriConstraintStream<A, B, C> stream,
            TriPredicate<A, B, C> predicate) {
        return stream.filter(predicate);
    }

    @Override
    protected TriJoiner<A, B, C> mergeJoiners(TriJoiner<A, B, C>... joiners) {
        int joinerCount = joiners.length;
        if (joinerCount == 0) {
            return DefaultTriJoiner.NONE;
        } else if (joinerCount == 1) {
            return joiners[0];
        }
        TriJoiner<A, B, C> result = joiners[0];
        for (int i = 1; i < joinerCount; i++) {
            result = result.and(joiners[i]);
        }
        return result;
    }

    @Override
    protected boolean isFilteringJoiner(TriJoiner<A, B, C> joiner) {
        return joiner instanceof FilteringTriJoiner;
    }

    @Override
    protected TriPredicate<A, B, C> extractPredicate(TriJoiner<A, B, C> joiner) {
        return ((FilteringTriJoiner<A, B, C>) joiner).getFilter();
    }

    @Override
    protected TriPredicate<A, B, C> mergePredicates(TriPredicate<A, B, C> predicate1,
            TriPredicate<A, B, C> predicate2) {
        return predicate1.and(predicate2);
    }

}
