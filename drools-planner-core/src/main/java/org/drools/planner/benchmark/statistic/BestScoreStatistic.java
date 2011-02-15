/**
 * Copyright 2010 JBoss Inc
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

package org.drools.planner.benchmark.statistic;

import java.awt.image.BufferedImage;
import java.io.OutputStream;
import java.util.List;
import java.util.Map;
import java.util.HashMap;
import java.util.ArrayList;
import java.util.Collections;
import java.io.File;
import java.io.Writer;
import java.io.OutputStreamWriter;
import java.io.FileOutputStream;
import java.io.IOException;

import javax.imageio.ImageIO;

import org.drools.planner.core.Solver;
import org.drools.planner.core.score.Score;
import org.apache.commons.io.IOUtils;
import org.drools.planner.core.score.definition.ScoreDefinition;
import org.jfree.chart.JFreeChart;
import org.jfree.chart.axis.NumberAxis;
import org.jfree.chart.plot.PlotOrientation;
import org.jfree.chart.plot.XYPlot;
import org.jfree.chart.renderer.xy.XYItemRenderer;
import org.jfree.chart.renderer.xy.XYStepRenderer;
import org.jfree.data.xy.XYSeries;
import org.jfree.data.xy.XYSeriesCollection;

/**
 * @author Geoffrey De Smet
 */
public class BestScoreStatistic implements SolverStatistic {

    private List<String> configNameList = new ArrayList<String>();
    // key is the configName
    private Map<String, BestScoreStatisticListener> bestScoreStatisticListenerMap
            = new HashMap<String, BestScoreStatisticListener>();
    private ScoreDefinition scoreDefinition = null;

    public void addListener(Solver solver, String configName) {
        if (configNameList.contains(configName)) {
            throw new IllegalArgumentException("Cannot add a listener with the same configName (" + configName
                    + ") twice.");
        }
        configNameList.add(configName);
        BestScoreStatisticListener bestScoreStatisticListener = new BestScoreStatisticListener();
        solver.addEventListener(bestScoreStatisticListener);
        bestScoreStatisticListenerMap.put(configName, bestScoreStatisticListener);
        if (scoreDefinition == null) {
            scoreDefinition = solver.getScoreDefinition();
        } else {
            if (!scoreDefinition.getClass().equals(solver.getScoreDefinition().getClass())) {
                throw new IllegalStateException("The scoreDefinition (" + solver.getScoreDefinition()
                        + ") should be of the same class as the other scoreDefinition (" + scoreDefinition + ")");
            }
        }
    }

    public void removeListener(Solver solver, String configName) {
        BestScoreStatisticListener bestScoreStatisticListener = bestScoreStatisticListenerMap.get(configName);
        solver.removeEventListener(bestScoreStatisticListener);
    }

    public CharSequence writeStatistic(File solverStatisticFilesDirectory, String baseName) {
        StringBuilder htmlFragment = new StringBuilder();
        htmlFragment.append(writeCsvStatistic(solverStatisticFilesDirectory, baseName));
        htmlFragment.append(writeGraphStatistic(solverStatisticFilesDirectory, baseName));
        return htmlFragment;
    }

    private List<TimeToBestScoresLine> extractTimeToBestScoresLineList() {
        Map<Long, TimeToBestScoresLine> timeToBestScoresLineMap = new HashMap<Long, TimeToBestScoresLine>();
        for (Map.Entry<String, BestScoreStatisticListener> listenerEntry : bestScoreStatisticListenerMap.entrySet()) {
            String configName = listenerEntry.getKey();
            List<BestScoreStatisticPoint> statisticPointList = listenerEntry.getValue()
                    .getBestScoreStatisticPointList();
            for (BestScoreStatisticPoint statisticPoint : statisticPointList) {
                long timeMillisSpend = statisticPoint.getTimeMillisSpend();
                TimeToBestScoresLine line = timeToBestScoresLineMap.get(timeMillisSpend);
                if (line == null) {
                    line = new TimeToBestScoresLine(timeMillisSpend);
                    timeToBestScoresLineMap.put(timeMillisSpend, line);
                }
                line.getConfigNameToScoreMap().put(configName, statisticPoint.getScore());
            }
        }
        List<TimeToBestScoresLine> timeToBestScoresLineList
                = new ArrayList<TimeToBestScoresLine>(timeToBestScoresLineMap.values());
        Collections.sort(timeToBestScoresLineList);
        return timeToBestScoresLineList;
    }

    protected class TimeToBestScoresLine implements Comparable<TimeToBestScoresLine> {

        private long timeMillisSpend;
        private Map<String, Score> configNameToScoreMap;

        public TimeToBestScoresLine(long timeMillisSpend) {
            this.timeMillisSpend = timeMillisSpend;
            configNameToScoreMap = new HashMap<String, Score>();
        }

        public long getTimeMillisSpend() {
            return timeMillisSpend;
        }

        public Map<String, Score> getConfigNameToScoreMap() {
            return configNameToScoreMap;
        }

        public int compareTo(TimeToBestScoresLine other) {
            return timeMillisSpend < other.timeMillisSpend ? -1 : (timeMillisSpend > other.timeMillisSpend ? 1 : 0);
        }

    }

    private CharSequence writeCsvStatistic(File solverStatisticFilesDirectory, String baseName) {
        List<TimeToBestScoresLine> timeToBestScoresLineList = extractTimeToBestScoresLineList();
        File csvStatisticFile = new File(solverStatisticFilesDirectory, baseName + "Statistic.csv");
        Writer writer = null;
        try {
            writer = new OutputStreamWriter(new FileOutputStream(csvStatisticFile), "utf-8");
            writer.append("\"TimeMillisSpend\"");
            for (String configName : configNameList) {
                writer.append(",\"").append(configName.replaceAll("\\\"","\\\"")).append("\"");
            }
            writer.append("\n");
            for (TimeToBestScoresLine timeToBestScoresLine : timeToBestScoresLineList) {
                writer.write(Long.toString(timeToBestScoresLine.getTimeMillisSpend()));
                for (String configName : configNameList) {
                    writer.append(",");
                    Score score = timeToBestScoresLine.getConfigNameToScoreMap().get(configName);
                    if (score != null) {
                        Double scoreGraphValue = scoreDefinition.translateScoreToGraphValue(score);
                        if (scoreGraphValue != null) {
                            writer.append(scoreGraphValue.toString());
                        }
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
        for (Map.Entry<String, BestScoreStatisticListener> listenerEntry : bestScoreStatisticListenerMap.entrySet()) {
            String configName = listenerEntry.getKey();
            XYSeries configSeries = new XYSeries(configName);
            List<BestScoreStatisticPoint> statisticPointList = listenerEntry.getValue()
                    .getBestScoreStatisticPointList();
            for (BestScoreStatisticPoint statisticPoint : statisticPointList) {
                long timeMillisSpend = statisticPoint.getTimeMillisSpend();
                Score score = statisticPoint.getScore();
                Double scoreGraphValue = scoreDefinition.translateScoreToGraphValue(score);
                if (scoreGraphValue != null) {
                    configSeries.add(timeMillisSpend, scoreGraphValue);
                }
            }
            seriesCollection.addSeries(configSeries);
        }
        NumberAxis xAxis = new NumberAxis("Time millis spend");
        xAxis.setNumberFormatOverride(new MillisecondsSpendNumberFormat());
        NumberAxis yAxis = new NumberAxis("Score");
        yAxis.setAutoRangeIncludesZero(false);
        XYItemRenderer renderer = new XYStepRenderer();
        XYPlot plot = new XYPlot(seriesCollection, xAxis, yAxis, renderer);
        plot.setOrientation(PlotOrientation.VERTICAL);
        JFreeChart chart = new JFreeChart(baseName + " best score statistic",
                JFreeChart.DEFAULT_TITLE_FONT, plot, true);
        BufferedImage chartImage = chart.createBufferedImage(1024, 768);
        File graphStatisticFile = new File(solverStatisticFilesDirectory, baseName + "Statistic.png");
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
