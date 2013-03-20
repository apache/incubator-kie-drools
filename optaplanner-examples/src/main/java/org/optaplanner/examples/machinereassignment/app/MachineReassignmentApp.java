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

package org.optaplanner.examples.machinereassignment.app;

import org.optaplanner.core.config.solver.XmlSolverFactory;
import org.optaplanner.core.api.solver.Solver;
import org.optaplanner.examples.common.app.CommonApp;
import org.optaplanner.examples.common.persistence.AbstractSolutionExporter;
import org.optaplanner.examples.common.persistence.AbstractSolutionImporter;
import org.optaplanner.examples.common.persistence.SolutionDao;
import org.optaplanner.examples.common.swingui.SolutionPanel;
import org.optaplanner.examples.machinereassignment.persistence.MachineReassignmentDaoImpl;
import org.optaplanner.examples.machinereassignment.persistence.MachineReassignmentSolutionExporter;
import org.optaplanner.examples.machinereassignment.persistence.MachineReassignmentSolutionImporter;
import org.optaplanner.examples.machinereassignment.swingui.MachineReassignmentPanel;

public class MachineReassignmentApp extends CommonApp {

    public static final String SOLVER_CONFIG
            = "/org/optaplanner/examples/machinereassignment/solver/machineReassignmentSolverConfig.xml";

    public static void main(String[] args) {
        fixateLookAndFeel();
        new MachineReassignmentApp().init();
    }

    @Override
    protected Solver createSolver() {
        XmlSolverFactory solverFactory = new XmlSolverFactory();
        solverFactory.configure(SOLVER_CONFIG);
        return solverFactory.buildSolver();
    }

    @Override
    protected SolutionPanel createSolutionPanel() {
        return new MachineReassignmentPanel();
    }

    @Override
    protected SolutionDao createSolutionDao() {
        return new MachineReassignmentDaoImpl();
    }

    @Override
    protected AbstractSolutionImporter createSolutionImporter() {
        return new MachineReassignmentSolutionImporter();
    }

    @Override
    protected AbstractSolutionExporter createSolutionExporter() {
        return new MachineReassignmentSolutionExporter();
    }

}
