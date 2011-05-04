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

package org.drools.planner.benchmark.statistic.calculatecount;

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
import java.util.List;
import java.util.Map;
import javax.imageio.ImageIO;

import org.apache.commons.io.IOUtils;
import org.drools.planner.benchmark.statistic.MillisecondsSpendNumberFormat;
import org.drools.planner.benchmark.statistic.SolverStatistic;
import org.drools.planner.core.Solver;
import org.drools.planner.core.localsearch.LocalSearchSolver;
import org.jfree.chart.JFreeChart;
import org.jfree.chart.axis.NumberAxis;
import org.jfree.chart.plot.PlotOrientation;
import org.jfree.chart.plot.XYPlot;
import org.jfree.chart.renderer.xy.XYItemRenderer;
import org.jfree.chart.renderer.xy.XYLineAndShapeRenderer;
import org.jfree.data.xy.XYSeries;
import org.jfree.data.xy.XYSeriesCollection;

public class CalculateCountStatistic implements SolverStatistic {

    private List<String> configNameList = new ArrayList<String>();
    // key is the configName
    private Map<String, CalculateCountStatisticListener> statisticListenerMap
            = new HashMap<String, CalculateCountStatisticListener>();

    public void addListener(Solver solver, String configName) {
        if (configNameList.contains(configName)) {
            throw new IllegalArgumentException("Cannot add a listener with the same configName (" + configName
                    + ") twice.");
        }
        configNameList.add(configName);
        CalculateCountStatisticListener statisticListener = new CalculateCountStatisticListener();
        ((LocalSearchSolver) solver).addLocalSearchSolverLifecycleListener(statisticListener);
        statisticListenerMap.put(configName, statisticListener);
    }

    public void removeListener(Solver solver, String configName) {
        CalculateCountStatisticListener statisticListener = statisticListenerMap.get(configName);
        ((LocalSearchSolver) solver).removeLocalSearchSolverLifecycleListener(statisticListener);
    }

    // ************************************************************************
    // Write methods
    // ************************************************************************

    public CharSequence writeStatistic(File solverStatisticFilesDirectory, String baseName) {
        StringBuilder htmlFragment = new StringBuilder();
        htmlFragment.append(writeCsvStatistic(solverStatisticFilesDirectory, baseName));
        htmlFragment.append(writeGraphStatistic(solverStatisticFilesDirectory, baseName));
        return htmlFragment;
    }

    private List<CalculateCountScvLine> extractCalculateCountScvLineList() {
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
        List<CalculateCountScvLine> calculateCountScvLineList
                = new ArrayList<CalculateCountScvLine>(timeToBestScoresLineMap.values());
        Collections.sort(calculateCountScvLineList);
        return calculateCountScvLineList;
    }

    protected class CalculateCountScvLine implements Comparable<CalculateCountScvLine> {

        private long timeMillisSpend;
        private Map<String, Long> configNameToCalculateCountPerSecondMap;

        public CalculateCountScvLine(long timeMillisSpend) {
            this.timeMillisSpend = timeMillisSpend;
            configNameToCalculateCountPerSecondMap = new HashMap<String, Long>();
        }

        public long getTimeMillisSpend() {
            return timeMillisSpend;
        }

        public Map<String, Long> getConfigNameToCalculateCountPerSecondMap() {
            return configNameToCalculateCountPerSecondMap;
        }

        public int compareTo(CalculateCountScvLine other) {
            return timeMillisSpend < other.timeMillisSpend ? -1 : (timeMillisSpend > other.timeMillisSpend ? 1 : 0);
        }

    }

    private CharSequence writeCsvStatistic(File solverStatisticFilesDirectory, String baseName) {
        List<CalculateCountScvLine> calculateCountScvLineList = extractCalculateCountScvLineList();
        File csvStatisticFile = new File(solverStatisticFilesDirectory, baseName + "CalculateCountStatistic.csv");
        Writer writer = null;
        try {
            writer = new OutputStreamWriter(new FileOutputStream(csvStatisticFile), "utf-8");
            writer.append("\"TimeMillisSpend\"");
            for (String configName : configNameList) {
                writer.append(",\"").append(configName.replaceAll("\\\"", "\\\"")).append("\"");
            }
            writer.append("\n");
            for (CalculateCountScvLine line : calculateCountScvLineList) {
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

    private CharSequence writeGraphStatistic(File solverStatisticFilesDirectory, String baseName) {
        XYSeriesCollection seriesCollection = new XYSeriesCollection();
        for (Map.Entry<String, CalculateCountStatisticListener> listenerEntry : statisticListenerMap.entrySet()) {
            String configName = listenerEntry.getKey();
            XYSeries configSeries = new XYSeries(configName);
            List<CalculateCountStatisticPoint> statisticPointList = listenerEntry.getValue().getStatisticPointList();
            for (CalculateCountStatisticPoint statisticPoint : statisticPointList) {
                long timeMillisSpend = statisticPoint.getTimeMillisSpend();
                long calculateCountPerSecond = statisticPoint.getCalculateCountPerSecond();
                configSeries.add(timeMillisSpend, calculateCountPerSecond);
            }
            seriesCollection.addSeries(configSeries);
        }
        NumberAxis xAxis = new NumberAxis("Time millis spend");
        xAxis.setNumberFormatOverride(new MillisecondsSpendNumberFormat());
        NumberAxis yAxis = new NumberAxis("Calculate count per second");
        yAxis.setAutoRangeIncludesZero(false);
        XYItemRenderer renderer = new XYLineAndShapeRenderer();
        XYPlot plot = new XYPlot(seriesCollection, xAxis, yAxis, renderer);
        plot.setOrientation(PlotOrientation.VERTICAL);
        JFreeChart chart = new JFreeChart(baseName + " calculate count statistic",
                JFreeChart.DEFAULT_TITLE_FONT, plot, true);
        BufferedImage chartImage = chart.createBufferedImage(1024, 768);
        File graphStatisticFile = new File(solverStatisticFilesDirectory, baseName + "CalculateCountStatistic.png");
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
