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

package org.drools.planner.examples.machinereassignment.swingui;

import java.awt.GridLayout;
import java.util.Collections;
import java.util.Iterator;
import java.util.LinkedHashMap;
import java.util.LinkedHashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;
import javax.swing.GroupLayout;
import javax.swing.JLabel;
import javax.swing.JPanel;

import org.apache.commons.lang.ObjectUtils;
import org.drools.planner.core.score.director.ScoreDirector;
import org.drools.planner.core.solution.Solution;
import org.drools.planner.core.solver.ProblemFactChange;
import org.drools.planner.examples.common.swingui.SolutionPanel;
import org.drools.planner.examples.machinereassignment.domain.MachineReassignment;
import org.drools.planner.examples.machinereassignment.domain.MrMachine;
import org.drools.planner.examples.machinereassignment.domain.MrProcessAssignment;
import org.drools.planner.examples.machinereassignment.domain.MrResource;

public class MachineReassignmentPanel extends SolutionPanel {

    public static final String LOGO_PATH
            = "/org/drools/planner/examples/machinereassignment/swingui/machineReassignmentLogo.png";

    private JPanel machineListPanel;

    private MrMachinePanel unassignedPanel;
    private JLabel tooBigLabel = null;
    private Map<MrMachine, MrMachinePanel> machineToPanelMap;
    private Map<MrProcessAssignment, MrMachinePanel> processAssignmentToPanelMap;

    public MachineReassignmentPanel() {
        GroupLayout layout = new GroupLayout(this);
        setLayout(layout);
        createMachineListPanel();
        JPanel headerPanel = new JPanel();
        layout.setHorizontalGroup(layout.createParallelGroup()
                .addComponent(headerPanel).addComponent(machineListPanel));
        layout.setVerticalGroup(layout.createSequentialGroup()
                .addComponent(headerPanel, GroupLayout.PREFERRED_SIZE, GroupLayout.PREFERRED_SIZE,
                        GroupLayout.PREFERRED_SIZE)
                .addComponent(machineListPanel, GroupLayout.PREFERRED_SIZE, GroupLayout.PREFERRED_SIZE,
                        GroupLayout.PREFERRED_SIZE));
    }

    private void createMachineListPanel() {
        machineListPanel = new JPanel(new GridLayout(0, 1));
        unassignedPanel = new MrMachinePanel(this, Collections.<MrResource>emptyList(), null);
        machineListPanel.add(unassignedPanel);
        machineToPanelMap = new LinkedHashMap<MrMachine, MrMachinePanel>();
        machineToPanelMap.put(null, unassignedPanel);
        processAssignmentToPanelMap = new LinkedHashMap<MrProcessAssignment, MrMachinePanel>();
    }

    private MachineReassignment getMachineReassignment() {
        return (MachineReassignment) solutionBusiness.getSolution();
    }

    public void resetPanel(Solution solution) {
        for (MrMachinePanel machinePanel : machineToPanelMap.values()) {
            if (machinePanel.getMachine() != null) {
                machineListPanel.remove(machinePanel);
            }
        }
        machineToPanelMap.clear();
        machineToPanelMap.put(null, unassignedPanel);
        processAssignmentToPanelMap.clear();
        unassignedPanel.clearMrProcessAssignments();
        updatePanel(solution);
    }

    @Override
    public void updatePanel(Solution solution) {
        MachineReassignment machineReassignment = (MachineReassignment) solution;
        List<MrResource> resourceList = machineReassignment.getResourceList();
        unassignedPanel.setResourceList(resourceList);
        if (machineReassignment.getMachineList().size() > 1000) {
            if (tooBigLabel == null) {
                tooBigLabel = new JLabel("The dataset is too big to show.");
                machineListPanel.add(tooBigLabel);
            }
        } else {
            if (tooBigLabel != null) {
                machineListPanel.remove(tooBigLabel);
                tooBigLabel = null;
            }
            Set<MrMachine> deadMachineSet = new LinkedHashSet<MrMachine>(machineToPanelMap.keySet());
            deadMachineSet.remove(null);
            for (MrMachine machine : machineReassignment.getMachineList()) {
                deadMachineSet.remove(machine);
                MrMachinePanel machinePanel = machineToPanelMap.get(machine);
                if (machinePanel == null) {
                    machinePanel = new MrMachinePanel(this, resourceList, machine);
                    machineListPanel.add(machinePanel);
                    machineToPanelMap.put(machine, machinePanel);
                }
            }
            Set<MrProcessAssignment> deadProcessAssignmentSet = new LinkedHashSet<MrProcessAssignment>(
                    processAssignmentToPanelMap.keySet());
            for (MrProcessAssignment processAssignment : machineReassignment.getProcessAssignmentList()) {
                deadProcessAssignmentSet.remove(processAssignment);
                MrMachinePanel machinePanel = processAssignmentToPanelMap.get(processAssignment);
                MrMachine machine = processAssignment.getMachine();
                if (machinePanel != null
                        && !ObjectUtils.equals(machinePanel.getMachine(), machine)) {
                    processAssignmentToPanelMap.remove(processAssignment);
                    machinePanel.removeMrProcessAssignment(processAssignment);
                    machinePanel = null;
                }
                if (machinePanel == null) {
                    machinePanel = machineToPanelMap.get(machine);
                    machinePanel.addMrProcessAssignment(processAssignment);
                    processAssignmentToPanelMap.put(processAssignment, machinePanel);
                }
            }
            for (MrProcessAssignment deadProcessAssignment : deadProcessAssignmentSet) {
                MrMachinePanel deadMachinePanel = processAssignmentToPanelMap.remove(deadProcessAssignment);
                deadMachinePanel.removeMrProcessAssignment(deadProcessAssignment);
            }
            for (MrMachine deadMachine : deadMachineSet) {
                MrMachinePanel deadMachinePanel = machineToPanelMap.remove(deadMachine);
                machineListPanel.remove(deadMachinePanel);
            }
            for (MrMachinePanel machinePanel : machineToPanelMap.values()) {
                machinePanel.update();
            }
        }
    }

    public void deleteMachine(final MrMachine machine) {
        logger.info("Scheduling delete of machine ({}).", machine);
        solutionBusiness.doProblemFactChange(new ProblemFactChange() {
            public void doChange(ScoreDirector scoreDirector) {
                MachineReassignment machineReassignment = (MachineReassignment) scoreDirector.getWorkingSolution();
                // First remove the planning fact from all planning entities that use it
                for (MrProcessAssignment processAssignment : machineReassignment.getProcessAssignmentList()) {
                    if (ObjectUtils.equals(processAssignment.getMachine(), machine)) {
                        // TODO HACK we are removing it because it becomes uninitialized,
                        // which means it has to be retracted
                        // This is nonsense from a ProblemFactChange point of view, FIXME!
                        scoreDirector.beforeEntityRemoved(processAssignment);
                        processAssignment.setMachine(null);
                        scoreDirector.afterEntityRemoved(processAssignment);
                    }
                }
                // Next remove it the planning fact itself
                for (Iterator<MrMachine> it = machineReassignment.getMachineList().iterator(); it.hasNext(); ) {
                    MrMachine workingMachine = it.next();
                    if (ObjectUtils.equals(workingMachine, machine)) {
                        scoreDirector.beforeProblemFactRemoved(workingMachine);
                        it.remove(); // remove from list
                        scoreDirector.beforeProblemFactRemoved(workingMachine);
                        break;
                    }
                }
            }
        });
        updatePanel(solutionBusiness.getSolution());
    }

//    private class MrProcessAssignmentAction extends AbstractAction {
//
//        private MrProcessAssignment processAssignment;
//
//        public MrProcessAssignmentAction(MrProcessAssignment processAssignment) {
//            super("=>");
//            this.processAssignment = processAssignment;
//        }
//
//        public void actionPerformed(ActionEvent e) {
//            List<MrMachine> machineList = getMachineReassignment().getMachineList();
//            JComboBox machineListField = new JComboBox(machineList.toArray());
//            machineListField.setSelectedItem(processAssignment.getMachine());
//            int result = JOptionPane.showConfirmDialog(MachineReassignmentPanel.this.getRootPane(), machineListField,
//                    "Select machine", JOptionPane.OK_CANCEL_OPTION);
//            if (result == JOptionPane.OK_OPTION) {
//                MrMachine toMrMachine = (MrMachine) machineListField.getSelectedItem();
//                solutionBusiness.doMove(new MrMachineChangeMove(processAssignment, toMrMachine));
//                solverAndPersistenceFrame.resetScreen();
//            }
//        }
//
//    }

}
