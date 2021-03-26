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

package org.optaplanner.test.impl.score.stream;

import java.util.Arrays;
import java.util.List;
import java.util.Map;
import java.util.Objects;
import java.util.Optional;
import java.util.concurrent.ConcurrentHashMap;
import java.util.stream.Collectors;

import org.optaplanner.core.api.domain.entity.PlanningEntity;
import org.optaplanner.core.api.domain.lookup.PlanningId;
import org.optaplanner.core.api.score.Score;
import org.optaplanner.core.config.util.ConfigUtils;
import org.optaplanner.core.impl.domain.common.accessor.MemberAccessor;
import org.optaplanner.core.impl.domain.entity.descriptor.EntityDescriptor;
import org.optaplanner.core.impl.domain.solution.descriptor.SolutionDescriptor;
import org.optaplanner.core.impl.score.director.InnerScoreDirector;
import org.optaplanner.core.impl.score.director.stream.AbstractConstraintStreamScoreDirectorFactory;
import org.optaplanner.core.impl.score.stream.ConstraintSession;
import org.optaplanner.test.api.score.stream.SingleConstraintVerification;

public final class DefaultSingleConstraintVerification<Solution_, Score_ extends Score<Score_>>
        implements SingleConstraintVerification<Solution_> {

    private final AbstractConstraintStreamScoreDirectorFactory<Solution_, Score_> scoreDirectorFactory;

    protected DefaultSingleConstraintVerification(
            AbstractConstraintStreamScoreDirectorFactory<Solution_, Score_> scoreDirectorFactory) {
        this.scoreDirectorFactory = scoreDirectorFactory;
    }

    private void assertDistinctPlanningIds(SolutionDescriptor<Solution_> solutionDescriptor, Object... facts) {
        if (facts.length < 2) {
            return;
        }
        // Group all entities by their @PlanningEntity-annotated class.
        Map<Class<?>, List<Object>> entitiesByClassMap = Arrays.stream(facts)
                .filter(fact -> solutionDescriptor.hasEntityDescriptor(fact.getClass()))
                .collect(Collectors.groupingBy(fact -> {
                    EntityDescriptor<Solution_> entityDescriptor =
                            solutionDescriptor.findEntityDescriptor(fact.getClass());
                    return entityDescriptor.getEntityClass();
                }, ConcurrentHashMap::new, Collectors.toList()));
        // Go over all the top-level classes, find and report duplicate IDs.
        entitiesByClassMap.forEach((clz, clzFacts) -> {
            MemberAccessor planningIdAccessor =
                    ConfigUtils.findPlanningIdMemberAccessor(clz, solutionDescriptor.getDomainAccessType());
            if (planningIdAccessor == null) {
                return;
            }
            // Group entities by the same ID.
            Map<Optional<Long>, List<Object>> entitiesWithSameIdMap = clzFacts.stream()
                    .collect(Collectors.groupingBy(fact -> {
                        Long planningId = (Long) planningIdAccessor.executeGetter(fact);
                        return Optional.ofNullable(planningId); // Return as reference to prevent null keys.
                    }));
            // Eliminate those matches where there are no duplicate IDs and find the first one.
            Optional<Map.Entry<Optional<Long>, List<Object>>> firstDuplicateIdEntry = entitiesWithSameIdMap.entrySet()
                    .stream()
                    .filter(e -> e.getValue().size() > 1) // More than 1 instance means there is a duplicate ID.
                    .findFirst();
            firstDuplicateIdEntry.ifPresent(entry -> {
                String value = entry.getKey()
                        .map(id -> Long.toString(id))
                        .orElse("null");
                throw new IllegalStateException(
                        "Multiple instances of " + PlanningEntity.class.getSimpleName() + "-annotated class ("
                                + clz.getCanonicalName() + ") share the same " + PlanningId.class.getSimpleName()
                                + " value (" + value + ").\n" +
                                "The instances are (" + entry.getValue() + ").\n" +
                                "Make sure that IDs of entities passed into the given(...) method are unique.");
            });
        });
    }

    @Override
    public final DefaultSingleConstraintAssertion<Solution_, Score_> given(Object... facts) {
        assertDistinctPlanningIds(scoreDirectorFactory.getSolutionDescriptor(), facts);
        try (ConstraintSession<Solution_, Score_> constraintSession =
                scoreDirectorFactory.newConstraintStreamingSession(true, null)) {
            Arrays.stream(facts).forEach(constraintSession::insert);
            return new DefaultSingleConstraintAssertion<>(scoreDirectorFactory, constraintSession.calculateScore(0),
                    constraintSession.getConstraintMatchTotalMap(), constraintSession.getIndictmentMap());
        }
    }

    @Override
    public final DefaultSingleConstraintAssertion<Solution_, Score_> givenSolution(Solution_ solution) {
        try (InnerScoreDirector<Solution_, Score_> scoreDirector = scoreDirectorFactory.buildScoreDirector(true, true)) {
            scoreDirector.setWorkingSolution(Objects.requireNonNull(solution));
            return new DefaultSingleConstraintAssertion<>(scoreDirectorFactory, scoreDirector.calculateScore(),
                    scoreDirector.getConstraintMatchTotalMap(), scoreDirector.getIndictmentMap());
        }
    }

}
