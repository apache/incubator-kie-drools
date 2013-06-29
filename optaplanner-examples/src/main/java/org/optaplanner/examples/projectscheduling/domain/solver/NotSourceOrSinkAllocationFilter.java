package org.optaplanner.examples.projectscheduling.domain.solver;

import org.optaplanner.core.impl.heuristic.selector.common.decorator.SelectionFilter;
import org.optaplanner.core.impl.score.director.ScoreDirector;
import org.optaplanner.examples.projectscheduling.domain.Allocation;
import org.optaplanner.examples.projectscheduling.domain.JobType;

public class NotSourceOrSinkAllocationFilter implements SelectionFilter<Allocation> {

    public boolean accept(ScoreDirector scoreDirector, Allocation allocation) {
        JobType jobType = allocation.getJob().getJobType();
        return jobType != JobType.SOURCE && jobType != JobType.SINK;
    }

}
