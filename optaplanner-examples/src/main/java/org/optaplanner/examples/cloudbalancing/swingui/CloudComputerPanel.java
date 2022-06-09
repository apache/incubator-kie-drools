package org.optaplanner.examples.cloudbalancing.swingui;

import java.awt.BorderLayout;
import java.awt.Color;
import java.awt.Dimension;
import java.awt.FlowLayout;
import java.awt.Graphics;
import java.awt.GridLayout;
import java.awt.event.ActionEvent;
import java.util.ArrayList;
import java.util.List;
import java.util.function.ToIntFunction;

import javax.swing.AbstractAction;
import javax.swing.Action;
import javax.swing.BorderFactory;
import javax.swing.JButton;
import javax.swing.JDialog;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.JTextField;

import org.optaplanner.examples.cloudbalancing.domain.CloudComputer;
import org.optaplanner.examples.cloudbalancing.domain.CloudProcess;
import org.optaplanner.swing.impl.SwingUtils;
import org.optaplanner.swing.impl.TangoColorFactory;

public class CloudComputerPanel extends JPanel {

    private final CloudBalancingPanel cloudBalancingPanel;
    private CloudComputer computer;
    private List<CloudProcess> processList = new ArrayList<>();

    private JLabel computerLabel;
    private JTextField cpuPowerField;
    private JTextField memoryField;
    private JTextField networkBandwidthField;
    private JTextField costField;

    private JLabel numberOfProcessesLabel;
    private CloudBar cpuPowerBar;
    private CloudBar memoryBar;
    private CloudBar networkBandwidthBar;
    private JButton detailsButton;

    public CloudComputerPanel(CloudBalancingPanel cloudBalancingPanel, CloudComputer computer) {
        super(new GridLayout(0, 5));
        this.cloudBalancingPanel = cloudBalancingPanel;
        this.computer = computer;
        setBorder(BorderFactory.createCompoundBorder(
                BorderFactory.createCompoundBorder(
                        BorderFactory.createEmptyBorder(1, 2, 1, 2),
                        BorderFactory.createLineBorder(Color.BLACK)),
                BorderFactory.createEmptyBorder(1, 1, 1, 1)));
        createTotalsUI();
        createBarsUI();
    }

    public CloudComputer getComputer() {
        return computer;
    }

    private String getComputerLabel() {
        return computer == null ? "Unassigned" : computer.getLabel();
    }

    private int getComputerCpuPower() {
        return computer == null ? 0 : computer.getCpuPower();
    }

    private int getComputerMemory() {
        return computer == null ? 0 : computer.getMemory();
    }

    private int getComputerNetworkBandwidth() {
        return computer == null ? 0 : computer.getNetworkBandwidth();
    }

    private int getComputerCost() {
        return computer == null ? 0 : computer.getCost();
    }

    private void createTotalsUI() {
        JPanel labelAndDeletePanel = new JPanel(new BorderLayout(5, 0));
        if (computer != null) {
            labelAndDeletePanel.add(new JLabel(cloudBalancingPanel.getCloudComputerIcon()), BorderLayout.WEST);
        }
        computerLabel = new JLabel(getComputerLabel());
        computerLabel.setEnabled(false);
        labelAndDeletePanel.add(computerLabel, BorderLayout.CENTER);
        if (computer != null) {
            JPanel deletePanel = new JPanel(new BorderLayout());
            JButton deleteButton = SwingUtils.makeSmallButton(new JButton(cloudBalancingPanel.getDeleteCloudComputerIcon()));
            deleteButton.setToolTipText("Delete");
            deleteButton.addActionListener(e -> cloudBalancingPanel.deleteComputer(computer));
            deletePanel.add(deleteButton, BorderLayout.NORTH);
            labelAndDeletePanel.add(deletePanel, BorderLayout.EAST);
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
        numberOfProcessesLabel.setBorder(BorderFactory.createEmptyBorder(0, 37, 0, 0));
        add(numberOfProcessesLabel);
        cpuPowerBar = new CloudBar(getComputerCpuPower(),
                cloudBalancingPanel.getMaximumComputerCpuPower(),
                CloudProcess::getRequiredCpuPower);
        cpuPowerBar.setEnabled(false);
        add(cpuPowerBar);
        memoryBar = new CloudBar(getComputerMemory(),
                cloudBalancingPanel.getMaximumComputerMemory(),
                CloudProcess::getRequiredMemory);
        memoryBar.setEnabled(false);
        add(memoryBar);
        networkBandwidthBar = new CloudBar(getComputerNetworkBandwidth(),
                cloudBalancingPanel.getMaximumComputerNetworkBandwidth(),
                CloudProcess::getRequiredNetworkBandwidth);
        networkBandwidthBar.setEnabled(false);
        add(networkBandwidthBar);
        detailsButton = new JButton(new AbstractAction("Details") {
            @Override
            public void actionPerformed(ActionEvent e) {
                CloudProcessListDialog processListDialog = new CloudProcessListDialog();
                processListDialog.setLocationRelativeTo(getRootPane());
                processListDialog.setVisible(true);
            }
        });
        detailsButton.setEnabled(false);
        add(detailsButton);
    }

    public void addProcess(CloudProcess process) {
        processList.add(process);
    }

    public void removeProcess(CloudProcess process) {
        processList.remove(process);
    }

    public void clearProcesses() {
        processList.clear();
    }

    public void update() {
        int usedCpuPower = 0;
        int usedMemory = 0;
        int usedNetworkBandwidth = 0;
        for (CloudProcess process : processList) {
            usedCpuPower += process.getRequiredCpuPower();
            usedMemory += process.getRequiredMemory();
            usedNetworkBandwidth += process.getRequiredNetworkBandwidth();
        }
        boolean used = processList.size() > 0;
        updateTotals(usedCpuPower, usedMemory, usedNetworkBandwidth, used);
        updateBars(used);
    }

    private void updateTotals(int usedCpuPower, int usedMemory, int usedNetworkBandwidth, boolean used) {
        computerLabel.setEnabled(used);
        cpuPowerField.setText(usedCpuPower + " GHz / " + getComputerCpuPower() + " GHz");
        cpuPowerField.setForeground(usedCpuPower > getComputerCpuPower() ? TangoColorFactory.SCARLET_3 : Color.BLACK);
        cpuPowerField.setEnabled(used);
        memoryField.setText(usedMemory + " GB / " + getComputerMemory() + " GB");
        memoryField.setForeground(usedMemory > getComputerMemory() ? TangoColorFactory.SCARLET_3 : Color.BLACK);
        memoryField.setEnabled(used);
        networkBandwidthField.setText(usedNetworkBandwidth + " GB / " + getComputerNetworkBandwidth() + " GB");
        networkBandwidthField.setForeground(usedNetworkBandwidth > getComputerNetworkBandwidth()
                ? TangoColorFactory.SCARLET_3
                : Color.BLACK);
        networkBandwidthField.setEnabled(used);
        costField.setEnabled(used);
    }

    private void updateBars(boolean used) {
        numberOfProcessesLabel.setText(processList.size() + " processes");
        numberOfProcessesLabel.setEnabled(used);
        cpuPowerBar.setEnabled(used);
        cpuPowerBar.repaint();
        memoryBar.setEnabled(used);
        memoryBar.repaint();
        networkBandwidthBar.setEnabled(used);
        networkBandwidthBar.repaint();
        detailsButton.setEnabled(used);
    }

    private class CloudBar extends JPanel {

        private int computerValue;
        private int maximumComputerValue;
        private ToIntFunction<CloudProcess> processValueExtractor;

        public CloudBar(int computerValue, int maximumComputerValue, ToIntFunction<CloudProcess> processValueExtractor) {
            this.computerValue = computerValue;
            this.maximumComputerValue = maximumComputerValue;
            this.processValueExtractor = processValueExtractor;
        }

        @Override
        protected void paintComponent(Graphics g) {
            Dimension size = getSize();
            g.setColor(getBackground());
            g.fillRect(0, 0, size.width, size.height);

            int maximumComputerWidth = size.width - 10;
            if (maximumComputerWidth <= 10) {
                g.setColor(TangoColorFactory.ALUMINIUM_6);
                g.drawString("?", 2, 2);
                return;
            }
            double pixelsPerValue = maximumComputerWidth / (double) maximumComputerValue;
            int computerWidth = (int) (pixelsPerValue * computerValue);
            if (this.computerValue > 0) {
                g.setColor(isEnabled() ? Color.WHITE : getBackground());
                g.fillRect(0, 0, computerWidth, size.height);
            }
            int offsetValue = 0;
            for (CloudProcess process : CloudComputerPanel.this.processList) {
                int processValue = processValueExtractor.applyAsInt(process);
                int offset = (int) (offsetValue * pixelsPerValue);
                int processWidth = (int) (processValue * pixelsPerValue) + 1;
                processWidth = Math.max(processWidth, 1);
                g.setColor(cloudBalancingPanel.getCloudProcessColor(process));
                g.fillRect(offset, 0, processWidth, size.height);
                offsetValue += processValue;
            }
            if (this.computerValue > 0) {
                g.setColor(isEnabled() ? Color.BLACK : TangoColorFactory.ALUMINIUM_5);
                g.drawRect(0, 0, computerWidth, size.height - 1);
            }
        }

    }

    private class CloudProcessListDialog extends JDialog {

        public CloudProcessListDialog() {
            setModal(true);
            setTitle(getComputerLabel());
            JPanel contentPanel = new JPanel();
            contentPanel.setLayout(new BorderLayout());
            contentPanel.add(createHeaderPanel(), BorderLayout.NORTH);
            JPanel assignmentsPanel = createAssignmentsPanel();
            JScrollPane assignmentsScrollPane = new JScrollPane(assignmentsPanel);
            assignmentsScrollPane.setPreferredSize(new Dimension(800, 400));
            assignmentsScrollPane.getVerticalScrollBar().setUnitIncrement(20);
            contentPanel.add(assignmentsScrollPane, BorderLayout.CENTER);
            JPanel buttonPanel = new JPanel(new FlowLayout());
            Action okAction = new AbstractAction("Ok") {
                @Override
                public void actionPerformed(ActionEvent e) {
                    dispose();
                }
            };
            buttonPanel.add(new JButton(okAction));
            contentPanel.add(buttonPanel, BorderLayout.SOUTH);
            setContentPane(contentPanel);
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
            JPanel assignmentsPanel = new JPanel(new GridLayout(0, 1));
            for (final CloudProcess process : processList) {
                assignmentsPanel.add(new CloudProcessAssignmentPanel(assignmentsPanel, process));
            }
            JPanel fillerAssignmentsPanel = new JPanel(new BorderLayout());
            fillerAssignmentsPanel.add(assignmentsPanel, BorderLayout.NORTH);
            return fillerAssignmentsPanel;
        }

        private class CloudProcessAssignmentPanel extends JPanel {

            public CloudProcessAssignmentPanel(JPanel assignmentsPanel, CloudProcess process) {
                super(new GridLayout(0, 5));
                JPanel labelAndDeletePanel = new JPanel(new BorderLayout(5, 0));
                labelAndDeletePanel.add(new JLabel(cloudBalancingPanel.getCloudProcessIcon()), BorderLayout.WEST);
                JLabel processLabel = new JLabel(process.getLabel());
                processLabel.setForeground(cloudBalancingPanel.getCloudProcessColor(process));
                labelAndDeletePanel.add(processLabel, BorderLayout.CENTER);
                JPanel deletePanel = new JPanel(new BorderLayout());
                JButton deleteButton = SwingUtils.makeSmallButton(new JButton(cloudBalancingPanel.getDeleteCloudProcessIcon()));
                deleteButton.setToolTipText("Delete");
                deleteButton.addActionListener(e -> {
                    cloudBalancingPanel.deleteProcess(process);
                    dispose();
                });
                deletePanel.add(deleteButton, BorderLayout.NORTH);
                labelAndDeletePanel.add(deletePanel, BorderLayout.EAST);
                add(labelAndDeletePanel);

                JTextField cpuPowerField = new JTextField(process.getRequiredCpuPower() + " GHz");
                cpuPowerField.setEditable(false);
                add(cpuPowerField);
                JTextField memoryField = new JTextField(process.getRequiredMemory() + " GB");
                memoryField.setEditable(false);
                add(memoryField);
                JTextField networkBandwidthField = new JTextField(process.getRequiredNetworkBandwidth() + " GB");
                networkBandwidthField.setEditable(false);
                add(networkBandwidthField);
                add(cloudBalancingPanel.createButton(process, () -> {
                    assignmentsPanel.remove(this);
                    assignmentsPanel.revalidate();
                }));
            }
        }

    }

}
