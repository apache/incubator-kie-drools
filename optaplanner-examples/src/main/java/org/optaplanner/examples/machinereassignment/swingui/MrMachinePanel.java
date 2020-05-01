/*
 * Copyright 2020 Red Hat, Inc. and/or its affiliates.
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

import java.awt.BorderLayout;
import java.awt.Color;
import java.awt.Dimension;
import java.awt.GridLayout;
import java.awt.event.ActionEvent;
import java.util.ArrayList;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;

import javax.swing.AbstractAction;
import javax.swing.BorderFactory;
import javax.swing.GroupLayout;
import javax.swing.JButton;
import javax.swing.JDialog;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.JTextField;

import org.optaplanner.examples.machinereassignment.domain.MrMachine;
import org.optaplanner.examples.machinereassignment.domain.MrMachineCapacity;
import org.optaplanner.examples.machinereassignment.domain.MrProcessAssignment;
import org.optaplanner.examples.machinereassignment.domain.MrResource;
import org.optaplanner.swing.impl.SwingUtils;
import org.optaplanner.swing.impl.TangoColorFactory;

public class MrMachinePanel extends JPanel {

    private final MachineReassignmentPanel machineReassignmentPanel;
    private List<MrResource> resourceList;
    private MrMachine machine;
    private List<MrProcessAssignment> processAssignmentList = new ArrayList<>();

    private JLabel machineLabel;
    private JPanel resourceListPanel = null;
    private Map<MrResource, JTextField> resourceFieldMap;
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
        createUI();
    }

    public MrMachine getMachine() {
        return machine;
    }

    private String getMachineLabel() {
        return machine == null ? "Unassigned" : machine.getLabel();
    }

    public void setResourceList(List<MrResource> resourceList) {
        this.resourceList = resourceList;
        resetResourceListPanel();
    }

    private void createUI() {
        JPanel labelAndDeletePanel = new JPanel(new BorderLayout());
        labelAndDeletePanel.setPreferredSize(new Dimension(150, 20));
        machineLabel = new JLabel(getMachineLabel());
        machineLabel.setEnabled(false);
        labelAndDeletePanel.add(machineLabel, BorderLayout.CENTER);
        if (machine != null) {
            JButton deleteButton = SwingUtils.makeSmallButton(new JButton("X"));
            deleteButton.setToolTipText("Delete");
            deleteButton.addActionListener(e -> machineReassignmentPanel.deleteMachine(machine));
            deleteButton.setToolTipText("Delete");
            labelAndDeletePanel.add(deleteButton, BorderLayout.EAST);
        }
        add(labelAndDeletePanel, BorderLayout.WEST);
        resetResourceListPanel();
        JPanel numberAndDetailsPanel = new JPanel(new BorderLayout());
        numberOfProcessesLabel = new JLabel("0 processes ", JLabel.RIGHT);
        numberOfProcessesLabel.setPreferredSize(new Dimension(100, 20));
        numberOfProcessesLabel.setEnabled(false);
        numberAndDetailsPanel.add(numberOfProcessesLabel, BorderLayout.WEST);
        detailsButton = new JButton(new AbstractAction("Details") {
            @Override
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

    public void resetResourceListPanel() {
        if (resourceListPanel != null) {
            remove(resourceListPanel);
        }
        resourceListPanel = new JPanel(new GridLayout(1, resourceList.size()));
        resourceFieldMap = new LinkedHashMap<>(resourceList.size());
        for (MrResource resource : resourceList) {
            long maximumCapacity = machine == null ? 0L : machine.getMachineCapacity(resource).getMaximumCapacity();
            JTextField resourceField = new JTextField("0 / " + maximumCapacity);
            resourceFieldMap.put(resource, resourceField);
            resourceField.setEditable(false);
            resourceField.setEnabled(false);
            resourceListPanel.add(resourceField);
        }
        add(resourceListPanel, BorderLayout.CENTER);
    }

    public void addProcessAssignment(MrProcessAssignment processAssignment) {
        processAssignmentList.add(processAssignment);
    }

    public void removeProcessAssignment(MrProcessAssignment processAssignment) {
        processAssignmentList.remove(processAssignment);
    }

    public void clearProcessAssignments() {
        processAssignmentList.clear();
    }

    public void update() {
        updateTotals();
    }

    private void updateTotals() {
        boolean used = processAssignmentList.size() > 0;
        machineLabel.setEnabled(used);
        for (MrResource resource : resourceList) {
            JTextField resourceField = resourceFieldMap.get(resource);
            long maximumCapacity;
            long safetyCapacity;
            if (machine != null) {
                MrMachineCapacity machineCapacity = machine.getMachineCapacity(resource);
                maximumCapacity = machineCapacity.getMaximumCapacity();
                safetyCapacity = machineCapacity.getSafetyCapacity();
            } else {
                maximumCapacity = 0L;
                safetyCapacity = 0L;
            }
            long usedTotal = 0L;
            for (MrProcessAssignment processAssignment : processAssignmentList) {
                usedTotal += processAssignment.getProcess().getProcessRequirement(resource).getUsage();
            }
            resourceField.setText(usedTotal + " / " + maximumCapacity);
            resourceField.setForeground(usedTotal > maximumCapacity ? TangoColorFactory.SCARLET_3
                    : (usedTotal > safetyCapacity ? TangoColorFactory.ORANGE_3 : Color.BLACK));
            resourceField.setEnabled(used);
        }
        numberOfProcessesLabel.setText(processAssignmentList.size() + " processes ");
        numberOfProcessesLabel.setEnabled(used);
    }

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
                processAssignmentLabel.setForeground(TangoColorFactory.SEQUENCE_1.get(colorIndex));
                assignmentsPanel.add(processAssignmentLabel);

                for (MrResource resource : resourceList) {
                    long usage = processAssignment.getProcess().getProcessRequirement(resource).getUsage();
                    JTextField resourceField = new JTextField(Long.toString(usage));
                    resourceField.setEditable(false);
                    assignmentsPanel.add(resourceField);
                }

                colorIndex = (colorIndex + 1) % TangoColorFactory.SEQUENCE_1.size();
            }
            return assignmentsPanel;
        }

    }

}
