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

import java.io.File;
import java.util.HashMap;
import java.util.Map;
import java.util.concurrent.Callable;

import org.optaplanner.benchmark.impl.measurement.ScoreDifferencePercentage;
import org.optaplanner.benchmark.impl.statistic.ProblemStatistic;
import org.optaplanner.benchmark.impl.statistic.SingleStatistic;
import org.optaplanner.benchmark.impl.statistic.StatisticType;
import org.optaplanner.core.api.score.FeasibilityScore;
import org.optaplanner.core.api.score.Score;
import org.optaplanner.core.api.solver.Solver;
import org.optaplanner.core.impl.domain.solution.descriptor.SolutionDescriptor;
import org.optaplanner.core.impl.solution.Solution;
import org.optaplanner.core.impl.solver.DefaultSolver;
import org.optaplanner.core.impl.solver.scope.DefaultSolverScope;
import org.optaplanner.persistence.xstream.XStreamResumeIO;
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

    // compared to winning singleBenchmark in the same ProblemBenchmark (which might not be the overall favorite)
    private Score winningScoreDifference = null;
    private ScoreDifferencePercentage worstScoreDifferencePercentage = null;
    // Ranking starts from 0
    private Integer ranking = null;

    private SingleBenchmarkState singleBenchmarkState;
    private final XStreamResumeIO xStreamResumeIO = new XStreamResumeIO();
    private boolean recovered = false;

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

    public Map<StatisticType, SingleStatistic> getSingleStatisticMap() {
        return singleStatisticMap;
    }

    public Integer getPlanningEntityCount() {
        return singleBenchmarkState.getPlanningEntityCount();
    }

    /**
     * @return null if {@link DefaultPlannerBenchmark#hasMultipleParallelBenchmarks()} return true
     */
    public Long getUsedMemoryAfterInputSolution() {
        return singleBenchmarkState.getUsedMemoryAfterInputSolution();
    }

    public Score getScore() {
        return singleBenchmarkState.getScore();
    }

    public void setScore(Score score) {
        this.singleBenchmarkState.setScore(score);
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
        return singleBenchmarkState.getTimeMillisSpend();
    }

    public long getCalculateCount() {
        return singleBenchmarkState.getCalculateCount();
    }

    public Integer getRanking() {
        return ranking;
    }

    public void setRanking(Integer ranking) {
        this.ranking = ranking;
    }

    public Boolean getSucceeded() {
        return singleBenchmarkState.getSucceeded();
    }

    public void setSucceeded(Boolean succeeded) {
        this.singleBenchmarkState.setSucceeded(succeeded);
    }

    public Throwable getFailureThrowable() {
        return singleBenchmarkState.getFailureThrowable();
    }

    public void setFailureThrowable(Throwable failureThrowable) {
        this.singleBenchmarkState.setFailureThrowable(failureThrowable);
    }

    public SingleBenchmarkState getSingleBenchmarkState() {
        return singleBenchmarkState;
    }

    public void setSingleBenchmarkState(SingleBenchmarkState singleBenchmarkState) {
        this.singleBenchmarkState = singleBenchmarkState;
    }

    public boolean getRecovered() {
        return recovered;
    }

    public void setRecovered(boolean recovered) {
        this.recovered = recovered;
    }

    // ************************************************************************
    // Benchmark methods
    // ************************************************************************

    public String getName() {
        return problemBenchmark.getName() + "_" + solverBenchmark.getName();
    }

    public String getSingleBenchmarkStatisticFilename(StatisticType type) {
        return getName() + "_" + type + ".csv";
    }

    public SingleBenchmark call() {
        if (singleBenchmarkState == null) {
            singleBenchmarkState = new SingleBenchmarkState(getName());
        }
        Runtime runtime = Runtime.getRuntime();
        Solution inputSolution = problemBenchmark.readPlanningProblem();
        if (!problemBenchmark.getPlannerBenchmark().hasMultipleParallelBenchmarks()) {
            runtime.gc();
            singleBenchmarkState.setUsedMemoryAfterInputSolution(runtime.totalMemory() - runtime.freeMemory());
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

        singleBenchmarkState.setTimeMillisSpend(solver.getTimeMillisSpend());
        DefaultSolverScope solverScope = ((DefaultSolver) solver).getSolverScope();
        singleBenchmarkState.setCalculateCount(solverScope.getCalculateCount());
        singleBenchmarkState.setScore(outputSolution.getScore());
        SolutionDescriptor solutionDescriptor = ((DefaultSolver) solver).getSolutionDescriptor();
        singleBenchmarkState.setPlanningEntityCount(solutionDescriptor.getEntityCount(outputSolution));
        singleBenchmarkState.setProblemScale(solutionDescriptor.getProblemScale(outputSolution));

        for (SingleStatistic singleStatistic : singleStatisticMap.values()) {
            singleStatistic.close(solver);
        }

        for (StatisticType type : singleStatisticMap.keySet()) {
            File statisticFile = new File(problemBenchmark.getPlannerBenchmark().getBenchmarkOutputDirectory().getPath(),
                    getSingleBenchmarkStatisticFilename(type));
            singleStatisticMap.get(type).writeCsvStatistic(statisticFile);
        }
        setSucceeded(true);
        xStreamResumeIO.write(getSingleBenchmarkState(),
                new File(problemBenchmark.getPlannerBenchmark().getBenchmarkOutputDirectory().getPath(), getName() + ".xml"));

        problemBenchmark.writeOutputSolution(this, outputSolution);
        return this;
    }

    public boolean isSuccess() {
        return singleBenchmarkState.getSucceeded() != null && singleBenchmarkState.getSucceeded().booleanValue();
    }

    public boolean isFailure() {
        return singleBenchmarkState.getSucceeded() != null && !singleBenchmarkState.getSucceeded().booleanValue();
    }

    public boolean isScoreFeasible() {
        if (getScore() instanceof FeasibilityScore) {
            return ((FeasibilityScore) getScore()).isFeasible();
        } else {
            return true;
        }
    }

    public Long getAverageCalculateCountPerSecond() {
        long timeMillisSpend = getTimeMillisSpend();
        if (timeMillisSpend == 0L) {
            // Avoid divide by zero exception on a fast CPU
            timeMillisSpend = 1L;
        }
        return getCalculateCount() * 1000L / timeMillisSpend;
    }

    public boolean isWinner() {
        return ranking != null && ranking.intValue() == 0;
    }

    public SingleStatistic getSingleStatistic(StatisticType statisticType) {
        return singleStatisticMap.get(statisticType);
    }

    @Override
    public String toString() {
        return getName();
    }

}
