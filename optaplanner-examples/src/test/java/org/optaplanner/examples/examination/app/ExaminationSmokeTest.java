package org.optaplanner.examples.examination.app;

import java.util.stream.Stream;

import org.optaplanner.core.api.score.buildin.hardsoft.HardSoftScore;
import org.optaplanner.core.api.score.stream.ConstraintStreamImplType;
import org.optaplanner.examples.common.app.SolverSmokeTest;
import org.optaplanner.examples.examination.domain.Examination;

class ExaminationSmokeTest extends SolverSmokeTest<Examination, HardSoftScore> {

    private static final String UNSOLVED_DATA_FILE = "data/examination/unsolved/exam_comp_set5.json";

    @Override
    protected ExaminationApp createCommonApp() {
        return new ExaminationApp();
    }

    @Override
    protected Stream<TestData<HardSoftScore>> testData() {
        return Stream.of(
                TestData.of(ConstraintStreamImplType.DROOLS, UNSOLVED_DATA_FILE,
                        HardSoftScore.ofSoft(-6942),
                        HardSoftScore.ofUninitialized(-34, 0, -8581)),
                TestData.of(ConstraintStreamImplType.BAVET, UNSOLVED_DATA_FILE,
                        HardSoftScore.ofSoft(-4195),
                        HardSoftScore.ofSoft(-4312)));
    }
}
