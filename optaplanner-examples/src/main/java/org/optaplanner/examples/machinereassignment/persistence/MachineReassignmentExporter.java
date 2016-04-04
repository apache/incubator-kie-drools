/*
 * Copyright 2011 Red Hat, Inc. and/or its affiliates.
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

package org.optaplanner.examples.machinereassignment.persistence;

import java.io.IOException;
import java.util.List;

import org.optaplanner.examples.common.persistence.AbstractTxtSolutionExporter;
import org.optaplanner.examples.machinereassignment.domain.MachineReassignment;
import org.optaplanner.examples.machinereassignment.domain.MrMachine;
import org.optaplanner.examples.machinereassignment.domain.MrProcessAssignment;

public class MachineReassignmentExporter extends AbstractTxtSolutionExporter<MachineReassignment> {

    public static void main(String[] args) {
        new MachineReassignmentExporter().convertAll();
    }

    public MachineReassignmentExporter() {
        super(new MachineReassignmentDao());
    }

    @Override
    public String getOutputFileSuffix() {
        return MachineReassignmentFileIO.FILE_EXTENSION;
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
