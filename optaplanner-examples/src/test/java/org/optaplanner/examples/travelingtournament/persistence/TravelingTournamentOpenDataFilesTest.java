package org.optaplanner.examples.travelingtournament.persistence;

import org.optaplanner.examples.common.app.CommonApp;
import org.optaplanner.examples.common.persistence.OpenDataFilesTest;
import org.optaplanner.examples.travelingtournament.app.TravelingTournamentApp;
import org.optaplanner.examples.travelingtournament.domain.TravelingTournament;

class TravelingTournamentOpenDataFilesTest extends OpenDataFilesTest<TravelingTournament> {

    @Override
    protected CommonApp<TravelingTournament> createCommonApp() {
        return new TravelingTournamentApp();
    }
}
