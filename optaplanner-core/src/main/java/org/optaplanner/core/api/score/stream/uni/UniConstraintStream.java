/*
 * Copyright 2018 Red Hat, Inc. and/or its affiliates.
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

package org.optaplanner.core.api.score.stream.uni;

import java.util.function.Predicate;

import org.optaplanner.core.api.domain.constraintweight.ConstraintWeight;
import org.optaplanner.core.api.domain.entity.PlanningEntity;
import org.optaplanner.core.api.score.Score;

/**
 *
 * @param <A> the type of the matched problem fact or {@link PlanningEntity planning entity}
 */
public interface UniConstraintStream<A> {

    UniConstraintStream<A> filter(Predicate<A> predicate);

    /**
     * Negatively impact the {@link Score}: subtract the {@link ConstraintWeight}.
     */
    void penalize();

//    /**
//     * Negatively impact the {@link Score}: subtract  the {@link ConstraintWeight} multiplied by the match weight.
//     * @param matchWeighter never null, the result of this function (matchWeight) is multiplied by the constraintWeight
//     */
//    void penalize(Function<A, Long> matchWeighter);

    /**
     * Positively impact the {@link Score}: add the {@link ConstraintWeight}.
     */
    void reward();

//    /**
//     * Positively impact the {@link Score}: add the {@link ConstraintWeight} multiplied by the match weight.
//     * @param matchWeighter never null, the result of this function (matchWeight) is multiplied by the constraintWeight
//     */
//    void reward(Function<A, Long> matchWeighter);

}
