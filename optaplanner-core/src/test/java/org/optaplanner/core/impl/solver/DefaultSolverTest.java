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

package org.optaplanner.core.impl.solver;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatCode;
import static org.assertj.core.api.Assertions.fail;

import java.util.Arrays;
import java.util.Collections;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.atomic.AtomicBoolean;

import org.junit.jupiter.api.Disabled;
import org.junit.jupiter.api.Test;
import org.optaplanner.core.api.score.buildin.simple.SimpleScore;
import org.optaplanner.core.api.score.stream.Constraint;
import org.optaplanner.core.api.score.stream.ConstraintFactory;
import org.optaplanner.core.api.score.stream.ConstraintProvider;
import org.optaplanner.core.api.solver.Solver;
import org.optaplanner.core.api.solver.SolverFactory;
import org.optaplanner.core.config.constructionheuristic.ConstructionHeuristicPhaseConfig;
import org.optaplanner.core.config.phase.custom.CustomPhaseConfig;
import org.optaplanner.core.config.score.director.ScoreDirectorFactoryConfig;
import org.optaplanner.core.config.solver.SolverConfig;
import org.optaplanner.core.config.solver.termination.TerminationConfig;
import org.optaplanner.core.impl.phase.custom.NoChangeCustomPhaseCommand;
import org.optaplanner.core.impl.testdata.domain.TestdataEntity;
import org.optaplanner.core.impl.testdata.domain.TestdataSolution;
import org.optaplanner.core.impl.testdata.domain.TestdataValue;
import org.optaplanner.core.impl.testdata.domain.chained.TestdataChainedAnchor;
import org.optaplanner.core.impl.testdata.domain.chained.TestdataChainedEntity;
import org.optaplanner.core.impl.testdata.domain.chained.TestdataChainedSolution;
import org.optaplanner.core.impl.testdata.domain.pinned.TestdataPinnedEntity;
import org.optaplanner.core.impl.testdata.domain.pinned.TestdataPinnedSolution;
import org.optaplanner.core.impl.testdata.util.PlannerTestUtils;
import org.optaplanner.core.impl.util.TestMeterRegistry;

import io.micrometer.core.instrument.Metrics;

public class DefaultSolverTest {

    @Test
    public void solve() {
        SolverConfig solverConfig = PlannerTestUtils.buildSolverConfig(
                TestdataSolution.class, TestdataEntity.class);
        SolverFactory<TestdataSolution> solverFactory = SolverFactory.create(solverConfig);
        Solver<TestdataSolution> solver = solverFactory.buildSolver();

        TestdataSolution solution = new TestdataSolution("s1");
        solution.setValueList(Arrays.asList(new TestdataValue("v1"), new TestdataValue("v2")));
        solution.setEntityList(Arrays.asList(new TestdataEntity("e1"), new TestdataEntity("e2")));

        solution = solver.solve(solution);
        assertThat(solution).isNotNull();
        assertThat(solution.getScore().isSolutionInitialized()).isTrue();
    }

    @Test
    public void solveMetrics() {
        TestMeterRegistry meterRegistry = new TestMeterRegistry();
        Metrics.addRegistry(meterRegistry);

        SolverConfig solverConfig = PlannerTestUtils.buildSolverConfig(
                TestdataSolution.class, TestdataEntity.class);
        SolverFactory<TestdataSolution> solverFactory = SolverFactory.create(solverConfig);

        Solver<TestdataSolution> solver = solverFactory.buildSolver();
        meterRegistry.publish();
        assertThat(meterRegistry.getMeasurement("optaplanner.solver.errors", "COUNT")).isEqualTo("0.0");
        assertThat(meterRegistry.getMeasurement("optaplanner.solver.solve-length", "ACTIVE_TASKS")).isEqualTo("0.0");

        TestdataSolution solution = new TestdataSolution("s1");
        solution.setValueList(Arrays.asList(new TestdataValue("v1"), new TestdataValue("v2")));
        solution.setEntityList(Arrays.asList(new TestdataEntity("e1"), new TestdataEntity("e2")));

        AtomicBoolean updatedTime = new AtomicBoolean();
        solver.addEventListener(event -> {
            if (!updatedTime.get()) {
                meterRegistry.getClock().addSeconds(2);
                meterRegistry.publish();
                assertThat(meterRegistry.getMeasurement("optaplanner.solver.solve-length", "ACTIVE_TASKS")).isEqualTo("1.0");
                assertThat(meterRegistry.getMeasurement("optaplanner.solver.solve-length", "DURATION"))
                        .isEqualTo(TimeUnit.SECONDS.toNanos(2) + ".0");
                updatedTime.set(true);
            }
        });
        solution = solver.solve(solution);

        meterRegistry.publish();
        assertThat(solution).isNotNull();
        assertThat(solution.getScore().isSolutionInitialized()).isTrue();

        assertThat(meterRegistry.getMeasurement("optaplanner.solver.solve-length", "DURATION")).isEqualTo("0.0");
        assertThat(meterRegistry.getMeasurement("optaplanner.solver.solve-length", "ACTIVE_TASKS")).isEqualTo("0.0");
        assertThat(meterRegistry.getMeasurement("optaplanner.solver.errors", "COUNT")).isEqualTo("0.0");
    }

    public static class ErrorThrowingConstraintProvider implements ConstraintProvider {

        @Override
        public Constraint[] defineConstraints(ConstraintFactory constraintFactory) {
            return new Constraint[] {
                    constraintFactory.from(TestdataEntity.class)
                            .filter(e -> {
                                throw new IllegalStateException("Thrown exception in constraint provider");
                            })
                            .penalize("throwing constraint", SimpleScore.ONE)
            };
        }
    }

    @Test
    public void solveMetricsError() {
        TestMeterRegistry meterRegistry = new TestMeterRegistry();
        Metrics.addRegistry(meterRegistry);

        SolverConfig solverConfig = PlannerTestUtils.buildSolverConfig(
                TestdataSolution.class, TestdataEntity.class);

        solverConfig.setScoreDirectorFactoryConfig(
                new ScoreDirectorFactoryConfig().withConstraintProviderClass(ErrorThrowingConstraintProvider.class));
        SolverFactory<TestdataSolution> solverFactory = SolverFactory.create(solverConfig);

        Solver<TestdataSolution> solver = solverFactory.buildSolver();
        meterRegistry.publish();
        assertThat(meterRegistry.getMeasurement("optaplanner.solver.errors", "COUNT")).isEqualTo("0.0");
        assertThat(meterRegistry.getMeasurement("optaplanner.solver.solve-length", "ACTIVE_TASKS")).isEqualTo("0.0");

        TestdataSolution solution = new TestdataSolution("s1");
        solution.setValueList(Arrays.asList(new TestdataValue("v1"), new TestdataValue("v2")));
        solution.setEntityList(Arrays.asList(new TestdataEntity("e1"), new TestdataEntity("e2")));

        meterRegistry.publish();

        assertThatCode(() -> {
            solver.solve(solution);
        }).hasMessageContaining("Thrown exception in constraint provider");

        meterRegistry.getClock().addSeconds(1);
        meterRegistry.publish();
        assertThat(meterRegistry.getMeasurement("optaplanner.solver.solve-length", "ACTIVE_TASKS")).isEqualTo("0.0");
        assertThat(meterRegistry.getMeasurement("optaplanner.solver.solve-length", "DURATION")).isEqualTo("0.0");
        assertThat(meterRegistry.getMeasurement("optaplanner.solver.errors", "COUNT")).isEqualTo("1.0");
    }

    @Test
    public void solveEmptyEntityList() {
        SolverConfig solverConfig = PlannerTestUtils.buildSolverConfig(TestdataSolution.class, TestdataEntity.class)
                .withPhases(new CustomPhaseConfig().withCustomPhaseCommands(
                        scoreDirector -> fail("All phases should be skipped because there are no movable entities.")));
        SolverFactory<TestdataSolution> solverFactory = SolverFactory.create(solverConfig);
        Solver<TestdataSolution> solver = solverFactory.buildSolver();

        TestdataSolution solution = new TestdataSolution("s1");
        solution.setValueList(Arrays.asList(new TestdataValue("v1"), new TestdataValue("v2")));
        solution.setEntityList(Collections.emptyList());

        solution = solver.solve(solution);
        assertThat(solution).isNotNull();
        assertThat(solution.getScore().isSolutionInitialized()).isTrue();
    }

    @Test
    public void solveChainedEmptyEntityList() {
        SolverConfig solverConfig = PlannerTestUtils
                .buildSolverConfig(TestdataChainedSolution.class, TestdataChainedEntity.class)
                .withPhases(new CustomPhaseConfig().withCustomPhaseCommands(
                        scoreDirector -> fail("All phases should be skipped because there are no movable entities.")));
        SolverFactory<TestdataChainedSolution> solverFactory = SolverFactory.create(solverConfig);
        Solver<TestdataChainedSolution> solver = solverFactory.buildSolver();

        TestdataChainedSolution solution = new TestdataChainedSolution("s1");
        solution.setChainedAnchorList(Arrays.asList(new TestdataChainedAnchor("v1"), new TestdataChainedAnchor("v2")));
        solution.setChainedEntityList(Collections.emptyList());

        solution = solver.solve(solution);
        assertThat(solution).isNotNull();
        assertThat(solution.getScore().isSolutionInitialized()).isTrue();
    }

    // TODO https://issues.redhat.com/browse/PLANNER-1738
    @Test
    @Disabled("We currently don't support an empty value list yet if the entity list is not empty.")
    public void solveEmptyValueList() {
        SolverConfig solverConfig = PlannerTestUtils.buildSolverConfig(TestdataSolution.class, TestdataEntity.class);
        SolverFactory<TestdataSolution> solverFactory = SolverFactory.create(solverConfig);
        Solver<TestdataSolution> solver = solverFactory.buildSolver();

        TestdataSolution solution = new TestdataSolution("s1");
        solution.setValueList(Collections.emptyList());
        solution.setEntityList(Arrays.asList(new TestdataEntity("e1"), new TestdataEntity("e2")));

        solution = solver.solve(solution);
        assertThat(solution).isNotNull();
        assertThat(solution.getScore().isSolutionInitialized()).isFalse();
    }

    @Test
    @Disabled("We currently don't support an empty value list yet if the entity list is not empty.")
    public void solveChainedEmptyValueList() {
        SolverConfig solverConfig = PlannerTestUtils.buildSolverConfig(TestdataChainedSolution.class,
                TestdataChainedEntity.class);
        SolverFactory<TestdataChainedSolution> solverFactory = SolverFactory.create(solverConfig);
        Solver<TestdataChainedSolution> solver = solverFactory.buildSolver();

        TestdataChainedSolution solution = new TestdataChainedSolution("s1");
        solution.setChainedAnchorList(Collections.emptyList());
        solution.setChainedEntityList(Arrays.asList(new TestdataChainedEntity("e1"), new TestdataChainedEntity("e2")));

        solution = solver.solve(solution);
        assertThat(solution).isNotNull();
        assertThat(solution.getScore().isSolutionInitialized()).isFalse();
    }

    @Test
    public void solveEmptyEntityListAndEmptyValueList() {
        SolverConfig solverConfig = PlannerTestUtils.buildSolverConfig(TestdataSolution.class, TestdataEntity.class)
                .withPhases(new CustomPhaseConfig().withCustomPhaseCommands(
                        scoreDirector -> fail("All phases should be skipped because there are no movable entities.")));
        SolverFactory<TestdataSolution> solverFactory = SolverFactory.create(solverConfig);
        Solver<TestdataSolution> solver = solverFactory.buildSolver();

        TestdataSolution solution = new TestdataSolution("s1");
        solution.setValueList(Collections.emptyList());
        solution.setEntityList(Collections.emptyList());

        solution = solver.solve(solution);
        assertThat(solution).isNotNull();
        assertThat(solution.getScore().isSolutionInitialized()).isTrue();
    }

    @Test
    public void solvePinnedEntityList() {
        SolverConfig solverConfig = PlannerTestUtils
                .buildSolverConfig(TestdataPinnedSolution.class, TestdataPinnedEntity.class)
                .withPhases(new CustomPhaseConfig().withCustomPhaseCommands(
                        scoreDirector -> fail("All phases should be skipped because there are no movable entities.")));
        SolverFactory<TestdataPinnedSolution> solverFactory = SolverFactory.create(solverConfig);
        Solver<TestdataPinnedSolution> solver = solverFactory.buildSolver();

        TestdataPinnedSolution solution = new TestdataPinnedSolution("s1");
        solution.setValueList(Arrays.asList(new TestdataValue("v1"), new TestdataValue("v2")));
        solution.setEntityList(Arrays.asList(new TestdataPinnedEntity("e1", true, false),
                new TestdataPinnedEntity("e2", false, true)));

        solution = solver.solve(solution);
        assertThat(solution).isNotNull();
        assertThat(solution.getScore().isSolutionInitialized()).isFalse();
    }

    @Test
    public void solveStopsWhenUninitialized() {
        SolverConfig solverConfig = PlannerTestUtils.buildSolverConfig(
                TestdataSolution.class, TestdataEntity.class);
        CustomPhaseConfig phaseConfig = new CustomPhaseConfig()
                .withCustomPhaseCommandClassList(Collections.singletonList(NoChangeCustomPhaseCommand.class));
        solverConfig.setPhaseConfigList(Collections.singletonList(phaseConfig));
        SolverFactory<TestdataSolution> solverFactory = SolverFactory.create(solverConfig);
        Solver<TestdataSolution> solver = solverFactory.buildSolver();

        TestdataSolution solution = new TestdataSolution("s1");
        solution.setValueList(Arrays.asList(new TestdataValue("v1"), new TestdataValue("v2")));
        solution.setEntityList(Arrays.asList(new TestdataEntity("e1"), new TestdataEntity("e2"),
                new TestdataEntity("e3"), new TestdataEntity("e4"), new TestdataEntity("e5")));

        solution = solver.solve(solution);
        assertThat(solution).isNotNull();
        assertThat(solution.getScore().isSolutionInitialized()).isFalse();
    }

    @Test
    public void solveStopsWhenPartiallyInitialized() {
        SolverConfig solverConfig = PlannerTestUtils.buildSolverConfig(
                TestdataSolution.class, TestdataEntity.class);
        ConstructionHeuristicPhaseConfig phaseConfig = new ConstructionHeuristicPhaseConfig();
        // Run only 2 steps, although 5 are needed to initialize all entities
        phaseConfig.setTerminationConfig(new TerminationConfig().withStepCountLimit(2));
        solverConfig.setPhaseConfigList(Collections.singletonList(phaseConfig));
        SolverFactory<TestdataSolution> solverFactory = SolverFactory.create(solverConfig);
        Solver<TestdataSolution> solver = solverFactory.buildSolver();

        TestdataSolution solution = new TestdataSolution("s1");
        solution.setValueList(Arrays.asList(new TestdataValue("v1"), new TestdataValue("v2")));
        solution.setEntityList(Arrays.asList(new TestdataEntity("e1"), new TestdataEntity("e2"),
                new TestdataEntity("e3"), new TestdataEntity("e4"), new TestdataEntity("e5")));

        solution = solver.solve(solution);
        assertThat(solution).isNotNull();
        assertThat(solution.getScore().isSolutionInitialized()).isFalse();
    }

}
