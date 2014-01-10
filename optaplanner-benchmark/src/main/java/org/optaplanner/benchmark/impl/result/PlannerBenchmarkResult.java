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

import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.Date;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.SortedMap;
import java.util.TreeMap;

import org.apache.commons.collections.comparators.ReverseComparator;
import org.optaplanner.benchmark.impl.report.BenchmarkReport;
import org.optaplanner.core.api.score.Score;
import org.optaplanner.core.api.solver.Solver;

/**
 * Represents the benchmarks on multiple {@link Solver} configurations on multiple problem instances (data sets).
 */
public class PlannerBenchmarkResult {

    private String name = null;

    private int parallelBenchmarkCount = -1;
    private long warmUpTimeMillisSpend = 0L;

    private List<SolverBenchmarkResult> solverBenchmarkResultList = null;
    private List<ProblemBenchmarkResult> unifiedProblemBenchmarkResultList = null;

    private Date startingTimestamp;
    private Long benchmarkTimeMillisSpend;

    // ************************************************************************
    // Report accumulates
    // ************************************************************************

    private Integer failureCount = null;
    private Long averageProblemScale = null;
    private Score averageScore = null;
    private SolverBenchmarkResult favoriteSolverBenchmarkResult = null;

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public int getParallelBenchmarkCount() {
        return parallelBenchmarkCount;
    }

    public void setParallelBenchmarkCount(int parallelBenchmarkCount) {
        this.parallelBenchmarkCount = parallelBenchmarkCount;
    }

    public long getWarmUpTimeMillisSpend() {
        return warmUpTimeMillisSpend;
    }

    public void setWarmUpTimeMillisSpend(long warmUpTimeMillisSpend) {
        this.warmUpTimeMillisSpend = warmUpTimeMillisSpend;
    }

    public List<SolverBenchmarkResult> getSolverBenchmarkResultList() {
        return solverBenchmarkResultList;
    }

    public void setSolverBenchmarkResultList(List<SolverBenchmarkResult> solverBenchmarkResultList) {
        this.solverBenchmarkResultList = solverBenchmarkResultList;
    }

    public List<ProblemBenchmarkResult> getUnifiedProblemBenchmarkResultList() {
        return unifiedProblemBenchmarkResultList;
    }

    public void setUnifiedProblemBenchmarkResultList(List<ProblemBenchmarkResult> unifiedProblemBenchmarkResultList) {
        this.unifiedProblemBenchmarkResultList = unifiedProblemBenchmarkResultList;
    }

    public Date getStartingTimestamp() {
        return startingTimestamp;
    }

    public void setStartingTimestamp(Date startingTimestamp) {
        this.startingTimestamp = startingTimestamp;
    }

    public Long getBenchmarkTimeMillisSpend() {
        return benchmarkTimeMillisSpend;
    }

    public void setBenchmarkTimeMillisSpend(Long benchmarkTimeMillisSpend) {
        this.benchmarkTimeMillisSpend = benchmarkTimeMillisSpend;
    }

    public Integer getFailureCount() {
        return failureCount;
    }

    public Long getAverageProblemScale() {
        return averageProblemScale;
    }

    public Score getAverageScore() {
        return averageScore;
    }

    public SolverBenchmarkResult getFavoriteSolverBenchmarkResult() {
        return favoriteSolverBenchmarkResult;
    }

    // ************************************************************************
    // Smart getters
    // ************************************************************************

    public boolean hasMultipleParallelBenchmarks() {
        return parallelBenchmarkCount > 1;
    }

    public boolean hasAnyFailure() {
        return failureCount > 0;
    }

    // ************************************************************************
    // Accumulate methods
    // ************************************************************************

    public void accumulateResults(BenchmarkReport benchmarkReport) {
        for (ProblemBenchmarkResult problemBenchmarkResult : unifiedProblemBenchmarkResultList) {
            problemBenchmarkResult.accumulateResults(benchmarkReport);
        }
        for (SolverBenchmarkResult solverBenchmarkResult : solverBenchmarkResultList) {
            solverBenchmarkResult.accumulateResults(benchmarkReport);
        }
        determineTotalsAndAverages();
        determineSolverBenchmarkRanking(benchmarkReport);
    }

    private void determineTotalsAndAverages() {
        failureCount = 0;
        long totalProblemScale = 0L;
        int problemScaleCount = 0;
        for (ProblemBenchmarkResult problemBenchmarkResult : unifiedProblemBenchmarkResultList) {
            Long problemScale = problemBenchmarkResult.getProblemScale();
            if (problemScale != null && problemScale >= 0L) {
                totalProblemScale += problemScale;
                problemScaleCount++;
            }
            failureCount +=  problemBenchmarkResult.getFailureCount();
        }
        averageProblemScale = problemScaleCount == 0 ? null : totalProblemScale / (long) problemScaleCount;
        Score totalScore = null;
        int solverBenchmarkCount = 0;
        for (SolverBenchmarkResult solverBenchmarkResult : solverBenchmarkResultList) {
            Score score = solverBenchmarkResult.getAverageScore();
            if (score != null) {
                totalScore = (totalScore == null) ? score : totalScore.add(score);
                solverBenchmarkCount++;
            }
        }
        if (totalScore != null) {
            averageScore = totalScore.divide(solverBenchmarkCount);
        }
    }

    private void determineSolverBenchmarkRanking(BenchmarkReport benchmarkReport) {
        List<SolverBenchmarkResult> rankableSolverBenchmarkResultList = new ArrayList<SolverBenchmarkResult>(solverBenchmarkResultList);
        // Do not rank a SolverBenchmarkResult that has a failure
        for (Iterator<SolverBenchmarkResult> it = rankableSolverBenchmarkResultList.iterator(); it.hasNext(); ) {
            SolverBenchmarkResult solverBenchmarkResult = it.next();
            if (solverBenchmarkResult.hasAnyFailure()) {
                it.remove();
            }
        }
        List<List<SolverBenchmarkResult>> sameRankingListList = createSameRankingListList(
                benchmarkReport, rankableSolverBenchmarkResultList);
        int ranking = 0;
        for (List<SolverBenchmarkResult> sameRankingList : sameRankingListList) {
            for (SolverBenchmarkResult solverBenchmarkResult : sameRankingList) {
                solverBenchmarkResult.setRanking(ranking);
            }
            ranking += sameRankingList.size();
        }
        favoriteSolverBenchmarkResult = sameRankingListList.isEmpty() ? null
                : sameRankingListList.get(0).get(0);
    }

    private List<List<SolverBenchmarkResult>> createSameRankingListList(
            BenchmarkReport benchmarkReport, List<SolverBenchmarkResult> rankableSolverBenchmarkResultList) {
        List<List<SolverBenchmarkResult>> sameRankingListList = new ArrayList<List<SolverBenchmarkResult>>(
                rankableSolverBenchmarkResultList.size());
        if (benchmarkReport.getSolverBenchmarkRankingComparator() != null) {
            Comparator<SolverBenchmarkResult> comparator = Collections.reverseOrder(
                    benchmarkReport.getSolverBenchmarkRankingComparator());
            Collections.sort(rankableSolverBenchmarkResultList, comparator);
            List<SolverBenchmarkResult> sameRankingList = null;
            SolverBenchmarkResult previousSolverBenchmarkResult = null;
            for (SolverBenchmarkResult solverBenchmarkResult : rankableSolverBenchmarkResultList) {
                if (previousSolverBenchmarkResult == null
                        || comparator.compare(previousSolverBenchmarkResult, solverBenchmarkResult) != 0) {
                    // New rank
                    sameRankingList = new ArrayList<SolverBenchmarkResult>();
                    sameRankingListList.add(sameRankingList);
                }
                sameRankingList.add(solverBenchmarkResult);
                previousSolverBenchmarkResult = solverBenchmarkResult;
            }
        } else if (benchmarkReport.getSolverBenchmarkRankingWeightFactory() != null) {
            SortedMap<Comparable, List<SolverBenchmarkResult>> rankedMap
                    = new TreeMap<Comparable, List<SolverBenchmarkResult>>(new ReverseComparator());
            for (SolverBenchmarkResult solverBenchmarkResult : rankableSolverBenchmarkResultList) {
                Comparable rankingWeight = benchmarkReport.getSolverBenchmarkRankingWeightFactory()
                        .createRankingWeight(rankableSolverBenchmarkResultList, solverBenchmarkResult);
                List<SolverBenchmarkResult> sameRankingList = rankedMap.get(rankingWeight);
                if (sameRankingList == null) {
                    sameRankingList = new ArrayList<SolverBenchmarkResult>();
                    rankedMap.put(rankingWeight, sameRankingList);
                }
                sameRankingList.add(solverBenchmarkResult);
            }
            for (Map.Entry<Comparable, List<SolverBenchmarkResult>> entry : rankedMap.entrySet()) {
                sameRankingListList.add(entry.getValue());
            }
        } else {
            throw new IllegalStateException("Ranking is impossible" +
                    " because solverBenchmarkRankingComparator and solverBenchmarkRankingWeightFactory are null.");
        }
        return sameRankingListList;
    }

    @Override
    public String toString() {
        return getName();
    }

}
