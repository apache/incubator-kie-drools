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

package org.optaplanner.examples.vehiclerouting.persistence;

import java.io.File;

import org.optaplanner.core.impl.solution.ProblemIO;
import org.optaplanner.core.impl.solution.Solution;

public class VehicleRoutingProblemIO implements ProblemIO {

    public static final String FILE_EXTENSION = "vrp";

    private VehicleRoutingImporter importer = new VehicleRoutingImporter();

    public String getFileExtension() {
        return "vrp";
    }

    public Solution read(File inputSolutionFile) {
        return importer.readSolution(inputSolutionFile);
    }

    public void write(Solution solution, File outputSolutionFile) {
        throw new UnsupportedOperationException();
    }

}
