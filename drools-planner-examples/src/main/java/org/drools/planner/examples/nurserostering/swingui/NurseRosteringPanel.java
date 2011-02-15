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

package org.drools.planner.examples.nurserostering.swingui;

import java.awt.Color;
import java.awt.GridLayout;
import java.awt.event.ActionEvent;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import javax.swing.AbstractAction;
import javax.swing.BorderFactory;
import javax.swing.JButton;
import javax.swing.JComboBox;
import javax.swing.JLabel;
import javax.swing.JOptionPane;
import javax.swing.JPanel;

import org.drools.planner.examples.common.swingui.SolutionPanel;
import org.drools.planner.examples.nurserostering.domain.Assignment;
import org.drools.planner.examples.nurserostering.domain.ShiftDate;
import org.drools.planner.examples.nurserostering.domain.NurseRoster;
import org.drools.planner.examples.nurserostering.domain.Employee;
import org.drools.planner.examples.nurserostering.solver.move.EmployeeChangeMove;

/**
 * TODO this code is highly unoptimized
 */
public class NurseRosteringPanel extends SolutionPanel {

    private static final Color HEADER_COLOR = Color.YELLOW;

    private GridLayout gridLayout;

    public NurseRosteringPanel() {
        gridLayout = new GridLayout(0, 1);
        setLayout(gridLayout);
    }

    private NurseRoster getNurseRoster() {
        return (NurseRoster) solutionBusiness.getSolution();
    }

    public void resetPanel() {
        removeAll();
        NurseRoster nurseRoster = getNurseRoster();
        gridLayout.setColumns(nurseRoster.getShiftDateList().size() + 1);
        JLabel headerCornerLabel = new JLabel("E \\ SD");
        headerCornerLabel.setBorder(BorderFactory.createCompoundBorder(
                BorderFactory.createLineBorder(Color.DARK_GRAY),
                BorderFactory.createEmptyBorder(2, 2, 2, 2)));
        headerCornerLabel.setBackground(HEADER_COLOR);
        headerCornerLabel.setOpaque(true);
        add(headerCornerLabel);
        for (ShiftDate shiftDate : nurseRoster.getShiftDateList()) {
            JLabel shiftDateLabel = new JLabel(shiftDate.getLabel());
            shiftDateLabel.setBorder(BorderFactory.createCompoundBorder(
                    BorderFactory.createLineBorder(Color.DARK_GRAY),
                    BorderFactory.createEmptyBorder(2, 2, 2, 2)));
            shiftDateLabel.setBackground(HEADER_COLOR);
            shiftDateLabel.setOpaque(true);
            add(shiftDateLabel);
        }
        Map<Employee, Map<ShiftDate, EmployeeShiftDatePanel>> employeeShiftDatePanelMap = new HashMap<Employee, Map<ShiftDate, EmployeeShiftDatePanel>>();
        for (Employee employee : nurseRoster.getEmployeeList()) {
            JLabel employeeLabel = new JLabel(employee.toString());
            employeeLabel.setBorder(BorderFactory.createCompoundBorder(
                    BorderFactory.createLineBorder(Color.DARK_GRAY),
                    BorderFactory.createEmptyBorder(2, 2, 2, 2)));
            employeeLabel.setBackground(HEADER_COLOR);
            employeeLabel.setOpaque(true);
            add(employeeLabel);
            Map<ShiftDate, EmployeeShiftDatePanel> shiftDatePanelMap = new HashMap<ShiftDate, EmployeeShiftDatePanel>();
            employeeShiftDatePanelMap.put(employee, shiftDatePanelMap);
            for (ShiftDate shiftDate : nurseRoster.getShiftDateList()) {
                EmployeeShiftDatePanel employeeShiftDatePanel = new EmployeeShiftDatePanel();
                if (employee.getContract().getWeekendDefinition().isWeekend(shiftDate.getDayOfWeek())) {
                    employeeShiftDatePanel.setBackground(Color.LIGHT_GRAY);
                }
                employeeShiftDatePanel.setToolTipText("employee " + employee.getCode()
                        + " on dayIndex " + shiftDate.getDayIndex());
                add(employeeShiftDatePanel);
                shiftDatePanelMap.put(shiftDate, employeeShiftDatePanel);
            }
        }
        if (nurseRoster.isInitialized()) {
            for (Assignment assignment : nurseRoster.getAssignmentList()) {
                Employee employee = assignment.getEmployee();
                EmployeeShiftDatePanel employeeShiftDatePanel = employeeShiftDatePanelMap.get(employee).get(assignment.getShiftDate());
                employeeShiftDatePanel.addAssignment(assignment);
            }
        }
    }

    private class EmployeeShiftDatePanel extends JPanel {

        public EmployeeShiftDatePanel() {
            super(new GridLayout(0, 1));
            setBorder(BorderFactory.createCompoundBorder(
                    BorderFactory.createLineBorder(Color.DARK_GRAY),
                    BorderFactory.createEmptyBorder(2, 2, 2, 2)));
        }

        public void addAssignment(Assignment assignment) {
            JButton button = new JButton(new AssignmentAction(assignment));
            add(button);
        }

    }

    private class AssignmentAction extends AbstractAction {

        private Assignment assignment;

        public AssignmentAction(Assignment assignment) {
            super(assignment.getLabel());
            this.assignment = assignment;
        }

        public void actionPerformed(ActionEvent e) {
            List<Employee> employeeList = getNurseRoster().getEmployeeList();
            JComboBox employeeListField = new JComboBox(employeeList.toArray());
            employeeListField.setSelectedItem(assignment.getEmployee());
            int result = JOptionPane.showConfirmDialog(NurseRosteringPanel.this.getRootPane(), employeeListField,
                    "Select employee", JOptionPane.OK_CANCEL_OPTION);
            if (result == JOptionPane.OK_OPTION) {
                Employee toEmployee = (Employee) employeeListField.getSelectedItem();
                solutionBusiness.doMove(new EmployeeChangeMove(assignment, toEmployee));
                workflowFrame.updateScreen();
            }
        }

    }

}
