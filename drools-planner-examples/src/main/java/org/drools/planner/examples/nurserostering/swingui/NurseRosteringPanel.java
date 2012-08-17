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

import java.awt.FlowLayout;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Iterator;
import java.util.LinkedHashMap;
import java.util.LinkedHashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;

import javax.swing.BoxLayout;
import javax.swing.GroupLayout;
import javax.swing.JButton;
import javax.swing.JLabel;
import javax.swing.JOptionPane;
import javax.swing.JPanel;
import javax.swing.JTextField;

import org.apache.commons.lang.ObjectUtils;
import org.drools.planner.core.score.director.ScoreDirector;
import org.drools.planner.core.solution.Solution;
import org.drools.planner.core.solver.ProblemFactChange;
import org.drools.planner.examples.common.swingui.SolutionPanel;
import org.drools.planner.examples.common.swingui.TangoColors;
import org.drools.planner.examples.nurserostering.domain.NurseRosterInfo;
import org.drools.planner.examples.nurserostering.domain.Shift;
import org.drools.planner.examples.nurserostering.domain.ShiftAssignment;
import org.drools.planner.examples.nurserostering.domain.NurseRoster;
import org.drools.planner.examples.nurserostering.domain.Employee;
import org.drools.planner.examples.nurserostering.domain.ShiftDate;
import org.drools.planner.examples.nurserostering.solver.move.EmployeeChangeMove;

public class NurseRosteringPanel extends SolutionPanel {

    public static final String LOGO_PATH = "/org/drools/planner/examples/nurserostering/swingui/nurseRosteringLogo.png";

    private JPanel employeeListPanel;

    private JTextField planningWindowStartField;
    private JButton advancePlanningWindowStartButton;
    private EmployeePanel unassignedPanel;
    private Map<Employee, EmployeePanel> employeeToPanelMap;
    private Map<ShiftAssignment, EmployeePanel> shiftAssignmentToPanelMap;

    public NurseRosteringPanel() {
        GroupLayout layout = new GroupLayout(this);
        setLayout(layout);
        createEmployeeListPanel();
        JPanel headerPanel = createHeaderPanel();
        layout.setHorizontalGroup(layout.createParallelGroup()
                .addComponent(headerPanel).addComponent(employeeListPanel));
        layout.setVerticalGroup(layout.createSequentialGroup()
                .addComponent(headerPanel, GroupLayout.PREFERRED_SIZE, GroupLayout.PREFERRED_SIZE,
                        GroupLayout.PREFERRED_SIZE)
                .addComponent(employeeListPanel, GroupLayout.PREFERRED_SIZE, GroupLayout.PREFERRED_SIZE,
                        GroupLayout.PREFERRED_SIZE));
    }

    private JPanel createHeaderPanel() {
        JPanel headerPanel = new JPanel(new FlowLayout(FlowLayout.LEFT));
        headerPanel.add(new JLabel("Planning window start:"));
        planningWindowStartField = new JTextField(10);
        planningWindowStartField.setEditable(false);
        headerPanel.add(planningWindowStartField);
        advancePlanningWindowStartButton = new JButton("Advance 1 day into the future");
        advancePlanningWindowStartButton.setEnabled(false);
        advancePlanningWindowStartButton.addActionListener(new ActionListener() {
            public void actionPerformed(ActionEvent e) {
                advancePlanningWindowStart();
            }
        });
        headerPanel.add(advancePlanningWindowStartButton);
        return headerPanel;
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

    @Override
    public boolean isRefreshScreenDuringSolving() {
        return true;
    }

    public NurseRoster getNurseRoster() {
        return (NurseRoster) solutionBusiness.getSolution();
    }

    public void resetPanel(Solution solution) {
        NurseRoster nurseRoster = (NurseRoster) solution;
        for (EmployeePanel employeePanel : employeeToPanelMap.values()) {
            if (employeePanel.getEmployee() != null) {
                employeeListPanel.remove(employeePanel);
            }
        }
        employeeToPanelMap.clear();
        employeeToPanelMap.put(null, unassignedPanel);
        shiftAssignmentToPanelMap.clear();
        unassignedPanel.clearShiftAssignments();
        updatePanel(nurseRoster);
        advancePlanningWindowStartButton.setEnabled(true);
        planningWindowStartField.setText(nurseRoster.getNurseRosterInfo().getPlanningWindowStart().getLabel());
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

    private void advancePlanningWindowStart() {
        logger.info("Advancing planningWindowStart.");
        if (solutionBusiness.isSolving()) {
            JOptionPane.showMessageDialog(this,
                    "The GUI does not support this action yet during solving.\nPlanner itself does support it.",
                    "Unsupported in GUI", JOptionPane.ERROR_MESSAGE);
            return;
        }
        solutionBusiness.doProblemFactChange(new ProblemFactChange() {
            public void doChange(ScoreDirector scoreDirector) {
                NurseRoster nurseRoster = (NurseRoster) scoreDirector.getWorkingSolution();
                NurseRosterInfo nurseRosterInfo = nurseRoster.getNurseRosterInfo();
                List<ShiftDate> shiftDateList = nurseRoster.getShiftDateList();
                ShiftDate planningWindowStart = nurseRosterInfo.getPlanningWindowStart();
                int windowStartIndex = shiftDateList.indexOf(planningWindowStart);
                if (windowStartIndex < 0) {
                    throw new IllegalStateException("The planningWindowStart ("
                            + planningWindowStart + ") must be in the shiftDateList ("
                            + shiftDateList +").");
                }
                ShiftDate oldLastShiftDate = shiftDateList.get(shiftDateList.size() - 1);
                ShiftDate newShiftDate = new ShiftDate();
                newShiftDate.setId(oldLastShiftDate.getId() + 1L);
                newShiftDate.setDayIndex(oldLastShiftDate.getDayIndex() + 1);
                newShiftDate.setDateString(oldLastShiftDate.determineNextDateString());
                newShiftDate.setDayOfWeek(oldLastShiftDate.getDayOfWeek().determineNextDayOfWeek());
                List<Shift> refShiftList = planningWindowStart.getShiftList();
                List<Shift> newShiftList = new ArrayList<Shift>(refShiftList.size());
                newShiftDate.setShiftList(newShiftList);
                nurseRoster.getShiftDateList().add(newShiftDate);
                scoreDirector.afterProblemFactAdded(newShiftDate);
                Shift oldLastShift = nurseRoster.getShiftList().get(nurseRoster.getShiftList().size() - 1);
                long shiftId = oldLastShift.getId() + 1L;
                int shiftIndex = oldLastShift.getIndex() + 1;
                long shiftAssignmentId = nurseRoster.getShiftAssignmentList().get(
                        nurseRoster.getShiftAssignmentList().size() - 1).getId() + 1L;
                for (Shift refShift : refShiftList) {
                    Shift newShift = new Shift();
                    newShift.setId(shiftId);
                    shiftId++;
                    newShift.setShiftDate(newShiftDate);
                    newShift.setShiftType(refShift.getShiftType());
                    newShift.setIndex(shiftIndex);
                    shiftIndex++;
                    newShift.setRequiredEmployeeSize(refShift.getRequiredEmployeeSize());
                    newShiftList.add(newShift);
                    nurseRoster.getShiftList().add(newShift);
                    scoreDirector.afterProblemFactAdded(newShift);
                    for (int indexInShift = 0; indexInShift < newShift.getRequiredEmployeeSize(); indexInShift++) {
                        ShiftAssignment newShiftAssignment = new ShiftAssignment();
                        newShiftAssignment.setId(shiftAssignmentId);
                        shiftAssignmentId++;
                        newShiftAssignment.setShift(newShift);
                        newShiftAssignment.setIndexInShift(indexInShift);
                        nurseRoster.getShiftAssignmentList().add(newShiftAssignment);
                        scoreDirector.afterEntityAdded(newShiftAssignment);
                    }
                }
                windowStartIndex++;
                ShiftDate newPlanningWindowStart = shiftDateList.get(windowStartIndex);
                nurseRosterInfo.setPlanningWindowStart(newPlanningWindowStart);
                nurseRosterInfo.setLastShiftDate(newShiftDate);
                scoreDirector.afterProblemFactChanged(nurseRosterInfo);
            }
        });
        resetPanel(solutionBusiness.getSolution());
        validate();
    }

    public void deleteEmployee(final Employee employee) {
        logger.info("Scheduling delete of employee ({}).", employee);
        solutionBusiness.doProblemFactChange(new ProblemFactChange() {
            public void doChange(ScoreDirector scoreDirector) {
                NurseRoster nurseRoster = (NurseRoster) scoreDirector.getWorkingSolution();
                // First remove the planning fact from all planning entities that use it
                for (ShiftAssignment shiftAssignment : nurseRoster.getShiftAssignmentList()) {
                    if (ObjectUtils.equals(shiftAssignment.getEmployee(), employee)) {
                        // TODO HACK we are removing it because it becomes uninitialized,
                        // which means it has to be retracted
                        // This is nonsense from a ProblemFactChange point of view, FIXME!
                        scoreDirector.beforeEntityRemoved(shiftAssignment);
                        shiftAssignment.setEmployee(null);
                        scoreDirector.afterEntityRemoved(shiftAssignment);
                    }
                }
                // Next remove it the planning fact itself
                for (Iterator<Employee> it = nurseRoster.getEmployeeList().iterator(); it.hasNext(); ) {
                    Employee workingEmployee = it.next();
                    if (ObjectUtils.equals(workingEmployee, employee)) {
                        scoreDirector.beforeProblemFactRemoved(workingEmployee);
                        it.remove(); // remove from list
                        scoreDirector.beforeProblemFactRemoved(employee);
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
