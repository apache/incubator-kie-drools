/*
 * Copyright 2015 Red Hat, Inc. and/or its affiliates.
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

package org.optaplanner.examples.coachshuttlegathering.app;

import org.optaplanner.core.api.solver.Solver;
import org.optaplanner.core.api.solver.SolverFactory;
import org.optaplanner.examples.coachshuttlegathering.domain.CoachShuttleGatheringSolution;
import org.optaplanner.examples.coachshuttlegathering.persistence.CoachShuttleGatheringDao;
import org.optaplanner.examples.coachshuttlegathering.persistence.CoachShuttleGatheringExporter;
import org.optaplanner.examples.coachshuttlegathering.persistence.CoachShuttleGatheringImporter;
import org.optaplanner.examples.coachshuttlegathering.swingui.CoachShuttleGatheringPanel;
import org.optaplanner.examples.common.app.CommonApp;
import org.optaplanner.examples.common.persistence.AbstractSolutionExporter;
import org.optaplanner.examples.common.persistence.AbstractSolutionImporter;
import org.optaplanner.examples.common.persistence.SolutionDao;
import org.optaplanner.examples.common.swingui.SolutionPanel;

public class CoachShuttleGatheringApp extends CommonApp<CoachShuttleGatheringSolution> {

    public static final String SOLVER_CONFIG
            = "org/optaplanner/examples/coachshuttlegathering/solver/coachShuttleGatheringSolverConfig.xml";

    public static void main(String[] args) {
        prepareSwingEnvironment();
        new CoachShuttleGatheringApp().init();
    }

    public CoachShuttleGatheringApp() {
        super("Coach shuttle gathering",
                "Transport passengers to a hub by using coaches and shuttles.",
                SOLVER_CONFIG,
                CoachShuttleGatheringPanel.LOGO_PATH);
    }

    @Override
    protected SolutionPanel createSolutionPanel() {
        return new CoachShuttleGatheringPanel();
    }

    @Override
    protected SolutionDao createSolutionDao() {
        return new CoachShuttleGatheringDao();
    }

    @Override
    protected AbstractSolutionImporter[] createSolutionImporters() {
        return new AbstractSolutionImporter[]{
                new CoachShuttleGatheringImporter()
        };
    }

    @Override
    protected AbstractSolutionExporter createSolutionExporter() {
        return new CoachShuttleGatheringExporter();
    }

}
