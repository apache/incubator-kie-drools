package org.optaplanner.examples.machinereassignment.app;

import java.util.stream.Stream;

import org.optaplanner.core.api.score.buildin.hardsoftlong.HardSoftLongScore;
import org.optaplanner.core.config.solver.EnvironmentMode;
import org.optaplanner.examples.common.app.SolverPerformanceTest;
import org.optaplanner.examples.machinereassignment.domain.MachineReassignment;

class MachineReassignmentPerformanceTest extends SolverPerformanceTest<MachineReassignment, HardSoftLongScore> {

    private static final String UNSOLVED_DATA_FILE = "data/machinereassignment/unsolved/model_a2_1.xml";

    @Override
    protected MachineReassignmentApp createCommonApp() {
        return new MachineReassignmentApp();
    }

    @Override
    protected Stream<TestData<HardSoftLongScore>> testData() {
        return Stream.of(
                testData(UNSOLVED_DATA_FILE, HardSoftLongScore.ofSoft(-86121794), EnvironmentMode.REPRODUCIBLE),
                testData(UNSOLVED_DATA_FILE, HardSoftLongScore.ofSoft(-204041393), EnvironmentMode.FAST_ASSERT));
    }
}
