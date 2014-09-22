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
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.Date;
import java.util.IdentityHashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.SortedMap;
import java.util.TreeMap;

import com.thoughtworks.xstream.annotations.XStreamAlias;
import com.thoughtworks.xstream.annotations.XStreamImplicit;
import com.thoughtworks.xstream.annotations.XStreamOmitField;
import org.drools.core.util.StringUtils;
import org.optaplanner.benchmark.impl.report.BenchmarkReport;
import org.optaplanner.core.api.score.Score;
import org.optaplanner.core.api.solver.Solver;
import org.optaplanner.core.api.solver.SolverFactory;
import org.optaplanner.core.config.util.ConfigUtils;

/**
 * Represents the benchmarks on multiple {@link Solver} configurations on multiple problem instances (data sets).
 */
@XStreamAlias("plannerBenchmarkResult")
public class PlannerBenchmarkResult {

    private String name;
    private Boolean aggregation;
    @XStreamOmitField // Moving or renaming a report directory after creation is allowed
    private File benchmarkReportDirectory;

    // If it is an aggregation, many properties can stay null

    private Integer availableProcessors = null;
    private Long maxMemory = null;
    private String optaPlannerVersion = null;
    private String javaVersion = null;
    private String javaVM = null;
    private String operatingSystem = null;

    private Integer parallelBenchmarkCount = null;
    private Long warmUpTimeMillisSpentLimit = null;

    @XStreamImplicit(itemFieldName = "solverBenchmarkResult")
    private List<SolverBenchmarkResult> solverBenchmarkResultList = null;
    @XStreamImplicit(itemFieldName = "unifiedProblemBenchmarkResult")
    private List<ProblemBenchmarkResult> unifiedProblemBenchmarkResultList = null;

    private Date startingTimestamp = null;
    private Long benchmarkTimeMillisSpent = null;

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

    public Boolean getAggregation() {
        return aggregation;
    }

    public void setAggregation(Boolean aggregation) {
        this.aggregation = aggregation;
    }

    public File getBenchmarkReportDirectory() {
        return benchmarkReportDirectory;
    }

    public void setBenchmarkReportDirectory(File benchmarkReportDirectory) {
        this.benchmarkReportDirectory = benchmarkReportDirectory;
    }

    public Integer getAvailableProcessors() {
        return availableProcessors;
    }

    public Long getMaxMemory() {
        return maxMemory;
    }

    public String getJavaVersion() {
        return javaVersion;
    }

    public String getJavaVM() {
        return javaVM;
    }

    public String getOperatingSystem() {
        return operatingSystem;
    }

    public String getOptaPlannerVersion() {
        return optaPlannerVersion;
    }

    public Integer getParallelBenchmarkCount() {
        return parallelBenchmarkCount;
    }

    public void setParallelBenchmarkCount(Integer parallelBenchmarkCount) {
        this.parallelBenchmarkCount = parallelBenchmarkCount;
    }

    public Long getWarmUpTimeMillisSpentLimit() {
        return warmUpTimeMillisSpentLimit;
    }

    public void setWarmUpTimeMillisSpentLimit(Long warmUpTimeMillisSpentLimit) {
        this.warmUpTimeMillisSpentLimit = warmUpTimeMillisSpentLimit;
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

    public Long getBenchmarkTimeMillisSpent() {
        return benchmarkTimeMillisSpent;
    }

    public void setBenchmarkTimeMillisSpent(Long benchmarkTimeMillisSpent) {
        this.benchmarkTimeMillisSpent = benchmarkTimeMillisSpent;
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
        return parallelBenchmarkCount == null || parallelBenchmarkCount > 1;
    }

    public boolean hasAnyFailure() {
        return failureCount > 0;
    }

    // ************************************************************************
    // Accumulate methods
    // ************************************************************************

    public void initBenchmarkReportDirectory(File benchmarkDirectory) {
        String timestamp = new SimpleDateFormat("yyyy-MM-dd_HHmmss").format(startingTimestamp);
        if (StringUtils.isEmpty(name)) {
            name = timestamp;
        }
        benchmarkReportDirectory = new File(benchmarkDirectory, timestamp);
        boolean benchmarkReportDirectoryAdded = benchmarkReportDirectory.mkdirs();
        if (!benchmarkReportDirectoryAdded) {
            throw new IllegalArgumentException("The benchmarkReportDirectory (" + benchmarkReportDirectory
                    + ") creation failed. It probably already exists.");
        }
        for (ProblemBenchmarkResult problemBenchmarkResult : unifiedProblemBenchmarkResultList) {
            problemBenchmarkResult.makeDirs(benchmarkReportDirectory);
        }
    }

    public void initSystemProperties() {
        availableProcessors = Runtime.getRuntime().availableProcessors();
        maxMemory = Runtime.getRuntime().maxMemory();
        optaPlannerVersion = SolverFactory.class.getPackage().getImplementationVersion();
        if (optaPlannerVersion == null) {
            optaPlannerVersion = "Unjarred development snapshot";
        }
        javaVersion = "Java " + System.getProperty("java.version") + " (" + System.getProperty("java.vendor") + ")";
        javaVM = "Java " + System.getProperty("java.vm.name") + " " + System.getProperty("java.vm.version")
                + " (" + System.getProperty("java.vm.vendor") + ")";
        operatingSystem = System.getProperty("os.name") + " " + System.getProperty("os.arch")
                + " " + System.getProperty("os.version");
    }

    public void accumulateResults(BenchmarkReport benchmarkReport) {
        for (ProblemBenchmarkResult problemBenchmarkResult : unifiedProblemBenchmarkResultList) {
            problemBenchmarkResult.accumulateResults(benchmarkReport);
        }
        for (SolverBenchmarkResult solverBenchmarkResult : solverBenchmarkResultList) {
            solverBenchmarkResult.accumulateResults(benchmarkReport);
        }
        determineTotalsAndAverages();
        determineSolverRanking(benchmarkReport);
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
            failureCount += problemBenchmarkResult.getFailureCount();
        }
        averageProblemScale = problemScaleCount == 0 ? null : totalProblemScale / (long) problemScaleCount;
        Score totalScore = null;
        int solverBenchmarkCount = 0;
        for (SolverBenchmarkResult solverBenchmarkResult : solverBenchmarkResultList) {
            Score score = solverBenchmarkResult.getAverageScore();
            if (score != null) {
                if (totalScore != null && !totalScore.isCompatibleArithmeticArgument(score)) {
                    // Mixing different use cases with different score definitions.
                    totalScore = null;
                    break;
                }
                totalScore = (totalScore == null) ? score : totalScore.add(score);
                solverBenchmarkCount++;
            }
        }
        if (totalScore != null) {
            averageScore = totalScore.divide(solverBenchmarkCount);
        }
    }

    private void determineSolverRanking(BenchmarkReport benchmarkReport) {
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
        if (benchmarkReport.getSolverRankingComparator() != null) {
            Comparator<SolverBenchmarkResult> comparator = Collections.reverseOrder(
                    benchmarkReport.getSolverRankingComparator());
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
        } else if (benchmarkReport.getSolverRankingWeightFactory() != null) {
            SortedMap<Comparable, List<SolverBenchmarkResult>> rankedMap
                    = new TreeMap<Comparable, List<SolverBenchmarkResult>>(Collections.reverseOrder());
            for (SolverBenchmarkResult solverBenchmarkResult : rankableSolverBenchmarkResultList) {
                Comparable rankingWeight = benchmarkReport.getSolverRankingWeightFactory()
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
                    " because solverRankingComparator and solverRankingWeightFactory are null.");
        }
        return sameRankingListList;
    }

    // ************************************************************************
    // Merger methods
    // ************************************************************************

    public static PlannerBenchmarkResult createMergedResult(List<SingleBenchmarkResult> singleBenchmarkResultList) {
        PlannerBenchmarkResult mergedResult = createMergeSingleton(singleBenchmarkResultList);
        Map<SolverBenchmarkResult, SolverBenchmarkResult> solverMergeMap
                = SolverBenchmarkResult.createMergeMap(mergedResult, singleBenchmarkResultList);
        Map<ProblemBenchmarkResult, ProblemBenchmarkResult> problemMergeMap
                = ProblemBenchmarkResult.createMergeMap(mergedResult, singleBenchmarkResultList);
        for (SingleBenchmarkResult singleBenchmarkResult : singleBenchmarkResultList) {
            SolverBenchmarkResult solverBenchmarkResult = solverMergeMap.get(
                    singleBenchmarkResult.getSolverBenchmarkResult());
            ProblemBenchmarkResult problemBenchmarkResult = problemMergeMap.get(
                    singleBenchmarkResult.getProblemBenchmarkResult());
            SingleBenchmarkResult.createMerge(solverBenchmarkResult, problemBenchmarkResult, singleBenchmarkResult);
        }
        return mergedResult;
    }

    protected static PlannerBenchmarkResult createMergeSingleton(List<SingleBenchmarkResult> singleBenchmarkResultList) {
        PlannerBenchmarkResult newResult = null;
        Map<PlannerBenchmarkResult, PlannerBenchmarkResult> mergeMap
                = new IdentityHashMap<PlannerBenchmarkResult, PlannerBenchmarkResult>();
        for (SingleBenchmarkResult singleBenchmarkResult : singleBenchmarkResultList) {
            PlannerBenchmarkResult oldResult = singleBenchmarkResult
                    .getSolverBenchmarkResult().getPlannerBenchmarkResult();
            if (!mergeMap.containsKey(oldResult)) {
                if (newResult == null) {
                    newResult = new PlannerBenchmarkResult();
                    newResult.setAggregation(true);
                    newResult.availableProcessors = oldResult.availableProcessors;
                    newResult.maxMemory = oldResult.maxMemory;
                    newResult.optaPlannerVersion = oldResult.optaPlannerVersion;
                    newResult.javaVersion = oldResult.javaVersion;
                    newResult.javaVM = oldResult.javaVM;
                    newResult.operatingSystem = oldResult.operatingSystem;

                    newResult.parallelBenchmarkCount = oldResult.parallelBenchmarkCount;
                    newResult.warmUpTimeMillisSpentLimit = oldResult.warmUpTimeMillisSpentLimit;
                    newResult.solverBenchmarkResultList = new ArrayList<SolverBenchmarkResult>();
                    newResult.unifiedProblemBenchmarkResultList = new ArrayList<ProblemBenchmarkResult>();
                    newResult.startingTimestamp = null;
                    newResult.benchmarkTimeMillisSpent = null;
                } else {
                    newResult.availableProcessors = ConfigUtils.mergeProperty(
                            newResult.availableProcessors, oldResult.availableProcessors);
                    newResult.maxMemory = ConfigUtils.mergeProperty(
                            newResult.maxMemory, oldResult.maxMemory);
                    newResult.optaPlannerVersion = ConfigUtils.mergeProperty(
                            newResult.optaPlannerVersion, oldResult.optaPlannerVersion);
                    newResult.javaVersion = ConfigUtils.mergeProperty(
                            newResult.javaVersion, oldResult.javaVersion);
                    newResult.javaVM = ConfigUtils.mergeProperty(
                            newResult.javaVM, oldResult.javaVM);
                    newResult.operatingSystem = ConfigUtils.mergeProperty(
                            newResult.operatingSystem, oldResult.operatingSystem);

                    newResult.parallelBenchmarkCount = ConfigUtils.mergeProperty(
                            newResult.parallelBenchmarkCount, oldResult.parallelBenchmarkCount);
                    newResult.warmUpTimeMillisSpentLimit = ConfigUtils.mergeProperty(
                            newResult.warmUpTimeMillisSpentLimit, oldResult.warmUpTimeMillisSpentLimit);
                }
                mergeMap.put(oldResult, newResult);
            }
        }
        return newResult;
    }

    public static PlannerBenchmarkResult createUnmarshallingFailedResult(String benchmarkReportDirectoryName) {
        PlannerBenchmarkResult result = new PlannerBenchmarkResult();
        result.setName("Failed unmarshalling " + benchmarkReportDirectoryName);
        result.setSolverBenchmarkResultList(Collections.<SolverBenchmarkResult>emptyList());
        result.setUnifiedProblemBenchmarkResultList(Collections.<ProblemBenchmarkResult>emptyList());
        return result;
    }

    @Override
    public String toString() {
        return getName();
    }

}
