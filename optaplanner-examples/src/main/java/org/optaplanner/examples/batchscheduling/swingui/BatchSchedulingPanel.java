package org.optaplanner.examples.batchscheduling.swingui;

import java.awt.BorderLayout;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

import javax.swing.JScrollPane;
import javax.swing.JTabbedPane;
import javax.swing.JTable;

import org.optaplanner.examples.batchscheduling.domain.Allocation;
import org.optaplanner.examples.batchscheduling.domain.AllocationPath;
import org.optaplanner.examples.batchscheduling.domain.Batch;
import org.optaplanner.examples.batchscheduling.domain.BatchSchedule;
import org.optaplanner.examples.batchscheduling.domain.RoutePath;
import org.optaplanner.examples.batchscheduling.domain.Segment;
import org.optaplanner.examples.common.swingui.SolutionPanel;

public class BatchSchedulingPanel extends SolutionPanel<BatchSchedule> {
    private static final long serialVersionUID = 1L;

    public static final String LOGO_PATH = "/org/optaplanner/examples/batchscheduling/swingui/batchSchedulingLogo.png";

    public BatchSchedulingPanel() {
        setLayout(new BorderLayout());
    }

    @Override
    public void resetPanel(BatchSchedule solution) {
        removeAll();
        repaint();

        JTabbedPane tabbedPane = new JTabbedPane();
        tabbedPane.add("Batches", createComponentPanel1(solution));
        tabbedPane.add("RoutePaths", createComponentPanel2(solution));
        tabbedPane.add("Segments", createComponentPanel3(solution));
        tabbedPane.add("Allocations", createComponentPanel4(solution));
        add(tabbedPane, BorderLayout.CENTER);
        setPreferredSize(PREFERRED_SCROLLABLE_VIEWPORT_SIZE);

        add(tabbedPane);
    }

    private JScrollPane createComponentPanel1(BatchSchedule solution) {
        SimpleDateFormat formatter = new SimpleDateFormat("HH:mm:ss");
        Date date = new Date();
        String strDate = formatter.format(date);

        List<String> tableHeader = new ArrayList<>();
        List<List<String>> tableData = new ArrayList<>();

        tableHeader.add("Batch");
        tableHeader.add("Volume");
        tableHeader.add("DelayRangeValue");
        tableHeader.add("Time Refreshed");

        for (Batch batch : solution.getBatchList()) {
            List<String> rowData = new ArrayList<>();
            rowData.add(batch.getName());
            rowData.add(batch.getVolume().toString());
            rowData.add(batch.getDelayRangeValue().toString());
            rowData.add(strDate);
            tableData.add(rowData);
        }

        JTable table = new JTable(tableToArray(tableData), tableRowToArray(tableHeader));
        table.getColumnModel().getColumn(1).setMinWidth(300);

        JScrollPane componentPanel = new JScrollPane(table);
        table.setFillsViewportHeight(true);
        return componentPanel;
    }

    private JScrollPane createComponentPanel2(BatchSchedule solution) {
        SimpleDateFormat formatter = new SimpleDateFormat("HH:mm:ss");
        Date date = new Date();
        String strDate = formatter.format(date);

        List<String> tableHeader = new ArrayList<>();
        List<List<String>> tableData = new ArrayList<>();

        tableHeader.add("Batch");
        tableHeader.add("RoutePath");
        tableHeader.add("Selected");
        tableHeader.add("Time Refreshed");

        for (Batch batch : solution.getBatchList()) {
            for (RoutePath routePath : batch.getRoutePathList()) {

                List<String> rowData = new ArrayList<>();
                String strSelectedRoutePath = "";
                rowData.add(batch.getName());
                rowData.add(routePath.getPath());

                if (solution.getAllocationPathList() != null) {
                    for (AllocationPath allocationPath : solution.getAllocationPathList()) {
                        if ((allocationPath.getBatch() != null) && (allocationPath.getRoutePath() != null)) {
                            if ((allocationPath.getBatch().getName().equals(batch.getName()))
                                    && (allocationPath.getRoutePath().getPath().equals(routePath.getPath()))) {
                                strSelectedRoutePath = "Selected";
                            }
                        }
                    }
                }

                rowData.add(strSelectedRoutePath);
                rowData.add(strDate);
                tableData.add(rowData);

            }
        }

        JTable table = new JTable(tableToArray(tableData), tableRowToArray(tableHeader));
        table.getColumnModel().getColumn(1).setMinWidth(300);

        JScrollPane componentPanel = new JScrollPane(table);
        table.setFillsViewportHeight(true);
        return componentPanel;
    }

    private JScrollPane createComponentPanel3(BatchSchedule solution) {
        List<String> tableHeader = new ArrayList<>();
        List<List<String>> tableData = new ArrayList<>();

        tableHeader.add("Batch");
        tableHeader.add("RoutePath");
        tableHeader.add("Segment");
        tableHeader.add("Length");
        tableHeader.add("FlowRate");
        tableHeader.add("Delay");
        tableHeader.add("Start Injection Time");
        tableHeader.add("End Injection Time");
        tableHeader.add("Start Delivery Time");
        tableHeader.add("End Delivery Time");
        tableHeader.add("Predecessor Start Delivery Time");

        for (Batch batch : solution.getBatchList()) {
            for (RoutePath routePath : batch.getRoutePathList()) {
                for (Segment segment : routePath.getSegmentList()) {

                    List<String> rowData = new ArrayList<>();
                    String strLength = "";
                    String strFlowRate = "";
                    String strDelay = "";
                    String strStartTime1 = "";
                    String strEndTime1 = "";
                    String strStartTime2 = "";
                    String strEndTime2 = "";
                    String strPEndTime = "";
                    rowData.add(batch.getName());
                    rowData.add(routePath.getPath());
                    rowData.add(segment.getName());

                    if (solution.getAllocationList() != null) {
                        for (Allocation allocation : solution.getAllocationList()) {

                            if ((allocation.getBatch() != null) && (allocation.getRoutePath() != null)
                                    && (allocation.getSegment() != null)) {
                                if ((allocation.getBatch().getName().equals(batch.getName()))
                                        && (allocation.getRoutePath().getPath().equals(routePath.getPath()))
                                        && (allocation.getSegment().getName().equals(segment.getName()))) {
                                    if (allocation.getSegment() != null) {
                                        strLength = Float.toString(allocation.getSegment().getLength());
                                        strFlowRate = Float.toString(allocation.getSegment().getFlowRate());
                                    } else {
                                        strLength = "";
                                        strFlowRate = "";
                                    }

                                    if (allocation.getDelay() != null) {
                                        strDelay = allocation.getDelay().toString();
                                    } else {
                                        strDelay = "";
                                    }

                                    if (allocation.getStartInjectionTime() != null) {
                                        strStartTime1 = allocation.getStartInjectionTime().toString();
                                    } else {
                                        strStartTime1 = "";
                                    }

                                    if (allocation.getEndInjectionTime() != null) {
                                        strEndTime1 = allocation.getEndInjectionTime().toString();
                                    } else {
                                        strEndTime1 = "";
                                    }

                                    if (allocation.getStartDeliveryTime() != null) {
                                        strStartTime2 = allocation.getStartDeliveryTime().toString();
                                    } else {
                                        strStartTime2 = "";
                                    }

                                    if (allocation.getEndDeliveryTime() != null) {
                                        strEndTime2 = allocation.getEndDeliveryTime().toString();
                                    } else {
                                        strEndTime2 = "";
                                    }

                                    if (allocation.getPredecessorsDoneDate() != null) {
                                        strPEndTime = allocation.getPredecessorsDoneDate().toString();
                                    } else {
                                        strPEndTime = "";
                                    }

                                }
                            }
                        }
                    }

                    rowData.add(strLength);
                    rowData.add(strFlowRate);
                    rowData.add(strDelay);
                    rowData.add(strStartTime1);
                    rowData.add(strEndTime1);
                    rowData.add(strStartTime2);
                    rowData.add(strEndTime2);
                    rowData.add(strPEndTime);
                    tableData.add(rowData);

                }
            }
        }

        JTable table = new JTable(tableToArray(tableData), tableRowToArray(tableHeader));
        table.getColumnModel().getColumn(1).setMinWidth(300);

        JScrollPane componentPanel = new JScrollPane(table);
        table.setFillsViewportHeight(true);
        return componentPanel;
    }

    private JScrollPane createComponentPanel4(BatchSchedule solution) {
        List<String> tableHeader = new ArrayList<>();
        List<List<String>> tableData = new ArrayList<>();

        tableHeader.add("Batch");
        tableHeader.add("RoutePath");
        tableHeader.add("Segment");
        tableHeader.add("Length");
        tableHeader.add("FlowRate");
        tableHeader.add("Delay");
        tableHeader.add("Start Injection Time");
        tableHeader.add("End Injection Time");
        tableHeader.add("Start Delivery Time");
        tableHeader.add("End Delivery Time");
        tableHeader.add("Predecessor Start Delivery Time");

        for (Batch batch : solution.getBatchList()) {
            for (RoutePath routePath : batch.getRoutePathList()) {
                for (Segment segment : routePath.getSegmentList()) {
                    List<String> rowData = new ArrayList<>();
                    String strLength = "";
                    String strFlowRate = "";
                    String strDelay = "";
                    String strStartTime1 = "";
                    String strEndTime1 = "";
                    String strStartTime2 = "";
                    String strEndTime2 = "";
                    String strPEndTime = "";
                    rowData.add(batch.getName());
                    rowData.add(routePath.getPath());
                    rowData.add(segment.getName());

                    if (solution.getAllocationList() != null) {
                        for (Allocation allocation : solution.getAllocationList()) {
                            if ((allocation.getBatch() != null) && (allocation.getRoutePath() != null)
                                    && (allocation.getSegment() != null)) {
                                if ((allocation.getBatch().getName().equals(batch.getName()))
                                        && (allocation.getRoutePath().getPath().equals(routePath.getPath()))
                                        && (allocation.getSegment().getName().equals(segment.getName()))) {
                                    if (allocation.getSegment() != null) {
                                        strLength = Float.toString(allocation.getSegment().getLength());
                                        strFlowRate = Float.toString(allocation.getSegment().getFlowRate());
                                    } else {
                                        strLength = "";
                                        strFlowRate = "";
                                    }

                                    if (allocation.getDelay() != null) {
                                        strDelay = allocation.getDelay().toString();
                                    } else {
                                        strDelay = "";
                                    }

                                    if (allocation.getStartInjectionTime() != null) {
                                        strStartTime1 = allocation.getStartInjectionTime().toString();
                                    } else {
                                        strStartTime1 = "";
                                    }

                                    if (allocation.getEndInjectionTime() != null) {
                                        strEndTime1 = allocation.getEndInjectionTime().toString();
                                    } else {
                                        strEndTime1 = "";
                                    }

                                    if (allocation.getStartDeliveryTime() != null) {
                                        strStartTime2 = allocation.getStartInjectionTime().toString();
                                    } else {
                                        strStartTime2 = "";
                                    }

                                    if (allocation.getEndDeliveryTime() != null) {
                                        strEndTime2 = allocation.getEndDeliveryTime().toString();
                                    } else {
                                        strEndTime2 = "";
                                    }

                                    if (allocation.getPredecessorsDoneDate() != null) {
                                        strPEndTime = allocation.getPredecessorsDoneDate().toString();
                                    } else {
                                        strPEndTime = "";
                                    }

                                }
                            }
                        }
                    }

                    if (!(strDelay.equals("") && strStartTime1.equals("") && strEndTime1.equals("")
                            && strPEndTime.equals(""))) {
                        rowData.add(strLength);
                        rowData.add(strFlowRate);
                        rowData.add(strDelay);
                        rowData.add(strStartTime1);
                        rowData.add(strEndTime1);
                        rowData.add(strStartTime2);
                        rowData.add(strEndTime2);
                        rowData.add(strPEndTime);
                        tableData.add(rowData);
                    }

                }
            }
        }

        JTable table = new JTable(tableToArray(tableData), tableRowToArray(tableHeader));
        table.getColumnModel().getColumn(1).setMinWidth(300);

        JScrollPane componentPanel = new JScrollPane(table);
        table.setFillsViewportHeight(true);
        return componentPanel;
    }

    private static Object[] tableRowToArray(List<String> tableRow) {
        return tableRow.toArray();
    }

    private static Object[][] tableToArray(List<List<String>> tableData) {
        return tableData.stream()
                .map(BatchSchedulingPanel::tableRowToArray)
                .toArray(Object[][]::new);
    }

}
