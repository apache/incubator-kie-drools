package org.optaplanner.examples.nurserostering.app;

import java.util.stream.Stream;

import org.optaplanner.core.api.score.buildin.hardsoft.HardSoftScore;
import org.optaplanner.core.config.solver.EnvironmentMode;
import org.optaplanner.examples.common.app.SolverPerformanceTest;
import org.optaplanner.examples.nurserostering.domain.NurseRoster;

class NurseRosteringPerformanceTest extends SolverPerformanceTest<NurseRoster, HardSoftScore> {

    private static final String UNSOLVED_DATA_FILE = "data/nurserostering/unsolved/medium_late01_initialized.xml";

    @Override
    protected NurseRosteringApp createCommonApp() {
        return new NurseRosteringApp();
    }

    @Override
    protected Stream<TestData<HardSoftScore>> testData() {
        return Stream.of(
                testData(UNSOLVED_DATA_FILE, HardSoftScore.ofSoft(-568), EnvironmentMode.REPRODUCIBLE),
                testData(UNSOLVED_DATA_FILE, HardSoftScore.ofSoft(-689), EnvironmentMode.FAST_ASSERT));
    }
}
