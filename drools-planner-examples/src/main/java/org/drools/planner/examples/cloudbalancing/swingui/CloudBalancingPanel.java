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
import org.drools.planner.core.score.director.ScoreDirector;
import org.drools.planner.core.solution.Solution;
import org.drools.planner.core.solver.ProblemFactChange;
import org.drools.planner.examples.cloudbalancing.domain.CloudProcess;
import org.drools.planner.examples.cloudbalancing.domain.CloudBalance;
import org.drools.planner.examples.cloudbalancing.domain.CloudComputer;
import org.drools.planner.examples.cloudbalancing.solver.move.CloudComputerChangeMove;
import org.drools.planner.examples.common.swingui.SolutionPanel;

public class CloudBalancingPanel extends SolutionPanel {

    public static final String LOGO_PATH = "/org/drools/planner/examples/cloudbalancing/swingui/cloudBalancingLogo.png";

    private JPanel computersPanel;

    private CloudComputerPanel unassignedPanel;
    private Map<CloudComputer, CloudComputerPanel> computerToPanelMap;
    private Map<CloudProcess, CloudComputerPanel> processToPanelMap;

    private int maximumComputerCpuPower;
    private int maximumComputerMemory;
    private int maximumComputerNetworkBandwidth;

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
        processToPanelMap = new LinkedHashMap<CloudProcess, CloudComputerPanel>();
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
        processToPanelMap.clear();
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
        }
        Set<CloudProcess> deadCloudProcessSet = new LinkedHashSet<CloudProcess>(
                processToPanelMap.keySet());
        for (CloudProcess process : cloudBalance.getProcessList()) {
            deadCloudProcessSet.remove(process);
            CloudComputerPanel computerPanel = processToPanelMap.get(process);
            CloudComputer computer = process.getComputer();
            if (computerPanel != null
                    && !ObjectUtils.equals(computerPanel.getComputer(), computer)) {
                processToPanelMap.remove(process);
                computerPanel.removeProcess(process);
                computerPanel = null;
            }
            if (computerPanel == null) {
                computerPanel = computerToPanelMap.get(computer);
                computerPanel.addProcess(process);
                processToPanelMap.put(process, computerPanel);
            }
        }
        for (CloudProcess deadProcess : deadCloudProcessSet) {
            CloudComputerPanel deadComputerPanel = processToPanelMap.remove(deadProcess);
            deadComputerPanel.removeProcess(deadProcess);
        }
        for (CloudComputer deadComputer : deadCloudComputerSet) {
            CloudComputerPanel deadComputerPanel = computerToPanelMap.remove(deadComputer);
            computersPanel.remove(deadComputerPanel);
        }
        for (CloudComputerPanel computerPanel : computerToPanelMap.values()) {
            computerPanel.update();
        }
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

    private class CloudProcessAction extends AbstractAction {

        private CloudProcess process;

        public CloudProcessAction(CloudProcess process) {
            super("=>");
            this.process = process;
        }

        public void actionPerformed(ActionEvent e) {
            List<CloudComputer> computerList = getCloudBalance().getComputerList();
            JComboBox computerListField = new JComboBox(computerList.toArray());
            computerListField.setSelectedItem(process.getComputer());
            int result = JOptionPane.showConfirmDialog(CloudBalancingPanel.this.getRootPane(), computerListField,
                    "Select computer", JOptionPane.OK_CANCEL_OPTION);
            if (result == JOptionPane.OK_OPTION) {
                CloudComputer toComputer = (CloudComputer) computerListField.getSelectedItem();
                solutionBusiness.doMove(new CloudComputerChangeMove(process, toComputer));
                solverAndPersistenceFrame.resetScreen();
            }
        }

    }

}
