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

package org.optaplanner.test.impl.score.stream;

import static java.util.Objects.requireNonNull;

import java.util.Objects;
import java.util.ServiceLoader;
import java.util.function.BiFunction;

import org.optaplanner.constraint.streams.common.AbstractConstraintStreamScoreDirectorFactory;
import org.optaplanner.constraint.streams.common.AbstractConstraintStreamScoreDirectorFactoryService;
import org.optaplanner.core.api.score.Score;
import org.optaplanner.core.api.score.stream.Constraint;
import org.optaplanner.core.api.score.stream.ConstraintFactory;
import org.optaplanner.core.api.score.stream.ConstraintProvider;
import org.optaplanner.core.api.score.stream.ConstraintStreamImplType;
import org.optaplanner.core.config.util.ConfigUtils;
import org.optaplanner.core.impl.domain.solution.descriptor.SolutionDescriptor;
import org.optaplanner.core.impl.score.director.ScoreDirectorFactoryService;
import org.optaplanner.core.impl.score.director.ScoreDirectorType;
import org.optaplanner.test.api.score.stream.ConstraintVerifier;

public final class DefaultConstraintVerifier<ConstraintProvider_ extends ConstraintProvider, Solution_, Score_ extends Score<Score_>>
        implements ConstraintVerifier<ConstraintProvider_, Solution_> {

    private final ServiceLoader<ScoreDirectorFactoryService> serviceLoader;
    private final ConstraintProvider_ constraintProvider;
    private final SolutionDescriptor<Solution_> solutionDescriptor;
    private ConstraintStreamImplType constraintStreamImplType;
    private Boolean droolsAlphaNetworkCompilationEnabled;

    public DefaultConstraintVerifier(ConstraintProvider_ constraintProvider, SolutionDescriptor<Solution_> solutionDescriptor) {
        this.serviceLoader = ServiceLoader.load(ScoreDirectorFactoryService.class);
        this.constraintProvider = constraintProvider;
        this.solutionDescriptor = solutionDescriptor;
    }

    public ConstraintStreamImplType getConstraintStreamImplType() {
        return Objects.requireNonNullElse(constraintStreamImplType, ConstraintStreamImplType.DROOLS);
    }

    @Override
    public ConstraintVerifier<ConstraintProvider_, Solution_> withConstraintStreamImplType(
            ConstraintStreamImplType constraintStreamImplType) {
        requireNonNull(constraintStreamImplType);
        this.constraintStreamImplType = constraintStreamImplType;
        return this;
    }

    public boolean isDroolsAlphaNetworkCompilationEnabled() {
        return Objects.requireNonNullElse(droolsAlphaNetworkCompilationEnabled, !ConfigUtils.isNativeImage());
    }

    @Override
    public ConstraintVerifier<ConstraintProvider_, Solution_> withDroolsAlphaNetworkCompilationEnabled(
            boolean droolsAlphaNetworkCompilationEnabled) {
        this.droolsAlphaNetworkCompilationEnabled = droolsAlphaNetworkCompilationEnabled;
        return this;
    }

    // ************************************************************************
    // Verify methods
    // ************************************************************************

    @Override
    public DefaultSingleConstraintVerification<Solution_, Score_> verifyThat(
            BiFunction<ConstraintProvider_, ConstraintFactory, Constraint> constraintFunction) {
        requireNonNull(constraintFunction);
        AbstractConstraintStreamScoreDirectorFactory<Solution_, Score_> scoreDirectorFactory =
                createScoreDirectorFactory(constraintFunction);
        return new DefaultSingleConstraintVerification<>(scoreDirectorFactory);
    }

    private AbstractConstraintStreamScoreDirectorFactory<Solution_, Score_> createScoreDirectorFactory(
            BiFunction<ConstraintProvider_, ConstraintFactory, Constraint> constraintFunction) {
        ConstraintProvider actualConstraintProvider = constraintFactory -> new Constraint[] {
                constraintFunction.apply(constraintProvider, constraintFactory)
        };
        return createScoreDirectorFactory(actualConstraintProvider);
    }

    private AbstractConstraintStreamScoreDirectorFactory<Solution_, Score_> createScoreDirectorFactory(
            ConstraintProvider constraintProvider) {
        boolean isDroolsAlphaNetworkCompilationEnabled =
                droolsAlphaNetworkCompilationEnabled == null ? getConstraintStreamImplType() != ConstraintStreamImplType.BAVET
                        : droolsAlphaNetworkCompilationEnabled;
        return serviceLoader.stream()
                .map(ServiceLoader.Provider::get)
                .filter(s -> s.getSupportedScoreDirectorType() == ScoreDirectorType.CONSTRAINT_STREAMS)
                .map(s -> (AbstractConstraintStreamScoreDirectorFactoryService<Solution_, Score_>) s)
                .filter(s -> s.supportsImplType(getConstraintStreamImplType()))
                .map(s -> s.buildScoreDirectorFactory(solutionDescriptor, constraintProvider,
                        isDroolsAlphaNetworkCompilationEnabled))
                .filter(Objects::nonNull)
                .findFirst()
                .orElseThrow(() -> {
                    String expectedModule = getConstraintStreamImplType() == ConstraintStreamImplType.BAVET
                            ? "optaplanner-constraint-streams-bavet"
                            : "optaplanner-constraint-streams-drools";
                    throw new IllegalStateException(
                            "Constraint Streams implementation was not found on the classpath.\n"
                                    + "Maybe include org.optaplanner:" + expectedModule + " dependency in your project?\n"
                                    + "Maybe ensure your uberjar bundles META-INF/services from included JAR files?");
                });
    }

    @Override
    public DefaultMultiConstraintVerification<Solution_, Score_> verifyThat() {
        AbstractConstraintStreamScoreDirectorFactory<Solution_, Score_> scoreDirectorFactory =
                createScoreDirectorFactory(constraintProvider);
        return new DefaultMultiConstraintVerification<>(scoreDirectorFactory, constraintProvider);
    }

}
