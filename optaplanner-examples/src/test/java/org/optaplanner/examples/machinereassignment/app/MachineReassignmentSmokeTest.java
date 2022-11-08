package org.optaplanner.examples.machinereassignment.app;

import java.util.stream.Stream;

import org.optaplanner.core.api.score.buildin.hardsoftlong.HardSoftLongScore;
import org.optaplanner.core.api.score.stream.ConstraintStreamImplType;
import org.optaplanner.examples.common.app.SolverSmokeTest;
import org.optaplanner.examples.machinereassignment.domain.MachineReassignment;

class MachineReassignmentSmokeTest extends SolverSmokeTest<MachineReassignment, HardSoftLongScore> {

    private static final String UNSOLVED_DATA_FILE = "data/machinereassignment/unsolved/model_a2_1.json";

    @Override
    protected MachineReassignmentApp createCommonApp() {
        return new MachineReassignmentApp();
    }

    @Override
    protected Stream<TestData<HardSoftLongScore>> testData() {
        return Stream.of(
                TestData.of(ConstraintStreamImplType.DROOLS, UNSOLVED_DATA_FILE,
                        HardSoftLongScore.ofSoft(-101312993),
                        HardSoftLongScore.ofSoft(-151402988)),
                TestData.of(ConstraintStreamImplType.BAVET, UNSOLVED_DATA_FILE,
                        HardSoftLongScore.ofSoft(-39203859),
                        HardSoftLongScore.ofSoft(-61212262)));
    }
}
