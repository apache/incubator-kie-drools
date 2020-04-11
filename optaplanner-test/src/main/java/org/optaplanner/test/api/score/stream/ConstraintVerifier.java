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

import java.util.function.BiFunction;

import org.optaplanner.core.api.domain.entity.PlanningEntity;
import org.optaplanner.core.api.domain.solution.PlanningSolution;
import org.optaplanner.core.api.score.stream.Constraint;
import org.optaplanner.core.api.score.stream.ConstraintFactory;
import org.optaplanner.core.api.score.stream.ConstraintProvider;
import org.optaplanner.core.api.score.stream.ConstraintStreamImplType;
import org.optaplanner.core.impl.domain.solution.descriptor.SolutionDescriptor;

import static java.util.Objects.requireNonNull;

public final class ConstraintVerifier<ConstraintProvider_ extends ConstraintProvider, Solution_> {

    private final ConstraintProvider_ constraintProvider;
    private final SolutionDescriptor<Solution_> solutionDescriptor;
    private ConstraintStreamImplType constraintStreamImplType = ConstraintStreamImplType.DROOLS;

    private ConstraintVerifier(ConstraintProvider_ constraintProvider, SolutionDescriptor<Solution_> solutionDescriptor) {
        this.constraintProvider = constraintProvider;
        this.solutionDescriptor = solutionDescriptor;
    }

    /**
     * Entry point to the API.
     * @param constraintProvider never null, {@link PlanningEntity} used by the {@link PlanningSolution}
     * @param planningSolutionClass never null, {@link PlanningSolution}-annotated class associated with the constraints
     * @param entityClasses never null, at least one, {@link PlanningEntity} types used by the {@link PlanningSolution}
     * @param <ConstraintProvider_> type of the {@link ConstraintProvider}
     * @param <Solution_> type of the {@link PlanningSolution}-annotated class
     * @return never null
     */
    public static <ConstraintProvider_ extends ConstraintProvider, Solution_> ConstraintVerifier<ConstraintProvider_, Solution_> build(
            ConstraintProvider_ constraintProvider,
            Class<Solution_> planningSolutionClass, Class<?>... entityClasses) {
        requireNonNull(constraintProvider);
        SolutionDescriptor<Solution_> solutionDescriptor =
                SolutionDescriptor.buildSolutionDescriptor(requireNonNull(planningSolutionClass), entityClasses);
        return new ConstraintVerifier<>(constraintProvider, solutionDescriptor);
    }

    protected SolutionDescriptor<Solution_> getSolutionDescriptor() {
        return solutionDescriptor;
    }

    /**
     * All subsequent calls to {@link #verifyThat(BiFunction)} and {@link #verifyThat()}
     * will use the given {@link ConstraintStreamImplType}.
     * @param constraintStreamImplType never null
     * @return this
     */
    public ConstraintVerifier<ConstraintProvider_, Solution_> withConstraintStreamImplType(
            ConstraintStreamImplType constraintStreamImplType) {
        this.constraintStreamImplType = constraintStreamImplType;
        return this;
    }

    /**
     * Creates a constraint verifier for a given {@link Constraint} of the {@link ConstraintProvider}.
     * @param constraintFunction never null
     * @return never null
     */
    public SingleConstraintVerifier<Solution_> verifyThat(
            BiFunction<ConstraintProvider_, ConstraintFactory, Constraint> constraintFunction) {
        requireNonNull(constraintFunction);
        return new SingleConstraintVerifier<>(this,
                (constraintFactory) -> constraintFunction.apply(constraintProvider, constraintFactory),
                constraintStreamImplType);
    }

    /**
     * Creates a constraint verifier for all constraints of the {@link ConstraintProvider}.
     * @return never null
     */
    public ConstraintProviderVerifier<Solution_> verifyThat() {
        return new ConstraintProviderVerifier<>(this, constraintProvider, constraintStreamImplType);
    }

}
