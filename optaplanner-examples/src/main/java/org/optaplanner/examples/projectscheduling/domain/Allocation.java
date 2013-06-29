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

package org.optaplanner.examples.projectscheduling.domain;

import java.util.ArrayList;
import java.util.List;

import com.thoughtworks.xstream.annotations.XStreamAlias;
import org.optaplanner.core.api.domain.entity.PlanningEntity;
import org.optaplanner.core.api.domain.value.ValueRange;
import org.optaplanner.core.api.domain.value.ValueRangeType;
import org.optaplanner.core.api.domain.variable.PlanningVariable;
import org.optaplanner.examples.common.domain.AbstractPersistable;
import org.optaplanner.examples.projectscheduling.domain.solver.NotSourceOrSinkAllocationFilter;
import org.optaplanner.examples.projectscheduling.domain.solver.PredecessorsDoneDateUpdatingVariableListener;

@PlanningEntity(movableEntitySelectionFilter = NotSourceOrSinkAllocationFilter.class)
@XStreamAlias("PsAllocation")
public class Allocation extends AbstractPersistable {

    private Job job;

    private Allocation sourceAllocation;
    private Allocation sinkAllocation;
    private List<Allocation> predecessorAllocationList;
    private List<Allocation> successorAllocationList;

    // Planning variables: changes during planning, between score calculations.
    private ExecutionMode executionMode;
    private Integer delay; // In days

    // Shadow variables
    private Integer predecessorsDoneDate;

    public Job getJob() {
        return job;
    }

    public void setJob(Job job) {
        this.job = job;
    }

    public Allocation getSourceAllocation() {
        return sourceAllocation;
    }

    public void setSourceAllocation(Allocation sourceAllocation) {
        this.sourceAllocation = sourceAllocation;
    }

    public Allocation getSinkAllocation() {
        return sinkAllocation;
    }

    public void setSinkAllocation(Allocation sinkAllocation) {
        this.sinkAllocation = sinkAllocation;
    }

    public List<Allocation> getPredecessorAllocationList() {
        return predecessorAllocationList;
    }

    public void setPredecessorAllocationList(List<Allocation> predecessorAllocationList) {
        this.predecessorAllocationList = predecessorAllocationList;
    }

    public List<Allocation> getSuccessorAllocationList() {
        return successorAllocationList;
    }

    public void setSuccessorAllocationList(List<Allocation> successorAllocationList) {
        this.successorAllocationList = successorAllocationList;
    }

    @PlanningVariable(variableListenerClasses = {PredecessorsDoneDateUpdatingVariableListener.class})
    @ValueRange(type = ValueRangeType.FROM_PLANNING_ENTITY_PROPERTY, planningEntityProperty = "executionModeRange")
    public ExecutionMode getExecutionMode() {
        return executionMode;
    }

    public void setExecutionMode(ExecutionMode executionMode) {
        this.executionMode = executionMode;
    }

    @PlanningVariable(variableListenerClasses = {PredecessorsDoneDateUpdatingVariableListener.class})
    @ValueRange(type = ValueRangeType.FROM_PLANNING_ENTITY_PROPERTY, planningEntityProperty = "delayRange")
    public Integer getDelay() {
        return delay;
    }

    public void setDelay(Integer delay) {
        this.delay = delay;
    }

    public Integer getPredecessorsDoneDate() {
        return predecessorsDoneDate;
    }

    public void setPredecessorsDoneDate(Integer predecessorsDoneDate) {
        this.predecessorsDoneDate = predecessorsDoneDate;
    }

    // ************************************************************************
    // Complex methods
    // ************************************************************************


    public Integer getStartDate() {
        if (predecessorsDoneDate == null || delay == null) {
            return null;
        }
        return predecessorsDoneDate + delay;
    }

    public Integer getEndDate() {
        if (predecessorsDoneDate == null || delay == null || executionMode == null) {
            return null;
        }
        return predecessorsDoneDate + delay + executionMode.getDuration();
    }

    // ************************************************************************
    // Ranges
    // ************************************************************************

    public List<ExecutionMode> getExecutionModeRange() {
        return job.getExecutionModeList();
    }

    private List<Integer> delayRange; // TODO remove this HACK
    public List<Integer> getDelayRange() {
        // TODO IMPROVE ME
        if (delayRange == null) {
            delayRange = new ArrayList<Integer>(50);
            for (int i = 0; i < 50; i++) {
                delayRange.add(i);
            }
        }
        return delayRange;
    }

}
