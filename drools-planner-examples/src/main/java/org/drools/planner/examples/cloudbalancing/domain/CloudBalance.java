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
import org.drools.planner.core.score.HardAndSoftScore;
import org.drools.planner.core.score.Score;
import org.drools.planner.core.solution.Solution;
import org.drools.planner.examples.common.domain.AbstractPersistable;

@XStreamAlias("CloudBalance")
public class CloudBalance extends AbstractPersistable implements Solution<HardAndSoftScore> {

    private List<CloudComputer> cloudComputerList;
    private List<CloudProcess> cloudProcessList;

    private List<CloudAssignment> cloudAssignmentList;

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

    public List<CloudAssignment> getCloudAssignmentList() {
        return cloudAssignmentList;
    }

    public void setCloudAssignmentList(List<CloudAssignment> cloudAssignmentList) {
        this.cloudAssignmentList = cloudAssignmentList;
    }

    public HardAndSoftScore getScore() {
        return score;
    }

    public void setScore(HardAndSoftScore score) {
        this.score = score;
    }

    public boolean isInitialized() {
        return (cloudAssignmentList != null);
    }

    public Collection<? extends Object> getFacts() {
        List<Object> facts = new ArrayList<Object>();
        facts.addAll(cloudComputerList);
        facts.addAll(cloudProcessList);

        if (isInitialized()) {
            facts.addAll(cloudAssignmentList);
        }
        return facts;
    }

    /**
     * Clone will only deep copy the {@link #cloudAssignmentList}.
     */
    public CloudBalance cloneSolution() {
        CloudBalance clone = new CloudBalance();
        clone.id = id;
        clone.cloudComputerList = cloudComputerList;
        clone.cloudProcessList = cloudProcessList;
        List<CloudAssignment> clonedCloudAssignmentList = new ArrayList<CloudAssignment>(
                cloudAssignmentList.size());
        for (CloudAssignment cloudAssignment : cloudAssignmentList) {
            CloudAssignment clonedCloudAssignment = cloudAssignment.clone();
            clonedCloudAssignmentList.add(clonedCloudAssignment);
        }
        clone.cloudAssignmentList = clonedCloudAssignmentList;
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
            if (cloudAssignmentList.size() != other.cloudAssignmentList.size()) {
                return false;
            }
            for (Iterator<CloudAssignment> it = cloudAssignmentList.iterator(), otherIt = other.cloudAssignmentList.iterator(); it.hasNext();) {
                CloudAssignment cloudAssignment = it.next();
                CloudAssignment otherCloudAssignment = otherIt.next();
                // Notice: we don't use equals()
                if (!cloudAssignment.solutionEquals(otherCloudAssignment)) {
                    return false;
                }
            }
            return true;
        }
    }

    public int hashCode() {
        HashCodeBuilder hashCodeBuilder = new HashCodeBuilder();
        for (CloudAssignment cloudAssignment : cloudAssignmentList) {
            // Notice: we don't use hashCode()
            hashCodeBuilder.append(cloudAssignment.solutionHashCode());
        }
        return hashCodeBuilder.toHashCode();
    }

}
