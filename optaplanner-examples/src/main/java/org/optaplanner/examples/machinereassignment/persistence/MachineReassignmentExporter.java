package org.optaplanner.examples.machinereassignment.persistence;

import java.io.IOException;
import java.util.List;

import org.optaplanner.examples.common.persistence.AbstractTxtSolutionExporter;
import org.optaplanner.examples.common.persistence.SolutionConverter;
import org.optaplanner.examples.machinereassignment.app.MachineReassignmentApp;
import org.optaplanner.examples.machinereassignment.domain.MachineReassignment;
import org.optaplanner.examples.machinereassignment.domain.MrMachine;
import org.optaplanner.examples.machinereassignment.domain.MrProcessAssignment;

public class MachineReassignmentExporter extends AbstractTxtSolutionExporter<MachineReassignment> {

    public static void main(String[] args) {
        SolutionConverter<MachineReassignment> converter =
                SolutionConverter.createExportConverter(MachineReassignmentApp.DATA_DIR_NAME,
                        new MachineReassignmentExporter(), new MachineReassignmentSolutionFileIO());
        converter.convertAll();
    }

    @Override
    public String getOutputFileSuffix() {
        return "txt";
    }

    @Override
    public TxtOutputBuilder<MachineReassignment> createTxtOutputBuilder() {
        return new MachineReassignmentOutputBuilder();
    }

    public static class MachineReassignmentOutputBuilder extends TxtOutputBuilder<MachineReassignment> {

        @Override
        public void writeSolution() throws IOException {
            boolean first = true;
            List<MrMachine> machineList = solution.getMachineList();
            for (MrProcessAssignment processAssignment : solution.getProcessAssignmentList()) {
                if (first) {
                    first = false;
                } else {
                    bufferedWriter.write(" ");
                }
                bufferedWriter.write(Integer.toString(machineList.indexOf(processAssignment.getMachine())));
            }
        }

    }

}
