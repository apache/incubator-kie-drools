package org.optaplanner.examples.curriculumcourse.persistence;

import org.optaplanner.examples.common.persistence.AbstractJsonSolutionFileIO;
import org.optaplanner.examples.curriculumcourse.domain.CourseSchedule;

public class CurriculumCourseSolutionFileIO extends AbstractJsonSolutionFileIO<CourseSchedule> {

    public CurriculumCourseSolutionFileIO() {
        super(CourseSchedule.class);
    }
}
