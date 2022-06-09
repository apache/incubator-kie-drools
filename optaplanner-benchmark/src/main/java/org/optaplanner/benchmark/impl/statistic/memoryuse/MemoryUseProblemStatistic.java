package org.optaplanner.benchmark.impl.statistic.memoryuse;

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

public class MemoryUseProblemStatistic extends ProblemStatistic {

    protected File graphFile = null;

    public MemoryUseProblemStatistic(ProblemBenchmarkResult problemBenchmarkResult) {
        super(problemBenchmarkResult, ProblemStatisticType.MEMORY_USE);
    }

    @Override
    public SubSingleStatistic createSubSingleStatistic(SubSingleBenchmarkResult subSingleBenchmarkResult) {
        return new MemoryUseSubSingleStatistic(subSingleBenchmarkResult);
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
        NumberAxis yAxis = new NumberAxis("Memory (bytes)");
        yAxis.setNumberFormatOverride(NumberFormat.getInstance(locale));
        XYPlot plot = new XYPlot(null, xAxis, yAxis, null);
        plot.setOrientation(PlotOrientation.VERTICAL);
        int seriesIndex = 0;
        for (SingleBenchmarkResult singleBenchmarkResult : problemBenchmarkResult.getSingleBenchmarkResultList()) {
            XYSeries usedSeries = new XYSeries(
                    singleBenchmarkResult.getSolverBenchmarkResult().getNameWithFavoriteSuffix() + " used");
            // TODO enable max memory, but in the same color as used memory, but with a dotted line instead
            //            XYSeries maxSeries = new XYSeries(
            //                    singleBenchmarkResult.getSolverBenchmarkResult().getNameWithFavoriteSuffix() + " max");
            XYItemRenderer renderer = new XYLineAndShapeRenderer();
            if (singleBenchmarkResult.hasAllSuccess()) {
                MemoryUseSubSingleStatistic subSingleStatistic = (MemoryUseSubSingleStatistic) singleBenchmarkResult
                        .getSubSingleStatistic(problemStatisticType);
                List<MemoryUseStatisticPoint> points = subSingleStatistic.getPointList();
                for (MemoryUseStatisticPoint point : points) {
                    long timeMillisSpent = point.getTimeMillisSpent();
                    MemoryUseMeasurement memoryUseMeasurement = point.getMemoryUseMeasurement();
                    usedSeries.add(timeMillisSpent, memoryUseMeasurement.getUsedMemory());
                    //                    maxSeries.add(timeMillisSpent, memoryUseMeasurement.getMaxMemory());
                }
            }
            XYSeriesCollection seriesCollection = new XYSeriesCollection();
            seriesCollection.addSeries(usedSeries);
            //            seriesCollection.addSeries(maxSeries);
            plot.setDataset(seriesIndex, seriesCollection);

            if (singleBenchmarkResult.getSolverBenchmarkResult().isFavorite()) {
                // Make the favorite more obvious
                renderer.setSeriesStroke(0, new BasicStroke(2.0f));
                //                renderer.setSeriesStroke(1, new BasicStroke(2.0f));
            }
            plot.setRenderer(seriesIndex, renderer);
            seriesIndex++;
        }
        JFreeChart chart = new JFreeChart(problemBenchmarkResult.getName() + " memory use statistic",
                JFreeChart.DEFAULT_TITLE_FONT, plot, true);
        graphFile = writeChartToImageFile(chart, problemBenchmarkResult.getName() + "MemoryUseStatistic");
    }

    @Override
    protected void fillWarningList() {
        if (problemBenchmarkResult.getPlannerBenchmarkResult().hasMultipleParallelBenchmarks()) {
            warningList.add("This memory use statistic shows the sum of the memory of all benchmarks "
                    + "that ran in parallel, due to parallelBenchmarkCount ("
                    + problemBenchmarkResult.getPlannerBenchmarkResult().getParallelBenchmarkCount() + ").");
        }
    }

}
