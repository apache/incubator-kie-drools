package org.optaplanner.examples.projectjobscheduling.persistence;

import org.optaplanner.examples.common.persistence.jackson.AbstractExampleSolutionFileIO;
import org.optaplanner.examples.projectjobscheduling.domain.Schedule;

public class ProjectJobSchedulingSolutionFileIO extends AbstractExampleSolutionFileIO<Schedule> {

    public ProjectJobSchedulingSolutionFileIO() {
        super(Schedule.class);
    }
}
