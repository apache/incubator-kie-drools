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

package org.optaplanner.benchmark.impl.statistic.acceptedselectedmovecount;

import java.awt.BasicStroke;
import java.awt.Paint;
import java.io.File;
import java.text.NumberFormat;
import java.util.Locale;

import org.optaplanner.benchmark.impl.DefaultPlannerBenchmark;
import org.optaplanner.benchmark.impl.ProblemBenchmark;
import org.optaplanner.benchmark.impl.SingleBenchmark;
import org.optaplanner.benchmark.impl.statistic.AbstractProblemStatistic;
import org.optaplanner.benchmark.impl.statistic.MillisecondsSpendNumberFormat;
import org.optaplanner.benchmark.impl.statistic.ProblemStatisticType;
import org.optaplanner.benchmark.impl.statistic.SingleStatistic;
import org.jfree.chart.JFreeChart;
import org.jfree.chart.axis.NumberAxis;
import org.jfree.chart.plot.DefaultDrawingSupplier;
import org.jfree.chart.plot.DrawingSupplier;
import org.jfree.chart.plot.PlotOrientation;
import org.jfree.chart.plot.XYPlot;
import org.jfree.chart.renderer.xy.XYItemRenderer;
import org.jfree.chart.renderer.xy.XYLineAndShapeRenderer;
import org.jfree.data.xy.XYSeries;
import org.jfree.data.xy.XYSeriesCollection;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class AcceptedSelectedMoveCountProblemStatistic extends AbstractProblemStatistic {

    protected File graphStatisticFile = null;
    public final Logger logger = LoggerFactory.getLogger(AcceptedSelectedMoveCountProblemStatistic.class);

    public AcceptedSelectedMoveCountProblemStatistic(ProblemBenchmark problemBenchmark) {
        super(problemBenchmark, ProblemStatisticType.ACCEPTED_SELECTED_MOVE_COUNT);
    }

    public SingleStatistic createSingleStatistic() {
        return new AcceptedSelectedMoveCountSingleStatistic();        
    }

    /**
     * @return never null, relative to the {@link DefaultPlannerBenchmark#benchmarkReportDirectory}
     * (not {@link ProblemBenchmark#problemReportDirectory})
     */
    public String getGraphFilePath() {
        return toFilePath(graphStatisticFile);
    }

    // ************************************************************************
    // Write methods
    // ************************************************************************

    protected void writeCsvStatistic() {        
        ProblemStatisticCsv csv = new ProblemStatisticCsv();
        for (SingleBenchmark singleBenchmark : problemBenchmark.getSingleBenchmarkList()) {
            if (singleBenchmark.isSuccess()) {
                AcceptedSelectedMoveCountSingleStatistic singleStatistic = (AcceptedSelectedMoveCountSingleStatistic)
                        singleBenchmark.getSingleStatistic(problemStatisticType);
                for (AcceptedSelectedMoveCountSingleStatisticPoint point : singleStatistic.getPointList()) {
                    long timeMillisSpend = point.getTimeMillisSpend();
                    AcceptedSelectedMoveCountMeasurement asMovecountMeasurement  = point.getASMoveCountMeasurement();
                    csv.addPoint(singleBenchmark, timeMillisSpend,
                            Long.toString(asMovecountMeasurement.getAcceptedMoveCount())
                            + "/" + Long.toString(asMovecountMeasurement.getSelectedMoveCount()));
                    
                }
            } else {
                csv.addPoint(singleBenchmark, 0L, "Failed");
            }
        }
        csvStatisticFile = new File(problemBenchmark.getProblemReportDirectory(),
                problemBenchmark.getName() + "AcceptedSelectedMoveCountStatistic.csv");
        csv.writeCsvStatisticFile();
    }

    protected void writeGraphStatistic() {
        Locale locale = problemBenchmark.getPlannerBenchmark().getBenchmarkReport().getLocale();
        NumberAxis xAxis = new NumberAxis("Time spend");
        xAxis.setNumberFormatOverride(new MillisecondsSpendNumberFormat(locale));
        NumberAxis yAxis = new NumberAxis("Accepted/selected moves per step");
        yAxis.setNumberFormatOverride(NumberFormat.getInstance(locale));

        
        XYPlot plot = new XYPlot(null, xAxis, yAxis, null);
        DrawingSupplier drawingSupplier = new DefaultDrawingSupplier();
        plot.setOrientation(PlotOrientation.VERTICAL);
        
        int seriesIndex = 0;
        for (SingleBenchmark singleBenchmark : problemBenchmark.getSingleBenchmarkList()) {
            XYSeries accSeries = new XYSeries(
                    singleBenchmark.getSolverBenchmark().getNameWithFavoriteSuffix() + " accepted");
            
            XYSeries selSeries = new XYSeries(
                    singleBenchmark.getSolverBenchmark().getNameWithFavoriteSuffix() + " selected");            
            
            XYItemRenderer renderer = new XYLineAndShapeRenderer();
            if (singleBenchmark.isSuccess()) {
                AcceptedSelectedMoveCountSingleStatistic singleStatistic = (AcceptedSelectedMoveCountSingleStatistic)
                        singleBenchmark.getSingleStatistic(problemStatisticType);
                for (AcceptedSelectedMoveCountSingleStatisticPoint point : singleStatistic.getPointList()) {
                    long timeMillisSpend = point.getTimeMillisSpend();
                    long acceptedMoveCount = point.getASMoveCountMeasurement().getAcceptedMoveCount();
                    long selectedMoveCount = point.getASMoveCountMeasurement().getSelectedMoveCount();

                    accSeries.add(timeMillisSpend, acceptedMoveCount);
     
                    selSeries.add(timeMillisSpend, selectedMoveCount);

                }
            }
            XYSeriesCollection seriesCollection = new XYSeriesCollection();
            seriesCollection.addSeries(accSeries);            
            seriesCollection.addSeries(selSeries);
        
            //dashed line for selected move count
            renderer.setSeriesStroke(1, new BasicStroke(2.0f, BasicStroke.CAP_ROUND, BasicStroke.JOIN_ROUND, 1.0f, new float[] {2.0f, 6.0f}, 0.0f));
                       
            //render both lines in the same color
            Paint linePaint = drawingSupplier.getNextPaint();
            renderer.setSeriesPaint(0, linePaint);            
            renderer.setSeriesPaint(1, linePaint);                
            
            plot.setDataset(seriesIndex, seriesCollection);          
            
            plot.setRenderer(seriesIndex, renderer);            
            seriesIndex++;
        }
        
        
        JFreeChart chart = new JFreeChart(problemBenchmark.getName() + " accepted/selected move count statistic",
                JFreeChart.DEFAULT_TITLE_FONT, plot, true);
        graphStatisticFile = writeChartToImageFile(chart, problemBenchmark.getName() + "AcceptedSelectedMoveCountStatistic");
    }

}
