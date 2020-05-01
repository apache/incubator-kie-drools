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

package org.optaplanner.core.impl.score.stream.bi;

import org.optaplanner.core.api.function.TriPredicate;
import org.optaplanner.core.api.score.stream.bi.BiConstraintStream;
import org.optaplanner.core.api.score.stream.tri.TriConstraintStream;
import org.optaplanner.core.api.score.stream.tri.TriJoiner;
import org.optaplanner.core.impl.score.stream.common.AbstractConstraintStreamHelper;
import org.optaplanner.core.impl.score.stream.tri.AbstractTriJoiner;
import org.optaplanner.core.impl.score.stream.tri.FilteringTriJoiner;

public final class BiConstraintStreamHelper<A, B, C>
        extends AbstractConstraintStreamHelper<C, TriConstraintStream<A, B, C>, TriJoiner<A, B, C>, TriPredicate<A, B, C>> {

    private final BiConstraintStream<A, B> stream;

    public BiConstraintStreamHelper(BiConstraintStream<A, B> stream) {
        this.stream = stream;
    }

    @Override
    protected TriConstraintStream<A, B, C> doJoin(Class<C> otherClass) {
        return stream.join(otherClass);
    }

    @Override
    protected TriConstraintStream<A, B, C> doJoin(Class<C> otherClass, TriJoiner<A, B, C> joiner) {
        return stream.join(otherClass, joiner);
    }

    @Override
    protected TriConstraintStream<A, B, C> doJoin(Class<C> otherClass, TriJoiner<A, B, C>... joiners) {
        return stream.join(otherClass, joiners);
    }

    @Override
    protected TriConstraintStream<A, B, C> filter(TriConstraintStream<A, B, C> stream,
            TriPredicate<A, B, C> predicate) {
        return stream.filter(predicate);
    }

    @Override
    protected TriJoiner<A, B, C> mergeJoiners(TriJoiner<A, B, C>... joiners) {
        return AbstractTriJoiner.merge(joiners);
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
