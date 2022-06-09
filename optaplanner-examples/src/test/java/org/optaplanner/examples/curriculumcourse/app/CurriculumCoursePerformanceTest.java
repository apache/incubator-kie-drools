package org.optaplanner.examples.curriculumcourse.app;

import java.util.stream.Stream;

import org.optaplanner.core.api.score.buildin.hardsoft.HardSoftScore;
import org.optaplanner.core.config.solver.EnvironmentMode;
import org.optaplanner.examples.common.app.SolverPerformanceTest;
import org.optaplanner.examples.curriculumcourse.domain.CourseSchedule;

class CurriculumCoursePerformanceTest extends SolverPerformanceTest<CourseSchedule, HardSoftScore> {

    private static final String UNSOLVED_DATA_FILE = "data/curriculumcourse/unsolved/comp01_initialized.xml";

    @Override
    protected CurriculumCourseApp createCommonApp() {
        return new CurriculumCourseApp();
    }

    @Override
    protected Stream<TestData<HardSoftScore>> testData() {
        return Stream.of(
                testData(UNSOLVED_DATA_FILE, HardSoftScore.ofSoft(-66), EnvironmentMode.REPRODUCIBLE),
                testData(UNSOLVED_DATA_FILE, HardSoftScore.ofSoft(-82), EnvironmentMode.FAST_ASSERT));
    }
}
