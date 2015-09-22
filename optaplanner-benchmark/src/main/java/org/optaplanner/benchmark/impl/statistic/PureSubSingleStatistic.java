/*
 * Copyright 2014 JBoss Inc
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
import java.util.List;

import com.thoughtworks.xstream.annotations.XStreamInclude;
import org.jfree.chart.JFreeChart;
import org.optaplanner.benchmark.config.statistic.SingleStatisticType;
import org.optaplanner.benchmark.impl.report.BenchmarkReport;
import org.optaplanner.benchmark.impl.result.SingleBenchmarkResult;
import org.optaplanner.benchmark.impl.result.SolverProblemBenchmarkResult;
import org.optaplanner.benchmark.impl.statistic.common.GraphSupport;
import org.optaplanner.benchmark.impl.statistic.single.constraintmatchtotalbestscore.ConstraintMatchTotalBestScoreSubSingleStatistic;
import org.optaplanner.benchmark.impl.statistic.single.constraintmatchtotalstepscore.ConstraintMatchTotalStepScoreSubSingleStatistic;
import org.optaplanner.benchmark.impl.statistic.single.pickedmovetypebestscore.PickedMoveTypeBestScoreDiffSubSingleStatistic;
import org.optaplanner.benchmark.impl.statistic.single.pickedmovetypestepscore.PickedMoveTypeStepScoreDiffSubSingleStatistic;

/**
 * 1 statistic of {@link SingleBenchmarkResult}.
 */
@XStreamInclude({
        ConstraintMatchTotalBestScoreSubSingleStatistic.class,
        ConstraintMatchTotalStepScoreSubSingleStatistic.class,
        PickedMoveTypeBestScoreDiffSubSingleStatistic.class,
        PickedMoveTypeStepScoreDiffSubSingleStatistic.class
})
public abstract class PureSubSingleStatistic<P extends StatisticPoint> extends SubSingleStatistic<P> {

    protected final SingleStatisticType singleStatisticType;

    protected PureSubSingleStatistic(SolverProblemBenchmarkResult solverProblemBenchmarkResult, SingleStatisticType singleStatisticType) {
        super(solverProblemBenchmarkResult);
        this.singleStatisticType = singleStatisticType;
    }

    @Override
    public SingleStatisticType getStatisticType() {
        return singleStatisticType;
    }

    // ************************************************************************
    // Write methods
    // ************************************************************************

    public abstract void writeGraphFiles(BenchmarkReport benchmarkReport);

    protected File writeChartToImageFile(JFreeChart chart, String fileNameBase) {
        File chartFile = new File(solverProblemBenchmarkResult.getReportDirectory(), fileNameBase + ".png");
        GraphSupport.writeChartToImageFile(chart, chartFile);
        return chartFile;
    }

    public File getGraphFile() {
        List<File> graphFileList = getGraphFileList();
        if (graphFileList == null || graphFileList.isEmpty()) {
            return null;
        } else if (graphFileList.size() > 1) {
            throw new IllegalStateException("Cannot get graph file for the PureSingleStatistic ( " + this
                    + " ) because it has more than 1 graph file. See method getGraphList() and "
                    + SingleStatisticType.class.getSimpleName() + ".hasScoreLevels()");
        } else {
            return graphFileList.get(0);
        }
    }

    public abstract List<File> getGraphFileList();

    @Override
    public String toString() {
        return solverProblemBenchmarkResult + "_" + singleStatisticType;
    }

}
