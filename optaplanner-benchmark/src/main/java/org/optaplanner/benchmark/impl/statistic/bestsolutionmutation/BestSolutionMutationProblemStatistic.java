/*
 * Copyright 2013 JBoss Inc
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

package org.optaplanner.benchmark.impl.statistic.bestsolutionmutation;

import java.awt.BasicStroke;
import java.io.BufferedReader;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.IOException;
import java.text.NumberFormat;
import java.util.ArrayList;
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
import org.optaplanner.benchmark.impl.DefaultPlannerBenchmark;
import org.optaplanner.benchmark.impl.ProblemBenchmark;
import org.optaplanner.benchmark.impl.SingleBenchmark;
import org.optaplanner.benchmark.impl.report.BenchmarkReport;
import org.optaplanner.benchmark.impl.statistic.AbstractProblemStatistic;
import org.optaplanner.benchmark.impl.statistic.common.MillisecondsSpendNumberFormat;
import org.optaplanner.benchmark.impl.statistic.ProblemStatisticType;
import org.optaplanner.benchmark.impl.statistic.SingleStatistic;
import org.optaplanner.core.impl.score.definition.ScoreDefinition;

public class BestSolutionMutationProblemStatistic extends AbstractProblemStatistic {

    protected File graphFile = null;

    public BestSolutionMutationProblemStatistic(ProblemBenchmark problemBenchmark) {
        super(problemBenchmark, ProblemStatisticType.BEST_SOLUTION_MUTATION);
    }

    @Override
    public SingleStatistic createSingleStatistic(SingleBenchmark singleBenchmark) {
        return new BestSolutionMutationSingleStatistic(singleBenchmark);
    }

    /**
     * @return never null
     */
    public File getGraphFile() {
        return graphFile;
    }

    // ************************************************************************
    // Write methods
    // ************************************************************************

    @Override
    public void writeGraphFiles(BenchmarkReport benchmarkReport) {
        Locale locale = benchmarkReport.getLocale();
        NumberAxis xAxis = new NumberAxis("Time spend");
        xAxis.setNumberFormatOverride(new MillisecondsSpendNumberFormat(locale));
        NumberAxis yAxis = new NumberAxis("Best solution mutation count");
        yAxis.setNumberFormatOverride(NumberFormat.getInstance(locale));
        yAxis.setAutoRangeIncludesZero(true);
        XYPlot plot = new XYPlot(null, xAxis, yAxis, null);
        plot.setOrientation(PlotOrientation.VERTICAL);
        int seriesIndex = 0;
        for (SingleBenchmark singleBenchmark : problemBenchmark.getSingleBenchmarkList()) {
            XYSeries series = new XYSeries(singleBenchmark.getSolverBenchmark().getNameWithFavoriteSuffix());
            XYItemRenderer renderer = new XYLineAndShapeRenderer();
            if (singleBenchmark.isSuccess()) {
                BestSolutionMutationSingleStatistic singleStatistic = (BestSolutionMutationSingleStatistic)
                        singleBenchmark.getSingleStatistic(problemStatisticType);
                for (BestSolutionMutationSingleStatisticPoint point : singleStatistic.getPointList()) {
                    long timeMillisSpend = point.getTimeMillisSpend();
                    long mutationCount = point.getMutationCount();
                    series.add(timeMillisSpend, mutationCount);
                }
            }
            plot.setDataset(seriesIndex, new XYSeriesCollection(series));

            if (singleBenchmark.getSolverBenchmark().isFavorite()) {
                // Make the favorite more obvious
                renderer.setSeriesStroke(0, new BasicStroke(2.0f));
            }
            plot.setRenderer(seriesIndex, renderer);
            seriesIndex++;
        }
        JFreeChart chart = new JFreeChart(problemBenchmark.getName() + " best solution mutation statistic",
                JFreeChart.DEFAULT_TITLE_FONT, plot, true);
        graphFile = writeChartToImageFile(chart, problemBenchmark.getName() + "BestSolutionMutationStatistic");
    }

}
