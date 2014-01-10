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

package org.optaplanner.benchmark.impl.statistic.bestscore;

import java.awt.BasicStroke;
import java.io.File;
import java.text.NumberFormat;
import java.util.ArrayList;
import java.util.List;
import java.util.Locale;

import org.jfree.chart.JFreeChart;
import org.jfree.chart.axis.NumberAxis;
import org.jfree.chart.plot.PlotOrientation;
import org.jfree.chart.plot.XYPlot;
import org.jfree.chart.renderer.xy.StandardXYItemRenderer;
import org.jfree.chart.renderer.xy.XYItemRenderer;
import org.jfree.chart.renderer.xy.XYStepRenderer;
import org.jfree.data.xy.XYSeries;
import org.jfree.data.xy.XYSeriesCollection;
import org.optaplanner.benchmark.impl.ProblemBenchmark;
import org.optaplanner.benchmark.impl.SingleBenchmarkResult;
import org.optaplanner.benchmark.impl.report.BenchmarkReport;
import org.optaplanner.benchmark.impl.statistic.AbstractProblemStatistic;
import org.optaplanner.benchmark.impl.statistic.common.MillisecondsSpendNumberFormat;
import org.optaplanner.benchmark.impl.statistic.ProblemStatisticType;
import org.optaplanner.benchmark.impl.statistic.SingleStatistic;
import org.optaplanner.core.impl.score.ScoreUtils;

public class BestScoreProblemStatistic extends AbstractProblemStatistic {

    protected List<File> graphFileList = null;

    public BestScoreProblemStatistic(ProblemBenchmark problemBenchmark) {
        super(problemBenchmark, ProblemStatisticType.BEST_SCORE);
    }

    @Override
    public SingleStatistic createSingleStatistic(SingleBenchmarkResult singleBenchmarkResult) {
        return new BestScoreSingleStatistic(singleBenchmarkResult);
    }

    /**
     * @return never null
     */
    public List<File> getGraphFileList() {
        return graphFileList;
    }

    // ************************************************************************
    // Write methods
    // ************************************************************************

    @Override
    public void writeGraphFiles(BenchmarkReport benchmarkReport) {
        List<XYPlot> plotList = new ArrayList<XYPlot>(BenchmarkReport.CHARTED_SCORE_LEVEL_SIZE);
        int seriesIndex = 0;
        for (SingleBenchmarkResult singleBenchmarkResult : problemBenchmark.getSingleBenchmarkResultList()) {
            List<XYSeries> seriesList = new ArrayList<XYSeries>(BenchmarkReport.CHARTED_SCORE_LEVEL_SIZE);
            // No direct ascending lines between 2 points, but a stepping line instead
            XYItemRenderer renderer = new XYStepRenderer();
            if (singleBenchmarkResult.isSuccess()) {
                BestScoreSingleStatistic singleStatistic = (BestScoreSingleStatistic)
                        singleBenchmarkResult.getSingleStatistic(problemStatisticType);
                for (BestScoreSingleStatisticPoint point : singleStatistic.getPointList()) {
                    long timeMillisSpend = point.getTimeMillisSpend();
                    double[] levelValues = ScoreUtils.extractLevelDoubles(point.getScore());
                    for (int i = 0; i < levelValues.length && i < BenchmarkReport.CHARTED_SCORE_LEVEL_SIZE; i++) {
                        if (i >= seriesList.size()) {
                            seriesList.add(new XYSeries(
                                    singleBenchmarkResult.getSolverBenchmark().getNameWithFavoriteSuffix()));
                        }
                        seriesList.get(i).add(timeMillisSpend, levelValues[i]);
                    }
                }
                // TODO if startingSolution is initialized and no improvement is made, a horizontal line should be shown
                // Draw a horizontal line from the last new best step to how long the solver actually ran
                long timeMillisSpend = singleBenchmarkResult.getTimeMillisSpend();
                double[] bestScoreLevels = ScoreUtils.extractLevelDoubles(singleBenchmarkResult.getScore());
                for (int i = 0; i < bestScoreLevels.length && i < BenchmarkReport.CHARTED_SCORE_LEVEL_SIZE; i++) {
                    if (i >= seriesList.size()) {
                        seriesList.add(new XYSeries(
                                singleBenchmarkResult.getSolverBenchmark().getNameWithFavoriteSuffix()));
                    }
                    seriesList.get(i).add(timeMillisSpend, bestScoreLevels[i]);
                }
                if (singleStatistic.getPointList().size() <= 1) {
                    // Workaround for https://sourceforge.net/tracker/?func=detail&aid=3387330&group_id=15494&atid=115494
                    renderer = new StandardXYItemRenderer(StandardXYItemRenderer.SHAPES_AND_LINES);
                }
            }
            if (singleBenchmarkResult.getSolverBenchmark().isFavorite()) {
                // Make the favorite more obvious
                renderer.setSeriesStroke(0, new BasicStroke(2.0f));
            }
            for (int i = 0; i < seriesList.size(); i++) {
                if (i >= plotList.size()) {
                    plotList.add(createPlot(benchmarkReport, i));
                }
                plotList.get(i).setDataset(seriesIndex, new XYSeriesCollection(seriesList.get(i)));
                plotList.get(i).setRenderer(seriesIndex, renderer);
            }
            seriesIndex++;
        }
        graphFileList = new ArrayList<File>(plotList.size());
        for (int scoreLevelIndex = 0; scoreLevelIndex < plotList.size(); scoreLevelIndex++) {
            JFreeChart chart = new JFreeChart(
                    problemBenchmark.getName() + " best score level " + scoreLevelIndex + " statistic",
                    JFreeChart.DEFAULT_TITLE_FONT, plotList.get(scoreLevelIndex), true);
            graphFileList.add(writeChartToImageFile(chart,
                    problemBenchmark.getName() + "BestScoreStatisticLevel" + scoreLevelIndex));
        }
    }

    private XYPlot createPlot(BenchmarkReport benchmarkReport, int scoreLevelIndex) {
        Locale locale = benchmarkReport.getLocale();
        NumberAxis xAxis = new NumberAxis("Time spend");
        xAxis.setNumberFormatOverride(new MillisecondsSpendNumberFormat(locale));
        NumberAxis yAxis = new NumberAxis("Best score level " + scoreLevelIndex);
        yAxis.setNumberFormatOverride(NumberFormat.getInstance(locale));
        yAxis.setAutoRangeIncludesZero(false);
        XYPlot plot = new XYPlot(null, xAxis, yAxis, null);
        plot.setOrientation(PlotOrientation.VERTICAL);
        return plot;
    }

}
