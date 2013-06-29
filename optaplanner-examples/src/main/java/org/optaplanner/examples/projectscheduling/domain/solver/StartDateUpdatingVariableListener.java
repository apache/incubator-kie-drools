package org.optaplanner.examples.projectscheduling.domain.solver;

import java.util.ArrayDeque;
import java.util.Queue;

import org.apache.commons.lang.ObjectUtils;
import org.optaplanner.core.impl.domain.variable.listener.PlanningVariableListener;
import org.optaplanner.core.impl.score.director.ScoreDirector;
import org.optaplanner.examples.projectscheduling.domain.Allocation;
import org.optaplanner.examples.projectscheduling.domain.JobType;

public class StartDateUpdatingVariableListener implements PlanningVariableListener<Allocation> {

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

    protected void updateAllocation(ScoreDirector scoreDirector, Allocation sourceAllocation) {
        Queue<Allocation> uncheckedSuccessorQueue = new ArrayDeque<Allocation>();
        uncheckedSuccessorQueue.add(sourceAllocation);
        while (!uncheckedSuccessorQueue.isEmpty()) {
            Allocation allocation = uncheckedSuccessorQueue.remove();
            boolean updated = updateStartDate(scoreDirector, allocation);
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
    protected boolean updateStartDate(ScoreDirector scoreDirector, Allocation allocation) {
        // For the source the startDate must be 0.
        Integer startDate = 0;
        for (Allocation predecessorAllocation : allocation.getPredecessorAllocationList()) {
            Integer endDate = predecessorAllocation.getEndDate();
            if (endDate == null) {
                startDate = null;
                break;
            }
            startDate = Math.max(startDate, endDate);
        }
        if (startDate != null) {
            Integer delay = allocation.getDelay();
            if (delay == null) {
                startDate = null;
            } else {
                startDate += delay;
            }
        }
        if (ObjectUtils.equals(startDate, allocation.getStartDate())) {
            return false;
        }
        scoreDirector.beforeVariableChanged(allocation, "startDate");
        allocation.setStartDate(startDate);
        scoreDirector.afterVariableChanged(allocation, "startDate");
        return true;
    }

}
