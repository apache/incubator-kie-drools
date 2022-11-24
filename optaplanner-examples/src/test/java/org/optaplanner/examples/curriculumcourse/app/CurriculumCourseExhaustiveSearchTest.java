package org.optaplanner.examples.curriculumcourse.app;

import java.util.stream.Stream;

import org.optaplanner.examples.common.app.AbstractExhaustiveSearchTest;
import org.optaplanner.examples.common.app.CommonApp;
import org.optaplanner.examples.curriculumcourse.domain.CourseSchedule;

class CurriculumCourseExhaustiveSearchTest extends AbstractExhaustiveSearchTest<CourseSchedule> {

    @Override
    protected CommonApp<CourseSchedule> createCommonApp() {
        return new CurriculumCourseApp();
    }

    @Override
    protected Stream<String> unsolvedFileNames() {
        return Stream.of("toy01.json");
    }
}
