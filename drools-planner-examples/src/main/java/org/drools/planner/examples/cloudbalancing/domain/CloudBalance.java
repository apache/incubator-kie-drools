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
import org.drools.planner.core.score.buildin.hardandsoft.HardAndSoftScore;
import org.drools.planner.core.solution.Solution;
import org.drools.planner.examples.common.domain.AbstractPersistable;

@XStreamAlias("CloudBalance")
public class CloudBalance extends AbstractPersistable implements Solution<HardAndSoftScore> {

    private List<CloudComputer> computerList;

    private List<CloudProcess> processList;

    private HardAndSoftScore score;

    public List<CloudComputer> getComputerList() {
        return computerList;
    }

    public void setComputerList(List<CloudComputer> computerList) {
        this.computerList = computerList;
    }

    @PlanningEntityCollectionProperty
    public List<CloudProcess> getProcessList() {
        return processList;
    }

    public void setProcessList(List<CloudProcess> processList) {
        this.processList = processList;
    }

    public HardAndSoftScore getScore() {
        return score;
    }

    public void setScore(HardAndSoftScore score) {
        this.score = score;
    }

    // ************************************************************************
    // Complex methods
    // ************************************************************************

    public Collection<? extends Object> getProblemFacts() {
        List<Object> facts = new ArrayList<Object>();
        facts.addAll(computerList);
        // Do not add the planning entity's (processList) because that will be done automatically
        return facts;
    }

    /**
     * Clone will only deep copy the {@link #processList}.
     */
    public CloudBalance cloneSolution() {
        CloudBalance clone = new CloudBalance();
        clone.id = id;
        clone.computerList = computerList;
        List<CloudProcess> clonedProcessList = new ArrayList<CloudProcess>(
                processList.size());
        for (CloudProcess process : processList) {
            CloudProcess clonedProcess = process.clone();
            clonedProcessList.add(clonedProcess);
        }
        clone.processList = clonedProcessList;
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
            if (processList.size() != other.processList.size()) {
                return false;
            }
            for (Iterator<CloudProcess> it = processList.iterator(), otherIt = other.processList.iterator(); it.hasNext();) {
                CloudProcess process = it.next();
                CloudProcess otherProcess = otherIt.next();
                // Notice: we don't use equals()
                if (!process.solutionEquals(otherProcess)) {
                    return false;
                }
            }
            return true;
        }
    }

    public int hashCode() {
        HashCodeBuilder hashCodeBuilder = new HashCodeBuilder();
        for (CloudProcess process : processList) {
            // Notice: we don't use hashCode()
            hashCodeBuilder.append(process.solutionHashCode());
        }
        return hashCodeBuilder.toHashCode();
    }

}
