package org.optaplanner.benchmark.impl;

import java.io.File;
import java.util.Collections;
import java.util.concurrent.ExecutorService;

import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;
import static org.assertj.core.api.Assertions.assertThatExceptionOfType;

import org.junit.Test;
import org.optaplanner.benchmark.api.PlannerBenchmarkException;
import org.optaplanner.benchmark.api.PlannerBenchmarkFactory;
import org.optaplanner.benchmark.config.PlannerBenchmarkConfig;
import org.optaplanner.benchmark.impl.report.BenchmarkReport;
import org.optaplanner.benchmark.impl.result.PlannerBenchmarkResult;
import org.optaplanner.benchmark.impl.result.SolverBenchmarkResult;
import org.optaplanner.core.config.SolverConfigContext;
import org.optaplanner.core.config.solver.SolverConfig;
import org.optaplanner.core.impl.testdata.domain.TestdataEntity;
import org.optaplanner.core.impl.testdata.domain.TestdataSolution;
import org.optaplanner.core.impl.testdata.util.PlannerTestUtils;

public class DefaultPlannerBenchmarkTest {

    @Test
    public void benchmarkingStartedTwice() {
        SolverConfig solverConfig = PlannerTestUtils.buildSolverConfig(
                TestdataSolution.class, TestdataEntity.class);
        PlannerBenchmarkFactory benchmarkFactory = PlannerBenchmarkFactory.create(
                PlannerBenchmarkConfig.createFromSolverConfig(solverConfig));

        TestdataSolution solution = mock(TestdataSolution.class);

        DefaultPlannerBenchmark benchmark = (DefaultPlannerBenchmark) benchmarkFactory.buildPlannerBenchmark(solution);
        benchmark.benchmarkingStarted();

        assertThatExceptionOfType(IllegalStateException.class).isThrownBy(benchmark::benchmarkingStarted).withNoCause();
    }

    @Test
    public void solverBenchmarkResultListIsEmpty() {
        SolverConfigContext solverConfigContext = mock(SolverConfigContext.class);
        File benchmarkDirectory = mock(File.class);
        ExecutorService executorService = mock(ExecutorService.class);
        BenchmarkReport benchmarkReport = mock(BenchmarkReport.class);

        // solverBenchmarkResultList is empty when instantiated by default constructor
        PlannerBenchmarkResult benchmarkResult = new PlannerBenchmarkResult();

        DefaultPlannerBenchmark benchmark = new DefaultPlannerBenchmark(benchmarkResult, solverConfigContext,
                                                                        benchmarkDirectory, executorService,
                                                                        executorService, benchmarkReport);

        assertThatExceptionOfType(IllegalArgumentException.class).isThrownBy(benchmark::benchmarkingStarted)
                .withMessageContaining("solverBenchmarkResultList").withMessageContaining("empty");
    }

    @Test
    public void benchmarkDirectoryIsNull() {
        SolverConfigContext solverConfigContext = mock(SolverConfigContext.class);
        ExecutorService executorService = mock(ExecutorService.class);
        BenchmarkReport benchmarkReport = mock(BenchmarkReport.class);
        SolverBenchmarkResult benchmarkResult = mock(SolverBenchmarkResult.class);

        PlannerBenchmarkResult plannerBenchmarkResult = new PlannerBenchmarkResult();
        plannerBenchmarkResult.setSolverBenchmarkResultList(Collections.singletonList(benchmarkResult));

        DefaultPlannerBenchmark benchmark = new DefaultPlannerBenchmark(plannerBenchmarkResult,
                                                                        solverConfigContext, null,
                                                                        executorService, executorService, benchmarkReport);
        assertThatExceptionOfType(IllegalArgumentException.class).isThrownBy(benchmark::benchmarkingStarted)
                .withMessageContaining("benchmarkDirectory").withMessageContaining("null");
    }

    @Test
    public void exceptionMessagePropagatesWhenThrownDuringWarmUp() {
        SolverConfig solverConfig = PlannerTestUtils.buildSolverConfig(
                TestdataSolution.class, TestdataEntity.class);
        PlannerBenchmarkFactory benchmarkFactory = PlannerBenchmarkFactory.create(
                PlannerBenchmarkConfig.createFromSolverConfig(solverConfig));

        TestdataSolution solution = mock(TestdataSolution.class);

        UnsupportedOperationException exception = new UnsupportedOperationException();
        when(solution.getEntityList()).thenThrow(exception);

        DefaultPlannerBenchmark benchmark = (DefaultPlannerBenchmark) benchmarkFactory.buildPlannerBenchmark(solution);

        assertThatExceptionOfType(PlannerBenchmarkException.class).isThrownBy(benchmark::benchmark).withCause(exception);
    }
}