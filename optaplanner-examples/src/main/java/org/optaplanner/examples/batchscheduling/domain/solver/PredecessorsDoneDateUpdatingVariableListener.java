package org.optaplanner.examples.batchscheduling.domain.solver;

import org.optaplanner.core.api.domain.variable.VariableListener;
import org.optaplanner.core.api.score.director.ScoreDirector;
import org.optaplanner.examples.batchscheduling.domain.Allocation;
import org.optaplanner.examples.batchscheduling.domain.Schedule;

public class PredecessorsDoneDateUpdatingVariableListener implements VariableListener<Schedule, Allocation> {

    @Override
    public void beforeEntityAdded(ScoreDirector<Schedule> scoreDirector, Allocation allocation) {
        // Do nothing
    }

    @Override
    public void afterEntityAdded(ScoreDirector<Schedule> scoreDirector, Allocation allocation) {
        updateAllocation(scoreDirector, allocation);
    }

    @Override
    public void beforeVariableChanged(ScoreDirector<Schedule> scoreDirector, Allocation allocation) {
        // Do nothing
    }

    @Override
    public void afterVariableChanged(ScoreDirector<Schedule> scoreDirector, Allocation allocation) {
        // Commented on 20th March
        updateAllocation(scoreDirector, allocation);
    }

    @Override
    public void beforeEntityRemoved(ScoreDirector<Schedule> scoreDirector, Allocation allocation) {
        // Do nothing
    }

    @Override
    public void afterEntityRemoved(ScoreDirector<Schedule> scoreDirector, Allocation allocation) {
        // Do nothing
    }

    protected void updateAllocation(ScoreDirector<Schedule> scoreDirector, Allocation arg1) {

        Allocation tempAllocation = arg1;

        // Iterate through Successor Allocations and pass StartDeliveryDate of the current Allocation.
        while (tempAllocation.getSuccessorAllocation() != null) {
            tempAllocation = tempAllocation.getSuccessorAllocation();

            Long predecessorStartDeliveryDate = 0L;
            predecessorStartDeliveryDate = tempAllocation.getPredecessorAllocation().getStartDeliveryTime();
            scoreDirector.beforeVariableChanged(tempAllocation, "predecessorsDoneDate");
            tempAllocation.setPredecessorsDoneDate(predecessorStartDeliveryDate);
            scoreDirector.afterVariableChanged(tempAllocation, "predecessorsDoneDate");

        }
    }

}
