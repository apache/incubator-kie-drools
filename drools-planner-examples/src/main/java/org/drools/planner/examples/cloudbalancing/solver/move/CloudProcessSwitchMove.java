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
import org.drools.planner.examples.cloudbalancing.domain.CloudProcess;
import org.drools.planner.examples.cloudbalancing.domain.CloudComputer;

public class CloudProcessSwitchMove implements Move, TabuPropertyEnabled {

    private CloudProcess leftCloudProcess;
    private CloudProcess rightCloudProcess;

    public CloudProcessSwitchMove(CloudProcess leftCloudProcess, CloudProcess rightCloudProcess) {
        this.leftCloudProcess = leftCloudProcess;
        this.rightCloudProcess = rightCloudProcess;
    }

    public boolean isMoveDoable(WorkingMemory workingMemory) {
        return !ObjectUtils.equals(leftCloudProcess.getComputer(), rightCloudProcess.getComputer());
    }

    public Move createUndoMove(WorkingMemory workingMemory) {
        return new CloudProcessSwitchMove(rightCloudProcess, leftCloudProcess);
    }

    public void doMove(WorkingMemory workingMemory) {
        CloudComputer oldLeftCloudComputer = leftCloudProcess.getComputer();
        CloudComputer oldRightCloudComputer = rightCloudProcess.getComputer();
        CloudBalancingMoveHelper.moveCloudComputer(workingMemory, leftCloudProcess, oldRightCloudComputer);
        CloudBalancingMoveHelper.moveCloudComputer(workingMemory, rightCloudProcess, oldLeftCloudComputer);
    }

    public Collection<? extends Object> getTabuProperties() {
        return Arrays.<CloudProcess>asList(leftCloudProcess, rightCloudProcess);
    }

    public boolean equals(Object o) {
        if (this == o) {
            return true;
        } else if (o instanceof CloudProcessSwitchMove) {
            CloudProcessSwitchMove other = (CloudProcessSwitchMove) o;
            return new EqualsBuilder()
                    .append(leftCloudProcess, other.leftCloudProcess)
                    .append(rightCloudProcess, other.rightCloudProcess)
                    .isEquals();
        } else {
            return false;
        }
    }

    public int hashCode() {
        return new HashCodeBuilder()
                .append(leftCloudProcess)
                .append(rightCloudProcess)
                .toHashCode();
    }

    public String toString() {
        return leftCloudProcess + " <=> " + rightCloudProcess;
    }

}
