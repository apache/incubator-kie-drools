/*
 * Copyright 2019 Red Hat, Inc. and/or its affiliates.
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

import org.junit.Ignore;
import org.junit.Test;
import org.optaplanner.core.config.constructionheuristic.ConstructionHeuristicPhaseConfig;
import org.optaplanner.core.config.localsearch.LocalSearchPhaseConfig;
import org.optaplanner.core.config.phase.PhaseConfig;
import org.optaplanner.core.config.phase.custom.CustomPhaseConfig;
import org.optaplanner.core.config.solver.SolverConfig;
import org.optaplanner.core.config.solver.SolverManagerConfig;
import org.optaplanner.core.impl.score.director.ScoreDirector;
import org.optaplanner.core.impl.testdata.domain.TestdataEntity;
import org.optaplanner.core.impl.testdata.domain.TestdataSolution;
import org.optaplanner.core.impl.testdata.domain.extended.TestdataUnannotatedExtendedSolution;
import org.optaplanner.core.impl.testdata.util.PlannerTestUtils;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertTrue;
import static org.junit.Assert.fail;
import static org.optaplanner.core.impl.testdata.util.PlannerAssert.assertSolutionInitialized;

public class SolverManagerTest {

    @Test(timeout = 1_000)
    public void solveBatch_2InParallel() throws ExecutionException, InterruptedException {
        final SolverConfig solverConfig = PlannerTestUtils.buildSolverConfig(TestdataSolution.class, TestdataEntity.class)
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

    @Test(timeout = 1_000)
    public void getSolverStatus() throws InterruptedException, BrokenBarrierException, ExecutionException {
        CyclicBarrier solverThreadReadyBarrier = new CyclicBarrier(2);
        CyclicBarrier mainThreadReadyBarrier = new CyclicBarrier(2);
        final SolverConfig solverConfig = PlannerTestUtils.buildSolverConfig(TestdataSolution.class, TestdataEntity.class)
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
        assertEquals(SolverStatus.SOLVING_ACTIVE, solverManager.getSolverStatus(1L));
        assertEquals(SolverStatus.SOLVING_ACTIVE, solverJob1.getSolverStatus());
        assertEquals(SolverStatus.SOLVING_SCHEDULED, solverManager.getSolverStatus(2L));
        assertEquals(SolverStatus.SOLVING_SCHEDULED, solverJob2.getSolverStatus());
        mainThreadReadyBarrier.await();
        solverThreadReadyBarrier.await();
        assertEquals(SolverStatus.NOT_SOLVING, solverManager.getSolverStatus(1L));
        assertEquals(SolverStatus.NOT_SOLVING, solverJob1.getSolverStatus());
        assertEquals(SolverStatus.SOLVING_ACTIVE, solverManager.getSolverStatus(2L));
        assertEquals(SolverStatus.SOLVING_ACTIVE, solverJob2.getSolverStatus());
        mainThreadReadyBarrier.await();
        solverJob1.getFinalBestSolution();
        solverJob2.getFinalBestSolution();
        assertEquals(SolverStatus.NOT_SOLVING, solverManager.getSolverStatus(1L));
        assertEquals(SolverStatus.NOT_SOLVING, solverJob1.getSolverStatus());
        assertEquals(SolverStatus.NOT_SOLVING, solverManager.getSolverStatus(2L));
        assertEquals(SolverStatus.NOT_SOLVING, solverJob2.getSolverStatus());
    }

    @Test
    public void exceptionInSolver() throws InterruptedException {
        final SolverConfig solverConfig = PlannerTestUtils.buildSolverConfig(TestdataSolution.class, TestdataEntity.class)
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
        try {
            solverJob1.getFinalBestSolution();
            fail("Exception got eaten.");
        } catch (ExecutionException e) {
            assertEquals(1, exceptionCount.get());
            assertEquals("exceptionInSolver", e.getCause().getCause().getMessage());
        }
        assertEquals(SolverStatus.NOT_SOLVING, solverManager.getSolverStatus(1L));
        assertEquals(SolverStatus.NOT_SOLVING, solverJob1.getSolverStatus());
    }

    @Test
    public void exceptionInConsumer() throws InterruptedException {
        final SolverConfig solverConfig = PlannerTestUtils.buildSolverConfig(TestdataSolution.class, TestdataEntity.class)
                .withPhases(new ConstructionHeuristicPhaseConfig());
        SolverManager<TestdataSolution, Long> solverManager = SolverManager.create(
                solverConfig, new SolverManagerConfig().withParallelSolverCount("1"));

        AtomicInteger exceptionCount = new AtomicInteger();
        SolverJob<TestdataSolution, Long> solverJob1 = solverManager.solve(1L,
                problemId -> PlannerTestUtils.generateTestdataSolution("s1"),
                bestSolution -> {
                    throw new IllegalStateException("exceptionInConsumer");
                }, (problemId, throwable) -> exceptionCount.incrementAndGet());
        try {
            solverJob1.getFinalBestSolution();
            fail("Exception got eaten.");
        } catch (ExecutionException e) {
            assertEquals(1, exceptionCount.get());
            assertEquals("exceptionInConsumer", e.getCause().getCause().getMessage());
        }
        assertEquals(SolverStatus.NOT_SOLVING, solverManager.getSolverStatus(1L));
        assertEquals(SolverStatus.NOT_SOLVING, solverJob1.getSolverStatus());
    }

    @Test
    public void solveGenerics() throws ExecutionException, InterruptedException {
        final SolverConfig solverConfig = PlannerTestUtils
                .buildSolverConfig(TestdataSolution.class, TestdataEntity.class);
        SolverManager<TestdataSolution, Long> solverManager = SolverManager
                .create(solverConfig, new SolverManagerConfig());

        BiConsumer<Object, Object> exceptionHandler = (o1, o2) -> fail("Solving failed.");
        Consumer<Object> finalBestSolutionConsumer = o -> {};
        Function<Object, TestdataUnannotatedExtendedSolution> problemFinder
                = o -> new TestdataUnannotatedExtendedSolution(PlannerTestUtils.generateTestdataSolution("s1"));

        SolverJob<TestdataSolution, Long> solverJob = solverManager.solve(1L, problemFinder, finalBestSolutionConsumer, exceptionHandler);
        solverJob.getFinalBestSolution();
    }

    @Ignore("Skip ahead not yet supported")
    @Test(timeout = 600_000)
    public void skipAhead() throws ExecutionException, InterruptedException {
        CountDownLatch latch = new CountDownLatch(1);
        final SolverConfig solverConfig = PlannerTestUtils.buildSolverConfig(TestdataSolution.class, TestdataEntity.class)
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
        assertTrue(eventCount.get() < 4);
    }

    @Ignore("https://issues.redhat.com/browse/PLANNER-1837")
    @Test
    public void solveMultipleThreadedMovesWithSolverManager_allGetSolved() throws ExecutionException, InterruptedException {
        int processCount = Runtime.getRuntime().availableProcessors();
        final SolverConfig solverConfig = PlannerTestUtils.buildSolverConfig(TestdataSolution.class, TestdataEntity.class)
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

    private void assertInitializedJobs(List<SolverJob<TestdataSolution, Integer>> jobs) throws InterruptedException, ExecutionException {
        for (SolverJob<TestdataSolution, Integer> job : jobs) {
            // Method getFinalBestSolution() waits for the solving to finish, therefore it ensures synchronization.
            assertSolutionInitialized(job.getFinalBestSolution());
        }
    }

    @Test(timeout = 2_000)
    public void submitMoreProblemsThanCpus_allGetSolved() throws InterruptedException, ExecutionException {
        // Use twice the amount of problems than available processors.
        int problemCount = Runtime.getRuntime().availableProcessors() * 2;

        SolverManager<TestdataSolution, Integer> solverManager = createSolverManagerTestableByDifferentConsumers();
        assertDifferentSolveMethods(problemCount, solverManager);
    }

    private SolverManager<TestdataSolution, Integer> createSolverManagerTestableByDifferentConsumers() {
        final SolverConfig solverConfig = PlannerTestUtils.buildSolverConfig(TestdataSolution.class, TestdataEntity.class)
                .withPhases(createGraduallyInitializedPhaseConfigs());

        SolverManagerConfig solverManagerConfig = new SolverManagerConfig();

        return SolverManager.create(solverConfig, solverManagerConfig);
    }

    private PhaseConfig[] createGraduallyInitializedPhaseConfigs() {
        PhaseConfig[] configs = new PhaseConfig[2];
        for (int x = 0; x < 2; x++) {
            configs[x] = createSetXEntityToXValuePhaseConfig(x);
        }
        return configs;
    }

    private CustomPhaseConfig createSetXEntityToXValuePhaseConfig(int x) {
        return new CustomPhaseConfig().withCustomPhaseCommands(
                (ScoreDirector<TestdataSolution> scoreDirector) -> {
                    TestdataSolution solution = scoreDirector.getWorkingSolution();
                    TestdataEntity entity = solution.getEntityList().get(x);
                    scoreDirector.beforeVariableChanged(entity, "value");
                    entity.setValue(solution.getValueList().get(x));
                    scoreDirector.afterVariableChanged(entity, "value");
                    scoreDirector.triggerVariableListeners();
                });
    }

    private void assertDifferentSolveMethods(int problemCount, SolverManager<TestdataSolution, Integer> solverManager) throws InterruptedException, ExecutionException {
        assertSolveWithoutConsumer(problemCount, solverManager);
        assertSolveWithConsumer(problemCount, solverManager, true);
        assertSolveWithConsumer(problemCount, solverManager, false);
    }

    private void assertSolveWithoutConsumer(int problemCount, SolverManager<TestdataSolution, Integer> solverManager)
            throws InterruptedException, ExecutionException {
        List<SolverJob<TestdataSolution, Integer>> jobs = new ArrayList<>(problemCount);

        for (int id = 0; id < problemCount; id++) {
            jobs.add(solverManager.solve(id, PlannerTestUtils.generateTestdataSolution(Integer.toString(id))));
        }
        assertInitializedJobs(jobs);
    }

    private void assertSolveWithConsumer(
            int problemCount, SolverManager<TestdataSolution, Integer> solverManager, boolean onlyFinalBestSolutions)
            throws ExecutionException, InterruptedException {

        // Two solutions for every problem.
        Map<Integer, List<TestdataSolution>> consumedSolutions = new HashMap<>(problemCount * 2);

        List<SolverJob<TestdataSolution, Integer>> jobs = new ArrayList<>(problemCount);

        for (int id = 0; id < problemCount; id++) {
            List<TestdataSolution> consumedBestSolutions = Collections.synchronizedList(new ArrayList<>());
            String solutionName = Integer.toString(id);
            if (onlyFinalBestSolutions) {
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
            consumedSolutions.put(id, consumedBestSolutions);
        }
        assertInitializedJobs(jobs);
        assertConsumedSolutions(problemCount, onlyFinalBestSolutions, consumedSolutions);
    }

    private void assertConsumedSolutions(int problemCount, boolean onlyFinalBestSolutions, Map<Integer, List<TestdataSolution>> consumedSolutions) {
        if (onlyFinalBestSolutions) {
            assertConsumedFinalBestSolutions(problemCount, consumedSolutions);
        } else {
            assertConsumedBestSolutions(problemCount, consumedSolutions);
        }
    }

    private void assertConsumedFinalBestSolutions(int problemCount, Map<Integer, List<TestdataSolution>> consumedSolutions) {
        for (int i = 0; i < problemCount; i++) {
            assertThat(consumedSolutions.get(i)).hasSize(1);
            TestdataSolution solution = consumedSolutions.get(i).get(0);
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

    private void assertConsumedBestSolutions(int problemCount, Map<Integer, List<TestdataSolution>> consumedSolutions) {
        for (int i = 0; i < problemCount; i++) {
            assertThat(consumedSolutions.get(i)).hasSize(2);
            assertConsumedFirstBestSolution(consumedSolutions.get(i).get(0));
            assertConsumedFinalBestSolution(consumedSolutions.get(i).get(1));
        }
    }

    private void assertConsumedFirstBestSolution(TestdataSolution solution) {
        TestdataEntity entity = solution.getEntityList().get(0);
        assertThat(entity.getCode()).isEqualTo("e1");
        assertThat(entity.getValue().getCode()).isEqualTo("v1");
        entity = solution.getEntityList().get(1);
        assertThat(entity.getCode()).isEqualTo("e2");
        assertThat(entity.getValue()).isNull();
    }

    @Test(timeout = 1_000)
    public void runSameIdProcesses_throwsIllegalStateException() {
        SolverManagerConfig solverManagerConfig = new SolverManagerConfig();

        SolverConfig solverConfig = PlannerTestUtils.buildSolverConfig(TestdataSolution.class, TestdataEntity.class)
                .withPhases(createPhaseWithConcurrentSolvingStart(2));

        SolverManager<TestdataSolution, Long> solverManager =
                SolverManager.create(solverConfig, solverManagerConfig);

        solverManager.solve(1L, PlannerTestUtils.generateTestdataSolution("solver"));
        assertThatThrownBy(() -> solverManager.solve(1L, PlannerTestUtils.generateTestdataSolution("solver")))
                .isInstanceOf(IllegalStateException.class).hasMessageContaining("already solving");
    }
}
