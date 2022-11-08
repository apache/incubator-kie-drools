package org.optaplanner.examples.machinereassignment.persistence;

import java.io.File;
import java.util.LinkedHashMap;
import java.util.Map;
import java.util.function.Function;
import java.util.stream.Collectors;

import org.optaplanner.examples.machinereassignment.domain.MachineReassignment;
import org.optaplanner.examples.machinereassignment.domain.MrMachine;
import org.optaplanner.persistence.jackson.impl.domain.solution.JacksonSolutionFileIO;

public class MachineReassignmentSolutionFileIO extends JacksonSolutionFileIO<MachineReassignment> {

    public MachineReassignmentSolutionFileIO() {
        super(MachineReassignment.class);
    }

    @Override
    public MachineReassignment read(File inputSolutionFile) {
        MachineReassignment machineReassignment = super.read(inputSolutionFile);
        /*
         * Replace the duplicate MrMachine instances in the machineMoveCostMap by references to instances from
         * the machineList.
         */
        Map<Long, MrMachine> machinesById = machineReassignment.getMachineList().stream()
                .collect(Collectors.toMap(MrMachine::getId, Function.identity()));
        for (MrMachine machine : machineReassignment.getMachineList()) {
            Map<MrMachine, Integer> originalCostMap = machine.getMachineMoveCostMap();
            Map<MrMachine, Integer> newCostMap = new LinkedHashMap<>(originalCostMap.size());
            originalCostMap.forEach((otherMachine, cost) -> newCostMap.put(machinesById.get(otherMachine.getId()), cost));
            machine.setMachineMoveCostMap(newCostMap);
        }
        return machineReassignment;
    }
}
