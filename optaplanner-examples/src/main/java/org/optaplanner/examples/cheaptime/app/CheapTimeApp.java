/*
 * Copyright 2014 Red Hat, Inc. and/or its affiliates.
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

package org.optaplanner.examples.cheaptime.app;

import org.optaplanner.core.api.solver.Solver;
import org.optaplanner.core.api.solver.SolverFactory;
import org.optaplanner.examples.cheaptime.domain.CheapTimeSolution;
import org.optaplanner.examples.cheaptime.persistence.CheapTimeDao;
import org.optaplanner.examples.cheaptime.persistence.CheapTimeExporter;
import org.optaplanner.examples.cheaptime.persistence.CheapTimeImporter;
import org.optaplanner.examples.cheaptime.swingui.CheapTimePanel;
import org.optaplanner.examples.common.app.CommonApp;
import org.optaplanner.examples.common.persistence.AbstractSolutionExporter;
import org.optaplanner.examples.common.persistence.AbstractSolutionImporter;
import org.optaplanner.examples.common.persistence.SolutionDao;
import org.optaplanner.examples.common.swingui.SolutionPanel;

public class CheapTimeApp extends CommonApp<CheapTimeSolution> {

    public static final String SOLVER_CONFIG
            = "org/optaplanner/examples/cheaptime/solver/cheapTimeSolverConfig.xml";

    public static void main(String[] args) {
        prepareSwingEnvironment();
        new CheapTimeApp().init();
    }

    public CheapTimeApp() {
        super("Cheap time scheduling",
                "Official competition name: ICON Challenge on Forecasting and Scheduling\n\n" +
                "Assign tasks to machines and time.\n\n" +
                "Each machine must have enough hardware to run all of its tasks.\n" +
                "Each task and machine consumes power. The power price differs over time.\n" +
                "Minimize the power cost.",
                SOLVER_CONFIG,
                CheapTimePanel.LOGO_PATH);
    }

    @Override
    protected SolutionPanel createSolutionPanel() {
        return new CheapTimePanel();
    }

    @Override
    protected SolutionDao createSolutionDao() {
        return new CheapTimeDao();
    }

    @Override
    protected AbstractSolutionImporter[] createSolutionImporters() {
        return new AbstractSolutionImporter[]{
                new CheapTimeImporter()
        };
    }

    @Override
    protected AbstractSolutionExporter createSolutionExporter() {
        return new CheapTimeExporter();
    }

}
