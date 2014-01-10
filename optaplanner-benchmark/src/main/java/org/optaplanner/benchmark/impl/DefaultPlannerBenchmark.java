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
public class DefaultPlannerBenchmark {

    private String name = null;

    private int parallelBenchmarkCount = -1;
    private long warmUpTimeMillisSpend = 0L;

    private List<SolverBenchmark> solverBenchmarkList = null;
    private List<ProblemBenchmark> unifiedProblemBenchmarkList = null;

    private Date startingTimestamp;
    private Long benchmarkTimeMillisSpend;

    // ************************************************************************
    // Report accumulates
    // ************************************************************************

    private Integer failureCount = null;
    private Long averageProblemScale = null;
    private Score averageScore = null;
    private SolverBenchmark favoriteSolverBenchmark = null;

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

    public List<SolverBenchmark> getSolverBenchmarkList() {
        return solverBenchmarkList;
    }

    public void setSolverBenchmarkList(List<SolverBenchmark> solverBenchmarkList) {
        this.solverBenchmarkList = solverBenchmarkList;
    }

    public List<ProblemBenchmark> getUnifiedProblemBenchmarkList() {
        return unifiedProblemBenchmarkList;
    }

    public void setUnifiedProblemBenchmarkList(List<ProblemBenchmark> unifiedProblemBenchmarkList) {
        this.unifiedProblemBenchmarkList = unifiedProblemBenchmarkList;
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

    public SolverBenchmark getFavoriteSolverBenchmark() {
        return favoriteSolverBenchmark;
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
        for (ProblemBenchmark problemBenchmark : unifiedProblemBenchmarkList) {
            problemBenchmark.accumulateResults(benchmarkReport);
        }
        for (SolverBenchmark solverBenchmark : solverBenchmarkList) {
            solverBenchmark.accumulateResults(benchmarkReport);
        }
        determineTotalsAndAverages();
        determineSolverBenchmarkRanking(benchmarkReport);
    }

    private void determineTotalsAndAverages() {
        failureCount = 0;
        long totalProblemScale = 0L;
        int problemScaleCount = 0;
        for (ProblemBenchmark problemBenchmark : unifiedProblemBenchmarkList) {
            Long problemScale = problemBenchmark.getProblemScale();
            if (problemScale != null && problemScale >= 0L) {
                totalProblemScale += problemScale;
                problemScaleCount++;
            }
            failureCount +=  problemBenchmark.getFailureCount();
        }
        averageProblemScale = problemScaleCount == 0 ? null : totalProblemScale / (long) problemScaleCount;
        Score totalScore = null;
        int solverBenchmarkCount = 0;
        for (SolverBenchmark solverBenchmark : solverBenchmarkList) {
            Score score = solverBenchmark.getAverageScore();
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
        List<SolverBenchmark> rankableSolverBenchmarkList = new ArrayList<SolverBenchmark>(solverBenchmarkList);
        // Do not rank a SolverBenchmark that has a failure
        for (Iterator<SolverBenchmark> it = rankableSolverBenchmarkList.iterator(); it.hasNext(); ) {
            SolverBenchmark solverBenchmark = it.next();
            if (solverBenchmark.hasAnyFailure()) {
                it.remove();
            }
        }
        List<List<SolverBenchmark>> sameRankingListList = createSameRankingListList(
                benchmarkReport, rankableSolverBenchmarkList);
        int ranking = 0;
        for (List<SolverBenchmark> sameRankingList : sameRankingListList) {
            for (SolverBenchmark solverBenchmark : sameRankingList) {
                solverBenchmark.setRanking(ranking);
            }
            ranking += sameRankingList.size();
        }
        favoriteSolverBenchmark = sameRankingListList.isEmpty() ? null
                : sameRankingListList.get(0).get(0);
    }

    private List<List<SolverBenchmark>> createSameRankingListList(
            BenchmarkReport benchmarkReport, List<SolverBenchmark> rankableSolverBenchmarkList) {
        List<List<SolverBenchmark>> sameRankingListList = new ArrayList<List<SolverBenchmark>>(
                rankableSolverBenchmarkList.size());
        if (benchmarkReport.getSolverBenchmarkRankingComparator() != null) {
            Comparator<SolverBenchmark> comparator = Collections.reverseOrder(
                    benchmarkReport.getSolverBenchmarkRankingComparator());
            Collections.sort(rankableSolverBenchmarkList, comparator);
            List<SolverBenchmark> sameRankingList = null;
            SolverBenchmark previousSolverBenchmark = null;
            for (SolverBenchmark solverBenchmark : rankableSolverBenchmarkList) {
                if (previousSolverBenchmark == null
                        || comparator.compare(previousSolverBenchmark, solverBenchmark) != 0) {
                    // New rank
                    sameRankingList = new ArrayList<SolverBenchmark>();
                    sameRankingListList.add(sameRankingList);
                }
                sameRankingList.add(solverBenchmark);
                previousSolverBenchmark = solverBenchmark;
            }
        } else if (benchmarkReport.getSolverBenchmarkRankingWeightFactory() != null) {
            SortedMap<Comparable, List<SolverBenchmark>> rankedMap
                    = new TreeMap<Comparable, List<SolverBenchmark>>(new ReverseComparator());
            for (SolverBenchmark solverBenchmark : rankableSolverBenchmarkList) {
                Comparable rankingWeight = benchmarkReport.getSolverBenchmarkRankingWeightFactory()
                        .createRankingWeight(rankableSolverBenchmarkList, solverBenchmark);
                List<SolverBenchmark> sameRankingList = rankedMap.get(rankingWeight);
                if (sameRankingList == null) {
                    sameRankingList = new ArrayList<SolverBenchmark>();
                    rankedMap.put(rankingWeight, sameRankingList);
                }
                sameRankingList.add(solverBenchmark);
            }
            for (Map.Entry<Comparable, List<SolverBenchmark>> entry : rankedMap.entrySet()) {
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
