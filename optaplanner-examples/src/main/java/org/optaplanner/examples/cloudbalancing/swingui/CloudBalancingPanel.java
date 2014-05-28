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

package org.optaplanner.examples.cloudbalancing.swingui;

import java.awt.GridLayout;
import java.awt.Insets;
import java.awt.event.ActionEvent;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.LinkedHashMap;
import java.util.LinkedHashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;
import javax.swing.AbstractAction;
import javax.swing.GroupLayout;
import javax.swing.ImageIcon;
import javax.swing.JButton;
import javax.swing.JComboBox;
import javax.swing.JLabel;
import javax.swing.JOptionPane;
import javax.swing.JPanel;

import org.apache.commons.lang.ObjectUtils;
import org.optaplanner.core.impl.score.director.ScoreDirector;
import org.optaplanner.core.api.domain.solution.Solution;
import org.optaplanner.core.impl.solver.ProblemFactChange;
import org.optaplanner.examples.cloudbalancing.domain.CloudBalance;
import org.optaplanner.examples.cloudbalancing.domain.CloudComputer;
import org.optaplanner.examples.cloudbalancing.domain.CloudProcess;
import org.optaplanner.examples.common.swingui.SolutionPanel;

public class CloudBalancingPanel extends SolutionPanel {

    public static final String LOGO_PATH = "/org/optaplanner/examples/cloudbalancing/swingui/cloudBalancingLogo.png";

    private final ImageIcon cloudComputerIcon;
    private final ImageIcon deleteCloudComputerIcon;

    private JPanel computersPanel;

    private CloudComputerPanel unassignedPanel;
    private Map<CloudComputer, CloudComputerPanel> computerToPanelMap;

    private int maximumComputerCpuPower;
    private int maximumComputerMemory;
    private int maximumComputerNetworkBandwidth;

    public CloudBalancingPanel() {
        cloudComputerIcon = new ImageIcon(getClass().getResource("cloudComputer.png"));
        deleteCloudComputerIcon = new ImageIcon(getClass().getResource("deleteCloudComputer.png"));
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

    public ImageIcon getCloudComputerIcon() {
        return cloudComputerIcon;
    }

    public ImageIcon getDeleteCloudComputerIcon() {
        return deleteCloudComputerIcon;
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
        computerToPanelMap = new LinkedHashMap<CloudComputer, CloudComputerPanel>();
        return computersPanel;
    }

    @Override
    public boolean isRefreshScreenDuringSolving() {
        return true;
    }

    private CloudBalance getCloudBalance() {
        return (CloudBalance) solutionBusiness.getSolution();
    }

    public void resetPanel(Solution solution) {
        CloudBalance cloudBalance = (CloudBalance) solution;
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
        updatePanel(solution);
    }

    @Override
    public void updatePanel(Solution solution) {
        CloudBalance cloudBalance = (CloudBalance) solution;
        Set<CloudComputer> deadCloudComputerSet = new LinkedHashSet<CloudComputer>(computerToPanelMap.keySet());
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

    public void deleteComputer(final CloudComputer computer) {
        logger.info("Scheduling delete of computer ({}).", computer);
        solutionBusiness.doProblemFactChange(new ProblemFactChange() {
            public void doChange(ScoreDirector scoreDirector) {
                CloudBalance cloudBalance = (CloudBalance) scoreDirector.getWorkingSolution();
                // First remove the planning fact from all planning entities that use it
                for (CloudProcess process : cloudBalance.getProcessList()) {
                    if (ObjectUtils.equals(process.getComputer(), computer)) {
                        // TODO HACK we are removing it because it becomes uninitialized,
                        // which means it has to be retracted
                        // This is nonsense from a ProblemFactChange point of view, FIXME!
                        scoreDirector.beforeEntityRemoved(process);
                        process.setComputer(null);
                        scoreDirector.afterEntityRemoved(process);
                    }
                }
                // A SolutionCloner does not clone problem fact lists (such as computerList)
                // Shallow clone the computerList so only workingSolution is affected, not bestSolution or guiSolution
                cloudBalance.setComputerList(new ArrayList<CloudComputer>(cloudBalance.getComputerList()));
                // Next remove it the planning fact itself
                for (Iterator<CloudComputer> it = cloudBalance.getComputerList().iterator(); it.hasNext(); ) {
                    CloudComputer workingComputer = it.next();
                    if (ObjectUtils.equals(workingComputer, computer)) {
                        scoreDirector.beforeProblemFactRemoved(workingComputer);
                        it.remove(); // remove from list
                        scoreDirector.beforeProblemFactRemoved(workingComputer);
                        break;
                    }
                }
            }
        });
        updatePanel(solutionBusiness.getSolution());
    }

    public JButton createButton(CloudProcess process) {
        JButton button = new JButton(new CloudProcessAction(process));
        button.setMargin(new Insets(0, 0, 0, 0));
        return button;
    }

    private class CloudProcessAction extends AbstractAction {

        private CloudProcess process;

        public CloudProcessAction(CloudProcess process) {
            super(process.getLabel());
            this.process = process;
        }

        public void actionPerformed(ActionEvent e) {
            JPanel listFieldsPanel = new JPanel(new GridLayout(1, 2));
            listFieldsPanel.add(new JLabel("Computer:"));
            List<CloudComputer> periodList = getCloudBalance().getComputerList();
            JComboBox computerListField = new JComboBox(periodList.toArray());
            computerListField.setSelectedItem(process.getComputer());
            listFieldsPanel.add(computerListField);
            int result = JOptionPane.showConfirmDialog(CloudBalancingPanel.this.getRootPane(), listFieldsPanel,
                    "Select computer", JOptionPane.OK_CANCEL_OPTION);
            if (result == JOptionPane.OK_OPTION) {
                CloudComputer toComputer = (CloudComputer) computerListField.getSelectedItem();
                if (process.getComputer() != toComputer) {
                    solutionBusiness.doChangeMove(process, "computer", toComputer);
                }
                solverAndPersistenceFrame.resetScreen();
            }
        }

    }

}
