/*
 * Copyright 2014 Red Hat, Inc. and/or its affiliates.
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

package org.optaplanner.benchmark.impl.statistic.subsingle.pickedmovetypestepscore;

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
import org.jfree.chart.renderer.xy.YIntervalRenderer;
import org.jfree.data.xy.XYIntervalSeries;
import org.jfree.data.xy.XYIntervalSeriesCollection;
import org.optaplanner.benchmark.config.statistic.SingleStatisticType;
import org.optaplanner.benchmark.impl.report.BenchmarkReport;
import org.optaplanner.benchmark.impl.result.SubSingleBenchmarkResult;
import org.optaplanner.benchmark.impl.statistic.PureSubSingleStatistic;
import org.optaplanner.benchmark.impl.statistic.common.MillisecondsSpentNumberFormat;
import org.optaplanner.core.api.domain.solution.Solution;
import org.optaplanner.core.api.score.Score;
import org.optaplanner.core.api.solver.Solver;
import org.optaplanner.core.impl.localsearch.scope.LocalSearchPhaseScope;
import org.optaplanner.core.impl.localsearch.scope.LocalSearchStepScope;
import org.optaplanner.core.impl.phase.event.PhaseLifecycleListenerAdapter;
import org.optaplanner.core.impl.phase.scope.AbstractPhaseScope;
import org.optaplanner.core.impl.phase.scope.AbstractStepScope;
import org.optaplanner.core.impl.score.ScoreUtils;
import org.optaplanner.core.impl.score.definition.ScoreDefinition;
import org.optaplanner.core.impl.solver.DefaultSolver;

@XStreamAlias("pickedMoveTypeStepScoreDiffSubSingleStatistic")
public class PickedMoveTypeStepScoreDiffSubSingleStatistic extends PureSubSingleStatistic<PickedMoveTypeStepScoreDiffStatisticPoint> {

    @XStreamOmitField
    private PickedMoveTypeStepScoreDiffSubSingleStatisticListener listener;

    @XStreamOmitField
    protected List<File> graphFileList = null;

    public PickedMoveTypeStepScoreDiffSubSingleStatistic(SubSingleBenchmarkResult subSingleBenchmarkResult) {
        super(subSingleBenchmarkResult, SingleStatisticType.PICKED_MOVE_TYPE_STEP_SCORE_DIFF);
        listener = new PickedMoveTypeStepScoreDiffSubSingleStatisticListener();
    }

    /**
     * @return never null
     */
    @Override
    public List<File> getGraphFileList() {
        return graphFileList;
    }

    // ************************************************************************
    // Lifecycle methods
    // ************************************************************************

    public void open(Solver<Solution> solver) {
        ((DefaultSolver<Solution>) solver).addPhaseLifecycleListener(listener);
    }

    public void close(Solver<Solution> solver) {
        ((DefaultSolver<Solution>) solver).removePhaseLifecycleListener(listener);
    }

    private class PickedMoveTypeStepScoreDiffSubSingleStatisticListener extends PhaseLifecycleListenerAdapter {

        private Score oldStepScore = null;

        @Override
        public void phaseStarted(AbstractPhaseScope phaseScope) {
            if (phaseScope instanceof LocalSearchPhaseScope) {
                oldStepScore = phaseScope.getStartingScore();
            }
        }

        @Override
        public void phaseEnded(AbstractPhaseScope phaseScope) {
            if (phaseScope instanceof LocalSearchPhaseScope) {
                oldStepScore = null;
            }
        }

        @Override
        public void stepEnded(AbstractStepScope stepScope) {
            if (stepScope instanceof LocalSearchStepScope) {
                localSearchStepEnded((LocalSearchStepScope) stepScope);
            }
        }

        private void localSearchStepEnded(LocalSearchStepScope stepScope) {
            long timeMillisSpent = stepScope.getPhaseScope().calculateSolverTimeMillisSpent();
            String moveType = stepScope.getStep().getSimpleMoveTypeDescription();
            Score newStepScore = stepScope.getScore();
            Score stepScoreDiff = newStepScore.subtract(oldStepScore);
            oldStepScore = newStepScore;
            pointList.add(new PickedMoveTypeStepScoreDiffStatisticPoint(
                    timeMillisSpent, moveType, stepScoreDiff));
        }

    }

    // ************************************************************************
    // CSV methods
    // ************************************************************************

    @Override
    protected String getCsvHeader() {
        return PickedMoveTypeStepScoreDiffStatisticPoint.buildCsvLine("timeMillisSpent", "moveType", "stepScoreDiff");
    }

    @Override
    protected PickedMoveTypeStepScoreDiffStatisticPoint createPointFromCsvLine(ScoreDefinition scoreDefinition,
            List<String> csvLine) {
        return new PickedMoveTypeStepScoreDiffStatisticPoint(Long.valueOf(csvLine.get(0)),
                csvLine.get(1), scoreDefinition.parseScore(csvLine.get(2)));
    }

    // ************************************************************************
    // Write methods
    // ************************************************************************

    @Override
    public void writeGraphFiles(BenchmarkReport benchmarkReport) {
        List<Map<String, XYIntervalSeries>> moveTypeToSeriesMapList
                = new ArrayList<Map<String, XYIntervalSeries>>(BenchmarkReport.CHARTED_SCORE_LEVEL_SIZE);
        for (PickedMoveTypeStepScoreDiffStatisticPoint point : getPointList()) {
            long timeMillisSpent = point.getTimeMillisSpent();
            String moveType = point.getMoveType();
            double[] levelValues = ScoreUtils.extractLevelDoubles(point.getStepScoreDiff());
            for (int i = 0; i < levelValues.length && i < BenchmarkReport.CHARTED_SCORE_LEVEL_SIZE; i++) {
                if (i >= moveTypeToSeriesMapList.size()) {
                    moveTypeToSeriesMapList.add(new LinkedHashMap<String, XYIntervalSeries>());
                }
                Map<String, XYIntervalSeries> moveTypeToSeriesMap = moveTypeToSeriesMapList.get(i);
                XYIntervalSeries series = moveTypeToSeriesMap.get(moveType);
                if (series == null) {
                    series = new XYIntervalSeries(moveType);
                    moveTypeToSeriesMap.put(moveType, series);
                }
                double yValue = levelValues[i];
                // In an XYInterval the yLow must be lower than yHigh
                series.add(timeMillisSpent, timeMillisSpent, timeMillisSpent,
                        yValue, (yValue > 0.0) ? 0.0 : yValue, (yValue > 0.0) ? yValue : 0.0);
            }
        }
        graphFileList = new ArrayList<File>(moveTypeToSeriesMapList.size());
        for (int scoreLevelIndex = 0; scoreLevelIndex < moveTypeToSeriesMapList.size(); scoreLevelIndex++) {
            XYPlot plot = createPlot(benchmarkReport, scoreLevelIndex);
            XYItemRenderer renderer = new YIntervalRenderer();
            plot.setRenderer(renderer);
            XYIntervalSeriesCollection seriesCollection = new XYIntervalSeriesCollection();
            for (XYIntervalSeries series : moveTypeToSeriesMapList.get(scoreLevelIndex).values()) {
                seriesCollection.addSeries(series);
            }
            plot.setDataset(seriesCollection);
            JFreeChart chart = new JFreeChart(subSingleBenchmarkResult.getName()
                    + " picked move type step score diff level " + scoreLevelIndex + " statistic",
                    JFreeChart.DEFAULT_TITLE_FONT, plot, true);
            graphFileList.add(writeChartToImageFile(chart,
                    "PickedMoveTypeStepScoreDiffStatisticLevel" + scoreLevelIndex));
        }
    }

    private XYPlot createPlot(BenchmarkReport benchmarkReport, int scoreLevelIndex) {
        Locale locale = benchmarkReport.getLocale();
        NumberAxis xAxis = new NumberAxis("Time spent");
        xAxis.setNumberFormatOverride(new MillisecondsSpentNumberFormat(locale));
        NumberAxis yAxis = new NumberAxis("Step score diff level " + scoreLevelIndex);
        yAxis.setNumberFormatOverride(NumberFormat.getInstance(locale));
        yAxis.setAutoRangeIncludesZero(true);
        XYPlot plot = new XYPlot(null, xAxis, yAxis, null);
        plot.setOrientation(PlotOrientation.VERTICAL);
        return plot;
    }

}
