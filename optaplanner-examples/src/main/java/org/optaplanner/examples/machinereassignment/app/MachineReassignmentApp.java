/*
 * Licensed to the Apache Software Foundation (ASF) under one
 * or more contributor license agreements.  See the NOTICE file
 * distributed with this work for additional information
 * regarding copyright ownership.  The ASF licenses this file
 * to you under the Apache License, Version 2.0 (the
 * "License"); you may not use this file except in compliance
 * with the License.  You may obtain a copy of the License at
 *
 *   http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing,
 * software distributed under the License is distributed on an
 * "AS IS" BASIS, WITHOUT WARRANTIES OR CONDITIONS OF ANY
 * KIND, either express or implied.  See the License for the
 * specific language governing permissions and limitations
 * under the License.
 */

package org.optaplanner.examples.machinereassignment.app;

import java.util.Collections;
import java.util.Set;

import org.optaplanner.examples.common.app.CommonApp;
import org.optaplanner.examples.common.persistence.AbstractSolutionExporter;
import org.optaplanner.examples.common.persistence.AbstractSolutionImporter;
import org.optaplanner.examples.machinereassignment.domain.MachineReassignment;
import org.optaplanner.examples.machinereassignment.persistence.MachineReassignmentExporter;
import org.optaplanner.examples.machinereassignment.persistence.MachineReassignmentImporter;
import org.optaplanner.examples.machinereassignment.persistence.MachineReassignmentSolutionFileIO;
import org.optaplanner.examples.machinereassignment.swingui.MachineReassignmentPanel;
import org.optaplanner.persistence.common.api.domain.solution.SolutionFileIO;

public class MachineReassignmentApp extends CommonApp<MachineReassignment> {

    public static final String SOLVER_CONFIG =
            "org/optaplanner/examples/machinereassignment/machineReassignmentSolverConfig.xml";

    public static final String DATA_DIR_NAME = "machinereassignment";

    public static void main(String[] args) {
        prepareSwingEnvironment();
        new MachineReassignmentApp().init();
    }

    public MachineReassignmentApp() {
        super("Machine reassignment",
                "Official competition name: Google ROADEF 2012 - Machine reassignment\n\n" +
                        "Reassign processes to machines.",
                SOLVER_CONFIG, DATA_DIR_NAME,
                MachineReassignmentPanel.LOGO_PATH);
    }

    @Override
    protected MachineReassignmentPanel createSolutionPanel() {
        return new MachineReassignmentPanel();
    }

    @Override
    public SolutionFileIO<MachineReassignment> createSolutionFileIO() {
        return new MachineReassignmentSolutionFileIO();
    }

    @Override
    protected Set<AbstractSolutionImporter<MachineReassignment>> createSolutionImporters() {
        return Collections.singleton(new MachineReassignmentImporter());
    }

    @Override
    protected Set<AbstractSolutionExporter<MachineReassignment>> createSolutionExporters() {
        return Collections.singleton(new MachineReassignmentExporter());
    }

}
