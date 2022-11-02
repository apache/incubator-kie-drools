package org.optaplanner.examples.cloudbalancing.swingui;

import java.awt.BorderLayout;
import java.awt.Color;
import java.awt.GridLayout;
import java.awt.event.ActionEvent;
import java.util.LinkedHashMap;
import java.util.LinkedHashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.function.LongFunction;

import javax.swing.AbstractAction;
import javax.swing.GroupLayout;
import javax.swing.ImageIcon;
import javax.swing.JButton;
import javax.swing.JComboBox;
import javax.swing.JLabel;
import javax.swing.JOptionPane;
import javax.swing.JPanel;

import org.optaplanner.examples.cloudbalancing.domain.CloudBalance;
import org.optaplanner.examples.cloudbalancing.domain.CloudComputer;
import org.optaplanner.examples.cloudbalancing.domain.CloudProcess;
import org.optaplanner.examples.cloudbalancing.swingui.realtime.AddComputerProblemChange;
import org.optaplanner.examples.cloudbalancing.swingui.realtime.AddProcessProblemChange;
import org.optaplanner.examples.cloudbalancing.swingui.realtime.DeleteComputerProblemChange;
import org.optaplanner.examples.cloudbalancing.swingui.realtime.DeleteProcessProblemChange;
import org.optaplanner.examples.common.swingui.SolutionPanel;
import org.optaplanner.examples.common.swingui.components.LabeledComboBoxRenderer;
import org.optaplanner.swing.impl.SwingUtils;
import org.optaplanner.swing.impl.TangoColorFactory;

public class CloudBalancingPanel extends SolutionPanel<CloudBalance> {

    public static final String LOGO_PATH = "/org/optaplanner/examples/cloudbalancing/swingui/cloudBalancingLogo.png";

    private final TangoColorFactory processColorFactory;
    private final ImageIcon cloudComputerIcon;
    private final ImageIcon addCloudComputerIcon;
    private final ImageIcon deleteCloudComputerIcon;
    private final ImageIcon cloudProcessIcon;
    private final ImageIcon addCloudProcessIcon;
    private final ImageIcon deleteCloudProcessIcon;

    private JPanel computersPanel;

    private CloudComputerPanel unassignedPanel;
    private Map<CloudComputer, CloudComputerPanel> computerToPanelMap;

    private int maximumComputerCpuPower;
    private int maximumComputerMemory;
    private int maximumComputerNetworkBandwidth;

    public CloudBalancingPanel() {
        processColorFactory = new TangoColorFactory();
        cloudComputerIcon = new ImageIcon(getClass().getResource("cloudComputer.png"));
        addCloudComputerIcon = new ImageIcon(getClass().getResource("addCloudComputer.png"));
        deleteCloudComputerIcon = new ImageIcon(getClass().getResource("deleteCloudComputer.png"));
        cloudProcessIcon = new ImageIcon(getClass().getResource("cloudProcess.png"));
        addCloudProcessIcon = new ImageIcon(getClass().getResource("addCloudProcess.png"));
        deleteCloudProcessIcon = new ImageIcon(getClass().getResource("deleteCloudProcess.png"));
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

    public Color getCloudProcessColor(CloudProcess process) {
        return processColorFactory.pickColor(process.getLabel());
    }

    public ImageIcon getCloudComputerIcon() {
        return cloudComputerIcon;
    }

    public ImageIcon getAddCloudComputerIcon() {
        return addCloudComputerIcon;
    }

    public ImageIcon getDeleteCloudComputerIcon() {
        return deleteCloudComputerIcon;
    }

    public ImageIcon getCloudProcessIcon() {
        return cloudProcessIcon;
    }

    public ImageIcon getAddCloudProcessIcon() {
        return addCloudProcessIcon;
    }

    public ImageIcon getDeleteCloudProcessIcon() {
        return deleteCloudProcessIcon;
    }

    public int getMaximumComputerCpuPower() {
        return maximumComputerCpuPower;
    }

    public int getMaximumComputerMemory() {
        return maximumComputerMemory;
    }

    public int getMaximumComputerNetworkBandwidth() {
        return maximumComputerNetworkBandwidth;
    }

    private JPanel createHeaderPanel() {
        JPanel headerPanel = new JPanel(new GridLayout(0, 5));
        JPanel addPanel = new JPanel(new GridLayout());
        JButton addComputerButton = SwingUtils.makeSmallButton(new JButton(addCloudComputerIcon));
        addComputerButton.setToolTipText("Add computer");
        addComputerButton
                .addActionListener(e -> addComputer(expectedId -> new CloudComputer(expectedId, 12, 32, 12, 400 + 400 + 600)));
        addPanel.add(addComputerButton);
        JButton addProcessButton = SwingUtils.makeSmallButton(new JButton(addCloudProcessIcon));
        addProcessButton.setToolTipText("Add process");
        addProcessButton.addActionListener(e -> addProcess(expectedId -> new CloudProcess(expectedId, 3, 8, 3)));
        addPanel.add(addProcessButton);
        JPanel cornerPanel = new JPanel(new BorderLayout());
        cornerPanel.add(addPanel, BorderLayout.EAST);
        headerPanel.add(cornerPanel);
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
        computerToPanelMap = new LinkedHashMap<>();
        return computersPanel;
    }

    @Override
    public void resetPanel(CloudBalance cloudBalance) {
        maximumComputerCpuPower = 0;
        maximumComputerMemory = 0;
        maximumComputerNetworkBandwidth = 0;
        for (CloudComputer computer : cloudBalance.getComputerList()) {
            if (computer.getCpuPower() > maximumComputerCpuPower) {
                maximumComputerCpuPower = computer.getCpuPower();
            }
            if (computer.getMemory() > maximumComputerMemory) {
                maximumComputerMemory = computer.getMemory();
            }
            if (computer.getNetworkBandwidth() > maximumComputerNetworkBandwidth) {
                maximumComputerNetworkBandwidth = computer.getNetworkBandwidth();
            }
        }
        for (CloudComputerPanel computerPanel : computerToPanelMap.values()) {
            if (computerPanel.getComputer() != null) {
                computersPanel.remove(computerPanel);
            }
        }
        computerToPanelMap.clear();
        computersPanel.removeAll();
        unassignedPanel = new CloudComputerPanel(this, null);
        computersPanel.add(unassignedPanel);
        computerToPanelMap.put(null, unassignedPanel);
        updatePanel(cloudBalance);
    }

    @Override
    public void updatePanel(CloudBalance cloudBalance) {
        Set<CloudComputer> deadCloudComputerSet = new LinkedHashSet<>(computerToPanelMap.keySet());
        deadCloudComputerSet.remove(null);
        for (CloudComputer computer : cloudBalance.getComputerList()) {
            deadCloudComputerSet.remove(computer);
            CloudComputerPanel computerPanel = computerToPanelMap.get(computer);
            if (computerPanel == null) {
                computerPanel = new CloudComputerPanel(this, computer);
                computersPanel.add(computerPanel);
                computerToPanelMap.put(computer, computerPanel);
            }
            computerPanel.clearProcesses();
        }
        unassignedPanel.clearProcesses();
        for (CloudProcess process : cloudBalance.getProcessList()) {
            CloudComputer computer = process.getComputer();
            CloudComputerPanel computerPanel = computerToPanelMap.get(computer);
            computerPanel.addProcess(process);
        }
        for (CloudComputer deadComputer : deadCloudComputerSet) {
            CloudComputerPanel deadComputerPanel = computerToPanelMap.remove(deadComputer);
            computersPanel.remove(deadComputerPanel);
        }
        for (CloudComputerPanel computerPanel : computerToPanelMap.values()) {
            computerPanel.update();
        }
        // If computersPanel.add() or computersPanel.remove() was called, the component needs to be validated.
        computersPanel.validate();
    }

    public void addComputer(final LongFunction<CloudComputer> computerGenerator) {
        logger.info("Scheduling addition of computer.");
        doProblemChange(new AddComputerProblemChange(computerGenerator));
    }

    public void deleteComputer(final CloudComputer computer) {
        logger.info("Scheduling delete of computer ({}).", computer);
        doProblemChange(new DeleteComputerProblemChange(computer));
    }

    public void addProcess(final LongFunction<CloudProcess> processGenerator) {
        logger.info("Scheduling addition of process.");
        doProblemChange(new AddProcessProblemChange(processGenerator));
    }

    public void deleteProcess(final CloudProcess process) {
        logger.info("Scheduling delete of process ({}).", process);
        doProblemChange(new DeleteProcessProblemChange(process));
    }

    public JButton createButton(CloudProcess process, Runnable removeAction) {
        return SwingUtils.makeSmallButton(new JButton(new CloudProcessAction(process, removeAction)));
    }

    private class CloudProcessAction extends AbstractAction {

        private final CloudProcess process;
        private final Runnable removeAction;

        public CloudProcessAction(CloudProcess process, Runnable removeAction) {
            super(process.getLabel());
            this.process = process;
            this.removeAction = removeAction;
        }

        @Override
        public void actionPerformed(ActionEvent e) {
            JPanel listFieldsPanel = new JPanel(new GridLayout(1, 2));
            listFieldsPanel.add(new JLabel("Computer:"));
            List<CloudComputer> computerList = getSolution().getComputerList();
            // Add 1 to array size to add null, which makes the entity unassigned
            JComboBox computerListField = new JComboBox(computerList.toArray(new Object[computerList.size() + 1]));
            LabeledComboBoxRenderer.applyToComboBox(computerListField);
            computerListField.setSelectedItem(process.getComputer());
            listFieldsPanel.add(computerListField);
            int result = JOptionPane.showConfirmDialog(CloudBalancingPanel.this.getRootPane(), listFieldsPanel,
                    "Select computer", JOptionPane.OK_CANCEL_OPTION);
            if (result == JOptionPane.OK_OPTION) {
                CloudComputer toComputer = (CloudComputer) computerListField.getSelectedItem();
                if (process.getComputer() != toComputer) {
                    doProblemChange((workingSolution, problemChangeDirector) -> problemChangeDirector.changeVariable(process,
                            "computer",
                            proc -> process.setComputer(toComputer)), true);
                    removeAction.run();
                }
                solverAndPersistenceFrame.resetScreen();
            }
        }
    }
}
