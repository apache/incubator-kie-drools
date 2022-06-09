package org.optaplanner.examples.cheaptime.swingui;

import static java.util.Comparator.comparing;
import static java.util.Comparator.nullsFirst;
import static java.util.function.Function.identity;

import java.awt.BasicStroke;
import java.awt.BorderLayout;
import java.util.ArrayList;
import java.util.Comparator;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

import javax.swing.JCheckBox;
import javax.swing.SwingConstants;

import org.jfree.chart.ChartPanel;
import org.jfree.chart.JFreeChart;
import org.jfree.chart.axis.NumberAxis;
import org.jfree.chart.plot.CombinedRangeXYPlot;
import org.jfree.chart.plot.PlotOrientation;
import org.jfree.chart.plot.XYPlot;
import org.jfree.chart.renderer.xy.HighLowRenderer;
import org.jfree.chart.renderer.xy.StandardXYItemRenderer;
import org.jfree.chart.renderer.xy.XYItemRenderer;
import org.jfree.chart.util.ShapeUtils;
import org.jfree.data.time.FixedMillisecond;
import org.jfree.data.time.ohlc.OHLCSeries;
import org.jfree.data.time.ohlc.OHLCSeriesCollection;
import org.jfree.data.xy.XYSeries;
import org.jfree.data.xy.XYSeriesCollection;
import org.optaplanner.examples.cheaptime.domain.CheapTimeSolution;
import org.optaplanner.examples.cheaptime.domain.Machine;
import org.optaplanner.examples.cheaptime.domain.Period;
import org.optaplanner.examples.cheaptime.domain.Resource;
import org.optaplanner.examples.cheaptime.domain.Task;
import org.optaplanner.examples.cheaptime.domain.TaskAssignment;
import org.optaplanner.examples.cheaptime.domain.TaskRequirement;
import org.optaplanner.examples.common.swingui.SolutionPanel;
import org.optaplanner.examples.common.util.Pair;
import org.optaplanner.swing.impl.TangoColorFactory;

public class CheapTimePanel extends SolutionPanel<CheapTimeSolution> {

    public static final String LOGO_PATH = "/org/optaplanner/examples/cheaptime/swingui/cheapTimeLogo.png";
    private static final Comparator<TaskAssignment> STABLE_COMPARATOR = comparing(
            (TaskAssignment a) -> a.getTask().getStartPeriodRangeFrom())
                    .thenComparingInt(a -> a.getTask().getStartPeriodRangeTo())
                    .thenComparingInt(a -> a.getTask().getDuration())
                    .thenComparingLong(TaskAssignment::getId);
    private static final Comparator<TaskAssignment> GROUP_BY_MACHINE_COMPARATOR = comparing(TaskAssignment::getMachine,
            nullsFirst(comparing(Machine::getId)))
                    .thenComparing(TaskAssignment::getStartPeriod, nullsFirst(comparing(identity())))
                    .thenComparingInt(a -> a.getTask().getDuration())
                    .thenComparingLong(TaskAssignment::getId);

    private final JCheckBox groupByMachineCheckBox;

    public CheapTimePanel() {
        setLayout(new BorderLayout());
        groupByMachineCheckBox = new JCheckBox("Group by assigned machine", false);
        groupByMachineCheckBox.setHorizontalAlignment(SwingConstants.RIGHT);
        groupByMachineCheckBox.addActionListener(e -> {
            updatePanel(getSolution());
            validate();
        });
    }

    @Override
    public void resetPanel(CheapTimeSolution solution) {
        removeAll();
        add(groupByMachineCheckBox, BorderLayout.NORTH);
        ChartPanel chartPanel = new ChartPanel(createChart(solution));
        add(chartPanel, BorderLayout.CENTER);
    }

    private JFreeChart createChart(CheapTimeSolution solution) {
        TangoColorFactory tangoColorFactory = new TangoColorFactory();
        NumberAxis rangeAxis = new NumberAxis("Period");
        rangeAxis.setRange(-0.5, solution.getGlobalPeriodRangeTo() + 0.5);
        XYPlot taskAssignmentPlot = createTaskAssignmentPlot(tangoColorFactory, solution);
        XYPlot periodCostPlot = createPeriodCostPlot(solution);
        XYPlot capacityPlot = createAvailableCapacityPlot(tangoColorFactory, solution);
        CombinedRangeXYPlot combinedPlot = new CombinedRangeXYPlot(rangeAxis);
        combinedPlot.add(taskAssignmentPlot, 5);
        combinedPlot.add(periodCostPlot, 1);
        combinedPlot.add(capacityPlot, 1);

        combinedPlot.setOrientation(PlotOrientation.HORIZONTAL);
        return new JFreeChart("Cheap Power Time Scheduling", JFreeChart.DEFAULT_TITLE_FONT,
                combinedPlot, true);
    }

    private XYPlot createTaskAssignmentPlot(TangoColorFactory tangoColorFactory, CheapTimeSolution solution) {
        OHLCSeriesCollection seriesCollection = new OHLCSeriesCollection();
        Map<Machine, OHLCSeries> machineSeriesMap = new LinkedHashMap<>(
                solution.getMachineList().size());
        HighLowRenderer renderer = new HighLowRenderer();
        renderer.setTickLength(0.0);
        int seriesIndex = 0;
        OHLCSeries unassignedProjectSeries = new OHLCSeries("Unassigned");
        seriesCollection.addSeries(unassignedProjectSeries);
        machineSeriesMap.put(null, unassignedProjectSeries);
        renderer.setSeriesStroke(seriesIndex, new BasicStroke(3.0f));
        renderer.setSeriesPaint(seriesIndex, TangoColorFactory.SCARLET_1);
        seriesIndex++;
        for (Machine machine : solution.getMachineList()) {
            OHLCSeries machineSeries = new OHLCSeries(machine.getLabel());
            seriesCollection.addSeries(machineSeries);
            machineSeriesMap.put(machine, machineSeries);
            renderer.setSeriesStroke(seriesIndex, new BasicStroke(3.0f));
            renderer.setSeriesPaint(seriesIndex, tangoColorFactory.pickColor(machine));
            seriesIndex++;
        }
        List<TaskAssignment> taskAssignmentList = solution.getTaskAssignmentList()
                .stream()
                .sorted(groupByMachineCheckBox.isSelected() ? GROUP_BY_MACHINE_COMPARATOR : STABLE_COMPARATOR)
                .collect(Collectors.toList());
        int pixelIndex = 0;
        for (TaskAssignment taskAssignment : taskAssignmentList) {
            Task task = taskAssignment.getTask();
            Integer startPeriod = taskAssignment.getStartPeriod();
            Integer endPeriod = taskAssignment.getEndPeriod();
            if (startPeriod == null) {
                startPeriod = task.getStartPeriodRangeFrom();
                endPeriod = startPeriod + task.getDuration();
            }
            OHLCSeries machineSeries = machineSeriesMap.get(taskAssignment.getMachine());
            machineSeries.add(new FixedMillisecond(pixelIndex), task.getStartPeriodRangeFrom(),
                    startPeriod, endPeriod, task.getStartPeriodRangeTo() + task.getDuration());
            pixelIndex++;
        }
        NumberAxis domainAxis = new NumberAxis("Task");
        domainAxis.setStandardTickUnits(NumberAxis.createIntegerTickUnits());
        domainAxis.setRange(-0.5, taskAssignmentList.size() - 0.5);
        domainAxis.setInverted(true);
        return new XYPlot(seriesCollection, domainAxis, null, renderer);
    }

    private XYPlot createPeriodCostPlot(CheapTimeSolution solution) {
        XYSeries series = new XYSeries("Power price");
        for (Period period : solution.getPeriodList()) {
            series.add(period.getPowerPriceMicros() / 1000000.0, period.getIndex());
        }
        XYSeriesCollection seriesCollection = new XYSeriesCollection();
        seriesCollection.addSeries(series);
        XYItemRenderer renderer = new StandardXYItemRenderer(StandardXYItemRenderer.SHAPES);
        renderer.setSeriesPaint(0, TangoColorFactory.ORANGE_1);
        renderer.setSeriesShape(0, ShapeUtils.createDiamond(2.0F));
        NumberAxis domainAxis = new NumberAxis("Power price");
        return new XYPlot(seriesCollection, domainAxis, null, renderer);
    }

    private XYPlot createAvailableCapacityPlot(TangoColorFactory tangoColorFactory, CheapTimeSolution solution) {
        Map<Pair<Machine, Resource>, List<Integer>> availableMap = new LinkedHashMap<>();
        for (Machine machine : solution.getMachineList()) {
            for (Resource resource : solution.getResourceList()) {
                List<Integer> machineAvailableList = new ArrayList<>(
                        solution.getGlobalPeriodRangeTo());
                for (int period = 0; period < solution.getGlobalPeriodRangeTo(); period++) {
                    machineAvailableList.add(machine.getCapacity(resource));
                }
                availableMap.put(Pair.of(machine, resource), machineAvailableList);
            }
        }
        for (TaskAssignment taskAssignment : solution.getTaskAssignmentList()) {
            Machine machine = taskAssignment.getMachine();
            Integer startPeriod = taskAssignment.getStartPeriod();
            if (machine != null && startPeriod != null) {
                Task task = taskAssignment.getTask();
                List<TaskRequirement> taskRequirementList = task.getTaskRequirementList();
                for (int i = 0; i < taskRequirementList.size(); i++) {
                    TaskRequirement taskRequirement = taskRequirementList.get(i);
                    Resource resource = solution.getResourceList().get(i);
                    Pair<Machine, Resource> machineResourcePair = Pair.of(machine, resource);
                    List<Integer> machineAvailableList = availableMap.get(machineResourcePair);
                    for (int j = 0; j < task.getDuration(); j++) {
                        int period = j + taskAssignment.getStartPeriod();
                        int available = machineAvailableList.get(period);
                        machineAvailableList.set(period, available - taskRequirement.getResourceUsage());
                    }
                }
            }
        }
        Map<Machine, XYSeries> machineSeriesMap = new LinkedHashMap<>();
        for (Machine machine : solution.getMachineList()) {
            XYSeries machineSeries = new XYSeries(machine.getLabel());
            machineSeriesMap.put(machine, machineSeries);
        }
        availableMap.forEach((machineResourcePair, machineAvailableList) -> {
            for (int period = 0; period < solution.getGlobalPeriodRangeTo(); period++) {
                int available = machineAvailableList.get(period);
                machineSeriesMap.get(machineResourcePair.getKey())
                        .add(available, period);
            }
        });
        XYSeriesCollection seriesCollection = new XYSeriesCollection();
        XYItemRenderer renderer = new StandardXYItemRenderer(StandardXYItemRenderer.SHAPES);
        machineSeriesMap.forEach((machine, machineSeries) -> {
            int seriesIndex = machine.getIndex();
            seriesCollection.addSeries(machineSeries);
            renderer.setSeriesPaint(seriesIndex, tangoColorFactory.pickColor(machine));
            renderer.setSeriesShape(seriesIndex, ShapeUtils.createDiamond(1.5F));
            renderer.setSeriesVisibleInLegend(seriesIndex, false);
        });
        NumberAxis domainAxis = new NumberAxis("Capacity");
        return new XYPlot(seriesCollection, domainAxis, null, renderer);
    }
}
