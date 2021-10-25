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
import java.util.List;
import java.util.Map;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.atomic.AtomicBoolean;
import java.util.concurrent.atomic.AtomicInteger;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Disabled;
import org.junit.jupiter.api.Test;
import org.optaplanner.core.api.score.buildin.hardsoft.HardSoftScore;
import org.optaplanner.core.api.score.buildin.simple.SimpleScore;
import org.optaplanner.core.api.score.director.ScoreDirector;
import org.optaplanner.core.api.score.stream.Constraint;
import org.optaplanner.core.api.score.stream.ConstraintFactory;
import org.optaplanner.core.api.score.stream.ConstraintProvider;
import org.optaplanner.core.api.solver.Solver;
import org.optaplanner.core.api.solver.SolverFactory;
import org.optaplanner.core.config.constructionheuristic.ConstructionHeuristicPhaseConfig;
import org.optaplanner.core.config.constructionheuristic.ConstructionHeuristicType;
import org.optaplanner.core.config.heuristic.selector.move.generic.ChangeMoveSelectorConfig;
import org.optaplanner.core.config.localsearch.LocalSearchPhaseConfig;
import org.optaplanner.core.config.localsearch.LocalSearchType;
import org.optaplanner.core.config.phase.custom.CustomPhaseConfig;
import org.optaplanner.core.config.score.director.ScoreDirectorFactoryConfig;
import org.optaplanner.core.config.solver.SolverConfig;
import org.optaplanner.core.config.solver.monitoring.MonitoringConfig;
import org.optaplanner.core.config.solver.monitoring.SolverMetric;
import org.optaplanner.core.config.solver.termination.TerminationConfig;
import org.optaplanner.core.impl.heuristic.selector.common.decorator.SelectionFilter;
import org.optaplanner.core.impl.heuristic.selector.move.generic.ChangeMove;
import org.optaplanner.core.impl.phase.custom.NoChangeCustomPhaseCommand;
import org.optaplanner.core.impl.testdata.domain.TestdataEntity;
import org.optaplanner.core.impl.testdata.domain.TestdataSolution;
import org.optaplanner.core.impl.testdata.domain.TestdataValue;
import org.optaplanner.core.impl.testdata.domain.chained.TestdataChainedAnchor;
import org.optaplanner.core.impl.testdata.domain.chained.TestdataChainedEntity;
import org.optaplanner.core.impl.testdata.domain.chained.TestdataChainedSolution;
import org.optaplanner.core.impl.testdata.domain.pinned.TestdataPinnedEntity;
import org.optaplanner.core.impl.testdata.domain.pinned.TestdataPinnedSolution;
import org.optaplanner.core.impl.testdata.domain.score.TestdataHardSoftScoreSolution;
import org.optaplanner.core.impl.testdata.util.PlannerTestUtils;
import org.optaplanner.core.impl.util.TestMeterRegistry;

import io.micrometer.core.instrument.Meter;
import io.micrometer.core.instrument.Metrics;
import io.micrometer.core.instrument.Tags;

public class DefaultSolverTest {

    @BeforeEach
    public void resetGlobalRegistry() {
        Metrics.globalRegistry.clear();
    }

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
    public void checkDefaultMeters() {
        TestMeterRegistry meterRegistry = new TestMeterRegistry();
        Metrics.addRegistry(meterRegistry);

        SolverConfig solverConfig = PlannerTestUtils.buildSolverConfig(
                TestdataSolution.class, TestdataEntity.class);
        SolverFactory<TestdataSolution> solverFactory = SolverFactory.create(solverConfig);

        DefaultSolver<TestdataSolution> solver = (DefaultSolver<TestdataSolution>) solverFactory.buildSolver();
        meterRegistry.publish(solver);
        assertThat(meterRegistry.getMeters().stream().map(Meter::getId)).isEmpty();

        TestdataSolution solution = new TestdataSolution("s1");
        solution.setValueList(Arrays.asList(new TestdataValue("v1"), new TestdataValue("v2")));
        solution.setEntityList(Arrays.asList(new TestdataEntity("e1"), new TestdataEntity("e2")));

        AtomicBoolean updatedTime = new AtomicBoolean();
        solver.addEventListener(event -> {
            if (!updatedTime.get()) {
                assertThat(meterRegistry.getMeters().stream().map(Meter::getId))
                        .containsExactlyInAnyOrder(
                                new Meter.Id(SolverMetric.SOLVE_DURATION.getMeterId(),
                                        Tags.empty(),
                                        null,
                                        null,
                                        Meter.Type.LONG_TASK_TIMER),
                                new Meter.Id(SolverMetric.ERROR_COUNT.getMeterId(),
                                        Tags.empty(),
                                        null,
                                        null,
                                        Meter.Type.COUNTER),
                                new Meter.Id(SolverMetric.SCORE_CALCULATION_COUNT.getMeterId(),
                                        Tags.empty(),
                                        null,
                                        null,
                                        Meter.Type.GAUGE));
                updatedTime.set(true);
            }
        });
        solver.solve(solution);

        // Score calculation count should be removed
        // since registering multiple gauges with the same id
        // make it return the average, and the solver holds
        // onto the solver scope, meaning it won't automatically
        // be deregistered.
        assertThat(meterRegistry.getMeters().stream().map(Meter::getId))
                .containsExactlyInAnyOrder(
                        new Meter.Id(SolverMetric.SOLVE_DURATION.getMeterId(),
                                Tags.empty(),
                                null,
                                null,
                                Meter.Type.LONG_TASK_TIMER),
                        new Meter.Id(SolverMetric.ERROR_COUNT.getMeterId(),
                                Tags.empty(),
                                null,
                                null,
                                Meter.Type.COUNTER));
    }

    @Test
    public void checkDefaultMetersTags() {
        TestMeterRegistry meterRegistry = new TestMeterRegistry();
        Metrics.addRegistry(meterRegistry);

        SolverConfig solverConfig = PlannerTestUtils.buildSolverConfig(
                TestdataSolution.class, TestdataEntity.class);
        SolverFactory<TestdataSolution> solverFactory = SolverFactory.create(solverConfig);

        DefaultSolver<TestdataSolution> solver = (DefaultSolver<TestdataSolution>) solverFactory.buildSolver();
        solver.setMonitorTagMap(Map.of("tag.key", "tag.value"));
        meterRegistry.publish(solver);
        assertThat(meterRegistry.getMeters().stream().map(Meter::getId)).isEmpty();

        TestdataSolution solution = new TestdataSolution("s1");
        solution.setValueList(Arrays.asList(new TestdataValue("v1"), new TestdataValue("v2")));
        solution.setEntityList(Arrays.asList(new TestdataEntity("e1"), new TestdataEntity("e2")));

        AtomicBoolean updatedTime = new AtomicBoolean();
        solver.addEventListener(event -> {
            if (!updatedTime.get()) {
                assertThat(meterRegistry.getMeters().stream().map(Meter::getId))
                        .containsExactlyInAnyOrder(
                                new Meter.Id(SolverMetric.SOLVE_DURATION.getMeterId(),
                                        Tags.empty(),
                                        null,
                                        null,
                                        Meter.Type.LONG_TASK_TIMER),
                                new Meter.Id(SolverMetric.ERROR_COUNT.getMeterId(),
                                        Tags.empty(),
                                        null,
                                        null,
                                        Meter.Type.COUNTER),
                                new Meter.Id(SolverMetric.SCORE_CALCULATION_COUNT.getMeterId(),
                                        Tags.of("tag.key", "tag.value"),
                                        null,
                                        null,
                                        Meter.Type.GAUGE));
                updatedTime.set(true);
            }
        });
        solver.solve(solution);

        // Score calculation count should be removed
        // since registering multiple gauges with the same id
        // make it return the average, and the solver holds
        // onto the solver scope, meaning it won't automatically
        // be deregistered.
        assertThat(meterRegistry.getMeters().stream().map(Meter::getId))
                .containsExactlyInAnyOrder(
                        new Meter.Id(SolverMetric.SOLVE_DURATION.getMeterId(),
                                Tags.empty(),
                                null,
                                null,
                                Meter.Type.LONG_TASK_TIMER),
                        new Meter.Id(SolverMetric.ERROR_COUNT.getMeterId(),
                                Tags.empty(),
                                null,
                                null,
                                Meter.Type.COUNTER));
    }

    @Test
    public void solveMetrics() {
        TestMeterRegistry meterRegistry = new TestMeterRegistry();
        Metrics.addRegistry(meterRegistry);

        SolverConfig solverConfig = PlannerTestUtils.buildSolverConfig(
                TestdataSolution.class, TestdataEntity.class);
        SolverFactory<TestdataSolution> solverFactory = SolverFactory.create(solverConfig);

        Solver<TestdataSolution> solver = solverFactory.buildSolver();
        ((DefaultSolver<TestdataSolution>) solver).setMonitorTagMap(Map.of("solver.id", "solveMetrics"));
        meterRegistry.publish(solver);

        TestdataSolution solution = new TestdataSolution("s1");
        solution.setValueList(Arrays.asList(new TestdataValue("v1"), new TestdataValue("v2")));
        solution.setEntityList(Arrays.asList(new TestdataEntity("e1"), new TestdataEntity("e2")));

        AtomicBoolean updatedTime = new AtomicBoolean();
        solver.addEventListener(event -> {
            if (!updatedTime.get()) {
                meterRegistry.getClock().addSeconds(2);
                meterRegistry.publish(solver);
                assertThat(meterRegistry.getMeasurement(SolverMetric.SOLVE_DURATION.getMeterId(), "ACTIVE_TASKS")).isOne();
                assertThat(meterRegistry.getMeasurement(SolverMetric.SOLVE_DURATION.getMeterId(), "DURATION").longValue())
                        .isEqualTo(TimeUnit.SECONDS.toNanos(2));
                updatedTime.set(true);
            }
        });
        solution = solver.solve(solution);

        meterRegistry.publish(solver);
        assertThat(solution).isNotNull();
        assertThat(solution.getScore().isSolutionInitialized()).isTrue();

        assertThat(meterRegistry.getMeasurement(SolverMetric.SOLVE_DURATION.getMeterId(), "DURATION")).isZero();
        assertThat(meterRegistry.getMeasurement(SolverMetric.SOLVE_DURATION.getMeterId(), "ACTIVE_TASKS")).isZero();
        assertThat(meterRegistry.getMeasurement(SolverMetric.ERROR_COUNT.getMeterId(), "COUNT")).isZero();
    }

    public static class BestScoreMetricConstraintProvider implements ConstraintProvider {

        @Override
        public Constraint[] defineConstraints(ConstraintFactory constraintFactory) {
            return new Constraint[] {
                    constraintFactory.forEach(TestdataEntity.class)
                            .filter(entity -> entity.getValue().getCode().startsWith("reward"))
                            .reward("rewarding value", HardSoftScore.ONE_SOFT)
            };
        }
    }

    public static class NoneValueSelectionFilter
            implements SelectionFilter<TestdataHardSoftScoreSolution, ChangeMove<TestdataHardSoftScoreSolution>> {
        @Override
        public boolean accept(ScoreDirector<TestdataHardSoftScoreSolution> scoreDirector,
                ChangeMove<TestdataHardSoftScoreSolution> selection) {
            return ((TestdataValue) (selection.getToPlanningValue())).getCode().equals("none");
        }
    }

    @Test
    public void solveBestScoreMetrics() {
        TestMeterRegistry meterRegistry = new TestMeterRegistry();
        Metrics.addRegistry(meterRegistry);

        SolverConfig solverConfig = PlannerTestUtils.buildSolverConfig(
                TestdataHardSoftScoreSolution.class, TestdataEntity.class);
        solverConfig.setScoreDirectorFactoryConfig(
                new ScoreDirectorFactoryConfig().withConstraintProviderClass(BestScoreMetricConstraintProvider.class));
        solverConfig.setTerminationConfig(new TerminationConfig().withBestScoreLimit("0hard/2soft"));
        solverConfig.setMonitoringConfig(new MonitoringConfig()
                .withSolverMetricList(List.of(SolverMetric.BEST_SCORE)));
        solverConfig.setPhaseConfigList(List.of(
                // Force OptaPlanner to select "none" value which reward 0 soft
                new ConstructionHeuristicPhaseConfig()
                        .withConstructionHeuristicType(ConstructionHeuristicType.FIRST_FIT)
                        .withMoveSelectorConfigList(
                                List.of(new ChangeMoveSelectorConfig()
                                        .withFilterClass(NoneValueSelectionFilter.class))),
                // Then do a local search, which allow OptaPlanner to select "reward" value
                // which reward 1 soft per entity
                new LocalSearchPhaseConfig()
                        .withLocalSearchType(LocalSearchType.HILL_CLIMBING)));
        SolverFactory<TestdataHardSoftScoreSolution> solverFactory = SolverFactory.create(solverConfig);

        Solver<TestdataHardSoftScoreSolution> solver = solverFactory.buildSolver();
        ((DefaultSolver<TestdataHardSoftScoreSolution>) solver).setMonitorTagMap(Map.of("solver.id", "solveMetrics"));
        meterRegistry.publish(solver);
        TestdataHardSoftScoreSolution solution = new TestdataHardSoftScoreSolution("s1");
        solution.setValueList(Arrays.asList(new TestdataValue("none"), new TestdataValue("reward")));
        solution.setEntityList(Arrays.asList(new TestdataEntity("e1"), new TestdataEntity("e2")));
        AtomicInteger step = new AtomicInteger(-1);

        solver.addEventListener(event -> {
            meterRegistry.publish(solver);
            System.out.println(event.getNewBestScore());

            // This event listener is added before the best score event listener
            // so it is one step behind
            if (step.get() != -1) {
                assertThat(
                        meterRegistry.getMeasurement(SolverMetric.BEST_SCORE.getMeterId() + ".hard.score", "VALUE").intValue())
                                .isEqualTo(0);
            }
            if (step.get() == 0) {
                assertThat(
                        meterRegistry.getMeasurement(SolverMetric.BEST_SCORE.getMeterId() + ".soft.score", "VALUE").intValue())
                                .isEqualTo(0);
            } else if (step.get() == 1) {
                assertThat(
                        meterRegistry.getMeasurement(SolverMetric.BEST_SCORE.getMeterId() + ".soft.score", "VALUE").intValue())
                                .isEqualTo(1);
            } else if (step.get() == 2) {
                assertThat(
                        meterRegistry.getMeasurement(SolverMetric.BEST_SCORE.getMeterId() + ".soft.score", "VALUE").intValue())
                                .isEqualTo(2);
            }
            step.incrementAndGet();
        });
        solution = solver.solve(solution);

        assertThat(step.get()).isEqualTo(2);
        meterRegistry.publish(solver);
        assertThat(solution).isNotNull();
        assertThat(meterRegistry.getMeasurement(SolverMetric.BEST_SCORE.getMeterId() + ".hard.score", "VALUE").intValue())
                .isEqualTo(0);
        assertThat(meterRegistry.getMeasurement(SolverMetric.BEST_SCORE.getMeterId() + ".soft.score", "VALUE").intValue())
                .isEqualTo(2);
    }

    public static class ErrorThrowingConstraintProvider implements ConstraintProvider {

        @Override
        public Constraint[] defineConstraints(ConstraintFactory constraintFactory) {
            return new Constraint[] {
                    constraintFactory.forEach(TestdataEntity.class)
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
        ((DefaultSolver<TestdataSolution>) solver).setMonitorTagMap(Map.of("solver.id", "solveMetricsError"));
        meterRegistry.publish(solver);

        TestdataSolution solution = new TestdataSolution("s1");
        solution.setValueList(Arrays.asList(new TestdataValue("v1"), new TestdataValue("v2")));
        solution.setEntityList(Arrays.asList(new TestdataEntity("e1"), new TestdataEntity("e2")));

        meterRegistry.publish(solver);

        assertThatCode(() -> {
            solver.solve(solution);
        }).hasStackTraceContaining("Thrown exception in constraint provider");

        meterRegistry.getClock().addSeconds(1);
        meterRegistry.publish(solver);
        assertThat(meterRegistry.getMeasurement(SolverMetric.SOLVE_DURATION.getMeterId(), "ACTIVE_TASKS")).isZero();
        assertThat(meterRegistry.getMeasurement(SolverMetric.SOLVE_DURATION.getMeterId(), "DURATION")).isZero();
        assertThat(meterRegistry.getMeasurement(SolverMetric.ERROR_COUNT.getMeterId(), "COUNT")).isOne();
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
