package org.optaplanner.examples.travelingtournament.app;

import org.optaplanner.examples.common.app.CommonApp;
import org.optaplanner.examples.common.app.UnsolvedDirSolveAllTurtleTest;
import org.optaplanner.examples.travelingtournament.domain.TravelingTournament;

class TravelingTournamentSolveAllTurtleTest extends UnsolvedDirSolveAllTurtleTest<TravelingTournament> {

    @Override
    protected CommonApp<TravelingTournament> createCommonApp() {
        return new TravelingTournamentApp();
    }
}
