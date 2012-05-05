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
import org.drools.planner.benchmark.core.DefaultPlannerBenchmark;
import org.drools.planner.benchmark.core.SingleBenchmark;
import org.drools.planner.benchmark.core.ProblemBenchmark;
import org.drools.planner.benchmark.core.SolverBenchmark;
import org.drools.planner.benchmark.core.statistic.twitterbootstrap.TwitterBootstrapUtils;
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

public class PlannerStatistic {

    private final DefaultPlannerBenchmark plannerBenchmark;
    private final File statisticDirectory;
    private final File htmlOverviewFile;

    public PlannerStatistic(DefaultPlannerBenchmark plannerBenchmark) {
        this.plannerBenchmark = plannerBenchmark;
        this.statisticDirectory = plannerBenchmark.getStatisticDirectory();
        htmlOverviewFile = new File(statisticDirectory, "index.html");
    }

    public void writeStatistics(List<SolverBenchmark> solverBenchmarkList) {
        TwitterBootstrapUtils.copyResourcesTo(statisticDirectory);
        // 2 lines at 80 chars per line give a max of 160 per entry
        StringBuilder htmlFragment = new StringBuilder(plannerBenchmark.getUnifiedProblemBenchmarkList().size() * 160);



        htmlFragment.append("  <div class=\"navbar navbar-fixed-top\">\n");
        htmlFragment.append("    <div class=\"navbar-inner\">\n");
        htmlFragment.append("      <div class=\"container\">\n");
        htmlFragment.append("        <ul class=\"nav\">\n");
        htmlFragment.append("          <li><a href=\"#summary\">Summary</a></li>\n");
        for (ProblemBenchmark problemBenchmark : plannerBenchmark.getUnifiedProblemBenchmarkList()) {
            htmlFragment.append("          <li><a href=\"#problem_").append(problemBenchmark.getName()).append("\">")
                    .append(problemBenchmark.getName()).append("</a></li>\n");
        }
        htmlFragment.append("        </ul>\n");
        htmlFragment.append("      </div>\n");
        htmlFragment.append("    </div>\n");
        htmlFragment.append("  </div>\n");


        htmlFragment.append("  <section id=\"summary\">\n");
        htmlFragment.append("    <h1>Summary</h1>\n");
        htmlFragment.append(writeBestScoreSummaryChart(solverBenchmarkList));
        htmlFragment.append(writeWinningScoreDifferenceSummaryChart(solverBenchmarkList));
        htmlFragment.append(writeTimeSpendSummaryChart(solverBenchmarkList));
        htmlFragment.append(writeScalabilitySummaryChart(solverBenchmarkList));
        htmlFragment.append(writeAverageCalculateCountPerSecondSummaryChart(solverBenchmarkList));
        htmlFragment.append(writeBestScoreSummaryTable(solverBenchmarkList));
        htmlFragment.append("  </section>\n");
        htmlFragment.append("  <h1>Solver benchmarks</h1>\n");
        htmlFragment.append("  <p>TODO</p>\n");
        htmlFragment.append("  <h1>Problem benchmarks</h1>\n");
        for (ProblemBenchmark problemBenchmark : plannerBenchmark.getUnifiedProblemBenchmarkList()) {
            htmlFragment.append("  <section id=\"problem_").append(problemBenchmark.getName()).append("\">\n");
            htmlFragment.append("    <div class=\"page-header\">\n");
            htmlFragment.append("      <h2>").append(problemBenchmark.getName()).append("</h2>\n");
            htmlFragment.append("    </div>\n");
            if (problemBenchmark.hasFailure()) {
                htmlFragment.append("    <p>This has ").append(problemBenchmark.getFailureCount())
                        .append(" failures.</p>\n");
            }
            if (problemBenchmark.hasAnySuccess() && problemBenchmark.getProblemStatisticList().size() > 0) {
                htmlFragment.append("    <div class=\"tabbable\">\n");
                htmlFragment.append("      <ul class=\"nav nav-tabs\">\n");
                boolean firstRow = true;
                for (ProblemStatistic problemStatistic : problemBenchmark.getProblemStatisticList()) {
                    htmlFragment.append("        <li").append(firstRow ? " class=\"active\"" : "")
                            .append("><a href=\"#problemStatistic_").append(problemStatistic.getAnchorId())
                            .append("\" data-toggle=\"tab\">").append(problemStatistic.getProblemStatisticType())
                            .append("</a></li>\n");
                    firstRow = false;
                }
                htmlFragment.append("      </ul>\n");
                htmlFragment.append("      <div class=\"tab-content\">\n");
                firstRow = true;
                for (ProblemStatistic problemStatistic : problemBenchmark.getProblemStatisticList()) {
                    htmlFragment.append("        <div class=\"tab-pane").append(firstRow ? " active" : "")
                            .append("\" id=\"problemStatistic_")
                            .append(problemStatistic.getAnchorId()) .append("\">\n");
                    htmlFragment.append(
                            problemStatistic.writeStatistic(statisticDirectory, problemBenchmark));
                    htmlFragment.append("        </div>\n");
                    firstRow = false;
                }
                htmlFragment.append("      </div>\n");
                htmlFragment.append("    </div>\n");
            }
            htmlFragment.append("  </section>\n");
        }
        writeHtmlOverview(htmlFragment);
    }

    private CharSequence writeBestScoreSummaryChart(List<SolverBenchmark> solverBenchmarkList) {
        DefaultCategoryDataset dataset = new DefaultCategoryDataset();
        for (SolverBenchmark solverBenchmark : solverBenchmarkList) {
            ScoreDefinition scoreDefinition = solverBenchmark.getSolverConfig().getScoreDirectorFactoryConfig()
                    .buildScoreDefinition();
            for (SingleBenchmark singleBenchmark : solverBenchmark.getSingleBenchmarkList()) {
                if (singleBenchmark.isSuccess()) {
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
        return "    <h2>Best score summary chart</h2>\n"
                + "  <img src=\"" + chartSummaryFile.getName() + "\"/>\n";
    }

    private CharSequence writeWinningScoreDifferenceSummaryChart(List<SolverBenchmark> solverBenchmarkList) {
        DefaultCategoryDataset dataset = new DefaultCategoryDataset();
        for (SolverBenchmark solverBenchmark : solverBenchmarkList) {
            ScoreDefinition scoreDefinition = solverBenchmark.getSolverConfig().getScoreDirectorFactoryConfig()
                    .buildScoreDefinition();
            for (SingleBenchmark singleBenchmark : solverBenchmark.getSingleBenchmarkList()) {
                if (singleBenchmark.isSuccess()) {
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
        return "    <h2>Winning score difference summary chart</h2>\n"
                + "  <img src=\"" + chartSummaryFile.getName() + "\"/>\n";
    }

    private CharSequence writeTimeSpendSummaryChart(List<SolverBenchmark> solverBenchmarkList) {
        DefaultCategoryDataset dataset = new DefaultCategoryDataset();
        for (SolverBenchmark solverBenchmark : solverBenchmarkList) {
            for (SingleBenchmark singleBenchmark : solverBenchmark.getSingleBenchmarkList()) {
                if (singleBenchmark.isSuccess()) {
                    long timeMillisSpend = singleBenchmark.getTimeMillisSpend();
                    String solverLabel = solverBenchmark.getName();
                    String planningProblemLabel = singleBenchmark.getProblemBenchmark().getName();
                    dataset.addValue(timeMillisSpend, solverLabel, planningProblemLabel);
                }
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
        return "    <h2>Time spend summary chart</h2>\n"
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
                if (singleBenchmark.isSuccess()) {
                    long problemScale = singleBenchmark.getProblemScale();
                    long timeMillisSpend = singleBenchmark.getTimeMillisSpend();
                    series.add((Long) problemScale, (Long) timeMillisSpend);
                }
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
        return "    <h2>Scalability summary chart</h2>\n"
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
                if (singleBenchmark.isSuccess()) {
                    long problemScale = singleBenchmark.getProblemScale();
                    long averageCalculateCountPerSecond = singleBenchmark.getAverageCalculateCountPerSecond();
                    series.add((Long) problemScale, (Long) averageCalculateCountPerSecond);
                }
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
        return "    <h2>Average calculate count summary chart</h2>\n"
                + "  <img src=\"" + chartSummaryFile.getName() + "\"/>\n";
    }

    private CharSequence writeBestScoreSummaryTable(List<SolverBenchmark> solverBenchmarkList) {
        StringBuilder htmlFragment = new StringBuilder(solverBenchmarkList.size() * 160);
        htmlFragment.append("    <h2>Best score summary table</h2>\n");
        htmlFragment.append("    <table class=\"table table-striped table-bordered\">\n");
        htmlFragment.append("      <tr><th>Solver</th>");
        for (ProblemBenchmark problemBenchmark : plannerBenchmark.getUnifiedProblemBenchmarkList()) {
            htmlFragment.append("<th>").append(problemBenchmark.getName()).append("</th>");
        }
        htmlFragment.append("<th>Average</th><th>Ranking</th></tr>\n");
        for (SolverBenchmark solverBenchmark : solverBenchmarkList) {
            htmlFragment.append("    <tr>");
            htmlFragment.append("<th>").append(solverBenchmark.getName()).append("</th>");
            for (ProblemBenchmark problemBenchmark : plannerBenchmark.getUnifiedProblemBenchmarkList()) {
                SingleBenchmark singleBenchmark = null;
                for (SingleBenchmark possibleSingleBenchmark : solverBenchmark.getSingleBenchmarkList()) {
                    if (problemBenchmark.equals(possibleSingleBenchmark.getProblemBenchmark())) {
                        singleBenchmark = possibleSingleBenchmark;
                        break;
                    }
                }
                if (singleBenchmark == null) {
                    htmlFragment.append("<td/>");
                } else if (!singleBenchmark.isSuccess()) {
                    htmlFragment.append("<td><span class=\"label warning\">Failed</span></td>");
                } else {
                    Score score = singleBenchmark.getScore();
                    htmlFragment.append("<td>").append(score.toString()).append("</td>");
                }
            }
            htmlFragment.append("<td>").append(solverBenchmark.getAverageScore()).append("</td>");
            Integer ranking = solverBenchmark.getRanking();
            if (ranking == null) {
                htmlFragment.append("<td/>");
            } else if (solverBenchmark.isRankingBest()) {
                htmlFragment.append("<td><span class=\"badge badge-success\">").append(ranking).append("</span></td>");
            } else {
                htmlFragment.append("<td><span class=\"badge\">").append(ranking).append("</span></td>");
            }
            htmlFragment.append("</tr>\n");
        }
        htmlFragment.append("    </table>\n");
        return htmlFragment.toString();
    }

    private void writeHtmlOverview(CharSequence htmlFragment) {
        Writer writer = null;
        try {
            writer = new OutputStreamWriter(new FileOutputStream(htmlOverviewFile), "UTF-8");
            writer.append("<!DOCTYPE html>\n");
            writer.append("<html lang=\"en\">\n");
            writer.append("<head>\n");
            writer.append("  <title>Statistic ").append(plannerBenchmark.getName()).append("</title>\n");
            writer.append("  <meta name=\"viewport\" content=\"width=device-width, initial-scale=1.0\">\n");
            writer.append("  <link href=\"css/bootstrap.css\" rel=\"stylesheet\">\n");
            writer.append("  <link href=\"css/bootstrap-responsive.css\" rel=\"stylesheet\">\n");
            writer.append("  <!-- Le HTML5 shim, for IE6-8 support of HTML5 elements -->\n");
            writer.append("  <!--[if lt IE 9]>\n");
            writer.append("    <script src=\"http://html5shim.googlecode.com/svn/trunk/html5.js\"></script>\n");
            writer.append("  <![endif]-->\n");
            writer.append("</head>\n");
            writer.append("<body>\n");
            writer.append(htmlFragment);
            writer.append("  <script src=\"js/jquery.js\"></script>\n");
            writer.append("  <script src=\"js/bootstrap.js\"></script>\n");
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
