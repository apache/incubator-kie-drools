/*
 * Copyright 2020 Red Hat, Inc. and/or its affiliates.
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

package org.optaplanner.benchmark.impl.statistic.subsingle.pickedmovetypebestscore;

import java.io.File;
import java.text.NumberFormat;
import java.util.ArrayList;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Locale;
import java.util.Map;

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

import com.thoughtworks.xstream.annotations.XStreamAlias;
import com.thoughtworks.xstream.annotations.XStreamOmitField;

@XStreamAlias("pickedMoveTypeBestScoreDiffSubSingleStatistic")
public class PickedMoveTypeBestScoreDiffSubSingleStatistic<Solution_>
        extends PureSubSingleStatistic<Solution_, PickedMoveTypeBestScoreDiffStatisticPoint> {

    @XStreamOmitField
    private PickedMoveTypeBestScoreDiffSubSingleStatisticListener listener;

    @XStreamOmitField
    protected List<File> graphFileList = null;

    public PickedMoveTypeBestScoreDiffSubSingleStatistic(SubSingleBenchmarkResult subSingleBenchmarkResult) {
        super(subSingleBenchmarkResult, SingleStatisticType.PICKED_MOVE_TYPE_BEST_SCORE_DIFF);
        listener = new PickedMoveTypeBestScoreDiffSubSingleStatisticListener();
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

    @Override
    public void open(Solver<Solution_> solver) {
        ((DefaultSolver<Solution_>) solver).addPhaseLifecycleListener(listener);
    }

    @Override
    public void close(Solver<Solution_> solver) {
        ((DefaultSolver<Solution_>) solver).removePhaseLifecycleListener(listener);
    }

    private class PickedMoveTypeBestScoreDiffSubSingleStatisticListener extends PhaseLifecycleListenerAdapter<Solution_> {

        private Score oldBestScore = null;

        @Override
        public void phaseStarted(AbstractPhaseScope<Solution_> phaseScope) {
            if (phaseScope instanceof LocalSearchPhaseScope) {
                oldBestScore = phaseScope.getBestScore();
            }
        }

        @Override
        public void phaseEnded(AbstractPhaseScope<Solution_> phaseScope) {
            if (phaseScope instanceof LocalSearchPhaseScope) {
                oldBestScore = null;
            }
        }

        @Override
        public void stepEnded(AbstractStepScope<Solution_> stepScope) {
            if (stepScope instanceof LocalSearchStepScope) {
                localSearchStepEnded((LocalSearchStepScope<Solution_>) stepScope);
            }
        }

        private void localSearchStepEnded(LocalSearchStepScope<Solution_> stepScope) {
            if (stepScope.getBestScoreImproved()) {
                long timeMillisSpent = stepScope.getPhaseScope().calculateSolverTimeMillisSpentUpToNow();
                String moveType = stepScope.getStep().getSimpleMoveTypeDescription();
                Score newBestScore = stepScope.getScore();
                Score bestScoreDiff = newBestScore.subtract(oldBestScore);
                oldBestScore = newBestScore;
                pointList.add(new PickedMoveTypeBestScoreDiffStatisticPoint(
                        timeMillisSpent, moveType, bestScoreDiff));
            }
        }

    }

    // ************************************************************************
    // CSV methods
    // ************************************************************************

    @Override
    protected String getCsvHeader() {
        return PickedMoveTypeBestScoreDiffStatisticPoint.buildCsvLine("timeMillisSpent", "moveType", "bestScoreDiff");
    }

    @Override
    protected PickedMoveTypeBestScoreDiffStatisticPoint createPointFromCsvLine(ScoreDefinition scoreDefinition,
            List<String> csvLine) {
        return new PickedMoveTypeBestScoreDiffStatisticPoint(Long.parseLong(csvLine.get(0)),
                csvLine.get(1), scoreDefinition.parseScore(csvLine.get(2)));
    }

    // ************************************************************************
    // Write methods
    // ************************************************************************

    @Override
    public void writeGraphFiles(BenchmarkReport benchmarkReport) {
        List<Map<String, XYIntervalSeries>> moveTypeToSeriesMapList = new ArrayList<>(BenchmarkReport.CHARTED_SCORE_LEVEL_SIZE);
        for (PickedMoveTypeBestScoreDiffStatisticPoint point : getPointList()) {
            long timeMillisSpent = point.getTimeMillisSpent();
            String moveType = point.getMoveType();
            double[] levelValues = ScoreUtils.extractLevelDoubles(point.getBestScoreDiff());
            for (int i = 0; i < levelValues.length && i < BenchmarkReport.CHARTED_SCORE_LEVEL_SIZE; i++) {
                if (i >= moveTypeToSeriesMapList.size()) {
                    moveTypeToSeriesMapList.add(new LinkedHashMap<>());
                }
                Map<String, XYIntervalSeries> moveTypeToSeriesMap = moveTypeToSeriesMapList.get(i);
                XYIntervalSeries series = moveTypeToSeriesMap.computeIfAbsent(moveType,
                        k -> new XYIntervalSeries(moveType));
                double yValue = levelValues[i];
                // In an XYInterval the yLow must be lower than yHigh
                series.add(timeMillisSpent, timeMillisSpent, timeMillisSpent,
                        yValue, (yValue > 0.0) ? 0.0 : yValue, (yValue > 0.0) ? yValue : 0.0);
            }
        }
        graphFileList = new ArrayList<>(moveTypeToSeriesMapList.size());
        for (int scoreLevelIndex = 0; scoreLevelIndex < moveTypeToSeriesMapList.size(); scoreLevelIndex++) {
            XYPlot plot = createPlot(benchmarkReport, scoreLevelIndex);
            XYItemRenderer renderer = new YIntervalRenderer();
            plot.setRenderer(renderer);
            XYIntervalSeriesCollection seriesCollection = new XYIntervalSeriesCollection();
            for (XYIntervalSeries series : moveTypeToSeriesMapList.get(scoreLevelIndex).values()) {
                seriesCollection.addSeries(series);
            }
            plot.setDataset(seriesCollection);
            String scoreLevelLabel = subSingleBenchmarkResult.getSingleBenchmarkResult().getProblemBenchmarkResult()
                    .findScoreLevelLabel(scoreLevelIndex);
            JFreeChart chart = new JFreeChart(subSingleBenchmarkResult.getName()
                    + " picked move type best " + scoreLevelLabel + " diff statistic",
                    JFreeChart.DEFAULT_TITLE_FONT, plot, true);
            graphFileList.add(writeChartToImageFile(chart,
                    "PickedMoveTypeBestScoreDiffStatisticLevel" + scoreLevelIndex));
        }
    }

    private XYPlot createPlot(BenchmarkReport benchmarkReport, int scoreLevelIndex) {
        Locale locale = benchmarkReport.getLocale();
        NumberAxis xAxis = new NumberAxis("Time spent");
        xAxis.setNumberFormatOverride(new MillisecondsSpentNumberFormat(locale));
        String scoreLevelLabel = subSingleBenchmarkResult.getSingleBenchmarkResult().getProblemBenchmarkResult()
                .findScoreLevelLabel(scoreLevelIndex);
        NumberAxis yAxis = new NumberAxis("Best " + scoreLevelLabel + "  diff");
        yAxis.setNumberFormatOverride(NumberFormat.getInstance(locale));
        yAxis.setAutoRangeIncludesZero(true);
        XYPlot plot = new XYPlot(null, xAxis, yAxis, null);
        plot.setOrientation(PlotOrientation.VERTICAL);
        return plot;
    }

}
