package org.optaplanner.examples.curriculumcourse.persistence;

import org.optaplanner.examples.common.persistence.jackson.AbstractExampleSolutionFileIO;
import org.optaplanner.examples.curriculumcourse.domain.CourseSchedule;

public class CurriculumCourseSolutionFileIO extends AbstractExampleSolutionFileIO<CourseSchedule> {

    public CurriculumCourseSolutionFileIO() {
        super(CourseSchedule.class);
    }
}
