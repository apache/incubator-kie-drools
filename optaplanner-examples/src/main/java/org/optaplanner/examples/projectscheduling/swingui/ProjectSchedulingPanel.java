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

import java.awt.Dimension;
import java.util.Date;
import java.util.LinkedHashMap;
import java.util.Map;

import org.jfree.chart.ChartFactory;
import org.jfree.chart.ChartPanel;
import org.jfree.chart.JFreeChart;
import org.jfree.chart.plot.CategoryPlot;
import org.jfree.data.gantt.Task;
import org.jfree.data.gantt.TaskSeries;
import org.jfree.data.gantt.TaskSeriesCollection;
import org.jfree.data.time.SimpleTimePeriod;
import org.joda.time.LocalDate;
import org.optaplanner.core.impl.solution.Solution;
import org.optaplanner.examples.common.swingui.SolutionPanel;
import org.optaplanner.examples.common.swingui.TangoColorFactory;
import org.optaplanner.examples.projectscheduling.domain.Allocation;
import org.optaplanner.examples.projectscheduling.domain.Project;
import org.optaplanner.examples.projectscheduling.domain.ProjectsSchedule;

public class ProjectSchedulingPanel extends SolutionPanel {

    public static final LocalDate SCHEDULE_START_DATE = new LocalDate(2014, 1, 1);

    private TangoColorFactory tangoColorFactory;

    public ProjectSchedulingPanel() {
    }

    @Override
    public boolean isRefreshScreenDuringSolving() {
        return true;
    }

    private ProjectsSchedule getProjectsSchedule() {
        return (ProjectsSchedule) solutionBusiness.getSolution();
    }

    public void resetPanel(Solution solution) {
        removeAll();
        tangoColorFactory = new TangoColorFactory();
        ProjectsSchedule projectsSchedule = (ProjectsSchedule) solution;
        ChartPanel chartPanel = new ChartPanel(createChart(projectsSchedule));
        chartPanel.setPreferredSize(new Dimension(1024, 768));
        add(chartPanel);
    }

    private JFreeChart createChart(ProjectsSchedule projectsSchedule) {
        TaskSeriesCollection seriesCollection = new TaskSeriesCollection();
        Map<Project, TaskSeries> taskSeriesMap = new LinkedHashMap<Project, TaskSeries>(
                projectsSchedule.getProjectList().size());
        for (Project project : projectsSchedule.getProjectList()) {
            TaskSeries taskSeries = new TaskSeries(project.getLabel());
            seriesCollection.add(taskSeries);
            taskSeriesMap.put(project, taskSeries);
        }
        for (Allocation allocation : projectsSchedule.getAllocationList()) {
            Integer startDate = allocation.getStartDate();
            Integer endDate = allocation.getEndDate();
            if (startDate != null && endDate != null) {
                Task task = new Task(allocation.getLabel(), toJdkDate(startDate), toJdkDate(endDate));
                taskSeriesMap.get(allocation.getProject()).add(task);
            }
        }
        JFreeChart chart = ChartFactory.createGanttChart("Project scheduling", "Job", "Allocation",
                seriesCollection, true, false, false);
        return chart;
    }

    private Date toJdkDate(Integer date) {
        return SCHEDULE_START_DATE.plusDays(date).toDateMidnight().toDate();
    }

}
