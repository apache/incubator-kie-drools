/*
 * Copyright 2014 JBoss Inc
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

import java.util.concurrent.Callable;

import org.optaplanner.benchmark.impl.result.ProblemBenchmarkResult;
import org.optaplanner.benchmark.impl.result.SingleBenchmarkResult;
import org.optaplanner.benchmark.impl.statistic.SingleStatistic;
import org.optaplanner.core.api.domain.solution.Solution;
import org.optaplanner.core.api.solver.Solver;
import org.optaplanner.core.impl.domain.solution.descriptor.SolutionDescriptor;
import org.optaplanner.core.impl.solver.DefaultSolver;
import org.optaplanner.core.impl.solver.scope.DefaultSolverScope;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.slf4j.MDC;

public class SingleBenchmarkRunner implements Callable<SingleBenchmarkRunner> {

    public static final String NAME_MDC = "singleBenchmark.name";

    protected final transient Logger logger = LoggerFactory.getLogger(getClass());

    private final SingleBenchmarkResult singleBenchmarkResult;

    private Throwable failureThrowable = null;

    public SingleBenchmarkRunner(SingleBenchmarkResult singleBenchmarkResult) {
        this.singleBenchmarkResult = singleBenchmarkResult;
    }

    public SingleBenchmarkResult getSingleBenchmarkResult() {
        return singleBenchmarkResult;
    }

    public Throwable getFailureThrowable() {
        return failureThrowable;
    }

    public void setFailureThrowable(Throwable failureThrowable) {
        this.failureThrowable = failureThrowable;
    }

    // ************************************************************************
    // Benchmark methods
    // ************************************************************************

    public SingleBenchmarkRunner call() {
        MDC.put(NAME_MDC, singleBenchmarkResult.getName());
        Runtime runtime = Runtime.getRuntime();
        ProblemBenchmarkResult problemBenchmarkResult = singleBenchmarkResult.getProblemBenchmarkResult();
        Solution inputSolution = problemBenchmarkResult.readPlanningProblem();
        if (!problemBenchmarkResult.getPlannerBenchmarkResult().hasMultipleParallelBenchmarks()) {
            runtime.gc();
            singleBenchmarkResult.setUsedMemoryAfterInputSolution(runtime.totalMemory() - runtime.freeMemory());
        }
        logger.trace("Benchmark inputSolution has been read for singleBenchmarkResult ({}).",
                singleBenchmarkResult.getName());

        // Intentionally create a fresh solver for every SingleBenchmarkResult to reset Random, tabu lists, ...
        Solver solver = singleBenchmarkResult.getSolverBenchmarkResult().getSolverConfig().buildSolver();

        for (SingleStatistic singleStatistic : singleBenchmarkResult.getEffectiveSingleStatisticMap().values()) {
            singleStatistic.open(solver);
        }

        solver.solve(inputSolution);
        long timeMillisSpent = solver.getTimeMillisSpent();
        Solution outputSolution = solver.getBestSolution();

        DefaultSolverScope solverScope = ((DefaultSolver) solver).getSolverScope();
        SolutionDescriptor solutionDescriptor = solverScope.getSolutionDescriptor();
        problemBenchmarkResult.registerScale(solutionDescriptor.getEntityCount(outputSolution),
                solutionDescriptor.getVariableCount(outputSolution),
                solutionDescriptor.getProblemScale(outputSolution));
        singleBenchmarkResult.setScore(outputSolution.getScore());
        singleBenchmarkResult.setTimeMillisSpent(timeMillisSpent);
        singleBenchmarkResult.setCalculateCount(solverScope.getCalculateCount());

        for (SingleStatistic singleStatistic : singleBenchmarkResult.getEffectiveSingleStatisticMap().values()) {
            singleStatistic.close(solver);
            singleStatistic.writeCsvStatisticFile();
        }
        problemBenchmarkResult.writeOutputSolution(singleBenchmarkResult, outputSolution);
        MDC.remove(NAME_MDC);
        return this;
    }

    public String getName() {
        return singleBenchmarkResult.getName();
    }

    @Override
    public String toString() {
        return singleBenchmarkResult.toString();
    }

}
