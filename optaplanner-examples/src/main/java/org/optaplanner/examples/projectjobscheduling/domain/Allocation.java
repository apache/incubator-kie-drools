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

package org.optaplanner.examples.projectjobscheduling.domain;

import java.util.List;

import org.optaplanner.core.api.domain.entity.PlanningEntity;
import org.optaplanner.core.api.domain.valuerange.CountableValueRange;
import org.optaplanner.core.api.domain.valuerange.ValueRangeFactory;
import org.optaplanner.core.api.domain.valuerange.ValueRangeProvider;
import org.optaplanner.core.api.domain.variable.CustomShadowVariable;
import org.optaplanner.core.api.domain.variable.PlanningVariable;
import org.optaplanner.core.api.domain.variable.PlanningVariableReference;
import org.optaplanner.examples.common.domain.AbstractPersistable;
import org.optaplanner.examples.projectjobscheduling.domain.solver.DelayStrengthComparator;
import org.optaplanner.examples.projectjobscheduling.domain.solver.ExecutionModeStrengthWeightFactory;
import org.optaplanner.examples.projectjobscheduling.domain.solver.NotSourceOrSinkAllocationFilter;
import org.optaplanner.examples.projectjobscheduling.domain.solver.PredecessorsDoneDateUpdatingVariableListener;

import com.thoughtworks.xstream.annotations.XStreamAlias;

@PlanningEntity(pinningFilter = NotSourceOrSinkAllocationFilter.class)
@XStreamAlias("PjsAllocation")
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

    @PlanningVariable(valueRangeProviderRefs = {
            "executionModeRange" }, strengthWeightFactoryClass = ExecutionModeStrengthWeightFactory.class)
    public ExecutionMode getExecutionMode() {
        return executionMode;
    }

    public void setExecutionMode(ExecutionMode executionMode) {
        this.executionMode = executionMode;
    }

    @PlanningVariable(valueRangeProviderRefs = { "delayRange" }, strengthComparatorClass = DelayStrengthComparator.class)
    public Integer getDelay() {
        return delay;
    }

    public void setDelay(Integer delay) {
        this.delay = delay;
    }

    @CustomShadowVariable(variableListenerClass = PredecessorsDoneDateUpdatingVariableListener.class, sources = {
            @PlanningVariableReference(variableName = "executionMode"),
            @PlanningVariableReference(variableName = "delay") })
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
        if (predecessorsDoneDate == null) {
            return null;
        }
        return predecessorsDoneDate + (delay == null ? 0 : delay);
    }

    public Integer getEndDate() {
        if (predecessorsDoneDate == null) {
            return null;
        }
        return predecessorsDoneDate + (delay == null ? 0 : delay)
                + (executionMode == null ? 0 : executionMode.getDuration());
    }

    public Project getProject() {
        return job.getProject();
    }

    public int getProjectCriticalPathEndDate() {
        return job.getProject().getCriticalPathEndDate();
    }

    public JobType getJobType() {
        return job.getJobType();
    }

    public String getLabel() {
        return "Job " + job.getId();
    }

    // ************************************************************************
    // Ranges
    // ************************************************************************

    @ValueRangeProvider(id = "executionModeRange")
    public List<ExecutionMode> getExecutionModeRange() {
        return job.getExecutionModeList();
    }

    @ValueRangeProvider(id = "delayRange")
    public CountableValueRange<Integer> getDelayRange() {
        return ValueRangeFactory.createIntValueRange(0, 500);
    }

}
