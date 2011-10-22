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

import java.awt.BorderLayout;
import java.awt.Color;
import java.awt.Dimension;
import java.awt.GridLayout;
import java.awt.event.ActionEvent;
import java.util.ArrayList;
import java.util.List;
import javax.swing.AbstractAction;
import javax.swing.BorderFactory;
import javax.swing.GroupLayout;
import javax.swing.JButton;
import javax.swing.JDialog;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.JTextField;

import org.drools.planner.examples.machinereassignment.domain.MrMachine;
import org.drools.planner.examples.machinereassignment.domain.MrProcessAssignment;
import org.drools.planner.examples.machinereassignment.domain.MrResource;

public class MrMachinePanel extends JPanel {

    private final MachineReassignmentPanel machineReassignmentPanel;
    private final List<MrResource> resourceList;
    private MrMachine machine;
    private List<MrProcessAssignment> processAssignmentList = new ArrayList<MrProcessAssignment>();

    private JLabel machineLabel;
    private JButton deleteButton;
    private List<JTextField> resourceFieldList;
    private JLabel numberOfProcessesLabel;
    private JButton detailsButton;

    public MrMachinePanel(MachineReassignmentPanel machineReassignmentPanel, List<MrResource> resourceList,
            MrMachine machine) {
        super(new BorderLayout());
        this.machineReassignmentPanel = machineReassignmentPanel;
        this.resourceList = resourceList;
        this.machine = machine;
        setBorder(BorderFactory.createCompoundBorder(
                BorderFactory.createCompoundBorder(
                        BorderFactory.createEmptyBorder(1, 2, 1, 2),
                        BorderFactory.createLineBorder(Color.BLACK)),
                BorderFactory.createEmptyBorder(1, 1, 1, 1)));
        addTotals();
    }

    public MrMachine getMachine() {
        return machine;
    }

    private String getMachineLabel() {
        return machine == null ? "Unassigned" : machine.getLabel();
    }

    private void addTotals() {
        JPanel labelAndDeletePanel = new JPanel(new BorderLayout());
        machineLabel = new JLabel(getMachineLabel());
        machineLabel.setEnabled(false);
        labelAndDeletePanel.add(machineLabel, BorderLayout.CENTER);
        if (machine != null) {
            deleteButton = new JButton(new AbstractAction("X") {
                public void actionPerformed(ActionEvent e) {
                    machineReassignmentPanel.deleteMachine(machine);
                }
            });
            labelAndDeletePanel.add(deleteButton, BorderLayout.EAST);
        }
        add(labelAndDeletePanel, BorderLayout.WEST);
        JPanel resourceListPanel = new JPanel(new GridLayout(1, resourceList.size()));
        resourceFieldList = new ArrayList<JTextField>(resourceList.size());
        for (MrResource resource : resourceList) {
            JTextField resourceField  = new JTextField("0 / " + "TODO"); // TODO
            resourceFieldList.add(resourceField);
            resourceField.setEditable(false);
            resourceField.setEnabled(false);
            resourceListPanel.add(resourceField);
        }
        add(resourceListPanel, BorderLayout.CENTER);
        JPanel numberAndDetailsPanel = new JPanel(new BorderLayout());
        numberOfProcessesLabel = new JLabel("    0 processes");
        numberOfProcessesLabel.setEnabled(false);
        numberAndDetailsPanel.add(numberOfProcessesLabel, BorderLayout.WEST);
        detailsButton = new JButton(new AbstractAction("Details") {
            public void actionPerformed(ActionEvent e) {
                MrProcessAssignmentListDialog processAssignmentListDialog = new MrProcessAssignmentListDialog();
                processAssignmentListDialog.setLocationRelativeTo(getRootPane());
                processAssignmentListDialog.setVisible(true);
            }
        });
        detailsButton.setEnabled(false);
        numberAndDetailsPanel.add(detailsButton, BorderLayout.CENTER);
        add(numberAndDetailsPanel, BorderLayout.EAST);
    }
    public void addMrProcessAssignment(MrProcessAssignment processAssignment) {
        processAssignmentList.add(processAssignment);
    }

    public void removeMrProcessAssignment(MrProcessAssignment processAssignment) {
        processAssignmentList.remove(processAssignment);
    }

    public void clearMrProcessAssignments() {
        processAssignmentList.clear();
    }

    public void update() {
//        int usedCpuPower = 0;
//        cpuPowerBar.clearProcessValues();
//        int usedMemory = 0;
//        memoryBar.clearProcessValues();
//        int usedNetworkBandwidth = 0;
//        networkBandwidthBar.clearProcessValues();
//        int colorIndex = 0;
//        for (MrProcessAssignment processAssignment : processAssignmentList) {
//            usedCpuPower += processAssignment.getRequiredCpuPower();
//            cpuPowerBar.addProcessValue(processAssignment.getRequiredCpuPower());
//            usedMemory += processAssignment.getRequiredMemory();
//            memoryBar.addProcessValue(processAssignment.getRequiredMemory());
//            usedNetworkBandwidth += processAssignment.getRequiredNetworkBandwidth();
//            networkBandwidthBar.addProcessValue(processAssignment.getRequiredNetworkBandwidth());
//            colorIndex = (colorIndex + 1) % CloudBalancingPanel.PROCESS_COLORS.length;
//        }
//        boolean used = processAssignmentList.size() > 0;
//        updateTotals(usedCpuPower, usedMemory, usedNetworkBandwidth, used);
    }

//    private void updateTotals(int usedCpuPower, int usedMemory, int usedNetworkBandwidth, boolean used) {
//        machineLabel.setEnabled(used);
//        cpuPowerField.setText(usedCpuPower + " GHz / " + getComputerCpuPower() + " GHz");
//        cpuPowerField.setForeground(usedCpuPower > getComputerCpuPower() ? Color.RED : Color.BLACK);
//        cpuPowerField.setEnabled(used);
//        memoryField.setText(usedMemory + " GB / " + getComputerMemory() + " GB");
//        memoryField.setForeground(usedMemory > getComputerMemory() ? Color.RED : Color.BLACK);
//        memoryField.setEnabled(used);
//        networkBandwidthField.setText(usedNetworkBandwidth + " GB / " + getComputerNetworkBandwidth() + " GB");
//        networkBandwidthField.setForeground(usedNetworkBandwidth > getComputerNetworkBandwidth()
//                ? Color.RED : Color.BLACK);
//        networkBandwidthField.setEnabled(used);
//        costField.setEnabled(used);
//    }

    private class MrProcessAssignmentListDialog extends JDialog {

        public MrProcessAssignmentListDialog() {
            setModal(true);
            setTitle(getMachineLabel());
            JPanel contentPanel = new JPanel();
            GroupLayout layout = new GroupLayout(contentPanel);
            contentPanel.setLayout(layout);
            JPanel assignmentsPanel = createAssignmentsPanel();
            JScrollPane contentScrollPane = new JScrollPane(assignmentsPanel);
            contentScrollPane.setPreferredSize(new Dimension(800, 200));
            contentScrollPane.getVerticalScrollBar().setUnitIncrement(20);
            setContentPane(contentScrollPane);
            pack();
        }

        private JPanel createAssignmentsPanel() {
            JPanel assignmentsPanel = new JPanel(new GridLayout(0, resourceList.size()));
            int colorIndex = 0;
            for (MrProcessAssignment processAssignment : processAssignmentList) {
                JLabel processAssignmentLabel = new JLabel(processAssignment.getLabel());
                processAssignmentLabel.setForeground(MachineReassignmentPanel.PROCESS_COLORS[colorIndex]);
                assignmentsPanel.add(processAssignmentLabel);

                for (MrResource resource : resourceList) {
                    JTextField resourceField = new JTextField("TODO"); // TODO
                    resourceField.setEditable(false);
                    assignmentsPanel.add(resourceField);
                }

                colorIndex = (colorIndex + 1) % MachineReassignmentPanel.PROCESS_COLORS.length;
            }
            return assignmentsPanel;
        }

    }
    
}
