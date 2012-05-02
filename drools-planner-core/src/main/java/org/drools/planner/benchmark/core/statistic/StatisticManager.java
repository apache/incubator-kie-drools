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

package org.drools.planner.benchmark.core.statistic;

import java.awt.BasicStroke;
import java.awt.image.BufferedImage;
import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.OutputStream;
import java.io.OutputStreamWriter;
import java.io.Writer;
import java.util.List;
import javax.imageio.ImageIO;

import org.apache.commons.io.IOUtils;
import org.drools.planner.benchmark.core.SingleBenchmark;
import org.drools.planner.benchmark.core.ProblemBenchmark;
import org.drools.planner.benchmark.core.SolverBenchmark;
import org.drools.planner.core.score.Score;
import org.drools.planner.core.score.definition.ScoreDefinition;
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
import org.jfree.chart.renderer.xy.StandardXYItemRenderer;
import org.jfree.chart.renderer.xy.XYItemRenderer;
import org.jfree.data.category.DefaultCategoryDataset;
import org.jfree.data.xy.XYSeries;
import org.jfree.data.xy.XYSeriesCollection;
import org.jfree.ui.TextAnchor;

public class StatisticManager {

    private final String benchmarkName;
    private final File statisticDirectory;
    private final File htmlOverviewFile;
    private final List<ProblemBenchmark> problemBenchmarkList;

    public StatisticManager(String benchmarkName, File statisticDirectory,
            List<ProblemBenchmark> problemBenchmarkList) {
        this.benchmarkName = benchmarkName;
        this.statisticDirectory = statisticDirectory;
        htmlOverviewFile = new File(statisticDirectory, "index.html");
        this.problemBenchmarkList = problemBenchmarkList;
    }

    public void writeStatistics(List<SolverBenchmark> solverBenchmarkList) {
        // 2 lines at 80 chars per line give a max of 160 per entry
        StringBuilder htmlFragment = new StringBuilder(problemBenchmarkList.size() * 160);
        htmlFragment.append("  <h1>Summary</h1>\n");
        htmlFragment.append(writeBestScoreSummaryChart(solverBenchmarkList));
        htmlFragment.append(writeWinningScoreDifferenceSummaryChart(solverBenchmarkList));
        htmlFragment.append(writeTimeSpendSummaryChart(solverBenchmarkList));
        htmlFragment.append(writeScalabilitySummaryChart(solverBenchmarkList));
        htmlFragment.append(writeAverageCalculateCountPerSecondSummaryChart(solverBenchmarkList));
        htmlFragment.append(writeBestScoreSummaryTable(solverBenchmarkList));
        htmlFragment.append("  <h1>Statistics</h1>\n");
        for (ProblemBenchmark problemBenchmark : problemBenchmarkList) {
            String problemBenchmarkName = problemBenchmark.getName();
            htmlFragment.append("  <h2>").append(problemBenchmarkName).append("</h2>\n");
            for (ProblemStatistic statistic : problemBenchmark.getProblemStatisticList()) {
                htmlFragment.append(
                        statistic.writeStatistic(statisticDirectory, problemBenchmarkName));
            }
        }
        writeHtmlOverview(htmlFragment);
    }

    private CharSequence writeBestScoreSummaryChart(List<SolverBenchmark> solverBenchmarkList) {
        DefaultCategoryDataset dataset = new DefaultCategoryDataset();
        for (SolverBenchmark solverBenchmark : solverBenchmarkList) {
            ScoreDefinition scoreDefinition = solverBenchmark.getSolverConfig().getScoreDirectorFactoryConfig()
                    .buildScoreDefinition();
            for (SingleBenchmark singleBenchmark : solverBenchmark.getSingleBenchmarkList()) {
                Score score = singleBenchmark.getScore();
                Double scoreGraphValue = scoreDefinition.translateScoreToGraphValue(score);
                String solverLabel = solverBenchmark.getName();
                if (solverBenchmark.isRankingBest()) {
                    solverLabel += " (winner)";
                }
                String planningProblemLabel = singleBenchmark.getProblemBenchmark().getName();
                dataset.addValue(scoreGraphValue, solverLabel, planningProblemLabel);
            }
        }
        CategoryAxis xAxis = new CategoryAxis("Data");
        xAxis.setCategoryMargin(0.40);
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
        File chartSummaryFile = new File(statisticDirectory, "bestScoreSummary.png");
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

    private CharSequence writeWinningScoreDifferenceSummaryChart(List<SolverBenchmark> solverBenchmarkList) {
        DefaultCategoryDataset dataset = new DefaultCategoryDataset();
        for (SolverBenchmark solverBenchmark : solverBenchmarkList) {
            ScoreDefinition scoreDefinition = solverBenchmark.getSolverConfig().getScoreDirectorFactoryConfig()
                    .buildScoreDefinition();
            for (SingleBenchmark singleBenchmark : solverBenchmark.getSingleBenchmarkList()) {
                Score score = singleBenchmark.getWinningScoreDifference();
                Double scoreGraphValue = scoreDefinition.translateScoreToGraphValue(score);
                String solverLabel = solverBenchmark.getName();
                if (solverBenchmark.isRankingBest()) {
                    solverLabel += " (winner)";
                }
                String planningProblemLabel = singleBenchmark.getProblemBenchmark().getName();
                dataset.addValue(scoreGraphValue, solverLabel, planningProblemLabel);
            }
        }
        CategoryAxis xAxis = new CategoryAxis("Data");
        xAxis.setCategoryMargin(0.40);
        NumberAxis yAxis = new NumberAxis("Winning score difference");
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
        JFreeChart chart = new JFreeChart("Winning score difference summary (higher is better)", JFreeChart.DEFAULT_TITLE_FONT,
                plot, true);
        BufferedImage chartImage = chart.createBufferedImage(1024, 768);
        File chartSummaryFile = new File(statisticDirectory, "winningScoreDifferenceSummary.png");
        OutputStream out = null;
        try {
            out = new FileOutputStream(chartSummaryFile);
            ImageIO.write(chartImage, "png", out);
        } catch (IOException e) {
            throw new IllegalArgumentException("Problem writing graphStatisticFile: " + chartSummaryFile, e);
        } finally {
            IOUtils.closeQuietly(out);
        }
        return "  <h2>Winning score difference summary chart</h2>\n"
                + "  <img src=\"" + chartSummaryFile.getName() + "\"/>\n";
    }

    private CharSequence writeTimeSpendSummaryChart(List<SolverBenchmark> solverBenchmarkList) {
        DefaultCategoryDataset dataset = new DefaultCategoryDataset();
        for (SolverBenchmark solverBenchmark : solverBenchmarkList) {
            for (SingleBenchmark singleBenchmark : solverBenchmark.getSingleBenchmarkList()) {
                long timeMillisSpend = singleBenchmark.getTimeMillisSpend();
                String solverLabel = solverBenchmark.getName();
                String planningProblemLabel = singleBenchmark.getProblemBenchmark().getName();
                dataset.addValue(timeMillisSpend, solverLabel, planningProblemLabel);
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
        File chartSummaryFile = new File(statisticDirectory, "timeSpendSummary.png");
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
            for (SingleBenchmark singleBenchmark : solverBenchmark.getSingleBenchmarkList()) {
                long problemScale = singleBenchmark.getProblemScale();
                long timeMillisSpend = singleBenchmark.getTimeMillisSpend();
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
        File chartSummaryFile = new File(statisticDirectory, "scalabilitySummary.png");
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
            for (SingleBenchmark singleBenchmark : solverBenchmark.getSingleBenchmarkList()) {
                long problemScale = singleBenchmark.getProblemScale();
                long averageCalculateCountPerSecond = singleBenchmark.getAverageCalculateCountPerSecond();
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
        File chartSummaryFile = new File(statisticDirectory, "averageCalculateCountSummary.png");
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
        for (ProblemBenchmark problemBenchmark : problemBenchmarkList) {
            htmlFragment.append("<th>").append(problemBenchmark.getName()).append("</th>");
        }
        htmlFragment.append("<th>Average</th><th>Ranking</th></tr>\n");
        boolean oddLine = true;
        for (SolverBenchmark solverBenchmark : solverBenchmarkList) {
            String backgroundColor = solverBenchmark.isRankingBest() ? "Yellow" : oddLine ? "White" : "LightGray";
            htmlFragment.append("    <tr style=\"background-color: ").append(backgroundColor).append("\"><th>")
                    .append(solverBenchmark.getName()).append("</th>");
            for (ProblemBenchmark problemBenchmark : problemBenchmarkList) {
                boolean noSingleBenchmark = true;
                for (SingleBenchmark singleBenchmark : solverBenchmark.getSingleBenchmarkList()) {
                    if (problemBenchmark.equals(singleBenchmark.getProblemBenchmark())) {
                        Score score = singleBenchmark.getScore();
                        htmlFragment.append("<td>").append(score.toString()).append("</td>");
                        noSingleBenchmark = false;
                        break;
                    }
                }
                if (noSingleBenchmark) {
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
        Writer writer = null;
        try {
            writer = new OutputStreamWriter(new FileOutputStream(htmlOverviewFile), "UTF-8");
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

    public File getHtmlOverviewFile() {
        return htmlOverviewFile;
    }

}
