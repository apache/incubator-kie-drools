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

package org.optaplanner.benchmark.impl.result;

import java.io.File;
import java.util.HashMap;
import java.util.Map;

import com.thoughtworks.xstream.annotations.XStreamAlias;
import com.thoughtworks.xstream.annotations.XStreamOmitField;
import org.optaplanner.benchmark.impl.measurement.ScoreDifferencePercentage;
import org.optaplanner.benchmark.impl.report.BenchmarkReport;
import org.optaplanner.benchmark.impl.statistic.ProblemStatistic;
import org.optaplanner.benchmark.impl.statistic.SingleStatistic;
import org.optaplanner.benchmark.impl.statistic.StatisticType;
import org.optaplanner.core.api.score.FeasibilityScore;
import org.optaplanner.core.api.score.Score;
import org.optaplanner.core.api.solver.Solver;

/**
 * Represents 1 benchmark for 1 {@link Solver} configuration for 1 problem instance (data set).
 */
@XStreamAlias("singleBenchmarkResult")
public class SingleBenchmarkResult {

    @XStreamOmitField // Bi-directional relationship restored through BenchmarkResultIO
    private SolverBenchmarkResult solverBenchmarkResult;
    @XStreamOmitField // Bi-directional relationship restored through BenchmarkResultIO
    private ProblemBenchmarkResult problemBenchmarkResult;

    @XStreamOmitField // Lazily restored when read through ProblemStatistic and CSV files
    private Map<StatisticType, SingleStatistic> singleStatisticMap;

    private Integer entityCount = null;
    private Long usedMemoryAfterInputSolution = null;

    private Boolean succeeded = null;
    private Score score = null;
    private long timeMillisSpent = -1L;
    private long calculateCount = -1L;

    // ************************************************************************
    // Report accumulates
    // ************************************************************************

    // Compared to winningSingleBenchmarkResult in the same ProblemBenchmarkResult (which might not be the overall favorite)
    private Score winningScoreDifference = null;
    private ScoreDifferencePercentage worstScoreDifferencePercentage = null;

    // Ranking starts from 0
    private Integer ranking = null;

    public SingleBenchmarkResult(SolverBenchmarkResult solverBenchmarkResult, ProblemBenchmarkResult problemBenchmarkResult) {
        this.solverBenchmarkResult = solverBenchmarkResult;
        this.problemBenchmarkResult = problemBenchmarkResult;
    }

    public void initSingleStatisticMap() {
        singleStatisticMap = new HashMap<StatisticType, SingleStatistic>(
                problemBenchmarkResult.getProblemStatisticList().size());
        for (ProblemStatistic problemStatistic : problemBenchmarkResult.getProblemStatisticList()) {
            SingleStatistic singleStatistic = problemStatistic.createSingleStatistic(this);
            singleStatisticMap.put(problemStatistic.getProblemStatisticType(), singleStatistic);
        }
    }

    public SolverBenchmarkResult getSolverBenchmarkResult() {
        return solverBenchmarkResult;
    }

    public void setSolverBenchmarkResult(SolverBenchmarkResult solverBenchmarkResult) {
        this.solverBenchmarkResult = solverBenchmarkResult;
    }

    public ProblemBenchmarkResult getProblemBenchmarkResult() {
        return problemBenchmarkResult;
    }

    public void setProblemBenchmarkResult(ProblemBenchmarkResult problemBenchmarkResult) {
        this.problemBenchmarkResult = problemBenchmarkResult;
    }

    public Map<StatisticType, SingleStatistic> getSingleStatisticMap() {
        return singleStatisticMap;
    }

    public Integer getEntityCount() {
        return entityCount;
    }

    public void setEntityCount(Integer entityCount) {
        this.entityCount = entityCount;
    }

    /**
     * @return null if {@link PlannerBenchmarkResult#hasMultipleParallelBenchmarks()} return true
     */
    public Long getUsedMemoryAfterInputSolution() {
        return usedMemoryAfterInputSolution;
    }

    public void setUsedMemoryAfterInputSolution(Long usedMemoryAfterInputSolution) {
        this.usedMemoryAfterInputSolution = usedMemoryAfterInputSolution;
    }

    public Boolean getSucceeded() {
        return succeeded;
    }

    public void setSucceeded(Boolean succeeded) {
        this.succeeded = succeeded;
    }

    public Score getScore() {
        return score;
    }

    public void setScore(Score score) {
        this.score = score;
    }

    public long getTimeMillisSpent() {
        return timeMillisSpent;
    }

    public void setTimeMillisSpent(long timeMillisSpent) {
        this.timeMillisSpent = timeMillisSpent;
    }

    public long getCalculateCount() {
        return calculateCount;
    }

    public void setCalculateCount(long calculateCount) {
        this.calculateCount = calculateCount;
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

    public Integer getRanking() {
        return ranking;
    }

    public void setRanking(Integer ranking) {
        this.ranking = ranking;
    }

    // ************************************************************************
    // Smart getters
    // ************************************************************************

    public String getName() {
        return problemBenchmarkResult.getName() + "_" + solverBenchmarkResult.getName();
    }

    public File getBenchmarkReportDirectory() {
        return problemBenchmarkResult.getBenchmarkReportDirectory();
    }

    public boolean isSuccess() {
        return succeeded != null && succeeded.booleanValue();
    }

    public boolean isFailure() {
        return succeeded != null && !succeeded.booleanValue();
    }

    public boolean isScoreFeasible() {
        if (score instanceof FeasibilityScore) {
            return ((FeasibilityScore) score).isFeasible();
        } else {
            return true;
        }
    }

    public Long getAverageCalculateCountPerSecond() {
        long timeMillisSpent = this.timeMillisSpent;
        if (timeMillisSpent == 0L) {
            // Avoid divide by zero exception on a fast CPU
            timeMillisSpent = 1L;
        }
        return calculateCount * 1000L / timeMillisSpent;
    }

    public boolean isWinner() {
        return ranking != null && ranking.intValue() == 0;
    }

    public SingleStatistic getSingleStatistic(StatisticType statisticType) {
        return singleStatisticMap.get(statisticType);
    }

    // ************************************************************************
    // Accumulate methods
    // ************************************************************************

    public String getSingleReportDirectoryPath() {
        return problemBenchmarkResult.getProblemReportDirectoryPath() + "/" + solverBenchmarkResult.getName();
    }

    public File getSingleReportDirectory() {
        return new File(getBenchmarkReportDirectory(), getSingleReportDirectoryPath());
    }

    public void makeDirs(File problemReportDirectory) {
        File singleReportDirectory = getSingleReportDirectory();
        singleReportDirectory.mkdirs();
    }

    public void accumulateResults(BenchmarkReport benchmarkReport) {

    }

    // ************************************************************************
    // Merger methods
    // ************************************************************************


    protected static SingleBenchmarkResult createMerge(SolverBenchmarkResult solverBenchmarkResult,
            ProblemBenchmarkResult problemBenchmarkResult, SingleBenchmarkResult oldResult) {
        SingleBenchmarkResult newResult = new SingleBenchmarkResult(solverBenchmarkResult, problemBenchmarkResult);

        newResult.initSingleStatisticMap();
        for (SingleStatistic singleStatistic : newResult.singleStatisticMap.values()) {
            singleStatistic.setPointList(oldResult.getSingleStatistic(singleStatistic.getStatisticType()).getPointList());
        }
        // Skip oldResult.reportDirectory
        newResult.entityCount = oldResult.entityCount;
        // Skip oldResult.usedMemoryAfterInputSolution
        newResult.succeeded = oldResult.succeeded;
        newResult.score = oldResult.score;
        newResult.timeMillisSpent = oldResult.timeMillisSpent;
        newResult.calculateCount = oldResult.calculateCount;

        solverBenchmarkResult.getSingleBenchmarkResultList().add(newResult);
        problemBenchmarkResult.getSingleBenchmarkResultList().add(newResult);
        return newResult;
    }

    @Override
    public String toString() {
        return getName();
    }

}
