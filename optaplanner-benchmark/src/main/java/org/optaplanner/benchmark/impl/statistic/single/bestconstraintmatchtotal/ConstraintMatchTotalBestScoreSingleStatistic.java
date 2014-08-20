/*
 * Copyright 2014 JBoss Inc
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

package org.optaplanner.benchmark.impl.statistic.single.bestconstraintmatchtotal;

import java.awt.BasicStroke;
import java.awt.Paint;
import java.io.File;
import java.text.NumberFormat;
import java.util.ArrayList;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Locale;
import java.util.Map;

import com.thoughtworks.xstream.annotations.XStreamAlias;
import com.thoughtworks.xstream.annotations.XStreamOmitField;
import org.jfree.chart.JFreeChart;
import org.jfree.chart.axis.NumberAxis;
import org.jfree.chart.plot.PlotOrientation;
import org.jfree.chart.plot.XYPlot;
import org.jfree.chart.renderer.xy.XYItemRenderer;
import org.jfree.chart.renderer.xy.XYLineAndShapeRenderer;
import org.jfree.chart.renderer.xy.YIntervalRenderer;
import org.jfree.data.xy.XYIntervalSeries;
import org.jfree.data.xy.XYIntervalSeriesCollection;
import org.jfree.data.xy.XYSeries;
import org.jfree.data.xy.XYSeriesCollection;
import org.optaplanner.benchmark.config.statistic.SingleStatisticType;
import org.optaplanner.benchmark.impl.report.BenchmarkReport;
import org.optaplanner.benchmark.impl.result.SingleBenchmarkResult;
import org.optaplanner.benchmark.impl.statistic.PureSingleStatistic;
import org.optaplanner.benchmark.impl.statistic.common.MillisecondsSpentNumberFormat;
import org.optaplanner.benchmark.impl.statistic.movecountperstep.MoveCountPerStepSingleStatistic;
import org.optaplanner.benchmark.impl.statistic.movecountperstep.MoveCountPerStepStatisticPoint;
import org.optaplanner.benchmark.impl.statistic.single.pickedmovetypebestscore.PickedMoveTypeBestScoreDiffStatisticPoint;
import org.optaplanner.core.api.score.Score;
import org.optaplanner.core.api.score.constraint.ConstraintMatchTotal;
import org.optaplanner.core.api.solver.Solver;
import org.optaplanner.core.impl.localsearch.scope.LocalSearchPhaseScope;
import org.optaplanner.core.impl.localsearch.scope.LocalSearchStepScope;
import org.optaplanner.core.impl.phase.event.PhaseLifecycleListenerAdapter;
import org.optaplanner.core.impl.phase.scope.AbstractPhaseScope;
import org.optaplanner.core.impl.phase.scope.AbstractStepScope;
import org.optaplanner.core.impl.score.ScoreUtils;
import org.optaplanner.core.impl.score.definition.ScoreDefinition;
import org.optaplanner.core.impl.solver.DefaultSolver;

@XStreamAlias("constraintMatchTotalBestScoreSingleStatistic")
public class ConstraintMatchTotalBestScoreSingleStatistic extends PureSingleStatistic<ConstraintMatchTotalBestScoreStatisticPoint> {

    @XStreamOmitField
    private ConstraintMatchTotalBestScoreSingleStatisticListener listener;

    protected File graphFile = null;

    public ConstraintMatchTotalBestScoreSingleStatistic(SingleBenchmarkResult singleBenchmarkResult) {
        super(singleBenchmarkResult, SingleStatisticType.CONSTRAINT_MATCH_TOTAL_BEST_SCORE);
        listener = new ConstraintMatchTotalBestScoreSingleStatisticListener();
    }

    /**
     * @return never null
     */
    public File getGraphFile() {
        return graphFile;
    }

    // ************************************************************************
    // Lifecycle methods
    // ************************************************************************

    public void open(Solver solver) {
        ((DefaultSolver) solver).addPhaseLifecycleListener(listener);
    }

    public void close(Solver solver) {
        ((DefaultSolver) solver).removePhaseLifecycleListener(listener);
    }

    private class ConstraintMatchTotalBestScoreSingleStatisticListener extends PhaseLifecycleListenerAdapter {

        @Override
        public void stepEnded(AbstractStepScope stepScope) {
            if (stepScope instanceof LocalSearchStepScope) {
                localSearchStepEnded((LocalSearchStepScope) stepScope);
            }
        }

        private void localSearchStepEnded(LocalSearchStepScope stepScope) {
            if (stepScope.getBestScoreImproved()) {
                long timeMillisSpent = stepScope.getPhaseScope().calculateSolverTimeMillisSpent();
                for (ConstraintMatchTotal constraintMatchTotal
                        : stepScope.getScoreDirector().getConstraintMatchTotals()) {
                    pointList.add(new ConstraintMatchTotalBestScoreStatisticPoint(
                            timeMillisSpent,
                            constraintMatchTotal.getConstraintId(),
                            constraintMatchTotal.getConstraintMatchCount(),
                            constraintMatchTotal.getWeightTotalAsNumber().doubleValue()));
                }
            }
        }

    }

    // ************************************************************************
    // CSV methods
    // ************************************************************************

    @Override
    protected String getCsvHeader() {
        return PickedMoveTypeBestScoreDiffStatisticPoint.buildCsvLine(
                "timeMillisSpent", "constraintId", "constraintMatchCount", "weightTotal");
    }

    @Override
    protected ConstraintMatchTotalBestScoreStatisticPoint createPointFromCsvLine(ScoreDefinition scoreDefinition,
            List<String> csvLine) {
        return new ConstraintMatchTotalBestScoreStatisticPoint(Long.valueOf(csvLine.get(0)), csvLine.get(1),
                Integer.valueOf(csvLine.get(2)), Double.valueOf(csvLine.get(3)));
    }

    // ************************************************************************
    // Write methods
    // ************************************************************************

    @Override
    public void writeGraphFiles(BenchmarkReport benchmarkReport) {
        Locale locale = benchmarkReport.getLocale();
        NumberAxis xAxis = new NumberAxis("Time spent");
        xAxis.setNumberFormatOverride(new MillisecondsSpentNumberFormat(locale));
        NumberAxis yAxis = new NumberAxis("Constraint match total weight");
        yAxis.setNumberFormatOverride(NumberFormat.getInstance(locale));
        yAxis.setAutoRangeIncludesZero(true);
        XYPlot plot = new XYPlot(null, xAxis, yAxis, null);
        plot.setOrientation(PlotOrientation.VERTICAL);

        Map<String, XYSeries> constraintIdToWeightSeriesMap = new LinkedHashMap<String, XYSeries>();
        for (ConstraintMatchTotalBestScoreStatisticPoint point : getPointList()) {
            String constraintId = point.getConstraintId();
            XYSeries weightSeries = constraintIdToWeightSeriesMap.get(constraintId);
            if (weightSeries == null) {
                weightSeries = new XYSeries(constraintId + " weight");
                constraintIdToWeightSeriesMap.put(constraintId, weightSeries);
            }
            long timeMillisSpent = point.getTimeMillisSpent();
            weightSeries.add(timeMillisSpent, point.getWeightTotal());
        }
        int seriesIndex = 0;
        for (XYSeries weightSeries : constraintIdToWeightSeriesMap.values()) {
            plot.setDataset(seriesIndex, new XYSeriesCollection(weightSeries));
            XYItemRenderer renderer = new XYLineAndShapeRenderer(true, false);
            plot.setRenderer(seriesIndex, renderer);
            seriesIndex++;
        }

        JFreeChart chart = new JFreeChart(singleBenchmarkResult.getName() + " constraint match total best score statistic",
                JFreeChart.DEFAULT_TITLE_FONT, plot, true);
        graphFile = writeChartToImageFile(chart, "ConstraintMatchTotalBestScoreStatistic");
    }

    private XYPlot createPlot(BenchmarkReport benchmarkReport, int scoreLevelIndex) {
        Locale locale = benchmarkReport.getLocale();
        NumberAxis xAxis = new NumberAxis("Time spent");
        xAxis.setNumberFormatOverride(new MillisecondsSpentNumberFormat(locale));
        NumberAxis yAxis = new NumberAxis("Best score diff level " + scoreLevelIndex);
        yAxis.setNumberFormatOverride(NumberFormat.getInstance(locale));
        yAxis.setAutoRangeIncludesZero(true);
        XYPlot plot = new XYPlot(null, xAxis, yAxis, null);
        plot.setOrientation(PlotOrientation.VERTICAL);
        return plot;
    }

}
