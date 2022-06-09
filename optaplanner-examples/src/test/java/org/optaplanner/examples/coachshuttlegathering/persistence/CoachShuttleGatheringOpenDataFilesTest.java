package org.optaplanner.examples.coachshuttlegathering.persistence;

import org.optaplanner.examples.coachshuttlegathering.app.CoachShuttleGatheringApp;
import org.optaplanner.examples.coachshuttlegathering.domain.CoachShuttleGatheringSolution;
import org.optaplanner.examples.common.app.CommonApp;
import org.optaplanner.examples.common.persistence.OpenDataFilesTest;

class CoachShuttleGatheringOpenDataFilesTest extends OpenDataFilesTest<CoachShuttleGatheringSolution> {

    @Override
    protected CommonApp<CoachShuttleGatheringSolution> createCommonApp() {
        return new CoachShuttleGatheringApp();
    }
}
