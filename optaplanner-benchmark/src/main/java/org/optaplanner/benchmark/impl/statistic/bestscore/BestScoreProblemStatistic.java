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
import org.optaplanner.benchmark.config.statistic.ProblemStatisticType;
import org.optaplanner.benchmark.impl.report.BenchmarkReport;
import org.optaplanner.benchmark.impl.result.ProblemBenchmarkResult;
import org.optaplanner.benchmark.impl.result.SingleBenchmarkResult;
import org.optaplanner.benchmark.impl.result.SubSingleBenchmarkResult;
import org.optaplanner.benchmark.impl.statistic.ProblemStatistic;
import org.optaplanner.benchmark.impl.statistic.SubSingleStatistic;
import org.optaplanner.benchmark.impl.statistic.common.MillisecondsSpentNumberFormat;

public class BestScoreProblemStatistic extends ProblemStatistic {

    protected List<File> graphFileList = null;

    private BestScoreProblemStatistic() {
        // Required by JAXB
    }

    public BestScoreProblemStatistic(ProblemBenchmarkResult problemBenchmarkResult) {
        super(problemBenchmarkResult, ProblemStatisticType.BEST_SCORE);
    }

    @Override
    public SubSingleStatistic createSubSingleStatistic(SubSingleBenchmarkResult subSingleBenchmarkResult) {
        return new BestScoreSubSingleStatistic(subSingleBenchmarkResult);
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
        List<XYPlot> plotList = new ArrayList<>(BenchmarkReport.CHARTED_SCORE_LEVEL_SIZE);
        int seriesIndex = 0;
        for (SingleBenchmarkResult singleBenchmarkResult : problemBenchmarkResult.getSingleBenchmarkResultList()) {
            List<XYSeries> seriesList = new ArrayList<>(BenchmarkReport.CHARTED_SCORE_LEVEL_SIZE);
            // No direct ascending lines between 2 points, but a stepping line instead
            XYItemRenderer renderer = new XYStepRenderer();
            if (singleBenchmarkResult.hasAllSuccess()) {
                BestScoreSubSingleStatistic subSingleStatistic = (BestScoreSubSingleStatistic) singleBenchmarkResult
                        .getSubSingleStatistic(problemStatisticType);
                List<BestScoreStatisticPoint> points = subSingleStatistic.getPointList();
                for (BestScoreStatisticPoint point : points) {
                    if (!point.getScore().isSolutionInitialized()) {
                        continue;
                    }
                    long timeMillisSpent = point.getTimeMillisSpent();
                    double[] levelValues = point.getScore().toLevelDoubles();
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
                double[] bestScoreLevels = singleBenchmarkResult.getMedian().getScore().toLevelDoubles();
                for (int i = 0; i < bestScoreLevels.length && i < BenchmarkReport.CHARTED_SCORE_LEVEL_SIZE; i++) {
                    if (i >= seriesList.size()) {
                        seriesList.add(new XYSeries(
                                singleBenchmarkResult.getSolverBenchmarkResult().getNameWithFavoriteSuffix()));
                    }
                    seriesList.get(i).add(timeMillisSpent, bestScoreLevels[i]);
                }
                if (subSingleStatistic.getPointList().size() <= 1) {
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
        graphFileList = new ArrayList<>(plotList.size());
        for (int scoreLevelIndex = 0; scoreLevelIndex < plotList.size(); scoreLevelIndex++) {
            String scoreLevelLabel = problemBenchmarkResult.findScoreLevelLabel(scoreLevelIndex);
            JFreeChart chart = new JFreeChart(
                    problemBenchmarkResult.getName() + " best " + scoreLevelLabel + " statistic",
                    JFreeChart.DEFAULT_TITLE_FONT, plotList.get(scoreLevelIndex), true);
            graphFileList.add(writeChartToImageFile(chart,
                    problemBenchmarkResult.getName() + "BestScoreStatisticLevel" + scoreLevelIndex));
        }
    }

    private XYPlot createPlot(BenchmarkReport benchmarkReport, int scoreLevelIndex) {
        Locale locale = benchmarkReport.getLocale();
        NumberAxis xAxis = new NumberAxis("Time spent");
        xAxis.setNumberFormatOverride(new MillisecondsSpentNumberFormat(locale));
        String scoreLevelLabel = problemBenchmarkResult.findScoreLevelLabel(scoreLevelIndex);
        NumberAxis yAxis = new NumberAxis("Best " + scoreLevelLabel);
        yAxis.setNumberFormatOverride(NumberFormat.getInstance(locale));
        yAxis.setAutoRangeIncludesZero(false);
        XYPlot plot = new XYPlot(null, xAxis, yAxis, null);
        plot.setOrientation(PlotOrientation.VERTICAL);
        return plot;
    }

}
