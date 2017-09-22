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
import java.util.Objects;
import java.util.concurrent.CountDownLatch;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.Future;
import java.util.concurrent.TimeUnit;
import java.util.stream.Collectors;
import java.util.stream.IntStream;

import org.junit.*;
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
import org.optaplanner.core.impl.testdata.domain.TestdataEntity;
import org.optaplanner.core.impl.testdata.domain.TestdataSolution;
import org.optaplanner.core.impl.testdata.domain.TestdataValue;
import org.optaplanner.core.impl.testdata.util.PlannerTestUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertTrue;
import static org.junit.Assert.fail;

public class DefaultPartitionedSearchPhaseTest {

    private static final Logger logger = LoggerFactory.getLogger(DefaultPartitionedSearchPhaseTest.class);

    private static void findCauseOrFail(Throwable ex, Class<? extends Throwable> cause) {
        findCauseOrFail(ex, cause, "");
    }

    private static void findCauseOrFail(Throwable ex, Class<? extends Throwable> cause, String msgSubstring) {
        Throwable t = ex.getCause();
        while (t != null) {
            if (cause.isAssignableFrom(t.getClass())) {
                break;
            } else {
                t = t.getCause();
            }
        }
        if (t == null) {
            logger.error("Solver failure was caused by something unexpected:", ex);
            fail("Solver failure should have been caused by " + cause.getCanonicalName()
                    + " but was caused by something unexpected.");
        } else {
            assertTrue("Exception message (" + t.getMessage() + ") should contain substring: " + msgSubstring,
                       Objects.toString(t.getMessage(), "").contains(msgSubstring));
        }
    }

    @Test
    public void partCount() {
        final int partSize = 3;
        final int partCount = 7;
        SolverFactory<TestdataSolution> solverFactory = createSolverFactory(false);
        setPartSize(solverFactory.getSolverConfig(), partSize);
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

    private static SolverFactory<TestdataSolution> createSolverFactory(boolean infinite) {
        SolverFactory<TestdataSolution> solverFactory = PlannerTestUtils
                .buildSolverFactory(TestdataSolution.class, TestdataEntity.class);
        SolverConfig solverConfig = solverFactory.getSolverConfig();
        PartitionedSearchPhaseConfig partitionedSearchPhaseConfig = new PartitionedSearchPhaseConfig();
        partitionedSearchPhaseConfig.setSolutionPartitionerClass(TestdataSolutionPartitioner.class);
        solverConfig.setPhaseConfigList(Arrays.asList(partitionedSearchPhaseConfig));
        ConstructionHeuristicPhaseConfig constructionHeuristicPhaseConfig = new ConstructionHeuristicPhaseConfig();
        LocalSearchPhaseConfig localSearchPhaseConfig = new LocalSearchPhaseConfig();
        if (!infinite) {
            localSearchPhaseConfig.setTerminationConfig(new TerminationConfig().withStepCountLimit(1));
        }
        partitionedSearchPhaseConfig.setPhaseConfigList(
                Arrays.asList(constructionHeuristicPhaseConfig, localSearchPhaseConfig));
        return solverFactory;
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

    private static void setPartSize(SolverConfig solverConfig, int partSize) {
        PartitionedSearchPhaseConfig phaseConfig
                = (PartitionedSearchPhaseConfig) solverConfig.getPhaseConfigList().get(0);
        Map<String, String> map = new HashMap<>();
        map.put("partSize", Integer.toString(partSize));
        phaseConfig.setSolutionPartitionerCustomProperties(map);
    }

    @Test
    public void exceptionPropagation() {
        final int partSize = 7;
        final int partCount = 3;

        TestdataSolution solution = createSolution(partCount * partSize - 1, 100);
        solution.getEntityList().add(new FaultyEntity("XYZ"));
        assertEquals(partSize * partCount, solution.getEntityList().size());

        SolverFactory<TestdataSolution> solverFactory = createSolverFactory(false);
        setPartSize(solverFactory.getSolverConfig(), partSize);
        Solver<TestdataSolution> solver = solverFactory.buildSolver();
        try {
            solver.solve(solution);
            fail("The exception was not propagated.");
        } catch (IllegalStateException ex) {
            findCauseOrFail(ex, ArithmeticException.class);
        }
    }

    @Test(timeout = 5000)
    public void terminateEarly() throws InterruptedException, ExecutionException {
        final int partSize = 1;
        final int partCount = 2;

        TestdataSolution solution = createSolution(partCount * partSize, 10);

        SolverFactory<TestdataSolution> solverFactory = createSolverFactory(true);
        setPartSize(solverFactory.getSolverConfig(), partSize);
        Solver<TestdataSolution> solver = solverFactory.buildSolver();

        ExecutorService executor = Executors.newSingleThreadExecutor();
        Future<TestdataSolution> solvedSolution = executor.submit(() -> {
            return solver.solve(solution);
        });

        while (!solver.isSolving()) {
            // wait until solver starts solving before terminating early
        }
        assertTrue(solver.terminateEarly());
        assertTrue(solver.isTerminateEarly());

        executor.shutdown();
        assertTrue(executor.awaitTermination(100, TimeUnit.MILLISECONDS));
        assertNotNull(solvedSolution.get());
    }

    @Test(timeout = 5000)
    // FIXME rename, because this test interrupts the main thread in PQueue iterator!!
    // TODO add another test that will interrupt one of the PartSolver threads (will require custom ThreadFactory that
    // will provide access to created threads)
    public void shutdownAbruptly() throws InterruptedException {
        final int partSize = 5;
        final int partCount = 3;

        TestdataSolution solution = createSolution(partCount * partSize - 1, 10);
        CountDownLatch latch = new CountDownLatch(1);
        solution.getEntityList().add(new BusyEntity("XYZ", latch));

        SolverFactory<TestdataSolution> solverFactory = createSolverFactory(true);
        setPartSize(solverFactory.getSolverConfig(), partSize);
        Solver<TestdataSolution> solver = solverFactory.buildSolver();

        ExecutorService executor = Executors.newSingleThreadExecutor();
        Future<TestdataSolution> solvedSolution = executor.submit(() -> {
            return solver.solve(solution);
        });

        latch.await();
        // Now we know the busy entity is busy so we can attempt to shut down.
        // This will initiate an abrupt shutdown that will interrupt all busy threads in the pool.
        executor.shutdownNow();

        // This verifies that solver checks Thread's interrupted flag and terminates solving when it detects the flag.
        assertTrue("Executor must terminate successfully when it's shut down abruptly",
                   executor.awaitTermination(1000, TimeUnit.MILLISECONDS));
        // This verifies that solver doesn't clear the interrupted flag
        try {
            solvedSolution.get();
            fail("InterruptedException should have been propagated to solver thread.");
        } catch (ExecutionException ex) {
            findCauseOrFail(ex, IllegalStateException.class, "Solver thread was interrupted in Partitioned Search");
            findCauseOrFail(ex, InterruptedException.class);
        }
    }

    public static class BusyEntity extends TestdataEntity {

        private static final Logger logger = LoggerFactory.getLogger(BusyEntity.class);
        private CountDownLatch latch;

        public BusyEntity() {
            // needed for cloning
        }

        public BusyEntity(String code, CountDownLatch cdl) {
            super(code);
            this.latch = cdl;
        }

        public CountDownLatch getLatch() {
            return latch;
        }

        public void setLatch(CountDownLatch latch) {
            this.latch = latch;
        }

        @Override
        public void setValue(TestdataValue value) {
            super.setValue(value);
            latch.countDown();
            logger.info("SETVALUE... STARTED");
            while (!Thread.currentThread().isInterrupted()) {
                // busy wait
            }
            logger.info("SETVALUE... INTERRUPTED!");
        }
    }

    public static class FaultyEntity extends TestdataEntity {

        private static final Logger logger = LoggerFactory.getLogger(FaultyEntity.class);

        public FaultyEntity() {
            // needed for cloning
        }

        public FaultyEntity(String code) {
            super(code);
        }

        @Override
        public void setValue(TestdataValue value) {
            super.setValue(value);
            logger.info("SOLVER FAULT");
            int zero = 0;
            logger.info("{}", 1 / zero);
        }
    }
}
