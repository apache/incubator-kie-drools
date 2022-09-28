package org.optaplanner.examples.projectjobscheduling.swingui;

import java.awt.BasicStroke;
import java.awt.BorderLayout;
import java.awt.Rectangle;
import java.util.LinkedHashMap;
import java.util.Map;

import org.jfree.chart.ChartPanel;
import org.jfree.chart.JFreeChart;
import org.jfree.chart.axis.NumberAxis;
import org.jfree.chart.plot.PlotOrientation;
import org.jfree.chart.plot.XYPlot;
import org.jfree.chart.renderer.xy.YIntervalRenderer;
import org.jfree.data.xy.YIntervalSeries;
import org.jfree.data.xy.YIntervalSeriesCollection;
import org.optaplanner.examples.common.swingui.SolutionPanel;
import org.optaplanner.examples.projectjobscheduling.domain.Allocation;
import org.optaplanner.examples.projectjobscheduling.domain.Project;
import org.optaplanner.examples.projectjobscheduling.domain.Schedule;
import org.optaplanner.swing.impl.TangoColorFactory;

public class ProjectJobSchedulingPanel extends SolutionPanel<Schedule> {

    public static final String LOGO_PATH =
            "/org/optaplanner/examples/projectjobscheduling/swingui/projectJobSchedulingLogo.png";

    public ProjectJobSchedulingPanel() {
        setLayout(new BorderLayout());
    }

    @Override
    public void resetPanel(Schedule schedule) {
        removeAll();
        ChartPanel chartPanel = new ChartPanel(createChart(schedule));
        add(chartPanel, BorderLayout.CENTER);
    }

    private JFreeChart createChart(Schedule schedule) {
        YIntervalSeriesCollection seriesCollection = new YIntervalSeriesCollection();
        Map<Project, YIntervalSeries> projectSeriesMap = new LinkedHashMap<>(
                schedule.getProjectList().size());
        YIntervalRenderer renderer = new YIntervalRenderer();
        int maximumEndDate = 0;
        int seriesIndex = 0;
        TangoColorFactory tangoColorFactory = new TangoColorFactory();
        for (Project project : schedule.getProjectList()) {
            YIntervalSeries projectSeries = new YIntervalSeries(project.getLabel());
            seriesCollection.addSeries(projectSeries);
            projectSeriesMap.put(project, projectSeries);
            renderer.setSeriesShape(seriesIndex, new Rectangle());
            renderer.setSeriesStroke(seriesIndex, new BasicStroke(3.0f));
            renderer.setSeriesPaint(seriesIndex, tangoColorFactory.pickColor(project));
            seriesIndex++;
        }
        for (Allocation allocation : schedule.getAllocationList()) {
            int startDate = allocation.getStartDate();
            int endDate = allocation.getEndDate();
            YIntervalSeries projectSeries = projectSeriesMap.get(allocation.getProject());
            projectSeries.add(allocation.getId(), (startDate + endDate) / 2.0,
                    startDate, endDate);
            maximumEndDate = Math.max(maximumEndDate, endDate);
        }
        NumberAxis domainAxis = new NumberAxis("Job");
        domainAxis.setStandardTickUnits(NumberAxis.createIntegerTickUnits());
        domainAxis.setRange(-0.5, schedule.getAllocationList().size() - 0.5);
        domainAxis.setInverted(true);
        NumberAxis rangeAxis = new NumberAxis("Day (start to end date)");
        rangeAxis.setRange(-0.5, maximumEndDate + 0.5);
        XYPlot plot = new XYPlot(seriesCollection, domainAxis, rangeAxis, renderer);
        plot.setOrientation(PlotOrientation.HORIZONTAL);
        plot.setDomainGridlinePaint(TangoColorFactory.ALUMINIUM_5);
        plot.setRangeGridlinePaint(TangoColorFactory.ALUMINIUM_5);
        return new JFreeChart("Project Job Scheduling", JFreeChart.DEFAULT_TITLE_FONT,
                plot, true);
    }

}
