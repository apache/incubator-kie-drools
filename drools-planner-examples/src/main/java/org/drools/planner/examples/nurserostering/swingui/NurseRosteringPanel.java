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

import java.awt.Dimension;
import java.awt.GridLayout;
import java.util.Collections;
import java.util.Iterator;
import java.util.LinkedHashMap;
import java.util.LinkedHashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;

import javax.swing.BoxLayout;
import javax.swing.GroupLayout;
import javax.swing.JPanel;

import org.apache.commons.lang.ObjectUtils;
import org.drools.FactHandle;
import org.drools.WorkingMemory;
import org.drools.planner.core.solution.Solution;
import org.drools.planner.core.solution.director.SolutionDirector;
import org.drools.planner.core.solver.ProblemFactChange;
import org.drools.planner.examples.common.swingui.SolutionPanel;
import org.drools.planner.examples.nurserostering.domain.Shift;
import org.drools.planner.examples.nurserostering.domain.ShiftAssignment;
import org.drools.planner.examples.nurserostering.domain.NurseRoster;
import org.drools.planner.examples.nurserostering.domain.Employee;
import org.drools.planner.examples.nurserostering.domain.ShiftDate;
import org.drools.planner.examples.nurserostering.solver.move.EmployeeChangeMove;

public class NurseRosteringPanel extends SolutionPanel {

    public static final Dimension SHIFT_DIMENSION = new Dimension(20, 20);

    private JPanel employeeListPanel;

    private EmployeePanel unassignedPanel;
    private Map<Employee, EmployeePanel> employeeToPanelMap;
    private Map<ShiftAssignment, EmployeePanel> shiftAssignmentToPanelMap;

    public NurseRosteringPanel() {
        GroupLayout layout = new GroupLayout(this);
        setLayout(layout);
        createEmployeeListPanel();
        JPanel headerPanel = new JPanel();
        layout.setHorizontalGroup(layout.createParallelGroup()
                .addComponent(headerPanel).addComponent(employeeListPanel));
        layout.setVerticalGroup(layout.createSequentialGroup()
                .addComponent(headerPanel, GroupLayout.PREFERRED_SIZE, GroupLayout.PREFERRED_SIZE,
                        GroupLayout.PREFERRED_SIZE)
                .addComponent(employeeListPanel, GroupLayout.PREFERRED_SIZE, GroupLayout.PREFERRED_SIZE,
                        GroupLayout.PREFERRED_SIZE));
    }

    private void createEmployeeListPanel() {
        employeeListPanel = new JPanel();
        employeeListPanel.setLayout(new BoxLayout(employeeListPanel, BoxLayout.Y_AXIS));
        unassignedPanel = new EmployeePanel(this, Collections.<ShiftDate>emptyList(), Collections.<Shift>emptyList(),
                null);
        employeeListPanel.add(unassignedPanel);
        employeeToPanelMap = new LinkedHashMap<Employee, EmployeePanel>();
        employeeToPanelMap.put(null, unassignedPanel);
        shiftAssignmentToPanelMap = new LinkedHashMap<ShiftAssignment, EmployeePanel>();
    }

    public NurseRoster getNurseRoster() {
        return (NurseRoster) solutionBusiness.getSolution();
    }

    public void resetPanel(Solution solution) {
        for (EmployeePanel employeePanel : employeeToPanelMap.values()) {
            if (employeePanel.getEmployee() != null) {
                employeeListPanel.remove(employeePanel);
            }
        }
        employeeToPanelMap.clear();
        employeeToPanelMap.put(null, unassignedPanel);
        shiftAssignmentToPanelMap.clear();
        unassignedPanel.clearShiftAssignments();
        updatePanel(solution);
    }

    @Override
    public void updatePanel(Solution solution) {
        NurseRoster nurseRoster = (NurseRoster) solution;
        List<ShiftDate> shiftDateList = nurseRoster.getShiftDateList();
        List<Shift> shiftList = nurseRoster.getShiftList();
        unassignedPanel.setShiftDateListAndShiftList(shiftDateList, shiftList);
        Set<Employee> deadEmployeeSet = new LinkedHashSet<Employee>(employeeToPanelMap.keySet());
        deadEmployeeSet.remove(null);
        for (Employee employee : nurseRoster.getEmployeeList()) {
            deadEmployeeSet.remove(employee);
            EmployeePanel employeePanel = employeeToPanelMap.get(employee);
            if (employeePanel == null) {
                employeePanel = new EmployeePanel(this, shiftDateList, shiftList, employee);
                employeeListPanel.add(employeePanel);
                employeeToPanelMap.put(employee, employeePanel);
            }
        }
        Set<ShiftAssignment> deadShiftAssignmentSet = new LinkedHashSet<ShiftAssignment>(
                shiftAssignmentToPanelMap.keySet());
        for (ShiftAssignment shiftAssignment : nurseRoster.getShiftAssignmentList()) {
            deadShiftAssignmentSet.remove(shiftAssignment);
            EmployeePanel employeePanel = shiftAssignmentToPanelMap.get(shiftAssignment);
            Employee employee = shiftAssignment.getEmployee();
            if (employeePanel != null
                    && !ObjectUtils.equals(employeePanel.getEmployee(), employee)) {
                shiftAssignmentToPanelMap.remove(shiftAssignment);
                employeePanel.removeShiftAssignment(shiftAssignment);
                employeePanel = null;
            }
            if (employeePanel == null) {
                employeePanel = employeeToPanelMap.get(employee);
                employeePanel.addShiftAssignment(shiftAssignment);
                shiftAssignmentToPanelMap.put(shiftAssignment, employeePanel);
            }
        }
        for (ShiftAssignment deadShiftAssignment : deadShiftAssignmentSet) {
            EmployeePanel deadEmployeePanel = shiftAssignmentToPanelMap.remove(deadShiftAssignment);
            deadEmployeePanel.removeShiftAssignment(deadShiftAssignment);
        }
        for (Employee deadEmployee : deadEmployeeSet) {
            EmployeePanel deadEmployeePanel = employeeToPanelMap.remove(deadEmployee);
            employeeListPanel.remove(deadEmployeePanel);
        }
        for (EmployeePanel employeePanel : employeeToPanelMap.values()) {
            employeePanel.update();
        }
    }

    public void deleteEmployee(final Employee employee) {
        logger.info("Scheduling delete of employee ({}).", employee);
        solutionBusiness.doProblemFactChange(new ProblemFactChange() {
            public void doChange(SolutionDirector solutionDirector) {
                NurseRoster nurseRoster = (NurseRoster) solutionDirector.getWorkingSolution();
                WorkingMemory workingMemory = solutionDirector.getWorkingMemory();
                // First remove the planning fact from all planning entities that use it
                for (ShiftAssignment shiftAssignment : nurseRoster.getShiftAssignmentList()) {
                    if (ObjectUtils.equals(shiftAssignment.getEmployee(), employee)) {
                        FactHandle shiftAssignmentHandle = workingMemory.getFactHandle(shiftAssignment);
                        shiftAssignment.setEmployee(null);
                        workingMemory.retract(shiftAssignmentHandle);
                    }
                }
                // Next remove it the planning fact itself
                for (Iterator<Employee> it = nurseRoster.getEmployeeList().iterator(); it.hasNext(); ) {
                    Employee workingEmployee = it.next();
                    if (ObjectUtils.equals(workingEmployee, employee)) {
                        FactHandle employeeHandle = workingMemory.getFactHandle(workingEmployee);
                        workingMemory.retract(employeeHandle);
                        it.remove(); // remove from list
                        break;
                    }
                }
            }
        });
        updatePanel(solutionBusiness.getSolution());
    }

    public void moveShiftAssignmentToEmployee(ShiftAssignment shiftAssignment, Employee toEmployee) {
        solutionBusiness.doMove(new EmployeeChangeMove(shiftAssignment, toEmployee));
        solverAndPersistenceFrame.resetScreen();
    }

}
