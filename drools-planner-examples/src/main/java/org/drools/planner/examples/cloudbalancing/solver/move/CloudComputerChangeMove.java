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

import java.util.Collection;
import java.util.Collections;

import org.apache.commons.lang.ObjectUtils;
import org.apache.commons.lang.builder.EqualsBuilder;
import org.apache.commons.lang.builder.HashCodeBuilder;
import org.drools.WorkingMemory;
import org.drools.planner.core.localsearch.decider.acceptor.tabu.TabuPropertyEnabled;
import org.drools.planner.core.move.Move;
import org.drools.planner.examples.cloudbalancing.domain.CloudAssignment;
import org.drools.planner.examples.cloudbalancing.domain.CloudComputer;

public class CloudComputerChangeMove implements Move, TabuPropertyEnabled {

    private CloudAssignment cloudAssignment;
    private CloudComputer toCloudComputer;

    public CloudComputerChangeMove(CloudAssignment cloudAssignment, CloudComputer toCloudComputer) {
        this.cloudAssignment = cloudAssignment;
        this.toCloudComputer = toCloudComputer;
    }

    public boolean isMoveDoable(WorkingMemory workingMemory) {
        return !ObjectUtils.equals(cloudAssignment.getCloudComputer(), toCloudComputer);
    }

    public Move createUndoMove(WorkingMemory workingMemory) {
        return new CloudComputerChangeMove(cloudAssignment, cloudAssignment.getCloudComputer());
    }

    public void doMove(WorkingMemory workingMemory) {
        CloudBalancingMoveHelper.moveCloudComputer(workingMemory, cloudAssignment, toCloudComputer);
    }

    public Collection<? extends Object> getTabuProperties() {
        return Collections.singletonList(cloudAssignment);
    }

    public boolean equals(Object o) {
        if (this == o) {
            return true;
        } else if (o instanceof CloudComputerChangeMove) {
            CloudComputerChangeMove other = (CloudComputerChangeMove) o;
            return new EqualsBuilder()
                    .append(cloudAssignment, other.cloudAssignment)
                    .append(toCloudComputer, other.toCloudComputer)
                    .isEquals();
        } else {
            return false;
        }
    }

    public int hashCode() {
        return new HashCodeBuilder()
                .append(cloudAssignment)
                .append(toCloudComputer)
                .toHashCode();
    }

    public String toString() {
        return cloudAssignment + " => " + toCloudComputer;
    }

}
