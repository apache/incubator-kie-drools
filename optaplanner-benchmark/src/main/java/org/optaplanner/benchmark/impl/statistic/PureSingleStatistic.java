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

import java.io.BufferedReader;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.OutputStreamWriter;
import java.io.Writer;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

import com.thoughtworks.xstream.annotations.XStreamInclude;
import com.thoughtworks.xstream.annotations.XStreamOmitField;
import org.apache.commons.io.IOUtils;
import org.jfree.chart.JFreeChart;
import org.optaplanner.benchmark.config.statistic.SingleStatisticType;
import org.optaplanner.benchmark.impl.report.BenchmarkReport;
import org.optaplanner.benchmark.impl.result.SingleBenchmarkResult;
import org.optaplanner.benchmark.impl.statistic.common.GraphSupport;
import org.optaplanner.benchmark.impl.statistic.pickedmovetypebestscore.PickedMoveTypeBestScoreDiffSingleStatistic;
import org.optaplanner.core.api.solver.Solver;
import org.optaplanner.core.impl.score.definition.ScoreDefinition;

/**
 * 1 statistic of {@link SingleBenchmarkResult}
 */
@XStreamInclude({
        PickedMoveTypeBestScoreDiffSingleStatistic.class
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

}
