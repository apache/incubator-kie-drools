/*
 * Copyright 2010 Red Hat, Inc. and/or its affiliates.
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
import org.optaplanner.examples.travelingtournament.domain.TravelingTournament;
import org.optaplanner.examples.travelingtournament.persistence.TravelingTournamentDao;
import org.optaplanner.examples.travelingtournament.persistence.TravelingTournamentExporter;
import org.optaplanner.examples.travelingtournament.persistence.TravelingTournamentImporter;
import org.optaplanner.examples.travelingtournament.swingui.TravelingTournamentPanel;

/**
 * WARNING: This is an old, complex, tailored example. You're probably better off with one of the other examples.
 */
public class TravelingTournamentApp extends CommonApp<TravelingTournament> {

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
                SOLVER_CONFIG,
                TravelingTournamentPanel.LOGO_PATH);
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
    protected AbstractSolutionImporter[] createSolutionImporters() {
        return new AbstractSolutionImporter[]{
                new TravelingTournamentImporter()
        };
    }

    @Override
    protected AbstractSolutionExporter createSolutionExporter() {
        return new TravelingTournamentExporter();
    }

}
