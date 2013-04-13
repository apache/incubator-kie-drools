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

package org.optaplanner.benchmark.impl.statistic.improvementratio;

import java.awt.BasicStroke;
import java.io.File;
import java.text.NumberFormat;
import java.util.HashMap;
import java.util.List;
import java.util.Locale;
import java.util.Map;

import org.jfree.chart.JFreeChart;
import org.jfree.chart.axis.NumberAxis;
import org.jfree.chart.plot.PlotOrientation;
import org.jfree.chart.plot.XYPlot;
import org.jfree.chart.renderer.xy.XYItemRenderer;
import org.jfree.chart.renderer.xy.XYStepRenderer;
import org.jfree.data.xy.XYSeries;
import org.jfree.data.xy.XYSeriesCollection;
import org.optaplanner.benchmark.impl.DefaultPlannerBenchmark;
import org.optaplanner.benchmark.impl.ProblemBenchmark;
import org.optaplanner.benchmark.impl.SingleBenchmark;
import org.optaplanner.benchmark.impl.statistic.AbstractProblemStatistic;
import org.optaplanner.benchmark.impl.statistic.MillisecondsSpendNumberFormat;
import org.optaplanner.benchmark.impl.statistic.ProblemStatisticType;
import org.optaplanner.benchmark.impl.statistic.SingleStatistic;
import org.optaplanner.core.impl.move.Move;

public class ImprovementRatioOverTimeProblemStatistic extends AbstractProblemStatistic {

    protected Map<String, File> graphStatisticFiles = null;

    public ImprovementRatioOverTimeProblemStatistic(final ProblemBenchmark problemBenchmark) {
        super(problemBenchmark, ProblemStatisticType.IMPROVEMENT_RATIO_OVER_TIME);
    }

    @Override
    public SingleStatistic createSingleStatistic() {
        return new ImprovementRatioOverTimeSingleStatistic();
    }

    /**
     * @return never null, each path is relative to the {@link DefaultPlannerBenchmark#benchmarkReportDirectory} (not
     *         {@link ProblemBenchmark#problemReportDirectory})
     */
    public Map<String, String> getGraphFilePaths() {
        final Map<String, String> graphFilePaths = new HashMap<String, String>(this.graphStatisticFiles.size());
        for (final Map.Entry<String, File> entry : this.graphStatisticFiles.entrySet()) {
            graphFilePaths.put(entry.getKey(), this.toFilePath(entry.getValue()));
        }
        return graphFilePaths;
    }

    // ************************************************************************
    // Write methods
    // ************************************************************************

    @Override
    protected void writeCsvStatistic() {
        // FIXME Planner doesn't support multiple CSV statistics per benchmark
    }

    @Override
    protected void writeGraphStatistic() {
        final Map<Class<? extends Move>, XYPlot> plots = new HashMap<Class<? extends Move>, XYPlot>();
        int seriesIndex = 0;
        for (final SingleBenchmark singleBenchmark : this.problemBenchmark.getSingleBenchmarkList()) {
            final Map<Class<? extends Move>, XYSeries> seriesMap = new HashMap<Class<? extends Move>, XYSeries>();
            // No direct ascending lines between 2 points, but a stepping line instead
            final XYItemRenderer renderer = new XYStepRenderer();
            if (singleBenchmark.isSuccess()) {
                final ImprovementRatioOverTimeSingleStatistic singleStatistic = (ImprovementRatioOverTimeSingleStatistic)
                        singleBenchmark.getSingleStatistic(this.problemStatisticType);
                for (final Map.Entry<Class<? extends Move>, List<ImprovementRatioOverTimeSingleStatisticPoint>> entry : singleStatistic.getPointLists().entrySet()) {
                    final Class<? extends Move> type = entry.getKey();
                    if (!seriesMap.containsKey(type)) {
                        seriesMap.put(type, new XYSeries(singleBenchmark.getSolverBenchmark().getNameWithFavoriteSuffix()));
                    }
                    final XYSeries series = seriesMap.get(type);
                    for (final ImprovementRatioOverTimeSingleStatisticPoint point : entry.getValue()) {
                        final long timeMillisSpend = point.getTimeMillisSpend();
                        final long ratio = point.getRatio();
                        series.add(timeMillisSpend, ratio);
                    }
                }
            }
            if (singleBenchmark.getSolverBenchmark().isFavorite()) {
                // Make the favorite more obvious
                renderer.setSeriesStroke(0, new BasicStroke(2.0f));
            }
            for (final Map.Entry<Class<? extends Move>, XYSeries> entry : seriesMap.entrySet()) {
                if (!plots.containsKey(entry.getKey())) {
                    plots.put(entry.getKey(), this.createPlot(entry.getKey()));
                }
                plots.get(entry.getKey()).setDataset(seriesIndex, new XYSeriesCollection(entry.getValue()));
                plots.get(entry.getKey()).setRenderer(seriesIndex, renderer);
            }
            for (int i = 0; i < seriesMap.size(); i++) {
            }
            seriesIndex++;
        }
        this.graphStatisticFiles = new HashMap<String, File>(plots.size());
        for (final Map.Entry<Class<? extends Move>, XYPlot> entry : plots.entrySet()) {
            final Class<? extends Move> type = entry.getKey();
            String id = type.getCanonicalName();
            String htmlSafeId = id.replace('.', '_');
            final JFreeChart chart = new JFreeChart(
                    this.problemBenchmark.getName() + " improvement ratio over time statistic, move type " + id,
                    JFreeChart.DEFAULT_TITLE_FONT, entry.getValue(), true);
            this.graphStatisticFiles.put(htmlSafeId, this.writeChartToImageFile(chart,
                    this.problemBenchmark.getName() + "ImprovementRatioOverTimeStatistic-" + id));
        }
    }

    private XYPlot createPlot(final Class<? extends Move> type) {
        final Locale locale = this.problemBenchmark.getPlannerBenchmark().getBenchmarkReport().getLocale();
        final NumberAxis xAxis = new NumberAxis("Time spend");
        xAxis.setNumberFormatOverride(new MillisecondsSpendNumberFormat(locale));
        final NumberAxis yAxis = new NumberAxis("% score-improving");
        yAxis.setNumberFormatOverride(NumberFormat.getInstance(locale));
        yAxis.setAutoRangeIncludesZero(false);
        final XYPlot plot = new XYPlot(null, xAxis, yAxis, null);
        plot.setOrientation(PlotOrientation.VERTICAL);
        return plot;
    }

}
