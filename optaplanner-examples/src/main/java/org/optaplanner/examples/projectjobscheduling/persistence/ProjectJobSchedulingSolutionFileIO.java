package org.optaplanner.examples.projectjobscheduling.persistence;

import org.optaplanner.examples.common.persistence.AbstractJsonSolutionFileIO;
import org.optaplanner.examples.projectjobscheduling.domain.Schedule;

public class ProjectJobSchedulingSolutionFileIO extends AbstractJsonSolutionFileIO<Schedule> {

    public ProjectJobSchedulingSolutionFileIO() {
        super(Schedule.class);
    }
}
