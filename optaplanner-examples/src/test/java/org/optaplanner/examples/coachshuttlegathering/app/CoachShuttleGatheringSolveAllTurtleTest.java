package org.optaplanner.examples.coachshuttlegathering.app;

import org.optaplanner.examples.coachshuttlegathering.domain.CoachShuttleGatheringSolution;
import org.optaplanner.examples.coachshuttlegathering.optional.score.CoachShuttleGatheringEasyScoreCalculator;
import org.optaplanner.examples.common.app.CommonApp;
import org.optaplanner.examples.common.app.UnsolvedDirSolveAllTurtleTest;

class CoachShuttleGatheringSolveAllTurtleTest extends UnsolvedDirSolveAllTurtleTest<CoachShuttleGatheringSolution> {

    @Override
    protected CommonApp<CoachShuttleGatheringSolution> createCommonApp() {
        return new CoachShuttleGatheringApp();
    }

    @Override
    protected Class<CoachShuttleGatheringEasyScoreCalculator> overwritingEasyScoreCalculatorClass() {
        return CoachShuttleGatheringEasyScoreCalculator.class;
    }
}
