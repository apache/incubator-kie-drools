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

package org.optaplanner.benchmark.impl.statistic.movecountperstep;

import java.awt.BasicStroke;
import java.awt.Paint;
import java.io.File;
import java.text.NumberFormat;
import java.util.Collections;
import java.util.List;
import java.util.Locale;

import org.jfree.chart.JFreeChart;
import org.jfree.chart.axis.NumberAxis;
import org.jfree.chart.plot.DefaultDrawingSupplier;
import org.jfree.chart.plot.DrawingSupplier;
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

@XStreamAlias("moveCountPerStepProblemStatistic")
public class MoveCountPerStepProblemStatistic extends ProblemStatistic {

    protected File graphFile = null;

    public MoveCountPerStepProblemStatistic(ProblemBenchmarkResult problemBenchmarkResult) {
        super(problemBenchmarkResult, ProblemStatisticType.MOVE_COUNT_PER_STEP);
    }

    @Override
    public SubSingleStatistic createSubSingleStatistic(SubSingleBenchmarkResult subSingleBenchmarkResult) {
        return new MoveCountPerStepSubSingleStatistic(subSingleBenchmarkResult);
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
        NumberAxis yAxis = new NumberAxis("Accepted/selected moves per step");
        yAxis.setNumberFormatOverride(NumberFormat.getInstance(locale));
        XYPlot plot = new XYPlot(null, xAxis, yAxis, null);
        DrawingSupplier drawingSupplier = new DefaultDrawingSupplier();
        plot.setOrientation(PlotOrientation.VERTICAL);

        int seriesIndex = 0;
        for (SingleBenchmarkResult singleBenchmarkResult : problemBenchmarkResult.getSingleBenchmarkResultList()) {
            XYSeries acceptedSeries = new XYSeries(
                    singleBenchmarkResult.getSolverBenchmarkResult().getNameWithFavoriteSuffix() + " accepted");
            XYSeries selectedSeries = new XYSeries(
                    singleBenchmarkResult.getSolverBenchmarkResult().getNameWithFavoriteSuffix() + " selected");
            XYItemRenderer renderer = new XYLineAndShapeRenderer(true, false);
            if (singleBenchmarkResult.hasAllSuccess()) {
                MoveCountPerStepSubSingleStatistic subSingleStatistic =
                        (MoveCountPerStepSubSingleStatistic) singleBenchmarkResult.getSubSingleStatistic(problemStatisticType);
                List<MoveCountPerStepStatisticPoint> list = subSingleStatistic.getPointList();
                for (MoveCountPerStepStatisticPoint point : list) {
                    long timeMillisSpent = point.getTimeMillisSpent();
                    long acceptedMoveCount = point.getMoveCountPerStepMeasurement().getAcceptedMoveCount();
                    long selectedMoveCount = point.getMoveCountPerStepMeasurement().getSelectedMoveCount();
                    acceptedSeries.add(timeMillisSpent, acceptedMoveCount);
                    selectedSeries.add(timeMillisSpent, selectedMoveCount);
                }
            }
            XYSeriesCollection seriesCollection = new XYSeriesCollection();
            seriesCollection.addSeries(acceptedSeries);
            seriesCollection.addSeries(selectedSeries);
            plot.setDataset(seriesIndex, seriesCollection);

            if (singleBenchmarkResult.getSolverBenchmarkResult().isFavorite()) {
                // Make the favorite more obvious
                renderer.setSeriesStroke(0, new BasicStroke(2.0f));
                // Dashed line for selected move count
                renderer.setSeriesStroke(1, new BasicStroke(
                        2.0f, BasicStroke.CAP_ROUND, BasicStroke.JOIN_ROUND, 1.0f, new float[] { 2.0f, 6.0f }, 0.0f));
            } else {
                // Dashed line for selected move count
                renderer.setSeriesStroke(1, new BasicStroke(
                        1.0f, BasicStroke.CAP_ROUND, BasicStroke.JOIN_ROUND, 1.0f, new float[] { 2.0f, 6.0f }, 0.0f));
            }
            // Render both lines in the same color
            Paint linePaint = drawingSupplier.getNextPaint();
            renderer.setSeriesPaint(0, linePaint);
            renderer.setSeriesPaint(1, linePaint);
            plot.setRenderer(seriesIndex, renderer);
            seriesIndex++;
        }

        JFreeChart chart = new JFreeChart(problemBenchmarkResult.getName() + " move count per step statistic",
                JFreeChart.DEFAULT_TITLE_FONT, plot, true);
        graphFile = writeChartToImageFile(chart, problemBenchmarkResult.getName() + "MoveCountPerStepStatistic");
    }

}
