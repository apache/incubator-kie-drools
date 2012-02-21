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

package org.drools.planner.examples.cloudbalancing.swingui;

import java.awt.BorderLayout;
import java.awt.Color;
import java.awt.Dimension;
import java.awt.Graphics;
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

import org.drools.planner.examples.cloudbalancing.domain.CloudProcess;
import org.drools.planner.examples.cloudbalancing.domain.CloudComputer;
import org.drools.planner.examples.common.swingui.TangoColors;

public class CloudComputerPanel extends JPanel {

    private final CloudBalancingPanel cloudBalancingPanel;
    private CloudComputer cloudComputer;
    private List<CloudProcess> cloudProcessList = new ArrayList<CloudProcess>();

    private JLabel computerLabel;
    private JButton deleteButton;
    private JTextField cpuPowerField;
    private JTextField memoryField;
    private JTextField networkBandwidthField;
    private JTextField costField;

    private JLabel numberOfProcessesLabel;
    private CloudBar cpuPowerBar;
    private CloudBar memoryBar;
    private CloudBar networkBandwidthBar;
    private JButton detailsButton;

    public CloudComputerPanel(CloudBalancingPanel cloudBalancingPanel, CloudComputer cloudComputer) {
        super(new GridLayout(0, 5));
        this.cloudBalancingPanel = cloudBalancingPanel;
        this.cloudComputer = cloudComputer;
        setBorder(BorderFactory.createCompoundBorder(
                BorderFactory.createCompoundBorder(
                    BorderFactory.createEmptyBorder(1, 2, 1, 2),
                    BorderFactory.createLineBorder(Color.BLACK)),
                BorderFactory.createEmptyBorder(1, 1, 1, 1)));
        createTotalsUI();
        createBarsUI();
    }

    public CloudComputer getCloudComputer() {
        return cloudComputer;
    }

    private String getComputerLabel() {
        return cloudComputer == null ? "Unassigned" : cloudComputer.getLabel();
    }

    private int getComputerCpuPower() {
        return cloudComputer == null ? 0 : cloudComputer.getCpuPower();
    }

    private int getComputerMemory() {
        return cloudComputer == null ? 0 : cloudComputer.getMemory();
    }

    private int getComputerNetworkBandwidth() {
        return cloudComputer == null ? 0 : cloudComputer.getNetworkBandwidth();
    }

    private int getComputerCost() {
        return cloudComputer == null ? 0 : cloudComputer.getCost();
    }

    private void createTotalsUI() {
        JPanel labelAndDeletePanel = new JPanel(new BorderLayout());
        computerLabel = new JLabel(getComputerLabel());
        computerLabel.setEnabled(false);
        labelAndDeletePanel.add(computerLabel, BorderLayout.CENTER);
        if (cloudComputer != null) {
            deleteButton = new JButton(new AbstractAction("X") {
                public void actionPerformed(ActionEvent e) {
                    cloudBalancingPanel.deleteComputer(cloudComputer);
                }
            });
            labelAndDeletePanel.add(deleteButton, BorderLayout.EAST);
        }
        add(labelAndDeletePanel);
        cpuPowerField = new JTextField("0 GHz / " + getComputerCpuPower() + " GHz");
        cpuPowerField.setEditable(false);
        cpuPowerField.setEnabled(false);
        add(cpuPowerField);
        memoryField = new JTextField("0 GB / " + getComputerMemory() + " GB");
        memoryField.setEditable(false);
        memoryField.setEnabled(false);
        add(memoryField);
        networkBandwidthField = new JTextField("0 GB / " + getComputerNetworkBandwidth() + " GB");
        networkBandwidthField.setEditable(false);
        networkBandwidthField.setEnabled(false);
        add(networkBandwidthField);
        costField = new JTextField(getComputerCost() + " $");
        costField.setEditable(false);
        costField.setEnabled(false);
        add(costField);
    }

    private void createBarsUI() {
        numberOfProcessesLabel = new JLabel("    0 processes");
        numberOfProcessesLabel.setEnabled(false);
        add(numberOfProcessesLabel);
        cpuPowerBar = new CloudBar(getComputerCpuPower());
        cpuPowerBar.setEnabled(false);
        add(cpuPowerBar);
        memoryBar = new CloudBar(getComputerMemory());
        memoryBar.setEnabled(false);
        add(memoryBar);
        networkBandwidthBar = new CloudBar(getComputerNetworkBandwidth());
        networkBandwidthBar.setEnabled(false);
        add(networkBandwidthBar);
        detailsButton = new JButton(new AbstractAction("Details") {
            public void actionPerformed(ActionEvent e) {
                CloudProcessListDialog cloudProcessListDialog = new CloudProcessListDialog();
                cloudProcessListDialog.setLocationRelativeTo(getRootPane());
                cloudProcessListDialog.setVisible(true);
            }
        });
        detailsButton.setEnabled(false);
        add(detailsButton);
    }

    public void addCloudProcess(CloudProcess cloudProcess) {
        cloudProcessList.add(cloudProcess);
    }

    public void removeCloudProcess(CloudProcess cloudProcess) {
        cloudProcessList.remove(cloudProcess);
    }

    public void clearCloudProcesss() {
        cloudProcessList.clear();
    }

    public void update() {
        int usedCpuPower = 0;
        cpuPowerBar.clearProcessValues();
        int usedMemory = 0;
        memoryBar.clearProcessValues();
        int usedNetworkBandwidth = 0;
        networkBandwidthBar.clearProcessValues();
        int colorIndex = 0;
        for (CloudProcess cloudProcess : cloudProcessList) {
            usedCpuPower += cloudProcess.getRequiredCpuPower();
            cpuPowerBar.addProcessValue(cloudProcess.getRequiredCpuPower());
            usedMemory += cloudProcess.getRequiredMemory();
            memoryBar.addProcessValue(cloudProcess.getRequiredMemory());
            usedNetworkBandwidth += cloudProcess.getRequiredNetworkBandwidth();
            networkBandwidthBar.addProcessValue(cloudProcess.getRequiredNetworkBandwidth());
            colorIndex = (colorIndex + 1) % TangoColors.SEQUENCE_1.length;
        }
        boolean used = cloudProcessList.size() > 0;
        updateTotals(usedCpuPower, usedMemory, usedNetworkBandwidth, used);
        updateBars(used);
    }

    private void updateTotals(int usedCpuPower, int usedMemory, int usedNetworkBandwidth, boolean used) {
        computerLabel.setEnabled(used);
        cpuPowerField.setText(usedCpuPower + " GHz / " + getComputerCpuPower() + " GHz");
        cpuPowerField.setForeground(usedCpuPower > getComputerCpuPower() ? Color.RED : Color.BLACK);
        cpuPowerField.setEnabled(used);
        memoryField.setText(usedMemory + " GB / " + getComputerMemory() + " GB");
        memoryField.setForeground(usedMemory > getComputerMemory() ? Color.RED : Color.BLACK);
        memoryField.setEnabled(used);
        networkBandwidthField.setText(usedNetworkBandwidth + " GB / " + getComputerNetworkBandwidth() + " GB");
        networkBandwidthField.setForeground(usedNetworkBandwidth > getComputerNetworkBandwidth()
                ? Color.RED : Color.BLACK);
        networkBandwidthField.setEnabled(used);
        costField.setEnabled(used);
    }

    private void updateBars(boolean used) {
        numberOfProcessesLabel.setText("    " + cloudProcessList.size() + " processes");
        numberOfProcessesLabel.setEnabled(used);
        cpuPowerBar.setEnabled(used);
        cpuPowerBar.repaint();
        memoryBar.setEnabled(used);
        memoryBar.repaint();
        networkBandwidthBar.setEnabled(used);
        networkBandwidthBar.repaint();
        detailsButton.setEnabled(used);
    }

    private static class CloudBar extends JPanel {

        private List<Integer> processValues = new ArrayList<Integer>();
        private int computerValue;

        public CloudBar(int computerValue) {
            this.computerValue = computerValue;
        }

        public void clearProcessValues() {
            processValues.clear();
        }

        public void addProcessValue(int processValue) {
            processValues.add(processValue);
        }

        @Override
        protected void paintComponent(Graphics g) {
            Dimension size = getSize();
            int rectHeight = size.height;
            g.setColor(getBackground());
            g.fillRect(0, 0, size.width, rectHeight);
            int computerWidth = size.width * 4 / 5;
            computerWidth = Math.max(computerWidth, 1);
            if (this.computerValue > 0) {
                g.setColor(isEnabled() ? Color.WHITE : getBackground());
                g.fillRect(0, 0, computerWidth, rectHeight);
            }

            int offset = 0;
            int colorIndex = 0;
            int safeComputerValue = this.computerValue;
            if (safeComputerValue <= 0) {
                safeComputerValue = 0;
                for (int processValue : processValues) {
                    safeComputerValue +=  processValue;
                }
            }
            for (int processValue : processValues) {
                int processWidth = processValue * computerWidth / safeComputerValue;
                processWidth = Math.max(processWidth, 1);
                g.setColor(TangoColors.SEQUENCE_1[colorIndex]);
                g.fillRect(offset, 0, processWidth, rectHeight);
                offset += processWidth;
                colorIndex = (colorIndex + 1) % TangoColors.SEQUENCE_1.length;
            }
            if (this.computerValue > 0) {
                g.setColor(isEnabled() ? Color.BLACK : Color.DARK_GRAY);
                g.drawRect(0, 0, computerWidth, rectHeight - 1);
            }
        }

    }

    private class CloudProcessListDialog extends JDialog {

        public CloudProcessListDialog() {
            setModal(true);
            setTitle(getComputerLabel());
            JPanel contentPanel = new JPanel();
            GroupLayout layout = new GroupLayout(contentPanel);
            contentPanel.setLayout(layout);
            JPanel headerPanel = createHeaderPanel();
            JPanel assignmentsPanel = createAssignmentsPanel();
            layout.setHorizontalGroup(layout.createParallelGroup()
                    .addComponent(headerPanel).addComponent(assignmentsPanel));
            layout.setVerticalGroup(layout.createSequentialGroup()
                    .addComponent(headerPanel, GroupLayout.PREFERRED_SIZE, GroupLayout.PREFERRED_SIZE,
                            GroupLayout.PREFERRED_SIZE)
                    .addComponent(assignmentsPanel, GroupLayout.PREFERRED_SIZE, GroupLayout.PREFERRED_SIZE,
                            GroupLayout.PREFERRED_SIZE));
            JScrollPane contentScrollPane = new JScrollPane(contentPanel);
            contentScrollPane.setPreferredSize(new Dimension(800, 200));
            contentScrollPane.getVerticalScrollBar().setUnitIncrement(20);
            setContentPane(contentScrollPane);
            pack();
        }

        private JPanel createHeaderPanel() {
            JPanel headerPanel = new JPanel(new GridLayout(0, 5));
            headerPanel.add(new JLabel(""));
            JLabel cpuPowerLabel = new JLabel("CPU power");
            headerPanel.add(cpuPowerLabel);
            JLabel memoryLabel = new JLabel("Memory");
            headerPanel.add(memoryLabel);
            JLabel networkBandwidthLabel = new JLabel("Network bandwidth");
            headerPanel.add(networkBandwidthLabel);
            headerPanel.add(new JLabel(""));
            return headerPanel;
        }

        private JPanel createAssignmentsPanel() {
            JPanel assignmentsPanel = new JPanel(new GridLayout(0, 5));
            int colorIndex = 0;
            for (CloudProcess cloudProcess : cloudProcessList) {
                JLabel cloudProcessLabel = new JLabel(cloudProcess.getLabel());
                cloudProcessLabel.setForeground(TangoColors.SEQUENCE_1[colorIndex]);
                assignmentsPanel.add(cloudProcessLabel);

                JTextField cpuPowerField = new JTextField(cloudProcess.getRequiredCpuPower() + " GHz");
                cpuPowerField.setEditable(false);
                assignmentsPanel.add(cpuPowerField);
                JTextField memoryField = new JTextField(cloudProcess.getRequiredMemory() + " GB");
                memoryField.setEditable(false);
                assignmentsPanel.add(memoryField);
                JTextField networkBandwidthField = new JTextField(cloudProcess.getRequiredNetworkBandwidth() + " GB");
                networkBandwidthField.setEditable(false);
                assignmentsPanel.add(networkBandwidthField);
                assignmentsPanel.add(new JLabel(""));

                colorIndex = (colorIndex + 1) % TangoColors.SEQUENCE_1.length;
            }
            return assignmentsPanel;
        }

    }
    
}
