package org.optaplanner.examples.nurserostering.swingui;

import java.awt.BorderLayout;
import java.awt.FlowLayout;
import java.awt.event.ActionEvent;
import java.util.ArrayList;
import java.util.Collections;
import java.util.LinkedHashMap;
import java.util.LinkedHashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;

import javax.swing.AbstractAction;
import javax.swing.BoxLayout;
import javax.swing.GroupLayout;
import javax.swing.ImageIcon;
import javax.swing.JButton;
import javax.swing.JLabel;
import javax.swing.JOptionPane;
import javax.swing.JPanel;
import javax.swing.JTextField;

import org.optaplanner.examples.common.swingui.SolutionPanel;
import org.optaplanner.examples.nurserostering.domain.Employee;
import org.optaplanner.examples.nurserostering.domain.NurseRoster;
import org.optaplanner.examples.nurserostering.domain.NurseRosterParametrization;
import org.optaplanner.examples.nurserostering.domain.Shift;
import org.optaplanner.examples.nurserostering.domain.ShiftAssignment;
import org.optaplanner.examples.nurserostering.domain.ShiftDate;

public class NurseRosteringPanel extends SolutionPanel<NurseRoster> {

    public static final String LOGO_PATH = "/org/optaplanner/examples/nurserostering/swingui/nurseRosteringLogo.png";

    private final ImageIcon employeeIcon;
    private final ImageIcon deleteEmployeeIcon;

    private JPanel employeeListPanel;

    private JTextField planningWindowStartField;
    private AbstractAction advancePlanningWindowStartAction;
    private EmployeePanel unassignedPanel;
    private Map<Employee, EmployeePanel> employeeToPanelMap;

    public NurseRosteringPanel() {
        employeeIcon = new ImageIcon(getClass().getResource("employee.png"));
        deleteEmployeeIcon = new ImageIcon(getClass().getResource("deleteEmployee.png"));
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

    public ImageIcon getEmployeeIcon() {
        return employeeIcon;
    }

    public ImageIcon getDeleteEmployeeIcon() {
        return deleteEmployeeIcon;
    }

    private JPanel createHeaderPanel() {
        JPanel headerPanel = new JPanel(new BorderLayout(20, 0));
        JPanel planningWindowPanel = new JPanel(new FlowLayout(FlowLayout.LEFT));
        planningWindowPanel.add(new JLabel("Planning window start:"));
        planningWindowStartField = new JTextField(10);
        planningWindowStartField.setEditable(false);
        planningWindowPanel.add(planningWindowStartField);
        advancePlanningWindowStartAction = new AbstractAction("Advance 1 day into the future") {
            @Override
            public void actionPerformed(ActionEvent e) {
                advancePlanningWindowStart();
            }
        };
        advancePlanningWindowStartAction.setEnabled(false);
        planningWindowPanel.add(new JButton(advancePlanningWindowStartAction));
        headerPanel.add(planningWindowPanel, BorderLayout.WEST);
        JLabel shiftTypeExplanation = new JLabel("E = Early shift, L = Late shift, ...");
        headerPanel.add(shiftTypeExplanation, BorderLayout.CENTER);
        return headerPanel;
    }

    private void createEmployeeListPanel() {
        employeeListPanel = new JPanel();
        employeeListPanel.setLayout(new BoxLayout(employeeListPanel, BoxLayout.Y_AXIS));
        unassignedPanel = new EmployeePanel(this, Collections.emptyList(), Collections.emptyList(),
                null);
        employeeListPanel.add(unassignedPanel);
        employeeToPanelMap = new LinkedHashMap<>();
        employeeToPanelMap.put(null, unassignedPanel);
    }

    @Override
    public void resetPanel(NurseRoster nurseRoster) {
        for (EmployeePanel employeePanel : employeeToPanelMap.values()) {
            if (employeePanel.getEmployee() != null) {
                employeeListPanel.remove(employeePanel);
            }
        }
        employeeToPanelMap.clear();
        employeeToPanelMap.put(null, unassignedPanel);
        unassignedPanel.clearShiftAssignments();
        preparePlanningEntityColors(nurseRoster.getShiftAssignmentList());
        List<ShiftDate> shiftDateList = nurseRoster.getShiftDateList();
        List<Shift> shiftList = nurseRoster.getShiftList();
        unassignedPanel.setShiftDateListAndShiftList(shiftDateList, shiftList);
        updatePanel(nurseRoster);
        advancePlanningWindowStartAction.setEnabled(true);
        planningWindowStartField.setText(nurseRoster.getNurseRosterParametrization().getPlanningWindowStart().getLabel());
    }

    @Override
    public void updatePanel(NurseRoster nurseRoster) {
        preparePlanningEntityColors(nurseRoster.getShiftAssignmentList());
        List<ShiftDate> shiftDateList = nurseRoster.getShiftDateList();
        List<Shift> shiftList = nurseRoster.getShiftList();
        Set<Employee> deadEmployeeSet = new LinkedHashSet<>(employeeToPanelMap.keySet());
        deadEmployeeSet.remove(null);
        for (Employee employee : nurseRoster.getEmployeeList()) {
            deadEmployeeSet.remove(employee);
            EmployeePanel employeePanel = employeeToPanelMap.get(employee);
            if (employeePanel == null) {
                employeePanel = new EmployeePanel(this, shiftDateList, shiftList, employee);
                employeeListPanel.add(employeePanel);
                employeeToPanelMap.put(employee, employeePanel);
            }
            employeePanel.clearShiftAssignments();
        }
        unassignedPanel.clearShiftAssignments();
        for (ShiftAssignment shiftAssignment : nurseRoster.getShiftAssignmentList()) {
            Employee employee = shiftAssignment.getEmployee();
            EmployeePanel employeePanel = employeeToPanelMap.get(employee);
            employeePanel.addShiftAssignment(shiftAssignment);
        }
        for (Employee deadEmployee : deadEmployeeSet) {
            EmployeePanel deadEmployeePanel = employeeToPanelMap.remove(deadEmployee);
            employeeListPanel.remove(deadEmployeePanel);
        }
        for (EmployeePanel employeePanel : employeeToPanelMap.values()) {
            employeePanel.update();
        }
    }

    @Override
    public boolean isIndictmentHeatMapEnabled() {
        return true;
    }

    private void advancePlanningWindowStart() {
        logger.info("Advancing planningWindowStart.");
        if (solutionBusiness.isSolving()) {
            JOptionPane.showMessageDialog(this.getTopLevelAncestor(),
                    "The GUI does not support this action yet during solving.\nOptaPlanner itself does support it.\n"
                            + "\nTerminate solving first and try again.",
                    "Unsupported in GUI", JOptionPane.INFORMATION_MESSAGE);
            return;
        }
        doProblemChange((nurseRoster, problemChangeDirector) -> {
            NurseRosterParametrization nurseRosterParametrization = nurseRoster.getNurseRosterParametrization();
            List<ShiftDate> shiftDateList = nurseRoster.getShiftDateList();
            ShiftDate planningWindowStart = nurseRosterParametrization.getPlanningWindowStart();
            int windowStartIndex = shiftDateList.indexOf(planningWindowStart);
            if (windowStartIndex < 0) {
                throw new IllegalStateException("The planningWindowStart ("
                        + planningWindowStart + ") must be in the shiftDateList ("
                        + shiftDateList + ").");
            }
            ShiftDate oldLastShiftDate = shiftDateList.get(shiftDateList.size() - 1);
            ShiftDate newShiftDate = new ShiftDate(oldLastShiftDate.getId() + 1L,
                    oldLastShiftDate.getDayIndex() + 1, oldLastShiftDate.getDate().plusDays(1));
            List<Shift> refShiftList = planningWindowStart.getShiftList();
            List<Shift> newShiftList = new ArrayList<>(refShiftList.size());
            newShiftDate.setShiftList(newShiftList);
            problemChangeDirector.addProblemFact(newShiftDate, nurseRoster.getShiftDateList()::add);
            Shift oldLastShift = nurseRoster.getShiftList().get(nurseRoster.getShiftList().size() - 1);
            long shiftId = oldLastShift.getId() + 1L;
            int shiftIndex = oldLastShift.getIndex() + 1;
            long shiftAssignmentId = nurseRoster.getShiftAssignmentList().get(
                    nurseRoster.getShiftAssignmentList().size() - 1).getId() + 1L;
            for (Shift refShift : refShiftList) {
                Shift newShift = new Shift(shiftId, newShiftDate, refShift.getShiftType(), shiftIndex,
                        refShift.getRequiredEmployeeSize());
                shiftId++;
                shiftIndex++;
                newShiftList.add(newShift);
                problemChangeDirector.addProblemFact(newShift, nurseRoster.getShiftList()::add);
                for (int indexInShift = 0; indexInShift < newShift.getRequiredEmployeeSize(); indexInShift++) {
                    ShiftAssignment newShiftAssignment = new ShiftAssignment(shiftAssignmentId, newShift, indexInShift);
                    shiftAssignmentId++;
                    problemChangeDirector.addEntity(newShiftAssignment, nurseRoster.getShiftAssignmentList()::add);
                }
            }
            windowStartIndex++;
            ShiftDate newPlanningWindowStart = shiftDateList.get(windowStartIndex);
            problemChangeDirector.changeProblemProperty(nurseRosterParametrization,
                    workingNurseRosterParametrization -> {
                        workingNurseRosterParametrization.setPlanningWindowStart(newPlanningWindowStart);
                        workingNurseRosterParametrization.setLastShiftDate(newShiftDate);
                    });
        }, true);
    }

    public void deleteEmployee(final Employee employee) {
        logger.info("Scheduling delete of employee ({}).", employee);
        doProblemChange((nurseRoster, problemChangeDirector) -> problemChangeDirector.lookUpWorkingObject(employee)
                .ifPresentOrElse(workingEmployee -> {
                    // First remove the problem fact from all planning entities that use it
                    for (ShiftAssignment shiftAssignment : nurseRoster.getShiftAssignmentList()) {
                        if (shiftAssignment.getEmployee() == workingEmployee) {
                            problemChangeDirector.changeVariable(shiftAssignment, "employee",
                                    workingShiftAssignment -> workingShiftAssignment.setEmployee(null));
                        }
                    }
                    // A SolutionCloner does not clone problem fact lists (such as employeeList)
                    // Shallow clone the employeeList so only workingSolution is affected, not bestSolution or guiSolution
                    ArrayList<Employee> employeeList = new ArrayList<>(nurseRoster.getEmployeeList());
                    nurseRoster.setEmployeeList(employeeList);
                    // Remove it the problem fact itself
                    problemChangeDirector.removeProblemFact(workingEmployee, employeeList::remove);
                }, () -> logger.info("Skipping problem change due to employee ({}) already deleted.", employee)));
    }

    public void moveShiftAssignmentToEmployee(ShiftAssignment shiftAssignment, Employee toEmployee) {
        doProblemChange(
                (workingSolution, problemChangeDirector) -> problemChangeDirector.changeVariable(shiftAssignment, "employee",
                        sa -> sa.setEmployee(toEmployee)));
        solverAndPersistenceFrame.resetScreen();
    }

}
