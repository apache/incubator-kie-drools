/*
 * Copyright 2015 Red Hat, Inc. and/or its affiliates.
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

package org.optaplanner.examples.cheaptime.solver.drools;

import java.util.Arrays;
import java.util.List;

import org.apache.commons.lang3.builder.CompareToBuilder;
import org.apache.commons.lang3.builder.EqualsBuilder;
import org.apache.commons.lang3.builder.HashCodeBuilder;
import org.optaplanner.examples.cheaptime.domain.Machine;
import org.optaplanner.examples.cheaptime.domain.Task;
import org.optaplanner.examples.cheaptime.domain.TaskAssignment;
import org.optaplanner.examples.cheaptime.domain.TaskRequirement;

public class MachinePeriodPart implements Comparable<MachinePeriodPart> {

    private final Machine machine;
    private final int period;

    private boolean active;
    private int[] resourceAvailableList;
    private int resourceInShortTotal;

    public MachinePeriodPart(Machine machine, int period, int resourceListSize, List<TaskAssignment> taskAssignmentList) {
        this.machine = machine;
        this.period = period;

        active = false;

        resourceAvailableList = new int[resourceListSize];
        for (int i = 0; i < resourceListSize; i++) {
            resourceAvailableList[i] = machine.getMachineCapacityList().get(i).getCapacity();
        }

        for (TaskAssignment taskAssignment : taskAssignmentList) {
            addTaskAssignment(taskAssignment);
        }

        resourceInShortTotal = 0;
        for (int resourceAvailable : resourceAvailableList) {
            if (resourceAvailable < 0) {
                resourceInShortTotal += resourceAvailable;
            }
        }
    }

    private void addTaskAssignment(TaskAssignment taskAssignment) {
        active = true;
        Task task = taskAssignment.getTask();
        for (int i = 0; i < resourceAvailableList.length; i++) {
            TaskRequirement taskRequirement = task.getTaskRequirementList().get(i);
            resourceAvailableList[i] -= taskRequirement.getResourceUsage();
        }
    }

    public boolean isActive() {
        return active;
    }

    public void setActive(boolean active) {
        this.active = active;
    }

    public int[] getResourceAvailableList() {
        return resourceAvailableList;
    }

    public void setResourceAvailableList(int[] resourceAvailableList) {
        this.resourceAvailableList = resourceAvailableList;
    }

    public int getResourceInShortTotal() {
        return resourceInShortTotal;
    }

    public Machine getMachine() {
        return machine;
    }

    public int getPeriod() {
        return period;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) {
            return true;
        } else if (o instanceof MachinePeriodPart) {
            MachinePeriodPart other = (MachinePeriodPart) o;
            return new EqualsBuilder()
                    .append(machine, other.machine)
                    .append(period, other.period)
                    .append(active, other.active)
                    .append(resourceAvailableList, other.resourceAvailableList)
                    .isEquals();
        } else {
            return false;
        }
    }

    @Override
    public int hashCode() {
        return new HashCodeBuilder()
                .append(machine)
                .append(period)
                .append(active)
                .append(resourceAvailableList)
                .toHashCode();
    }

    @Override
    public int compareTo(MachinePeriodPart other) {
        return new CompareToBuilder()
                .append(machine, other.machine)
                .append(period, other.period)
                .append(active, other.active)
                .append(resourceAvailableList, other.resourceAvailableList)
                .toComparison();
    }

    @Override
    public String toString() {
        return machine + ", period = " + period + ", active = " + active
                + ", resourceAvailableList = " + Arrays.toString(resourceAvailableList);
    }
}
