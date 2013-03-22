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

import org.optaplanner.benchmark.impl.DefaultPlannerBenchmark;
import org.optaplanner.benchmark.impl.ProblemBenchmark;
import org.optaplanner.benchmark.impl.SingleBenchmark;
import org.optaplanner.benchmark.impl.statistic.AbstractProblemStatistic;
import org.optaplanner.benchmark.impl.report.BenchmarkReport;
import org.optaplanner.benchmark.impl.statistic.MillisecondsSpendNumberFormat;
import org.optaplanner.benchmark.impl.statistic.ProblemStatisticType;
import org.optaplanner.benchmark.impl.statistic.SingleStatistic;
import org.optaplanner.core.api.score.Score;
import org.jfree.chart.JFreeChart;
import org.jfree.chart.axis.NumberAxis;
import org.jfree.chart.plot.PlotOrientation;
import org.jfree.chart.plot.XYPlot;
import org.jfree.chart.renderer.xy.StandardXYItemRenderer;
import org.jfree.chart.renderer.xy.XYItemRenderer;
import org.jfree.chart.renderer.xy.XYStepRenderer;
import org.jfree.data.xy.XYSeries;
import org.jfree.data.xy.XYSeriesCollection;

public class BestScoreProblemStatistic extends AbstractProblemStatistic {

    protected List<File> graphStatisticFileList = null;

    public BestScoreProblemStatistic(ProblemBenchmark problemBenchmark) {
        super(problemBenchmark, ProblemStatisticType.BEST_SOLUTION_CHANGED);
    }

    public SingleStatistic createSingleStatistic() {
        return new BestScoreSingleStatistic();
    }

    /**
     * @return never null, each path is relative to the {@link DefaultPlannerBenchmark#benchmarkReportDirectory}
     * (not {@link ProblemBenchmark#problemReportDirectory})
     */
    public List<String> getGraphFilePathList() {
        List<String> graphFilePathList = new ArrayList<String>(graphStatisticFileList.size());
        for (File graphStatisticFile : graphStatisticFileList) {
            graphFilePathList.add(toFilePath(graphStatisticFile));
        }
        return graphFilePathList;
    }

    // ************************************************************************
    // Write methods
    // ************************************************************************

    protected void writeCsvStatistic() {
        ProblemStatisticCsv csv = new ProblemStatisticCsv();
        for (SingleBenchmark singleBenchmark : problemBenchmark.getSingleBenchmarkList()) {
            if (singleBenchmark.isSuccess()) {
                BestScoreSingleStatistic singleStatistic = (BestScoreSingleStatistic)
                        singleBenchmark.getSingleStatistic(problemStatisticType);
                for (BestScoreSingleStatisticPoint point : singleStatistic.getPointList()) {
                    long timeMillisSpend = point.getTimeMillisSpend();
                    Score score = point.getScore();
                    if (score != null) {
                        csv.addPoint(singleBenchmark, timeMillisSpend, score.toString());
                    }
                }
            } else {
                csv.addPoint(singleBenchmark, 0L, "Failed");
            }
        }
        csvStatisticFile = new File(problemBenchmark.getProblemReportDirectory(),
                problemBenchmark.getName() + "BestScoreStatistic.csv");
        csv.writeCsvStatisticFile();
    }

    protected void writeGraphStatistic() {
        List<XYPlot> plotList = new ArrayList<XYPlot>(BenchmarkReport.CHARTED_SCORE_LEVEL_SIZE);
        int seriesIndex = 0;
        for (SingleBenchmark singleBenchmark : problemBenchmark.getSingleBenchmarkList()) {
            List<XYSeries> seriesList = new ArrayList<XYSeries>(BenchmarkReport.CHARTED_SCORE_LEVEL_SIZE);
            // No direct ascending lines between 2 points, but a stepping line instead
            XYItemRenderer renderer = new XYStepRenderer();
            if (singleBenchmark.isSuccess()) {
                BestScoreSingleStatistic singleStatistic = (BestScoreSingleStatistic)
                        singleBenchmark.getSingleStatistic(problemStatisticType);
                for (BestScoreSingleStatisticPoint point : singleStatistic.getPointList()) {
                    long timeMillisSpend = point.getTimeMillisSpend();
                    double[] levelValues = point.getScore().toDoubleLevels();
                    for (int i = 0; i < levelValues.length && i < BenchmarkReport.CHARTED_SCORE_LEVEL_SIZE; i++) {
                        if (i >= seriesList.size()) {
                            seriesList.add(new XYSeries(
                                    singleBenchmark.getSolverBenchmark().getNameWithFavoriteSuffix()));
                        }
                        seriesList.get(i).add(timeMillisSpend, levelValues[i]);
                    }
                }
                // TODO if startingSolution is initialized and no improvement is made, a horizontal line should be shown
                // Draw a horizontal line from the last new best step to how long the solver actually ran
                long timeMillisSpend = singleBenchmark.getTimeMillisSpend();
                double[] bestScoreLevels = singleBenchmark.getScore().toDoubleLevels();
                for (int i = 0; i < bestScoreLevels.length && i < BenchmarkReport.CHARTED_SCORE_LEVEL_SIZE; i++) {
                    if (i >= seriesList.size()) {
                        seriesList.add(new XYSeries(
                                singleBenchmark.getSolverBenchmark().getNameWithFavoriteSuffix()));
                    }
                    seriesList.get(i).add(timeMillisSpend, bestScoreLevels[i]);
                }
                if (singleStatistic.getPointList().size() <= 1) {
                    // Workaround for https://sourceforge.net/tracker/?func=detail&aid=3387330&group_id=15494&atid=115494
                    renderer = new StandardXYItemRenderer(StandardXYItemRenderer.SHAPES_AND_LINES);
                }
            }
            if (singleBenchmark.getSolverBenchmark().isFavorite()) {
                // Make the favorite more obvious
                renderer.setSeriesStroke(0, new BasicStroke(2.0f));
            }
            for (int i = 0; i < seriesList.size(); i++) {
                if (i >= plotList.size()) {
                    plotList.add(createPlot(i));
                }
                plotList.get(i).setDataset(seriesIndex, new XYSeriesCollection(seriesList.get(i)));
                plotList.get(i).setRenderer(seriesIndex, renderer);
            }
            seriesIndex++;
        }
        graphStatisticFileList = new ArrayList<File>(plotList.size());
        for (int scoreLevelIndex = 0; scoreLevelIndex < plotList.size(); scoreLevelIndex++) {
            JFreeChart chart = new JFreeChart(
                    problemBenchmark.getName() + " best score level " + scoreLevelIndex + " statistic",
                    JFreeChart.DEFAULT_TITLE_FONT, plotList.get(scoreLevelIndex), true);
            graphStatisticFileList.add(writeChartToImageFile(chart,
                    problemBenchmark.getName() + "BestScoreStatisticLevel" + scoreLevelIndex));
        }
    }

    private XYPlot createPlot(int scoreLevelIndex) {
        Locale locale = problemBenchmark.getPlannerBenchmark().getBenchmarkReport().getLocale();
        NumberAxis xAxis = new NumberAxis("Time spend");
        xAxis.setNumberFormatOverride(new MillisecondsSpendNumberFormat(locale));
        NumberAxis yAxis = new NumberAxis("Score level " + scoreLevelIndex);
        yAxis.setNumberFormatOverride(NumberFormat.getInstance(locale));
        yAxis.setAutoRangeIncludesZero(false);
        XYPlot plot = new XYPlot(null, xAxis, yAxis, null);
        plot.setOrientation(PlotOrientation.VERTICAL);
        return plot;
    }

}
