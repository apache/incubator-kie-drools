/*
 * Copyright 2011 Red Hat, Inc. and/or its affiliates.
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

package org.optaplanner.examples.machinereassignment.swingui;

import java.awt.GridLayout;
import java.awt.event.ActionEvent;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Iterator;
import java.util.LinkedHashMap;
import java.util.LinkedHashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;
import javax.swing.AbstractAction;
import javax.swing.GroupLayout;
import javax.swing.JComboBox;
import javax.swing.JLabel;
import javax.swing.JOptionPane;
import javax.swing.JPanel;

import org.apache.commons.lang3.ObjectUtils;
import org.optaplanner.core.api.domain.solution.Solution;
import org.optaplanner.core.impl.score.director.ScoreDirector;
import org.optaplanner.core.impl.solver.ProblemFactChange;
import org.optaplanner.examples.common.swingui.SolutionPanel;
import org.optaplanner.examples.common.swingui.components.LabeledComboBoxRenderer;
import org.optaplanner.examples.machinereassignment.domain.MachineReassignment;
import org.optaplanner.examples.machinereassignment.domain.MrMachine;
import org.optaplanner.examples.machinereassignment.domain.MrProcessAssignment;
import org.optaplanner.examples.machinereassignment.domain.MrResource;

public class MachineReassignmentPanel extends SolutionPanel {

    public static final String LOGO_PATH
            = "/org/optaplanner/examples/machinereassignment/swingui/machineReassignmentLogo.png";

    private JPanel machineListPanel;

    private MrMachinePanel unassignedPanel;
    private JLabel tooBigLabel = null;
    private Map<MrMachine, MrMachinePanel> machineToPanelMap;

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
        unassignedPanel.clearProcessAssignments();
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
                machinePanel.clearProcessAssignments();
            }
            unassignedPanel.clearProcessAssignments();
            for (MrProcessAssignment processAssignment : machineReassignment.getProcessAssignmentList()) {
                MrMachine machine = processAssignment.getMachine();
                MrMachinePanel machinePanel = machineToPanelMap.get(machine);
                machinePanel.addProcessAssignment(processAssignment);
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
        doProblemFactChange(new ProblemFactChange() {
            public void doChange(ScoreDirector scoreDirector) {
                MachineReassignment machineReassignment = (MachineReassignment) scoreDirector.getWorkingSolution();
                // First remove the problem fact from all planning entities that use it
                for (MrProcessAssignment processAssignment : machineReassignment.getProcessAssignmentList()) {
                    if (ObjectUtils.equals(processAssignment.getOriginalMachine(), machine)) {
                        scoreDirector.beforeProblemFactChanged(processAssignment);
                        processAssignment.setOriginalMachine(null);
                        scoreDirector.afterProblemFactChanged(processAssignment);
                    }
                    if (ObjectUtils.equals(processAssignment.getMachine(), machine)) {
                        scoreDirector.beforeVariableChanged(processAssignment, "machine");
                        processAssignment.setMachine(null);
                        scoreDirector.afterVariableChanged(processAssignment, "machine");
                    }
                }
                scoreDirector.triggerVariableListeners();
                // A SolutionCloner does not clone problem fact lists (such as machineList)
                // Shallow clone the machineList so only workingSolution is affected, not bestSolution or guiSolution
                machineReassignment.setMachineList(new ArrayList<MrMachine>(machineReassignment.getMachineList()));
                // Remove it the problem fact itself
                for (Iterator<MrMachine> it = machineReassignment.getMachineList().iterator(); it.hasNext(); ) {
                    MrMachine workingMachine = it.next();
                    if (ObjectUtils.equals(workingMachine, machine)) {
                        scoreDirector.beforeProblemFactRemoved(workingMachine);
                        it.remove(); // remove from list
                        scoreDirector.afterProblemFactRemoved(workingMachine);
                        break;
                    }
                }
            }
        });
    }

    private class MrProcessAssignmentAction extends AbstractAction {

        private MrProcessAssignment processAssignment;

        public MrProcessAssignmentAction(MrProcessAssignment processAssignment) {
            super(processAssignment.getLabel());
            this.processAssignment = processAssignment;
        }

        public void actionPerformed(ActionEvent e) {
            List<MrMachine> machineList = getMachineReassignment().getMachineList();
            // Add 1 to array size to add null, which makes the entity unassigned
            JComboBox machineListField = new JComboBox(
                    machineList.toArray(new Object[machineList.size() + 1]));
            LabeledComboBoxRenderer.applyToComboBox(machineListField);
            machineListField.setSelectedItem(processAssignment.getMachine());
            int result = JOptionPane.showConfirmDialog(MachineReassignmentPanel.this.getRootPane(), machineListField,
                    "Select machine", JOptionPane.OK_CANCEL_OPTION);
            if (result == JOptionPane.OK_OPTION) {
                MrMachine toMrMachine = (MrMachine) machineListField.getSelectedItem();
                solutionBusiness.doChangeMove(processAssignment, "machine", toMrMachine);
                solverAndPersistenceFrame.resetScreen();
            }
        }

    }

}
