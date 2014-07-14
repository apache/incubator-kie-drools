/*
 * Copyright 2014 JBoss Inc
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

package org.optaplanner.examples.cheaptime.swingui;

import java.awt.BasicStroke;
import java.awt.BorderLayout;
import java.awt.GridLayout;
import java.awt.Insets;
import java.awt.Rectangle;
import java.awt.event.ActionEvent;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.LinkedHashMap;
import java.util.LinkedHashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;
import javax.swing.AbstractAction;
import javax.swing.GroupLayout;
import javax.swing.ImageIcon;
import javax.swing.JButton;
import javax.swing.JComboBox;
import javax.swing.JLabel;
import javax.swing.JOptionPane;
import javax.swing.JPanel;

import org.apache.commons.lang.ObjectUtils;
import org.jfree.chart.ChartPanel;
import org.jfree.chart.JFreeChart;
import org.jfree.chart.axis.NumberAxis;
import org.jfree.chart.plot.PlotOrientation;
import org.jfree.chart.plot.XYPlot;
import org.jfree.chart.renderer.xy.YIntervalRenderer;
import org.jfree.data.xy.YIntervalSeries;
import org.jfree.data.xy.YIntervalSeriesCollection;
import org.optaplanner.core.api.domain.solution.Solution;
import org.optaplanner.core.impl.score.director.ScoreDirector;
import org.optaplanner.core.impl.solver.ProblemFactChange;
import org.optaplanner.examples.cheaptime.domain.CheapTimeSolution;
import org.optaplanner.examples.cheaptime.domain.Machine;
import org.optaplanner.examples.cheaptime.domain.TaskAssignment;
import org.optaplanner.examples.common.swingui.SolutionPanel;

public class CheapTimePanel extends SolutionPanel {

//    public static final String LOGO_PATH = "/org/optaplanner/examples/cheapTime/swingui/cheapTimeLogo.png";

    public CheapTimePanel() {
        setLayout(new BorderLayout());
    }

    @Override
    public boolean isRefreshScreenDuringSolving() {
        return true;
    }

    private CheapTimeSolution getCheapTimeSolution() {
        return (CheapTimeSolution) solutionBusiness.getSolution();
    }

    public void resetPanel(Solution solution) {
        removeAll();
        ChartPanel chartPanel = new ChartPanel(createChart((CheapTimeSolution) solution));
        add(chartPanel, BorderLayout.CENTER);
    }

    private JFreeChart createChart(CheapTimeSolution solution) {
        YIntervalSeriesCollection seriesCollection = new YIntervalSeriesCollection();
        Map<Machine, YIntervalSeries> machineSeriesMap = new LinkedHashMap<Machine, YIntervalSeries>(
                solution.getMachineList().size());
        YIntervalRenderer renderer = new YIntervalRenderer();
        int seriesIndex = 0;
        for (Machine machine : solution.getMachineList()) {
            YIntervalSeries projectSeries = new YIntervalSeries(machine.getLabel());
            seriesCollection.addSeries(projectSeries);
            machineSeriesMap.put(machine, projectSeries);
            renderer.setSeriesShape(seriesIndex, new Rectangle());
            renderer.setSeriesStroke(seriesIndex, new BasicStroke(3.0f));
            seriesIndex++;
        }
        YIntervalSeries unassignedProjectSeries = new YIntervalSeries("Unassigned");
        seriesCollection.addSeries(unassignedProjectSeries);
        machineSeriesMap.put(null, unassignedProjectSeries);
        renderer.setSeriesShape(seriesIndex, new Rectangle());
        renderer.setSeriesStroke(seriesIndex, new BasicStroke(3.0f));
        for (TaskAssignment taskAssignment : solution.getTaskAssignmentList()) {
            Integer startPeriod = taskAssignment.getStartPeriod();
            Integer endPeriod = taskAssignment.getEndPeriod();
            if (startPeriod == null) {
                startPeriod = 0;
                endPeriod = 0;
            }
            YIntervalSeries machineSeries = machineSeriesMap.get(taskAssignment.getMachine());
            machineSeries.add(taskAssignment.getId(), (startPeriod + endPeriod) / 2.0,
                    startPeriod, endPeriod);
        }
        NumberAxis domainAxis = new NumberAxis("Task");
        domainAxis.setStandardTickUnits(NumberAxis.createIntegerTickUnits());
        domainAxis.setRange(-0.5, solution.getTaskAssignmentList().size() - 0.5);
        domainAxis.setInverted(true);
        NumberAxis rangeAxis = new NumberAxis("Period");
        rangeAxis.setRange(-0.5, solution.getGlobalPeriodRangeTo() + 0.5);
        XYPlot plot = new XYPlot(seriesCollection, domainAxis, rangeAxis, renderer);
        plot.setOrientation(PlotOrientation.HORIZONTAL);
        return new JFreeChart("Project Job Scheduling", JFreeChart.DEFAULT_TITLE_FONT,
                plot, true);
    }


}
