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

package org.optaplanner.core.api.solver;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.assertj.core.api.Assertions.fail;
import static org.optaplanner.core.api.solver.SolverStatus.NOT_SOLVING;
import static org.optaplanner.core.api.solver.SolverStatus.SOLVING_ACTIVE;
import static org.optaplanner.core.api.solver.SolverStatus.SOLVING_SCHEDULED;
import static org.optaplanner.core.impl.testdata.util.PlannerAssert.assertSolutionInitialized;

import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.concurrent.BrokenBarrierException;
import java.util.concurrent.CountDownLatch;
import java.util.concurrent.CyclicBarrier;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.atomic.AtomicInteger;
import java.util.function.BiConsumer;
import java.util.function.Consumer;
import java.util.function.Function;
import java.util.stream.Collectors;
import java.util.stream.IntStream;

import org.junit.jupiter.api.Disabled;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.Timeout;
import org.optaplanner.core.api.score.director.ScoreDirector;
import org.optaplanner.core.config.constructionheuristic.ConstructionHeuristicPhaseConfig;
import org.optaplanner.core.config.localsearch.LocalSearchPhaseConfig;
import org.optaplanner.core.config.phase.PhaseConfig;
import org.optaplanner.core.config.phase.custom.CustomPhaseConfig;
import org.optaplanner.core.config.solver.SolverConfig;
import org.optaplanner.core.config.solver.SolverManagerConfig;
import org.optaplanner.core.config.solver.termination.TerminationConfig;
import org.optaplanner.core.impl.testdata.domain.TestdataEntity;
import org.optaplanner.core.impl.testdata.domain.TestdataSolution;
import org.optaplanner.core.impl.testdata.domain.extended.TestdataUnannotatedExtendedSolution;
import org.optaplanner.core.impl.testdata.util.PlannerTestUtils;

public class SolverManagerTest {

    @Test
    @Timeout(60)
    public void solveBatch_2InParallel() throws ExecutionException, InterruptedException {
        SolverConfig solverConfig = PlannerTestUtils.buildSolverConfig(TestdataSolution.class, TestdataEntity.class)
                .withPhases(createPhaseWithConcurrentSolvingStart(2), new ConstructionHeuristicPhaseConfig());
        SolverManager<TestdataSolution, Long> solverManager = SolverManager.create(
                solverConfig, new SolverManagerConfig().withParallelSolverCount("2"));

        SolverJob<TestdataSolution, Long> solverJob1 = solverManager.solve(1L,
                PlannerTestUtils.generateTestdataSolution("s1"));
        SolverJob<TestdataSolution, Long> solverJob2 = solverManager.solve(2L,
                PlannerTestUtils.generateTestdataSolution("s2"));

        assertSolutionInitialized(solverJob1.getFinalBestSolution());
        assertSolutionInitialized(solverJob2.getFinalBestSolution());
    }

    private CustomPhaseConfig createPhaseWithConcurrentSolvingStart(int barrierPartiesCount) {
        CyclicBarrier barrier = new CyclicBarrier(barrierPartiesCount);
        return new CustomPhaseConfig().withCustomPhaseCommands(
                scoreDirector -> {
                    try {
                        barrier.await();
                    } catch (InterruptedException | BrokenBarrierException e) {
                        fail("Cyclic barrier failed.");
                    }
                });
    }

    @Test
    @Timeout(60)
    public void getSolverStatus() throws InterruptedException, BrokenBarrierException, ExecutionException {
        CyclicBarrier solverThreadReadyBarrier = new CyclicBarrier(2);
        CyclicBarrier mainThreadReadyBarrier = new CyclicBarrier(2);
        SolverConfig solverConfig = PlannerTestUtils.buildSolverConfig(TestdataSolution.class, TestdataEntity.class)
                .withPhases(new CustomPhaseConfig().withCustomPhaseCommands(
                        scoreDirector -> {
                            try {
                                solverThreadReadyBarrier.await();
                            } catch (InterruptedException | BrokenBarrierException e) {
                                fail("Cyclic barrier failed.");
                            }
                            try {
                                mainThreadReadyBarrier.await();
                            } catch (InterruptedException | BrokenBarrierException e) {
                                fail("Cyclic barrier failed.");
                            }
                        }), new ConstructionHeuristicPhaseConfig());
        // Only 1 solver can run at the same time to predict the solver status of each job.
        SolverManager<TestdataSolution, Long> solverManager = SolverManager.create(
                solverConfig, new SolverManagerConfig().withParallelSolverCount("1"));

        SolverJob<TestdataSolution, Long> solverJob1 = solverManager.solve(1L,
                PlannerTestUtils.generateTestdataSolution("s1"));
        solverThreadReadyBarrier.await();
        SolverJob<TestdataSolution, Long> solverJob2 = solverManager.solve(2L,
                PlannerTestUtils.generateTestdataSolution("s2"));
        assertThat(solverManager.getSolverStatus(1L)).isEqualTo(SOLVING_ACTIVE);
        assertThat(solverJob1.getSolverStatus()).isEqualTo(SOLVING_ACTIVE);
        assertThat(solverManager.getSolverStatus(2L)).isEqualTo(SOLVING_SCHEDULED);
        assertThat(solverJob2.getSolverStatus()).isEqualTo(SOLVING_SCHEDULED);
        mainThreadReadyBarrier.await();
        solverThreadReadyBarrier.await();
        assertThat(solverManager.getSolverStatus(1L)).isEqualTo(NOT_SOLVING);
        assertThat(solverJob1.getSolverStatus()).isEqualTo(NOT_SOLVING);
        assertThat(solverManager.getSolverStatus(2L)).isEqualTo(SOLVING_ACTIVE);
        assertThat(solverJob2.getSolverStatus()).isEqualTo(SOLVING_ACTIVE);
        mainThreadReadyBarrier.await();
        solverJob1.getFinalBestSolution();
        solverJob2.getFinalBestSolution();
        assertThat(solverManager.getSolverStatus(1L)).isEqualTo(NOT_SOLVING);
        assertThat(solverJob1.getSolverStatus()).isEqualTo(NOT_SOLVING);
        assertThat(solverManager.getSolverStatus(2L)).isEqualTo(NOT_SOLVING);
        assertThat(solverJob2.getSolverStatus()).isEqualTo(NOT_SOLVING);
    }

    @Test
    @Timeout(60)
    public void exceptionInSolver() {
        SolverConfig solverConfig = PlannerTestUtils.buildSolverConfig(TestdataSolution.class, TestdataEntity.class)
                .withPhases(new CustomPhaseConfig().withCustomPhaseCommands(
                        scoreDirector -> {
                            throw new IllegalStateException("exceptionInSolver");
                        }));
        SolverManager<TestdataSolution, Long> solverManager = SolverManager.create(
                solverConfig, new SolverManagerConfig().withParallelSolverCount("1"));

        AtomicInteger exceptionCount = new AtomicInteger();
        SolverJob<TestdataSolution, Long> solverJob1 = solverManager.solve(1L,
                problemId -> PlannerTestUtils.generateTestdataSolution("s1"),
                null, (problemId, throwable) -> exceptionCount.incrementAndGet());
        assertThatThrownBy(solverJob1::getFinalBestSolution)
                .isInstanceOf(ExecutionException.class)
                .hasRootCauseMessage("exceptionInSolver");
        assertThat(exceptionCount.get()).isEqualTo(1);
        assertThat(solverManager.getSolverStatus(1L)).isEqualTo(NOT_SOLVING);
        assertThat(solverJob1.getSolverStatus()).isEqualTo(NOT_SOLVING);
    }

    @Test
    @Timeout(60)
    public void exceptionInConsumer() {
        SolverConfig solverConfig = PlannerTestUtils.buildSolverConfig(TestdataSolution.class, TestdataEntity.class)
                .withPhases(new ConstructionHeuristicPhaseConfig());
        SolverManager<TestdataSolution, Long> solverManager = SolverManager.create(
                solverConfig, new SolverManagerConfig().withParallelSolverCount("1"));

        AtomicInteger exceptionCount = new AtomicInteger();
        SolverJob<TestdataSolution, Long> solverJob1 = solverManager.solve(1L,
                problemId -> PlannerTestUtils.generateTestdataSolution("s1"),
                bestSolution -> {
                    throw new IllegalStateException("exceptionInConsumer");
                }, (problemId, throwable) -> exceptionCount.incrementAndGet());
        assertThatThrownBy(solverJob1::getFinalBestSolution)
                .isInstanceOf(ExecutionException.class)
                .hasRootCauseMessage("exceptionInConsumer");
        assertThat(exceptionCount.get()).isEqualTo(1);
        assertThat(solverManager.getSolverStatus(1L)).isEqualTo(NOT_SOLVING);
        assertThat(solverJob1.getSolverStatus()).isEqualTo(NOT_SOLVING);
    }

    @Test
    @Timeout(60)
    public void solveGenerics() throws ExecutionException, InterruptedException {
        SolverConfig solverConfig = PlannerTestUtils
                .buildSolverConfig(TestdataSolution.class, TestdataEntity.class);
        SolverManager<TestdataSolution, Long> solverManager = SolverManager
                .create(solverConfig, new SolverManagerConfig());

        BiConsumer<Object, Object> exceptionHandler = (o1, o2) -> fail("Solving failed.");
        Consumer<Object> finalBestSolutionConsumer = o -> {
        };
        Function<Object, TestdataUnannotatedExtendedSolution> problemFinder = o -> new TestdataUnannotatedExtendedSolution(
                PlannerTestUtils.generateTestdataSolution("s1"));

        SolverJob<TestdataSolution, Long> solverJob = solverManager.solve(1L, problemFinder, finalBestSolutionConsumer,
                exceptionHandler);
        solverJob.getFinalBestSolution();
    }

    @Disabled("Skip ahead not yet supported")
    @Test
    @Timeout(60)
    public void skipAhead() throws ExecutionException, InterruptedException {
        CountDownLatch latch = new CountDownLatch(1);
        SolverConfig solverConfig = PlannerTestUtils.buildSolverConfig(TestdataSolution.class, TestdataEntity.class)
                .withPhases(new CustomPhaseConfig().withCustomPhaseCommands(
                        (ScoreDirector<TestdataSolution> scoreDirector) -> {
                            TestdataSolution solution = scoreDirector.getWorkingSolution();
                            TestdataEntity entity = solution.getEntityList().get(0);
                            scoreDirector.beforeVariableChanged(entity, "value");
                            entity.setValue(solution.getValueList().get(0));
                            scoreDirector.afterVariableChanged(entity, "value");
                            scoreDirector.triggerVariableListeners();
                        }, (ScoreDirector<TestdataSolution> scoreDirector) -> {
                            TestdataSolution solution = scoreDirector.getWorkingSolution();
                            TestdataEntity entity = solution.getEntityList().get(1);
                            scoreDirector.beforeVariableChanged(entity, "value");
                            entity.setValue(solution.getValueList().get(1));
                            scoreDirector.afterVariableChanged(entity, "value");
                            scoreDirector.triggerVariableListeners();
                        }, (ScoreDirector<TestdataSolution> scoreDirector) -> {
                            TestdataSolution solution = scoreDirector.getWorkingSolution();
                            TestdataEntity entity = solution.getEntityList().get(2);
                            scoreDirector.beforeVariableChanged(entity, "value");
                            entity.setValue(solution.getValueList().get(2));
                            scoreDirector.afterVariableChanged(entity, "value");
                            scoreDirector.triggerVariableListeners();
                        }, (ScoreDirector<TestdataSolution> scoreDirector) -> {
                            // In the next best solution event, both e1 and e2 are definitely not null (but e3 might be).
                            latch.countDown();
                            TestdataSolution solution = scoreDirector.getWorkingSolution();
                            TestdataEntity entity = solution.getEntityList().get(3);
                            scoreDirector.beforeVariableChanged(entity, "value");
                            entity.setValue(solution.getValueList().get(3));
                            scoreDirector.afterVariableChanged(entity, "value");
                            scoreDirector.triggerVariableListeners();
                        }));
        SolverManager<TestdataSolution, Long> solverManager = SolverManager.create(
                solverConfig, new SolverManagerConfig().withParallelSolverCount("1"));
        AtomicInteger eventCount = new AtomicInteger();
        SolverJob<TestdataSolution, Long> solverJob1 = solverManager.solveAndListen(1L,
                problemId -> PlannerTestUtils.generateTestdataSolution("s1", 4),
                bestSolution -> {
                    if (bestSolution.getEntityList().get(1).getValue() == null) {
                        // The problem itself causes a best solution event. TODO Do we really want that behavior?
                        return;
                    }
                    eventCount.incrementAndGet();
                    if (bestSolution.getEntityList().get(2).getValue() == null) {
                        try {
                            latch.await();
                        } catch (InterruptedException e) {
                            fail("Latch failed.");
                        }
                    } else if (bestSolution.getEntityList().get(3).getValue() == null) {
                        fail("No skip ahead occurred: both e2 and e3 are null in a best solution event.");
                    }
                });
        assertSolutionInitialized(solverJob1.getFinalBestSolution());
        // EventCount can be 2 or 3, depending on the race, but it can never be 4.
        assertThat(eventCount).hasValueLessThan(4);
    }

    @Test
    @Timeout(600)
    public void terminateEarly() throws InterruptedException, BrokenBarrierException {
        CyclicBarrier startedBarrier = new CyclicBarrier(2);
        SolverConfig solverConfig = PlannerTestUtils.buildSolverConfig(TestdataSolution.class, TestdataEntity.class)
                .withTerminationConfig(new TerminationConfig())
                .withPhases(new CustomPhaseConfig().withCustomPhaseCommands((scoreDirector) -> {
                    try {
                        startedBarrier.await();
                    } catch (InterruptedException | BrokenBarrierException e) {
                        throw new IllegalStateException("The startedBarrier failed.", e);
                    }
                }),
                        new ConstructionHeuristicPhaseConfig(),
                        new LocalSearchPhaseConfig());

        SolverManager<TestdataSolution, Long> solverManager = SolverManager.create(
                solverConfig, new SolverManagerConfig().withParallelSolverCount("1"));

        SolverJob<TestdataSolution, Long> solverJob1 = solverManager.solve(1L,
                PlannerTestUtils.generateTestdataSolution("s1", 4));
        SolverJob<TestdataSolution, Long> solverJob2 = solverManager.solve(2L,
                PlannerTestUtils.generateTestdataSolution("s2", 4));
        SolverJob<TestdataSolution, Long> solverJob3 = solverManager.solve(3L,
                PlannerTestUtils.generateTestdataSolution("s3", 4));

        // Give solver 1 enough time to start
        startedBarrier.await();
        assertThat(solverManager.getSolverStatus(1L)).isEqualTo(SOLVING_ACTIVE);
        assertThat(solverJob1.getSolverStatus()).isEqualTo(SOLVING_ACTIVE);
        assertThat(solverManager.getSolverStatus(2L)).isEqualTo(SOLVING_SCHEDULED);
        assertThat(solverJob2.getSolverStatus()).isEqualTo(SOLVING_SCHEDULED);
        assertThat(solverManager.getSolverStatus(3L)).isEqualTo(SOLVING_SCHEDULED);
        assertThat(solverJob3.getSolverStatus()).isEqualTo(SOLVING_SCHEDULED);

        // Terminate solver 2 before it begins
        solverManager.terminateEarly(2L);
        assertThat(solverManager.getSolverStatus(1L)).isEqualTo(SOLVING_ACTIVE);
        assertThat(solverJob1.getSolverStatus()).isEqualTo(SOLVING_ACTIVE);
        assertThat(solverManager.getSolverStatus(2L)).isEqualTo(NOT_SOLVING);
        assertThat(solverJob2.getSolverStatus()).isEqualTo(NOT_SOLVING);
        assertThat(solverManager.getSolverStatus(3L)).isEqualTo(SOLVING_SCHEDULED);
        assertThat(solverJob3.getSolverStatus()).isEqualTo(SOLVING_SCHEDULED);

        // Terminate solver 1 while it is running, allowing solver 3 to start
        solverManager.terminateEarly(1L);
        assertThat(solverManager.getSolverStatus(1L)).isEqualTo(NOT_SOLVING);
        assertThat(solverJob1.getSolverStatus()).isEqualTo(NOT_SOLVING);
        // Give solver 3 enough time to start
        startedBarrier.await();
        assertThat(solverManager.getSolverStatus(3L)).isEqualTo(SOLVING_ACTIVE);
        assertThat(solverJob3.getSolverStatus()).isEqualTo(SOLVING_ACTIVE);

        // Terminate solver 3 while it is running
        solverManager.terminateEarly(3L);
        assertThat(solverManager.getSolverStatus(3L)).isEqualTo(NOT_SOLVING);
        assertThat(solverJob3.getSolverStatus()).isEqualTo(NOT_SOLVING);
    }

    /**
     * Tests whether SolverManager can solve on multiple threads problems that use multiple thread counts.
     */
    @Disabled("https://issues.redhat.com/browse/PLANNER-1837")
    @Test
    @Timeout(60)
    public void solveMultipleThreadedMovesWithSolverManager_allGetSolved() throws ExecutionException, InterruptedException {
        int processCount = Runtime.getRuntime().availableProcessors();
        SolverConfig solverConfig = PlannerTestUtils.buildSolverConfig(TestdataSolution.class, TestdataEntity.class)
                .withPhases(new ConstructionHeuristicPhaseConfig(), new LocalSearchPhaseConfig())
                //                .withTerminationConfig(new TerminationConfig().withSecondsSpentLimit(4L))
                // Adds moveThreadCount to the solver config.
                .withMoveThreadCount("AUTO");
        // Creates solverManagerConfig with multiple threads.
        SolverManager<TestdataSolution, Integer> solverManager = SolverManager.create(solverConfig, new SolverManagerConfig());

        List<SolverJob<TestdataSolution, Integer>> jobs = new ArrayList<>();
        for (int i = 0; i < processCount; i++) {
            jobs.add(solverManager.solve(i, PlannerTestUtils.generateTestdataSolution("s" + i, 10)));
        }

        assertInitializedJobs(jobs);
    }

    private void assertInitializedJobs(List<SolverJob<TestdataSolution, Integer>> jobs)
            throws InterruptedException, ExecutionException {
        for (SolverJob<TestdataSolution, Integer> job : jobs) {
            // Method getFinalBestSolution() waits for the solving to finish, therefore it ensures synchronization.
            assertSolutionInitialized(job.getFinalBestSolution());
        }
    }

    @Test
    @Timeout(60)
    public void submitMoreProblemsThanCpus_allGetSolved() throws InterruptedException, ExecutionException {
        // Use twice the amount of problems than available processors.
        int problemCount = Runtime.getRuntime().availableProcessors() * 2;

        SolverManager<TestdataSolution, Integer> solverManager = createSolverManagerTestableByDifferentConsumers();
        assertDifferentSolveMethods(problemCount, solverManager);
    }

    private SolverManager<TestdataSolution, Integer> createSolverManagerTestableByDifferentConsumers() {
        List<PhaseConfig> phaseConfigList = IntStream.of(0, 1)
                .mapToObj((x) -> new CustomPhaseConfig().withCustomPhaseCommands(
                        (ScoreDirector<TestdataSolution> scoreDirector) -> {
                            TestdataSolution solution = scoreDirector.getWorkingSolution();
                            TestdataEntity entity = solution.getEntityList().get(x);
                            scoreDirector.beforeVariableChanged(entity, "value");
                            entity.setValue(solution.getValueList().get(x));
                            scoreDirector.afterVariableChanged(entity, "value");
                            scoreDirector.triggerVariableListeners();
                        }))
                .collect(Collectors.toList());

        SolverConfig solverConfig = PlannerTestUtils.buildSolverConfig(TestdataSolution.class, TestdataEntity.class)
                .withPhases(phaseConfigList.toArray(new PhaseConfig[0]));

        SolverManagerConfig solverManagerConfig = new SolverManagerConfig();

        return SolverManager.create(solverConfig, solverManagerConfig);
    }

    private void assertDifferentSolveMethods(int problemCount, SolverManager<TestdataSolution, Integer> solverManager)
            throws InterruptedException, ExecutionException {
        assertSolveWithoutConsumer(problemCount, solverManager);
        assertSolveWithConsumer(problemCount, solverManager, true);
        assertSolveWithConsumer(problemCount, solverManager, false);
    }

    private void assertSolveWithoutConsumer(int problemCount, SolverManager<TestdataSolution, Integer> solverManager)
            throws InterruptedException, ExecutionException {
        List<SolverJob<TestdataSolution, Integer>> jobs = new ArrayList<>(problemCount);

        for (int id = 0; id < problemCount; id++) {
            jobs.add(solverManager.solve(id, PlannerTestUtils.generateTestdataSolution(String.format("s%d", id))));
        }
        assertInitializedJobs(jobs);
    }

    private void assertSolveWithConsumer(
            int problemCount, SolverManager<TestdataSolution, Integer> solverManager, boolean listenWhileSolving)
            throws ExecutionException, InterruptedException {

        // Two solutions should be created for every problem.
        Map<Integer, List<TestdataSolution>> solutionMap = new HashMap<>(problemCount * 2);

        List<SolverJob<TestdataSolution, Integer>> jobs = new ArrayList<>(problemCount);

        for (int id = 0; id < problemCount; id++) {
            List<TestdataSolution> consumedBestSolutions = Collections.synchronizedList(new ArrayList<>());
            String solutionName = String.format("s%d", id);
            if (listenWhileSolving) {
                jobs.add(solverManager.solve(
                        id,
                        problemId -> PlannerTestUtils.generateTestdataSolution(solutionName, 2),
                        consumedBestSolutions::add, null));
            } else {
                jobs.add(solverManager.solveAndListen(
                        id,
                        problemId -> PlannerTestUtils.generateTestdataSolution(solutionName, 2),
                        consumedBestSolutions::add, null));
            }
            solutionMap.put(id, consumedBestSolutions);
        }
        assertInitializedJobs(jobs);

        if (listenWhileSolving) {
            assertConsumedSolutionsWithListeningWhileSolving(solutionMap);
        } else {
            assertConsumedSolutions(solutionMap);
        }
    }

    private void assertConsumedSolutions(Map<Integer, List<TestdataSolution>> consumedSolutions) {
        for (List<TestdataSolution> consumedSolution : consumedSolutions.values()) {
            assertThat(consumedSolution).hasSize(2);
            assertConsumedFirstBestSolution(consumedSolution.get(0));
            assertConsumedFinalBestSolution(consumedSolution.get(1));
        }
    }

    private void assertConsumedSolutionsWithListeningWhileSolving(Map<Integer, List<TestdataSolution>> consumedSolutions) {
        for (List<TestdataSolution> consumedSolution : consumedSolutions.values()) {
            assertThat(consumedSolution).hasSize(1);
            TestdataSolution solution = consumedSolution.get(0);
            assertConsumedFinalBestSolution(solution);
        }
    }

    private void assertConsumedFinalBestSolution(TestdataSolution solution) {
        TestdataEntity entity = solution.getEntityList().get(0);
        assertThat(entity.getCode()).isEqualTo("e1");
        assertThat(entity.getValue().getCode()).isEqualTo("v1");
        entity = solution.getEntityList().get(1);
        assertThat(entity.getCode()).isEqualTo("e2");
        assertThat(entity.getValue().getCode()).isEqualTo("v2");
    }

    private void assertConsumedFirstBestSolution(TestdataSolution solution) {
        TestdataEntity entity = solution.getEntityList().get(0);
        assertThat(entity.getCode()).isEqualTo("e1");
        assertThat(entity.getValue().getCode()).isEqualTo("v1");
        entity = solution.getEntityList().get(1);
        assertThat(entity.getCode()).isEqualTo("e2");
        assertThat(entity.getValue()).isNull();
    }

    @Test
    @Timeout(60)
    public void runSameIdProcesses_throwsIllegalStateException() {
        SolverManagerConfig solverManagerConfig = new SolverManagerConfig();

        SolverConfig solverConfig = PlannerTestUtils.buildSolverConfig(TestdataSolution.class, TestdataEntity.class)
                .withPhases(createPhaseWithConcurrentSolvingStart(2));

        SolverManager<TestdataSolution, Long> solverManager = SolverManager.create(solverConfig, solverManagerConfig);

        solverManager.solve(1L, PlannerTestUtils.generateTestdataSolution("s1"));
        assertThatThrownBy(() -> solverManager.solve(1L, PlannerTestUtils.generateTestdataSolution("s1")))
                .isInstanceOf(IllegalStateException.class).hasMessageContaining("already solving");
    }
}
