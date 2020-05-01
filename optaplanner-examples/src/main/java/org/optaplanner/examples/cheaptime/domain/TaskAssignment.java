/*
 * Copyright 2014 Red Hat, Inc. and/or its affiliates.
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

import org.optaplanner.core.api.domain.entity.PlanningEntity;
import org.optaplanner.core.api.domain.valuerange.CountableValueRange;
import org.optaplanner.core.api.domain.valuerange.ValueRangeFactory;
import org.optaplanner.core.api.domain.valuerange.ValueRangeProvider;
import org.optaplanner.core.api.domain.variable.PlanningVariable;
import org.optaplanner.examples.cheaptime.domain.solver.TaskAssignmentDifficultyComparator;
import org.optaplanner.examples.common.domain.AbstractPersistable;

import com.thoughtworks.xstream.annotations.XStreamAlias;

@PlanningEntity(difficultyComparatorClass = TaskAssignmentDifficultyComparator.class)
@XStreamAlias("CtTaskAssignment")
public class TaskAssignment extends AbstractPersistable {

    private Task task;

    // Planning variables: changes during planning, between score calculations.
    private Machine machine;
    private Integer startPeriod;

    public Task getTask() {
        return task;
    }

    public void setTask(Task task) {
        this.task = task;
    }

    @PlanningVariable(valueRangeProviderRefs = { "machineRange" })
    public Machine getMachine() {
        return machine;
    }

    public void setMachine(Machine machine) {
        this.machine = machine;
    }

    @PlanningVariable(valueRangeProviderRefs = { "startPeriodRange" })
    public Integer getStartPeriod() {
        return startPeriod;
    }

    public void setStartPeriod(Integer startPeriod) {
        this.startPeriod = startPeriod;
    }

    // ************************************************************************
    // Complex methods
    // ************************************************************************

    /**
     * The startPeriod is included and the endPeriod is excluded.
     *
     * @return null if {@link #getStartPeriod()} is null
     */
    public Integer getEndPeriod() {
        if (startPeriod == null) {
            return null;
        }
        return startPeriod + task.getDuration();
    }

    public String getLabel() {
        return task.getLabel();
    }

    // ************************************************************************
    // Ranges
    // ************************************************************************

    @ValueRangeProvider(id = "startPeriodRange")
    public CountableValueRange<Integer> getStartPeriodRange() {
        return ValueRangeFactory.createIntValueRange(task.getStartPeriodRangeFrom(), task.getStartPeriodRangeTo());
    }

}
