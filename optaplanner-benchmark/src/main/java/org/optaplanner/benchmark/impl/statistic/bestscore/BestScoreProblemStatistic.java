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

import com.thoughtworks.xstream.annotations.XStreamAlias;
import org.jfree.chart.JFreeChart;
import org.jfree.chart.axis.NumberAxis;
import org.jfree.chart.plot.PlotOrientation;
import org.jfree.chart.plot.XYPlot;
import org.jfree.chart.renderer.xy.StandardXYItemRenderer;
import org.jfree.chart.renderer.xy.XYItemRenderer;
import org.jfree.chart.renderer.xy.XYStepRenderer;
import org.jfree.data.xy.XYSeries;
import org.jfree.data.xy.XYSeriesCollection;
import org.optaplanner.benchmark.config.statistic.ProblemStatisticType;
import org.optaplanner.benchmark.impl.report.BenchmarkReport;
import org.optaplanner.benchmark.impl.result.ProblemBenchmarkResult;
import org.optaplanner.benchmark.impl.result.SingleBenchmarkResult;
import org.optaplanner.benchmark.impl.statistic.ProblemStatistic;
import org.optaplanner.benchmark.impl.statistic.SingleStatistic;
import org.optaplanner.benchmark.impl.result.SolverProblemBenchmarkResult;
import org.optaplanner.benchmark.impl.statistic.common.MillisecondsSpentNumberFormat;
import org.optaplanner.core.impl.score.ScoreUtils;

@XStreamAlias("bestScoreProblemStatistic")
public class BestScoreProblemStatistic extends ProblemStatistic {

    protected List<File> graphFileList = null;

    public BestScoreProblemStatistic(ProblemBenchmarkResult problemBenchmarkResult) {
        super(problemBenchmarkResult, ProblemStatisticType.BEST_SCORE);
    }

    @Override
    public SingleStatistic createSingleStatistic(SolverProblemBenchmarkResult solverProblemBenchmarkResult) {
        return new BestScoreSingleStatistic(solverProblemBenchmarkResult);
    }

    /**
     * @return never null
     */
    @Override
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
        for (SingleBenchmarkResult singleBenchmarkResult : problemBenchmarkResult.getSingleBenchmarkResultList()) {
            List<XYSeries> seriesList = new ArrayList<XYSeries>(BenchmarkReport.CHARTED_SCORE_LEVEL_SIZE);
            // No direct ascending lines between 2 points, but a stepping line instead
            XYItemRenderer renderer = new XYStepRenderer();
            if (singleBenchmarkResult.isSuccess()) {
                BestScoreSingleStatistic singleStatistic = (BestScoreSingleStatistic)
                        singleBenchmarkResult.getSingleStatistic(problemStatisticType);
                for (BestScoreStatisticPoint point : singleStatistic.getPointList()) {
                    long timeMillisSpent = point.getTimeMillisSpent();
                    double[] levelValues = ScoreUtils.extractLevelDoubles(point.getScore());
                    for (int i = 0; i < levelValues.length && i < BenchmarkReport.CHARTED_SCORE_LEVEL_SIZE; i++) {
                        if (i >= seriesList.size()) {
                            seriesList.add(new XYSeries(
                                    singleBenchmarkResult.getSolverBenchmarkResult().getNameWithFavoriteSuffix()));
                        }
                        seriesList.get(i).add(timeMillisSpent, levelValues[i]);
                    }
                }
                // TODO if startingSolution is initialized and no improvement is made, a horizontal line should be shown
                // Draw a horizontal line from the last new best step to how long the solver actually ran
                long timeMillisSpent = singleBenchmarkResult.getTimeMillisSpent();
                double[] bestScoreLevels = ScoreUtils.extractLevelDoubles(singleBenchmarkResult.getScore());
                for (int i = 0; i < bestScoreLevels.length && i < BenchmarkReport.CHARTED_SCORE_LEVEL_SIZE; i++) {
                    if (i >= seriesList.size()) {
                        seriesList.add(new XYSeries(
                                singleBenchmarkResult.getSolverBenchmarkResult().getNameWithFavoriteSuffix()));
                    }
                    seriesList.get(i).add(timeMillisSpent, bestScoreLevels[i]);
                }
                if (singleStatistic.getPointList().size() <= 1) {
                    // Workaround for https://sourceforge.net/tracker/?func=detail&aid=3387330&group_id=15494&atid=115494
                    renderer = new StandardXYItemRenderer(StandardXYItemRenderer.SHAPES_AND_LINES);
                }
            }
            if (singleBenchmarkResult.getSolverBenchmarkResult().isFavorite()) {
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
                    problemBenchmarkResult.getName() + " best score level " + scoreLevelIndex + " statistic",
                    JFreeChart.DEFAULT_TITLE_FONT, plotList.get(scoreLevelIndex), true);
            graphFileList.add(writeChartToImageFile(chart,
                    problemBenchmarkResult.getName() + "BestScoreStatisticLevel" + scoreLevelIndex));
        }
    }

    private XYPlot createPlot(BenchmarkReport benchmarkReport, int scoreLevelIndex) {
        Locale locale = benchmarkReport.getLocale();
        NumberAxis xAxis = new NumberAxis("Time spent");
        xAxis.setNumberFormatOverride(new MillisecondsSpentNumberFormat(locale));
        NumberAxis yAxis = new NumberAxis("Best score level " + scoreLevelIndex);
        yAxis.setNumberFormatOverride(NumberFormat.getInstance(locale));
        yAxis.setAutoRangeIncludesZero(false);
        XYPlot plot = new XYPlot(null, xAxis, yAxis, null);
        plot.setOrientation(PlotOrientation.VERTICAL);
        return plot;
    }

}
