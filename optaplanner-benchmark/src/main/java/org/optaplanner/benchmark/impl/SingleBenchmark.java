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

package org.optaplanner.benchmark.impl;

import java.util.HashMap;
import java.util.Map;
import java.util.concurrent.Callable;

import org.optaplanner.benchmark.impl.measurement.ScoreDifferencePercentage;
import org.optaplanner.benchmark.impl.statistic.ProblemStatistic;
import org.optaplanner.benchmark.impl.statistic.SingleStatistic;
import org.optaplanner.benchmark.impl.statistic.StatisticType;
import org.optaplanner.core.api.solver.Solver;
import org.optaplanner.core.impl.domain.solution.SolutionDescriptor;
import org.optaplanner.core.api.score.Score;
import org.optaplanner.core.impl.solution.Solution;
import org.optaplanner.core.impl.solver.DefaultSolver;
import org.optaplanner.core.impl.solver.scope.DefaultSolverScope;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * Represents 1 benchmark for 1 {@link Solver} configuration for 1 problem instance (data set).
 */
public class SingleBenchmark implements Callable<SingleBenchmark> {

    protected final transient Logger logger = LoggerFactory.getLogger(getClass());

    private final SolverBenchmark solverBenchmark;
    private final ProblemBenchmark problemBenchmark;

    private Map<StatisticType, SingleStatistic> singleStatisticMap = new HashMap<StatisticType, SingleStatistic>();

    private Integer planningEntityCount = null;
    private Long usedMemoryAfterInputSolution = null;
    private Score score = null;
    // compared to winning singleBenchmark in the same ProblemBenchmark (which might not be the overall favorite)
    private Score winningScoreDifference = null;
    private ScoreDifferencePercentage worstScoreDifferencePercentage = null;
    private long timeMillisSpend = -1L;
    private long calculateCount = -1L;
    // Ranking starts from 0
    private Integer ranking = null;

    private Boolean succeeded = null;
    private Throwable failureThrowable = null;

    public SingleBenchmark(SolverBenchmark solverBenchmark, ProblemBenchmark problemBenchmark) {
        this.solverBenchmark = solverBenchmark;
        this.problemBenchmark = problemBenchmark;
    }

    public SolverBenchmark getSolverBenchmark() {
        return solverBenchmark;
    }

    public ProblemBenchmark getProblemBenchmark() {
        return problemBenchmark;
    }

    public Integer getPlanningEntityCount() {
        return planningEntityCount;
    }

    /**
     * @return null if {@link DefaultPlannerBenchmark#hasMultipleParallelBenchmarks()} return true
     */
    public Long getUsedMemoryAfterInputSolution() {
        return usedMemoryAfterInputSolution;
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

    public ScoreDifferencePercentage getWorstScoreDifferencePercentage() {
        return worstScoreDifferencePercentage;
    }

    public void setWorstScoreDifferencePercentage(ScoreDifferencePercentage worstScoreDifferencePercentage) {
        this.worstScoreDifferencePercentage = worstScoreDifferencePercentage;
    }

    public long getTimeMillisSpend() {
        return timeMillisSpend;
    }

    public long getCalculateCount() {
        return calculateCount;
    }

    public Integer getRanking() {
        return ranking;
    }

    public void setRanking(Integer ranking) {
        this.ranking = ranking;
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
        Runtime runtime = Runtime.getRuntime();
        Solution inputSolution = problemBenchmark.readPlanningProblem();
        if (!problemBenchmark.getPlannerBenchmark().hasMultipleParallelBenchmarks()) {
            runtime.gc();
            usedMemoryAfterInputSolution = runtime.totalMemory() - runtime.freeMemory();
        }
        logger.trace("Benchmark inputSolution has been read for singleBenchmark ({}_{}).",
                problemBenchmark.getName(), solverBenchmark.getName() );

        // Intentionally create a fresh solver for every SingleBenchmark to reset Random, tabu lists, ...
        Solver solver = solverBenchmark.getSolverConfig().buildSolver();
        for (ProblemStatistic problemStatistic : problemBenchmark.getProblemStatisticList()) {
            SingleStatistic singleStatistic = problemStatistic.createSingleStatistic();
            singleStatistic.open(solver);
            singleStatisticMap.put(problemStatistic.getProblemStatisticType(), singleStatistic);
        }

        solver.setPlanningProblem(inputSolution);
        solver.solve();
        Solution outputSolution = solver.getBestSolution();

        timeMillisSpend = solver.getTimeMillisSpend();
        DefaultSolverScope solverScope = ((DefaultSolver) solver).getSolverScope();
        calculateCount = solverScope.getCalculateCount();
        score = outputSolution.getScore();
        SolutionDescriptor solutionDescriptor = ((DefaultSolver) solver).getSolutionDescriptor();
        planningEntityCount = solutionDescriptor.getPlanningEntityCount(outputSolution);
        problemBenchmark.registerProblemScale(solutionDescriptor.getProblemScale(outputSolution));

        for (SingleStatistic singleStatistic : singleStatisticMap.values()) {
            singleStatistic.close(solver);
        }
        problemBenchmark.writeOutputSolution(this, outputSolution);
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

    public boolean isWinner() {
        return ranking != null && ranking.intValue() == 0;
    }

    public SingleStatistic getSingleStatistic(StatisticType statisticType) {
        return singleStatisticMap.get(statisticType);
    }

}
