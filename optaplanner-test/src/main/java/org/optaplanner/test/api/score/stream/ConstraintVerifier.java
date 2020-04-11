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

package org.optaplanner.test.api.score.stream;

import java.util.function.Function;
import java.util.stream.Stream;

import org.optaplanner.core.api.domain.entity.PlanningEntity;
import org.optaplanner.core.api.domain.solution.PlanningSolution;
import org.optaplanner.core.api.score.stream.Constraint;
import org.optaplanner.core.api.score.stream.ConstraintFactory;
import org.optaplanner.core.api.score.stream.ConstraintProvider;
import org.optaplanner.core.api.score.stream.ConstraintStreamImplType;
import org.optaplanner.core.impl.domain.solution.descriptor.SolutionDescriptor;

import static java.util.Objects.requireNonNull;

public final class ConstraintVerifier<Solution_> {

    private final SolutionDescriptor<Solution_> solutionDescriptor;
    private ConstraintStreamImplType constraintStreamImplType = ConstraintStreamImplType.DROOLS;

    private ConstraintVerifier(SolutionDescriptor<Solution_> solutionDescriptor) {
        this.solutionDescriptor = solutionDescriptor;
    }

    /**
     * Entry point to the API.
     * @param planningSolutionClass never null, {@link PlanningSolution}-annotated class associated with the constraints
     * @param firstPlanningEntityClass never null, {@link PlanningEntity} used by the {@link PlanningSolution}
     * @param otherPlanningEntityClasses optional, extra entity classes if {@link PlanningSolution} uses more than one
     * @param <Solution_> type of the {@link PlanningSolution}-annotated class
     * @return never null
     */
    public static <Solution_> ConstraintVerifier<Solution_> build(Class<Solution_> planningSolutionClass,
            Class<?> firstPlanningEntityClass, Class<?>... otherPlanningEntityClasses) {
        Class[] entityClasses = Stream.concat(Stream.of(requireNonNull(firstPlanningEntityClass)),
                Stream.of(otherPlanningEntityClasses))
                .toArray(Class[]::new);
        SolutionDescriptor<Solution_> solutionDescriptor =
                SolutionDescriptor.buildSolutionDescriptor(requireNonNull(planningSolutionClass), entityClasses);
        return new ConstraintVerifier<>(solutionDescriptor);
    }

    protected SolutionDescriptor<Solution_> getSolutionDescriptor() {
        return solutionDescriptor;
    }

    /**
     * All subsequent calls to {@link #verifyThat(Function)} and {@link #verifyThat(ConstraintProvider)}
     * will use the given {@link ConstraintStreamImplType}.
     * @param constraintStreamImplType never null
     * @return this
     */
    public ConstraintVerifier<Solution_> withConstraintStreamImplType(
            ConstraintStreamImplType constraintStreamImplType) {
        this.constraintStreamImplType = constraintStreamImplType;
        return this;
    }

    /**
     * Creates a constraint verifier for a given {@link Constraint}.
     * @param constraintFunction never null
     * @return never null
     */
    public SingleConstraintVerifier<Solution_> verifyThat(Function<ConstraintFactory, Constraint> constraintFunction) {
        return new SingleConstraintVerifier<>(this, requireNonNull(constraintFunction), constraintStreamImplType);
    }

    /**
     * Creates a constraint verifier for a given {@link ConstraintProvider}.
     * @param constraintProvider never null
     * @return never null
     */
    public ConstraintProviderVerifier<Solution_> verifyThat(ConstraintProvider constraintProvider) {
        return new ConstraintProviderVerifier<>(this, requireNonNull(constraintProvider), constraintStreamImplType);
    }
}
