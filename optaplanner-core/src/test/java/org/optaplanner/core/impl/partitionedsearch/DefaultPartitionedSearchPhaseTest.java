/*
 * Copyright 2017 Red Hat, Inc. and/or its affiliates.
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
package org.optaplanner.core.impl.partitionedsearch;

import java.util.Arrays;
import java.util.HashMap;
import java.util.Map;
import java.util.concurrent.CountDownLatch;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.Future;
import java.util.concurrent.TimeUnit;
import java.util.stream.Collectors;
import java.util.stream.IntStream;

import org.junit.Test;
import org.optaplanner.core.api.solver.Solver;
import org.optaplanner.core.api.solver.SolverFactory;
import org.optaplanner.core.config.constructionheuristic.ConstructionHeuristicPhaseConfig;
import org.optaplanner.core.config.localsearch.LocalSearchPhaseConfig;
import org.optaplanner.core.config.partitionedsearch.PartitionedSearchPhaseConfig;
import org.optaplanner.core.config.solver.SolverConfig;
import org.optaplanner.core.config.solver.termination.TerminationConfig;
import org.optaplanner.core.impl.partitionedsearch.scope.PartitionedSearchPhaseScope;
import org.optaplanner.core.impl.phase.event.PhaseLifecycleListenerAdapter;
import org.optaplanner.core.impl.phase.scope.AbstractPhaseScope;
import org.optaplanner.core.impl.solver.DefaultSolver;
import org.optaplanner.core.impl.solver.scope.DefaultSolverScope;
import org.optaplanner.core.impl.testdata.domain.TestdataEntity;
import org.optaplanner.core.impl.testdata.domain.TestdataSolution;
import org.optaplanner.core.impl.testdata.domain.TestdataValue;
import org.optaplanner.core.impl.testdata.util.PlannerTestUtils;

import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.Assert.fail;
import static org.junit.Assert.*;

public class DefaultPartitionedSearchPhaseTest {

    @Test(timeout = 5_000L)
    public void partCount() {
        partCount(SolverConfig.MOVE_THREAD_COUNT_NONE);
    }

    @Test(timeout = 5_000L)
    public void partCountAndMoveThreadCount() {
        partCount("2");
    }

    public void partCount(String moveThreadCount) {
        final int partSize = 3;
        final int partCount = 7;
        SolverFactory<TestdataSolution> solverFactory = createSolverFactory(false, moveThreadCount, partSize);
        DefaultSolver<TestdataSolution> solver = (DefaultSolver<TestdataSolution>) solverFactory.buildSolver();
        PartitionedSearchPhase<TestdataSolution> phase
                = (PartitionedSearchPhase<TestdataSolution>) solver.getPhaseList().get(0);
        phase.addPhaseLifecycleListener(new PhaseLifecycleListenerAdapter<TestdataSolution>() {
            @Override
            public void phaseStarted(AbstractPhaseScope<TestdataSolution> phaseScope) {
                assertEquals(Integer.valueOf(partCount), ((PartitionedSearchPhaseScope) phaseScope).getPartCount());
            }
        });
        solver.solve(createSolution(partCount * partSize, 2));
    }

    private static SolverFactory<TestdataSolution> createSolverFactory(boolean infinite, String moveThreadCount, int partSize) {
        SolverConfig solverConfig = PlannerTestUtils
                .buildSolverConfig(TestdataSolution.class, TestdataEntity.class);
        solverConfig.setMoveThreadCount(moveThreadCount);
        PartitionedSearchPhaseConfig partitionedSearchPhaseConfig = new PartitionedSearchPhaseConfig();
        partitionedSearchPhaseConfig.setSolutionPartitionerClass(TestdataSolutionPartitioner.class);
        Map<String, String> solutionPartitionerCustomProperties = new HashMap<>();
        solutionPartitionerCustomProperties.put("partSize", Integer.toString(partSize));
        partitionedSearchPhaseConfig.setSolutionPartitionerCustomProperties(solutionPartitionerCustomProperties);
        solverConfig.setPhaseConfigList(Arrays.asList(partitionedSearchPhaseConfig));
        ConstructionHeuristicPhaseConfig constructionHeuristicPhaseConfig = new ConstructionHeuristicPhaseConfig();
        LocalSearchPhaseConfig localSearchPhaseConfig = new LocalSearchPhaseConfig();
        if (!infinite) {
            localSearchPhaseConfig.setTerminationConfig(new TerminationConfig().withStepCountLimit(1));
        }
        partitionedSearchPhaseConfig.setPhaseConfigList(
                Arrays.asList(constructionHeuristicPhaseConfig, localSearchPhaseConfig));
        return SolverFactory.create(solverConfig);
    }

    private static TestdataSolution createSolution(int entities, int values) {
        TestdataSolution solution = new TestdataSolution();
        solution.setEntityList(IntStream.range(0, entities)
                .mapToObj(i -> new TestdataEntity(Character.toString((char) (65 + i))))
                .collect(Collectors.toList())
        );
        solution.setValueList(IntStream.range(0, values)
                .mapToObj(i -> new TestdataValue(Integer.toString(i)))
                .collect(Collectors.toList())
        );
        return solution;
    }

    @Test(timeout = 5_000L)
    public void exceptionPropagation() {
        final int partSize = 7;
        final int partCount = 3;

        TestdataSolution solution = createSolution(partCount * partSize - 1, 100);
        solution.getEntityList().add(new TestdataFaultyEntity("XYZ"));
        assertEquals(partSize * partCount, solution.getEntityList().size());

        SolverFactory<TestdataSolution> solverFactory = createSolverFactory(false, SolverConfig.MOVE_THREAD_COUNT_NONE, partSize);
        Solver<TestdataSolution> solver = solverFactory.buildSolver();
        try {
            solver.solve(solution);
            fail("The exception was not propagated.");
        } catch (IllegalStateException ex) {
            assertThat(ex).hasMessageMatching(".*partIndex.*Relayed.*");
            assertThat(ex).hasRootCauseExactlyInstanceOf(TestdataFaultyEntity.TestException.class);
        }
    }

    @Test(timeout = 5_000L)
    public void terminateEarly() throws InterruptedException, ExecutionException {
        final int partSize = 1;
        final int partCount = 2;

        TestdataSolution solution = createSolution(partCount * partSize, 10);

        SolverFactory<TestdataSolution> solverFactory = createSolverFactory(true, SolverConfig.MOVE_THREAD_COUNT_NONE, partSize);
        Solver<TestdataSolution> solver = solverFactory.buildSolver();
        CountDownLatch solvingStarted = new CountDownLatch(1);
        ((DefaultSolver<TestdataSolution>) solver).addPhaseLifecycleListener(
                new PhaseLifecycleListenerAdapter<TestdataSolution>() {
            @Override
            public void solvingStarted(DefaultSolverScope<TestdataSolution> solverScope) {
                solvingStarted.countDown();
            }
        });

        ExecutorService executor = Executors.newSingleThreadExecutor();
        Future<TestdataSolution> solutionFuture = executor.submit(() -> {
            return solver.solve(solution);
        });

        // make sure solver has started solving before terminating early
        solvingStarted.await();
        assertTrue(solver.terminateEarly());
        assertTrue(solver.isTerminateEarly());

        executor.shutdown();
        assertTrue(executor.awaitTermination(100, TimeUnit.MILLISECONDS));
        assertNotNull(solutionFuture.get());
    }

    @Test(timeout = 5_000L)
    public void shutdownMainThreadAbruptly() throws InterruptedException {
        final int partSize = 5;
        final int partCount = 3;

        TestdataSolution solution = createSolution(partCount * partSize - 1, 10);
        CountDownLatch sleepAnnouncement = new CountDownLatch(1);
        solution.getEntityList().add(new TestdataSleepingEntity("XYZ", sleepAnnouncement));

        SolverFactory<TestdataSolution> solverFactory = createSolverFactory(true, SolverConfig.MOVE_THREAD_COUNT_NONE, partSize);
        Solver<TestdataSolution> solver = solverFactory.buildSolver();

        ExecutorService executor = Executors.newSingleThreadExecutor();
        Future<TestdataSolution> solutionFuture = executor.submit(() -> {
            return solver.solve(solution);
        });

        sleepAnnouncement.await();
        // Now we know the sleeping entity is sleeping so we can attempt to shut down.
        // This will initiate an abrupt shutdown that will interrupt the main solver thread.
        executor.shutdownNow();

        // This verifies that PartitionQueue doesn't clear interrupted flag when the main solver thread is interrupted.
        assertTrue("Executor must terminate successfully when it's shut down abruptly",
                executor.awaitTermination(100, TimeUnit.MILLISECONDS));

        // This verifies that interruption is propagated to caller (wrapped as an IllegalStateException)
        try {
            solutionFuture.get();
            fail("InterruptedException should have been propagated to solver thread.");
        } catch (ExecutionException ex) {
            assertThat(ex).hasCause(new IllegalStateException("Solver thread was interrupted in Partitioned Search."));
            assertThat(ex).hasRootCauseExactlyInstanceOf(InterruptedException.class);
        }
    }

}
