package org.optaplanner.examples.curriculumcourse.persistence;

import org.optaplanner.examples.curriculumcourse.domain.CourseSchedule;
import org.optaplanner.persistence.xstream.impl.domain.solution.XStreamSolutionFileIO;

public class CurriculumCourseXmlSolutionFileIO extends XStreamSolutionFileIO<CourseSchedule> {

    public CurriculumCourseXmlSolutionFileIO() {
        super(CourseSchedule.class);
    }
}
