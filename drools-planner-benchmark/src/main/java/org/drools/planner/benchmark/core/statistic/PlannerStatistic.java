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
import java.text.DecimalFormat;
import java.util.HashMap;
import java.util.Map;
import javax.imageio.ImageIO;

import freemarker.template.Configuration;
import freemarker.template.Template;
import freemarker.template.TemplateException;
import org.apache.commons.io.IOUtils;
import org.drools.planner.benchmark.core.DefaultPlannerBenchmark;
import org.drools.planner.benchmark.core.SingleBenchmark;
import org.drools.planner.benchmark.core.ProblemBenchmark;
import org.drools.planner.benchmark.core.SolverBenchmark;
import org.drools.planner.benchmark.core.measurement.ScoreDifferencePercentage;
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

    protected final DefaultPlannerBenchmark plannerBenchmark;
    protected final File htmlOverviewFile;

    protected File bestScoreSummaryFile = null;
    protected File winningScoreDifferenceSummaryFile = null;
    protected File worstScoreDifferencePercentageSummaryFile = null;
    protected File timeSpendSummaryFile = null;
    protected File scalabilitySummaryFile = null;
    protected File averageCalculateCountSummaryFile = null;

    public PlannerStatistic(DefaultPlannerBenchmark plannerBenchmark) {
        this.plannerBenchmark = plannerBenchmark;
        htmlOverviewFile = new File(plannerBenchmark.getBenchmarkReportDirectory(), "index.html");
    }

    public DefaultPlannerBenchmark getPlannerBenchmark() {
        return plannerBenchmark;
    }

    public File getBestScoreSummaryFile() {
        return bestScoreSummaryFile;
    }

    public File getWinningScoreDifferenceSummaryFile() {
        return winningScoreDifferenceSummaryFile;
    }

    public File getWorstScoreDifferencePercentageSummaryFile() {
        return worstScoreDifferencePercentageSummaryFile;
    }

    public File getTimeSpendSummaryFile() {
        return timeSpendSummaryFile;
    }

    public File getScalabilitySummaryFile() {
        return scalabilitySummaryFile;
    }

    public File getAverageCalculateCountSummaryFile() {
        return averageCalculateCountSummaryFile;
    }

    public void writeStatistics() {
        writeBestScoreSummaryChart();
        writeWinningScoreDifferenceSummaryChart();
        writeWorstScoreDifferencePercentageSummaryChart();
        writeTimeSpendSummaryChart();
        writeScalabilitySummaryChart();
        writeAverageCalculateCountPerSecondSummaryChart();
        for (ProblemBenchmark problemBenchmark : plannerBenchmark.getUnifiedProblemBenchmarkList()) {
            if (problemBenchmark.hasAnySuccess()) {
                for (ProblemStatistic problemStatistic : problemBenchmark.getProblemStatisticList()) {
                    problemStatistic.writeStatistic();
                }
            }
        }
        writeStatisticsWebsite();
    }

    private void writeBestScoreSummaryChart() {
        DefaultCategoryDataset dataset = new DefaultCategoryDataset();
        for (SolverBenchmark solverBenchmark : plannerBenchmark.getSolverBenchmarkList()) {
            ScoreDefinition scoreDefinition = solverBenchmark.getSolverConfig().getScoreDirectorFactoryConfig()
                    .buildScoreDefinition();
            for (SingleBenchmark singleBenchmark : solverBenchmark.getSingleBenchmarkList()) {
                if (singleBenchmark.isSuccess()) {
                    Score score = singleBenchmark.getScore();
                    Double scoreGraphValue = scoreDefinition.translateScoreToGraphValue(score);
                    String solverLabel = solverBenchmark.getNameWithFavoriteSuffix();
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
        JFreeChart chart = new JFreeChart("Best score summary (higher score is better)",
                JFreeChart.DEFAULT_TITLE_FONT, plot, true);
        BufferedImage chartImage = chart.createBufferedImage(1024, 768);
        bestScoreSummaryFile = new File(plannerBenchmark.getBenchmarkReportDirectory(), "bestScoreSummary.png");
        OutputStream out = null;
        try {
            out = new FileOutputStream(bestScoreSummaryFile);
            ImageIO.write(chartImage, "png", out);
        } catch (IOException e) {
            throw new IllegalArgumentException("Problem writing bestScoreSummaryFile: " + bestScoreSummaryFile, e);
        } finally {
            IOUtils.closeQuietly(out);
        }
    }

    private void writeWinningScoreDifferenceSummaryChart() {
        DefaultCategoryDataset dataset = new DefaultCategoryDataset();
        for (SolverBenchmark solverBenchmark : plannerBenchmark.getSolverBenchmarkList()) {
            ScoreDefinition scoreDefinition = solverBenchmark.getSolverConfig().getScoreDirectorFactoryConfig()
                    .buildScoreDefinition();
            for (SingleBenchmark singleBenchmark : solverBenchmark.getSingleBenchmarkList()) {
                if (singleBenchmark.isSuccess()) {
                    Score score = singleBenchmark.getWinningScoreDifference();
                    Double scoreGraphValue = scoreDefinition.translateScoreToGraphValue(score);
                    String solverLabel = solverBenchmark.getNameWithFavoriteSuffix();
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
        JFreeChart chart = new JFreeChart("Winning score difference summary (higher is better)",
                JFreeChart.DEFAULT_TITLE_FONT, plot, true);
        BufferedImage chartImage = chart.createBufferedImage(1024, 768);
        winningScoreDifferenceSummaryFile = new File(plannerBenchmark.getBenchmarkReportDirectory(),
                "winningScoreDifferenceSummary.png");
        OutputStream out = null;
        try {
            out = new FileOutputStream(winningScoreDifferenceSummaryFile);
            ImageIO.write(chartImage, "png", out);
        } catch (IOException e) {
            throw new IllegalArgumentException("Problem writing winningScoreDifferenceSummaryFile: "
                    + winningScoreDifferenceSummaryFile, e);
        } finally {
            IOUtils.closeQuietly(out);
        }
    }

    private void writeWorstScoreDifferencePercentageSummaryChart() {
        DefaultCategoryDataset dataset = new DefaultCategoryDataset();
        for (SolverBenchmark solverBenchmark : plannerBenchmark.getSolverBenchmarkList()) {
            for (SingleBenchmark singleBenchmark : solverBenchmark.getSingleBenchmarkList()) {
                if (singleBenchmark.isSuccess()) {
                    double graphValue = singleBenchmark.getWorstScoreDifferencePercentage().getLastPercentageLevel();
                    String solverLabel = solverBenchmark.getNameWithFavoriteSuffix();
                    String planningProblemLabel = singleBenchmark.getProblemBenchmark().getName();
                    dataset.addValue(graphValue, solverLabel, planningProblemLabel);
                }
            }
        }
        CategoryAxis xAxis = new CategoryAxis("Data");
        xAxis.setCategoryMargin(0.40);
        NumberAxis yAxis = new NumberAxis("Worst score difference percentage");
        yAxis.setNumberFormatOverride(new DecimalFormat("0.00%"));
        BarRenderer renderer = new BarRenderer();
        ItemLabelPosition positiveItemLabelPosition = new ItemLabelPosition(
                ItemLabelAnchor.OUTSIDE12, TextAnchor.BOTTOM_CENTER);
        renderer.setBasePositiveItemLabelPosition(positiveItemLabelPosition);
        ItemLabelPosition negativeItemLabelPosition = new ItemLabelPosition(
                ItemLabelAnchor.OUTSIDE6, TextAnchor.TOP_CENTER);
        renderer.setBaseNegativeItemLabelPosition(negativeItemLabelPosition);
        renderer.setBaseItemLabelGenerator(new StandardCategoryItemLabelGenerator(
                StandardCategoryItemLabelGenerator.DEFAULT_LABEL_FORMAT_STRING, new DecimalFormat("0.00%")));
        renderer.setBaseItemLabelsVisible(true);
        CategoryPlot plot = new CategoryPlot(dataset, xAxis, yAxis,
                renderer);
        plot.setOrientation(PlotOrientation.VERTICAL);
        JFreeChart chart = new JFreeChart("Worst score difference percentage summary (higher is better)",
                JFreeChart.DEFAULT_TITLE_FONT, plot, true);
        BufferedImage chartImage = chart.createBufferedImage(1024, 768);
        worstScoreDifferencePercentageSummaryFile = new File(plannerBenchmark.getBenchmarkReportDirectory(),
                "worstScoreDifferencePercentageSummary.png");
        OutputStream out = null;
        try {
            out = new FileOutputStream(worstScoreDifferencePercentageSummaryFile);
            ImageIO.write(chartImage, "png", out);
        } catch (IOException e) {
            throw new IllegalArgumentException("Problem writing worstScoreDifferencePercentageSummaryFile: "
                    + worstScoreDifferencePercentageSummaryFile, e);
        } finally {
            IOUtils.closeQuietly(out);
        }
    }

    private void writeTimeSpendSummaryChart() {
        DefaultCategoryDataset dataset = new DefaultCategoryDataset();
        for (SolverBenchmark solverBenchmark : plannerBenchmark.getSolverBenchmarkList()) {
            for (SingleBenchmark singleBenchmark : solverBenchmark.getSingleBenchmarkList()) {
                if (singleBenchmark.isSuccess()) {
                    long timeMillisSpend = singleBenchmark.getTimeMillisSpend();
                    String solverLabel = solverBenchmark.getNameWithFavoriteSuffix();
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
        JFreeChart chart = new JFreeChart("Time spend summary (lower time is better)",
                JFreeChart.DEFAULT_TITLE_FONT, plot, true);
        BufferedImage chartImage = chart.createBufferedImage(1024, 768);
        timeSpendSummaryFile = new File(plannerBenchmark.getBenchmarkReportDirectory(), "timeSpendSummary.png");
        OutputStream out = null;
        try {
            out = new FileOutputStream(timeSpendSummaryFile);
            ImageIO.write(chartImage, "png", out);
        } catch (IOException e) {
            throw new IllegalArgumentException("Problem writing timeSpendSummaryFile: " + timeSpendSummaryFile, e);
        } finally {
            IOUtils.closeQuietly(out);
        }
    }

    private void writeScalabilitySummaryChart() {
        NumberAxis xAxis = new NumberAxis("Problem scale");
        NumberAxis yAxis = new NumberAxis("Time spend");
        yAxis.setNumberFormatOverride(new MillisecondsSpendNumberFormat());
        XYPlot plot = new XYPlot(null, xAxis, yAxis, null);
        int seriesIndex = 0;
        for (SolverBenchmark solverBenchmark : plannerBenchmark.getSolverBenchmarkList()) {
            String solverLabel = solverBenchmark.getNameWithFavoriteSuffix();
            XYSeries series = new XYSeries(solverLabel);
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
        scalabilitySummaryFile = new File(plannerBenchmark.getBenchmarkReportDirectory(), "scalabilitySummary.png");
        OutputStream out = null;
        try {
            out = new FileOutputStream(scalabilitySummaryFile);
            ImageIO.write(chartImage, "png", out);
        } catch (IOException e) {
            throw new IllegalArgumentException("Problem writing scalabilitySummaryFile: " + scalabilitySummaryFile, e);
        } finally {
            IOUtils.closeQuietly(out);
        }
    }

    private void writeAverageCalculateCountPerSecondSummaryChart() {
        NumberAxis xAxis = new NumberAxis("Problem scale");
        NumberAxis yAxis = new NumberAxis("Average calculate count per second");
        XYPlot plot = new XYPlot(null, xAxis, yAxis, null);
        int seriesIndex = 0;
        for (SolverBenchmark solverBenchmark : plannerBenchmark.getSolverBenchmarkList()) {
            String solverLabel = solverBenchmark.getNameWithFavoriteSuffix();
            XYSeries series = new XYSeries(solverLabel);
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
        averageCalculateCountSummaryFile = new File(plannerBenchmark.getBenchmarkReportDirectory(),
                "averageCalculateCountSummary.png");
        OutputStream out = null;
        try {
            out = new FileOutputStream(averageCalculateCountSummaryFile);
            ImageIO.write(chartImage, "png", out);
        } catch (IOException e) {
            throw new IllegalArgumentException("Problem writing averageCalculateCountSummaryFile: "
                    + averageCalculateCountSummaryFile, e);
        } finally {
            IOUtils.closeQuietly(out);
        }
    }

    private void writeStatisticsWebsite() {
        WebsiteResourceUtils.copyResourcesTo(plannerBenchmark.getBenchmarkReportDirectory());

        Configuration freemarkerCfg = new Configuration();
        freemarkerCfg.setDefaultEncoding("UTF-8");
        // TODO Benchmark reports should be interchangeable? If yes, also fix the locale for the charts
        // freemarkerCfg.setLocale(Locale.US);
        freemarkerCfg.setClassForTemplateLoading(PlannerStatistic.class, "");

        String templateFilename = "index.html.ftl";
        Map<String, Object> model = new HashMap<String, Object>();
        model.put("plannerStatistic", this);

        Writer writer = null;
        try {
            Template template = freemarkerCfg.getTemplate(templateFilename);
            writer = new OutputStreamWriter(new FileOutputStream(htmlOverviewFile), "UTF-8");
            template.process(model, writer);
        } catch (IOException e) {
            throw new IllegalArgumentException("Can not read templateFilename (" + templateFilename
                    + ") or write htmlOverviewFile (" + htmlOverviewFile + ").", e);
        } catch (TemplateException e) {
            throw new IllegalArgumentException("Can not process Freemarker templateFilename (" + templateFilename
                    + ") to htmlOverviewFile (" + htmlOverviewFile + ").", e);
        } finally {
            IOUtils.closeQuietly(writer);
        }
    }

    public File getHtmlOverviewFile() {
        return htmlOverviewFile;
    }

}
