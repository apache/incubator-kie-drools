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

package org.optaplanner.examples.tsp.app;

import org.optaplanner.core.config.solver.XmlSolverFactory;
import org.optaplanner.core.api.solver.Solver;
import org.optaplanner.examples.common.app.CommonApp;
import org.optaplanner.examples.common.persistence.AbstractSolutionImporter;
import org.optaplanner.examples.common.persistence.SolutionDao;
import org.optaplanner.examples.common.swingui.SolutionPanel;
import org.optaplanner.examples.tsp.persistence.TspDaoImpl;
import org.optaplanner.examples.tsp.persistence.TspSolutionImporter;
import org.optaplanner.examples.tsp.swingui.TspPanel;

public class TspApp extends CommonApp {

    public static final String SOLVER_CONFIG
            = "/org/optaplanner/examples/tsp/solver/tspSolverConfig.xml";

    public static void main(String[] args) {
        fixateLookAndFeel();
        new TspApp().init();
    }

    @Override
    protected Solver createSolver() {
        XmlSolverFactory solverFactory = new XmlSolverFactory();
        solverFactory.configure(SOLVER_CONFIG);
        return solverFactory.buildSolver();
    }

    @Override
    protected SolutionPanel createSolutionPanel() {
        return new TspPanel();
    }

    @Override
    protected SolutionDao createSolutionDao() {
        return new TspDaoImpl();
    }

    @Override
    protected AbstractSolutionImporter createSolutionImporter() {
        return new TspSolutionImporter();
    }

}
