/*
 * Copyright 2020 Red Hat, Inc. and/or its affiliates.
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *      http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package org.optaplanner.examples.projectjobscheduling.domain.solver;

import java.util.ArrayDeque;
import java.util.Objects;
import java.util.Queue;

import org.optaplanner.core.api.domain.variable.VariableListener;
import org.optaplanner.core.api.score.director.ScoreDirector;
import org.optaplanner.examples.projectjobscheduling.domain.Allocation;
import org.optaplanner.examples.projectjobscheduling.domain.Schedule;

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

    protected void updateAllocation(ScoreDirector<Schedule> scoreDirector, Allocation originalAllocation) {
        Queue<Allocation> uncheckedSuccessorQueue = new ArrayDeque<>();
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
    protected boolean updatePredecessorsDoneDate(ScoreDirector<Schedule> scoreDirector, Allocation allocation) {
        // For the source the doneDate must be 0.
        Integer doneDate = 0;
        for (Allocation predecessorAllocation : allocation.getPredecessorAllocationList()) {
            int endDate = predecessorAllocation.getEndDate();
            doneDate = Math.max(doneDate, endDate);
        }
        if (Objects.equals(doneDate, allocation.getPredecessorsDoneDate())) {
            return false;
        }
        scoreDirector.beforeVariableChanged(allocation, "predecessorsDoneDate");
        allocation.setPredecessorsDoneDate(doneDate);
        scoreDirector.afterVariableChanged(allocation, "predecessorsDoneDate");
        return true;
    }

}
