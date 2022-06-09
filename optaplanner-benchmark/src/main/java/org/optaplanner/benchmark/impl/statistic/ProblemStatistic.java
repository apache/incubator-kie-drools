package org.optaplanner.benchmark.impl.statistic;

import java.io.File;
import java.util.ArrayList;
import java.util.List;

import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;
import javax.xml.bind.annotation.XmlSeeAlso;
import javax.xml.bind.annotation.XmlTransient;

import org.jfree.chart.JFreeChart;
import org.optaplanner.benchmark.config.statistic.ProblemStatisticType;
import org.optaplanner.benchmark.impl.report.BenchmarkReport;
import org.optaplanner.benchmark.impl.report.ReportHelper;
import org.optaplanner.benchmark.impl.result.ProblemBenchmarkResult;
import org.optaplanner.benchmark.impl.result.SingleBenchmarkResult;
import org.optaplanner.benchmark.impl.result.SubSingleBenchmarkResult;
import org.optaplanner.benchmark.impl.statistic.bestscore.BestScoreProblemStatistic;
import org.optaplanner.benchmark.impl.statistic.bestsolutionmutation.BestSolutionMutationProblemStatistic;
import org.optaplanner.benchmark.impl.statistic.common.GraphSupport;
import org.optaplanner.benchmark.impl.statistic.memoryuse.MemoryUseProblemStatistic;
import org.optaplanner.benchmark.impl.statistic.movecountperstep.MoveCountPerStepProblemStatistic;
import org.optaplanner.benchmark.impl.statistic.scorecalculationspeed.ScoreCalculationSpeedProblemStatistic;
import org.optaplanner.benchmark.impl.statistic.stepscore.StepScoreProblemStatistic;

/**
 * 1 statistic of {@link ProblemBenchmarkResult}.
 */
@XmlAccessorType(XmlAccessType.FIELD)
@XmlSeeAlso({
        BestScoreProblemStatistic.class,
        StepScoreProblemStatistic.class,
        ScoreCalculationSpeedProblemStatistic.class,
        BestSolutionMutationProblemStatistic.class,
        MoveCountPerStepProblemStatistic.class,
        MemoryUseProblemStatistic.class
})
public abstract class ProblemStatistic {

    @XmlTransient // Bi-directional relationship restored through BenchmarkResultIO
    protected ProblemBenchmarkResult<Object> problemBenchmarkResult;

    protected final ProblemStatisticType problemStatisticType;

    // ************************************************************************
    // Report accumulates
    // ************************************************************************

    protected List<String> warningList = null;

    public ProblemStatistic() {
        problemStatisticType = null;
    }

    protected ProblemStatistic(ProblemBenchmarkResult problemBenchmarkResult, ProblemStatisticType problemStatisticType) {
        this.problemBenchmarkResult = problemBenchmarkResult;
        this.problemStatisticType = problemStatisticType;
    }

    public ProblemBenchmarkResult getProblemBenchmarkResult() {
        return problemBenchmarkResult;
    }

    public void setProblemBenchmarkResult(ProblemBenchmarkResult problemBenchmarkResult) {
        this.problemBenchmarkResult = problemBenchmarkResult;
    }

    public ProblemStatisticType getProblemStatisticType() {
        return problemStatisticType;
    }

    public String getAnchorId() {
        return ReportHelper.escapeHtmlId(problemBenchmarkResult.getName() + "_" + problemStatisticType.name());
    }

    public List<String> getWarningList() {
        return warningList;
    }

    public List<SubSingleStatistic> getSubSingleStatisticList() {
        List<SingleBenchmarkResult> singleBenchmarkResultList = problemBenchmarkResult.getSingleBenchmarkResultList();
        List<SubSingleStatistic> subSingleStatisticList = new ArrayList<>(singleBenchmarkResultList.size());
        for (SingleBenchmarkResult singleBenchmarkResult : singleBenchmarkResultList) {
            if (singleBenchmarkResult.getSubSingleBenchmarkResultList().isEmpty()) {
                continue;
            }
            // All subSingles have the same sub single statistics
            subSingleStatisticList.add(singleBenchmarkResult.getSubSingleBenchmarkResultList().get(0)
                    .getEffectiveSubSingleStatisticMap().get(problemStatisticType));
        }
        return subSingleStatisticList;
    }

    public abstract SubSingleStatistic createSubSingleStatistic(SubSingleBenchmarkResult subSingleBenchmarkResult);

    // ************************************************************************
    // Write methods
    // ************************************************************************

    public void accumulateResults(BenchmarkReport benchmarkReport) {
        warningList = new ArrayList<>();
        fillWarningList();
    }

    public abstract void writeGraphFiles(BenchmarkReport benchmarkReport);

    protected void fillWarningList() {
    }

    protected File writeChartToImageFile(JFreeChart chart, String fileNameBase) {
        File chartFile = new File(problemBenchmarkResult.getProblemReportDirectory(), fileNameBase + ".png");
        GraphSupport.writeChartToImageFile(chart, chartFile);
        return chartFile;
    }

    public File getGraphFile() {
        List<File> graphFileList = getGraphFileList();
        if (graphFileList == null || graphFileList.isEmpty()) {
            return null;
        } else if (graphFileList.size() > 1) {
            throw new IllegalStateException("Cannot get graph file for the ProblemStatistic (" + this
                    + ") because it has more than 1 graph file. See method getGraphList() and "
                    + ProblemStatisticType.class.getSimpleName() + ".hasScoreLevels()");
        } else {
            return graphFileList.get(0);
        }
    }

    public abstract List<File> getGraphFileList();

    @Override
    public String toString() {
        return problemBenchmarkResult + "_" + problemStatisticType;
    }

}
