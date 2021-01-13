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

package org.optaplanner.examples.machinereassignment.app;

import java.util.HashSet;
import java.util.Set;

import org.optaplanner.examples.common.app.CommonApp;
import org.optaplanner.examples.common.persistence.AbstractSolutionExporter;
import org.optaplanner.examples.common.persistence.AbstractSolutionImporter;
import org.optaplanner.examples.machinereassignment.domain.MachineReassignment;
import org.optaplanner.examples.machinereassignment.persistence.MachineReassignmentExporter;
import org.optaplanner.examples.machinereassignment.persistence.MachineReassignmentImporter;
import org.optaplanner.examples.machinereassignment.persistence.MachineReassignmentXmlSolutionFileIO;
import org.optaplanner.examples.machinereassignment.swingui.MachineReassignmentPanel;
import org.optaplanner.persistence.common.api.domain.solution.SolutionFileIO;

public class MachineReassignmentApp extends CommonApp<MachineReassignment> {

    public static final String SOLVER_CONFIG =
            "org/optaplanner/examples/machinereassignment/solver/machineReassignmentSolverConfig.xml";

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
        return new MachineReassignmentXmlSolutionFileIO();
    }

    @Override
    protected AbstractSolutionImporter[] createSolutionImporters() {
        return new AbstractSolutionImporter[] {
                new MachineReassignmentImporter()
        };
    }

    @Override
    protected Set<AbstractSolutionExporter> createSolutionExporters() {
        Set<AbstractSolutionExporter> exporters = new HashSet<>(1);
        exporters.add(new MachineReassignmentExporter());
        return exporters;
    }

}
