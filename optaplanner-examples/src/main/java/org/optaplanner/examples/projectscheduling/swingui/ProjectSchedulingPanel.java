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

package org.optaplanner.examples.projectscheduling.swingui;

import java.awt.BasicStroke;
import java.awt.BorderLayout;
import java.awt.Dimension;
import java.awt.Rectangle;
import java.util.ArrayList;
import java.util.Date;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;

import org.jfree.chart.ChartFactory;
import org.jfree.chart.ChartPanel;
import org.jfree.chart.JFreeChart;
import org.jfree.chart.axis.DateAxis;
import org.jfree.chart.axis.NumberAxis;
import org.jfree.chart.axis.SymbolAxis;
import org.jfree.chart.axis.ValueAxis;
import org.jfree.chart.plot.CategoryPlot;
import org.jfree.chart.plot.PlotOrientation;
import org.jfree.chart.plot.XYPlot;
import org.jfree.chart.renderer.xy.XYBarRenderer;
import org.jfree.chart.renderer.xy.YIntervalRenderer;
import org.jfree.data.gantt.Task;
import org.jfree.data.gantt.TaskSeries;
import org.jfree.data.gantt.TaskSeriesCollection;
import org.jfree.data.gantt.XYTaskDataset;
import org.jfree.data.time.SimpleTimePeriod;
import org.jfree.data.xy.YIntervalSeries;
import org.jfree.data.xy.YIntervalSeriesCollection;
import org.optaplanner.core.impl.solution.Solution;
import org.optaplanner.examples.common.swingui.SolutionPanel;
import org.optaplanner.examples.common.swingui.TangoColorFactory;
import org.optaplanner.examples.projectscheduling.domain.Allocation;
import org.optaplanner.examples.projectscheduling.domain.Project;
import org.optaplanner.examples.projectscheduling.domain.ProjectsSchedule;

public class ProjectSchedulingPanel extends SolutionPanel {

    public ProjectSchedulingPanel() {
        setLayout(new BorderLayout());
    }

    @Override
    public boolean isRefreshScreenDuringSolving() {
        return true;
    }

    public void resetPanel(Solution solution) {
        removeAll();
        ProjectsSchedule projectsSchedule = (ProjectsSchedule) solution;
        ChartPanel chartPanel = new ChartPanel(createChart(projectsSchedule));
        add(chartPanel, BorderLayout.CENTER);
    }

    private JFreeChart createChart(ProjectsSchedule projectsSchedule) {
        YIntervalSeriesCollection seriesCollection = new YIntervalSeriesCollection();
        Map<Project, YIntervalSeries> projectSeriesMap = new LinkedHashMap<Project, YIntervalSeries>(
                projectsSchedule.getProjectList().size());
        YIntervalRenderer renderer = new YIntervalRenderer();
        int maximumEndDate = 0;
        int seriesIndex = 0;
        for (Project project : projectsSchedule.getProjectList()) {
            YIntervalSeries projectSeries = new YIntervalSeries(project.getLabel());
            seriesCollection.addSeries(projectSeries);
            projectSeriesMap.put(project, projectSeries);
            renderer.setSeriesShape(seriesIndex, new Rectangle());
            renderer.setSeriesStroke(seriesIndex, new BasicStroke(3.0f));
            seriesIndex++;
        }
        for (Allocation allocation : projectsSchedule.getAllocationList()) {
            Integer startDate = allocation.getStartDate();
            Integer endDate = allocation.getEndDate();
            if (startDate != null && endDate != null) {
                YIntervalSeries projectSeries = projectSeriesMap.get(allocation.getProject());
                projectSeries.add(allocation.getId(), (startDate + endDate) / 2.0,
                        startDate, endDate);
                maximumEndDate = Math.max(maximumEndDate, endDate);
            }
        }
        NumberAxis domainAxis = new NumberAxis("Job");
        domainAxis.setStandardTickUnits(NumberAxis.createIntegerTickUnits());
        domainAxis.setRange(-0.5, projectsSchedule.getAllocationList().size() - 0.5);
        domainAxis.setInverted(true);
        NumberAxis rangeAxis = new NumberAxis("Day (start to end date)");
        rangeAxis.setRange(-0.5, maximumEndDate + 0.5);
        XYPlot plot = new XYPlot(seriesCollection, domainAxis, rangeAxis, renderer);
        plot.setOrientation(PlotOrientation.HORIZONTAL);
        return new JFreeChart("Project scheduling", JFreeChart.DEFAULT_TITLE_FONT,
                plot, true);
    }

}
