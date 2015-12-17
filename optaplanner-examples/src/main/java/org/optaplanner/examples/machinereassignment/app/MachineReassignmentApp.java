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

import org.optaplanner.core.api.solver.Solver;
import org.optaplanner.core.api.solver.SolverFactory;
import org.optaplanner.examples.common.app.CommonApp;
import org.optaplanner.examples.common.persistence.AbstractSolutionExporter;
import org.optaplanner.examples.common.persistence.AbstractSolutionImporter;
import org.optaplanner.examples.common.persistence.SolutionDao;
import org.optaplanner.examples.common.swingui.SolutionPanel;
import org.optaplanner.examples.machinereassignment.domain.MachineReassignment;
import org.optaplanner.examples.machinereassignment.persistence.MachineReassignmentDao;
import org.optaplanner.examples.machinereassignment.persistence.MachineReassignmentExporter;
import org.optaplanner.examples.machinereassignment.persistence.MachineReassignmentImporter;
import org.optaplanner.examples.machinereassignment.swingui.MachineReassignmentPanel;

public class MachineReassignmentApp extends CommonApp<MachineReassignment> {

    public static final String SOLVER_CONFIG
            = "org/optaplanner/examples/machinereassignment/solver/machineReassignmentSolverConfig.xml";

    public static void main(String[] args) {
        prepareSwingEnvironment();
        new MachineReassignmentApp().init();
    }

    public MachineReassignmentApp() {
        super("Machine reassignment",
                "Official competition name: Google ROADEF 2012 - Machine reassignment\n\n" +
                        "Reassign processes to machines.",
                SOLVER_CONFIG,
                MachineReassignmentPanel.LOGO_PATH);
    }

    @Override
    protected SolutionPanel createSolutionPanel() {
        return new MachineReassignmentPanel();
    }

    @Override
    protected SolutionDao createSolutionDao() {
        return new MachineReassignmentDao();
    }

    @Override
    protected AbstractSolutionImporter[] createSolutionImporters() {
        return new AbstractSolutionImporter[]{
                new MachineReassignmentImporter()
        };
    }

    @Override
    protected AbstractSolutionExporter createSolutionExporter() {
        return new MachineReassignmentExporter();
    }

}
