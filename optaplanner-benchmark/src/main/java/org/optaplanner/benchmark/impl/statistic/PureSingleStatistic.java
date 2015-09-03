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
import org.optaplanner.benchmark.impl.statistic.common.GraphSupport;
import org.optaplanner.benchmark.impl.statistic.single.constraintmatchtotalbestscore.ConstraintMatchTotalBestScoreSingleStatistic;
import org.optaplanner.benchmark.impl.statistic.single.constraintmatchtotalstepscore.ConstraintMatchTotalStepScoreSingleStatistic;
import org.optaplanner.benchmark.impl.statistic.single.pickedmovetypebestscore.PickedMoveTypeBestScoreDiffSingleStatistic;
import org.optaplanner.benchmark.impl.statistic.single.pickedmovetypestepscore.PickedMoveTypeStepScoreDiffSingleStatistic;

/**
 * 1 statistic of {@link SingleBenchmarkResult}.
 */
@XStreamInclude({
        ConstraintMatchTotalBestScoreSingleStatistic.class,
        ConstraintMatchTotalStepScoreSingleStatistic.class,
        PickedMoveTypeBestScoreDiffSingleStatistic.class,
        PickedMoveTypeStepScoreDiffSingleStatistic.class
})
public abstract class PureSingleStatistic<P extends StatisticPoint> extends SingleStatistic<P> {

    protected final SingleStatisticType singleStatisticType;

    protected PureSingleStatistic(SingleBenchmarkResult singleBenchmarkResult, SingleStatisticType singleStatisticType) {
        super(singleBenchmarkResult);
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
        File chartFile = new File(singleBenchmarkResult.getSingleReportDirectory(), fileNameBase + ".png");
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
        return singleBenchmarkResult + "_" + singleStatisticType;
    }

}
