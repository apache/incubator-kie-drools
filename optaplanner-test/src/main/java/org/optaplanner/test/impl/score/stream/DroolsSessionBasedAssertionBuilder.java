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

import org.kie.api.runtime.KieSession;
import org.optaplanner.core.api.domain.entity.PlanningEntity;
import org.optaplanner.core.api.domain.lookup.PlanningId;
import org.optaplanner.core.api.score.Score;
import org.optaplanner.core.api.score.stream.ConstraintProvider;
import org.optaplanner.core.config.util.ConfigUtils;
import org.optaplanner.core.impl.domain.common.accessor.MemberAccessor;
import org.optaplanner.core.impl.domain.entity.descriptor.EntityDescriptor;
import org.optaplanner.core.impl.domain.solution.descriptor.SolutionDescriptor;
import org.optaplanner.core.impl.score.director.drools.DroolsScoreDirector;
import org.optaplanner.core.impl.score.director.stream.DroolsConstraintStreamScoreDirectorFactory;
import org.optaplanner.core.impl.score.holder.AbstractScoreHolder;

final class DroolsSessionBasedAssertionBuilder<Solution_, Score_ extends Score<Score_>>
        implements SessionBasedAssertionBuilder<Solution_, Score_> {

    private final DroolsConstraintStreamScoreDirectorFactory<Solution_, Score_> constraintStreamScoreDirectorFactory;

    public DroolsSessionBasedAssertionBuilder(
            DroolsConstraintStreamScoreDirectorFactory<Solution_, Score_> constraintStreamScoreDirectorFactory) {
        this.constraintStreamScoreDirectorFactory = Objects.requireNonNull(constraintStreamScoreDirectorFactory);
    }

    @Override
    public DefaultMultiConstraintAssertion<Solution_, Score_> multiConstraintGiven(
            ConstraintProvider constraintProvider, Object... facts) {
        AbstractScoreHolder<Score_> scoreHolder = runSession(facts);
        return new DefaultMultiConstraintAssertion<>(constraintProvider, scoreHolder.extractScore(0),
                scoreHolder.getConstraintMatchTotalMap(), scoreHolder.getIndictmentMap());
    }

    @Override
    public DefaultSingleConstraintAssertion<Solution_, Score_> singleConstraintGiven(Object... facts) {
        assertDistinctPlanningIds(constraintStreamScoreDirectorFactory.getSolutionDescriptor(), facts);
        AbstractScoreHolder<Score_> scoreHolder = runSession(facts);
        return new DefaultSingleConstraintAssertion<>(constraintStreamScoreDirectorFactory, scoreHolder.extractScore(0),
                scoreHolder.getConstraintMatchTotalMap(), scoreHolder.getIndictmentMap());
    }

    private AbstractScoreHolder<Score_> runSession(Object... facts) {
        KieSession session = constraintStreamScoreDirectorFactory.newConstraintStreamingSession(true, null);
        Arrays.stream(facts).forEach(session::insert);
        session.fireAllRules();
        return (AbstractScoreHolder<Score_>) session.getGlobal(DroolsScoreDirector.GLOBAL_SCORE_HOLDER_KEY);
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
            Map<Optional<Object>, List<Object>> entitiesWithSameIdMap = clzFacts.stream()
                    .collect(Collectors.groupingBy(fact -> {
                        Object planningId = planningIdAccessor.executeGetter(fact);
                        return Optional.ofNullable(planningId); // Return as reference to prevent null keys.
                    }));
            // Eliminate those matches where there are no duplicate IDs and find the first one.
            Optional<Map.Entry<Optional<Object>, List<Object>>> firstDuplicateIdEntry = entitiesWithSameIdMap.entrySet()
                    .stream()
                    .filter(e -> e.getValue().size() > 1) // More than 1 instance means there is a duplicate ID.
                    .findFirst();
            firstDuplicateIdEntry.ifPresent(entry -> {
                String value = entry.getKey()
                        .map(Objects::toString)
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

}
