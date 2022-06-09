package org.optaplanner.examples.curriculumcourse.persistence;

import org.optaplanner.examples.common.persistence.AbstractSolutionImporter;
import org.optaplanner.examples.common.persistence.ImportDataFilesTest;
import org.optaplanner.examples.curriculumcourse.app.CurriculumCourseApp;
import org.optaplanner.examples.curriculumcourse.domain.CourseSchedule;

class CurriculumCourseImporterTest extends ImportDataFilesTest<CourseSchedule> {

    @Override
    protected AbstractSolutionImporter<CourseSchedule> createSolutionImporter() {
        return new CurriculumCourseImporter();
    }

    @Override
    protected String getDataDirName() {
        return CurriculumCourseApp.DATA_DIR_NAME;
    }
}
