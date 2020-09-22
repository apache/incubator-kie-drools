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

package org.optaplanner.examples.tennis.optional.score;

import static org.optaplanner.core.api.score.stream.Joiners.equal;
import static org.optaplanner.core.api.score.stream.Joiners.lessThan;

import java.util.LinkedHashMap;
import java.util.Map;
import java.util.function.BiFunction;
import java.util.function.Function;

import org.apache.commons.lang3.tuple.Pair;
import org.optaplanner.core.api.score.buildin.hardmediumsoft.HardMediumSoftScore;
import org.optaplanner.core.api.score.stream.Constraint;
import org.optaplanner.core.api.score.stream.ConstraintFactory;
import org.optaplanner.core.api.score.stream.ConstraintProvider;
import org.optaplanner.core.impl.score.stream.bi.DefaultBiConstraintCollector;
import org.optaplanner.core.impl.score.stream.uni.DefaultUniConstraintCollector;
import org.optaplanner.examples.tennis.domain.TeamAssignment;
import org.optaplanner.examples.tennis.domain.UnavailabilityPenalty;

public final class TennisConstraintProvider implements ConstraintProvider {

    private static final String CONSTRAINT_PACKAGE = "org.optaplanner.examples.tennis.solver";

    private static <A> DefaultUniConstraintCollector<A, ?, LoadBalanceData> loadBalance(
            Function<A, Object> groupKey) {
        return new DefaultUniConstraintCollector<>(
                LoadBalanceData::new,
                (resultContainer, a) -> {
                    Object mapped = groupKey.apply(a);
                    return resultContainer.apply(mapped);
                },
                resultContainer -> resultContainer);
    }

    private static <A, B> DefaultBiConstraintCollector<A, B, ?, LoadBalanceData> loadBalance(
            BiFunction<A, B, Object> groupKey) {
        return new DefaultBiConstraintCollector<>(
                LoadBalanceData::new,
                (resultContainer, a, b) -> {
                    Object mapped = groupKey.apply(a, b);
                    return resultContainer.apply(mapped);
                },
                resultContainer -> resultContainer);
    }

    @Override
    public Constraint[] defineConstraints(ConstraintFactory constraintFactory) {
        return new Constraint[] {
                oneAssignmentPerDatePerTeam(constraintFactory),
                unavailabilityPenalty(constraintFactory),
                fairAssignmentCountPerTeam(constraintFactory),
                evenlyConfrontationCount(constraintFactory)
        };
    }

    protected Constraint oneAssignmentPerDatePerTeam(ConstraintFactory constraintFactory) {
        return constraintFactory.from(TeamAssignment.class)
                .join(TeamAssignment.class,
                        equal(TeamAssignment::getTeam),
                        equal(TeamAssignment::getDay),
                        lessThan(TeamAssignment::getId))
                .penalize(CONSTRAINT_PACKAGE, "oneAssignmentPerDatePerTeam", HardMediumSoftScore.ONE_HARD);
    }

    protected Constraint unavailabilityPenalty(ConstraintFactory constraintFactory) {
        return constraintFactory.from(UnavailabilityPenalty.class)
                .ifExists(TeamAssignment.class,
                        equal(UnavailabilityPenalty::getTeam, TeamAssignment::getTeam),
                        equal(UnavailabilityPenalty::getDay, TeamAssignment::getDay))
                .penalize(CONSTRAINT_PACKAGE, "unavailabilityPenalty", HardMediumSoftScore.ONE_HARD);
    }

    protected Constraint fairAssignmentCountPerTeam(ConstraintFactory constraintFactory) {
        return constraintFactory.from(TeamAssignment.class)
                .groupBy(loadBalance(TeamAssignment::getTeam))
                .penalize(CONSTRAINT_PACKAGE, "fairAssignmentCountPerTeam", HardMediumSoftScore.ONE_MEDIUM,
                        result -> (int) result.getZeroDeviationSquaredSumRootMillis());
    }

    protected Constraint evenlyConfrontationCount(ConstraintFactory constraintFactory) {
        return constraintFactory.from(TeamAssignment.class)
                .join(TeamAssignment.class,
                        equal(TeamAssignment::getDay),
                        lessThan(assignment -> assignment.getTeam().getId()))
                .groupBy(loadBalance(
                        (assignment, otherAssignment) -> Pair.of(assignment.getTeam(), otherAssignment.getTeam())))
                .penalize(CONSTRAINT_PACKAGE, "evenlyConfrontationCount", HardMediumSoftScore.ONE_SOFT,
                        result -> (int) result.getZeroDeviationSquaredSumRootMillis());
    }

    private static final class LoadBalanceData {

        private final Map<Object, Long> groupCountMap = new LinkedHashMap<>(0);
        // the sum of squared deviation from zero
        private long squaredSum = 0L;

        private Runnable apply(Object mapped) {
            long count = groupCountMap.compute(mapped,
                    (key, value) -> (value == null) ? 1L : value + 1L);
            // squaredZeroDeviation = squaredZeroDeviation - (count - 1)² + count²
            // <=> squaredZeroDeviation = squaredZeroDeviation + (2 * count - 1)
            squaredSum += (2 * count - 1);
            return () -> {
                Long computed = groupCountMap.compute(mapped,
                        (key, value) -> (value == 1L) ? null : value - 1L);
                squaredSum -= (computed == null) ? 1L : (2 * computed + 1);
            };
        }

        public long getZeroDeviationSquaredSumRootMillis() {
            return (long) (Math.sqrt((double) squaredSum) * 1_000);
        }

    }

}
