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

package org.drools.planner.benchmark.core.statistic.calculatecount;

import java.awt.image.BufferedImage;
import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.OutputStream;
import java.io.OutputStreamWriter;
import java.io.Writer;
import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import javax.imageio.ImageIO;

import org.apache.commons.io.IOUtils;
import org.drools.planner.benchmark.core.statistic.AbstractProblemStatistic;
import org.drools.planner.benchmark.core.statistic.MillisecondsSpendNumberFormat;
import org.drools.planner.benchmark.core.statistic.ProblemStatisticType;
import org.drools.planner.core.Solver;
import org.drools.planner.core.solver.DefaultSolver;
import org.jfree.chart.JFreeChart;
import org.jfree.chart.axis.NumberAxis;
import org.jfree.chart.plot.PlotOrientation;
import org.jfree.chart.plot.XYPlot;
import org.jfree.chart.renderer.xy.XYItemRenderer;
import org.jfree.chart.renderer.xy.XYLineAndShapeRenderer;
import org.jfree.data.xy.XYSeries;
import org.jfree.data.xy.XYSeriesCollection;

public class CalculateCountStatistic extends AbstractProblemStatistic {

    private List<String> configNameList = new ArrayList<String>();
    // key is the configName
    private Map<String, CalculateCountStatisticListener> statisticListenerMap
            = new LinkedHashMap<String, CalculateCountStatisticListener>();

    public CalculateCountStatistic() {
        super(ProblemStatisticType.CALCULATE_COUNT_PER_SECOND);
    }

    public void addListener(Solver solver, String configName) {
        if (configNameList.contains(configName)) {
            throw new IllegalArgumentException("Cannot add a listener with the same configName (" + configName
                    + ") twice.");
        }
        configNameList.add(configName);
        CalculateCountStatisticListener statisticListener = new CalculateCountStatisticListener();
        ((DefaultSolver) solver).addSolverPhaseLifecycleListener(statisticListener);
        statisticListenerMap.put(configName, statisticListener);
    }

    public void removeListener(Solver solver, String configName) {
        CalculateCountStatisticListener statisticListener = statisticListenerMap.get(configName);
        ((DefaultSolver) solver).removeSolverPhaseLifecycleListener(statisticListener);
    }

    // ************************************************************************
    // Write methods
    // ************************************************************************

    private List<CalculateCountScvLine> extractCsvLineList() {
        Map<Long, CalculateCountScvLine> timeToBestScoresLineMap = new HashMap<Long, CalculateCountScvLine>();
        for (Map.Entry<String, CalculateCountStatisticListener> listenerEntry : statisticListenerMap.entrySet()) {
            String configName = listenerEntry.getKey();
            List<CalculateCountStatisticPoint> statisticPointList = listenerEntry.getValue().getStatisticPointList();
            for (CalculateCountStatisticPoint statisticPoint : statisticPointList) {
                long timeMillisSpend = statisticPoint.getTimeMillisSpend();
                CalculateCountScvLine line = timeToBestScoresLineMap.get(timeMillisSpend);
                if (line == null) {
                    line = new CalculateCountScvLine(timeMillisSpend);
                    timeToBestScoresLineMap.put(timeMillisSpend, line);
                }
                line.getConfigNameToCalculateCountPerSecondMap().put(configName, statisticPoint.getCalculateCountPerSecond());
            }
        }
        List<CalculateCountScvLine> csvLineList = new ArrayList<CalculateCountScvLine>(timeToBestScoresLineMap.values());
        Collections.sort(csvLineList);
        return csvLineList;
    }

    protected static class CalculateCountScvLine extends AbstractProblemStatisticScvLine {

        private Map<String, Long> configNameToCalculateCountPerSecondMap;

        public CalculateCountScvLine(long timeMillisSpend) {
            super(timeMillisSpend);
            configNameToCalculateCountPerSecondMap = new HashMap<String, Long>();
        }

        public Map<String, Long> getConfigNameToCalculateCountPerSecondMap() {
            return configNameToCalculateCountPerSecondMap;
        }

    }

    protected CharSequence writeCsvStatistic(File statisticDirectory, String baseName) {
        List<CalculateCountScvLine> csvLineList = extractCsvLineList();
        File csvStatisticFile = new File(statisticDirectory, baseName + "CalculateCountStatistic.csv");
        Writer writer = null;
        try {
            writer = new OutputStreamWriter(new FileOutputStream(csvStatisticFile), "UTF-8");
            writer.append("\"TimeMillisSpend\"");
            for (String configName : configNameList) {
                writer.append(",\"").append(configName.replaceAll("\\\"", "\\\"")).append("\"");
            }
            writer.append("\n");
            for (CalculateCountScvLine line : csvLineList) {
                writer.write(Long.toString(line.getTimeMillisSpend()));
                for (String configName : configNameList) {
                    writer.append(",");
                    Long calculateCountPerSecond = line.getConfigNameToCalculateCountPerSecondMap().get(configName);
                    if (calculateCountPerSecond != null) {
                        writer.append(calculateCountPerSecond.toString());
                    }
                }
                writer.append("\n");
            }
        } catch (IOException e) {
            throw new IllegalArgumentException("Problem writing csvStatisticFile: " + csvStatisticFile, e);
        } finally {
            IOUtils.closeQuietly(writer);
        }
        return "  <p><a href=\"" + csvStatisticFile.getName() + "\">CVS file</a></p>\n";
    }

    protected CharSequence writeGraphStatistic(File statisticDirectory, String baseName) {
        XYSeriesCollection seriesCollection = new XYSeriesCollection();
        for (Map.Entry<String, CalculateCountStatisticListener> listenerEntry : statisticListenerMap.entrySet()) {
            String configName = listenerEntry.getKey();
            XYSeries series = new XYSeries(configName);
            List<CalculateCountStatisticPoint> statisticPointList = listenerEntry.getValue().getStatisticPointList();
            for (CalculateCountStatisticPoint statisticPoint : statisticPointList) {
                long timeMillisSpend = statisticPoint.getTimeMillisSpend();
                long calculateCountPerSecond = statisticPoint.getCalculateCountPerSecond();
                series.add(timeMillisSpend, calculateCountPerSecond);
            }
            seriesCollection.addSeries(series);
        }
        NumberAxis xAxis = new NumberAxis("Time spend");
        xAxis.setNumberFormatOverride(new MillisecondsSpendNumberFormat());
        NumberAxis yAxis = new NumberAxis("Calculate count per second");
        yAxis.setAutoRangeIncludesZero(false);
        XYItemRenderer renderer = new XYLineAndShapeRenderer();
        XYPlot plot = new XYPlot(seriesCollection, xAxis, yAxis, renderer);
        plot.setOrientation(PlotOrientation.VERTICAL);
        JFreeChart chart = new JFreeChart(baseName + " calculate count statistic",
                JFreeChart.DEFAULT_TITLE_FONT, plot, true);
        BufferedImage chartImage = chart.createBufferedImage(1024, 768);
        File graphStatisticFile = new File(statisticDirectory, baseName + "CalculateCountStatistic.png");
        OutputStream out = null;
        try {
            out = new FileOutputStream(graphStatisticFile);
            ImageIO.write(chartImage, "png", out);
        } catch (IOException e) {
            throw new IllegalArgumentException("Problem writing graphStatisticFile: " + graphStatisticFile, e);
        } finally {
            IOUtils.closeQuietly(out);
        }
        return "  <img src=\"" + graphStatisticFile.getName() + "\"/>\n";
    }

}
