/*
 * Copyright 2011 JBoss Inc
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

package org.drools.planner.examples.machinereassignment.persistence;

import java.io.File;

import org.drools.planner.core.solution.ProblemIO;
import org.drools.planner.core.solution.Solution;

public class MachineReassignmentProblemIO implements ProblemIO {

    private MachineReassignmentSolutionImporter importer = new MachineReassignmentSolutionImporter();
    private MachineReassignmentSolutionExporter exporter = new MachineReassignmentSolutionExporter();

    public String getFileExtension() {
        // In sync with importer.getInputFileSuffix() and exporter.getOutputFileSuffix()
        return "txt";
    }

    public Solution read(File inputSolutionFile) {
        return importer.readSolution(inputSolutionFile);
    }

    public void write(Solution solution, File outputSolutionFile) {
        exporter.writeSolution(solution, outputSolutionFile);
    }

}
