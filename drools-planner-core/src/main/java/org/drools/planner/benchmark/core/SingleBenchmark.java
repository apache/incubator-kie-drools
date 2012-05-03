/*
 * Copyright 2010 JBoss Inc
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

package org.drools.planner.benchmark.core;

import java.util.concurrent.Callable;

import org.drools.planner.benchmark.core.statistic.ProblemStatistic;
import org.drools.planner.core.Solver;
import org.drools.planner.core.domain.solution.SolutionDescriptor;
import org.drools.planner.core.score.Score;
import org.drools.planner.core.solution.Solution;
import org.drools.planner.core.solver.DefaultSolver;
import org.drools.planner.core.solver.DefaultSolverScope;

/**
 * Represents 1 benchmark for 1 {@link Solver} configuration for 1 problem instance (data set).
 */
public class SingleBenchmark implements Callable<SingleBenchmark> {

    private SolverBenchmark solverBenchmark = null;
    private ProblemBenchmark problemBenchmark = null;

    private int planningEntityCount = -1;
    private long problemScale = -1;
    private Score score = null;
    // compared to winning singleBenchmark (which might not be the overall winner)
    private Score winningScoreDifference = null;
    private long timeMillisSpend = -1L;
    private long calculateCount = -1L;

    private Boolean succeeded = null;
    private Throwable failureThrowable = null;

    public SolverBenchmark getSolverBenchmark() {
        return solverBenchmark;
    }

    public void setSolverBenchmark(SolverBenchmark solverBenchmark) {
        this.solverBenchmark = solverBenchmark;
    }

    public ProblemBenchmark getProblemBenchmark() {
        return problemBenchmark;
    }

    public void setProblemBenchmark(ProblemBenchmark problemBenchmark) {
        this.problemBenchmark = problemBenchmark;
    }

    public int getPlanningEntityCount() {
        return planningEntityCount;
    }

    public void setPlanningEntityCount(int planningEntityCount) {
        this.planningEntityCount = planningEntityCount;
    }

    public long getProblemScale() {
        return problemScale;
    }

    public void setProblemScale(long problemScale) {
        this.problemScale = problemScale;
    }

    public Score getScore() {
        return score;
    }

    public void setScore(Score score) {
        this.score = score;
    }

    public Score getWinningScoreDifference() {
        return winningScoreDifference;
    }

    public void setWinningScoreDifference(Score winningScoreDifference) {
        this.winningScoreDifference = winningScoreDifference;
    }

    public long getTimeMillisSpend() {
        return timeMillisSpend;
    }

    public void setTimeMillisSpend(long timeMillisSpend) {
        this.timeMillisSpend = timeMillisSpend;
    }

    public long getCalculateCount() {
        return calculateCount;
    }

    public void setCalculateCount(long calculateCount) {
        this.calculateCount = calculateCount;
    }

    public Boolean getSucceeded() {
        return succeeded;
    }

    public void setSucceeded(Boolean succeeded) {
        this.succeeded = succeeded;
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

    public String getName() {
        return problemBenchmark.getName() + "_" + solverBenchmark.getName();
    }

    public SingleBenchmark call() {
        // Intentionally create a fresh solver for every SingleBenchmark to reset Random, tabu lists, ...
        Solver solver = solverBenchmark.getSolverConfig().buildSolver();
        for (ProblemStatistic statistic : problemBenchmark.getProblemStatisticList()) {
            statistic.addListener(solver, solverBenchmark.getName());
        }

        solver.setPlanningProblem(problemBenchmark.readPlanningProblem());
        solver.solve();
        Solution outputSolution = solver.getBestSolution();

        timeMillisSpend = solver.getTimeMillisSpend();
        DefaultSolverScope solverScope = ((DefaultSolver) solver).getSolverScope();
        calculateCount = solverScope.getCalculateCount();
        score = outputSolution.getScore();
        SolutionDescriptor solutionDescriptor = ((DefaultSolver) solver).getSolutionDescriptor();
        planningEntityCount = solutionDescriptor.getPlanningEntityCount(outputSolution);
        problemScale = solutionDescriptor.getProblemScale(outputSolution);
        for (ProblemStatistic statistic : problemBenchmark.getProblemStatisticList()) {
            statistic.removeListener(solver, solverBenchmark.getName());
        }
        problemBenchmark.writeSolution(this, outputSolution);
        return this;
    }

    public boolean isSuccess() {
        return succeeded != null && succeeded.booleanValue();
    }

    public boolean isFailure() {
        return succeeded != null && !succeeded.booleanValue();
    }

    public Long getAverageCalculateCountPerSecond() {
        long timeMillisSpend = this.timeMillisSpend;
        if (timeMillisSpend == 0L) {
            // Avoid divide by zero exception on a fast CPU
            timeMillisSpend = 1L;
        }
        return calculateCount * 1000L / timeMillisSpend;
    }

}
