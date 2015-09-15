/*
 * Copyright 2015 JBoss Inc
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *       http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package org.optaplanner.benchmark.impl.result;

import java.io.File;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import com.thoughtworks.xstream.annotations.XStreamAlias;
import com.thoughtworks.xstream.annotations.XStreamOmitField;
import org.optaplanner.benchmark.impl.measurement.ScoreDifferencePercentage;
import org.optaplanner.benchmark.impl.report.BenchmarkReport;
import org.optaplanner.benchmark.impl.statistic.ProblemStatistic;
import org.optaplanner.benchmark.impl.statistic.PureSingleStatistic;
import org.optaplanner.benchmark.impl.statistic.SingleStatistic;
import org.optaplanner.benchmark.impl.statistic.StatisticType;
import org.optaplanner.core.api.score.FeasibilityScore;
import org.optaplanner.core.api.score.Score;
import org.optaplanner.core.api.solver.Solver;
import org.optaplanner.core.impl.score.ScoreUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * Represents 1 benchmark run for 1 Single Benchmark configuration for 1 {@link Solver} configuration for 1 problem
 * instance (data set).
 */
@XStreamAlias("subSingleBenchmarkResult")
public class SubSingleBenchmarkResult implements SolverProblemBenchmarkResult {

    protected static final transient Logger logger = LoggerFactory.getLogger(SubSingleBenchmarkResult.class);

    @XStreamOmitField
    private SingleBenchmarkResult singleBenchmarkResult;

    private int subSingleBenchmarkIndex = -1;

    // @XStreamImplicit() // TODO FIXME
    private List<PureSingleStatistic> subPureSingleStatisticList = null;

    @XStreamOmitField // Lazily restored when read through ProblemStatistic and CSV files
    private Map<StatisticType, SingleStatistic> effectiveSingleStatisticMap;

    private Long usedMemoryAfterInputSolution = null;

    private Boolean succeeded = null;
    private Integer uninitializedVariableCount = null;
    private Score score = null;
    private long timeMillisSpent = -1L;
    private long calculateCount = -1L;

    // ************************************************************************
    // Report accumulates
    // ************************************************************************

    // Compared to winningSubSingleBenchmarkResult in the same SubSingleBenchmarkResult (which might not be the overall favorite)
    private Score winningScoreDifference = null;
    private ScoreDifferencePercentage worstScoreDifferencePercentage = null;

    // Ranking starts from 0
    private Integer ranking = null;

    public SubSingleBenchmarkResult(SingleBenchmarkResult singleBenchmarkResult, int subSingleBenchmarkIndex) {
        this.singleBenchmarkResult = singleBenchmarkResult;
        this.subSingleBenchmarkIndex = subSingleBenchmarkIndex;
    }

    public List<PureSingleStatistic> getSubPureSingleStatisticList() {
        return subPureSingleStatisticList;
    }

    public void setSubPureSingleStatisticList(List<PureSingleStatistic> subPureSingleStatisticList) {
        this.subPureSingleStatisticList = subPureSingleStatisticList;
    }

    public void initSubSingleStatisticMap() {
        List<ProblemStatistic> problemStatisticList = singleBenchmarkResult.getProblemBenchmarkResult().getProblemStatisticList();
        effectiveSingleStatisticMap = new HashMap<StatisticType, SingleStatistic>(
                problemStatisticList.size() + subPureSingleStatisticList.size());
        for (ProblemStatistic problemStatistic : problemStatisticList) {
            SingleStatistic singleStatistic = problemStatistic.createSingleStatistic(this);
            effectiveSingleStatisticMap.put(singleStatistic.getStatisticType(), singleStatistic);
        }
        for (PureSingleStatistic pureSingleStatistic : subPureSingleStatisticList) {
            effectiveSingleStatisticMap.put(pureSingleStatistic.getStatisticType(), pureSingleStatistic.getStatisticType().buildPureSingleStatistic(this));
        }
    }

    public SingleBenchmarkResult getSingleBenchmarkResult() {
        return singleBenchmarkResult;
    }

    public void setSingleBenchmarkResult(SingleBenchmarkResult singleBenchmarkResult) {
        this.singleBenchmarkResult = singleBenchmarkResult;
    }

    public int getSubSingleBenchmarkIndex() {
        return subSingleBenchmarkIndex;
    }

    public void setSubSingleBenchmarkIndex(int subSingleBenchmarkIndex) {
        this.subSingleBenchmarkIndex = subSingleBenchmarkIndex;
    }

    public Map<StatisticType, SingleStatistic> getEffectiveSingleStatisticMap() {
        return effectiveSingleStatisticMap;
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

    public Integer getUninitializedVariableCount() {
        return uninitializedVariableCount;
    }

    public void setUninitializedVariableCount(Integer uninitializedVariableCount) {
        this.uninitializedVariableCount = uninitializedVariableCount;
    }

    @Override
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

    /**
     * @return never null, filename safe
     */
    public String getName() {
        return singleBenchmarkResult.getName() + "_" + subSingleBenchmarkIndex;
    }

    @Override
    public boolean isSuccess() {
        return succeeded != null && succeeded.booleanValue();
    }

    public boolean isInitialized() {
        return uninitializedVariableCount != null && uninitializedVariableCount == 0;
    }

    @Override
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
        return effectiveSingleStatisticMap.get(statisticType);
    }

    public String getScoreWithUninitializedPrefix() {
        return ScoreUtils.getScoreWithUninitializedPrefix(uninitializedVariableCount, score);
    }

    @Override
    public SolverBenchmarkResult getSolverBenchmarkResult() {
        return singleBenchmarkResult.getSolverBenchmarkResult();
    }

    @Override
    public ProblemBenchmarkResult getProblemBenchmarkResult() {
        return singleBenchmarkResult.getProblemBenchmarkResult();
    }

    // ************************************************************************
    // Accumulate methods
    // ************************************************************************

    public String getReportDirectoryPath() {
        return singleBenchmarkResult.getReportDirectoryPath() + "_" + subSingleBenchmarkIndex;
    }

    public File getReportDirectory() {
        return new File(singleBenchmarkResult.getReportDirectory(), getReportDirectoryPath());
    }

    public void makeDirs() {
        File subSingleReportDirectory = getReportDirectory();
        subSingleReportDirectory.mkdirs();
    }

    public void accumulateResults(BenchmarkReport benchmarkReport) {

    }

    // ************************************************************************
    // Merger methods
    // ************************************************************************


    protected static SubSingleBenchmarkResult createMerge(SingleBenchmarkResult singleBenchmarkResult, SubSingleBenchmarkResult oldResult, int subSingleBenchmarkIndex) {
        SubSingleBenchmarkResult newResult = new SubSingleBenchmarkResult(singleBenchmarkResult, subSingleBenchmarkIndex);
        newResult.subPureSingleStatisticList = new ArrayList<PureSingleStatistic>(oldResult.subPureSingleStatisticList.size());
        for (PureSingleStatistic oldSingleStatistic : oldResult.subPureSingleStatisticList) {
            newResult.subPureSingleStatisticList.add(
                    oldSingleStatistic.getStatisticType().buildPureSingleStatistic(newResult));
        }

        newResult.initSubSingleStatisticMap();
        for (SingleStatistic singleStatistic : newResult.effectiveSingleStatisticMap.values()) {
            SingleStatistic oldSingleStatistic = oldResult.getSingleStatistic(singleStatistic.getStatisticType());
            if (!oldSingleStatistic.getCsvFile().exists()) {
                if (oldResult.isFailure()) {
                    singleStatistic.initPointList();
                    logger.debug("Old result ( {} ) is a failure, skipping merge of it's single statistic ( {} ).",
                            oldResult, oldSingleStatistic);
                    continue;
                } else {
                    throw new IllegalStateException("Could not find old result's ( " + oldResult
                            + " ) single statistic's ( " + oldSingleStatistic + " ) CSV file.");
                }
            }
            oldSingleStatistic.unhibernatePointList();
            singleStatistic.setPointList(oldSingleStatistic.getPointList());
            oldSingleStatistic.hibernatePointList();
        }
        // Skip oldResult.reportDirectory
        // Skip oldResult.usedMemoryAfterInputSolution
        newResult.succeeded = oldResult.succeeded;
        newResult.score = oldResult.score;
        newResult.uninitializedVariableCount = oldResult.uninitializedVariableCount;
        newResult.timeMillisSpent = oldResult.timeMillisSpent;
        newResult.calculateCount = oldResult.calculateCount;

        singleBenchmarkResult.getSubSingleBenchmarkResultList().add(newResult);
        return newResult;
    }

    @Override
    public String toString() {
        return getName();
    }

}
