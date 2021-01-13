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

import java.util.HashSet;
import java.util.Set;

import org.optaplanner.examples.common.app.CommonApp;
import org.optaplanner.examples.common.persistence.AbstractSolutionExporter;
import org.optaplanner.examples.common.persistence.AbstractSolutionImporter;
import org.optaplanner.examples.travelingtournament.domain.TravelingTournament;
import org.optaplanner.examples.travelingtournament.persistence.TravelingTournamentExporter;
import org.optaplanner.examples.travelingtournament.persistence.TravelingTournamentImporter;
import org.optaplanner.examples.travelingtournament.persistence.TravelingTournamentXmlSolutionFileIO;
import org.optaplanner.examples.travelingtournament.swingui.TravelingTournamentPanel;
import org.optaplanner.persistence.common.api.domain.solution.SolutionFileIO;

/**
 * WARNING: This is an old, complex, tailored example. You're probably better off with one of the other examples.
 */
public class TravelingTournamentApp extends CommonApp<TravelingTournament> {

    public static final String SOLVER_CONFIG =
            "org/optaplanner/examples/travelingtournament/solver/travelingTournamentSolverConfig.xml";

    public static final String DATA_DIR_NAME = "travelingtournament";

    public static void main(String[] args) {
        prepareSwingEnvironment();
        new TravelingTournamentApp().init();
    }

    public TravelingTournamentApp() {
        super("Traveling tournament",
                "Official competition name: TTP - Traveling tournament problem\n\n" +
                        "Assign sport matches to days. Minimize the distance travelled.",
                SOLVER_CONFIG, DATA_DIR_NAME,
                TravelingTournamentPanel.LOGO_PATH);
    }

    @Override
    protected TravelingTournamentPanel createSolutionPanel() {
        return new TravelingTournamentPanel();
    }

    @Override
    public SolutionFileIO<TravelingTournament> createSolutionFileIO() {
        return new TravelingTournamentXmlSolutionFileIO();
    }

    @Override
    protected AbstractSolutionImporter[] createSolutionImporters() {
        return new AbstractSolutionImporter[] {
                new TravelingTournamentImporter()
        };
    }

    @Override
    protected Set<AbstractSolutionExporter> createSolutionExporters() {
        Set<AbstractSolutionExporter> exporters = new HashSet<>(1);
        exporters.add(new TravelingTournamentExporter());
        return exporters;
    }

}
