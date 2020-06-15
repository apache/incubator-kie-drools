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

package org.optaplanner.benchmark.impl;

import static org.assertj.core.api.Assertions.assertThatExceptionOfType;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

import java.io.File;
import java.util.Collections;
import java.util.concurrent.ExecutorService;

import org.junit.jupiter.api.Test;
import org.optaplanner.benchmark.api.PlannerBenchmarkException;
import org.optaplanner.benchmark.api.PlannerBenchmarkFactory;
import org.optaplanner.benchmark.config.PlannerBenchmarkConfig;
import org.optaplanner.benchmark.impl.report.BenchmarkReport;
import org.optaplanner.benchmark.impl.result.PlannerBenchmarkResult;
import org.optaplanner.benchmark.impl.result.SolverBenchmarkResult;
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
        File benchmarkDirectory = mock(File.class);
        ExecutorService executorService = mock(ExecutorService.class);
        BenchmarkReport benchmarkReport = mock(BenchmarkReport.class);

        // solverBenchmarkResultList is empty when instantiated by default constructor
        PlannerBenchmarkResult benchmarkResult = new PlannerBenchmarkResult();

        DefaultPlannerBenchmark benchmark = new DefaultPlannerBenchmark(benchmarkResult,
                benchmarkDirectory, executorService,
                executorService, benchmarkReport);

        assertThatExceptionOfType(IllegalArgumentException.class).isThrownBy(benchmark::benchmarkingStarted)
                .withMessageContaining("solverBenchmarkResultList").withMessageContaining("empty");
    }

    @Test
    public void benchmarkDirectoryIsNull() {
        ExecutorService executorService = mock(ExecutorService.class);
        BenchmarkReport benchmarkReport = mock(BenchmarkReport.class);
        SolverBenchmarkResult benchmarkResult = mock(SolverBenchmarkResult.class);

        PlannerBenchmarkResult plannerBenchmarkResult = new PlannerBenchmarkResult();
        plannerBenchmarkResult.setSolverBenchmarkResultList(Collections.singletonList(benchmarkResult));

        DefaultPlannerBenchmark benchmark = new DefaultPlannerBenchmark(plannerBenchmarkResult, null,
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
