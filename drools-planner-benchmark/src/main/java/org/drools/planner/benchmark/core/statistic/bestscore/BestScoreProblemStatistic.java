/*
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

package org.drools.planner.benchmark.core.statistic.bestscore;

import java.awt.BasicStroke;
import java.awt.image.BufferedImage;
import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.OutputStream;
import javax.imageio.ImageIO;

import org.apache.commons.io.IOUtils;
import org.drools.planner.benchmark.core.ProblemBenchmark;
import org.drools.planner.benchmark.core.SingleBenchmark;
import org.drools.planner.benchmark.core.statistic.AbstractProblemStatistic;
import org.drools.planner.benchmark.core.statistic.MillisecondsSpendNumberFormat;
import org.drools.planner.benchmark.core.statistic.ProblemStatisticType;
import org.drools.planner.benchmark.core.statistic.SingleStatistic;
import org.drools.planner.core.Solver;
import org.drools.planner.core.score.Score;
import org.drools.planner.core.score.definition.ScoreDefinition;
import org.jfree.chart.JFreeChart;
import org.jfree.chart.axis.NumberAxis;
import org.jfree.chart.plot.PlotOrientation;
import org.jfree.chart.plot.XYPlot;
import org.jfree.chart.renderer.xy.StandardXYItemRenderer;
import org.jfree.chart.renderer.xy.XYItemRenderer;
import org.jfree.chart.renderer.xy.XYStepRenderer;
import org.jfree.data.xy.XYSeries;
import org.jfree.data.xy.XYSeriesCollection;

public class BestScoreProblemStatistic extends AbstractProblemStatistic {

    private ScoreDefinition scoreDefinition = null;

    public BestScoreProblemStatistic(ProblemBenchmark problemBenchmark) {
        super(problemBenchmark, ProblemStatisticType.BEST_SOLUTION_CHANGED);
    }

    public SingleStatistic createSingleStatistic(Solver solver) {
        return new BestScoreSingleStatistic(solver);
    }

    // ************************************************************************
    // Write methods
    // ************************************************************************

    @Override
    public void writeStatistic() {
        for (SingleBenchmark singleBenchmark : problemBenchmark.getSingleBenchmarkList()) {
            ScoreDefinition newScoreDefinition = singleBenchmark.getSolverBenchmark().getSolverConfig()
                    .getScoreDirectorFactoryConfig().buildScoreDefinition();
            if (scoreDefinition == null) {
                scoreDefinition = newScoreDefinition;
            } else {
                if (!scoreDefinition.getClass().equals(newScoreDefinition.getClass())) {
                    throw new IllegalStateException("The new scoreDefinition (" + newScoreDefinition
                            + ") should be of the same class as the other scoreDefinition (" + scoreDefinition + ")");
                }
            }
        }
        super.writeStatistic();
    }

    protected void writeCsvStatistic() {
        ProblemStatisticCsv csv = new ProblemStatisticCsv();
        for (SingleBenchmark singleBenchmark : problemBenchmark.getSingleBenchmarkList()) {
            if (singleBenchmark.isSuccess()) {
                BestScoreSingleStatistic singleStatistic = (BestScoreSingleStatistic)
                        singleBenchmark.getSingleStatistic(problemStatisticType);
                for (BestScoreSingleStatisticPoint point : singleStatistic.getPointList()) {
                    long timeMillisSpend = point.getTimeMillisSpend();
                    Score score = point.getScore();
                    if (score != null) {
                        Double scoreGraphValue = scoreDefinition.translateScoreToGraphValue(score);
                        if (scoreGraphValue != null) {
                            csv.addPoint(singleBenchmark, timeMillisSpend, scoreGraphValue);
                        }
                    }
                }
            } else {
                csv.addPoint(singleBenchmark, 0L, "Failed");
            }
        }
        csvStatisticFile = new File(problemBenchmark.getProblemReportDirectory(),
                problemBenchmark.getName() + "BestScoreStatistic.csv");
        csv.writeCsvStatisticFile();
    }

    protected void writeGraphStatistic() {
        NumberAxis xAxis = new NumberAxis("Time spend");
        xAxis.setNumberFormatOverride(new MillisecondsSpendNumberFormat());
        NumberAxis yAxis = new NumberAxis("Score");
        yAxis.setAutoRangeIncludesZero(false);
        XYPlot plot = new XYPlot(null, xAxis, yAxis, null);
        plot.setOrientation(PlotOrientation.VERTICAL);
        int seriesIndex = 0;
        for (SingleBenchmark singleBenchmark : problemBenchmark.getSingleBenchmarkList()) {
            XYSeries series = new XYSeries(singleBenchmark.getSolverBenchmark().getNameWithFavoriteSuffix());
            // No direct ascending lines between 2 points, but a stepping line instead
            XYItemRenderer renderer = new XYStepRenderer();
            if (singleBenchmark.isSuccess()) {
                BestScoreSingleStatistic singleStatistic = (BestScoreSingleStatistic)
                        singleBenchmark.getSingleStatistic(problemStatisticType);
                for (BestScoreSingleStatisticPoint point : singleStatistic.getPointList()) {
                    long timeMillisSpend = point.getTimeMillisSpend();
                    Score score = point.getScore();
                    Double scoreGraphValue = scoreDefinition.translateScoreToGraphValue(score);
                    if (scoreGraphValue != null) {
                        series.add(timeMillisSpend, scoreGraphValue);
                    }
                }
                if (singleStatistic.getPointList().size() <= 1) {
                    // Workaround for https://sourceforge.net/tracker/?func=detail&aid=3387330&group_id=15494&atid=115494
                    renderer = new StandardXYItemRenderer(StandardXYItemRenderer.SHAPES);
                }
            }
            plot.setDataset(seriesIndex, new XYSeriesCollection(series));
            if (singleBenchmark.getSolverBenchmark().isFavorite()) {
                // Make the favorite more obvious
                renderer.setSeriesStroke(0, new BasicStroke(2.0f));
            }
            plot.setRenderer(seriesIndex, renderer);
            seriesIndex++;
        }
        JFreeChart chart = new JFreeChart(problemBenchmark.getName() + " best score statistic",
                JFreeChart.DEFAULT_TITLE_FONT, plot, true);
        BufferedImage chartImage = chart.createBufferedImage(1024, 768);
        graphStatisticFile = new File(problemBenchmark.getProblemReportDirectory(),
                problemBenchmark.getName() + "BestScoreStatistic.png");
        OutputStream out = null;
        try {
            out = new FileOutputStream(graphStatisticFile);
            ImageIO.write(chartImage, "png", out);
        } catch (IOException e) {
            throw new IllegalArgumentException("Problem writing graphStatisticFile: " + graphStatisticFile, e);
        } finally {
            IOUtils.closeQuietly(out);
        }
    }

}
