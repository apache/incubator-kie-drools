package org.optaplanner.core.impl.partitionedsearch;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatIllegalStateException;
import static org.assertj.core.api.Assertions.assertThatThrownBy;

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

import org.junit.jupiter.api.Disabled;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.Timeout;
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
import org.optaplanner.core.impl.solver.scope.SolverScope;
import org.optaplanner.core.impl.testdata.domain.TestdataEntity;
import org.optaplanner.core.impl.testdata.domain.TestdataSolution;
import org.optaplanner.core.impl.testdata.domain.TestdataValue;
import org.optaplanner.core.impl.testdata.util.PlannerTestUtils;

class DefaultPartitionedSearchPhaseTest {

    @Test
    @Timeout(5)
    void partCount() {
        partCount(SolverConfig.MOVE_THREAD_COUNT_NONE);
    }

    @Test
    @Timeout(5)
    void partCountAndMoveThreadCount() {
        partCount("2");
    }

    void partCount(String moveThreadCount) {
        final int partSize = 3;
        final int partCount = 7;
        SolverFactory<TestdataSolution> solverFactory = createSolverFactory(false, moveThreadCount, partSize);
        DefaultSolver<TestdataSolution> solver = (DefaultSolver<TestdataSolution>) solverFactory.buildSolver();
        PartitionedSearchPhase<TestdataSolution> phase = (PartitionedSearchPhase<TestdataSolution>) solver.getPhaseList()
                .get(0);
        phase.addPhaseLifecycleListener(new PhaseLifecycleListenerAdapter<TestdataSolution>() {
            @Override
            public void phaseStarted(AbstractPhaseScope<TestdataSolution> phaseScope) {
                assertThat(((PartitionedSearchPhaseScope) phaseScope).getPartCount()).isEqualTo(Integer.valueOf(partCount));
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
                .collect(Collectors.toList()));
        solution.setValueList(IntStream.range(0, values)
                .mapToObj(i -> new TestdataValue(Integer.toString(i)))
                .collect(Collectors.toList()));
        return solution;
    }

    @Test
    @Timeout(5)
    void exceptionPropagation() {
        final int partSize = 7;
        final int partCount = 3;

        TestdataSolution solution = createSolution(partCount * partSize - 1, 100);
        solution.getEntityList().add(new TestdataFaultyEntity("XYZ"));
        assertThat(solution.getEntityList().size()).isEqualTo(partSize * partCount);

        SolverFactory<TestdataSolution> solverFactory = createSolverFactory(false, SolverConfig.MOVE_THREAD_COUNT_NONE,
                partSize);
        Solver<TestdataSolution> solver = solverFactory.buildSolver();
        assertThatIllegalStateException()
                .isThrownBy(() -> solver.solve(solution))
                .withMessageMatching(".*partIndex.*Relayed.*")
                .withRootCauseExactlyInstanceOf(TestdataFaultyEntity.TestException.class);
    }

    @Test
    @Timeout(5)
    void terminateEarly() throws InterruptedException, ExecutionException {
        final int partSize = 1;
        final int partCount = 2;

        TestdataSolution solution = createSolution(partCount * partSize, 10);

        SolverFactory<TestdataSolution> solverFactory = createSolverFactory(true, SolverConfig.MOVE_THREAD_COUNT_NONE,
                partSize);
        Solver<TestdataSolution> solver = solverFactory.buildSolver();
        CountDownLatch solvingStarted = new CountDownLatch(1);
        ((DefaultSolver<TestdataSolution>) solver).addPhaseLifecycleListener(
                new PhaseLifecycleListenerAdapter<TestdataSolution>() {
                    @Override
                    public void solvingStarted(SolverScope<TestdataSolution> solverScope) {
                        solvingStarted.countDown();
                    }
                });

        ExecutorService executor = Executors.newSingleThreadExecutor();
        Future<TestdataSolution> solutionFuture = executor.submit(() -> solver.solve(solution));

        // make sure solver has started solving before terminating early
        solvingStarted.await();
        assertThat(solver.terminateEarly()).isTrue();
        assertThat(solver.isTerminateEarly()).isTrue();

        executor.shutdown();
        assertThat(executor.awaitTermination(1, TimeUnit.SECONDS)).isTrue();
        assertThat(solutionFuture.get()).isNotNull();
    }

    @Disabled("PLANNER-2249")
    @Test
    @Timeout(5)
    void shutdownMainThreadAbruptly() throws InterruptedException {
        final int partSize = 5;
        final int partCount = 3;

        TestdataSolution solution = createSolution(partCount * partSize - 1, 10);
        CountDownLatch sleepAnnouncement = new CountDownLatch(1);
        solution.getEntityList().add(new TestdataSleepingEntity("XYZ", sleepAnnouncement));

        SolverFactory<TestdataSolution> solverFactory = createSolverFactory(true, SolverConfig.MOVE_THREAD_COUNT_NONE,
                partSize);
        Solver<TestdataSolution> solver = solverFactory.buildSolver();

        ExecutorService executor = Executors.newSingleThreadExecutor();
        Future<TestdataSolution> solutionFuture = executor.submit(() -> solver.solve(solution));

        sleepAnnouncement.await();
        // Now we know the sleeping entity is sleeping so we can attempt to shut down.
        // This will initiate an abrupt shutdown that will interrupt the main solver thread.
        executor.shutdownNow();

        // This verifies that PartitionQueue doesn't clear interrupted flag when the main solver thread is interrupted.
        assertThat(executor.awaitTermination(100, TimeUnit.MILLISECONDS))
                .as("Executor must terminate successfully when it's shut down abruptly")
                .isTrue();

        // This verifies that interruption is propagated to caller (wrapped as an IllegalStateException)
        assertThatThrownBy(solutionFuture::get)
                .isInstanceOf(ExecutionException.class)
                .hasCause(new IllegalStateException("Solver thread was interrupted in Partitioned Search."))
                .hasRootCauseExactlyInstanceOf(InterruptedException.class);
    }

}
