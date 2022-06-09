package org.optaplanner.benchmark.impl.result;

import java.io.File;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;
import java.util.Comparator;
import java.util.HashMap;
import java.util.IdentityHashMap;
import java.util.Iterator;
import java.util.LinkedHashSet;
import java.util.List;
import java.util.Map;
import java.util.Objects;
import java.util.Set;

import javax.xml.bind.annotation.XmlElement;
import javax.xml.bind.annotation.XmlElements;
import javax.xml.bind.annotation.XmlIDREF;
import javax.xml.bind.annotation.XmlTransient;

import org.optaplanner.benchmark.config.statistic.ProblemStatisticType;
import org.optaplanner.benchmark.config.statistic.SingleStatisticType;
import org.optaplanner.benchmark.impl.loader.FileProblemProvider;
import org.optaplanner.benchmark.impl.loader.InstanceProblemProvider;
import org.optaplanner.benchmark.impl.loader.ProblemProvider;
import org.optaplanner.benchmark.impl.measurement.ScoreDifferencePercentage;
import org.optaplanner.benchmark.impl.ranking.TotalScoreSingleBenchmarkRankingComparator;
import org.optaplanner.benchmark.impl.report.BenchmarkReport;
import org.optaplanner.benchmark.impl.report.ReportHelper;
import org.optaplanner.benchmark.impl.statistic.ProblemStatistic;
import org.optaplanner.benchmark.impl.statistic.PureSubSingleStatistic;
import org.optaplanner.benchmark.impl.statistic.bestscore.BestScoreProblemStatistic;
import org.optaplanner.benchmark.impl.statistic.bestsolutionmutation.BestSolutionMutationProblemStatistic;
import org.optaplanner.benchmark.impl.statistic.memoryuse.MemoryUseProblemStatistic;
import org.optaplanner.benchmark.impl.statistic.movecountperstep.MoveCountPerStepProblemStatistic;
import org.optaplanner.benchmark.impl.statistic.scorecalculationspeed.ScoreCalculationSpeedProblemStatistic;
import org.optaplanner.benchmark.impl.statistic.stepscore.StepScoreProblemStatistic;
import org.optaplanner.core.api.domain.solution.PlanningScore;
import org.optaplanner.core.api.solver.Solver;
import org.optaplanner.core.config.util.ConfigUtils;
import org.optaplanner.core.impl.score.definition.ScoreDefinition;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * Represents 1 problem instance (data set) benchmarked on multiple {@link Solver} configurations.
 */
public class ProblemBenchmarkResult<Solution_> {

    private static final Logger LOGGER = LoggerFactory.getLogger(ProblemBenchmarkResult.class);

    @XmlTransient // Bi-directional relationship restored through BenchmarkResultIO
    private PlannerBenchmarkResult plannerBenchmarkResult;

    private String name = null;

    @XmlElements({
            @XmlElement(type = InstanceProblemProvider.class, name = "instanceProblemProvider"),
            @XmlElement(type = FileProblemProvider.class, name = "fileProblemProvider")
    })
    private ProblemProvider<Solution_> problemProvider;
    private boolean writeOutputSolutionEnabled = false;

    @XmlElements({
            @XmlElement(name = "bestScoreProblemStatistic", type = BestScoreProblemStatistic.class),
            @XmlElement(name = "stepScoreProblemStatistic", type = StepScoreProblemStatistic.class),
            @XmlElement(name = "scoreCalculationSpeedProblemStatistic", type = ScoreCalculationSpeedProblemStatistic.class),
            @XmlElement(name = "bestSolutionMutationProblemStatistic", type = BestSolutionMutationProblemStatistic.class),
            @XmlElement(name = "moveCountPerStepProblemStatistic", type = MoveCountPerStepProblemStatistic.class),
            @XmlElement(name = "memoryUseProblemStatistic", type = MemoryUseProblemStatistic.class),
    })
    private List<ProblemStatistic> problemStatisticList = null;

    @XmlIDREF
    @XmlElement(name = "singleBenchmarkResult")
    private List<SingleBenchmarkResult> singleBenchmarkResultList = null;

    private Long entityCount = null;
    private Long variableCount = null;
    private Long maximumValueCount = null;
    private Long problemScale = null;
    private Long inputSolutionLoadingTimeMillisSpent = null;

    @XmlTransient // Loaded lazily from singleBenchmarkResults
    private Integer maximumSubSingleCount = null;

    // ************************************************************************
    // Report accumulates
    // ************************************************************************

    private Long averageUsedMemoryAfterInputSolution = null;
    private Integer failureCount = null;
    private SingleBenchmarkResult winningSingleBenchmarkResult = null;
    private SingleBenchmarkResult worstSingleBenchmarkResult = null;
    private Long worstScoreCalculationSpeed = null;

    // ************************************************************************
    // Constructors and simple getters/setters
    // ************************************************************************

    private ProblemBenchmarkResult() {
        // Required by JAXB
    }

    public ProblemBenchmarkResult(PlannerBenchmarkResult plannerBenchmarkResult) {
        this.plannerBenchmarkResult = plannerBenchmarkResult;
    }

    public PlannerBenchmarkResult getPlannerBenchmarkResult() {
        return plannerBenchmarkResult;
    }

    public void setPlannerBenchmarkResult(PlannerBenchmarkResult plannerBenchmarkResult) {
        this.plannerBenchmarkResult = plannerBenchmarkResult;
    }

    /**
     * @return never null, filename safe
     */
    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public ProblemProvider<Solution_> getProblemProvider() {
        return problemProvider;
    }

    public void setProblemProvider(ProblemProvider<Solution_> problemProvider) {
        this.problemProvider = problemProvider;
    }

    public boolean isWriteOutputSolutionEnabled() {
        return writeOutputSolutionEnabled;
    }

    public void setWriteOutputSolutionEnabled(boolean writeOutputSolutionEnabled) {
        this.writeOutputSolutionEnabled = writeOutputSolutionEnabled;
    }

    public List<ProblemStatistic> getProblemStatisticList() {
        return problemStatisticList;
    }

    public void setProblemStatisticList(List<ProblemStatistic> problemStatisticList) {
        this.problemStatisticList = problemStatisticList;
    }

    public List<SingleBenchmarkResult> getSingleBenchmarkResultList() {
        return singleBenchmarkResultList;
    }

    public void setSingleBenchmarkResultList(List<SingleBenchmarkResult> singleBenchmarkResultList) {
        this.singleBenchmarkResultList = singleBenchmarkResultList;
    }

    public Long getEntityCount() {
        return entityCount;
    }

    public Long getVariableCount() {
        return variableCount;
    }

    public Long getMaximumValueCount() {
        return maximumValueCount;
    }

    public Long getProblemScale() {
        return problemScale;
    }

    public Long getInputSolutionLoadingTimeMillisSpent() {
        return inputSolutionLoadingTimeMillisSpent;
    }

    public Integer getMaximumSubSingleCount() {
        return maximumSubSingleCount;
    }

    public Long getAverageUsedMemoryAfterInputSolution() {
        return averageUsedMemoryAfterInputSolution;
    }

    public Integer getFailureCount() {
        return failureCount;
    }

    public SingleBenchmarkResult getWinningSingleBenchmarkResult() {
        return winningSingleBenchmarkResult;
    }

    public SingleBenchmarkResult getWorstSingleBenchmarkResult() {
        return worstSingleBenchmarkResult;
    }

    public Long getWorstScoreCalculationSpeed() {
        return worstScoreCalculationSpeed;
    }

    // ************************************************************************
    // Smart getters
    // ************************************************************************

    public String getAnchorId() {
        return ReportHelper.escapeHtmlId(name);
    }

    public String findScoreLevelLabel(int scoreLevel) {
        ScoreDefinition scoreDefinition = singleBenchmarkResultList.get(0).getSolverBenchmarkResult().getScoreDefinition();
        String[] levelLabels = scoreDefinition.getLevelLabels();
        if (scoreLevel >= levelLabels.length) {
            throw new IllegalArgumentException("The scoreLevel (" + scoreLevel
                    + ") isn't lower than the scoreLevelsSize (" + scoreDefinition.getLevelsSize()
                    + ") implied by the @" + PlanningScore.class.getSimpleName() + " on the planning solution class.");
        }
        return levelLabels[scoreLevel];
    }

    public File getBenchmarkReportDirectory() {
        return plannerBenchmarkResult.getBenchmarkReportDirectory();
    }

    public boolean hasAnyFailure() {
        return failureCount > 0;
    }

    public boolean hasAnySuccess() {
        return singleBenchmarkResultList.size() - failureCount > 0;
    }

    public boolean hasAnyStatistic() {
        if (problemStatisticList.size() > 0) {
            return true;
        }
        for (SingleBenchmarkResult singleBenchmarkResult : singleBenchmarkResultList) {
            if (singleBenchmarkResult.getMedian().getPureSubSingleStatisticList().size() > 0) {
                return true;
            }
        }
        return false;
    }

    public boolean hasProblemStatisticType(ProblemStatisticType problemStatisticType) {
        for (ProblemStatistic problemStatistic : problemStatisticList) {
            if (problemStatistic.getProblemStatisticType() == problemStatisticType) {
                return true;
            }
        }
        return false;
    }

    public Collection<SingleStatisticType> extractSingleStatisticTypeList() {
        Set<SingleStatisticType> singleStatisticTypeSet = new LinkedHashSet<>();
        for (SingleBenchmarkResult singleBenchmarkResult : singleBenchmarkResultList) {
            for (PureSubSingleStatistic pureSubSingleStatistic : singleBenchmarkResult.getMedian()
                    .getPureSubSingleStatisticList()) {
                singleStatisticTypeSet.add(pureSubSingleStatistic.getStatisticType());
            }
        }
        return singleStatisticTypeSet;
    }

    public List<PureSubSingleStatistic> extractPureSubSingleStatisticList(SingleStatisticType singleStatisticType) {
        List<PureSubSingleStatistic> pureSubSingleStatisticList = new ArrayList<>(
                singleBenchmarkResultList.size());
        for (SingleBenchmarkResult singleBenchmarkResult : singleBenchmarkResultList) {
            for (PureSubSingleStatistic pureSubSingleStatistic : singleBenchmarkResult.getMedian()
                    .getPureSubSingleStatisticList()) {
                if (pureSubSingleStatistic.getStatisticType() == singleStatisticType) {
                    pureSubSingleStatisticList.add(pureSubSingleStatistic);
                }
            }
        }
        return pureSubSingleStatisticList;
    }

    // ************************************************************************
    // Work methods
    // ************************************************************************

    public String getProblemReportDirectoryName() {
        return name;
    }

    public File getProblemReportDirectory() {
        return new File(getBenchmarkReportDirectory(), name);
    }

    public void makeDirs() {
        File problemReportDirectory = getProblemReportDirectory();
        problemReportDirectory.mkdirs();
        for (SingleBenchmarkResult singleBenchmarkResult : singleBenchmarkResultList) {
            singleBenchmarkResult.makeDirs();
        }
    }

    public int getTotalSubSingleCount() {
        int totalSubSingleCount = 0;
        for (SingleBenchmarkResult singleBenchmarkResult : singleBenchmarkResultList) {
            totalSubSingleCount += singleBenchmarkResult.getSubSingleCount();
        }
        return totalSubSingleCount;
    }

    public Solution_ readProblem() {
        long startTimeMillis = System.currentTimeMillis();
        Solution_ inputSolution = problemProvider.readProblem();
        inputSolutionLoadingTimeMillisSpent = System.currentTimeMillis() - startTimeMillis;
        return inputSolution;
    }

    public void writeSolution(SubSingleBenchmarkResult subSingleBenchmarkResult, Solution_ solution) {
        if (!writeOutputSolutionEnabled) {
            return;
        }
        problemProvider.writeSolution(solution, subSingleBenchmarkResult);
    }

    // ************************************************************************
    // Accumulate methods
    // ************************************************************************

    public void accumulateResults(BenchmarkReport benchmarkReport) {
        for (SingleBenchmarkResult singleBenchmarkResult : singleBenchmarkResultList) {
            singleBenchmarkResult.accumulateResults(benchmarkReport);
        }
        determineTotalsAndAveragesAndRanking();
        determineWinningScoreDifference();
        for (ProblemStatistic problemStatistic : problemStatisticList) {
            problemStatistic.accumulateResults(benchmarkReport);
        }
    }

    private void determineTotalsAndAveragesAndRanking() {
        failureCount = 0;
        maximumSubSingleCount = 0;
        worstScoreCalculationSpeed = null;
        long totalUsedMemoryAfterInputSolution = 0L;
        int usedMemoryAfterInputSolutionCount = 0;
        List<SingleBenchmarkResult> successResultList = new ArrayList<>(singleBenchmarkResultList);
        // Do not rank a SingleBenchmarkResult that has a failure
        for (Iterator<SingleBenchmarkResult> it = successResultList.iterator(); it.hasNext();) {
            SingleBenchmarkResult singleBenchmarkResult = it.next();
            if (singleBenchmarkResult.hasAnyFailure()) {
                failureCount++;
                it.remove();
            } else {
                int subSingleCount = singleBenchmarkResult.getSubSingleBenchmarkResultList().size();
                if (subSingleCount > maximumSubSingleCount) {
                    maximumSubSingleCount = subSingleCount;
                }
                if (singleBenchmarkResult.getUsedMemoryAfterInputSolution() != null) {
                    totalUsedMemoryAfterInputSolution += singleBenchmarkResult.getUsedMemoryAfterInputSolution();
                    usedMemoryAfterInputSolutionCount++;
                }
                if (worstScoreCalculationSpeed == null
                        || singleBenchmarkResult.getScoreCalculationSpeed() < worstScoreCalculationSpeed) {
                    worstScoreCalculationSpeed = singleBenchmarkResult.getScoreCalculationSpeed();
                }
            }
        }
        if (usedMemoryAfterInputSolutionCount > 0) {
            averageUsedMemoryAfterInputSolution = totalUsedMemoryAfterInputSolution / usedMemoryAfterInputSolutionCount;
        }
        determineRanking(successResultList);
    }

    private void determineRanking(List<SingleBenchmarkResult> rankedSingleBenchmarkResultList) {
        Comparator<SingleBenchmarkResult> singleBenchmarkRankingComparator = new TotalScoreSingleBenchmarkRankingComparator();
        rankedSingleBenchmarkResultList.sort(Collections.reverseOrder(singleBenchmarkRankingComparator));
        int ranking = 0;
        SingleBenchmarkResult previousSingleBenchmarkResult = null;
        int previousSameRankingCount = 0;
        for (SingleBenchmarkResult singleBenchmarkResult : rankedSingleBenchmarkResultList) {
            if (previousSingleBenchmarkResult != null
                    && singleBenchmarkRankingComparator.compare(previousSingleBenchmarkResult, singleBenchmarkResult) != 0) {
                ranking += previousSameRankingCount;
                previousSameRankingCount = 0;
            }
            singleBenchmarkResult.setRanking(ranking);
            previousSingleBenchmarkResult = singleBenchmarkResult;
            previousSameRankingCount++;
        }
        winningSingleBenchmarkResult = rankedSingleBenchmarkResultList.isEmpty() ? null
                : rankedSingleBenchmarkResultList.get(0);
        worstSingleBenchmarkResult = rankedSingleBenchmarkResultList.isEmpty() ? null
                : rankedSingleBenchmarkResultList.get(rankedSingleBenchmarkResultList.size() - 1);
    }

    private void determineWinningScoreDifference() {
        for (SingleBenchmarkResult singleBenchmarkResult : singleBenchmarkResultList) {
            if (singleBenchmarkResult.hasAnyFailure()) {
                continue;
            }
            singleBenchmarkResult.setWinningScoreDifference(
                    singleBenchmarkResult.getAverageScore().subtract(winningSingleBenchmarkResult.getAverageScore()));
            singleBenchmarkResult.setWorstScoreDifferencePercentage(
                    ScoreDifferencePercentage.calculateScoreDifferencePercentage(
                            worstSingleBenchmarkResult.getAverageScore(), singleBenchmarkResult.getAverageScore()));
            singleBenchmarkResult.setWorstScoreCalculationSpeedDifferencePercentage(
                    ScoreDifferencePercentage.calculateDifferencePercentage(
                            (double) worstScoreCalculationSpeed,
                            (double) singleBenchmarkResult.getScoreCalculationSpeed()));
        }
    }

    /**
     * HACK to avoid loading the problem just to extract its problemScale.
     * Called multiple times, for every {@link SingleBenchmarkResult} of this {@link ProblemBenchmarkResult}.
     *
     * @param registeringEntityCount {@code >= 0}
     * @param registeringVariableCount {@code >= 0}
     * @param registeringProblemScale {@code >= 0}
     */
    public void registerScale(long registeringEntityCount, long registeringVariableCount,
            long registeringMaximumValueCount, long registeringProblemScale) {
        if (entityCount == null) {
            entityCount = registeringEntityCount;
        } else if (entityCount.longValue() != registeringEntityCount) {
            LOGGER.warn("The problemBenchmarkResult ({}) has different entityCount values ([{},{}]).\n"
                    + "This is normally impossible for 1 inputSolutionFile.",
                    getName(), entityCount, registeringEntityCount);
            // The entityCount is not unknown (null), but known to be ambiguous
            entityCount = -1L;
        }
        if (variableCount == null) {
            variableCount = registeringVariableCount;
        } else if (variableCount.longValue() != registeringVariableCount) {
            LOGGER.warn("The problemBenchmarkResult ({}) has different variableCount values ([{},{}]).\n"
                    + "This is normally impossible for 1 inputSolutionFile.",
                    getName(), variableCount, registeringVariableCount);
            // The variableCount is not unknown (null), but known to be ambiguous
            variableCount = -1L;
        }
        if (maximumValueCount == null) {
            maximumValueCount = registeringMaximumValueCount;
        } else if (maximumValueCount.longValue() != registeringMaximumValueCount) {
            LOGGER.warn("The problemBenchmarkResult ({}) has different maximumValueCount values ([{},{}]).\n"
                    + "This is normally impossible for 1 inputSolutionFile.",
                    getName(), maximumValueCount, registeringMaximumValueCount);
            // The maximumValueCount is not unknown (null), but known to be ambiguous
            maximumValueCount = -1L;
        }
        if (problemScale == null) {
            problemScale = registeringProblemScale;
        } else if (problemScale.longValue() != registeringProblemScale) {
            LOGGER.warn("The problemBenchmarkResult ({}) has different problemScale values ([{},{}]).\n"
                    + "This is normally impossible for 1 inputSolutionFile.",
                    getName(), problemScale, registeringProblemScale);
            // The problemScale is not unknown (null), but known to be ambiguous
            problemScale = -1L;
        }
    }

    /**
     * Used by {@link org.optaplanner.benchmark.impl.ProblemBenchmarksFactory#buildProblemBenchmarkList}.
     *
     * @param o sometimes null
     * @return true if equal
     */
    @Override
    public boolean equals(Object o) {
        if (this == o) {
            return true;
        } else if (o instanceof ProblemBenchmarkResult) {
            ProblemBenchmarkResult other = (ProblemBenchmarkResult) o;
            return problemProvider.equals(other.getProblemProvider());
        } else {
            return false;
        }
    }

    @Override
    public int hashCode() {
        return problemProvider.hashCode();
    }

    // ************************************************************************
    // Merger methods
    // ************************************************************************

    protected static <Solution_> Map<ProblemBenchmarkResult, ProblemBenchmarkResult> createMergeMap(
            PlannerBenchmarkResult newPlannerBenchmarkResult, List<SingleBenchmarkResult> singleBenchmarkResultList) {
        // IdentityHashMap but despite that different ProblemBenchmarkResult instances are merged
        Map<ProblemBenchmarkResult, ProblemBenchmarkResult> mergeMap = new IdentityHashMap<>();
        Map<ProblemProvider<Solution_>, ProblemBenchmarkResult> problemProviderToNewResultMap = new HashMap<>();
        for (SingleBenchmarkResult singleBenchmarkResult : singleBenchmarkResultList) {
            ProblemBenchmarkResult<Solution_> oldResult = singleBenchmarkResult.getProblemBenchmarkResult();
            if (!mergeMap.containsKey(oldResult)) {
                ProblemBenchmarkResult<Solution_> newResult;
                if (!problemProviderToNewResultMap.containsKey(oldResult.problemProvider)) {
                    newResult = new ProblemBenchmarkResult<>(newPlannerBenchmarkResult);
                    newResult.name = oldResult.name;
                    newResult.problemProvider = oldResult.problemProvider;
                    // Skip oldResult.problemReportDirectory
                    newResult.problemStatisticList = new ArrayList<>(oldResult.problemStatisticList.size());
                    for (ProblemStatistic oldProblemStatistic : oldResult.problemStatisticList) {
                        newResult.problemStatisticList.add(
                                oldProblemStatistic.getProblemStatisticType().buildProblemStatistic(newResult));
                    }
                    newResult.singleBenchmarkResultList = new ArrayList<>(
                            oldResult.singleBenchmarkResultList.size());
                    newResult.entityCount = oldResult.entityCount;
                    newResult.variableCount = oldResult.variableCount;
                    newResult.maximumValueCount = oldResult.maximumValueCount;
                    newResult.problemScale = oldResult.problemScale;
                    problemProviderToNewResultMap.put(oldResult.problemProvider, newResult);
                    newPlannerBenchmarkResult.getUnifiedProblemBenchmarkResultList().add(newResult);
                } else {
                    newResult = problemProviderToNewResultMap.get(oldResult.problemProvider);
                    if (!Objects.equals(oldResult.name, newResult.name)) {
                        throw new IllegalStateException(
                                "The oldResult (" + oldResult + ") and newResult (" + newResult
                                        + ") should have the same name, because they have the same problemProvider ("
                                        + oldResult.problemProvider + ").");
                    }
                    newResult.problemStatisticList.removeIf(
                            newStatistic -> !oldResult.hasProblemStatisticType(newStatistic.getProblemStatisticType()));
                    newResult.entityCount = ConfigUtils.meldProperty(oldResult.entityCount, newResult.entityCount);
                    newResult.variableCount = ConfigUtils.meldProperty(oldResult.variableCount, newResult.variableCount);
                    newResult.maximumValueCount = ConfigUtils.meldProperty(oldResult.maximumValueCount,
                            newResult.maximumValueCount);
                    newResult.problemScale = ConfigUtils.meldProperty(oldResult.problemScale, newResult.problemScale);
                }
                mergeMap.put(oldResult, newResult);
            }
        }
        return mergeMap;
    }

    @Override
    public String toString() {
        return getName();
    }

}
