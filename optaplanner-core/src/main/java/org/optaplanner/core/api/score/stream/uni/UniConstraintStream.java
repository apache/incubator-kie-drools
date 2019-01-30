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

import java.util.function.BiPredicate;
import java.util.function.Function;
import java.util.function.Predicate;

import org.optaplanner.core.api.domain.constraintweight.ConstraintWeight;
import org.optaplanner.core.api.domain.entity.PlanningEntity;
import org.optaplanner.core.api.score.Score;
import org.optaplanner.core.api.score.stream.ConstraintStream;
import org.optaplanner.core.api.score.stream.bi.BiConstraintStream;
import org.optaplanner.core.api.score.stream.bi.BiJoiner;

/**
 * A {@link ConstraintStream} that matches one fact.
 * @param <A> the type of the matched fact (either a problem fact or a {@link PlanningEntity planning entity})
 * @see ConstraintStream
 */
public interface UniConstraintStream<A> extends ConstraintStream {

    /**
     * Exhaustively test each fact against the {@link Predicate}
     * and match if {@link Predicate#test(Object)} returns true.
     * @param predicate never null
     * @return never null
     */
    UniConstraintStream<A> filter(Predicate<A> predicate);

    /**
     * Create a new {@link BiConstraintStream} for every combination of A and B for which the {@link BiJoiner}
     * is true (for the property it extracts from both facts).
     * <p>
     * Important: This is faster and more scalable than a join
     * followed by a {@link BiConstraintStream#filter(BiPredicate)},
     * because it applies hashing and/or indexing on the Property,
     * so it doesn't create nor checks every combination of A and B.
     * @param other never null
     * @param joiner never null
     * @param <B> the type of the second matched fact
     * @param <Property_> a property that is both retrievable from this stream and the other stream
     * @return a stream that matches every combination of A and B for which the {@link BiJoiner} is true
     */
    <B, Property_> BiConstraintStream<A, B> join(UniConstraintStream<B> other, BiJoiner<A, B, Property_> joiner);

    <GroupKey_, ResultContainer_, Result_> BiConstraintStream<GroupKey_, Result_> groupBy(
            Function<A, GroupKey_> groupKeyMapping,
            UniCollector<A, ResultContainer_, Result_> collector);

    /**
     * Negatively impact the {@link Score}: subtract the {@link ConstraintWeight} for each match.
     */
    void penalize();

//    /**
//     * Negatively impact the {@link Score}: subtract  the {@link ConstraintWeight} multiplied by the match weight.
//     * @param matchWeighter never null, the result of this function (matchWeight) is multiplied by the constraintWeight
//     */
//    void penalize(Function<A, Long> matchWeighter);

    /**
     * Positively impact the {@link Score}: add the {@link ConstraintWeight} for each match.
     */
    void reward();

//    /**
//     * Positively impact the {@link Score}: add the {@link ConstraintWeight} multiplied by the match weight.
//     * @param matchWeighter never null, the result of this function (matchWeight) is multiplied by the constraintWeight
//     */
//    void reward(Function<A, Long> matchWeighter);

}
