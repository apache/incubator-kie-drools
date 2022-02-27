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
import static org.assertj.core.api.Assertions.assertThatIllegalStateException;
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
import java.util.concurrent.atomic.AtomicReference;
import java.util.function.BiConsumer;
import java.util.function.Consumer;
import java.util.function.Function;
import java.util.stream.Collectors;
import java.util.stream.IntStream;

import org.junit.jupiter.api.AfterEach;
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
import org.optaplanner.core.impl.testdata.domain.TestdataValue;
import org.optaplanner.core.impl.testdata.domain.extended.TestdataUnannotatedExtendedSolution;
import org.optaplanner.core.impl.testdata.util.PlannerTestUtils;

class SolverManagerTest {

    private SolverManager<TestdataSolution, Long> solverManager;

    @AfterEach
    void closeSolverManager() {
        if (solverManager != null) {
            solverManager.close();
        }
    }

    @Test
    void create() {
        SolverConfig solverConfig = PlannerTestUtils.buildSolverConfig(TestdataSolution.class, TestdataEntity.class);
        SolverManager.create(solverConfig).close();
        SolverManagerConfig solverManagerConfig = new SolverManagerConfig();
        SolverManager.create(solverConfig, solverManagerConfig).close();
        SolverFactory<TestdataSolution> solverFactory = SolverFactory.create(solverConfig);
        SolverManager.create(solverFactory).close();
        SolverManager.create(solverFactory, solverManagerConfig).close();
    }

    @Test
    @Timeout(60)
    void solveBatch_2InParallel() throws ExecutionException, InterruptedException {
        SolverConfig solverConfig = PlannerTestUtils.buildSolverConfig(TestdataSolution.class, TestdataEntity.class)
                .withPhases(createPhaseWithConcurrentSolvingStart(2), new ConstructionHeuristicPhaseConfig());
        solverManager = SolverManager.create(
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
    void getSolverStatus() throws InterruptedException, BrokenBarrierException, ExecutionException {
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
        solverManager = SolverManager.create(
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
    void exceptionInSolver() {
        SolverConfig solverConfig = PlannerTestUtils.buildSolverConfig(TestdataSolution.class, TestdataEntity.class)
                .withPhases(new CustomPhaseConfig().withCustomPhaseCommands(
                        scoreDirector -> {
                            throw new IllegalStateException("exceptionInSolver");
                        }));
        solverManager = SolverManager.create(
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
    void exceptionInConsumer() throws InterruptedException {
        SolverConfig solverConfig = PlannerTestUtils.buildSolverConfig(TestdataSolution.class, TestdataEntity.class)
                .withPhases(new ConstructionHeuristicPhaseConfig());
        solverManager = SolverManager.create(
                solverConfig, new SolverManagerConfig().withParallelSolverCount("1"));

        CountDownLatch consumerInvoked = new CountDownLatch(1);
        AtomicReference<Throwable> errorInConsumer = new AtomicReference<>();
        SolverJob<TestdataSolution, Long> solverJob1 = solverManager.solve(1L,
                problemId -> PlannerTestUtils.generateTestdataSolution("s1"),
                bestSolution -> {
                    throw new IllegalStateException("exceptionInConsumer");
                }, (problemId, throwable) -> {
                    errorInConsumer.set(throwable);
                    consumerInvoked.countDown();
                });

        consumerInvoked.await();
        assertThat(errorInConsumer.get())
                .isInstanceOf(IllegalStateException.class)
                .hasMessage("exceptionInConsumer");
        assertThat(solverManager.getSolverStatus(1L)).isEqualTo(NOT_SOLVING);
        assertThat(solverJob1.getSolverStatus()).isEqualTo(NOT_SOLVING);
    }

    @Test
    @Timeout(60)
    void solveGenerics() throws ExecutionException, InterruptedException {
        SolverConfig solverConfig = PlannerTestUtils
                .buildSolverConfig(TestdataSolution.class, TestdataEntity.class);
        solverManager = SolverManager
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

    @Test
    @Timeout(60)
    void skipAhead() throws ExecutionException, InterruptedException {
        CountDownLatch latch = new CountDownLatch(1);
        SolverConfig solverConfig = PlannerTestUtils.buildSolverConfig(TestdataSolution.class,
                TestdataEntity.class)
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
        solverManager = SolverManager.create(
                solverConfig, new SolverManagerConfig().withParallelSolverCount("1"));
        AtomicInteger bestSolutionCount = new AtomicInteger();
        AtomicInteger finalBestSolutionCount = new AtomicInteger();
        AtomicReference<Throwable> consumptionError = new AtomicReference<>();
        CountDownLatch finalBestSolutionConsumed = new CountDownLatch(1);
        SolverJob<TestdataSolution, Long> solverJob1 = solverManager.solveAndListen(1L,
                problemId -> PlannerTestUtils.generateTestdataSolution("s1", 4),
                bestSolution -> {
                    boolean isFirstReceivedSolution = bestSolutionCount.incrementAndGet() == 1;
                    if (bestSolution.getEntityList().get(1).getValue() == null) {
                        // This best solution may be skipped as well.
                        try {
                            latch.await();
                        } catch (InterruptedException e) {
                            fail("Latch failed.");
                        }
                    } else if (bestSolution.getEntityList().get(2).getValue() == null && !isFirstReceivedSolution) {
                        fail("No skip ahead occurred: both e2 and e3 are null in a best solution event.");
                    }
                },
                finalBestSolution -> {
                    finalBestSolutionCount.incrementAndGet();
                    finalBestSolutionConsumed.countDown();
                },
                (problemId, throwable) -> consumptionError.set(throwable));
        assertSolutionInitialized(solverJob1.getFinalBestSolution());
        // EventCount can be 2 or 3, depending on the race, but it can never be 4.
        assertThat(bestSolutionCount).hasValueLessThan(4);
        finalBestSolutionConsumed.await();
        assertThat(finalBestSolutionCount.get()).isEqualTo(1);
        if (consumptionError.get() != null) {
            fail("Error in the best solution consumer.", consumptionError.get());
        }
    }

    @Test
    @Timeout(600)
    void terminateEarly() throws InterruptedException, BrokenBarrierException {
        CyclicBarrier startedBarrier = new CyclicBarrier(2);
        SolverConfig solverConfig = PlannerTestUtils.buildSolverConfig(TestdataSolution.class,
                TestdataEntity.class)
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

        solverManager = SolverManager.create(
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
    void solveMultipleThreadedMovesWithSolverManager_allGetSolved() throws ExecutionException, InterruptedException {
        int processCount = Runtime.getRuntime().availableProcessors();
        SolverConfig solverConfig =
                PlannerTestUtils.buildSolverConfig(TestdataSolution.class, TestdataEntity.class)
                        .withPhases(new ConstructionHeuristicPhaseConfig(), new LocalSearchPhaseConfig())
                        //                .withTerminationConfig(new TerminationConfig().withSecondsSpentLimit(4L))
                        // Adds moveThreadCount to the solver config.
                        .withMoveThreadCount("AUTO");
        // Creates solverManagerConfig with multiple threads.
        solverManager =
                SolverManager.create(solverConfig, new SolverManagerConfig());

        List<SolverJob<TestdataSolution, Long>> jobs = new ArrayList<>();
        for (long i = 0; i < processCount; i++) {
            jobs.add(solverManager.solve(i, PlannerTestUtils.generateTestdataSolution("s" + i, 10)));
        }

        assertInitializedJobs(jobs);
    }

    private void assertInitializedJobs(List<SolverJob<TestdataSolution, Long>> jobs)
            throws InterruptedException, ExecutionException {
        for (SolverJob<TestdataSolution, Long> job : jobs) {
            // Method getFinalBestSolution() waits for the solving to finish, therefore it ensures synchronization.
            assertSolutionInitialized(job.getFinalBestSolution());
        }
    }

    @Test
    @Timeout(60)
    void submitMoreProblemsThanCpus_allGetSolved() throws InterruptedException, ExecutionException {
        // Use twice the amount of problems than available processors.
        int problemCount = Runtime.getRuntime().availableProcessors() * 2;

        solverManager = createSolverManagerTestableByDifferentConsumers();
        assertSolveWithoutConsumer(problemCount, solverManager);
        assertSolveWithConsumer(problemCount, solverManager, true);
        assertSolveWithConsumer(problemCount, solverManager, false);
    }

    private SolverManager<TestdataSolution, Long> createSolverManagerTestableByDifferentConsumers() {
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

        SolverConfig solverConfig = PlannerTestUtils.buildSolverConfig(TestdataSolution.class,
                TestdataEntity.class)
                .withPhases(phaseConfigList.toArray(new PhaseConfig[0]));

        SolverManagerConfig solverManagerConfig = new SolverManagerConfig();

        return SolverManager.create(solverConfig, solverManagerConfig);
    }

    private void assertSolveWithoutConsumer(int problemCount, SolverManager<TestdataSolution, Long> solverManager)
            throws InterruptedException, ExecutionException {
        List<SolverJob<TestdataSolution, Long>> jobs = new ArrayList<>(problemCount);

        for (long id = 0; id < problemCount; id++) {
            jobs.add(solverManager.solve(id, PlannerTestUtils.generateTestdataSolution(String.format("s%d", id))));
        }
        assertInitializedJobs(jobs);
    }

    private void assertSolveWithConsumer(
            int problemCount, SolverManager<TestdataSolution, Long> solverManager, boolean listenWhileSolving)
            throws ExecutionException, InterruptedException {

        // Two solutions should be created for every problem.
        Map<Long, List<TestdataSolution>> solutionMap = new HashMap<>(problemCount * 2);

        CountDownLatch finalBestSolutionConsumed = new CountDownLatch(problemCount);
        List<SolverJob<TestdataSolution, Long>> jobs = new ArrayList<>(problemCount);

        for (long id = 0; id < problemCount; id++) {
            List<TestdataSolution> consumedBestSolutions = Collections.synchronizedList(new ArrayList<>());
            String solutionName = String.format("s%d", id);
            if (listenWhileSolving) {
                jobs.add(solverManager.solveAndListen(
                        id,
                        problemId -> PlannerTestUtils.generateTestdataSolution(solutionName, 2),
                        consumedBestSolutions::add, (finalBestSolution) -> {
                            finalBestSolutionConsumed.countDown();
                        }, null));
            } else {
                jobs.add(solverManager.solve(
                        id,
                        problemId -> PlannerTestUtils.generateTestdataSolution(solutionName, 2),
                        (finalBestSolution) -> {
                            consumedBestSolutions.add(finalBestSolution);
                            finalBestSolutionConsumed.countDown();
                        }, null));
            }
            solutionMap.put(id, consumedBestSolutions);
        }
        assertInitializedJobs(jobs);

        finalBestSolutionConsumed.await(); // Wait till all final best solutions have been consumed.
        if (listenWhileSolving) {
            assertConsumedSolutionsWithListeningWhileSolving(solutionMap);
        } else {
            assertConsumedSolutions(solutionMap);
        }
    }

    private void assertConsumedSolutions(Map<Long, List<TestdataSolution>> consumedSolutions) {
        for (List<TestdataSolution> consumedSolution : consumedSolutions.values()) {
            assertThat(consumedSolution).hasSize(1);
            assertConsumedFinalBestSolution(consumedSolution.get(0));
        }
    }

    private void assertConsumedSolutionsWithListeningWhileSolving(Map<Long, List<TestdataSolution>> consumedSolutions) {
        consumedSolutions.forEach((problemId, bestSolutions) -> {
            if (bestSolutions.size() == 2) {
                assertConsumedFirstBestSolution(bestSolutions.get(0));
                assertConsumedFinalBestSolution(bestSolutions.get(1));
            } else if (bestSolutions.size() == 1) { // The fist best solution has been skipped.
                assertConsumedFinalBestSolution(bestSolutions.get(0));
            } else {
                fail("Unexpected number of received best solutions ("
                        + bestSolutions.size() + "). Should be either 1 or 2.");
            }
        });
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
    void runSameIdProcesses_throwsIllegalStateException() {
        SolverManagerConfig solverManagerConfig = new SolverManagerConfig();

        SolverConfig solverConfig = PlannerTestUtils.buildSolverConfig(TestdataSolution.class,
                TestdataEntity.class)
                .withPhases(createPhaseWithConcurrentSolvingStart(2));

        solverManager = SolverManager.create(solverConfig, solverManagerConfig);

        solverManager.solve(1L, PlannerTestUtils.generateTestdataSolution("s1"));
        assertThatThrownBy(() -> solverManager.solve(1L, PlannerTestUtils.generateTestdataSolution("s1")))
                .isInstanceOf(IllegalStateException.class).hasMessageContaining("already solving");
    }

    @Test
    @Timeout(60)
    void submitProblemChange() throws InterruptedException {
        SolverConfig solverConfig = PlannerTestUtils.buildSolverConfig(TestdataSolution.class, TestdataEntity.class);
        solverConfig.setDaemon(true);
        solverManager = SolverManager.create(solverConfig);
        CountDownLatch solverStarted = new CountDownLatch(1);
        CountDownLatch solutionWithProblemChangeReceived = new CountDownLatch(1);
        final long problemId = 1L;

        final int entityAndValueCount = 4;
        AtomicReference<TestdataSolution> bestSolution = new AtomicReference<>();
        solverManager.solveAndListen(problemId,
                id -> PlannerTestUtils.generateTestdataSolution("s1", entityAndValueCount),
                testdataSolution -> {
                    solverStarted.countDown();
                    if (testdataSolution.getValueList().size() == entityAndValueCount + 1) {
                        bestSolution.set(testdataSolution);
                        solutionWithProblemChangeReceived.countDown();
                    }
                });

        solverStarted.await();
        solverManager.addProblemChange(problemId, (workingSolution, problemChangeDirector) -> {
            problemChangeDirector.addProblemFact(new TestdataValue("addedValue"),
                    workingSolution.getValueList()::add);
        });

        solutionWithProblemChangeReceived.await();
        assertThat(bestSolution.get().getValueList()).hasSize(entityAndValueCount + 1);
    }

    @Test
    @Timeout(60)
    void addProblemChangeToNonExistingProblem_failsFast() {
        SolverConfig solverConfig = PlannerTestUtils.buildSolverConfig(TestdataSolution.class, TestdataEntity.class);
        solverManager = SolverManager.create(solverConfig);

        solverManager.solveAndListen(1L, id -> PlannerTestUtils.generateTestdataSolution("s1", 4), testdataSolution -> {
        });
        final long nonExistingProblemId = 999L;
        assertThatIllegalStateException()
                .isThrownBy(() -> solverManager.addProblemChange(nonExistingProblemId,
                        (workingSolution, problemChangeDirector) -> problemChangeDirector.addProblemFact(
                                new TestdataValue("addedValue"),
                                workingSolution.getValueList()::add)))
                .withMessageContaining(String.valueOf(nonExistingProblemId));
    }

    @Test
    @Timeout(60)
    void addProblemChangeToWaitingSolver() throws InterruptedException {
        CountDownLatch solvingPausedLatch = new CountDownLatch(1);
        PhaseConfig<?> pausedPhaseConfig = new CustomPhaseConfig().withCustomPhaseCommands(
                scoreDirector -> {
                    try {
                        solvingPausedLatch.await();
                    } catch (InterruptedException e) {
                        fail("CountDownLatch failed.");
                    }
                });

        SolverConfig solverConfig = PlannerTestUtils.buildSolverConfig(TestdataSolution.class, TestdataEntity.class)
                .withPhases(pausedPhaseConfig, new ConstructionHeuristicPhaseConfig());
        // Allow only a single active solver.
        SolverManagerConfig solverManagerConfig = new SolverManagerConfig().withParallelSolverCount("1");
        solverManager = SolverManager.create(solverConfig, solverManagerConfig);

        // The first solver waits until the test sends a problem change.
        solverManager.solve(1L, PlannerTestUtils.generateTestdataSolution("s1", 4));

        // The second solver is scheduled and waits for the fist solver to finish.
        final long secondProblemId = 2L;
        final int entityAndValueCount = 4;
        CountDownLatch solutionWithProblemChangeReceived = new CountDownLatch(1);
        AtomicReference<TestdataSolution> bestSolution = new AtomicReference<>();
        solverManager.solveAndListen(secondProblemId,
                id -> PlannerTestUtils.generateTestdataSolution("s2", entityAndValueCount),
                testdataSolution -> {
                    if (testdataSolution.getValueList().size() == entityAndValueCount + 1) {
                        bestSolution.set(testdataSolution);
                        solutionWithProblemChangeReceived.countDown();
                    }
                });

        solverManager.addProblemChange(secondProblemId, (workingSolution, problemChangeDirector) -> {
            problemChangeDirector.addProblemFact(new TestdataValue("addedValue"),
                    workingSolution.getValueList()::add);
        });

        // The first solver can proceed. When it finishes, the second solver starts solving and picks up the change.
        solvingPausedLatch.countDown();

        solutionWithProblemChangeReceived.await();
        assertThat(bestSolution.get().getValueList()).hasSize(entityAndValueCount + 1);
    }
}
