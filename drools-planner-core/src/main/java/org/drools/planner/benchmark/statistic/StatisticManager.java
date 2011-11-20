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

package org.drools.planner.benchmark.statistic;

import java.awt.BasicStroke;
import java.awt.image.BufferedImage;
import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.OutputStream;
import java.io.OutputStreamWriter;
import java.io.Writer;
import java.util.LinkedHashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;
import javax.imageio.ImageIO;

import org.apache.commons.io.FilenameUtils;
import org.apache.commons.io.IOUtils;
import org.drools.planner.benchmark.SolverBenchmark;
import org.drools.planner.benchmark.SolverBenchmarkResult;
import org.drools.planner.core.score.Score;
import org.drools.planner.core.score.definition.ScoreDefinition;
import org.jfree.chart.ChartFactory;
import org.jfree.chart.JFreeChart;
import org.jfree.chart.axis.CategoryAxis;
import org.jfree.chart.axis.NumberAxis;
import org.jfree.chart.labels.ItemLabelAnchor;
import org.jfree.chart.labels.ItemLabelPosition;
import org.jfree.chart.labels.StandardCategoryItemLabelGenerator;
import org.jfree.chart.plot.CategoryPlot;
import org.jfree.chart.plot.PlotOrientation;
import org.jfree.chart.plot.XYPlot;
import org.jfree.chart.renderer.category.BarRenderer;
import org.jfree.chart.renderer.category.CategoryItemRenderer;
import org.jfree.chart.renderer.xy.StandardXYItemRenderer;
import org.jfree.chart.renderer.xy.XYItemRenderer;
import org.jfree.data.category.DefaultCategoryDataset;
import org.jfree.data.xy.XYSeries;
import org.jfree.data.xy.XYSeriesCollection;
import org.jfree.ui.TextAnchor;

public class StatisticManager {

    private final String benchmarkName;
    private final File solverStatisticFilesDirectory;
    private final Map<File, List<SolverStatistic>> unsolvedSolutionFileToStatisticMap;

    public StatisticManager(String benchmarkName, File solverStatisticFilesDirectory,
            Map<File, List<SolverStatistic>> unsolvedSolutionFileToStatisticMap) {
        this.benchmarkName = benchmarkName;
        this.solverStatisticFilesDirectory = solverStatisticFilesDirectory;
        this.unsolvedSolutionFileToStatisticMap = unsolvedSolutionFileToStatisticMap;
    }

    public void writeStatistics(List<SolverBenchmark> solverBenchmarkList) {
        // 2 lines at 80 chars per line give a max of 160 per entry
        StringBuilder htmlFragment = new StringBuilder(unsolvedSolutionFileToStatisticMap.size() * 160);
        htmlFragment.append("  <h1>Summary</h1>\n");
        htmlFragment.append(writeBestScoreSummaryChart(solverBenchmarkList));
        htmlFragment.append(writeTimeSpendSummaryChart(solverBenchmarkList));
        htmlFragment.append(writeScalabilitySummaryChart(solverBenchmarkList));
        htmlFragment.append(writeAverageCalculateCountPerSecondSummaryChart(solverBenchmarkList));
        htmlFragment.append(writeBestScoreSummaryTable(solverBenchmarkList));
        htmlFragment.append("  <h1>Statistics</h1>\n");
        for (Map.Entry<File, List<SolverStatistic>> entry : unsolvedSolutionFileToStatisticMap.entrySet()) {
            File unsolvedSolutionFile = entry.getKey();
            List<SolverStatistic> statisticList = entry.getValue();
            String baseName = FilenameUtils.getBaseName(unsolvedSolutionFile.getName());
            htmlFragment.append("  <h2>").append(baseName).append("</h2>\n");
            for (SolverStatistic statistic : statisticList) {
                htmlFragment.append(statistic.writeStatistic(solverStatisticFilesDirectory, baseName));
            }
        }
        writeHtmlOverview(htmlFragment);
    }

    private CharSequence writeBestScoreSummaryChart(List<SolverBenchmark> solverBenchmarkList) {
        DefaultCategoryDataset dataset = new DefaultCategoryDataset();
        for (SolverBenchmark solverBenchmark : solverBenchmarkList) {
            ScoreDefinition scoreDefinition = solverBenchmark.getSolverConfig().getScoreDefinitionConfig()
                    .buildScoreDefinition();
            for (SolverBenchmarkResult result : solverBenchmark.getSolverBenchmarkResultList()) {
                Score score = result.getScore();
                Double scoreGraphValue = scoreDefinition.translateScoreToGraphValue(score);
                String solverLabel = solverBenchmark.getName();
                if (solverBenchmark.getRanking() == 0) {
                    solverLabel += " (winner)";
                }
                dataset.addValue(scoreGraphValue, solverLabel, result.getUnsolvedSolutionFile().getName());
            }
        }
        CategoryAxis xAxis = new CategoryAxis("Data");
        xAxis.setCategoryMargin(0.50d);
        NumberAxis yAxis = new NumberAxis("Score");
        BarRenderer renderer = new BarRenderer();
        ItemLabelPosition positiveItemLabelPosition = new ItemLabelPosition(
                ItemLabelAnchor.OUTSIDE12, TextAnchor.BOTTOM_CENTER);
        renderer.setBasePositiveItemLabelPosition(positiveItemLabelPosition);
        ItemLabelPosition negativeItemLabelPosition = new ItemLabelPosition(
                ItemLabelAnchor.OUTSIDE6, TextAnchor.TOP_CENTER);
        renderer.setBaseNegativeItemLabelPosition(negativeItemLabelPosition);
        renderer.setBaseItemLabelGenerator(new StandardCategoryItemLabelGenerator());
        renderer.setBaseItemLabelsVisible(true);
        CategoryPlot plot = new CategoryPlot(dataset, xAxis, yAxis,
                renderer);
        plot.setOrientation(PlotOrientation.VERTICAL);
        JFreeChart chart = new JFreeChart("Best score summary (higher score is better)", JFreeChart.DEFAULT_TITLE_FONT,
                plot, true);
        BufferedImage chartImage = chart.createBufferedImage(1024, 768);
        File chartSummaryFile = new File(solverStatisticFilesDirectory, "bestScoreSummary.png");
        OutputStream out = null;
        try {
            out = new FileOutputStream(chartSummaryFile);
            ImageIO.write(chartImage, "png", out);
        } catch (IOException e) {
            throw new IllegalArgumentException("Problem writing graphStatisticFile: " + chartSummaryFile, e);
        } finally {
            IOUtils.closeQuietly(out);
        }
        return "  <h2>Best score summary chart</h2>\n"
                + "  <img src=\"" + chartSummaryFile.getName() + "\"/>\n";
    }

    private CharSequence writeTimeSpendSummaryChart(List<SolverBenchmark> solverBenchmarkList) {
        DefaultCategoryDataset dataset = new DefaultCategoryDataset();
        for (SolverBenchmark solverBenchmark : solverBenchmarkList) {
            for (SolverBenchmarkResult result : solverBenchmark.getSolverBenchmarkResultList()) {
                long timeMillisSpend = result.getTimeMillisSpend();
                String solverLabel = solverBenchmark.getName();
                dataset.addValue(timeMillisSpend, solverLabel, result.getUnsolvedSolutionFile().getName());
            }
        }
        CategoryAxis xAxis = new CategoryAxis("Data");
        NumberAxis yAxis = new NumberAxis("Time spend");
        yAxis.setNumberFormatOverride(new MillisecondsSpendNumberFormat());
        BarRenderer renderer = new BarRenderer();
        ItemLabelPosition positiveItemLabelPosition = new ItemLabelPosition(
                ItemLabelAnchor.OUTSIDE12, TextAnchor.BOTTOM_CENTER);
        renderer.setBasePositiveItemLabelPosition(positiveItemLabelPosition);
        ItemLabelPosition negativeItemLabelPosition = new ItemLabelPosition(
                ItemLabelAnchor.OUTSIDE6, TextAnchor.TOP_CENTER);
        renderer.setBaseNegativeItemLabelPosition(negativeItemLabelPosition);
        renderer.setBaseItemLabelGenerator(new StandardCategoryItemLabelGenerator(
                StandardCategoryItemLabelGenerator.DEFAULT_LABEL_FORMAT_STRING, new MillisecondsSpendNumberFormat()));
        renderer.setBaseItemLabelsVisible(true);
        CategoryPlot plot = new CategoryPlot(dataset, xAxis, yAxis,
                renderer);
        plot.setOrientation(PlotOrientation.VERTICAL);
        JFreeChart chart = new JFreeChart("Time spend summary (lower time is better)", JFreeChart.DEFAULT_TITLE_FONT,
                plot, true);
        BufferedImage chartImage = chart.createBufferedImage(1024, 768);
        File chartSummaryFile = new File(solverStatisticFilesDirectory, "timeSpendSummary.png");
        OutputStream out = null;
        try {
            out = new FileOutputStream(chartSummaryFile);
            ImageIO.write(chartImage, "png", out);
        } catch (IOException e) {
            throw new IllegalArgumentException("Problem writing graphStatisticFile: " + chartSummaryFile, e);
        } finally {
            IOUtils.closeQuietly(out);
        }
        return "  <h2>Time spend summary chart</h2>\n"
                + "  <img src=\"" + chartSummaryFile.getName() + "\"/>\n";
    }

    private CharSequence writeScalabilitySummaryChart(List<SolverBenchmark> solverBenchmarkList) {
        NumberAxis xAxis = new NumberAxis("Problem scale");
        NumberAxis yAxis = new NumberAxis("Time spend");
        yAxis.setNumberFormatOverride(new MillisecondsSpendNumberFormat());
        XYPlot plot = new XYPlot(null, xAxis, yAxis, null);
        int seriesIndex = 0;
        for (SolverBenchmark solverBenchmark : solverBenchmarkList) {
            XYSeries series = new XYSeries(solverBenchmark.getName());
            ScoreDefinition scoreDefinition = solverBenchmark.getSolverConfig().getScoreDefinitionConfig()
                    .buildScoreDefinition();
            for (SolverBenchmarkResult result : solverBenchmark.getSolverBenchmarkResultList()) {
                long problemScale = result.getProblemScale();
                long timeMillisSpend = result.getTimeMillisSpend();
                series.add((Long) problemScale, (Long) timeMillisSpend);
            }
            XYSeriesCollection seriesCollection = new XYSeriesCollection();
            seriesCollection.addSeries(series);
            plot.setDataset(seriesIndex, seriesCollection);
            XYItemRenderer renderer = new StandardXYItemRenderer(StandardXYItemRenderer.SHAPES_AND_LINES);
            // Use dashed line
            renderer.setSeriesStroke(0, new BasicStroke(
                    1.0f, BasicStroke.CAP_ROUND, BasicStroke.JOIN_ROUND, 1.0f, new float[]{2.0f, 6.0f}, 0.0f
            ));
            plot.setRenderer(seriesIndex, renderer);
            seriesIndex++;
        }
        plot.setOrientation(PlotOrientation.VERTICAL);
        JFreeChart chart = new JFreeChart("Scalability summary (lower is better)",
                JFreeChart.DEFAULT_TITLE_FONT, plot, true);
        BufferedImage chartImage = chart.createBufferedImage(1024, 768);
        File chartSummaryFile = new File(solverStatisticFilesDirectory, "scalabilitySummary.png");
        OutputStream out = null;
        try {
            out = new FileOutputStream(chartSummaryFile);
            ImageIO.write(chartImage, "png", out);
        } catch (IOException e) {
            throw new IllegalArgumentException("Problem writing graphStatisticFile: " + chartSummaryFile, e);
        } finally {
            IOUtils.closeQuietly(out);
        }
        return "  <h2>Scalability summary chart</h2>\n"
                + "  <img src=\"" + chartSummaryFile.getName() + "\"/>\n";
    }

    private CharSequence writeAverageCalculateCountPerSecondSummaryChart(List<SolverBenchmark> solverBenchmarkList) {
        NumberAxis xAxis = new NumberAxis("Problem scale");
        NumberAxis yAxis = new NumberAxis("Average calculate count per second");
        XYPlot plot = new XYPlot(null, xAxis, yAxis, null);
        int seriesIndex = 0;
        for (SolverBenchmark solverBenchmark : solverBenchmarkList) {
            XYSeries series = new XYSeries(solverBenchmark.getName());
            ScoreDefinition scoreDefinition = solverBenchmark.getSolverConfig().getScoreDefinitionConfig()
                    .buildScoreDefinition();
            for (SolverBenchmarkResult result : solverBenchmark.getSolverBenchmarkResultList()) {
                long problemScale = result.getProblemScale();
                long averageCalculateCountPerSecond = result.getAverageCalculateCountPerSecond();
                series.add((Long) problemScale, (Long) averageCalculateCountPerSecond);
            }
            XYSeriesCollection seriesCollection = new XYSeriesCollection();
            seriesCollection.addSeries(series);
            plot.setDataset(seriesIndex, seriesCollection);
            XYItemRenderer renderer = new StandardXYItemRenderer(StandardXYItemRenderer.SHAPES_AND_LINES);
            // Use dashed line
            renderer.setSeriesStroke(0, new BasicStroke(
                    1.0f, BasicStroke.CAP_ROUND, BasicStroke.JOIN_ROUND, 1.0f, new float[]{2.0f, 6.0f}, 0.0f
            ));
            plot.setRenderer(seriesIndex, renderer);
            seriesIndex++;
        }
        plot.setOrientation(PlotOrientation.VERTICAL);
        JFreeChart chart = new JFreeChart("Average calculate count summary (higher is better)",
                JFreeChart.DEFAULT_TITLE_FONT, plot, true);
        BufferedImage chartImage = chart.createBufferedImage(1024, 768);
        File chartSummaryFile = new File(solverStatisticFilesDirectory, "averageCalculateCountSummary.png");
        OutputStream out = null;
        try {
            out = new FileOutputStream(chartSummaryFile);
            ImageIO.write(chartImage, "png", out);
        } catch (IOException e) {
            throw new IllegalArgumentException("Problem writing graphStatisticFile: " + chartSummaryFile, e);
        } finally {
            IOUtils.closeQuietly(out);
        }
        return "  <h2>Average calculate count summary chart</h2>\n"
                + "  <img src=\"" + chartSummaryFile.getName() + "\"/>\n";
    }

    private CharSequence writeBestScoreSummaryTable(List<SolverBenchmark> solverBenchmarkList) {
        StringBuilder htmlFragment = new StringBuilder(solverBenchmarkList.size() * 160);
        htmlFragment.append("  <h2>Best score summary table</h2>\n");
        htmlFragment.append("  <table border=\"1\">\n");
        htmlFragment.append("    <tr><th>Solver</th>");
        Set<File> unsolvedSolutionFileSet = new LinkedHashSet<File>();
        for (SolverBenchmark solverBenchmark : solverBenchmarkList) {
            for (SolverBenchmarkResult result : solverBenchmark.getSolverBenchmarkResultList()) {
                File unsolvedSolutionFile = result.getUnsolvedSolutionFile();
                if (unsolvedSolutionFileSet.add(unsolvedSolutionFile)) {
                    htmlFragment.append("<th>").append(unsolvedSolutionFile.getName()).append("</th>");
                }
            }
        }
        htmlFragment.append("<th>Average</th><th>Ranking</th></tr>\n");
        boolean oddLine = true;
        for (SolverBenchmark solverBenchmark : solverBenchmarkList) {
            String backgroundColor = solverBenchmark.getRanking() == 0 ? "Yellow" : oddLine ? "White" : "LightGray";
            htmlFragment.append("    <tr style=\"background-color: ").append(backgroundColor).append("\"><th>")
                    .append(solverBenchmark.getName()).append("</th>");
            for (File unsolvedSolutionFile : unsolvedSolutionFileSet) {
                boolean noResult = true;
                for (SolverBenchmarkResult result : solverBenchmark.getSolverBenchmarkResultList()) {
                    if (unsolvedSolutionFile.equals(result.getUnsolvedSolutionFile())) {
                        Score score = result.getScore();
                        htmlFragment.append("<td>").append(score.toString()).append("</td>");
                        noResult = false;
                        break;
                    }
                }
                if (noResult) {
                    htmlFragment.append("<td/>");
                }
            }
            htmlFragment.append("<td>").append(solverBenchmark.getAverageScore().toString())
                    .append("</td><td>").append(solverBenchmark.getRanking()).append("</td>");
            htmlFragment.append("</tr>\n");
            oddLine = !oddLine;
        }
        htmlFragment.append("  </table>\n");
        return htmlFragment.toString();
    }

    private void writeHtmlOverview(CharSequence htmlFragment) {
        File htmlOverviewFile = new File(solverStatisticFilesDirectory, "index.html");
        Writer writer = null;
        try {
            writer = new OutputStreamWriter(new FileOutputStream(htmlOverviewFile), "utf-8");
            writer.append("<html>\n");
            writer.append("<head>\n");
            writer.append("  <title>Statistic ").append(benchmarkName).append("</title>\n");
            writer.append("</head>\n");
            writer.append("<body>\n");
            writer.append(htmlFragment);
            writer.append("</body>\n");
            writer.append("</html>\n");
        } catch (IOException e) {
            throw new IllegalArgumentException("Problem writing htmlOverviewFile: " + htmlOverviewFile, e);
        } finally {
            IOUtils.closeQuietly(writer);
        }
    }

}
