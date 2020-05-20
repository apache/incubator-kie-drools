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

package org.optaplanner.examples.cheaptime.domain;

import java.util.List;

import org.optaplanner.core.api.domain.solution.PlanningEntityCollectionProperty;
import org.optaplanner.core.api.domain.solution.PlanningScore;
import org.optaplanner.core.api.domain.solution.PlanningSolution;
import org.optaplanner.core.api.domain.solution.ProblemFactCollectionProperty;
import org.optaplanner.core.api.domain.solution.ProblemFactProperty;
import org.optaplanner.core.api.domain.valuerange.ValueRangeProvider;
import org.optaplanner.core.api.score.buildin.hardmediumsoftlong.HardMediumSoftLongScore;
import org.optaplanner.examples.common.domain.AbstractPersistable;
import org.optaplanner.persistence.xstream.api.score.buildin.hardmediumsoftlong.HardMediumSoftLongScoreXStreamConverter;

import com.thoughtworks.xstream.annotations.XStreamAlias;
import com.thoughtworks.xstream.annotations.XStreamConverter;

@PlanningSolution
@XStreamAlias("CtCheapTimeSolution")
public class CheapTimeSolution extends AbstractPersistable {

    private int timeResolutionInMinutes;
    private int globalPeriodRangeFrom; // Inclusive
    private int globalPeriodRangeTo; // Exclusive

    private List<Resource> resourceList;
    private List<Machine> machineList;
    private List<MachineCapacity> machineCapacityList;
    private List<Task> taskList;
    private List<TaskRequirement> taskRequirementList;
    // Order is equal to global periodRange so int period can be used for the index
    private List<PeriodPowerPrice> periodPowerPriceList;

    private List<TaskAssignment> taskAssignmentList;

    @XStreamConverter(HardMediumSoftLongScoreXStreamConverter.class)
    private HardMediumSoftLongScore score;

    public int getTimeResolutionInMinutes() {
        return timeResolutionInMinutes;
    }

    public void setTimeResolutionInMinutes(int timeResolutionInMinutes) {
        this.timeResolutionInMinutes = timeResolutionInMinutes;
    }

    public int getGlobalPeriodRangeFrom() {
        return globalPeriodRangeFrom;
    }

    public void setGlobalPeriodRangeFrom(int globalPeriodRangeFrom) {
        this.globalPeriodRangeFrom = globalPeriodRangeFrom;
    }

    public int getGlobalPeriodRangeTo() {
        return globalPeriodRangeTo;
    }

    public void setGlobalPeriodRangeTo(int globalPeriodRangeTo) {
        this.globalPeriodRangeTo = globalPeriodRangeTo;
    }

    @ProblemFactCollectionProperty
    public List<Resource> getResourceList() {
        return resourceList;
    }

    @ValueRangeProvider(id = "machineRange")
    @ProblemFactCollectionProperty
    public List<Machine> getMachineList() {
        return machineList;
    }

    public void setMachineList(List<Machine> machineList) {
        this.machineList = machineList;
    }

    public void setResourceList(List<Resource> resourceList) {
        this.resourceList = resourceList;
    }

    @ProblemFactCollectionProperty
    public List<MachineCapacity> getMachineCapacityList() {
        return machineCapacityList;
    }

    public void setMachineCapacityList(List<MachineCapacity> machineCapacityList) {
        this.machineCapacityList = machineCapacityList;
    }

    @ProblemFactCollectionProperty
    public List<Task> getTaskList() {
        return taskList;
    }

    public void setTaskList(List<Task> taskList) {
        this.taskList = taskList;
    }

    @ProblemFactCollectionProperty
    public List<TaskRequirement> getTaskRequirementList() {
        return taskRequirementList;
    }

    public void setTaskRequirementList(List<TaskRequirement> taskRequirementList) {
        this.taskRequirementList = taskRequirementList;
    }

    @ProblemFactCollectionProperty
    public List<PeriodPowerPrice> getPeriodPowerPriceList() {
        return periodPowerPriceList;
    }

    public void setPeriodPowerPriceList(List<PeriodPowerPrice> periodPowerPriceList) {
        this.periodPowerPriceList = periodPowerPriceList;
    }

    @PlanningEntityCollectionProperty
    public List<TaskAssignment> getTaskAssignmentList() {
        return taskAssignmentList;
    }

    public void setTaskAssignmentList(List<TaskAssignment> taskAssignmentList) {
        this.taskAssignmentList = taskAssignmentList;
    }

    @PlanningScore
    public HardMediumSoftLongScore getScore() {
        return score;
    }

    public void setScore(HardMediumSoftLongScore score) {
        this.score = score;
    }

    // ************************************************************************
    // Complex methods
    // ************************************************************************

    @ProblemFactProperty
    public CheapTimeSolution getCheapTimeSolution() {
        return this;
    }

}
