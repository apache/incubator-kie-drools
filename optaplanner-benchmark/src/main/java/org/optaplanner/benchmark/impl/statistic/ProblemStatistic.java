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

package org.optaplanner.benchmark.impl.statistic;

import java.io.File;
import java.util.ArrayList;
import java.util.List;

import com.thoughtworks.xstream.annotations.XStreamInclude;
import com.thoughtworks.xstream.annotations.XStreamOmitField;
import org.jfree.chart.JFreeChart;
import org.optaplanner.benchmark.config.statistic.ProblemStatisticType;
import org.optaplanner.benchmark.impl.report.BenchmarkReport;
import org.optaplanner.benchmark.impl.report.ReportHelper;
import org.optaplanner.benchmark.impl.result.ProblemBenchmarkResult;
import org.optaplanner.benchmark.impl.result.SingleBenchmarkResult;
import org.optaplanner.benchmark.impl.statistic.bestscore.BestScoreProblemStatistic;
import org.optaplanner.benchmark.impl.statistic.bestsolutionmutation.BestSolutionMutationProblemStatistic;
import org.optaplanner.benchmark.impl.statistic.calculatecount.CalculateCountProblemStatistic;
import org.optaplanner.benchmark.impl.statistic.common.GraphSupport;
import org.optaplanner.benchmark.impl.statistic.memoryuse.MemoryUseProblemStatistic;
import org.optaplanner.benchmark.impl.statistic.movecountperstep.MoveCountPerStepProblemStatistic;
import org.optaplanner.benchmark.impl.statistic.stepscore.StepScoreProblemStatistic;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * 1 statistic of {@link ProblemBenchmarkResult}
 */
@XStreamInclude({
        BestScoreProblemStatistic.class,
        StepScoreProblemStatistic.class,
        CalculateCountProblemStatistic.class,
        BestSolutionMutationProblemStatistic.class,
        MoveCountPerStepProblemStatistic.class,
        MemoryUseProblemStatistic.class
})
public abstract class ProblemStatistic {

    protected final transient Logger logger = LoggerFactory.getLogger(getClass());

    @XStreamOmitField // Bi-directional relationship restored through BenchmarkResultIO
    protected ProblemBenchmarkResult problemBenchmarkResult;

    protected final ProblemStatisticType problemStatisticType;

    // ************************************************************************
    // Report accumulates
    // ************************************************************************

    protected List<String> warningList = null;

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

    public List<SingleStatistic> getSingleStatisticList() {
        List<SingleBenchmarkResult> singleBenchmarkResultList = problemBenchmarkResult.getSingleBenchmarkResultList();
        List<SingleStatistic> singleStatisticList = new ArrayList<SingleStatistic>(singleBenchmarkResultList.size());
        for (SingleBenchmarkResult singleBenchmarkResult : singleBenchmarkResultList) {
            singleStatisticList.add(singleBenchmarkResult.getEffectiveSingleStatisticMap().get(problemStatisticType));
        }
        return singleStatisticList;
    }

    public abstract SingleStatistic createSingleStatistic(SingleBenchmarkResult singleBenchmarkResult);

    // ************************************************************************
    // Write methods
    // ************************************************************************

    public void accumulateResults(BenchmarkReport benchmarkReport) {
        warningList = new ArrayList<String>();
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

}
