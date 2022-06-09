package org.optaplanner.examples.machinereassignment.persistence;

import org.optaplanner.examples.machinereassignment.domain.MachineReassignment;
import org.optaplanner.persistence.xstream.impl.domain.solution.XStreamSolutionFileIO;

public class MachineReassignmentXmlSolutionFileIO extends XStreamSolutionFileIO<MachineReassignment> {

    public MachineReassignmentXmlSolutionFileIO() {
        super(MachineReassignment.class);
    }
}
