/*
 * Copyright 2011 Red Hat, Inc. and/or its affiliates.
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
import java.time.ZoneId;
import java.util.ArrayList;
import java.util.Comparator;
import java.util.HashMap;
import java.util.List;
import java.util.Locale;
import java.util.Map;
import java.util.NavigableSet;
import java.util.TreeSet;
import javax.imageio.ImageIO;

import freemarker.template.Configuration;
import freemarker.template.Template;
import freemarker.template.TemplateException;
import org.jfree.chart.JFreeChart;
import org.jfree.chart.axis.CategoryAxis;
import org.jfree.chart.axis.LogarithmicAxis;
import org.jfree.chart.axis.NumberAxis;
import org.jfree.chart.labels.ItemLabelAnchor;
import org.jfree.chart.labels.ItemLabelPosition;
import org.jfree.chart.labels.StandardCategoryItemLabelGenerator;
import org.jfree.chart.plot.CategoryPlot;
import org.jfree.chart.plot.PlotOrientation;
import org.jfree.chart.plot.XYPlot;
import org.jfree.chart.renderer.category.BarRenderer;
import org.jfree.chart.renderer.category.BoxAndWhiskerRenderer;
import org.jfree.chart.renderer.xy.StandardXYItemRenderer;
import org.jfree.chart.renderer.xy.XYItemRenderer;
import org.jfree.chart.ui.TextAnchor;
import org.jfree.data.category.DefaultCategoryDataset;
import org.jfree.data.statistics.DefaultBoxAndWhiskerCategoryDataset;
import org.jfree.data.xy.XYDataItem;
import org.jfree.data.xy.XYSeries;
import org.jfree.data.xy.XYSeriesCollection;
import org.optaplanner.benchmark.impl.ranking.SolverRankingWeightFactory;
import org.optaplanner.benchmark.impl.result.PlannerBenchmarkResult;
import org.optaplanner.benchmark.impl.result.ProblemBenchmarkResult;
import org.optaplanner.benchmark.impl.result.SingleBenchmarkResult;
import org.optaplanner.benchmark.impl.result.SolverBenchmarkResult;
import org.optaplanner.benchmark.impl.result.SubSingleBenchmarkResult;
import org.optaplanner.benchmark.impl.statistic.ProblemStatistic;
import org.optaplanner.benchmark.impl.statistic.PureSubSingleStatistic;
import org.optaplanner.benchmark.impl.statistic.SubSingleStatistic;
import org.optaplanner.benchmark.impl.statistic.common.MillisecondsSpentNumberFormat;
import org.optaplanner.core.config.solver.EnvironmentMode;
import org.optaplanner.core.impl.score.ScoreUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import static java.lang.Double.isFinite;

public class BenchmarkReport {

    protected final transient Logger logger = LoggerFactory.getLogger(getClass());

    public static final int CHARTED_SCORE_LEVEL_SIZE = 15;
    public static final int LOG_SCALE_MIN_DATASETS_COUNT = 5;

    private final PlannerBenchmarkResult plannerBenchmarkResult;

    private Locale locale = null;
    private ZoneId timezoneId = null;
    private Comparator<SolverBenchmarkResult> solverRankingComparator = null;
    private SolverRankingWeightFactory solverRankingWeightFactory = null;
    private File summaryDirectory = null;
    private List<File> bestScoreSummaryChartFileList = null;
    private List<File> bestScoreScalabilitySummaryChartFileList = null;
    private List<File> bestScoreDistributionSummaryChartFileList = null;
    private List<File> winningScoreDifferenceSummaryChartFileList = null;
    private List<File> worstScoreDifferencePercentageSummaryChartFileList = null;
    private File scoreCalculationSpeedSummaryChartFile = null;
    private File worstScoreCalculationSpeedDifferencePercentageSummaryChartFile = null;
    private File timeSpentSummaryChartFile = null;
    private File timeSpentScalabilitySummaryChartFile = null;
    private List<File> bestScorePerTimeSpentSummaryChartFileList = null;

    private Integer defaultShownScoreLevelIndex = null;
    private List<String> warningList = null;

    private File htmlOverviewFile = null;

    public BenchmarkReport(PlannerBenchmarkResult plannerBenchmarkResult) {
        this.plannerBenchmarkResult = plannerBenchmarkResult;
    }

    public PlannerBenchmarkResult getPlannerBenchmarkResult() {
        return plannerBenchmarkResult;
    }

    public Locale getLocale() {
        return locale;
    }

    public void setLocale(Locale locale) {
        this.locale = locale;
    }

    public ZoneId getTimezoneId() {
        return timezoneId;
    }

    public void setTimezoneId(ZoneId timezoneId) {
        this.timezoneId = timezoneId;
    }

    public Comparator<SolverBenchmarkResult> getSolverRankingComparator() {
        return solverRankingComparator;
    }

    public void setSolverRankingComparator(Comparator<SolverBenchmarkResult> solverRankingComparator) {
        this.solverRankingComparator = solverRankingComparator;
    }

    public SolverRankingWeightFactory getSolverRankingWeightFactory() {
        return solverRankingWeightFactory;
    }

    public void setSolverRankingWeightFactory(SolverRankingWeightFactory solverRankingWeightFactory) {
        this.solverRankingWeightFactory = solverRankingWeightFactory;
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

    public List<File> getBestScoreDistributionSummaryChartFileList() {
        return bestScoreDistributionSummaryChartFileList;
    }

    public List<File> getWinningScoreDifferenceSummaryChartFileList() {
        return winningScoreDifferenceSummaryChartFileList;
    }

    public List<File> getWorstScoreDifferencePercentageSummaryChartFileList() {
        return worstScoreDifferencePercentageSummaryChartFileList;
    }

    public File getScoreCalculationSpeedSummaryChartFile() {
        return scoreCalculationSpeedSummaryChartFile;
    }

    public File getWorstScoreCalculationSpeedDifferencePercentageSummaryChartFile() {
        return worstScoreCalculationSpeedDifferencePercentageSummaryChartFile;
    }

    public File getTimeSpentSummaryChartFile() {
        return timeSpentSummaryChartFile;
    }

    public File getTimeSpentScalabilitySummaryChartFile() {
        return timeSpentScalabilitySummaryChartFile;
    }

    public List<File> getBestScorePerTimeSpentSummaryChartFileList() {
        return bestScorePerTimeSpentSummaryChartFileList;
    }

    public Integer getDefaultShownScoreLevelIndex() {
        return defaultShownScoreLevelIndex;
    }

    public List<String> getWarningList() {
        return warningList;
    }

    public File getHtmlOverviewFile() {
        return htmlOverviewFile;
    }

    // ************************************************************************
    // Smart getters
    // ************************************************************************

    public String getRelativePathToBenchmarkReportDirectory(File file) {
        String benchmarkReportDirectoryPath = plannerBenchmarkResult.getBenchmarkReportDirectory().getAbsoluteFile().toURI().getPath();
        String filePath = file.getAbsoluteFile().toURI().getPath();
        if (!filePath.startsWith(benchmarkReportDirectoryPath)) {
            throw new IllegalArgumentException("The filePath (" + filePath
                    + ") does not start with the benchmarkReportDirectoryPath (" + benchmarkReportDirectoryPath + ").");
        }
        String relativePath = filePath.substring(benchmarkReportDirectoryPath.length());
        if (relativePath.startsWith("/")) {
            relativePath = relativePath.substring(1);
        }
        return relativePath;
    }

    public String getSolverRankingClassSimpleName() {
        Class solverRankingClass = getSolverRankingClass();
        return solverRankingClass == null ? null : solverRankingClass.getSimpleName();
    }

    public String getSolverRankingClassFullName() {
        Class solverRankingClass = getSolverRankingClass();
        return solverRankingClass == null ? null : solverRankingClass.getName();
    }

    // ************************************************************************
    // Write methods
    // ************************************************************************

    public void writeReport() {
        logger.info("Generating benchmark report...");
        summaryDirectory = new File(plannerBenchmarkResult.getBenchmarkReportDirectory(), "summary");
        summaryDirectory.mkdir();
        plannerBenchmarkResult.accumulateResults(this);
        fillWarningList();
        writeBestScoreSummaryChart();
        writeBestScoreScalabilitySummaryChart();
        writeWinningScoreDifferenceSummaryChart();
        writeWorstScoreDifferencePercentageSummaryChart();
        writeBestScoreDistributionSummaryChart();
        writeScoreCalculationSpeedSummaryChart();
        writeWorstScoreCalculationSpeedDifferencePercentageSummaryChart();
        writeTimeSpentSummaryChart();
        writeTimeSpentScalabilitySummaryChart();
        writeBestScorePerTimeSpentSummaryChart();
        for (ProblemBenchmarkResult<Object> problemBenchmarkResult : plannerBenchmarkResult.getUnifiedProblemBenchmarkResultList()) {
            for (SingleBenchmarkResult singleBenchmarkResult : problemBenchmarkResult.getSingleBenchmarkResultList()) {
                for (SubSingleBenchmarkResult subSingleBenchmarkResult : singleBenchmarkResult.getSubSingleBenchmarkResultList()) {
                    if (!subSingleBenchmarkResult.hasAllSuccess()) {
                        continue;
                    }
                    for (SubSingleStatistic subSingleStatistic : subSingleBenchmarkResult.getEffectiveSubSingleStatisticMap().values()) {
                        try {
                            subSingleStatistic.unhibernatePointList();
                        } catch (IllegalStateException e) {
                            if (!plannerBenchmarkResult.getAggregation()) {
                                throw new IllegalStateException("Failed to unhibernate point list of SubSingleStatistic ("
                                        + subSingleStatistic + ") of SubSingleBenchmark (" + subSingleBenchmarkResult + ").", e);
                            }
                            logger.trace("This is expected, aggregator doesn't copy CSV files. Could not read CSV file "
                                    + "({}) of sub single statistic ({}).", subSingleStatistic.getCsvFile().getAbsolutePath(), subSingleStatistic);
                        }
                    }
                }
            }
        }
        for (ProblemBenchmarkResult<Object> problemBenchmarkResult : plannerBenchmarkResult.getUnifiedProblemBenchmarkResultList()) {
            if (problemBenchmarkResult.hasAnySuccess()) {
                for (ProblemStatistic problemStatistic : problemBenchmarkResult.getProblemStatisticList()) {
                    problemStatistic.writeGraphFiles(this);
                }
                for (SingleBenchmarkResult singleBenchmarkResult : problemBenchmarkResult.getSingleBenchmarkResultList()) {
                    if (singleBenchmarkResult.hasAllSuccess()) {
                        for (PureSubSingleStatistic pureSubSingleStatistic : singleBenchmarkResult.getMedian().getPureSubSingleStatisticList()) {
                            pureSubSingleStatistic.writeGraphFiles(this);
                        }
                    }
                }
            }
        }
        for (ProblemBenchmarkResult<Object> problemBenchmarkResult : plannerBenchmarkResult.getUnifiedProblemBenchmarkResultList()) {
            for (SingleBenchmarkResult singleBenchmarkResult : problemBenchmarkResult.getSingleBenchmarkResultList()) {
                for (SubSingleBenchmarkResult subSingleBenchmarkResult : singleBenchmarkResult.getSubSingleBenchmarkResultList()) {
                    if (!subSingleBenchmarkResult.hasAllSuccess()) {
                        continue;
                    }
                    for (SubSingleStatistic subSingleStatistic : subSingleBenchmarkResult.getEffectiveSubSingleStatisticMap().values()) {
                        if (plannerBenchmarkResult.getAggregation()) {
                            subSingleStatistic.setPointList(null);
                        } else {
                            subSingleStatistic.hibernatePointList();
                        }
                    }
                }
            }
        }
        determineDefaultShownScoreLevelIndex();
        writeHtmlOverviewFile();
    }

    protected void fillWarningList() {
        warningList = new ArrayList<>();
        String javaVmName = System.getProperty("java.vm.name");
        if (javaVmName != null && javaVmName.contains("Client VM")) {
            warningList.add("The Java VM (" + javaVmName + ") is the Client VM."
                    + " This decreases performance."
                    + " Maybe start the java process with the argument \"-server\" to get better results.");
        }
        Integer parallelBenchmarkCount = plannerBenchmarkResult.getParallelBenchmarkCount();
        Integer availableProcessors = plannerBenchmarkResult.getAvailableProcessors();
        if (parallelBenchmarkCount != null && availableProcessors != null
                && parallelBenchmarkCount > availableProcessors) {
            warningList.add("The parallelBenchmarkCount (" + parallelBenchmarkCount
                    + ") is higher than the number of availableProcessors (" + availableProcessors + ")."
                    + " This decreases performance."
                    + " Maybe reduce the parallelBenchmarkCount.");
        }
        EnvironmentMode environmentMode = plannerBenchmarkResult.getEnvironmentMode();
        if (environmentMode != null && environmentMode.isAsserted()) {
            warningList.add("The environmentMode (" + environmentMode + ") is asserting."
                    + " This decreases performance."
                    + " Maybe set the environmentMode to " + EnvironmentMode.REPRODUCIBLE + ".");
        }
        String loggingLevelOptaPlannerCore = plannerBenchmarkResult.getLoggingLevelOptaPlannerCore();
        if (loggingLevelOptaPlannerCore != null && loggingLevelOptaPlannerCore.equals("trace")) {
            warningList.add("The loggingLevel (" + loggingLevelOptaPlannerCore + ") of org.optaplanner.core is high."
                    + " This decreases performance."
                    + " Maybe set the loggingLevel to debug or lower.");
        }
        String loggingLevelDroolsCore = plannerBenchmarkResult.getLoggingLevelDroolsCore();
        if (loggingLevelDroolsCore != null
                && (loggingLevelDroolsCore.equals("trace") || loggingLevelDroolsCore.equals("debug"))) {
            warningList.add("The loggingLevel (" + loggingLevelDroolsCore + ") of org.drools.core is high."
                    + " This decreases performance."
                    + " Maybe set the loggingLevel to info or lower.");
        }
    }

    private void writeBestScoreSummaryChart() {
        // Each scoreLevel has its own dataset and chartFile
        List<DefaultCategoryDataset> datasetList = new ArrayList<>(CHARTED_SCORE_LEVEL_SIZE);
        for (SolverBenchmarkResult solverBenchmarkResult : plannerBenchmarkResult.getSolverBenchmarkResultList()) {
            String solverLabel = solverBenchmarkResult.getNameWithFavoriteSuffix();
            for (SingleBenchmarkResult singleBenchmarkResult : solverBenchmarkResult.getSingleBenchmarkResultList()) {
                String problemLabel = singleBenchmarkResult.getProblemBenchmarkResult().getName();
                if (singleBenchmarkResult.hasAllSuccess()) {
                    double[] levelValues = ScoreUtils.extractLevelDoubles(singleBenchmarkResult.getAverageScore());
                    for (int i = 0; i < levelValues.length && i < CHARTED_SCORE_LEVEL_SIZE; i++) {
                        if (i >= datasetList.size()) {
                            datasetList.add(new DefaultCategoryDataset());
                        }
                        if (isFinite(levelValues[i])) {
                            datasetList.get(i).addValue(levelValues[i], solverLabel, problemLabel);
                        }
                    }
                }
            }
        }
        bestScoreSummaryChartFileList = new ArrayList<>(datasetList.size());
        int scoreLevelIndex = 0;
        for (DefaultCategoryDataset dataset : datasetList) {
            String scoreLevelLabel = plannerBenchmarkResult.findScoreLevelLabel(scoreLevelIndex);
            CategoryPlot plot = createBarChartPlot(dataset,
                    "Best " + scoreLevelLabel, NumberFormat.getInstance(locale));
            JFreeChart chart = new JFreeChart("Best " + scoreLevelLabel + " summary (higher is better)",
                    JFreeChart.DEFAULT_TITLE_FONT, plot, true);
            bestScoreSummaryChartFileList.add(writeChartToImageFile(chart, "bestScoreSummaryLevel" + scoreLevelIndex));
            scoreLevelIndex++;
        }
    }

    private void writeBestScoreScalabilitySummaryChart() {
        // Each scoreLevel has its own dataset and chartFile
        List<List<XYSeries>> seriesListList = new ArrayList<>(
                CHARTED_SCORE_LEVEL_SIZE);
        int solverBenchmarkIndex = 0;
        for (SolverBenchmarkResult solverBenchmarkResult : plannerBenchmarkResult.getSolverBenchmarkResultList()) {
            String solverLabel = solverBenchmarkResult.getNameWithFavoriteSuffix();
            for (SingleBenchmarkResult singleBenchmarkResult : solverBenchmarkResult.getSingleBenchmarkResultList()) {
                if (singleBenchmarkResult.hasAllSuccess()) {
                    long problemScale = singleBenchmarkResult.getProblemBenchmarkResult().getProblemScale();
                    double[] levelValues = ScoreUtils.extractLevelDoubles(singleBenchmarkResult.getAverageScore());
                    for (int i = 0; i < levelValues.length && i < CHARTED_SCORE_LEVEL_SIZE; i++) {
                        if (i >= seriesListList.size()) {
                            seriesListList.add(new ArrayList<>(
                                    plannerBenchmarkResult.getSolverBenchmarkResultList().size()));
                        }
                        List<XYSeries> seriesList = seriesListList.get(i);
                        while (solverBenchmarkIndex >= seriesList.size()) {
                            seriesList.add(new XYSeries(solverLabel));
                        }
                        seriesList.get(solverBenchmarkIndex).add((double) problemScale, levelValues[i]);
                    }
                }
            }
            solverBenchmarkIndex++;
        }
        bestScoreScalabilitySummaryChartFileList = new ArrayList<>(seriesListList.size());
        int scoreLevelIndex = 0;
        for (List<XYSeries> seriesList : seriesListList) {
            String scoreLevelLabel = plannerBenchmarkResult.findScoreLevelLabel(scoreLevelIndex);
            XYPlot plot = createScalabilityPlot(seriesList,
                    "Problem scale", NumberFormat.getInstance(locale),
                    "Best " + scoreLevelLabel, NumberFormat.getInstance(locale));
            JFreeChart chart = new JFreeChart(
                    "Best " + scoreLevelLabel + " scalability summary (higher is better)",
                    JFreeChart.DEFAULT_TITLE_FONT, plot, true);
            bestScoreScalabilitySummaryChartFileList.add(
                    writeChartToImageFile(chart, "bestScoreScalabilitySummaryLevel" + scoreLevelIndex));
            scoreLevelIndex++;
        }
    }

    private void writeBestScoreDistributionSummaryChart() {
        // Each scoreLevel has its own dataset and chartFile
        List<DefaultBoxAndWhiskerCategoryDataset> datasetList = new ArrayList<>(CHARTED_SCORE_LEVEL_SIZE);
        for (SolverBenchmarkResult solverBenchmarkResult : plannerBenchmarkResult.getSolverBenchmarkResultList()) {
            String solverLabel = solverBenchmarkResult.getNameWithFavoriteSuffix();
            for (SingleBenchmarkResult singleBenchmarkResult : solverBenchmarkResult.getSingleBenchmarkResultList()) {
                String problemLabel = singleBenchmarkResult.getProblemBenchmarkResult().getName();
                if (singleBenchmarkResult.hasAllSuccess()) {
                    List<List<Double>> distributionLevelList = new ArrayList<>(CHARTED_SCORE_LEVEL_SIZE);
                    for (SubSingleBenchmarkResult subSingleBenchmarkResult : singleBenchmarkResult.getSubSingleBenchmarkResultList()) {
                        double[] levelValues = ScoreUtils.extractLevelDoubles(subSingleBenchmarkResult.getAverageScore());
                        for (int i = 0; i < levelValues.length && i < CHARTED_SCORE_LEVEL_SIZE; i++) {
                            if (i >= distributionLevelList.size()) {
                                distributionLevelList.add(new ArrayList<>(singleBenchmarkResult.getSubSingleCount()));
                            }
                            distributionLevelList.get(i).add(levelValues[i]);
                        }
                    }
                    for (int i = 0; i < distributionLevelList.size() && i < CHARTED_SCORE_LEVEL_SIZE; i++) {
                        if (i >= datasetList.size()) {
                            datasetList.add(new DefaultBoxAndWhiskerCategoryDataset());
                        }
                        datasetList.get(i).add(
                                distributionLevelList.get(i),
                                solverLabel,
                                problemLabel);
                    }
                }
            }
        }
        bestScoreDistributionSummaryChartFileList = new ArrayList<>(datasetList.size());
        int scoreLevelIndex = 0;
        for (DefaultBoxAndWhiskerCategoryDataset dataset : datasetList) {
            String scoreLevelLabel = plannerBenchmarkResult.findScoreLevelLabel(scoreLevelIndex);
            CategoryPlot plot = createBoxAndWhiskerChartPlot(dataset,
                    "Best " + scoreLevelLabel, NumberFormat.getInstance(locale));
            JFreeChart chart = new JFreeChart("Best " + scoreLevelLabel + " distribution summary (higher is better)",
                    JFreeChart.DEFAULT_TITLE_FONT, plot, true);
            bestScoreDistributionSummaryChartFileList.add(writeChartToImageFile(chart, "bestScoreDistributionSummaryLevel" + scoreLevelIndex));
            scoreLevelIndex++;
        }
    }

    private void writeWinningScoreDifferenceSummaryChart() {
        // Each scoreLevel has its own dataset and chartFile
        List<DefaultCategoryDataset> datasetList = new ArrayList<>(CHARTED_SCORE_LEVEL_SIZE);
        for (SolverBenchmarkResult solverBenchmarkResult : plannerBenchmarkResult.getSolverBenchmarkResultList()) {
            String solverLabel = solverBenchmarkResult.getNameWithFavoriteSuffix();
            for (SingleBenchmarkResult singleBenchmarkResult : solverBenchmarkResult.getSingleBenchmarkResultList()) {
                String problemLabel = singleBenchmarkResult.getProblemBenchmarkResult().getName();
                if (singleBenchmarkResult.hasAllSuccess()) {
                    double[] levelValues = ScoreUtils.extractLevelDoubles(singleBenchmarkResult.getWinningScoreDifference());
                    for (int i = 0; i < levelValues.length && i < CHARTED_SCORE_LEVEL_SIZE; i++) {
                        if (i >= datasetList.size()) {
                            datasetList.add(new DefaultCategoryDataset());
                        }
                        if (isFinite(levelValues[i])) {
                            datasetList.get(i).addValue(levelValues[i], solverLabel, problemLabel);
                        }
                    }
                }
            }
        }
        winningScoreDifferenceSummaryChartFileList = new ArrayList<>(datasetList.size());
        int scoreLevelIndex = 0;
        for (DefaultCategoryDataset dataset : datasetList) {
            String scoreLevelLabel = plannerBenchmarkResult.findScoreLevelLabel(scoreLevelIndex);
            CategoryPlot plot = createBarChartPlot(dataset,
                    "Winning " + scoreLevelLabel + " difference", NumberFormat.getInstance(locale));
            JFreeChart chart = new JFreeChart("Winning " + scoreLevelLabel + " difference summary (higher is better)",
                    JFreeChart.DEFAULT_TITLE_FONT, plot, true);
            winningScoreDifferenceSummaryChartFileList.add(
                    writeChartToImageFile(chart, "winningScoreDifferenceSummaryLevel" + scoreLevelIndex));
            scoreLevelIndex++;
        }
    }

    private void writeWorstScoreDifferencePercentageSummaryChart() {
        // Each scoreLevel has its own dataset and chartFile
        List<DefaultCategoryDataset> datasetList = new ArrayList<>(CHARTED_SCORE_LEVEL_SIZE);
        for (SolverBenchmarkResult solverBenchmarkResult : plannerBenchmarkResult.getSolverBenchmarkResultList()) {
            String solverLabel = solverBenchmarkResult.getNameWithFavoriteSuffix();
            for (SingleBenchmarkResult singleBenchmarkResult : solverBenchmarkResult.getSingleBenchmarkResultList()) {
                String problemLabel = singleBenchmarkResult.getProblemBenchmarkResult().getName();
                if (singleBenchmarkResult.hasAllSuccess()) {
                    double[] levelValues = singleBenchmarkResult.getWorstScoreDifferencePercentage().getPercentageLevels();
                    for (int i = 0; i < levelValues.length && i < CHARTED_SCORE_LEVEL_SIZE; i++) {
                        if (i >= datasetList.size()) {
                            datasetList.add(new DefaultCategoryDataset());
                        }
                        if (isFinite(levelValues[i])) {
                            datasetList.get(i).addValue(levelValues[i], solverLabel, problemLabel);
                        }
                    }
                }
            }
        }
        worstScoreDifferencePercentageSummaryChartFileList = new ArrayList<>(datasetList.size());
        int scoreLevelIndex = 0;
        for (DefaultCategoryDataset dataset : datasetList) {
            String scoreLevelLabel = plannerBenchmarkResult.findScoreLevelLabel(scoreLevelIndex);
            CategoryPlot plot = createBarChartPlot(dataset,
                    "Worst " + scoreLevelLabel + " difference percentage",
                    NumberFormat.getPercentInstance(locale));
            JFreeChart chart = new JFreeChart("Worst " + scoreLevelLabel + " difference percentage"
                    + " summary (higher is better)",
                    JFreeChart.DEFAULT_TITLE_FONT, plot, true);
            worstScoreDifferencePercentageSummaryChartFileList.add(
                    writeChartToImageFile(chart, "worstScoreDifferencePercentageSummaryLevel" + scoreLevelIndex));
            scoreLevelIndex++;
        }
    }

    private void writeScoreCalculationSpeedSummaryChart() {
        List<XYSeries> seriesList = new ArrayList<>(plannerBenchmarkResult.getSolverBenchmarkResultList().size());
        for (SolverBenchmarkResult solverBenchmarkResult : plannerBenchmarkResult.getSolverBenchmarkResultList()) {
            String solverLabel = solverBenchmarkResult.getNameWithFavoriteSuffix();
            XYSeries series = new XYSeries(solverLabel);
            for (SingleBenchmarkResult singleBenchmarkResult : solverBenchmarkResult.getSingleBenchmarkResultList()) {
                if (singleBenchmarkResult.hasAllSuccess()) {
                    long problemScale = singleBenchmarkResult.getProblemBenchmarkResult().getProblemScale();
                    long scoreCalculationSpeed = singleBenchmarkResult.getScoreCalculationSpeed();
                    series.add((Long) problemScale, (Long) scoreCalculationSpeed);
                }
            }
            seriesList.add(series);
        }
        XYPlot plot = createScalabilityPlot(seriesList,
                "Problem scale", NumberFormat.getInstance(locale),
                "Score calculation speed per second", NumberFormat.getInstance(locale));
        JFreeChart chart = new JFreeChart("Score calculation speed summary (higher is better)",
                JFreeChart.DEFAULT_TITLE_FONT, plot, true);
        scoreCalculationSpeedSummaryChartFile = writeChartToImageFile(chart, "scoreCalculationSpeedSummary");
    }

    private void writeWorstScoreCalculationSpeedDifferencePercentageSummaryChart() {
        DefaultCategoryDataset dataset = new DefaultCategoryDataset();
        for (SolverBenchmarkResult solverBenchmarkResult : plannerBenchmarkResult.getSolverBenchmarkResultList()) {
            String solverLabel = solverBenchmarkResult.getNameWithFavoriteSuffix();
            for (SingleBenchmarkResult singleBenchmarkResult : solverBenchmarkResult.getSingleBenchmarkResultList()) {
                String problemLabel = singleBenchmarkResult.getProblemBenchmarkResult().getName();
                if (singleBenchmarkResult.hasAllSuccess()) {
                    double worstScoreCalculationSpeedDifferencePercentage
                            = singleBenchmarkResult.getWorstScoreCalculationSpeedDifferencePercentage();
                    if (isFinite(worstScoreCalculationSpeedDifferencePercentage)) {
                        dataset.addValue(worstScoreCalculationSpeedDifferencePercentage, solverLabel, problemLabel);
                    }
                }
            }
        }
        CategoryPlot plot = createBarChartPlot(dataset,
                "Worst score calculation speed difference percentage",
                NumberFormat.getPercentInstance(locale));
        JFreeChart chart = new JFreeChart("Worst score calculation speed difference percentage"
                + " summary (higher is better)",
                JFreeChart.DEFAULT_TITLE_FONT, plot, true);
        worstScoreCalculationSpeedDifferencePercentageSummaryChartFile = writeChartToImageFile(chart, "worstScoreCalculationSpeedDifferencePercentageSummary");
    }

    private void writeTimeSpentSummaryChart() {
        DefaultCategoryDataset dataset = new DefaultCategoryDataset();
        for (SolverBenchmarkResult solverBenchmarkResult : plannerBenchmarkResult.getSolverBenchmarkResultList()) {
            String solverLabel = solverBenchmarkResult.getNameWithFavoriteSuffix();
            for (SingleBenchmarkResult singleBenchmarkResult : solverBenchmarkResult.getSingleBenchmarkResultList()) {
                String problemLabel = singleBenchmarkResult.getProblemBenchmarkResult().getName();
                if (singleBenchmarkResult.hasAllSuccess()) {
                    long timeMillisSpent = singleBenchmarkResult.getTimeMillisSpent();
                    dataset.addValue(timeMillisSpent, solverLabel, problemLabel);
                }
            }
        }
        CategoryPlot plot = createBarChartPlot(dataset, "Time spent", new MillisecondsSpentNumberFormat(locale));
        JFreeChart chart = new JFreeChart("Time spent summary (lower time is better)",
                JFreeChart.DEFAULT_TITLE_FONT, plot, true);
        timeSpentSummaryChartFile = writeChartToImageFile(chart, "timeSpentSummary");
    }

    private void writeTimeSpentScalabilitySummaryChart() {
        List<XYSeries> seriesList = new ArrayList<>(plannerBenchmarkResult.getSolverBenchmarkResultList().size());
        for (SolverBenchmarkResult solverBenchmarkResult : plannerBenchmarkResult.getSolverBenchmarkResultList()) {
            String solverLabel = solverBenchmarkResult.getNameWithFavoriteSuffix();
            XYSeries series = new XYSeries(solverLabel);
            for (SingleBenchmarkResult singleBenchmarkResult : solverBenchmarkResult.getSingleBenchmarkResultList()) {
                if (singleBenchmarkResult.hasAllSuccess()) {
                    long problemScale = singleBenchmarkResult.getProblemBenchmarkResult().getProblemScale();
                    long timeMillisSpent = singleBenchmarkResult.getTimeMillisSpent();
                    series.add((Long) problemScale, (Long) timeMillisSpent);
                }
            }
            seriesList.add(series);
        }
        XYPlot plot = createScalabilityPlot(seriesList,
                "Problem scale", NumberFormat.getInstance(locale),
                "Time spent", new MillisecondsSpentNumberFormat(locale));
        JFreeChart chart = new JFreeChart("Time spent scalability summary (lower is better)",
                JFreeChart.DEFAULT_TITLE_FONT, plot, true);
        timeSpentScalabilitySummaryChartFile = writeChartToImageFile(chart, "timeSpentScalabilitySummary");
    }

    private void writeBestScorePerTimeSpentSummaryChart() {
        // Each scoreLevel has its own dataset and chartFile
        List<List<XYSeries>> seriesListList = new ArrayList<>(
                CHARTED_SCORE_LEVEL_SIZE);
        int solverBenchmarkIndex = 0;
        for (SolverBenchmarkResult solverBenchmarkResult : plannerBenchmarkResult.getSolverBenchmarkResultList()) {
            String solverLabel = solverBenchmarkResult.getNameWithFavoriteSuffix();
            for (SingleBenchmarkResult singleBenchmarkResult : solverBenchmarkResult.getSingleBenchmarkResultList()) {
                if (singleBenchmarkResult.hasAllSuccess()) {
                    long timeMillisSpent = singleBenchmarkResult.getTimeMillisSpent();
                    double[] levelValues = ScoreUtils.extractLevelDoubles(singleBenchmarkResult.getAverageScore());
                    for (int i = 0; i < levelValues.length && i < CHARTED_SCORE_LEVEL_SIZE; i++) {
                        if (i >= seriesListList.size()) {
                            seriesListList.add(new ArrayList<>(
                                    plannerBenchmarkResult.getSolverBenchmarkResultList().size()));
                        }
                        List<XYSeries> seriesList = seriesListList.get(i);
                        while (solverBenchmarkIndex >= seriesList.size()) {
                            seriesList.add(new XYSeries(solverLabel));
                        }
                        seriesList.get(solverBenchmarkIndex).add((Long) timeMillisSpent, (Double) levelValues[i]);
                    }
                }
            }
            solverBenchmarkIndex++;
        }
        bestScorePerTimeSpentSummaryChartFileList = new ArrayList<>(seriesListList.size());
        int scoreLevelIndex = 0;
        for (List<XYSeries> seriesList : seriesListList) {
            String scoreLevelLabel = plannerBenchmarkResult.findScoreLevelLabel(scoreLevelIndex);
            XYPlot plot = createScalabilityPlot(seriesList,
                    "Time spent", new MillisecondsSpentNumberFormat(locale),
                    "Best " + scoreLevelLabel, NumberFormat.getInstance(locale));
            JFreeChart chart = new JFreeChart(
                    "Best " + scoreLevelLabel + " per time spent summary (higher left is better)",
                    JFreeChart.DEFAULT_TITLE_FONT, plot, true);
            bestScorePerTimeSpentSummaryChartFileList.add(
                    writeChartToImageFile(chart, "bestScorePerTimeSpentSummaryLevel" + scoreLevelIndex));
            scoreLevelIndex++;
        }
    }

    // ************************************************************************
    // Chart helper methods
    // ************************************************************************

    private CategoryPlot createBarChartPlot(DefaultCategoryDataset dataset,
            String yAxisLabel, NumberFormat yAxisNumberFormat) {
        CategoryAxis xAxis = new CategoryAxis("Data");
        xAxis.setCategoryMargin(0.40);
        NumberAxis yAxis = new NumberAxis(yAxisLabel);
        yAxis.setNumberFormatOverride(yAxisNumberFormat);
        BarRenderer renderer = createBarChartRenderer(yAxisNumberFormat);
        CategoryPlot plot = new CategoryPlot(dataset, xAxis, yAxis, renderer);
        plot.setOrientation(PlotOrientation.VERTICAL);
        return plot;
    }

    private BarRenderer createBarChartRenderer(NumberFormat numberFormat) {
        BarRenderer renderer = new BarRenderer();
        ItemLabelPosition positiveItemLabelPosition = new ItemLabelPosition(
                ItemLabelAnchor.OUTSIDE12, TextAnchor.BOTTOM_CENTER);
        renderer.setDefaultPositiveItemLabelPosition(positiveItemLabelPosition);
        ItemLabelPosition negativeItemLabelPosition = new ItemLabelPosition(
                ItemLabelAnchor.OUTSIDE6, TextAnchor.TOP_CENTER);
        renderer.setDefaultNegativeItemLabelPosition(negativeItemLabelPosition);
        renderer.setDefaultItemLabelGenerator(new StandardCategoryItemLabelGenerator(
                StandardCategoryItemLabelGenerator.DEFAULT_LABEL_FORMAT_STRING, numberFormat));
        renderer.setDefaultItemLabelsVisible(true);
        return renderer;
    }

    private XYPlot createScalabilityPlot(List<XYSeries> seriesList,
            String xAxisLabel, NumberFormat xAxisNumberFormat,
            String yAxisLabel, NumberFormat yAxisNumberFormat) {
        NumberAxis xAxis;
        if (useLogarithmicProblemScale(seriesList)) {
            LogarithmicAxis logarithmicAxis = new LogarithmicAxis(xAxisLabel + " (logarithmic)");
            logarithmicAxis.setAllowNegativesFlag(true);
            xAxis = logarithmicAxis;
        } else {
            xAxis = new NumberAxis(xAxisLabel);
        }
        xAxis.setNumberFormatOverride(xAxisNumberFormat);
        NumberAxis yAxis = new NumberAxis(yAxisLabel);
        yAxis.setNumberFormatOverride(yAxisNumberFormat);
        XYPlot plot = new XYPlot(null, xAxis, yAxis, null);
        int seriesIndex = 0;
        for (XYSeries series : seriesList) {
            XYSeriesCollection seriesCollection = new XYSeriesCollection();
            seriesCollection.addSeries(series);
            plot.setDataset(seriesIndex, seriesCollection);
            XYItemRenderer renderer = createScalabilityPlotRenderer(yAxisNumberFormat);
            plot.setRenderer(seriesIndex, renderer);
            seriesIndex++;
        }
        plot.setOrientation(PlotOrientation.VERTICAL);
        return plot;
    }

    protected boolean useLogarithmicProblemScale(List<XYSeries> seriesList) {
        NavigableSet<Double> xValueSet = new TreeSet<>();
        int xValueListSize = 0;
        for (XYSeries series : seriesList) {
            for (XYDataItem dataItem : (List<XYDataItem>) series.getItems()) {
                xValueSet.add(dataItem.getXValue());
                xValueListSize++;
            }
        }
        if (xValueListSize < LOG_SCALE_MIN_DATASETS_COUNT) {
            return false;
        }
        // If 60% of the points are in 20% of the value space, use a logarithmic scale
        double threshold = 0.2 * (xValueSet.last() - xValueSet.first());
        int belowThresholdCount = xValueSet.headSet(threshold).size();
        return belowThresholdCount >= (0.6 * xValueSet.size());
    }

    private XYItemRenderer createScalabilityPlotRenderer(NumberFormat numberFormat) {
        XYItemRenderer renderer = new StandardXYItemRenderer(StandardXYItemRenderer.SHAPES_AND_LINES);
        // Use dashed line
        renderer.setSeriesStroke(0, new BasicStroke(
                1.0f, BasicStroke.CAP_ROUND, BasicStroke.JOIN_ROUND, 1.0f, new float[] {2.0f, 6.0f}, 0.0f
        ));
        return renderer;
    }

    private CategoryPlot createBoxAndWhiskerChartPlot(DefaultBoxAndWhiskerCategoryDataset dataset,
            String yAxisLabel, NumberFormat yAxisNumberFormat) {
        CategoryAxis xAxis = new CategoryAxis("Data");
        NumberAxis yAxis = new NumberAxis(yAxisLabel);
        yAxis.setNumberFormatOverride(yAxisNumberFormat);
        BoxAndWhiskerRenderer renderer = new BoxAndWhiskerRenderer();
        renderer.setItemMargin(0.10);
        renderer.setMeanVisible(false);
        // Improve readability by avoiding low contrast with light colors
        renderer.setUseOutlinePaintForWhiskers(true);
        CategoryPlot plot = new CategoryPlot(dataset, xAxis, yAxis, renderer);
        plot.setOrientation(PlotOrientation.VERTICAL);
        return plot;
    }

    private File writeChartToImageFile(JFreeChart chart, String fileNameBase) {
        BufferedImage chartImage = chart.createBufferedImage(1024, 768);
        File summaryChartFile = new File(summaryDirectory, fileNameBase + ".png");
        try (OutputStream out = new FileOutputStream(summaryChartFile)) {
            ImageIO.write(chartImage, "png", out);
        } catch (IOException e) {
            throw new IllegalArgumentException("Failed writing summaryChartFile (" + summaryChartFile + ").", e);
        }
        return summaryChartFile;
    }

    private void determineDefaultShownScoreLevelIndex() {
        defaultShownScoreLevelIndex = Integer.MAX_VALUE;
        for (ProblemBenchmarkResult<Object> problemBenchmarkResult : plannerBenchmarkResult.getUnifiedProblemBenchmarkResultList()) {
            if (problemBenchmarkResult.hasAnySuccess()) {
                double[] winningScoreLevels = ScoreUtils.extractLevelDoubles(
                        problemBenchmarkResult.getWinningSingleBenchmarkResult().getAverageScore());
                int[] differenceCount = new int[winningScoreLevels.length];
                for (int i = 0; i < differenceCount.length; i++) {
                    differenceCount[i] = 0;
                }
                for (SingleBenchmarkResult singleBenchmarkResult : problemBenchmarkResult.getSingleBenchmarkResultList()) {
                    if (singleBenchmarkResult.hasAllSuccess()) {
                        double[] scoreLevels = ScoreUtils.extractLevelDoubles(singleBenchmarkResult.getAverageScore());
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
        File benchmarkReportDirectory = plannerBenchmarkResult.getBenchmarkReportDirectory();
        WebsiteResourceUtils.copyResourcesTo(benchmarkReportDirectory);

        htmlOverviewFile = new File(benchmarkReportDirectory, "index.html");
        Configuration freemarkerCfg = new Configuration(Configuration.DEFAULT_INCOMPATIBLE_IMPROVEMENTS);
        freemarkerCfg.setDefaultEncoding("UTF-8");
        freemarkerCfg.setLocale(locale);
        freemarkerCfg.setClassForTemplateLoading(BenchmarkReport.class, "");

        String templateFilename = "benchmarkReport.html.ftl";
        Map<String, Object> model = new HashMap<>();
        model.put("benchmarkReport", this);
        model.put("reportHelper", new ReportHelper());

        try (Writer writer = new OutputStreamWriter(new FileOutputStream(htmlOverviewFile), "UTF-8")) {
            Template template = freemarkerCfg.getTemplate(templateFilename);
            template.process(model, writer);
        } catch (IOException e) {
            throw new IllegalArgumentException("Can not read templateFilename (" + templateFilename
                    + ") or write htmlOverviewFile (" + htmlOverviewFile + ").", e);
        } catch (TemplateException e) {
            throw new IllegalArgumentException("Can not process Freemarker templateFilename (" + templateFilename
                    + ") to htmlOverviewFile (" + htmlOverviewFile + ").", e);
        }
    }

    private Class getSolverRankingClass() {
        if (solverRankingComparator != null) {
            return solverRankingComparator.getClass();
        } else if (solverRankingWeightFactory != null) {
            return solverRankingWeightFactory.getClass();
        } else {
            return null;
        }
    }

}
