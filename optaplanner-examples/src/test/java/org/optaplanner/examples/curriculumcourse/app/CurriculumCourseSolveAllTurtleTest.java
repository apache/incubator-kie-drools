package org.optaplanner.examples.curriculumcourse.app;

import org.optaplanner.examples.common.app.CommonApp;
import org.optaplanner.examples.common.app.UnsolvedDirSolveAllTurtleTest;
import org.optaplanner.examples.curriculumcourse.domain.CourseSchedule;

class CurriculumCourseSolveAllTurtleTest extends UnsolvedDirSolveAllTurtleTest<CourseSchedule> {

    @Override
    protected CommonApp<CourseSchedule> createCommonApp() {
        return new CurriculumCourseApp();
    }
}
