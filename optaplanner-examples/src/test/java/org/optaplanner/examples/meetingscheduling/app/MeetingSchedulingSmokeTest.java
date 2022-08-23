package org.optaplanner.examples.meetingscheduling.app;

import java.util.stream.Stream;

import org.optaplanner.core.api.score.buildin.hardmediumsoft.HardMediumSoftScore;
import org.optaplanner.core.api.score.stream.ConstraintStreamImplType;
import org.optaplanner.examples.common.app.SolverSmokeTest;
import org.optaplanner.examples.meetingscheduling.domain.MeetingSchedule;

class MeetingSchedulingSmokeTest extends SolverSmokeTest<MeetingSchedule, HardMediumSoftScore> {

    private static final String UNSOLVED_DATA_FILE = "data/meetingscheduling/unsolved/50meetings-160timegrains-5rooms.xlsx";

    @Override
    protected MeetingSchedulingApp createCommonApp() {
        return new MeetingSchedulingApp();
    }

    @Override
    protected Stream<TestData<HardMediumSoftScore>> testData() {
        return Stream.of(
                TestData.of(ConstraintStreamImplType.DROOLS, UNSOLVED_DATA_FILE,
                        HardMediumSoftScore.ofUninitialized(-6, -58, -300, -7065),
                        HardMediumSoftScore.ofUninitialized(-14, -93, -220, -5746)),
                TestData.of(ConstraintStreamImplType.BAVET, UNSOLVED_DATA_FILE,
                        HardMediumSoftScore.of(-29, -344, -9227),
                        HardMediumSoftScore.ofUninitialized(-20, -116, -143, -5094)));
    }
}
