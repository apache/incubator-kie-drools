/*
 * Copyright 2013 Red Hat, Inc. and/or its affiliates.
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

import java.io.File;
import java.util.function.Predicate;

import org.optaplanner.examples.common.persistence.AbstractSolutionImporter;
import org.optaplanner.examples.common.persistence.ImportDataFilesTest;
import org.optaplanner.examples.machinereassignment.app.MachineReassignmentApp;
import org.optaplanner.examples.machinereassignment.domain.MachineReassignment;

public class MachineReassignmentImporterTest extends ImportDataFilesTest<MachineReassignment> {

    @Override
    protected AbstractSolutionImporter<MachineReassignment> createSolutionImporter() {
        return new MachineReassignmentImporter();
    }

    @Override
    protected String getDataDirName() {
        return MachineReassignmentApp.DATA_DIR_NAME;
    }

    @Override
    protected Predicate<File> dataFileInclusionFilter() {
        // The dataset B10 requires more than 1GB heap space on JDK 6 to load (not on JDK 7)
        return file -> !file.getName().equals("model_b_10.txt");
    }
}
