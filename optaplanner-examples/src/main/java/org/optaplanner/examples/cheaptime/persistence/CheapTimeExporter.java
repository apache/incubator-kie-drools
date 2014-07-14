/*
 * Copyright 2014 JBoss Inc
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

package org.optaplanner.examples.cheaptime.persistence;

import java.io.IOException;
import java.util.List;

import org.optaplanner.core.api.domain.solution.Solution;
import org.optaplanner.examples.cheaptime.domain.CheapTimeSolution;
import org.optaplanner.examples.cheaptime.domain.Machine;
import org.optaplanner.examples.cheaptime.domain.TaskAssignment;
import org.optaplanner.examples.common.persistence.AbstractTxtSolutionExporter;

public class CheapTimeExporter extends AbstractTxtSolutionExporter {

    public static void main(String[] args) {
        new CheapTimeExporter().convertAll();
    }

    public CheapTimeExporter() {
        super(new CheapTimeDao());
    }

    @Override
    public String getOutputFileSuffix() {
        return CheapTimeSolutionFileIO.FILE_EXTENSION;
    }

    public TxtOutputBuilder createTxtOutputBuilder() {
        return new CheapTimeOutputBuilder();
    }

    public static class CheapTimeOutputBuilder extends TxtOutputBuilder {

        private CheapTimeSolution solution;

        public void setSolution(Solution solution) {
            this.solution = (CheapTimeSolution) solution;
        }

        public void writeSolution() throws IOException {
            List<Machine> machineList = solution.getMachineList();
            bufferedWriter.write(machineList.size() + "\n");
            for (Machine machine : machineList) {
                bufferedWriter.write(machine.getId() + "\n");
                throw new UnsupportedOperationException("TODO implement me"); // TODO implement me
            }
            List<TaskAssignment> taskAssignmentList = solution.getTaskAssignmentList();
            bufferedWriter.write(taskAssignmentList.size() + "\n");
            for (TaskAssignment taskAssignment : taskAssignmentList) {
                bufferedWriter.write(taskAssignment.getTask().getId() + " "
                        + taskAssignment.getMachine().getId() + " "
                        + taskAssignment.getStartPeriod() + "\n");
            }
        }

    }

}
