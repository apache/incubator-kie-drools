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

package org.drools.planner.examples.cloudbalancing.domain;

import java.util.ArrayList;
import java.util.Collection;
import java.util.Iterator;
import java.util.List;

import com.thoughtworks.xstream.annotations.XStreamAlias;
import org.apache.commons.lang.builder.HashCodeBuilder;
import org.drools.planner.api.domain.solution.PlanningEntityCollectionProperty;
import org.drools.planner.core.score.HardAndSoftScore;
import org.drools.planner.core.solution.Solution;
import org.drools.planner.examples.common.domain.AbstractPersistable;

@XStreamAlias("CloudBalance")
public class CloudBalance extends AbstractPersistable implements Solution<HardAndSoftScore> {

    private List<CloudComputer> cloudComputerList;
    private List<CloudProcess> cloudProcessList;

    private List<CloudProcessAssignment> cloudProcessAssignmentList;

    private HardAndSoftScore score;

    public List<CloudComputer> getCloudComputerList() {
        return cloudComputerList;
    }

    public void setCloudComputerList(List<CloudComputer> cloudComputerList) {
        this.cloudComputerList = cloudComputerList;
    }

    public List<CloudProcess> getCloudProcessList() {
        return cloudProcessList;
    }

    public void setCloudProcessList(List<CloudProcess> cloudProcessList) {
        this.cloudProcessList = cloudProcessList;
    }

    @PlanningEntityCollectionProperty
    public List<CloudProcessAssignment> getCloudProcessAssignmentList() {
        return cloudProcessAssignmentList;
    }

    public void setCloudProcessAssignmentList(List<CloudProcessAssignment> cloudProcessAssignmentList) {
        this.cloudProcessAssignmentList = cloudProcessAssignmentList;
    }

    public HardAndSoftScore getScore() {
        return score;
    }

    public void setScore(HardAndSoftScore score) {
        this.score = score;
    }

    public Collection<? extends Object> getProblemFacts() {
        List<Object> facts = new ArrayList<Object>();
        facts.addAll(cloudComputerList);
        facts.addAll(cloudProcessList);
        // Do not add the planning entity's (cloudProcessAssignmentList) because that will be done automatically
        return facts;
    }

    /**
     * Clone will only deep copy the {@link #cloudProcessAssignmentList}.
     */
    public CloudBalance cloneSolution() {
        CloudBalance clone = new CloudBalance();
        clone.id = id;
        clone.cloudComputerList = cloudComputerList;
        clone.cloudProcessList = cloudProcessList;
        List<CloudProcessAssignment> clonedCloudProcessAssignmentList = new ArrayList<CloudProcessAssignment>(
                cloudProcessAssignmentList.size());
        for (CloudProcessAssignment cloudProcessAssignment : cloudProcessAssignmentList) {
            CloudProcessAssignment clonedCloudProcessAssignment = cloudProcessAssignment.clone();
            clonedCloudProcessAssignmentList.add(clonedCloudProcessAssignment);
        }
        clone.cloudProcessAssignmentList = clonedCloudProcessAssignmentList;
        clone.score = score;
        return clone;
    }

    public boolean equals(Object o) {
        if (this == o) {
            return true;
        }
        if (id == null || !(o instanceof CloudBalance)) {
            return false;
        } else {
            CloudBalance other = (CloudBalance) o;
            if (cloudProcessAssignmentList.size() != other.cloudProcessAssignmentList.size()) {
                return false;
            }
            for (Iterator<CloudProcessAssignment> it = cloudProcessAssignmentList.iterator(), otherIt = other.cloudProcessAssignmentList.iterator(); it.hasNext();) {
                CloudProcessAssignment cloudProcessAssignment = it.next();
                CloudProcessAssignment otherCloudProcessAssignment = otherIt.next();
                // Notice: we don't use equals()
                if (!cloudProcessAssignment.solutionEquals(otherCloudProcessAssignment)) {
                    return false;
                }
            }
            return true;
        }
    }

    public int hashCode() {
        HashCodeBuilder hashCodeBuilder = new HashCodeBuilder();
        for (CloudProcessAssignment cloudProcessAssignment : cloudProcessAssignmentList) {
            // Notice: we don't use hashCode()
            hashCodeBuilder.append(cloudProcessAssignment.solutionHashCode());
        }
        return hashCodeBuilder.toHashCode();
    }

}
