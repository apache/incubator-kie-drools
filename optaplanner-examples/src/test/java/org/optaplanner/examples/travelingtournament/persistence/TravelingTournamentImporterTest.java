package org.optaplanner.examples.travelingtournament.persistence;

import org.optaplanner.examples.common.persistence.AbstractSolutionImporter;
import org.optaplanner.examples.common.persistence.ImportDataFilesTest;
import org.optaplanner.examples.travelingtournament.app.TravelingTournamentApp;
import org.optaplanner.examples.travelingtournament.domain.TravelingTournament;

class TravelingTournamentImporterTest extends ImportDataFilesTest<TravelingTournament> {

    @Override
    protected AbstractSolutionImporter<TravelingTournament> createSolutionImporter() {
        return new TravelingTournamentImporter();
    }

    @Override
    protected String getDataDirName() {
        return TravelingTournamentApp.DATA_DIR_NAME;
    }
}
