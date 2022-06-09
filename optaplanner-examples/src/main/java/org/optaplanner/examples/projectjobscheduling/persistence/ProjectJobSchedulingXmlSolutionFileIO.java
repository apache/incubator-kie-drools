package org.optaplanner.examples.projectjobscheduling.persistence;

import org.optaplanner.examples.projectjobscheduling.domain.Schedule;
import org.optaplanner.persistence.xstream.impl.domain.solution.XStreamSolutionFileIO;

public class ProjectJobSchedulingXmlSolutionFileIO extends XStreamSolutionFileIO<Schedule> {

    public ProjectJobSchedulingXmlSolutionFileIO() {
        super(Schedule.class);
    }
}
