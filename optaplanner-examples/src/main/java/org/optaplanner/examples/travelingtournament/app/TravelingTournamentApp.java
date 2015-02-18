/*
 * Copyright 2010 JBoss Inc
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

package org.optaplanner.examples.travelingtournament.app;

import org.optaplanner.core.api.solver.Solver;
import org.optaplanner.core.api.solver.SolverFactory;
import org.optaplanner.examples.common.app.CommonApp;
import org.optaplanner.examples.common.persistence.AbstractSolutionExporter;
import org.optaplanner.examples.common.persistence.AbstractSolutionImporter;
import org.optaplanner.examples.common.persistence.SolutionDao;
import org.optaplanner.examples.common.swingui.SolutionPanel;
import org.optaplanner.examples.travelingtournament.persistence.TravelingTournamentDao;
import org.optaplanner.examples.travelingtournament.persistence.TravelingTournamentExporter;
import org.optaplanner.examples.travelingtournament.persistence.TravelingTournamentImporter;
import org.optaplanner.examples.travelingtournament.swingui.TravelingTournamentPanel;

public class TravelingTournamentApp extends CommonApp {

    public static final String SOLVER_CONFIG
            = "org/optaplanner/examples/travelingtournament/solver/travelingTournamentSolverConfig.xml";

    public static void main(String[] args) {
        prepareSwingEnvironment();
        new TravelingTournamentApp().init();
    }

    public TravelingTournamentApp() {
        super("Traveling tournament",
                "Official competition name: TTP - Traveling tournament problem\n\n" +
                        "Assign sport matches to days. Minimize the distance travelled.",
                TravelingTournamentPanel.LOGO_PATH);
    }

    @Override
    protected Solver createSolver() {
        SolverFactory solverFactory = SolverFactory.createFromXmlResource(SOLVER_CONFIG);
        return solverFactory.buildSolver();
    }

    @Override
    protected SolutionPanel createSolutionPanel() {
        return new TravelingTournamentPanel();
    }

    @Override
    protected SolutionDao createSolutionDao() {
        return new TravelingTournamentDao();
    }

    @Override
    protected AbstractSolutionImporter createSolutionImporter() {
        return new TravelingTournamentImporter();
    }

    @Override
    protected AbstractSolutionExporter createSolutionExporter() {
        return new TravelingTournamentExporter();
    }

}
