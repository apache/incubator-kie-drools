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

package org.drools.planner.benchmark.core.statistic.memoryuse;

import java.awt.image.BufferedImage;
import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.OutputStream;
import java.io.OutputStreamWriter;
import java.io.Writer;
import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import javax.imageio.ImageIO;

import org.apache.commons.io.IOUtils;
import org.drools.planner.benchmark.core.ProblemBenchmark;
import org.drools.planner.benchmark.core.SingleBenchmark;
import org.drools.planner.benchmark.core.statistic.AbstractProblemStatistic;
import org.drools.planner.benchmark.core.statistic.MillisecondsSpendNumberFormat;
import org.drools.planner.benchmark.core.statistic.ProblemStatisticType;
import org.drools.planner.benchmark.core.statistic.SingleStatistic;
import org.drools.planner.benchmark.core.statistic.bestscore.BestScoreSingleStatistic;
import org.drools.planner.benchmark.core.statistic.bestscore.BestScoreSingleStatisticPoint;
import org.drools.planner.core.Solver;
import org.drools.planner.core.score.Score;
import org.jfree.chart.JFreeChart;
import org.jfree.chart.axis.NumberAxis;
import org.jfree.chart.plot.PlotOrientation;
import org.jfree.chart.plot.XYPlot;
import org.jfree.chart.renderer.xy.XYAreaRenderer2;
import org.jfree.chart.renderer.xy.XYItemRenderer;
import org.jfree.data.xy.XYSeries;
import org.jfree.data.xy.XYSeriesCollection;

public class MemoryUseProblemStatistic extends AbstractProblemStatistic {

    public MemoryUseProblemStatistic() {
        super(ProblemStatisticType.MEMORY_USE);
    }

    public SingleStatistic createSingleStatistic(Solver solver) {
        return new MemoryUseSingleStatistic(solver);
    }

    // ************************************************************************
    // Write methods
    // ************************************************************************

    protected CharSequence writeCsvStatistic(File statisticDirectory, ProblemBenchmark problemBenchmark) {
        ProblemStatisticCsv csv = new ProblemStatisticCsv();
        for (SingleBenchmark singleBenchmark : problemBenchmark.getSingleBenchmarkList()) {
            MemoryUseSingleStatistic singleStatistic = (MemoryUseSingleStatistic)
                    singleBenchmark.getSingleStatistic(problemStatisticType);
            for (MemoryUseSingleStatisticPoint point : singleStatistic.getPointList()) {
                long timeMillisSpend = point.getTimeMillisSpend();
                MemoryUseMeasurement memoryUseMeasurement = point.getMemoryUseMeasurement();
                String value = "\"" + Long.toString(memoryUseMeasurement.getUsedMemory())
                        + "/" + Long.toString(memoryUseMeasurement.getMaxMemory()) + "\"";
                csv.addPoint(singleBenchmark, timeMillisSpend, value);
            }
        }
        File csvStatisticFile = new File(statisticDirectory, problemBenchmark.getName() + "MemoryUseStatistic.csv");
        return csv.writeCsvStatisticFile(csvStatisticFile, problemBenchmark);
    }

    protected CharSequence writeGraphStatistic(File statisticDirectory, ProblemBenchmark problemBenchmark) {
        XYSeriesCollection seriesCollection = new XYSeriesCollection();
        for (SingleBenchmark singleBenchmark : problemBenchmark.getSingleBenchmarkList()) {
            MemoryUseSingleStatistic singleStatistic = (MemoryUseSingleStatistic)
                    singleBenchmark.getSingleStatistic(problemStatisticType);
            XYSeries usedSeries = new XYSeries(singleBenchmark.getSolverBenchmark().getName() + " used");
            XYSeries maxSeries = new XYSeries(singleBenchmark.getSolverBenchmark().getName() + " max");
            for (MemoryUseSingleStatisticPoint point : singleStatistic.getPointList()) {
                long timeMillisSpend = point.getTimeMillisSpend();
                MemoryUseMeasurement memoryUseMeasurement = point.getMemoryUseMeasurement();
                usedSeries.add(timeMillisSpend, memoryUseMeasurement.getUsedMemory());
                maxSeries.add(timeMillisSpend, memoryUseMeasurement.getMaxMemory());
            }
            seriesCollection.addSeries(usedSeries);
            seriesCollection.addSeries(maxSeries);
        }
        NumberAxis xAxis = new NumberAxis("Time spend");
        xAxis.setNumberFormatOverride(new MillisecondsSpendNumberFormat());
        NumberAxis yAxis = new NumberAxis("Memory");
        yAxis.setAutoRangeIncludesZero(false);
        XYItemRenderer renderer = new XYAreaRenderer2();
        XYPlot plot = new XYPlot(seriesCollection, xAxis, yAxis, renderer);
        plot.setOrientation(PlotOrientation.VERTICAL);
        JFreeChart chart = new JFreeChart(problemBenchmark.getName() + " memory use statistic",
                JFreeChart.DEFAULT_TITLE_FONT, plot, true);
        BufferedImage chartImage = chart.createBufferedImage(1024, 768);
        File graphStatisticFile = new File(statisticDirectory, problemBenchmark.getName() + "MemoryUseStatistic.png");
        OutputStream out = null;
        try {
            out = new FileOutputStream(graphStatisticFile);
            ImageIO.write(chartImage, "png", out);
        } catch (IOException e) {
            throw new IllegalArgumentException("Problem writing graphStatisticFile: " + graphStatisticFile, e);
        } finally {
            IOUtils.closeQuietly(out);
        }
        return "    <img src=\"" + graphStatisticFile.getName() + "\"/>\n";
    }

}
