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

package org.drools.planner.examples.nurserostering.swingui;

import java.awt.BorderLayout;
import java.awt.Color;
import java.awt.Component;
import java.awt.Dimension;
import java.awt.GridLayout;
import java.awt.Insets;
import java.awt.event.ActionEvent;
import java.util.ArrayList;
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

import org.drools.planner.examples.common.swingui.TangoColors;
import org.drools.planner.examples.nurserostering.domain.Employee;
import org.drools.planner.examples.nurserostering.domain.Shift;
import org.drools.planner.examples.nurserostering.domain.ShiftAssignment;
import org.drools.planner.examples.nurserostering.domain.ShiftDate;
import org.drools.planner.examples.nurserostering.domain.WeekendDefinition;

public class EmployeePanel extends JPanel {

    private final NurseRosteringPanel nurseRosteringPanel;
    private List<ShiftDate> shiftDateList;
    private List<Shift> shiftList;
    private Employee employee;

    private JLabel employeeLabel;
    private JButton deleteButton;
    private JPanel shiftDateListPanel = null;
    private Map<ShiftDate,JPanel> shiftDatePanelMap;
    private Map<Shift, JPanel> shiftPanelMap;
    private JLabel numberOfShiftAssignmentsLabel;

    private Map<ShiftAssignment, JButton> shiftAssignmentButtonMap = new HashMap<ShiftAssignment, JButton> ();

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
        JPanel labelAndDeletePanel = new JPanel(new BorderLayout());
        labelAndDeletePanel.setPreferredSize(new Dimension(150, 20));
        employeeLabel = new JLabel(getEmployeeLabel());
        employeeLabel.setEnabled(false);
        labelAndDeletePanel.add(employeeLabel, BorderLayout.CENTER);
        if (employee != null) {
            deleteButton = new JButton(new AbstractAction("X") {
                public void actionPerformed(ActionEvent e) {
                    nurseRosteringPanel.deleteEmployee(employee);
                }
            });
            labelAndDeletePanel.add(deleteButton, BorderLayout.EAST);
        }
        add(labelAndDeletePanel, BorderLayout.WEST);
        resetShiftListPanel();
        numberOfShiftAssignmentsLabel = new JLabel("0 assignments", JLabel.RIGHT);
        numberOfShiftAssignmentsLabel.setPreferredSize(new Dimension(130, 20));
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
        shiftDatePanelMap = new LinkedHashMap<ShiftDate, JPanel>(shiftDateList.size());
        for (ShiftDate shiftDate : shiftDateList) {
            JPanel shiftDatePanel = new JPanel(new GridLayout(1, 0));
            if (weekendDefinition.isWeekend(shiftDate.getDayOfWeek())) {
                shiftDatePanel.setBackground(TangoColors.ALUMINIUM_2);
            }
            shiftDatePanel.setBorder(BorderFactory.createCompoundBorder(
                    BorderFactory.createLineBorder(TangoColors.ALUMINIUM_6),
                    BorderFactory.createEmptyBorder(2, 2, 2, 2)));
            shiftDatePanelMap.put(shiftDate, shiftDatePanel);
            shiftDateListPanel.add(shiftDatePanel);
        }
        shiftPanelMap = new LinkedHashMap<Shift, JPanel>(shiftList.size());
        for (Shift shift : shiftList) {
            JPanel shiftDatePanel = shiftDatePanelMap.get(shift.getShiftDate());
            JPanel shiftPanel = new JPanel();
            shiftPanel.setLayout(new BoxLayout(shiftPanel, BoxLayout.Y_AXIS));
            shiftPanel.setBackground(shiftDatePanel.getBackground());
            shiftPanel.setToolTipText((employee == null ? "Unassigned" : employee.getLabel())
                    + " on " + shift.getLabel());
            shiftPanelMap.put(shift, shiftPanel);
            shiftDatePanel.add(shiftPanel);
        }
        add(shiftDateListPanel, BorderLayout.CENTER);
    }

    public void addShiftAssignment(ShiftAssignment shiftAssignment) {
        JPanel shiftPanel = shiftPanelMap.get(shiftAssignment.getShift());
        JButton shiftAssignmentButton = new JButton(new ShiftAssignmentAction(shiftAssignment));
        shiftAssignmentButton.setMargin(new Insets(0, 0, 0, 0));
        shiftAssignmentButton.setToolTipText((employee == null ? "Unassigned" : employee.getLabel())
                + " on " + shiftAssignment.getShift().getLabel());
        shiftPanel.add(shiftAssignmentButton);
        shiftAssignmentButtonMap.put(shiftAssignment, shiftAssignmentButton);
    }

    public void removeShiftAssignment(ShiftAssignment shiftAssignment) {
        JPanel shiftPanel = shiftPanelMap.get(shiftAssignment.getShift());
        JButton shiftAssignmentButton = shiftAssignmentButtonMap.remove(shiftAssignment);
        shiftPanel.remove(shiftAssignmentButton);
    }

    public void clearShiftAssignments() {
        for (JPanel shiftPanel : shiftPanelMap.values()) {
            shiftPanel.removeAll();
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
        }

        public void actionPerformed(ActionEvent e) {
            List<Employee> employeeList = nurseRosteringPanel.getNurseRoster().getEmployeeList();
            JComboBox employeeListField = new JComboBox(employeeList.toArray());
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
