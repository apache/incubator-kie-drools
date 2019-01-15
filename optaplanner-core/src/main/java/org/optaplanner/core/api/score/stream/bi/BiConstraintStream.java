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

package org.optaplanner.core.api.score.stream.bi;

import java.util.function.BiPredicate;
import java.util.function.Function;
import java.util.function.Predicate;

import org.drools.core.beliefsystem.defeasible.Join;
import org.optaplanner.core.api.domain.constraintweight.ConstraintWeight;
import org.optaplanner.core.api.domain.entity.PlanningEntity;
import org.optaplanner.core.api.score.Score;
import org.optaplanner.core.api.score.stream.ConstraintStream;
import org.optaplanner.core.api.score.stream.uni.UniConstraintStream;

/**
 * A {@link ConstraintStream} that matches two facts.
 * @param <A> the type of the first matched fact (either a problem fact or a {@link PlanningEntity planning entity})
 * @param <B> the type of the second matched fact (either a problem fact or a {@link PlanningEntity planning entity})
 * @see ConstraintStream
 */
public interface BiConstraintStream<A, B> extends ConstraintStream {

    /**
     * Exhaustively test each tuple of facts against the {@link BiPredicate}
     * and match if {@link BiPredicate#test(Object, Object)} returns true.
     * <p>
     * Important: This is slower and less scalable than {@link UniConstraintStream#join(UniConstraintStream, BiJoiner)}
     * with a proper {@link BiJoiner} predicate (such as {@link BiJoiner#equals(Function, Function)},
     * because the latter applies hashing and/or indexing, so it doesn't create every combination just to filter it out.
     * @param predicate never null
     * @return never null
     */
    BiConstraintStream<A, B> filter(BiPredicate<A, B> predicate);

    /**
     * Negatively impact the {@link Score}: subtract the {@link ConstraintWeight} for each match.
     */
    void penalize();

    /**
     * Positively impact the {@link Score}: add the {@link ConstraintWeight} for each match.
     */
    void reward();

}
