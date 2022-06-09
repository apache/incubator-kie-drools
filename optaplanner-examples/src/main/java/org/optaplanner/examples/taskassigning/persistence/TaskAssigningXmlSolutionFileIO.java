package org.optaplanner.examples.taskassigning.persistence;

import org.optaplanner.examples.taskassigning.domain.TaskAssigningSolution;
import org.optaplanner.persistence.xstream.impl.domain.solution.XStreamSolutionFileIO;

public class TaskAssigningXmlSolutionFileIO extends XStreamSolutionFileIO<TaskAssigningSolution> {

    public TaskAssigningXmlSolutionFileIO() {
        super(TaskAssigningSolution.class);
    }
}
