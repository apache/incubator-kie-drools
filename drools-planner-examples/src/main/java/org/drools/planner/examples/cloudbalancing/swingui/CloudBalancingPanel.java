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

package org.drools.planner.examples.cloudbalancing.swingui;

import java.awt.Color;
import java.awt.GridLayout;
import java.awt.event.ActionEvent;
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

import org.apache.commons.lang.ObjectUtils;
import org.drools.FactHandle;
import org.drools.WorkingMemory;
import org.drools.planner.core.solution.Solution;
import org.drools.planner.core.solution.director.SolutionDirector;
import org.drools.planner.core.solver.ProblemFactChange;
import org.drools.planner.examples.cloudbalancing.domain.CloudProcessAssignment;
import org.drools.planner.examples.cloudbalancing.domain.CloudBalance;
import org.drools.planner.examples.cloudbalancing.domain.CloudComputer;
import org.drools.planner.examples.cloudbalancing.solver.move.CloudComputerChangeMove;
import org.drools.planner.examples.common.swingui.SolutionPanel;

/**
 * TODO this code is highly unoptimized
 */
public class CloudBalancingPanel extends SolutionPanel {

    public static final Color[] PROCESS_COLORS = {
            Color.GREEN, Color.YELLOW, Color.BLUE, Color.RED, Color.CYAN, Color.ORANGE, Color.MAGENTA
    };

    private JPanel computersPanel;

    private CloudComputerPanel unassignedPanel;
    private Map<CloudComputer, CloudComputerPanel> cloudComputerToPanelMap;
    private Map<CloudProcessAssignment, CloudComputerPanel> cloudProcessAssignmentToPanelMap;

    public CloudBalancingPanel() {
        GroupLayout layout = new GroupLayout(this);
        setLayout(layout);
        JPanel headerPanel = createHeaderPanel();
        JPanel computersPanel = createComputersPanel();
        layout.setHorizontalGroup(layout.createParallelGroup()
                .addComponent(headerPanel).addComponent(computersPanel));
        layout.setVerticalGroup(layout.createSequentialGroup()
                .addComponent(headerPanel, GroupLayout.PREFERRED_SIZE, GroupLayout.PREFERRED_SIZE,
                        GroupLayout.PREFERRED_SIZE)
                .addComponent(computersPanel, GroupLayout.PREFERRED_SIZE, GroupLayout.PREFERRED_SIZE,
                        GroupLayout.PREFERRED_SIZE));
    }

    private JPanel createHeaderPanel() {
        JPanel headerPanel = new JPanel(new GridLayout(0, 5));
        JLabel emptyLabel = new JLabel("");
        headerPanel.add(emptyLabel);
        JLabel cpuPowerLabel = new JLabel("CPU power");
        headerPanel.add(cpuPowerLabel);
        JLabel memoryLabel = new JLabel("Memory");
        headerPanel.add(memoryLabel);
        JLabel networkBandwidthLabel = new JLabel("Network bandwidth");
        headerPanel.add(networkBandwidthLabel);
        JLabel costLabel = new JLabel("Cost");
        headerPanel.add(costLabel);
        return headerPanel;
    }

    private JPanel createComputersPanel() {
        computersPanel = new JPanel(new GridLayout(0, 1));
        unassignedPanel = new CloudComputerPanel(this, null);
        computersPanel.add(unassignedPanel);
        cloudComputerToPanelMap = new LinkedHashMap<CloudComputer, CloudComputerPanel>();
        cloudComputerToPanelMap.put(null, unassignedPanel);
        cloudProcessAssignmentToPanelMap = new LinkedHashMap<CloudProcessAssignment, CloudComputerPanel>();
        return computersPanel;
    }

    private CloudBalance getCloudBalance() {
        return (CloudBalance) solutionBusiness.getSolution();
    }

    public void resetPanel(Solution solution) {
        for (CloudComputerPanel cloudComputerCloudComputerPanel : cloudComputerToPanelMap.values()) {
            if (cloudComputerCloudComputerPanel.getCloudComputer() != null) {
                computersPanel.remove(cloudComputerCloudComputerPanel);
            }
        }
        cloudComputerToPanelMap.clear();
        cloudComputerToPanelMap.put(null, unassignedPanel);
        cloudProcessAssignmentToPanelMap.clear();
        unassignedPanel.clearCloudProcessAssignments();
        updatePanel(solution);
    }

    @Override
    public void updatePanel(Solution solution) {
        CloudBalance cloudBalance = (CloudBalance) solution;
        Set<CloudComputer> deadCloudComputerSet = new LinkedHashSet<CloudComputer>(cloudComputerToPanelMap.keySet());
        deadCloudComputerSet.remove(null);
        for (CloudComputer cloudComputer : ((CloudBalance) solution).getCloudComputerList()) {
            deadCloudComputerSet.remove(cloudComputer);
            CloudComputerPanel cloudComputerPanel = cloudComputerToPanelMap.get(cloudComputer);
            if (cloudComputerPanel == null) {
                cloudComputerPanel = new CloudComputerPanel(this, cloudComputer);
                computersPanel.add(cloudComputerPanel);
                cloudComputerToPanelMap.put(cloudComputer, cloudComputerPanel);
            }
        }
        Set<CloudProcessAssignment> deadCloudProcessAssignmentSet = new LinkedHashSet<CloudProcessAssignment>(
                cloudProcessAssignmentToPanelMap.keySet());
        for (CloudProcessAssignment cloudProcessAssignment : cloudBalance.getCloudProcessAssignmentList()) {
            deadCloudProcessAssignmentSet.remove(cloudProcessAssignment);
            CloudComputerPanel cloudComputerPanel = cloudProcessAssignmentToPanelMap.get(cloudProcessAssignment);
            CloudComputer cloudComputer = cloudProcessAssignment.getCloudComputer();
            if (cloudComputerPanel != null
                    && !ObjectUtils.equals(cloudComputerPanel.getCloudComputer(), cloudComputer)) {
                cloudProcessAssignmentToPanelMap.remove(cloudProcessAssignment);
                cloudComputerPanel.removeCloudProcessAssignment(cloudProcessAssignment);
                cloudComputerPanel = null;
            }
            if (cloudComputerPanel == null) {
                cloudComputerPanel = cloudComputerToPanelMap.get(cloudComputer);
                cloudComputerPanel.addCloudProcessAssignment(cloudProcessAssignment);
                cloudProcessAssignmentToPanelMap.put(cloudProcessAssignment, cloudComputerPanel);
            }
        }
        for (CloudProcessAssignment deadCloudProcessAssignment : deadCloudProcessAssignmentSet) {
            CloudComputerPanel deadCloudComputerPanel = cloudProcessAssignmentToPanelMap.remove(deadCloudProcessAssignment);
            deadCloudComputerPanel.removeCloudProcessAssignment(deadCloudProcessAssignment);
        }
        for (CloudComputer deadCloudComputer : deadCloudComputerSet) {
            CloudComputerPanel deadCloudComputerPanel = cloudComputerToPanelMap.remove(deadCloudComputer);
            computersPanel.remove(deadCloudComputerPanel);
        }
    }

    public void deleteComputer(final CloudComputer cloudComputer) {
        logger.info("Scheduling deleting of computer ({}).", cloudComputer.getLabel());
        solutionBusiness.doProblemFactChange(new ProblemFactChange() {
            public void doChange(SolutionDirector solutionDirector) {
                CloudBalance cloudBalance = (CloudBalance) solutionDirector.getWorkingSolution();
                WorkingMemory workingMemory = solutionDirector.getWorkingMemory();
                // First remove the planning fact from all planning entities that use it
                for (CloudProcessAssignment cloudProcessAssignment : cloudBalance.getCloudProcessAssignmentList()) {
                    if (ObjectUtils.equals(cloudProcessAssignment.getCloudComputer(), cloudComputer)) {
                        FactHandle cloudProcessAssignmentHandle = workingMemory.getFactHandle(cloudProcessAssignment);
                        cloudProcessAssignment.setCloudComputer(null);
                        workingMemory.retract(cloudProcessAssignmentHandle);
                    }
                }
                // Next remove it the planning fact itself
                for (Iterator<CloudComputer> it = cloudBalance.getCloudComputerList().iterator(); it.hasNext(); ) {
                    CloudComputer workingCloudComputer = it.next();
                    if (ObjectUtils.equals(workingCloudComputer, cloudComputer)) {
                        FactHandle cloudComputerHandle = workingMemory.getFactHandle(workingCloudComputer);
                        workingMemory.retract(cloudComputerHandle);
                        it.remove(); // remove from list
                        break;
                    }
                }
            }
        });
        updatePanel(solutionBusiness.getSolution());
    }

    private class CloudProcessAssignmentAction extends AbstractAction {

        private CloudProcessAssignment cloudProcessAssignment;

        public CloudProcessAssignmentAction(CloudProcessAssignment cloudProcessAssignment) {
            super("=>");
            this.cloudProcessAssignment = cloudProcessAssignment;
        }

        public void actionPerformed(ActionEvent e) {
            List<CloudComputer> cloudComputerList = getCloudBalance().getCloudComputerList();
            JComboBox cloudComputerListField = new JComboBox(cloudComputerList.toArray());
            cloudComputerListField.setSelectedItem(cloudProcessAssignment.getCloudComputer());
            int result = JOptionPane.showConfirmDialog(CloudBalancingPanel.this.getRootPane(), cloudComputerListField,
                    "Select cloud computer", JOptionPane.OK_CANCEL_OPTION);
            if (result == JOptionPane.OK_OPTION) {
                CloudComputer toCloudComputer = (CloudComputer) cloudComputerListField.getSelectedItem();
                solutionBusiness.doMove(new CloudComputerChangeMove(cloudProcessAssignment, toCloudComputer));
                solverAndPersistenceFrame.resetScreen();
            }
        }

    }

}
