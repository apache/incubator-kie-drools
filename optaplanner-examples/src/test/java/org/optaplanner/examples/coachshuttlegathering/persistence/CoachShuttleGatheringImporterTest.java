package org.optaplanner.examples.coachshuttlegathering.persistence;

import org.optaplanner.examples.coachshuttlegathering.app.CoachShuttleGatheringApp;
import org.optaplanner.examples.coachshuttlegathering.domain.CoachShuttleGatheringSolution;
import org.optaplanner.examples.common.persistence.AbstractSolutionImporter;
import org.optaplanner.examples.common.persistence.ImportDataFilesTest;

class CoachShuttleGatheringImporterTest extends ImportDataFilesTest<CoachShuttleGatheringSolution> {

    @Override
    protected AbstractSolutionImporter<CoachShuttleGatheringSolution> createSolutionImporter() {
        return new CoachShuttleGatheringImporter();
    }

    @Override
    protected String getDataDirName() {
        return CoachShuttleGatheringApp.DATA_DIR_NAME;
    }
}
