package org.optaplanner.core.impl.solver;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatCode;
import static org.assertj.core.api.Assertions.fail;

import java.util.Arrays;
import java.util.Collections;
import java.util.List;
import java.util.Map;
import java.util.concurrent.CountDownLatch;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.atomic.AtomicBoolean;
import java.util.concurrent.atomic.AtomicInteger;
import java.util.concurrent.atomic.AtomicReference;
import java.util.stream.Collectors;
import java.util.stream.IntStream;

import org.assertj.core.api.SoftAssertions;
import org.assertj.core.api.junit.jupiter.SoftAssertionsExtension;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Disabled;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.Timeout;
import org.junit.jupiter.api.extension.ExtendWith;
import org.optaplanner.core.api.score.Score;
import org.optaplanner.core.api.score.ScoreManager;
import org.optaplanner.core.api.score.buildin.hardsoft.HardSoftScore;
import org.optaplanner.core.api.score.buildin.simple.SimpleScore;
import org.optaplanner.core.api.score.calculator.EasyScoreCalculator;
import org.optaplanner.core.api.score.director.ScoreDirector;
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
import org.optaplanner.core.impl.phase.custom.CustomPhaseCommand;
import org.optaplanner.core.impl.phase.custom.NoChangeCustomPhaseCommand;
import org.optaplanner.core.impl.phase.event.PhaseLifecycleListenerAdapter;
import org.optaplanner.core.impl.phase.scope.AbstractStepScope;
import org.optaplanner.core.impl.score.DummySimpleScoreEasyScoreCalculator;
import org.optaplanner.core.impl.testdata.domain.TestdataEntity;
import org.optaplanner.core.impl.testdata.domain.TestdataSolution;
import org.optaplanner.core.impl.testdata.domain.TestdataValue;
import org.optaplanner.core.impl.testdata.domain.chained.TestdataChainedAnchor;
import org.optaplanner.core.impl.testdata.domain.chained.TestdataChainedEntity;
import org.optaplanner.core.impl.testdata.domain.chained.TestdataChainedSolution;
import org.optaplanner.core.impl.testdata.domain.list.TestdataListEntity;
import org.optaplanner.core.impl.testdata.domain.list.TestdataListSolution;
import org.optaplanner.core.impl.testdata.domain.list.TestdataListValue;
import org.optaplanner.core.impl.testdata.domain.multientity.TestdataHerdEntity;
import org.optaplanner.core.impl.testdata.domain.multientity.TestdataLeadEntity;
import org.optaplanner.core.impl.testdata.domain.multientity.TestdataMultiEntitySolution;
import org.optaplanner.core.impl.testdata.domain.pinned.TestdataPinnedEntity;
import org.optaplanner.core.impl.testdata.domain.pinned.TestdataPinnedSolution;
import org.optaplanner.core.impl.testdata.domain.score.TestdataHardSoftScoreSolution;
import org.optaplanner.core.impl.testdata.util.PlannerTestUtils;
import org.optaplanner.core.impl.testutil.TestMeterRegistry;

import io.micrometer.core.instrument.Meter;
import io.micrometer.core.instrument.Metrics;
import io.micrometer.core.instrument.Tags;

@ExtendWith(SoftAssertionsExtension.class)
class DefaultSolverTest {

    @BeforeEach
    void resetGlobalRegistry() {
        Metrics.globalRegistry.clear();
    }

    @Test
    void solve() {
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
    void checkDefaultMeters() {
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
    void checkDefaultMetersTags() {
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

    // TODO: Enable with Micrometer 1.7.8 or later.
    @Disabled("https://github.com/micrometer-metrics/micrometer/issues/2947")
    @Test
    void solveMetrics() {
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
                        .isEqualTo(2L);
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

    public static class BestScoreMetricEasyScoreCalculator
            implements EasyScoreCalculator<TestdataHardSoftScoreSolution, HardSoftScore> {

        @Override
        public HardSoftScore calculateScore(TestdataHardSoftScoreSolution testdataSolution) {
            long count = testdataSolution.getEntityList()
                    .stream()
                    .filter(e -> e.getValue() != null)
                    .filter(e -> e.getValue().getCode().startsWith("reward"))
                    .count();
            return HardSoftScore.ofSoft((int) count);
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
    void solveBestScoreMetrics() {
        TestMeterRegistry meterRegistry = new TestMeterRegistry();
        Metrics.addRegistry(meterRegistry);

        SolverConfig solverConfig = PlannerTestUtils.buildSolverConfig(
                TestdataHardSoftScoreSolution.class, TestdataEntity.class);
        solverConfig.setScoreDirectorFactoryConfig(
                new ScoreDirectorFactoryConfig().withEasyScoreCalculatorClass(BestScoreMetricEasyScoreCalculator.class));
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

    private static class SetTestdataEntityValueCustomPhaseCommand implements CustomPhaseCommand<TestdataHardSoftScoreSolution> {
        final TestdataEntity entity;
        final TestdataValue value;

        public SetTestdataEntityValueCustomPhaseCommand(TestdataEntity entity, TestdataValue value) {
            this.entity = entity;
            this.value = value;
        }

        @Override
        public void changeWorkingSolution(ScoreDirector<TestdataHardSoftScoreSolution> scoreDirector) {
            TestdataEntity workingEntity = scoreDirector.lookUpWorkingObject(entity);
            TestdataValue workingValue = scoreDirector.lookUpWorkingObject(value);

            scoreDirector.beforeVariableChanged(workingEntity, "value");
            workingEntity.setValue(workingValue);
            scoreDirector.afterVariableChanged(workingEntity, "value");
            scoreDirector.triggerVariableListeners();
        }
    }

    @Test
    void solveStepScoreMetrics() {
        TestMeterRegistry meterRegistry = new TestMeterRegistry();
        Metrics.addRegistry(meterRegistry);

        SolverConfig solverConfig = PlannerTestUtils.buildSolverConfig(
                TestdataHardSoftScoreSolution.class, TestdataEntity.class);
        solverConfig.setScoreDirectorFactoryConfig(
                new ScoreDirectorFactoryConfig().withEasyScoreCalculatorClass(BestScoreMetricEasyScoreCalculator.class));
        solverConfig.setTerminationConfig(new TerminationConfig().withBestScoreLimit("0hard/3soft"));
        solverConfig.setMonitoringConfig(new MonitoringConfig()
                .withSolverMetricList(List.of(SolverMetric.STEP_SCORE)));

        TestdataHardSoftScoreSolution solution = new TestdataHardSoftScoreSolution("s1");
        TestdataEntity e1 = new TestdataEntity("e1");
        TestdataEntity e2 = new TestdataEntity("e2");
        TestdataEntity e3 = new TestdataEntity("e3");
        TestdataValue none = new TestdataValue("none");
        TestdataValue reward = new TestdataValue("reward");
        solution.setValueList(Arrays.asList(none, reward));
        solution.setEntityList(Arrays.asList(e1, e2, e3));

        solverConfig.setPhaseConfigList(List.of(
                // Force OptaPlanner to select "none" value which reward 0 soft
                new ConstructionHeuristicPhaseConfig()
                        .withConstructionHeuristicType(ConstructionHeuristicType.FIRST_FIT)
                        .withMoveSelectorConfigList(
                                List.of(new ChangeMoveSelectorConfig()
                                        .withFilterClass(NoneValueSelectionFilter.class))),
                // Then do a custom phase, to force certain steps to be taken
                new CustomPhaseConfig()
                        .withCustomPhaseCommands(
                                new SetTestdataEntityValueCustomPhaseCommand(e1, reward),
                                new SetTestdataEntityValueCustomPhaseCommand(e2, reward),
                                new SetTestdataEntityValueCustomPhaseCommand(e1, none),
                                new SetTestdataEntityValueCustomPhaseCommand(e1, reward),
                                new SetTestdataEntityValueCustomPhaseCommand(e3, reward))));
        SolverFactory<TestdataHardSoftScoreSolution> solverFactory = SolverFactory.create(solverConfig);

        Solver<TestdataHardSoftScoreSolution> solver = solverFactory.buildSolver();
        ((DefaultSolver<TestdataHardSoftScoreSolution>) solver).setMonitorTagMap(Map.of("solver.id", "solveMetrics"));
        AtomicInteger step = new AtomicInteger(-1);

        ((DefaultSolver<TestdataHardSoftScoreSolution>) solver)
                .addPhaseLifecycleListener(new PhaseLifecycleListenerAdapter<TestdataHardSoftScoreSolution>() {
                    @Override
                    public void stepEnded(AbstractStepScope<TestdataHardSoftScoreSolution> stepScope) {
                        super.stepEnded(stepScope);
                        meterRegistry.publish(solver);

                        // first 3 steps are construction heuristic steps and don't have a step score since it uninitialized
                        if (step.get() < 2) {
                            step.incrementAndGet();
                            return;
                        }

                        assertThat(
                                meterRegistry.getMeasurement(SolverMetric.STEP_SCORE.getMeterId() + ".hard.score", "VALUE")
                                        .intValue())
                                                .isEqualTo(0);

                        if (step.get() == 2) {
                            assertThat(
                                    meterRegistry.getMeasurement(SolverMetric.STEP_SCORE.getMeterId() + ".soft.score", "VALUE")
                                            .intValue())
                                                    .isEqualTo(0);
                        } else if (step.get() == 3) {
                            assertThat(
                                    meterRegistry.getMeasurement(SolverMetric.STEP_SCORE.getMeterId() + ".soft.score", "VALUE")
                                            .intValue())
                                                    .isEqualTo(1);
                        } else if (step.get() == 4) {
                            assertThat(
                                    meterRegistry.getMeasurement(SolverMetric.STEP_SCORE.getMeterId() + ".soft.score", "VALUE")
                                            .intValue())
                                                    .isEqualTo(2);
                        } else if (step.get() == 5) {
                            assertThat(
                                    meterRegistry.getMeasurement(SolverMetric.STEP_SCORE.getMeterId() + ".soft.score", "VALUE")
                                            .intValue())
                                                    .isEqualTo(1);
                        } else if (step.get() == 6) {
                            assertThat(
                                    meterRegistry.getMeasurement(SolverMetric.STEP_SCORE.getMeterId() + ".soft.score", "VALUE")
                                            .intValue())
                                                    .isEqualTo(2);
                        }
                        step.incrementAndGet();
                    }
                });
        solution = solver.solve(solution);

        assertThat(step.get()).isEqualTo(7);
        meterRegistry.publish(solver);
        assertThat(solution).isNotNull();
        assertThat(meterRegistry.getMeasurement(SolverMetric.STEP_SCORE.getMeterId() + ".hard.score", "VALUE").intValue())
                .isEqualTo(0);
        assertThat(meterRegistry.getMeasurement(SolverMetric.STEP_SCORE.getMeterId() + ".soft.score", "VALUE").intValue())
                .isEqualTo(3);
    }

    public static class ErrorThrowingEasyScoreCalculator implements EasyScoreCalculator<TestdataSolution, SimpleScore> {

        @Override
        public SimpleScore calculateScore(TestdataSolution testdataSolution) {
            throw new IllegalStateException("Thrown exception in constraint provider");
        }
    }

    @Test
    void solveMetricsError() {
        TestMeterRegistry meterRegistry = new TestMeterRegistry();
        Metrics.addRegistry(meterRegistry);

        SolverConfig solverConfig = PlannerTestUtils.buildSolverConfig(
                TestdataSolution.class, TestdataEntity.class);

        solverConfig.setScoreDirectorFactoryConfig(
                new ScoreDirectorFactoryConfig().withEasyScoreCalculatorClass(ErrorThrowingEasyScoreCalculator.class));
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
    void solveEmptyEntityList() {
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
    void solveChainedEmptyEntityList() {
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
    void solveEmptyValueList() {
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
    void solveChainedEmptyValueList() {
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
    void solveEmptyEntityListAndEmptyValueList() {
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
    void solvePinnedEntityList() {
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
    void solveStopsWhenUninitialized() {
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
    void solveStopsWhenPartiallyInitialized() {
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

    @Test
    @Timeout(60)
    void solveWithProblemChange() throws InterruptedException {
        SolverConfig solverConfig = PlannerTestUtils.buildSolverConfig(TestdataSolution.class, TestdataEntity.class);
        solverConfig.setDaemon(true); // Avoid terminating the solver too quickly.
        SolverFactory<TestdataSolution> solverFactory = SolverFactory.create(solverConfig);
        Solver<TestdataSolution> solver = solverFactory.buildSolver();
        final int valueCount = 4;
        TestdataSolution solution = TestdataSolution.generateSolution(valueCount, valueCount);

        AtomicReference<TestdataSolution> bestSolution = new AtomicReference<>();
        CountDownLatch solverStarted = new CountDownLatch(1);
        CountDownLatch solutionWithProblemChangeReceived = new CountDownLatch(1);
        solver.addEventListener(bestSolutionChangedEvent -> {
            solverStarted.countDown();
            if (bestSolutionChangedEvent.isEveryProblemChangeProcessed()) {
                TestdataSolution newBestSolution = bestSolutionChangedEvent.getNewBestSolution();
                if (newBestSolution.getValueList().size() == valueCount + 1) {
                    bestSolution.set(newBestSolution);
                    solutionWithProblemChangeReceived.countDown();
                }
            }
        });

        ExecutorService executorService = Executors.newSingleThreadExecutor();
        executorService.submit(() -> {
            solver.solve(solution);
        });

        solverStarted.await(); // Make sure we submit a ProblemChange only after the Solver started solving.
        solver.addProblemChange((workingSolution, problemChangeDirector) -> {
            problemChangeDirector.addProblemFact(new TestdataValue("added value"), solution.getValueList()::add);
        });

        solutionWithProblemChangeReceived.await();
        assertThat(bestSolution.get().getValueList()).hasSize(valueCount + 1);

        solver.terminateEarly();
        executorService.shutdown();
    }

    @Test
    void solveRepeatedlyBasicVariable(SoftAssertions softly) {
        SolverConfig solverConfig = PlannerTestUtils.buildSolverConfig(TestdataSolution.class, TestdataEntity.class);
        ConstructionHeuristicPhaseConfig phaseConfig = new ConstructionHeuristicPhaseConfig();
        // Run only 2 steps at a time, although 5 are needed to initialize all entities.
        int stepCountLimit = 2;
        phaseConfig.setTerminationConfig(new TerminationConfig().withStepCountLimit(stepCountLimit));
        solverConfig.setPhaseConfigList(Collections.singletonList(phaseConfig));
        SolverFactory<TestdataSolution> solverFactory = SolverFactory.create(solverConfig);
        Solver<TestdataSolution> solver = solverFactory.buildSolver();

        TestdataSolution solution = new TestdataSolution("s1");
        solution.setValueList(Arrays.asList(new TestdataValue("v1"), new TestdataValue("v2")));
        final int entityCount = 5;
        solution.setEntityList(IntStream.rangeClosed(1, entityCount)
                .mapToObj(id -> new TestdataEntity("e" + id))
                .collect(Collectors.toList()));

        Score<?> score = ScoreManager.create(solverFactory).updateScore(solution);
        assertThat(score.getInitScore()).isEqualTo(-entityCount);
        assertThat(score.isSolutionInitialized()).isFalse();

        // Keep restarting the solver until the solution is initialized.
        for (int initScore = -entityCount; initScore < 0; initScore += stepCountLimit) {
            softly.assertThat(solution.getScore().getInitScore()).isEqualTo(initScore);
            softly.assertThat(solution.getScore().isSolutionInitialized()).isFalse();
            solution = solver.solve(solution);
        }

        // Finally, the initScore is 0.
        softly.assertThat(solution.getScore().getInitScore()).isZero();
        softly.assertThat(solution.getScore().isSolutionInitialized()).isTrue();
    }

    @Test
    void solveRepeatedlyListVariable(SoftAssertions softly) {
        SolverConfig solverConfig = PlannerTestUtils.buildSolverConfig(
                TestdataListSolution.class, TestdataListEntity.class, TestdataListValue.class);

        // Run only 7 steps at a time, although the total number of steps needed to complete CH is equal to valueCount.
        final int stepCountLimit = 7;
        ConstructionHeuristicPhaseConfig phaseConfig = new ConstructionHeuristicPhaseConfig();
        phaseConfig.setTerminationConfig(new TerminationConfig().withStepCountLimit(stepCountLimit));
        solverConfig.setPhaseConfigList(Collections.singletonList(phaseConfig));
        SolverFactory<TestdataListSolution> solverFactory = SolverFactory.create(solverConfig);
        Solver<TestdataListSolution> solver = solverFactory.buildSolver();

        final int valueCount = 24;
        TestdataListSolution solution = TestdataListSolution.generateUninitializedSolution(valueCount, 8);

        Score<?> score = ScoreManager.create(solverFactory).updateScore(solution);
        assertThat(score.getInitScore()).isEqualTo(-valueCount);
        assertThat(score.isSolutionInitialized()).isFalse();

        // Keep restarting the solver until the solution is initialized.
        for (int initScore = -valueCount; initScore < 0; initScore += stepCountLimit) {
            softly.assertThat(solution.getScore().getInitScore()).isEqualTo(initScore);
            softly.assertThat(solution.getScore().isSolutionInitialized()).isFalse();
            solution = solver.solve(solution);
        }

        // Finally, the initScore is 0.
        softly.assertThat(solution.getScore().getInitScore()).isZero();
        softly.assertThat(solution.getScore().isSolutionInitialized()).isTrue();
    }

    @Test
    void constructionHeuristicAllocateToValueFromQueue() {
        SolverConfig solverConfig = PlannerTestUtils.buildSolverConfig(TestdataSolution.class, TestdataEntity.class);
        ConstructionHeuristicPhaseConfig phaseConfig = new ConstructionHeuristicPhaseConfig()
                .withConstructionHeuristicType(ConstructionHeuristicType.ALLOCATE_TO_VALUE_FROM_QUEUE);
        solverConfig.setPhaseConfigList(Collections.singletonList(phaseConfig));
        SolverFactory<TestdataSolution> solverFactory = SolverFactory.create(solverConfig);
        Solver<TestdataSolution> solver = solverFactory.buildSolver();

        TestdataSolution solution = new TestdataSolution("s1");
        solution.setValueList(Arrays.asList(new TestdataValue("v1"), new TestdataValue("v2")));
        solution.setEntityList(Arrays.asList(new TestdataEntity("e1")));

        solution = solver.solve(solution);
        assertThat(solution).isNotNull();
        assertThat(solution.getScore().isSolutionInitialized()).isTrue();
    }

    @Test
    void defaultSolveWithMultipleGenuinePlanningEntities() {
        SolverConfig solverConfig = new SolverConfig()
                .withSolutionClass(TestdataMultiEntitySolution.class)
                .withEntityClasses(TestdataLeadEntity.class, TestdataHerdEntity.class)
                .withEasyScoreCalculatorClass(DummySimpleScoreEasyScoreCalculator.class)
                .withTerminationConfig(new TerminationConfig()
                        .withBestScoreLimit("0"));
        SolverFactory<TestdataMultiEntitySolution> solverFactory = SolverFactory.create(solverConfig);
        Solver<TestdataMultiEntitySolution> solver = solverFactory.buildSolver();

        TestdataMultiEntitySolution solution = new TestdataMultiEntitySolution("s1");
        solution.setValueList(Arrays.asList(new TestdataValue("v1"), new TestdataValue("v2")));
        solution.setLeadEntityList(Arrays.asList(new TestdataLeadEntity("lead1"), new TestdataLeadEntity("lead2")));
        solution.setHerdEntityList(Arrays.asList(new TestdataHerdEntity("herd1"), new TestdataHerdEntity("herd2")));

        solution = solver.solve(solution);
        assertThat(solution).isNotNull();
        assertThat(solution.getScore().isSolutionInitialized()).isTrue();
    }
}
