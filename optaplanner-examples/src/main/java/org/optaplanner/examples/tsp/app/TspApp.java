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

package org.optaplanner.examples.tsp.app;

import org.optaplanner.core.api.solver.Solver;
import org.optaplanner.core.api.solver.SolverFactory;
import org.optaplanner.examples.common.app.CommonApp;
import org.optaplanner.examples.common.persistence.AbstractSolutionExporter;
import org.optaplanner.examples.common.persistence.AbstractSolutionImporter;
import org.optaplanner.examples.common.persistence.SolutionDao;
import org.optaplanner.examples.common.swingui.SolutionPanel;
import org.optaplanner.examples.tsp.domain.TravelingSalesmanTour;
import org.optaplanner.examples.tsp.persistence.TspDao;
import org.optaplanner.examples.tsp.persistence.TspExporter;
import org.optaplanner.examples.tsp.persistence.TspImageStipplerImporter;
import org.optaplanner.examples.tsp.persistence.TspImporter;
import org.optaplanner.examples.tsp.swingui.TspPanel;

public class TspApp extends CommonApp<TravelingSalesmanTour> {

    public static final String SOLVER_CONFIG
            = "org/optaplanner/examples/tsp/solver/tspSolverConfig.xml";

    public static void main(String[] args) {
        prepareSwingEnvironment();
        new TspApp().init();
    }

    public TspApp() {
        super("Traveling salesman",
                "Official competition name: TSP - Traveling salesman problem\n\n" +
                        "Determine the order in which to visit all cities.\n\n" +
                        "Find the shortest route to visit all cities.",
                SOLVER_CONFIG,
                TspPanel.LOGO_PATH);
    }

    @Override
    protected SolutionPanel createSolutionPanel() {
        return new TspPanel();
    }

    @Override
    protected SolutionDao createSolutionDao() {
        return new TspDao();
    }

    @Override
    protected AbstractSolutionImporter[] createSolutionImporters() {
        return new AbstractSolutionImporter[]{
                new TspImporter(),
                new TspImageStipplerImporter()
        };
    }

    @Override
    protected AbstractSolutionExporter createSolutionExporter() {
        return new TspExporter();
    }

}
