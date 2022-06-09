package org.optaplanner.examples.meetingscheduling.app;

import java.util.stream.Stream;

import org.optaplanner.core.api.score.buildin.hardmediumsoft.HardMediumSoftScore;
import org.optaplanner.core.config.solver.EnvironmentMode;
import org.optaplanner.examples.common.app.SolverPerformanceTest;
import org.optaplanner.examples.meetingscheduling.domain.MeetingSchedule;

class MeetingSchedulingPerformanceTest extends SolverPerformanceTest<MeetingSchedule, HardMediumSoftScore> {

    private static final String UNSOLVED_DATA_FILE = "data/meetingscheduling/unsolved/50meetings-160timegrains-5rooms.xlsx";

    @Override
    protected MeetingSchedulingApp createCommonApp() {
        return new MeetingSchedulingApp();
    }

    @Override
    protected Stream<TestData<HardMediumSoftScore>> testData() {
        return Stream.of(
                testData(UNSOLVED_DATA_FILE, HardMediumSoftScore.of(-25, -100, -8166), EnvironmentMode.REPRODUCIBLE),
                testData(UNSOLVED_DATA_FILE, HardMediumSoftScore.of(-36, -64, -5921), EnvironmentMode.FAST_ASSERT));
    }
}
