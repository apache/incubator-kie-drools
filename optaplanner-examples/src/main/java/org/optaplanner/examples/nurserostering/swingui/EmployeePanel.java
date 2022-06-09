package org.optaplanner.examples.nurserostering.swingui;

import java.awt.BorderLayout;
import java.awt.Color;
import java.awt.Dimension;
import java.awt.GridLayout;
import java.awt.event.ActionEvent;
import java.util.HashMap;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;

import javax.swing.AbstractAction;
import javax.swing.BorderFactory;
import javax.swing.BoxLayout;
import javax.swing.JButton;
import javax.swing.JComboBox;
import javax.swing.JLabel;
import javax.swing.JOptionPane;
import javax.swing.JPanel;

import org.optaplanner.examples.common.swingui.components.LabeledComboBoxRenderer;
import org.optaplanner.examples.nurserostering.domain.Employee;
import org.optaplanner.examples.nurserostering.domain.Shift;
import org.optaplanner.examples.nurserostering.domain.ShiftAssignment;
import org.optaplanner.examples.nurserostering.domain.ShiftDate;
import org.optaplanner.examples.nurserostering.domain.ShiftType;
import org.optaplanner.examples.nurserostering.domain.WeekendDefinition;
import org.optaplanner.swing.impl.SwingUtils;
import org.optaplanner.swing.impl.TangoColorFactory;

public class EmployeePanel extends JPanel {

    public static final int WEST_HEADER_WIDTH = 160;
    public static final int EAST_HEADER_WIDTH = 130;

    private final NurseRosteringPanel nurseRosteringPanel;
    private List<ShiftDate> shiftDateList;
    private List<Shift> shiftList;
    private Employee employee;

    private JButton deleteButton;
    private JPanel shiftDateListPanel = null;
    private Map<ShiftDate, JPanel> shiftDatePanelMap;
    private Map<Shift, JPanel> shiftPanelMap;
    private JLabel numberOfShiftAssignmentsLabel;

    private Map<ShiftAssignment, JButton> shiftAssignmentButtonMap = new HashMap<>();

    public EmployeePanel(NurseRosteringPanel nurseRosteringPanel, List<ShiftDate> shiftDateList, List<Shift> shiftList,
            Employee employee) {
        super(new BorderLayout());
        this.nurseRosteringPanel = nurseRosteringPanel;
        this.shiftDateList = shiftDateList;
        this.shiftList = shiftList;
        this.employee = employee;
        setBorder(BorderFactory.createCompoundBorder(
                BorderFactory.createCompoundBorder(
                        BorderFactory.createEmptyBorder(1, 2, 1, 2),
                        BorderFactory.createLineBorder(Color.BLACK)),
                BorderFactory.createEmptyBorder(1, 1, 1, 1)));
        createUI();
    }

    public Employee getEmployee() {
        return employee;
    }

    private String getEmployeeLabel() {
        return employee == null ? "Unassigned" : employee.getLabel();
    }

    public void setShiftDateListAndShiftList(List<ShiftDate> shiftDateList, List<Shift> shiftList) {
        this.shiftDateList = shiftDateList;
        this.shiftList = shiftList;
        resetShiftListPanel();
    }

    private void createUI() {
        JPanel labelAndDeletePanel = new JPanel(new BorderLayout(5, 0));
        if (employee != null) {
            labelAndDeletePanel.add(new JLabel(nurseRosteringPanel.getEmployeeIcon()), BorderLayout.WEST);
        }
        JLabel employeeLabel = new JLabel(getEmployeeLabel());
        employeeLabel.setEnabled(false);
        labelAndDeletePanel.add(employeeLabel, BorderLayout.CENTER);
        if (employee != null) {
            JPanel deletePanel = new JPanel(new BorderLayout());
            deleteButton = SwingUtils.makeSmallButton(new JButton(nurseRosteringPanel.getDeleteEmployeeIcon()));
            deleteButton.setToolTipText("Delete");
            deleteButton.addActionListener(e -> nurseRosteringPanel.deleteEmployee(employee));
            deletePanel.add(deleteButton, BorderLayout.NORTH);
            labelAndDeletePanel.add(deletePanel, BorderLayout.EAST);
        }
        labelAndDeletePanel.setPreferredSize(new Dimension(WEST_HEADER_WIDTH,
                (int) labelAndDeletePanel.getPreferredSize().getHeight()));
        add(labelAndDeletePanel, BorderLayout.WEST);
        resetShiftListPanel();
        numberOfShiftAssignmentsLabel = new JLabel("0 assignments", JLabel.RIGHT);
        numberOfShiftAssignmentsLabel.setPreferredSize(new Dimension(EAST_HEADER_WIDTH, 20));
        numberOfShiftAssignmentsLabel.setEnabled(false);
        add(numberOfShiftAssignmentsLabel, BorderLayout.EAST);
    }

    public void resetShiftListPanel() {
        if (shiftDateListPanel != null) {
            remove(shiftDateListPanel);
        }
        WeekendDefinition weekendDefinition = (employee == null) ? WeekendDefinition.SATURDAY_SUNDAY
                : employee.getContract().getWeekendDefinition();
        shiftDateListPanel = new JPanel(new GridLayout(1, 0));
        shiftDatePanelMap = new LinkedHashMap<>(shiftDateList.size());
        for (ShiftDate shiftDate : shiftDateList) {
            JPanel shiftDatePanel = new JPanel(new GridLayout(1, 0));
            Color backgroundColor = weekendDefinition.isWeekend(shiftDate.getDayOfWeek())
                    ? TangoColorFactory.ALUMINIUM_2
                    : shiftDatePanel.getBackground();
            if (employee != null) {
                if (employee.getDayOffRequestMap().containsKey(shiftDate)) {
                    backgroundColor = TangoColorFactory.ALUMINIUM_4;
                } else if (employee.getDayOnRequestMap().containsKey(shiftDate)) {
                    backgroundColor = TangoColorFactory.SCARLET_1;
                }
            }
            shiftDatePanel.setBackground(backgroundColor);
            boolean inPlanningWindow = nurseRosteringPanel.getSolution().getNurseRosterParametrization()
                    .isInPlanningWindow(shiftDate);
            shiftDatePanel.setEnabled(inPlanningWindow);
            shiftDatePanel.setBorder(BorderFactory.createCompoundBorder(
                    BorderFactory
                            .createLineBorder(inPlanningWindow ? TangoColorFactory.ALUMINIUM_6 : TangoColorFactory.ALUMINIUM_3),
                    BorderFactory.createEmptyBorder(2, 2, 2, 2)));
            shiftDatePanelMap.put(shiftDate, shiftDatePanel);
            if (employee == null) {
                // TODO HACK should be in NurseRosterPanel.createHeaderPanel
                JPanel wrappingShiftDatePanel = new JPanel(new BorderLayout());
                JLabel shiftDateLabel = new JLabel(shiftDate.getLabel(), JLabel.CENTER);
                shiftDateLabel.setEnabled(shiftDatePanel.isEnabled());
                wrappingShiftDatePanel.add(shiftDateLabel, BorderLayout.NORTH);
                wrappingShiftDatePanel.add(shiftDatePanel, BorderLayout.CENTER);
                shiftDateListPanel.add(wrappingShiftDatePanel);
            } else {
                shiftDateListPanel.add(shiftDatePanel);
            }
        }
        shiftPanelMap = new LinkedHashMap<>(shiftList.size());
        for (Shift shift : shiftList) {
            JPanel shiftDatePanel = shiftDatePanelMap.get(shift.getShiftDate());
            JPanel shiftPanel = new JPanel();
            shiftPanel.setEnabled(shiftDatePanel.isEnabled());
            shiftPanel.setLayout(new BoxLayout(shiftPanel, BoxLayout.Y_AXIS));
            Color backgroundColor = shiftDatePanel.getBackground();
            if (employee != null) {
                if (employee.getShiftOffRequestMap().containsKey(shift)) {
                    backgroundColor = TangoColorFactory.ALUMINIUM_4;
                } else if (employee.getShiftOnRequestMap().containsKey(shift)) {
                    backgroundColor = TangoColorFactory.SCARLET_1;
                }
            }
            shiftPanel.setBackground(backgroundColor);
            shiftPanel.setToolTipText("<html>Date: " + shift.getShiftDate().getLabel() + "<br/>"
                    + "Employee: " + (employee == null ? "unassigned" : employee.getLabel())
                    + "</html>");
            shiftPanelMap.put(shift, shiftPanel);
            shiftDatePanel.add(shiftPanel);
        }
        add(shiftDateListPanel, BorderLayout.CENTER);
    }

    public void addShiftAssignment(ShiftAssignment shiftAssignment) {
        Shift shift = shiftAssignment.getShift();
        JPanel shiftPanel = shiftPanelMap.get(shift);
        JButton shiftAssignmentButton = SwingUtils.makeSmallButton(new JButton(new ShiftAssignmentAction(shiftAssignment)));
        shiftAssignmentButton.setEnabled(shiftPanel.isEnabled());
        if (employee != null) {
            if (employee.getDayOffRequestMap().containsKey(shift.getShiftDate())
                    || employee.getShiftOffRequestMap().containsKey(shift)) {
                shiftAssignmentButton.setForeground(TangoColorFactory.SCARLET_1);
            }
        }
        Color color = nurseRosteringPanel.determinePlanningEntityColor(shiftAssignment, shift.getShiftType());
        shiftAssignmentButton.setBackground(color);
        String toolTip = nurseRosteringPanel.determinePlanningEntityTooltip(shiftAssignment);
        shiftAssignmentButton.setToolTipText(toolTip);
        shiftPanel.add(shiftAssignmentButton);
        shiftPanel.repaint();
        shiftAssignmentButtonMap.put(shiftAssignment, shiftAssignmentButton);
    }

    public void clearShiftAssignments() {
        for (JPanel shiftPanel : shiftPanelMap.values()) {
            shiftPanel.removeAll();
            shiftPanel.repaint();
        }
        shiftAssignmentButtonMap.clear();
    }

    public void update() {
        numberOfShiftAssignmentsLabel.setText(shiftAssignmentButtonMap.size() + " assignments");
    }

    private class ShiftAssignmentAction extends AbstractAction {

        private ShiftAssignment shiftAssignment;

        public ShiftAssignmentAction(ShiftAssignment shiftAssignment) {
            super(shiftAssignment.getShift().getShiftType().getCode());
            this.shiftAssignment = shiftAssignment;
            Shift shift = shiftAssignment.getShift();
            ShiftType shiftType = shift.getShiftType();
            // Tooltip
            putValue(SHORT_DESCRIPTION, "<html>Date: " + shift.getShiftDate().getLabel() + "<br/>"
                    + "Shift type: " + shiftType.getLabel() + " (from " + shiftType.getStartTimeString()
                    + " to " + shiftType.getEndTimeString() + ")<br/>"
                    + "Employee: " + (employee == null ? "unassigned" : employee.getLabel())
                    + "</html>");
        }

        @Override
        public void actionPerformed(ActionEvent e) {
            List<Employee> employeeList = nurseRosteringPanel.getSolution().getEmployeeList();
            // Add 1 to array size to add null, which makes the entity unassigned
            JComboBox employeeListField = new JComboBox(
                    employeeList.toArray(new Object[employeeList.size() + 1]));
            LabeledComboBoxRenderer.applyToComboBox(employeeListField);
            employeeListField.setSelectedItem(shiftAssignment.getEmployee());
            int result = JOptionPane.showConfirmDialog(EmployeePanel.this.getRootPane(), employeeListField,
                    "Select employee", JOptionPane.OK_CANCEL_OPTION);
            if (result == JOptionPane.OK_OPTION) {
                Employee toEmployee = (Employee) employeeListField.getSelectedItem();
                nurseRosteringPanel.moveShiftAssignmentToEmployee(shiftAssignment, toEmployee);
            }
        }

    }

}
