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
import org.drools.planner.examples.cloudbalancing.domain.CloudProcessAssignment;
import org.drools.planner.examples.cloudbalancing.domain.CloudComputer;

public class CloudProcessAssignmentSwitchMove implements Move, TabuPropertyEnabled {

    private CloudProcessAssignment leftCloudProcessAssignment;
    private CloudProcessAssignment rightCloudProcessAssignment;

    public CloudProcessAssignmentSwitchMove(CloudProcessAssignment leftCloudProcessAssignment, CloudProcessAssignment rightCloudProcessAssignment) {
        this.leftCloudProcessAssignment = leftCloudProcessAssignment;
        this.rightCloudProcessAssignment = rightCloudProcessAssignment;
    }

    public boolean isMoveDoable(WorkingMemory workingMemory) {
        return !ObjectUtils.equals(leftCloudProcessAssignment.getCloudComputer(), rightCloudProcessAssignment.getCloudComputer());
    }

    public Move createUndoMove(WorkingMemory workingMemory) {
        return new CloudProcessAssignmentSwitchMove(rightCloudProcessAssignment, leftCloudProcessAssignment);
    }

    public void doMove(WorkingMemory workingMemory) {
        CloudComputer oldLeftCloudComputer = leftCloudProcessAssignment.getCloudComputer();
        CloudComputer oldRightCloudComputer = rightCloudProcessAssignment.getCloudComputer();
        CloudBalancingMoveHelper.moveCloudComputer(workingMemory, leftCloudProcessAssignment, oldRightCloudComputer);
        CloudBalancingMoveHelper.moveCloudComputer(workingMemory, rightCloudProcessAssignment, oldLeftCloudComputer);
    }

    public Collection<? extends Object> getTabuProperties() {
        return Arrays.<CloudProcessAssignment>asList(leftCloudProcessAssignment, rightCloudProcessAssignment);
    }

    public boolean equals(Object o) {
        if (this == o) {
            return true;
        } else if (o instanceof CloudProcessAssignmentSwitchMove) {
            CloudProcessAssignmentSwitchMove other = (CloudProcessAssignmentSwitchMove) o;
            return new EqualsBuilder()
                    .append(leftCloudProcessAssignment, other.leftCloudProcessAssignment)
                    .append(rightCloudProcessAssignment, other.rightCloudProcessAssignment)
                    .isEquals();
        } else {
            return false;
        }
    }

    public int hashCode() {
        return new HashCodeBuilder()
                .append(leftCloudProcessAssignment)
                .append(rightCloudProcessAssignment)
                .toHashCode();
    }

    public String toString() {
        return leftCloudProcessAssignment + " <=> " + rightCloudProcessAssignment;
    }

}
