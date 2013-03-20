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

package org.optaplanner.benchmark.impl.report;

import java.awt.BasicStroke;
import java.awt.image.BufferedImage;
import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.OutputStream;
import java.io.OutputStreamWriter;
import java.io.Writer;
import java.text.NumberFormat;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Locale;
import java.util.Map;
import javax.imageio.ImageIO;

import freemarker.template.Configuration;
import freemarker.template.Template;
import freemarker.template.TemplateException;
import org.apache.commons.io.IOUtils;
import org.optaplanner.benchmark.impl.DefaultPlannerBenchmark;
import org.optaplanner.benchmark.impl.ProblemBenchmark;
import org.optaplanner.benchmark.impl.SingleBenchmark;
import org.optaplanner.benchmark.impl.SolverBenchmark;
import org.optaplanner.benchmark.impl.statistic.MillisecondsSpendNumberFormat;
import org.optaplanner.benchmark.impl.statistic.ProblemStatistic;
import org.optaplanner.core.api.solver.SolverFactory;
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

public class BenchmarkReport {

    public static final int CHARTED_SCORE_LEVEL_SIZE = 5;

    protected final DefaultPlannerBenchmark plannerBenchmark;
    protected Locale locale;

    protected File htmlOverviewFile = null;
    protected File summaryDirectory = null;
    protected List<File> bestScoreSummaryChartFileList = null;
    protected List<File> bestScoreScalabilitySummaryChartFileList = null;
    protected List<File> winningScoreDifferenceSummaryChartFileList = null;
    protected List<File> worstScoreDifferencePercentageSummaryChartFileList = null;
    protected File timeSpendSummaryChartFile = null;
    protected File timeSpendScalabilitySummaryChartFile = null;
    protected File averageCalculateCountSummaryChartFile = null;
    protected Integer defaultShownScoreLevelIndex = null;

    protected List<String> warningList = null;

    public BenchmarkReport(DefaultPlannerBenchmark plannerBenchmark) {
        this.plannerBenchmark = plannerBenchmark;
    }

    public DefaultPlannerBenchmark getPlannerBenchmark() {
        return plannerBenchmark;
    }

    public Locale getLocale() {
        return locale;
    }

    public void setLocale(Locale locale) {
        this.locale = locale;
    }

    public File getHtmlOverviewFile() {
        return htmlOverviewFile;
    }

    public File getSummaryDirectory() {
        return summaryDirectory;
    }

    public List<File> getBestScoreSummaryChartFileList() {
        return bestScoreSummaryChartFileList;
    }

    public List<File> getBestScoreScalabilitySummaryChartFileList() {
        return bestScoreScalabilitySummaryChartFileList;
    }

    public List<File> getWinningScoreDifferenceSummaryChartFileList() {
        return winningScoreDifferenceSummaryChartFileList;
    }

    public List<File> getWorstScoreDifferencePercentageSummaryChartFileList() {
        return worstScoreDifferencePercentageSummaryChartFileList;
    }

    public File getTimeSpendSummaryChartFile() {
        return timeSpendSummaryChartFile;
    }

    public File getTimeSpendScalabilitySummaryChartFile() {
        return timeSpendScalabilitySummaryChartFile;
    }

    public File getAverageCalculateCountSummaryChartFile() {
        return averageCalculateCountSummaryChartFile;
    }

    public Integer getDefaultShownScoreLevelIndex() {
        return defaultShownScoreLevelIndex;
    }

    public List<String> getWarningList() {
        return warningList;
    }

    // ************************************************************************
    // Write methods
    // ************************************************************************

    public int getAvailableProcessors() {
        return Runtime.getRuntime().availableProcessors();
    }

    public long getMaxMemory() {
        return Runtime.getRuntime().maxMemory();
    }

    public String getJavaVersion() {
        return "Java " + System.getProperty("java.version") + " (" + System.getProperty("java.vendor") + ")";
    }

    public String getJavaVM() {
        return "Java " + System.getProperty("java.vm.name") + " " + System.getProperty("java.vm.version")
                + " (" + System.getProperty("java.vm.vendor") + ")";
    }

    public String getOperatingSystem() {
        return System.getProperty("os.name") + " " + System.getProperty("os.arch")
                + " " + System.getProperty("os.version");
    }

    /**
     * @return sometimes null (only during development)
     */
    public String getPlannerVersion() {
        return SolverFactory.class.getPackage().getImplementationVersion();
    }

    public void writeReport() {
        summaryDirectory = new File(plannerBenchmark.getBenchmarkReportDirectory(), "summary");
        summaryDirectory.mkdir();
        fillWarningList();
        writeBestScoreSummaryCharts();
        writeBestScoreScalabilitySummaryChart();
        writeWinningScoreDifferenceSummaryChart();
        writeWorstScoreDifferencePercentageSummaryChart();
        writeTimeSpendSummaryChart();
        writeTimeSpendScalabilitySummaryChart();
        writeAverageCalculateCountPerSecondSummaryChart();
        for (ProblemBenchmark problemBenchmark : plannerBenchmark.getUnifiedProblemBenchmarkList()) {
            if (problemBenchmark.hasAnySuccess()) {
                for (ProblemStatistic problemStatistic : problemBenchmark.getProblemStatisticList()) {
                    problemStatistic.writeStatistic();
                }
            }
        }
        determineDefaultShownScoreLevelIndex();
        writeHtmlOverviewFile();
    }

    protected void fillWarningList() {
        warningList = new ArrayList<String>();
        String javaVmName = System.getProperty("java.vm.name");
        if (javaVmName != null && javaVmName.contains("Client VM")) {
            warningList.add("The Java VM (" + javaVmName + ") is the Client VM."
                    + " Consider starting the java process with the argument \"-server\" to get better results.");
        }
        int availableProcessors = getAvailableProcessors();
        if (plannerBenchmark.getParallelBenchmarkCount() > availableProcessors) {
            warningList.add("The parallelBenchmarkCount (" + plannerBenchmark.getParallelBenchmarkCount()
                    + ") is higher than the number of availableProcessors (" + availableProcessors + ").");
        }
    }

    private void writeBestScoreSummaryCharts() {
        // Each scoreLevel has it's own dataset and chartFile
        List<DefaultCategoryDataset> datasetList = new ArrayList<DefaultCategoryDataset>(CHARTED_SCORE_LEVEL_SIZE);
        for (SolverBenchmark solverBenchmark : plannerBenchmark.getSolverBenchmarkList()) {
            String solverLabel = solverBenchmark.getNameWithFavoriteSuffix();
            for (SingleBenchmark singleBenchmark : solverBenchmark.getSingleBenchmarkList()) {
                String planningProblemLabel = singleBenchmark.getProblemBenchmark().getName();
                if (singleBenchmark.isSuccess()) {
                    double[] levelValues = singleBenchmark.getScore().toDoubleLevels();
                    for (int i = 0; i < levelValues.length && i < CHARTED_SCORE_LEVEL_SIZE; i++) {
                        if (i >= datasetList.size()) {
                            datasetList.add(new DefaultCategoryDataset());
                        }
                        datasetList.get(i).addValue(levelValues[i], solverLabel, planningProblemLabel);
                    }
                }
            }
        }
        bestScoreSummaryChartFileList = new ArrayList<File>(datasetList.size());
        int scoreLevelIndex = 0;
        for (DefaultCategoryDataset dataset : datasetList) {
            CategoryPlot plot = createBarChartPlot(dataset,
                    "Score level " + scoreLevelIndex, NumberFormat.getInstance(locale));
            JFreeChart chart = new JFreeChart("Best score level " + scoreLevelIndex + " summary (higher is better)",
                    JFreeChart.DEFAULT_TITLE_FONT, plot, true);
            bestScoreSummaryChartFileList.add(writeChartToImageFile(chart, "bestScoreSummaryLevel" + scoreLevelIndex));
            scoreLevelIndex++;
        }
    }

    private void writeBestScoreScalabilitySummaryChart() {
        // Each scoreLevel has it's own dataset and chartFile
        List<List<XYSeries>> seriesListList = new ArrayList<List<XYSeries>>(
                CHARTED_SCORE_LEVEL_SIZE);
        int solverBenchmarkIndex = 0;
        for (SolverBenchmark solverBenchmark : plannerBenchmark.getSolverBenchmarkList()) {
            String solverLabel = solverBenchmark.getNameWithFavoriteSuffix();
            for (SingleBenchmark singleBenchmark : solverBenchmark.getSingleBenchmarkList()) {
                if (singleBenchmark.isSuccess()) {
                    long problemScale = singleBenchmark.getProblemBenchmark().getProblemScale();
                    double[] levelValues = singleBenchmark.getScore().toDoubleLevels();
                    for (int i = 0; i < levelValues.length && i < CHARTED_SCORE_LEVEL_SIZE; i++) {
                        if (i >= seriesListList.size()) {
                            seriesListList.add(new ArrayList<XYSeries>(
                                    plannerBenchmark.getSolverBenchmarkList().size()));
                        }
                        List<XYSeries> seriesList = seriesListList.get(i);
                        if (solverBenchmarkIndex >= seriesList.size()) {
                            seriesList.add(new XYSeries(solverLabel));
                        }
                        seriesList.get(solverBenchmarkIndex).add((double) problemScale, levelValues[i]);
                    }
                }
            }
            solverBenchmarkIndex++;
        }
        bestScoreScalabilitySummaryChartFileList = new ArrayList<File>(seriesListList.size());
        int scoreLevelIndex = 0;
        for (List<XYSeries> seriesList : seriesListList) {
            XYPlot plot = createScalabilityPlot(seriesList,
                    "Score level " + scoreLevelIndex, NumberFormat.getInstance(locale));
            JFreeChart chart = new JFreeChart(
                    "Best score scalability level " + scoreLevelIndex + " summary (higher is better)",
                    JFreeChart.DEFAULT_TITLE_FONT, plot, true);
            bestScoreScalabilitySummaryChartFileList.add(
                    writeChartToImageFile(chart, "bestScoreScalabilitySummaryLevel" + scoreLevelIndex));
            scoreLevelIndex++;
        }
    }

    private void writeWinningScoreDifferenceSummaryChart() {
        // Each scoreLevel has it's own dataset and chartFile
        List<DefaultCategoryDataset> datasetList = new ArrayList<DefaultCategoryDataset>(CHARTED_SCORE_LEVEL_SIZE);
        for (SolverBenchmark solverBenchmark : plannerBenchmark.getSolverBenchmarkList()) {
            String solverLabel = solverBenchmark.getNameWithFavoriteSuffix();
            for (SingleBenchmark singleBenchmark : solverBenchmark.getSingleBenchmarkList()) {
                String planningProblemLabel = singleBenchmark.getProblemBenchmark().getName();
                if (singleBenchmark.isSuccess()) {
                    double[] levelValues = singleBenchmark.getWinningScoreDifference().toDoubleLevels();
                    for (int i = 0; i < levelValues.length && i < CHARTED_SCORE_LEVEL_SIZE; i++) {
                        if (i >= datasetList.size()) {
                            datasetList.add(new DefaultCategoryDataset());
                        }
                        datasetList.get(i).addValue(levelValues[i], solverLabel, planningProblemLabel);
                    }
                }
            }
        }
        winningScoreDifferenceSummaryChartFileList = new ArrayList<File>(datasetList.size());
        int scoreLevelIndex = 0;
        for (DefaultCategoryDataset dataset : datasetList) {
            CategoryPlot plot = createBarChartPlot(dataset,
                    "Winning score difference level " + scoreLevelIndex, NumberFormat.getInstance(locale));
            JFreeChart chart = new JFreeChart("Winning score difference level " + scoreLevelIndex
                    + " summary (higher is better)",
                    JFreeChart.DEFAULT_TITLE_FONT, plot, true);
            winningScoreDifferenceSummaryChartFileList.add(
                    writeChartToImageFile(chart, "winningScoreDifferenceSummaryLevel" + scoreLevelIndex));
            scoreLevelIndex++;
        }
    }

    private void writeWorstScoreDifferencePercentageSummaryChart() {
        // Each scoreLevel has it's own dataset and chartFile
        List<DefaultCategoryDataset> datasetList = new ArrayList<DefaultCategoryDataset>(CHARTED_SCORE_LEVEL_SIZE);
        for (SolverBenchmark solverBenchmark : plannerBenchmark.getSolverBenchmarkList()) {
            String solverLabel = solverBenchmark.getNameWithFavoriteSuffix();
            for (SingleBenchmark singleBenchmark : solverBenchmark.getSingleBenchmarkList()) {
                String planningProblemLabel = singleBenchmark.getProblemBenchmark().getName();
                if (singleBenchmark.isSuccess()) {
                    double[] levelValues = singleBenchmark.getWorstScoreDifferencePercentage().getPercentageLevels();
                    for (int i = 0; i < levelValues.length && i < CHARTED_SCORE_LEVEL_SIZE; i++) {
                        if (i >= datasetList.size()) {
                            datasetList.add(new DefaultCategoryDataset());
                        }
                        datasetList.get(i).addValue(levelValues[i], solverLabel, planningProblemLabel);
                    }
                }
            }
        }
        worstScoreDifferencePercentageSummaryChartFileList = new ArrayList<File>(datasetList.size());
        int scoreLevelIndex = 0;
        for (DefaultCategoryDataset dataset : datasetList) {
            CategoryPlot plot = createBarChartPlot(dataset,
                    "Worst score difference percentage level " + scoreLevelIndex,
                    NumberFormat.getPercentInstance(locale));
            JFreeChart chart = new JFreeChart("Worst score difference percentage level " + scoreLevelIndex
                    + " summary (higher is better)",
                    JFreeChart.DEFAULT_TITLE_FONT, plot, true);
            worstScoreDifferencePercentageSummaryChartFileList.add(
                    writeChartToImageFile(chart, "worstScoreDifferencePercentageSummaryLevel" + scoreLevelIndex));
            scoreLevelIndex++;
        }
    }

    private void writeTimeSpendSummaryChart() {
        DefaultCategoryDataset dataset = new DefaultCategoryDataset();
        for (SolverBenchmark solverBenchmark : plannerBenchmark.getSolverBenchmarkList()) {
            String solverLabel = solverBenchmark.getNameWithFavoriteSuffix();
            for (SingleBenchmark singleBenchmark : solverBenchmark.getSingleBenchmarkList()) {
                String planningProblemLabel = singleBenchmark.getProblemBenchmark().getName();
                if (singleBenchmark.isSuccess()) {
                    long timeMillisSpend = singleBenchmark.getTimeMillisSpend();
                    dataset.addValue(timeMillisSpend, solverLabel, planningProblemLabel);
                }
            }
        }
        CategoryPlot plot = createBarChartPlot(dataset, "Time spend", new MillisecondsSpendNumberFormat(locale));
        JFreeChart chart = new JFreeChart("Time spend summary (lower time is better)",
                JFreeChart.DEFAULT_TITLE_FONT, plot, true);
        timeSpendSummaryChartFile = writeChartToImageFile(chart, "timeSpendSummary");
    }

    private void writeTimeSpendScalabilitySummaryChart() {
        List<XYSeries> seriesList = new ArrayList<XYSeries>(plannerBenchmark.getSolverBenchmarkList().size());
        for (SolverBenchmark solverBenchmark : plannerBenchmark.getSolverBenchmarkList()) {
            String solverLabel = solverBenchmark.getNameWithFavoriteSuffix();
            XYSeries series = new XYSeries(solverLabel);
            for (SingleBenchmark singleBenchmark : solverBenchmark.getSingleBenchmarkList()) {
                if (singleBenchmark.isSuccess()) {
                    long problemScale = singleBenchmark.getProblemBenchmark().getProblemScale();
                    long timeMillisSpend = singleBenchmark.getTimeMillisSpend();
                    series.add((Long) problemScale, (Long) timeMillisSpend);
                }
            }
            seriesList.add(series);
        }
        XYPlot plot = createScalabilityPlot(seriesList,
                "Time spend", new MillisecondsSpendNumberFormat(locale));
        JFreeChart chart = new JFreeChart("Time spend scalability summary (lower is better)",
                JFreeChart.DEFAULT_TITLE_FONT, plot, true);
        timeSpendScalabilitySummaryChartFile = writeChartToImageFile(chart, "timeSpendScalabilitySummary");
    }

    private void writeAverageCalculateCountPerSecondSummaryChart() {
        List<XYSeries> seriesList = new ArrayList<XYSeries>(plannerBenchmark.getSolverBenchmarkList().size());
        for (SolverBenchmark solverBenchmark : plannerBenchmark.getSolverBenchmarkList()) {
            String solverLabel = solverBenchmark.getNameWithFavoriteSuffix();
            XYSeries series = new XYSeries(solverLabel);
            for (SingleBenchmark singleBenchmark : solverBenchmark.getSingleBenchmarkList()) {
                if (singleBenchmark.isSuccess()) {
                    long problemScale = singleBenchmark.getProblemBenchmark().getProblemScale();
                    long averageCalculateCountPerSecond = singleBenchmark.getAverageCalculateCountPerSecond();
                    series.add((Long) problemScale, (Long) averageCalculateCountPerSecond);
                }
            }
            seriesList.add(series);
        }
        XYPlot plot = createScalabilityPlot(seriesList,
                "Average calculate count per second", NumberFormat.getInstance(locale));
        JFreeChart chart = new JFreeChart("Average calculate count summary (higher is better)",
                JFreeChart.DEFAULT_TITLE_FONT, plot, true);
        averageCalculateCountSummaryChartFile = writeChartToImageFile(chart, "averageCalculateCountSummary");
    }

    private CategoryPlot createBarChartPlot(DefaultCategoryDataset dataset,
            String yAxisLabel, NumberFormat numberFormat) {
        CategoryAxis xAxis = new CategoryAxis("Data");
        xAxis.setCategoryMargin(0.40);
        NumberAxis yAxis = new NumberAxis(yAxisLabel);
        yAxis.setNumberFormatOverride(numberFormat);
        BarRenderer renderer = createBarChartRenderer(numberFormat);
        CategoryPlot plot = new CategoryPlot(dataset, xAxis, yAxis, renderer);
        plot.setOrientation(PlotOrientation.VERTICAL);
        return plot;
    }

    private BarRenderer createBarChartRenderer(NumberFormat numberFormat) {
        BarRenderer renderer = new BarRenderer();
        ItemLabelPosition positiveItemLabelPosition = new ItemLabelPosition(
                ItemLabelAnchor.OUTSIDE12, TextAnchor.BOTTOM_CENTER);
        renderer.setBasePositiveItemLabelPosition(positiveItemLabelPosition);
        ItemLabelPosition negativeItemLabelPosition = new ItemLabelPosition(
                ItemLabelAnchor.OUTSIDE6, TextAnchor.TOP_CENTER);
        renderer.setBaseNegativeItemLabelPosition(negativeItemLabelPosition);
        renderer.setBaseItemLabelGenerator(new StandardCategoryItemLabelGenerator(
                StandardCategoryItemLabelGenerator.DEFAULT_LABEL_FORMAT_STRING, numberFormat));
        renderer.setBaseItemLabelsVisible(true);
        return renderer;
    }

    private XYPlot createScalabilityPlot(List<XYSeries> seriesList, String yAxisLabel, NumberFormat numberFormat) {
        NumberAxis xAxis = new NumberAxis("Problem scale");
        xAxis.setNumberFormatOverride(NumberFormat.getInstance(locale));
        NumberAxis yAxis = new NumberAxis(yAxisLabel);
        yAxis.setNumberFormatOverride(numberFormat);
        XYPlot plot = new XYPlot(null, xAxis, yAxis, null);
        int seriesIndex = 0;
        for (XYSeries series : seriesList) {
            XYSeriesCollection seriesCollection = new XYSeriesCollection();
            seriesCollection.addSeries(series);
            plot.setDataset(seriesIndex, seriesCollection);
            XYItemRenderer renderer = createScalabilityPlotRenderer(numberFormat);
            plot.setRenderer(seriesIndex, renderer);
            seriesIndex++;
        }
        plot.setOrientation(PlotOrientation.VERTICAL);
        return plot;
    }

    private XYItemRenderer createScalabilityPlotRenderer(NumberFormat numberFormat) {
        XYItemRenderer renderer = new StandardXYItemRenderer(StandardXYItemRenderer.SHAPES_AND_LINES);
        // Use dashed line
        renderer.setSeriesStroke(0, new BasicStroke(
                1.0f, BasicStroke.CAP_ROUND, BasicStroke.JOIN_ROUND, 1.0f, new float[]{2.0f, 6.0f}, 0.0f
        ));
        return renderer;
    }

    private File writeChartToImageFile(JFreeChart chart, String fileNameBase) {
        BufferedImage chartImage = chart.createBufferedImage(1024, 768);
        File summaryChartFile = new File(summaryDirectory, fileNameBase + ".png");
        OutputStream out = null;
        try {
            out = new FileOutputStream(summaryChartFile);
            ImageIO.write(chartImage, "png", out);
        } catch (IOException e) {
            throw new IllegalArgumentException("Problem writing summaryChartFile: " + summaryChartFile, e);
        } finally {
            IOUtils.closeQuietly(out);
        }
        return summaryChartFile;
    }

    private void determineDefaultShownScoreLevelIndex() {
        defaultShownScoreLevelIndex = Integer.MAX_VALUE;
        for (ProblemBenchmark problemBenchmark : plannerBenchmark.getUnifiedProblemBenchmarkList()) {
            if (problemBenchmark.hasAnySuccess()) {
                double[] winningScoreLevels = problemBenchmark.getWinningSingleBenchmark().getScore().toDoubleLevels();
                int[] differenceCount = new int[winningScoreLevels.length];
                for (int i = 0; i < differenceCount.length; i++) {
                    differenceCount[i] = 0;
                }
                for (SingleBenchmark singleBenchmark : problemBenchmark.getSingleBenchmarkList()) {
                    if (singleBenchmark.isSuccess()) {
                        double[] scoreLevels = singleBenchmark.getScore().toDoubleLevels();
                        for (int i = 0; i < scoreLevels.length; i++) {
                            if (scoreLevels[i] != winningScoreLevels[i]) {
                                differenceCount[i] = differenceCount[i] + 1;
                            }
                        }
                    }
                }
                int firstInterestingLevel = differenceCount.length - 1;
                for (int i = 0; i < differenceCount.length; i++) {
                    if (differenceCount[i] > 0) {
                        firstInterestingLevel = i;
                        break;
                    }
                }
                if (defaultShownScoreLevelIndex > firstInterestingLevel) {
                    defaultShownScoreLevelIndex = firstInterestingLevel;
                }
            }
        }
    }

    private void writeHtmlOverviewFile() {
        WebsiteResourceUtils.copyResourcesTo(plannerBenchmark.getBenchmarkReportDirectory());

        htmlOverviewFile = new File(plannerBenchmark.getBenchmarkReportDirectory(), "index.html");
        Configuration freemarkerCfg = new Configuration();
        freemarkerCfg.setDefaultEncoding("UTF-8");
        freemarkerCfg.setLocale(locale);
        freemarkerCfg.setClassForTemplateLoading(BenchmarkReport.class, "");

        String templateFilename = "benchmarkReport.html.ftl";
        Map<String, Object> model = new HashMap<String, Object>();
        model.put("benchmarkReport", this);

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

}
