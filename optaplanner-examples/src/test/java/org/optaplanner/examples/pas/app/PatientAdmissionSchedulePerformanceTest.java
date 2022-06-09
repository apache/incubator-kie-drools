package org.optaplanner.examples.pas.app;

import java.util.stream.Stream;

import org.optaplanner.core.api.score.buildin.hardmediumsoft.HardMediumSoftScore;
import org.optaplanner.core.config.solver.EnvironmentMode;
import org.optaplanner.examples.common.app.SolverPerformanceTest;
import org.optaplanner.examples.pas.domain.PatientAdmissionSchedule;

class PatientAdmissionSchedulePerformanceTest
        extends SolverPerformanceTest<PatientAdmissionSchedule, HardMediumSoftScore> {

    private static final String UNSOLVED_DATA_FILE = "data/pas/unsolved/testdata01.xml";

    @Override
    protected PatientAdmissionScheduleApp createCommonApp() {
        return new PatientAdmissionScheduleApp();
    }

    @Override
    protected Stream<TestData<HardMediumSoftScore>> testData() {
        return Stream.of(
                testData(UNSOLVED_DATA_FILE, HardMediumSoftScore.of(0, 0, -7364), EnvironmentMode.REPRODUCIBLE),
                testData(UNSOLVED_DATA_FILE, HardMediumSoftScore.of(0, 0, -7442), EnvironmentMode.FAST_ASSERT));
    }
}
