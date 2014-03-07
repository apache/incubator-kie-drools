package org.optaplanner.examples.projectjobscheduling.domain.solver;

import java.util.ArrayDeque;
import java.util.Queue;

import org.apache.commons.lang.ObjectUtils;
import org.optaplanner.core.impl.domain.variable.listener.VariableListener;
import org.optaplanner.core.impl.score.director.ScoreDirector;
import org.optaplanner.examples.projectjobscheduling.domain.Allocation;

public class PredecessorsDoneDateUpdatingVariableListener implements VariableListener<Allocation> {

    public void beforeEntityAdded(ScoreDirector scoreDirector, Allocation allocation) {
        // Do nothing
    }

    public void afterEntityAdded(ScoreDirector scoreDirector, Allocation allocation) {
        updateAllocation(scoreDirector, allocation);
    }

    public void beforeVariableChanged(ScoreDirector scoreDirector, Allocation allocation) {
        // Do nothing
    }

    public void afterVariableChanged(ScoreDirector scoreDirector, Allocation allocation) {
        updateAllocation(scoreDirector, allocation);
    }

    public void beforeEntityRemoved(ScoreDirector scoreDirector, Allocation allocation) {
        // Do nothing
    }

    public void afterEntityRemoved(ScoreDirector scoreDirector, Allocation allocation) {
        // Do nothing
    }

    protected void updateAllocation(ScoreDirector scoreDirector, Allocation originalAllocation) {
        Queue<Allocation> uncheckedSuccessorQueue = new ArrayDeque<Allocation>();
        uncheckedSuccessorQueue.addAll(originalAllocation.getSuccessorAllocationList());
        while (!uncheckedSuccessorQueue.isEmpty()) {
            Allocation allocation = uncheckedSuccessorQueue.remove();
            boolean updated = updatePredecessorsDoneDate(scoreDirector, allocation);
            if (updated) {
                uncheckedSuccessorQueue.addAll(allocation.getSuccessorAllocationList());
            }
        }
    }

    /**
     * @param scoreDirector never null
     * @param allocation never null
     * @return true if the startDate changed
     */
    protected boolean updatePredecessorsDoneDate(ScoreDirector scoreDirector, Allocation allocation) {
        // For the source the doneDate must be 0.
        Integer doneDate = 0;
        for (Allocation predecessorAllocation : allocation.getPredecessorAllocationList()) {
            int endDate = predecessorAllocation.getEndDate();
            doneDate = Math.max(doneDate, endDate);
        }
        if (ObjectUtils.equals(doneDate, allocation.getPredecessorsDoneDate())) {
            return false;
        }
        scoreDirector.beforeVariableChanged(allocation, "predecessorsDoneDate");
        allocation.setPredecessorsDoneDate(doneDate);
        scoreDirector.afterVariableChanged(allocation, "predecessorsDoneDate");
        return true;
    }

}
