package org.optaplanner.examples.batchscheduling.domain.solver;

import org.optaplanner.core.api.domain.variable.VariableListener;
import org.optaplanner.core.api.score.director.ScoreDirector;
import org.optaplanner.examples.batchscheduling.domain.Allocation;
import org.optaplanner.examples.batchscheduling.domain.BatchSchedule;

public class PredecessorsDoneDateUpdatingVariableListener implements VariableListener<BatchSchedule, Allocation> {

    @Override
    public void beforeEntityAdded(ScoreDirector<BatchSchedule> scoreDirector, Allocation allocation) {
        // Do nothing
    }

    @Override
    public void afterEntityAdded(ScoreDirector<BatchSchedule> scoreDirector, Allocation allocation) {
        updateAllocation(scoreDirector, allocation);
    }

    @Override
    public void beforeVariableChanged(ScoreDirector<BatchSchedule> scoreDirector, Allocation allocation) {
        // Do nothing
    }

    @Override
    public void afterVariableChanged(ScoreDirector<BatchSchedule> scoreDirector, Allocation allocation) {
        // Commented on 20th March
        updateAllocation(scoreDirector, allocation);
    }

    @Override
    public void beforeEntityRemoved(ScoreDirector<BatchSchedule> scoreDirector, Allocation allocation) {
        // Do nothing
    }

    @Override
    public void afterEntityRemoved(ScoreDirector<BatchSchedule> scoreDirector, Allocation allocation) {
        // Do nothing
    }

    protected void updateAllocation(ScoreDirector<BatchSchedule> scoreDirector, Allocation arg1) {
        Allocation tempAllocation = arg1;

        // Iterate through Successor Allocations and pass StartDeliveryDate of the current Allocation.
        while (tempAllocation.getSuccessorAllocation() != null) {
            tempAllocation = tempAllocation.getSuccessorAllocation();

            Long predecessorStartDeliveryDate = tempAllocation.getPredecessorAllocation().getStartDeliveryTime();
            scoreDirector.beforeVariableChanged(tempAllocation, "predecessorsDoneDate");
            tempAllocation.setPredecessorsDoneDate(predecessorStartDeliveryDate);
            scoreDirector.afterVariableChanged(tempAllocation, "predecessorsDoneDate");
        }
    }

}
