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
import org.optaplanner.benchmark.impl.result.SubSingleBenchmarkResult;
import org.optaplanner.benchmark.impl.statistic.SubSingleStatistic;
import org.optaplanner.core.api.domain.solution.Solution;
import org.optaplanner.core.api.solver.Solver;
import org.optaplanner.core.impl.domain.solution.descriptor.SolutionDescriptor;
import org.optaplanner.core.impl.solver.DefaultSolver;
import org.optaplanner.core.impl.solver.scope.DefaultSolverScope;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.slf4j.MDC;

public class SubSingleBenchmarkRunner implements Callable<SubSingleBenchmarkRunner> {

    public static final String NAME_MDC = "subSingleBenchmark.name";

    protected final transient Logger logger = LoggerFactory.getLogger(getClass());

    private final SubSingleBenchmarkResult subSingleBenchmarkResult;

    private final ClassLoader classLoader;

    private Throwable failureThrowable = null;

    public SubSingleBenchmarkRunner(SubSingleBenchmarkResult subSingleBenchmarkResult) {
        this(subSingleBenchmarkResult, null);
    }

    public SubSingleBenchmarkRunner(SubSingleBenchmarkResult subSingleBenchmarkResult, ClassLoader classLoader) {
        this.subSingleBenchmarkResult = subSingleBenchmarkResult;
        this.classLoader = classLoader != null ? classLoader : getClass().getClassLoader();
    }

    public SubSingleBenchmarkResult getSubSingleBenchmarkResult() {
        return subSingleBenchmarkResult;
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

    @Override
    public SubSingleBenchmarkRunner call() {
        MDC.put(NAME_MDC, subSingleBenchmarkResult.getName());
        Runtime runtime = Runtime.getRuntime();
        ProblemBenchmarkResult problemBenchmarkResult = subSingleBenchmarkResult.getSingleBenchmarkResult().getProblemBenchmarkResult();
        Solution inputSolution = problemBenchmarkResult.readPlanningProblem();
        if (!problemBenchmarkResult.getPlannerBenchmarkResult().hasMultipleParallelBenchmarks()) {
            runtime.gc();
            subSingleBenchmarkResult.setUsedMemoryAfterInputSolution(runtime.totalMemory() - runtime.freeMemory());
        }
        logger.trace("Benchmark inputSolution has been read for subSingleBenchmarkResult ({}).",
                subSingleBenchmarkResult);

        // Intentionally create a fresh solver for every SingleBenchmarkResult to reset Random, tabu lists, ...
        Solver solver = subSingleBenchmarkResult.getSingleBenchmarkResult().getSolverBenchmarkResult().getSolverConfig().buildSolver(getClass().getClassLoader());

        for (SubSingleStatistic subSingleStatistic : subSingleBenchmarkResult.getEffectiveSubSingleStatisticMap().values()) {
            subSingleStatistic.open(solver);
            subSingleStatistic.initPointList();
        }

        solver.solve(inputSolution);
        long timeMillisSpent = solver.getTimeMillisSpent();
        Solution outputSolution = solver.getBestSolution();

        DefaultSolverScope solverScope = ((DefaultSolver) solver).getSolverScope();
        SolutionDescriptor solutionDescriptor = solverScope.getSolutionDescriptor();
        problemBenchmarkResult.registerScale(solutionDescriptor.getEntityCount(outputSolution),
                solutionDescriptor.getGenuineVariableCount(outputSolution),
                solutionDescriptor.getProblemScale(outputSolution));
        subSingleBenchmarkResult.setScore(outputSolution.getScore());
        subSingleBenchmarkResult.setUninitializedVariableCount(solverScope.getBestUninitializedVariableCount());
        subSingleBenchmarkResult.setTimeMillisSpent(timeMillisSpent);
        subSingleBenchmarkResult.setCalculateCount(solverScope.getCalculateCount());

        for (SubSingleStatistic subSingleStatistic : subSingleBenchmarkResult.getEffectiveSubSingleStatisticMap().values()) {
            subSingleStatistic.close(solver);
            subSingleStatistic.hibernatePointList();
        }
        problemBenchmarkResult.writeOutputSolution(subSingleBenchmarkResult, outputSolution);
        MDC.remove(NAME_MDC);
        return this;
    }

    public String getName() {
        return subSingleBenchmarkResult.getName();
    }

    @Override
    public String toString() {
        return subSingleBenchmarkResult.toString();
    }

}
