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

    private HardAndSoftScore score;

    public List<CloudComputer> getCloudComputerList() {
        return cloudComputerList;
    }

    public void setCloudComputerList(List<CloudComputer> cloudComputerList) {
        this.cloudComputerList = cloudComputerList;
    }

    @PlanningEntityCollectionProperty
    public List<CloudProcess> getCloudProcessList() {
        return cloudProcessList;
    }

    public void setCloudProcessList(List<CloudProcess> cloudProcessList) {
        this.cloudProcessList = cloudProcessList;
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
        // Do not add the planning entity's (cloudProcessList) because that will be done automatically
        return facts;
    }

    /**
     * Clone will only deep copy the {@link #cloudProcessList}.
     */
    public CloudBalance cloneSolution() {
        CloudBalance clone = new CloudBalance();
        clone.id = id;
        clone.cloudComputerList = cloudComputerList;
        List<CloudProcess> clonedCloudProcessList = new ArrayList<CloudProcess>(
                cloudProcessList.size());
        for (CloudProcess cloudProcess : cloudProcessList) {
            CloudProcess clonedCloudProcess = cloudProcess.clone();
            clonedCloudProcessList.add(clonedCloudProcess);
        }
        clone.cloudProcessList = clonedCloudProcessList;
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
            if (cloudProcessList.size() != other.cloudProcessList.size()) {
                return false;
            }
            for (Iterator<CloudProcess> it = cloudProcessList.iterator(), otherIt = other.cloudProcessList.iterator(); it.hasNext();) {
                CloudProcess cloudProcess = it.next();
                CloudProcess otherCloudProcess = otherIt.next();
                // Notice: we don't use equals()
                if (!cloudProcess.solutionEquals(otherCloudProcess)) {
                    return false;
                }
            }
            return true;
        }
    }

    public int hashCode() {
        HashCodeBuilder hashCodeBuilder = new HashCodeBuilder();
        for (CloudProcess cloudProcess : cloudProcessList) {
            // Notice: we don't use hashCode()
            hashCodeBuilder.append(cloudProcess.solutionHashCode());
        }
        return hashCodeBuilder.toHashCode();
    }

}
