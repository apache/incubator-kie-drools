package org.optaplanner.examples.machinereassignment.persistence;

import java.io.File;

import org.optaplanner.examples.common.persistence.AbstractJsonSolutionFileIO;
import org.optaplanner.examples.machinereassignment.domain.MachineReassignment;
import org.optaplanner.examples.machinereassignment.domain.MrMachine;

public class MachineReassignmentSolutionFileIO extends AbstractJsonSolutionFileIO<MachineReassignment> {

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
        deduplicateEntities(machineReassignment, MachineReassignment::getMachineList, MrMachine::getId,
                MrMachine::getMachineMoveCostMap, MrMachine::setMachineMoveCostMap);
        return machineReassignment;
    }
}
