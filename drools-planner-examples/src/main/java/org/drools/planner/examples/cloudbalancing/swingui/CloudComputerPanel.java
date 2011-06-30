package org.drools.planner.examples.cloudbalancing.swingui;

import java.awt.Color;
import java.awt.Dimension;
import java.awt.Graphics;
import java.awt.GridLayout;
import java.awt.LayoutManager;
import java.security.PrivateKey;
import java.util.ArrayList;
import java.util.List;
import javax.swing.BorderFactory;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.JTextField;

import org.drools.planner.examples.cloudbalancing.domain.CloudAssignment;
import org.drools.planner.examples.cloudbalancing.domain.CloudComputer;

public class CloudComputerPanel extends JPanel {

    private CloudComputer cloudComputer;
    private List<CloudAssignment> cloudAssignmentList = new ArrayList<CloudAssignment>();

    private JLabel computerLabel;
    private JTextField cpuPowerField;
    private JTextField memoryField;
    private JTextField networkBandwidthField;
    private JTextField costField;

    private JLabel numberOfProcessesLabel;
    private CloudBar cpuPowerBar;
    private CloudBar memoryBar;
    private CloudBar networkBandwidthBar;

    public CloudComputerPanel(CloudComputer cloudComputer) {
        super(new GridLayout(0, 5));
        this.cloudComputer = cloudComputer;
        setBorder(BorderFactory.createCompoundBorder(
                BorderFactory.createCompoundBorder(
                    BorderFactory.createEmptyBorder(1, 2, 1, 2),
                    BorderFactory.createLineBorder(Color.BLACK)),
                BorderFactory.createEmptyBorder(1, 1, 1, 1)));
        addTotals();
        addBars();
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

    private void addTotals() {
        computerLabel = new JLabel(getComputerLabel());
        computerLabel.setEnabled(false);
        add(computerLabel);
        cpuPowerField = new JTextField("0 GHz / " + getComputerCpuPower() + " GHz");
        cpuPowerField.setEditable(false);
        cpuPowerField.setEnabled(false);
        add(cpuPowerField);
        memoryField = new JTextField("0 GB / " + getComputerMemory() + " GB");
        memoryField.setEditable(false);
        memoryField.setEnabled(false);
        add(memoryField);
        networkBandwidthField = new JTextField( "0 GB / " + getComputerNetworkBandwidth() + " GB");
        networkBandwidthField.setEditable(false);
        networkBandwidthField.setEnabled(false);
        add(networkBandwidthField);
        costField = new JTextField(getComputerCost() + " $");
        costField.setEditable(false);
        costField.setEnabled(false);
        add(costField);
    }

    private void addBars() {
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
        add(new JLabel());
    }
    public void addCloudAssignment(CloudAssignment cloudAssignment) {
        cloudAssignmentList.add(cloudAssignment);
        update();
    }

    public void removeCloudAssignment(CloudAssignment cloudAssignment) {
        cloudAssignmentList.remove(cloudAssignment);
        update();
    }

    public void clearCloudAssignments() {
        cloudAssignmentList.clear();
        update();
    }

    public void update() {
        int usedCpuPower = 0;
        cpuPowerBar.clearProcessValues();
        int usedMemory = 0;
        memoryBar.clearProcessValues();
        int usedNetworkBandwidth = 0;
        networkBandwidthBar.clearProcessValues();
        int colorIndex = 0;
        for (CloudAssignment cloudAssignment : cloudAssignmentList) {
            usedCpuPower += cloudAssignment.getMinimalCpuPower();
            cpuPowerBar.addProcessValue(cloudAssignment.getMinimalCpuPower());
            usedMemory += cloudAssignment.getMinimalMemory();
            memoryBar.addProcessValue(cloudAssignment.getMinimalMemory());
            usedNetworkBandwidth += cloudAssignment.getMinimalNetworkBandwidth();
            networkBandwidthBar.addProcessValue(cloudAssignment.getMinimalNetworkBandwidth());
            colorIndex = (colorIndex + 1) % CloudBalancingPanel.PROCESS_COLORS.length;
        }
        boolean used = cloudAssignmentList.size() > 0;
        updateTotals(usedCpuPower, usedMemory, usedNetworkBandwidth, used);
        numberOfProcessesLabel.setText("    " + cloudAssignmentList.size() + " processes");
        numberOfProcessesLabel.setEnabled(used);
        cpuPowerBar.setEnabled(used);
        cpuPowerBar.repaint();
        memoryBar.setEnabled(used);
        memoryBar.repaint();
        networkBandwidthBar.setEnabled(used);
        networkBandwidthBar.repaint();
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
                g.setColor(CloudBalancingPanel.PROCESS_COLORS[colorIndex]);
                g.fillRect(offset, 0, processWidth, rectHeight);
                offset += processWidth;
                colorIndex = (colorIndex + 1) % CloudBalancingPanel.PROCESS_COLORS.length;
            }
            if (this.computerValue > 0) {
                g.setColor(isEnabled() ? Color.BLACK : Color.DARK_GRAY);
                g.drawRect(0, 0, computerWidth, rectHeight - 1);
            }
        }

    }

    
}
