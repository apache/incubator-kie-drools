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

import org.optaplanner.benchmark.impl.statistic.ProblemStatistic;
import org.optaplanner.benchmark.impl.statistic.SingleStatistic;
import org.optaplanner.core.api.solver.Solver;
import org.optaplanner.core.impl.domain.solution.descriptor.SolutionDescriptor;
import org.optaplanner.core.impl.solution.Solution;
import org.optaplanner.core.impl.solver.DefaultSolver;
import org.optaplanner.core.impl.solver.scope.DefaultSolverScope;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class SingleBenchmarkRunner implements Callable<SingleBenchmarkRunner> {

    protected final transient Logger logger = LoggerFactory.getLogger(getClass());

    private final SingleBenchmark singleBenchmark;

    private Throwable failureThrowable = null;

    public SingleBenchmarkRunner(SingleBenchmark singleBenchmark) {
        this.singleBenchmark = singleBenchmark;
    }

    public SingleBenchmark getSingleBenchmark() {
        return singleBenchmark;
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
        Runtime runtime = Runtime.getRuntime();
        ProblemBenchmark problemBenchmark = singleBenchmark.getProblemBenchmark();
        SolverBenchmark solverBenchmark = singleBenchmark.getSolverBenchmark();
        Solution inputSolution = problemBenchmark.readPlanningProblem();
        if (!problemBenchmark.getPlannerBenchmark().hasMultipleParallelBenchmarks()) {
            runtime.gc();
            singleBenchmark.setUsedMemoryAfterInputSolution(runtime.totalMemory() - runtime.freeMemory());
        }
        logger.trace("Benchmark inputSolution has been read for singleBenchmark ({}_{}).",
                problemBenchmark.getName(), solverBenchmark.getName() );

        // Intentionally create a fresh solver for every SingleBenchmark to reset Random, tabu lists, ...
        Solver solver = solverBenchmark.getSolverConfig().buildSolver();

        for (SingleStatistic singleStatistic : singleBenchmark.getSingleStatisticMap().values()) {
            singleStatistic.open(solver);
        }

        solver.setPlanningProblem(inputSolution);
        solver.solve();
        long timeMillisSpend = solver.getTimeMillisSpend();
        Solution outputSolution = solver.getBestSolution();

        SolutionDescriptor solutionDescriptor = ((DefaultSolver) solver).getSolutionDescriptor();
        singleBenchmark.setPlanningEntityCount(solutionDescriptor.getEntityCount(outputSolution));
        problemBenchmark.registerProblemScale(solutionDescriptor.getProblemScale(outputSolution));
        singleBenchmark.setScore(outputSolution.getScore());
        singleBenchmark.setTimeMillisSpend(timeMillisSpend);
        DefaultSolverScope solverScope = ((DefaultSolver) solver).getSolverScope();
        singleBenchmark.setCalculateCount(solverScope.getCalculateCount());

        for (SingleStatistic singleStatistic : singleBenchmark.getSingleStatisticMap().values()) {
            singleStatistic.close(solver);
            singleStatistic.writeCsvStatisticFile();
        }
        problemBenchmark.writeOutputSolution(singleBenchmark, outputSolution);
        return this;
    }

    public String getName() {
        return singleBenchmark.getName();
    }

    @Override
    public String toString() {
        return singleBenchmark.toString();
    }

}
