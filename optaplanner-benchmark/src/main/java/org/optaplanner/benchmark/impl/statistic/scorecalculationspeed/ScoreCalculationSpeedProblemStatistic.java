/*
 * Copyright 2011 Red Hat, Inc. and/or its affiliates.
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

package org.optaplanner.benchmark.impl.statistic.scorecalculationspeed;

import java.awt.BasicStroke;
import java.io.File;
import java.text.NumberFormat;
import java.util.Collections;
import java.util.List;
import java.util.Locale;

import org.jfree.chart.JFreeChart;
import org.jfree.chart.axis.NumberAxis;
import org.jfree.chart.plot.PlotOrientation;
import org.jfree.chart.plot.XYPlot;
import org.jfree.chart.renderer.xy.XYItemRenderer;
import org.jfree.chart.renderer.xy.XYLineAndShapeRenderer;
import org.jfree.data.xy.XYSeries;
import org.jfree.data.xy.XYSeriesCollection;
import org.optaplanner.benchmark.config.statistic.ProblemStatisticType;
import org.optaplanner.benchmark.impl.report.BenchmarkReport;
import org.optaplanner.benchmark.impl.result.ProblemBenchmarkResult;
import org.optaplanner.benchmark.impl.result.SingleBenchmarkResult;
import org.optaplanner.benchmark.impl.result.SubSingleBenchmarkResult;
import org.optaplanner.benchmark.impl.statistic.ProblemStatistic;
import org.optaplanner.benchmark.impl.statistic.SubSingleStatistic;
import org.optaplanner.benchmark.impl.statistic.common.MillisecondsSpentNumberFormat;

import com.thoughtworks.xstream.annotations.XStreamAlias;

@XStreamAlias("scoreCalculationSpeedProblemStatistic")
public class ScoreCalculationSpeedProblemStatistic extends ProblemStatistic {

    protected File graphFile = null;

    public ScoreCalculationSpeedProblemStatistic(ProblemBenchmarkResult problemBenchmarkResult) {
        super(problemBenchmarkResult, ProblemStatisticType.SCORE_CALCULATION_SPEED);
    }

    @Override
    public SubSingleStatistic createSubSingleStatistic(SubSingleBenchmarkResult subSingleBenchmarkResult) {
        return new ScoreCalculationSpeedSubSingleStatistic(subSingleBenchmarkResult);
    }

    /**
     * @return never null
     */
    @Override
    public List<File> getGraphFileList() {
        return Collections.singletonList(graphFile);
    }

    // ************************************************************************
    // Write methods
    // ************************************************************************

    @Override
    public void writeGraphFiles(BenchmarkReport benchmarkReport) {
        Locale locale = benchmarkReport.getLocale();
        NumberAxis xAxis = new NumberAxis("Time spent");
        xAxis.setNumberFormatOverride(new MillisecondsSpentNumberFormat(locale));
        NumberAxis yAxis = new NumberAxis("Score calculation speed per second");
        yAxis.setNumberFormatOverride(NumberFormat.getInstance(locale));
        yAxis.setAutoRangeIncludesZero(false);
        XYPlot plot = new XYPlot(null, xAxis, yAxis, null);
        plot.setOrientation(PlotOrientation.VERTICAL);
        int seriesIndex = 0;
        for (SingleBenchmarkResult singleBenchmarkResult : problemBenchmarkResult.getSingleBenchmarkResultList()) {
            XYSeries series = new XYSeries(singleBenchmarkResult.getSolverBenchmarkResult().getNameWithFavoriteSuffix());
            XYItemRenderer renderer = new XYLineAndShapeRenderer();
            if (singleBenchmarkResult.hasAllSuccess()) {
                ScoreCalculationSpeedSubSingleStatistic subSingleStatistic =
                        (ScoreCalculationSpeedSubSingleStatistic) singleBenchmarkResult
                                .getSubSingleStatistic(problemStatisticType);
                List<ScoreCalculationSpeedStatisticPoint> points = subSingleStatistic.getPointList();
                for (ScoreCalculationSpeedStatisticPoint point : points) {
                    long timeMillisSpent = point.getTimeMillisSpent();
                    long scoreCalculationSpeed = point.getScoreCalculationSpeed();
                    series.add(timeMillisSpent, scoreCalculationSpeed);
                }
            }
            plot.setDataset(seriesIndex, new XYSeriesCollection(series));

            if (singleBenchmarkResult.getSolverBenchmarkResult().isFavorite()) {
                // Make the favorite more obvious
                renderer.setSeriesStroke(0, new BasicStroke(2.0f));
            }
            plot.setRenderer(seriesIndex, renderer);
            seriesIndex++;
        }
        JFreeChart chart = new JFreeChart(problemBenchmarkResult.getName() + " score calculation speed statistic",
                JFreeChart.DEFAULT_TITLE_FONT, plot, true);
        graphFile = writeChartToImageFile(chart, problemBenchmarkResult.getName() + "ScoreCalculationSpeedStatistic");
    }

}
