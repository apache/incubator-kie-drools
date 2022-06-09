package org.optaplanner.examples.curriculumcourse.persistence;

import org.optaplanner.examples.common.app.CommonApp;
import org.optaplanner.examples.common.persistence.OpenDataFilesTest;
import org.optaplanner.examples.curriculumcourse.app.CurriculumCourseApp;
import org.optaplanner.examples.curriculumcourse.domain.CourseSchedule;

class CurriculumCourseOpenDataFilesTest extends OpenDataFilesTest<CourseSchedule> {

    @Override
    protected CommonApp<CourseSchedule> createCommonApp() {
        return new CurriculumCourseApp();
    }
}
