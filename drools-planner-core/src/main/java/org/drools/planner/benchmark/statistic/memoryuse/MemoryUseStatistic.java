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

package org.drools.planner.benchmark.statistic.memoryuse;

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
import org.drools.planner.benchmark.statistic.AbstractSolverStatistic;
import org.drools.planner.benchmark.statistic.MillisecondsSpendNumberFormat;
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

public class MemoryUseStatistic extends AbstractSolverStatistic {

    private List<String> configNameList = new ArrayList<String>();
    // key is the configName
    private Map<String, MemoryUseStatisticListener> statisticListenerMap
            = new HashMap<String, MemoryUseStatisticListener>();

    public void addListener(Solver solver, String configName) {
        if (configNameList.contains(configName)) {
            throw new IllegalArgumentException("Cannot add a listener with the same configName (" + configName
                    + ") twice.");
        }
        configNameList.add(configName);
        MemoryUseStatisticListener statisticListener = new MemoryUseStatisticListener();
        ((LocalSearchSolver) solver).addLocalSearchSolverLifecycleListener(statisticListener);
        statisticListenerMap.put(configName, statisticListener);
    }

    public void removeListener(Solver solver, String configName) {
        MemoryUseStatisticListener statisticListener = statisticListenerMap.get(configName);
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

    private List<MemoryUseScvLine> extractCsvLineList() {
        Map<Long, MemoryUseScvLine> timeToBestScoresLineMap = new HashMap<Long, MemoryUseScvLine>();
        for (Map.Entry<String, MemoryUseStatisticListener> listenerEntry : statisticListenerMap.entrySet()) {
            String configName = listenerEntry.getKey();
            List<MemoryUseStatisticPoint> statisticPointList = listenerEntry.getValue().getStatisticPointList();
            for (MemoryUseStatisticPoint statisticPoint : statisticPointList) {
                long timeMillisSpend = statisticPoint.getTimeMillisSpend();
                MemoryUseScvLine line = timeToBestScoresLineMap.get(timeMillisSpend);
                if (line == null) {
                    line = new MemoryUseScvLine(timeMillisSpend);
                    timeToBestScoresLineMap.put(timeMillisSpend, line);
                }
                line.getConfigNameToMemoryUseMeasurementMap().put(configName, statisticPoint.getMemoryUseMeasurement());
            }
        }
        List<MemoryUseScvLine> csvLineList = new ArrayList<MemoryUseScvLine>(timeToBestScoresLineMap.values());
        Collections.sort(csvLineList);
        return csvLineList;
    }

    protected class MemoryUseScvLine implements Comparable<MemoryUseScvLine> {

        private long timeMillisSpend;
        private Map<String, MemoryUseMeasurement> configNameToMemoryUseMeasurementMap;

        public MemoryUseScvLine(long timeMillisSpend) {
            this.timeMillisSpend = timeMillisSpend;
            configNameToMemoryUseMeasurementMap = new HashMap<String, MemoryUseMeasurement>();
        }

        public long getTimeMillisSpend() {
            return timeMillisSpend;
        }

        public Map<String, MemoryUseMeasurement> getConfigNameToMemoryUseMeasurementMap() {
            return configNameToMemoryUseMeasurementMap;
        }

        public int compareTo(MemoryUseScvLine other) {
            return timeMillisSpend < other.timeMillisSpend ? -1 : (timeMillisSpend > other.timeMillisSpend ? 1 : 0);
        }

    }

    private CharSequence writeCsvStatistic(File solverStatisticFilesDirectory, String baseName) {
        List<MemoryUseScvLine> csvLineList = extractCsvLineList();
        File csvStatisticFile = new File(solverStatisticFilesDirectory, baseName + "MemoryUseStatistic.csv");
        Writer writer = null;
        try {
            writer = new OutputStreamWriter(new FileOutputStream(csvStatisticFile), "utf-8");
            writer.append("\"TimeMillisSpend\"");
            for (String configName : configNameList) {
                writer.append(",\"").append(configName.replaceAll("\\\"", "\\\"")).append(" used\"");
                writer.append(",\"").append(configName.replaceAll("\\\"", "\\\"")).append(" max\"");
            }
            writer.append("\n");
            for (MemoryUseScvLine line : csvLineList) {
                writer.write(Long.toString(line.getTimeMillisSpend()));
                for (String configName : configNameList) {
                    writer.append(",");
                    MemoryUseMeasurement memoryUseMeasurement = line.getConfigNameToMemoryUseMeasurementMap()
                            .get(configName);
                    if (memoryUseMeasurement != null) {
                        writer.append(Long.toString(memoryUseMeasurement.getUsedMemory()));
                        writer.append(",");
                        writer.append(Long.toString(memoryUseMeasurement.getMaxMemory()));
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
        for (Map.Entry<String, MemoryUseStatisticListener> listenerEntry : statisticListenerMap.entrySet()) {
            String configName = listenerEntry.getKey();
            XYSeries configUsedSeries = new XYSeries(configName + " used");
            XYSeries configMaxSeries = new XYSeries(configName + " max");
            List<MemoryUseStatisticPoint> statisticPointList = listenerEntry.getValue().getStatisticPointList();
            for (MemoryUseStatisticPoint statisticPoint : statisticPointList) {
                long timeMillisSpend = statisticPoint.getTimeMillisSpend();
                MemoryUseMeasurement memoryUseMeasurement = statisticPoint.getMemoryUseMeasurement();
                configUsedSeries.add(timeMillisSpend, memoryUseMeasurement.getUsedMemory());
                configMaxSeries.add(timeMillisSpend, memoryUseMeasurement.getMaxMemory());
            }
            seriesCollection.addSeries(configUsedSeries);
            seriesCollection.addSeries(configMaxSeries);
        }
        NumberAxis xAxis = new NumberAxis("Time millis spend");
        xAxis.setNumberFormatOverride(new MillisecondsSpendNumberFormat());
        NumberAxis yAxis = new NumberAxis("Memory");
        yAxis.setAutoRangeIncludesZero(false);
        XYItemRenderer renderer = new XYLineAndShapeRenderer();
        XYPlot plot = new XYPlot(seriesCollection, xAxis, yAxis, renderer);
        plot.setOrientation(PlotOrientation.VERTICAL);
        JFreeChart chart = new JFreeChart(baseName + " memory use statistic",
                JFreeChart.DEFAULT_TITLE_FONT, plot, true);
        BufferedImage chartImage = chart.createBufferedImage(1024, 768);
        File graphStatisticFile = new File(solverStatisticFilesDirectory, baseName + "MemoryUseStatistic.png");
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
