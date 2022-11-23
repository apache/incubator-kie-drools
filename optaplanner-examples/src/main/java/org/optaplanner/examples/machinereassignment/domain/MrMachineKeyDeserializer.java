package org.optaplanner.examples.machinereassignment.domain;

import org.optaplanner.examples.common.persistence.jackson.AbstractKeyDeserializer;
import org.optaplanner.examples.machinereassignment.persistence.MachineReassignmentSolutionFileIO;

/**
 * @see MachineReassignmentSolutionFileIO
 */
final class MrMachineKeyDeserializer extends AbstractKeyDeserializer<MrMachine> {

    public MrMachineKeyDeserializer() {
        super(MrMachine.class);
    }

    @Override
    protected MrMachine createInstance(long id) {
        return new MrMachine(id);
    }
}
