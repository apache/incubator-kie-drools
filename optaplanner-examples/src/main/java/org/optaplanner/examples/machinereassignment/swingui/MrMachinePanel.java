package org.optaplanner.examples.machinereassignment.swingui;

import java.awt.BorderLayout;
import java.awt.Color;
import java.awt.Dimension;
import java.awt.GridLayout;
import java.util.ArrayList;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;

import javax.swing.BorderFactory;
import javax.swing.JButton;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.JTextField;

import org.optaplanner.examples.machinereassignment.domain.MrMachine;
import org.optaplanner.examples.machinereassignment.domain.MrMachineCapacity;
import org.optaplanner.examples.machinereassignment.domain.MrProcessAssignment;
import org.optaplanner.examples.machinereassignment.domain.MrResource;
import org.optaplanner.swing.impl.SwingUtils;
import org.optaplanner.swing.impl.TangoColorFactory;

public class MrMachinePanel extends JPanel {

    private final MachineReassignmentPanel machineReassignmentPanel;
    private List<MrResource> resourceList;
    private MrMachine machine;
    private List<MrProcessAssignment> processAssignmentList = new ArrayList<>();

    private JLabel machineLabel;
    private JPanel resourceListPanel = null;
    private Map<MrResource, JTextField> resourceFieldMap;
    private JLabel numberOfProcessesLabel;

    public MrMachinePanel(MachineReassignmentPanel machineReassignmentPanel, List<MrResource> resourceList,
            MrMachine machine) {
        super(new BorderLayout());
        this.machineReassignmentPanel = machineReassignmentPanel;
        this.resourceList = resourceList;
        this.machine = machine;
        setBorder(BorderFactory.createCompoundBorder(
                BorderFactory.createCompoundBorder(
                        BorderFactory.createEmptyBorder(1, 2, 1, 2),
                        BorderFactory.createLineBorder(Color.BLACK)),
                BorderFactory.createEmptyBorder(1, 1, 1, 1)));
        createUI();
    }

    public MrMachine getMachine() {
        return machine;
    }

    private String getMachineLabel() {
        return machine == null ? "Unassigned" : machine.getLabel();
    }

    public void setResourceList(List<MrResource> resourceList) {
        this.resourceList = resourceList;
        resetResourceListPanel();
    }

    private void createUI() {
        JPanel labelAndDeletePanel = new JPanel(new BorderLayout());
        labelAndDeletePanel.setPreferredSize(new Dimension(150, 20));
        machineLabel = new JLabel(getMachineLabel());
        machineLabel.setEnabled(false);
        labelAndDeletePanel.add(machineLabel, BorderLayout.CENTER);
        if (machine != null) {
            JButton deleteButton = SwingUtils.makeSmallButton(new JButton("X"));
            deleteButton.setToolTipText("Delete");
            deleteButton.addActionListener(e -> machineReassignmentPanel.deleteMachine(machine));
            deleteButton.setToolTipText("Delete");
            labelAndDeletePanel.add(deleteButton, BorderLayout.EAST);
        }
        add(labelAndDeletePanel, BorderLayout.WEST);
        resetResourceListPanel();
        JPanel numberAndDetailsPanel = new JPanel(new BorderLayout());
        numberOfProcessesLabel = new JLabel("0 processes ", JLabel.RIGHT);
        numberOfProcessesLabel.setPreferredSize(new Dimension(100, 20));
        numberOfProcessesLabel.setEnabled(false);
        numberAndDetailsPanel.add(numberOfProcessesLabel, BorderLayout.WEST);
        add(numberAndDetailsPanel, BorderLayout.EAST);
    }

    public void resetResourceListPanel() {
        if (resourceListPanel != null) {
            remove(resourceListPanel);
        }
        resourceListPanel = new JPanel(new GridLayout(1, resourceList.size()));
        resourceFieldMap = new LinkedHashMap<>(resourceList.size());
        for (MrResource resource : resourceList) {
            long maximumCapacity = machine == null ? 0L : machine.getMachineCapacity(resource).getMaximumCapacity();
            JTextField resourceField = new JTextField("0 / " + maximumCapacity);
            resourceFieldMap.put(resource, resourceField);
            resourceField.setEditable(false);
            resourceField.setEnabled(false);
            resourceListPanel.add(resourceField);
        }
        add(resourceListPanel, BorderLayout.CENTER);
    }

    public void addProcessAssignment(MrProcessAssignment processAssignment) {
        processAssignmentList.add(processAssignment);
    }

    public void removeProcessAssignment(MrProcessAssignment processAssignment) {
        processAssignmentList.remove(processAssignment);
    }

    public void clearProcessAssignments() {
        processAssignmentList.clear();
    }

    public void update() {
        updateTotals();
    }

    private void updateTotals() {
        boolean used = processAssignmentList.size() > 0;
        machineLabel.setEnabled(used);
        for (MrResource resource : resourceList) {
            JTextField resourceField = resourceFieldMap.get(resource);
            long maximumCapacity;
            long safetyCapacity;
            if (machine != null) {
                MrMachineCapacity machineCapacity = machine.getMachineCapacity(resource);
                maximumCapacity = machineCapacity.getMaximumCapacity();
                safetyCapacity = machineCapacity.getSafetyCapacity();
            } else {
                maximumCapacity = 0L;
                safetyCapacity = 0L;
            }
            long usedTotal = 0L;
            for (MrProcessAssignment processAssignment : processAssignmentList) {
                usedTotal += processAssignment.getProcess().getProcessRequirement(resource).getUsage();
            }
            resourceField.setText(usedTotal + " / " + maximumCapacity);
            resourceField.setForeground(usedTotal > maximumCapacity ? TangoColorFactory.SCARLET_3
                    : (usedTotal > safetyCapacity ? TangoColorFactory.ORANGE_3 : Color.BLACK));
            resourceField.setEnabled(used);
        }
        numberOfProcessesLabel.setText(processAssignmentList.size() + " processes ");
        numberOfProcessesLabel.setEnabled(used);
    }

}
