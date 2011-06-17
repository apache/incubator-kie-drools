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
import java.awt.Dimension;
import java.awt.FlowLayout;
import java.awt.Graphics;
import java.awt.GridLayout;
import java.awt.event.ActionEvent;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;

import javax.swing.AbstractAction;
import javax.swing.BorderFactory;
import javax.swing.BoxLayout;
import javax.swing.JButton;
import javax.swing.JComboBox;
import javax.swing.JLabel;
import javax.swing.JOptionPane;
import javax.swing.JPanel;
import javax.swing.JTextArea;
import javax.swing.JTextField;

import org.drools.planner.examples.cloudbalancing.domain.CloudAssignment;
import org.drools.planner.examples.cloudbalancing.domain.CloudBalance;
import org.drools.planner.examples.cloudbalancing.domain.CloudComputer;
import org.drools.planner.examples.cloudbalancing.domain.CloudProcess;
import org.drools.planner.examples.cloudbalancing.solver.move.CloudComputerChangeMove;
import org.drools.planner.examples.common.swingui.SolutionPanel;

/**
 * TODO this code is highly unoptimized
 */
public class CloudBalancingPanel extends SolutionPanel {

    private static final Color[] PROCESS_COLORS = {
            Color.GREEN, Color.YELLOW, Color.BLUE, Color.RED, Color.CYAN, Color.ORANGE, Color.MAGENTA
    };

    public CloudBalancingPanel() {
        setLayout(new GridLayout(0, 5));
    }

    private CloudBalance getCloudBalance() {
        return (CloudBalance) solutionBusiness.getSolution();
    }

    public void resetPanel() {
        removeAll();
        CloudBalance cloudBalance = getCloudBalance();
        addHeaders();

        List<CloudComputer> cloudComputerList = cloudBalance.getCloudComputerList();
        Map<CloudComputer, List<CloudAssignment>> computerToAssignmentMap
                = new LinkedHashMap<CloudComputer, List<CloudAssignment>>(cloudComputerList.size());
        List<CloudAssignment> unassignedCloudAssignmentList = new ArrayList<CloudAssignment>();
        for (CloudComputer cloudComputer : cloudComputerList) {
            List<CloudAssignment> cloudAssignmentList = new ArrayList<CloudAssignment>();
            computerToAssignmentMap.put(cloudComputer, cloudAssignmentList);
        }
        for (CloudAssignment cloudAssignment : cloudBalance.getCloudAssignmentList()) {
            CloudComputer cloudComputer = cloudAssignment.getCloudComputer();
            if (cloudComputer != null) {
                List<CloudAssignment> cloudAssignmentList = computerToAssignmentMap.get(cloudComputer);
                cloudAssignmentList.add(cloudAssignment);
            } else {
                unassignedCloudAssignmentList.add(cloudAssignment);
            }
        }
        for (Map.Entry<CloudComputer, List<CloudAssignment>> entry : computerToAssignmentMap.entrySet()) {
            addCloudComputer(entry.getKey(), entry.getValue());
        }
        addUnassignedCloudAssignmentList(unassignedCloudAssignmentList);
    }

    private void addHeaders() {
        JLabel emptyLabel = new JLabel("");
        add(emptyLabel);
        JLabel cpuPowerLabel = new JLabel("CPU power");
        add(cpuPowerLabel);
        JLabel memoryLabel = new JLabel("Memory");
        add(memoryLabel);
        JLabel networkBandwidthLabel = new JLabel("Network bandwidth");
        add(networkBandwidthLabel);
        JLabel costLabel = new JLabel("Cost");
        add(costLabel);
    }

    private void addCloudComputer(CloudComputer cloudComputer, List<CloudAssignment> cloudAssignmentList) {
        boolean costUsed = cloudAssignmentList.size() > 0;
        addComputer(cloudComputer, costUsed);

        int usedCpuPower = 0;
        CloudBar cpuPowerBar = new CloudBar(cloudComputer.getCpuPower());
        int usedMemory = 0;
        CloudBar memoryBar = new CloudBar(cloudComputer.getMemory());
        int usedNetworkBandwidth = 0;
        CloudBar networkBandwidthBar = new CloudBar(cloudComputer.getNetworkBandwidth());
        int colorIndex = 0;
        for (CloudAssignment cloudAssignment : cloudAssignmentList) {
            addCloudAssignment(cloudAssignment, PROCESS_COLORS[colorIndex]);
            usedCpuPower += cloudAssignment.getMinimalCpuPower();
            cpuPowerBar.addProcessValue(cloudAssignment.getMinimalCpuPower());
            usedMemory += cloudAssignment.getMinimalMemory();
            memoryBar.addProcessValue(cloudAssignment.getMinimalMemory());
            usedNetworkBandwidth += cloudAssignment.getMinimalNetworkBandwidth();
            networkBandwidthBar.addProcessValue(cloudAssignment.getMinimalNetworkBandwidth());
            colorIndex = (colorIndex + 1) % PROCESS_COLORS.length;
        }
        addTotals(usedCpuPower, usedMemory, usedNetworkBandwidth);
        addBars(cpuPowerBar, memoryBar, networkBandwidthBar);
    }

    private void addComputer(CloudComputer cloudComputer, boolean costUsed) {
        JLabel computerLabel = new JLabel(cloudComputer.getLabel());
        add(computerLabel);
        JTextField cpuPowerField = new JTextField(cloudComputer.getCpuPower() + " GHz");
        cpuPowerField.setEditable(false);
        add(cpuPowerField);
        JTextField memoryField = new JTextField(cloudComputer.getMemory() + " GB");
        memoryField.setEditable(false);
        add(memoryField);
        JTextField networkBandwidthField = new JTextField(cloudComputer.getNetworkBandwidth() + " GB");
        networkBandwidthField.setEditable(false);
        add(networkBandwidthField);
        JTextField costField = new JTextField(cloudComputer.getCost() + " $");
        costField.setEditable(false);
        costField.setEnabled(costUsed);
        add(costField);
    }

    private void addUnassignedCloudAssignmentList(List<CloudAssignment> unassignedCloudAssignmentList) {
        addUnassignedHeaders();

        int usedCpuPower = 0;
        int usedMemory = 0;
        int usedNetworkBandwidth = 0;
        int colorIndex = 0;
        for (CloudAssignment cloudAssignment : unassignedCloudAssignmentList) {
            addCloudAssignment(cloudAssignment, PROCESS_COLORS[colorIndex]);
            usedCpuPower += cloudAssignment.getMinimalCpuPower();
            usedMemory += cloudAssignment.getMinimalMemory();
            usedNetworkBandwidth += cloudAssignment.getMinimalNetworkBandwidth();
            colorIndex = (colorIndex + 1) % PROCESS_COLORS.length;
        }
        addTotals(usedCpuPower, usedMemory, usedNetworkBandwidth);
    }

    private void addUnassignedHeaders() {
        add(new JLabel("Unassigned"));
        add(new JLabel(""));
        add(new JLabel(""));
        add(new JLabel(""));
        add(new JLabel(""));
    }

    private void addCloudAssignment(CloudAssignment cloudAssignment, Color color) {
        JLabel processLabel = new JLabel("    " + cloudAssignment.getLabel());
        processLabel.setForeground(color);
        add(processLabel);

        JTextField cpuPowerField = new JTextField(cloudAssignment.getMinimalCpuPower() + " GHz");
        cpuPowerField.setEditable(false);
        add(cpuPowerField);
        JTextField memoryField = new JTextField(cloudAssignment.getMinimalMemory() + " GB");
        memoryField.setEditable(false);
        add(memoryField);
        JTextField networkBandwidthField = new JTextField(cloudAssignment.getMinimalNetworkBandwidth() + " GB");
        networkBandwidthField.setEditable(false);
        add(networkBandwidthField);
        JButton button = new JButton(new CloudAssignmentAction(cloudAssignment));
        add(button);
    }

    private void addTotals(int usedCpuPower, int usedMemory, int usedNetworkBandwidth) {
        JLabel totalLabel = new JLabel("    " + "Total");
        add(totalLabel);
        JTextField usedCpuPowerField = new JTextField(usedCpuPower + " GHz");
        usedCpuPowerField.setEditable(false);
        add(usedCpuPowerField);
        JTextField usedMemoryField = new JTextField(usedMemory + " GB");
        usedMemoryField.setEditable(false);
        add(usedMemoryField);
        JTextField usedNetworkBandwidthField = new JTextField(usedNetworkBandwidth + " GB");
        usedNetworkBandwidthField.setEditable(false);
        add(usedNetworkBandwidthField);
        add(new JLabel(""));
    }

    private void addBars(CloudBar cpuPowerBar, CloudBar memoryBar, CloudBar networkBandwidthBar) {
        add(new JLabel(""));
        add(cpuPowerBar);
        add(memoryBar);
        add(networkBandwidthBar);
        add(new JLabel(""));
    }

    private static class CloudBar extends JPanel {

        private List<Integer> processValues = new ArrayList<Integer>();
        private int computerValue;

        public CloudBar(int computerValue) {
            this.computerValue = computerValue;
        }
        
        public void addProcessValue(int processValue) {
            processValues.add(processValue);
        }

        @Override
        protected void paintComponent(Graphics g) {
            Dimension size = getSize();
            int rectHeight = size.height - 1;
            g.setColor(Color.WHITE);
            g.fillRect(0, 0, size.width, rectHeight);
            int computerWidth = size.width * 4 / 5;
            computerWidth = Math.max(computerWidth, 1);

            int offset = 0;
            int colorIndex = 0;
            for (int processValue : processValues) {
                int processWidth = processValue * computerWidth / computerValue;
                processWidth = Math.max(processWidth, 1);
                g.setColor(PROCESS_COLORS[colorIndex]);
                g.fillRect(offset, 0, processWidth, rectHeight);
                offset += processWidth;
                colorIndex = (colorIndex + 1) % PROCESS_COLORS.length;
            }
            g.setColor(Color.BLACK);
            g.drawRect(0, 0, computerWidth, rectHeight);
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
