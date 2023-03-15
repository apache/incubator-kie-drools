package org.optaplanner.examples.machinereassignment.swingui;

import java.awt.GridLayout;
import java.util.ArrayList;
import java.util.Collections;
import java.util.LinkedHashMap;
import java.util.LinkedHashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;

import javax.swing.GroupLayout;
import javax.swing.JLabel;
import javax.swing.JPanel;

import org.optaplanner.examples.common.swingui.SolutionPanel;
import org.optaplanner.examples.machinereassignment.domain.MachineReassignment;
import org.optaplanner.examples.machinereassignment.domain.MrMachine;
import org.optaplanner.examples.machinereassignment.domain.MrProcessAssignment;
import org.optaplanner.examples.machinereassignment.domain.MrResource;

public class MachineReassignmentPanel extends SolutionPanel<MachineReassignment> {

    public static final String LOGO_PATH = "/org/optaplanner/examples/machinereassignment/swingui/machineReassignmentLogo.png";

    private JPanel machineListPanel;

    private MrMachinePanel unassignedPanel;
    private JLabel tooBigLabel = null;
    private Map<MrMachine, MrMachinePanel> machineToPanelMap;

    public MachineReassignmentPanel() {
        GroupLayout layout = new GroupLayout(this);
        setLayout(layout);
        createMachineListPanel();
        JPanel headerPanel = new JPanel();
        layout.setHorizontalGroup(layout.createParallelGroup()
                .addComponent(headerPanel).addComponent(machineListPanel));
        layout.setVerticalGroup(layout.createSequentialGroup()
                .addComponent(headerPanel, GroupLayout.PREFERRED_SIZE, GroupLayout.PREFERRED_SIZE,
                        GroupLayout.PREFERRED_SIZE)
                .addComponent(machineListPanel, GroupLayout.PREFERRED_SIZE, GroupLayout.PREFERRED_SIZE,
                        GroupLayout.PREFERRED_SIZE));
    }

    private void createMachineListPanel() {
        machineListPanel = new JPanel(new GridLayout(0, 1));
        unassignedPanel = new MrMachinePanel(this, Collections.emptyList(), null);
        machineListPanel.add(unassignedPanel);
        machineToPanelMap = new LinkedHashMap<>();
        machineToPanelMap.put(null, unassignedPanel);
    }

    @Override
    public void resetPanel(MachineReassignment machineReassignment) {
        for (MrMachinePanel machinePanel : machineToPanelMap.values()) {
            if (machinePanel.getMachine() != null) {
                machineListPanel.remove(machinePanel);
            }
        }
        machineToPanelMap.clear();
        machineToPanelMap.put(null, unassignedPanel);
        unassignedPanel.clearProcessAssignments();
        updatePanel(machineReassignment);
    }

    @Override
    public void updatePanel(MachineReassignment machineReassignment) {
        List<MrResource> resourceList = machineReassignment.getResourceList();
        unassignedPanel.setResourceList(resourceList);
        if (machineReassignment.getMachineList().size() > 1000) {
            if (tooBigLabel == null) {
                tooBigLabel = new JLabel("The dataset is too big to show.");
                machineListPanel.add(tooBigLabel);
            }
        } else {
            if (tooBigLabel != null) {
                machineListPanel.remove(tooBigLabel);
                tooBigLabel = null;
            }
            Set<MrMachine> deadMachineSet = new LinkedHashSet<>(machineToPanelMap.keySet());
            deadMachineSet.remove(null);
            for (MrMachine machine : machineReassignment.getMachineList()) {
                deadMachineSet.remove(machine);
                MrMachinePanel machinePanel = machineToPanelMap.get(machine);
                if (machinePanel == null) {
                    machinePanel = new MrMachinePanel(this, resourceList, machine);
                    machineListPanel.add(machinePanel);
                    machineToPanelMap.put(machine, machinePanel);
                }
                machinePanel.clearProcessAssignments();
            }
            unassignedPanel.clearProcessAssignments();
            for (MrProcessAssignment processAssignment : machineReassignment.getProcessAssignmentList()) {
                MrMachine machine = processAssignment.getMachine();
                MrMachinePanel machinePanel = machineToPanelMap.get(machine);
                machinePanel.addProcessAssignment(processAssignment);
            }
            for (MrMachine deadMachine : deadMachineSet) {
                MrMachinePanel deadMachinePanel = machineToPanelMap.remove(deadMachine);
                machineListPanel.remove(deadMachinePanel);
            }
            for (MrMachinePanel machinePanel : machineToPanelMap.values()) {
                machinePanel.update();
            }
        }
    }

    public void deleteMachine(final MrMachine machine) {
        logger.info("Scheduling delete of machine ({}).", machine);
        doProblemChange((machineReassignment, problemChangeDirector) -> problemChangeDirector.lookUpWorkingObject(machine)
                .ifPresentOrElse(workingMachine -> {
                    // First remove the problem fact from all planning entities that use it
                    for (MrProcessAssignment processAssignment : machineReassignment.getProcessAssignmentList()) {
                        if (processAssignment.getOriginalMachine() == workingMachine) {
                            problemChangeDirector.changeProblemProperty(processAssignment,
                                    workingProcessAssignment -> workingProcessAssignment.setOriginalMachine(null));
                        }
                        if (processAssignment.getMachine() == workingMachine) {
                            problemChangeDirector.changeVariable(processAssignment, "machine",
                                    workingProcessAssignment -> workingProcessAssignment.setMachine(null));
                        }
                    }
                    // A SolutionCloner does not clone problem fact lists (such as machineList)
                    // Shallow clone the machineList so only workingSolution is affected, not bestSolution or guiSolution
                    ArrayList<MrMachine> machineList = new ArrayList<>(machineReassignment.getMachineList());
                    machineReassignment.setMachineList(machineList);
                    // Remove it the problem fact itself
                    problemChangeDirector.removeProblemFact(workingMachine, machineList::remove);
                }, () -> logger.info("Skipping problem change due to machine ({}) already deleted.", machine)));
    }

}
