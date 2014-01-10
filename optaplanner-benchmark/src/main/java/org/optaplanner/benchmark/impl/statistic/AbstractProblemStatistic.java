/*
 * Copyright 2011 JBoss Inc
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

import java.awt.image.BufferedImage;
import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.OutputStream;
import java.util.ArrayList;
import java.util.List;

import javax.imageio.ImageIO;

import org.apache.commons.io.IOUtils;
import org.jfree.chart.JFreeChart;
import org.optaplanner.benchmark.impl.ProblemBenchmark;
import org.optaplanner.benchmark.impl.SingleBenchmark;
import org.optaplanner.benchmark.impl.report.BenchmarkReport;
import org.optaplanner.benchmark.impl.report.ReportHelper;

public abstract class AbstractProblemStatistic implements ProblemStatistic {

    protected final ProblemBenchmark problemBenchmark;
    protected final ProblemStatisticType problemStatisticType;

    protected List<String> warningList = null;

    protected AbstractProblemStatistic(ProblemBenchmark problemBenchmark, ProblemStatisticType problemStatisticType) {
        this.problemBenchmark = problemBenchmark;
        this.problemStatisticType = problemStatisticType;
    }

    public ProblemBenchmark getProblemBenchmark() {
        return problemBenchmark;
    }

    public ProblemStatisticType getProblemStatisticType() {
        return problemStatisticType;
    }

    public String getAnchorId() {
        return ReportHelper.escapeHtmlId(problemBenchmark.getName() + "_" + problemStatisticType.name());
    }

    public List<String> getWarningList() {
        return warningList;
    }

    public List<SingleStatistic> getSingleStatisticList() {
        List<SingleBenchmark> singleBenchmarkList = problemBenchmark.getSingleBenchmarkList();
        List<SingleStatistic> singleStatisticList = new ArrayList<SingleStatistic>(singleBenchmarkList.size());
        for (SingleBenchmark singleBenchmark : singleBenchmarkList) {
            singleStatisticList.add(singleBenchmark.getSingleStatisticMap().get(problemStatisticType));
        }
        return singleStatisticList;
    }

    // ************************************************************************
    // Write methods
    // ************************************************************************

    @Override
    public void accumulateResults(BenchmarkReport benchmarkReport) {
        warningList = new ArrayList<String>();
        fillWarningList();
    }

    protected void fillWarningList() {
    }

    protected File writeChartToImageFile(JFreeChart chart, String fileNameBase) {
        BufferedImage chartImage = chart.createBufferedImage(1024, 768);
        File chartFile = new File(problemBenchmark.getProblemReportDirectory(), fileNameBase + ".png");
        OutputStream out = null;
        try {
            out = new FileOutputStream(chartFile);
            ImageIO.write(chartImage, "png", out);
        } catch (IOException e) {
            throw new IllegalArgumentException("Problem writing chartFile: " + chartFile, e);
        } finally {
            IOUtils.closeQuietly(out);
        }
        return chartFile;
    }

}
