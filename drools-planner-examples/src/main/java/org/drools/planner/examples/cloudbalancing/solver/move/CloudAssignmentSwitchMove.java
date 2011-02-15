/*
 * Copyright 2010 JBoss Inc
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

package org.drools.planner.examples.cloudbalancing.solver.move;

import java.util.Arrays;
import java.util.Collection;

import org.apache.commons.lang.ObjectUtils;
import org.apache.commons.lang.builder.EqualsBuilder;
import org.apache.commons.lang.builder.HashCodeBuilder;
import org.drools.WorkingMemory;
import org.drools.planner.core.localsearch.decider.acceptor.tabu.TabuPropertyEnabled;
import org.drools.planner.core.move.Move;
import org.drools.planner.examples.cloudbalancing.domain.CloudAssignment;
import org.drools.planner.examples.cloudbalancing.domain.CloudComputer;
import org.drools.planner.examples.nurserostering.domain.Assignment;
import org.drools.planner.examples.nurserostering.domain.Employee;
import org.drools.planner.examples.nurserostering.solver.move.NurseRosteringMoveHelper;

public class CloudAssignmentSwitchMove implements Move, TabuPropertyEnabled {

    private CloudAssignment leftCloudAssignment;
    private CloudAssignment rightCloudAssignment;

    public CloudAssignmentSwitchMove(CloudAssignment leftCloudAssignment, CloudAssignment rightCloudAssignment) {
        this.leftCloudAssignment = leftCloudAssignment;
        this.rightCloudAssignment = rightCloudAssignment;
    }

    public boolean isMoveDoable(WorkingMemory workingMemory) {
        return !ObjectUtils.equals(leftCloudAssignment.getCloudComputer(), rightCloudAssignment.getCloudComputer());
    }

    public Move createUndoMove(WorkingMemory workingMemory) {
        return new CloudAssignmentSwitchMove(rightCloudAssignment, leftCloudAssignment);
    }

    public void doMove(WorkingMemory workingMemory) {
        CloudComputer oldLeftCloudComputer = leftCloudAssignment.getCloudComputer();
        CloudComputer oldRightCloudComputer = rightCloudAssignment.getCloudComputer();
        CloudBalancingMoveHelper.moveCloudComputer(workingMemory, leftCloudAssignment, oldRightCloudComputer);
        CloudBalancingMoveHelper.moveCloudComputer(workingMemory, rightCloudAssignment, oldLeftCloudComputer);
    }

    public Collection<? extends Object> getTabuProperties() {
        return Arrays.<CloudAssignment>asList(leftCloudAssignment, rightCloudAssignment);
    }

    public boolean equals(Object o) {
        if (this == o) {
            return true;
        } else if (o instanceof CloudAssignmentSwitchMove) {
            CloudAssignmentSwitchMove other = (CloudAssignmentSwitchMove) o;
            return new EqualsBuilder()
                    .append(leftCloudAssignment, other.leftCloudAssignment)
                    .append(rightCloudAssignment, other.rightCloudAssignment)
                    .isEquals();
        } else {
            return false;
        }
    }

    public int hashCode() {
        return new HashCodeBuilder()
                .append(leftCloudAssignment)
                .append(rightCloudAssignment)
                .toHashCode();
    }

    public String toString() {
        return leftCloudAssignment + " <=> " + rightCloudAssignment;
    }

}
