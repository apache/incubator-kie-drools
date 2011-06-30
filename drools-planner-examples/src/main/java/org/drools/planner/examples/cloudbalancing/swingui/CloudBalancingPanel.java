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

import java.awt.BorderLayout;
import java.awt.Color;
import java.awt.GridLayout;
import java.awt.event.ActionEvent;
import java.util.ArrayList;
import java.util.LinkedHashMap;
import java.util.LinkedHashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;

import javax.swing.AbstractAction;
import javax.swing.JButton;
import javax.swing.JComboBox;
import javax.swing.JLabel;
import javax.swing.JOptionPane;
import javax.swing.JPanel;
import javax.swing.JTextField;

import org.apache.commons.lang.ObjectUtils;
import org.drools.planner.core.solution.Solution;
import org.drools.planner.examples.cloudbalancing.domain.CloudAssignment;
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

    private JPanel contentPanel;

    private CloudComputerPanel unassignedPanel;
    private Map<CloudComputer, CloudComputerPanel> cloudComputerToPanelMap;
    private Map<CloudAssignment, CloudComputerPanel> cloudAssignmentToPanelMap;

    public CloudBalancingPanel() {
        setLayout(new BorderLayout());
        addHeaderPanel();
        addContentPanel();
    }

    private void addHeaderPanel() {
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
        add(headerPanel, BorderLayout.NORTH);
    }

    private void addContentPanel() {
        contentPanel = new JPanel(new GridLayout(0, 1));
        unassignedPanel = new CloudComputerPanel(null);
        contentPanel.add(unassignedPanel);
        cloudComputerToPanelMap = new LinkedHashMap<CloudComputer, CloudComputerPanel>();
        cloudComputerToPanelMap.put(null, unassignedPanel);
        cloudAssignmentToPanelMap = new LinkedHashMap<CloudAssignment, CloudComputerPanel>();
        add(contentPanel, BorderLayout.SOUTH);
    }

    private CloudBalance getCloudBalance() {
        return (CloudBalance) solutionBusiness.getSolution();
    }

    public void resetPanel(Solution solution) {
        for (CloudComputerPanel cloudComputerCloudComputerPanel : cloudComputerToPanelMap.values()) {
            if (cloudComputerCloudComputerPanel.getCloudComputer() != null) {
                contentPanel.remove(cloudComputerCloudComputerPanel);
            }
        }
        cloudComputerToPanelMap.clear();
        cloudComputerToPanelMap.put(null, unassignedPanel);
        cloudAssignmentToPanelMap.clear();
        unassignedPanel.clearCloudAssignments();
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
                cloudComputerPanel = new CloudComputerPanel(cloudComputer);
                contentPanel.add(cloudComputerPanel);
                cloudComputerToPanelMap.put(cloudComputer, cloudComputerPanel);
            }
        }
        Set<CloudAssignment> deadCloudAssignmentSet = new LinkedHashSet<CloudAssignment>(
                cloudAssignmentToPanelMap.keySet());
        for (CloudAssignment cloudAssignment : cloudBalance.getCloudAssignmentList()) {
            deadCloudAssignmentSet.remove(cloudAssignment);
            CloudComputerPanel cloudComputerPanel = cloudAssignmentToPanelMap.get(cloudAssignment);
            CloudComputer cloudComputer = cloudAssignment.getCloudComputer();
            if (cloudComputerPanel != null
                    && !ObjectUtils.equals(cloudComputerPanel.getCloudComputer(), cloudComputer)) {
                cloudAssignmentToPanelMap.remove(cloudAssignment);
                cloudComputerPanel.removeCloudAssignment(cloudAssignment);
                cloudComputerPanel = null;
            }
            if (cloudComputerPanel == null) {
                cloudComputerPanel = cloudComputerToPanelMap.get(cloudComputer);
                cloudComputerPanel.addCloudAssignment(cloudAssignment);
                cloudAssignmentToPanelMap.put(cloudAssignment, cloudComputerPanel);
            }
        }
        for (CloudAssignment deadCloudAssignment : deadCloudAssignmentSet) {
            CloudComputerPanel deadCloudComputerPanel = cloudAssignmentToPanelMap.remove(deadCloudAssignment);
            deadCloudComputerPanel.removeCloudAssignment(deadCloudAssignment);
        }
        for (CloudComputer deadCloudComputer : deadCloudComputerSet) {
            CloudComputerPanel deadCloudComputerPanel = cloudComputerToPanelMap.remove(deadCloudComputer);
            contentPanel.remove(deadCloudComputerPanel);
        }
    }

    private class CloudAssignmentAction extends AbstractAction {

        private CloudAssignment cloudAssignment;

        public CloudAssignmentAction(CloudAssignment cloudAssignment) {
            super("=>");
            this.cloudAssignment = cloudAssignment;
        }

        public void actionPerformed(ActionEvent e) {
            List<CloudComputer> cloudComputerList = getCloudBalance().getCloudComputerList();
            JComboBox cloudComputerListField = new JComboBox(cloudComputerList.toArray());
            cloudComputerListField.setSelectedItem(cloudAssignment.getCloudComputer());
            int result = JOptionPane.showConfirmDialog(CloudBalancingPanel.this.getRootPane(), cloudComputerListField,
                    "Select cloud computer", JOptionPane.OK_CANCEL_OPTION);
            if (result == JOptionPane.OK_OPTION) {
                CloudComputer toCloudComputer = (CloudComputer) cloudComputerListField.getSelectedItem();
                solutionBusiness.doMove(new CloudComputerChangeMove(cloudAssignment, toCloudComputer));
                workflowFrame.updateScreen();
            }
        }

    }

}
