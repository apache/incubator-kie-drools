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

package org.optaplanner.constraint.streams.uni;

import java.util.List;
import java.util.function.BiPredicate;

import org.optaplanner.constraint.streams.bi.DefaultBiJoiner;
import org.optaplanner.constraint.streams.bi.FilteringBiJoiner;
import org.optaplanner.constraint.streams.common.AbstractConstraintStreamHelper;
import org.optaplanner.core.api.score.stream.bi.BiConstraintStream;
import org.optaplanner.core.api.score.stream.bi.BiJoiner;
import org.optaplanner.core.api.score.stream.uni.UniConstraintStream;

public final class UniConstraintStreamHelper<A, B>
        extends AbstractConstraintStreamHelper<B, BiConstraintStream<A, B>, BiJoiner<A, B>, BiPredicate<A, B>> {

    private final InnerUniConstraintStream<A> stream;

    public UniConstraintStreamHelper(InnerUniConstraintStream<A> stream) {
        this.stream = stream;
    }

    @Override
    protected BiConstraintStream<A, B> doJoin(UniConstraintStream<B> otherStream, List<BiJoiner<A, B>> joiners) {
        return stream.actuallyJoin(otherStream, joiners.toArray(new DefaultBiJoiner[0]));
    }

    @Override
    protected BiConstraintStream<A, B> filter(BiConstraintStream<A, B> stream, BiPredicate<A, B> predicate) {
        return stream.filter(predicate);
    }

    @Override
    protected boolean isFilteringJoiner(BiJoiner<A, B> joiner) {
        return joiner instanceof FilteringBiJoiner;
    }

    @Override
    public BiPredicate<A, B> extractPredicate(BiJoiner<A, B> joiner) {
        return ((FilteringBiJoiner<A, B>) joiner).getFilter();
    }

    @Override
    protected BiPredicate<A, B> mergePredicates(BiPredicate<A, B> predicate1, BiPredicate<A, B> predicate2) {
        return predicate1.and(predicate2);
    }

}
