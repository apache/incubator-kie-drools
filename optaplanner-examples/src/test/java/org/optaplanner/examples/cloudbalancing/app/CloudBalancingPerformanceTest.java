package org.optaplanner.examples.cloudbalancing.app;

import java.util.stream.Stream;

import org.optaplanner.core.api.score.buildin.hardsoft.HardSoftScore;
import org.optaplanner.core.config.solver.EnvironmentMode;
import org.optaplanner.examples.cloudbalancing.domain.CloudBalance;
import org.optaplanner.examples.common.app.SolverPerformanceTest;

class CloudBalancingPerformanceTest extends SolverPerformanceTest<CloudBalance, HardSoftScore> {

    private static final String UNSOLVED_DATA_FILE = "data/cloudbalancing/unsolved/200computers-600processes.xml";

    @Override
    protected CloudBalancingApp createCommonApp() {
        return new CloudBalancingApp();
    }

    @Override
    protected Stream<TestData<HardSoftScore>> testData() {
        return Stream.of(
                testData(UNSOLVED_DATA_FILE, HardSoftScore.of(0, -220930), EnvironmentMode.REPRODUCIBLE),
                testData(UNSOLVED_DATA_FILE, HardSoftScore.of(0, -229390), EnvironmentMode.FAST_ASSERT));
    }
}
